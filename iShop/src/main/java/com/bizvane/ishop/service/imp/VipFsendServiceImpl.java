package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.sun.common.service.http.HttpClient;
import com.bizvane.ishop.dao.VipFsendMapper;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.VipFsend;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    private static HttpClient httpClient = new HttpClient();

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
        String corp_code = vipFsend.getCorp_code();
        String sms_vips = vipFsend.getSms_vips();
        JSONObject vips_obj = JSONObject.parseObject(sms_vips);
        String type = vips_obj.get("type").toString();
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
        return message;
    }

    @Override
    public PageInfo<VipFsend> getAllVipFsendByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<VipFsend> vipFsends;
        PageHelper.startPage(page_number, page_size);
        vipFsends = vipFsendMapper.selectAllFsend(corp_code, search_value);
        for (VipFsend vipFsend : vipFsends) {
            vipFsend.setIsactive(CheckUtils.CheckIsactive(vipFsend.getIsactive()));
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

        JSONArray vip_infos;
        String openids = "";
        String phone = "";
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
                vip_infos = msg_obj.getJSONArray("vip_info");
                for (int i = 0; i < vip_infos.size(); i++) {
                    JSONObject vip_obj = vip_infos.getJSONObject(i);
                    phone = phone + vip_obj.getString("MOBILE_VIP") + ",";
                    if (!vip_obj.getString("OPEN_ID").equals("")) {
                        openids = openids + vip_obj.getString("OPEN_ID") + ",";
                    }
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
                vip_infos = msg_obj.getJSONArray("vip_info");
                for (int i = 0; i < vip_infos.size(); i++) {
                    JSONObject vip_obj = vip_infos.getJSONObject(i);
                    phone = phone + vip_obj.getString("MOBILE_VIP") + ",";
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
            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipInfo", datalist);
            String message1 = dataBox.data.get("message").value;
            JSONObject msg_obj = JSONObject.parseObject(message1);
            vip_infos = msg_obj.getJSONArray("vip_info");
            for (int i = 0; i < vip_infos.size(); i++) {
                JSONObject vip_obj = vip_infos.getJSONObject(i);
                phone = phone + vip_obj.getString("MOBILE_VIP") + ",";
                if (!vip_obj.getString("OPEN_ID").equals("")) {
                    openids = openids + vip_obj.getString("OPEN_ID") + ",";
                }

            }
        }
        vipFsend.setSms_code(sms_code);
        vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
        vipFsend.setCreater(user_id);
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
            } else {
                //发送类型：微信模板消息
                JSONObject template_content = JSONObject.parseObject(content);
                template_content.put("openid", openids);
                String result = sendTemplate(template_content);
                    JSONObject info = JSONObject.parseObject(result);
                    if ("0".equals(info.getString("errcode"))) {
                        return status;
                    } else if(info.getString("errcode").equals("40003")){
                        status = "invalid";
                        return status;
                    }else{
                        status = "发送失败";
                        return status;
                    }

            }
        } else {
            status = "发送失败";
            return status;
        }
    }

        @Override
        public String update (String message, String user_id) throws Exception {
            return "";
        }

        @Override
        public int delete ( int id) throws Exception {
            return vipFsendMapper.deleteById(id);
        }

        @Override
        public PageInfo<VipFsend> getAllVipFsendScreen ( int page_number, int page_size, String
        corp_code, Map < String, String > map) throws Exception {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("corp_code", corp_code);
            params.put("map", map);
            PageHelper.startPage(page_number, page_size);
            List<VipFsend> list1 = vipFsendMapper.selectAllFsendScreen(params);
            for (VipFsend activityVip : list1) {
                activityVip.setIsactive(CheckUtils.CheckIsactive(activityVip.getIsactive()));
            }
            PageInfo<VipFsend> page = new PageInfo<VipFsend>(list1);
            return page;
        }

        @Override
        public VipFsend getVipFsendForId (String corp_code, String sms_code) throws Exception {

            return vipFsendMapper.selectForId(corp_code, sms_code);
        }


        //微信模板

    public static String sendTemplate(JSONObject extras) throws Exception {

        RequestBody body = RequestBody.create(Common.JSON, extras.toJSONString());
        Request request = new Request.Builder().url(Common.SENDTEMPLATE_URL).post(body).build();
        Response response = httpClient.post(request);
        String result = response.body().string();
        return result;
    }
}
