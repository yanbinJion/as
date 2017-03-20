package com.wisdomrouter.app.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
	/**
	 * Weather
	 */
	private static final String[] WEEK = { "天", "一", "二", "三", "四", "五", "六" };
	public static final String XING_QI = "星期";
	public static final String ZHOU = "周";

	public static String getWeek(int num, String format) {
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		int weekNum = c.get(Calendar.DAY_OF_WEEK) + num;
		if (weekNum > 7)
			weekNum = weekNum - 7;
		return format + WEEK[weekNum - 1];
	}

	public static String getZhouWeek() {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd");
		return format.format(new Date(System.currentTimeMillis())) + " "
				+ getWeek(0, ZHOU);
	}

	/**
	 * 获取时间差,几天前,等等
	 * 
	 * @param timeStr
	 * @return
	 */
	public static String getDay(String timeStr) {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TimeZone timeZoneSH = TimeZone.getTimeZone("Asia/Shanghai");
		df.setTimeZone(timeZoneSH);

		java.util.Date now = new java.util.Date();// 当前时间

		String time = df.format((Long.parseLong(timeStr)) * 1000l);
		java.util.Date date;
		try {
			date = df.parse(time);
			long l = now.getTime() - date.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			if (day > 0) {
				switch ((int) day) {
				case 1:
					sb.append("昨天");
					break;
				case 2:
					sb.append("前天");
					break;

				default:
					SimpleDateFormat inputFormat = new SimpleDateFormat(
							"MM-dd HH:mm");
					time = inputFormat.format((Long.parseLong(timeStr)) * 1000);
					sb.append(time);
					break;
				}
			} else {
				if (hour > 0) {
					sb.append(hour + "小时前");
				} else {
					if (min > 0) {
						sb.append(min + "分钟前");
					} else {
						sb.append("刚刚");
					}
				}
			}

			return sb.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * Data
	 */
	public static String getContentDate(String month, String day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
		TimeZone timeZoneSH = TimeZone.getTimeZone("Asia/Shanghai");
		sdf.setTimeZone(timeZoneSH);
		java.util.Date d = new java.util.Date();
		String str = sdf.format(d);
		String nowday = str.substring(8, 10);
		String result = null;

		int temp = Integer.parseInt(nowday) - Integer.parseInt(day);
		switch (temp) {
		case 0:
			result = "今天";
			break;
		case 1:
			result = "昨天";
			break;
		case 2:
			result = "前天";
			break;
		default:
			StringBuilder sb = new StringBuilder();
			sb.append(Integer.parseInt(month) + "月");
			sb.append(Integer.parseInt(day) + "日");
			sb.append(month + "-");
			sb.append(day + " ");
			result = sb.toString();
			break;
		}
		return result;
	}

	public static String getContentTime(int timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = null;
		try {
			TimeZone timeZoneSH = TimeZone.getTimeZone("Asia/Shanghai");
			sdf.setTimeZone(timeZoneSH);
			java.util.Date currentdate = new java.util.Date();// 当前时间

			long i = (currentdate.getTime() / 1000 - timestamp) / (60);
			Timestamp now = new Timestamp(System.currentTimeMillis());// 获取系统当前时间
			System.out.println("now-->" + now);// 返回结果精确到毫秒。

			String str = sdf.format(new Timestamp(IntToLong(timestamp)));
			time = str.substring(11, 16);

			String month = str.substring(5, 7);
			String day = str.substring(8, 10);
			time = getContentDate(month, day) + time;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	public static String getContentTime2(int timestamp) {
		String time = null;
		try {
			TimeZone timeZoneSH = TimeZone.getTimeZone("Asia/Shanghai");
			SimpleDateFormat inputFormat = new SimpleDateFormat("MM-dd HH:mm");
			inputFormat.setTimeZone(timeZoneSH);
			time = inputFormat.format((Long.parseLong(timestamp + "")) * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}
	public static String getDay2(String timestamp) {
		String time = null;
		try {
			TimeZone timeZoneSH = TimeZone.getTimeZone("Asia/Shanghai");
			SimpleDateFormat inputFormat = new SimpleDateFormat("dd");
			inputFormat.setTimeZone(timeZoneSH);
			time = inputFormat.format((Long.parseLong(timestamp + "")) * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}
	public static String getmonth(String timestamp) {
		String time = null;
		try {
			TimeZone timeZoneSH = TimeZone.getTimeZone("Asia/Shanghai");
			SimpleDateFormat inputFormat = new SimpleDateFormat("M月");
			inputFormat.setTimeZone(timeZoneSH);
			time = inputFormat.format((Long.parseLong(timestamp + "")) * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	public static String getContentTime3(String timestamp) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
		String time = null;
		try {
			TimeZone timeZoneSH = TimeZone.getTimeZone("Asia/Shanghai");
			inputFormat.setTimeZone(timeZoneSH);
			time = inputFormat.format((Long.parseLong(timestamp)) * 1000l);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	// java Timestamp构造函数需传入Long型
	public static long IntToLong(int i) {
		long result = (long) i;
		result *= 1000;
		return result;
	}

	/**
	 * 将时间戳转换成年/月/日
	 * 
	 * @param time
	 * @return
	 */
	public static final String getStrDate(String time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = null;
		TimeZone timeZoneSH = TimeZone.getTimeZone("Asia/Shanghai");
		if (time.equals("")) {
			return "";
		}
		sdf = new SimpleDateFormat("yyyy年MM月dd日");
		sdf.setTimeZone(timeZoneSH);
		long loc_time = Long.valueOf(time);
		re_StrTime = sdf.format(new Date(loc_time * 1000L));
		return re_StrTime;
	}

	/**
	 * 将时间戳转换成年/月/日
	 * 
	 * @param time
	 * @return
	 */
	public static final String getStrDate2(String time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = null;
		TimeZone timeZoneSH = TimeZone.getTimeZone("Asia/Shanghai");
		if (time.equals("")) {
			return "";
		}
		sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		sdf.setTimeZone(timeZoneSH);
		long loc_time = Long.valueOf(time);
		re_StrTime = sdf.format(new Date(loc_time * 1000L));
		return re_StrTime;
	}

	/**
	 * 将时间戳转换成年/月/日
	 * 
	 * @param time
	 * @return
	 */
	public static final String getStrDateG(String time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = null;
		TimeZone timeZoneSH = TimeZone.getTimeZone("Asia/Shanghai");
		if (time.equals("")) {
			return "";
		}
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(timeZoneSH);
		long loc_time = Long.valueOf(time);
		re_StrTime = sdf.format(new Date(loc_time * 1000L));
		return re_StrTime;
	}
}
