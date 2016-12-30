package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;


import com.bizvane.ishop.entity.VipGroup;
import com.bizvane.ishop.entity.WxTemplate;
import com.bizvane.sun.common.service.http.HttpClient;
import com.bizvane.ishop.dao.VipFsendMapper;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.VipFsend;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;

import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by nanji on 2016/11/24.
 */
@Service
public class VipFsendServiceImpl implements VipFsendService {
    @Autowired
    VipFsendMapper vipFsendMapper;
    @Autowired
    private IceInterfaceService iceInterfaceService;
    @Autowired
    private WxTemplateService wxTemplateService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private VipGroupService vipGroupService;
    @Autowired
    MongoDBClient mongodbClient;

    private static HttpClient httpClient = new HttpClient();
    private static final Logger logger = Logger.getLogger(VipFsendServiceImpl.class);


    /**
     * 查看选择接收群发消息的会员信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public String getVipFsendById(int id) throws Exception {

        String message = "";
        String vip_id = "";
        VipFsend vipFsend = vipFsendMapper.selectById(id);
        String sms_code=vipFsend.getSms_code();
        String corp_code = vipFsend.getCorp_code();
        String send_type=vipFsend.getSend_type();
        String send_scope = vipFsend.getSend_scope();
        String sms_vips = vipFsend.getSms_vips();

        JSONObject vips_obj = JSONObject.parseObject(sms_vips);
        if (send_type.equals("sms")) {

            if(send_scope.equals("vip")){
                String type = vips_obj.get("type").toString().trim();
                if (type.equals("1")) {
                    String area_code = vips_obj.get("area_code").toString();
                    String brand_code = vips_obj.get("brand_code").toString();
                    String store_code = vips_obj.get("store_code").toString();
                    String user_code = vips_obj.get("user_code").toString();
                    if (user_code.equals("")) {
                        if (store_code.equals("")) {
                            List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                            for (int i = 0; i < storeList.size(); i++) {
                                store_code = store_code + storeList.get(i).getStore_code() + ",";
                            }
                        }
                        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                        Data data_store_code = new Data("store_codes", store_code, ValueType.PARAM);
                        Map datalist = new HashMap<String, Data>();
                        datalist.put(data_corp_code.key, data_corp_code);
                        datalist.put(data_store_code.key, data_store_code);
                        DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo", datalist);
                        message = dataBox.data.get("message").value;
                    } else {
                        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                        Data data_user_code = new Data("user_codes", user_code, ValueType.PARAM);
                        Map datalist = new HashMap<String, Data>();
                        datalist.put(data_corp_code.key, data_corp_code);
                        datalist.put(data_user_code.key, data_user_code);
                        DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo", datalist);
                        logger.info("------vipFsend群发消息查看详情-vip列表" + dataBox.status.toString());
                        message = dataBox.data.get("message").value;
                    }
                } else if (type.equals("2")) {
                    String vips = vips_obj.get("vips").toString();
                    Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                    Data data_vip_id = new Data("vip_ids", vips, ValueType.PARAM);
                    Map datalist = new HashMap<String, Data>();
                    datalist.put(data_corp_code.key, data_corp_code);
                    datalist.put(data_vip_id.key, data_vip_id);
                    DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo", datalist);
                    logger.info("------vipFsend群发消息-vip列表" + dataBox.status.toString());
                    message = dataBox.data.get("message").value;
                }
                return message;
            }else if(send_scope.equals("vip_group")){

                JSONObject obj=new JSONObject();
                JSONObject obj1=new JSONObject();
                obj1.put("vip_name","彭旭丽");
                obj1.put("cardno","13016691660");
                obj1.put("vip_phone","13016691660");

                JSONArray arr=new JSONArray();
                arr.add(obj1);
                obj.put("vip_info",arr);
                message=JSON.toJSONString(obj);
                return message;
            }

        } else if (send_type.equals("wxmass")) {
            //如果发送类型是微信群发消息，根据筛选会员方式获取vip_id
            JSONArray vip_infos=null;
            if(send_scope.equals("vip")){
                String type = vips_obj.get("type").toString().trim();
                if (type.equals("1")) {
                    String area_code = vips_obj.get("area_code").toString();
                    String brand_code = vips_obj.get("brand_code").toString();
                    String store_code = vips_obj.get("store_code").toString();
                    String user_code = vips_obj.get("user_code").toString();
                    if (user_code.equals("")) {
                        if (store_code.equals("")) {
                            List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                            for (int i = 0; i < storeList.size(); i++) {
                                store_code = store_code + storeList.get(i).getStore_code() + ",";
                            }
                        }
                        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                        Data data_store_code = new Data("store_codes", store_code, ValueType.PARAM);
                        Map datalist = new HashMap<String, Data>();
                        datalist.put(data_corp_code.key, data_corp_code);
                        datalist.put(data_store_code.key, data_store_code);
                        DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo", datalist);
                        logger.info("------vipFsend群发消息查看详情-vip列表" + dataBox.status.toString());
                        message = dataBox.data.get("message").value;
                        JSONObject msg_obj = JSONObject.parseObject(message);
                        vip_infos = msg_obj.getJSONArray("vip_info");
                        for (int i = 0; i < vip_infos.size(); i++) {
                            JSONObject vip_obj = vip_infos.getJSONObject(i);
                            vip_id = vip_id + vip_obj.getString("vip_id") + ",";
                            vip_obj.put("is_send","未发送");

                        }
                    } else {
                        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                        Data data_user_code = new Data("user_codes", user_code, ValueType.PARAM);
                        Map datalist = new HashMap<String, Data>();
                        datalist.put(data_corp_code.key, data_corp_code);
                        datalist.put(data_user_code.key, data_user_code);
                        DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo", datalist);
                        logger.info("------vipFsend群发消息查看详情-vip列表" + dataBox.status.toString());
                        message = dataBox.data.get("message").value;
                        JSONObject msg_obj = JSONObject.parseObject(message);
                        vip_infos = msg_obj.getJSONArray("vip_info");
                        for (int i = 0; i < vip_infos.size(); i++) {
                            JSONObject vip_obj = vip_infos.getJSONObject(i);
                            vip_id = vip_id + vip_obj.getString("vip_id") + ",";
                            vip_obj.put("is_send","未发送");
                        }

                    }
                } else if (type.equals("2")) {
                    vip_id = vips_obj.get("vips").toString();
                    Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                    Data data_vip_id = new Data("vip_ids", vip_id, ValueType.PARAM);
                    Map datalist = new HashMap<String, Data>();
                    datalist.put(data_corp_code.key, data_corp_code);
                    datalist.put(data_vip_id.key, data_vip_id);

                    DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo", datalist);
                    logger.info("------vipFsend群发消息查看详情-vip列表" + dataBox.status.toString());
                    message = dataBox.data.get("message").value;
                    JSONObject msg_obj = JSONObject.parseObject(message);
                    vip_infos = msg_obj.getJSONArray("vip_info");
                    for (int i = 0; i < vip_infos.size(); i++) {
                        JSONObject vip_obj = vip_infos.getJSONObject(i);
                        vip_obj.put("is_send","未发送");

                    }
                }

                //查询MongoDB数据库获取列表
                String vipid[] = vip_id.split(",");

                List<Map<String, Object>> list = new ArrayList();
                String vip = "";
                for (int i = 0; i < vipid.length; i++) {
                    vip = vipid[i];
                    Map query_key = new HashMap();
                    query_key.put("wxMass", "wxMass");
                    query_key.put("mass", sms_code);
                    query_key.put("vip_id", vip);
                    List<Map<String, Object>> message_list = mongodbClient.query("vip_message_content", query_key);

                    if (message_list.size() == 0) {
                        JSONObject info=new JSONObject();
                        info.put("vip_info",vip_infos);
                        message = JSON.toJSONString(info);
                    } else {
                        list.addAll(message_list);
                        JSONObject vips_info = new JSONObject();
                        vips_info.put("vip_info", list);
                        message = JSON.toJSONString(vips_info);
                    }
                }
                return message;
            }else if(send_scope.equals("vip_group")){

                JSONObject obj=new JSONObject();
                JSONObject obj1=new JSONObject();
                obj1.put("vip_name","彭旭丽");
                obj1.put("vip_id","316424");
                obj1.put("is_send","未发送");
                JSONArray arr=new JSONArray();
                arr.add(obj1);
                obj.put("vip_info",arr);

                message=JSON.toJSONString(obj);
                return message;
            }

        }else {
            message = "发送类型不合法";
        }
        return message;
    }

    @Override
    public VipFsend getVipFsendInfoById(int id) throws Exception {
        VipFsend vipFsend=vipFsendMapper.selectById(id);

        return vipFsend;
    }

    @Override
    public PageInfo<VipFsend> getAllVipFsendByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<VipFsend> vipFsends;
        PageHelper.startPage(page_number, page_size);
        vipFsends = vipFsendMapper.selectAllFsend(corp_code, search_value);
        for (VipFsend vipFsend : vipFsends) {
            vipFsend.setIsactive(CheckUtils.CheckIsactive(vipFsend.getIsactive()));
            String send_type = vipFsend.getSend_type();
            String result = change_sendType(send_type);
            vipFsend.setSend_type(result);
        }

        PageInfo<VipFsend> page = new PageInfo<VipFsend>(vipFsends);

        return page;
    }

    @Override
    public String insert(String message, String user_id,int tem_id) throws Exception {

        String status = Common.DATABEAN_CODE_SUCCESS;
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
        Date now = new Date();
        //群发消息的发送类型
        String send_type = jsonObject.get("send_type").toString().trim();
        String send_scope = jsonObject.get("send_scope").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(now);
        VipFsend vipFsend = WebUtils.JSON2Bean(jsonObject, VipFsend.class);
        String content = vipFsend.getContent();
        String sms_vips = vipFsend.getSms_vips().trim();

        String openids = "";
        String phone = "";
        String vip_id = "";
        String vip_name = "";
        String vip_group_code = "";
        String group_type = "";
        if(send_scope.equals("vip")){
            JSONObject sms_vips_obj = JSONObject.parseObject(sms_vips);
            String vips = sms_vips_obj.get("vips").toString();
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_vip_id = new Data("vip_ids", vips, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_vip_id.key, data_vip_id);
//            vip_id = vip_id + vips;
            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo", datalist);
            logger.info("------vipFsend群发消息-vip列表" + dataBox.status.toString());
            if (dataBox.status.toString().equals("SUCCESS")){
                String message1 = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(message1);
                JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
                for (int i = 0; i < vip_infos.size(); i++) {
                    JSONObject vip_obj = vip_infos.getJSONObject(i);
                    phone = phone + vip_obj.getString("vip_phone") + ",";
                    vip_name = vip_name + vip_obj.getString("vip_name") + ",";

                    if (!vip_obj.containsKey("open_id") ) {
                        openids = openids + "null" + ",";
                    }else {
                        openids = openids + vip_obj.getString("open_id") + ",";
                    }
                }
            }
        }
        //插表成功后调用接口
        if (send_type.equals("sms")) {
            //发送类型：短信
            //发送范围：指定会员
            if(send_scope.equals("vip")){

                Data data_channel = new Data("channel", "santong", ValueType.PARAM);
                Data data_phone = new Data("phone", phone, ValueType.PARAM);
                Data data_text = new Data("text", content, ValueType.PARAM);

                Map datalist = new HashMap<String, Data>();
                datalist.put(data_channel.key, data_channel);
                datalist.put(data_phone.key, data_phone);
                datalist.put(data_text.key, data_text);
                DataBox dataBox = iceInterfaceService.iceInterfaceV3("SendSMS", datalist);
                logger.info("------vipFsend群发消息-vip列表" + dataBox.status.toString());
                if (!dataBox.status.toString().equals("SUCCESS")) {
                    status = "发送失败";
                    return status;
                }
            }else if(send_scope.equals("vip_group")){
                //发送范围：分组会员

            }
        } else if (send_type.equals("wxmass")) {
            //发送类型：微信群发消息
            //发送范围：指定会员
            if(send_scope.equals("vip")){
                //根据id查询微信模板信息
                WxTemplate wxTemplate= wxTemplateService.getTemplateById(tem_id);
                String app_user_name=wxTemplate.getApp_user_name();
                String template_id=wxTemplate.getTemplate_id();
                //逗号分割
                String openid[] = openids.split(",");
                String vipid[] = vip_id.split(",");
                String vipname[] = vip_name.split(",");
//                String open_id="";
                String id="";
                String name="";
                //message_id 插入MongoDB,主键
                String message_id = app_user_name + openid + System.currentTimeMillis();

                JSONObject template_content = new JSONObject();
                String  result="";
                if(openid.length>=2){
                    //调用微信群发消息接口
                    String trans_open_ids = "";
                    for (int i = 0; i < openid.length; i++) {
                        if (!openid[i].equals("") && !openid[i].equals("null"))
                            trans_open_ids = trans_open_ids + openid[i] + ",";
                    }
                    template_content.put("content", content);//微信群发内容
                    template_content.put("app_user_name", app_user_name); //app_user_name
                    template_content.put("openid", trans_open_ids);//所选择会员的openid
                    result = sendWxMass(template_content);

                }else if(openid.length>=1){
                    //调用微信模板消息接口
                    JSONObject con=new JSONObject();
                    con.put("first", "您好，您的专属导购给您发了一条消息");
                    con.put("keyword1", System.currentTimeMillis()); //关键词
                    con.put("keyword2", Common.DATETIME_FORMAT.format(now)); //关键词
                    con.put("remark", "请点击查看！"); //备注
                    template_content.put("content",con);
                    template_content.put("app_user_name",app_user_name);
                    template_content.put("template_id",template_id);
                    template_content.put("openid",openid[0]);
                    template_content.put("message_id",message_id);
                    result = sendTemplate(template_content);
                }
                JSONObject info = JSONObject.parseObject(result);

                //群发消息发送成功之后存入mongoDB
                if ("0".equals(info.getString("errcode"))) {
                    for (int i = 0; i < openid.length; i++) {
                        String open_id = openid[i];
                        for (int j = 0; j < vipid.length; j++) {
                            id = vipid[i];
                            for (int k = 0; k <vipname.length ; k++) {
                                name = vipname[i];
                            }
                        }
                        insertMongoDB(open_id,corp_code, id, app_user_name, name,content,sms_code,message_id);
                    }
                    return status;
                } else {
                    status = info.get("errmsg").toString();
                    return status;
                }
            }else if(send_scope.equals("vip_group")){
                // status = Common.DATABEAN_CODE_SUCCESS;
            }
        } else {
            status = "发送类型不合法";
            return status;
        }

        vipFsend.setSms_code(sms_code);
        vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
        vipFsend.setCreater(user_id);
        vipFsend.setSend_type(send_type);
        vipFsend.setModifier(user_id);
        vipFsend.setSms_vips(sms_vips);
        vipFsend.setContent(content);
        vipFsend.setIsactive(Common.IS_ACTIVE_Y);
        vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
        vipFsendMapper.insertFsend(vipFsend);

        return status;
    }



    @Override
    public int delete(int id) throws Exception {
        return vipFsendMapper.deleteById(id);
    }

    @Override
    public PageInfo<VipFsend> getAllVipFsendScreen(int page_number, int page_size, String
            corp_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        JSONObject date = JSONObject.parseObject(map.get("created_date"));
        params.put("created_date_start", date.get("start").toString());
        String end = date.get("end").toString();
        if (!end.equals(""))
            end = end + " 23:59:59";
        params.put("created_date_end", end);
        map.remove("created_date");

        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<VipFsend> list1 = vipFsendMapper.selectAllFsendScreen(params);
        for (VipFsend vipFsend : list1) {
            vipFsend.setIsactive(CheckUtils.CheckIsactive(vipFsend.getIsactive()));
            String send_type = vipFsend.getSend_type();
            String result = change_sendType(send_type);
            vipFsend.setSend_type(result);

        }
        PageInfo<VipFsend> page = new PageInfo<VipFsend>(list1);
        return page;
    }


    /**
     * 发送类型转换
     */
    public String change_sendType(String send_type) {
        String result = "";
        if (send_type == null) {
            result = "";
        } else if (send_type.equals("sms")) {
            result = "短信";
        } else if (send_type.equals("wxmass")) {
            result = "微信群发消息";
        } else {
            result = "";
        }
        return result;
    }

    /**
     * 微信群发消息
     * @param extras
     * @return
     * @throws Exception
     */
    public static String sendWxMass(JSONObject extras) throws Exception {

        RequestBody body = RequestBody.create(Common.JSON, extras.toJSONString());
        Request request = new Request.Builder().url(Common.SENDWXMASS_URL).post(body).build();
        Response response = httpClient.post(request);
        String result = response.body().string();
        return result;
    }

    /**
     * 插入mongoDB
     * @param openid
     * @param corp_code
     * @param vip_id
     * @param app_user_name
     * @param vip_name
     * @param content
     * @throws Exception
     */
    public void insertMongoDB(String openid, String corp_code,String vip_id, String app_user_name, String vip_name,String content,String mass,String message_id) throws Exception {

        Date now = new Date();
        String message_date = Common.DATETIME_FORMAT.format(now);
        //String message_id = app_user_name + openid + System.currentTimeMillis();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_vip_message_content);
        DBObject saveData = new BasicDBObject();
        saveData.put("_id", message_id);
        saveData.put("message_target", "1");
        saveData.put("corp_code", corp_code);
        saveData.put("open_id", openid);
        saveData.put("vip_id", vip_id);
        saveData.put("mass", mass);
        saveData.put("message_type", "text");
        saveData.put("message_content", content);
        saveData.put("message_date", message_date);
        saveData.put("auth_appid", app_user_name);
        saveData.put("wxMass", "wxMass");
        saveData.put("is_send", "Y");
        saveData.put("vip_name", vip_name);
        collection.insert(saveData);

    }
    /**
     * 微信发送模板消息
     *
     * @param extras
     * @return
     * @throws Exception
     */
    public static String sendTemplate(JSONObject extras) throws Exception {

        RequestBody body = RequestBody.create(Common.JSON, extras.toJSONString());
        Request request = new Request.Builder().url(Common.SENDTEMPLATE_URL).post(body).build();
        Response response = httpClient.post(request);
        String result = response.body().string();
        return result;
    }





}
