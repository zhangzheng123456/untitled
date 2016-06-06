package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Brand;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ZhouZhou on 2016/6/4.
 */
public interface BrandService {
    Brand getBrandById(int id) throws SQLException;

    Brand getBrandByCode(String corp_code,String brand_code) throws SQLException;

    PageInfo<Brand> getAllBrandByPage(int page_number, int page_size, String corp_code, String search_value) throws SQLException;

    List<Brand> getAllBrand(String corp_code,String search_value) throws SQLException;

    int insert(Brand brand) throws SQLException;

    int update(Brand brand) throws SQLException;

    int delete(int id) throws SQLException;
}
