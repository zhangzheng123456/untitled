package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Group;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface GroupMapper {

    Group selectByGroupId(@Param("group_id")int group_id) throws SQLException;

    List<Group> selectAllGroup(@Param("corp_code")String corp_code, @Param("role_code")String role_code, @Param("search_value")String search_value) throws SQLException;

    List<Group> selectAllGroupScreen(Map<String,Object> map) throws SQLException;

    Group selectByCode(@Param("corp_code")String corp_code,@Param("group_code")String group_code,@Param("isactive")String isactive) throws SQLException;

    List<Group> selectUserGroup(@Param("corp_code")String corp_code,@Param("role_code") String role_code) throws SQLException;

    List<Group> selectByRole(@Param("role_code")String role_code) throws SQLException;

    String selectMaxCode() throws SQLException;

    int insertGroup(Group record) throws SQLException;

    int updateGroup(Group record) throws SQLException;

    int deleteByGroupId(Integer id) throws SQLException;

    Group selectByName(@Param("corp_code")String corp_code,@Param("group_name") String group_name,@Param("isactive")String isactive) throws SQLException;

    List<Group> selectByCorpRole(@Param("corp_code")String corp_code,@Param("role_code")String role_code) throws SQLException;

}