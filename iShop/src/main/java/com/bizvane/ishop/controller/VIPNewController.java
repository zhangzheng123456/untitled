package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.*;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.mongodb.*;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
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
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/vipNew")
public class VIPNewController {

    private static final Logger logger = Logger.getLogger(VIPNewController.class);

    String id;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    VipLabelService vipLabelService;
    @Autowired
    VipParamService vipParamService;
    @Autowired
    StoreService storeService;
    @Autowired
    UserService userService;
    @Autowired
    ParamConfigureService paramConfigureService;
    @Autowired
    CorpParamService corpParamService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    VipRulesService vipRulesService;
    @Autowired
    VipGroupService vipGroupService;
    @Autowired
    VipService vipService;
    @Autowired
    CRMInterfaceService crmInterfaceService;
    @Autowired
    private BaseService baseService;
    @Autowired
    VipRecordService vipRecordService;


    /**
     * 会员信息
     * 会员详细资料+扩展信息+备注
     */
    @RequestMapping(value = "/vipInfo", method = RequestMethod.POST)
    @ResponseBody
    public String vipInfo(HttpServletRequest request) {
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String phone = jsonObject.get("phone").toString();
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();

            String extend_info = "";
            JSONArray extend = new JSONArray();
            List<VipParam> vipParams = vipParamService.selectParamByCorp(corp_code);
            String cust_col = "";
            for (int i = 0; i < vipParams.size(); i++) {
                cust_col = cust_col + vipParams.get(i).getParam_name() + ",";
                JSONObject extend_obj = new JSONObject();
                extend_obj.put("name", vipParams.get(i).getParam_desc());
                extend_obj.put("key", vipParams.get(i).getParam_name());
                extend_obj.put("type", vipParams.get(i).getParam_type());
                extend_obj.put("values", vipParams.get(i).getParam_values());
                extend_obj.put("is_must", vipParams.get(i).getRequired());
                extend_obj.put("class", vipParams.get(i).getParam_class());
                extend_obj.put("show_order", vipParams.get(i).getShow_order());
                extend.add(extend_obj);
            }


            String month = "12";
            if (corp_code.equals("C10016"))
                month = "3";

            String protect_points = "--";
            DataBox dataBox = iceInterfaceService.getVipInfoByPhone(corp_code,phone,cust_col,month);
            if (dataBox.status.toString().equals("SUCCESS")){
                String vip_info = dataBox.data.get("message").value;
                JSONArray vip_array = JSONArray.parseArray(vip_info);
                JSONArray new_vip = new JSONArray();
                for (int i = 0; i < vip_array.size(); i++) {
                    JSONObject vip = vip_array.getJSONObject(i);
                    vip.put("month", month);
                    if (vip.containsKey("total_amount") && NumberUtils.isNumber(vip.getString("total_amount"))){
                        vip.put("total_amount", NumberUtil.keepPrecision(vip.getString("total_amount")));
                    }
                    if (vip.containsKey("consume_times")){
                        vip.put("consume_times", vip.getString("consume_times").split("\\.")[0]);
                    }
                    //保护积分
                    if (vip.containsKey("protect_points")){
                        protect_points = vip.get("protect_points").toString();
                    }
                    vip.put("protect_points", protect_points);
                    //拓展信息
                    if (vip.containsKey("custom") || !vip.get("custom").toString().equals("null")){
                        extend_info = vip.get("custom").toString();
                        vip.remove("custom");
                    }
                    if (vip.containsKey("province")){
                        String province = vip.get("province").toString();
                        if (province.equals("null") || province.equals("")){
                            String store_code = vip.get("store_code").toString();
                            Store store = storeService.getStoreByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
                            if (store != null && store.getProvince() != null){
                                vip.put("province", store.getProvince());
                                vip.put("city", store.getCity());
                                vip.put("area", store.getArea());
                                vip.put("address", store.getStreet());
                            }
                        }
                    }
                    JSONObject result = new JSONObject();
                    result.put("list", vip);
                    result.put("extend", extend);
                    result.put("extend_info", extend_info);
                    new_vip.add(result);
                }
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(new_vip.toJSONString());
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("信息错误");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员信息
     * 相册+标签+备忘
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

//            List<VipAlbum> vipAlbumList = new ArrayList<VipAlbum>();
            List<VipLabel> vipLabelList = new ArrayList<VipLabel>();
            String album = "[]";
            String memo = "[]";

            DBCursor dbCursor = vipService.findVipByMongo(corp_code, vip_id);
            if (jsonObject.containsKey("type")) {
                if (jsonObject.get("type").equals("1")) {
                    //相册
                    while (dbCursor.hasNext()) {
                        DBObject vip = dbCursor.next();
                        if (vip.containsField("album")) {
                            album = obj.get("album").toString();
                        }
                    }
                } else if (jsonObject.get("type").equals("2")) {
                    //标签
                   // vipLabelList = vipLabelService.selectLabelByVip(corp_code, vip_id);
                    vipLabelList = vipLabelService.selectLabelByVipToHbase(corp_code, vip_id);

                } else if (jsonObject.get("type").equals("3")) {
                    //备忘
                    while (dbCursor.hasNext()) {
                        DBObject vip = dbCursor.next();
                        if (vip.containsField("memo")) {
                            memo = vip.get("memo").toString();
                        }
                    }
                }
            } else {
                //相册
                while (dbCursor.hasNext()) {
                    DBObject vip = dbCursor.next();
                    if (vip.containsField("album")) {
                        album = vip.get("album").toString();
                    }
                    if (vip.containsField("memo")) {
                        memo = vip.get("memo").toString();
                    }
                }
                //标签
              //  vipLabelList = vipLabelService.selectLabelByVip(corp_code, vip_id);
                  vipLabelList = vipLabelService.selectLabelByVipToHbase(corp_code, vip_id);

            }
            obj.put("Album", album);
            obj.put("Memo", memo);
            obj.put("Label", JSON.toJSONString(vipLabelList));
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
    @RequestMapping(value = "/vipRemark", method = RequestMethod.POST)
    @ResponseBody
    public String vipRemark(HttpServletRequest request) {
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();

            String extend_info = "";
            String remark = "";
            String avatar = "";
            JSONArray extend = new JSONArray();
            List<VipParam> vipParams = vipParamService.selectParamByCorp(corp_code);
            String cust_col = "";
            for (int i = 0; i < vipParams.size(); i++) {
                cust_col = cust_col + vipParams.get(i).getParam_name() + ",";
                JSONObject extend_obj = new JSONObject();
                extend_obj.put("name", vipParams.get(i).getParam_desc());
                extend_obj.put("key", vipParams.get(i).getParam_name());
                extend_obj.put("type", vipParams.get(i).getParam_type());
                extend_obj.put("values", vipParams.get(i).getParam_values());
                extend_obj.put("is_must", vipParams.get(i).getRequired());
                extend_obj.put("class", vipParams.get(i).getParam_class());
                extend_obj.put("show_order", vipParams.get(i).getShow_order());
                extend.add(extend_obj);
            }

            DBCursor dbCursor = vipService.findVipByMongo(corp_code,vip_id);
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                if (obj.containsField("remark"))
                    remark = obj.get("remark").toString();
                if (obj.containsField("avatar"))
                    avatar = obj.get("avatar").toString();
            }

            String month = "12";
            if (corp_code.equals("C10016"))
                month = "3";

            String protect_points = "--";
            DataBox dataBox = iceInterfaceService.getVipInfo(corp_code,vip_id,cust_col,month);
            if (dataBox.status.toString().equals("SUCCESS")){
                String vip_info = dataBox.data.get("message").value;
                JSONObject vip = JSONObject.parseObject(vip_info);
                vip.put("vip_avatar", avatar);
                vip.put("month", month);
                if (vip.containsKey("total_amount") && NumberUtils.isNumber(vip.getString("total_amount"))){
                    vip.put("total_amount", NumberUtil.keepPrecision(vip.getString("total_amount")));
                }
                if (vip.containsKey("consume_times")){
                    vip.put("consume_times", vip.getString("consume_times").split("\\.")[0]);
                }
                //保护积分
                if (vip.containsKey("protect_points")){
                    protect_points = vip.get("protect_points").toString();
                }
                vip.put("protect_points", protect_points);
                //拓展信息
                if (vip.containsKey("custom") || !vip.get("custom").toString().equals("null")){
                    extend_info = vip.get("custom").toString();
                    vip.remove("custom");
                }
                if (vip.containsKey("province")){
                    String province = vip.get("province").toString();
                    if (province.equals("null") || province.equals("")){
                        String store_code = vip.get("store_code").toString();
                        Store store = storeService.getStoreByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
                        if (store != null && store.getProvince() != null){
                            vip.put("province", store.getProvince());
                            vip.put("city", store.getCity());
                            vip.put("area", store.getArea());
                            vip.put("address", store.getStreet());
                        }
                    }
                }
                JSONObject result = new JSONObject();
                result.put("list", vip);
                result.put("extend", extend);
                result.put("extend_info", extend_info);
                result.put("remark", remark);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result.toString());
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("信息错误");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
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
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            String search_value = jsonObject.get("searchValue").toString();
            logger.info("json-----555555555555555555---------corp_code-" + corp_code);

            Map datalist = iceInterfaceService.vipBasicMethod2(page_num, page_size,corp_code, request,sort_key,sort_value);
            Data data_search_value = new Data("phone_or_id", search_value, ValueType.PARAM);
            datalist.put(data_search_value.key, data_search_value);
            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipSearch3", datalist);
            String result = dataBox.data.get("message").value;

            String field = "";
            if (jsonObject.containsKey("field")){
                field = jsonObject.getString("field");
            }
            if (field.equals("fsend")){
                result = vipService.vipLastSendTime(corp_code,result);
            }else {
                result = vipService.vipAvatar(corp_code,result);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result);
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
    @RequestMapping(value = "/newScreen", method = RequestMethod.POST)
    @ResponseBody
    public String newScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String screens = jsonObject.get("screen").toString();
            JSONArray screen = JSONArray.parseArray(screens);

            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            DataBox dataBox = vipGroupService.vipScreenBySolrNew(screen,corp_code,page_num,page_size,role_code,brand_code,area_code,store_code,user_code,sort_key,sort_value);
            if (dataBox.status.toString().equals("SUCCESS")){
                String result = dataBox.data.get("message").value;
                String field = "";
                if (jsonObject.containsKey("field")){
                    field = jsonObject.getString("field");
                }
                if (field.equals("fsend")){
                    result = vipService.vipLastSendTime(corp_code,result);
                }else {
                    result = vipService.vipAvatar(corp_code,result);
                }
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result);
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("筛选失败");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_corp_code = request.getSession().getAttribute("corp_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            JSONObject jsonObj2 = JSONObject.parseObject(param);
            String output_message = jsonObj2.get("message").toString();
            JSONObject output_message_object = JSONObject.parseObject(output_message);
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(output_message_object);

            String corp_code = user_corp_code;
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = "C10000";
            }
            logger.info("json--------------corp_code-" + corp_code);

            String jlist = jsonObject.get("tablemanager").toString();
            JSONArray array = JSONArray.parseArray(jlist);
            String cust_cols = "";
            for (int i = 0; i < array.size(); i++) {
                JSONObject json = array.getJSONObject(i);
                String column_name = json.getString("column_name");
                if (column_name.startsWith("CUST_"))
                    cust_cols = cust_cols + column_name + ",";
                if (column_name.equals("user_name"))
                    cust_cols = cust_cols + "user_code,";
                if (column_name.equals("store_name"))
                    cust_cols = cust_cols + "store_code,";
            }

            JSONObject result2 = new JSONObject();

            String pathname = "";
            JSONArray jsonArray;
            if (jsonObject.containsKey("ids") && !jsonObject.get("ids").equals("")){
                String ids = jsonObject.get("ids").toString().trim();

                //根据vip_id，获取会员资料(包括拓展信息)
                Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                Data data_vip_id = new Data("vip_ids", ids, ValueType.PARAM);
                Data data_cust_cols = new Data("cust_cols", cust_cols, ValueType.PARAM);

                Map datalist = new HashMap<String, Data>();
                datalist.put(data_corp_code.key, data_corp_code);
                datalist.put(data_vip_id.key, data_vip_id);
                datalist.put(data_cust_cols.key, data_cust_cols);

                DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetVipExtendInfo", datalist);

                String result = dataBox.data.get("message").value;
                JSONObject object = JSONObject.parseObject(result);
                jsonArray = JSONArray.parseArray(object.get("message").toString());

                List list = WebUtils.Json2List2(jsonArray);
                pathname = OutExeclHelper.OutExecl_vip(jsonArray, list, map, response, request,"会员档案");
                result2.put("path", JSON.toJSONString("lupload/" + pathname));

            }else {
                String page_num = jsonObject.getString("page_num");
                String page_size = jsonObject.getString("page_size");

                DataBox dataBox = null;
                String screen_message = jsonObject.get("screen_message").toString();
                String searchValue = jsonObject.get("searchValue").toString().trim();
                if (!searchValue.equals("")) {
                    Map datalist = iceInterfaceService.vipBasicMethod2(page_num,page_size,corp_code,request,sort_key,sort_value);
                    Data data_search_value = new Data("phone_or_id", searchValue, ValueType.PARAM);
                    datalist.put(data_search_value.key, data_search_value);
                    dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipSearch3", datalist);
                    String result = dataBox.data.get("message").value;
                    JSONObject object = JSONObject.parseObject(result);
                    jsonArray = JSONArray.parseArray(object.get("all_vip_list").toString());
                    int count = object.getInteger("count");

                    int pageNum = Integer.parseInt(page_num);
                    int pageSize = Integer.parseInt(page_size);
                    int start_line = (pageNum-1) * pageSize + 1;
                    int end_line = pageNum*pageSize;
                    if (count < pageNum*pageSize)
                        end_line = count;
                    List list = WebUtils.Json2List2(jsonArray);
                    pathname = OutExeclHelper.OutExecl_vip(jsonArray, list, map, response, request,"会员档案("+start_line+"-"+end_line+")");
                    result2.put("path", JSON.toJSONString("lupload/" + pathname));

                }else{

                    String columnName = "";
                    String showName = "";
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject json = array.getJSONObject(i);
                        columnName += json.get("column_name").toString()+",";
                        showName += json.get("show_name").toString()+",";
                    }
                    if (columnName.endsWith(","))
                        columnName = columnName.substring(0,columnName.length()-1);
                    if (showName.endsWith(","))
                        showName = showName.substring(0,showName.length()-1);
                    JSONArray post_array = new JSONArray();
                    if(screen_message.equals("") ){
                        post_array = vipGroupService.vipScreen2ArrayNew(new JSONArray(),corp_code,role_code,brand_code,area_code,store_code,user_code);

                    }else if(!screen_message.equals("")){
                        JSONArray screen = JSON.parseArray(screen_message);
                        post_array = vipGroupService.vipScreen2ArrayNew(screen,corp_code,role_code,brand_code,area_code,store_code,user_code);
                    }
                    dataBox = iceInterfaceService.vipScreen2ExeclMethod(page_num,page_size,corp_code,JSON.toJSONString(post_array),sort_key,sort_value,cust_cols,columnName,showName,user_corp_code+"&&"+user_code);
                    String result = dataBox.data.get("message").value;
                    JSONObject object = JSONObject.parseObject(result);
                    pathname = object.get("oss_url").toString();
                    result2.put("path", object.get("oss_url").toString());
                    result2.put("path_type","oss");
                }

            }
            logger.info("=======cust_cols:"+cust_cols);

            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result2.toString());
        }catch (Ice.MemoryLimitException im){
            System.out.println("===============ice异常========================================");
            errormessage = "导出数据过大,请筛选后导出";
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
            im.printStackTrace();
        }catch (Exception ex) {
            System.out.println("===============总异常========================================");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }



    /**
     * 收藏夹
     */
    @RequestMapping(value = "/avorites", method = RequestMethod.POST)
    @ResponseBody
    public String avoritesMethod(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String open_id = jsonObject.get("open_id").toString();
            String vip_card_no = jsonObject.get("vip_card_no").toString();
            String app_id = jsonObject.get("app_id").toString();

            DataBox dataBox = iceInterfaceService.favorites(corp_code,app_id,open_id,vip_card_no);

            String result = dataBox.data.get("message").value;
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }

    /**
     * 购物车
     */
    @RequestMapping(value = "/shoppingCarts", method = RequestMethod.POST)
    @ResponseBody
    public String shoppingCart(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String vip_id = jsonObject.get("vip_id").toString();
            String page_num = jsonObject.get("page_num").toString();
            String page_size = jsonObject.get("page_size").toString();

            JSONObject object = new JSONObject();
            DataBox dataBox = iceInterfaceService.shoppingCart(corp_code,vip_id,page_size,page_num);
            if (dataBox.status.toString().equals("SUCCESS")){
                object = JSONObject.parseObject(dataBox.data.get("message").value);
                JSONArray list = object.getJSONArray("list");

                JSONArray new_list = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    JSONObject oo = list.getJSONObject(i);
                    Iterator it = oo.keySet().iterator();

                    JSONObject new_oo = new JSONObject();
                    while (it.hasNext()){
                        String key = it.next().toString();
                        new_oo.put(key.replace(";","_"),oo.getString(key));
                    }
                    new_list.add(new_oo);
                }
                object.put("list",new_list);
            }

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(object.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }

    /**
     * 回访反馈
     */
    @RequestMapping(value = "/visitAndFeedback", method = RequestMethod.POST)
    @ResponseBody
    public String visitAndFeedback(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_back_record);
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String vip_id = jsonObject.get("vip_id").toString();
            int page_num = jsonObject.getInteger("page_num");
            int page_size = jsonObject.getInteger("page_size");

            String lists = jsonObject.get("list").toString();

            JSONArray screen_array = JSONArray.parseArray(lists);
            BasicDBObject queryCondition = vipRecordService.getScreen(screen_array,corp_code);

            BasicDBObject dbObject = new BasicDBObject();
            dbObject.put("corp_code",corp_code);
            dbObject.put("vip_id",vip_id);

            BasicDBObject dbObject1 = new BasicDBObject();
            dbObject1.put("CORP_ID",corp_code);
            dbObject1.put("VIP_ID",vip_id);

            BasicDBList list = new BasicDBList();
            list.add(dbObject);
            list.add(dbObject1);

            queryCondition.put("$or",list);

            logger.info("========visitAndFeedback====queryCondition="+queryCondition.toJson());
            DBCursor dbCursor1 = cursor.find(queryCondition);
            int total = dbCursor1.count();
            int pages = MongoUtils.getPages(dbCursor1, page_size);
            DBCursor dbCursor = MongoUtils.sortAndPage(dbCursor1, page_num, page_size, "created_date", -1);

            JSONObject result = new JSONObject();
            JSONArray array = vipRecordService.transFeedBack(dbCursor,corp_code);
            result.put("list", array);
            result.put("pages", pages);
            result.put("page_number", page_num);
            result.put("page_size", page_size);
            result.put("total", total);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }


    /**
     * 优惠券
     */
    @RequestMapping(value = "/coupon", method = RequestMethod.POST)
    @ResponseBody
    public String couponMethod(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String app_id = jsonObject.get("app_id").toString();
            String open_id = jsonObject.get("open_id").toString();
            String phone = jsonObject.get("phone").toString();
            String type = jsonObject.get("type").toString();
            String vip_id=jsonObject.getString("vip_id");

            JSONArray array = new JSONArray();
            String result = array.toString();
            if (!open_id.equals("") && !open_id.equals("null") && !open_id.equals("无") &&
                    !app_id.equals("") && !app_id.equals("null") && !app_id.equals("无")){
                DataBox dataBox = iceInterfaceService.coupon(corp_code,vip_id,app_id,open_id,phone,type);
                if (dataBox.status.toString().equals("SUCCESS"))
                    result = dataBox.data.get("message").value;
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }



    //会员图表获取，排序
    @RequestMapping(value = "/vipChart", method = RequestMethod.POST)
    @ResponseBody
    public String vipChart(HttpServletRequest request) {
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

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();

            String query_type = jsonObject.get("type").toString();
            String year = jsonObject.get("year").toString();

            String result="";
            DataBox dataBox = new DataBox();
            String brand_code1 = "";
            if (query_type.equals("analysis")){
                //会员分析
                String store_label = jsonObject.get("store_label").toString();

                String area_code = "";
                String store_id = "";
                if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)) {
                    store_id = jsonObject.get("store_code").toString().trim();
                    area_code = jsonObject.get("area_code").toString().trim();
                    String brand_code = jsonObject.get("brand_code").toString().trim();
                    if (store_id.equals("")) {
                        if (!area_code.equals("") || !brand_code.equals("")){
                            List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "","");
                            for (int i = 0; i < storeList.size(); i++) {
                                store_id = store_id + storeList.get(i).getStore_code() + ",";
                            }
                            if (!brand_code.isEmpty()){
                                brand_code1 = brand_code;
                            }
                        }

                    }
                }else if (role_code.equals(Common.ROLE_AM)){
                    store_id = jsonObject.get("store_code").toString().trim();
                    area_code = jsonObject.get("area_code").toString().trim();
                    String brand_code = jsonObject.get("brand_code").toString().trim();
                    if (!brand_code.isEmpty()){
                        brand_code1 = brand_code;
                    }
                    String area_store_code = "";
                    if (store_id.equals("")){
                        if (area_code.equals("")){
                            area_code = request.getSession().getAttribute("area_code").toString();
                            area_code = area_code.replace(Common.SPECIAL_HEAD,"");
                            area_store_code = request.getSession().getAttribute("store_code").toString();
                            area_store_code = area_store_code.replace(Common.SPECIAL_HEAD,"");
                        }
                        List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "",area_store_code);
                        for (int i = 0; i < storeList.size(); i++) {
                            store_id = store_id + storeList.get(i).getStore_code() + ",";
                        }
                    }
                }else {
                    store_id = jsonObject.get("store_code").toString().trim();
                    if (store_id.equals("")) {
                        area_code = jsonObject.get("area_code").toString().trim();
                        String brand_code = jsonObject.get("brand_code").toString().trim();
                        if (!brand_code.isEmpty()){
                            brand_code1 = brand_code;
                        }
                        List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "","");
                        for (int i = 0; i < storeList.size(); i++) {
                            store_id = store_id + storeList.get(i).getStore_code() + ",";
                        }
                    }
                }
                if (corp_code.equals("C10222") && !store_id.isEmpty()){
                    Store store = storeService.getStoreByCode(corp_code,store_id.split(",")[0],Common.IS_ACTIVE_Y);
                    if (store != null && store.getBrand_code() != null && !store.getBrand_code().isEmpty())
                        brand_code1 = store.getBrand_code().replace(Common.SPECIAL_HEAD,"").split(",")[0];
                }

                dataBox = iceInterfaceService.storeSearchForWeb(corp_code,store_id,store_label,brand_code1,year);
                if (dataBox.status.toString().equals("SUCCESS")) {
                    result = dataBox.data.get("message").value;
                    if (result.equals("{}")){
                        result = "";
                    }else{
                        JSONObject object = JSONObject.parseObject(result);
                        object = vipService.pareseData1(object);
                        result = object.toString();

                    }
                }
            }else if (query_type.equals("vipInfo")){
                //会员档案
                String vip_id = jsonObject.get("vip_id").toString();
                dataBox = iceInterfaceService.vipTagSearchForWeb(corp_code, vip_id, "",year,"","");
                if (dataBox.status.toString().equals("SUCCESS")) {
//                    logger.info("----------vipChart:" + dataBox.data.get("message").value);
                    result = dataBox.data.get("message").value;
                    JSONObject object = JSON.parseObject(result);
                    JSONObject object1 = object.getJSONObject("message");
                    object1 = vipService.pareseData1(object1);

                    object.put("message",object1);
                    result = object.toString();
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

}
