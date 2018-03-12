package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Area;
import com.bizvane.ishop.entity.CorpParam;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CorpParamMapper {
    CorpParam selectById(int id) throws SQLException;

    List<CorpParam> selectByParamId(@Param("param_id") String param_id) throws SQLException;

    List<CorpParam> selectAllParam(@Param("search_value") String search_value) throws SQLException;

    List<CorpParam> selectByCorpParam(@Param("corp_code") String corp_code,@Param("param_id") String param_id,@Param("isactive") String isactive) throws SQLException;

    int insert(CorpParam record) throws SQLException;

    int update(CorpParam record) throws SQLException;

    int deleteById(int id) throws SQLException;

    List<CorpParam> selectAllParamScreen(Map<String, Object> params) throws SQLException;

    CorpParam selectByParamName(@Param("param_name") String param_name,@Param("isactive") String isactive)throws Exception;

    List<CorpParam> selectParamByName(@Param("corp_code") String corp_code,@Param("param_name") String param_name) throws SQLException;

}