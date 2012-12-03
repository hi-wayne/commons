package com.hiwayne.commons.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangwei
 */
public class ThreadNamedFactory implements ThreadFactory {
	private static final AtomicInteger pollNO = new AtomicInteger(1);
	private static final String defaultPrefix = "pool-";
	
	private final AtomicInteger threadNO = new AtomicInteger(1);
	private final String threadPrefix;
	private final ThreadGroup threadGroup;
	private final boolean daemoFlag;


	/**
	 * 默认的线程命名方法
	 */
	public ThreadNamedFactory() {
		this(defaultPrefix + pollNO.getAndIncrement(), false);
	}

	/**
	 * 按前缀进行线程命名
	 * 
	 * @param prefix
	 */
	public ThreadNamedFactory(String prefix) {
		this(prefix, false);
	}

	/**
	 * 按前缀进行线程命名
	 * 
	 * @param prefix
	 *            线程名前缀
	 * @param daemo
	 *            是否是影子线程
	 */
	public ThreadNamedFactory(String prefix, boolean daemo) {
		threadPrefix = prefix + "-thread-";
		daemoFlag = daemo;
		SecurityManager s = System.getSecurityManager();
		threadGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s
				.getThreadGroup();
	}

	/**
	 * 对传入线程类进行命名设置
	 */
	public Thread newThread(Runnable runnable) {
		String name = threadPrefix + threadNO.getAndIncrement();
		Thread ret = new Thread(threadGroup, runnable, name, 0);
		ret.setDaemon(daemoFlag);
		return ret;
	}

	/**
	 * 
	 * @return
	 */
	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}
}