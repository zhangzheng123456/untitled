package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.TableManagerMapper;
import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.entity.TablePrivilege;
import com.bizvane.ishop.service.TableManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yin on 2016/6/30.
 */
@Service
public class TableManagerServiceImpl implements TableManagerService {
    @Autowired
    private TableManagerMapper managerMapper;
    @Override
    public List<TableManager> selAllByCode(String function_code) throws Exception{
        return managerMapper.selAllByCode(function_code);
    }

    /**
     * 获取页面可筛选的列
     */
    @Override
    public List<TableManager> selByCode(String function_code) throws Exception{
        return managerMapper.selByCode(function_code);
    }


    @Override
    public int insert(TablePrivilege tablePrivilege) {
        return managerMapper.insert(tablePrivilege);
    }

    @Override
    public List<TableManager> selTableList() throws Exception {
        return managerMapper.selTableList();
    }

    @Override
    public int updateTable(String column_code, String id) {
        return managerMapper.updateTable(column_code,id);
    }

    @Override
    public List<TableManager> selVipScreenValue() throws Exception{
        return managerMapper.selVipScreenValue();
    }
}
