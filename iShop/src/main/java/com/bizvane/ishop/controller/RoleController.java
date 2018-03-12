package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Function;
import com.bizvane.ishop.entity.Group;
import com.bizvane.ishop.entity.Role;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.GroupService;
import com.bizvane.ishop.service.RoleService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */
/*
*用户及权限
*/
@Controller
@RequestMapping("/user")
public class RoleController {

    @Autowired
    private FunctionService functionService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private BaseService baseService;
    private static Logger logger = LoggerFactory.getLogger((RoleController.class));
    String id;

    /**
     * 角色定义
     */
    @RequestMapping(value = "/role/list", method = RequestMethod.GET)
    @ResponseBody
    public String RoleManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONObject result = new JSONObject();
            PageInfo<Role> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = roleService.selectAllRole(page_number, page_size, "");
                result.put("list", JSON.toJSONString(list));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result.toString());
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
     * 角色定义
     * 新增
     */
    @RequestMapping(value = "/role/add", method = RequestMethod.POST)
    @ResponseBody
    public String addRole(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String user_id = request.getSession(false).getAttribute("user_code").toString();
            // String jsString = request.getSession(false).getAttribute("param").toString();
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);

            Role role1 = new Role();
            role1.setRole_code(jsonObject.get("role_code").toString());
            role1.setRole_name(jsonObject.get("role_name").toString());
            role1.setRemark(jsonObject.get("remark").toString());
            Date now = new Date();
            role1.setModified_date(Common.DATETIME_FORMAT.format(now));
            role1.setModifier(user_id);
            role1.setCreated_date(Common.DATETIME_FORMAT.format(now));
            role1.setCreater(user_id);
            role1.setIsactive(jsonObject.get("isactive").toString());
            String result = roleService.insertRole(role1);
            if (!result.equals(Common.DATABEAN_CODE_SUCCESS)) {

                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);

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
                String function = "权限管理_角色定义";
                String action = Common.ACTION_ADD;
                String t_corp_code = operation_corp_code;
                String t_code = action_json.get("role_code").toString();
                String t_name = action_json.get("role_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setId(id);
                Role role=roleService.getRoleForID(role1.getRole_code());
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(String.valueOf(role.getId()));
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/role/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteRole(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            String[] role_ids = jsonObject.get("id").toString().split(",");
            String msg = null;
            for (int i = 0; i < role_ids.length; i++) {
                int role_id = Integer.parseInt(role_ids[i]);
                String role_code = roleService.selectByRoleId(role_id).getRole_code();
                List<Group> groups = groupService.selectByRole(role_code);
                if (groups.size() > 0) {
                    msg = "角色" + role_code + "下有所属群组，请先处理角色下群组再删除";
                    break;
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
                String function = "权限管理_角色定义";
                String action = Common.ACTION_DEL;
                String t_corp_code = operation_corp_code;
                String t_code = roleService.selectByRoleId(role_id).getRole_code();
                String t_name = roleService.selectByRoleId(role_id).getRole_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
            if (msg == null) {
                for (int i = 0; i < role_ids.length; i++) {
                    int role_id = Integer.parseInt(role_ids[i]);
                    String role_code = roleService.selectByRoleId(role_id).getRole_code();
                    roleService.deleteByRoleId(role_id,role_code);
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
            ;
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/role/select", method = RequestMethod.POST)
    @ResponseBody
    public String editbefore(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {

            String user_id = request.getSession(false).getAttribute("user_id").toString();
            String jsString = request.getParameter("param");

             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
             JSONObject jsonObject = JSONObject.parseObject(message);
            int role_id = Integer.parseInt(jsonObject.get("id").toString());
            Role role = roleService.selectByRoleId(role_id);
            String role_code = role.getRole_code();
            JSONArray role_privilege = functionService.selectRolePrivilege(role_code);

            int privilege_count = role_privilege.size();
            JSONObject result = new JSONObject();
            result.put("data", JSON.toJSONString(role));
            result.put("privilege_count", privilege_count);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());

        } catch (Exception e) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /**
     * 角色定义
     * 编辑
     */
    @RequestMapping(value = "/role/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editRole(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
             JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            Role role1 = new Role();
            role1.setId(Integer.parseInt(jsonObject.get("id").toString()));
            role1.setRole_code(jsonObject.get("role_code").toString());
            role1.setRole_name(jsonObject.get("role_name").toString());
            role1.setRemark(jsonObject.get("remark").toString());
            Date now = new Date();
            role1.setModified_date(Common.DATETIME_FORMAT.format(now));
            role1.setModifier(user_id);
            role1.setIsactive(jsonObject.get("isactive").toString());

            String result = roleService.updateByRoleId(role1);
            if (!result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);

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
                String function = "权限管理_角色定义";
                String action = Common.ACTION_UPD;
                String t_corp_code = operation_corp_code;
                String t_code = action_json.get("role_code").toString();
                String t_name = action_json.get("role_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("edit success");
            }
        } catch (Exception ex) {
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 角色定义之
     * 编辑角色信息之
     * 查看权限
     */
    @RequestMapping(value = "/role/check_power", method = RequestMethod.POST)
    @ResponseBody
    public String roleCheckPower(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject =JSONObject.parseObject(message);
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

            String role_code = jsonObject.get("role_code").toString();
            //获取群组角色的权限
            JSONArray role_privilege = functionService.selectRolePrivilege(role_code);

            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(funcs));
            result.put("live", role_privilege);

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

}
