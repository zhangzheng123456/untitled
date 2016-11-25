package com.bizvane.ishop.constant;

import java.text.SimpleDateFormat;

public class Common {

    //请求发送的类型失败
    public static final String DATABEAN_CODE_ERROR = "-1";
    //请求发送的类型成功
    public static final String DATABEAN_CODE_SUCCESS = "0";

    //系统管理员role
    public static final String ROLE_SYS = "R6000";
    //企业管理员
    public static final String ROLE_GM = "R5000";
    //品牌管理role
    public static final String ROLE_BM = "R4800";
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

    public static final SimpleDateFormat DATETIME_FORMAT_DAY_NO = new SimpleDateFormat("yyyyMMdd");

    public static final SimpleDateFormat DATETIME_FORMAT_DAY_NUM = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    //目标时间类型（日）
    public static final String TIME_TYPE_DAY = "D";
    //目标时间类型（周）
    public static final String TIME_TYPE_WEEK = "W";
    //目标时间类型（月）
    public static final String TIME_TYPE_MONTH = "M";
    //目标时间类型（年）
    public static final String TIME_TYPE_YEAR = "Y";

    public static final String SPECIAL_HEAD = "§";

    public static final String VIP_LABEL_TYPE_SYS = "sys";
    //签到
    public static final String STATUS_SIGN_IN = "0";
    //签退
    public static final String STATUS_SIGN_OUT = "-1";

    //导出数量限制10000条
    public static int EXPORTEXECLCOUNT =10000;

    //数据显示类型——文本框
    public static final String DATE_SHOW_TYPE_TEXT = "text";
    //数据显示类型——下拉 框
    public static final String DATE_SHOW_TYPE_SELECT = "select";
    //数据显示类型——日历
    public static final String DATE_SHOW_TYPE_DATE = "date";
    //数据显示类型——长文本
    public static final String DATE_SHOW_TYPE_LONGTEXT = "longtext";

    public static  final String ACTION_ADD ="新增";

    public static  final String ACTION_DEL ="删除";

    public static  final String ACTION_UPD ="编辑";

}
