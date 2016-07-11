package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Role;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface RoleService {

    Role selectByRoleId(int role_id) throws SQLException;

    String insertRole(Role record) throws SQLException;

    String updateByRoleId(Role record) throws SQLException;

    int deleteByRoleId(int id) throws SQLException;

    PageInfo<Role> selectAllRole(int page_number, int page_size, String search_value) throws SQLException;

    List<Role> selectAll(String search_value) throws SQLException;

    List<Role> selectCorpRole(String role_code) throws SQLException;

    String roleCodeExist(String role_code) throws SQLException;

    String roleNameExist(String role_name) throws SQLException;
}
