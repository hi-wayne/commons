package com.hiwayne.commons.aspect.cache;

import java.lang.reflect.Method;

import net.rubyeye.xmemcached.MemcachedClient;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiwayne.commons.exception.CasDbException;
import com.hiwayne.commons.string.StringUtil;

/**
 * 删除cache内信息
 * 
 * @author wangwei
 * 
 */
@Aspect
public class CHashingCacheDelAspectJ {
	private static final Logger logger = LoggerFactory
			.getLogger(CHashingCacheDelAspectJ.class);

	private MemcachedClient memcachedClient;

	@Pointcut("@annotation(com.hiwayne.commons.aspect.cache.CHashingCacheDelAnnotation)")
	public void methodPointcut() {
	}

	@Around("methodPointcut()")
	public Object methodCacheHold(ProceedingJoinPoint joinPoint)
			throws Throwable {
		StringBuffer runInfo = new StringBuffer();
		Object resultObj = null;
		if (memcachedClient == null) {
			return joinPoint.proceed();
		}
		CasDbException casDbException = null;
		try {
			resultObj = joinPoint.proceed();
		} catch (CasDbException e) {
			casDbException = e;
		}

		// 调用原始方法的参数
		Method[] method = Class.forName(
				joinPoint.getTarget().getClass().getName()).getMethods();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();

		// 注释参数
		boolean cacheKeyMD5 = false;// 对传入的key是否进行md5处理
		String[] keyPrefix = null;// key前缀
		Object[] keyParMain = null;// key值参数
		Object[] keyParSub = null;// key值2级参数
		int[] keyParMainIndex = {};// key值参数，坐标
		int[] keyParSubIndex = {};// key值2级参数，坐标

		// 通过反射获取类中方法的信息
		for (Method m : method) {
			if (m.getName().equals(methodName)) {
				if (m.getParameterTypes().length == arguments.length
						&& arguments != null) {// 进行简单的方法签名校验(只验证方法名和参数个数)
					CHashingCacheDelAnnotation aCHashingCacheDelAnnotation = m
							.getAnnotation(CHashingCacheDelAnnotation.class);

					keyPrefix = aCHashingCacheDelAnnotation.keyPrefix();
					keyParMainIndex = aCHashingCacheDelAnnotation
							.keyParMainIndex();
					keyParSubIndex = aCHashingCacheDelAnnotation
							.keyParSubIndex();
					cacheKeyMD5 = aCHashingCacheDelAnnotation.cacheKeyMD5();
					break;
				}
			}
		}
		// key配置信息检查
		// Assert.notNull(keyPrefix, "keyPrefix 没有定义" + keyPrefix);

		keyParMain = new Object[keyPrefix.length];// key值参数
		keyParSub = new Object[keyPrefix.length];// key值2级参数
		if (arguments != null) {
			if (keyParMainIndex != null) {
				for (int i = 0; i < keyParMainIndex.length; i++) {
					int index = keyParMainIndex[i];
					if (index > arguments.length) {
						throw new Exception("keyMainIndex 越界");
					}
					Object obj = null;
					if (index != -1) {
						obj = arguments[index];
					}
					keyParMain[i] = obj;
				}
			}

			if (keyParSubIndex != null) {
				for (int i = 0; i < keyParSubIndex.length; i++) {
					int index = keyParSubIndex[i];
					if (index > arguments.length) {
						throw new Exception("keySubIndex 越界");
					}
					Object obj = null;
					if (index != -1) {
						obj = arguments[index];
					}
					keyParSub[i] = obj;
				}
			}
		} else {
			throw new Exception("请检查配置");
		}

		for (int i = 0; i < keyPrefix.length; i++) {
			// 生成key
			StringBuffer cacheKeyBuffer = new StringBuffer();
			if (keyParMain[i] != null) {
				cacheKeyBuffer.append(":");
				cacheKeyBuffer.append(keyParMain[i]);
			}
			if (keyParSub[i] != null) {
				cacheKeyBuffer.append(":");
				cacheKeyBuffer.append(keyParSub[i]);
			}
			String cacheKey = "";
			if (cacheKeyMD5) {
				cacheKey = keyPrefix[i] + ":" + StringUtil.md5(cacheKeyBuffer);
			} else {
				cacheKey = keyPrefix[i] + ":" + cacheKeyBuffer.toString();
			}
			memcachedClient.deleteWithNoReply(cacheKey);
			if (logger.isInfoEnabled()) {
				runInfo.append("(cacheKey=");
				runInfo.append(cacheKey);
				runInfo.append(",src=");
				runInfo.append(cacheKeyBuffer);
				runInfo.append(")");
			}
		}

		if (logger.isInfoEnabled()) {
			logger.info(runInfo.toString());
		}
		runInfo = null;
		runInfo = null;
		method = null;
		methodName = null;
		arguments = null;
		keyPrefix = null;// key前缀
		keyParMain = null;// key值参数
		keyParSub = null;// key值2级参数

		if (casDbException != null)
			throw casDbException;
		return resultObj;

	}

	public MemcachedClient getMemcachedClient() {
		return memcachedClient;
	}

	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}
}
