package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.controller.VIPRecordController;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import org.apache.hadoop.mapred.IFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
@Service
public class VipRecordServiceImpl implements VipRecordService {

    @Autowired
    CorpService corpService;
    @Autowired
    UserService userService;
    @Autowired
    StoreService storeService;
    @Autowired
    BaseService baseService;

    private static final Logger log = Logger.getLogger(VipRecordServiceImpl.class);

    public JSONArray transRecord(DBCursor dbCursor) throws Exception{
        //  System.out.println("---进入查询---");
        JSONArray array = new JSONArray();
        while (dbCursor.hasNext()) {
            // System.out.println("---有值---");
            DBObject obj = dbCursor.next();
            JSONObject object = new JSONObject();
            String id = obj.get("_id").toString();
            object.put("id",id);

            String corp_code1 = "";
            if (obj.containsField("corp_code")) {
                corp_code1 = obj.get("corp_code").toString();
            }else if (obj.containsField("CORP_ID")){
                corp_code1 = obj.get("CORP_ID").toString();
            }
            object.put("corp_code",corp_code1);

            if (obj.containsField("user_id")){
                object.put("user_id",obj.get("user_id").toString());
                String user_name = "";
                if (obj.containsField("user_name") && obj.get("user_name") != null && obj.get("user_name").equals("null")){
                    user_name = obj.get("user_name").toString();
                }else {
                    List<User> users = userService.userCodeExist(obj.get("user_id").toString(),corp_code1,Common.IS_ACTIVE_Y);
                    if (users.size()>0){
                        user_name = users.get(0).getUser_name();
                    }
                }
                object.put("user_name",user_name);
            }else if (obj.containsField("EMP_ID")){
                List<User> users = userService.userIdExist(obj.get("user_id").toString(),corp_code1);
                String user_name = "";
                String user_code = "";
                if (users.size()>0){
                    user_name = users.get(0).getUser_name();
                    user_code = users.get(0).getUser_code();
                }
                object.put("user_id",user_code);
                object.put("user_name",user_name);
            }else {
                object.put("user_id","");
                object.put("user_name","");
            }
            //门店
            if (obj.containsField("store_name")){
                object.put("store_name",obj.get("store_name").toString());
            }else if (obj.containsField("STORE_ID")){
                Store store = storeService.storeIdExist(corp_code1,obj.get("STORE_ID").toString());
                if (store != null){
                    object.put("store_name",store.getStore_name());
                }else {
                    object.put("store_name","");
                }
            }else{
                object.put("store_name","");
            }
            //品牌
            if (obj.containsField("brand_name") && obj.get("brand_name")!=null){
                object.put("brand_name",obj.get("brand_name").toString());
            }else if (obj.containsField("BRAND_ID")){
                object.put("brand_name",obj.get("BRAND_ID").toString());
            }else{
                object.put("brand_name","");
            }
            //EMP_ID
            if (obj.containsField("user_id_app")){
                object.put("user_id_app",obj.get("user_id_app").toString());
            }else if (obj.containsField("EMP_ID")){
                object.put("user_id_app",obj.get("EMP_ID").toString());
            }else{
                object.put("user_id_app","");
            }

            //openid
            if (obj.containsField("open_id")){
                object.put("open_id",obj.get("open_id").toString());
            }else {
                object.put("open_id","");
            }
            if (obj.containsField("vip_id")){
                object.put("vip_id",obj.get("vip_id").toString());
            }else if (obj.containsField("VIP_ID")){
                object.put("vip_id",obj.get("VIP_ID").toString());
            }else {
                object.put("vip_id","");
            }

            if (obj.containsField("company_name")){
                if(obj.get("company_name")==null){
                    object.put("company_name", "");
                }else {
                    object.put("company_name", obj.get("company_name") + "");
                }
            }else {
                object.put("company_name","");
                Corp corp = corpService.selectByCorpId(0,corp_code1,"Y");
                if (corp != null)
                    object.put("company_name",corp.getCorp_name());
            }

            //会员名称
            if (obj.containsField("vip_name")){
                object.put("vip_name",obj.get("vip_name").toString());
            }else if (obj.containsField("VIP_NAME")){
                object.put("vip_name",obj.get("VIP_NAME").toString());
            }else {
                object.put("vip_name","");
            }
            //会员卡号
            if (obj.containsField("vip_card_no")){
                object.put("vip_card_no",obj.get("vip_card_no").toString());
            }else if (obj.containsField("VIP_CARDNO")){
                object.put("vip_card_no",obj.get("VIP_CARDNO").toString());
            }else {
                object.put("vip_card_no","");
            }

            //内容
            if (obj.containsField("record_message") && obj.get("record_message") != null){
                object.put("message_content",obj.get("record_message").toString());
            }else if (obj.containsField("COMMENTS")){
                object.put("message_content",obj.get("COMMENTS").toString());
            }else {
                object.put("message_content","");
            }
            //创建时间
            if (obj.containsField("created_date")){
                object.put("created_date",obj.get("created_date").toString());
            }else if (obj.containsField("T_CR")){
                object.put("created_date",obj.get("T_CR").toString());
            }
            //方式
            if (obj.containsField("action")){
                String action = obj.get("action").toString();
                if (action.equals("1")){
                    object.put("action","电话");
                }else if (action.equals("2")){
                    object.put("action","短信");
                }else if (action.equals("3")){
                    object.put("action","微信");
                }
            }else {
                object.put("action","微信");
            }

            //发起方
            if (obj.containsField("TYPE")){
                String initiator = obj.get("TYPE").toString();
                if (initiator.equals("EMP"))
                    object.put("initiator","导购");
                if (initiator.equals("VIP"))
                    object.put("initiator","顾客");
            }else {
                object.put("initiator","导购");
            }
            //审核状态
            if (obj.containsField("EM_STATUS")){
                String initiator = obj.get("EM_STATUS").toString();
                if (initiator.equals("1"))
                    initiator = "未审核";
                if (initiator.equals("2"))
                    initiator = "已审核";
                object.put("feedback_status",initiator);
            }else {
                object.put("feedback_status","");
            }
            //审核结果
            if (obj.containsField("BY2")){
                String initiator = obj.get("BY2").toString();
                object.put("feedback_result",initiator);
            }else if (obj.containsField("RECOMMENTS")){
                String initiator = obj.get("RECOMMENTS").toString();
                object.put("feedback_result",initiator);
            }else {
                object.put("feedback_result","");
            }
            //类型
            if (obj.containsField("TYPE")){
                object.put("type","投诉");
                if (obj.containsField("action"))
                    object.put("type","咨询");
            }else {
                object.put("type","爱秀回访");
            }

            array.add(object);
        }
        return array;
    }


    public JSONArray transFeedBack(DBCursor dbCursor,String corp_code) throws Exception{
        JSONArray array = new JSONArray();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            JSONObject object = new JSONObject();
            String id = obj.get("_id").toString();
            object.put("id",id);

            if (obj.containsField("user_id") ){
                String user_code = obj.get("user_id").toString();
                object.put("user_id",user_code);
                String user_name = "";
                if (obj.containsField("user_name") && obj.get("user_name") != null && obj.get("user_name").equals("null")){
                    user_name = obj.get("user_name").toString();
                }else {
                    //验证企业下用户编号是否存在  查的 def_user 表
                    List<User> users = userService.userCodeExist(user_code,corp_code,Common.IS_ACTIVE_Y);
                    if (users.size()>0){
                        user_name = users.get(0).getUser_name();
                    }
                }
                object.put("user_name",user_name);
            }else if (obj.containsField("EMP_ID")){
                //验证企业下用户id是否已存在
                List<User> users = userService.userIdExist(obj.get("user_id").toString(),corp_code);
                String user_name = "";
                String user_code = "";
                if (users.size() >0){
                    user_name = users.get(0).getUser_name();
                    user_code = users.get(0).getUser_code();
                }
                object.put("user_id",user_code);
                object.put("user_name",user_name);
            }else {
                object.put("user_name","");
            }

            //内容
            if (obj.containsField("record_message") && obj.get("record_message") != null){
                object.put("message_content",obj.get("record_message").toString());
            }else if (obj.containsField("COMMENTS")){
                object.put("message_content",obj.get("COMMENTS").toString());
            }else {
                object.put("message_content","");
            }
            //创建时间
            if (obj.containsField("created_date")){
                object.put("created_date",obj.get("created_date").toString());
            }else if (obj.containsField("T_CR")){
                object.put("created_date",obj.get("T_CR").toString());
            }
            //方式
            if (obj.containsField("action")){
                String action = obj.get("action").toString();
                if (action.equals("1")){
                    object.put("action","电话");
                }else if (action.equals("2")){
                    object.put("action","短信");
                }else if (action.equals("3")){
                    object.put("action","微信");
                }
            }else {
                object.put("action","微信");
            }
            //发起方
            if (obj.containsField("TYPE")){
                String initiator = obj.get("TYPE").toString();
                if (initiator.equals("EMP"))
                    object.put("initiator","导购");
                if (initiator.equals("VIP"))
                    object.put("initiator","顾客");
            }else {
                object.put("initiator","导购");
            }
            //审核状态
            if (obj.containsField("EM_STATUS")){
                String initiator = obj.get("EM_STATUS").toString();
                if (initiator.equals("1"))
                    initiator = "未审核";
                if (initiator.equals("2"))
                    initiator = "已审核";
                object.put("feedback_status",initiator);
            }else {
                object.put("feedback_status","");
            }
            //审核结果
            if (obj.containsField("BY2")){
                String initiator = obj.get("BY2").toString();
                object.put("feedback_result",initiator);
            }else if (obj.containsField("RECOMMENTS")){
                String initiator = obj.get("RECOMMENTS").toString();
                object.put("feedback_result",initiator);
            }else {
                object.put("feedback_result","");
            }
            //类型
            if (obj.containsField("TYPE")){
//                String initiator = obj.get("TYPE").toString();
                object.put("type","投诉");
                if (obj.containsField("action"))
                    object.put("type","咨询");
            }else {
                object.put("type","爱秀回访");
            }

            array.add(object);
        }
        return array;
    }


    public BasicDBObject getScreen(JSONArray screen,String corp_code) throws Exception{
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        for (int i = 0; i < screen.size(); i++) {
            String info = screen.get(i).toString();
            JSONObject json = JSONObject.parseObject(info);
            String screen_key = json.get("screen_key").toString();
            String screen_value = json.get("screen_value").toString();

            //会员卡号
            if (screen_key.equals("vip_card_no") && !screen_value.equals("")){
                BasicDBList list = new BasicDBList();
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);

                list.add(new BasicDBObject("vip_card_no", pattern));
                list.add(new BasicDBObject("VIP_CARDNO", pattern));
                BasicDBObject oo = new BasicDBObject();
                oo.put("$or",list);
                values.add(oo);
            }

            //姓名
            if (screen_key.equals("vip_name") && !screen_value.equals("")){
                BasicDBList list = new BasicDBList();
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);

                list.add(new BasicDBObject("vip_name", pattern));
                list.add(new BasicDBObject("VIP_NAME", pattern));
                BasicDBObject oo = new BasicDBObject();
                oo.put("$or",list);
                values.add(oo);
            }

            //门店
            if (screen_key.equals("store_name") && !screen_value.equals("")){
                BasicDBList list = new BasicDBList();
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);

                list.add(new BasicDBObject("store_name", pattern));
                list.add(new BasicDBObject("STORE_NAME", pattern));
                BasicDBObject oo = new BasicDBObject();
                oo.put("$or",list);
                values.add(oo);
            }
            //品牌
            if (screen_key.equals("brand_name") && !screen_value.equals("")){
                BasicDBList list = new BasicDBList();
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                list.add(new BasicDBObject("brand_name", pattern));
                list.add(new BasicDBObject("BRAND_ID", pattern));
                BasicDBObject oo = new BasicDBObject();
                oo.put("$or",list);
                values.add(oo);
            }

            if (screen_key.equals("action")){
                if (screen_value.equals("电话")){
                    values.add(new BasicDBObject("action","1"));
                }else if (screen_value.equals("短信")){
                    values.add(new BasicDBObject("action","2"));
                }else if (screen_value.equals("微信")){
                    BasicDBList list = new BasicDBList();
                    list.add(new BasicDBObject("action", "3"));
                    list.add(new BasicDBObject("action", null));
                    BasicDBObject oo = new BasicDBObject();
                    oo.put("$or",list);
                    values.add(oo);
                }
            }
            if (screen_key.equals("initiator")){
                if (screen_value.equals("导购")) {
                    BasicDBList list = new BasicDBList();
                    list.add(new BasicDBObject("TYPE", "EMP"));
                    list.add(new BasicDBObject("TYPE", null));
                    BasicDBObject oo = new BasicDBObject();
                    oo.put("$or",list);
                    values.add(oo);
                }else if (screen_value.equals("顾客")){
                    values.add(new BasicDBObject("TYPE", "VIP"));
                }
            }
            if (screen_key.equals("feedback_status") && !screen_value.equals("")){
                if (screen_value.equals("未审核"))
                    screen_value = "1";
                if (screen_value.equals("已审核"))
                    screen_value = "2";
                values.add(new BasicDBObject("EM_STATUS", screen_value));
            }
            if (screen_key.equals("type")){
                if (screen_value.equals("爱秀回访")){
                    values.add(new BasicDBObject("TYPE", null));
                }else if (screen_value.equals("投诉")){
                    values.add(new BasicDBObject("TYPE", new BasicDBObject("$ne",null)));
                    values.add(new BasicDBObject("action", null));
                }else if (screen_value.equals("咨询")){
                    values.add(new BasicDBObject("TYPE", "VIP"));
                    values.add(new BasicDBObject("action", "3"));
                }

            }
            if (screen_key.equals("created_date")){
                JSONObject date = JSON.parseObject(screen_value);
                String start = date.get("start").toString();
                String end = date.get("end").toString();
                if (!start.equals("") && start != null) {
                    BasicDBObject oo = new BasicDBObject();
                    BasicDBList list = new BasicDBList();
                    list.add(new BasicDBObject(screen_key,new BasicDBObject(QueryOperators.GTE, start + " 00:00:00")));
                    list.add(new BasicDBObject("T_CR",new BasicDBObject(QueryOperators.GTE, start + " 00:00:00")));
                    oo.put("$or",list);
                    values.add(oo);
                }
                if (!end.equals("") && end != null) {
                    BasicDBObject oo = new BasicDBObject();
                    BasicDBList list = new BasicDBList();
                    list.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.LTE, end + " 23:59:59")));
                    list.add(new BasicDBObject("T_CR", new BasicDBObject(QueryOperators.LTE, end + " 23:59:59")));
                    oo.put("$or",list);
                    values.add(oo);
                }
            }
            if ( screen_key.equals("user_name") && !screen_value.equals("")) {
//                if (screen_value.startsWith("|") || screen_value.startsWith(",") || screen_value.startsWith("，")) {
//                    screen_value = screen_value.substring(1);
//                }
//                if (screen_value.endsWith("|") || screen_value.endsWith(",") || screen_value.endsWith("，")) {
//                    screen_value = screen_value.substring(0, screen_value.length() - 1);
//                }
//                screen_value = screen_value.replaceAll(",", "|");
//                screen_value = screen_value.replaceAll("，", "|");
//                screen_value = WebUtils.El2Str1(screen_value);

                BasicDBObject oo = new BasicDBObject();
                BasicDBList list = new BasicDBList();

                PageInfo<User> users = userService.selectUsersOfTask(1,50,corp_code,screen_value,"","",null,"","");
                if (users.getList().size() > 0){
                    String emp_id = "";
                    for (int j = 0; j < users.getList().size(); j++) {
                        emp_id += users.getList().get(j).getUser_id() + "|";
                    }
                    String screen_value1 = WebUtils.El2Str1(emp_id.substring(0,emp_id.length()-1));
                    Pattern pattern = Pattern.compile("^.*" + screen_value1 + ".*$", Pattern.CASE_INSENSITIVE);
                    list.add(new BasicDBObject("EMP_ID", pattern));
                }

                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);

                if (list.size() > 0){
                    list.add(new BasicDBObject(screen_key, pattern));
                    oo.put("$or",list);
                    values.add(oo);
                }else {
                    values.add(new BasicDBObject(screen_key, pattern));
                }
            }
            //员工编号
            if (screen_key.equals("user_id") && !screen_value.equals("")){
                BasicDBList list = new BasicDBList();
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                list.add(new BasicDBObject("EMP_ID", pattern));
                list.add(new BasicDBObject("user_id", pattern));
                BasicDBObject oo = new BasicDBObject();
                oo.put("$or",list);
                values.add(oo);
            }
            //企业
            if (screen_key.equals("company_name") && !screen_value.equals("")){
                BasicDBList list = new BasicDBList();
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                list.add(new BasicDBObject("company_name", pattern));

                List<Corp> corps = corpService.selectAllCorp(1,50,screen_value).getList();
                if (corps.size() > 0){
                    String CORP_ID = "";
                    for (int j = 0; j < corps.size(); j++) {
                        CORP_ID += corps.get(j).getCorp_code() + "|";
                    }
                    String screen_value1 = WebUtils.El2Str1(CORP_ID.substring(0,CORP_ID.length()-1));
//                    Pattern pattern = Pattern.compile("^.*" + screen_value1 + ".*$", Pattern.CASE_INSENSITIVE);
                    list.add(new BasicDBObject("CORP_ID", screen_value1));
                }

                BasicDBObject oo = new BasicDBObject();
                oo.put("$or",list);
                values.add(oo);
            }
        }

        if (values.size() > 0)
            queryCondition.put("$and", values);

        log.info("==========queryCondition"+JSON.toJSONString(queryCondition));
        return queryCondition;
    }


    public BasicDBList getRoleCondition(HttpServletRequest request,String corp_code,BasicDBList value) throws Exception{
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        if (role_code.equals(Common.ROLE_SYS)) {

        } else{
            BasicDBList list = new BasicDBList();
            list.add(new BasicDBObject("corp_code", corp_code));
            list.add(new BasicDBObject("CORP_ID", corp_code));
            BasicDBObject oo = new BasicDBObject();
            oo.put("$or",list);
            value.add(oo);
            if (role_code.equals(Common.ROLE_AM) || role_code.equals(Common.ROLE_BM) || role_code.equals(Common.ROLE_SM)) {

                List<Store> storeList = new ArrayList<Store>();
                if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                    storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");

                } else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    store_code=store_code.replace(Common.SPECIAL_HEAD, "");
                    storeList = storeService.selectByStoreCodes(store_code,corp_code,Common.IS_ACTIVE_Y);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String area_store = request.getSession().getAttribute("store_code").toString();
                   // System.out.println("-------------area_code--------------------" + area_code+"====="+area_store);
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");

                    area_store = area_store.replace(Common.SPECIAL_HEAD, "");
                   // System.out.println("-------------area_code--------------------" + area_code+"====="+area_store);
                    storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, "", "", area_store);
                }
                BasicDBList store_codes = new BasicDBList();
                BasicDBList store_ids = new BasicDBList();

                System.out.println("-------------storeList--------------------" + storeList.size());
                for (int i = 0; i < storeList.size(); i++) {
                    String store_code = storeList.get(i).getStore_code();
                    String store_id = storeList.get(i).getStore_id();
                    store_codes.add(store_code);
                    store_ids.add(store_id);
                }
                BasicDBList list1 = new BasicDBList();
                list1.add(new BasicDBObject("store_code", new BasicDBObject("$in", store_codes)));
                list1.add(new BasicDBObject("STORE_ID", new BasicDBObject("$in", store_ids)));
                BasicDBObject oo1 = new BasicDBObject();
                oo1.put("$or",list1);
                value.add(oo1);

            } else if (role_code.equals(Common.ROLE_STAFF)){
                BasicDBList list1 = new BasicDBList();
                list1.add(new BasicDBObject("user_id", user_code));
                list1.add(new BasicDBObject("EMP_ID", user_code));
                BasicDBObject oo1 = new BasicDBObject();
                oo1.put("$or",list1);

                value.add(oo1);
            }
        }
        return value;
    }
}
