package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.Function;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.entity.TableManager;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public interface FunctionService {

    JSONArray selectAllFunctions(String corp_code,String user_code,String group_code, String role_code)throws Exception;

    JSONArray selectActionByFun(String corp_code,String user_code,String group_code, String role_code,String function_code)throws Exception;

    List<TableManager> selectColumnByFun(String corp_code, String user_code, String group_code, String role_code, String function_code) throws Exception;

    List<Function> selectAllPrivilege(String corp_code,String role_code, String user_code, String group_code,String search_value)throws Exception;

    JSONArray selectRAGPrivilege(String role_code,String group_code)throws Exception;

    JSONArray selectRolePrivilege(String role_code)throws Exception;

    JSONArray selectGroupPrivilege(String corp_code,String group_code)throws Exception;

    JSONArray selectUserPrivilege(String corp_code,String user_code)throws Exception;

    String updatePrivilege(String master_code,String user_id,JSONArray array)throws Exception;

}
