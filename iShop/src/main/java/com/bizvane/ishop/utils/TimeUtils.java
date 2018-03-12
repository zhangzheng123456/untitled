package com.bizvane.ishop.utils;

import com.bizvane.ishop.constant.Common;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ZhouZhou on 2016/6/24.
 */
public class TimeUtils {

    public static final SimpleDateFormat DATETIME_FORMAT_DATE_MS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.111");
    //前几天的日期
    //@param day
    public static String beforDays(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -day);
        String yesterday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());

        return yesterday;
    }
    public static Date getLastDateByDay(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, i);
        return cal.getTime();
    }
    //后几月的日期
    public static Date getLastDate(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, i);
        return cal.getTime();
    }

    //后几分钟的日期
    public static Date getLastMin(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, i);
        return cal.getTime();
    }

    //某日零点
    public static String getTimeOf0(String date) throws Exception {
        Calendar cal = Calendar.getInstance();
        Date time = Common.DATETIME_FORMAT_DAY.parse(date);
        cal.setTime(time);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 0);
        return Common.DATETIME_FORMAT.format(cal.getTime());
    }


    public static int getYearWeek(String time) throws ParseException {
        Calendar ca = Calendar.getInstance();
        SimpleDateFormat dsf = new SimpleDateFormat("yyyyMMdd");
        Date date = dsf.parse(time);

        ca.setTime(date);

        String year = time.substring(0, 4);
        String startDate = year + "0101";
        Date date1 = dsf.parse(startDate);
        Calendar ca1 = Calendar.getInstance();
        ca1.setTime(date1);

        String NextstartDate = (Integer.parseInt(year) + 1) + "0101";
        Date date2 = dsf.parse(NextstartDate);
        Calendar ca2 = Calendar.getInstance();
        ca2.setTime(date2);

        int weekDay = ca1.get(Calendar.DAY_OF_WEEK);
        int nextWeekDay = ca2.get(Calendar.DAY_OF_WEEK);
        int days = ca.get(Calendar.DAY_OF_YEAR);
        int intervalDays = 7 - (weekDay - 2);//今年第一周有几天

        int okDay = weekDay - 1;//今年的1号是星期几

        int koDay = nextWeekDay - 1;//下一年的1号是星期几

        if (intervalDays == 8) {
            intervalDays = 1;
        }


        if (okDay == 0) {
            okDay = 7;
        }


        if (koDay == 0) {
            koDay = 7;
        }

        if (okDay > 4) {
            if (days - intervalDays <= 0) {
                return getYearWeek((Integer.parseInt(year) - 1) + "1231");
            } else {
                if (ca.get(Calendar.WEEK_OF_YEAR) == 1) {
                    if (koDay >= 4) {
                        return 1;
                    } else {
                        return (days - intervalDays - 1) / 7 + 1;
                    }
                } else {
                    return (days - intervalDays - 1) / 7 + 1;
                }

            }

        } else {
            if (days - intervalDays <= 0) {
                return 1;
            } else {
                if (ca.get(Calendar.WEEK_OF_YEAR) == 1) {
                    if (koDay >= 4) {
                        return (days - intervalDays - 1) / 7 + 2;
                    } else {
                        return 1;


                    }
                } else {
                    return (days - intervalDays - 1) / 7 + 2;
                }
            }

        }
    }
    public static String getWeek2(String time) throws Exception {
        String a_time_W;
        time = time.replace("-", "");
        if (TimeUtils.getYearWeek(time) >= 10) {
            a_time_W = String.valueOf(TimeUtils.getYearWeek(time));
        } else {
            a_time_W = "0" + String.valueOf(TimeUtils.getYearWeek(time));
        }
        return removerZero(a_time_W);
    }
    //某日所属的周一到周日
    public static String getWeek(String date) throws Exception {
        Date time = Common.DATETIME_FORMAT_DAY.parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值

        String monday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());
        cal.add(Calendar.DATE, 6);
        String sunday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());

        return monday+" "+sunday;
    }
    /**
     * 去除日期中的零
     *
     * @param value
     * @return
     */
    public static String removerZero(String value) {
        if (StringUtils.isNotBlank(value)) {
            int data = 0;
            data = Integer.parseInt(value);
            if (data < 10) {
                value = value.replace("0", "");
                return value;
            }
        }
        return value;
    }
    /**
     * get current time with milliseconds
     *
     * @return
     */
    public static String getTimeWithMS(long timeInMillis) {
        return getTime(timeInMillis, DATETIME_FORMAT_DATE_MS);
    }
    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    public static String formatDateByPattern(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    public static String getCron(java.util.Date date) {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        return formatDateByPattern(date, dateFormat);
    }


    /**
     * 某日所属的周一到周日
     *
     * @param date (yyyy-MM-dd)
     * @return
     * @throws Exception
     */
    public static String getWeek2Day(String date) throws Exception {
        Date time = Common.DATETIME_FORMAT_DAY.parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值

        String monday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String tuesday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String wednesday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String thursday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String friday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String saturday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String sunday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());

        return monday + "," + tuesday + "," + wednesday + "," + thursday + "," + friday + "," + saturday + "," + sunday;
    }

    /**
     * 某日所属的周一到周日
     *
     * @param date (yyyy-MM-dd)
     * @return
     * @throws Exception
     */
    public static String getWeek2Day1(String date) throws Exception {
       SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
        Date time = simpleDateFormat.parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值

        String monday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String tuesday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String wednesday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String thursday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String friday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String saturday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String sunday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());

        return monday + "," + tuesday + "," + wednesday + "," + thursday + "," + friday + "," + saturday + "," + sunday;
    }


    //获取该月下的所有日期
    public static List<String> getMonthAllDays(String getDate) {
        List<String> dateList = new ArrayList<String>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String str = getDate;
            Date d = sdf.parse(str);
            // 月初
            System.out.println("月初" + sdf.format(getMonthStart(d)));
            // 月末
            System.out.println("月末" + sdf.format(getMonthEnd(d)));

            Date date = getMonthStart(d);
            Date monthEnd = getMonthEnd(d);
            while (!date.after(monthEnd)) {
                dateList.add(sdf.format(date));
                date = getNext(date);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return dateList;
    }



    private static Date getMonthStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int index = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, (1 - index));
        return calendar.getTime();
    }

    private static Date getMonthEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        int index = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, (-index));
        return calendar.getTime();
    }

    private static Date getNext(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    public static List<String> getYearAllDays(String getDate){
        //某年的所有月
        List<String> dates = new ArrayList<String>();
        for (int j = 1; j < 13; j++) {
            String date_part = "";
            if (j <= 9) {
                date_part = "0" + j;
            } else {
                date_part = j + "";
            }
            //获取该年的所有月2015-12-01
            dates.add(getDate.substring(0, 5) + date_part);
        }
        return dates;
    }

    /**
     * 根据指定日期 需要减去的天数 获取前一天的新日期
     *
     * @param date 日期       2016-5-17
     * @return 2016-5-16
     */
    public static Date getNextDay(String date,int i) throws Exception {
        SimpleDateFormat DATETIME_FORMAT_DAY = new SimpleDateFormat("yyyy-MM-dd");
        Date time = DATETIME_FORMAT_DAY.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.DAY_OF_MONTH, -i);
        time = calendar.getTime();
        return time;
    }

    /**
     * 根据指定日期 需要减去的天数 获取后一天的新日期
     *
     * @param date 日期       2016-5-17
     * @return 2016-5-16
     */
    public static Date getToDay(String date, int i) throws Exception{
        Date time = Common.DATETIME_FORMAT_DAY.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.DAY_OF_MONTH, i);
        time = calendar.getTime();
        return time;
    }

    /**
     * 计算两个日期的差
     *
     * @param date1
     * @param date2
     * @return
     * @throws ParseException
     */
    public static int calculateDate(String date1, String date2, SimpleDateFormat simpleDateFormat) throws ParseException {

        Date now = simpleDateFormat.parse(date1);
        Date date = simpleDateFormat.parse(date2);
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        return (int) day;
    }

    /**
     * @param :@param firstStr
     * @param :@param secondStr
     * @Title: compareDateTime
     * @Description: 比较日期大小
     * first < second true
     */
    public static boolean compareDateTime(String firstStr, String secondStr, SimpleDateFormat simpleDateFormat) {
        Date firstDate = null;
        Date secondDate = null;
        try {
            firstDate = simpleDateFormat.parse(firstStr);
            secondDate = simpleDateFormat.parse(secondStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long firstLongTime = firstDate.getTime();
        long secondLongTime = secondDate.getTime();
        if (firstLongTime > secondLongTime) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取两个日期之间的日期(包含开始，结束时间)
     * @param start 开始日期
     * @param end 结束日期
     * @return 日期集合
     */
    public static List<String> getBetweenDates(String start, String end)throws  Exception {
        Date startDate = Common.DATETIME_FORMAT_DAY.parse(start);
        Date endDate = Common.DATETIME_FORMAT_DAY.parse(end);
        List<String> result = new ArrayList<String>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(startDate);
        result.add(Common.DATETIME_FORMAT_DAY.format(tempStart.getTime()));
        tempStart.add(Calendar.DAY_OF_YEAR, 1);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(endDate);
        while (tempStart.before(tempEnd)) {
            result.add(Common.DATETIME_FORMAT_DAY.format(tempStart.getTime()));
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        result.add(Common.DATETIME_FORMAT_DAY.format(tempEnd.getTime()));

        //当开始时间等于结束时间时，取其中一个时间
        if(result.size()==2&&result.get(0).toString().equals(result.get(1).toString())){
            List<String> tempList= new ArrayList<String>();
            for(String date:result){
                if(!tempList.contains(date)){
                    tempList.add(date);
                }
            }
            return tempList;
        }else{
            return result;
        }
    }
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }


    public static int getDiscrepantDays(Date dateStart, Date dateEnd) {
        return (int) ((dateEnd.getTime() - dateStart.getTime()) / 1000 / 60 / 60 / 24);
    }

    public static Date timeStampToDate(String timeStamp){
        Long timestamp = Long.parseLong(timeStamp);
        Date date = new Date(timestamp);
        return date;
    }

}
