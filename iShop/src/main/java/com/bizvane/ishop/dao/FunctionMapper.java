package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Action;
import com.bizvane.ishop.entity.Function;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public interface FunctionMapper {

    //获取user所有功能模块
    List<Function> selectAllFun(@Param("user_code") String user_code, @Param("group_code") String group_code, @Param("role_code") String role_code);
    //按功能获取user动作权限
    List<Action> selectActionByFun(@Param("user_code") String user_code, @Param("group_code") String group_code, @Param("role_code") String role_code, @Param("function_code") String function_code);

    List<Function> selectRolePrivilege(@Param("role_code") String role_code);

    List<Function> selectGroupPrivilege(@Param("group_code") String group_code);

    List<Function> selectUserPrivilege(@Param("user_code") String user_code);

    List<Function> selectPrivilege(@Param("user_code") String user_code, @Param("role_code") String role_code, @Param("group_code") String group_code);

    int insertFunction(Function function);
}
