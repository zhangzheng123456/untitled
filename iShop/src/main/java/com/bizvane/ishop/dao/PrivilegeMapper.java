package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Privilege;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PrivilegeMapper {

    int insert(Privilege record);

    List<Privilege> selectGroup(@Param("group_code") String group_code);

    int deleteGroup(@Param("group_code") String group_code);

}