package com.hiwayne.commons.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	/**
	 * 进行日期增加操作
	 * 
	 * @param currDate
	 * @param addDate
	 * @return
	 */
	public static Date addNDate(Date currDate, Integer addDate) {
		DateCache.currentDate();
		Calendar now = Calendar.getInstance();
		now.setTime(currDate);
		now.add(Calendar.DAY_OF_YEAR, addDate);
		return now.getTime();
	}

	/**
	 * 进行小时增加操作
	 * 
	 * @param currDate
	 * @param addHour
	 * @return
	 */
	public static Date addHour(Date currDate, Integer addHour) {
		DateCache.currentDate();
		Calendar now = Calendar.getInstance();
		now.setTime(currDate);
		now.add(Calendar.HOUR_OF_DAY, addHour);
		return now.getTime();
	}

	/**
	 * 进行年增加操作
	 * 
	 * @param currDate
	 * @param addHour
	 * @return
	 */
	public static Date addYear(Date currDate, Integer addYear) {
		DateCache.currentDate();
		Calendar now = Calendar.getInstance();
		now.setTime(currDate);
		now.add(Calendar.YEAR, addYear);
		return now.getTime();
	}

	/**
	 * 进行分钟增加操作
	 * 
	 * @param currDate
	 * @param addHour
	 * @return
	 */
	public static Date addMinute(Date currDate, Integer addMinute) {
		DateCache.currentDate();
		Calendar now = Calendar.getInstance();
		now.setTime(currDate);
		now.add(Calendar.MINUTE, addMinute);
		return now.getTime();
	}

	/**
	 * 进行秒钟增加操作
	 * 
	 * @param currDate
	 * @param addHour
	 * @return
	 */
	public static Date addSecond(Date currDate, Integer addSecond) {
		DateCache.currentDate();
		Calendar now = Calendar.getInstance();
		now.setTime(currDate);
		now.add(Calendar.SECOND, addSecond);
		return now.getTime();
	}

	/**
	 * 返回较大的日期
	 * 
	 * @param oneDate
	 * @param twoDate
	 * @return
	 */
	public static Date getMaxDate(Date oneDate, Date twoDate) {
		if (oneDate == null) {
			if (twoDate == null) {
				return null;
			} else {
				return twoDate;
			}
		} else {
			if (twoDate == null) {
				return oneDate;
			} else {
				if (oneDate.getTime() > twoDate.getTime()) {
					return oneDate;
				} else {
					return twoDate;
				}
			}
		}
	}

	/**
	 * 获取明天0时
	 * 
	 * @param currDate
	 * @return Date
	 */
	public static Date getTomorrow0(Date currDate) {
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.setTime(currDate);
		tomorrow.add(Calendar.DAY_OF_YEAR, 1);
		tomorrow.set(Calendar.HOUR_OF_DAY, 0);
		tomorrow.set(Calendar.MINUTE, 0);
		tomorrow.set(Calendar.SECOND, 0);
		tomorrow.set(Calendar.MILLISECOND, 0);
		return tomorrow.getTime();
	}

	/**
	 * 获取当天剩余秒数
	 * 
	 * @param currDate
	 * @return 获取当天剩余秒数
	 */
	public static Integer getDayRemainSecond(Date currDate) {
		long temp = (TimeUtil.getTomorrow0(currDate).getTime() - currDate
				.getTime()) / 1000;
		return (int) temp;
	}
 

	public static String currDateStr(Date currDate) {
		String time = new SimpleDateFormat("yyyy-MM-dd").format(currDate);
		return time;
	}

	public static void main(String[] args) {
		System.out.println(currDateStr(new Date()));
	}
}
