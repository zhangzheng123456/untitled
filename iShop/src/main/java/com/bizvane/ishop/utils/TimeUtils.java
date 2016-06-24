package com.bizvane.ishop.utils;

import com.bizvane.ishop.constant.Common;

import java.util.Calendar;

/**
 * Created by ZhouZhou on 2016/6/24.
 */
public class TimeUtils {

    public static String beforDays(int day){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,   -day);
        String yesterday = Common.DATETIME_FORMAT_DAY.format(cal.getTime());

        return yesterday;
    }

    public static void main(String[] args){

        System.out.println(beforDays(1));
    }
}
