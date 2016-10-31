package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.WeimobMapper;
import com.bizvane.ishop.entity.Weimob;
import com.bizvane.ishop.service.WeimobService;
import com.bizvane.ishop.utils.IshowHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by ZhouZhou on 2016/10/10.
 */

@Service
public class WeiMobServiceImpl implements WeimobService{

//    String spuFullUrlHead = "https://open.weimob.com/api/mname/WE_MALL/cname/spuFullInfoGet?accesstoken=";
    String spuFullUrlHead = "https://dopen.weimob.com/api/1_0/wangpu/Spu/FullInfoGet?accesstoken=";

    IshowHttpClient ishowHttpClient = new IshowHttpClient();

    @Autowired
    WeimobMapper weimobMapper;

    private static final Logger logger = Logger.getLogger(WeiMobServiceImpl.class);

    public Weimob selectByCorpId(String corp_code) throws Exception {
        return weimobMapper.selectByCorpId(corp_code);
    }

    public int insert(Weimob weimob) throws Exception{
        return weimobMapper.insertCorpWeimob(weimob);
    }

    public int update(Weimob weimob) throws Exception{
        return weimobMapper.updateCorpWeimob(weimob);
    }

    public String getAccessTokenByCode(String client_id,String client_secret) throws Exception {
        Date startTime = new Date();
        JSONObject obj = new JSONObject();
        Weimob weimob = selectByCorpId("C10116");
        String code = weimob.getCode();
        String url = "https://dopen.weimob.com/fuwu/b/oauth2/token?code="+code+"&grant_type=authorization_code&client_id="+client_id+"&client_secret="+client_secret+"&redirect_uri="+CommonValue.REDIRECT_URL;
        String context = ishowHttpClient.post(url,obj);
        System.out.println(context);

        JSONObject jsonObject = JSONObject.parseObject(context);
        String accessToken= jsonObject.get("access_token").toString();
        String refresh_token= jsonObject.get("refresh_token").toString();
        weimob.setAccess_token(accessToken);
        weimob.setRefresh_access_token(refresh_token);
        weimob.setLast_time(Common.DATETIME_FORMAT.format(startTime));
        weimob.setLast_time_refresh(Common.DATETIME_FORMAT.format(startTime));
        update(weimob);
        return accessToken;
    }


    public String refreshAccessToken(String client_id,String client_secret,String refresh_token) throws Exception {
        Date startTime = new Date();
        JSONObject obj = new JSONObject();
        Weimob weimob = selectByCorpId("C10116");
        String url = "https://dopen.weimob.com/fuwu/b/oauth2/token?grant_type=refresh_token&client_id="+client_id+"&client_secret="+client_secret+"&refresh_token="+refresh_token;
        String context = ishowHttpClient.post(url,obj);
        System.out.println(context);

        JSONObject jsonObject = JSONObject.parseObject(context);
        String accessToken= jsonObject.get("access_token").toString();
        String refresh_token1= jsonObject.get("refresh_token").toString();
        weimob.setAccess_token(accessToken);
        weimob.setRefresh_access_token(refresh_token1);
        weimob.setLast_time(Common.DATETIME_FORMAT.format(startTime));
        weimob.setLast_time_refresh(Common.DATETIME_FORMAT.format(startTime));
        update(weimob);
        return accessToken;
    }

    public String generateToken(String client_id,String client_secret) throws Exception{
        Date now = new Date();
        Weimob weimob = selectByCorpId("C10116");
        String access_token = weimob.getAccess_token();
        String refresh_access_token = weimob.getRefresh_access_token();
        Date last_time = Common.DATETIME_FORMAT.parse(weimob.getLast_time());
        Date last_time_refresh = Common.DATETIME_FORMAT.parse(weimob.getLast_time_refresh());

        long timediff = (now.getTime() - last_time.getTime());
        if (timediff>3000000){
            access_token = refreshAccessToken(client_id,client_secret ,refresh_access_token);
        }
        return access_token;
    }

    public JSONArray getList(String accessToken, int rowno) throws Exception{

        int pagesize = 10;
        int pageno = rowno/pagesize +1;
//        JSONObject obj = spuFullInfoGet(accessToken, 1, 1, pagesize, false);
//        String data = obj.get("data").toString();
//        JSONObject object = JSONObject.parseObject(data);
//        String count = object.get("page_count").toString();

        JSONArray array = new JSONArray();
//        if(pageno<=Integer.parseInt(count)){
            JSONObject spuFull = spuFullInfoGet(accessToken, 1, pageno, pagesize, false);
            String dataSpuFull = spuFull.get("data").toString();
            JSONObject objectSpuFull = JSONObject.parseObject(dataSpuFull);
            String dataList = objectSpuFull.get("page_data").toString();
            JSONArray pageData = JSONArray.parseArray(dataList);
            for (int i = 0; i < pageData.size(); i++) {
                JSONObject param = new JSONObject();
                JSONObject obj_spu = pageData.getJSONObject(i).getJSONObject("spu");
                String code = pageData.getJSONObject(i).getJSONObject("spu").get("spu_id").toString();
//                String image = pageData.getJSONObject(i).getJSONObject("spu").get("default_img").toString();
//                String title = pageData.getJSONObject(i).getJSONObject("spu").get("spu_name").toString();
//                String price = pageData.getJSONObject(i).getJSONObject("spu").get("low_sellprice").toString();
                String url = "http://55757490.m.weimob.com/vshop/Goods/GoodsDetail1/"+code;
                param.put("id", code);
                param.put("photo", obj_spu.get("default_img").toString());
                param.put("name", obj_spu.get("spu_name").toString());
                param.put("finalPrice", obj_spu.get("low_sellprice").toString());
                param.put("url", url);
                param.put("shareUrl", url);
                param.put("isNew", "");
                param.put("isHot", "");
                param.put("classifyID", obj_spu.get("classify_ids").toString());
                //		logger.debug("list ->" + param);
                array.add(param);
            }

//        }
        return array;
    }

    public JSONArray getSearchClassify(String accessToken,String xx) throws Exception {
        JSONObject obj = spuFullInfoGet(accessToken, 1, 1, 1, false);
        String data1 = obj.get("data").toString();
        JSONObject data = JSONObject.parseObject(data1);

//        JSONObject data = spuFullInfoGet(accessToken, 1, 1, 10, false).getJSONObject("data");
        int rowCount = Integer.parseInt(data.get("row_count").toString());

        int circle = rowCount/180;
        if (rowCount%180 > 0){
            circle = circle + 1;
        }
        JSONArray page_data = new JSONArray();
        for (int i = 1; i < circle+1; i++) {
            JSONObject obj1 = spuFullInfoGet(accessToken, 1, i, 180, false);
            String data2 = obj1.get("data").toString();
            JSONObject data_obj = JSONObject.parseObject(data2);
            String page = data_obj.get("page_data").toString();
            JSONArray page_data1 = JSONArray.parseArray(page);
            page_data.addAll(page_data1);
        }
//        JSONArray page_data = spuFullInfoGet(accessToken, 1, 1, rowCount, false).getJSONObject("data").getJSONArray("page_data");

        JSONArray array = new JSONArray();
        for (int i = 0; i < page_data.size(); i++) {
            JSONObject obj_spu = page_data.getJSONObject(i).getJSONObject("spu");
            String classify = obj_spu.get("classify_ids").toString();
            if(classify.contains(xx)){
                JSONObject param = new JSONObject();
                String code = obj_spu.get("spu_id").toString();
//                String image = obj_spu.get("default_img").toString();
//                String title = obj_spu.get("spu_name").toString();
//                String price = obj_spu.get("low_sellprice").toString();
//                String classifyID = obj_spu.get("classify_ids").toString();
                String url = "http://55757490.m.weimob.com/vshop/Goods/GoodsDetail1/"+code;
                param.put("id", code);
                param.put("photo", obj_spu.get("default_img").toString());
                param.put("name", obj_spu.get("spu_name").toString());
                param.put("finalPrice", obj_spu.get("low_sellprice").toString());
                param.put("url", url);
                param.put("shareUrl", url);
                param.put("isNew", "");
                param.put("isHot", "");
                param.put("classifyID", obj_spu.get("classify_ids").toString());

                logger.debug("search Classify ->" + param);
                array.add(param);
            }
        }
        return array;
    }

    public JSONArray getSearchTitle(String accessToken,String xx) throws Exception{
        JSONObject obj = spuFullInfoGet(accessToken, 1, 1, 1, false);
        String data1 = obj.get("data").toString();
        JSONObject data = JSONObject.parseObject(data1);

        int rowCount = Integer.parseInt(data.get("row_count").toString());

        int circle = rowCount/180;
        if (rowCount%180 > 0){
            circle = circle + 1;
        }
        JSONArray page_data = new JSONArray();
        for (int i = 1; i < circle+1; i++) {
            JSONObject obj1 = spuFullInfoGet(accessToken, 1, i, 180, false);
            String data2 = obj1.get("data").toString();
            JSONObject data_obj = JSONObject.parseObject(data2);
            String page = data_obj.get("page_data").toString();
            JSONArray page_data1 = JSONArray.parseArray(page);
            page_data.addAll(page_data1);
        }
//        JSONArray page_data = spuFullInfoGet(accessToken, 1, 1, rowCount, false).getJSONObject("data").getJSONArray("page_data");
        JSONArray array = new JSONArray();
        for (int i = 0; i < page_data.size(); i++) {
            JSONObject obj_spu = page_data.getJSONObject(i).getJSONObject("spu");

            String spu_name = obj_spu.get("spu_name").toString();
            if(spu_name.contains(xx)){
                JSONObject param = new JSONObject();
                String code = page_data.getJSONObject(i).getJSONObject("spu").get("spu_id").toString();
//                String image = page_data.getJSONObject(i).getJSONObject("spu").get("default_img").toString();
//                String title = page_data.getJSONObject(i).getJSONObject("spu").get("spu_name").toString();
//                String price = page_data.getJSONObject(i).getJSONObject("spu").get("low_sellprice").toString();
//                String classifyID = page_data.getJSONObject(i).getJSONObject("spu").get("classify_ids").toString();
                String url = "http://55757490.m.weimob.com/vshop/Goods/GoodsDetail1/"+code;
                param.put("id", code);
                param.put("id", obj_spu.get("spu_id").toString());
                param.put("photo", obj_spu.get("default_img").toString());
                param.put("name", obj_spu.get("spu_name").toString());
                param.put("finalPrice", obj_spu.get("low_sellprice").toString());
                param.put("url", url);
                param.put("shareUrl", url);
                param.put("isNew", "");
                param.put("isHot", "");
                param.put("classifyID", obj_spu.get("classify_ids").toString());

                logger.debug("search Title ->" + param);
                array.add(param);
            }
        }
        return array;
    }

    //获取商品信息和规格信息
    public JSONObject spuFullInfoGet(String accessToken, int isOnsale, int pageNo, int pageSize, boolean includeDescription) throws Exception {
        JSONObject param = new JSONObject();
        param.put("is_onsale", isOnsale);
        param.put("page_no", pageNo);
        param.put("page_size", pageSize);
        param.put("include_description", includeDescription);
        String url = spuFullUrlHead+accessToken;
        String context = ishowHttpClient.post(url,param);
        JSONObject spuFull = JSONObject.parseObject(context);
//        System.out.println("getSpuFull"+context);
        return spuFull;
    }

    //获取商品分组
    public JSONArray goodsclassifyGet(String accessToken) throws Exception {
        JSONObject param = new JSONObject();

        JSONArray array = new JSONArray();
        param.put("classify_pid", 0);
        param.put("page_no", 1);
        param.put("page_size", 40);
        String url = "https://dopen.weimob.com/api/1_0/wangpu/GoodsClassify/Get?accesstoken="+accessToken;
        JSONObject classify = JSONObject.parseObject(ishowHttpClient.post(url,param));
        JSONArray classifyvalue = classify.getJSONObject("data").getJSONArray("page_data");
        for (int i = 0; i < classifyvalue.size(); i++) {
            JSONObject getparam = new JSONObject();
            JSONObject classify_data = classifyvalue.getJSONObject(i);
            String classify_id = classify_data.get("classify_id").toString();
            String classify_name = classify_data.get("classify_name").toString();
            getparam.put("code", classify_id);
            getparam.put("name", classify_name);

            JSONArray array1 = new JSONArray();
            JSONObject param1 = new JSONObject();
            param1.put("classify_pid", classify_id);
            param1.put("page_no", 1);
            param1.put("page_size", 20);
//            String url = "https://dopen.weimob.com/api/1_0/wangpu/GoodsClassify/Get?accesstoken="+accessToken;
            JSONObject classify1 = JSONObject.parseObject(ishowHttpClient.post(url,param1));
            JSONArray classifyvalue1 = classify1.getJSONObject("data").getJSONArray("page_data");
            for (int j = 0; j < classifyvalue1.size(); j++) {
                JSONObject getparam1 = new JSONObject();
                JSONObject classify_data1 = classifyvalue1.getJSONObject(j);

                String classify_id1 = classify_data1.get("classify_id").toString();
                String classify_name1 = classify_data1.get("classify_name").toString();
                getparam1.put("code", classify_id1);
                getparam1.put("name", classify_name1);
                array1.add(getparam1);
            }
            getparam.put("brandLists", array1);

            array.add(getparam);
        }

//        System.out.println("goodsclassifyGet"+array.toString());
        return array;
    }

//    public JSONArray goodsclassifyGetSon(String accessToken) throws Exception {
//        JSONArray classifySon = new JSONArray();
//        JSONArray array11 = goodsclassifyGet(accessToken);
//        for (int j = 0; j < array11.size(); j++) {
//            JSONArray array = new JSONArray();
//            JSONObject param = new JSONObject();
//            param.put("classify_pid", array11.getJSONObject(j).get("code").toString());
//            param.put("page_no", 1);
//            param.put("page_size", 20);
//            String url = "https://dopen.weimob.com/api/1_0/wangpu/GoodsClassify/Get?accesstoken="+accessToken;
//            JSONObject classify = JSONObject.parseObject(ishowHttpClient.post(url,param));
//            JSONArray classifyvalue = classify.getJSONObject("data").getJSONArray("page_data");
//            for (int i = 0; i < classifyvalue.size(); i++) {
//                JSONObject getparam = new JSONObject();
//                String classify_id = classifyvalue.getJSONObject(i).get("classify_id").toString();
//                String classify_name = classifyvalue.getJSONObject(i).get("classify_name").toString();
//                getparam.put("code", classify_id);
//                getparam.put("name", classify_name);
//                array.add(getparam);
//            }
////            System.out.println("goodsclassifyGet"+array.toString());
//            classifySon.add(array);
//        }
//        return classifySon;
//    }
}
