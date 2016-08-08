package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipRecordType;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/21.
 *
 * @@version
 */
public interface VipRecordTypeMapper {

    int deleteByPrimaryKey(Integer id) throws SQLException;

    int insert(VipRecordType VipRecordType) throws SQLException;

    VipRecordType selectByPrimaryKey(Integer id) throws SQLException;

    int updateByPrimaryKey(VipRecordType VipRecordType) throws SQLException;

    List<VipRecordType> selectAllVipRecordType(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    VipRecordType selectCode(@Param("corp_code") String corp_code, @Param("type_code") String type_code) throws SQLException;

    VipRecordType selectName(@Param("corp_code") String corp_code, @Param("type_name") String type_name) throws SQLException;
}
