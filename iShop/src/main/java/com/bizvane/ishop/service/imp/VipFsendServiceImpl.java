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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        VipFsend vipFsend = vipFsendMapper.selectById(id);
        String sms_code=vipFsend.getSms_code();
        String corp_code = vipFsend.getCorp_code();
        String send_scope = vipFsend.getSend_scope();
        String send_type = vipFsend.getSend_type();

                if(send_type.equals("sms")){
//如果发送类型是微信群发消息，根据筛选会员方式获取vip_id
                    if(send_scope.equals("vip")){
                        String sms_vips = vipFsend.getSms_vips();
                        JSONObject vips_obj = JSONObject.parseObject(sms_vips);
                        String vip_id = vips_obj.get("vips").toString();
                        //查询MongoDB数据库获取列表
                        String vipid[] = vip_id.split(",");
                        List<Map<String, Object>> list = new ArrayList();
                        for (int i = 0; i < vipid.length; i++) {
                            String vip = vipid[i];
                            Map query_key = new HashMap();
                            query_key.put("sms_code", sms_code);
                            query_key.put("vip_id", vip);
                            query_key.put("corp_code", corp_code);

                            List<Map<String, Object>> message_list = mongodbClient.query("vip_message_content", query_key);
                            if (message_list.size() == 0) {
                                JSONObject info=new JSONObject();
                                info.put("vip_info",null);
                                message = JSON.toJSONString(info);
                            } else {
                                list.addAll(message_list);
                                JSONObject vips_info = new JSONObject();
                                vips_info.put("vip_info", list);
                                message = JSON.toJSONString(vips_info);
                            }
                        }
                    }else if(send_scope.equals("vip_group")){
                        JSONObject obj=new JSONObject();
                        JSONObject obj1=new JSONObject();
                        obj1.put("vip_name","彭旭丽");
                        obj1.put("vip_id","316424");
                        obj1.put("is_send","未发送");
                        obj1.put("cardno","13016691660");
                        JSONArray arr=new JSONArray();
                        arr.add(obj1);
                        obj.put("vip_info",arr);

                        message=JSON.toJSONString(obj);
                    }
                }else if(send_type.equals("wxmass")){
                    //如果发送类型是微信群发消息，根据筛选会员方式获取vip_id
                    if(send_scope.equals("vip")){
                        String sms_vips = vipFsend.getSms_vips();
                        JSONObject vips_obj = JSONObject.parseObject(sms_vips);
                        String vip_id = vips_obj.get("vips").toString();
                        //查询MongoDB数据库获取列表
                        String vipid[] = vip_id.split(",");
                        List<Map<String, Object>> list = new ArrayList();
                        for (int i = 0; i < vipid.length; i++) {
                            String vip = vipid[i];
                            Map query_key = new HashMap();
                            query_key.put("sms_code", sms_code);
                            query_key.put("vip_id", vip);
                            query_key.put("corp_code", corp_code);

                            List<Map<String, Object>> message_list = mongodbClient.query("vip_message_content", query_key);
                            if (message_list.size() == 0) {
                                JSONObject info=new JSONObject();
                                info.put("vip_info",null);
                                message = JSON.toJSONString(info);
                            } else {
                                list.addAll(message_list);
                                JSONObject vips_info = new JSONObject();
                                vips_info.put("vip_info", list);
                                message = JSON.toJSONString(vips_info);
                            }
                        }
                    }else if(send_scope.equals("vip_group")){
                        JSONObject obj=new JSONObject();
                        JSONObject obj1=new JSONObject();
                        obj1.put("vip_name","彭旭丽");
                        obj1.put("vip_id","316424");
                        obj1.put("is_send","未发送");
                        obj1.put("is_read","未读");
                        obj1.put("cardno","13016691660");
                        JSONArray arr=new JSONArray();
                        arr.add(obj1);
                        obj.put("vip_info",arr);

                        message=JSON.toJSONString(obj);
                    }
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

        if(send_scope.equals("vip")){
            String open_id = "";
            String phone = "";
            String vip_id = "";
            String vip_name = "";
            String cardno = "";
            JSONObject sms_vips_obj = JSONObject.parseObject(sms_vips);
            vip_id = sms_vips_obj.get("vips").toString();
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_vip_id = new Data("vip_ids", vip_id, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_vip_id.key, data_vip_id);
            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo", datalist);
            logger.info("------vipFsend群发消息-vip列表" + dataBox.status.toString());
            if (dataBox.status.toString().equals("SUCCESS")){
                String message1 = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(message1);
                JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
                for (int i = 0; i < vip_infos.size(); i++) {
                    JSONObject vip_obj = vip_infos.getJSONObject(i);
                    phone = phone + vip_obj.getString("vip_phone") + ",";
                    cardno = cardno + vip_obj.getString("cardno") + ",";
                    vip_name = vip_name + vip_obj.getString("vip_name") + ",";
                    if (!vip_obj.containsKey("open_id") || vip_obj.getString("open_id").equals("")) {
                        open_id = open_id + "null" + ",";
                    }else {
                        open_id = open_id + vip_obj.getString("open_id") + ",";
                    }
                }
            }
            //逗号分割
            String[] phones = phone.split(",");
            String[] cardnos = cardno.split(",");
            String[] open_ids = open_id.split(",");
            String[] vip_ids = vip_id.split(",");
            String[] vip_names = vip_name.split(",");

            String send_phone = "";
            String send_cardno = "";
            String send_open_id = "";
            String send_vip_id = "";
            String send_vip_name = "";
            if (send_type.equals("sms")) {
                //发送类型：短信
                Pattern pattern4 = Pattern.compile("(^(\\d{3,4}-)?\\d{7,8})$|(1[3,4,5,7,8]{1}\\d{9})");
                for (int i = 0; i < phones.length; i++) {
                    Matcher matcher = pattern4.matcher(phones[i].trim());
                    String message_id = corp_code + vip_ids[i] + System.currentTimeMillis();
                    if (matcher.matches() == false) {
                        insertMongoDB(corp_code, user_id, "", "", vip_ids[i], vip_names[i],cardnos[i],phones[i], sms_code, "", content, message_id, "N");
                    } else {
                        send_phone = send_phone + phones[i] + ",";
                        send_cardno = send_cardno + cardnos[i] + ",";
                        send_vip_id = send_vip_id + vip_ids[i] + ",";
                        send_vip_name = send_vip_name + vip_names[i] + ",";
                    }
                }
                if (!send_phone.equals("")) {
                    Data data_channel = new Data("channel", "santong", ValueType.PARAM);
                    Data data_phone = new Data("phone", send_phone, ValueType.PARAM);
                    Data data_text = new Data("text", content, ValueType.PARAM);

                    Map datalist1 = new HashMap<String, Data>();
                    datalist1.put(data_channel.key, data_channel);
                    datalist1.put(data_phone.key, data_phone);
                    datalist1.put(data_text.key, data_text);
                    DataBox dataBox1 = iceInterfaceService.iceInterfaceV3("SendSMS", datalist1);
                    logger.info("------vipFsend群发消息-vip列表" + dataBox1.status.toString());

                    String[] send_phones = send_phone.split(",");
                    String[] send_cardnos = send_cardno.split(",");
                    String[] send_vip_ids = send_vip_id.split(",");
                    String[] send_vip_names = send_vip_name.split(",");
                    for (int i = 0; i < send_phones.length; i++) {
                        String message_id = corp_code + send_vip_ids[i] + System.currentTimeMillis();
                        insertMongoDB(corp_code, user_id, "", "", send_vip_ids[i], send_vip_names[i],send_cardnos[i],send_phones[i], sms_code, "", content, message_id, "Y");
                    }
                }
            }else {
                //根据id查询微信模板信息
                WxTemplate wxTemplate= wxTemplateService.getTemplateById(tem_id);
                String app_id=wxTemplate.getApp_id();
                String app_user_name=wxTemplate.getApp_user_name();
                String template_id=wxTemplate.getTemplate_id();

                for (int i = 0; i < vip_ids.length; i++) {
                    String message_id = corp_code + vip_ids[i] + System.currentTimeMillis();
                    String openid = open_ids[i];
                    if (openid.equals("") || openid.equals("null")){
                        insertMongoDB(corp_code,user_id,template_id,"",vip_ids[i],vip_names[i],cardnos[i],phones[i],sms_code,app_id,content,message_id,"N");
                    }else {
                        send_open_id = send_open_id + open_ids[i] + ",";
                        send_vip_id = send_vip_id + vip_ids[i] + ",";
                        send_vip_name = send_vip_name + vip_names[i] + ",";
                        send_cardno = send_cardno + cardnos[i] + ",";
                        send_phone = send_phone + phones[i] + ",";
                    }
                }
                if (!send_open_id.equals("")){
                    String[] send_open_ids = send_open_id.split(",");
                    String[] send_vip_ids = send_vip_id.split(",");
                    String[] send_vip_names = send_vip_name.split(",");
                    String[] send_phones = send_phone.split(",");
                    String[] send_cardnos = send_cardno.split(",");

                    String  result="";
                    if(send_open_ids.length >= 2){
                        //调用微信群发消息接口
                        JSONObject template_content = new JSONObject();
                        template_content.put("content", content);//微信群发内容
                        template_content.put("app_user_name", app_user_name); //app_user_name
                        template_content.put("openid", send_open_id);//所选择会员的openid
                        result = sendWxMass(template_content);
                    }else if(send_open_ids.length == 1){
                        //调用微信模板消息接口
                        JSONObject con=new JSONObject();
                        JSONObject template_content = new JSONObject();
                        String message_id = corp_code + send_vip_ids[0] + System.currentTimeMillis();
                        con.put("first", "您好，您的专属导购给您发了一条消息");
                        con.put("keyword1", System.currentTimeMillis()); //关键词
                        con.put("keyword2", Common.DATETIME_FORMAT.format(now)); //关键词
                        con.put("remark", "请点击查看！"); //备注
                        template_content.put("content",con);
                        template_content.put("app_user_name",app_user_name);
                        template_content.put("template_id",template_id);
                        template_content.put("openid",send_open_ids[0]);
                        template_content.put("message_id",message_id);
                        result = sendTemplate(template_content);
                    }
                    for (int i = 0; i < send_open_ids.length; i++) {
                        String message_id = corp_code + send_vip_ids[i] + System.currentTimeMillis();
                        JSONObject info = JSONObject.parseObject(result);
                        //群发消息发送成功之后存入mongoDB
                         if ("0".equals(info.getString("errcode"))) {
                             insertMongoDB(corp_code,user_id,"","",send_vip_ids[i],send_vip_names[i],send_cardnos[i],send_phones[i],sms_code,"",content,message_id,"Y");
                         } else {
                             insertMongoDB(corp_code,user_id,"","",send_vip_ids[i],send_vip_names[i],send_cardnos[i],send_phones[i],sms_code,"",content,message_id,"N");
                             status = info.get("errmsg").toString();
                             return status;
                         }
                    }
                }
            }
        }else {

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
     * 插入mongoDB
     * @param openid
     * @param corp_code
     * @param vip_id
     * @param app_id
     * @param vip_name
     * @param content
     * @throws Exception
     */
    public void insertMongoDB(String corp_code,String user_code, String template_id,String openid, String vip_id,String vip_name,String cardno,String vip_phone,
                              String sms_code, String app_id, String content,String message_id,String send_status) throws Exception {
        Date now = new Date();
        String message_date = Common.DATETIME_FORMAT.format(now);
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_vip_message_content);
        DBObject saveData = new BasicDBObject();
        saveData.put("_id", message_id);
        saveData.put("message_target", "1");
        saveData.put("corp_code", corp_code);
        saveData.put("open_id", openid);
        saveData.put("vip_id", vip_id);
        saveData.put("cardno", cardno);
        saveData.put("vip_phone", vip_phone);
        saveData.put("sms_code", sms_code);
        saveData.put("message_type", "text");
        saveData.put("message_content", content);
        saveData.put("user_code", user_code);
        saveData.put("message_date", message_date);
        saveData.put("auth_appid", app_id);
        saveData.put("template", template_id);
        saveData.put("is_read", "N");
        saveData.put("is_send", send_status);
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


}
