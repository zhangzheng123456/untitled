package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.ErrorLogMapper;
import com.bizvane.ishop.entity.ErrorLog;
import com.bizvane.ishop.service.ErrorLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/8/24.
 */
@Service
public class ErrorLogServiceImpl implements ErrorLogService{
    @Autowired
    ErrorLogMapper errorLogMapper;

    public ErrorLog getLogById(int id) throws Exception {
        return errorLogMapper.selectByLogId(id);
    }

    /**
     * 分页显示错误日志
     * @param page_number
     * @param page_size
     * @param search_value
     * @return
     * @throws Exception
     */
    public PageInfo<ErrorLog> getAllLog(int page_number, int page_size, String search_value) throws Exception{
        List<ErrorLog> logs;
        PageHelper.startPage(page_number, page_size);
        logs = errorLogMapper.selectAllLog(search_value);

        PageInfo<ErrorLog> page = new PageInfo<ErrorLog>(logs);
        return page;
    }

    /**
     * 删除错误日志
     * @param id
     * @return
     * @throws Exception
     */
    public int delete(int id) throws Exception{
        return  this.errorLogMapper.deleteByLogId(id);
    }

    public PageInfo<ErrorLog> selectAllLogScreen(int page_number, int page_size, Map<String, String> map) throws Exception {
        List<ErrorLog> logs;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        logs=errorLogMapper.selectAllLogScreen(params);
        PageInfo<ErrorLog> page = new PageInfo<ErrorLog>(logs);
        return page;
    }

}
