package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.AppLoginLogMapper;
import com.bizvane.ishop.entity.AppLoginLog;
import com.bizvane.ishop.service.AppLoginLogService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/8/24.
 */
@Service
public class AppLoginLogServiceImpl implements AppLoginLogService {
    @Autowired
    private AppLoginLogMapper loginLogMapper;
    @Override
    public PageInfo<AppLoginLog> selectAllAppLoginLog(int page_number, int page_size,String corp_code, String search_value) {
        PageHelper.startPage(page_number, page_size);
        List<AppLoginLog> appLoginLogs = loginLogMapper.selectAllAppLoginLog(corp_code, search_value);
        for (AppLoginLog appLoginLog:appLoginLogs) {
            appLoginLog.setIsactive(CheckUtils.CheckIsactive(appLoginLog.getIsactive()));
        }
        PageInfo<AppLoginLog> page = new PageInfo<AppLoginLog>(appLoginLogs);
        return page;
    }

    @Override
    public PageInfo<AppLoginLog> selectAllScreen(int page_number, int page_size, String corp_code,Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<AppLoginLog> list = loginLogMapper.selectAllScreen(params);
        for (AppLoginLog appLoginLog:list) {
            appLoginLog.setIsactive(CheckUtils.CheckIsactive(appLoginLog.getIsactive()));
        }
        PageInfo<AppLoginLog> page = new PageInfo<AppLoginLog>(list);
        return page;
    }

    @Override
    public int delAppLoginlogById(int id) {
        return loginLogMapper.delAppLoginlogById(id);
    }
}
