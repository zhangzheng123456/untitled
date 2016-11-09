package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

public interface RoleMapper {

    List<Role> selectAllRole(@Param("search_value") String search_value) throws SQLException;

    List<Role> selectUserRole(@Param("role_code") String role_code) throws SQLException;

    Role selectByRoleId(@Param("role_id") int role_id) throws SQLException;

    int insertRole(Role record) throws SQLException;

    int updateByRoleId(Role record) throws SQLException;

    int deleteByRoleId(Integer id) throws SQLException;

    int countRoleCode(@Param("role_code") String role_code) throws SQLException;
    Role getRoleForID(@Param("role_code") String role_code) throws SQLException;
    int countRoleName(@Param("role_name") String role_name) throws SQLException;
}