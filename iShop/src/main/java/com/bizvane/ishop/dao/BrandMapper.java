package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Brand;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BrandMapper {
    Brand selectByBrandId(int id) throws SQLException;

    Brand selectByBrandCode(@Param("corp_code") String corp_code, @Param("brand_code") String brand_code, @Param("isactive") String isactive) throws SQLException;

    Brand selectByBrandName(@Param("corp_code") String corp_code, @Param("brand_name") String brand_name, @Param("isactive") String isactive) throws SQLException;

    List<Brand> selectAllBrand(@Param("corp_code") String corp_code, @Param("search_value") String search_value,@Param("manager_corp_arr")String[] manager_corp_arr) throws SQLException;

    List<Brand> selectPartBrand(Map<String, Object> params) throws SQLException;

    List<Brand> selectBrands(Map<String, Object> params) throws SQLException;

    int insertBrand(Brand brand) throws SQLException;

    int updateBrand(Brand brand) throws SQLException;

    int deleteByBrandId(int id) throws SQLException;

    int getGoodsCount(@Param("corp_code") String corp_code, @Param("brand_code") String brand_code) throws SQLException;

    List<Brand> selectAllBrandScreen(Map<String, Object> params) throws SQLException;

    List<Brand> selectBrandByLabel(@Param("corp_code")String corp_code,@Param("brand_code") String[] brand_code)throws SQLException;
}