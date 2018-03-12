package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.CorpWechat;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.sun.common.service.http.HttpClient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class NetWorkServiceImpl {

    private static HttpClient httpClient = new HttpClient();

    @Autowired
    CorpService corpService;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//    private static final String wechat_url_outdoor = "http://dev-wechat.bizvane.com/new/outdoor";
    private static final String wechat_url_outdoor = "http://pub-wechat.bizvane.com/new/outdoor";



    /**
     * 根据app_id获取领取电子会员卡地址
     *
     * @throws IOException
     * @returnget
     */
    public String getCardUrl(String corp_code,String app_id) throws Exception {
        String open_card_url = "";
        JSONObject jsonObject = new JSONObject();
        JSONObject merchandiseMap = new JSONObject();
        merchandiseMap.put("ltconfig", "wx_cardtemp");
        merchandiseMap.put("columun", "opcardurl");
        merchandiseMap.put("condition", "wx_public_id;appid==" + app_id);
        JSONObject signinfoMap = new JSONObject();
        signinfoMap.put("customcode", app_id);
        String time = String.valueOf(System.currentTimeMillis());
        signinfoMap.put("ts", time);
        signinfoMap.put("sign", getSign(corp_code,app_id,time));
        signinfoMap.put("method", "com.bizvane.sun.wx.method.GetJSONArray");
        merchandiseMap.put("signinfo", signinfoMap);
        jsonObject.put("data", merchandiseMap);

        RequestBody body = RequestBody.create(JSON, jsonObject.toJSONString());
        Request request = new Request.Builder().url(wechat_url_outdoor).post(body).build();
        Response response = httpClient.post(request);
        String result = response.body().string();
        JSONObject result_obj = JSONObject.parseObject(result);
        if (result_obj.containsKey("code") && result_obj.getString("code").equals("0")){
            String data = result_obj.get("data").toString();
            JSONObject data_obj = JSONObject.parseObject(data);
            JSONArray list = data_obj.getJSONArray("list");
            if (list.size() > 0){
                JSONObject object = list.getJSONObject(0);
                open_card_url = object.getString("opcardurl");
            }
        }
        return open_card_url;
    }

    public String getSign(String corp_code,String app_id,String time) throws Exception{
        CorpWechat corpWechat = corpService.getCorpByAppId(corp_code,app_id);
        String sign = "";
        if (corpWechat != null){
            String access_key = corpWechat.getAccess_key();
            sign = CheckUtils.encryptMD5Hash(app_id + time + access_key).toUpperCase();

            System.out.println("============"+app_id+"==="+time+"==="+access_key);
            System.out.println("===========sign="+sign);

        }

        return sign;
    }





}
