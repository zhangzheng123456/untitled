package com.bizvane.ishop.constant;

import java.text.SimpleDateFormat;

public class Common {

    //请求发送的类型失败
    public static final String DATABEAN_CODE_ERROR = "-1";
    //请求发送的类型成功
    public static final String DATABEAN_CODE_SUCCESS = "0";

    //系统管理员role
    public static final String ROLE_SYS = "R6000";
    //总经理role
    public static final String ROLE_GM = "R5000";
    //区经role
    public static final String ROLE_AM = "R4000";
    //店长role
    public static final String ROLE_SM = "R3000";
    //导购role
    public static final String ROLE_STAFF = "R2000";

    //是否可用（是）
    public static final String IS_ACTIVE_Y = "Y";
    //是否可用（否）
    public static final String IS_ACTIVE_N = "N";

    //已授权
    public static final String IS_AUTHORIZE_Y = "Y";
    //未授权
    public static final String IS_AUTHORIZE_N = "N";

    //时间格式
    public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final SimpleDateFormat DATETIME_FORMAT_DAY = new SimpleDateFormat("yyyy-MM-dd");

    //时间类型（日）
    public static final String TIME_TYPE_DAY = "1";
    //时间类型（周）
    public static final String TIME_TYPE_WEEK = "2";
    //时间类型（月）
    public static final String TIME_TYPE_MONTH = "3";
    //时间类型（年）
    public static final String TIME_TYPE_YEAR = "4";

}
