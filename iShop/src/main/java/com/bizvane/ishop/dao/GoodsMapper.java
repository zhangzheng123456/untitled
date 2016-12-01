package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.DefGoodsMatch;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.entity.GoodsMatch;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface GoodsMapper {
    Goods selectByPrimaryKey(int id) throws SQLException;

    int insert(Goods record) throws SQLException;

    int updateByPrimaryKey(Goods record) throws SQLException;

    int deleteByPrimaryKey(int id) throws SQLException;

    List<Goods> selectAllGoods(Map<String,Object> map) throws SQLException;

    List<Goods> selectAllGoodsForApp(Map<String,Object> map) throws SQLException;

    List<Goods> selectAllGoodsByBrand(Map<String,Object> map) throws SQLException;

    List<Goods> matchGoodsList(@Param("corp_code") String corp_code, @Param("search_value") String search_value,
                               @Param("goods_code") String goods_code, @Param("brand_code") String brand_code, @Param("isactive") String isactive) throws SQLException;

    List<Goods> selectAllGoodsScreen(Map<String,Object> map) throws SQLException;

    Goods getGoodsByCode(@Param("corp_code") String corp_code, @Param("goods_code") String goods_code, @Param("isactive") String isactive) throws SQLException;

    Goods getGoodsByName(@Param("corp_code") String corp_code,@Param("goods_name") String goods_name, @Param("isactive") String isactive) throws SQLException;

    //获取企业FAB季度
    List<Goods> selectCorpGoodsQuarter(@Param("corp_code") String corp_code) throws SQLException;
    //获取企业FAB波段
    List<Goods> selectCorpGoodsWave(@Param("corp_code") String corp_code) throws SQLException;

    List<Goods> getMatchFab(@Param("corp_code")String corp_code,@Param("search_value") String search_value) throws SQLException;

}