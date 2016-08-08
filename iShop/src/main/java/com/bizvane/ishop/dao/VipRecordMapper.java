package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipRecord;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
public interface VipRecordMapper {
    VipRecord selecctById(int id) throws SQLException;

    int deleteByPrimary(Integer id) throws SQLException;

    int insert(VipRecord VipRecord) throws SQLException;

    int updateByPrimaryKey(VipRecord VipRecord) throws SQLException;

    List<VipRecord> selectAllVipRecordInfo(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    List<VipRecord> selectAllVipRecordScreen(Map<String,Object> map) throws SQLException;
}
