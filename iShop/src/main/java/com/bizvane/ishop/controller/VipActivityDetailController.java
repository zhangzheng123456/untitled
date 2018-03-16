package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.bizvane.ishop.utils.MD5Sum;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.DataBox;
import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by nanji on 2017/1/6.
 */
@Controller
@RequestMapping("/vipActivity/detail")
public class VipActivityDetailController {
    @Autowired
    private VipActivityDetailService vipActivityDetailService;
    @Autowired
    private VipActivityService vipActivityService;
    @Autowired
    private VipGroupService vipGroupService;
    @Autowired
    private VipCardTypeService vipCardTypeService;
    @Autowired
    private ActivityAnalyService activityAnalyService;
    @Autowired
    private IceInterfaceService iceInterfaceService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    CRMInterfaceService crmInterfaceService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    VipParamService vipParamService;
    @Autowired
    WxTemplateService wxTemplateService;
    @Autowired
    CorpService corpService;
    @Autowired
    VipPointsAdjustService vipPointsAdjustService;

    private static final Logger logger = Logger.getLogger(VipActivityDetailController.class);

    String id;

    /**
     * 会员活动
     * 添加
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addActivity(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String id = "";
        try {
            String server_name = request.getServerName();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            //根据活动类型判断新增或者编辑

            String result = this.vipActivityDetailService.insertOrUpdate(request,message, user_id, server_name);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("新增成功");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 获取活动详细信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String selectActivity(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_code = jsonObject.getString("activity_code");
            VipActivityDetail vipActivityDetail = vipActivityDetailService.selActivityDetailByCode(activity_code);
            JSONObject result = new JSONObject();

            String actvity_type = vipActivityDetail.getActivity_type();
            String send_coupon_type = vipActivityDetail.getSend_coupon_type();
            String recruit = vipActivityDetail.getRecruit();

            if (actvity_type.equals("coupon")) {
                if (send_coupon_type.equals("card")) {
                    String coupon_type = vipActivityDetail.getCoupon_type();
                    if (coupon_type != null && !coupon_type.equals("")) {
                        JSONArray couponInfo = JSON.parseArray(coupon_type);
                        String vip_card_type_name = "";
                        for (int i = 0; i < couponInfo.size(); i++) {
                            JSONObject obj = couponInfo.getJSONObject(i);
                            //根据会员卡编号查询会员卡类型名称
                            if (obj.containsKey("vip_card_type_code") && !obj.getString("vip_card_type_code").equals("")) {
                                String vip_card_type_code = obj.getString("vip_card_type_code");
                                VipCardType vipCardType = vipCardTypeService.getVipCardTypeByCode(vipActivityDetail.getCorp_code(), vip_card_type_code, Common.IS_ACTIVE_Y);
                                if (vipCardType != null) {
                                    vip_card_type_name = vipCardType.getVip_card_type_name();
                                }
                                obj.put("vip_card_type_name", vip_card_type_name);
                            }
                        }
                        vipActivityDetail.setCoupon_type(JSON.toJSONString(couponInfo));
                    }
                } else if (send_coupon_type.equals("consume")) {
                    List<VipActivityDetailConsume> consumes = vipActivityDetailService.selActivityDetailConsume(activity_code);
                    for (int i = 0; i < consumes.size(); i++) {
                        String start = consumes.get(i).getDiscount_start();
                        String end = consumes.get(i).getDiscount_end();
                        if (start != null && !start.equals(""))
                            consumes.get(i).setDiscount_start(String.valueOf(Double.parseDouble(start) * 10));
                        if (end != null && !end.equals(""))
                            consumes.get(i).setDiscount_end(String.valueOf(Double.parseDouble(end) * 10));
                    }
                    vipActivityDetail.setConsume_condition(JSON.toJSONString(consumes));
                } else if (send_coupon_type.equals("anniversary")) {
                    List<VipActivityDetailAnniversary> anniversaries = vipActivityDetailService.selActivityDetailAnniversary(activity_code);
                    vipActivityDetail.setCoupon_type(JSON.toJSONString(anniversaries));
                }
            }
            //活动类型：招募活动
            if (recruit != null && !recruit.equals("")) {
                JSONArray recruitInfo = JSON.parseArray(recruit);
                String vip_card_type_name = "";
                for (int i = 0; i < recruitInfo.size(); i++) {
                    JSONObject obj = recruitInfo.getJSONObject(i);
                    if (obj.containsKey("vip_card_type_code") && !obj.getString("vip_card_type_code").equals("")) {
                        String vip_card_type_code = obj.getString("vip_card_type_code");
                        VipCardType vipCardType = vipCardTypeService.getVipCardTypeByCode(vipActivityDetail.getCorp_code(), vip_card_type_code, Common.IS_ACTIVE_Y);
                        if (vipCardType != null) {
                            vip_card_type_name = vipCardType.getVip_card_type_name();
                        }
                        obj.put("vip_card_type_name", vip_card_type_name);
                    }
                }
                vipActivityDetail.setRecruit(JSON.toJSONString(recruitInfo));
            }
            JSONObject apply_obj = new JSONObject();
            if (actvity_type.equals("invite")) {
                String apply_desc = vipActivityDetail.getApply_desc();
                if (apply_desc != null && !apply_desc.equals("")) {
                    vipActivityDetail.setActivity_url(CommonValue.ishop_url + "/goods/mobile/apply.html?activity_code=" + activity_code);
                    apply_obj = JSONObject.parseObject(apply_desc);
                } else {
                    apply_obj.put("apply_title", "");
                }
            }
            if ("online_apply".equals(actvity_type)) {
                List<VipActivityDetailApply> list = vipActivityDetailService.selectActivityApplyByCode(activity_code);
                vipActivityDetail.setApply_type_info(JSON.toJSONString(list));
            }
            if (vipActivityDetail.getCoupon_type().equals("")) {
                vipActivityDetail.setCoupon_type("[]");
            }
            vipActivityDetail.setActivity_type(CheckUtils.CheckVipActivityType(vipActivityDetail.getActivity_type()));
            JSONObject activity_detail = JSON.parseObject(JSON.toJSONString(vipActivityDetail));
            if (!apply_obj.isEmpty())
                activity_detail.putAll(apply_obj);

            result.put("activityVip", activity_detail);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/selActivityByCodeAndName", method = RequestMethod.POST)
    @ResponseBody
    public String selActivityByCodeAndName(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            // String activity_code = "AC1019920170112222001864";
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_code = jsonObject.getString("activity_code");
            List<HashMap<String, Object>> coupon = new ArrayList<HashMap<String, Object>>();
            coupon = activityAnalyService.getCouponByActivityCode1(activity_code);

            JSONObject result = new JSONObject();
            result.put("coupon", coupon);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return dataBean.getJsonStr();

    }


    /**
     * 获取线上报名项目的详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/mobile/applyItem", method = RequestMethod.POST)
    @ResponseBody
    public String mobileApplyItem(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int item_id = jsonObject.getInteger("item_id");
            VipActivityDetailApply vipActivityDetailApply = vipActivityDetailService.selectActivityApplyById(item_id);
            VipActivityDetail vipActivityDetail=vipActivityDetailService.selActivityDetailByCode(vipActivityDetailApply.getActivity_code());
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("apply_info", vipActivityDetailApply);
            jsonObject1.put("ac_detail_info",vipActivityDetail);
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject1.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(e.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 获取线上报名活动列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/mobile/applyList", method = RequestMethod.POST)
    @ResponseBody
    public String mobileApplyList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_apply);
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String card_no = jsonObject.getString("card_no");
            String app_id = jsonObject.getString("app_id");
            String open_id = jsonObject.getString("open_id");

            if (card_no != null && card_no.equals("")) {
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_REDIRECT);
                dataBean.setMessage(CommonValue.wei_member_url.replace("@appid@", app_id));
                return dataBean.getJsonStr();
            }
            List<VipActivity> new_list = new ArrayList<VipActivity>();

            JSONObject object = new JSONObject();
            object.put("key", "VIP_OPEN_ID");
            object.put("type", "text");
            object.put("value", open_id);

            List<VipActivity> vipActivities = vipActivityService.getVipActivityByAppid(app_id, "1", "online_apply");
            for (int i = 0; i < vipActivities.size(); i++) {
                //判断此任务是全部会员还是有筛选条件的会员
                String corp_code = vipActivities.get(i).getCorp_code();
                String target_vips = vipActivities.get(i).getTarget_vips();
                if (target_vips != null && !target_vips.equals("") && !target_vips.equals("[]")) {
                    JSONArray array = JSONArray.parseArray(target_vips);
                    array.add(object);
                    DataBox dataBox = iceInterfaceService.vipScreenMethod2("1", "3", corp_code,JSON.toJSONString(array),"","");
//                    DataBox dataBox = vipGroupService.vipScreenBySolr(array, corp_code, "1", "3", "", "", "", "", "", "", "");
                    String result = dataBox.data.get("message").value;
                    JSONObject result_obj = JSONObject.parseObject(result);
                    JSONArray vip_array = result_obj.getJSONArray("all_vip_list");
                    //如果该会员在该任务的目标会员中
                    if (vip_array.size() > 0) {
                        new_list.add(vipActivities.get(i));
                    }
                } else {
                    new_list.add(vipActivities.get(i));
                }
            }

            //已完成（报名成功）
            JSONArray compl = new JSONArray();
            BasicDBObject queryCondition = new BasicDBObject();
            queryCondition.put("app_id", app_id);
            queryCondition.put("open_id", open_id);
            BasicDBList values = new BasicDBList();
            values.add(new BasicDBObject("status", "1"));
//            values.add(new BasicDBObject("status", "-1"));
            queryCondition.put("$or", values);
            DBCursor dbCursor1 = cursor.find(queryCondition).sort(new BasicDBObject("modified_date", -1));
            List<String> activity_codes = new ArrayList<String>();
            while (dbCursor1.hasNext()) {
                DBObject dbObject1 = dbCursor1.next();
                String status = dbObject1.get("status").toString();
                JSONObject task_obj = JSONObject.parseObject(dbObject1.get("vipActivity").toString());
                VipActivity vipActivity = WebUtils.JSON2Bean(task_obj, VipActivity.class);
                compl.add(WebUtils.bean2JSONObject(vipActivity));
                //报名成功
                if (status.equals("1"))
                    activity_codes.add(vipActivity.getActivity_code());
            }

            //未完成
            JSONArray uncompl = new JSONArray();
            for (int i = 0; i < new_list.size(); i++) {
                VipActivity vipActivity = new_list.get(i);
                String activity_code = vipActivity.getActivity_code();
                if (!activity_codes.contains(activity_code))
                    uncompl.add(WebUtils.bean2JSONObject(vipActivity));
            }
            JSONObject result = new JSONObject();
            result.put("uncompl", uncompl);
            result.put("coml", compl);

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 获取活动报名信息（带入个人信息）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/mobile/applyDetail", method = RequestMethod.POST)
    @ResponseBody
    public String mobileApplyDetail(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String card_no = jsonObject.getString("card_no");
            String app_id = jsonObject.getString("app_id");
            String open_id = jsonObject.getString("open_id");
            String activity_code = jsonObject.getString("activity_code");

            if (card_no.equals("")) {
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_REDIRECT);
                dataBean.setMessage(CommonValue.wei_member_url.replace("@appid@", app_id));
                return dataBean.getJsonStr();
            }
            VipActivity activity = vipActivityService.getActivityByCode(activity_code);
            if (activity == null || !activity.getActivity_state().equals(Common.ACTIVITY_STATUS_1)){
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("活动不存在或已结束");
                return dataBean.getJsonStr();
            }
            VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(activity_code);
            List<VipActivityDetailApply> applies = vipActivityDetailService.selectActivityApplyByCode(activity_code);

            JSONArray cust_array = JSONArray.parseArray(detail.getApply_condition());
            String cust_cols = "";
            for (int i = 0; i < cust_array.size(); i++) {
                if (cust_array.getJSONObject(i).getString("param_name").startsWith("CUST_")) {
                    cust_cols = cust_cols + cust_array.getJSONObject(i).getString("param_name") + ",";
                }
            }

            String corp_code = activity.getCorp_code();
            DataBox dataBox = iceInterfaceService.getVipByOpenId(corp_code, open_id, cust_cols);
            JSONArray vip_array = JSONArray.parseArray(dataBox.data.get("message").value);
            JSONObject vip_info = vip_array.getJSONObject(0);
            String sex_vip = vip_info.getString("sex");
            if (sex_vip != null && (sex_vip.equalsIgnoreCase("W") || sex_vip.equals("0") || sex_vip.equalsIgnoreCase("F"))) {
                vip_info.put("sex_vip", "女");
            } else {
                vip_info.put("sex_vip", "男");
            }
            vip_info.put("birthday", vip_info.getString("vip_birthday") != null ? vip_info.getString("vip_birthday").replace("无", "") : "");
            vip_info.put("vip_name", vip_info.getString("vip_name") != null ? vip_info.getString("vip_name") : "");
            String province = vip_info.containsKey("province") ? vip_info.getString("province") : "";
            String city = vip_info.containsKey("city") ? vip_info.getString("city") : "";
            String area = vip_info.containsKey("area") ? vip_info.getString("area") : "";
            JSONObject p = new JSONObject();
            p.put("province", province);
            p.put("city", city);
            p.put("area", area);
            vip_info.put("province", p);
            vip_info.put("address", vip_info.containsKey("address") ? vip_info.getString("address") : "");

            //添加自身的资料到扩展资料数组中
            JSONArray apply_condition = JSONObject.parseArray(detail.getApply_condition());

            //对必填的扩展信息排序
            List<HashMap<String, String>> condtionList = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < apply_condition.size(); i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject jsonObject2 = apply_condition.getJSONObject(i);
                Set<String> stringSet = jsonObject2.keySet();
                for (String str : stringSet) {
                    map.put(str, jsonObject2.getString(str));
                }
                condtionList.add(map);
            }
//            Collections.sort(condtionList, new Comparator<HashMap<String, String>>() {
//                @Override
//                public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
//                    return o2.get("required").toString().compareTo(o1.get("required".toString()));
//                }
//            });

            JSONObject result = new JSONObject();
            result.put("activity_theme", activity.getActivity_theme());//活动名称
            result.put("activity_desc", activity.getActivity_desc());//活动简介
            result.put("apply_condition", condtionList);//需要填写的扩展资料
            result.put("apply_list", applies);//每个项目的详情
            result.put("vipInfo", vip_info);//会员信息
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 线上报名（保存报名信息-跳转至支付页面）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/mobile/saveApplyInfo", method = RequestMethod.POST)
    @ResponseBody
    public String saveApplyInfo(HttpServletRequest request,HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_apply);
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = (JSONObject) JSON.parse(jsString, Feature.OrderedField);
            logger.info(jsonObj.toString());

//            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = jsonObj.getJSONObject("message");
            String app_id = jsonObject.getString("app_id");
            String open_id = jsonObject.getString("open_id");
            String activity_code = jsonObject.getString("activity_code");
            JSONObject extend_obj = jsonObject.getJSONObject("apply_info");
            int item_id = Integer.parseInt(jsonObject.getString("item_id"));
            String pay_type = jsonObject.getString("pay_type");


            VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
            if (vipActivity == null || !vipActivity.getActivity_state().equals(Common.ACTIVITY_STATUS_1)){
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("活动不存在或已结束");
                return dataBean.getJsonStr();
            }
            VipActivityDetailApply activityDetailApply = vipActivityDetailService.selectActivityApplyById(item_id);
            if (activityDetailApply.getLast_count().equals("0")){
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("此项目名额已满");
                return dataBean.getJsonStr();
            }
            String corp_code = vipActivity.getCorp_code();

            DataBox dataBox_vip = iceInterfaceService.getVipByOpenId(corp_code, open_id, "");
            JSONArray vip_array = JSONArray.parseArray(dataBox_vip.data.get("message").value);
            if (vip_array.size() > 0) {
                JSONObject vip_info = vip_array.getJSONObject(0);
                String vip_id = vip_info.getString("vip_id");

//                JSONObject extend_obj = JSONObject.parseObject(apply_info);
                JSONArray array = new JSONArray();
                JSONArray schedule = new JSONArray();
                String vip_name = "";
                String sex = "";
                String birthday = "";
                String address = "";
                String province = "";
                Set<String> keys = extend_obj.keySet();
                for (String key : keys) {
                    JSONObject obj = new JSONObject();
                    String value = extend_obj.get(key).toString();
                    obj.put("column", key);
                    obj.put("value", value);
                    if (key.startsWith("CUST_")) {
                        List<VipParam> vipParamList = vipParamService.selectByParamName(corp_code, key, "Y");
                        if (vipParamList.size() > 0) {
                            VipParam vipParam = vipParamList.get(0);
                            String param_attribute = vipParam.getParam_attribute();
                            if ("sex".equals(param_attribute)) {
                                if ("男".equals(value)) {
                                    value = "1";
                                } else if ("女".equals(value)) {
                                    value = "0";
                                }
                            }
                            obj.put("column", key);
                            obj.put("value", value);
                            array.add(obj);
                        }
                    }
                    if (key.equals("vip_name"))
                        vip_name = value;
                    if (key.equals("sex_vip")) {
                        if (value.equals("女")) {
                            sex = "F";
                        } else {
                            sex = "M";
                        }
                    }
                    if (key.equals("birthday"))
                        birthday = value;
                    if (key.equals("address"))
                        address = value;
                    if (key.equals("province")) {
                        province = value;
                        if (!province.isEmpty()) {
                            JSONObject p = JSONObject.parseObject(province);
                            String province1 = p.getString("province");
                            if (province1.isEmpty())
                                continue;
                        }
                    }
                    //查找扩展参数名字
                    if(key.startsWith("CUST_")) {
                        List<VipParam> vipParamList = vipParamService.selectByParamName(corp_code, key, "Y");
                        obj.put("param_desc",vipParamList.get(0).getParam_desc());
                    }else{
                        if (key.equals("vip_name")){
                            obj.put("param_desc","姓名(个人资料)");
                        }
                        if (key.equals("sex_vip")) {
                            obj.put("param_desc","性别(个人资料)");
                        }
                        if (key.equals("birthday")){
                            obj.put("param_desc","生日(个人资料)");
                        }
                        if (key.equals("address")){
                            obj.put("param_desc","详细地址(个人资料)");
                        }
                        if (key.equals("province")){
                            obj.put("param_desc","省市区(个人资料)");
                        }
                    }
                    schedule.add(obj);
                }
                logger.info(JSON.toJSONString(schedule));

                if (array.size() > 0) {
                    DataBox dataBox = iceInterfaceService.saveVipExtendInfo(corp_code, vip_id, "", JSON.toJSONString(array));
                    logger.info("==========saveVipExtendInfo=" + dataBox.status.toString());
                    if (!dataBox.status.toString().equals("SUCCESS")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage(dataBox.msg);
                        return dataBean.getJsonStr();
                    }
                }
                if (!vip_name.equals("") || !birthday.equals("") || !sex.equals("") || !province.equals("") || !address.equals("")) {
                    if (corp_code.equals("C10016")) {
                        HashMap<String, Object> vipInfo = new HashMap<String, Object>();
                        vipInfo.put("id", vip_id);
                        if (!vip_name.equals(""))
                            vipInfo.put("VIPNAME", vip_name);
                        if (!birthday.equals(""))
                            vipInfo.put("BIRTHDAY", birthday.replace("-", ""));
                        if (!sex.equals(""))
                            vipInfo.put("SEX", sex);
                        crmInterfaceService.modInfoVip(corp_code, vipInfo);
                    }
                    String province1 = "";
                    String city = "";
                    String area = "";
                    if (!province.isEmpty()) {
                        JSONObject p = JSONObject.parseObject(province);
                        province1 = p.getString("province");
                        city = p.getString("city");
                        area = p.getString("area");
                    }

                    DataBox dataBox = iceInterfaceAPIService.vipProfileBackup(corp_code, vip_id, "", "", vip_name, birthday, sex, "", province1, city, area, address, "", "", "", "");
                    logger.info("==========vipProfileBackup=" + dataBox.status.toString());
                    if (!dataBox.status.toString().equals("SUCCESS")) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId("1");
                        dataBean.setMessage(dataBox.msg);
                        return dataBean.getJsonStr();
                    }
                }

                DBObject updateCondition = new BasicDBObject();
                updateCondition.put("app_id", app_id );
                updateCondition.put("activity_code", activity_code);
                updateCondition.put("open_id", open_id);
                DBCursor dbObjects = cursor.find(updateCondition);
                if (dbObjects.hasNext()){
                    DBObject updatedValue = new BasicDBObject();
                    DBObject dbObject=dbObjects.next();
                    String status = dbObject.get("status") != null && !dbObject.get("status").equals("") ?dbObject.get("status").toString():"0";
//                    if (!status.equals("1")){
                        updatedValue.put("status", status);
                        updatedValue.put("apply_info", schedule);
                        updatedValue.put("vipActivity", WebUtils.bean2JSONObject(vipActivity));
                        updatedValue.put("apply_item", WebUtils.bean2JSONObject(activityDetailApply));
                        updatedValue.put("vip", vip_info);
                        updatedValue.put("modified_date", Common.DATETIME_FORMAT.format(new Date()));
                        updatedValue.put("pay_type", pay_type);
                        DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                        cursor.update(updateCondition, updateSetValue);
//                    }
                }else {
                    BasicDBObject dbObject = new BasicDBObject();
                    dbObject.put("_id", app_id + "_" + activity_code + item_id + "_" + open_id);
                    dbObject.put("corp_code", corp_code);
                    dbObject.put("app_id", app_id);
                    dbObject.put("activity_code", activity_code);
                    dbObject.put("item_id", item_id+"");
                    dbObject.put("status", "0");
                    dbObject.put("open_id", open_id);
                    dbObject.put("apply_info", schedule);
                    dbObject.put("vipActivity", WebUtils.bean2JSONObject(vipActivity));
                    dbObject.put("apply_item", WebUtils.bean2JSONObject(activityDetailApply));
                    dbObject.put("vip", vip_info);
                    dbObject.put("modified_date", Common.DATETIME_FORMAT.format(new Date()));
                    dbObject.put("created_date", Common.DATETIME_FORMAT.format(new Date()));
                    dbObject.put("pay_type", pay_type);
                    cursor.save(dbObject);
                }

                String pay_url = "online_activity_pay_info.html?vip_id=" + vip_id + "&activity_code=" + activity_code + "&item_id=" + item_id + "&pay_type=" + pay_type;
                pay_url = java.net.URLEncoder.encode(pay_url);
                String url = CommonValue.wx_pay_url.replace("@appid@", app_id).replace("@html@", pay_url);

                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(url);
            }else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("会员信息有误，请稍后再试");
                return dataBean.getJsonStr();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 支付页面-获取活动详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/mobile/getApplyInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getApplyInfo(HttpServletRequest request,HttpServletResponse response){
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_apply);
        String id = "";
        try {

            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String data = stringBuffer.toString();
            JSONObject jsonObject = JSONObject.parseObject(data);

            String activity_code = jsonObject.getString("activity_code");
            int item_id = Integer.parseInt(jsonObject.getString("item_id"));
            VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
            if (vipActivity == null || !vipActivity.getActivity_state().equals(Common.ACTIVITY_STATUS_1)){
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("活动不存在或已结束");
                return dataBean.getJsonStr();
            }
            VipActivityDetailApply activityDetailApply = vipActivityDetailService.selectActivityApplyById(item_id);
            if (activityDetailApply.getLast_count().equals("0")){
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("此项目名额已满");
                return dataBean.getJsonStr();
            }
            JSONObject object = new JSONObject();
            object.put("activity_theme",vipActivity.getActivity_theme());
            object.put("activity_desc",vipActivity.getActivity_desc());
            object.put("item_name",activityDetailApply.getItem_name());
            object.put("item_pic",activityDetailApply.getItem_picture());
            object.put("fee_money",activityDetailApply.getFee_money());
            object.put("fee_point",activityDetailApply.getFee_points());

            object.put("pay_return_url",CommonValue.wei_activity_url.replace("@APPID@",vipActivity.getApp_id()).replace("@TYPE@","paySuccess"));

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(object.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 支付成功后-回调
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/mobile/applyPay", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String apply(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_apply);
        String id = "";
        try {
            System.out.println("==========进入方法"+Common.DATETIME_FORMAT.format(new Date()));
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String data = stringBuffer.toString();
            JSONObject jsonObject = JSONObject.parseObject(data);

            String corp_code = jsonObject.getString("corp_code").toUpperCase();
            String app_id = jsonObject.getString("app_id");
            String open_id = jsonObject.getString("open_id");
            String activity_code = jsonObject.getString("activity_code");
            int item_id = Integer.parseInt(jsonObject.getString("item_id"));
            String order_id = jsonObject.getString("order_id");
            String pay_result = jsonObject.getString("pay_result");
            String pay_time = jsonObject.getString("pay_time");

            System.out.println(".......pay_result........."+pay_result);

            DataBox dataBox = iceInterfaceService.getVipByOpenId(corp_code, open_id, "");


            JSONArray vip_array = JSONArray.parseArray(dataBox.data.get("message").value);

            System.out.println("=========================会员=============="+vip_array.toString());

            if (vip_array.size() > 0){

                System.out.println("=================获取会员==============");

                JSONObject vip_info = vip_array.getJSONObject(0);
//                String vip_id = vip_info.getString("vip_id");

                VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
                VipActivityDetailApply activityDetailApply = vipActivityDetailService.selectActivityApplyById(item_id);
                if (activityDetailApply != null) {
                    String is_send_notice = "N";

                    if (pay_result.equals("Y")){
                        if (activityDetailApply.getLast_count() != null && !activityDetailApply.getLast_count().equals("")){
                            int last_count = Integer.parseInt(activityDetailApply.getLast_count());
                            last_count = last_count - 1;
                            activityDetailApply.setLast_count(String.valueOf(last_count));
                            vipActivityDetailService.updateActivityApply(activityDetailApply);
                        }
                        System.out.println("==========名额-1"+Common.DATETIME_FORMAT.format(new Date()));

                        List<WxTemplate> wxTemplates = wxTemplateService.selectTempByAppId(app_id,"",Common.TEMPLATE_NAME_1);
                        if (wxTemplates.size() > 0){
                            String template_id = wxTemplates.get(0).getTemplate_id();
                            JSONObject template_content = new JSONObject();
                            template_content.put("first","亲爱的会员，您已成功报名");
                            template_content.put("keyword1",vipActivity.getActivity_theme());
                            template_content.put("keyword2","已报名");
                            template_content.put("remark","感谢您的参与，请按时参加");

                            String result = wxTemplateService.sendTemplateMsg(app_id, open_id, template_id, template_content, "");
                            logger.info("=============result"+result);
                            JSONObject info = JSONObject.parseObject(result);
                            String errcode = info.getString("errcode");
                            String errmsg = info.getString("errmsg");
                            if ("0".equals(errcode)) {
                                is_send_notice = "Y";
                            }else {
                                is_send_notice = errmsg;
                            }
                        }
                        System.out.println("==========发送模板"+Common.DATETIME_FORMAT.format(new Date()));

                        BasicDBObject findObject = new BasicDBObject();
                        findObject.put("app_id", app_id );
                        findObject.put("activity_code", activity_code);
                        findObject.put("open_id", open_id);
                        DBCursor cursor1 = cursor.find(findObject);
                        if (cursor1.hasNext()){
                            DBObject updateCondition = new BasicDBObject();
                            updateCondition.put("app_id", app_id );
                            updateCondition.put("activity_code", activity_code);
                            updateCondition.put("open_id", open_id);
                            DBObject updatedValue = new BasicDBObject();
                            updatedValue.put("status", "1");
                            updatedValue.put("is_send_notice", is_send_notice);
                            updatedValue.put("order_id", order_id);
                            updatedValue.put("pay_time", pay_time);
                            updatedValue.put("pay_result", pay_result);
                            updatedValue.put("vip", vip_info);
                            updatedValue.put("modified_date", Common.DATETIME_FORMAT.format(new Date()));
                            DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                            cursor.update(updateCondition, updateSetValue);

                            System.out.println("==========结束"+Common.DATETIME_FORMAT.format(new Date()));

                            dataBean.setId(id);
                            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                            dataBean.setMessage("报名成功");
                        }else {
                            dataBean.setId(id);
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setMessage("请先填写报名资料");
                        }
                    }else {
                        DBObject updateCondition = new BasicDBObject();
                        updateCondition.put("app_id", app_id );
                        updateCondition.put("activity_code", activity_code);
                        updateCondition.put("open_id", open_id);
                        DBObject updatedValue = new BasicDBObject();
                        updatedValue.put("order_id", order_id);
                        updatedValue.put("pay_time", pay_time);
                        updatedValue.put("pay_result", pay_result);
                        updatedValue.put("vip", vip_info);
                        updatedValue.put("modified_date", Common.DATETIME_FORMAT.format(new Date()));
                        DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                        cursor.update(updateCondition, updateSetValue);

                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setMessage("报名失败");
                    }
                }else {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("项目不存在");
                }
            }else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("会员信息错误");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 支付页面-获取活动详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/mobile/getSuccessApplyInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getSuccessApplyInfo(HttpServletRequest request,HttpServletResponse response){
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_apply);
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String open_id = jsonObject.getString("open_id");
            String app_id = jsonObject.getString("app_id");
            String activity_code = jsonObject.getString("activity_code");

            DBObject dbObject = new BasicDBObject();
            dbObject.put("app_id",app_id);
            dbObject.put("open_id",open_id);
            dbObject.put("status","1");
            dbObject.put("activity_code",activity_code);

            String td_allow = "N";
            VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(activity_code);
            if (detail != null){
                td_allow = detail.getTd_allow();
                if (td_allow.equals("Y")){
                    String td_end_time = detail.getTd_end_time();
                    String now_date=Common.DATETIME_FORMAT.format(new Date());
                    if(!td_end_time.equals("") && now_date.compareTo(td_end_time)>0){
                        td_allow = "N";
                    }
                }
            }
            DBCursor cursor1 = cursor.find(dbObject);
            if (cursor1.hasNext()){
                JSONObject object = JSONObject.parseObject(cursor1.next().toString());
                object.put("td_allow",td_allow);
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(object.toString());
            }else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("未找到报名记录");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 线上报名（退款）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/mobile/refund", method = RequestMethod.POST)
    @ResponseBody
    public String refund(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_activity_apply);
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String app_id = jsonObject.getString("app_id");
            String open_id = jsonObject.getString("open_id");
            String activity_code = jsonObject.getString("activity_code");
            int item_id = Integer.parseInt(jsonObject.getString("item_id"));

            String access_key = "";
            CorpWechat corpWechat = corpService.getCorpByAppId("",app_id);
            if (corpWechat != null)
                access_key = corpWechat.getAccess_key();

            logger.info("====="+access_key);
            VipActivityDetail vipActivityDetail=vipActivityDetailService.selActivityDetailByCode(activity_code);
            String td_allow=vipActivityDetail.getTd_allow();
            String td_end_time=vipActivityDetail.getTd_end_time();
            if(td_allow.equals("Y")){
                String now_date=Common.DATETIME_FORMAT.format(new Date());
                if(!td_end_time.equals("") && now_date.compareTo(td_end_time)>0){
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("您已超出退订的截至时间");
                    return  dataBean.getJsonStr();
                }
            }else{
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("该活动不允许退订");
                return  dataBean.getJsonStr();
            }

            BasicDBObject  basicDBObject=new BasicDBObject();
            basicDBObject.put("app_id", app_id );
            basicDBObject.put("activity_code", activity_code);
            basicDBObject.put("open_id", open_id);
            basicDBObject.put("status","1");
            DBCursor dbObjects=cursor.find(basicDBObject);
            if(dbObjects.count()>0){
                DBObject dbObject = dbObjects.next();
                String order_id = dbObject.get("order_id").toString();
                if (dbObject.containsField("sign_status") && dbObject.get("sign_status").equals("Y")){
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("您已签到，不可退订");
                    return  dataBean.getJsonStr();
                }
                VipActivityDetailApply activityDetailApply = vipActivityDetailService.selectActivityApplyById(item_id);
                if (activityDetailApply != null) {
                    String ts = System.currentTimeMillis()+"";
                    JSONObject signinfo_obj = new JSONObject();
                    signinfo_obj.put("customcode",app_id);
                    signinfo_obj.put("ts",ts);
                    signinfo_obj.put("method","com.bizvane.sun.wx.method.crm.EnrollRefund");
                    signinfo_obj.put("sign",(MD5Sum.getMD5Str32(app_id + ts + access_key)).toUpperCase());

                    JSONObject data_obj = new JSONObject();
                    data_obj.put("pay_no",order_id);
                    data_obj.put("signinfo",signinfo_obj);

                    JSONObject object = new JSONObject();
                    object.put("data",data_obj);

                    String result = IshowHttpClient.post(CommonValue.baiyou_url,object);

                    JSONObject result_obj = JSONObject.parseObject(result);
                    if (result_obj.containsKey("code") && result_obj.getString("code").equals("0")){
                        if (activityDetailApply.getLimit_count() != null && !activityDetailApply.getLimit_count().equals("")) {

                            /*********************************************/
                            int last_count = Integer.parseInt(activityDetailApply.getLast_count());
                            last_count = last_count + 1;
                            activityDetailApply.setLast_count(String.valueOf(last_count));
                            vipActivityDetailService.updateActivityApply(activityDetailApply);
                        }

                        DBObject updateCondition = new BasicDBObject();
                        updateCondition.put("_id", app_id + "_" + activity_code + item_id + "_" + open_id);
                        DBObject updatedValue = new BasicDBObject();
                        updatedValue.put("status", "-1");
                        updatedValue.put("modified_date", Common.DATETIME_FORMAT.format(new Date()));
                        DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                        cursor.update(updateCondition, updateSetValue);
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setMessage("退款成功");
                    }else {
                        String refund_message = "退订失败";
                        if (result_obj.getString("message") != null)
                            refund_message = result_obj.getString("message");
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setMessage(refund_message);
                    }
                } else {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("未找到符合您的报名活动");
                }
            }else{
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("未找到符合您的报名活动");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/mobile/qrcode",method = RequestMethod.POST)
    @ResponseBody
    public  String createActivityQrcode(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        String id="";
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String activity_code=jsonObject.getString("activity_code");
            VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
            String activity_id=String.valueOf(vipActivity.getId());

            if(StringUtils.isNotBlank(vipActivity.getQrcode())){
                JSONObject result_obj=new JSONObject();
                result_obj.put("qrcode",vipActivity.getQrcode());
                result_obj.put("qrcode_content",vipActivity.getQrcode_content());
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result_obj.toString());
                return  dataBean.getJsonStr();
            }

            String prd = "ishop";
            String src = "activity";
            String app_id = vipActivity.getApp_id();

            //生成永久二维码
            String url = CommonValue.wechat_url+"/creatQrcode?auth_appid="+app_id+"&prd="+prd+"&src="+src+"&activity_id="+activity_id;
            String result = IshowHttpClient.get(url);
            logger.info("------------creatQrcode  result" + result);
            JSONObject result_obj = new JSONObject();

            int msg = 0;
            if (!result.startsWith("{")) {
                msg = -1;
            }else{
                JSONObject obj = JSONObject.parseObject(result);
                if (result.contains("errcode")) {
                    msg = -1;
                } else {
                    String picture = obj.get("picture").toString();
                    String qrcode_url = obj.get("url").toString();
                    result_obj.put("qrcode",picture);
                    result_obj.put("qrcode_content",qrcode_url);
                    //保存活动二维码
                    VipActivity activity=new VipActivity();
                    activity.setActivity_code(activity_code);
                    activity.setQrcode(picture);
                    activity.setQrcode_content(qrcode_url);
                    vipActivityService.updateVipActivity(activity);
                }
            }
            if (msg == 0){
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result_obj.toString());
            }else {
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("生成二维码失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


}
