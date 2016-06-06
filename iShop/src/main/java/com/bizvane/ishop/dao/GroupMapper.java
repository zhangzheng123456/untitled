package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Group;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GroupMapper {

    Group selectByGroupId(@Param("group_id")int group_id);

    List<Group> selectAllGroup(@Param("corp_code")String corp_code, @Param("search_value")String search_value);

    Group selectCorpGroup(@Param("corp_code")String corp_code, @Param("group_code")String group_code);

    int insertGroup(Group record);

    int updateGroup(Group record);

    int deleteByGroupId(Integer id);

}