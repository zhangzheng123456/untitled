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
//    public final static String baiyou_url = "http://www.pub-wechat.bizvane.com/new/outdoor";
//    public final static String ishop_url = "http://ishop.app.bizvane.com/";
//    public final static String wechat_redirect_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_base&component_appid=wxa6780115cc7c1db5#wechat_redirect";
//    //微商城首页
//    public final static String wei_home_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.pub-wechat.bizvane.com%2FbizvaneApp%2Fviews%2Findex.html%3Fopenid%3d@openid@&response_type=code&scope=snsapi_base&state=123&component_appid=wx7e7319a046a816d5#wechat_redirect";
//    //微商城 邀请注册
//    public final static String wei_register_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.pub-wechat.bizvane.com%2FbizvaneApp%2Fviews%2Fmember_center.html%3Fshare_appid%3D@appid@%26openid%3d@openid@&source=1&response_type=code&scope=snsapi_base&state=123&component_appid=wx7e7319a046a816d5#wechat_redirect";
//    //微商城 领取会员卡
//    public final static String wei_get_card_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.pub-wechat.bizvane.com%2FbizvaneApp%2Fviews%2Fmember_center.html%3Fshare_appid%3D@appid@%26openid%3d@openid@&response_type=code&scope=snsapi_base&state=123&component_appid=wx7e7319a046a816d5#wechat_redirect";
//    //微商城 会员中心
//    public final static String wei_member_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.pub-wechat.bizvane.com%2FbizvaneApp%2Fviews%2Fmember_center.html%3Fsource=1&response_type=code&scope=snsapi_base&state=123&component_appid=wx7e7319a046a816d5#wechat_redirect";
//    //微商城 会员任务
//    public final static String wei_task_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@APPID@&redirect_uri=http%3a%2f%2fwechat.app.bizvane.com%2fapp%2fwechat%2fwebAuthorizationTask&response_type=code&scope=snsapi_base&component_appid=wxa6780115cc7c1db5#wechat_redirect";
//    //微商城 会员报名活动
//    public final static String wei_activity_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@APPID@&redirect_uri=http%3a%2f%2fwechat.app.bizvane.com%2fapp%2fwechat%2fwebAuthorizationTask%3Ftype=@TYPE@&response_type=code&scope=snsapi_base&component_appid=wxa6780115cc7c1db5#wechat_redirect";
//    //微商城 生日礼物
//    public final static String wei_birthPresent_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.pub-wechat.bizvane.com%2FbizvaneApp%2Fviews%2Fbirthday.html&response_type=code&scope=snsapi_base&state=123&component_appid=wx7e7319a046a816d5#wechat_redirect";
//    //微商城 会员权益
//    public final static String wei_rule_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.pub-wechat.bizvane.com%2FbizvaneApp%2Fviews%2Fviprules.html&response_type=code&scope=snsapi_base&state=123&component_appid=wx7e7319a046a816d5#wechat_redirect";
//    //获取OpenId
//    public final static String wei_openId_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3a%2f%2fwechat.app.bizvane.com%2fapp%2fwechat%2fgetOpenIdByWx?@match_value@&response_type=code&scope=snsapi_userinfo&component_appid=wxa6780115cc7c1db5#wechat_redirect";
//    //替换秀搭地址微信
//    public final static String wx_shopmatch_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=@url@&response_type=code&scope=snsapi_base&component_appid=wxa6780115cc7c1db5#wechat_redirect";
//    //替换地址微信
//    public final static String wx_getWxUserinfo_url = "http://wechat.app.bizvane.com/app/wechat/getOpenIdByWx?code=@code@&appid=@appid@";
//    //跳转至微信支付页面
//     public final static String wx_pay_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.pub-wechat.bizvane.com%2FbizvaneApp%2Fviews%2F@html@&response_type=code&scope=snsapi_base&state=123&component_appid=wx7e7319a046a816d5#wechat_redirect";


    public final static String wechat_url = "http://wechat.dev.bizvane.com/app/wechat";
    public final static String baiyou_url = "http://www.dev-wechat.bizvane.com/new/outdoor";
    public final static String ishop_url = "http://ishop.dev.bizvane.com/";
    public final static String wechat_redirect_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_base&component_appid=wx722fb7eaa40020e9#wechat_redirect";
//    微商城首页
    public final static String wei_home_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.dev-wechat.bizvane.com%2FbizvaneApp%2Fviews%2Findex.html%3Fopenid%3d@openid@&response_type=code&scope=snsapi_base&state=123&component_appid=wx14f921fdb5330ff6#wechat_redirect";
//    微商城 邀请注册
    public final static String wei_register_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.dev-wechat.bizvane.com%2FbizvaneApp%2Fviews%2Fmember_center.html%3Fshare_appid%3D@appid@%26openid%3d@openid@&source=1&response_type=code&scope=snsapi_base&state=123&component_appid=wx14f921fdb5330ff6#wechat_redirect";
//    微商城 领取会员卡
    public final static String wei_get_card_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.dev-wechat.bizvane.com%2FbizvaneApp%2Fviews%2Fmember_center.html%3Fshare_appid%3D@appid@%26openid%3d@openid@&response_type=code&scope=snsapi_base&state=123&component_appid=wx14f921fdb5330ff6#wechat_redirect";
//    微商城 会员中心
    public final static String wei_member_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.dev-wechat.bizvane.com%2FbizvaneApp%2Fviews%2Fmember_center.html%3Fsource=1&response_type=code&scope=snsapi_base&state=123&component_appid=wx14f921fdb5330ff6#wechat_redirect";
//    微商城 会员任务
    public final static String wei_task_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@APPID@&redirect_uri=http%3a%2f%2fwechat.dev.bizvane.com%2fapp%2fwechat%2fwebAuthorizationTask&response_type=code&scope=snsapi_base&component_appid=wx722fb7eaa40020e9#wechat_redirect";
//    微商城 会员报名活动
    public final static String wei_activity_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@APPID@&redirect_uri=http%3a%2f%2fwechat.dev.bizvane.com%2fapp%2fwechat%2fwebAuthorizationTask%3Ftype=@TYPE@&response_type=code&scope=snsapi_base&component_appid=wx722fb7eaa40020e9#wechat_redirect";
//    微商城 生日礼物
    public final static String wei_birthPresent_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.dev-wechat.bizvane.com%2FbizvaneApp%2Fviews%2Fbirthday.html&response_type=code&scope=snsapi_base&state=123&component_appid=wx14f921fdb5330ff6#wechat_redirect";
//    微商城 会员权益
    public final static String wei_rule_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.dev-wechat.bizvane.com%2FbizvaneApp%2Fviews%2Fviprules.html&response_type=code&scope=snsapi_base&state=123&component_appid=wx14f921fdb5330ff6#wechat_redirect";
//    获取OpenId
    public final static String wei_openId_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3a%2f%2fwechat.dev.bizvane.com%2fapp%2fwechat%2fgetOpenIdByWx?@match_value@&response_type=code&scope=snsapi_userinfo&component_appid=wx722fb7eaa40020e9#wechat_redirect";
//    替换秀搭地址微信
    public final static String wx_shopmatch_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=@url@&response_type=code&scope=snsapi_userinfo&component_appid=wx722fb7eaa40020e9#wechat_redirect";
//    替换地址微信
    public final static String wx_getWxUserinfo_url = "http://wechat.dev.bizvane.com/app/wechat/getOpenIdByWx?code=@code@&appid=@appid@";
//    跳转至微信支付页面
    public final static String wx_pay_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=@appid@&redirect_uri=http%3A%2F%2F@appid@.dev-wechat.bizvane.com%2FbizvaneApp%2Fviews%2F@html@&response_type=code&scope=snsapi_base&state=123&component_appid=wx14f921fdb5330ff6#wechat_redirect";

    //table名（mongodb）
    //用户行为日志
    public final static String table_log_user_action = "log_person_action";
    //产品分享日志
    public final static String table_log_production_share = "log_product_share";
    //会员信息
    public final static String table_vip_info = "vip_info";
    //群发消息记录表
    public final static String table_vip_batchsend_message = "vip_batchsend_message";
    //会员回访记录(new)
    public final static String table_vip_back_record = "vip_back_record";
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
    //秀搭主表
    public final static String table_wechat_reply_def = "wechat_reply_def";
    //会员标签
    public final static String table_vip_label_def = "vip_label_def";
    //错误日志
    public final static String table_error_log = "error_log";

    //会员审核
    public final static String table_vip_check = "vip_check";
    //会员活动执行
    public final static String table_vip_activity_allocation = "vip_activity_allocation";
    //会员活动报名(参与记录)
    public final static String table_vip_activity_join_log = "vip_activity_join_log";
    //会员活动打开链接
    public final static String table_vip_activity_openUrl_log="vip_activity_openUrl_log";
    //会员活动(线下报名记录)
    public final static String table_vip_activity_apply="vip_activity_apply";
    //会员发展分析
    public final static String table_vip_emp_relation = "vip_emp_relation";
    //分享导购
    public final static  String table_vip_share_log="vip_share_log";
    //优惠券发送记录
    public final static  String table_vip_send_coupon="vip_send_coupon";
    //用户登录日志
    public final static String table_sms_log = "log_sms_send";
    //渠道二维码扫码日志
    public final static  String table_vip_qrcode_relation ="vip_qrcode_relation";
    //店铺开启和关闭记录
    public final static  String table_store_start_end_log ="store_start_end_log";
    //会员任务 任务进度记录
    public final static  String table_vip_task_schedule ="vip_task_schedule";
    //回访记录
    public final static String table_vip_message_content="vip_message_content";
    //积分清零记录
    public final static String table_vip_integral_modify_log="vip_integral_modify_log";
    //会员邀请注册记录
    public final static String table_vip_invite_register_log="vip_invite_register_log";
    //秀搭分享日志
    public final static String table_shop_match_pageviews_log = "shop_match_pageviews_log";
    //秀搭浏览日志
    public final static String table_shop_match_share_log = "shop_match_share_log";
    //会员分享链接
    public  final  static  String table_vip_shareUrl_log_wx="vip_shareUrl_log_wx";

    public final static String table_vip_approved_log="vip_approved_log";

    public final static String table_vip_label_log="vip_label_log";

    public final static String table_coupon_log="coupon_log";
    //员工券
    public final static String table_user_ticket= "user_ticket";

    //会员积分调整
    public  final  static  String  table_vip_points_adjust="vip_points_adjust";

    //审核进度
    public final static  String table_function_check_schedule ="function_check_schedule";

    //导入会员(新增会员活动 第一步选择会员)
    public final static  String table_batch_import_vip ="batch_import_vip";
    //升降级记录
    public final static  String table_vip_updown_log ="vip_updown_log";

    //参数定义

    //新增会员是否验证单号
    public final static String ADD_VIP_CHECK_BILL = "ADD_VIP_CHECK_BILL";
    //新增会员是否输入卡号
    public final static String ADD_VIP_INPUT_CARDNO = "ADD_VIP_INPUT_CARDNO";
    //员工是否校验手机号
    public final static String IS_CHECK_PHONE = "IS_CHECK_PHONE";
    //crm数据库连接参数
    public final static String CRM_DB_ACCOUNT = "CRM_DB_ACCOUNT";
    //首页显示logo
    public final static String HOME_LOGO = "HOME_LOGO";
    //CRM图表权限配置
    public final static String CRM_CHART_PRIVIL_CONF = "CRM_CHART_PRIVIL_CONF";
    //CRM年龄段
    public final static String AGE_GROUP_CONF = "AGE_GROUP_CONF";
    //活动任务是否发送通知
    public final static String ACT_TASK_NOTICE =  "ACT_TASK_NOTICE";
//    //会员筛选是否隐藏基本筛选
//    public final static String HIDE_BASIC_SCREEN =  "HIDE_BASIC_SCREEN";

    //微商城商品详情链接（tommy）
    public final static String goods_detail_url ="http://m.tommy.com.cn/index.php/product/detail?productId=";

//链接正式微商城
    public static String wx_url( String[] split_ids,String[] split_counts, String show_code,String appid,String user_id){
        /**
         * https://open.weixin.qq.com/connect/oauth2/authorize?
         appid=wx222cbd523cae37f0
         &redirect_uri=http%3a%2f%2fwx222cbd523cae37f0.pub-wechat.bizvane.com%2fbizvaneApp%2fviews%2forder_confirm.html%3fproductitem_id%3d%5b1%2c2%2c3%5d%26count%3d%5b1%2c1%2c1%5d%26showcode%3d123ABC
         &response_type=code&scope=snsapi_base&state=123&component_appid=wx7e7319a046a816d5#wechat_redirect
         */
        StringBuffer sb = new StringBuffer();
        sb.append("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid);
        sb.append("&redirect_uri=http%3a%2f%2f"+appid+".pub-wechat.bizvane.com%2fbizvaneApp%2fviews%2forder_confirm.html%3fproductitem_id%3d%5b");
        for (int i = 0; i <split_ids.length ; i++) {
            if(i==split_ids.length-1){
                sb.append(split_ids[i]);
            }else{
                sb.append(split_ids[i]+"%2c");
            }
        }
        sb.append("%5d%26count%3d%5b");

        for (int i = 0; i <split_counts.length ; i++) {
            if(i==split_counts.length-1){
                sb.append(split_counts[i]);
            }else{
                sb.append(split_counts[i]+"%2c");
            }
        }
        sb.append("%5d%26showcode%3d"+show_code);
        sb.append("%26guidecode%3d"+user_id);
        sb.append("&response_type=code&scope=snsapi_base&state=123&component_appid=wx7e7319a046a816d5#wechat_redirect");
        System.out.println("----微商城链接(正式)-----"+sb.toString());
        return sb.toString();
    }

    //链接测试微商城
    public static String wx_url_dev( String[] split_ids,String[] split_counts, String show_code,String appid,String user_id){
        StringBuffer sb = new StringBuffer();
        sb.append("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid);
        sb.append("&redirect_uri=http%3a%2f%2f"+appid+".dev-wechat.bizvane.com%2fbizvaneApp%2fviews%2forder_confirm.html%3fproductitem_id%3d%5b");
        for (int i = 0; i <split_ids.length ; i++) {
            if(i==split_ids.length-1){
                sb.append(split_ids[i]);
            }else{
                sb.append(split_ids[i]+"%2c");
            }
        }
        sb.append("%5d%26count%3d%5b");

        for (int i = 0; i <split_counts.length ; i++) {
            if(i==split_counts.length-1){
                sb.append(split_counts[i]);
            }else{
                sb.append(split_counts[i]+"%2c");
            }
        }
        sb.append("%5d%26showcode%3d"+show_code);
        sb.append("%26guidecode%3d"+user_id);
        sb.append("&response_type=code&scope=snsapi_base&state=123&component_appid=wx14f921fdb5330ff6#wechat_redirect");
        System.out.println("----微商城链接(测试)-----"+sb.toString());
        return sb.toString();
    }

}
