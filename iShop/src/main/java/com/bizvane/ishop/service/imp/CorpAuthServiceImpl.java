//package com.bizvane.ishop.service.imp;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.bizvane.ishop.constant.Common;
//import com.bizvane.ishop.constant.CommonValue;
//import com.bizvane.ishop.dao.*;
//import com.bizvane.ishop.entity.CorpJD;
//import com.bizvane.ishop.service.CorpAuthService;
//import com.bizvane.ishop.utils.CheckUtils;
//import com.bizvane.ishop.utils.IshowHttpClient;
//import com.bizvane.ishop.utils.TimeUtils;
//import com.bizvane.ishop.utils.WebUtils;
//import com.jd.open.api.sdk.DefaultJdClient;
//import com.jd.open.api.sdk.JdClient;
//import com.jd.open.api.sdk.domain.crm.CrmMemberService.CrmMemberResult;
//import com.jd.open.api.sdk.domain.crm.GradePromotionService.GradePromotion;
//import com.jd.open.api.sdk.domain.order.OrderNotPayService.PaginatedInfo;
//import com.jd.open.api.sdk.domain.seller.ShopSafService.ShopJosResult;
//import com.jd.open.api.sdk.request.crm.CrmGradeGetRequest;
//import com.jd.open.api.sdk.request.crm.CrmMemberSearchRequest;
//import com.jd.open.api.sdk.request.order.PopOrderNotPayOrderInfoRequest;
//import com.jd.open.api.sdk.request.seller.SellerVenderInfoGetRequest;
//import com.jd.open.api.sdk.request.seller.VenderShopQueryRequest;
//import com.jd.open.api.sdk.response.crm.CrmGradeGetResponse;
//import com.jd.open.api.sdk.response.crm.CrmMemberSearchResponse;
//import com.jd.open.api.sdk.response.order.PopOrderNotPayOrderInfoResponse;
//import com.jd.open.api.sdk.response.seller.SellerVenderInfoGetResponse;
//import com.jd.open.api.sdk.response.seller.VenderInfoResult;
//import com.jd.open.api.sdk.response.seller.VenderShopQueryResponse;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.InputStream;
//import java.math.BigDecimal;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.*;
//
///**
// * Created by zhou on 2017/8/2.
// */
//@Service
//public class CorpAuthServiceImpl implements CorpAuthService {
//    @Autowired
//    private CorpJDMapper corpJDMapper;
//
//    private static final Logger logger = Logger.getLogger(CorpAuthServiceImpl.class);
//
//
//    IshowHttpClient httpclient = new IshowHttpClient();
//
//    //授权，获取code
////    public static String uri = "https://oauth.jd.com/oauth/authorize?response_type=code&client_id=" +appKey+
////            "redirect_uri="+redirect_uri+"&state=bizvane";
//
//    public List<CorpJD> selectAuthByCorp(String corp_code) throws Exception {
//        return corpJDMapper.selectAuthByCorp(corp_code);
//    }
//
//    public CorpJD selectAuthByUid(String u_id) throws Exception {
//        return corpJDMapper.selectAuthByUid(u_id);
//    }
//
//    public int insertCorpJD(CorpJD corpJD) throws Exception {
//        return corpJDMapper.insertCorpJD(corpJD);
//    }
//
//    public int updateCorpJD(CorpJD corpJD) throws Exception {
//        return corpJDMapper.updateCorpJD(corpJD);
//    }
//    public int deleteCorpJD(String corp_code) throws Exception {
//        return corpJDMapper.deleteCorpJD(corp_code);
//    }
//
//
//    //首次授权，获取token
//    public void getAccessToken(String code) throws Exception{
//        String url ="https://oauth.jd.com/oauth/token?code="+code+"&grant_type=authorization_code&client_id="+CommonValue.JD_APPKey+"&redirect_uri="+CommonValue.JD_REDIRECT_URI+"&state=bizvane&client_secret="+CommonValue.JD_APPSecret;
//        URL uri = new URL(url);
//
//        HttpURLConnection conn =(HttpURLConnection) uri.openConnection();
//        conn.setRequestProperty("Accept-Charset","utf-8");
//        conn.setRequestMethod("POST");
//        conn.getResponseCode();
//        InputStream is =conn.getInputStream();
//        String jsonStr = CheckUtils.inputStream2String(is);
//
//        logger.info("============"+jsonStr);
//        JSONObject jsonObject = JSON.parseObject(jsonStr);
//        String errorcode = jsonObject.getString("code");
//        if (errorcode.equals("0")){
//            String access_token = jsonObject.getString("access_token");
//            String expires_in = jsonObject.getString("expires_in");
//            String refresh_token = jsonObject.getString("refresh_token");
//            String u_id = jsonObject.getString("uid");
//            String user_nick = jsonObject.getString("user_nick");
//            String time = jsonObject.getString("time");
//
//            CorpJD corpJD = selectAuthByUid(u_id);
//            if (corpJD != null){
//                corpJD.setU_id(u_id);
//                corpJD.setUser_nick(user_nick);
//                corpJD.setAuthorizer_token(access_token);
//                corpJD.setAuthorizer_refresh_token(refresh_token);
//                corpJD.setExpires_in(expires_in);
//                corpJD.setLast_time(Common.DATETIME_FORMAT.format(TimeUtils.timeStampToDate(time)));
//
//                corpJD.setIs_authorize(Common.IS_AUTHORIZE_Y);
//                corpJD.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
//                corpJD.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
//                corpJD.setIsactive(Common.IS_ACTIVE_Y);
//                updateCorpJD(corpJD);
//            }else {
//                corpJD = new CorpJD();
//                corpJD.setU_id(u_id);
//                corpJD.setUser_nick(user_nick);
//                corpJD.setAuthorizer_token(access_token);
//                corpJD.setAuthorizer_refresh_token(refresh_token);
//                corpJD.setExpires_in(expires_in);
//                corpJD.setLast_time(Common.DATETIME_FORMAT.format(TimeUtils.timeStampToDate(time)));
//
//                corpJD.setIs_authorize(Common.IS_AUTHORIZE_Y);
//                corpJD.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
//                corpJD.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
//                corpJD.setIsactive(Common.IS_ACTIVE_Y);
//                insertCorpJD(corpJD);
//            }
//        }
//
//    }
//
//    //获取token
//    public String refreshToken(String u_id) throws Exception{
//        CorpJD entity = selectAuthByUid(u_id);
//        String access_token = entity.getAuthorizer_token();
//        String refresh_token = entity.getAuthorizer_refresh_token();
//        String expirestime = entity.getExpires_in();
//        Date starttime = Common.DATETIME_FORMAT.parse(entity.getLast_time());
//
//        Date nowtime = new Date();
//        long timediff = (nowtime.getTime() - starttime.getTime()) / 1000;
//        if (timediff > (Long.parseLong(expirestime) - 600)) {
//            //提前10min刷新token
//
//            String url = "https://oauth.jd.com/oauth/token?client_id="+CommonValue.JD_APPKey+"&client_secret="+CommonValue.JD_APPSecret+"&grant_type=refresh_token&refresh_token="+refresh_token;
//            URL uri = new URL(url);
//            HttpURLConnection conn =(HttpURLConnection) uri.openConnection();
//            conn.setRequestProperty("Accept-Charset","utf-8");
//            conn.setRequestMethod("POST");
//            conn.getResponseCode();
//            InputStream is =conn.getInputStream();
//            String jsonStr = CheckUtils.inputStream2String(is);
//
//            logger.info("============"+jsonStr);
//            JSONObject jsonObject = JSON.parseObject(jsonStr);
//            String errorcode = jsonObject.getString("code");
//            if (errorcode.equals("0")){
//                access_token = jsonObject.getString("access_token");
//                String expires_in = jsonObject.getString("expires_in");
//                String refresh_token1 = jsonObject.getString("refresh_token");
//                String time = jsonObject.getString("time");
//
//                CorpJD corpJD = new CorpJD();
//                corpJD.setU_id(u_id);
//                corpJD.setAuthorizer_token(access_token);
//                corpJD.setAuthorizer_refresh_token(refresh_token1);
//                corpJD.setExpires_in(expires_in);
//                corpJD.setLast_time(Common.DATETIME_FORMAT.format(TimeUtils.timeStampToDate(time)));
//                corpJD.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
//
//                updateCorpJD(corpJD);
//            }
//        }
//        return access_token;
//    }
//
//
//    public JSONObject JDApi(String method,String u_id, String param) throws Exception{
//        JSONObject obj = new JSONObject();
//        if (method.equals("shopInfo")){
//            ShopJosResult shopJosResult= shopInfo(u_id);
//            obj = WebUtils.bean2JSONObject(shopJosResult);
//        }else if (method.equals("venderInfo")){
//            VenderInfoResult shopJosResult= venderInfo(u_id,param);
//            obj = WebUtils.bean2JSONObject(shopJosResult);
//        }else if (method.equals("notPayOrder")){
//            PaginatedInfo paginatedInfo = notPayOrder(u_id,param);
//            obj = WebUtils.bean2JSONObject(paginatedInfo);
//        }
//        return obj;
//    }
//
//
//
//    /**
//     *  店铺信息查询
//     *http://jos.jd.com/api/detail.htm?apiName=jingdong.vender.shop.query&id=494
//     * @param u_id
//     * @return
//     * @throws Exception
//     */
//    public ShopJosResult shopInfo(String u_id) throws Exception {
//        String access_token = refreshToken(u_id);
//        JdClient client=new DefaultJdClient(CommonValue.JD_SERVER_URL,access_token,CommonValue.JD_APPKey,CommonValue.JD_APPSecret);
//
//        VenderShopQueryRequest request=new VenderShopQueryRequest();
//        VenderShopQueryResponse response=client.execute(request);
//
//        return response.getShopJosResult();
//    }
//
//
//    /**
//     * 查询商家基本信息
//     * http://jos.jd.com/api/detail.htm?apiName=jingdong.seller.vender.info.get&id=493
//     * @param u_id
//     * @return
//     * @throws Exception
//     */
//    public VenderInfoResult venderInfo(String u_id,String param) throws Exception {
//        String access_token = refreshToken(u_id);
//        JdClient client=new DefaultJdClient(CommonValue.JD_SERVER_URL,access_token,CommonValue.JD_APPKey,CommonValue.JD_APPSecret);
//
//        SellerVenderInfoGetRequest request=new SellerVenderInfoGetRequest();
//
//        JSONObject param_obj = JSONObject.parseObject(param);
//        if (param_obj.containsKey("ext_json_param")){
//            String ext_json_param = param_obj.getString("ext_json_param");
//            request.setExtJsonParam(ext_json_param);//非必填
//        }
//
//        SellerVenderInfoGetResponse response=client.execute(request);
//
//        if (response.getCode().equals("0")){
//            response.getVenderInfoResult();
//            logger.info("===getVenderInfoResult"+response.getVenderInfoResult());
//        }
//        logger.info("getMsg"+response.getMsg() +"===getVenderInfoResult"+response.getVenderInfoResult());
//        return response.getVenderInfoResult();
//    }
//
//    /**
//     *批量查询未付款订单
//     * http://jos.jd.com/api/detail.htm?apiName=jingdong.pop.order.notPayOrderInfo&id=1687
//     *
//     * @param u_id
//     * @param param
//     * @return
//     * @throws Exception
//     */
//    public PaginatedInfo notPayOrder(String u_id,String param) throws Exception{
//        String access_token = refreshToken(u_id);
//        JdClient client=new DefaultJdClient(CommonValue.JD_SERVER_URL,access_token,CommonValue.JD_APPKey,CommonValue.JD_APPSecret);
//
//        PopOrderNotPayOrderInfoRequest request=new PopOrderNotPayOrderInfoRequest();
//
//        JSONObject param_obj = JSONObject.parseObject(param);
//        String start_time = param_obj.getString("start_time");
//        String end_time = param_obj.getString("end_time");
//        int page_num = param_obj.getInteger("page_num");
//        int page_size = param_obj.getInteger("page_size");
//
//        request.setStartDate(Common.DATETIME_FORMAT.parse(start_time));  //必填
//        request.setEndDate(Common.DATETIME_FORMAT.parse(end_time));//必填
//        request.setPage( page_num );//必填
//        request.setPageSize( page_size );//必填
//
//        PopOrderNotPayOrderInfoResponse response=client.execute(request);
//        return response.getPaginatedInfo();
//    }
//
//
//
//    /**
//     * 获取会员等级
//     *
//     * http://jos.jd.com/api/detail.htm?apiName=jingdong.crm.grade.get&id=304
//     * @param u_id
//     * @return
//     * @throws Exception
//     */
//    public GradePromotion[]  crmGradeGet(String u_id) throws Exception {
//        String access_token = refreshToken(u_id);
//        JdClient client=new DefaultJdClient(CommonValue.JD_SERVER_URL,access_token,CommonValue.JD_APPKey,CommonValue.JD_APPSecret);
//
//        CrmGradeGetRequest request = new CrmGradeGetRequest();
//        CrmGradeGetResponse response = client.execute(request);
//        return response.getGradePromotions();
//    }
//
//    /**
//     * 查询会员信息
//     *
//     * http://jos.jd.com/api/detail.htm?apiName=jingdong.crm.member.search&id=335
//     * @param u_id
//     * @return
//     * @throws Exception
//     */
//    public CrmMemberResult crmMemberSearch(String u_id,String param) throws Exception {
//        String access_token = refreshToken(u_id);
//        JdClient client=new DefaultJdClient(CommonValue.JD_SERVER_URL,access_token,CommonValue.JD_APPKey,CommonValue.JD_APPSecret);
//
//        CrmMemberSearchRequest request=new CrmMemberSearchRequest();
//
//        JSONObject param_obj = JSONObject.parseObject(param);
//        if (param_obj.containsKey("vip_name")){
//            String vip_name = param_obj.getString("vip_name");
//            request.setCustomerPin(vip_name);
//        }
//        if (param_obj.containsKey("grade")){
//            String grade = param_obj.getString("grade");
//            request.setGrade(grade);
//        }
//        if (param_obj.containsKey("first_time")){
//            String first_time = param_obj.getString("first_time");
//            request.setMinLastTradeTime(Common.DATETIME_FORMAT_DAY.parse(first_time));
//        }
//        if (param_obj.containsKey("last_time")){
//            String last_time = param_obj.getString("last_time");
//            request.setMaxLastTradeTime(Common.DATETIME_FORMAT_DAY.parse(last_time));
//        }
//        if (param_obj.containsKey("min_trade_count")){
//            int min_trade_count = param_obj.getInteger("min_trade_count");
//            request.setMinTradeCount( min_trade_count );
//        }
//        if (param_obj.containsKey("max_trade_count")){
//            int max_trade_count = param_obj.getInteger("max_trade_count");
//            request.setMaxTradeCount( max_trade_count );
//        }
//        if (param_obj.containsKey("avg_price")){
//            BigDecimal avg_price = param_obj.getBigDecimal("avg_price");
//            request.setAvgPrice( avg_price );
//        }
//        if (param_obj.containsKey("min_trade_amount")){
//            BigDecimal min_trade_amount = param_obj.getBigDecimal("min_trade_amount");
//            request.setMinTradeAmount( min_trade_amount );
//        }
//        int page_num = param_obj.getInteger("page_num");
//        int page_size = param_obj.getInteger("page_size");
//
//        request.setCurrentPage( page_num );
//        request.setPageSize( page_size );
//
//        CrmMemberSearchResponse response=client.execute(request);
//        return response.getCrmMemberResult();
//    }
//
//
//    public void getTBAccessToken(String code) throws Exception{
//        Map<String,String> props=new HashMap<String,String>();
//        props.put("grant_type","authorization_code");
//        props.put("code",code);
//        props.put("client_id",CommonValue.TB_APPKey);
//        props.put("client_secret",CommonValue.TB_APPSecret);
//        props.put("redirect_uri","http://www.test.com");
//        props.put("view","web");
//        String s="";
//        try{
//            s=com.taobao.api.internal.util.WebUtils.doPost(CommonValue.TB_SERVER_URL, props, 30000, 30000);
//            System.out.println(s);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    public void refreshAccessToken(String refresh_token) throws Exception{
//        Map<String,String> props=new HashMap<String,String>();
//        props.put("grant_type","refresh_token");
//        props.put("refresh_token",refresh_token);
//        props.put("client_id",CommonValue.TB_APPKey);
//        props.put("client_secret",CommonValue.TB_APPSecret);
//        String s="";
//        try{
//            s=com.taobao.api.internal.util.WebUtils.doPost(CommonValue.TB_SERVER_URL, props, 30000, 30000);
//            System.out.println(s);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//
//    }
//}
