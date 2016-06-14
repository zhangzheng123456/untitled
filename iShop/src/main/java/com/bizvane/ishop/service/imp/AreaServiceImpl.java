package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.AreaMapper;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.entity.Area;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.AreaService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ZhouZhou on 2016/6/4.
 */

@Service
public class AreaServiceImpl implements AreaService {
    @Autowired
    AreaMapper areaMapper;
    @Autowired
    StoreMapper storeMapper;

    /**
     * 根据区域id
     * 获取某区域信息
     */
    @Override
    public Area getAreaById(int id) throws SQLException {
        return areaMapper.selectByAreaId(id);
    }

    /**
     * 根据区域编号
     * 获取区域信息
     */
    @Override
    public Area getAreaByCode(String corp_code, String area_code) throws SQLException {
        return areaMapper.selectCorpArea(corp_code, area_code);
    }

    /**
     * 分页显示区域
     */
    @Override
    public PageInfo<Area> getAllAreaByPage(int page_number, int page_size, String corp_code, String search_value) throws SQLException {
        List<Area> areas;
        PageHelper.startPage(page_number, page_size);
        areas = areaMapper.selectAllArea(corp_code, search_value);
        PageInfo<Area> page = new PageInfo<Area>(areas);

        return page;
    }

    @Override
    public List<Area> getAllArea(String corp_code, String search_value) throws SQLException {
        List<Area> areas;
        areas = areaMapper.selectAllArea(corp_code, search_value);
        return areas;
    }

    //获得区域下店铺
    @Override
    public List<Store> getAreaStore(String corp_code, String area_code) throws SQLException {
        return storeMapper.selectStoreBrandArea(corp_code, "", "%" + area_code + "%");
    }

    @Override
    public int insert(Area area) throws SQLException {
        return areaMapper.insertArea(area);
    }

    @Override
    public int update(Area area) throws SQLException {
        return areaMapper.updateArea(area);
    }

    @Override
    public int delete(int id) throws SQLException {
        return areaMapper.deleteByAreaId(id);
    }

}
