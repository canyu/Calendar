package com.cam.lib.calendar.util;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间类公共类
 * Created by yuCan on 17-2-8.
 */

public class ClockUtil {

    public final static int FIRST_DAY = 57600;//第一天的时间(从1970.1.2号开始)
    public final static int FIRST_DAY_YEAR = 1970;//
    public final static int FIRST_DAY_MONTH = 0;//
    public final static int FIRST_DAY_DAY = 2;//
    public final static int MILLIS_PER_SECOND = 1000;//一秒包含的毫秒数
    public final static int DAYS_PER_WEEK = 7;//一个星期包含的天数
    public final static int HOURS_PER_DAY = 24;//一天包含的小时数
    public final static int SECONDS_PER_MINUTE = 60;//一分钟包含的秒数
    public final static int WEEKS_PER_MONTH = 6;//一个月包含的星期数(默认设置为6)
    public final static int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * SECONDS_PER_MINUTE;//一天的秒数
    public final static int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;//一天的秒数
    public final static int SECONDS_PER_WEEK = SECONDS_PER_DAY * DAYS_PER_WEEK;//一个星期的秒数
    public final static int DAYS_PER_MONTH = DAYS_PER_WEEK * WEEKS_PER_MONTH;//一个月包含的天数(默认为6个星期)

    public final static String TIME_FORMAT_DAY = "dd";
    public final static String TIME_FORMAT_DATE = "yyyy.MM.dd";
    public final static String TIME_FORMAT_FUUL = "yyyy.MM.dd HH.mm.ss";

    private synchronized static Calendar getCalendar(int time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(((long)time) * MILLIS_PER_SECOND);
        return cal;
    }

    public synchronized static int getCurrentTime(){
        return (int)(System.currentTimeMillis() / MILLIS_PER_SECOND);
    }
    @SuppressLint("SimpleDateFormat")
    public synchronized static String getTimeFormat(long time, String format){
        long lTime = time * MILLIS_PER_SECOND;
        Date date = new Date();
        date.setTime(lTime);
        SimpleDateFormat simpleFormat = new SimpleDateFormat();
        simpleFormat.applyPattern(format);
        return simpleFormat.format(date);
    }

    /**
     * 传入以秒作为单位的时间
     * @return int[]的length = 6
     * <p>[年,月(0-11),日(1-31),时,分,秒]</p>
     */
    public synchronized static int[] getTimeToArray(int time){
        Calendar cal = getCalendar(time);
        return new int[]{
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND)
        };
    }

    /**
     * 传入[年,月（0-11）,日(1-31),时,分,秒]
     */
    public synchronized static int getTimeFromArray(@NonNull int[] time){
        Calendar cal = Calendar.getInstance();
        cal.set(time[0], time[1],time[2],time[3],time[4],time[5]);
        return (int)(cal.getTimeInMillis() / MILLIS_PER_SECOND);
    }

    /**
     * 传入[年,月（0-11）,日(1-31),时,分,秒]
     */
    public synchronized static int getTime(int year, int month, int day){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month,day,0,0,0);
        return (int)(cal.getTimeInMillis() / MILLIS_PER_SECOND);
    }

    public synchronized static int getTimeAtTheBeginningOfToday(){
        int[] arr = getTimeToArray(getCurrentTime());
        arr[3] = arr[4] = arr[5] = 0;
        return getTimeFromArray(arr);
    }

    public synchronized static int getTimeAtTheBeginningOfTheDay(int time){
        int[] arr = getTimeToArray(time);
        arr[3] = arr[4] = arr[5] = 0;
        return getTimeFromArray(arr);
    }

    public synchronized static int getTodayOfLastMonth(int time){
        int[] arr = getTimeToArray(time);
        if(arr[1] == 0){
            arr[0] --;
            arr[1] = 11;
        }else{
            arr[1] --;
        }
        int monthDay = getMonthDayNum(arr[0], arr[1] + 1);
        if(monthDay < arr[2]){
            arr[2] = monthDay;
        }
        return getTimeFromArray(arr);
    }

    public synchronized static int getTodayOfNextMonth(int time){
        int[] arr = getTimeToArray(time);
        if(arr[1] == 11){
            arr[0] ++;
            arr[1] = 0;
        }else{
            arr[1] ++;
        }
        int monthDay = getMonthDayNum(arr[0], arr[1] + 1);
        if(monthDay < arr[2]){
            arr[2] = monthDay;
        }
        return getTimeFromArray(arr);
    }
    /**
     *
     * @param year 年份
     * @param month 月份(1-12)
     * @return 该月的天数
     */
    public synchronized static int getMonthDayNum(int year, int month){
        boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                return isLeapYear ? 29 : 28;
            default:
                return 30;
        }
    }
    /**
     * 取time所在该星期的第几天
     * @param time 时间
     * @param isMondayFirstDay 是否周一为第一天(否则表示周日为第一天)
     * @return 1-7
     */
    public synchronized static int getCurrentWeekDay(int time, boolean isMondayFirstDay){
        Calendar cal = getCalendar(time);
        int curWeekPos = cal.get(Calendar.DAY_OF_WEEK);
        if(isMondayFirstDay){
            return curWeekPos == Calendar.SUNDAY ? 7 : curWeekPos - 1;
        }else{
            return curWeekPos;
        }
    }

    /**
     * 取该日期所在周的第一天的时间戳
     * @param year 年
     * @param month  月
     * @param day 日
     * @param isMondayFirstDay 是否周一为第一天(否则表示周日为第一天)
     * @return 1-7
     */
    public synchronized static int getFirstWeekDay(int year, int month, int day, boolean isMondayFirstDay){
        int dayTime = getTime(year, month, day);
        return dayTime - (ClockUtil.getCurrentWeekDay(dayTime, isMondayFirstDay) - 1) * ClockUtil.SECONDS_PER_DAY;
    }

    /**
     * 是否时周末(周六周日)
     * @param time 时间戳
     */
    public synchronized static boolean isWeekend(int time){
        Calendar cal = getCalendar(time);
        switch (cal.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SATURDAY:
            case Calendar.SUNDAY:{
                return true;
            }
            default:{
                return false;
            }
        }
    }

    /**
     * 是否是第一天的前一天
     * @param year 年
     * @param month 月(0-11)
     * @param day 日
     */
    public synchronized static boolean isDayBeforeFirstDay(int year, int month, int day){
        return year == FIRST_DAY_YEAR && month == FIRST_DAY_MONTH && day == (FIRST_DAY_DAY - 1);
    }

    public synchronized static boolean isDaySameWeekWithFirstDay(int year, int month, int day){
        switch (year){
            case FIRST_DAY_YEAR: return month == FIRST_DAY_MONTH && day == (FIRST_DAY_DAY - 1);
            case FIRST_DAY_YEAR - 1: return month == 11 && day > 27;
            default: return false;
        }
    }

    public synchronized static boolean isValidDay(int year, int month, int day){
        if(year >FIRST_DAY_YEAR){
            return true;
        }
        if(year == FIRST_DAY_YEAR){
            if(month != FIRST_DAY_MONTH || day != FIRST_DAY_DAY - 1){
                return true;
            }
        }
        return false;
    }

    public synchronized static boolean isSameYearMonth(int time1, int time2){
        int[] arr = getTimeToArray(time1);
        int year = arr[0], month = arr[1];
        arr = getTimeToArray(time2);
        return year == arr[0] && month == arr[1];
    }
}
