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

    int insertRole(Role record) throws SQLException;

    int updateByRoleId(Role record) throws SQLException;

    int deleteByRoleId(int id) throws SQLException;

    PageInfo<Role> selectAllRole(int page_number, int page_size,String corp_code, String search_value) throws SQLException;

    List<Role> selectCorpRole(String corp_code,String role_head) throws SQLException;

    String selectMaxRoleCode(String role_head);

}
