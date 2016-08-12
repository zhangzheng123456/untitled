package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.ParamConfigure;
import org.apache.ibatis.annotations.Param;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
/**
 * Created by yan on 2016/8/10.
 */
public interface ParamConfigureMapper {
    ParamConfigure selectById(int id) throws SQLException;
    List<ParamConfigure> selectAllParam( @Param("search_value") String search_value) throws SQLException;

    List<ParamConfigure> selectParams(@Param("search_value")String  search_value) throws SQLException;
    int insertParam(ParamConfigure paramConfigure) throws SQLException;

    int updateParam(ParamConfigure paramConfigure) throws SQLException;

    int deleteByParamId(int id) throws SQLException;
    ParamConfigure selectParamByKey(@Param("param_name") String param_name) throws SQLException;
    ParamConfigure selectParamByName(@Param("param_desc") String param_desc) throws SQLException;

    List<ParamConfigure> selectByParamSearch(Map<String, Object> params) throws SQLException;

}
