package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by zhou on 2016/6/6.
 */
/*
*用户及权限
*/
@Controller
@RequestMapping("/vipActivity")
public class wwGroupController {

    @Autowired
    WxTemplateService wxTemplateService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    VipGroupService vipGroupService;

    private static Logger logger = LoggerFactory.getLogger((wwGroupController.class));
    String id;

//    @RequestMapping(value = "/api/bufaWxsms", method = RequestMethod.GET)
//    @ResponseBody
//    public String bufaWxsms(HttpServletRequest request, HttpServletResponse response) {
//        DataBean dataBean = new DataBean();
//        String errormessage = "数据异常，导出失败";
//        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_vip_batchsend_message);
//        int count = 0;
//        try {
//            String sms_code = request.getParameter("sms_code");
//            String page_num = request.getParameter("page_num");
//            String page_size = request.getParameter("page_size");
//
//            String content = "{\"keyword1\":\"阿吉豆“6.18”年中购物节\",\"keyword2\":\"2017.6.16-2017.6.18\",\"remark\":\"普卡会员享受正价货品1件9折2件8折，金卡、银卡会员享受正价货品8折；\\n以上活动为微信会员专享，不与其他优惠同享，买单时请出示您的微信会员卡号\\n活动时间：2017.6.16-2017.6.18\",\"first\":\"亲爱的\\\"#name#\\\" ，来自阿吉豆的邀约\",\"url\":\"\"}";
//            String template_id = "1flLjWlBJ4UgiSuS-HZvWOacXn2GbGTav1BmEORVnh0";
//            String app_id = "wxee3f0a269d0a8d3d";
//            String corp_code = "C10222";
//            String user_code1 = "AT14";
//            JSONObject condition_obj = JSONObject.parseObject("{\"list\":[{\"name\":\"关注官微\",\"type\":\"text\",\"value\":\"Y\",\"key\":\"18\"},{\"name\":\"所属品牌\",\"type\":\"text\",\"value\":\"A\",\"key\":\"brand_code\",\"value_name\":\"阿吉豆\"}],\"operator\":\"AND\"}");
//            condition_obj = vipGroupService.vipGroupCustom(condition_obj,corp_code,"R5000","","","","");
//            DataBox dataBox = iceInterfaceService.VipCustomSearchForWeb(page_num, page_size,corp_code,condition_obj.toString(),"");
//            if (dataBox.status.toString().equals("SUCCESS")) {
//                String message1 = dataBox.data.get("message").value;
//                JSONObject msg_obj = JSONObject.parseObject(message1);
//                JSONArray array = msg_obj.getJSONArray("all_vip_list");
//                logger.info("=======会员群发会员数"+array.size());
//                for (int i = 0; i < array.size(); i++) {
//                    JSONObject obj = array.getJSONObject(i);
//                    String vip_id = obj.getString("vip_id");
//                    String vip_name = obj.getString("vip_name");
//                    String open_id = obj.getString("open_id");
//                    String phone = obj.getString("vip_phone");
//                    String cardno = obj.getString("cardno");
//
//                    BasicDBObject bd = new BasicDBObject();
//                    bd.put("vip_id",vip_id);
//                    bd.put("sms_code",sms_code);
//
//                    if (collection.find(bd).hasNext()){
//                        logger.info("========已经存在该会员群发");
//                        continue;
//                    }
//                    Date now = new Date();
//                    String message_date = Common.DATETIME_FORMAT.format(now);
//
//                    String message_id = corp_code + vip_id + System.currentTimeMillis();
//                    if (open_id.equals("") || open_id.equals("null")) {
//                        //会员未绑定微信，记录【未发送】
//                        DBObject saveData = new BasicDBObject();
//                        saveData.put("_id", message_id);
//                        saveData.put("message_target", "1");
//                        saveData.put("corp_code", corp_code);
//                        saveData.put("open_id", "");
//                        saveData.put("vip_id", vip_id);
//                        saveData.put("cardno", cardno);
//                        saveData.put("vip_phone", phone);
//                        saveData.put("sms_code", sms_code);
//                        saveData.put("message_type", "text");
//                        saveData.put("message_content", content);
//                        saveData.put("user_code", user_code1);
//                        saveData.put("message_date", message_date);
//                        saveData.put("auth_appid", app_id);
//                        saveData.put("template", template_id);
//                        saveData.put("is_read", "N");
//                        saveData.put("is_send", "N");
//                        saveData.put("vip_name", vip_name);
//                        collection.insert(saveData);
//                        count++;
//                    } else {
//                        //调用发送模板消息接口
//                        JSONObject content_obj = JSONObject.parseObject(content);
//                        String first = content_obj.getString("first");
//                        String keyword1 = content_obj.getString("keyword1");
//                        String keyword2 = content_obj.getString("keyword2");
//                        String remark = content_obj.getString("remark");
//                        String url = content_obj.getString("url");
//
//                        first = first.replace("\"#name#\"", vip_name);
//
//                        JSONObject template_content = new JSONObject();
//                        template_content.put("first", first);
//                        template_content.put("keyword1", keyword1); //关键词
//                        template_content.put("keyword2", keyword2); //关键词
//                        template_content.put("remark", remark); //备注
//
//                        JSONObject con = new JSONObject();
//                        JSONObject template_content1 = new JSONObject();
//                        template_content1.put("content", con);
//                        template_content1.put("app_id", app_id);
//                        template_content1.put("open_id", open_id);
//                        template_content1.put("temp_id", template_id);
//                        template_content1.put("msg_content", template_content);
//                        template_content1.put("template_url", url);
//
//                        String result = IshowHttpClient.post("http://wechat.app.bizvane.com/app/wechat/message/sendTemplate",template_content1);
////                        String result = wxTemplateService.sendTemplateMsg(app_id,open_id,template_id,template_content,url);
//                        JSONObject info = JSONObject.parseObject(result);
//                        String errcode = info.getString("errcode");
//                        if ("0".equals(errcode)) {
//                            DBObject saveData = new BasicDBObject();
//                            saveData.put("_id", message_id);
//                            saveData.put("message_target", "1");
//                            saveData.put("corp_code", corp_code);
//                            saveData.put("open_id", open_id);
//                            saveData.put("vip_id", vip_id);
//                            saveData.put("cardno", cardno);
//                            saveData.put("vip_phone", phone);
//                            saveData.put("sms_code", sms_code);
//                            saveData.put("message_type", "text");
//                            saveData.put("message_content", content);
//                            saveData.put("user_code", user_code1);
//                            saveData.put("message_date", message_date);
//                            saveData.put("auth_appid", app_id);
//                            saveData.put("template", template_id);
//                            saveData.put("is_read", "N");
//                            saveData.put("is_send", "Y");
//                            saveData.put("vip_name", vip_name);
//                            collection.insert(saveData);
////                            insertMongoDB(corp_code, user_code1, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, "", content, message_id, "Y");
//                            count++;
//
//                        } else {
//                            DBObject saveData = new BasicDBObject();
//                            saveData.put("_id", message_id);
//                            saveData.put("message_target", "1");
//                            saveData.put("corp_code", corp_code);
//                            saveData.put("open_id", open_id);
//                            saveData.put("vip_id", vip_id);
//                            saveData.put("cardno", cardno);
//                            saveData.put("vip_phone", phone);
//                            saveData.put("sms_code", sms_code);
//                            saveData.put("message_type", "text");
//                            saveData.put("message_content", content);
//                            saveData.put("user_code", user_code1);
//                            saveData.put("message_date", message_date);
//                            saveData.put("auth_appid", app_id);
//                            saveData.put("template", template_id);
//                            saveData.put("is_read", "N");
//                            saveData.put("is_send", "N");
//                            saveData.put("vip_name", vip_name);
//                            collection.insert(saveData);
////                            insertMongoDB(corp_code, user_code1, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, "", content, message_id, "N");
//                            count++;
//                        }
//                    }
//                }
//                Data data_phone = new Data("phone", "15251891037", ValueType.PARAM);
//                Data data_text = new Data("message_content", "已发送"+array.size(), ValueType.PARAM);
//
//                Map datalist = new HashMap<String, Data>();
//                datalist.put(data_phone.key, data_phone);
//                datalist.put(data_text.key, data_text);
//
//            iceInterfaceService.iceInterfaceV2("SendSMS", datalist);
//            }
//            dataBean.setId("1");
//            dataBean.setMessage(String.valueOf(count));
//            dataBean.setCode("0");
//        }catch (Exception ex) {
//            System.out.println("===============总异常========================================");
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(errormessage);
//            ex.printStackTrace();
//        }
//        return dataBean.getJsonStr();
//    }

}
