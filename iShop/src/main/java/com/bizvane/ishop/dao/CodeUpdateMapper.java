package com.bizvane.ishop.dao;

import org.apache.ibatis.annotations.Param;


public interface CodeUpdateMapper {

    int updateUser(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                     @Param("new_group_code") String new_group_code, @Param("old_group_code") String old_group_code,
                     @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code,
                     @Param("new_area_code") String new_area_code, @Param("old_area_code") String old_area_code);

    int updateStore(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                    @Param("new_brand_code") String new_brand_code, @Param("old_brand_code") String old_brand_code,
                    @Param("new_area_code") String new_area_code, @Param("old_area_code") String old_area_code);

    int updateUserAchvGoal(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                           @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code,
                           @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code);

    int updateStoreAchvGoal(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                            @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code);

    int updateSign(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                   @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code);

    int updateGoods(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                    @Param("new_brand_code") String new_brand_code, @Param("old_brand_code") String old_brand_code);
}