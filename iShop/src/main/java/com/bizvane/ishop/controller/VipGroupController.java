package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.VipGroup;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.service.VipGroupService;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/9/1.
 */

@Controller
@RequestMapping("/vipGroup")
public class VipGroupController {
    private static final Logger logger = Logger.getLogger(VipGroupController.class);
    @Autowired
    private VipGroupService vipGroupService;
    @Autowired
    IceInterfaceService iceInterfaceService;

    String id;

    /**
     * 列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String vipGroupList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();

        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            PageInfo<VipGroup> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = vipGroupService.getAllVipGroupByPage(page_number, page_size,"","", "");
            } else if (role_code.equals(Common.ROLE_GM)){
                list = vipGroupService.getAllVipGroupByPage(page_number, page_size, corp_code,"", "");
            }else {
                list = vipGroupService.getAllVipGroupByPage(page_number, page_size, corp_code,user_code, "");
            }
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 根据id查看
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String selectById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String id = jsonObject.get("id").toString();

//            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
//            int vip_count = 0;
            VipGroup vipGroup = vipGroupService.getVipGroupById(Integer.parseInt(id));
//            if (vipGroup != null) {
//                String corp_code = vipGroup.getCorp_code();
//                String vip_group_code = vipGroup.getVip_group_code();
//
//                BasicDBObject dbObject=new BasicDBObject();
//                dbObject.put("vip_group_code",vip_group_code);
//                dbObject.put("corp_code",corp_code);
//                DBCursor dbCursor= cursor.find(dbObject);
//                vip_count = dbCursor.size();
//                vipGroup.setVip_count(vip_count);
//            }
            data = JSON.toJSONString(vipGroup);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(data);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 新增
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addVipGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--vipGroup add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String user_id = request.getSession().getAttribute("user_code").toString();
            String result = vipGroupService.insert(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                JSONObject jsonObject = JSONObject.parseObject(message);
                String vip_group_code = jsonObject.get("vip_group_code").toString().trim();
                String corp_code = jsonObject.get("corp_code").toString().trim();
                VipGroup vipGroup = vipGroupService.getVipGroupByCode(corp_code,vip_group_code,jsonObject.get("isactive").toString());
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(String.valueOf(vipGroup.getId()));
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }

        return dataBean.getJsonStr();
    }

    /**
     * 编辑
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String updateVipGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json------updateVipGroup---------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = vipGroupService.update(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("edit success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
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
    public String deleteVipGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--------deleteVipGroup-------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vipGroup_id = jsonObject.get("id").toString();
            String[] ids = vipGroup_id.split(",");
            String msg = null;
            int count = 0;
            for (int i = 0; i < ids.length; i++) {
                logger.info("inter---------------" + Integer.valueOf(ids[i]));
                vipGroupService.delete(Integer.valueOf(ids[i]));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 验证会员分组编号唯一性
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/vipGroupCodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String vipGroupCodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_group_code = jsonObject.get("vip_group_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            VipGroup vipGroup = vipGroupService.getVipGroupByCode(corp_code, vip_group_code, Common.IS_ACTIVE_Y);
            if (vipGroup != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("会员分组编号已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("会员分组编号不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);

            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);


        }
        return dataBean.getJsonStr();


    }

//    /**
//     * 验证会员分组名称唯一性
//     *
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/vipGroupNameExist", method = RequestMethod.POST)
//    @ResponseBody
//    public String vipGroupNameExist(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            String message = jsonObj.get("message").toString();
//            JSONObject jsonObject = JSONObject.parseObject(message);
//            String vip_group_name = jsonObject.get("vip_group_name").toString();
//            String corp_code = jsonObject.get("corp_code").toString();
//            VipGroup vipGroup = vipGroupService.getVipGroupByName(corp_code, vip_group_name, Common.IS_ACTIVE_Y);
//            if (vipGroup != null) {
//                dataBean.setId(id);
//                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                dataBean.setMessage("会员分组名称已被使用");
//            } else {
//                dataBean.setId(id);
//                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                dataBean.setMessage("会员分组名称不存在");
//            }
//        } catch (Exception ex) {
//            dataBean.setId(id);
//
//            dataBean.setMessage(ex.getMessage() + ex.toString());
//            logger.info(ex.getMessage() + ex.toString());
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//
//
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 搜索
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String searchVipGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            JSONObject result = new JSONObject();
            PageInfo<VipGroup> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = vipGroupService.getAllVipGroupByPage(page_number, page_size,"","", search_value);
            } else if (role_code.equals(Common.ROLE_GM)){
                list = vipGroupService.getAllVipGroupByPage(page_number, page_size, corp_code,"",search_value);
            }else {
                list = vipGroupService.getAllVipGroupByPage(page_number, page_size, corp_code,user_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();

    }

    /**
     * 筛选
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String vipGroupScreen(HttpServletRequest request) {

        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            JSONObject result = new JSONObject();
            PageInfo<VipGroup> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = vipGroupService.getAllVipGrouScreen(page_number, page_size, "", "",map);
            } else if (role_code.equals(Common.ROLE_GM)){
                list = vipGroupService.getAllVipGrouScreen(page_number, page_size,corp_code,"",map);
            }else {
                list = vipGroupService.getAllVipGrouScreen(page_number, page_size,corp_code,user_code,map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage() + ex.toString());
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
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<VipGroup> list = null;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = vipGroupService.getAllVipGroupByPage(1, 30000,"","", search_value);
                } else if (role_code.equals(Common.ROLE_GM)){
                    list = vipGroupService.getAllVipGroupByPage(1, 30000, corp_code,"",search_value);
                }else {
                    list = vipGroupService.getAllVipGroupByPage(1, 30000, corp_code,user_code,search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = vipGroupService.getAllVipGrouScreen(1, 30000, "", "",map);
                } else if (role_code.equals(Common.ROLE_GM)){
                    list = vipGroupService.getAllVipGrouScreen(1, 30000,corp_code,"",map);
                }else {
                    list = vipGroupService.getAllVipGrouScreen(1, 30000,corp_code,user_code,map);
                }
            }
            List<VipGroup> vipGroups = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(vipGroups);
            if (vipGroups.size() >= 29999) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json, vipGroups, map, response, request);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /***
     * 根据所选导购
     * 获取会员列表
     */
    @RequestMapping(value = "/allVip", method = RequestMethod.POST)
    @ResponseBody
    public String allVip(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

//            String corp_code = jsonObject.get("corp_code").toString();
            String vip_group_id = jsonObject.get("vip_group_id").toString();

            String vip_ids = "";
            VipGroup vipGroup = vipGroupService.getVipGroupById(Integer.parseInt(vip_group_id));

//            VipGroup vipGroup = vipGroupService.getVipGroupByCode(corp_code,vip_group_code,Common.IS_ACTIVE_Y);
            if (vipGroup != null && vipGroup.getVip_ids() != null && !vipGroup.getVip_ids().equals("")){
                vip_ids = vipGroup.getVip_ids();
                vip_ids = vip_ids.replace(Common.SPECIAL_HEAD,"");
            }

            //获取会员列表
            Map datalist = iceInterfaceService.vipBasicMethod(jsonObject,request);
            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisAllVip", datalist);
//            logger.info("-------vip列表" + dataBox.data.get("message").value);
            String result = dataBox.data.get("message").value;

            JSONObject obj = JSON.parseObject(result);
            String vipLists = obj.get("all_vip_list").toString();
            JSONArray array = JSONArray.parseArray(vipLists);
            //获取会员的分组标识
            JSONArray new_array = vipGroupService.checkVipsGroup(array,vip_ids);
            obj.put("all_vip_list",new_array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(obj.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员分组批量分配会员
     * 保存
     */
    @RequestMapping(value = "/saveVips", method = RequestMethod.POST)
    @ResponseBody
    public String saveVips(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            int vips_group_id = Integer.valueOf(jsonObject.get("vip_group_id").toString());
            String vip_group_code = jsonObject.get("vip_group_code").toString();
            String vip_group_name = jsonObject.get("vip_group_name").toString();
            String vip_group_remark = jsonObject.get("vip_group_remark").toString();
            String vips_choose = jsonObject.get("choose").toString();
            String vips_quit = jsonObject.get("quit").toString();

            VipGroup vipGroup = vipGroupService.getVipGroupById(vips_group_id);
//            String vip_ids = "";
            String vip_ids = vipGroup.getVip_ids();
            if (vip_ids == null){
                vip_ids = "";
            }
            if (!vips_choose.equals("")){
                String[] choose = vips_choose.split(",");
                for (int i = 0; i < choose.length; i++) {
                    vip_ids = vip_ids + Common.SPECIAL_HEAD + choose[i] + ",";
                }
            }
           if (!vips_quit.equals("")){
               String[] quit = vips_quit.split(",");
               for (int i = 0; i < quit.length; i++) {
                   vip_ids = vip_ids.replace(Common.SPECIAL_HEAD+quit[i]+",","");
               }
           }

            vipGroup.setVip_ids(vip_ids);
            vipGroupService.updateVipGroup(vipGroup);
            vipGroup = vipGroupService.getVipGroupById(vips_group_id);

            JSONObject obj = new JSONObject();
            obj.put("vip_group_code",vip_group_code);
            obj.put("vip_group_name",vip_group_name);
            obj.put("vip_group_remark",vip_group_remark);
            obj.put("vip_count",vipGroup.getVip_count());
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

//    /**
//     * 会员分组批量分配会员
//     * 保存mongodb
//     */
//    @RequestMapping(value = "/saveVips", method = RequestMethod.POST)
//    @ResponseBody
//    public String saveVips(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String param = request.getParameter("param");
//            logger.info("json---------------" + param);
//            JSONObject jsonObj = JSONObject.parseObject(param);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject jsonObject = JSONObject.parseObject(message);
//
//            //mongodb
//            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
//            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
//
//            String vip_group_code = jsonObject.get("vip_group_code").toString();
//            String vips_choose = jsonObject.get("choose").toString();
//            String vips_quit = jsonObject.get("quit").toString();
//
//            JSONArray array = JSONArray.parseArray(vips_choose);
//            for (int i = 0; i < array.size(); i++) {
//                String vip = array.get(i).toString();
//                JSONObject vip_info = JSONObject.parseObject(vip);
//                String vip_id = vip_info.get("vip_id").toString();
//                String corp_code = vip_info.get("corp_code").toString();
//                String card_no = vip_info.get("card_no").toString();
//                String phone = vip_info.get("phone").toString();
//
//                Map keyMap = new HashMap();
//                keyMap.put("_id", corp_code+card_no);
//                BasicDBObject queryCondition = new BasicDBObject();
//                queryCondition.putAll(keyMap);
//                DBCursor dbCursor1 = cursor.find(queryCondition);
//                if (dbCursor1.size()>0){
//                    //记录存在，更新
//                    String vip_group_code1 = "";
//                    DBObject object = dbCursor1.next();
//                    if (object.containsField("vip_group_code"))
//                        vip_group_code1 = object.get("vip_group_code").toString();
//                    DBObject updateCondition=new BasicDBObject();
//                    updateCondition.put("_id", corp_code+card_no);
//                    DBObject updatedValue=new BasicDBObject();
//                    updatedValue.put("vip_group_code", vip_group_code1 + vip_group_code + ",");
//                    DBObject updateSetValue=new BasicDBObject("$set",updatedValue);
//                    cursor.update(updateCondition, updateSetValue);
//                }else {
//                    //记录不存在，插入
//                    DBObject saveData = new BasicDBObject();
//                    saveData.put("_id", corp_code + card_no);
//                    saveData.put("vip_id", vip_id);
//                    saveData.put("corp_code", corp_code);
//                    saveData.put("card_no", card_no);
//                    saveData.put("phone", phone);
//                    saveData.put("corp_code", corp_code);
//                    saveData.put("vip_group_code", vip_group_code+",");
//                    cursor.save(saveData);
//                }
//            }
//
//            JSONArray array1 = JSONArray.parseArray(vips_quit);
//            for (int i = 0; i < array1.size(); i++) {
//                String vip = array1.get(i).toString();
//                JSONObject vip_info = JSONObject.parseObject(vip);
//                String vip_id = vip_info.get("vip_id").toString();
//                String corp_code = vip_info.get("corp_code").toString();
//                String card_no = vip_info.get("card_no").toString();
//                String phone = vip_info.get("phone").toString();
//
//                Map keyMap = new HashMap();
//                keyMap.put("_id", corp_code+card_no);
//                BasicDBObject queryCondition = new BasicDBObject();
//                queryCondition.putAll(keyMap);
//                DBCursor dbCursor1 = cursor.find(queryCondition);
//                if (dbCursor1.size()>0){
//                    //记录存在，更新
//                    String vip_group_code1 = "";
//                    DBObject object = dbCursor1.next();
//                    if (object.containsField("vip_group_code"))
//                        vip_group_code1 = object.get("vip_group_code").toString();
//                    vip_group_code1 = vip_group_code1.replace(vip_group_code+",","");
//                    DBObject updateCondition=new BasicDBObject();
//                    updateCondition.put("_id", corp_code+card_no);
//                    DBObject updatedValue=new BasicDBObject();
//                    updatedValue.put("vip_group_code", vip_group_code1);
//                    DBObject updateSetValue=new BasicDBObject("$set",updatedValue);
//                    cursor.update(updateCondition, updateSetValue);
//                }
//            }
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage("save success");
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage());
//            logger.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 获取企业下会员分组
     * @param request
     * @return
     */
    @RequestMapping(value = "/getCorpGroups", method = RequestMethod.POST)
    @ResponseBody
    public String getCorpGroups(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String search_value = "";
            if (jsonObject.containsKey("searchValue"))
                search_value = jsonObject.get("searchValue").toString();

            JSONObject result = new JSONObject();
            List<VipGroup> vipGroups = vipGroupService.selectCorpVipGroups(corp_code,search_value);
            result.put("list", JSON.toJSONString(vipGroups));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();

    }

}
