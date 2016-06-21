package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Area;
import com.bizvane.ishop.entity.Store;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ZhouZhou on 2016/6/4.
 */
public interface AreaService {
    Area getAreaById(int id) throws SQLException;

    Area getAreaByCode(String corp_code, String area_code) throws SQLException;

    PageInfo<Area> getAllAreaByPage(int page_number, int page_size, String corp_code, String search_value) throws SQLException;

    List<Area> getAllArea(String corp_code) throws SQLException;

    List<Store> getAreaStore(String corp_code, String area_code) throws SQLException;

    int insert(Area area) throws SQLException;

    int update(Area area) throws SQLException;

    int delete(int id) throws SQLException;

    Area getAreaByName(String corp_code, String area_name);

    List<Area> getAreaByCorp(String corp_code);
}
