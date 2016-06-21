package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.AppversionMapper;
import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.service.AppversionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by yin on 2016/6/21.
 */
@Service
public class AppversionServiceImpl implements AppversionService{
    @Autowired
    private AppversionMapper appversionMapper;
    @Override
    public List<Appversion> selectAllAppversion() throws SQLException {
        return appversionMapper.selectAllAppversion("");
    }

    @Override
    public PageInfo<Appversion> selectAllAppversion(int page_number, int page_size, String search_value) throws SQLException {
        PageHelper.startPage(page_number, page_size);
        List<Appversion> appversions = appversionMapper.selectAllAppversion(search_value);
        PageInfo<Appversion> page = new PageInfo<Appversion>(appversions);
        return page;
    }

    @Override
    public Appversion selAppversionById(int id) throws SQLException {
        return appversionMapper.selAppversionById(id);
    }

    @Override
    public int delAppversionById(int id) throws SQLException {
        return appversionMapper.delAppversionById(id);
    }

    @Override
    public int updAppversionById(Appversion appversion) throws SQLException {
        return appversionMapper.updAppversionById(appversion);
    }

    @Override
    public int addAppversion(Appversion appversion) throws SQLException {
        return appversionMapper.addAppversion(appversion);
    }
}
