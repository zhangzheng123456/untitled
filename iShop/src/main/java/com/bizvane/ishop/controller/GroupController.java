package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Group;
import com.bizvane.ishop.entity.Role;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.Function;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.GroupService;
import com.bizvane.ishop.service.RoleService;
import com.bizvane.ishop.service.UserService;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);

    private static Logger logger = LoggerFactory.getLogger((GroupController.class));
    String id;

    /**
     * 群组管理
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String groupManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int user_id = Integer.parseInt(request.getSession().getAttribute("user_id").toString());
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONArray actions = functionService.selectActionByFun(corp_code+user_code,corp_code+group_code, role_code, function_code);
            JSONObject result = new JSONObject();
            PageInfo<Group> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = groupService.getGroupAll(page_number, page_size, "", "");
            } else {
                list = groupService.getGroupAll(page_number, page_size, corp_code, "");
            }
            result.put("list", JSON.toJSONString(list));
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 群组管理
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--area add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            Group group = new Group();
            Date now = new Date();

            //为新增群组，计算group_code
            String max_code = groupService.selectMaxCode();
            int code = Integer.parseInt(max_code.substring(1, max_code.length())) + 1;
            Integer c = code;
            int length = max_code.length() - c.toString().length() - 1;
            String group_code = "G";
            for (int i = 0; i < length; i++) {
                group_code = group_code + "0";
            }
            group_code = group_code + code;

            group.setGroup_code(group_code);
            group.setGroup_name(jsonObject.get("group_name").toString());
            group.setRole_code(jsonObject.get("role_code").toString());
            group.setCorp_code(jsonObject.get("corp_code").toString());
            group.setRemark(jsonObject.get("remark").toString());
            group.setCreated_date(sdf.format(now));
            group.setCreater(user_id);
            group.setModified_date(sdf.format(now));
            group.setModifier(user_id);
            group.setIsactive(jsonObject.get("isactive").toString());
            groupService.insertGroup(group);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("add success");
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
        String user_id = request.getSession().getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--area edit-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            Group group = new Group();
            Date now = new Date();
            group.setId(Integer.parseInt(jsonObject.get("id").toString()));
            group.setGroup_code(jsonObject.get("group_code").toString());
            group.setGroup_name(jsonObject.get("group_name").toString());
            group.setRole_code(jsonObject.get("role_code").toString());
            group.setCorp_code(jsonObject.get("corp_code").toString());
            group.setRemark(jsonObject.get("remark").toString());
            group.setModifier(user_id);
            group.setModified_date(sdf.format(now));
            group.setIsactive(jsonObject.get("isactive").toString());
            groupService.updateGroup(group);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String area_id = jsonObject.get("id").toString();
            String[] ids = area_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                Group group = groupService.getGroupById(Integer.valueOf(ids[i]));
                String group_code = group.getGroup_code();
                String corp_code = group.getCorp_code();
                List<User> users = userService.selectGroup(corp_code,group_code);
                if (users.size() == 0) {
                groupService.deleteGroup(Integer.valueOf(ids[i]));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
                }else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("该群组下有所属员工，请先处理群组下员工再删除！");
                }
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String group_id = jsonObject.get("id").toString();
            Group group = groupService.getGroupById(Integer.parseInt(group_id));
            String corp_code = group.getCorp_code();
            String role_code = group.getRole_code();
            String group_code = group.getGroup_code();
            //群组用户个数
            int user_count = userService.selectGroupUser(corp_code,group_code);

            //获取群组角色的权限
            JSONArray role_privilege = functionService.selectRolePrivilege(role_code);
            //获取群组自定义的权限
            JSONArray group_privilege = functionService.selectGroupPrivilege(corp_code+group_code);
            //群组权限个数
            int privilege_count = role_privilege.size()+group_privilege.size();

            JSONObject group_info = new JSONObject();
            group_info.put("user_count",user_count);
            group_info.put("privilege_count",privilege_count);
            group_info.put("data",JSON.toJSONString(group));
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Group> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = groupService.getGroupAll(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                list = groupService.getGroupAll(page_number, page_size, corp_code, search_value);
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
            if (role_code.equals(Common.ROLE_SYS)) {
                roles = roleService.selectAll("");
            } else {
                roles = roleService.selectCorpRole(role_code);
            }
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String group_code = jsonObject.get("group_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            PageInfo<User> users = userService.selectGroupUser(page_number, page_size, corp_code, group_code);
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String login_user_code = request.getSession().getAttribute("user_code").toString();
            String login_corp_code = request.getSession().getAttribute("corp_code").toString();
            String login_role_code = request.getSession().getAttribute("role_code").toString();
            String login_group_code = request.getSession().getAttribute("group_code").toString();

            //获取登录用户的所有权限
            List<Function> funcs = functionService.selectAllPrivilege(login_role_code, login_corp_code+login_user_code, login_corp_code+login_group_code);

            String group_code = jsonObject.get("group_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Group group = groupService.selectByCode(corp_code,group_code,"Y");
            String role_code = group.getRole_code();

            //获取群组角色的权限
            JSONArray role_privilege = functionService.selectRolePrivilege(role_code);

            //获取群组自定义的权限
            JSONArray group_privilege = functionService.selectGroupPrivilege(corp_code+group_code);

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
            String user_id = request.getSession().getAttribute("user_id").toString();

            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String list = jsonObject.get("list").toString();
            JSONArray array = JSONArray.parseArray(list);

            String group_code = jsonObject.get("group_code").toString();
            String master_code;
            if (jsonObject.has("corp_code")) {
                String corp_code = jsonObject.get("corp_code").toString();
                master_code = corp_code + group_code;
            }else {
                master_code = group_code;
            }

            String result = functionService.updatePrivilege(master_code, user_id,array);
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

}
