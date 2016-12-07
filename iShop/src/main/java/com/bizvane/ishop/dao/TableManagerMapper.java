package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.entity.TablePrivilege;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/30.
 */
public interface TableManagerMapper {
    List<TableManager> selAllByCode(@Param("function_code")String function_code) throws SQLException;

    List<TableManager> selByCode(@Param("function_code")String function_code) throws SQLException;

    int updateTable(@Param("column_code")String column_code,@Param("id")String id);

    List<TableManager> selTableList();

    int insert(TablePrivilege tablePrivilege);

    List<TableManager> selFuncColumns(Map<String, Object> params) throws SQLException;

    List<TableManager> selByFunc(@Param("function_code")String function_code) throws SQLException;

}
