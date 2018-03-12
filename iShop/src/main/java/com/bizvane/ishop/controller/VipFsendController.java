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
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.common.service.redis.RedisClient;
import com.bizvane.sun.v1.common.DataBox;
import com.github.pagehelper.PageInfo;
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/25.
 */
@Controller
@RequestMapping("/vipFsend")
public class VipFsendController {

    @Autowired
    private VipFsendService vipFsendService;
    @Autowired
    private IceInterfaceService iceInterfaceService;
    @Autowired
    private VipGroupService vipGroupService;
    @Autowired
    private ScheduleJobService scheduleJobService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private WxTemplateService wxTemplateService;
    @Autowired
    private WxTemplateContentService wxTemplateContentService;
    @Autowired
    private VipActivityService vipActivityService;
    @Autowired
    private VipTaskService vipTaskService;

    @Autowired
    private  MongoDBClient mongoDBClient;
    @Autowired
    private RedisClient redisClient;

    private static final Logger logger = Logger.getLogger(VipFsendController.class);

    String id;


    /**
     * 群发消息
     * <p>
     * 发送消息
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addFsend(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String group_code = request.getSession().getAttribute("group_code").toString();

        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();

            JSONObject jsonObject = JSONObject.parseObject(message);
            VipFsend vipFsend = WebUtils.JSON2Bean(jsonObject, VipFsend.class);

//            if (!vipFsend.getSend_type().equals("sms")) {
//                List<WxTemplate> wxTemplates = wxTemplateService.selectTempByAppId(vipFsend.getApp_id(), "", Common.TEMPLATE_NAME_1);
//                if (wxTemplates.size() < 1){
//                    dataBean.setId(id);
//                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                    dataBean.setMessage("未设置群发模板");
//                    return dataBean.getJsonStr();
//                }
//            }

            if (vipFsend.getSend_scope().equals("vip_condition")){
                JSONArray array = JSONArray.parseArray(vipFsend.getSms_vips());
                array = vipGroupService.vipScreen2Array(array,vipFsend.getCorp_code(),role_code,brand_code,area_code,store_code,user_code);
                vipFsend.setSms_vips_(JSON.toJSONString(array));
            }
            if (vipFsend.getSend_scope().equals("vip_condition_new")){
                JSONArray array = JSONArray.parseArray(vipFsend.getSms_vips());
                array = vipGroupService.vipScreen2ArrayNew(array,vipFsend.getCorp_code(),role_code,brand_code,area_code,store_code,user_code);
                vipFsend.setSms_vips_(JSON.toJSONString(array));
            }
            if (vipFsend.getSend_scope().equals("vip_group")){
                JSONObject screen = new JSONObject();
                screen.put("role_code",role_code);
                screen.put("brand_code",brand_code);
                screen.put("area_code",area_code);
                screen.put("store_code",store_code);
                screen.put("user_code",user_code);
                vipFsend.setSms_vips(screen.toString());
            }
            String result = this.vipFsendService.insert(vipFsend, user_code,group_code,role_code);

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_fsend_id = jsonObject.get("id").toString();
            String[] ids = vip_fsend_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                VipFsend vipFsend = vipFsendService.getVipFsendInfoById(Integer.parseInt(ids[i]));
                if (vipFsend != null){
                    String sms_code = vipFsend.getSms_code();
                    scheduleJobService.deleteScheduleByGroup(sms_code);
                    vipFsendService.delete(Integer.parseInt(ids[i]));
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 群发消息
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<VipFsend> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = vipFsendService.getAllVipFsendByPage(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = this.vipFsendService.getAllVipFsendByPage(page_number, page_size, "", search_value);
                } else {
                    list = vipFsendService.getAllVipFsendByPage(page_number, page_size, corp_code, search_value);
                }
            }
            list=vipFsendService.switchVipFsend(list);
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 群发消息
     * 筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<VipFsend> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipFsendService.getAllVipFsendScreen(page_number, page_size, "", map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = vipFsendService.getAllVipFsendScreen(page_number, page_size, corp_code, map);
            }
            list=vipFsendService.switchVipFsend(list);
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 查看选择接收短信的会员信息
     * 以及模板消息的已读状态
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/checkVipInfo", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean bean = new DataBean();
        try {
            String jsString = request.getParameter("param");

           logger.info("json-select-------------" + jsString);
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
           //  JSONObject jsonObject = JSONObject.parseObject(message);
           // String vipFsend_id = jsonObject.get("id").toString().trim();
            String info = vipFsendService.getVipFsendByMessage(message);
            if (info != null) {
                bean.setCode(Common.DATABEAN_CODE_SUCCESS);
                bean.setId("1");
                bean.setMessage(info);
            } else {
                bean.setCode(Common.DATABEAN_CODE_ERROR);
                bean.setId("1");
                bean.setMessage("查看失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage(e.toString());
        }
        return bean.getJsonStr();
    }

    /**
     * 根据id查看详情
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String select(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_brand_code = request.getSession().getAttribute("brand_code").toString();
        String user_area_code = request.getSession().getAttribute("area_code").toString();
        String user_store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String data = null;
        try {
            String jsString = request.getParameter("param");

            logger.info("json-select-------------" + jsString);
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            String vipFsend_id = jsonObject.get("id").toString().trim();
            VipFsend info = vipFsendService.getVipFsendInfoById(Integer.valueOf(vipFsend_id));

            String sms_vips = info.getSms_vips();
            String send_scope = info.getSend_scope();
            String corp_code = info.getCorp_code();
            if (sms_vips.equals("") ){
                info.setVips_count("0");
            }else if (send_scope.equals("vip")){
                JSONObject vips_obj = JSONObject.parseObject(sms_vips);
                String vips = vips_obj.getString("vips");
                int count = vips.split(",").length;
                info.setVips_count(String.valueOf(count));
            }else if (send_scope.equals("vip_condition")){
                DataBox dataBox = iceInterfaceService.vipScreenMethod2("1","2",corp_code,info.getSms_vips_(),"","");
                String list = dataBox.data.get("message").value;
                String count = JSONObject.parseObject(list).getString("count");
                info.setVips_count(count);
            }else if (send_scope.equals("vip_condition_new")){
                DataBox dataBox = iceInterfaceService.newStyleVipSearchForWeb("1","1",corp_code,info.getSms_vips_(),null,null);
                String list = dataBox.data.get("message").value;
                String count = JSONObject.parseObject(list).getString("count");
                info.setVips_count(count);
            }

            if (info.getApp_id() != null && !info.getApp_id().isEmpty()){
                CorpWechat corpWechat = corpService.getCorpByAppId(info.getCorp_code(),info.getApp_id());
                if (corpWechat != null){
                    info.setApp_name(corpWechat.getApp_name());
                }
            }
            if (info.getTemplate_name() == null || info.getTemplate_name().equals(""))
                info.setTemplate_name(Common.TEMPLATE_NAME_1);
            String content = info.getContent();
            String send_type = info.getSend_type();
            String code = info.getActivity_vip_code();
            if (code.contains("VIPTASK")){
                VipTask vipTask = vipTaskService.selectByTaskCode(code.replace("VIPTASK",""));
                String app_id = vipTask.getApp_id();
                List<WxTemplateContent> contents = wxTemplateContentService.selectContentById(app_id,Common.TEMPLATE_NAME_3,"","");
                JSONObject con = new JSONObject();
                if (contents.size() > 0){
                    String first = contents.get(0).getTemplate_first();
                    String remark = contents.get(0).getTemplate_remark();
                    con.put("first",first);
                    con.put("remark",remark);
                }else {
                    con.put("first","您好，您有新的待办任务");
                    con.put("remark","完成更多会员任务获得奖励");
                }
                con.put("keyword1",vipTask.getTask_title());
                con.put("keyword2","待办");
                info.setContent(con.toString());
            }else if (code.contains("AC") && send_type.equals("wxmass")){
                VipActivity vipActivity = vipActivityService.getActivityByCode(code);
                String theme = vipActivity.getActivity_theme();

                JSONObject content_obj = JSONObject.parseObject(content);
                JSONObject con = new JSONObject();
                con.put("first", "亲爱的会员，"+content_obj.getString("title"));
                con.put("keyword1", theme); //关键词
                con.put("keyword2", content_obj.getString("info_content")); //关键词
                con.put("remark", "请点击查看详情！"); //备注
                info.setContent(con.toString());
            }
            data=JSON.toJSONString(info);

            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(data);
        } catch (Exception e) {
            e.printStackTrace();
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage(e.toString());
        }

        return bean.getJsonStr();
    }


    /**
     * 发送范围：指定会员
     * 详情页查看会员
     * @param request
     * @return
     */
    @RequestMapping(value = "/getSendVip", method = RequestMethod.POST)
    @ResponseBody
    public String getSendVip(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_brand_code = request.getSession().getAttribute("brand_code").toString();
        String user_area_code = request.getSession().getAttribute("area_code").toString();
        String user_store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");

            logger.info("json-getSendVip-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String sms_vips = jsonObject.getString("sms_vips");
            String send_scope = jsonObject.getString("send_scope");

            int page_num = Integer.parseInt(jsonObject.getString("pageNumber"));
            int page_size = Integer.parseInt(jsonObject.getString("pageSize"));

            JSONObject result = new JSONObject();
            String list  = "";
            JSONObject messages = new JSONObject();
            JSONArray all_vip_list = new JSONArray();
            messages.put("pageNum", page_num);
            messages.put("pageSize", page_size);
            messages.put("pages", 0);
            messages.put("count", 0);

            if (send_scope.equals("vip")){
                JSONObject vips_obj = JSONObject.parseObject(sms_vips);
                String vips = vips_obj.getString("vips");
                DataBox dataBox = iceInterfaceService.getVipInfo(corp_code,vips);
                list = dataBox.data.get("message").value;

                JSONObject list_obj = JSONObject.parseObject(list);
                JSONArray vips_array = list_obj.getJSONArray("vip_info");
                int count = vips_array.size();
                int pages = 0;
                if (count >= page_num * page_size) {
                    for (int i = (page_num - 1) * page_size; i < page_num * page_size; i++) {
                        all_vip_list.add(vips_array.get(i));
                    }
                } else {
                    for (int i = (page_num - 1) * page_size; i < count; i++) {
                        all_vip_list.add(vips_array.get(i));
                    }
                }
                if (count % page_size == 0) {
                    pages = count / page_size;
                } else {
                    pages = count / page_size + 1;
                }
                messages.put("all_vip_list", all_vip_list);
                messages.put("pages", pages);
                messages.put("count", count);
                list = messages.toString();
            }else if (send_scope.equals("vip_condition")){
                JSONArray screen = JSONArray.parseArray(sms_vips);
                String sort_key = "join_date";
                String sort_value = "desc";
                DataBox dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,String.valueOf(page_num),String.valueOf(page_size),role_code,user_brand_code,user_area_code,user_store_code,user_code,sort_key,sort_value);
                list = dataBox.data.get("message").value;
            }else if (send_scope.equals("vip_condition_new")){
                JSONArray screen = JSONArray.parseArray(sms_vips);
                String sort_key = "join_date";
                String sort_value = "desc";
                DataBox dataBox = vipGroupService.vipScreenBySolrNew(screen,corp_code,String.valueOf(page_num),String.valueOf(page_size),role_code,user_brand_code,user_area_code,user_store_code,user_code,sort_key,sort_value);
                list = dataBox.data.get("message").value;
            }
            result.put("list",list);

            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage(e.toString());
        }
        return bean.getJsonStr();
    }


    /**
     * 发送会员数
     * @param request
     * @return
     */
    @RequestMapping(value = "/getSendCount", method = RequestMethod.POST)
    @ResponseBody
    public String getSendCount(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_brand_code = request.getSession().getAttribute("brand_code").toString();
        String user_area_code = request.getSession().getAttribute("area_code").toString();
        String user_store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");

            logger.info("json-getSendVip-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            int count = 0;
            if (jsonObject.containsKey("send_scope")){
                String send_scope = jsonObject.getString("send_scope");
                String sms_vips = jsonObject.getString("sms_vips");
                String vip_group_code = jsonObject.getString("vip_group_code");

                if (send_scope.equals("vip_group"))
                    sms_vips = vip_group_code;

//                count = vipFsendService.getSendVipCount(corp_code,send_scope,sms_vips,role_code,user_brand_code,user_area_code,user_store_code,user_code);

//                if (jsonObject.containsKey("type") && jsonObject.getString("type").equals("noTrans")){
//                    DataBox dataBox  = iceInterfaceService.vipScreenMethod2("1", "2", corp_code,sms_vips,"","");
////                    DataBox dataBox = vipGroupService.vipScreenBySolrNew(JSONArray.parseArray(sms_vips), corp_code, "1", "2", role_code, user_brand_code, user_area_code, user_store_code, user_code,"","");
//                    if (dataBox.status.toString().equals("SUCCESS")) {
//                        String message1 = dataBox.data.get("message").value;
//                        JSONObject msg_obj = JSONObject.parseObject(message1);
//                        count = Integer.parseInt(msg_obj.get("count").toString());
//                    }
//                }else {
                    count = vipFsendService.getSendVipCount(corp_code,send_scope,sms_vips,role_code,user_brand_code,user_area_code,user_store_code,user_code);
//                }
            }else if (jsonObject.containsKey("activity_code")){
                String activity_code = jsonObject.getString("activity_code");
                List<VipFsend> vipFsends = vipFsendService.getSendByActivityCode(corp_code,activity_code);
                if (vipFsends.size() > 0){
                    //群发记录中条件记录了创建人的条件
                    String sms_vips = vipFsends.get(0).getSms_vips_();

                    DataBox dataBox = iceInterfaceService.vipScreenMethod2( "1", "2",  corp_code,sms_vips, "", "");
                    if (dataBox.status.toString().equals("SUCCESS")) {
                        String message1 = dataBox.data.get("message").value;
                        JSONObject msg_obj = JSONObject.parseObject(message1);
                        count = Integer.parseInt(msg_obj.get("count").toString());
                    }
//                    count = vipFsendService.getSendVipCount(corp_code,vipFsends.get(0).getSend_scope(),sms_vips,"","","","","");
                }

            }else if (jsonObject.containsKey("point_contidion")){
                String point_contidion = jsonObject.getString("point_contidion");
                count = vipFsendService.getSendVipCount(corp_code,"vip_condition_new",point_contidion,role_code,user_brand_code,user_area_code,user_store_code,user_code);
            }
            JSONObject result = new JSONObject();
            result.put("count",count);

            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage(e.toString());
        }
        return bean.getJsonStr();
    }


    /**
     * 群发消息
     * <p>
     * 审核通过
     */
    @RequestMapping(value = "/checkFsend", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String checkFsend(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_fsend_id = jsonObject.get("id").toString();
            String[] ids = vip_fsend_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                VipFsend vipFsend = vipFsendService.getVipFsendInfoById(Integer.parseInt(ids[i]));
                if (vipFsend != null){
                    String check_status = vipFsend.getCheck_status();
                    String corp_code = vipFsend.getCorp_code();
                    String sms_code = vipFsend.getSms_code();
                    if (check_status.equals("Y")){
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setMessage(sms_code+"已通过审核，无需重复操作");
                        return dataBean.getJsonStr();
                    }
                    if (check_status.equals("已停止")){
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setMessage(sms_code+"已停止，不可通过审核");
                        return dataBean.getJsonStr();
                    }
                    if (check_status != null && check_status.equals("N")){
                        String status = vipFsendService.checkVipFsend(vipFsend,user_code);
                        if (!status.equals(Common.DATABEAN_CODE_SUCCESS)){
                            redisClient.set("VipFsend Status"+corp_code+sms_code,"Y");
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId(id);
                            dataBean.setMessage(status);
                            return dataBean.getJsonStr();
                        }
                    }
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/outExecl", method = RequestMethod.POST)
    @ResponseBody
    public  String outExecl(HttpServletRequest request, HttpServletResponse response){
        DataBean dataBean=new DataBean();
        String errormessage = "数据异常，导出失败";
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();

            MongoTemplate mongoTemplate= this.mongoDBClient.getMongoTemplate();
            DBCollection dbCollection=mongoTemplate.getCollection(CommonValue.table_vip_batchsend_message);

            JSONObject jsonObject=JSON.parseObject(message);
            String vipFsend_id = jsonObject.get("id").toString().trim();
           // int page_num=Integer.parseInt(jsonObject.getString("page_num"));
           // int page_size=Integer.parseInt(jsonObject.getString("page_size"));

            VipFsend vipFsend = vipFsendService.getVipFsendInfoById(Integer.parseInt(vipFsend_id));
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
            } else if(StringUtils.isNotBlank(search)) {
                BasicDBObject basicDBObject1=new BasicDBObject();
                BasicDBList basicDBList1=new BasicDBList();
                basicDBList1.add(new BasicDBObject("vip_name",new BasicDBObject("$regex", search)));
                basicDBList1.add(new BasicDBObject("cardno",new BasicDBObject("$regex", search)));
                basicDBObject1.put("$or",basicDBList1);
                basicDBList.add(basicDBObject1);
            }
            basicDBObject.put("$and",basicDBList);
            DBCursor dbCursor=dbCollection.find(basicDBObject).sort(new BasicDBObject("message_date",-1));
//            int total = dbCursor.count();
////            int start_line = (page_num-1) * page_size + 1;
////            int end_line = page_num*page_size;
////            if (total < page_num*page_size)
////                end_line = total;
//            DBCursor dbCursor1=MongoUtils.sortAndPage(dbCursor,page_num,page_size,"message_date",-1);
            List list= MongoUtils.dbCursorToList(dbCursor);
            JSONArray jsonArray=new JSONArray();
            for (int i = 0; i <list.size() ; i++) {
                Map map= (Map) list.get(i);
                String send_type= map.get("is_send").toString();
                if(send_type.equals("N")){
                    map.put("is_send","未发送");
                }else if(send_type.equals("Y")){
                    map.put("is_send","已发送");
                }
                JSONObject jsonObject1=new JSONObject();
                jsonObject1.putAll(map);
                jsonArray.add(map);
            }
            LinkedHashMap linkMap=new LinkedHashMap();
            linkMap.put("vip_id","会员编号");
            linkMap.put("vip_name","会员名称");
            linkMap.put("vip_phone","手机号");
            linkMap.put("cardno","会员卡号");
            linkMap.put("is_send","发送状态");
            linkMap.put("message_date","发送时间");
            String pathname= OutExeclHelper.OutExecl2(jsonArray.toString(),jsonArray,linkMap,response,request,"群发消息_接收会员");
            JSONObject result2 = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result2.put("path", "lupload/" + pathname);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result2.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return  dataBean.getJsonStr();
    }


    /**
     * 群发消息
     * <p>
     * 审核通过
     */
    @RequestMapping(value = "/termFsend", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String termFsend(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_fsend_id = jsonObject.get("id").toString();
            String[] ids = vip_fsend_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                VipFsend vipFsend = vipFsendService.getVipFsendInfoById(Integer.parseInt(ids[i]));
                if (vipFsend != null){
                    String sms_code = vipFsend.getSms_code();
                    String corp_code = vipFsend.getCorp_code();
                    String check_status = vipFsend.getCheck_status();
                    if (check_status.equals("N")){
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setMessage(sms_code+"为未审核，无需停止");
                        return dataBean.getJsonStr();
                    }
                    redisClient.set("VipFsend Status"+corp_code+sms_code,"N");

                    vipFsend.setCheck_status("已停止");
                    vipFsend.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
                    vipFsend.setModifier(user_code);
                    vipFsendService.updateVipFsend(vipFsend);

                    scheduleJobService.delete(sms_code,sms_code);
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

}
