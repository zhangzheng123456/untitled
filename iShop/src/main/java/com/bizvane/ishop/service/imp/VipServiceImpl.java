package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.VipCardType;
import com.bizvane.ishop.entity.VipParam;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.NumberUtil;
import com.bizvane.ishop.utils.OssUtils;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.DataBox;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Created by ZhouZhou on 2017/1/16.
 */
@Service
public class VipServiceImpl implements VipService {

    @Autowired
    CRMInterfaceService crmInterfaceService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    StoreService storeService;
    @Autowired
    VipCardTypeService vipCardTypeService;
    @Autowired
    WebService webService;
    @Autowired
    VipParamService vipParamService;

    private static final Logger logger = Logger.getLogger(VipServiceImpl.class);

    public DBCursor findVipByMongo(String corp_code,String vip_id) throws Exception{
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("vip_id", vip_id);
        dbObject.put("corp_code", corp_code);
        DBCursor dbCursor = cursor.find(dbObject);

        return dbCursor;
    }

    public JSONObject addVip(String corp_code,JSONObject jsonObject) throws Exception{
        JSONObject resu_obj = new JSONObject();
        String billNo = jsonObject.get("billNo").toString().trim();
        String vip_name = jsonObject.get("vip_name").toString().trim();
        String user_code = jsonObject.get("user_code").toString();
        String user_name = jsonObject.get("user_name").toString();
        String store_code = jsonObject.get("store_code").toString();
        String vip_card_type = jsonObject.get("vip_card_type").toString();
        String phone = jsonObject.get("phone").toString();
        String birthday = jsonObject.get("birthday").toString();
        String sex = jsonObject.get("sex").toString();
        String streets = jsonObject.get("address").toString();
        String address_detail = jsonObject.get("address_detail").toString();
        String recom_vip_card_no = jsonObject.get("recom_vip_card_no").toString().trim();
        String wechat = "";

        Store store = storeService.getStoreByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
        String store_name = store.getStore_name();
        String dealer = store.getDealer();
        if (jsonObject.containsKey("wechat"))
            wechat = jsonObject.getString("wechat");
        birthday = birthday.replace("-","");
        HashMap<String,Object> vipInfo = new HashMap<String, Object>();
        vipInfo.put("VIPNAME",vip_name);
        vipInfo.put("SEX",sex);
        vipInfo.put("BIRTHDAY",birthday);
        vipInfo.put("MOBIL",phone);
        //开卡人姓名
        vipInfo.put("SALESREP_ID__NAME",user_name);
        vipInfo.put("C_CUSTOMER_ID__NAME",dealer);
        vipInfo.put("C_STORE_ID__NAME",store_name);

        //零售单号
        vipInfo.put("DOCNOS",billNo);
        //会员卡类型
        vipInfo.put("C_VIPTYPE_ID__NAME",vip_card_type);
        //推荐入会的vip卡号
        vipInfo.put("VIPCARDNO__CARDNO",recom_vip_card_no);
        vipInfo.put("WECHAT",wechat);

        logger.info("==========CRMservice ==addvipStart"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date()));
        logger.info("-----vipInfo:"+JSON.toJSONString(vipInfo));
        String result = crmInterfaceService.addVip(corp_code,vipInfo);
        logger.info("==========CRMservice ==addvipEnd"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date()));

        JSONObject result_obj = JSONObject.parseObject(result);
        String code = result_obj.getString("code");
        if (code.equals("0")){
            result = result_obj.getString("rows");
            JSONObject obj = JSONObject.parseObject(result);
            String vip_id = obj.getString("id");
            String card_no = obj.getString("CARDNO");
            vip_card_type = obj.getString("C_VIPTYPE_ID");

            logger.info("==========HBase ==addvipStart"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date()));

            //调毛伟栋新增接口存入Hbase
            iceInterfaceAPIService.addNewVip(corp_code,vip_id,vip_name,sex,birthday,phone,vip_card_type,card_no,store_code,user_code,streets);
            logger.info("==========HBase ==addvipEnd"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date()));

//            webService.anniverActiRetroative(corp_code,vip_id,"");
            String province = "";
            String city = "";
            String area = "";
            String[] street = streets.split("/");
            if (street.length>=1){
                province = street[0];
            }
            if (street.length>=2){
                city = street[1];
            }
            if (street.length>=3){
                area = street[2];
            }
            //存入省，市，通讯地址
            if (!province.equals("") || !city.equals("") || !address_detail.equals("")){
                logger.info("-----vipInfo 省市区:"+obj.toString());

                HashMap<String,Object> vip_addr = new HashMap<String, Object>();
                vip_addr.put("id",Integer.parseInt(vip_id));
                vip_addr.put("C_PROVINCE_ID__NAME",province);
                vip_addr.put("C_CITY_ID__NAME",city);
                vip_addr.put("ADDRESS",address_detail);
                crmInterfaceService.updateBaseInfoVip(corp_code,vip_addr);
            }

            resu_obj.put("code","0");
            resu_obj.put("message",card_no);
        }else {
            String msg = result_obj.getString("message");
            resu_obj.put("code","-1");
            resu_obj.put("message",msg);
        }
        return resu_obj;
    }

    public JSONObject saveVipInfo(JSONObject jsonObject,Date now) throws Exception{
        JSONObject vipInfo_obj = new JSONObject();
        vipInfo_obj.put("date",Common.DATETIME_FORMAT.format(now));

        String vip_id = jsonObject.get("vip_id").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String card_no = jsonObject.get("card_no").toString();
        String phone = jsonObject.get("phone").toString();
        if (jsonObject.containsKey("extend") && !jsonObject.get("extend").toString().equals("")) {
            //扩展信息
            String extend = jsonObject.get("extend").toString();
            JSONObject extend_obj = JSONObject.parseObject(extend);
            Iterator<String> iter = extend_obj.keySet().iterator();
            JSONArray array = new JSONArray();
            while (iter.hasNext()) {
                JSONObject obj = new JSONObject();
                String name = iter.next();//格式:param_name CUST_1503062326772
                String value = extend_obj.get(name).toString().trim();

                List<VipParam> vipParamList = vipParamService.selectByParamName(corp_code, name, "Y");
                if (vipParamList.size() > 0) {
                    VipParam vipParam = vipParamList.get(0);
                    String param_attribute = vipParam.getParam_attribute();
                    if ("sex".equals(param_attribute)) {
                        if ("男".equals(value)) {
                            value = "1";
                        } else if ("女".equals(value)) {
                            value = "0";
                        }
                    }
                    obj.put("column", name);
                    obj.put("value", value);
                    array.add(obj);
                }
            }
            iceInterfaceService.saveVipExtendInfo(corp_code,vip_id,"", JSON.toJSONString(array));
        }else {
            MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
            Map keyMap = new HashMap();
            keyMap.put("_id", corp_code + vip_id);
            BasicDBObject queryCondition = new BasicDBObject();
            queryCondition.putAll(keyMap);
            DBCursor dbCursor1 = cursor.find(queryCondition);
            if (dbCursor1.size() > 0) {
                //记录存在，更新
                DBObject updateCondition = new BasicDBObject();
                updateCondition.put("_id", corp_code + vip_id);

                DBObject updatedValue = new BasicDBObject();
                if (jsonObject.containsKey("remark")) {
                    //备注
                    String remark = jsonObject.get("remark").toString();
                    updatedValue.put("remark", remark);
                }
                if (jsonObject.containsKey("avatar") && !jsonObject.get("avatar").toString().equals("")) {
                    //头像
                    String avatar = jsonObject.get("avatar").toString();
                    updatedValue.put("avatar", avatar);
                    vipInfo_obj.put("oss_url",avatar);
                }
                if (jsonObject.containsKey("image_url") && !jsonObject.get("image_url").toString().equals("")) {
                    //相册
                    DBObject obj = dbCursor1.next();
                    JSONArray array = new JSONArray();
                    if (obj.containsField("album")) {
                        String album = obj.get("album").toString();
                        array = JSON.parseArray(album);
                    }
                    String image_url = jsonObject.get("image_url").toString();
                    String srcKey = image_url.replace("http://products-image.oss-cn-hangzhou.aliyuncs.com/","");
                    String destKey = "Album/Vip/iShow/"+card_no+"_"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date())+".jpg";
                    OssUtils oo = new OssUtils();
                    oo.renameObject(Common.BUCKET_NAME,srcKey,Common.BUCKET_NAME,destKey);
                    image_url = "http://products-image.oss-cn-hangzhou.aliyuncs.com/"+destKey;
                    JSONObject image = new JSONObject();
                    image.put("image_url", image_url);
                    image.put("time", Common.DATETIME_FORMAT.format(now));
                    array.add(image);
                    vipInfo_obj.put("oss_url",image_url);
                    updatedValue.put("album", array);
                }
                if (jsonObject.containsKey("memo") && !jsonObject.get("memo").toString().equals("")) {
                    //备忘
                    DBObject obj = dbCursor1.next();
                    JSONArray array = new JSONArray();
                    if (obj.containsField("memo")) {
                        String memo = obj.get("memo").toString();
                        array = JSON.parseArray(memo);
                    }
                    String content = jsonObject.get("memo").toString();
                    String time = jsonObject.get("time").toString();
                    now = Common.DATETIME_FORMAT.parse(time);
                    JSONObject memo_obj = new JSONObject();
                    memo_obj.put("content", content);
                    memo_obj.put("time", time);
                    memo_obj.put("memoid", corp_code + vip_id + Common.DATETIME_FORMAT_DAY_NUM.format(now));
                    array.add(memo_obj);

                    updatedValue.put("memo", array);
                }
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                cursor.update(updateCondition, updateSetValue);
            } else {
                //记录不存在，插入
                DBObject saveData = new BasicDBObject();
                saveData.put("_id", corp_code + vip_id);
                saveData.put("vip_id", vip_id);
                saveData.put("corp_code", corp_code);
                saveData.put("card_no", card_no);
                saveData.put("phone", phone);
                saveData.put("corp_code", corp_code);
                String remark = "";
                String avatar = "";
                JSONArray album_array = new JSONArray();
                JSONArray memo_array = new JSONArray();

                if (jsonObject.containsKey("remark")) {
                    remark = jsonObject.get("remark").toString();
                }
                if (jsonObject.containsKey("avatar")) {
                    avatar = jsonObject.get("avatar").toString();
                    vipInfo_obj.put("oss_url",avatar);
                }
                if (jsonObject.containsKey("image_url")) {
                    String image_url = jsonObject.get("image_url").toString();

                    String srcKey = image_url.replace("http://products-image.oss-cn-hangzhou.aliyuncs.com/","");
                    String destKey = "Album/Vip/iShow/"+card_no+"_"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date())+".jpg";
                    OssUtils oo = new OssUtils();
                    oo.renameObject(Common.BUCKET_NAME,srcKey,Common.BUCKET_NAME,destKey);
                    image_url = "http://products-image.oss-cn-hangzhou.aliyuncs.com/"+destKey;

                    JSONObject image = new JSONObject();
                    image.put("image_url", image_url);
                    image.put("time", Common.DATETIME_FORMAT.format(now));
                    album_array.add(image);
                    vipInfo_obj.put("oss_url",image_url);
                }
                if (jsonObject.containsKey("memo")) {
                    String content = jsonObject.get("memo").toString();
                    String time = jsonObject.get("time").toString();
                    now = Common.DATETIME_FORMAT.parse(time);
                    JSONObject memo_obj = new JSONObject();
                    memo_obj.put("content", content);
                    memo_obj.put("time", time);
                    memo_obj.put("memoid", corp_code + vip_id + Common.DATETIME_FORMAT_DAY_NUM.format(now));
                    memo_array.add(memo_obj);
                }
                saveData.put("remark", remark);
                saveData.put("avatar", avatar);
                saveData.put("album", album_array);
                saveData.put("memo", memo_array);
                cursor.save(saveData);
            }
        }
        return vipInfo_obj;
    }

    public String recharge(JSONObject jsonObject,String user_code1,String user_name1) throws Exception{
        String status = Common.DATABEAN_CODE_SUCCESS;
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_check);
        Date now = new Date();
        String type = jsonObject.get("type").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String vip_id = jsonObject.get("vip_id").toString();
        String vip_name = jsonObject.get("vip_name").toString();
        String card_no = jsonObject.get("card_no").toString();//会员卡号
        String remark = jsonObject.get("remark").toString();
        String store_code = jsonObject.get("store_code").toString();//操作店仓
        String store_name = jsonObject.get("store_name").toString();//操作店仓
        String create_date = jsonObject.get("date").toString();
        String date = create_date.replace("-","");//单据日期
        String user_code = "";
        String user_name = "";
        if (jsonObject.containsKey("user_code") && !jsonObject.getString("user_code").equals(""))
            user_code = jsonObject.get("user_code").toString();//经办人
        if (jsonObject.containsKey("user_name") && !jsonObject.getString("user_name").equals(""))
            user_name = jsonObject.get("user_name").toString();//经办人

        DBObject object = new BasicDBObject();
        object.put("corp_code",corp_code);
        object.put("status","0"); //未审核
        object.put("vip_id",vip_id);
        object.put("card_no",card_no);
        object.put("vip_name",vip_name);

        object.put("created_date",Common.DATETIME_FORMAT.format(now));
        object.put("creater",user_name1+"("+user_code1+")");
        object.put("modified_date",Common.DATETIME_FORMAT.format(now));
        object.put("modifier",user_name1+"("+user_code1+")");
        object.put("remark",remark);
        object.put("isactive","Y");

        if (type.equals("pay")) {
            String recharge_type = jsonObject.get("pay_type").toString();//1：直接充值:2：退换转充值

            String tag_price = jsonObject.get("price").toString();//吊牌金额
            String pay_price = jsonObject.get("pay_price").toString();//实付金额
            String discount = jsonObject.get("discount").toString();//折扣
            String activity_content = "无";
            if (jsonObject.containsKey("activity_content")){
                activity_content = jsonObject.get("activity_content").toString();//活动
            }

            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put("BILLDATE",date);
            map.put("RECHARGE_TYPE",recharge_type);
            map.put("C_VIPMONEY_STORE_ID__NAME",store_name);
            map.put("SALESREP_ID__NAME",user_name);
            map.put("C_VIP_ID__CARDNO",card_no);
            map.put("TOT_AMT_ACTUAL",tag_price);
            map.put("AMOUNT_ACTUAL",pay_price);
            map.put("ACTIVE_CONTENT",activity_content);
            map.put("DESCRIPTION",remark);
            String result = crmInterfaceService.addPrepaidDocuments(corp_code,map);

            logger.info("json------------addPrepaidDocuments---" + result);

            JSONObject result_obj = JSONObject.parseObject(result);
            String code = result_obj.getString("code");
            if (code.equals("0")){
                result = result_obj.getString("rows");
                JSONObject obj = JSONObject.parseObject(result);
                String bill_id = obj.getString("id");
                String bill_no = obj.getString("DOCNO");

                object.put("_id",corp_code+"_"+bill_id+"_"+System.currentTimeMillis());
                object.put("type","充值");
                object.put("bill_no",bill_no);

                object.put("bill_date",date);
                object.put("store_code",store_code);
                object.put("store_name",store_name);
                object.put("user_code",user_code);
                object.put("user_name",user_name);
                object.put("recharge_type",recharge_type);
                object.put("tag_price",tag_price);
                object.put("pay_price",pay_price);
                object.put("active_content",activity_content);
                object.put("discount",discount);
                cursor.save(object);

            }else {
                return result_obj.getString("message");
            }
        } else if (type.equals("refund")) {
            String recharge_type = jsonObject.get("pay_type").toString();//1:充值单退款，2:余额退款
            String source_no = jsonObject.get("sourceNo").toString();//来源单号

            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put("BILLDATE",date);
            if (recharge_type.equals("1")){
                map.put("RECHARGE_TYPE","VM");
            }else if (recharge_type.equals("2")){
                map.put("RECHARGE_TYPE","BA");
            }
            map.put("C_VIPMONEY_STORE_ID__NAME",store_name);
            map.put("ORGDOCNO",source_no);
            map.put("C_VIP_ID__CARDNO",card_no);
            map.put("DESCRIPTION",remark);
            String result = crmInterfaceService.addRefund(corp_code,map);

            logger.info("json------------addRefund---" + result);

            JSONObject result_obj = JSONObject.parseObject(result);
            String code = result_obj.getString("code");
            if (code.equals("0")){
                result = result_obj.getString("rows");
                JSONObject obj = JSONObject.parseObject(result);
                String bill_id = obj.getString("id");
                String bill_no = obj.getString("DOCNO");

                object.put("_id",corp_code+"_"+bill_id+"_"+System.currentTimeMillis());
//                object.put("check_type","refund"); //退款
                object.put("bill_no",bill_no);
                object.put("type","退款");

                object.put("bill_date",date);
                object.put("store_code",store_code);
                object.put("store_name",store_name);
                object.put("user_code",user_code);
                object.put("user_name",user_name);
                object.put("recharge_type",recharge_type);
                object.put("source_no",source_no);

                cursor.save(object);
            }else {
                return result_obj.getString("message");
            }
        }
        return status;
    }

    public String numberFormat(String text) throws Exception {
        JSONObject result_obj = JSONObject.parseObject(text);
        JSONObject result_obj_all = result_obj.getJSONObject("all");
        JSONObject result_obj_old = result_obj.getJSONObject("old");
        JSONObject result_obj_new = result_obj.getJSONObject("new");

        //所有会员
        String percent_all = result_obj_all.getString("percent");//业绩占比
        String k_price_all_all = result_obj_all.getString("k_price_all");//客件总价
        String price_one_all = result_obj_all.getString("price_one");//件单价
        percent_all = NumberUtil.fmtMicrometer(percent_all);
        k_price_all_all = NumberUtil.keepPrecision(k_price_all_all);
        price_one_all = NumberUtil.keepPrecision(price_one_all);

        result_obj_all.put("percent",percent_all);
        result_obj_all.put("k_price_all",k_price_all_all);
        result_obj_all.put("price_one",price_one_all);

        //新会员
        String percent_new = result_obj_new.getString("percent");//业绩占比
        String k_price_all_new = result_obj_new.getString("k_price_all");//客件总价
        String price_one_new = result_obj_new.getString("price_one");//件单价
        percent_new = NumberUtil.fmtMicrometer(percent_new);
        k_price_all_new = NumberUtil.keepPrecision(k_price_all_new);
        price_one_new = NumberUtil.keepPrecision(price_one_new);

        result_obj_new.put("percent",percent_new);
        result_obj_new.put("k_price_all",k_price_all_new);
        result_obj_new.put("price_one",price_one_new);

        //老会员
        String percent_old = result_obj_old.getString("percent");//业绩占比
        String k_price_all_old = result_obj_old.getString("k_price_all"); //客件总价
        String price_one_old = result_obj_old.getString("price_one");//件单价
        if (!percent_old.equals("--"))
            percent_old = NumberUtil.fmtMicrometer(percent_old);
        if (!k_price_all_old.equals("--"))
            k_price_all_old = NumberUtil.keepPrecision(k_price_all_old);
        if (!price_one_old.equals("--"))
            price_one_old = NumberUtil.keepPrecision(price_one_old);

        result_obj_old.put("percent",percent_old);
        result_obj_old.put("k_price_all",k_price_all_old);
        result_obj_old.put("price_one",price_one_old);

        JSONObject result = new JSONObject();
        result.put("all",result_obj_all);
        result.put("new",result_obj_new);
        result.put("old",result_obj_old);

        return result.toString();
    }

    /**
     * 会员列表，获取会员头像
     * @param corp_code
     * @param result
     * @return
     * @throws Exception
     */
    public String vipAvatar(String corp_code,String result) throws Exception {
        JSONObject result_obj = JSONObject.parseObject(result);
        JSONArray vip_array = result_obj.getJSONArray("all_vip_list");
        logger.info("------AnalysisAllVip-vip列表" + vip_array.size());

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
        JSONArray array = new JSONArray();
        for (int i = 0; i < vip_array.size(); i++) {
            JSONObject vip = vip_array.getJSONObject(i);
            String vip_id = vip.getString("vip_id");
            Map keyMap = new HashMap();
            keyMap.put("_id", corp_code + vip_id);
            BasicDBObject queryCondition = new BasicDBObject();
            queryCondition.putAll(keyMap);
            DBCursor dbCursor1 = cursor.find(queryCondition);
            String avatar = "";
            if (dbCursor1.hasNext()) {
                DBObject obj = dbCursor1.next();
                if (obj.containsField("avatar"))
                    avatar = obj.get("avatar").toString();
            }
            vip.put("avatar", avatar);
            array.add(vip);
        }
        result_obj.put("all_vip_list" , array);

        return result_obj.toString();
    }

    /**
     * 群发消息会员列表，
     * 获取会员最后一次群发时间
     * @param corp_code
     * @param result
     * @return
     * @throws Exception
     */
    public String vipLastSendTime(String corp_code,String result) throws Exception {
        JSONObject result_obj = JSONObject.parseObject(result);
        JSONArray vip_array = result_obj.getJSONArray("all_vip_list");
        logger.info("------AnalysisAllVip-vip列表" + vip_array.size());

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_batchsend_message);
        JSONArray array = new JSONArray();
        for (int i = 0; i < vip_array.size(); i++) {
            JSONObject vip = vip_array.getJSONObject(i);
            String vip_id = vip.getString("vip_id");

            BasicDBObject queryCondition = new BasicDBObject();
            queryCondition.put("vip_id",vip_id);
            queryCondition.put("corp_code",corp_code);

            BasicDBObject sort_obj = new BasicDBObject();
            sort_obj.put("message_date",-1);
            DBCursor dbCursor1 = cursor.find(queryCondition).sort(sort_obj);
            String message_date = "";
            if (dbCursor1.hasNext()) {
                DBObject obj = dbCursor1.next();
                message_date = obj.get("message_date").toString();
            }
            vip.put("message_date", message_date);
            array.add(vip);
        }
        result_obj.put("all_vip_list", array);

        return result_obj.toString();
    }

    /**
     * 店铺VIp,导购VIP 时间范围
     * @param jsonArray
     * @param start_time
     * @param end_time
     * @return
     * @throws Exception
     */
    public JSONArray vipAnalysisTime(JSONArray jsonArray,String start_time ,String end_time)throws Exception{

        if (start_time.equals("") && end_time.equals("")){
            Date now = new Date();
            start_time = Common.DATETIME_FORMAT_DAY.format(TimeUtils.getLastDate(now, -1));
            end_time = Common.DATETIME_FORMAT_DAY.format(now);//时间戳
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            obj.put("start_time",start_time+"~"+end_time);
        }
        return jsonArray;
    }

    public JSONObject getVip(String corp_code,String phone,String vip_card_type) throws Exception{

        DataBox dataBox1 = iceInterfaceAPIService.getVipInfoByCard(corp_code,"",phone);
        String message1 = dataBox1.data.get("message").value;
        JSONObject result_obj1 = JSONObject.parseObject(message1);
        JSONArray vip_array1 = result_obj1.getJSONArray("vip_detail_info");

        JSONArray new_array = new JSONArray();
            for (int i = 0; i < vip_array1.size(); i++) {
                String store_id = vip_array1.getJSONObject(i).getString("store_id");
                if (store_id.equals("")){
                    new_array.add(vip_array1.getJSONObject(i));
                }
        }

        JSONObject vip_info = new JSONObject();
        if (new_array.size() > 1){
            VipCardType vipCardType1 = vipCardTypeService.getVipCardTypeByName(corp_code,vip_card_type,Common.IS_ACTIVE_Y);
            String brand_code = vipCardType1.getBrand_code();

            logger.info("----------brand_code:"+brand_code);
            for (int i = 0; i < new_array.size(); i++) {
                String card_type_id = new_array.getJSONObject(i).getString("card_type_id");
                logger.info("----------card_type_id:"+brand_code);
                VipCardType vipCardType = vipCardTypeService.getVipCardTypeByName(corp_code,card_type_id,Common.IS_ACTIVE_Y);
                if (vipCardType == null){
                    vip_info = new_array.getJSONObject(i);
                    break;
                }else {
                    if (brand_code.equals(vipCardType.getBrand_code())){
                        vip_info = new_array.getJSONObject(i);
                        break;
                    }
                }
            }
        }else if (new_array.size() == 1){
            vip_info = new_array.getJSONObject(0);
        }
        return vip_info;
    }

    public JSONObject pareseData1(JSONObject object1){
        if (object1.containsKey("Type")){
            JSONObject object2 = JSONObject.parseObject(object1.get("Type").toString());
            object2 = pasreData(object2);
            object1.put("Type", object2);
        }
        if (object1.containsKey("TypeMid")){
            JSONObject object2 = JSONObject.parseObject(object1.get("TypeMid").toString());
            object2 = pasreData(object2);
            object1.put("TypeMid", object2);
        }
        if (object1.containsKey("TypeSm")){
            JSONObject object2 = JSONObject.parseObject(object1.get("TypeSm").toString());
            object2 = pasreData(object2);
            object1.put("TypeSm", object2);
        }
        if (object1.containsKey("Series")){
            JSONObject object2 = JSONObject.parseObject(object1.get("Series").toString());
            object2 = pasreData(object2);
            object1.put("Series", object2);
        }
        if (object1.containsKey("Season")){
            JSONObject object2 = JSONObject.parseObject(object1.get("Season").toString());
            object2 = pasreData(object2);
            object1.put("Season", object2);
        }
        if (object1.containsKey("TypeSeries")){
            JSONObject object2 = JSONObject.parseObject(object1.get("TypeSeries").toString());
            object2 = pasreData(object2);
            object1.put("TypeSeries", object2);
        }
        return object1;
    }

    public  JSONObject  pasreData(JSONObject object2){
        JSONArray jsonArray_trade_amt1 = JSONArray.parseArray(object2.get("trade_amt").toString());
        JSONArray jsonArray_trade_num1 = JSONArray.parseArray(object2.get("trade_num").toString());
        JSONArray jsonArray_discount1 = JSONArray.parseArray(object2.get("discount").toString());

        JSONArray jsonArray_trade_amt = new JSONArray();
        JSONArray jsonArray_trade_num = new JSONArray();
        JSONArray jsonArray_discount = new JSONArray();

        for (int i = 0; i < jsonArray_trade_amt1.size(); i++) {
            String value = jsonArray_trade_amt1.getJSONObject(i).getString("value");
            Double a = Double.parseDouble(value);
            if (a != 0){
                jsonArray_trade_amt.add(jsonArray_trade_amt1.getJSONObject(i));
            }
        }
        for (int i = 0; i < jsonArray_trade_num1.size(); i++) {
            String value = jsonArray_trade_num1.getJSONObject(i).getString("value");
            Double a = Double.parseDouble(value);
            if (a != 0){
                jsonArray_trade_num.add(jsonArray_trade_num1.getJSONObject(i));
            }
        }
        for (int i = 0; i < jsonArray_discount1.size(); i++) {
            String value = jsonArray_discount1.getJSONObject(i).getString("value");
            Double a = Double.parseDouble(value);
            if (a != 0){
                jsonArray_discount.add(jsonArray_discount1.getJSONObject(i));
            }
        }
        object2.put("trade_amt",jsonArray_trade_amt);
        object2.put("trade_num",jsonArray_trade_num);
        object2.put("discount",jsonArray_discount);

        if (jsonArray_trade_amt.size() >= 7){
            jsonArray_trade_amt = WebUtils.sortDesc(jsonArray_trade_amt,"value");
            JSONArray jsonArray_other_amt = new JSONArray();
            double other_amt = 0.0;
            int count = 0;
            for (int i = 0; i < jsonArray_trade_amt.size(); i++) {
                if (count <= 6) {
                    JSONObject object3 = jsonArray_trade_amt.getJSONObject(i);
                    if (object3.getString("name").equals("其他")){
                        other_amt += Double.parseDouble(object3.get("value").toString());
                    }else {
                        jsonArray_other_amt.add(jsonArray_trade_amt.get(i));
                        count += 1;
                    }
                } else {
                    JSONObject object3 = JSON.parseObject(jsonArray_trade_amt.get(i).toString());
                    other_amt += Double.parseDouble(object3.get("value").toString());
                }
            }

            JSONObject object4 = new JSONObject();
            object4.put("name", "其他");
            object4.put("value", NumberUtil.keepPrecision(other_amt));
            jsonArray_other_amt.add(object4);

            object2.put("trade_amt", jsonArray_other_amt);
        }
        if (jsonArray_trade_num.size() >= 7){
            jsonArray_trade_num = WebUtils.sortDesc(jsonArray_trade_num,"value");

            JSONArray jsonArray_other_num = new JSONArray();
            double other_num = 0.0;
            int count = 0;
            for (int i = 0; i < jsonArray_trade_num.size(); i++) {
                if (count <= 6) {
                    JSONObject object3 = jsonArray_trade_num.getJSONObject(i);
                    if (object3.getString("name").equals("其他")){
                        other_num += Double.parseDouble(object3.get("value").toString());
                    }else {
                        jsonArray_other_num.add(jsonArray_trade_num.get(i));
                        count += 1;
                    }
                } else {
                    JSONObject object3 = JSON.parseObject(jsonArray_trade_num.get(i).toString());
                    other_num += Double.parseDouble(object3.get("value").toString());
                }
            }
            JSONObject object5 = new JSONObject();
            object5.put("name", "其他");
            object5.put("value", NumberUtil.keepPrecision(other_num));
            jsonArray_other_num.add(object5);

            object2.put("trade_num", jsonArray_other_num);
        }
        if (jsonArray_discount.size() >= 7){
            jsonArray_discount = WebUtils.sortDesc(jsonArray_discount,"value");

            JSONArray jsonArray_other_discount = new JSONArray();
            double other_discount = 0.0;
            int count = 0;
            for (int i = 0; i < jsonArray_discount.size(); i++) {
                if (count <= 6) {
                    JSONObject object3 = jsonArray_discount.getJSONObject(i);
                    if (object3.getString("name").equals("其他")) {
                        other_discount += Double.parseDouble(object3.get("value").toString());
                    } else {
                        jsonArray_other_discount.add(jsonArray_discount.get(i));
                        count += 1;
                    }
                }else {
                    JSONObject object3 = JSON.parseObject(jsonArray_discount.get(i).toString());
                    other_discount += Double.parseDouble(object3.get("value").toString());
                }
            }
            if(jsonArray_discount.size()>6){
                int i = jsonArray_discount.size() - 6;
                other_discount=other_discount/i;
            }
            String other_discount_str = NumberUtil.keepPrecision(other_discount);

            JSONObject object6 = new JSONObject();
            object6.put("name", "其他");
            object6.put("value", NumberUtil.keepPrecision(other_discount_str));
            jsonArray_other_discount.add(object6);

            object2.put("discount", jsonArray_other_discount);
        }

        return object2;
    }

    public JSONObject getVipAuthCode(String corp_code,String vip_id,String phone,String vip_name,String type) throws Exception{
        JSONObject object = new JSONObject();
        HashMap<String,Object> map = new HashMap<String, Object>();
        map.put("id",vip_id);
        map.put("phone",phone);
        String result = "";
        Random r = new Random();
        Double d = r.nextDouble();
        String authcode = d.toString().substring(3, 3 + 4);
//        String text = "";
        if (type.equals("1")){
            // 预存款密码
            map.put("PASS_WORD",authcode);
            result = crmInterfaceService.modfiy_passwordVip(corp_code,map);
//            text = "尊敬的#VIP_NAME# :您本次充值消费密码为#AuthCode#";
        }else if (type.equals("2")){
            //积分付款密码
            map.put("INTEGRAL_PASSWORD",authcode);
            result = crmInterfaceService.modIntegral_passwordVip(corp_code,map);
//            text = "尊敬的#VIP_NAME# :您本次积分付款密码为#AuthCode#";
        }else if (type.equals("3")){
            //积分付款密码
            result = crmInterfaceService.getVipBirthTicket(corp_code,map);
//            text = "尊敬的#VIP_NAME#:您本次生日券券号#ticketno#，验证码#checkno#，面额#money#;";
        }
        JSONObject result_obj = JSONObject.parseObject(result);
        String code = result_obj.getString("code");
        if (code.equals("0")){
//            text = text.replace("#VIP_NAME#",vip_name);
//            text = text.replace("#AuthCode#",authcode);
            if (type.equals("3")){
                JSONArray message_array = result_obj.getJSONArray("result");
                if (message_array.size() < 1){
                    object.put("errorcode",Common.DATABEAN_CODE_ERROR);
                    object.put("errormessage","当前VIP不存在有效生日券");
                    return object;
                }else {
//                    for (int i = 0; i < message_array.size(); i++) {
//                        JSONObject message_obj = message_array.getJSONObject(i);
//                        text = text.replace("#ticketno#",message_obj.getString("ticketno"));
//                        text = text.replace("#checkno#",message_obj.getString("checkno"));
//                        text = text.replace("#money#",message_obj.getString("money"));
//                        iceInterfaceService.sendSmsV2(corp_code,text,phone);
//                    }
                }
            }else {
                object.put("errorcode",Common.DATABEAN_CODE_SUCCESS);
                object.put("errormessage",authcode);
//                iceInterfaceService.sendSmsV2(corp_code,text,phone);
            }
        }else {
            object.put("errorcode",result_obj.getString("code"));
            object.put("errormessage",result_obj.getString("message"));
        }
        return object;
    }
}
