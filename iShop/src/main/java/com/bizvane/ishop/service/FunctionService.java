package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.Function;
import com.bizvane.ishop.entity.Privilege;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/24.
 */
public interface FunctionService {

    JSONArray selectAllFunctions(String corp_code,String user_code,String group_code, String role_code)throws Exception;

    List<Map<String,String>> selectActionByFun(String corp_code, String user_code, String group_code, String role_code, String function_code)throws Exception;

    List<Function> selectAllPrivilege(String corp_code,String role_code, String user_code, String group_code,String search_value)throws Exception;

    JSONArray selectRAGPrivilege(String role_code,String group_code)throws Exception;

    JSONArray selectRolePrivilege(String role_code)throws Exception;

    JSONArray selectGroupPrivilege(String corp_code,String group_code)throws Exception;

    JSONArray selectUserPrivilege(String corp_code,String user_code)throws Exception;

    String updatePrivilege(String master_code,String user_id,JSONArray array)throws Exception;

    //============================

    List<Map<String,String>> selectColumnByFun(String corp_code, String user_code, String group_code, String role_code, String function_code) throws Exception;

    List<Map<String,String>> selectRWByFun(String corp_code, String user_code, String group_code, String role_code, String function_code) throws Exception;

    JSONArray selectLoginPrivilege(String corp_code, String role_code, String user_code, String group_code, String search_value) throws Exception;

    JSONArray selectPrivilegeStatus(String user_code,String group_code,String role_code,String live_status,String die_status,JSONArray privilege_array) throws Exception;

    String updateACPrivilege(String master_code, String user_code, String del_act_id, JSONArray add_act,String del_col_id,JSONArray add_col) throws Exception;

    List<Privilege> selectColPrivilegeByUser(String function_code,String master_code) throws Exception;

    int updateColPrivilegeByUser(String function_code,String chart_order,String corp_code,String user_code) throws Exception;

    List<Privilege> selectPrivilegeByAct(String search_value,String action_name,String function_code) throws Exception;

    List<Privilege> selectMasterCodeByFunctionName(String function_name,String action_name) throws Exception;
}
