package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.dao.VIPRelationMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.DataBox;
import com.mongodb.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhou on 2016/6/1.
 *
 * @@version
 */

@Service
public class WebServiceImpl implements WebService {

    @Autowired
    VIPRelationMapper vipRelationMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    StoreMapper storeMapper;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    StoreService storeService;
    @Autowired
    VipActivityService vipActivityService;
    @Autowired
    CorpService corpService;
    @Autowired
    VipActivityDetailService vipActivityDetailService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    WxTemplateService wxTemplateService;
    @Autowired
    VipGroupService  vipGroupService;
    @Autowired
    VipPointsAdjustService vipPointsAdjustService;

    private static final Logger logger = Logger.getLogger(WebServiceImpl.class);

    public DBCursor selectEmpVip(String app_user_name, String open_id) throws SQLException {

        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);

        Map keyMap = new HashMap();
        keyMap.put("_id", app_user_name + open_id);

        BasicDBObject queryCondition = new BasicDBObject();
        queryCondition.putAll(keyMap);
        DBCursor dbCursor = cursor.find(queryCondition);

        return dbCursor;
    }

    public List<VIPStoreRelation> selectStoreVip(String app_user_name, String open_id) throws SQLException {
        return vipRelationMapper.selectStoreVip(open_id, app_user_name);
    }

    public void processNewVip(String corp_code,String app_id,String open_id,JSONObject vipInfo_obj,String invite_open_id) throws Exception{
        final String corp_code1 = corp_code;
        final String app_id1 = app_id;
        final String open_id1 = open_id;
        final JSONObject vipInfo_obj1 = vipInfo_obj;
        final String invite_open_id1 = invite_open_id;
        final MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    logger.info("-----------多线程---");
                    BasicDBObject queryCondition = new BasicDBObject();
                    queryCondition.put("open_id", open_id1);
                    queryCondition.put("app_id", app_id1);

                    //修改会员导购关联表（微转会）
                    DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);
                    DBCursor dbCursor = cursor.find(queryCondition);
                    if (dbCursor.hasNext()) {
                        DBObject updateCondition = new BasicDBObject();
                        updateCondition.put("open_id", open_id1);
                        updateCondition.put("app_id", app_id1);

                        DBObject updatedValue = new BasicDBObject();
                        updatedValue.put("transVip", "Y");
                        updatedValue.put("transVip_date", Common.DATETIME_FORMAT.format(new Date()));
                        updatedValue.put("vip", vipInfo_obj1);

                        DataBox dataBox = iceInterfaceService.getVipByOpenId(corp_code1, open_id1,"");
                        if (dataBox.status.toString().equals("SUCCESS")) {
                            JSONArray array = JSONArray.parseArray(dataBox.data.get("message").value);
                            if (array.size() > 0)
                                updatedValue.put("vip", array.get(0));
                        }

                        DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                        cursor.update(updateCondition, updateSetValue);
                    }

                    //修改会员渠道二维码关联表（微转会）
                    DBCollection cursor1 = mongoTemplate.getCollection(CommonValue.table_vip_qrcode_relation);
                    DBCursor dbCursor1 = cursor1.find(queryCondition);
                    if (dbCursor1.hasNext()) {
                        DBObject updateCondition = new BasicDBObject();
                        updateCondition.put("open_id", open_id1);
                        updateCondition.put("app_id", app_id1);

                        DBObject updatedValue = new BasicDBObject();
                        updatedValue.put("transVip", "Y");
                        updatedValue.put("vip", vipInfo_obj1);
                        updatedValue.put("transVip_date", Common.DATETIME_FORMAT.format(new Date()));

                        DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                        cursor1.update(updateCondition, updateSetValue);
                    }

                    if (!invite_open_id1.equals("")) {
                        DataBox dataBox = iceInterfaceService.getVipByOpenId(corp_code1, invite_open_id1,"");
                        JSONObject invite_vip_info = new JSONObject();
                        String vip_id = "";
                        if (dataBox.status.toString().equals("SUCCESS")) {
                            JSONArray array = JSONArray.parseArray(dataBox.data.get("message").value);
                            if (array.size() > 0) {
                                invite_vip_info = array.getJSONObject(0);
                                vip_id = invite_vip_info.getString("vip_id");
                            }

                        }
                        int flag = saveInviteRegister(app_id1, invite_open_id1, invite_vip_info, open_id1, vipInfo_obj1);
                        DBCollection cursor2 = mongoTemplate.getCollection(CommonValue.table_vip_points_adjust);

                        if (flag == 1){
                        JSONArray present = getCouponAndPresent(invite_open_id1, app_id1);
                        logger.info("==============getCouponAndPresent" + JSON.toJSONString(present));
                        for (int i = 0; i < present.size(); i++) {
                            JSONObject obj = present.getJSONObject(i);
                            String activity_code = obj.getString("activity_code");
                            if (obj.containsKey("coupon")) {
                                JSONArray coupon_array = JSONArray.parseArray(obj.getString("coupon"));
                                for (int j = 0; j < coupon_array.size(); j++) {
                                    JSONObject coupon_obj = coupon_array.getJSONObject(j);
                                    String coupon_code = coupon_obj.getString("coupon_code");
                                    String coupon_name = coupon_obj.getString("coupon_name");

                                    DataBox data = iceInterfaceAPIService.sendCoupons(corp_code1, vip_id, coupon_code,coupon_name, app_id1, open_id1, "邀请注册活动", activity_code, "",activity_code+"#"+coupon_code+j+vip_id+open_id1);
                                }
                            }
                            if (obj.containsKey("point") && !obj.getString("point").equals("")) {
                                String sendPoints = obj.getString("point");
                                DataBox data = iceInterfaceAPIService.sendPoints(corp_code1, vip_id, sendPoints,activity_code+"#"+vip_id+open_id1);
                                String result = "{\"code\":\"0\",\"data\":\"\",\"message\":\"积分赠送成功\"}";
                                String state = "Y";
                                if (!data.status.toString().equals("SUCCESS")) {
                                    result = "{\"code\":\"-1\",\"data\":\"\",\"message\":\"积分赠送失败\"}";
                                    state = "N";
                                }
                                VipPointsAdjust vipPointsAdjust = vipPointsAdjustService.selectPointsAdjustByBillCode(activity_code);
                                JSONObject adjust_obj = WebUtils.bean2JSONObject(vipPointsAdjust);

                                BasicDBObject dbObject = new BasicDBObject();
                                dbObject.put("vip", invite_vip_info);
                                dbObject.put("adJustInfo", adjust_obj);//单据信息
                                dbObject.put("corp_code", corp_code1);
                                dbObject.put("sendPoints", sendPoints);
                                dbObject.put("modified_date", Common.DATETIME_FORMAT.format(new Date())); //调整时间
                                dbObject.put("_id", activity_code + vip_id);
                                dbObject.put("state", state);//未提交
                                dbObject.put("vip_bill_code", activity_code + vip_id);//线下单据
                                dbObject.put("state_info", result);
                                cursor2.save(dbObject);
                            }
                        }
                    }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                System.out.println(new Date()+"Asynchronous task");
            }
        });
        executorService.shutdown();
    }

    public int saveInviteRegister(String app_id,String invite_open_id,JSONObject invite_vipInfo,String register_open_id,JSONObject register_vipInfo) throws Exception{
        logger.info("---------------saveInviteRegister--"+app_id+"----"+invite_open_id);
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor2 = mongoTemplate.getCollection(CommonValue.table_vip_invite_register_log);

        BasicDBObject query = new BasicDBObject();
        query.put("registe_open_id", register_open_id);
        query.put("app_id", app_id);
        DBCursor cursor = cursor2.find(query);
        int flag = 0;
        if (cursor.size() < 1){
            DBObject invite_obj = new BasicDBObject();
            invite_obj.put("invite_open_id", invite_open_id);
            invite_obj.put("invite_vipInfo", invite_vipInfo);
            invite_obj.put("registe_open_id", register_open_id);
            invite_obj.put("register_vipInfo", register_vipInfo);
            invite_obj.put("app_id", app_id);
            invite_obj.put("register_time", Common.DATETIME_FORMAT.format(new Date()));
            cursor2.save(invite_obj);
            flag = 1;

            String regist_vip_name = register_vipInfo.getString("NAME_VIP");
            String regist_card_no = register_vipInfo.getString("CARD_NO_VIP");
            if (regist_card_no.length() > 4){
                regist_card_no = regist_card_no.replaceFirst(regist_card_no.substring(regist_card_no.length()-4, regist_card_no.length()), "****");
            }
            JSONObject notice = wxTemplateService.inviteRegistralNotice(app_id,regist_vip_name,regist_card_no,invite_vipInfo);
            if (notice != null){
                String template_id = notice.getString("template_id");
                JSONObject template_content = notice.getJSONObject("template_content");
                String template_url = notice.getString("template_url");

                wxTemplateService.sendTemplateMsg(app_id,invite_open_id,template_id,template_content,template_url);
            }
        }
        return flag;
    }

    //获取执行中的活动（邀请注册）邀请成功后赠送的优惠券和积分
    public JSONArray getCouponAndPresent(String invite_open_id,String app_id) throws Exception{
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_invite_register_log);
        BasicDBObject basicDBObject=new BasicDBObject();
        basicDBObject.put("invite_open_id",invite_open_id);
        basicDBObject.put("app_id",app_id);

        //获取邀请注册成功的人数
        DBCursor dbCursor=cursor.find(basicDBObject);

        JSONArray couponArray=new JSONArray();
        //获取执行中的活动（邀请注册）的集合
        List<VipActivity> vipActivityList= vipActivityService.getVipActivityByAppid(app_id,"1","register");
        logger.info("-------------vipActivityList.size():"+vipActivityList.size());

        for(int i=0;i<vipActivityList.size();i++){
            VipActivity vipActivity=vipActivityList.get(i);
            String activity_code = vipActivity.getActivity_code();
            //邀请人数
            int invite_num=0;
            JSONObject vip_info = new JSONObject();
            while (dbCursor.hasNext()){
                DBObject dbObject=dbCursor.next();
                if(dbObject.get("register_time").toString().compareTo(vipActivity.getStart_time())>0){
                    ++invite_num;
                }
                String vip = dbObject.get("invite_vipInfo").toString();
                vip_info = JSONObject.parseObject(vip);
            }
            //判断该会员是否符合该活动
            String target_vip=vipActivity.getTarget_vips();
            String corp_code=vipActivity.getCorp_code();
            if(!target_vip.equals("[]")) {
                JSONArray target_array = JSON.parseArray(target_vip);
                JSONObject object = new JSONObject();
                object.put("key", "VIP_OPEN_ID");
                object.put("type", "text");
                object.put("value", invite_open_id);
                target_array.add(object);
                DataBox dataBox1 = iceInterfaceService.vipScreenMethod2("1", "3", corp_code,JSON.toJSONString(target_array),"","");

//                DataBox dataBox1 = vipGroupService.vipScreenBySolr(target_array, corp_code, "1", "3", "", "", "", "", "", "", "");
                String result_data = dataBox1.data.get("message").value;
                JSONObject result_obj = JSONObject.parseObject(result_data);
                JSONArray vip_array = result_obj.getJSONArray("all_vip_list");
                if (vip_array.size() == 0) {
                    continue;
                }
            }
            logger.info("-------------vipActivity:"+vipActivity.getActivity_code());

            VipActivityDetail vipActivityDetail=vipActivityDetailService.selActivityDetailByCode(vipActivity.getActivity_code());
            String register_data=vipActivityDetail.getRegister_data();
            //获取本任务所有的计划
            JSONArray jsonArray=JSON.parseArray(register_data);
            for(int j=0;j<jsonArray.size();j++){
                JSONObject jsonObject=jsonArray.getJSONObject(j);
                //人数区间
                String number_interval=jsonObject.getString("number_interval");
                JSONObject jsonObject1=JSON.parseObject(number_interval);
                //开始人数
                int start=Integer.parseInt(jsonObject1.getString("start"));
                //结束人数
                int end=Integer.parseInt(jsonObject1.getString("end"));
                //每邀请人数
                int invite=Integer.parseInt(jsonObject.getString("invite"));
                logger.info("========上限:"+start+"=======下限:"+end+"==========每:"+invite+"人==========invite_num="+invite_num);
                if(invite_num>=start&&invite_num<=end){
                    JSONObject jsonObject2 = new JSONObject();
                    if(invite!=1){
                        if((invite_num-start+1)/invite==0){
                            //送券 积分
                            jsonObject2 =jsonObject.getJSONObject("present");
                            jsonObject2.put("activity_code",activity_code);
                            couponArray.add(jsonObject2);
                        }
                    }else{
                        //送券 积分
                        jsonObject2=jsonObject.getJSONObject("present");
                        jsonObject2.put("activity_code",activity_code);
                        couponArray.add(jsonObject2);
                    }
                    if (!jsonObject2.isEmpty()){
                        BasicDBObject saveDBObject = new BasicDBObject();
                        saveDBObject.put("activity_code",vipActivity.getActivity_code());
                        saveDBObject.put("corp_code",vipActivity.getCorp_code());
                        saveDBObject.put("vip_id",vip_info.getString("vip_id"));
                        saveDBObject.put("activity_id","");
                        saveDBObject.put("app_id",app_id);
                        String point = "";
                        String coupon = "";
                        if (jsonObject2.containsKey("point"))
                            point = jsonObject2.getString("point");
                        if (jsonObject2.containsKey("coupon"))
                            coupon = jsonObject2.getString("coupon");
                        saveDBObject.put("present_point",point);
                        saveDBObject.put("coupon_type",coupon);
                        saveDBObject.put("vip",vip_info);
                        saveDBObject.put("join_date", Common.DATETIME_FORMAT.format(new Date()));
                        saveDBObject.put("open_id",invite_open_id);

                        DBCollection cursor1 = mongoTemplate.getCollection(CommonValue.table_vip_activity_join_log);
                        cursor1.save(saveDBObject);

                        iceInterfaceAPIService.DisposeActivityData(corp_code,vip_info.getString("vip_id"),vipActivity.getActivity_code(),"register");
                    }

                }
            }
        }

        return couponArray;
    }

    //根据企业编号，vip_id,获取优惠券活动（纪念日类型）（执行中）下的优惠券和积分
    public void anniverActiRetroative(String corp_code, String vip_id, String cust_cols)throws Exception{
        //获取vip信息
        DataBox dataBox= iceInterfaceService.getVipExtendInfo(corp_code,vip_id, cust_cols);
        String list=dataBox.data.get("message").value;
        JSONObject list_obj = JSONObject.parseObject(list);
        JSONArray vips_array = list_obj.getJSONArray("message");
        JSONObject vip_json=vips_array.getJSONObject(0);
        String acts = vip_json.getString("acts");

        //获取会员生日
        String birthday = vip_json.getString("vip_birthday").replace("-","");
        int birthday_num = 0;
        if (!birthday.equals("")){
            birthday = birthday.substring(4,birthday.length());
            birthday_num=Integer.parseInt(birthday);
        }
        logger.info("----------anniverActiRetroative acts:"+acts+"----birthday_num:"+birthday_num);

        //获取该企业下所有执行中的纪念日活动
        List<VipActivityDetailAnniversary> anniversaries = vipActivityDetailService.selCorpAnniversary(corp_code);
        logger.info("----------anniverActiRetroative anniversaries"+anniversaries.size());
        //获取所有的纪念日发券
        List<VipActivityDetailAnniversary> detail_list=new ArrayList<VipActivityDetailAnniversary>();
        for (int i = 0; i < anniversaries.size(); i++) {
            //如果该会员没有被选择参加该活动,则过滤掉此活动
            VipActivityDetailAnniversary anniversary = anniversaries.get(i);
            VipActivity vipActivity= vipActivityService.getActivityByCode(anniversary.getActivity_code());
            if(vipActivity != null){
                String target_vip=vipActivity.getTarget_vips();
                if(!target_vip.equals("[]")){
                    JSONArray target_array=JSON.parseArray(target_vip);
                    JSONObject object = new JSONObject();
                    object.put("key","VIP_ID");
                    object.put("type","text");
                    object.put("value",vip_id);
                    target_array.add(object);
                    DataBox dataBox1 = vipGroupService.vipScreenBySolr(target_array,corp_code,"1","3","","","","","","","");
                    String result_data = dataBox1.data.get("message").value;
                    JSONObject result_obj = JSONObject.parseObject(result_data);
                    JSONArray vip_array = result_obj.getJSONArray("all_vip_list");
                    if (vip_array.size()==0) {
                        continue;
                    }
                }
                anniversary.setStart_time(vipActivity.getStart_time());
                detail_list.add(anniversary);
            }
        }
        logger.info("----------anniverActiRetroative detail_list"+detail_list.size());
        JSONArray all_array=new JSONArray();
        for (int i = 0; i <detail_list.size() ; i++) {
            VipActivityDetailAnniversary vipDetail= detail_list.get(i);

            int current_date=Integer.parseInt(Common.DATETIME_FORMAT_DAY_NO.format(new Date()).substring(4,8));
            int start_time=Integer.parseInt(vipDetail.getStart_time().split(" ")[0].replaceAll("-","").substring(4,8));
            String param_name = vipDetail.getParam_name();

            boolean is_join = false;
            if(param_name.equals("BIRTHDAY")){
                if(birthday_num<=current_date && birthday_num>=start_time && !acts.contains(vipDetail.getActivity_code()+vipDetail.getId())){
                    is_join = true;
                }
            }else{
                if (vip_json.containsKey(param_name)){
                    int param_value = Integer.parseInt(vip_json.getString(param_name).replace("-",""));
                    if(param_value<=current_date && param_value>=start_time && !acts.contains(vipDetail.getActivity_code()+vipDetail.getId())){
                        is_join = true;
                    }
                }
            }
            if (is_join == true){
                if (vipDetail.getSend_points() != null && !vipDetail.getSend_points().equals("")){
                    DataBox data = iceInterfaceAPIService.sendPoints(corp_code,vip_id,vipDetail.getSend_points(),vipDetail.getActivity_code()+"^"+vipDetail.getId()+"#"+vip_id);
                }
                if (vipDetail.getCoupon_type() != null && !vipDetail.getCoupon_type().equals("")){
                    JSONArray coupon_array = JSONArray.parseArray(vipDetail.getCoupon_type());
                    for (int j = 0; j < coupon_array.size(); j++) {
                        JSONObject coupon_obj = coupon_array.getJSONObject(j);
                        String coupon_code = coupon_obj.getString("coupon_code");
                        String coupon_name = coupon_obj.getString("coupon_name");
                        DataBox data = iceInterfaceAPIService.sendCoupons(corp_code,vip_id,coupon_code,coupon_name,"","","纪念日活动",vipDetail.getActivity_code(),"",vipDetail.getActivity_code()+"^"+vipDetail.getId()+"#"+coupon_code+j+vip_id);
                    }
                }
            }
        }
        logger.info("----------anniverActiRetroative all_array"+all_array.size());
        return ;
    }
}
