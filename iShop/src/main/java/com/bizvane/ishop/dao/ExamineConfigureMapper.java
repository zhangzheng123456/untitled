package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.ExamineConfigure;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public interface ExamineConfigureMapper {

    ExamineConfigure  selectById(int id) throws SQLException;

    List<ExamineConfigure> selectAll(@Param("corp_code") String corp_code,@Param("search_value") String search_value) throws SQLException;

    List<ExamineConfigure> selectAllScreen(HashMap<String,Object> map) throws SQLException;

    int  deleteById(@Param("id") int id) throws SQLException;

    ExamineConfigure selectByName(@Param("corp_code") String corp_code,@Param("function_bill_name") String function_bill_name) throws SQLException;

    int insertExamine(ExamineConfigure examineConfigure) throws SQLException;

    int updateExamine(ExamineConfigure examineConfigure) throws SQLException;

}
