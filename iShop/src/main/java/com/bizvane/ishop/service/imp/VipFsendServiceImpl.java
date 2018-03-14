package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.VipFsendMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.sun.common.service.http.HttpClient;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.common.service.redis.RedisClient;
import com.bizvane.sun.v1.common.DataBox;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private VipGroupService vipGroupService;
    @Autowired
    private CorpService corpService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    ScheduleJobService scheduleJobService;
    @Autowired
    VipActivityService vipActivityService;
    @Autowired
    FunctionService functionService;
    @Autowired
    UserService userService;
    @Autowired
    VipTaskService vipTaskService;
    @Autowired
    RedisClient redisClient;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;

    @Autowired
    MongoDBClient mongoDBClient;



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
        String sms_code = vipFsend.getSms_code();
        String corp_code = vipFsend.getCorp_code();

        Map query_key = new HashMap();
        query_key.put("sms_code", sms_code);
        query_key.put("corp_code", corp_code);

        JSONObject info = new JSONObject();

        List<Map<String, Object>> message_list = mongodbClient.query(CommonValue.table_vip_batchsend_message, query_key);
        info.put("vip_info", message_list);
        message = JSON.toJSONString(info);

        return message;
    }

    @Override
    public VipFsend getVipFsendInfoById(int id) throws Exception {
        VipFsend vipFsend = vipFsendMapper.selectById(id);

        vipFsend.setSend_time(transTime(vipFsend.getSend_time()));

        return vipFsend;
    }

    @Override
    public VipFsend getVipFsendInfoByCode(String corp_code, String sms_code) throws Exception {
        VipFsend vipFsend;
        if (corp_code.equals("")){
            vipFsend = vipFsendMapper.selectByCode1(sms_code);
        }else {
            vipFsend = vipFsendMapper.selectByCode(corp_code, sms_code);
        }
        return vipFsend;
    }

    @Override
    public int updateVipFsend(VipFsend vipFsend) throws Exception {
        return vipFsendMapper.updateFsend(vipFsend);
    }

    public int updateVipFsendByCode(VipFsend vipFsend) throws Exception {
        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection cursor=mongoTemplate.getCollection(CommonValue.table_batch_import_vip);
        if(vipFsend.getSend_scope().equals("input_file")){
            BasicDBObject basicDBObject=new BasicDBObject();
            basicDBObject.put("_id",vipFsend.getSms_vips());
            DBObject dbObject=cursor.findOne(basicDBObject);
            String info=dbObject.get("cardInfo").toString();
            JSONArray jsonArray=JSON.parseArray(info);
            vipFsend.setCardno_num(String.valueOf(jsonArray.size()));
        }

        return vipFsendMapper.updateVipFsendByCode(vipFsend);
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
            vipFsend.setSend_time(transTime(vipFsend.getSend_time()));
            String code = vipFsend.getActivity_vip_code();
            String content = vipFsend.getContent();
            if (code.contains("VIPTASK")){
                content = code.replace("VIPTASK","")+"任务处理通知";
            }else if (code.contains("AC")){
                content = code+"活动群发";
            }
            content=content.replace("first","导航语");
            content=content.replace("url","详情链接");
            content=content.replace("remark","备注");
            content=content.replace("{","").replace("}","");

            vipFsend.setContent(content);
        }

        PageInfo<VipFsend> page = new PageInfo<VipFsend>(vipFsends);

        return page;
    }

    public String transTime (String send_time){
        if (send_time.contains("?")){
            String time = "";
            String[] times = send_time.split(" ");
            String sec = times[0];
            String min = times[1];
            String hour = times[2];
            String d = times[3];
            String M = times[4];
            if (times.length > 6){
                String y = times[6];
                time = y+"-"+M+"-"+d+" "+hour+":"+min+":"+sec;
            }else {
                if (!M.equals("*")) {
                    time = "每年"+M+"月"+d+"日 "+hour+":"+min+":"+sec;
                }else {
                    time = "每月"+d+"日 "+hour+":"+min+":"+sec;
                }
            }
            return time;
        }
        return  send_time;
    }

    @Override
    public String insert(VipFsend vipFsend, String user_code, String group_code, String role_code) throws Exception {
        MongoTemplate mongoTemplate=this.mongoDBClient.getMongoTemplate();
        DBCollection cursor=mongoTemplate.getCollection(CommonValue.table_batch_import_vip);

        Date now = new Date();
        //群发消息的发送类型
        String corp_code = vipFsend.getCorp_code();
        String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(now);

        vipFsend.setSms_code(sms_code);
        vipFsend.setCheck_status("N");
        vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
        vipFsend.setModifier(user_code);
        vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
        vipFsend.setCreater(user_code);
        vipFsend.setIsactive(Common.IS_ACTIVE_Y);

        if (vipFsend.getApp_id() == null)
            vipFsend.setApp_id("");

        if(vipFsend.getSend_scope().equals("input_file")){
            BasicDBObject basicDBObject=new BasicDBObject();
            basicDBObject.put("_id",vipFsend.getSms_vips());
            DBObject dbObject=cursor.findOne(basicDBObject);
            String info=dbObject.get("cardInfo").toString();
            JSONArray jsonArray=JSON.parseArray(info);
            vipFsend.setCardno_num(String.valueOf(jsonArray.size()));
        }

        insertSend(vipFsend,user_code,group_code,role_code);

        int id = getVipFsendInfoByCode(corp_code,sms_code).getId();
        return String.valueOf(id);
    }


    public String checkVipFsend(VipFsend vipFsend, String user_code) throws Exception{
        String corp_code = vipFsend.getCorp_code();
        String sms_code = vipFsend.getSms_code();

        String send_time = vipFsend.getSend_time();
        if (vipFsend.getSend_time() != null && !vipFsend.getSend_time().isEmpty() && !send_time.contains("?")){
            if(send_time.compareTo(Common.DATETIME_FORMAT.format(new Date())) < 0){
                return vipFsend.getSms_code()+"已经超过发送时间，审核失败";
            }
        }
        if (vipFsend.getSend_time() == null || vipFsend.getSend_time().isEmpty()){
            send_time = Common.DATETIME_FORMAT.format(TimeUtils.getLastMin(new Date(),2));
            String task_code = vipFsend.getActivity_vip_code().replace("VIPTASK","");
            VipTask vipTask = vipTaskService.selectByTaskCode(task_code);

            if (vipTask.getTask_status().equals("2")){
                return vipFsend.getSms_code()+"已经超过发送时间，审核失败";
            }
            vipFsend.setSend_time(send_time);
        }

        String corn_expression = send_time;
        if (!send_time.contains("?")){
            Date time = Common.DATETIME_FORMAT.parse(send_time);
            corn_expression = TimeUtils.getCron(time);
        }
        JSONObject func = new JSONObject();
        func.put("method", "sendSMS");
        func.put("corp_code", corp_code);
        func.put("user_code", user_code);
        func.put("code", sms_code);
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJob_name(sms_code);
        scheduleJob.setJob_group(sms_code);
        scheduleJob.setFunc(func.toString());
        scheduleJob.setStatus("N");
        scheduleJob.setCron_expression(corn_expression);
        scheduleJobService.insert(scheduleJob);

        vipFsend.setCheck_status("Y");
        vipFsend.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
        vipFsend.setModifier(user_code);
        updateVipFsendByCode(vipFsend);
        return Common.DATABEAN_CODE_SUCCESS;
    }

    @Transactional
    public String sendMessage(final VipFsend vipFsend, String user_code) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;

        final VipFsend vipFsend1 = vipFsend;
        final String user_code1 = user_code;
        String send_type = vipFsend1.getSend_type();
        String corp_code = vipFsend1.getCorp_code();

        if (!vipFsend.getCheck_status().equals("Y")){
            return "";
        }
        if (send_type.equals("sms")) {
            sendSMS(corp_code, vipFsend1, user_code1);
        } else {
            //微信群发 文件导入
            MongoTemplate mongoTemplate=this.mongodbClient.getMongoTemplate();
            final DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_batch_import_vip);

            ExecutorService executorService = Executors.newFixedThreadPool(5);
            executorService.execute(new Runnable() {
                public void run() {
                    try {
                        logger.info("-------sendMessage----多线程---");
                        String corp_code = vipFsend1.getCorp_code();
                        String sms_code = vipFsend1.getSms_code();
                        String content = vipFsend1.getContent();
                        String send_type = vipFsend1.getSend_type();
                        String send_scope = vipFsend1.getSend_scope();
//                        String sms_vips = vipFsend1.getSms_vips();
                        String sms_vips_ = vipFsend1.getSms_vips_();

                        logger.info("-------sendMessage----多线程---send_type:"+send_type);

                        if (vipFsend1.getApp_id() != null){
                            String app_id = vipFsend1.getApp_id();
                            String template_name = Common.TEMPLATE_NAME_1;
                            if (vipFsend1.getTemplate_name() != null && !vipFsend1.getTemplate_name().equals("")){
                                template_name = vipFsend1.getTemplate_name();
                            }

                            List<WxTemplate> wxTemplates = wxTemplateService.selectTempByAppId(app_id,"",template_name);
                            String template_id = wxTemplates.get(0).getTemplate_id();
                            String template_cont= wxTemplates.get(0).getTemplate_content();

                                //调用发送模板消息接口
                            if (send_scope.equals("vip_condition")) {
                                if (sms_vips_ != null && !sms_vips_.equals("")){
                                    JSONArray screen = new JSONArray();

                                    screen = JSONArray.parseArray(sms_vips_);
                                    JSONObject open_id_obj = new JSONObject();
                                    open_id_obj.put("key", Common.VIP_SCREEN_OPENID_KEY);
                                    open_id_obj.put("type","text");
                                    open_id_obj.put("value","Y");
                                    screen.add(open_id_obj);

                                    DataBox dataBox = iceInterfaceService.vipScreenMethod2("1", "2", corp_code,JSON.toJSONString(screen),"","");
//                                DataBox dataBox = vipGroupService.vipScreenBySolr(screen, corp_code, "1", "3", Common.ROLE_GM, "", "", "", "","","");
                                    if (dataBox.status.toString().equals("SUCCESS")) {
                                        String message1 = dataBox.data.get("message").value;
                                        JSONObject msg_obj = JSONObject.parseObject(message1);
                                        int count = msg_obj.getInteger("count");
                                        int doc_count = 1;
                                        if (count > 10000) {
                                            doc_count = count / 10000 + 1;
                                        }
                                        for (int j = 1; j < doc_count + 1; j++) {
                                            DataBox dataBox1 = iceInterfaceService.vipScreenMethod2(j + "", "10000", corp_code, JSON.toJSONString(screen), "", "");
//                                        DataBox dataBox1 = vipGroupService.vipScreenBySolr(screen, corp_code, j+"", "10000", Common.ROLE_GM, "", "", "", "","","");
                                            if (dataBox.status.toString().equals("SUCCESS")) {
                                                String message2 = dataBox1.data.get("message").value;
                                                JSONObject msg_obj1 = JSONObject.parseObject(message2);
                                                JSONArray jsonArray = msg_obj1.getJSONArray("all_vip_list");
                                                for (int k = 0; k < jsonArray.size(); k++) {

                                                    logger.info(" redis 群发的状态"+redisClient.get("VipFsend Status"+corp_code+sms_code));
                                                    if (redisClient.get("VipFsend Status"+corp_code+sms_code) != null && redisClient.get("VipFsend Status"+corp_code+sms_code).toString().equals("N")){
                                                        break;
                                                    }

                                                    try{
                                                        JSONObject vip_info = jsonArray.getJSONObject(k);
                                                        String vip_id = vip_info.getString("vip_id");
                                                        String vip_name = vip_info.getString("vip_name");
                                                        String open_id = vip_info.getString("open_id");
                                                        String phone = vip_info.getString("vip_phone");
                                                        String cardno = vip_info.getString("cardno");

                                                        JSONObject content_obj = JSONObject.parseObject(content);
                                                        String url = content_obj.getString("url");
                                                        JSONObject template_content = new JSONObject();
                                                        for (String key : content_obj.keySet()) {
                                                            String value = content_obj.getString(key);
                                                            template_content.put(key, textReplace(value, vip_info));
                                                        }
                                                        String message_id = corp_code + vip_id + System.currentTimeMillis();

                                                        String result = wxTemplateService.sendTemplateMsg(app_id, open_id, template_id, template_content, url);
                                                        JSONObject info = JSONObject.parseObject(result);
                                                        String errcode = info.getString("errcode");
                                                        String errmsg = info.getString("errmsg");
                                                        if ("0".equals(errcode)) {
                                                            insertMongoDB(corp_code, user_code1, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, app_id, template_content.toString(), message_id, "Y", errmsg);
                                                        } else {
                                                            insertMongoDB(corp_code, user_code1, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, app_id, template_content.toString(), message_id, "N", errmsg);
                                                        }
                                                    }catch (Exception e){
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (send_scope.equals("vip_condition_new")) {
                                if (sms_vips_ != null && !sms_vips_.equals("")) {
                                    JSONArray screen = new JSONArray();

                                    screen = JSONArray.parseArray(sms_vips_);
                                    JSONObject open_id_obj = new JSONObject();
                                    open_id_obj.put("key", "SUB_WECHAT");
                                    open_id_obj.put("type", "radio");
                                    open_id_obj.put("groupName", "微会员");

                                    JSONArray array = new JSONArray();
                                    JSONObject object = new JSONObject();
                                    object.put("opera", "");
                                    object.put("logic", "");
                                    object.put("value", "Y");
                                    object.put("dataName", "是");
                                    array.add(object);
                                    open_id_obj.put("value", array);
                                    screen.add(open_id_obj);

//                                DataBox dataBox = vipGroupService.vipScreenBySolrNew(screen1, corp_code, "1", "3", Common.ROLE_GM, "", "", "", "","","");
                                    DataBox dataBox = iceInterfaceService.newStyleVipSearchForWeb("1", "2", corp_code, JSON.toJSONString(screen), "", "");

                                    if (dataBox.status.toString().equals("SUCCESS")) {
                                        String message1 = dataBox.data.get("message").value;
                                        JSONObject msg_obj = JSONObject.parseObject(message1);
                                        int count = msg_obj.getInteger("count");
                                        int doc_count = 1;
                                        if (count > 10000) {
                                            doc_count = count / 10000 + 1;
                                        }
                                        logger.info("===========群发消息===总条数"+count+"======"+doc_count+"页");

                                        for (int j = 1; j < doc_count + 1; j++) {
                                            logger.info("===========群发消息===第"+j);
                                            DataBox dataBox1 = iceInterfaceService.newStyleVipSearchForWeb(j + "", "10000", corp_code, JSON.toJSONString(screen), "", "");
//                                        DataBox dataBox1 = vipGroupService.vipScreenBySolrNew(screen2, corp_code, j+"", "10000", Common.ROLE_GM, "", "", "", "","","");
                                            if (dataBox.status.toString().equals("SUCCESS")) {
                                                String message2 = dataBox1.data.get("message").value;
                                                JSONObject msg_obj1 = JSONObject.parseObject(message2);
                                                JSONArray jsonArray = msg_obj1.getJSONArray("all_vip_list");
                                                logger.info("===========群发消息===xx"+jsonArray.size());

                                                for (int k = 0; k < jsonArray.size(); k++) {

                                                    logger.info(" redis 群发的状态"+redisClient.get("VipFsend Status"+corp_code+sms_code));
                                                    if (redisClient.get("VipFsend Status"+corp_code+sms_code) != null && redisClient.get("VipFsend Status"+corp_code+sms_code).toString().equals("N")){
                                                        break;
                                                    }

                                                    try {
                                                        JSONObject vip_info = jsonArray.getJSONObject(k);
                                                        String vip_id = vip_info.getString("vip_id");
                                                        String vip_name = vip_info.getString("vip_name");
                                                        String open_id = vip_info.getString("open_id");
                                                        String phone = vip_info.getString("vip_phone");
                                                        String cardno = vip_info.getString("cardno");

                                                        JSONObject content_obj = JSONObject.parseObject(content);
                                                        String url = content_obj.getString("url");
                                                        JSONObject template_content = new JSONObject();
                                                        for (String key : content_obj.keySet()) {
                                                            String value = content_obj.getString(key);
                                                            template_content.put(key, textReplace(value, vip_info));
                                                        }
                                                        String message_id = corp_code + vip_id + System.currentTimeMillis();
                                                        String result = wxTemplateService.sendTemplateMsg(app_id, open_id, template_id, template_content, url);
                                                        JSONObject info = JSONObject.parseObject(result);
                                                        String errcode = info.getString("errcode");
                                                        String errmsg = info.getString("errmsg");
                                                        if ("0".equals(errcode)) {
                                                            insertMongoDB(corp_code, user_code1, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, app_id, template_content.toString(), message_id, "Y", errmsg);
                                                        } else {
                                                            insertMongoDB(corp_code, user_code1, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, app_id, template_content.toString(), message_id, "N", errmsg);
                                                        }
                                                    }catch (Exception e){
                                                        iceInterfaceService.sendSmsV2("C20000","群发消息异常"+e.getMessage(),"15251891037","10000","群发消息异常");
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }else if(send_scope.equals("input_file")){
                                //微信群发 导入文件
                                BasicDBObject basicDBObject=new BasicDBObject();
                                basicDBObject.put("_id",vipFsend.getSms_vips());
                                DBObject dbObject=cursor.findOne(basicDBObject);
                                String cardInfo=dbObject.get("cardInfo").toString();
                                DataBox dataBox=iceInterfaceAPIService.getVipByCardInfo(cardInfo,corp_code);
                                String message1 = dataBox.data.get("message").value;
                                String all_vip=JSON.parseObject(message1).getString("all_vip_list");
                                JSONArray jsonArray=JSON.parseArray(all_vip);
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    if (redisClient.get("VipFsend Status"+corp_code+sms_code) != null && redisClient.get("VipFsend Status"+corp_code+sms_code).toString().equals("N")){
                                        break;
                                    }
                                    try{
                                        JSONObject vip_info = jsonArray.getJSONObject(i);
                                        String vip_id = vip_info.getString("vip_id");
                                        String vip_name = vip_info.getString("vip_name");
                                        String open_id = vip_info.getString("open_id");
                                        String phone = vip_info.getString("vip_phone");
                                        String cardno = vip_info.getString("cardno");

                                        JSONObject content_obj = JSONObject.parseObject(content);
                                        String url = content_obj.getString("url");
                                        JSONObject template_content = new JSONObject();
                                        for (String key:content_obj.keySet()){
                                            String value = content_obj.getString(key);
                                            template_content.put(key,textReplace(value,vip_info));
                                        }
                                        String message_id = corp_code + vip_id + System.currentTimeMillis();
                                        String result = wxTemplateService.sendTemplateMsg(app_id,open_id,template_id,template_content,url);
                                        JSONObject info = JSONObject.parseObject(result);
                                        String errcode = info.getString("errcode");
                                        String errmsg = info.getString("errmsg");
                                        if ("0".equals(errcode)) {
                                            insertMongoDB(corp_code, user_code1, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, app_id, template_content.toString(), message_id, "Y",errmsg);
                                        } else {
                                            insertMongoDB(corp_code, user_code1, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, app_id, template_content.toString(), message_id, "N",errmsg);
                                        }
                                    }catch (Exception e){
                                        iceInterfaceService.sendSmsV2("C20000","群发消息异常"+e.getMessage(),"15251891037","10000","群发消息异常");
                                    }
                                }
                            } else {
                                JSONArray new_array = new JSONArray();
                                JSONArray array = getSendVips(vipFsend1, user_code1);
                                new_array.addAll(array);

                                //群发消息，群发模版消息
                                for (int i = 0; i < new_array.size(); i++) {

                                    logger.info(" redis 群发的状态"+redisClient.get("VipFsend Status"+corp_code+sms_code));
                                    if (redisClient.get("VipFsend Status"+corp_code+sms_code) != null && redisClient.get("VipFsend Status"+corp_code+sms_code).toString().equals("N")){
                                        break;
                                    }

                                    try{
                                        JSONObject vip_info = new_array.getJSONObject(i);
                                        String vip_id = vip_info.getString("vip_id");
                                        String vip_name = vip_info.getString("vip_name");
                                        String open_id = vip_info.getString("open_id");
                                        String phone = vip_info.getString("vip_phone");
                                        String cardno = vip_info.getString("cardno");

                                        JSONObject content_obj = JSONObject.parseObject(content);
                                        String url = content_obj.getString("url");
                                        JSONObject template_content = new JSONObject();
                                        for (String key:content_obj.keySet()){
                                            String value = content_obj.getString(key);
                                            template_content.put(key,textReplace(value,vip_info));
                                        }
                                        String message_id = corp_code + vip_id + System.currentTimeMillis();
                                        String result = wxTemplateService.sendTemplateMsg(app_id,open_id,template_id,template_content,url);
                                        JSONObject info = JSONObject.parseObject(result);
                                        String errcode = info.getString("errcode");
                                        String errmsg = info.getString("errmsg");
                                        if ("0".equals(errcode)) {
                                            insertMongoDB(corp_code, user_code1, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, app_id, template_content.toString(), message_id, "Y",errmsg);
                                        } else {
                                            insertMongoDB(corp_code, user_code1, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, app_id, template_content.toString(), message_id, "N",errmsg);
                                        }
                                    }catch (Exception e){
                                        iceInterfaceService.sendSmsV2("C20000","群发消息异常"+e.getMessage(),"15251891037","10000","群发消息异常");

                                    }

                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    logger.info("------------sendMessage:"+new Date()+"Asynchronous task");
                }
            });
            executorService.shutdown();

        }
        return status;
    }


    public JSONArray getSendVips(VipFsend vipFsend, String user_code) throws Exception {
        String corp_code = vipFsend.getCorp_code();
        String sms_vips = vipFsend.getSms_vips();
        String send_scope = vipFsend.getSend_scope();

        JSONArray vip_infos = new JSONArray();
        if (send_scope.equals("vip")) {
            JSONObject sms_vips_obj = JSONObject.parseObject(sms_vips);
            String vip_id = sms_vips_obj.get("vips").toString();
            //获取会员信息（vip_id多个逗号分割）
            DataBox dataBox = iceInterfaceService.getVipInfo(corp_code,vip_id);
            logger.info("------vipFsend群发消息-vip列表" + dataBox.status.toString());
            if (dataBox.status.toString().equals("SUCCESS")) {
                String message1 = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(message1);
                JSONArray vip_info = msg_obj.getJSONArray("vip_info");
                vip_infos.addAll(vip_info);
            }
        } else if (send_scope.equals("vip_group") && !vipFsend.getVip_group_code().equals("")) {
            String role_code = Common.ROLE_GM;
            String brand_code = "";
            String area_code = "";
            String store_code = "";
            String user_code1 = "";
            if (!sms_vips.isEmpty()){
                JSONObject screen = JSONObject.parseObject(sms_vips);
                role_code = screen.getString("role_code");
                brand_code = screen.getString("brand_code");
                area_code = screen.getString("area_code");
                store_code = screen.getString("store_code");
                user_code1 = screen.getString("user_code");
            }
            String vip_group_code = vipFsend.getVip_group_code();
            String[] group_codes = vip_group_code.split(",");
            for (int i = 0; i < group_codes.length; i++) {
                DataBox dataBox;
                VipGroup vipGroup = vipGroupService.getVipGroupByCode(corp_code,group_codes[i], Common.IS_ACTIVE_Y);
                String group_type = vipGroup.getGroup_type();
                if (group_type.equals("define")){
                    String condition = vipGroup.getGroup_condition();
                    JSONObject condition_obj = JSONObject.parseObject(condition);

                    JSONObject open_id = new JSONObject();
                    open_id.put("key", Common.VIP_SCREEN_OPENID_KEY);
                    open_id.put("type","text");
                    open_id.put("value","Y");

                    JSONArray array = new JSONArray();
                    array.add(condition_obj);
                    array.add(open_id);

                    JSONObject new_condition = new JSONObject();
                    new_condition.put("list",array);
                    new_condition.put("operator","AND");
                    new_condition = vipGroupService.vipGroupCustom(new_condition,corp_code,role_code, brand_code, area_code, store_code,user_code1);
                    dataBox = iceInterfaceService.VipCustomSearchForWeb("1", "100000",corp_code,new_condition.toString(),"","define","");
                }else  if (group_type.equals("define_v2")){
                    String condition = vipGroup.getGroup_condition();
                    JSONArray condition_array = JSONArray.parseArray(condition);
                    condition_array = vipGroupService.vipScreen2ArrayNew(condition_array,corp_code,role_code, brand_code, area_code, store_code,user_code1);

                    JSONObject open_id = new JSONObject();
                    open_id.put("key", Common.VIP_SCREEN_OPENID_KEY);
                    open_id.put("type","text");
                    open_id.put("value","Y");
                    condition_array.add(open_id);

                    dataBox = iceInterfaceService.VipCustomSearchForWeb("1", "100000",corp_code,condition_array.toJSONString(),"","define_v2","");
                }else {
                    JSONArray screen = new JSONArray();
                    JSONObject post_obj = new JSONObject();
                    post_obj.put("key", Common.VIP_SCREEN_GROUP_KEY);
                    post_obj.put("type", "text");
                    post_obj.put("value", group_codes[i]);
                    screen.add(post_obj);
                    logger.info("json--------------screen-" + JSON.toJSONString(screen));
                    dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,"1", "100000",role_code, brand_code, area_code, store_code,user_code1,"","");
                }
                logger.info("--------------vipGroup:"+dataBox.status.toString());

                if (dataBox.status.toString().equals("SUCCESS")) {
                    String message1 = dataBox.data.get("message").value;
                    JSONObject msg_obj = JSONObject.parseObject(message1);
                    JSONArray vip_info = msg_obj.getJSONArray("all_vip_list");
                    vip_infos.addAll(vip_info);
                }
            }
        }
        logger.info("--------------vip_infos:"+vip_infos.size());
        for (int i = 0; i < vip_infos.size(); i++) {
            JSONObject vip_obj = vip_infos.getJSONObject(i);
            String open_id = "";
            if (!vip_obj.containsKey("open_id") || vip_obj.getString("open_id").equals("")) {
                open_id = "null";
            } else {
                open_id = vip_obj.getString("open_id");
            }
            vip_obj.put("open_id", open_id);
        }
        return vip_infos;
    }

    public int getSendVipCount(String corp_code,String send_scope,String sms_vips,  String role_code, String user_brand_code, String user_area_code, String user_store_code, String user_code) throws Exception {

        int count = 0;
        if (send_scope.equals("vip")) {
            JSONObject sms_vips_obj = JSONObject.parseObject(sms_vips);
            String vip_id = sms_vips_obj.get("vips").toString();
            //获取会员信息（vip_id多个逗号分割）
            if (!vip_id.equals(""))
                count = vip_id.split(",").length;
        } else if (send_scope.equals("vip_condition")) {
            JSONArray screen = JSONArray.parseArray(sms_vips);
            DataBox dataBox = vipGroupService.vipScreenBySolr(screen, corp_code, "1", "2", role_code, user_brand_code, user_area_code, user_store_code, user_code,"","");

            if (dataBox.status.toString().equals("SUCCESS")) {
                String message1 = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(message1);
                count = Integer.parseInt(msg_obj.get("count").toString());
            }
        } else if (send_scope.equals("vip_condition_new")) {
            JSONArray screen = JSONArray.parseArray(sms_vips);
            DataBox dataBox = vipGroupService.vipScreenBySolrNew(screen, corp_code, "1", "2", role_code, user_brand_code, user_area_code, user_store_code, user_code,"","");
            if (dataBox.status.toString().equals("SUCCESS")) {
                String message1 = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(message1);
                count = Integer.parseInt(msg_obj.get("count").toString());
            }
        } else if (send_scope.equals("vip_group") && !sms_vips.equals("")) {
            DataBox dataBox;
            VipGroup vipGroup = vipGroupService.getVipGroupByCode(corp_code,sms_vips, Common.IS_ACTIVE_Y);
            String group_type = vipGroup.getGroup_type();
            if (group_type.equals("define")){
                String condition = vipGroup.getGroup_condition();
                JSONObject condition_obj = JSONObject.parseObject(condition);
                condition_obj = vipGroupService.vipGroupCustom(condition_obj,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
                dataBox = iceInterfaceService.VipCustomSearchForWeb("1", "3",corp_code,condition_obj.toString(),"","define","");
            }else if (group_type.equals("define_v2")){
                String condition = vipGroup.getGroup_condition();
                JSONArray condition_array = JSONArray.parseArray(condition);
                condition_array = vipGroupService.vipScreen2ArrayNew(condition_array,corp_code,role_code, user_brand_code,user_area_code,user_store_code,user_code);

                dataBox = iceInterfaceService.VipCustomSearchForWeb("1", "3",corp_code,condition_array.toJSONString(),"","define_v2","");
            }else {
                JSONArray screen = new JSONArray();
                JSONObject post_obj = new JSONObject();
                post_obj.put("key", Common.VIP_SCREEN_GROUP_KEY);
                post_obj.put("type", "text");
                post_obj.put("value", sms_vips);
                screen.add(post_obj);
                logger.info("json--------------screen-" + JSON.toJSONString(screen));
                dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,"1", "3",role_code,user_brand_code,user_area_code,user_store_code,user_code,"","");
            }
            logger.info("--------------vipGroup:"+dataBox.status.toString());

            if (dataBox.status.toString().equals("SUCCESS")) {
                String message1 = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(message1);
                count = Integer.parseInt(msg_obj.get("count").toString());
            }
        }else if(send_scope.equals("input_file")){
            MongoTemplate mongoTemplate=this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_batch_import_vip);
            BasicDBObject basicDBObject=new BasicDBObject();
            basicDBObject.put("_id",sms_vips);
            DBObject dbObject=cursor.findOne(basicDBObject);
            String cardInfo= dbObject.get("cardInfo").toString();
            JSONArray cardArray=JSON.parseArray(cardInfo);
            count=cardArray.size();
        }
        logger.info("--------------count:"+count);
        return count;
    }

    public void sendSMS(String corp_code, VipFsend vipFsend, String user_code) throws Exception{
        String sms_code = vipFsend.getSms_code();
        String send_scope = vipFsend.getSend_scope();
        String content = vipFsend.getContent();

        logger.info("===============sendSMS="+sms_code+"=====send_scope:"+send_scope+"=======user_code:"+user_code+"=======sms_vips:"+vipFsend.getSms_vips_());

        if (send_scope.equals("vip_condition")){
            String sms_vips = vipFsend.getSms_vips_();

            JSONArray sms_screen = JSONArray.parseArray(sms_vips);
            JSONArray array = vipGroupService.vipScreen2Array(sms_screen,corp_code,"","","","","");
            iceInterfaceService.batchSendSMS(corp_code,vipFsend.getActivity_vip_code(),content,JSON.toJSONString(array),"","sms",sms_code,user_code,"","");
        }else if (send_scope.equals("vip_condition_new")){
//            JSONArray sms_screen = JSONArray.parseArray(sms_vips);
//            JSONArray array = vipGroupService.vipScreen2ArrayNew(sms_screen,corp_code,"","","","","");
            String sms_vips = vipFsend.getSms_vips_();
            logger.info(sms_vips);
            iceInterfaceService.batchSendSMS(corp_code,vipFsend.getActivity_vip_code(),content,"","","sms",sms_code,user_code,"",sms_vips);
        } else if (send_scope.equals("vip_group") && !vipFsend.getVip_group_code().equals(""))  {
            String vip_group_code = vipFsend.getVip_group_code();
            String[] group_codes = vip_group_code.split(",");
            for (int i = 0; i < group_codes.length; i++) {
                DataBox dataBox;
                VipGroup vipGroup = vipGroupService.getVipGroupByCode(corp_code, group_codes[i], Common.IS_ACTIVE_Y);
                String group_type = vipGroup.getGroup_type();
                if (group_type.equals("define")) {
                    String condition = vipGroup.getGroup_condition();
                    JSONObject condition_obj = JSONObject.parseObject(condition);
                    condition_obj = vipGroupService.vipGroupCustom(condition_obj, corp_code, "", "", "", "", "");

                    iceInterfaceService.batchSendSMS(corp_code,"",content,"",condition_obj.toString(),"sms",sms_code,user_code,"","");
                }else  if (group_type.equals("define_v2")){
                    String condition = vipGroup.getGroup_condition();
                    JSONArray condition_array = JSONArray.parseArray(condition);
                    condition_array = vipGroupService.vipScreen2ArrayNew(condition_array,corp_code,"", "", "", "","");

                    iceInterfaceService.batchSendSMS(corp_code,"",content,"","","sms",sms_code,user_code,"",JSON.toJSONString(condition_array));
                } else {
                    JSONArray screen = new JSONArray();
                    JSONObject post_obj = new JSONObject();
                    post_obj.put("key", Common.VIP_SCREEN_GROUP_KEY);
                    post_obj.put("type", "text");
                    post_obj.put("value", group_codes[i]);
                    screen.add(post_obj);
                    logger.info("json--------------screen-" + JSON.toJSONString(screen));

                    screen = vipGroupService.vipScreen2Array(screen,corp_code,"","","","","");
                    iceInterfaceService.batchSendSMS(corp_code,"",content,JSON.toJSONString(screen),"","sms",sms_code,user_code,"","");
                }
            }
        } else if(send_scope.equals("input_file")){
            MongoTemplate mongoTemplate=this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_batch_import_vip);
            BasicDBObject basicDBObject=new BasicDBObject();
            basicDBObject.put("_id",vipFsend.getSms_vips());
            DBObject dbObject=cursor.findOne(basicDBObject);
            String cardInfo=dbObject.get("cardInfo").toString();
            DataBox dataBox=iceInterfaceAPIService.getVipByCardInfo(cardInfo,corp_code);
            String message1 = dataBox.data.get("message").value;
            String all_vip=JSON.parseObject(message1).getString("all_vip_list");
            JSONArray jsonArray=JSON.parseArray(all_vip);

            //发送类型：短信
            Pattern pattern4 = Pattern.compile("(^(\\d{3,4}-)?\\d{7,8})$|(1[3,4,5,7,8]{1}\\d{9})");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String vip_id = obj.getString("vip_id");
                String vip_name = obj.getString("vip_name");
                String phone = obj.getString("vip_phone");
                String cardno = obj.getString("cardno");

                Matcher matcher = pattern4.matcher(phone.trim());
                String message_id = corp_code + vip_id + System.currentTimeMillis();
                if (matcher.matches() == false) {
                    //手机号非法，记录【不发送】
                    insertMongoDB(corp_code, user_code, "", "", vip_id, vip_name, cardno, phone, sms_code, "", content, message_id, "N","手机号格式不对");
                } else {
                    //参数
                    String text = textReplace(content,obj);
                    DataBox dataBox1=iceInterfaceService.sendSmsV3(corp_code,text,phone,user_code,"群发消息",vip_id);
                    String send_status="";
                    if(dataBox1.status.toString().equals("SUCCESS")){
                        send_status="Y";
                    }else{
                        send_status="N";
                    }
                    String errmsg=dataBox1.msg;
                    //记录【发送】
                    insertMongoDB(corp_code, user_code, "", "", vip_id, vip_name, cardno, phone, sms_code, "", content, message_id, send_status,errmsg);
                }
            }

        } else {
            JSONArray vip_infos = new JSONArray();
            String sms_vips = vipFsend.getSms_vips();

            JSONObject sms_vips_obj = JSONObject.parseObject(sms_vips);
            String vip_ids = sms_vips_obj.get("vips").toString();
            //获取会员信息（vip_id多个逗号分割）
            DataBox dataBox = iceInterfaceService.getVipInfo(corp_code,vip_ids);
            logger.info("------vipFsend群发消息-vip列表" + dataBox.status.toString());
            if (dataBox.status.toString().equals("SUCCESS")) {
                String message1 = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(message1);
                JSONArray vip_info = msg_obj.getJSONArray("vip_info");
                vip_infos.addAll(vip_info);
            }

            //发送类型：短信
            Pattern pattern4 = Pattern.compile("(^(\\d{3,4}-)?\\d{7,8})$|(1[3,4,5,7,8]{1}\\d{9})");
            for (int i = 0; i < vip_infos.size(); i++) {
                JSONObject obj = vip_infos.getJSONObject(i);
                String vip_id = obj.getString("vip_id");
                String vip_name = obj.getString("vip_name");
                String phone = obj.getString("vip_phone");
                String cardno = obj.getString("cardno");

                Matcher matcher = pattern4.matcher(phone.trim());
                String message_id = corp_code + vip_id + System.currentTimeMillis();
                if (matcher.matches() == false) {
                    //手机号非法，记录【不发送】
                    insertMongoDB(corp_code, user_code, "", "", vip_id, vip_name, cardno, phone, sms_code, "", content, message_id, "N","手机号格式不对");
                } else {
                    //参数
                    String text = textReplace(content,obj);
                    DataBox dataBox1=iceInterfaceService.sendSmsV3(corp_code,text,phone,user_code,"群发消息",vip_id);
                    String send_status="";
                    if(dataBox1.status.toString().equals("SUCCESS")){
                        send_status="Y";
                    }else{
                        send_status="N";
                    }
                    String errmsg=dataBox1.msg;
                    //记录【发送】
                    insertMongoDB(corp_code, user_code, "", "", vip_id, vip_name, cardno, phone, sms_code, "", content, message_id, send_status,errmsg);
                }
            }
        }

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
        if (map.containsKey("created_date")){
            JSONObject date = JSONObject.parseObject(map.get("created_date"));
            params.put("created_date_start", date.get("start").toString());
            String end = date.get("end").toString();
            if (!end.equals(""))
                end = end + " 23:59:59";
            params.put("created_date_end", end);
            map.remove("created_date");
        }

        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<VipFsend> list1 = vipFsendMapper.selectAllFsendScreen(params);
        for (VipFsend vipFsend : list1) {
            vipFsend.setIsactive(CheckUtils.CheckIsactive(vipFsend.getIsactive()));
            String send_type = vipFsend.getSend_type();
            String result = change_sendType(send_type);
            vipFsend.setSend_type(result);
            vipFsend.setSend_time(transTime(vipFsend.getSend_time()));

            String code = vipFsend.getActivity_vip_code();
            String content = vipFsend.getContent();

            if (code.contains("VIPTASK")){

            }else if (code.contains("AC")){
                content = code+"活动群发";
            }
            content=content.replace("first","导航语");
            content=content.replace("url","详情链接");
            content=content.replace("remark","备注");
            content=content.replace("{","").replace("}","");

            vipFsend.setContent(content);
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
     *
     * @param openid
     * @param corp_code
     * @param vip_id
     * @param app_id
     * @param vip_name
     * @param content
     * @throws Exception
     */
    public void insertMongoDB(String corp_code, String user_code, String template_id, String openid, String vip_id, String vip_name, String cardno, String vip_phone,
                              String sms_code, String app_id, String content, String message_id, String send_status,String errmsg) throws Exception {
        Date now = new Date();
        String message_date = Common.DATETIME_FORMAT.format(now);
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_vip_batchsend_message);
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
        saveData.put("errmsg", errmsg);

        collection.insert(saveData);

    }



    /**
     * 微信群发消息
     *
     * @param
     * @return
     * @throws Exception
     */
    public String sendWxMass(String content,String app_user_name,String send_open_id,String message_type) throws Exception {
        JSONObject template_content = new JSONObject();
        template_content.put("content", content);//微信群发内容
        template_content.put("app_user_name", app_user_name); //app_user_name
        template_content.put("openid", send_open_id);//所选择会员的openid
        template_content.put("message_type", message_type);//发送 消息类型

        RequestBody body = RequestBody.create(Common.JSON, template_content.toJSONString());
        Request request = new Request.Builder().url(CommonValue.wechat_url+"/sendWxMass").post(body).build();
        Response response = httpClient.post(request);
        String result = response.body().string();
        return result;
    }

    /**
     * 微信客服消息
     *
     * @param
     * @return
     * @throws Exception
     */
    public String sendWxCustMsg(String content,String app_id,String send_open_id,String message_type,String corp_code,String user_code) throws Exception {

        JSONObject template_content = new JSONObject();
        template_content.put("message_content", content);//微信群发内容
        template_content.put("auth_appid", app_id); //app_user_name
        template_content.put("open_id", send_open_id);//所选择会员的openid
        template_content.put("message_type", message_type);//发送 消息类型
        template_content.put("send_type", 1);//发送 发送类型
        template_content.put("corp_code", corp_code);//发送 企业编号
        template_content.put("user_code", user_code);//发送 员工编号

        return IshowHttpClient.post(CommonValue.wechat_url+"/weixinCore/replymessage",template_content);
    }


    public int insertSend(VipFsend vipFsend) throws Exception {
        vipFsendMapper.insertFsend(vipFsend);
        return 1;
    }
    public int insertSend(VipFsend vipFsend, String user_code, String group_code, String role_code) throws Exception {
        vipFsendMapper.insertFsend(vipFsend);
        String corp_code = vipFsend.getCorp_code();

        List<Map<String,String>> actions_fun = functionService.selectActionByFun(corp_code, user_code, group_code, role_code, "F0041");
        int flag = 0;
        for (int i = 0; i < actions_fun.size(); i++) {
            Map<String,String> act = actions_fun.get(i);
            if (act.get("act_name").equals("noneed-check") ) {
                checkVipFsend(vipFsend, user_code);
                flag = 1;
                break;
            }
        }
        if (flag == 0){
            Set<String> phones = new HashSet<String>();
            List<Privilege> acts = functionService.selectPrivilegeByAct(corp_code,"check","F0041");
            for (int i = 0; i < acts.size(); i++) {
                String master_code = acts.get(i).getMaster_code();
                master_code = master_code.replace(corp_code,"");
                if (master_code.startsWith("U")){
                    User user = userService.selectUserByCode(corp_code,master_code.substring(1,master_code.length()), Common.IS_ACTIVE_Y);
                    logger.info("==============check SMS user："+master_code.substring(1,master_code.length()));
                    if (user != null){
                        phones.add(user.getPhone());
                    }
                }
                if (master_code.startsWith("G")){
                    List<User> users = userService.selectGroupUser(corp_code,master_code.substring(1,master_code.length()));
                    logger.info("==============check SMS group："+master_code.substring(1,master_code.length()));
                    for (int j = 0; j < users.size(); j++) {
                        phones.add(users.get(j).getPhone());
                    }
                }
            }

            Object[] phones1 = phones.toArray();
            for (int i = 0; i < phones.size(); i++) {
                iceInterfaceService.sendSmsV2(corp_code,"尊敬的用户您好，您有一条群发消息待审核，请登录CRM后台查看具体信息",phones1[i].toString(),user_code,"群发审核提醒");
            }
        }
        return 1;
    }

    @Override
    public int delSendByActivityCode(String corp_code, String activity_vip_code) throws Exception {
        return vipFsendMapper.delSendByActivityCode(corp_code, activity_vip_code);
    }

    @Override
    public List<VipFsend> getSendByActivityCode(String corp_code, String activity_vip_code) throws Exception {
        return vipFsendMapper.getSendByActivityCode(corp_code, activity_vip_code);
    }

    public int updSendByType(String line_code, String line_value, String activity_vip_code) throws Exception {
        return vipFsendMapper.updSendByType(line_code, line_value, activity_vip_code);
    }


    public String textReplace(String content,JSONObject vip_info) throws Exception{
        String store_name = vip_info.getString("store_name");
        if (store_name.equals("无"))
            store_name = "未知";
        String vip_name = vip_info.getString("vip_name");
        String vip_birthday = vip_info.getString("vip_birthday");
        String join_date = vip_info.getString("join_date");
        String sex = vip_info.getString("sex");
        if (sex.equalsIgnoreCase("W") || sex.equals("F") || sex.equals("0") || sex.equals("女") || sex.equals("F")) {
            sex = "女";
        } else {
            sex = "男";
        }

        String text = content.replace("\"#store#\"", store_name);
        text = text.replace("\"#name#\"", vip_name);
        text = text.replace("\"#birthday#\"", vip_birthday);
        text = text.replace("\"#join_time#\"", join_date);
        text = text.replace("\"#sex#\"",sex);

        return text;
    }

    //活动调用群发
    public String sendSmsActivity(final VipFsend vipFsend, final String user_code, final String activity_code) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        String send_type = vipFsend.getSend_type();
        final String corp_code = vipFsend.getCorp_code();
        final String sms_code = vipFsend.getSms_code();
        final String content = vipFsend.getContent();
        final String app_id = vipFsend.getApp_id();
        final String send_scope = vipFsend.getSend_scope();
        final String sms_vips = vipFsend.getSms_vips_();
        if (send_type.equals("sms")) {

            //发送类型：短信
            if (send_scope.equals("vip_condition_new")) {
//                JSONArray sms_screen = JSONArray.parseArray(sms_vips);
//                JSONArray array = vipGroupService.vipScreen2ArrayNew(sms_screen,corp_code,"","","","","");
                DataBox dataBox = iceInterfaceService.batchSendSMS(corp_code,activity_code,content,"","",send_type,sms_code,user_code,"",sms_vips);
                logger.info("=====活动群发结果："+dataBox.status);
            }else if(send_scope.equals("vip_condition")){
                JSONArray sms_screen = JSONArray.parseArray(sms_vips);
                JSONArray array = vipGroupService.vipScreen2Array(sms_screen,corp_code,"","","","","");
                DataBox dataBox = iceInterfaceService.batchSendSMS(corp_code,activity_code,content,JSON.toJSONString(array),"",send_type,sms_code,user_code,"","");
                logger.info("=====活动群发结果："+dataBox.status);
            }
        } else {
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(new Runnable() {
                public void run() {
                    try {
                        logger.info("-------sendMessage----多线程---");

                        if (vipFsend.getApp_id() != null && !vipFsend.getApp_id().equals("")){
                            VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
                            if (vipActivity != null){

                                List<WxTemplate> wxTemplates = wxTemplateService.selectTempByAppId(app_id,"", Common.TEMPLATE_NAME_1);

                                String template_id = wxTemplates.get(0).getTemplate_id();
                                String app_user_name = corpService.getCorpByAppId(corp_code,app_id).getApp_user_name();
                                JSONObject content_obj = JSONObject.parseObject(content);
                                String title = content_obj.getString("title");
                                String info_content = content_obj.getString("info_content");

                                if (send_scope.equals("vip_condition")) {
                                    JSONArray screen = new JSONArray();
                                    if (sms_vips != null && !sms_vips.equals("")){
                                        screen = JSONArray.parseArray(sms_vips);
                                        JSONObject open_id = new JSONObject();
                                        open_id.put("key", Common.VIP_SCREEN_OPENID_KEY);
                                        open_id.put("type","text");
                                        open_id.put("value","Y");
                                        screen.add(open_id);
                                    }
//                                    DataBox dataBox = iceInterfaceService.vipScreenMethod2("1", "3", corp_code,JSON.toJSONString(screen),"","");

                                    DataBox dataBox = iceInterfaceService.vipScreenMethod2("1", "3", corp_code, JSON.toJSONString(screen), "", "");
                                    if (dataBox.status.toString().equals("SUCCESS")) {
                                        String message1 = dataBox.data.get("message").value;
                                        JSONObject msg_obj = JSONObject.parseObject(message1);
                                        int count = msg_obj.getInteger("count");
                                        int doc_count = 1;
                                        if (count > 10000) {
                                            doc_count = count / 10000 + 1;
                                        }
                                        for (int j = 1; j < doc_count + 1; j++) {
//                                            DataBox dataBox1 = iceInterfaceService.vipScreenMethod2(j+"", "10000", corp_code,JSON.toJSONString(screen),"","");
                                            DataBox dataBox1 = iceInterfaceService.vipScreenMethod2(j+"", "10000", corp_code, JSON.toJSONString(screen), "", "");
//                                            DataBox dataBox1 = vipGroupService.vipScreenBySolr(screen, corp_code, j+"", "10000", Common.ROLE_GM, "", "", "", "","","");
                                            if (dataBox.status.toString().equals("SUCCESS")) {
                                                String message2 = dataBox1.data.get("message").value;
                                                JSONObject msg_obj1 = JSONObject.parseObject(message2);
                                                JSONArray jsonArray = msg_obj1.getJSONArray("all_vip_list");
                                                for (int k = 0; k < jsonArray.size(); k++) {

                                                    logger.info(" redis 群发的状态"+redisClient.get("VipFsend Status"+corp_code+sms_code));
                                                    if (redisClient.get("VipFsend Status"+corp_code+sms_code) != null && redisClient.get("VipFsend Status"+corp_code+sms_code).toString().equals("N")){
                                                        break;
                                                    }

                                                    try{
                                                        JSONObject obj = jsonArray.getJSONObject(k);
                                                        String vip_id = obj.getString("vip_id");
                                                        String vip_name = obj.getString("vip_name");
                                                        String open_id = obj.getString("open_id");
                                                        String phone = obj.getString("vip_phone");
                                                        String cardno = obj.getString("cardno");
                                                        String message_id = corp_code + vip_id + System.currentTimeMillis();
                                                        String templ_url = CommonValue.ishop_url+"/mobile/activity/wxTelempte.html?sms_code="+sms_code+"vv"+vip_id;
                                                        if (open_id.equals("") || open_id.equals("null")) {
                                                            //会员未绑定微信，记录【未发送】
                                                            insertMongoDB(corp_code, user_code, template_id, "", vip_id, vip_name, cardno, phone, sms_code, app_id, content, message_id, "N","没有open_id");
                                                        } else {
                                                            String title1 = textReplace(title,obj);
                                                            String info_content1 = textReplace(info_content,obj);
//                                                        String result = sendTemplate1(corp_code,app_user_name,template_id,open_id,title1,templ_url,vipActivity.getActivity_theme(),info_content1);

                                                            //调用微信模板消息接口
                                                            JSONObject con = new JSONObject();
                                                            con.put("first", "亲爱的会员，"+title1);
                                                            con.put("keyword1", vipActivity.getActivity_theme()); //关键词
                                                            con.put("keyword2", info_content1); //关键词
                                                            con.put("remark", "请点击查看详情"); //备注

                                                            String result = wxTemplateService.sendTemplateMsg(app_id,open_id,template_id,con,templ_url);

                                                            JSONObject info = JSONObject.parseObject(result);
                                                            String errmsg = info.getString("errmsg");
                                                            if ("0".equals(info.getString("errcode"))) {
                                                                insertMongoDB(corp_code, user_code, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, app_id, content, message_id, "Y",errmsg);
                                                            }else {
                                                                insertMongoDB(corp_code, user_code, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, app_id, content, message_id, "N",errmsg);
                                                            }
                                                        }
                                                    }catch (Exception e){}

                                                }
                                            }
                                        }
                                    }
                                }else  if (send_scope.equals("vip_condition_new")) {
                                    JSONArray screen = new JSONArray();
                                    if (sms_vips != null && !sms_vips.equals("")){
                                        screen = JSONArray.parseArray(sms_vips);
                                        JSONObject open_id = new JSONObject();
                                        open_id.put("key","SUB_WECHAT");
                                        open_id.put("type","radio");
                                        open_id.put("groupName","微会员");

                                        JSONArray array = new JSONArray();
                                        JSONObject object = new JSONObject();
                                        object.put("opera","");
                                        object.put("logic","");
                                        object.put("value","Y");
                                        object.put("dataName","是");
                                        array.add(object);
                                        open_id.put("value",array);
                                        screen.add(open_id);
                                    }
                                    DataBox dataBox = iceInterfaceService.newStyleVipSearchForWeb("1", "3", corp_code,JSON.toJSONString(screen),"","");
//                                    DataBox dataBox = vipGroupService.vipScreenBySolrNew(screen1, corp_code, "1", "3", Common.ROLE_GM, "", "", "", "","","");
                                    if (dataBox.status.toString().equals("SUCCESS")) {
                                        String message1 = dataBox.data.get("message").value;
                                        JSONObject msg_obj = JSONObject.parseObject(message1);
                                        int count = msg_obj.getInteger("count");
                                        int doc_count = 1;
                                        if (count > 10000) {
                                            doc_count = count / 10000 + 1;
                                        }
                                        for (int j = 1; j < doc_count + 1; j++) {
                                            DataBox dataBox1 = iceInterfaceService.newStyleVipSearchForWeb(j+"", "10000", corp_code,JSON.toJSONString(screen),"","");

//                                            DataBox dataBox1 = vipGroupService.vipScreenBySolrNew(screen, corp_code, j+"", "10000", Common.ROLE_GM, "", "", "", "","","");
                                            if (dataBox.status.toString().equals("SUCCESS")) {
                                                String message2 = dataBox1.data.get("message").value;
                                                JSONObject msg_obj1 = JSONObject.parseObject(message2);
                                                JSONArray jsonArray = msg_obj1.getJSONArray("all_vip_list");
                                                for (int k = 0; k < jsonArray.size(); k++) {

                                                    logger.info(" redis 群发的状态"+redisClient.get("VipFsend Status"+corp_code+sms_code));
                                                    if (redisClient.get("VipFsend Status"+corp_code+sms_code) != null && redisClient.get("VipFsend Status"+corp_code+sms_code).toString().equals("N")){
                                                        break;
                                                    }
                                                    try{
                                                        JSONObject obj = jsonArray.getJSONObject(k);
                                                        String vip_id = obj.getString("vip_id");
                                                        String vip_name = obj.getString("vip_name");
                                                        String open_id = obj.getString("open_id");
                                                        String phone = obj.getString("vip_phone");
                                                        String cardno = obj.getString("cardno");
                                                        String message_id = corp_code + vip_id + System.currentTimeMillis();
                                                        String templ_url = CommonValue.ishop_url+"/mobile/activity/wxTelempte.html?sms_code="+sms_code+"vv"+vip_id;
                                                        if (open_id.equals("") || open_id.equals("null")) {
                                                            //会员未绑定微信，记录【未发送】
                                                            insertMongoDB(corp_code, user_code, "", "", vip_id, vip_name, cardno, phone, sms_code, app_id, content, message_id, "N","没有open_id");
                                                        } else {
                                                            String title1 = textReplace(title,obj);
                                                            String info_content1 = textReplace(info_content,obj);
//                                                        String result = sendTemplate1(corp_code,app_user_name,template_id,open_id,title1,templ_url,vipActivity.getActivity_theme(),info_content1);

                                                            JSONObject con = new JSONObject();
                                                            con.put("first", "亲爱的会员，"+title1);
                                                            con.put("keyword1", vipActivity.getActivity_theme()); //关键词
                                                            con.put("keyword2", info_content1); //关键词
                                                            con.put("remark", "请点击查看详情"); //备注
                                                            String result = wxTemplateService.sendTemplateMsg(app_id,open_id,template_id,con,templ_url);

                                                            JSONObject info = JSONObject.parseObject(result);
                                                            String errmsg = info.getString("errmsg");
                                                            if ("0".equals(info.getString("errcode"))) {
                                                                insertMongoDB(corp_code, user_code, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, app_id, content, message_id, "Y",errmsg);
                                                            }else {
                                                                insertMongoDB(corp_code, user_code, template_id, open_id, vip_id, vip_name, cardno, phone, sms_code, app_id, content, message_id, "N",errmsg);
                                                            }
                                                        }
                                                    }catch (Exception e){}

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    logger.info("------------sendMessage:"+new Date()+"Asynchronous task");
                }
            });
            executorService.shutdown();
        }

        return status;
    }


    public String sendTemplate1(String corp_code,String app_user_name,String template_id,String open_id,String title,String templ_url,String activity_theme,String content) throws Exception {


        title = "亲爱的会员，"+title;
        String remark = "请点击查看详情！";

        //调用微信模板消息接口
        JSONObject con = new JSONObject();
        JSONObject template_content = new JSONObject();
        String message_id = corp_code + open_id + System.currentTimeMillis();
        con.put("first", title);
        con.put("keyword1", activity_theme); //关键词
        con.put("keyword2", content); //关键词
        con.put("remark", remark); //备注
        template_content.put("content", con);
        template_content.put("app_user_name", app_user_name);
        template_content.put("template_id", template_id);
        template_content.put("openid", open_id);
        template_content.put("message_id", message_id);
        template_content.put("template_url", templ_url);

        String result = IshowHttpClient.post(CommonValue.wechat_url+"/sendActivityNotice",template_content);
        return result;
    }


    @Override
    public String getVipFsendByMessage(String message) throws Exception {
        MongoTemplate mongoTemplate= this.mongodbClient.getMongoTemplate();
        DBCollection dbCollection=mongoTemplate.getCollection(CommonValue.table_vip_batchsend_message);

        JSONObject jsonObject=JSON.parseObject(message);
        String vipFsend_id = jsonObject.get("id").toString().trim();
        int page_num=Integer.parseInt(jsonObject.getString("page_num"));
        int page_size=Integer.parseInt(jsonObject.getString("page_size"));

        VipFsend vipFsend = vipFsendMapper.selectById(Integer.parseInt(vipFsend_id));
        String sms_code = vipFsend.getSms_code();
        String corp_code = vipFsend.getCorp_code();
        BasicDBObject basicDBObject=new BasicDBObject();
        BasicDBList basicDBList=new BasicDBList();
        basicDBList.add(new BasicDBObject("sms_code",sms_code));
        basicDBList.add(new BasicDBObject("corp_code",corp_code));
        //筛选条件
        String screen=jsonObject.getString("screen");
        //搜索条件
        String search=jsonObject.getString("search");
        if(StringUtils.isNotBlank(screen)) {
            JSONObject screen_obj = JSON.parseObject(screen);
            String vip_name = screen_obj.getString("vip_name");//会员名称
            String vip_cardno = screen_obj.getString("vip_cardno");//会员卡号
            String is_send = screen_obj.getString("is_send");//发送状态
            String vip_phone=screen_obj.getString("vip_phone");//手机号
            String send_start = JSONArray.parseObject(screen_obj.getString("send_date")).getString("start");
            String send_end = JSONArray.parseObject(screen_obj.getString("send_date")).getString("end");

            if (StringUtils.isNotBlank(vip_name)) {
                basicDBList.add(new BasicDBObject("vip_name", new BasicDBObject("$regex", vip_name)));
            }
            if (StringUtils.isNotBlank(vip_cardno)) {
                basicDBList.add(new BasicDBObject("cardno", new BasicDBObject("$regex", vip_cardno)));
            }
            if (StringUtils.isNotBlank(is_send)) {
                basicDBList.add(new BasicDBObject("is_send", is_send));
            }
            if (StringUtils.isNotBlank(send_start)) {
                basicDBList.add(new BasicDBObject("message_date", new BasicDBObject(QueryOperators.GTE, send_start)));//+" 00:00:00"
            }
            if (StringUtils.isNotBlank(send_end)) {
                basicDBList.add(new BasicDBObject("message_date", new BasicDBObject(QueryOperators.LTE, send_end)));//+" 23:59:59"
            }
            if(StringUtils.isNotBlank(vip_phone)){
                basicDBList.add(new BasicDBObject("vip_phone",new BasicDBObject("$regex",vip_phone)));
            }
        } else  if(StringUtils.isNotBlank(search)) {
            BasicDBObject basicDBObject1=new BasicDBObject();
            BasicDBList basicDBList1=new BasicDBList();
            basicDBList1.add(new BasicDBObject("vip_name",new BasicDBObject("$regex", search)));
            basicDBList1.add(new BasicDBObject("cardno",new BasicDBObject("$regex", search)));
            basicDBObject1.put("$or",basicDBList1);
            basicDBList.add(basicDBObject1);
        }

        basicDBObject.put("$and",basicDBList);
        DBCursor dbCursor=dbCollection.find(basicDBObject);
        logger.info("=========getVipFsendByMessage"+dbCursor.count());
        int count=dbCursor.count();
        int pages = MongoUtils.getPages(dbCursor, page_size);
        DBCursor dbCursor1= MongoUtils.sortAndPage(dbCursor,page_num,page_size,"message_date",-1);
        List list= MongoUtils.dbCursorToList(dbCursor1);


        //获取会员总数
        BasicDBObject basicDBObject1=new BasicDBObject();
        basicDBObject1.put("sms_code",sms_code);
        basicDBObject1.put("corp_code",corp_code);
        int all_count=dbCollection.find(basicDBObject1).count();
        //获取未发送会员
        basicDBObject1.put("is_send","N");
        int fail_count=dbCollection.find(basicDBObject1).count();
        int success_count=all_count-fail_count;

        JSONObject info = new JSONObject();

        info.put("vip_info", list);
        info.put("count",count);
        info.put("pages",pages);
        info.put("page_num",page_num);
        info.put("page_size",page_size);
        info.put("fail_count",fail_count);
        info.put("success_count",success_count);
        message = JSON.toJSONString(info);

        return message;
    }


    public  PageInfo<VipFsend> switchVipFsend(PageInfo<VipFsend> list) throws Exception{
        MongoTemplate mongoTemplate= this.mongodbClient.getMongoTemplate();
        DBCollection dbCollection=mongoTemplate.getCollection(CommonValue.table_vip_batchsend_message);
        for (int i = 0; i < list.getList().size(); i++) {
            VipFsend vipFsend=list.getList().get(i);
            //获取会员总数
            BasicDBObject basicDBObject1=new BasicDBObject();
            basicDBObject1.put("sms_code",vipFsend.getSms_code());
            basicDBObject1.put("corp_code",vipFsend.getCorp_code());
            int all_count=dbCollection.find(basicDBObject1).count();
            //获取未发送会员
            basicDBObject1.put("is_send","N");
            int fail_count=dbCollection.find(basicDBObject1).count();
            int success_count=all_count-fail_count;
            vipFsend.setFail_count(fail_count);
            vipFsend.setSuccess_count(success_count);
        }
        return  list;
    }


}
