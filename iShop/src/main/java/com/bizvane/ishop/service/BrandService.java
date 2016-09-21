package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.Store;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/6/4.
 */
public interface BrandService {
    Brand getBrandById(int id) throws Exception;

    Brand getBrandByCode(String corp_code, String brand_code) throws Exception;

    PageInfo<Brand> getAllBrandByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    List<Brand> getAllBrand(String corp_code) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id) throws Exception;

    Brand getBrandByName(String corp_code, String brand_name) throws Exception;

    String insertExecl(Brand brand) throws Exception;

    int getGoodsCount(String corp_code, String brand_code) throws Exception;

    PageInfo<Brand> getAllBrandScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception;
}
