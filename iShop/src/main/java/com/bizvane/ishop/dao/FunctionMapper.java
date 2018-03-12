package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Action;
import com.bizvane.ishop.entity.Function;
import com.bizvane.ishop.entity.Privilege;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public interface FunctionMapper {

    //获取user所有功能模块
    List<Function> selectAllFun(@Param("user_code") String user_code, @Param("group_code") String group_code, @Param("role_code") String role_code) throws SQLException;
    //按功能获取user动作权限
    List<Privilege> selectActionByFun(@Param("user_code") String user_code, @Param("group_code") String group_code, @Param("role_code") String role_code, @Param("function_code") String function_code) throws SQLException;

    List<Function> selectRolePrivilege(@Param("role_code") String role_code) throws SQLException;

    List<Function> selectGroupPrivilege(@Param("group_code") String group_code) throws SQLException;

    List<Function> selectUserPrivilege(@Param("user_code") String user_code) throws SQLException;

    List<Function> selectPrivilege(@Param("user_code") String user_code, @Param("role_code") String role_code, @Param("group_code") String group_code,@Param("search_value") String search_value) throws SQLException;

    int insertFunction(Function function) throws SQLException;

    List<Privilege> selectPrivilegeByAct(@Param("search_value") String search_value,@Param("action_name") String action_name,@Param("function_code") String function_code) throws SQLException;

}
