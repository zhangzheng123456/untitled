package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Area;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.Store;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/6/4.
 */
public interface AreaService {
    Area getAreaById(int id) throws Exception;

    Area getAreaByCode(String corp_code, String area_code, String isactive) throws Exception;

    PageInfo<Area> getAllAreaByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    List<Area> getAllArea(String corp_code) throws Exception;

    List<Store> getAreaStore(String corp_code, String area_code) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id) throws Exception;

    Area getAreaByName(String corp_code, String area_name, String isactive) throws Exception;

    List<Area> getAreaByCorp(String corp_code) throws Exception;

    Area selAreaByCorp(String corp_code, String area_code, String isactive) throws Exception;

    String insertExecl(Area area) throws Exception;

    PageInfo<Area> getAllAreaScreen(int page_number, int page_size, String corp_code, String area_codes, Map<String, String> map) throws Exception;

    PageInfo<Area> selectByAreaCode(int page_number, int page_size, String corp_code, String area_codes, String search_value) throws Exception;
}
