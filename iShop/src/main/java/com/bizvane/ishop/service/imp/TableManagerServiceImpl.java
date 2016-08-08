package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.TableManagerMapper;
import com.bizvane.ishop.entity.TableManager;
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
        List<TableManager> tableManagers = managerMapper.selAllByCode(function_code);
        for (TableManager manager: tableManagers) {
            System.out.println(manager.getColumn_name()+"---"+manager.getShow_name());
        }
        return managerMapper.selAllByCode(function_code);
    }

    /**
     * 获取页面可筛选的列
     */
    @Override
    public List<TableManager> selByCode(String function_code) throws Exception{
        return managerMapper.selByCode(function_code);
    }
}
