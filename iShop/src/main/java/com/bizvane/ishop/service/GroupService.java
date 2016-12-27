package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Group;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/6/6.
 */
public interface GroupService {

    Group getGroupById(int id) throws Exception;

    List<Group> selectUserGroup(String corp_code, String role_code) throws Exception;

    List<Group> selectByRole(String role_code) throws Exception;

    Group selectByCode(String corp_code, String group_code, String isactive) throws Exception;

    Group selectByName(String corp_code, String group_name, String isactive) throws Exception;

    PageInfo<Group> getGroupAll(int page_number, int page_size, String corp_code, String role_code, String search_value) throws Exception;

    List<Group> getGroupAll(String corp_code, String role_code) throws SQLException;

    PageInfo<Group> getAllGroupScreen(int page_number, int page_size, String corp_code, String role_code, Map<String,String> map) throws Exception;
    String selectMaxCode() throws Exception;

    String insertGroup(Group group) throws Exception;

    String updateGroup(Group group) throws Exception;

    int deleteGroup(int id,String group_code,String corp_code) throws Exception;

    String selRoleByGroupCode(String corp_code,String group_code) throws Exception;

    List<Group> selectByCorpRole(String corp_code, String role_code) throws Exception;
}
