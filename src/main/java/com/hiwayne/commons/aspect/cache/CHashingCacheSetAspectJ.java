package com.hiwayne.commons.aspect.cache;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiwayne.commons.exception.CasDbException;
import com.hiwayne.commons.exception.MutualLockTimeOutException;
import com.hiwayne.commons.string.StringUtil;

/**
 * 执行方法后，把结果保存到memcache
 * 
 * @author wangwei
 * 
 */
@Aspect
public class CHashingCacheSetAspectJ {
	private static final Logger logger = LoggerFactory
			.getLogger(CHashingCacheSetAspectJ.class);

	private MemcachedClient memcachedClient;

	public static final int MUTUALTIME = 3;// 互斥key存活周期5秒
	public static final int CASCOUNT = 5;// 互斥尝试cas次数
	public static final long DEFAULT_SLEEPMILLISECONDS = 500;// 拿锁操作当前线程休息500毫秒

	public static final int DEFAULT_MAXLEFTSECOND = 600;// 生命周期

	@Pointcut("@annotation(com.hiwayne.commons.aspect.cache.CHashingCacheSetAnnotation)")
	public void methodPointcut() {
	}

	@Around("methodPointcut()")
	public Object methodCacheHold(ProceedingJoinPoint joinPoint)
			throws Throwable {
		StringBuffer runInfo = new StringBuffer();

		if (memcachedClient == null) {
			return joinPoint.proceed();
		}
		// 调用原始方法的参数
		Method[] method = Class.forName(
				joinPoint.getTarget().getClass().getName()).getMethods();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();

		// 参数坐标
		int useCacheIndex = -1;
		int leftSecondIndex = -1;
		int keyParMainIndex = -1;// key值参数，坐标
		int keyParSubIndex = -1;// key值2级参数，坐标
		int keyParSub2Index = -1;// key值3级参数，坐标
		int keyParSub3Index = -1;// key值4级参数，坐标

		// 注释参数
		// 注释参数
		boolean cacheKeyMD5 = false;// 对传入的key是否进行md5处理
		String keyPrefix = null;// key前缀
		int maxLeftSecond = DEFAULT_MAXLEFTSECOND;//
		int saveNullLeftSecond = DEFAULT_MAXLEFTSECOND;
		boolean mutual = false;// 是否使用互斥处理

		boolean useCache = true;// 是否使用cache
		String keyParMain = null;// key值参数
		String keyParSub = null;// key值2级参数
		String keyParSub2 = null;// key值3级参数
		String keyParSub3 = null;// key值3级参数

		boolean saveNull = false;// 是否保存空值

		// 通过反射获取类中方法的信息
		boolean getMethodInfo = false;
		for (Method m : method) {
			if (m.getName().equals(methodName)) {
				if (m.getParameterTypes().length == arguments.length
						&& arguments != null) {// 进行简单的方法签名校验(只验证方法名和参数个数)
					CHashingCacheSetAnnotation aCHashingCacheSetAnnotation = m
							.getAnnotation(CHashingCacheSetAnnotation.class);
					keyPrefix = aCHashingCacheSetAnnotation.keyPrefix();
					maxLeftSecond = aCHashingCacheSetAnnotation.maxLeftSecond();
					if (maxLeftSecond <= 0) {
						maxLeftSecond = 0;// 0为永远不超时
					}
					mutual = aCHashingCacheSetAnnotation.mutual();
					useCacheIndex = aCHashingCacheSetAnnotation.useCacheIndex();
					leftSecondIndex = aCHashingCacheSetAnnotation
							.leftSecondIndex();
					keyParMainIndex = aCHashingCacheSetAnnotation
							.keyParMainIndex();
					keyParSubIndex = aCHashingCacheSetAnnotation
							.keyParSubIndex();
					keyParSub2Index = aCHashingCacheSetAnnotation
							.keyParSub2Index();
					keyParSub3Index = aCHashingCacheSetAnnotation
							.keyParSub3Index();
					saveNull = aCHashingCacheSetAnnotation.saveNull();
					saveNullLeftSecond = aCHashingCacheSetAnnotation
							.saveNullLeftSecond();
					cacheKeyMD5 = aCHashingCacheSetAnnotation.cacheKeyMD5();
					getMethodInfo = true;
					break;
				}
			}
		}
		// key配置信息检查
		// Assert.notNull(keyPrefix, "keyPrefix 没有定义" + keyPrefix);
		if (getMethodInfo && arguments != null) {
			if (keyPrefix == null) {
				throw new Exception("keyPrefix is null");
			}
			if (useCacheIndex > arguments.length + 1) {
				throw new Exception("useCacheIndex 越界");
			}
			if (leftSecondIndex > arguments.length + 1) {
				throw new Exception("leftSecondIndex 越界");
			}
			if (keyParMainIndex > arguments.length + 1) {
				throw new Exception("keyParMainIndex 越界");
			}
			if (keyParSubIndex > arguments.length + 1) {
				throw new Exception("keyParSubIndex 越界");
			}
			if (keyParSub2Index > arguments.length + 1) {
				throw new Exception("keyParSub2Index 越界");
			}
			if (keyParSub3Index > arguments.length + 1) {
				throw new Exception("keyParSub3Index 越界");
			}
			if (useCacheIndex != -1) {
				Object obj = arguments[useCacheIndex];
				if (obj != null) {
					useCache = (Boolean) obj;
				}
			}
			if (leftSecondIndex != -1) {
				Object obj = arguments[leftSecondIndex];
				if (obj != null) {
					maxLeftSecond = (Integer) obj;
				}
			}
			if (keyParMainIndex != -1) {
				Object obj = arguments[keyParMainIndex];
				if (obj == null) {
					keyParMain = "";
				} else {
					keyParMain = obj.toString();
				}
			}
			if (keyParSubIndex != -1) {
				Object obj = arguments[keyParSubIndex];
				if (obj == null) {
					keyParSub = "";
				} else {
					keyParSub = obj.toString();
				}
			}
			if (keyParSub2Index != -1) {
				Object obj = arguments[keyParSub2Index];
				if (obj == null) {
					keyParSub2 = "";
				} else {
					keyParSub2 = obj.toString();
				}
			}
			if (keyParSub3Index != -1) {
				Object obj = arguments[keyParSub3Index];
				if (obj == null) {
					keyParSub3 = "";
				} else {
					keyParSub3 = obj.toString();
				}
			}
		} else {
			throw new Exception("请检查配置");
		}

		Object resultObj = null;

		// 生成key
		StringBuffer cacheKeyBuffer = new StringBuffer(keyParMain);
		if (keyParSubIndex != -1) {
			cacheKeyBuffer.append(":");
			cacheKeyBuffer.append(keyParSub);
		}
		if (keyParSub2Index != -1) {
			cacheKeyBuffer.append(":");
			cacheKeyBuffer.append(keyParSub2);
		}
		if (keyParSub3Index != -1) {
			cacheKeyBuffer.append(":");
			cacheKeyBuffer.append(keyParSub3);
		}
		String cacheKey = "";
		if (cacheKeyMD5) {
			cacheKey = keyPrefix + ":" + StringUtil.md5(cacheKeyBuffer);
		} else {
			cacheKey = keyPrefix + ":" + cacheKeyBuffer.toString();
		}
		if (logger.isInfoEnabled()) {
			runInfo.append("(cacheKey=");
			runInfo.append(cacheKey);
			runInfo.append(",src=");
			runInfo.append(cacheKeyBuffer);
			runInfo.append(")");
		}

		// 原生方法（并且进行互斥判断和处理）
		boolean lockFlag = true;
		String mutualKey = "mutual:" + cacheKey;
		if (mutual) {
			lockFlag = false;
			// 拿锁操作
			lockFlag = memcachedClient.add(mutualKey, MUTUALTIME, 1);
			if (!lockFlag) {
				for (int i = 0; i < CASCOUNT; i++) {
					TimeUnit.MILLISECONDS.sleep(DEFAULT_SLEEPMILLISECONDS);
					lockFlag = memcachedClient.add(mutualKey, MUTUALTIME, 1);// 设置互斥锁
					if (lockFlag) {
						break;
					} else {
						if (logger.isInfoEnabled()) {
							runInfo.append(",互斥处理拿锁 mutualKey=");
							runInfo.append(mutualKey);
							runInfo.append(",cacheVersion=");
							runInfo.append("失败次数=");
							runInfo.append(i + 1);
						}
					}
				}
			}
			if (logger.isInfoEnabled()) {
				runInfo.append(",互斥锁的信息=");
				runInfo.append(lockFlag);
			}
		}
		boolean isSaveNullObject = false;
		if (lockFlag) {// 互斥锁拿到
			if (logger.isInfoEnabled()) {
				runInfo.append(",开始调用原生方法");
			}
			try {
				resultObj = joinPoint.proceed();
				if (saveNull) {// 要求进行null值存储
					if (resultObj == null) {
						resultObj = new NullValuPOJO();
						isSaveNullObject = true;
					}
				}
			} catch (CasDbException e) {
				if (logger.isInfoEnabled()) {
					runInfo.append(",根据结果保存memcahce操作发生数据版本异常");
				}
				if (mutual) {
					memcachedClient.deleteWithNoReply(mutualKey);// 执行后删除互斥锁
				}
				memcachedClient.deleteWithNoReply(cacheKey);// 系统报cas异常，证明数据版本不对，进行删除cache操作
				throw e;
			}
		} else {
			throw new MutualLockTimeOutException();
		}

		// 回写cache
		if (useCache) {
			if (resultObj != null) {
				if (resultObj instanceof VersionData) {// 有版本信息的数据
					VersionData newVersionData = (VersionData) resultObj;
					boolean addFlag = memcachedClient.add(cacheKey,
							maxLeftSecond, resultObj);
					if (!addFlag) {// cache中有数据add失败
						GetsResponse<Object> aGetsResponse = memcachedClient
								.gets(cacheKey);// 获取cache内信息和cas值
						if (aGetsResponse != null) {
							long casVersion = aGetsResponse.getCas();
							Object cacheDate = aGetsResponse.getValue();
							if (cacheDate instanceof VersionData) {
								VersionData cacheVersionData = (VersionData) cacheDate;
								long cacheVersionNO = cacheVersionData
										.getVersionNo();
								long currVersionNO = newVersionData
										.getVersionNo();
								if (cacheVersionData != null
										&& cacheVersionNO < currVersionNO) {
									// 如果按cas值进行更新失败，表示在更新过程中cache值又发生后了编号,此操作默认失败
									boolean upFlag = memcachedClient.cas(
											cacheKey, maxLeftSecond, resultObj,
											casVersion);// 按cas值进行更新
									if (!upFlag) {
										//
									}
									if (logger.isInfoEnabled()) {
										runInfo.append(",用原生方法结果写cache");
									}
								} else if (cacheVersionData != null
										&& cacheVersionNO == currVersionNO) {
									// 数据版本一致不进行cache更新
									if (logger.isInfoEnabled()) {
										runInfo.append(",数据版本一致不进行cache更新 currVersionNO=");
										runInfo.append(currVersionNO);
										runInfo.append(",cacheVersionNO=");
										runInfo.append(cacheVersionNO);
									}
								} else {
									resultObj = cacheVersionData;
									if (logger.isInfoEnabled()) {
										runInfo.append(",cache信息版本高,返回cache内信息 currVersionNO=");
										runInfo.append(currVersionNO);
										runInfo.append(",cacheVersionNO=");
										runInfo.append(cacheVersionNO);
									}
								}
							} else {
								memcachedClient.deleteWithNoReply(cacheKey);//
								if (logger.isInfoEnabled()) {
									runInfo.append(",cache中信息和系统产生的不一致,del cache");
								}
							}
						} else {
							if (logger.isInfoEnabled()) {
								runInfo.append(",根据结果保存memcahce结束-(cache突然挂了无法写入)");
							}
						}
					} else {

						if (logger.isInfoEnabled()) {
							runInfo.append(",用原生方法结果(有版本)add cache");
						}
					}
				} else {// 无版本信息的数据
					if (isSaveNullObject) {
						memcachedClient.set(cacheKey, saveNullLeftSecond,
								resultObj);
					} else {
						memcachedClient.set(cacheKey, maxLeftSecond, resultObj);
					}

					if (logger.isInfoEnabled()) {
						runInfo.append(",用原生方法结果(无版本)set cache");
					}
				}
			} else {
				if (logger.isInfoEnabled()) {
					runInfo.append(",原生方法无结果,不写cache");
				}
			}
		} else {
			if (logger.isInfoEnabled()) {
				runInfo.append(",不要求进行回写cache操作");
			}
		}
		if (mutual) {
			// 执行后删除互斥锁
			memcachedClient.deleteWithNoReply(mutualKey);
		}

		if (logger.isInfoEnabled()) {
			logger.info(runInfo.toString());
		}
		if (isSaveNullObject) {
			resultObj = null;
		}
		runInfo = null;
		cacheKeyBuffer = null;
		cacheKey = null;
		mutualKey = null;
		method = null;
		methodName = null;
		arguments = null;
		keyPrefix = null;
		keyParMain = null;
		keyParSub = null;
		keyParSub2 = null;
		keyParSub3 = null;
		// 自己执行结束
		return resultObj;
	}

	public MemcachedClient getMemcachedClient() {
		return memcachedClient;
	}

	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}

}