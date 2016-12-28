package com.bizvane.ishop.constant;

/**
 * Created by ZhouZhou on 2016/9/22.
 */
public class CommonValue {

    //OSS授权秘钥常量
    public final static String ACCESS_KEY_ID = "fKjYsWXmOd7MHG4I";
    public final static String ACCESS_KEY_SECRET = "DTAJNaALuE6MErpVrC5y3l7tYxGUPd";
    public final static String ENDPOINT = "http://oss-cn-hangzhou.aliyuncs.com";
    //OSS请求访问的Bucket名字
    public final static String bucketName = "products-image";


    //桃花季 微盟账号
//    public final static String appID = "1bcd0f1a7c6188deecf5009adcfd7906";
//    public final static String appSecert = "0aa192196369e848bce12c664bbdafee";
    public final static String CLIENT_ID = "6A18998AA80E9AF0A0B18A1CA26295CB";
    public final static String CLIENT_SECRET = "D747A297458D75239387F46DC95D2B35";
    public final static String REDIRECT_URL = "http://ishop.dev.bizvane.com/api/weimob/auth";

//    public final static String wechat_url = "http://wechat.app.bizvane.com/app/wechat";
    public final static String wechat_url = "http://wechat.dev.bizvane.com/app/wechat";

//    public final static String ishop_url = "http://ishop.app.bizvane.com/";
    public final static String ishop_url = "http://ishop.dev.bizvane.com/";

    //table名（mongodb）
    //用户行为日志
    public final static String table_log_user_action = "log_person_action";
    //产品分享日志
    public final static String table_log_production_share = "log_product_share";
    //会员信息
    public final static String table_vip_info = "vip_info";
    //会员回访记录
    public final static String table_vip_back_record = "vip_back_record";
    //会员回访记录(new)
    public final static String table_vip_message_content = "vip_message_content";
    //用户操作日志
    public final static String table_log_user_operation = "log_user_operation";
    //用户登录日志
    public final static String table_login_log = "login_log";
    //用户签到记录
    public final static String table_sign_content="staff_sign";
    //秀搭主表
    public final static String table_shop_match_def = "shop_match_def";
    //秀搭附表
    public final static String table_shop_match_rel = "shop_match_rel";
    //会员标签
    public final static String table_vip_label_def = "vip_label_def";
    //错误日志
    public final static String table_error_log = "error_log";
    //会员活动执行
    public final static String table_vip_activity_allocation = "vip_activity_allocation";


    //参数
    //新增会员是否验证单号
    public final static String ADD_VIP_CHECK_BILL = "ADD_VIP_CHECK_BILL";
    //新增会员是否输入卡号
    public final static String ADD_VIP_INPUT_CARDNO = "ADD_VIP_INPUT_CARDNO";
}
