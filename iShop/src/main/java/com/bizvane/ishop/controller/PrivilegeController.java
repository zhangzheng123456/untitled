package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.CorpParam;
import com.bizvane.ishop.entity.ParamConfigure;
import com.bizvane.ishop.entity.Privilege;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.CorpParamService;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.ParamConfigureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/1.
 */
@Controller
@RequestMapping("/privilege")
public class PrivilegeController {

    @Autowired
    private FunctionService functionService;
    @Autowired
    ParamConfigureService paramConfigureService;
    @Autowired
    CorpParamService corpParamService;
    @Autowired
    private BaseService baseService;
    private static Logger logger = LoggerFactory.getLogger((PrivilegeController.class));
    String id;

    /**
     * 查看权限
     */
    @RequestMapping(value = "/checkPower", method = RequestMethod.POST)
    @ResponseBody
    public String checkPower(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String login_user_code = request.getSession().getAttribute("user_code").toString();
        String login_corp_code = request.getSession().getAttribute("corp_code").toString();
        String login_role_code = request.getSession().getAttribute("role_code").toString();
        String login_group_code = request.getSession().getAttribute("group_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String type = jsonObject.get("type").toString();
            String search_value = "";
            if (jsonObject.containsKey("searchValue")) {
                search_value = jsonObject.get("searchValue").toString();
            }
            //获取登录用户的所有权限
            JSONArray funcs = functionService.selectLoginPrivilege(login_corp_code, login_role_code, login_user_code, login_group_code, search_value);

            JSONArray privilege_status = new JSONArray();
            if (type.equals("group")) {
                //查看群组权限
                String group_code = jsonObject.get("group_code").toString();
                String corp_code = jsonObject.get("corp_code").toString();
                String role_code = jsonObject.get("role_code").toString();
                group_code = corp_code + "G" + group_code;
                //设置该群组所在角色的权限为不可修改状态
                JSONArray privilege_status1 = functionService.selectPrivilegeStatus("", "", role_code, "N", "Y", funcs);
                //设置该群组拥有的权限为可修改状态
                privilege_status = functionService.selectPrivilegeStatus("", group_code, "", "Y", "N", privilege_status1);
            } else if (type.equals("role")) {
                //查看角色权限
                String role_code = jsonObject.get("role_code").toString();
                //设置该角色拥有的权限为可修改状态
                privilege_status = functionService.selectPrivilegeStatus("", "", role_code, "Y", "N", funcs);
            } else {
                //查看用户权限
                String corp_code = jsonObject.get("corp_code").toString();
                String group_code = jsonObject.get("group_code").toString();
                String user_code = jsonObject.get("user_code").toString();
                String role_code = jsonObject.get("role_code").toString();
                group_code = corp_code + "G" + group_code;
                user_code = corp_code + "U" + user_code;
                //设置该用户所在群组，角色的权限为不可修改状态
                JSONArray privilege_status1 = functionService.selectPrivilegeStatus("", group_code, role_code, "N", "Y", funcs);
                //设置该用户拥有的权限为可修改状态
                privilege_status = functionService.selectPrivilegeStatus(user_code, "", "", "Y", "N", privilege_status1);
            }

            JSONObject result = new JSONObject();
            result.put("list", privilege_status);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 新增，编辑权限
     */
    @RequestMapping(value = "/checkPower/save", method = RequestMethod.POST)
    @ResponseBody
    public String checkPowerSave(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String del_act_id = jsonObject.get("del_act_id").toString();
            String del_col_id = jsonObject.get("del_col_id").toString();
            String add_action = jsonObject.get("add_action").toString();
            String add_column = jsonObject.get("add_column").toString();

            String type = jsonObject.get("type").toString();

            JSONArray act_array = JSONArray.parseArray(add_action);
            JSONArray col_array = JSONArray.parseArray(add_column);
            String master_code;
            if (type.equals("group")) {
                String group_code = jsonObject.get("group_code").toString();
                String corp_code = jsonObject.get("corp_code").toString();
                master_code = corp_code + "G" + group_code;
            } else if (type.equals("role")) {
                String role_code = jsonObject.get("role_code").toString();
                master_code = role_code;
            } else {
                String user_code1 = jsonObject.get("user_code").toString();
                String corp_code = jsonObject.get("corp_code").toString();
                master_code = corp_code + "U" + user_code1;
            }

            String result = functionService.updateACPrivilege(master_code, user_code, del_act_id, act_array, del_col_id, col_array);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
                if (type.equals("group")) {
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
                    String function = "群组管理_编辑权限";
                    String action = Common.ACTION_ADD;
                    String t_corp_code = action_json.get("corp_code").toString();
                    String t_code = action_json.get("group_code").toString();
                    String t_name = action_json.get("add_column").toString();
                    String remark = action_json.get("del_col_id").toString();
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                    //-------------------行为日志结束----


                } else if (type.equals("role")) {
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
                    String function = "角色定义_编辑权限";
                    String action = Common.ACTION_ADD;
                    String t_corp_code = "";
                    String t_code = action_json.get("role_code").toString();
                    String t_name = action_json.get("add_column").toString();
                    String remark = action_json.get("del_col_id").toString();
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                    //-------------------行为日志结束----

                }else{

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
                    String function = "其他_编辑权限";
                    String action = Common.ACTION_ADD;
                    String t_corp_code = "";
                    String t_code ="";
                    String t_name = action_json.get("add_column").toString();
                    String remark = action_json.get("del_col_id").toString();
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                    //-------------------行为日志结束----


                }


            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 记住会员分析，会员列表页面的图表
     */
    @RequestMapping(value = "/vip/chartOrder", method = RequestMethod.POST)
    @ResponseBody
    public String vipChartOrder(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String group_code = request.getSession().getAttribute("group_code").toString();

        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String type = jsonObject.get("type").toString();
            String function_code = jsonObject.get("function_code").toString();
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();

            if (type.equals("show")) {
                //图表显示
                String conf = "";
                ParamConfigure param = paramConfigureService.getParamByKey(CommonValue.CRM_CHART_PRIVIL_CONF, Common.IS_ACTIVE_Y);
                if (param != null) {
                    String id = String.valueOf(param.getId());
                    List<CorpParam> corpParams = corpParamService.selectByCorpParam(corp_code, id, Common.IS_ACTIVE_Y);
                    if (corpParams.size() > 0)
                        conf = corpParams.get(0).getParam_value();
                }

                List<Privilege> privileges1 = new ArrayList<Privilege>();
                String can_add = "Y";
                if (conf.equals("")) {
                    List<Privilege> privileges = functionService.selectColPrivilegeByUser(function_code, corp_code + "U" + user_code);
                    for (int i = 0; i < privileges.size(); i++) {
                        if (privileges.get(i).getColumn_name().startsWith("[") && privileges.get(i).getColumn_name().endsWith("]")) {
                            privileges1.add(privileges.get(i));
                        }
                    }
                } else {
                    Privilege privilege = new Privilege();
                    privilege.setColumn_name(conf);
                    privilege.setFunction_code(function_code);
                    privilege.setEnable("Y");
                    privilege.setIsactive(Common.IS_ACTIVE_Y);
                    privileges1.add(privilege);
                    can_add = "N";
                }
                JSONObject result = new JSONObject();
                result.put("can_add", can_add);
                result.put("list", privileges1);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(result.toString());
            } else {
                //更新图表显示和顺序
                String order = jsonObject.get("order").toString();
                functionService.updateColPrivilegeByUser(function_code, order, corp_code, user_code);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }
}
