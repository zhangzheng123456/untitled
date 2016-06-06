package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Group;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by ZhouZhou on 2016/6/6.
 */
public interface GroupService {

    Group getGroupById(int id) throws SQLException;

    PageInfo<Group> getGroupAll(int page_number, int page_size, String corp_code, String search_value) throws SQLException;

    int insertGroup(Group group) throws SQLException;

    int updateGroup(Group group) throws SQLException;

    int deleteGroup(int id) throws SQLException;

}
