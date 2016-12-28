package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.VipGroup;
import com.bizvane.ishop.service.*;
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
import java.util.*;

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
    @Autowired
    private BaseService baseService;
    @Autowired
    private TableManagerService tableManagerService;

    String id;


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

            VipGroup vipGroup = vipGroupService.getVipGroupById(Integer.parseInt(id));
            if (vipGroup != null) {
                String group_type = vipGroup.getGroup_type();
                String group_condition = vipGroup.getGroup_condition();
                if (group_type.equals("define")){
                    JSONArray condition = JSONArray.parseArray(group_condition);
                    List<TableManager> tableManagers = tableManagerService.selVipScreenValue();
                    for (int i = 0; i < condition.size(); i++) {
                        JSONObject condition_obj = condition.getJSONObject(i);
                        String key = condition_obj.getString("key");
                        String type = condition_obj.getString("type");
                        String value = condition_obj.getString("value");
                        if (type.equals("text") && value.equals("")){
                            //筛选值为空
                            continue;
                        }else if (type.equals("json") && value.equals("{}")){
                            //筛选值为空
                            continue;
                        }else {
                            //根据key值，找出其对应name
                            for (int j = 0; j < tableManagers.size(); j++) {
                                if (key.equals(tableManagers.get(j).getFilter_weight())){
                                    String key_name = tableManagers.get(j).getShow_name();
                                    condition_obj.put("name",key_name);
                                    break;}
                            }
                        }
                    }
                    vipGroup.setGroup_condition(condition.toJSONString());
                }
            }
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
            org.json.JSONObject group_obj = new org.json.JSONObject(message);
            String user_id = request.getSession().getAttribute("user_code").toString();
            VipGroup vipGroup = WebUtils.JSON2Bean(group_obj,VipGroup.class);

            String vip_group_code = "VG"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
            vipGroup.setVip_group_code(vip_group_code);
            String result = vipGroupService.insert(vipGroup, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                JSONObject jsonObject = JSONObject.parseObject(message);
                String corp_code = jsonObject.get("corp_code").toString().trim();
                VipGroup vipGroup1 = vipGroupService.getVipGroupByCode(corp_code,vip_group_code,jsonObject.get("isactive").toString());
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(String.valueOf(vipGroup1.getId()));


                //----------------行为日志------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "会员管理_会员分组";
                String action = Common.ACTION_ADD;
                String t_corp_code = corp_code;
                String t_code = vip_group_code;
                String t_name = action_json.get("vip_group_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
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
            org.json.JSONObject group_obj = new org.json.JSONObject(message);
            VipGroup vipGroup = WebUtils.JSON2Bean(group_obj,VipGroup.class);
            String result = vipGroupService.update(vipGroup, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("edit success");

                //----------------行为日志开始------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "会员管理_会员分组";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("vip_group_code").toString();
                String t_name = action_json.get("vip_group_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
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
                VipGroup vipGroupById = vipGroupService.getVipGroupById(Integer.valueOf(ids[i]));
                vipGroupService.delete(Integer.valueOf(ids[i]));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");

                //----------------行为日志开始------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "会员管理_会员分组";
                String action = Common.ACTION_DEL;
                String t_corp_code = vipGroupById.getCorp_code();
                String t_code = vipGroupById.getVip_group_code();
                String t_name = vipGroupById.getVip_group_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
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

    /**
     * 验证会员分组名称唯一性
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/vipGroupNameExist", method = RequestMethod.POST)
    @ResponseBody
    public String vipGroupNameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_group_name = jsonObject.get("vip_group_name").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            VipGroup vipGroup = vipGroupService.getVipGroupByName(corp_code, vip_group_name, Common.IS_ACTIVE_Y);
            if (vipGroup != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("会员分组名称已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("会员分组名称不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);

            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);


        }
        return dataBean.getJsonStr();
    }

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
                list = vipGroupService.getAllVipGroupByPage(page_number, page_size,"", search_value);
            }else {
                list = vipGroupService.getAllVipGroupByPage(page_number, page_size, corp_code, search_value);
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

            JSONObject result = new JSONObject();
            PageInfo<VipGroup> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = vipGroupService.getAllVipGrouScreen(page_number, page_size, "",map);
            }else {
                list = vipGroupService.getAllVipGrouScreen(page_number, page_size,corp_code,map);
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
                    list = vipGroupService.getAllVipGroupByPage(1, Common.EXPORTEXECLCOUNT,"", search_value);
                }else {
                    list = vipGroupService.getAllVipGroupByPage(1, Common.EXPORTEXECLCOUNT, corp_code,search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = vipGroupService.getAllVipGrouScreen(1, Common.EXPORTEXECLCOUNT, "", map);
                }else {
                    list = vipGroupService.getAllVipGrouScreen(1, Common.EXPORTEXECLCOUNT,corp_code,map);
                }
            }
            List<VipGroup> vipGroups = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(vipGroups);
            if (vipGroups.size() >= Common.EXPORTEXECLCOUNT) {
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
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            String search_value = "";
            if (jsonObject.containsKey("search_value"))
                search_value = jsonObject.get("search_value").toString();

            JSONObject result = new JSONObject();
            List<VipGroup> vipGroups ;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                corp_code = jsonObject.getString("corp_code");
            }
            vipGroups = vipGroupService.selectCorpVipGroups(corp_code,search_value);

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

    /**
     * 查看分组下会员
     * @param request
     * @return
     */
    @RequestMapping(value = "/groupVips", method = RequestMethod.POST)
    @ResponseBody
    public String groupVips(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String id = jsonObject.get("id").toString();
            String type = jsonObject.get("type").toString();
            VipGroup vipGroup = vipGroupService.getVipGroupById(Integer.parseInt(id));
            String corp_code = vipGroup.getCorp_code();

            DataBox dataBox = new DataBox();
            if (type.equals("list")){

                String group_type = vipGroup.getGroup_type();
                if (group_type.equals("define")){
                    String group_condition = vipGroup.getGroup_condition();
                    JSONArray screen = JSONArray.parseArray(group_condition);
                    dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,page_num,page_size,request);
                }else {
                    Map datalist = iceInterfaceService.vipBasicMethod1(page_size,page_num,corp_code,request);
                    dataBox = iceInterfaceService.iceInterfaceV2("AnalysisAllVip", datalist);
                }

            }

            String result = dataBox.data.get("message").value;
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();

    }
}
