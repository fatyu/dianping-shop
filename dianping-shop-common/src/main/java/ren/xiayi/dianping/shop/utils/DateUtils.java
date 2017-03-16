package ren.xiayi.dianping.shop.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * 日期工具
 */
public class DateUtils {

	public static final int MILLINS_OF_DAY = 86400000;

	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	public static final String YYYYMMDDHHMMSS = "yyyy/MM/dd HH:mm:ss";

	private DateUtils() {
		super();
	}

	/**
	 * 日期转字符串 日期格式默认yyyy-MM-dd HH:mm:ss
	 */
	public static String dateToString(Date date, String pattern) {
		final SimpleDateFormat sdf = new SimpleDateFormat(StringUtils.isBlank(pattern) ? YYYY_MM_DD_HH_MM_SS : pattern);
		String formatResult = sdf.format(date);
		return formatResult;
	}

	/**
	 * 字符串转日期:默认格式yyyy-MM-dd
	 */
	public static Date stringToDateYyyyMMdd(String dateString) throws ParseException {
		return stringToDate(dateString, YYYY_MM_DD);
	}

	/**
	 * 字符串转日期:默认格式yyyy-MM-dd HH:mm:ss
	 */
	public static Date stringToDateYyyyMMddhhmmss(String dateString) throws ParseException {
		return stringToDate(dateString, YYYY_MM_DD_HH_MM_SS);

	}

	/**
	 * 字符串转日期:默认格式yyyy-MM-dd HH:mm
	 */
	public static Date stringToDateYyyyMMddhhmm(String dateString) throws ParseException {
		return stringToDate(dateString, YYYY_MM_DD_HH_MM);

	}

	/**
	 * 字符串转日期
	 */
	public static Date stringToDate(String dateString, String pattern) throws ParseException {
		return org.apache.commons.lang3.time.DateUtils.parseDate(dateString,
				StringUtils.isBlank(pattern) ? YYYY_MM_DD_HH_MM_SS : pattern);
	}

	/**
	 * 字符串日期格式修改
	 */
	public static String changePattern(String dateString, String pattern) throws ParseException {
		Date stringToDateYyyyMMddhhmmss = stringToDateYyyyMMddhhmmss(
				StringUtils.isBlank(pattern) ? YYYY_MM_DD_HH_MM_SS : pattern);
		return dateToString(stringToDateYyyyMMddhhmmss, pattern);
	}

	/**
	 * 计算日期和当前日期之间的月份数
	 */
	public static int calcMonthBetweenDateAndNow(Date date) {
		if (null == date) {
			return 0;
		}
		Calendar bef = Calendar.getInstance();
		Calendar aft = Calendar.getInstance();
		bef.setTime(date);
		aft.setTime(new Date());
		int years = aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR);
		int months = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
		return 12 * years + months;
	}

	/**
	 * 计算两个时间之间的日期数
	 *   注：如计算区间为 2015-12-8 09:00:00 - 2015-12-9 09:00:00，算作2015-12-8与2015-12-9两天，则跨度为2天
	 */
	public static int calcDayBetweenDates(Date earlierDay, Date laterDay) {
		if (null == earlierDay || null == laterDay) {
			return 0;
		}
		long millins = laterDay.getTime() - earlierDay.getTime();
		Number day = millins / MILLINS_OF_DAY;
		return day.intValue() + 1;
	}

	/**
	 * 友好的时间显示
	 */
	public static String friendlyTime(Date date) {
		if (null == date) {
			return "";
		}
		int ct = (int) ((System.currentTimeMillis() - date.getTime()) / 1000);
		if (ct < 3600) {
			return Math.max(ct / 60, 1) + "分钟前";
		}
		if (ct >= 3600 && ct < 86400) {
			return ct / 3600 + "小时前";
		}
		if (ct >= 86400 && ct < 2592000) { //86400 * 30
			int day = ct / 86400;
			return (day > 1) ? day + "天前" : "昨天";
		}
		if (ct >= 2592000 && ct < 31104000) {
			return ct / 2592000 + "个月前";
		}
		return ct / 31104000 + "年前";
	}

	/**
	 * 获得当前日期周的某天,0当前周的周日5当前周的周5
	 * @return
	 */
	public static String getCurrentSomeDay(int day) {
		int mondayPlus = getSundayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + day);
		Date monday = currentDate.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date modayDate = null;
		try {
			modayDate = dateFormat.parse(dateFormat.format(monday));
		} catch (ParseException e) {
		}
		return dateFormat.format(modayDate);
	}

	/**
	 * 获得当前日期周的某天,0当前周的周日5当前周的周5
	 * @return
	 */
	public static String getCurrentSomeDay(int day, String pattern) {
		int mondayPlus = getSundayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + day);
		Date monday = currentDate.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern == null ? "yyyy-MM-dd HH:mm:ss" : pattern);
		Date modayDate = null;
		try {
			modayDate = dateFormat.parse(dateFormat.format(monday));
		} catch (ParseException e) {
		}
		return dateFormat.format(modayDate);
	}

	/**
	 * 获得当前日期与本周日相差的天数,周日为一周的开始
	 * @return
	 */
	private static int getSundayPlus() {
		Calendar cd = Calendar.getInstance();
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
		return 1 - dayOfWeek;

	}

	/**
	 * 获得当前日期为每周的第几天,周一为第一天
	 * @param pattern
	 * @param dateTime
	 * @return
	 */
	public static int getDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int weekOfDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (weekOfDay == 0) {
			weekOfDay = 7;
		}
		return weekOfDay;
	}

	/**
	 * 获得当天剩余的秒数
	 */
	public static long surplusTodaySencods() {
		Calendar calendar = Calendar.getInstance();
		long now = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		long tomorrow = calendar.getTimeInMillis();
		return (tomorrow - now) / 1000;
	}

	public static Date getBeginningOfMonth() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.DAY_OF_MONTH, 1);
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		return today.getTime();
	}

	/**
	 * 获取第二天凌晨时间
	 */
	public static Date getTomorrow() {
		Date tomorrow = getToday();
		tomorrow = addDays(tomorrow, 1);
		return tomorrow;
	}

	/**
	 * 获得当天的日期
	 */
	public static Date getToday() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		return today.getTime();
	}

	/**
	 * 获取昨日的日期
	 */
	public static Date getYesterday() {
		Calendar yesterday = Calendar.getInstance();
		yesterday.set(Calendar.HOUR_OF_DAY, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);
		yesterday.set(Calendar.MILLISECOND, 0);
		yesterday.add(Calendar.DAY_OF_YEAR, -1);
		return yesterday.getTime();
	}

	/**
	 * 获取上周周日日期
	 */
	public static Date getLastWeekSunday() {
		Calendar date = Calendar.getInstance(Locale.CHINA);
		date.setFirstDayOfWeek(Calendar.MONDAY);
		date.add(Calendar.WEEK_OF_MONTH, -1);
		date.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);//设置周日
		return date.getTime();
	}

	/**
	 * 获取上周周一日期
	 */
	public static Date getLastWeekMonday() {
		Calendar date = Calendar.getInstance(Locale.CHINA);
		date.setFirstDayOfWeek(Calendar.MONDAY);
		date.add(Calendar.WEEK_OF_MONTH, -1);
		date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//设置周一
		return date.getTime();
	}

	public static Date addMonths(final Date date, final int amount) {
		return add(date, Calendar.MONTH, amount);
	}

	public static Date addDays(final Date date, final int amount) {
		return add(date, Calendar.DAY_OF_YEAR, amount);
	}

	public static Date addHours(final Date date, final int amount) {
		return add(date, Calendar.HOUR_OF_DAY, amount);
	}

	public static Date addMinutes(final Date date, final int amount) {
		return add(date, Calendar.MINUTE, amount);
	}

	public static Date addSeconds(final Date date, final int amount) {
		return add(date, Calendar.SECOND, amount);
	}

	private static Date add(final Date date, final int calendarField, final int amount) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, amount);
		return c.getTime();
	}

}
