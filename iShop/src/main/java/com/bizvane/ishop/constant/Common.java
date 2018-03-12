package com.bizvane.ishop.constant;

import okhttp3.MediaType;

import java.text.SimpleDateFormat;

public class Common {

    //请求发送的类型失败
    public static final String DATABEAN_CODE_ERROR = "-1";
    //请求发送的类型成功
    public static final String DATABEAN_CODE_SUCCESS = "0";
    //请求发送的类型成功
    public static final String DATABEAN_CODE_REDIRECT = "1";

    //系统管理员role
    public static final String ROLE_SYS = "R6000";
    //集团管理员role
    public static final String ROLE_CM = "R5500";
    //企业管理员
    public static final String ROLE_GM = "R5000";
    //品牌管理role
    public static final String ROLE_BM = "R4800";
    //区经role
    public static final String ROLE_AM = "R4000";
    //城市经理role
    public static final String ROLE_CITY = "R3500";
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

    public static final SimpleDateFormat DATETIME_FORMAT_NUM = new SimpleDateFormat("yyyyMMddHHmmss");

    public static final SimpleDateFormat DATETIME_FORMAT_DAY = new SimpleDateFormat("yyyy-MM-dd");

    public static final SimpleDateFormat DATETIME_FORMAT_DAY_NO = new SimpleDateFormat("yyyyMMdd");

    public static final SimpleDateFormat DATETIME_FORMAT_DAY_NUM = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static final SimpleDateFormat DATETIME_DAY = new SimpleDateFormat("MMdd");
    //目标时间类型（日）
    public static final String TIME_TYPE_DAY = "D";
    //目标时间类型（周）
    public static final String TIME_TYPE_WEEK = "W";
    //目标时间类型（月）
    public static final String TIME_TYPE_MONTH = "M";
    //目标时间类型（年）
    public static final String TIME_TYPE_YEAR = "Y";

    //进行分割作用  比如一个导购可能所属很多个店铺 §店铺,§店铺...  或者分割商品之类
    public static final String SPECIAL_HEAD = "§";

    public static final String VIP_LABEL_TYPE_SYS = "sys";
    //签到
    public static final String STATUS_SIGN_IN = "0";
    //签退
    public static final String STATUS_SIGN_OUT = "-1";

    //导出数量限制10000条
    public static int EXPORTEXECLCOUNT = 10000;


    public static final String ACTION_ADD = "新增";

    public static final String ACTION_DEL = "删除";

    public static final String ACTION_UPD = "编辑";

    public static final String ACTION_CHANGPASS = "修改密码";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //未执行
    public static final String ACTIVITY_STATUS_0 = "0";
    //执行中
    public static final String ACTIVITY_STATUS_1 = "1";
    //已结束
    public static final String ACTIVITY_STATUS_2 = "2";


    public static final String SEND_TYPE_WX = "wxmass";

    public static final String SEND_TYPE_SMS = "sms";

    public static final String SEND_TYPE_EMAIL = "email";

    public static final String CORN_EXPRESSION = "s min h d m ?";

    public static final String BUCKET_NAME = "products-image";

    public static final String VIP_SCREEN_BIRTH_KEY = "1";
    public static final String VIP_SCREEN_ANNIVERSARY_KEY = "2";
    public static final String VIP_SCREEN_CARDNO_KEY = "11";
    public static final String VIP_SCREEN_STORE_KEY = "14";
    public static final String VIP_SCREEN_USER_KEY = "15";
    public static final String VIP_SCREEN_GROUP_KEY = "16";
    public static final String VIP_SCREEN_OPENID_KEY = "18";

    //拓展参数定义日期类型
    public static final String VIP_PARAM_TYPE_DATE = "date";
    public static final String VIP_PARAM_TYPE_TEXT = "text";
    public static final String VIP_PARAM_TYPE_SELECT = "select";

    public static final String DATE_FORMAT = "-01";

    public static final String TEMPLATE_NAME_1 = "服务状态提醒";

    public static final String TEMPLATE_NAME_2 = "邀请注册成功通知";

    public static final String TEMPLATE_NAME_3 = "任务处理通知";

    public static final String TEMPLATE_NAME_4 = "积分到期提醒";

    public static final String TEMPLATE_NAME_5 = "会员等级变更提醒";

}
