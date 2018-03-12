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

    Brand getBrandByCode(String corp_code, String brand_code,String isactive) throws Exception;

    Brand getBrandByName(String corp_code, String brand_name,String isactive) throws Exception;

    PageInfo<Brand> getAllBrandByPage(int page_number, int page_size, String corp_code, String search_value,String manager_corp) throws Exception;

    PageInfo<Brand> getPartBrandByPage(int page_number, int page_size, String corp_code,String[] brand_code, String search_value) throws SQLException;

    List<Brand> getActiveBrand(String corp_code, String search_value, String[] brand_codes) throws Exception;

    List<Brand> getActiveBrand(String corp_code, String search_value, String[] brand_codes,String manager_corp) throws Exception;


    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id,String user_code) throws Exception;

    String insertExecl(Brand brand) throws Exception;

    String updateExecl(Brand brand) throws Exception;

    int getGoodsCount(String corp_code, String brand_code) throws Exception;

    PageInfo<Brand> getAllBrandScreen(int page_number, int page_size, String corp_code,String[] brand_code, Map<String, String> map,String manager_corp) throws Exception;

    List<Brand> selectBrandByLabel(String corp_code,String[] brand_code)throws SQLException;
}
