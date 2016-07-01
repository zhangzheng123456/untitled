package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Group;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ZhouZhou on 2016/6/6.
 */
public interface GroupService {

    Group getGroupById(int id) throws SQLException;

    List<Group> selectUserGroup(String corp_code, String role_code) throws SQLException;

    List<Group> selectByRole(String role_code) throws SQLException;

    Group selectByCode(String corp_code, String group_code, String isactive) throws SQLException;

    Group selectByName(String corp_code, String group_name, String isactive) throws SQLException;

    PageInfo<Group> getGroupAll(int page_number, int page_size, String corp_code, String role_code, String search_value) throws SQLException;

    String selectMaxCode();

    String insertGroup(Group group) throws SQLException;

    String updateGroup(Group group) throws SQLException;

    int deleteGroup(int id) throws SQLException;

}
