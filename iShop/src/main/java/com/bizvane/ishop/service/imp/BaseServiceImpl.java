package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.BaseMapper;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.BaseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/7/14.
 */
@Service
public class BaseServiceImpl implements BaseService{
    @Autowired
    private BaseMapper baseMapper;
    @Override
    public PageInfo<HashMap<String, Object>> queryMetaList(int page_number, int page_size, Map<String, Object> params) throws SQLException {
       List<HashMap<String, Object>> list;
        PageHelper.startPage(page_number, page_size);
        list=baseMapper.queryMetaList(params);
        PageInfo<HashMap<String, Object>> page = new PageInfo<HashMap<String, Object>>(list);
        return page;
    }



}
