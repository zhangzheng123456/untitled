package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.utils.IshowHttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by ZhouZhou on 2016/10/10.
 */

@Service
public class WeiMobServiceImpl {

    String spuFullUrlHead = "https://open.weimob.com/api/mname/WE_MALL/cname/spuFullInfoGet?accesstoken=";
    IshowHttpClient ishowHttpClient = new IshowHttpClient();
    public String accessToken="";
    public Date startTime;

    private static final Logger logger = Logger.getLogger(WeiMobServiceImpl.class);




    public JSONArray getList(String accessToken, int rowno) throws Exception{

        int pagesize = 10;
        int pageno = rowno/pagesize +1;
        JSONObject obj = getSpuFull(accessToken, 1, 1, pagesize, false);
        String data = obj.get("data").toString();
        JSONObject object = JSONObject.parseObject(data);
        String count = object.get("page_count").toString();

        JSONArray array = new JSONArray();
        if(pageno<=Integer.parseInt(count)){
            JSONObject spuFull = getSpuFull(accessToken, 1, pageno, pagesize, false);
            String dataSpuFull = spuFull.get("data").toString();
            JSONObject objectSpuFull = JSONObject.parseObject(dataSpuFull);
            String dataList = objectSpuFull.get("page_data").toString();
            JSONArray pageData = JSONArray.parseArray(dataList);
            for (int i = 0; i < pageData.size(); i++) {
                JSONObject param = new JSONObject();
                String code = pageData.getJSONObject(i).getJSONObject("spu").get("spu_id").toString();
                String image = pageData.getJSONObject(i).getJSONObject("spu").get("default_img").toString();
                String title = pageData.getJSONObject(i).getJSONObject("spu").get("spu_name").toString();
                String price = pageData.getJSONObject(i).getJSONObject("spu").get("low_sellprice").toString();
                String url = "http://55757490.m.weimob.com/vshop/Goods/GoodsDetail1/"+code;
                param.put("id", code);
                param.put("photo", image);
                param.put("name", title);
                param.put("finalPrice", price);
                param.put("url", url);
                param.put("shareUrl", url);
                param.put("isNew", "");
                param.put("isHot", "");
                //		logger.debug("list ->" + param);
                array.add(param);
            }

        }
        return array;
    }

    public JSONArray getSearchClassify(String accessToken,String xx) throws Exception {


        JSONObject data = getSpuFull(accessToken, 1, 1, 10, false).getJSONObject("data");
        int rowCount = Integer.parseInt(data.get("row_count").toString());
        JSONArray page_data = getSpuFull(accessToken, 1, 1, rowCount, false).
                getJSONObject("data").getJSONArray("page_data");

        JSONArray array = new JSONArray();
        for (int i = 0; i < page_data.size(); i++) {
            String classify = page_data.getJSONObject(i).getJSONObject("spu").get("classify_ids").toString();
            if(classify.contains(xx)){
                JSONObject param = new JSONObject();
                String code = page_data.getJSONObject(i).getJSONObject("spu").get("spu_id").toString();
                String image = page_data.getJSONObject(i).getJSONObject("spu").get("default_img").toString();
                String title = page_data.getJSONObject(i).getJSONObject("spu").get("spu_name").toString();
                String price = page_data.getJSONObject(i).getJSONObject("spu").get("low_sellprice").toString();
                String classifyID = page_data.getJSONObject(i).getJSONObject("spu").get("classify_ids").toString();
                String url = "http://55757490.m.weimob.com/vshop/Goods/GoodsDetail1/"+code;

                param.put("id", code);
                param.put("photo", image);
                param.put("name", title);
                param.put("finalPrice", price);
                param.put("url", url);
                param.put("shareUrl", url);
                param.put("isNew", "");
                param.put("isHot", "");
                param.put("classifyID", classifyID);

                logger.debug("search Classify ->" + param);
                array.add(param);
            }
        }
        return array;
    }

    public JSONArray getSearchTitle(String accessToken,String xx) throws Exception{
        JSONObject data = getSpuFull(accessToken, 1, 1, 10, false).getJSONObject("data");
        int rowCount = Integer.parseInt(data.get("row_count").toString());

        JSONArray page_data = getSpuFull(accessToken, 1, 1, rowCount, false).
                getJSONObject("data").getJSONArray("page_data");

        JSONArray array = new JSONArray();
        for (int i = 0; i < page_data.size(); i++) {
            String spu_name = page_data.getJSONObject(i).getJSONObject("spu").get("spu_name").toString();
            if(spu_name.contains(xx)){
                JSONObject param = new JSONObject();
                String code = page_data.getJSONObject(i).getJSONObject("spu").get("spu_id").toString();
                String image = page_data.getJSONObject(i).getJSONObject("spu").get("default_img").toString();
                String title = page_data.getJSONObject(i).getJSONObject("spu").get("spu_name").toString();
                String price = page_data.getJSONObject(i).getJSONObject("spu").get("low_sellprice").toString();
                String classifyID = page_data.getJSONObject(i).getJSONObject("spu").get("classify_ids").toString();
                String url = "http://55757490.m.weimob.com/vshop/Goods/GoodsDetail1/"+code;
                param.put("id", code);
                param.put("photo", image);
                param.put("name", title);
                param.put("finalPrice", price);
                param.put("isNew", "");
                param.put("isHot", "");
                param.put("url", url);
                param.put("shareUrl", url);
                param.put("classifyID", classifyID);

                logger.debug("search Title ->" + param);
                array.add(param);
            }
        }
        return array;
    }



    public String getAccessToken(String appID,String appSecert) throws Exception {
        startTime = new Date();
        String url = "https://open.weimob.com/common/token?grant_type=client_credential&appid="+appID+"&secret="+appSecert;
        String context = ishowHttpClient.get(url);
        JSONObject jsonObject = JSONObject.parseObject(context);
        accessToken= jsonObject.getJSONObject("data").get("access_token").toString();
        System.out.println(accessToken);
        return accessToken;
    }

    //��ȡ��Ʒ��Ϣ�͹����Ϣ
    public JSONObject getSpuFull(String accessToken,int isOnsale,int pageNo,int pageSize,boolean includeDescription) throws Exception {
        org.json.JSONObject param = new org.json.JSONObject();
        param.put("is_onsale", isOnsale);
        param.put("page_no", pageNo);
        param.put("page_size", pageSize);
        param.put("include_description", includeDescription);
        String url = spuFullUrlHead+accessToken;
        String context = ishowHttpClient.post(url,param);
        JSONObject spuFull = JSONObject.parseObject(context);
        System.out.println("getSpuFull"+context);

        return spuFull;
    }

    //	��ȡ��Ʒ����
    public JSONArray getClassify(String accessToken) throws Exception {
        org.json.JSONObject param = new org.json.JSONObject();

        JSONArray array = new JSONArray();
        param.put("classify_pid", 0);
        param.put("page_no", 1);
        param.put("page_size", 40);
        String url = "https://open.weimob.com/api/mname/WE_MALL/cname/goodsclassifyGet?accesstoken="+accessToken;
        JSONObject classify = JSONObject.parseObject(ishowHttpClient.post(url,param));
        JSONArray classifyvalue = classify.getJSONObject("data").getJSONArray("page_data");
        for (int i = 0; i < classifyvalue.size(); i++) {
            JSONObject getparam = new JSONObject();
            String classify_id = classifyvalue.getJSONObject(i).get("classify_id").toString();
            String classify_name = classifyvalue.getJSONObject(i).get("classify_name").toString();
            getparam.put("code", classify_id);
            getparam.put("name", classify_name);
            array.add(getparam);
        }

        System.out.println("getClassify"+array.toString());
        return array;
    }

    public JSONArray getClassifySon(String accessToken) throws Exception {
        JSONArray classifySon = new JSONArray();
        JSONArray array11 = getClassify(accessToken);
        for (int j = 0; j < array11.size(); j++) {
            JSONArray array = new JSONArray();
            org.json.JSONObject param = new org.json.JSONObject();
            param.put("classify_pid", array11.getJSONObject(j).get("code").toString());
            param.put("page_no", 1);
            param.put("page_size", 20);
            String url = "https://open.weimob.com/api/mname/WE_MALL/cname/goodsclassifyGet?accesstoken="+accessToken;
            JSONObject classify = JSONObject.parseObject(ishowHttpClient.post(url,param));
            JSONArray classifyvalue = classify.getJSONObject("data").getJSONArray("page_data");
            for (int i = 0; i < classifyvalue.size(); i++) {
                JSONObject getparam = new JSONObject();
                String classify_id = classifyvalue.getJSONObject(i).get("classify_id").toString();
                String classify_name = classifyvalue.getJSONObject(i).get("classify_name").toString();
                getparam.put("code", classify_id);
                getparam.put("name", classify_name);
                array.add(getparam);
            }
            System.out.println("getClassify"+array.toString());
            classifySon.add(array);
        }
        return classifySon;
    }
}
