package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.entity.TablePrivilege;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yin on 2016/6/30.
 */
public interface TableManagerService {
    List<TableManager> selAllByCode(String function_code)throws Exception;

    List<TableManager> selByCode(String function_code)throws Exception;

    int updateTable(String table_code,String id);

    List<TableManager> selTableList();
    int insert(TablePrivilege tablePrivilege);
}
