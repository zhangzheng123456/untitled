package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.AreaMapper;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.entity.Area;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.AreaService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Area> getAllArea(String corp_code) throws SQLException {
        List<Area> areas;
        areas = areaMapper.selectAreas(corp_code);
        return areas;
    }

    //获得区域下店铺
    @Override
    public List<Store> getAreaStore(String corp_code, String area_code) throws SQLException {
        return storeMapper.selectStoreBrandArea(corp_code, "", "%" + area_code + "%");
    }

    @Override
    @Transactional
    public String insert(String message, String user_id) throws SQLException {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        String area_code = jsonObject.get("area_code").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String area_name = jsonObject.get("area_name").toString();
        Area area = getAreaByCode(corp_code, area_code);
        Area area1 = getAreaByName(corp_code, area_name);
        if (area == null && area1 == null) {
            area = new Area();
            Date now = new Date();
            area.setArea_code(area_code);
            area.setArea_name(area_name);
            area.setCorp_code(corp_code);
            area.setCreated_date(Common.DATETIME_FORMAT.format(now));
            area.setCreater(user_id);
            area.setModified_date(Common.DATETIME_FORMAT.format(now));
            area.setModifier(user_id);
            area.setIsactive(jsonObject.get("isactive").toString());
            areaMapper.insertArea(area);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else if (area != null) {
            result = "区域编号已存在";
        } else {
            result = "区域名称已存在";
        }
        return result;
    }

    @Override
    @Transactional
    public String update(String message, String user_id) throws SQLException {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        int area_id = Integer.parseInt(jsonObject.get("id").toString());

        String area_code = jsonObject.get("area_code").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String area_name = jsonObject.get("area_name").toString();

        Area area = getAreaById(area_id);
        Area area1 = getAreaByCode(corp_code, area_code);
        Area area2 = getAreaByName(corp_code, area_code);

        if ((area.getArea_code().equals(area_code) || area1 == null)
                && (area.getArea_name().equals(area_name) || area2 == null)) {
            area = new Area();
            Date now = new Date();
            area.setId(area_id);
            area.setArea_code(area_code);
            area.setArea_name(area_name);
            area.setCorp_code(corp_code);
            area.setModified_date(Common.DATETIME_FORMAT.format(now));
            area.setModifier(user_id);
            area.setIsactive(jsonObject.get("isactive").toString());
            areaMapper.updateArea(area);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else if (!area.getArea_code().equals(area_code) && area1 != null) {
            result = "区域编号已存在";
        } else {
            result = "区域名称已存在";
        }
        return result;
    }

    @Override
    @Transactional
    public int delete(int id) throws SQLException {
        return areaMapper.deleteByAreaId(id);
    }

    @Override
    public Area getAreaByName(String corp_code, String area_name) {
        Area area = this.areaMapper.selectArea_Name(corp_code, area_name);
        return area;
    }

    @Override
    public List<Area> getAreaByCorp(String corp_code) {
        List<Area> list = this.areaMapper.getAreaByCorp(corp_code);
        return list;
    }

    @Override
    public Area selAreaByCorp(String corp_code, String area_code, String isactive) {
        return areaMapper.selAreaByCorp(corp_code, area_code, isactive);
    }

    @Override
    public String insertExecl(Area area) {
        areaMapper.insertArea(area);
        return "add success";
    }

    @Override
    public PageInfo<Area> getAllAreaScreen(int page_number, int page_size, String corp_code, Map<String, String> map) {
        List<Area> areas;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        areas = areaMapper.selectAllAreaScreen(params);
        PageInfo<Area> page = new PageInfo<Area>();
        return page;
    }
}
