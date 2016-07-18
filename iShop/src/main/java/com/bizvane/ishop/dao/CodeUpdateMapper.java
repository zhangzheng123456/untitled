package com.bizvane.ishop.dao;

import org.apache.ibatis.annotations.Param;


public interface CodeUpdateMapper {

    int updateUser(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                     @Param("new_group_code") String new_group_code, @Param("old_group_code") String old_group_code,
                     @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code,
                     @Param("new_area_code") String new_area_code, @Param("old_area_code") String old_area_code);
}