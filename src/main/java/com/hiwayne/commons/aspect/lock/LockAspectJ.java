package com.hiwayne.commons.aspect.lock;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import net.rubyeye.xmemcached.MemcachedClient;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiwayne.commons.exception.MutualLockTimeOutException;

@Aspect
public class LockAspectJ {

	private static final Logger logger = LoggerFactory
			.getLogger(LockAspectJ.class);

	public static final long DEFAULT_SleepMilliSeconds = 400;// 拿锁操作当前线程休息400毫秒
	public static final int DEFAULT_lockLeftSecond = 5;// 拿锁操作互斥5秒

	private long sleepMilliSeconds = DEFAULT_SleepMilliSeconds;
	private int lockLeftSecond = DEFAULT_lockLeftSecond;

	private MemcachedClient memcachedClient;

	@Pointcut("@annotation(com.hiwayne.commons.aspect.lock.LockAnnotation)")
	public void methodPointcut() {
	}

	@Around("methodPointcut()")
	public Object methodCacheHold(ProceedingJoinPoint joinPoint)
			throws Throwable {

		// 调用原始方法的参数
		Method[] method = Class.forName(
				joinPoint.getTarget().getClass().getName()).getMethods();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();

		// 注释参数
		int lockTime = lockLeftSecond;// 锁定默认生命期3秒
		String keyPrefix = null;// key前缀
		String keyParMain = null;// key值参数
		String keyParSub = null;// key值2级参数
		int keyParMainIndex = -1;// key值参数，坐标
		int keyParSubIndex = -1;// key值2级参数，坐标

		// 通过反射获取类中方法的信息
		boolean getMethodInfo = false;
		for (Method m : method) {
			if (m.getName().equals(methodName)) {
				if (m.getParameterTypes().length == arguments.length
						&& arguments != null) {// 进行简单的方法签名校验(只验证方法名和参数个数)
					LockAnnotation aLockAnnotation = m
							.getAnnotation(LockAnnotation.class);
					lockTime = aLockAnnotation.lockTime();
					keyPrefix = aLockAnnotation.keyPrefix();
					keyParMainIndex = aLockAnnotation.keyParMainIndex();
					keyParSubIndex = aLockAnnotation.keyParSubIndex();
					getMethodInfo = true;
					break;
				}
			}
		}

		if (lockTime == -1) {
			lockTime = lockLeftSecond;// 默认生命期10秒
		}
		// key配置信息检查
		if (getMethodInfo && arguments != null) {
			if (keyParMainIndex > arguments.length + 1) {
				throw new Exception("keyMainIndex 越界");
			}
			if (keyParSubIndex > arguments.length + 1) {
				throw new Exception("keySubIndex 越界");
			}
			if (keyParMainIndex != -1) {
				keyParMain = arguments[keyParMainIndex].toString();
			}
			if (keyParSubIndex != -1) {
				keyParSub = arguments[keyParSubIndex].toString();
			}
		} else {
			throw new Exception("请检查配置");
		}
		// 生成key
		StringBuffer lockKeyBuffer = new StringBuffer(keyPrefix);
		if (keyParMain != null) {
			lockKeyBuffer.append(":");
			lockKeyBuffer.append(keyParMain);
		}
		if (keyParSub != null) {
			lockKeyBuffer.append(":");
			lockKeyBuffer.append(keyParSub);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("加锁处理( ，有效期=" + lockTime + "，key前缀=" + keyPrefix
					+ "，key1=" + keyParMain + "，key2=" + keyParSub + ")");
		}
		// 拿锁成功标志
		boolean getLock = false;

		// 拿锁操作
		getLock = memcachedClient.add(lockKeyBuffer.toString(), lockTime, 1);
		if (!getLock) {
			for (int i = 0; i < 4; i++) {
				TimeUnit.MILLISECONDS.sleep(sleepMilliSeconds);
				getLock = memcachedClient.add(lockKeyBuffer.toString(),
						lockTime, 1);
				if (getLock) {
					break;
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("在memcahe中拿锁" + lockKeyBuffer + "失败次数="
								+ (i + 1));
					}
				}
			}
		}

		// 原生方法
		Object result = null;
		// 成功拿锁
		if (getLock) {
			if (logger.isDebugEnabled()) {
				logger.debug("拿锁" + lockKeyBuffer + "成功,进入原生方法");
			}
			result = joinPoint.proceed();

			// 释放锁(如果释放失败maxLeft时间后此key会失效)
			memcachedClient.delete(lockKeyBuffer.toString());
			if (logger.isDebugEnabled()) {
				logger.debug("解锁" + lockKeyBuffer + "成功,原生方法执行完毕");
			}
		} else {
			throw new MutualLockTimeOutException();
		}

		// 自己执行结束
		return result;
	}

	public long getSleepMilliSeconds() {
		return sleepMilliSeconds;
	}

	public void setSleepMilliSeconds(long sleepMilliSeconds) {
		this.sleepMilliSeconds = sleepMilliSeconds;
	}

	public MemcachedClient getMemcachedClient() {
		return memcachedClient;
	}

	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}
}
