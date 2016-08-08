package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VIPInfo;
import com.bizvane.ishop.entity.VipLabel;
import com.bizvane.ishop.entity.VipLabel;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by lixiang on 2016/6/12.
 *
 * @@version
 */
public interface VipLabelMapper {

    int deleteByPrimaryKey(Integer id) throws SQLException;

    int insert(VipLabel record) throws SQLException;

    VipLabel selectByPrimaryKey(Integer id) throws SQLException;

    int updateByPrimaryKey(VipLabel record) throws SQLException;

    List<VipLabel> selectAllVipLabel(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    List<VipLabel> selectAllViplabelScreen(Map<String, Object> params) throws SQLException;

    VipLabel selectVipLabelName(@Param("corp_code") String corp_code, @Param("label_name") String tag_name) throws SQLException;

    // VipLabel selectTypeCodeByName(@Param("corp_code") String corp_code, @Param("type_name") String type_name);
}