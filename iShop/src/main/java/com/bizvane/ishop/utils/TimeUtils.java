package com.bizvane.ishop.utils;

import com.bizvane.ishop.constant.Common;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ZhouZhou on 2016/6/24.
 */
public class TimeUtils {

    //前几天的日期
    //@param day
    public static String beforDays(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -day);
        String yesterday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());

        return yesterday;
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


    public static void main(String[] args) throws Exception {

        System.out.println(getWeek("2016-6-18"));
    }
}
