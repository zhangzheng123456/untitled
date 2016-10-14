package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.entity.TablePrivilege;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by yin on 2016/6/30.
 */
public interface TableManagerMapper {
    List<TableManager> selAllByCode(@Param("function_code")String function_code) throws SQLException;

    List<TableManager> selByCode(@Param("function_code")String function_code) throws SQLException;

    int updateTable(@Param("table_code")String table_code,@Param("id")String id);

    List<TableManager> selTableList();

    int insert(TablePrivilege tablePrivilege);
}
