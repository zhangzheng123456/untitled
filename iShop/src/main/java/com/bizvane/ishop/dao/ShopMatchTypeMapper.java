package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.ShopMatchType;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by PC on 2017/5/2.
 */
public interface ShopMatchTypeMapper {
    List<ShopMatchType> selectAllMatchType(@Param("corp_code")String corp_code,@Param("search_value") String search_value,@Param("role_type")String role_type,@Param("manager_corp_arr")String[] manager_corp_arr) throws SQLException;


    int addShopMatchType(ShopMatchType shopMatchType) throws SQLException;

    int updShopMatchType(ShopMatchType shopMatchType) throws SQLException;

    int delShopMatchTypeById(int id) throws SQLException;

    ShopMatchType selShopMatchTypeById(@Param("id") int id) throws SQLException;

    List<ShopMatchType> checkName(@Param("corp_code")String corp_code,@Param("shopmatch_type")String shopmatch_type) throws SQLException;
}
