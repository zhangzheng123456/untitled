package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Function;
import com.bizvane.ishop.entity.Privilege;
import com.bizvane.ishop.entity.TablePrivilege;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface PrivilegeMapper {

    int insert(Privilege record) throws SQLException;

    int update(Privilege record) throws SQLException;

    int delete(@Param("master_code") String master_code) throws SQLException;

    int deleteActPrivileges(Map<String,Object> map)throws SQLException;
    //按功能获取user动作权限
    List<Privilege> selectActionByFun(@Param("user_code") String user_code, @Param("group_code") String group_code, @Param("role_code") String role_code, @Param("function_code") String function_code) throws SQLException;

    List<Privilege> selectColumnByFun(@Param("user_code") String user_code, @Param("group_code") String group_code, @Param("role_code") String role_code, @Param("function_code") String function_code) throws SQLException;

    List<Privilege> selectPrivilegeAct(@Param("user_code") String user_code, @Param("group_code") String group_code, @Param("role_code") String role_code)  throws SQLException;

    List<Privilege> selectPrivilegeCol(@Param("user_code") String user_code, @Param("group_code") String group_code, @Param("role_code") String role_code)  throws SQLException;

    List<Function> selectPrivilegeFunc(@Param("user_code") String user_code, @Param("group_code") String group_code, @Param("role_code") String role_code, @Param("search_value") String search_value)  throws SQLException;

    int insertColPrivilege(TablePrivilege tablePrivilege) throws SQLException;

    int deleteColPrivileges(Map<String,Object> map)throws SQLException;

    int deleteUserColPrivileges(@Param("function_code") String function_code, @Param("master_code") String master_code)throws SQLException;

    List<Privilege> selectColPrivilegeByUser(@Param("function_code") String function_code, @Param("master_code") String master_code)  throws SQLException;


}