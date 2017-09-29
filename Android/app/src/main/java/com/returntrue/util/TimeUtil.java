package com.returntrue.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by josephwang on 15/7/15.
 */
public class TimeUtil
{
    public static String getTimeText() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Calendar.getInstance().get(Calendar.YEAR));
        int month = (Calendar.getInstance().get(Calendar.MONTH) + 1);
        String monthText = "" + month;
        if (month < 10) {
            monthText = "0" + month;
        }
        buffer.append(monthText);
        JLog.d(JLog.JosephWang, "getTimeText " + buffer.toString());
        return buffer.toString();
    }

    public static  Calendar getDateCelling()
    {
        Calendar now = Calendar.getInstance();

        Calendar result = Calendar.getInstance();
        result.clear();
        result.set(Calendar.YEAR, now.get(Calendar.YEAR));
        result.set(Calendar.MONTH, now.get(Calendar.MONTH));
        result.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
        result.set(Calendar.HOUR, 6);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result;
    }

    public static Calendar getNightFloor()
    {
        Calendar now = Calendar.getInstance();

        Calendar result = Calendar.getInstance();
        result.clear();
        result.set(Calendar.YEAR, now.get(Calendar.YEAR));
        result.set(Calendar.MONTH, now.get(Calendar.MONTH));
        result.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
        result.set(Calendar.HOUR, 18);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);
        return result;
    }

    public static Calendar getDate(int year, int month, int date, int hour, int minute, int second) {
        Calendar today = Calendar.getInstance();
        today.clear();
        today.set(Calendar.YEAR, year);
        today.set(Calendar.MONTH, month);
        today.set(Calendar.DAY_OF_MONTH, date);

        today.set(Calendar.HOUR_OF_DAY, hour);
        today.set(Calendar.MINUTE, minute);
        today.set(Calendar.SECOND, second);
        return today;
    }

    public static String currentTimeStamp() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return formater.format(Calendar.getInstance().getTime());
    }

    public static String calendarToString(Calendar date) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return formater.format(date.getTime());
    }

    public static String calendarToString(Calendar date, String formate) {
        SimpleDateFormat formater = new SimpleDateFormat(formate);
        return formater.format(date.getTime());
    }

    public static String calendarToTimeString(long time, String formate) {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.setTimeInMillis(time);

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = Math.abs(tz.getOffset(cal.getTimeInMillis()) / 3600000);

        date.add(Calendar.HOUR, -offsetInMillis);
        SimpleDateFormat formater = new SimpleDateFormat(formate);
        return formater.format(date.getTime());
    }

    public static String DBTimeString(long time)
    {
       return calendarToTimeString(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static String calendarToString(long date, String formater) {
        Date dates = new Date(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat(formater); // yyyy MM
        // dd
        return dateFormat.format(dates);
    }

    public static String todayCalendarString() {
        return todayCalendarString("yyyy/MM/dd") ;
    }

    public static String todayCalendarString(String formate) {
        Calendar calendar = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat formater = new SimpleDateFormat(formate);
        return formater.format(calendar.getTime());
    }

    public static Calendar stringToCalendar(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(date);
        return calendar;
    }

    public static long stringToCalendarTime(String dateString)
    {
        return stringToCalendar(dateString).getTimeInMillis();
    }

    public static long stringToCalendarTime(String dateString, String formater) {
        SimpleDateFormat sdf = new SimpleDateFormat(formater);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    public static Calendar timeSecondToCalendar(String timeSecond) {
        return timeSecondToCalendar(Long.parseLong(timeSecond));
    }

    public static Calendar timeSecondToCalendar(Long timeSecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(timeSecond);
        return calendar;
    }

    public static Date stringToDate(String serverDateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date date = (Date) sdf.parse(serverDateTime);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Calendar StringToCalendar(String time) {
        JLog.d(JLog.JosephWang, "StringToCalendar time " + time);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date date = (Date) sdf.parse(time);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static Calendar StringToCalendar(String time, String foramter) {
        JLog.d(JLog.JosephWang, "StringToCalendar time " + time);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(foramter);
        try {
            Date date = (Date) sdf.parse(time);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * "2015-07-17T21:29:09.3011250Z"
     * @param utc
     * @return
     */
    public static String UtcTODateString(String utc)
    {
        return UtcTODateString(utc, "yyyy-MM-dd HH:mm:ss");
    }
    public static String UtcTODateString(String utc, String formater)
    {
        try
        {
            if (utc.contains("-") && !formater.contains("-"))
            {
                utc = utc.replaceAll("-", "/");
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(formater);
            Date date = dateFormat.parse(utc);
            TimeZone zone = TimeZone.getDefault();
            Calendar calendar = GregorianCalendar.getInstance(zone);
            calendar.setTime(date);
            int offsetInMillis = zone.getOffset(calendar.getTimeInMillis());
            String offset = String.format("%2d", Math.abs(offsetInMillis / 3600000));
            calendar.add(Calendar.HOUR, Integer.parseInt(offset.trim()));
            return calendarToString(calendar, formater);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static String getLastWorkDateText()
    {
        Calendar current = Calendar.getInstance();
        int dayOfWeek = current.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek != Calendar.MONDAY &&
            dayOfWeek != Calendar.SUNDAY &&
            dayOfWeek != Calendar.SATURDAY)
        {
            current.add(Calendar.DAY_OF_MONTH, -1);
            return calendarToString(current, "yyyy/MM/dd");
        }
        else
        {
            /****禮拜五******/
            if (dayOfWeek == Calendar.MONDAY)
            {
                current.add(Calendar.DAY_OF_MONTH, -3);
            }
            else if (dayOfWeek == Calendar.SATURDAY)
            {
                current.add(Calendar.DAY_OF_MONTH, -1);
            }
            else if (dayOfWeek == Calendar.SUNDAY)
            {
                current.add(Calendar.DAY_OF_MONTH, -2);
            }
        }
        return calendarToString(current, "yyyy/MM/dd");
    }

    public static Calendar beforeMonth(int beforeMonth)
    {
        Calendar now = Calendar.getInstance();
        Calendar want = Calendar.getInstance();
        want.clear();
        want.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH) - beforeMonth, 1);
        return want;
    }

    public static String lastDateInYear()
    {
        Calendar now = Calendar.getInstance();
        Calendar want = Calendar.getInstance();
        want.clear();
        want.set(now.get(Calendar.YEAR), 11, 31);
        return calendarToString(want, "yyyy/MM/dd");
    }

    public static Calendar beforeDate(int beforedate)
    {
        Calendar now = Calendar.getInstance();
        Calendar want = Calendar.getInstance();
        want.clear();
        want.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH),  now.get(Calendar.DAY_OF_MONTH) - beforedate);
        return want;
    }


    public static Calendar beforeYear(int beforeYear)
    {
        Calendar now = Calendar.getInstance();
        Calendar want = Calendar.getInstance();
        want.clear();
        want.set(now.get(Calendar.YEAR) - beforeYear, 0, 1);
        return want;
    }


    public static String beforeDateString(int beforedate, String formater)
    {
        return calendarToString(beforeDate(beforedate), formater);
    }

    public static Calendar springTimeCalendar()
    {
        Calendar now = Calendar.getInstance();
        Calendar want = Calendar.getInstance();
        want.clear();
        want.set(now.get(Calendar.YEAR), 0,  1 );
        return want;
    }

    public static long springTime()
    {
        return springTimeCalendar().getTimeInMillis();
    }

    public static String springTimeDate(String formater)
    {
        return calendarToString(springTimeCalendar(), formater);
    }

    public static Calendar summerTimeCalendar()
    {
        Calendar now = Calendar.getInstance();
        Calendar want = Calendar.getInstance();
        want.clear();
        want.set(now.get(Calendar.YEAR), 3,  1);
        return want;
    }

    public static long summerTime()
    {
        return summerTimeCalendar().getTimeInMillis();
    }

    public static String summerTimeDate(String formater)
    {
        return calendarToString(summerTimeCalendar(), formater);
    }

    public static Calendar fallTimeCalendar()
    {
        Calendar now = Calendar.getInstance();
        Calendar want = Calendar.getInstance();
        want.clear();
        want.set(now.get(Calendar.YEAR), 6,  1 );
        return want;
    }

    public static long fallTime()
    {
        return fallTimeCalendar().getTimeInMillis();
    }

    public static String fallTimeDate(String formater)
    {
        return calendarToString(fallTimeCalendar(), formater);
    }

    public static Calendar winterTimeCalendar()
    {
        Calendar now = Calendar.getInstance();
        Calendar want = Calendar.getInstance();
        want.clear();
        want.set(now.get(Calendar.YEAR), 9,  1 );
        return want;
    }

    public static long winterTime()
    {
        return winterTimeCalendar().getTimeInMillis();
    }

    public static String winterTimeDate(String formater)
    {
        return calendarToString(winterTimeCalendar(), formater);
    }
}
