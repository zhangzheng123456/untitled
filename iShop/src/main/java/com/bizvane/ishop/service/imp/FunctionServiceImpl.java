package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.FunctionMapper;
import com.bizvane.ishop.dao.PrivilegeMapper;
import com.bizvane.ishop.dao.TableManagerMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Administrator on 2016/5/24.
 */
@Service
public class FunctionServiceImpl implements FunctionService {

    @Autowired
    FunctionMapper functionMapper;
    @Autowired
    PrivilegeMapper privilegeMapper;
    @Autowired
    TableManagerMapper tableManagerMapper;

    /**
     * 获取user所有列表功能模块
     */
    public JSONArray selectAllFunctions(String corp_code,String user_code, String group_code, String role_code) throws Exception{
        List<Function> func_info;
        user_code = corp_code +"U"+user_code;
        group_code = corp_code +"G"+group_code;
        //获取user所有功能模块
        func_info = functionMapper.selectAllFun(user_code, group_code, role_code);
        JSONArray modules = new JSONArray();
        for (int i = 0; i < func_info.size(); i++) {
            String module = func_info.get(i).getModule_name();
            String func = func_info.get(i).getFunction_name();
            String func_code = func_info.get(i).getFunction_code();
            String url = func_info.get(i).getUrl();
            String icon = func_info.get(i).getIcon();
            if (func.equals("")) {
                JSONObject obj1 = new JSONObject();
                obj1.put("mod_name", module);
                obj1.put("icon", icon);
                obj1.put("func_code", func_code);
                obj1.put("url", url);
                obj1.put("functions", "");
                modules.add(obj1);
            } else {
                if (modules.size() == 0) {
                    JSONObject obj = new JSONObject();
                    JSONArray functions = new JSONArray();
                    JSONObject obj1 = new JSONObject();
                    obj1.put("fun_name", func);
                    obj1.put("func_code", func_code);
                    obj1.put("url", url);
                    functions.add(obj1);
                    obj.put("mod_name", module);
                    obj.put("icon", icon);
                    obj.put("functions", functions);
                    modules.add(obj);
                } else {
                    for (int j = 0; j < modules.size(); j++) {
                        JSONObject object = (JSONObject) modules.get(j);
                        if (object.get("mod_name").equals(module)) {
                            String a = object.get("functions").toString();
                            JSONArray qq = JSONArray.parseArray(a);
                            JSONObject obj2 = new JSONObject();
                            obj2.put("fun_name", func);
                            obj2.put("func_code", func_code);
                            obj2.put("url", url);
                            qq.add(obj2);
                            object.put("functions", qq);
                            break;
                        } else {
                            if (j == modules.size() - 1) {
                                JSONObject mod = new JSONObject();
                                JSONArray functions = new JSONArray();
                                mod.put("mod_name", module);
                                mod.put("icon", icon);
                                mod.put("functions", functions);
                                modules.add(mod);
                            }
                            continue;
                        }
                    }
                }
            }
        }
        return modules;
    }

    /**
     * 获取用户所有动作权限
     * （暂未使用）
     */
//    public JSONArray selectAllActions(String user_code, String role_code, String group_code) {
//        List<Function> act_info = functionMapper.selectPrivilege(user_code, role_code, group_code);
//        JSONArray functions = new JSONArray();
//
//        for (int i = 0; i < act_info.size(); i++) {
//            String func_code = act_info.get(i).getFunction_code();
//            //       String func = act_info.get(i).getFunction_name();
//            String act = act_info.get(i).getAction_name();
//            System.out.println(act + "---------" + func_code);
//            if (functions.size() == 0) {
//                JSONObject obj = new JSONObject();
//                JSONArray actions = new JSONArray();
//                JSONObject obj1 = new JSONObject();
//                obj1.put("act_name", act);
//                actions.add(obj1);
//                obj.put("func_code", func_code);
//                obj.put("actions", actions);
//                functions.add(obj);
//            } else {
//                for (int j = 0; j < functions.size(); j++) {
//                    JSONObject object = (JSONObject) functions.get(j);
//                    System.out.println(object);
//                    if (object.get("func_code").equals(func_code)) {
//                        String a = object.get("actions").toString();
//                        JSONArray qq = JSONArray.parseArray(a);
//                        JSONObject obj2 = new JSONObject();
//                        obj2.put("act_name", act);
//                        qq.add(obj2);
//                        object.put("actions", qq);
//                        break;
//                    } else {
//                        if (j == functions.size() - 1) {
//                            JSONObject fun = new JSONObject();
//                            JSONArray actions = new JSONArray();
//                            fun.put("func_code", func_code);
//                            fun.put("actions", actions);
//                            functions.add(fun);
//                        }
//                        continue;
//                    }
//                }
//            }
//        }
//        return functions;
//    }

    /**
     * 按功能获取user动作权限
     */
    public List<Map<String,String>> selectActionByFun(String corp_code,String user_code, String group_code, String role_code, String function_code) throws Exception{
        List<Privilege> act_info;
        user_code = corp_code +"U"+user_code;
        group_code = corp_code +"G"+group_code;
        List<Map<String,String>> actionList = new ArrayList<Map<String,String>>();
        act_info = privilegeMapper.selectActionByFun(user_code, group_code, role_code, function_code);
        for (int i = 0; i < act_info.size(); i++) {
            String act = act_info.get(i).getAction_name();
            Map<String,String> action = new HashMap<String, String>();
            action.put("act_name", act);
            if (!actionList.contains(action))
                actionList.add(action);
        }
        return actionList;
    }


    /**
     * 按功能获取user列表显示字段
     */
    public List<Map<String,String>> selectColumnByFun(String corp_code,String user_code, String group_code, String role_code, String function_code) throws Exception{
        user_code = corp_code +"U"+user_code;
        group_code = corp_code +"G"+group_code;
        List<Map<String,String>> columnList = new ArrayList<Map<String,String>>();

        List<Privilege> column_info = privilegeMapper.selectColumnByFun(user_code, group_code, role_code, function_code);
        for (int i = 0; i < column_info.size(); i++) {
            String column_name = column_info.get(i).getColumn_name();
            String show_name = column_info.get(i).getShow_name();

            Map<String,String> column = new HashMap<String, String>();
            column.put("column_name", column_name);
            column.put("show_name", show_name);

            if (!columnList.contains(column))
                columnList.add(column);
        }
        return columnList;
    }


    /**
     * 列出登录用户的所有权限
     */
    public List<Function> selectAllPrivilege(String corp_code, String role_code, String user_code, String group_code, String search_value) throws Exception{
        user_code = corp_code +"U"+user_code;
        group_code = corp_code +"G"+group_code;
        List<Function> privilege = functionMapper.selectPrivilege(user_code, role_code, group_code, search_value);
        return privilege;
    }

    /**
     * 列出用户所属群组的所有权限
     */
    public JSONArray selectRAGPrivilege(String role_code, String group_code) throws Exception{
        JSONArray array = new JSONArray();
        List<Function> info = functionMapper.selectPrivilege("", role_code, group_code, "");
        for (int i = 0; i < info.size(); i++) {
            int id = info.get(i).getId();
            JSONObject object = new JSONObject();
            object.put("id", id);
            array.add(object);
        }
        return array;
    }

    /**
     * 列出所选角色的权限
     * 返回action id
     */
    public JSONArray selectRolePrivilege(String role_code) throws Exception{
        JSONArray array = new JSONArray();
        List<Function> act_info = functionMapper.selectRolePrivilege(role_code);
        for (int i = 0; i < act_info.size(); i++) {
            int id = act_info.get(i).getId();
            JSONObject corps = new JSONObject();
            corps.put("id", id);
            array.add(corps);
        }
        return array;
    }

    /**
     * 列出所选群组的权限
     * 返回action id
     */
    public JSONArray selectGroupPrivilege(String corp_code,String group_code) throws Exception{
        JSONArray array = new JSONArray();
        group_code = corp_code +"G"+group_code;
        List<Function> act_info = functionMapper.selectGroupPrivilege(group_code);
        for (int i = 0; i < act_info.size(); i++) {
            int id = act_info.get(i).getId();
            JSONObject corps = new JSONObject();
            corps.put("id", id);
            array.add(corps);
        }
        return array;
    }

    /**
     * 列出所选用户的权限
     * 返回action id
     */
    public JSONArray selectUserPrivilege(String corp_code,String user_code) throws Exception{
        JSONArray array = new JSONArray();
        user_code = corp_code +"U"+user_code;
        List<Function> act_info = functionMapper.selectUserPrivilege(user_code);
        for (int i = 0; i < act_info.size(); i++) {
            int id = act_info.get(i).getId();
            JSONObject corps = new JSONObject();
            corps.put("id", id);
            array.add(corps);
        }
        return array;
    }

    /**
     * 更新（用户/群组/角色）的权限
     *
     */
    public String updatePrivilege(String master_code, String user_id, JSONArray array) throws Exception{
        try {
            Date now = new Date();
            //先删除权限下所有权限
            System.out.println("-------begin delete group---------");
            privilegeMapper.delete(master_code);
            //再插入画面选择的权限
            for (int i = 0; i < array.size(); i++) {
                String info = array.get(i).toString();
                JSONObject json = JSONObject.parseObject(info);
                String action_code = json.get("action_code").toString();
                String function_code = json.get("function_code").toString();
                String column_code = "";
                if (json.containsKey("column_code") && !json.get("column_code").equals("")){
                    column_code = json.get("column_code").toString();
                }
                Privilege privilege = new Privilege();
                privilege.setAction_code(action_code);
                privilege.setFunction_code(function_code);
                privilege.setMaster_code(master_code);

                privilege.setEnable(Common.IS_ACTIVE_Y);
                privilege.setModified_date(Common.DATETIME_FORMAT.format(now));
                privilege.setModifier(user_id);
                privilege.setCreated_date(Common.DATETIME_FORMAT.format(now));
                privilege.setCreater(user_id);
                privilege.setIsactive(Common.IS_ACTIVE_Y);
                privilegeMapper.insert(privilege);
            }
            return Common.DATABEAN_CODE_SUCCESS;
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }



    //============================================

    /**
     * 列出登录用户的所有权限
     */
    public JSONArray selectLoginPrivilege(String corp_code, String role_code, String user_code, String group_code, String search_value) throws Exception{
        user_code = corp_code +"U"+user_code;
        group_code = corp_code +"G"+group_code;
        List<Privilege> privilege_act = privilegeMapper.selectPrivilegeAct(user_code, group_code, role_code);
        List<Privilege> privilege_col = privilegeMapper.selectPrivilegeCol(user_code, group_code, role_code);
        List<Privilege> privilege_func = privilegeMapper.selectPrivilegeFunc(user_code, group_code, role_code, search_value);

        JSONArray privilege_array = new JSONArray();
        for (int i = 0; i < privilege_func.size(); i++) {
            String function_code = privilege_func.get(i).getFunction_code();
            String function_name = privilege_func.get(i).getFunction_name();
            JSONObject obj = new JSONObject();
            obj.put("function_code",function_code);
            obj.put("function_name",function_name);
            JSONArray action_array = new JSONArray();
            JSONArray column_array = new JSONArray();
            for (int j = 0; j < privilege_act.size(); j++) {
                String function_code1 = privilege_act.get(j).getFunction_code();
                if (function_code.equals(function_code1)){
                    int id = privilege_act.get(j).getId();
                    String action_code = privilege_act.get(j).getAction_code();
                    String action_name = privilege_act.get(j).getAction_name();
                    JSONObject actions_obj = new JSONObject();
                    actions_obj.put("action_id",id);
                    actions_obj.put("action_code",action_code);
                    actions_obj.put("action_name",action_name);
                    actions_obj.put("is_live","N");
                    actions_obj.put("is_die","N");
                    action_array.add(actions_obj);
                }
            }
            for (int j = 0; j < privilege_col.size(); j++) {
                String function_code1 = privilege_col.get(j).getFunction_code();
                if (function_code.equals(function_code1)){
                    int id = privilege_col.get(j).getId();
                    String column_name = privilege_col.get(j).getColumn_name();
                    String show_name = privilege_col.get(j).getShow_name();
                    JSONObject column_obj = new JSONObject();
                    column_obj.put("column_id",id);
                    column_obj.put("column_name",column_name);
                    column_obj.put("show_name",show_name);
                    column_obj.put("is_live","N");
                    column_obj.put("is_die","N");
                    column_array.add(column_obj);
                }
            }
            obj.put("actions",action_array);
            obj.put("columns",column_array);
            privilege_array.add(obj);
        }
        return privilege_array;
    }

    /**
     * 列出所选角色的权限
     * 返回action id
     */
    public JSONArray selectPrivilegeStatus(String user_code,String group_code,String role_code,String live_status,String die_status,JSONArray privilege_array) throws Exception{
        List<Privilege> act_info = privilegeMapper.selectPrivilegeAct(user_code,group_code,role_code);
        List<Privilege> col_info = privilegeMapper.selectPrivilegeCol(user_code,group_code,role_code);

        for (int i = 0; i < privilege_array.size(); i++) {
            JSONObject func = privilege_array.getJSONObject(i);
            String function_code = func.getString("function_code");
            JSONArray actions = func.getJSONArray("actions");
            JSONArray columns = func.getJSONArray("columns");
            for (int j = 0; j < act_info.size(); j++) {
                String function_code1 = act_info.get(j).getFunction_code();
                String action_code1 = act_info.get(j).getAction_code();
                if (function_code1.equals(function_code)){
                    for (int k = 0; k < actions.size(); k++) {
                        JSONObject act_obj = actions.getJSONObject(k);
                        if (action_code1.equals(act_obj.getString("action_code"))){
                            act_obj.put("is_die",die_status);
                            act_obj.put("is_live",live_status);
                        }
                    }
                }else {
                    continue;
                }
            }
            for (int j = 0; j < col_info.size(); j++) {
                String function_code1 = col_info.get(j).getFunction_code();
                String column_name1 = col_info.get(j).getColumn_name();
                if (function_code1.equals(function_code)){
                    for (int k = 0; k < columns.size(); k++) {
                        JSONObject col_obj = columns.getJSONObject(k);
                        if (column_name1.equals(col_obj.getString("column_name"))){
                            col_obj.put("is_die",die_status);
                            col_obj.put("is_live",live_status);
                        }
                    }
                }else {
                    continue;
                }
            }
        }
        return privilege_array;
    }

    /**
     * 更新（用户/群组/角色）的权限
     *
     */
    @Transactional
    public String updateACPrivilege(String master_code, String user_code, String del_act_id, JSONArray add_act,String del_col_id,JSONArray add_col) throws Exception{
        Date now = new Date();
        //先删除
        System.out.println("-------begin delete group---------");
        if (!del_act_id.equals("")){
            String[] del_act = del_act_id.split(",");
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("ids",del_act);
            privilegeMapper.deleteActPrivileges(map);
        }
        if (!del_col_id.equals("")){
            String[] del_col = del_col_id.split(",");
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("ids",del_col);
            privilegeMapper.deleteColPrivileges(map);
        }
        //再插入画面选择的权限
        for (int i = 0; i < add_act.size(); i++) {
            String info = add_act.get(i).toString();
            JSONObject json = JSONObject.parseObject(info);
            String action_code = json.get("action_code").toString();
            String function_code = json.get("function_code").toString();

            Privilege privilege = new Privilege();
            privilege.setAction_code(action_code);
            privilege.setFunction_code(function_code);
            privilege.setMaster_code(master_code);
            privilege.setEnable(Common.IS_ACTIVE_Y);
            privilege.setModified_date(Common.DATETIME_FORMAT.format(now));
            privilege.setModifier(user_code);
            privilege.setCreated_date(Common.DATETIME_FORMAT.format(now));
            privilege.setCreater(user_code);
            privilege.setIsactive(Common.IS_ACTIVE_Y);
            privilegeMapper.insert(privilege);
        }
        for (int i = 0; i < add_col.size(); i++) {
            String info = add_col.get(i).toString();
            JSONObject json = JSONObject.parseObject(info);
            String column_name = json.get("column_name").toString();
            String function_code = json.get("function_code").toString();

            TablePrivilege privilege = new TablePrivilege();
            privilege.setColumn_name(column_name);
            privilege.setFunction_code(function_code);
            privilege.setMaster_code(master_code);
            privilege.setEnable(Common.IS_ACTIVE_Y);
            privilege.setModified_date(Common.DATETIME_FORMAT.format(now));
            privilege.setModifier(user_code);
            privilege.setCreated_date(Common.DATETIME_FORMAT.format(now));
            privilege.setCreater(user_code);
            privilege.setIsactive(Common.IS_ACTIVE_Y);
            privilegeMapper.insertColPrivilege(privilege);
        }
        return Common.DATABEAN_CODE_SUCCESS;
    }
}
