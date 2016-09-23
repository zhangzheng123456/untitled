package com.bizvane.ishop.dao;

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

    List<Goods> selectAllGoods(@Param("corp_code") String corp_code, @Param("search_value") String search_value, @Param("isactive") String isactive) throws SQLException;

    List<Goods> selectAllGoodsForApp(@Param("corp_code") String corp_code,@Param("goods_quarter") String goods_quarter,@Param("goods_wave") String goods_wave,
                                     @Param("brand_code") String brand_code,@Param("time_start") String time_start,@Param("time_end") String time_end, @Param("search_value") String search_value, @Param("isactive") String isactive) throws SQLException;

    List<Goods> selectAllGoodsByBrand(@Param("corp_code") String corp_code, @Param("search_value") String search_value, @Param("brand_code") String brand_code) throws SQLException;

    List<Goods> matchGoodsList(@Param("corp_code") String corp_code, @Param("search_value") String search_value,
                               @Param("goods_code") String goods_code, @Param("brand_code") String brand_code, @Param("isactive") String isactive) throws SQLException;

    List<Goods> selectAllGoodsScreen(Map<String,Object> map) throws SQLException;

    Goods getGoodsByCode(@Param("corp_code") String corp_code, @Param("goods_code") String goods_code, @Param("isactive") String isactive) throws SQLException;

    Goods getGoodsByName(@Param("corp_code") String corp_code,@Param("goods_name") String goods_name, @Param("isactive") String isactive) throws SQLException;

    List<GoodsMatch> selectMatchGoods1(@Param("corp_code") String corp_code, @Param("goods_code") String goods_code) throws SQLException;

    List<GoodsMatch> selectMatchGoods2(@Param("corp_code") String corp_code, @Param("goods_code") String goods_code) throws SQLException;

    int insertMatch(GoodsMatch goodsMatch);

    int deleteMatch(@Param("corp_code") String corp_code, @Param("goods_code") String goods_code) throws SQLException;

    //获取企业FAB季度
    List<Goods> selectCorpGoodsQuarter(@Param("corp_code") String corp_code) throws SQLException;
    //获取企业FAB波段
    List<Goods> selectCorpGoodsWave(@Param("corp_code") String corp_code) throws SQLException;

}