package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/vip")
public class VIPController {

    private static final Logger logger = Logger.getLogger(VIPController.class);

    String id;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    VipAlbumService vipAlbumService;
    @Autowired
    VipLabelService vipLabelService;
    @Autowired
    VipParamService vipParamService;
    @Autowired
    BaseService baseService;
    @Autowired
    MongoDBClient mongodbClient;


    /**
     * 会员信息
     * 相册+标签
     */
    @RequestMapping(value = "/vipConsumCount", method = RequestMethod.POST)
    @ResponseBody
    public String vipConsumCount(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject obj = new JSONObject();
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();

            List<VipAlbum> vipAlbumList = new ArrayList<VipAlbum>();
            List<VipLabel> vipLabelList = new ArrayList<VipLabel>();
            if (jsonObject.containsKey("type")){
                if (jsonObject.get("type").equals("1")){
                    //相册
                    vipAlbumList = vipAlbumService.selectAlbumByVip(corp_code,vip_id);
                }else if (jsonObject.get("type").equals("2")){
                    //标签
                    vipLabelList = vipLabelService.selectLabelByVip(corp_code,vip_id);
                }
            }else {
                //相册
                vipAlbumList = vipAlbumService.selectAlbumByVip(corp_code, vip_id);
                //标签
                vipLabelList = vipLabelService.selectLabelByVip(corp_code, vip_id);
            }
            obj.put("Album",JSON.toJSONString(vipAlbumList));
            obj.put("Label",JSON.toJSONString(vipLabelList));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(obj.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员信息
     * 会员详细资料+扩展信息+备注
     */
    @RequestMapping(value = "/vipInfo", method = RequestMethod.POST)
    @ResponseBody
    public String vipInfo(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();

            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_vip_id.key, data_vip_id);
            datalist.put(data_corp_code.key, data_corp_code);

            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipDetail", datalist);
            String vip_info = dataBox.data.get("message").value;
            JSONObject vip = JSONObject.parseObject(vip_info);

            String extend_info = "";
            String remark = "";
            JSONArray extend = new JSONArray();

            List<VipParam> vipParams = vipParamService.selectAllParam(corp_code,Common.IS_ACTIVE_Y);
            for (int i = 0; i < vipParams.size(); i++) {
                JSONObject extend_obj = new JSONObject();
                extend_obj.put("name",vipParams.get(i).getParam_desc());
                extend_obj.put("key",vipParams.get(i).getParam_name());
                extend_obj.put("type",vipParams.get(i).getParam_type());
                extend_obj.put("values",vipParams.get(i).getParam_values());
                extend_obj.put("is_must",vipParams.get(i).getRequired());
                extend_obj.put("class",vipParams.get(i).getParam_class());
                extend.add(extend_obj);
            }
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();

            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
            BasicDBObject dbObject=new BasicDBObject();
            dbObject.put("vip_id",vip_id);
            dbObject.put("corp_code",corp_code);
            DBCursor dbCursor= cursor.find(dbObject);

            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                if (obj.containsField("extend"))
                    extend_info = obj.get("extend").toString();
                if (obj.containsField("remark"))
                    remark = obj.get("remark").toString();
            }

            JSONObject result = new JSONObject();
            result.put("list",vip);
            result.put("extend",extend);
            result.put("extend_info",extend_info);
            result.put("remark",remark);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员信息
     * 会员积分记录+衣橱
     */
    @RequestMapping(value = "/vipPoints", method = RequestMethod.POST)
    @ResponseBody
    public String vipPoints(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String store_id = jsonObject.get("store_id").toString();
            String store_code = baseService.storeIdConvertStoreCode(corp_code,store_id);

            Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_store_code = new Data("store_id", store_code, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_vip_id.key, data_vip_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_store_code.key, data_store_code);

            JSONObject result_points = new JSONObject();
            JSONObject result_wardrobes = new JSONObject();

            if (jsonObject.containsKey("type")){
                //获取积分列表
                if (jsonObject.get("type").equals("1")){
//                    if (jsonObject.containsKey("time")){}
                    DataBox dataBox_points = iceInterfaceService.iceInterfaceV2("VipInfoScoreDetail", datalist);
                    logger.info("-------VipInfoScoreDetail:" + dataBox_points.data.get("message").value);
                    String points = dataBox_points.data.get("message").value;
                    result_points = JSONObject.parseObject(points);
                }else if(jsonObject.get("type").equals("2")){
                    //获取衣橱列表
//                    if (jsonObject.containsKey("time")){}
                    DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipMoneyRecord", datalist);
                    logger.info("-------AnalysisVipMoneyRecord:" + dataBox.data.get("message").value);
                    String result = dataBox.data.get("message").value;
                    result_wardrobes = JSONObject.parseObject(result);
                }
            }else {
                //获取积分和衣橱信息
                DataBox dataBox_points = iceInterfaceService.iceInterfaceV2("VipInfoScoreDetail", datalist);
                logger.info("-------VipInfoScoreDetail:" + dataBox_points.data.get("message").value);
                String points = dataBox_points.data.get("message").value;
                result_points = JSONObject.parseObject(points);

                DataBox dataBox_wardrobes = iceInterfaceService.iceInterfaceV2("AnalysisVipMoneyRecord", datalist);
                logger.info("-------AnalysisVipMoneyRecord:" + dataBox_wardrobes.data.get("message").value);
                String wardrobes = dataBox_wardrobes.data.get("message").value;
                result_wardrobes = JSONObject.parseObject(wardrobes);
            }
            JSONObject result = new JSONObject();
            result.put("result_points",result_points);
            result.put("result_consumn",result_wardrobes);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表
     * 筛选
     */
    @RequestMapping(value = "/vipScreen", method = RequestMethod.POST)
    @ResponseBody
    public String vipScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            



            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("");

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员信息
     * 保存mongodb
     */
    @RequestMapping(value = "/vipSaveInfo", method = RequestMethod.POST)
    @ResponseBody
    public String vipSaveInfo(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String card_no = jsonObject.get("card_no").toString();
            String phone = jsonObject.get("phone").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
            Map keyMap = new HashMap();
            keyMap.put("_id", corp_code+card_no);
            BasicDBObject queryCondition = new BasicDBObject();
            queryCondition.putAll(keyMap);
            DBCursor dbCursor1 = cursor.find(queryCondition);
            if (dbCursor1.size()>0){
                //记录存在，更新
                DBObject updateCondition=new BasicDBObject();
                updateCondition.put("_id", corp_code+card_no);

                DBObject updatedValue=new BasicDBObject();
                if (jsonObject.containsKey("extend")) {
                    String extend = jsonObject.get("extend").toString();
                    updatedValue.put("extend", extend);
                }
                if (jsonObject.containsKey("remark")) {
                    String remark = jsonObject.get("remark").toString();
                    updatedValue.put("remark", remark);
                }
                DBObject updateSetValue=new BasicDBObject("$set",updatedValue);
                cursor.update(updateCondition, updateSetValue);
            }else {
                //记录不存在，插入
                DBObject saveData = new BasicDBObject();
                saveData.put("_id", corp_code + card_no);
                saveData.put("vip_id", vip_id);
                saveData.put("corp_code", corp_code);
                saveData.put("card_no", card_no);
                saveData.put("phone", phone);
                saveData.put("corp_code", corp_code);
                if (jsonObject.containsKey("extend")) {
                    String extend = jsonObject.get("extend").toString();
                    saveData.put("extend", extend);
                }
                if (jsonObject.containsKey("remark")) {
                    String remark = jsonObject.get("remark").toString();
                    saveData.put("remark", remark);
                }
                cursor.save(saveData);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("save success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }



    //会员积分列表
    @RequestMapping(value = "/allVipPointsRecord", method = RequestMethod.POST)
    @ResponseBody
    public String allVipPointsRecord(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            Map datalist = iceInterfaceService.vipBasicMethod(jsonObject,request);
            DataBox dataBox = iceInterfaceService.iceInterface("VipDetailQuery", datalist);
            logger.info("-------会员积分" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}
