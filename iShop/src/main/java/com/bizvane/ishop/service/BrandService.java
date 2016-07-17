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
    Brand getBrandById(int id) throws SQLException;

    Brand getBrandByCode(String corp_code, String brand_code) throws SQLException;

    PageInfo<Brand> getAllBrandByPage(int page_number, int page_size, String corp_code, String search_value) throws SQLException;

    List<Brand> getAllBrand(String corp_code) throws SQLException;

    List<Store> getBrandStore(String corp_code, String brand_code) throws SQLException;

    String insert(String message, String user_id) throws SQLException;

    String update(String message, String user_id) throws SQLException;

    int delete(int id) throws SQLException;

    Brand getBrandByName(String corp_code, String brand_name) throws SQLException;

    String insertExecl(Brand brand) throws SQLException;

    int getGoodsCount(String corp_code, String brand_code) throws SQLException;

    int getStoresCount(String corp_code, String brand_code) throws SQLException;

    PageInfo<Brand> getAllBrandScreen(int page_number, int page_size, String corp_code, Map<String, String> map);
}
