package com.bizvane.ishop.service.imp;

import IceInternal.Ex;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.sun.common.service.http.HttpClient;
import com.bizvane.ishop.dao.VipFsendMapper;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.VipFsend;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.jdbc.SpringJdbcService;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.common.utils.JSONUtil;
import com.bizvane.sun.common.utils.MapUtil;
import com.bizvane.sun.common.utils.SpringUtil;
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
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
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
    private UserService userService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private ValidateCodeService validateService;
    @Autowired
    MongoDBClient mongodbClient;

    private static HttpClient httpClient = new HttpClient();


    /**
     * 查看选择接收群发消息的会员信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public String getVipFsendById(int id, String send_type, String content) throws Exception {
        String message = "";
        String vip_id = "";
        String vip_name = "";
        VipFsend vipFsend = vipFsendMapper.selectById(id);
        String corp_code = vipFsend.getCorp_code();
        String sms_vips = vipFsend.getSms_vips();
        JSONObject vips_obj = JSONObject.parseObject(sms_vips);
        String type = vips_obj.get("type").toString().trim();
        if (send_type.equals("sms")) {
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
                message = dataBox.data.get("message").value;
            }
        } else if (send_type.equals("template")) {
            //如果发送类型是微信模板消息，根据筛选会员方式获取vip_id
            MongoDBClient mongoDBClient = SpringUtil.getBean("mongodbClient");
            //字符串去换行符
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(content);
            String m1 = m.replaceAll("");
            JSONObject contents = JSONObject.parseObject(m1);
            String message_id = contents.get("message_id").toString().trim();

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
                    JSONObject msg_obj = JSONObject.parseObject(message);
                    JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
                    for (int i = 0; i < vip_infos.size(); i++) {
                        JSONObject vip_obj = vip_infos.getJSONObject(i);
                        vip_id = vip_id + vip_obj.getString("VIP_ID") + ",";
                        vip_name = vip_id + vip_obj.getString("NAME_VIP") + ",";

                    }
                } else {
                    Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                    Data data_user_code = new Data("user_codes", user_code, ValueType.PARAM);
                    Map datalist = new HashMap<String, Data>();
                    datalist.put(data_corp_code.key, data_corp_code);
                    datalist.put(data_user_code.key, data_user_code);
                    DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo", datalist);
                    message = dataBox.data.get("message").value;
                    JSONObject msg_obj = JSONObject.parseObject(message);
                    JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
                    for (int i = 0; i < vip_infos.size(); i++) {
                        JSONObject vip_obj = vip_infos.getJSONObject(i);
                        vip_id = vip_id + vip_obj.getString("VIP_ID") + ",";
                        vip_name = vip_id + vip_obj.getString("NAME_VIP") + ",";
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
                String message1 = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(message1);
                JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
                for (int i = 0; i < vip_infos.size(); i++) {
                    JSONObject vip_obj = vip_infos.getJSONObject(i);
                    vip_name = vip_id + vip_obj.getString("NAME_VIP") + ",";
                }

            }
            //查询MongoDB数据库获取列表
            String vipid[] = vip_id.split(",");
            String vipname[] = vip_name.split(",");
            List<Map<String, Object>> list = new ArrayList();
            for (int i = 0; i < vipid.length; i++) {
                for (int j = 0; j < vipname.length; j++) {
                    String vip = vipid[i];
                    String name = vipname[i];
                    Map query_key = new HashMap();
                    query_key.put("template", "fsend");
                    query_key.put("message_id", message_id);
                    query_key.put("vip_id", vip);
                    List<Map<String, Object>> message_list = mongoDBClient.query("vip_message_content", query_key);
                    if (message_list.size() == 0) {
                        Map<String, Object> list_fail = new HashedMap();
                        list_fail.put("vip_id", vip);
                        list_fail.put("vip_name", name);
                        list_fail.put("is_read", "发送失败");
                        list.add(list_fail);
                    } else {
                        list.addAll(message_list);
                    }
                }
            }
        } else {
            message = "发送类型不合法";
        }
        return message;
    }

    @Override
    public PageInfo<VipFsend> getAllVipFsendByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<VipFsend> vipFsends;
        PageHelper.startPage(page_number, page_size);
        vipFsends = vipFsendMapper.selectAllFsend(corp_code, search_value);
        for (VipFsend vipFsend : vipFsends) {
            vipFsend.setIsactive(CheckUtils.CheckIsactive(vipFsend.getIsactive()));
            String send_type=vipFsend.getSend_type();
            String result="";
            if(send_type==null){
                result="";
            }else if(send_type.equals("sms")){
                result="短信";
            }else if(send_type.equals("template")){
                result="微信模板";
            }else{
                result="";
            }
            vipFsend.setSend_type(result);
        }

        PageInfo<VipFsend> page = new PageInfo<VipFsend>(vipFsends);

        return page;
    }

    @Override
    public String insert(String message, String user_id) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
        Date now = new Date();
        //群发消息的发送类型
        String send_type = jsonObject.get("send_type").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(now);
        VipFsend vipFsend = WebUtils.JSON2Bean(jsonObject, VipFsend.class);
        // String sms_vips = vipFsend.getSms_vips();
        String sms_vips = jsonObject.get("sms_vips").toString().trim();
        String content = vipFsend.getContent();
        JSONObject sms_vips_obj = JSONObject.parseObject(sms_vips);
        String type = sms_vips_obj.getString("type");
        String openids = "";
        String phone = "";
        String vip_id = "";
        String vip_name = "";
        if (type.equals("1")) {
            String area_code = sms_vips_obj.get("area_code").toString();
            String brand_code = sms_vips_obj.get("brand_code").toString();
            String store_code = sms_vips_obj.get("store_code").toString();
            String vip_user_code = sms_vips_obj.get("user_code").toString();

            if (vip_user_code.equals("")) {

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
                String message1 = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(message1);
                JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
                for (int i = 0; i < vip_infos.size(); i++) {
                    JSONObject vip_obj = vip_infos.getJSONObject(i);
                    phone = phone + vip_obj.getString("MOBILE_VIP") + ",";
                    vip_id = vip_id + vip_obj.getString("VIP_ID") + ",";
                    if (!vip_obj.getString("OPEN_ID").equals("")) {
                        openids = openids + vip_obj.getString("OPEN_ID") + ",";
                    }
                    vip_name = vip_id + vip_obj.getString("NAME_VIP") + ",";

                }
            } else {
                Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                Data data_user_code = new Data("user_codes", user_id, ValueType.PARAM);

                Map datalist = new HashMap<String, Data>();
                datalist.put(data_corp_code.key, data_corp_code);
                datalist.put(data_user_code.key, data_user_code);
                DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo", datalist);
                String message1 = dataBox.data.get("message").value;
                JSONObject msg_obj = JSONObject.parseObject(message1);
                JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
                for (int i = 0; i < vip_infos.size(); i++) {
                    JSONObject vip_obj = vip_infos.getJSONObject(i);
                    phone = phone + vip_obj.getString("MOBILE_VIP") + ",";
                    vip_id = vip_id + vip_obj.getString("VIP_ID") + ",";
                    vip_name = vip_id + vip_obj.getString("NAME_VIP") + ",";
                    if (!vip_obj.getString("OPEN_ID").equals("")) {
                        openids = openids + vip_obj.getString("OPEN_ID") + ",";
                    }
                }
            }
        } else {
            String vips = sms_vips_obj.get("vips").toString();
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_vip_id = new Data("vip_ids", vips, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_vip_id.key, data_vip_id);
            vip_id = vip_id + vips;
            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo", datalist);
            String message1 = dataBox.data.get("message").value;
            JSONObject msg_obj = JSONObject.parseObject(message1);
            JSONArray vip_infos = msg_obj.getJSONArray("vip_info");
            for (int i = 0; i < vip_infos.size(); i++) {
                JSONObject vip_obj = vip_infos.getJSONObject(i);
                phone = phone + vip_obj.getString("MOBILE_VIP") + ",";
                vip_name = vip_id + vip_obj.getString("NAME_VIP") + ",";
                if (!vip_obj.getString("OPEN_ID").equals("")) {
                    openids = openids + vip_obj.getString("OPEN_ID") + ",";
                }
            }
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
        int num = 0;
        num = vipFsendMapper.insertFsend(vipFsend);
        System.out.print(num);
        if (num > 0) {
            //插表成功后调用接口
            if (send_type.equals("sms")) {
                //发送类型：短信
                Data data_channel = new Data("channel", "santong", ValueType.PARAM);
                Data data_phone = new Data("phone", phone, ValueType.PARAM);
                Data data_text = new Data("text", content, ValueType.PARAM);

                Map datalist = new HashMap<String, Data>();
                datalist.put(data_channel.key, data_channel);
                datalist.put(data_phone.key, data_phone);
                datalist.put(data_text.key, data_text);
                DataBox dataBox = iceInterfaceService.iceInterfaceV3("SendSMS", datalist);
                if (!dataBox.status.toString().equals("SUCCESS")) {
                    status = "发送失败";
                    return status;
                }
                status = Common.DATABEAN_CODE_SUCCESS;
                return status;
            } else if (send_type.equals("template")) {
                //发送类型：微信模板消息
                JSONObject template_content = JSONObject.parseObject(content);
                template_content.put("openid", openids);
                String auth_appid = template_content.get("app_user_name").toString().trim();
                String message_id = template_content.get("message_id").toString().trim();
                JSONObject test = new JSONObject();
                test.put("errcode", "0");
                String result = test.toString();
                //String result = sendTemplate(template_content);
                JSONObject info = JSONObject.parseObject(result);
                String openid[] = openids.split(",");
                String vipid[] = vip_id.split(",");
                String vipname[] = vip_name.split(",");
                for (int i = 0; i < openid.length; i++) {
                    for (int j = 0; j < vipid.length; j++) {
                        String open_id = openid[i];
                        String id = vipid[i];
                        insertMongoDB(corp_code, open_id, id, auth_appid, vipname[i]);
                    }
                }
                if ("0".equals(info.getString("errcode"))) {
                    updateReadInfo(message_id);
                    return status;
                } else if (info.getString("errcode").equals("40003")) {
                    status = "invalid";
                    return status;
                } else {
                    status = "发送失败";
                    return status;
                }
            } else {
                status = "发送类型不合法";
                return status;
            }
        } else {
            status = "发送失败";
            return status;
        }
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        return "";
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
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<VipFsend> list1 = vipFsendMapper.selectAllFsendScreen(params);
        for (VipFsend vipFsend : list1) {
            vipFsend.setIsactive(CheckUtils.CheckIsactive(vipFsend.getIsactive()));
            String send_type=vipFsend.getSend_type();
            String result="";
            if(send_type==null){
                result="";
            }else if(send_type.equals("sms")){
                result="短信";
            }else if(send_type.equals("template")){
                result="微信模板";
            }else{
                send_type="";
            }
            vipFsend.setSend_type(result);

        }
        PageInfo<VipFsend> page = new PageInfo<VipFsend>(list1);
        return page;
    }

    @Override
    public VipFsend getVipFsendForId(String corp_code, String sms_code) throws Exception {

        return vipFsendMapper.selectForId(corp_code, sms_code);
    }


    /**
     * 微信发送模板
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
     * 插入mongoDB
     *
     * @param corp_code
     * @param openid
     * @param vip_id
     * @param app_user_name
     * @param vip_name
     * @throws Exception
     */
    public void insertMongoDB(String corp_code, String openid, String vip_id, String app_user_name, String vip_name) throws Exception {

        Date now = new Date();
        String message_date = Common.DATETIME_FORMAT.format(now);
        String message_id = app_user_name + openid + System.currentTimeMillis();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_vip_message_content);
        DBObject saveData = new BasicDBObject();
        saveData.put("_id", message_id);
        saveData.put("message_target", "1");
        saveData.put("corp_code", corp_code);
        saveData.put("open_id", openid);
        saveData.put("vip_id", vip_id);
        saveData.put("message_type", "text");
        saveData.put("message_content", "");
        saveData.put("user_code", "");
        saveData.put("message_date", message_date);
        saveData.put("auth_appid", "");
        saveData.put("template", "fsend");
        saveData.put("is_read", "N");
        // saveData.put("is_send", "Y");
        saveData.put("vip_name", vip_name);
        collection.insert(saveData);

    }

    /**
     * 更新已读状态
     *
     * @param message_id
     * @return
     * @throws Exception
     */
    public JSONObject updateReadInfo(String message_id) throws Exception {
        JSONObject message = new JSONObject();
        MongoDBClient mongoDBClient = SpringUtil.getBean("mongodbClient");
        Map query_key = new HashMap();
        query_key.put("_id", "read" + System.currentTimeMillis());
        List<Map<String, Object>> message_list = mongoDBClient.query("vip_message_content", query_key);
        if (message_list.size() > 0) {
            message = JSONObject.parseObject(JSONUtil.getJsonString(message_list.get(0)));
            Map old = message_list.get(0);
            Object _id = old.get("_id");
            Map map_new = new HashMap();
            map_new.put("_id", _id);
            map_new.put("message_target", old.get("message_target"));
            map_new.put("corp_code", old.get("corp_code"));
            map_new.put("open_id", old.get("open_id"));
            map_new.put("vip_id", old.get("vip_id"));
            map_new.put("message_type", old.get("message_type"));
            map_new.put("message_content", old.get("message_content"));
            map_new.put("user_code", old.get("user_code"));
            map_new.put("message_date", old.get("message_date"));
            map_new.put("auth_appid", old.get("auth_appid"));
            map_new.put("template", old.get("template"));
            map_new.put("is_read", "Y");
            // map_new.put("is_send", old.get("is_send"));
            mongoDBClient.update("vip_message_content", map_new, old);
        }
        return message;
    }
}
