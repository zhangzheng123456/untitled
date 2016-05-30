package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Role;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface RoleMapper {

    List<Role> selectAllRole(@Param("corp_code") String corp_code,@Param("search_value") String search_value);

    List<Role> selectUserRole(@Param("corp_code") String corp_code,@Param("role_code") String role_code);

    Role selectByRoleId(@Param("role_id") int role_id);

    String selectMaxRoleCode(String role_head);

    int insertRole(Role record);

    int updateByRoleId(Role record);

    int deleteByRoleId(Integer id);

}