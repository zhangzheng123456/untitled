package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.WxTemplate;
import org.apache.ibatis.annotations.Param;
import java.sql.SQLException;
import java.util.List;

public interface WxTemplateMapper {

   WxTemplate selectById(int id) throws SQLException;

    List<WxTemplate> selectAllWxTemplate(@Param("corp_code") String corp_code,@Param("search_value") String search_value) throws SQLException;

    int insertWxTemplate(WxTemplate record) throws SQLException;

    int updateWxTemplate(WxTemplate record) throws SQLException;

    int deleteWxTemplate(@Param("id") int id) throws SQLException;

}