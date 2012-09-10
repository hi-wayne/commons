package com.viei.commons.date;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateCache {

	private static final Logger logger = LoggerFactory
			.getLogger(DateCache.class);


	/**
	 * 获取当前时间,返回日期
	 * 
	 * @return
	 */
	public static Date currentDate() {
		if (currDate == null) {
			currDate = new Date();
		}
		return currDate;
	}

	/**
	 * 获取当前时间,返回毫秒
	 * 
	 * @return
	 */
	public static Long currTimeMillis() {
		if (currTimeMillis == null) {
			currTimeMillis = System.currentTimeMillis();
		}
		return currTimeMillis;
	}

	/**
	 * 获取当日剩余秒数
	 * 
	 * @return the dayRemainerSecond
	 */
	public static Integer getDayRemainerSecond() {
		if (null == dayRemainerSecond) {
			dayRemainerSecond = TimeUtil.getDayRemainSecond(currDate);
		}
		return dayRemainerSecond;
	}

 
	// 线程池
	private final static ScheduledExecutorService executorPool = Executors
			.newSingleThreadScheduledExecutor();
	
	// 线程池的循环间隔时间默认5秒
	private static final long tickUnit = Long.parseLong(System.getProperty(
			"datecache.tick", String.valueOf(5000)));
	
	// 启动线程池
	static {
		// tickUnit个MILLISECONDS时间单位后，每隔tickUnit个MILLISECONDS时间单位执行DateTicker线程
		executorPool.scheduleAtFixedRate(new DoTicker(), tickUnit, tickUnit,
				TimeUnit.MILLISECONDS);
		// 关闭钩子
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				executorPool.shutdown();
			}
		});
	}

	// 周期运算实现
	private static class DoTicker implements Runnable {
		public void run() {
			currDate = new Date();
			currTimeMillis = System.currentTimeMillis();
			dayRemainerSecond = TimeUtil.getDayRemainSecond(currDate); 
			if (logger.isDebugEnabled()) {
				logger.debug("日期cache进行日期更新操作结束");
			}
		}
	}
	
	// 进行cache的变量
	private static volatile Date currDate = new Date();
	private static volatile Long currTimeMillis = System.currentTimeMillis();
	private static volatile Integer dayRemainerSecond; 


}
