package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.CorpParam;
import com.bizvane.ishop.entity.VipParam;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/9/7.
 */
public interface VipParamMapper {
    VipParam selectById(int id) throws SQLException;

    List<VipParam> selectAllParam(@Param("corp_code")String corp_code,@Param("search_value") String search_value,@Param("isactive") String isactive) throws SQLException;

    List<VipParam> checkParamName(@Param("corp_code") String corp_code,@Param("param_name") String param_name,@Param("isactive") String isactive) throws SQLException;

    List<VipParam> checkParamDesc(@Param("corp_code") String corp_code,@Param("param_desc") String param_desc,@Param("isactive") String isactive) throws SQLException;

    int insert(VipParam record) throws SQLException;

    int update(VipParam record) throws SQLException;

    int deleteById(int id) throws SQLException;

    List<VipParam> selectAllParamScreen(Map<String, Object> params) throws SQLException;

    List<VipParam> selectParamByCorp(@Param("corp_code")String corp_code) throws SQLException;

    String selectMaxOrderByCorp(@Param("corp_code")String corp_code) throws SQLException;
}
