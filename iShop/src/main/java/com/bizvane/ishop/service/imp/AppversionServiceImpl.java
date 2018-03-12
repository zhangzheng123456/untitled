package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.AppversionMapper;
import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.entity.Feedback;
import com.bizvane.ishop.service.AppversionService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/21.
 */
@Service
public class AppversionServiceImpl implements AppversionService {
    @Autowired
    private AppversionMapper appversionMapper;

    @Override
    public List<Appversion> selectAllAppversion() throws SQLException {
        return appversionMapper.selectAllAppversion("");
    }

    @Override
    public PageInfo<Appversion> selectAllAppversion(int page_number, int page_size, String search_value) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<Appversion> appversions = appversionMapper.selectAllAppversion(search_value);
        for (Appversion appversion : appversions) {
            appversion.setIsactive(CheckUtils.CheckIsactive(appversion.getIsactive()));
        }
        PageInfo<Appversion> page = new PageInfo<Appversion>(appversions);
        return page;
    }

    @Override
    public PageInfo<Appversion> selectAllAppversion1(int page_number, int page_size, String search_value) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<Appversion> appversions = appversionMapper.selectAllAppversionByPlatform(search_value);

        for (Appversion appversion : appversions) {
            appversion.setIsactive(CheckUtils.CheckIsactive(appversion.getIsactive()));
        }
        PageInfo<Appversion> page = new PageInfo<Appversion>(appversions);
        return page;
    }

    @Override
    public PageInfo<Appversion> selectAllScreen(int page_number, int page_size, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<Appversion> list = appversionMapper.selectAllScreen(params);
        for (Appversion appversion : list) {
            appversion.setIsactive(CheckUtils.CheckIsactive(appversion.getIsactive()));
        }
        PageInfo<Appversion> page = new PageInfo<Appversion>(list);
        return page;
    }

    @Override
    public Appversion selAppversionById(int id) throws Exception {
        return appversionMapper.selAppversionById(id);
    }

    @Override
    public int delAppversionById(int id) throws Exception {
        return appversionMapper.delAppversionById(id);
    }

    @Override
    public int updAppversionById(Appversion appversion) throws Exception {
        return appversionMapper.updAppversionById(appversion);
    }

    @Override
    public int addAppversion(Appversion appversion) throws Exception {
        return appversionMapper.addAppversion(appversion);
    }

    @Override
    public Appversion selAppversionForId(String corp_code, String version_id,String paltform) throws Exception {
        return appversionMapper.selAppversionForId(corp_code, version_id,paltform);
    }

    @Override
    public List<Appversion> selLatestVersion() throws Exception {
        return appversionMapper.selLatestVersion();
    }
}
