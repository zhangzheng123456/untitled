package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.System;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhou on 2016/6/6.
 */
/*
*用户及权限
*/
@Controller
@RequestMapping("/user/group")
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private BaseService baseService;
    @Autowired
    VipActivityService vipActivityService;
    @Autowired
    VipActivityDetailService vipActivityDetailService;

    private static Logger logger = LoggerFactory.getLogger((GroupController.class));
    String id;


    /**
     * 群组管理
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--area add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            Group group = new Group();
            Date now = new Date();

            group.setGroup_code(jsonObject.get("group_code").toString());
            group.setGroup_name(jsonObject.get("group_name").toString());
            group.setRole_code(jsonObject.get("role_code").toString());
            group.setCorp_code(jsonObject.get("corp_code").toString());
            group.setRemark(jsonObject.get("remark").toString());
            group.setCreated_date(Common.DATETIME_FORMAT.format(now));
            group.setCreater(user_id);
            group.setModified_date(Common.DATETIME_FORMAT.format(now));
            group.setModifier(user_id);
            group.setIsactive(jsonObject.get("isactive").toString());
            String result = groupService.insertGroup(group);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                Group group1 = groupService.selectByCode(group.getCorp_code(),group.getGroup_code(),group.getIsactive());
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(String.valueOf(group1.getId()));

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
                String function = "权限管理_群组管理";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("group_code").toString();
                String t_name = action_json.get("group_name").toString();
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
     * 群组管理
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--area edit-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            Group group = new Group();
            Date now = new Date();
            group.setId(Integer.parseInt(jsonObject.get("id").toString()));
            group.setGroup_code(jsonObject.get("group_code").toString());
            group.setGroup_name(jsonObject.get("group_name").toString());
            group.setRole_code(jsonObject.get("role_code").toString());
            group.setCorp_code(jsonObject.get("corp_code").toString());
            group.setRemark(jsonObject.get("remark").toString());
            group.setModifier(user_id);
            group.setModified_date(Common.DATETIME_FORMAT.format(now));
            group.setIsactive(jsonObject.get("isactive").toString());
            String result = groupService.updateGroup(group);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("edit success");


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
                String function = "权限管理_群组管理";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("group_code").toString();
                String t_name = action_json.get("group_name").toString();
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
            String area_id = jsonObject.get("id").toString();
            String[] ids = area_id.split(",");
            String msg = null;
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                Group group = groupService.getGroupById(Integer.valueOf(ids[i]));
                if (group != null) {
                    String group_code = group.getGroup_code();
                    String corp_code = group.getCorp_code();
                    List<User> users = userService.selectGroupUser(corp_code, group_code);
                    if (users.size() > 0) {
                        msg = "群组"+group_code+"下有所属员工，请先处理群组下员工再删除";
                        break;
                    }
                }

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
                String function = "权限管理_群组管理";
                String action = Common.ACTION_DEL;
                String t_corp_code = operation_corp_code;
                String t_code = group.getGroup_code();
                String t_name = group.getGroup_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
            if (msg == null) {
                for (int i = 0; i < ids.length; i++) {
                    Group group = groupService.getGroupById(Integer.valueOf(ids[i]));
                    if (group != null) {
                        String group_code = group.getGroup_code();
                        String corp_code = group.getCorp_code();
                        groupService.deleteGroup(Integer.valueOf(ids[i]), group_code, corp_code);
                    }
                }
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("删除成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            }
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
     * 群组管理
     * 选择群组
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");

            logger.info("json-select-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String group_id = jsonObject.get("id").toString();
            Group group = groupService.getGroupById(Integer.parseInt(group_id));
            String corp_code = group.getCorp_code();
            String role_code = group.getRole_code();
            String group_code = group.getGroup_code();
            //群组用户个数
            List<User> user_counts = userService.selectGroupUser(corp_code, group_code);

            int user_count = user_counts.size();
            //获取群组角色的权限
            JSONArray role_privilege = functionService.selectRolePrivilege(role_code);
            //获取群组自定义的权限
            if (role_code.equals(Common.ROLE_CM)){
                corp_code = "C00000";
            }
            JSONArray group_privilege = functionService.selectGroupPrivilege(corp_code, group_code);
            //群组权限个数
            int privilege_count = role_privilege.size() + group_privilege.size();

            JSONObject group_info = new JSONObject();
            group_info.put("user_count", user_count);
            group_info.put("privilege_count", privilege_count);
            group_info.put("data", JSON.toJSONString(group));
            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(group_info.toString());
        } catch (Exception e) {
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage(e.getMessage());
        }
        logger.info("info-----" + bean.getJsonStr());
        return bean.getJsonStr();
    }

    /**
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
            PageInfo<Group> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = groupService.getGroupAll(page_number, page_size, "", "", search_value);
            }else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
               String corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                list = groupService.getGroupAll(page_number, page_size, corp_code, "", "");

                //   list = groupService.getGroupAll(page_number, page_size, "", "", search_value,manager_corp);
            } else if (role_code.equals(Common.ROLE_GM)) {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                list = groupService.getGroupAll(page_number, page_size, corp_code, "", "");
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                list = groupService.getGroupAll(page_number, page_size, corp_code, role_code, search_value);
            }
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
     * 页面筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
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
//            String screen = jsonObject.get("screen").toString();
//            JSONObject jsonScreen = new JSONObject(screen);
            Map<String,String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Group> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = groupService.getAllGroupScreen(page_number, page_size, "", "", map);
            } else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                String corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>"+corp_code);
                list = groupService.getAllGroupScreen(page_number, page_size, corp_code, "",map);

                //  list = groupService.getAllGroupScreen(page_number, page_size, "", "", map,manager_corp);
            } else if (role_code.equals(Common.ROLE_GM)) {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                list = groupService.getAllGroupScreen(page_number, page_size, corp_code, "",map);
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                list = groupService.getAllGroupScreen(page_number, page_size, corp_code, role_code, map);
            }
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
     * 群组管理之
     * 角色选择
     */
    @RequestMapping(value = "/role", method = RequestMethod.POST)
    @ResponseBody
    public String roleSelect(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            List<Role> roles;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                roles = roleService.selectAll("");
//            } else {
                roles = roleService.selectCorpRole(role_code);
//            }
            JSONArray array = new JSONArray();
            for (int i = 0; i < roles.size(); i++) {
                Role role = roles.get(i);
                String role_code1 = role.getRole_code();
                String role_name = role.getRole_name();
                JSONObject obj = new JSONObject();
                obj.put("role_code", role_code1);
                obj.put("role_name", role_name);
                array.add(obj);
            }
            JSONObject result = new JSONObject();
            result.put("role", array);
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
     * 群组管理之
     * 编辑群组信息之
     * 查看名单
     */
    @RequestMapping(value = "/check_name", method = RequestMethod.POST)
    @ResponseBody
    public String groupCheckName(HttpServletRequest request) {
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
            String group_code = jsonObject.get("group_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            PageInfo<User> users = userService.selectGroupUser(page_number, page_size, corp_code, group_code,search_value);
            JSONObject result = new JSONObject();

            result.put("list", JSON.toJSONString(users));
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
     * 群组管理之
     * 编辑群组信息
     * 之查看权限
     */
    @RequestMapping(value = "/check_power", method = RequestMethod.POST)
    @ResponseBody
    public String groupCheckPower(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String login_user_code = request.getSession().getAttribute("user_code").toString();
            String login_corp_code = request.getSession().getAttribute("corp_code").toString();
            String login_role_code = request.getSession().getAttribute("role_code").toString();
            String login_group_code = request.getSession().getAttribute("group_code").toString();

            String search_value = "";
            if (jsonObject.containsKey("searchValue")) {
                search_value = jsonObject.get("searchValue").toString();
            }
            //获取登录用户的所有权限
            List<Function> funcs = functionService.selectAllPrivilege(login_corp_code,login_role_code, login_user_code, login_group_code, search_value);

            String group_code = jsonObject.get("group_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Group group = groupService.selectByCode(corp_code, group_code, Common.IS_ACTIVE_Y);
            String role_code = group.getRole_code();

            //获取群组角色的权限
            JSONArray role_privilege = functionService.selectRolePrivilege(role_code);

            //获取群组自定义的权限
            if (role_code.equals(Common.ROLE_CM)){
                corp_code = "C00000";
            }
            JSONArray group_privilege = functionService.selectGroupPrivilege(corp_code, group_code);

            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(funcs));
            result.put("die", role_privilege);
            result.put("live", group_privilege);

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
     * 群组管理之
     * 编辑群组信息之
     * 查看权限之
     * 新增权限
     */
    @RequestMapping(value = "/check_power/save", method = RequestMethod.POST)
    @ResponseBody
    public String addGroupCheckPower(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_id = request.getSession().getAttribute("user_code").toString();

            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String list = jsonObject.get("list").toString();
            JSONArray array = JSONArray.parseArray(list);

            String group_code = jsonObject.get("group_code").toString();
            String master_code;
            if (jsonObject.containsKey("corp_code")) {
                String corp_code = jsonObject.get("corp_code").toString();
                master_code = corp_code +"G"+ group_code;
            } else {
                master_code = group_code;
            }
            String result = functionService.updatePrivilege(master_code, user_id, array);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/code_exist", method = RequestMethod.POST)
    @ResponseBody
    public String codeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_id = request.getSession().getAttribute("user_id").toString();

            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String group_code = jsonObject.get("group_code").toString();
            Group group = groupService.selectByCode(corp_code, group_code, Common.IS_ACTIVE_Y);
            if (group == null) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("该群组编号已存在");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/name_exist", method = RequestMethod.POST)
    @ResponseBody
    public String name_exist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_id = request.getSession().getAttribute("user_id").toString();

            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String group_name = jsonObject.get("group_name").toString();
            Group group = groupService.selectByName(corp_code, group_name, Common.IS_ACTIVE_Y);
            if (group == null) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("该群组名称已存在");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
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
        String errormessage="数据异常，导出失败";
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<Group> list;
            if(screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = groupService.getGroupAll(1, Common.EXPORTEXECLCOUNT, "", "", search_value);
                } else if (role_code.equals(Common.ROLE_GM)) {
                    list = groupService.getGroupAll(1, Common.EXPORTEXECLCOUNT, corp_code, "", search_value);
                } else {
                    list = groupService.getGroupAll(1, Common.EXPORTEXECLCOUNT, corp_code, role_code, search_value);
                }
            }else{
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = groupService.getAllGroupScreen(1, Common.EXPORTEXECLCOUNT, "", "", map);
                } else if (role_code.equals(Common.ROLE_GM)) {
                    list = groupService.getAllGroupScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "",map);
                } else {
                    list = groupService.getAllGroupScreen(1, Common.EXPORTEXECLCOUNT, corp_code, role_code, map);
                }
            }
            List<Group> groups = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(groups);
            if(groups.size()>=Common.EXPORTEXECLCOUNT){
                errormessage="导出数据过大";
                int i=9/0;
            }
            LinkedHashMap<String,String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json,groups, map, response, request,"权限群组");
            JSONObject result = new JSONObject();
            if(pathname==null||pathname.equals("")){
                errormessage="数据异常，导出失败";
                int a=8/0;
            }
            result.put("path",JSON.toJSONString("lupload/"+pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /***
     * Execl增加
     */
    @RequestMapping(value = "/addByExecl", method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional()
    public String addByExecl(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException {
        DataBean dataBean = new DataBean();
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        String result = "";
        Workbook rwb=null;
        try {
            rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = 5;//得到所有的列
            int rows = rs.getRows();//得到所有的行
//            int actualRows = LuploadHelper.getRightRows(rs);
//            if(actualRows != rows){
//                if(rows-actualRows==1){
//                    result = "：第"+rows+"行存在空白行,请删除";
//                }else{
//                    result = "：第"+(actualRows+1)+"行至第"+rows+"存在空白行,请删除";
//                }
//                int i = 5 / 0;
//            }
            if(rows<4){
                result="：请从模板第4行开始插入正确数据";
                int i=5/0;
            }
            if (rows > 9999) {
                result = "：数据量过大，导入失败";
                int i = 5 / 0;
            }
            Cell[] column3 = rs.getColumn(0);
            Pattern pattern1 = Pattern.compile("C\\d{5}");
            if(!role_code.equals(Common.ROLE_SYS)){
                for (int i=3;i<column3.length;i++){
                    if(column3[i].getContents().toString().trim().equals("")){
                        continue;
                    }
                    if(!column3[i].getContents().toString().trim().equals(corp_code)){
                        result = "：第" + (i + 1) + "行企业编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                    Matcher matcher = pattern1.matcher(column3[i].getContents().toString().trim());
                    if (matcher.matches() == false) {
                        result = "：第" + (i + 1) + "行企业编号格式有误";
                        int b = 5 / 0;
                        break;
                    }
                }
            }
            for (int i = 3; i < column3.length; i++) {
                if(column3[i].getContents().toString().trim().equals("")){
                    continue;
                }
                Matcher matcher = pattern1.matcher(column3[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行企业编号格式有误";
                    int b = 5 / 0;
                    break;
                }
                Corp corp = corpService.selectByCorpId(0, column3[i].getContents().toString().trim(),Common.IS_ACTIVE_Y);
                if (corp == null) {
                    result = "：第" + (i + 1) + "行企业编号不存在";
                    int b = 5 / 0;
                    break;
                }

            }
            Cell[] column2 = rs.getColumn(1);
            for (int i = 3; i < column2.length; i++) {
                if(column2[i].getContents().toString().trim().equals("")){
                    continue;
                }
                if (!column2[i].getContents().toString().trim().equals("R2000") && !column2[i].getContents().toString().trim().equals("R3000") && !column2[i].getContents().toString().trim().equals("R4000")) {
                    result = "：第" + (i + 1) + "行角色编号有误";
                    int b = 5 / 0;
                    break;
                }
            }
            String onlyCell1 = LuploadHelper.CheckOnly(rs.getColumn(2));
            if(onlyCell1.equals("存在重复值")){
                result = "：Execl中群组编号存在重复值";
                int b = 5 / 0;
            }
            String onlyCell2 = LuploadHelper.CheckOnly(rs.getColumn(3));
            if(onlyCell2.equals("存在重复值")){
                result = "：Execl中群组名称存在重复值";
                int b = 5 / 0;
            }
            Cell[] column = rs.getColumn(2);
            Pattern pattern = Pattern.compile("G\\d{4}");
            for (int i = 3; i < column.length; i++) {
                if(column[i].getContents().toString().trim().equals("")){
                    continue;
                }
                Matcher matcher = pattern.matcher(column[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行群组编号格式有误";
                    int b = 5 / 0;
                    break;
                }
                Group group = groupService.selectByCode(column3[i].getContents().toString().trim(), column[i].getContents().toString().trim(), "");
                if (group != null) {
                    result = "：第" + (i + 1) + "行群组编号已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column1 = rs.getColumn(3);
            for (int i = 3; i < column1.length; i++) {

                if(column1[i].getContents().toString().trim().equals("")){
                    continue;
                }
                Group group = groupService.selectByName(column3[i].getContents().toString().trim(), column1[i].getContents().toString().trim(), "");
                if (group != null) {
                    result = "：第" + (i + 1) + "行群组名称已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            ArrayList<Group> groups=new ArrayList<Group>();
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    Group group = new Group();
                    String cellCorp = rs.getCell(j++, i).getContents().toString().trim();
                    String role_codeExecl = rs.getCell(j++, i).getContents().toString().trim();
                    String group_code = rs.getCell(j++, i).getContents().toString().trim();
                    String group_name = rs.getCell(j++, i).getContents().toString().trim();
                    String isactive = rs.getCell(j++, i).getContents().toString().trim();
                    if(cellCorp.equals("") && role_codeExecl.equals("") && role_codeExecl.equals("") && group_code.equals("") && group_name.equals("") ){
                        continue;
                    }
                    if(cellCorp.equals("")||role_codeExecl.equals("") || role_codeExecl.equals("") || group_code.equals("") || group_name.equals("") ){
                        result = "：第"+(i+1)+"行信息不完整,请参照Execl中对应的批注";
                        int a=5/0;
                    }
                    if(!role_code.equals(Common.ROLE_SYS)){
                        group.setCorp_code(corp_code);
                    }else{
                        group.setCorp_code(cellCorp);
                    }
                    group.setRole_code(role_codeExecl);
                    group.setGroup_code(group_code);
                    group.setGroup_name(group_name);
                    if (isactive.toUpperCase().equals("N")) {
                        group.setIsactive("N");
                    } else {
                        group.setIsactive("Y");
                    }
                    groups.add(group);
                //    result = groupService.insertGroup(group);
                }
            }
            for (Group group: groups
                 ) {
                result = groupService.insertGroup(group);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(result);
        }finally {
            if(rwb!=null){
                rwb.close();
            }
            System.gc();
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/api/InputActivity", method = RequestMethod.GET)
    @ResponseBody
    public String apiInputActivity(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int count = 0;
            String corp_code = "C10183";
            String activity_themes = "开卡送券A188\n" +
                    "开卡送券A260\n" +
                    "开卡送券A004\n" +
                    "开卡送券A282\n" +
                    "开卡送券A286\n" +
                    "开卡送券A236\n" +
                    "开卡送券A144\n" +
                    "开卡送券A272\n" +
                    "开卡送券A204\n" +
                    "开卡送券A074\n" +
                    "开卡送券A141\n" +
                    "开卡送券A268\n" +
                    "开卡送券A275\n" +
                    "开卡送券A293\n" +
                    "开卡送券A081\n" +
                    "开卡送券A288\n" +
                    "开卡送券A189\n" +
                    "开卡送券A067\n" +
                    "开卡送券A183\n" +
                    "开卡送券A054\n" +
                    "开卡送券A143\n" +
                    "开卡送券A145\n" +
                    "开卡送券A250\n" +
                    "开卡送券A069\n" +
                    "开卡送券A252\n" +
                    "开卡送券A122\n" +
                    "开卡送券A109\n" +
                    "开卡送券A280\n" +
                    "开卡送券A299\n" +
                    "开卡送券A063\n" +
                    "开卡送券A271\n" +
                    "开卡送券A309\n" +
                    "开卡送券R009\n" +
                    "开卡送券A110\n" +
                    "开卡送券A056\n" +
                    "开卡送券A336\n" +
                    "开卡送券A337\n" +
                    "开卡送券A338\n" +
                    "开卡送券T005\n" +
                    "开卡送券A345\n" +
                    "开卡送券A346\n" +
                    "开卡送券A347\n" +
                    "开卡送券A348\n" +
                    "开卡送券A349\n" +
                    "开卡送券A350\n" +
                    "开卡送券A351\n" +
                    "开卡送券A352\n" +
                    "开卡送券A353\n" +
                    "开卡送券A354\n" +
                    "开卡送券A355\n" +
                    "开卡送券A356\n" +
                    "开卡送券A357\n" +
                    "开卡送券A358\n" +
                    "开卡送券A359\n" +
                    "开卡送券A361\n" +
                    "开卡送券A362\n" +
                    "开卡送券A363\n" +
                    "开卡送券A364\n" +
                    "开卡送券A365\n" +
                    "开卡送券A366\n" +
                    "开卡送券A367\n" +
                    "开卡送券A368\n" +
                    "开卡送券A369\n" +
                    "开卡送券A370\n" +
                    "开卡送券A372\n" +
                    "开卡送券A371\n" +
                    "开卡送券A373\n" +
                    "开卡送券A374\n" +
                    "开卡送券A375\n" +
                    "开卡送券A376\n" +
                    "开卡送券A360\n" +
                    "开卡送券A378\n" +
                    "开卡送券A380\n" +
                    "开卡送券A384\n" +
                    "开卡送券A385\n" +
                    "开卡送券A387\n" +
                    "开卡送券A391\n" +
                    "开卡送券A398\n" +
                    "开卡送券A402\n" +
                    "开卡送券A403\n" +
                    "开卡送券A404\n" +
                    "开卡送券A391\n" +
                    "开卡送券A409\n" +
                    "开卡送券A410\n" +
                    "开卡送券A414\n" +
                    "开卡送券A416\n" +
                    "开卡送券A417\n" +
                    "开卡送券A419\n" +
                    "开卡送券R001\n" +
                    "开卡送券R007\n" +
                    "开卡送券R010\n";

            String area_names = "常德郭华芬\n" +
                    "厦门越千阳发展有限公司\n" +
                    "佛山顺德区东汇成商贸有限公司\n" +
                    "南昌付伟俊\n" +
                    "温州市乐绮服装有限公司\n" +
                    "澳门noble group\n" +
                    "四川遂宁李海燕\n" +
                    "济南鑫钇服饰有限公司\n" +
                    "衡阳胡亚丽\n" +
                    "山东淄博(青岛左岸经贸)\n" +
                    "四川成都睿理商贸有限公司\n" +
                    "内蒙古欧尚时装有限责任公司\n" +
                    "赤峰邵德卓\n" +
                    "内蒙古元欧时装有限责任公司\n" +
                    "郑州莉荣服饰有限公司\n" +
                    "乐清杨宇佩\n" +
                    "贵州青茶麦社贸易有限公司\n" +
                    "海宁谢菊霞\n" +
                    "无锡金辰皓\n" +
                    "保定（石家庄尚岑商贸有限公司）\n" +
                    "四川成都三智一境贸易有限公司\n" +
                    "四川彭州黄正君（眼之魅名品服）\n" +
                    "连云港刘路\n" +
                    "萧山陈萍\n" +
                    "大同天一美佳\n" +
                    "舟山定海福润德商贸有限公司\n" +
                    "浙江桐乡邱琴芳\n" +
                    "珠海王飞\n" +
                    "新疆张金子商贸有限责任公司\n" +
                    "西昌\n" +
                    "牡丹江甜甜百货\n" +
                    "资阳黄红\n" +
                    "香港V&A\n" +
                    "山西长治马小珍\n" +
                    "张家港宏佳伟业服饰\n" +
                    "新疆隆通达商贸有限公司\n" +
                    "长治马小珍程雪\n" +
                    "烟台市雍惠茂经贸有限公司ED\n" +
                    "特许订货意向客户\n" +
                    "佛山顺德区东汇成商贸有限公司ED \n" +
                    "保定ED（石家庄尚岑商贸有限公司）\n" +
                    "张家港宏佳伟业服饰ED\n" +
                    "西昌王瑶ED\n" +
                    "海宁谢菊霞ED\n" +
                    "萧山陈萍ED\n" +
                    "山东淄博(青岛左岸经贸)ED\n" +
                    "郑州莉荣服饰有限公司ED\n" +
                    "乐清杨宇佩ED\n" +
                    "浙江桐乡邱琴芳ED\n" +
                    "丹东汇隆实业发展有限公司ED\n" +
                    "舟山定海福润德商贸有限公司ED\n" +
                    "成都睿理商贸有限公司ED\n" +
                    "成都三智一境贸易有限公司ED\n" +
                    "四川遂宁李海燕ED\n" +
                    "昆明蔓铭经贸有限公司ED\n" +
                    "无锡金辰皓ED\n" +
                    "常德郭华芬ED\n" +
                    "贵州青茶麦社贸易有限公司ED\n" +
                    "澳门noble group（ED）\n" +
                    "连云港刘路ED\n" +
                    "大同天一美佳ED\n" +
                    "厦门市越千阳发展有限公司ED\n" +
                    "牡丹江甜甜百货ED\n" +
                    "济南鑫钇服饰有限公司ED\n" +
                    "珠海市飞尚贸易有限公司ED\n" +
                    "赤峰邵德卓ED\n" +
                    "温州市乐绮服装有限公司ED\n" +
                    "海南尚睿实业有限公司ED\n" +
                    "新疆张金子商贸有限责任公司ED\n" +
                    "资阳黄红ED\n" +
                    "四川彭州黄正君ED\n" +
                    "台州好世商贸有限公司ED\n" +
                    "苏州秀致尚服饰商贸有限公司ED\n" +
                    "金华市驭骊服饰有限公司ED\n" +
                    "株洲红粉时尚商贸有限公司ED\n" +
                    "喀什市超世纪商贸有限公司ED\n" +
                    "驻马店祝瑞庆ED\n" +
                    "惠州王淑梅ED\n" +
                    "西安德珈贸易有限公司ED\n" +
                    "乐山市曹芳商贸有限公司ED\n" +
                    "哈尔滨元福祥经贸有限公司ED\n" +
                    "驻马店信阳祝瑞庆ED\n" +
                    "衡阳市鼎尚服饰有限公司ED\n" +
                    "辽宁岳希兰ED\n" +
                    "新乡卫杰ED\n" +
                    "甘肃恩典商贸有限公司ED\n" +
                    "柳州市恩瑟贸易有限公司ED\n" +
                    "SS AW时装一人有限公司（澳门ED）\n" +
                    "华南自营分区\n" +
                    "华东自营分区\n" +
                    "华北自营分区\n";
            String[] themes = activity_themes.split("\n");
            String[] areas = area_names.split("\n");
            for (int i = 0; i < themes.length; i++) {
                String theme = themes[i];
                String area_code = theme.replace("开卡送券","");
                theme = "ED"+themes[i];
                VipActivity vipActivity = vipActivityService.getVipActivityByTheme(corp_code,theme);
                if (vipActivity == null){
                    JSONArray coupon_array = new JSONArray();
                    JSONObject coupon_obj1 =  new JSONObject();
                    coupon_obj1.put("coupon_code",area_code+"300"+","+area_code+"300");
                    coupon_obj1.put("coupon_name","新开卡礼券300元("+areas[i]+")"+","+"新开卡礼券300元("+areas[i]+")");
                    coupon_obj1.put("present_point","");
                    coupon_obj1.put("vip_card_type_code","8");

                    coupon_array.add(coupon_obj1);
                    JSONObject coupon_obj2 =  new JSONObject();
                    coupon_obj2.put("coupon_code",area_code+"300"+","+area_code+"300");
                    coupon_obj2.put("coupon_name","新开卡礼券300元("+areas[i]+")"+","+"新开卡礼券300元("+areas[i]+")");
                    coupon_obj2.put("present_point","");
                    coupon_obj2.put("vip_card_type_code","11");
                    coupon_array.add(coupon_obj2);

                    JSONObject coupon_obj3 =  new JSONObject();
                    coupon_obj3.put("coupon_code",area_code+"300"+","+area_code+"300");
                    coupon_obj3.put("coupon_name","新开卡礼券300元("+areas[i]+")"+","+"新开卡礼券300元("+areas[i]+")");
                    coupon_obj3.put("present_point","");
                    coupon_obj3.put("vip_card_type_code","12");
                    coupon_array.add(coupon_obj3);

                    VipActivity activity = new VipActivity();
                    Date now = new Date();
                    String activity_code = "A" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(now);
                    activity.setCorp_code(corp_code);
                    activity.setActivity_code(activity_code);
                    activity.setActivity_theme(theme);
                    activity.setStart_time("2017-09-25  00:00:00");
                    activity.setEnd_time("2092-09-24  0:00:00");
                    activity.setRun_mode("coupon");
                    activity.setTarget_vips("[{\"type\":\"json\",\"key\":\"13\",\"value\":{\"start\":\"2017-01-01\",\"end\":\"\"}}]");
                    activity.setVip_condition("{\"screen\":[]}");
                    activity.setTarget_vips_count("335989");
                    activity.setTask_status("N");
                    activity.setSend_status("N");
                    activity.setActivity_state(Common.ACTIVITY_STATUS_0);
                    activity.setRun_scope("{\"store_code\":\"\",\"brand_code\":\"\",\"area_code\":\""+area_code+"\"}");
                    activity.setCreated_date("2017-09-24 17:45:19");
                    activity.setModified_date("2017-09-24 17:45:19");
                    activity.setCreater("10000");
                    activity.setModifier("10000");
                    activity.setIsactive(Common.IS_ACTIVE_Y);
                    activity.setApp_id("wx390dcdc19a4f9967");
                    activity.setApp_name("Edition10");
                    groupService.insert(activity);


                    VipActivityDetail detail = new VipActivityDetail();
                    detail.setActivity_code(activity_code);
                    detail.setCorp_code(corp_code);
                    detail.setActivity_type("coupon");
                    detail.setSend_coupon_type("card");
                    detail.setCoupon_type(JSON.toJSONString(coupon_array));
                    detail.setCreated_date("2017-09-24 17:45:19");
                    detail.setModified_date("2017-09-24 17:45:19");
                    detail.setCreater("10000");
                    detail.setModifier("10000");
                    detail.setIsactive(Common.IS_ACTIVE_Y);
                    groupService.insertDetail(detail);

                    count += 1;
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("创建活动数："+count);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/InputActivity1", method = RequestMethod.GET)
    @ResponseBody
    public String apiInputActivity1(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int count = 0;
            String corp_code = "C10183";
            String activity_themes = "开卡送券T005\n" +
                    "开卡送券R010\n" +
                    "开卡送券R007\n" +
                    "开卡送券R001\n" +
                    "开卡送券A412\n" +
                    "开卡送券A411\n" +
                    "开卡送券A407\n" +
                    "开卡送券A398\n" +
                    "开卡送券A397\n" +
                    "开卡送券A396\n" +
                    "开卡送券A390\n" +
                    "开卡送券A389\n" +
                    "开卡送券A382\n" +
                    "开卡送券A381\n" +
                    "开卡送券A377\n" +
                    "开卡送券A342\n" +
                    "开卡送券A341\n" +
                    "开卡送券A340\n" +
                    "开卡送券A339\n" +
                    "开卡送券A318\n" +
                    "开卡送券A318\n" +
                    "开卡送券A317\n" +
                    "开卡送券A317\n" +
                    "开卡送券A306\n" +
                    "开卡送券A305\n";

            String area_names = "特许订货意向客户\n" +
                    "华北自营分区\n" +
                    "华东自营分区\n" +
                    "华南自营分区\n" +
                    "台州市贝安贸易有限公司LM\n" +
                    "无锡红蔻时尚商贸有限公司LM\n" +
                    "连云港吴娟LM\n" +
                    "沈阳富之雅商贸有限公司LM\n" +
                    "西安梦莎商贸有限公司LM\n" +
                    "上海宥佳实业有限公司LM\n" +
                    "沈阳富之雅商贸有限公司LM\n" +
                    "重庆艾墨尔企业管理咨询有限公司LM\n" +
                    "恩施岳雪洁LM\n" +
                    "潍坊卓普商贸有限公司LM\n" +
                    "贵州青茶麦社贸易有限公司LM\n" +
                    "济南鑫钇服饰有限公司LM\n" +
                    "南昌沅尚实业发展有限公司LM\n" +
                    "遂宁市燕雪容贸易有限公司LM\n" +
                    "青岛左岸经贸有限公司LM\n" +
                    "安徽銮祺贸易有限公司LM\n" +
                    "安徽銮祺贸易有限公司\n" +
                    "浙江泽邑商贸有限公司LM\n" +
                    "浙江泽邑商贸有限公司\n" +
                    "吉林班南\n" +
                    "成都童友执服饰有限公司\n";

            String[] themes = activity_themes.split("\n");
            String[] areas = area_names.split("\n");
            for (int i = 0; i < themes.length; i++) {
                String theme = themes[i];
                String area_code = theme.replace("开卡送券","");
                theme = "LM"+themes[i];
                VipActivity vipActivity = vipActivityService.getVipActivityByTheme(corp_code,theme);
                if (vipActivity == null){
                    JSONArray coupon_array = new JSONArray();
                    JSONObject coupon_obj1 =  new JSONObject();
                    coupon_obj1.put("coupon_code",area_code+"50"+","+area_code+"50");
                    coupon_obj1.put("coupon_name","新开卡礼券50元("+areas[i]+")"+","+"新开卡礼券50元("+areas[i]+")");
                    coupon_obj1.put("present_point","");
                    coupon_obj1.put("vip_card_type_code","29");
                    coupon_array.add(coupon_obj1);

                    VipActivity activity = new VipActivity();
                    Date now = new Date();
                    String activity_code = "A" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(now);
                    activity.setCorp_code(corp_code);
                    activity.setActivity_code(activity_code);
                    activity.setActivity_theme(theme);
                    activity.setStart_time("2017-09-25  00:00:00");
                    activity.setEnd_time("2092-09-24  0:00:00");
                    activity.setRun_mode("coupon");
                    activity.setTarget_vips("[{\"type\":\"json\",\"key\":\"13\",\"value\":{\"start\":\"2017-01-01\",\"end\":\"\"}}]");
                    activity.setVip_condition("{\"screen\":[]}");
                    activity.setTarget_vips_count("335989");
                    activity.setTask_status("N");
                    activity.setSend_status("N");
                    activity.setActivity_state(Common.ACTIVITY_STATUS_0);
                    activity.setRun_scope("{\"store_code\":\"\",\"brand_code\":\"\",\"area_code\":\""+area_code+"\"}");
                    activity.setCreated_date("2017-09-24 17:45:19");
                    activity.setModified_date("2017-09-24 17:45:19");
                    activity.setCreater("10000");
                    activity.setModifier("10000");
                    activity.setIsactive(Common.IS_ACTIVE_Y);
                    activity.setApp_id("wx21f0cea9bab670a6");
                    activity.setApp_name("littleMOCO");
                    groupService.insert(activity);


                    VipActivityDetail detail = new VipActivityDetail();
                    detail.setActivity_code(activity_code);
                    detail.setCorp_code(corp_code);
                    detail.setActivity_type("coupon");
                    detail.setSend_coupon_type("card");
                    detail.setCoupon_type(JSON.toJSONString(coupon_array));
                    detail.setCreated_date("2017-09-24 17:45:19");
                    detail.setModified_date("2017-09-24 17:45:19");
                    detail.setCreater("10000");
                    detail.setModifier("10000");
                    detail.setIsactive(Common.IS_ACTIVE_Y);
                    groupService.insertDetail(detail);

                    count += 1;
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("创建活动数："+count);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/api/InputActivityBirth", method = RequestMethod.GET)
    @ResponseBody
    public String apiInputActivityBirth(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int count = 0;
            String corp_code = "C10183";
            String activity_themes = "生日送券A188\n" +
                    "生日送券A260\n" +
                    "生日送券A004\n" +
                    "生日送券A282\n" +
                    "生日送券A286\n" +
                    "生日送券A236\n" +
                    "生日送券A144\n" +
                    "生日送券A272\n" +
                    "生日送券A204\n" +
                    "生日送券A074\n" +
                    "生日送券A141\n" +
                    "生日送券A268\n" +
                    "生日送券A275\n" +
                    "生日送券A293\n" +
                    "生日送券A081\n" +
                    "生日送券A288\n" +
                    "生日送券A189\n" +
                    "生日送券A067\n" +
                    "生日送券A183\n" +
                    "生日送券A054\n" +
                    "生日送券A143\n" +
                    "生日送券A145\n" +
                    "生日送券A250\n" +
                    "生日送券A069\n" +
                    "生日送券A252\n" +
                    "生日送券A122\n" +
                    "生日送券A109\n" +
                    "生日送券A280\n" +
                    "生日送券A299\n" +
                    "生日送券A063\n" +
                    "生日送券A271\n" +
                    "生日送券A309\n" +
                    "生日送券A110\n" +
                    "生日送券A056\n" +
                    "生日送券A336\n" +
                    "生日送券A337\n" +
                    "生日送券A338\n" +
                    "生日送券T005\n" +
                    "生日送券A345\n" +
                    "生日送券A346\n" +
                    "生日送券A347\n" +
                    "生日送券A348\n" +
                    "生日送券A349\n" +
                    "生日送券A350\n" +
                    "生日送券A351\n" +
                    "生日送券A352\n" +
                    "生日送券A353\n" +
                    "生日送券A354\n" +
                    "生日送券A355\n" +
                    "生日送券A356\n" +
                    "生日送券A357\n" +
                    "生日送券A358\n" +
                    "生日送券A359\n" +
                    "生日送券A361\n" +
                    "生日送券A362\n" +
                    "生日送券A363\n" +
                    "生日送券A364\n" +
                    "生日送券A365\n" +
                    "生日送券A366\n" +
                    "生日送券A367\n" +
                    "生日送券A368\n" +
                    "生日送券A369\n" +
                    "生日送券A370\n" +
                    "生日送券A372\n" +
                    "生日送券A371\n" +
                    "生日送券A373\n" +
                    "生日送券A374\n" +
                    "生日送券A375\n" +
                    "生日送券A376\n" +
                    "生日送券A360\n" +
                    "生日送券A378\n" +
                    "生日送券A380\n" +
                    "生日送券A384\n" +
                    "生日送券A385\n" +
                    "生日送券A387\n" +
                    "生日送券A391\n" +
                    "生日送券A398\n" +
                    "生日送券A402\n" +
                    "生日送券A403\n" +
                    "生日送券A404\n" +
                    "生日送券A391\n" +
                    "生日送券A409\n" +
                    "生日送券A410\n" +
                    "生日送券A414\n" +
                    "生日送券A416\n" +
                    "生日送券A417\n" +
                    "生日送券A419\n" +
                    "生日送券R001\n" +
                    "生日送券R007\n" +
                    "生日送券R010\n";

            String area_names = "常德郭华芬\n" +
                    "厦门越千阳发展有限公司\n" +
                    "佛山顺德区东汇成商贸有限公司\n" +
                    "南昌付伟俊\n" +
                    "温州市乐绮服装有限公司\n" +
                    "澳门noble group\n" +
                    "四川遂宁李海燕\n" +
                    "济南鑫钇服饰有限公司\n" +
                    "衡阳胡亚丽\n" +
                    "山东淄博(青岛左岸经贸)\n" +
                    "四川成都睿理商贸有限公司\n" +
                    "内蒙古欧尚时装有限责任公司\n" +
                    "赤峰邵德卓\n" +
                    "内蒙古元欧时装有限责任公司\n" +
                    "郑州莉荣服饰有限公司\n" +
                    "乐清杨宇佩\n" +
                    "贵州青茶麦社贸易有限公司\n" +
                    "海宁谢菊霞\n" +
                    "无锡金辰皓\n" +
                    "保定（石家庄尚岑商贸有限公司）\n" +
                    "四川成都三智一境贸易有限公司\n" +
                    "四川彭州黄正君（眼之魅名品服）\n" +
                    "连云港刘路\n" +
                    "萧山陈萍\n" +
                    "大同天一美佳\n" +
                    "舟山定海福润德商贸有限公司\n" +
                    "浙江桐乡邱琴芳\n" +
                    "珠海王飞\n" +
                    "新疆张金子商贸有限责任公司\n" +
                    "西昌\n" +
                    "牡丹江甜甜百货\n" +
                    "资阳黄红\n" +
                    "山西长治马小珍\n" +
                    "张家港宏佳伟业服饰\n" +
                    "新疆隆通达商贸有限公司\n" +
                    "长治马小珍程雪\n" +
                    "烟台市雍惠茂经贸有限公司ED\n" +
                    "特许订货意向客户\n" +
                    "佛山顺德区东汇成商贸有限公司ED \n" +
                    "保定ED（石家庄尚岑商贸有限公司）\n" +
                    "张家港宏佳伟业服饰ED\n" +
                    "西昌王瑶ED\n" +
                    "海宁谢菊霞ED\n" +
                    "萧山陈萍ED\n" +
                    "山东淄博(青岛左岸经贸)ED\n" +
                    "郑州莉荣服饰有限公司ED\n" +
                    "乐清杨宇佩ED\n" +
                    "浙江桐乡邱琴芳ED\n" +
                    "丹东汇隆实业发展有限公司ED\n" +
                    "舟山定海福润德商贸有限公司ED\n" +
                    "成都睿理商贸有限公司ED\n" +
                    "成都三智一境贸易有限公司ED\n" +
                    "四川遂宁李海燕ED\n" +
                    "昆明蔓铭经贸有限公司ED\n" +
                    "无锡金辰皓ED\n" +
                    "常德郭华芬ED\n" +
                    "贵州青茶麦社贸易有限公司ED\n" +
                    "澳门noble group（ED）\n" +
                    "连云港刘路ED\n" +
                    "大同天一美佳ED\n" +
                    "厦门市越千阳发展有限公司ED\n" +
                    "牡丹江甜甜百货ED\n" +
                    "济南鑫钇服饰有限公司ED\n" +
                    "珠海市飞尚贸易有限公司ED\n" +
                    "赤峰邵德卓ED\n" +
                    "温州市乐绮服装有限公司ED\n" +
                    "海南尚睿实业有限公司ED\n" +
                    "新疆张金子商贸有限责任公司ED\n" +
                    "资阳黄红ED\n" +
                    "四川彭州黄正君ED\n" +
                    "台州好世商贸有限公司ED\n" +
                    "苏州秀致尚服饰商贸有限公司ED\n" +
                    "金华市驭骊服饰有限公司ED\n" +
                    "株洲红粉时尚商贸有限公司ED\n" +
                    "喀什市超世纪商贸有限公司ED\n" +
                    "驻马店祝瑞庆ED\n" +
                    "惠州王淑梅ED\n" +
                    "西安德珈贸易有限公司ED\n" +
                    "乐山市曹芳商贸有限公司ED\n" +
                    "哈尔滨元福祥经贸有限公司ED\n" +
                    "驻马店信阳祝瑞庆ED\n" +
                    "衡阳市鼎尚服饰有限公司ED\n" +
                    "辽宁岳希兰ED\n" +
                    "新乡卫杰ED\n" +
                    "甘肃恩典商贸有限公司ED\n" +
                    "柳州市恩瑟贸易有限公司ED\n" +
                    "SS AW时装一人有限公司（澳门ED）\n" +
                    "华南自营分区\n" +
                    "华东自营分区\n" +
                    "华北自营分区\n";

            String[] themes = activity_themes.split("\n");
            String[] areas = area_names.split("\n");
            for (int i = 0; i < themes.length; i++) {
                String theme = themes[i];
                String area_code = theme.replace("生日送券","");
                    theme = "ED"+theme;

                VipActivity vipActivity = vipActivityService.getVipActivityByTheme(corp_code,theme);
                if (vipActivity == null){
                    JSONArray coupon_array = new JSONArray();
                    JSONObject coupon_obj1 =  new JSONObject();
                    coupon_obj1.put("coupon_code",area_code+"07");
                    coupon_obj1.put("coupon_name","生日7折券("+areas[i]+")");
                    coupon_array.add(coupon_obj1);

                    JSONArray cou_array = new JSONArray();
                    JSONObject object = new JSONObject();
                    object.put("anniversary_time","M");
                    object.put("param_desc","生日（个人资料）");
                    object.put("param_name","BIRTHDAY");
                    object.put("coupon_type","coupon_array");
                    cou_array.add(object);

                    VipActivity activity = new VipActivity();
                    Date now = new Date();
                    String activity_code = "A" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(now);
                    activity.setCorp_code(corp_code);
                    activity.setActivity_code(activity_code);
                    activity.setActivity_theme(theme);
                    activity.setStart_time("2017-9-26  00:00:00");
                    activity.setEnd_time("2092-9-24  0:00:00");
                    activity.setRun_mode("coupon");
                    activity.setTarget_vips("[{\"names\":\"卡类型\",\"type\":\"text\",\"value\":\"ED尊贵卡(8折),ED贵宾卡(8.5折),ED会员卡(8.8折)\",\"key\":\"12\"}]");
                    activity.setVip_condition("{\"screen_type\":\"difficult\",\"screen\":[{\"names\":\"卡类型\",\"type\":\"text\",\"value\":\"ED尊贵卡(8折),ED贵宾卡(8.5折),ED会员卡(8.8折)\",\"key\":\"12\"}]}");
                    activity.setTarget_vips_count("335989");
                    activity.setTask_status("N");
                    activity.setSend_status("N");
                    activity.setActivity_state(Common.ACTIVITY_STATUS_0);
                    activity.setRun_scope("{\"store_code\":\"\",\"brand_code\":\"\",\"area_code\":\""+area_code+"\"}");
                    activity.setCreated_date("2017-09-24 17:45:19");
                    activity.setModified_date("2017-09-24 17:45:19");
                    activity.setCreater("10000");
                    activity.setModifier("10000");
                    activity.setIsactive(Common.IS_ACTIVE_Y);
                    activity.setApp_id("wx21f0cea9bab670a6");
                    activity.setApp_name("littleMOCO");
                    groupService.insert(activity);

                    VipActivityDetail detail = new VipActivityDetail();
                    detail.setActivity_code(activity_code);
                    detail.setCorp_code(corp_code);
                    detail.setActivity_type("coupon");
                    detail.setSend_coupon_type("anniversary");
                    detail.setCoupon_type(JSON.toJSONString(cou_array));
                    detail.setCreated_date("2017-09-24 17:45:19");
                    detail.setModified_date("2017-09-24 17:45:19");
                    detail.setCreater("10000");
                    detail.setModifier("10000");
                    detail.setIsactive(Common.IS_ACTIVE_Y);
                    groupService.insertDetail(detail);

                    VipActivityDetailAnniversary anniversary = new VipActivityDetailAnniversary();
                    anniversary.setActivity_code(activity_code);
                    anniversary.setCorp_code(corp_code);
                    anniversary.setParam_name("BIRTHDAY");
                    anniversary.setParam_desc("生日（个人资料）");
                    anniversary.setRun_time_type("M");
                    anniversary.setCoupon_type(JSON.toJSONString(coupon_array));
                    anniversary.setCreated_date("2017-09-24 17:45:19");
                    anniversary.setModified_date("2017-09-24 17:45:19");
                    anniversary.setCreater("10000");
                    anniversary.setModifier("10000");
                    anniversary.setIsactive(Common.IS_ACTIVE_N);
                    groupService.insertDetailAnniversary(anniversary);
                    count += 1;

                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("创建活动数："+count);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/api/InputActivityBirth1", method = RequestMethod.GET)
    @ResponseBody
    public String apiInputActivityBirth1(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int count = 0;
            String corp_code = "C10183";
            String activity_themes = "生日送券T005\n" +
                    "生日送券R010\n" +
                    "生日送券R007\n" +
                    "生日送券R001\n" +
                    "生日送券A412\n" +
                    "生日送券A411\n" +
                    "生日送券A407\n" +
                    "生日送券A398\n" +
                    "生日送券A397\n" +
                    "生日送券A396\n" +
                    "生日送券A390\n" +
                    "生日送券A389\n" +
                    "生日送券A382\n" +
                    "生日送券A381\n" +
                    "生日送券A377\n" +
                    "生日送券A342\n" +
                    "生日送券A341\n" +
                    "生日送券A340\n" +
                    "生日送券A339\n" +
                    "生日送券A318\n" +
                    "生日送券A318\n" +
                    "生日送券A317\n" +
                    "生日送券A317\n" +
                    "生日送券A306\n" +
                    "生日送券A305\n";

            String area_names = "特许订货意向客户\n" +
                    "华北自营分区\n" +
                    "华东自营分区\n" +
                    "华南自营分区\n" +
                    "台州市贝安贸易有限公司LM\n" +
                    "无锡红蔻时尚商贸有限公司LM\n" +
                    "连云港吴娟LM\n" +
                    "沈阳富之雅商贸有限公司LM\n" +
                    "西安梦莎商贸有限公司LM\n" +
                    "上海宥佳实业有限公司LM\n" +
                    "沈阳富之雅商贸有限公司LM\n" +
                    "重庆艾墨尔企业管理咨询有限公司LM\n" +
                    "恩施岳雪洁LM\n" +
                    "潍坊卓普商贸有限公司LM\n" +
                    "贵州青茶麦社贸易有限公司LM\n" +
                    "济南鑫钇服饰有限公司LM\n" +
                    "南昌沅尚实业发展有限公司LM\n" +
                    "遂宁市燕雪容贸易有限公司LM\n" +
                    "青岛左岸经贸有限公司LM\n" +
                    "安徽銮祺贸易有限公司LM\n" +
                    "安徽銮祺贸易有限公司\n" +
                    "浙江泽邑商贸有限公司LM\n" +
                    "浙江泽邑商贸有限公司\n" +
                    "吉林班南\n" +
                    "成都童友执服饰有限公司\n";

            String[] themes = activity_themes.split("\n");
            String[] areas = area_names.split("\n");
            for (int i = 0; i < themes.length; i++) {
                String theme = themes[i];
                String area_code = theme.replace("生日送券","");
                theme = "LM"+theme;

                VipActivity vipActivity = vipActivityService.getVipActivityByTheme(corp_code,theme);
                if (vipActivity == null){
                    JSONArray coupon_array = new JSONArray();
                    JSONObject coupon_obj1 =  new JSONObject();
                    coupon_obj1.put("coupon_code",area_code+"07");
                    coupon_obj1.put("coupon_name","生日7折券("+areas[i]+")");
                    coupon_array.add(coupon_obj1);

                    JSONArray cou_array = new JSONArray();
                    JSONObject object = new JSONObject();
                    object.put("anniversary_time","M");
                    object.put("param_desc","生日（个人资料）");
                    object.put("param_name","BIRTHDAY");
                    object.put("coupon_type","coupon_array");
                    cou_array.add(object);

                    VipActivity activity = new VipActivity();
                    Date now = new Date();
                    String activity_code = "A" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(now);
                    activity.setCorp_code(corp_code);
                    activity.setActivity_code(activity_code);
                    activity.setActivity_theme(theme);
                    activity.setStart_time("2017-9-26  00:00:00");
                    activity.setEnd_time("2092-9-24  0:00:00");
                    activity.setRun_mode("coupon");
                    activity.setTarget_vips("[{\"names\":\"卡类型\",\"type\":\"text\",\"value\":\"小MO会员卡(8.8折)\",\"key\":\"12\"}]");
                    activity.setVip_condition("{\"screen_type\":\"difficult\",\"screen\":[{\"names\":\"卡类型\",\"type\":\"text\",\"value\":\"小MO会员卡(8.8折)\",\"key\":\"12\"}]}");
                    activity.setTarget_vips_count("335989");
                    activity.setTask_status("N");
                    activity.setSend_status("N");
                    activity.setActivity_state(Common.ACTIVITY_STATUS_0);
                    activity.setRun_scope("{\"store_code\":\"\",\"brand_code\":\"\",\"area_code\":\""+area_code+"\"}");
                    activity.setCreated_date("2017-09-24 17:45:19");
                    activity.setModified_date("2017-09-24 17:45:19");
                    activity.setCreater("10000");
                    activity.setModifier("10000");
                    activity.setIsactive(Common.IS_ACTIVE_Y);
                    activity.setApp_id("wx21f0cea9bab670a6");
                    activity.setApp_name("littleMOCO");
                    groupService.insert(activity);

                    VipActivityDetail detail = new VipActivityDetail();
                    detail.setActivity_code(activity_code);
                    detail.setCorp_code(corp_code);
                    detail.setActivity_type("coupon");
                    detail.setSend_coupon_type("anniversary");
                    detail.setCoupon_type(JSON.toJSONString(cou_array));
                    detail.setCreated_date("2017-09-24 17:45:19");
                    detail.setModified_date("2017-09-24 17:45:19");
                    detail.setCreater("10000");
                    detail.setModifier("10000");
                    detail.setIsactive(Common.IS_ACTIVE_Y);
                    groupService.insertDetail(detail);

                    VipActivityDetailAnniversary anniversary = new VipActivityDetailAnniversary();
                    anniversary.setActivity_code(activity_code);
                    anniversary.setCorp_code(corp_code);
                    anniversary.setParam_name("BIRTHDAY");
                    anniversary.setParam_desc("生日（个人资料）");
                    anniversary.setRun_time_type("M");
                    anniversary.setCoupon_type(JSON.toJSONString(coupon_array));
                    anniversary.setCreated_date("2017-09-24 17:45:19");
                    anniversary.setModified_date("2017-09-24 17:45:19");
                    anniversary.setCreater("10000");
                    anniversary.setModifier("10000");
                    anniversary.setIsactive(Common.IS_ACTIVE_N);
                    groupService.insertDetailAnniversary(anniversary);
                    count += 1;

                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("创建活动数："+count);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/InputActivityBirth2", method = RequestMethod.GET)
    @ResponseBody
    public String apiInputActivityBirth2(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int count = 0;
            String corp_code = "C10183";
            String activity_themes = "生日送券A415\n" +
                    "生日送券A413\n" +
                    "生日送券A408\n" +
                    "生日送券A406\n" +
                    "生日送券A405\n" +
                    "生日送券A400\n" +
                    "生日送券A399\n" +
                    "生日送券A395\n" +
                    "生日送券A394\n" +
                    "生日送券A393\n" +
                    "生日送券A392\n" +
                    "生日送券A388\n" +
                    "生日送券A386\n" +
                    "生日送券A383\n" +
                    "生日送券A379\n" +
                    "生日送券A373\n" +
                    "生日送券A370\n" +
                    "生日送券A353\n" +
                    "生日送券A344\n" +
                    "生日送券A343\n" +
                    "生日送券A335\n" +
                    "生日送券A334\n" +
                    "生日送券A333\n" +
                    "生日送券A332\n" +
                    "生日送券A332\n" +
                    "生日送券A331\n" +
                    "生日送券A330\n" +
                    "生日送券A329\n" +
                    "生日送券A328\n" +
                    "生日送券A327\n" +
                    "生日送券A326\n" +
                    "生日送券A325\n" +
                    "生日送券A324\n" +
                    "生日送券A323\n" +
                    "生日送券A322\n" +
                    "生日送券A321\n" +
                    "生日送券A320\n" +
                    "生日送券A319\n" +
                    "生日送券A316\n" +
                    "生日送券A315\n" +
                    "生日送券A314\n" +
                    "生日送券A313\n" +
                    "生日送券A312\n" +
                    "生日送券A311\n" +
                    "生日送券A310\n" +
                    "生日送券A309\n" +
                    "生日送券A308\n" +
                    "生日送券A307\n" +
                    "生日送券A304\n" +
                    "生日送券A303\n" +
                    "生日送券A302\n" +
                    "生日送券A301\n" +
                    "生日送券A300\n" +
                    "生日送券A299\n" +
                    "生日送券A298\n" +
                    "生日送券A297\n" +
                    "生日送券A296\n" +
                    "生日送券A295\n" +
                    "生日送券A294\n" +
                    "生日送券A292\n" +
                    "生日送券A291\n" +
                    "生日送券A290\n" +
                    "生日送券A289\n" +
                    "生日送券A288\n" +
                    "生日送券A287\n" +
                    "生日送券A286\n" +
                    "生日送券A285\n" +
                    "生日送券A284\n" +
                    "生日送券A283\n" +
                    "生日送券A281\n" +
                    "生日送券A280\n" +
                    "生日送券A279\n" +
                    "生日送券A278\n" +
                    "生日送券A277\n" +
                    "生日送券A276\n" +
                    "生日送券A275\n" +
                    "生日送券A274\n" +
                    "生日送券A273\n" +
                    "生日送券A272\n" +
                    "生日送券A271\n" +
                    "生日送券A270\n" +
                    "生日送券A269\n" +
                    "生日送券A268\n" +
                    "生日送券A267\n" +
                    "生日送券A266\n" +
                    "生日送券A265\n" +
                    "生日送券A264\n" +
                    "生日送券A263\n" +
                    "生日送券A262\n" +
                    "生日送券A261\n" +
                    "生日送券A260\n" +
                    "生日送券A260\n" +
                    "生日送券A259\n" +
                    "生日送券A257\n" +
                    "生日送券A256\n" +
                    "生日送券A255\n" +
                    "生日送券A254\n" +
                    "生日送券A253\n" +
                    "生日送券A252\n" +
                    "生日送券A251\n" +
                    "生日送券A250\n" +
                    "生日送券A249\n" +
                    "生日送券A248\n" +
                    "生日送券A247\n" +
                    "生日送券A246\n" +
                    "生日送券A245\n" +
                    "生日送券A244\n" +
                    "生日送券A243\n" +
                    "生日送券A242\n" +
                    "生日送券A241\n" +
                    "生日送券A240\n" +
                    "生日送券A238\n" +
                    "生日送券A237\n" +
                    "生日送券A236\n" +
                    "生日送券A235\n" +
                    "生日送券A234\n" +
                    "生日送券A233\n" +
                    "生日送券A232\n" +
                    "生日送券A231\n" +
                    "生日送券A230\n" +
                    "生日送券A228\n" +
                    "生日送券A227\n" +
                    "生日送券A226\n" +
                    "生日送券A225\n" +
                    "生日送券A224\n" +
                    "生日送券A223\n" +
                    "生日送券A222\n" +
                    "生日送券A221\n" +
                    "生日送券A220\n" +
                    "生日送券A209\n" +
                    "生日送券A208\n" +
                    "生日送券A207\n" +
                    "生日送券A205\n" +
                    "生日送券A204\n" +
                    "生日送券A203\n" +
                    "生日送券A202\n" +
                    "生日送券A201\n" +
                    "生日送券A200\n" +
                    "生日送券A199\n" +
                    "生日送券A198\n" +
                    "生日送券A197\n" +
                    "生日送券A195\n" +
                    "生日送券A194\n" +
                    "生日送券A193\n" +
                    "生日送券A192\n" +
                    "生日送券A191\n" +
                    "生日送券A190\n" +
                    "生日送券A189\n" +
                    "生日送券A188\n" +
                    "生日送券A187\n" +
                    "生日送券A186\n" +
                    "生日送券A185\n" +
                    "生日送券A183\n" +
                    "生日送券A182\n" +
                    "生日送券A181\n" +
                    "生日送券A180\n" +
                    "生日送券A179\n" +
                    "生日送券A178\n" +
                    "生日送券A177\n" +
                    "生日送券A176\n" +
                    "生日送券A175\n" +
                    "生日送券A174\n" +
                    "生日送券A173\n" +
                    "生日送券A172\n" +
                    "生日送券A171\n" +
                    "生日送券A170\n" +
                    "生日送券A169\n" +
                    "生日送券A168\n" +
                    "生日送券A167\n" +
                    "生日送券A166\n" +
                    "生日送券A165\n" +
                    "生日送券A164\n" +
                    "生日送券A164\n" +
                    "生日送券A163\n" +
                    "生日送券A162\n" +
                    "生日送券A161\n" +
                    "生日送券A160\n" +
                    "生日送券A159\n" +
                    "生日送券A157\n" +
                    "生日送券A156\n" +
                    "生日送券A155\n" +
                    "生日送券A154\n" +
                    "生日送券A153\n" +
                    "生日送券A152\n" +
                    "生日送券A151\n" +
                    "生日送券A150\n" +
                    "生日送券A149\n" +
                    "生日送券A148\n" +
                    "生日送券A147\n" +
                    "生日送券A145\n" +
                    "生日送券A144\n" +
                    "生日送券A143\n" +
                    "生日送券A142\n" +
                    "生日送券A141\n" +
                    "生日送券A140\n" +
                    "生日送券A139\n" +
                    "生日送券A138\n" +
                    "生日送券A137\n" +
                    "生日送券A136\n" +
                    "生日送券A135\n" +
                    "生日送券A134\n" +
                    "生日送券A133\n" +
                    "生日送券A132\n" +
                    "生日送券A131\n" +
                    "生日送券A130\n" +
                    "生日送券A129\n" +
                    "生日送券A127\n" +
                    "生日送券A126\n" +
                    "生日送券A125\n" +
                    "生日送券A124\n" +
                    "生日送券A123\n" +
                    "生日送券A122\n" +
                    "生日送券A121\n" +
                    "生日送券A120\n" +
                    "生日送券A119\n" +
                    "生日送券A118\n" +
                    "生日送券A116\n" +
                    "生日送券A115\n" +
                    "生日送券A112\n" +
                    "生日送券A111\n" +
                    "生日送券A110\n" +
                    "生日送券A109\n" +
                    "生日送券A107\n" +
                    "生日送券A106\n" +
                    "生日送券A105\n" +
                    "生日送券A104\n" +
                    "生日送券A102\n" +
                    "生日送券A101\n" +
                    "生日送券A099\n" +
                    "生日送券A097\n" +
                    "生日送券A096\n" +
                    "生日送券A095\n" +
                    "生日送券A094\n" +
                    "生日送券A093\n" +
                    "生日送券A091\n" +
                    "生日送券A090\n" +
                    "生日送券A089\n" +
                    "生日送券A088\n" +
                    "生日送券A087\n" +
                    "生日送券A086\n" +
                    "生日送券A085\n" +
                    "生日送券A083\n" +
                    "生日送券A082\n" +
                    "生日送券A081\n" +
                    "生日送券A079\n" +
                    "生日送券A078\n" +
                    "生日送券A077\n" +
                    "生日送券A076\n" +
                    "生日送券A075\n" +
                    "生日送券A074\n" +
                    "生日送券A073\n" +
                    "生日送券A072\n" +
                    "生日送券A071\n" +
                    "生日送券A070\n" +
                    "生日送券A069\n" +
                    "生日送券A068\n" +
                    "生日送券A067\n" +
                    "生日送券A066\n" +
                    "生日送券A065\n" +
                    "生日送券A064\n" +
                    "生日送券A063\n" +
                    "生日送券A062\n" +
                    "生日送券A061\n" +
                    "生日送券A059\n" +
                    "生日送券A058\n" +
                    "生日送券A057\n" +
                    "生日送券A056\n" +
                    "生日送券A054\n" +
                    "生日送券A053\n" +
                    "生日送券A051\n" +
                    "生日送券A050\n" +
                    "生日送券A049\n" +
                    "生日送券A048\n" +
                    "生日送券A047\n" +
                    "生日送券A046\n" +
                    "生日送券A045\n" +
                    "生日送券A044\n" +
                    "生日送券A043\n" +
                    "生日送券A042\n" +
                    "生日送券A041\n" +
                    "生日送券A039\n" +
                    "生日送券A037\n" +
                    "生日送券A034\n" +
                    "生日送券A033\n" +
                    "生日送券A032\n" +
                    "生日送券A031\n" +
                    "生日送券A030\n" +
                    "生日送券A029\n" +
                    "生日送券A028\n" +
                    "生日送券A027\n" +
                    "生日送券A026\n" +
                    "生日送券A025\n" +
                    "生日送券A024\n" +
                    "生日送券A023\n" +
                    "生日送券A021\n" +
                    "生日送券A019\n" +
                    "生日送券A018\n" +
                    "生日送券A016\n" +
                    "生日送券A015\n" +
                    "生日送券A014\n" +
                    "生日送券A013\n" +
                    "生日送券A012\n" +
                    "生日送券A011\n" +
                    "生日送券A010\n" +
                    "生日送券A009\n" +
                    "生日送券A008\n" +
                    "生日送券A007\n" +
                    "生日送券A004\n" +
                    "生日送券A003\n" +
                    "生日送券A002\n" +
                    "生日送券R011\n" +
                    "生日送券R010\n" +
                    "生日送券R007\n" +
                    "生日送券R001\n";

            String area_names = "乌鲁木齐东方丽人服饰有限公司MO\n" +
                    "荆门市鼎鑫贸易有限公司MO\n" +
                    "瑞丽芒市左开云\n" +
                    "庆阳王维东MO\n" +
                    "麻城市源尚服饰有限公司MO\n" +
                    "湖北凯隆达投资有限公司MO\n" +
                    "新疆隆通达商贸有限公司MO\n" +
                    "大理娄军辉\n" +
                    "普洱李青\n" +
                    "景洪玉宽\n" +
                    "保山孙登云\n" +
                    "盐城孙玲\n" +
                    "松原陈芳\n" +
                    "喀什市超世纪商贸有限公司\n" +
                    "吉林梅河口杨雪\n" +
                    "温州市乐绮服装有限公司ED\n" +
                    "济南鑫钇服饰有限公司ED\n" +
                    "乐清杨宇佩ED\n" +
                    "禹州张莉\n" +
                    "宿州魏素兰\n" +
                    "芜湖市鸠兹服饰有限公司\n" +
                    "漯河赵焱\n" +
                    "新乡卫杰\n" +
                    "新密张红艳\n" +
                    "新密新郑张红艳\n" +
                    "周口蔡静\n" +
                    "三门峡刘芳\n" +
                    "开封新玛特\n" +
                    "濮阳梁峰\n" +
                    "商丘沙咏梅\n" +
                    "永城陈红凯\n" +
                    "驻马店田玲\n" +
                    "安阳杜婷\n" +
                    "新乡长垣县胡永勤\n" +
                    "南阳杨向东\n" +
                    "平顶山杨智伟\n" +
                    "焦作王晓丽\n" +
                    "许昌祝瑞庆\n" +
                    "孝感官正商贸有限公司\n" +
                    "神木李常青\n" +
                    "榆林高艳芳\n" +
                    "汉中徐建彬\n" +
                    "渭南郭少星\n" +
                    "延安马婷婷\n" +
                    "宝鸡市姳尚服饰有限公司\n" +
                    "资阳黄红\n" +
                    "甘肃武威赵礼\n" +
                    "甘肃张掖赵丽丽\n" +
                    "阜新于力\n" +
                    "达州大竹县李代梅\n" +
                    "十堰市明笃商贸有限公司\n" +
                    "天津静海县毛秀梅\n" +
                    "廊坊梁涛\n" +
                    "新疆张金子商贸有限责任公司\n" +
                    "海南尚睿实业有限公司\n" +
                    "丹东汇隆实业发展有限公司\n" +
                    "湖北新动向商贸有限公司\n" +
                    "沧州李健\n" +
                    "仙桃市伟佳商贸有限公司\n" +
                    "朝阳王学海\n" +
                    "淮北凡伟\n" +
                    "西宁谭馨\n" +
                    "吉林盛庄商贸有限公司\n" +
                    "乐清杨宇佩\n" +
                    "齐齐哈尔杨柳\n" +
                    "温州市乐绮服装有限公司\n" +
                    "南昌沅尚实业发展有限公司\n" +
                    "惠州王淑梅\n" +
                    "湖州李燕\n" +
                    "重庆焜昱商贸有限公司\n" +
                    "珠海王飞\n" +
                    "大荆蔡女士\n" +
                    "大连金州岳希兰\n" +
                    "甘肃恩典商贸有限公司\n" +
                    "东莞玖鑫商贸有限公司\n" +
                    "赤峰邵德卓\n" +
                    "抚州万里玲\n" +
                    "红河州新都贸易有限公司\n" +
                    "济南鑫钇服饰有限公司\n" +
                    "牡丹江甜甜百货\n" +
                    "太原段晓清\n" +
                    "酒泉陆海蓉\n" +
                    "内蒙古欧尚时装有限责任公司\n" +
                    "台州好世商贸有限公司\n" +
                    "宿迁市盛卓贸易有限公司\n" +
                    "扬州蕾尚商贸有限公司\n" +
                    "梧州孔碧媛\n" +
                    "邛崃杨梦\n" +
                    "南充南部县陈婉怡\n" +
                    "唐山厚德宏恩商贸有限公司\n" +
                    "厦门越千阳发展有限公司\n" +
                    "厦门市越千阳发展有限公司\n" +
                    "沧州吴忠广\n" +
                    "临汾张新华\n" +
                    "阜新周美玉\n" +
                    "莆田林志高\n" +
                    "福清徐金玉\n" +
                    "大连庄河张婷婷\n" +
                    "大同天一美佳\n" +
                    "恩施潘巧云\n" +
                    "连云港刘路\n" +
                    "苍南林海棠\n" +
                    "黄石瞿萍\n" +
                    "深圳森美丽服饰\n" +
                    "亳州杨树\n" +
                    "荆州美琪服饰有限公司\n" +
                    "秦皇岛陈岳武\n" +
                    "个旧周媛\n" +
                    "海拉尔吴暇\n" +
                    "宿迁沭阳丁磊\n" +
                    "吉安罗圆媛\n" +
                    "湖南耒阳王文娟\n" +
                    "河北承德胡显峰\n" +
                    "澳门noble group\n" +
                    "巴彦淖尔杨力嘉\n" +
                    "建瓯范瑞华\n" +
                    "盘锦刘海韬\n" +
                    "辽源付鑫\n" +
                    "广安郭剑波\n" +
                    "益阳陈仕良\n" +
                    "格尔木王爱辉\n" +
                    "清远潘燕屏\n" +
                    "中山小榄朱巧婓\n" +
                    "十堰肖丹丹\n" +
                    "营口鲅鱼圈区韩松\n" +
                    "锦州侯睿\n" +
                    "拉萨丁小莉\n" +
                    "四平郭霞\n" +
                    "梧州侯齐花\n" +
                    "临海鹿城路杨鹏\n" +
                    "北安张跃三\n" +
                    "大庆王慧\n" +
                    "乌鲁木齐张凯\n" +
                    "衡阳胡亚丽\n" +
                    "宁国程军\n" +
                    "晋城常丽波\n" +
                    "资阳肖雪\n" +
                    "马鞍山徐锐\n" +
                    "鹰潭齐秋英\n" +
                    "LANECRAWFORD（北京）\n" +
                    "LAB CONCEPT (HONG KONG)\n" +
                    "黄山张末莲\n" +
                    "樟木头钟文佳\n" +
                    "乌兰察布张永胜\n" +
                    "靖江王洁\n" +
                    "海口李文\n" +
                    "湖北襄樊孙雯\n" +
                    "贵州青茶麦社贸易有限公司\n" +
                    "常德郭华芬\n" +
                    "瓦房店姜景涛\n" +
                    "温岭冯乐燕\n" +
                    "徐州胡建国\n" +
                    "无锡金辰皓\n" +
                    "内蒙满洲里闫红\n" +
                    "佛山市三水恒福兴达投资发展\n" +
                    "昆明蔓铭经贸有限公司\n" +
                    "桂林都锦服饰有限公司\n" +
                    "四川峨眉山陈斌\n" +
                    "瑞安陈晓丹\n" +
                    "荆门郭晓红\n" +
                    "温州泰顺彭建华\n" +
                    "淮安戴晓佳\n" +
                    "哈尔滨元福祥经贸有限公司\n" +
                    "珠海优仕时裳企业管理公司\n" +
                    "柳州市恩瑟贸易有限公司\n" +
                    "淄博张店区陈小良\n" +
                    "宜兴陆亿\n" +
                    "韶关杨锦燕\n" +
                    "淮安马淮萍\n" +
                    "巴中罗齐国\n" +
                    "广元王维海\n" +
                    "烟台邢希建（佳跃经贸）\n" +
                    "烟台市雍惠茂经贸有限公司\n" +
                    "漳州孙晓梅\n" +
                    "河源卢思易\n" +
                    "中山许小华\n" +
                    "东莞长安叶振宁\n" +
                    "沈阳黄晓贝（天瑞柏利贸易）\n" +
                    "温州水头陈祖麟\n" +
                    "盐城市字集服饰贸易有限公司\n" +
                    "天水曹小利\n" +
                    "瓦房店大伟时代购物中心\n" +
                    "潮州何红\n" +
                    "福州雷燕\n" +
                    "四川雅安杨钧（浪漫服饰）\n" +
                    "呼和浩特燕晓东（包头凌跃商贸有限公司）\n" +
                    "眉山徐建兵\n" +
                    "太原刘亚莉（汶怡服装有限公司）\n" +
                    "黑龙江省牡丹江王钊\n" +
                    "四川彭州黄正君（眼之魅名品服）\n" +
                    "四川遂宁李海燕\n" +
                    "四川成都三智一境贸易有限公司\n" +
                    "福建福鼎市大姥郑芙蓉\n" +
                    "四川成都睿理商贸有限公司\n" +
                    "湛江鑫海何名江\n" +
                    "玉林吴英杰（南宁百货大楼）\n" +
                    "辽宁省葫芦岛李强\n" +
                    "浙江上虞万和城叶柏庆\n" +
                    "聊城宏图物资（任斌）\n" +
                    "嘉兴范朗服饰有限公司\n" +
                    "山东济宁孟涛（上海启恒工贸）\n" +
                    "云南普洱李青\n" +
                    "四川自贡黄凯\n" +
                    "甘肃酒泉张金秀\n" +
                    "甘肃庆阳任建星（许德鹏）\n" +
                    "四川什邡春熙杨雪\n" +
                    "阜阳王利娟\n" +
                    "德阳广汉赵昆蓉\n" +
                    "惠州何晓琪\n" +
                    "乌海张海霞\n" +
                    "汕头市吸引力时装有限公司\n" +
                    "舟山定海福润德商贸有限公司\n" +
                    "鄂尔多斯孙俊丽\n" +
                    "合肥麒翎贸易有限公司\n" +
                    "潍坊百谷王经贸有限公司\n" +
                    "东营宜海蓝商贸有限公司\n" +
                    "江西赣州刘燕\n" +
                    "贵州泓源洁琳公司\n" +
                    "辽宁辽阳刘瑶\n" +
                    "南通利亿经贸公司\n" +
                    "山西长治马小珍\n" +
                    "浙江桐乡邱琴芳\n" +
                    "佳木斯高原\n" +
                    "绵阳王晓蓉\n" +
                    "赤峰徐亮亮\n" +
                    "靖江天一百崔理学\n" +
                    "辽宁瓦房店邹明发\n" +
                    "宁波奉化葛竺琼\n" +
                    "临海鹿城路汪阳\n" +
                    "秦皇岛现代马红卫\n" +
                    "上饶亿升陈菊琴\n" +
                    "昆山市玉山镇玫瑰秀服饰\n" +
                    "云南昭通罗黎\n" +
                    "台州耀达罗慧\n" +
                    "汕头曾苏霓（已结业）\n" +
                    "重庆鼎灵商贸有限公司\n" +
                    "湖州李燕\n" +
                    "兰州国芳张娜妮\n" +
                    "金华市驭骊服饰有限公司\n" +
                    "巴彦淖尔杨宗颖\n" +
                    "普兰邹明发\n" +
                    "余姚胡雅红\n" +
                    "六安涂霄月白云商厦（已结业）\n" +
                    "郑州莉荣服饰有限公司\n" +
                    "台州温岭陈瑜\n" +
                    "云南省曲靖胡冬梅\n" +
                    "宁波北仑谢承芳\n" +
                    "玉环兰莎莎\n" +
                    "慈溪胡雅红\n" +
                    "山东淄博(青岛左岸经贸)\n" +
                    "贵阳百盛（贵阳雪浣绫服饰）\n" +
                    "山西大同祁斌\n" +
                    "玉溪刘琼梅\n" +
                    "海城赵美竹\n" +
                    "萧山陈萍\n" +
                    "义乌陆文科（宁波海曙公司）\n" +
                    "海宁谢菊霞\n" +
                    "嘉善沈英\n" +
                    "乌鲁木齐梁斌\n" +
                    "乐山市曹芳商贸有限公司\n" +
                    "西昌\n" +
                    "四川攀枝花专卖店\n" +
                    "四川南充盛华堂百货（陈佳）\n" +
                    "临安施燕（已结业）\n" +
                    "新会周海芹\n" +
                    "吴江市-陈秀丽\n" +
                    "张家港宏佳伟业服饰\n" +
                    "保定（石家庄尚岑商贸有限公司）\n" +
                    "南宁鸿硕商贸有限公司（黄杉）\n" +
                    "浙江富阳吴叶波\n" +
                    "塘下高洁\n" +
                    "宁波象山杨构仙\n" +
                    "嘉兴天虹杨明军\n" +
                    "虹桥陈圆\n" +
                    "衢州俞刚\n" +
                    "江门卢琳\n" +
                    "齐齐哈尔赵天阳\n" +
                    "唐山桂鼎商贸有限公司（胡琳娜）\n" +
                    "福建省福州市邹晶\n" +
                    "陕西省宝鸡市银座百货\n" +
                    "宁夏银川沈燕\n" +
                    "西安嘉正\n" +
                    "桂林市钟如玉（已结业）\n" +
                    "玉林牟春凤\n" +
                    "昆明曹睿（已结业）\n" +
                    "厦门李志宏\n" +
                    "南昌多美丽唐云华\n" +
                    "南昌唐丽娟\n" +
                    "安徽蚌埠叶利胜\n" +
                    "安徽马鞍山\n" +
                    "江苏常州凌清\n" +
                    "江苏镇江于雷\n" +
                    "南京市徐丽丽\n" +
                    "福建泉州庄哲声\n" +
                    "合肥尹冶（已结业）\n" +
                    "吉首贺娟娟\n" +
                    "株洲红粉时尚商贸有限公司\n" +
                    "越南胡志明市\n" +
                    "阳江刘小丹\n" +
                    "海口刘砺迁\n" +
                    "广州赖柏林\n" +
                    "东莞张永达\n" +
                    "东莞塘厦欧肖凤\n" +
                    "东莞地王广场唐雅尼\n" +
                    "肇庆吴晓燕\n" +
                    "珠海市何清梅\n" +
                    "惠州梁雪敏\n" +
                    "佛山顺德区东汇成商贸有限公司\n" +
                    "辽宁沈阳葫芦岛王海涛\n" +
                    "辽宁沈阳王敬华\n" +
                    "特许自营分区\n" +
                    "华北自营分区\n" +
                    "华东自营分区\n" +
                    "华南自营分区\n";

            String[] themes = activity_themes.split("\n");
            String[] areas = area_names.split("\n");
            for (int i = 0; i < themes.length; i++) {
                String theme = themes[i];
                String area_code = theme.replace("生日送券","");
                theme = "MO"+theme;

                VipActivity vipActivity = vipActivityService.getVipActivityByTheme(corp_code,theme);
                if (vipActivity == null){
                    JSONArray coupon_array = new JSONArray();
                    JSONObject coupon_obj1 =  new JSONObject();
                    coupon_obj1.put("coupon_code",area_code+"07");
                    coupon_obj1.put("coupon_name","生日7折券("+areas[i]+")");
                    coupon_array.add(coupon_obj1);

                    JSONArray cou_array = new JSONArray();
                    JSONObject object = new JSONObject();
                    object.put("anniversary_time","M");
                    object.put("param_desc","生日（个人资料）");
                    object.put("param_name","BIRTHDAY");
                    object.put("coupon_type","coupon_array");
                    cou_array.add(object);

                    VipActivity activity = new VipActivity();
                    Date now = new Date();
                    String activity_code = "A" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(now);
                    activity.setCorp_code(corp_code);
                    activity.setActivity_code(activity_code);
                    activity.setActivity_theme(theme);
                    activity.setStart_time("2017-9-29  00:00:00");
                    activity.setEnd_time("2092-9-24  0:00:00");
                    activity.setRun_mode("coupon");
                    activity.setTarget_vips("[{\"names\":\"卡类型\",\"type\":\"text\",\"value\":\"MOCO会员卡(8.8折),MOCO贵宾卡(8.5折),MOCO尊贵卡(8折)\",\"key\":\"12\"}]");
                    activity.setVip_condition("{\"screen_type\":\"difficult\",\"screen\":[{\"names\":\"卡类型\",\"type\":\"text\",\"value\":\"MOCO会员卡(8.8折),MOCO贵宾卡(8.5折),MOCO尊贵卡(8折)\",\"key\":\"12\"}]}");
                    activity.setTarget_vips_count("335989");
                    activity.setTask_status("N");
                    activity.setSend_status("N");
                    activity.setActivity_state(Common.ACTIVITY_STATUS_0);
                    activity.setRun_scope("{\"store_code\":\"\",\"brand_code\":\"\",\"area_code\":\""+area_code+"\"}");
                    activity.setCreated_date("2017-09-29 17:45:19");
                    activity.setModified_date("2017-09-29 17:45:19");
                    activity.setCreater("10000");
                    activity.setModifier("10000");
                    activity.setIsactive(Common.IS_ACTIVE_Y);
                    activity.setApp_id("wx222cbd523cae37f0");
                    activity.setApp_name("MO&Co.官方服务号");
                    groupService.insert(activity);

                    VipActivityDetail detail = new VipActivityDetail();
                    detail.setActivity_code(activity_code);
                    detail.setCorp_code(corp_code);
                    detail.setActivity_type("coupon");
                    detail.setSend_coupon_type("anniversary");
                    detail.setCoupon_type(JSON.toJSONString(cou_array));
                    detail.setCreated_date("2017-09-29 17:45:19");
                    detail.setModified_date("2017-09-29 17:45:19");
                    detail.setCreater("10000");
                    detail.setModifier("10000");
                    detail.setIsactive(Common.IS_ACTIVE_Y);
                    groupService.insertDetail(detail);

                    VipActivityDetailAnniversary anniversary = new VipActivityDetailAnniversary();
                    anniversary.setActivity_code(activity_code);
                    anniversary.setCorp_code(corp_code);
                    anniversary.setParam_name("BIRTHDAY");
                    anniversary.setParam_desc("生日（个人资料）");
                    anniversary.setRun_time_type("M");
                    anniversary.setCoupon_type(JSON.toJSONString(coupon_array));
                    anniversary.setCreated_date("2017-09-29 17:45:19");
                    anniversary.setModified_date("2017-09-29 17:45:19");
                    anniversary.setCreater("10000");
                    anniversary.setModifier("10000");
                    anniversary.setIsactive(Common.IS_ACTIVE_N);
                    groupService.insertDetailAnniversary(anniversary);
                    count += 1;

                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("创建活动数："+count);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

//    @RequestMapping(value = "/api/update", method = RequestMethod.GET)
//    @ResponseBody
//    public String update(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            int count = 0;
//
//            String corp_code = "C10183";
//            Map<String,String> map = new HashMap<String, String>();
//            map.put("app_name","Edition10");
//            map.put("activity_theme","开卡");
//
//            PageInfo<VipActivity> vipActivities = vipActivityService.selectActivityAllScreen(1,163,corp_code,"",map);
//            List<VipActivity> vipActivities1 = vipActivities.getList();
//
////            PageInfo<VipActivityDetail> vipActivityDetails = vipActivityDetailService.selectAllActivityDetail(1,220,"C10183","","");
////            List<VipActivityDetail> vipActivities1 = vipActivityDetails.getList();
//
//            for (int i = 0; i < vipActivities1.size(); i++) {
//                String activity_code = vipActivities1.get(i).getActivity_code();
//                VipActivityDetail vipAct = vipActivityDetailService.selActivityDetailByCode(activity_code);
//                String run_mode = vipAct.getActivity_type();
//                if (run_mode.equals("coupon") && vipAct.getSend_coupon_type().equals("card")){
//                    String coupon_type = vipAct.getCoupon_type();
//                    JSONArray coup_array = JSONArray.parseArray(coupon_type);
//                    JSONArray new_array = new JSONArray();
//                    for (int j = 0; j < coup_array.size(); j++) {
//                        JSONObject coup_obj = coup_array.getJSONObject(j);
//                        if (coup_obj.get("vip_card_type_code").equals("11")){
//                            new_array.add(coup_obj);
//                        }
//                    }
//                    vipAct.setCoupon_type(JSON.toJSONString(new_array));
//                    groupService.updateDetail(vipAct);
//                }
//            }
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId(id);
//            dataBean.setMessage("创建活动数："+count);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

}
