package com.dessert.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/** 
 * 日期时间工具类 
 *  
 * @author renta 
 *  
 */
public class DateUtil {
    private static final SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
    private static final SimpleDateFormat datetimeFormatLackSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(  
            "yyyy-MM-dd");  
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat(  
            "HH:mm:ss");  
  
    /** 
     * Get current date and time.  <p>
     *  
     * 日期时间格式yyyy-MM-dd HH:mm:ss 
     *  
     * @return 
     */  
    public static String currentDatetime() {  
        return datetimeFormat.format(now());  
    }  
  
    /** 
     * Get current date and time.
     * <p> 
     * 日期时间格式yyyy-MM-dd HH:mm:ss 
     *  
     * @param date with type of Date
     * @return 
     */  
    public static String formatDatetime(Date date) {  
        return datetimeFormat.format(date);  
    }  
  
    /** 
     * Get current date and time without second. 
     * <p> 
     * 日期时间格式yyyy-MM-dd HH:mm
     *  
     * @param date with type of Date 
     * @return 
     */  
    public static String formatDatetimeLackSecond(Date date) {  
    	return datetimeFormatLackSecond.format(date);  
    }  
    
    /** 
     * Get current date and time.
     * <p>
     * 格式化日期时间 , 格式为yyyy-MM-dd HH:mm:ss
     *  
     * @param date 
     * @param pattern 
     *            格式化模式，详见{@link SimpleDateFormat}构造器 
     *            <code>SimpleDateFormat(String pattern)</code> 
     * @return 
     */  
    public static String formatDatetime(Date date, String pattern) {  
        SimpleDateFormat customFormat = (SimpleDateFormat) datetimeFormat  
                .clone();  
        customFormat.applyPattern(pattern);  
        return customFormat.format(date);  
    }  
  
    /** 
     * 获得当前日期 
     * <p> 
     * 日期格式yyyy-MM-dd 
     *  
     * @return 
     */  
    public static String currentDate() {  
        return dateFormat.format(now());  
    }  
  
    /** 
     * Get the date.
     * <p> 
     * 格式化日期, 日期格式yyyy-MM-dd 
     *  
     * @return 
     */  
    public static String formatDate(Date date) {  
        return dateFormat.format(date);  
    }  
  
    /** 
     * 获得当前时间 
     * <p> 
     * 时间格式HH:mm:ss 
     *  
     * @return 
     */  
    public static String currentTime() {  
        return timeFormat.format(now());  
    }  
  
    /** 
     * 格式化时间 
     * <p> 
     * 时间格式HH:mm:ss 
     *  
     * @return 
     */  
    public static String formatTime(Date date) {  
        return timeFormat.format(date);  
    }  
  
    /** 
     * 获得当前时间的<code>java.util.Date</code>对象 
     *  
     * @return 
     */  
    public static Date now() {  
        return new Date();  
    }  
  
    
    public static Calendar calendar() {  
        Calendar cal = GregorianCalendar.getInstance(Locale.CHINESE);  
        cal.setFirstDayOfWeek(Calendar.MONDAY);  
        return cal;  
    }  
  
    /** 
     * Get current millisecond.
     * <p> 
     * 获得当前时间的毫秒数.
     *  
     * @return 当前时间戳
     */  
    public static long millis() {  
        return System.currentTimeMillis();  
    }  
  
    /** 
     *  
     * 获得当前Chinese月份 
     *  
     * @return 
     */  
    public static int month() {  
        return calendar().get(Calendar.MONTH) + 1;  
    }  
  
    /** 
     * 获得月份中的第几天 
     *  
     * @return 
     */  
    public static int dayOfMonth() {  
        return calendar().get(Calendar.DAY_OF_MONTH);  
    }  
  
    /** 
     * 今天是星期的第几天 
     *  
     * @return 
     */  
    public static int dayOfWeek() {  
        return calendar().get(Calendar.DAY_OF_WEEK);  
    }  
  
    /** 
     * 今天是年中的第几天 
     *  
     * @return 
     */  
    public static int dayOfYear() {  
        return calendar().get(Calendar.DAY_OF_YEAR);  
    }  
  
    /** 
     * 判断原日期是否在目标日期之前 
     *  
     * @param src 
     * @param dst 
     * @return 
     */  
    public static boolean isBefore(Date src, Date dst) {  
        return src.before(dst);  
    }  
  
    /** 
     *判断原日期是否在目标日期之后 
     *  
     * @param src 
     * @param dst 
     * @return 
     */  
    public static boolean isAfter(Date src, Date dst) {  
        return src.after(dst);  
    }  
  
    /** 
     *判断两日期是否相同 
     *  
     * @param date1 
     * @param date2 
     * @return 
     */  
    public static boolean isEqual(Date date1, Date date2) {  
        return date1.compareTo(date2) == 0;  
    }  
  
    /** 
     * 判断某个日期是否在某个日期范围 
     *  
     * @param beginDate 
     *            日期范围开始 
     * @param endDate 
     *            日期范围结束 
     * @param src 
     *            需要判断的日期 
     * @return 
     */  
    public static boolean between(Date beginDate, Date endDate, Date src) {  
        return beginDate.before(src) && endDate.after(src);  
    }  
  
    /** 
     * Get the last day of month.
     * <p> 
     * 获得当前月的最后一天 , HH:mm:ss为0，毫秒为999 
     *  
     * @return 
     */  
    public static Date lastDayOfMonth() {  
        Calendar cal = calendar();  
        cal.set(Calendar.DAY_OF_MONTH, 0); // 月归0  
        cal.set(Calendar.HOUR_OF_DAY, 0);// 日归0  
        cal.set(Calendar.MINUTE, 0);// 分归0
        cal.set(Calendar.SECOND, 0);// 秒归0  
        cal.set(Calendar.MILLISECOND, 0);// 毫秒归0  
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);// 月份+1  
        cal.set(Calendar.MILLISECOND, -1);// 毫秒-1  
        return cal.getTime();  
    }  
  
    /** 
     * Get the specified month based on current day.
     * <p> 
     * 获取相对于当前时间  前后指定月份的时间
     *  
     * @param months 具体月数,可以为正负
     *  
     * @return 时间戳
     */  
    public static Date getRelativeMonth(int months) {  
    	Date currentDate = DateUtil.now();
    	Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+months);
		return calendar.getTime();
    } 
    
    /** 
     * Get the specified month based on specified date.
     * <p> 
     * 获取相对于指定时间  前后指定月份的时间
     *  
     * @param months 具体月数,可以为正负
     *  
     * @return 时间戳
     */  
    public static Date getRelativeMonth(Date oriDate, int months) {  
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(oriDate);
    	calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+months);
    	return calendar.getTime();
    } 
    
    /** 
     * Get the specified date based on current day.
     * <p> 
     * 获取相对于当前时间  前后指定天数的时间
     *  
     * @param days 具体天数,可以为正负
     *  
     * @return 时间戳
     */  
    public static Date getRelativeDay(int days) {  
    	Date currentDate = DateUtil.now();
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(currentDate);
    	calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)+days);
    	return calendar.getTime();
    } 
    
    /** 
     * Get the specified date based on specified date.
     * <p> 
     * 获取相对于指定时间  前后指定天数的时间
     *  
     * @param days 具体天数,可以为正负
     *  
     * @return 时间戳
     */  
    public static Date getRelativeDay(Date oriDate, int days) {  
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(oriDate);
    	calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)+days);
    	return calendar.getTime();
    } 
    
    /** 
     * Get the specified date based on current minute.
     * <p> 
     * 获取相对于当前时间  前后指定分钟的时间
     *  
     * @param minutes 具体分钟数,可以为正负
     *  
     * @return 时间戳
     */  
    public static Date getRelativeMinute(int minutes) {  
    	Date currentDate = DateUtil.now();
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(currentDate);
    	calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+minutes);
    	return calendar.getTime();
    } 
    
    /** 
     * Get the specified date based on specified date.
     * <p> 
     * 获取相对于指定时间  前后指定分钟的时间
     *  
     * @param minutes 具体分钟数,可以为正负
     *  
     * @return 时间戳
     */  
    public static Date getRelativeMinute(Date oriDate, int minutes) {  
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(oriDate);
    	calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+minutes);
    	return calendar.getTime();
    } 
    
    /** 
     * Get the specified date based on current minute.
     * <p> 
     * 获取相对于当前时间  前后指定秒钟的时间
     *  
     * @param seconds 具体秒数,可以为正负
     *  
     * @return 时间戳
     */  
    public static Date getRelativeSecond(int seconds) {  
    	Date currentDate = DateUtil.now();
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(currentDate);
    	calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND)+seconds);
    	return calendar.getTime();
    } 
    
    /** 
     * Get the specified date based on specified date.
     * <p> 
     * 获取相对于指定时间  前后指定秒钟的时间
     *  
     * @param seconds 具体秒数,可以为正负
     *  
     * @return 时间戳
     */  
    public static Date getRelativeSecond(Date oriDate, int seconds) {  
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(oriDate);
    	calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND)+seconds);
    	return calendar.getTime();
    } 
    
    /** 
     * 获得当前月的第一天 
     * <p> 
     * HH:mm:ss SS为零 
     *  
     * @return 
     */  
    public static Date firstDayOfMonth() {  
        Calendar cal = calendar();  
        cal.set(Calendar.DAY_OF_MONTH, 1); // M月置1  
        cal.set(Calendar.HOUR_OF_DAY, 0);// H置零  
        cal.set(Calendar.MINUTE, 0);// m置零  
        cal.set(Calendar.SECOND, 0);// s置零  
        cal.set(Calendar.MILLISECOND, 0);// S置零  
        return cal.getTime();  
    }  
  
    private static Date weekDay(int week) {  
        Calendar cal = calendar();  
        cal.set(Calendar.DAY_OF_WEEK, week);  
        return cal.getTime();  
    }  
  
    /** 
     * 获得周五日期 
     * <p> 
     * 注：日历工厂方法{@link #calendar()}设置类每个星期的第一天为Monday，US等每星期第一天为sunday 
     *  
     * @return 
     */  
    public static Date friday() {  
        return weekDay(Calendar.FRIDAY);  
    }  
  
    /** 
     * 获得周六日期 
     * <p> 
     * 注：日历工厂方法{@link #calendar()}设置类每个星期的第一天为Monday，US等每星期第一天为sunday 
     *  
     * @return 
     */  
    public static Date saturday() {  
        return weekDay(Calendar.SATURDAY);  
    }  
  
    /** 
     * 获得周日日期 
     * <p> 
     * 注：日历工厂方法{@link #calendar()}设置类每个星期的第一天为Monday，US等每星期第一天为sunday 
     *  
     * @return 
     */  
    public static Date sunday() {  
        return weekDay(Calendar.SUNDAY);  
    }  
  
    
    /**
     * Switch string to date. <p>
     * 将字符串日期时间转换成java.util.Date类型
     * 
     * @param stringTime
     * @return
     */
    public static Date parseStringToDate(String stringTime){
    	Date rtn = null;
    	try {
	    	switch(stringTime.length()){
		    	case 19:
		    		rtn = parseDatetime(stringTime);
		    		break;
		    	case 16:
		    		rtn = parseDatetimeLackSecond(stringTime);
		    		break;
		    	case 10:
					rtn = parseDate(stringTime);
		    		break;
		    	default:
		    		rtn = parseDatetime(stringTime);
	    	}
    	} catch (ParseException e) {
			e.printStackTrace();
		}
    	return rtn;
    }
    
    /** 
     * Switch string to date.
     * <p> 
     * 将字符串日期时间转换成java.util.Date类型 ,日期时间格式yyyy-MM-dd HH:mm:ss 
     *  
     * @param datetime 
     * @return 
     */  
    public static Date parseDatetime(String datetime) throws ParseException {  
        return datetimeFormat.parse(datetime);  
    }  
  
    /** 
     * Switch string to date.
     * <p> 
     * 将字符串日期时间转换成java.util.Date类型 ,日期时间格式yyyy-MM-dd HH:mm 
     *  
     * @param datetime 
     * @return 
     */  
    public static Date parseDatetimeLackSecond(String datetime) throws ParseException {  
    	return datetimeFormatLackSecond.parse(datetime);  
    }  
    
    /** 
     * 将字符串日期转换成java.util.Date类型 
     *<p> 
     * 日期时间格式yyyy-MM-dd 
     *  
     * @param date 
     * @return 
     * @throws ParseException 
     */  
    public static Date parseDate(String date) throws ParseException {  
        return dateFormat.parse(date);  
    }  
  
    /** 
     * 将字符串日期转换成java.util.Date类型 
     *<p> 
     * 时间格式 HH:mm:ss 
     *  
     * @param time 
     * @return 
     * @throws ParseException 
     */  
    public static Date parseTime(String time) throws ParseException {  
        return timeFormat.parse(time);  
    }  
  
    /** 
     * 根据自定义pattern将字符串日期转换成java.util.Date类型 
     *  
     * @param datetime 
     * @param pattern 
     * @return 
     * @throws ParseException 
     */  
    public static Date parseDatetime(String datetime, String pattern)  
            throws ParseException {  
        SimpleDateFormat format = (SimpleDateFormat) datetimeFormat.clone();  
        format.applyPattern(pattern);  
        return format.parse(datetime);  
    } 
    
    
}
