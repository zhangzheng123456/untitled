package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Role;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface RoleService {

    Role selectByRoleId(int role_id) throws Exception;

    String insertRole(Role record) throws Exception;

    String updateByRoleId(Role record) throws Exception;

    int deleteByRoleId(int id, String role_code) throws Exception;

    PageInfo<Role> selectAllRole(int page_number, int page_size, String search_value) throws Exception;

    List<Role> selectAll(String search_value) throws Exception;

    List<Role> selectCorpRole(String role_code) throws Exception;

    Role getRoleForID(String role_code) throws Exception;

    String roleCodeExist(String role_code) throws Exception;

    String roleNameExist(String role_name) throws Exception;
}
