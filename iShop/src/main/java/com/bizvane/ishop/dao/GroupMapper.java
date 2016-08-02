package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Group;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GroupMapper {

    Group selectByGroupId(@Param("group_id")int group_id);

    List<Group> selectAllGroup(@Param("corp_code")String corp_code, @Param("role_code")String role_code, @Param("search_value")String search_value);

    List<Group> selectAllGroupScreen(Map<String,Object> map);


    Group selectByCode(@Param("corp_code")String corp_code,@Param("group_code")String group_code,@Param("isactive")String isactive);

    List<Group> selectUserGroup(@Param("corp_code")String corp_code,@Param("role_code") String role_code);

    List<Group> selectByRole(@Param("role_code")String role_code);

    String selectMaxCode();

    int insertGroup(Group record);

    int updateGroup(Group record);

    int deleteByGroupId(Integer id);

    Group selectByName(String corp_code, String group_name);


}