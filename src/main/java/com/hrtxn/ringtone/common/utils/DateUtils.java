package com.hrtxn.ringtone.common.utils;

import jodd.datetime.JDateTime;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Author:lile
 * Date:2019/7/15 12:09
 * Description:
 */
@Slf4j
public final class DateUtils {

    public static String FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";

    private DateUtils() {

    }

    /**
     * 获得时间的毫秒数.
     *
     * @param date 时间
     * @return 毫秒数
     */
    public static long getNow(Date date) {
        return date.getTime();
    }

    /***
     * 使用yyyy-MM-dd HH:mm:ss格式化当前时间.
     * @return 格式化的日期字符串
     */
    public static String formatNow() {
        return formatNow("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 使用pattern格式化当前时间
     *
     * @param pattern 格式
     * @return 格式化的日期字符串
     */
    public static String formatNow(String pattern) {
        return format(new Date(), pattern);
    }


    /**
     * 使用yyyyMMddHHmmss 格式化当前时间。
     *
     * @return
     */
    public static String getTimestamp() {
        return formatNow("yyyyMMddHHmmss");
    }

    /**
     * 获得时间天的毫秒数.
     *
     * @param date 时间
     * @return 毫秒数
     */
    public static long getDay(Date date) {
        return getTimeInMillis(format(date, "yyyy-MM-dd"), "yyyy-MM-dd");
    }

    /**
     * 获得时间月的毫秒数.
     *
     * @param date 时间
     * @return 毫秒数
     */
    public static long getMonth(Date date) {
        return getTimeInMillis(format(date, "yyyy-MM"), "yyyy-MM");
    }

    /**
     * 获得时间年的毫秒数.
     *
     * @param date 时间
     * @return 毫秒数
     */
    public static long getYear(Date date) {
        return getTimeInMillis(format(date, "yyyy"), "yyyy");
    }

    /**
     * 把日期格式化成字符串.
     *
     * @param date    待格式化的日期
     * @param pattern 格式
     * @return 格式化的日期字符串
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String format(Date date) {
        return format(date, FORMAT_DEFAULT);
    }

    /**
     * 根据指定年月日获取日期.
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 日期
     */
    public static Date getDate(int year, int month, int day) {
        JDateTime jdt = new JDateTime(year, month, day);
        return jdt.convertToDate();
    }


    /**
     * 根据指定年月日获取毫秒时间.
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 时间
     */
    public static long getTimeInMillis(int year, int month, int day) {
        JDateTime jdt = new JDateTime(year, month, day);
        return jdt.getTimeInMillis();
    }

    /**
     * 把毫秒日期格式化成字符串.
     *
     * @param date    待格式化的毫秒日期
     * @param pattern 格式
     * @return 格式化的日期字符串
     */
    public static String format(long date, String pattern) {
        Date newdate = new Date(date);
        return format(newdate, pattern);
    }

    /**
     * 把指定格式的字符串转换成日期.
     *
     * @param text    待格式化的字符串
     * @param pattern 格式
     * @return 转换后的日期
     */
    public static Date getDate(String text, String pattern) {
        if (text == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(text);
        } catch (ParseException e) {
            // TODO 自动生成 catch 块
            log.error("将指定格式的字符串转换成日期出错！", e);
        }
        return null;
    }

    /**
     * 把指定格式的字符串转换成毫秒日期.
     *
     * @param text    待格式化的字符串
     * @param pattern 格式
     * @return 转换后的毫秒日期;如果格式不正确,返回0
     */
    public static long getTimeInMillis(String text, String pattern) {
        Date d = getDate(text, pattern);
        return d == null ? 0L : d.getTime();
    }

    /**
     * 把时间间隔转换为字符串形式,*天*小时*分*秒.
     *
     * @param interval 时间间隔,须大于0
     * @return 天小时分秒的表述形式.如果interval不大于0, 返回空(" ").
     */
    public static String timeInterval(long interval) {
        if (interval <= 0) {
            return "";
        }
        long seconds = interval / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        hours = hours % 24;
        minutes = minutes % 60;
        seconds = seconds % 60;
        return days + "天" + hours + "小时" + minutes + "分" + seconds + "秒";
    }

    /**
     * 获取从指定日期到现在的年小时分秒表述.
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 天小时分秒的表述形式
     */
    public static String timeIntervalFrom(int year, int month, int day) {
        return timeInterval(System.currentTimeMillis() - getTimeInMillis(year, month, day));
    }

    /**
     * 获取每个月的天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int getDateNum(int year, int month) {
        if (month == 2) {
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) return 29;
            else return 28;
        }
        switch (month) {
            case 1:
                return 31;
            case 3:
                return 31;
            case 4:
                return 30;
            case 5:
                return 31;
            case 6:
                return 30;
            case 7:
                return 31;
            case 8:
                return 31;
            case 9:
                return 30;
            case 10:
                return 31;
            case 11:
                return 30;
            case 12:
                return 31;
        }
        return 0;
    }

    /**
     * 获取当前年份
     *
     * @return
     */
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 获取当前月份
     */
    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * 获取指定日期的年份
     *
     * @param date
     * @return
     */
    public static int getyear(Date date) {
        return Integer.parseInt(format(date, "yyyy"));
    }

    /**
     * 获取指定日期的月份
     *
     * @param date
     * @return
     */
    public static int getmonth(Date date) {
        return Integer.parseInt(format(date, "MM"));
    }

    public static int getday(Date date) {
        return Integer.parseInt(format(date, "dd"));
    }

    public static String getMonthTime(int year, int month) {
        if (month == 0) {
            return year + "-%";
        } else if (month < 10) {
            return year + "-0" + month + "-%";

        } else {
            return year + "-" + month + "-%";
        }
    }

    public static Date getDate(JDateTime now) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(now.toString("yyyy-MM-dd"));
            return date;
        } catch (Exception e) {
            return null;
        }

    }

    public static String operateBeginDate(String beginDate) {
        if (beginDate == null || "".equals(beginDate)) {
            return beginDate;
        }
        return beginDate + " 00:00:00";
    }

    public static String operateEndDate(String endDate) {
        if (endDate == null || "".equals(endDate)) {
            return endDate;
        }
        return endDate + " 23:00:00";
    }


    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = format.format(today);
        return result;
    }

    /**
     * 获取未来 第 past 天的日期
     *
     * @param past
     * @return
     */
    public static String getFetureDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    /**
     * 将时间转为随机数
     *
     * @return
     */
    public static Integer getTimeRadom() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);//获取年份
        int month = ca.get(Calendar.MONTH) + 1;//获取月份
        int day = ca.get(Calendar.DATE);//获取日 
        int minute = ca.get(Calendar.MINUTE);//分    
        int hour = ca.get(Calendar.HOUR);//小时    
        int second = ca.get(Calendar.SECOND);//秒    
        Integer one = year + month + day + minute + hour + second;
        int week = ca.get(Calendar.WEEK_OF_YEAR);
        String radom = one.toString() + (week<10?"0"+week:""+week);
        return Integer.parseInt(radom);
    }

    /**
     * 当前时间字符串
     * @return
     */
    public static String getTime() {
        Calendar ca = Calendar.getInstance();
        Integer year = ca.get(Calendar.YEAR);//获取年份
        Integer month = ca.get(Calendar.MONTH) + 1;//获取月份
        Integer day = ca.get(Calendar.DATE);//获取日 
        Integer minute = ca.get(Calendar.MINUTE);//分    
        Integer hour = ca.get(Calendar.HOUR);//小时    
        Integer second = ca.get(Calendar.SECOND);//秒    
        return year.toString()+ month.toString() + day.toString() + minute.toString() + hour.toString()+ second.toString();
    }


    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1,Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }
}
