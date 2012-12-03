package com.hiwayne.commons.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author wangwei
 * 
 */
public class PoolDispatcher {
	public static int HOLDINGTASK_MULTIPLE = 10;
	public static float MAXPOOLSIZE_MULTIPLE = 1.5f;
	private ThreadPoolExecutor threadPool;

	public PoolDispatcher(int poolSize) {
		this(poolSize, (int) (MAXPOOLSIZE_MULTIPLE * poolSize),
				TimeUnit.SECONDS, new ThreadPoolExecutor.AbortPolicy(),
				"pool-dispatcher");
	}

	public PoolDispatcher(int poolSize, long keepAliveTime, TimeUnit unit,
			RejectedExecutionHandler rejectedExecutionHandler, String prefix) {
		this.threadPool = new ThreadPoolExecutor(poolSize,
				(int) (MAXPOOLSIZE_MULTIPLE * poolSize), keepAliveTime, unit,
				new ArrayBlockingQueue<Runnable>(poolSize
						* HOLDINGTASK_MULTIPLE), new ThreadNamedFactory(prefix));
		this.threadPool.setRejectedExecutionHandler(rejectedExecutionHandler);
	}

	public final void dispatch(Runnable r) {
		if (!this.threadPool.isShutdown()) {
			this.threadPool.execute(r);
		}
	}

	public void stop() {
		this.threadPool.shutdown();
		try {
			this.threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}