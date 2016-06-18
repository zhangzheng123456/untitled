package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.Function;
import com.bizvane.ishop.entity.Goods;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public interface FunctionService {
    JSONArray selectAllActions(int user_id,String role_code,String group_code);

    JSONArray selectAllFunctions(String user_code,String group_code, String role_code);

    JSONArray selectActionByFun(String user_code,String group_code, String role_code,String function_code);

    List<Function> selectAllPrivilege(String role_code, int user_id, String group_code);

    JSONArray selectRAGPrivilege(String role_code,String group_code);

    JSONArray selectRolePrivilege(String role_code);

    JSONArray selectGroupPrivilege(String group_code);

    JSONArray selectUserPrivilege(String user_id);

    String updatePrivilege(String master_code,String user_id,JSONArray array);

}
