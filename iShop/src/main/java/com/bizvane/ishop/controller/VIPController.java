package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
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
    VipGroupService vipGroupService;
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
            String avatar = "";
            String vip_group_code = "";
            String vip_group_name = "";

            JSONArray extend = new JSONArray();

            List<VipParam> vipParams = vipParamService.selectParamByCorp(corp_code);
            for (int i = 0; i < vipParams.size(); i++) {
                JSONObject extend_obj = new JSONObject();
                extend_obj.put("name",vipParams.get(i).getParam_desc());
                extend_obj.put("key",vipParams.get(i).getParam_name());
                extend_obj.put("type",vipParams.get(i).getParam_type());
                extend_obj.put("values",vipParams.get(i).getParam_values());
                extend_obj.put("is_must",vipParams.get(i).getRequired());
                extend_obj.put("class",vipParams.get(i).getParam_class());
                extend_obj.put("show_order",vipParams.get(i).getShow_order());
                extend.add(extend_obj);
            }

//            List<VipGroup> vipGroups = vipGroupService.selectCorpVipGroups(corp_code);
//            if (vipGroups.size()>0){
//                String values = "";
//                for (int i = 0; i < vipGroups.size(); i++) {
//                    values = values + vipGroups.get(i).getVip_group_name()+",";
//                }
//                JSONObject extend_obj = new JSONObject();
//                extend_obj.put("name","会员分组");
//                extend_obj.put("key","vip_group");
//                extend_obj.put("type","select");
//                extend_obj.put("values",values);
//                extend_obj.put("is_must","Y");
//                extend_obj.put("class","");
//                extend_obj.put("show_order","0");
//                extend.add(extend_obj);
//            }
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
                if (obj.containsField("avatar"))
                    avatar = obj.get("avatar").toString();
                if (obj.containsField("vip_group_code"))
                    vip_group_code = obj.get("vip_group_code").toString();
            }

            if (!vip_group_code.equals("")){
                VipGroup vipGroup = vipGroupService.getVipGroupByCode(corp_code,vip_group_code,Common.IS_ACTIVE_Y);
                if (vipGroup != null){
                    vip_group_name = vipGroup.getVip_group_name();
                }
            }
            vip.put("vip_avatar",avatar);
            vip.put("vip_group_name",vip_group_name);

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

            String goods_code = "";
            String goods_name = "";
            String order_id = "";
            String time_start = "";
            String time_end = "";

            if (jsonObject.containsKey("goods_code")){
                goods_code = jsonObject.get("goods_code").toString();
            }
            if (jsonObject.containsKey("goods_name")){
                goods_name = jsonObject.get("goods_name").toString();
            }
            if (jsonObject.containsKey("order_id")){
                order_id = jsonObject.get("order_id").toString();
            }
            if (jsonObject.containsKey("time_start")){
                time_start = jsonObject.get("time_start").toString();
            }
            if (jsonObject.containsKey("time_end")){
                time_end = jsonObject.get("time_end").toString();
            }
            Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_store_code = new Data("store_id", store_code, ValueType.PARAM);
            Data data_goods_code = new Data("good_code", goods_code, ValueType.PARAM);
            Data data_goods_name = new Data("good_name", goods_name, ValueType.PARAM);
            Data data_order_id = new Data("order_id", order_id, ValueType.PARAM);
            Data data_time_start = new Data("time_start", time_start, ValueType.PARAM);
            Data data_time_end = new Data("time_end", time_end, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_vip_id.key, data_vip_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_store_code.key, data_store_code);
            datalist.put(data_goods_code.key, data_goods_code);
            datalist.put(data_goods_name.key, data_goods_name);
            datalist.put(data_order_id.key, data_order_id);
            datalist.put(data_time_start.key, data_time_start);
            datalist.put(data_time_end.key, data_time_end);

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
     * 会员列表（目前只支持姓名，手机号，会员卡号）
     * 搜索
     */
    @RequestMapping(value = "/vipSearch", method = RequestMethod.POST)
    @ResponseBody
    public String vipSearch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            if (role_code.equals(Common.ROLE_SYS)){
                corp_code = jsonObject.get("corp_code").toString();
            }
            String search_value = jsonObject.get("searchValue").toString();
            logger.info("json--------------corp_code-" + corp_code);

            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_search_value = new Data("phone_or_id", search_value, ValueType.PARAM);
            Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
            Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_search_value.key, data_search_value);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_page_num.key, data_page_num);
            datalist.put(data_page_size.key, data_page_size);

            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipSearch", datalist);
            logger.info("-------VipSearch:" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            JSONObject obj = JSON.parseObject(result);
            String vipLists = obj.get("all_vip_list").toString();
            JSONArray array = JSONArray.parseArray(vipLists);
            JSONArray new_array = vipGroupService.findVipsGroup(array);
            obj.put("all_vip_list",new_array);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
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
     * 会员列表（目前只支持导购店铺）
     * 筛选
     */
    @RequestMapping(value = "/vipScreen", method = RequestMethod.POST)
    @ResponseBody
    public String vipScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String user_code = jsonObject.get("user_code").toString();
            String store_code = jsonObject.get("store_code").toString();
            String area_code = jsonObject.get("area_code").toString();
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();

            if (role_code.equals(Common.ROLE_SYS)){
                corp_code = jsonObject.get("corp_code").toString();
            }
            logger.info("json--------------corp_code-" + corp_code);
            DataBox dataBox = null;
            if (user_code.equals("")) {
                if (!store_code.equals("")) {
                    role_code = Common.ROLE_SM;
                }else {
                    role_code = Common.ROLE_AM;
                }
                Data data_user_id = new Data("user_id", user_code, ValueType.PARAM);
                Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                Data data_role_code = new Data("role_code", role_code, ValueType.PARAM);
                Data data_store_id = new Data("store_id", store_code, ValueType.PARAM);
                Data data_area_code = new Data("area_code", area_code, ValueType.PARAM);
                Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
                Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);

                Map datalist = new HashMap<String, Data>();
                datalist.put(data_user_id.key, data_user_id);
                datalist.put(data_corp_code.key, data_corp_code);
                datalist.put(data_store_id.key, data_store_id);
                datalist.put(data_area_code.key, data_area_code);
                datalist.put(data_role_code.key, data_role_code);
                datalist.put(data_page_num.key, data_page_num);
                datalist.put(data_page_size.key, data_page_size);

                dataBox = iceInterfaceService.iceInterfaceV2("AnalysisAllVip", datalist);
            }else {
                Data data_user_id = new Data("user_id", user_code, ValueType.PARAM);
                Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
                Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);

                Map datalist = new HashMap<String, Data>();
                datalist.put(data_user_id.key, data_user_id);
                datalist.put(data_corp_code.key, data_corp_code);
                datalist.put(data_page_num.key, data_page_num);
                datalist.put(data_page_size.key, data_page_size);

                dataBox = iceInterfaceService.iceInterfaceV2("AnalysisEmpsVip", datalist);
            }
            logger.info("-------VipSearch:" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            JSONObject obj = JSON.parseObject(result);
            String vipLists = obj.get("all_vip_list").toString();
            JSONArray array = JSONArray.parseArray(vipLists);
            JSONArray new_array = vipGroupService.findVipsGroup(array);
            obj.put("all_vip_list",new_array);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
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
     * 会员信息(头像，扩展信息，备注)
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
                if (jsonObject.containsKey("extend") && !jsonObject.get("extend").toString().equals("")) {
                    String extend = jsonObject.get("extend").toString();
                    updatedValue.put("extend", extend);
                }
                if (jsonObject.containsKey("remark")) {
                    String remark = jsonObject.get("remark").toString();
                    updatedValue.put("remark", remark);
                }
                if (jsonObject.containsKey("avatar") && !jsonObject.get("avatar").toString().equals("")) {
                    String avatar = jsonObject.get("avatar").toString();
                    updatedValue.put("avatar", avatar);
                }
                if (jsonObject.containsKey("vip_group_code") && !jsonObject.get("vip_group_code").toString().equals("")) {
                    String vip_group_code = jsonObject.get("vip_group_code").toString();
                    updatedValue.put("vip_group_code", vip_group_code);
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
                if (jsonObject.containsKey("avatar")) {
                    String avatar = jsonObject.get("avatar").toString();
                    saveData.put("avatar", avatar);
                }
                if (jsonObject.containsKey("vip_group_code")) {
                    String vip_group_code = jsonObject.get("vip_group_code").toString();
                    saveData.put("vip_group_code", vip_group_code);
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

}
