package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Brand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BrandMapper {
    Brand selectByBrandId(int id);

    Brand selectCorpBrand(@Param("corp_code") String corp_code, @Param("brand_code") String brand_code);

    List<Brand> selectAllBrand(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    int insertBrand(Brand brand);

    int updateBrand(Brand brand);

    int deleteByBrandId(int id);

    Brand selectByBrandName(@Param("corp_code") String corp_code, @Param("brand_name") String brand_name);
}