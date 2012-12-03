package com.hiwayne.commons.aspect.cache;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiwayne.commons.string.StringUtil;
 

/**
 * 根据结果保存到本地heap中
 * 
 * @author wangwei
 * 
 */
@Aspect
public class HeapCacheGetAspectJ {

	private static final Logger logger = LoggerFactory
			.getLogger(HeapCacheGetAspectJ.class);

	private ConcurrentHashMap<String, Cache> ehCacheMap;

	@Pointcut("@annotation(com.hiwayne.commons.aspect.cache.HeapCacheGetAnnotation)")
	public void methodPointcut() {
	}

	@Around("methodPointcut()")
	public Object methodCacheHold(ProceedingJoinPoint joinPoint)
			throws Throwable {
		StringBuffer runInfo = new StringBuffer();
		if (ehCacheMap == null) {
			return joinPoint.proceed();
		}
		// 调用原始方法的参数
		Method[] method = Class.forName(
				joinPoint.getTarget().getClass().getName()).getMethods();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();

		// 注释参数
		String ehCacheName = null;// ehCache名称
		boolean cacheKeyMD5 = false;// 对传入的key是否进行md5处理
		String keyPrefix = null;// key前缀
		String keyParMain = null;// key值参数
		String keyParSub = null;// key值2级参数
		String keyParSub2 = null;// key值3级参数
		String keyParSub3 = null;// key值3级参数
		String keyParSub4 = null;// key值3级参数
		int keyParMainIndex = -1;// key值参数，坐标
		int keyParSubIndex = -1;// key值2级参数，坐标
		int keyParSub2Index = -1;// key值3级参数，坐标
		int keyParSub3Index = -1;// key值3级参数，坐标
		int keyParSub4Index = -1;// key值3级参数，坐标
		boolean saveNull = false;

		// 通过反射获取类中方法的信息
		boolean getMethodInfo = false;
		for (Method m : method) {
			if (m.getName().equals(methodName)) {
				if (m.getParameterTypes().length == arguments.length
						&& arguments != null) {// 进行简单的方法签名校验(只验证方法名和参数个数)
					HeapCacheGetAnnotation aHeapCacheAnnotation = m
							.getAnnotation(HeapCacheGetAnnotation.class);
					ehCacheName = aHeapCacheAnnotation.ehCacheName();
					keyPrefix = aHeapCacheAnnotation.keyPrefix();
					keyParMainIndex = aHeapCacheAnnotation.keyParMainIndex();
					keyParSubIndex = aHeapCacheAnnotation.keyParSubIndex();
					keyParSub2Index = aHeapCacheAnnotation.keyParSub2Index();
					keyParSub3Index = aHeapCacheAnnotation.keyParSub3Index();
					keyParSub4Index = aHeapCacheAnnotation.keyParSub4Index();
					saveNull = aHeapCacheAnnotation.saveNull();
					cacheKeyMD5 = aHeapCacheAnnotation.cacheKeyMD5();
					getMethodInfo = true;
					break;
				}
			}
		}
		// key配置信息检查
		// Assert.notNull(keyPrefix, "keyPrefix 没有定义" + keyPrefix);
		if (getMethodInfo && arguments != null) {
			if (ehCacheName == null || ehCacheMap.get(ehCacheName) == null) {
				throw new Exception("ehCacheName is null");
			}
			if (keyPrefix == null) {
				throw new Exception("keyPrefix is null");
			}
			if (keyParMainIndex > arguments.length + 1) {
				throw new Exception("keyMainIndex 越界");
			}
			if (keyParSubIndex > arguments.length + 1) {
				throw new Exception("keySubIndex 越界");
			}
			if (keyParSub2Index > arguments.length + 1) {
				throw new Exception("keySub2Index 越界");
			}
			if (keyParSub3Index > arguments.length + 1) {
				throw new Exception("keySub3Index 越界");
			}
			if (keyParSub4Index > arguments.length + 1) {
				throw new Exception("keySub4Index 越界");
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
			if (keyParSub4Index != -1) {
				Object obj = arguments[keyParSub4Index];
				if (obj == null) {
					keyParSub4 = "";
				} else {
					keyParSub4 = obj.toString();
				}
			}
		} else {
			throw new Exception("请检查配置");
		}
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
		if (keyParSub4Index != -1) {
			cacheKeyBuffer.append(":");
			cacheKeyBuffer.append(keyParSub4);
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
			runInfo.append(",ehCacheName=");
			runInfo.append(ehCacheName);
			runInfo.append(")");
		}

		// 查询cache
		boolean invoke = true;
		boolean isSaveNullObject = false;
		Object resultObj = null;
		Element element = null;
		element = ehCacheMap.get(ehCacheName).get(cacheKey);
		if (element == null) {
			if (logger.isInfoEnabled()) {
				runInfo.append(",ehcache内没有信息");
			}
		} else {
			resultObj = element.getValue();
			if (resultObj != null) {
				if (resultObj instanceof NullValuPOJO) {// 有版本信息的数据
					isSaveNullObject = true;
				}
				invoke = false;
				if (logger.isInfoEnabled()) {
					runInfo.append(",成功获取ehcache内");
				}
			} else {
				invoke = true;
				if (logger.isInfoEnabled()) {
					runInfo.append(",ehcache内没有信息");
				}
			}
		}

		// 最终处理部分
		// 原生方法
		if (invoke) {
			resultObj = joinPoint.proceed();
			if (saveNull) {// 要求进行null值存储
				if (resultObj == null) {
					resultObj = new NullValuPOJO();
					isSaveNullObject = true;
				}
			}
			// 回写cache
			if (resultObj != null) {
				Element aElement = new Element(cacheKey, resultObj);
				ehCacheMap.get(ehCacheName).put(aElement);
				if (logger.isInfoEnabled()) {
					runInfo.append(",用原生方法结果写ehcache");
				}
			} else {
				if (logger.isInfoEnabled()) {
					runInfo.append(",原生方法无结果,不写ehcache");
				}
			}
		} else {
			if (logger.isInfoEnabled()) {
				runInfo.append(",直接从cache拿");
			}
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
		keyPrefix = null;// key前缀
		keyParMain = null;// key值参数
		keyParSub = null;// key值2级参数
		keyParSub2 = null;// key值3级参数
		keyParSub3 = null;// key值3级参数
		keyParSub4 = null;// key值3级参数
		element = null;
		return resultObj;
	}

	public ConcurrentHashMap<String, Cache> getEhCacheMap() {
		return ehCacheMap;
	}

	public void setEhCacheMap(ConcurrentHashMap<String, Cache> ehCacheMap) {
		this.ehCacheMap = ehCacheMap;
	}

}
