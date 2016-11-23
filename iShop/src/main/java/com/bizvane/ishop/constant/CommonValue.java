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
    //会员任务分配
    public final static String table_vip_task_allocation = "vip_task_allocation";
    //用户登录日志
    public final static String table_login_log = "login_log";
}
