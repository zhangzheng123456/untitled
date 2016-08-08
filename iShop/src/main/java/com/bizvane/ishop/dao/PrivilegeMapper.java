package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Privilege;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

public interface PrivilegeMapper {

    int insert(Privilege record) throws SQLException;

    int delete(@Param("master_code") String master_code) throws SQLException;

}