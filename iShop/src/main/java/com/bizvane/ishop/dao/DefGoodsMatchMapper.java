package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.DefGoodsMatch;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by PC on 2016/11/24.
 */
public interface DefGoodsMatchMapper {
    List<DefGoodsMatch> selectMatchGoods(@Param("corp_code")String corp_code,@Param("manager_corp_arr")String[] manager_corp_arr)throws Exception;

    List<DefGoodsMatch> selMatchBySeach(@Param("corp_code")String corp_code,@Param("search_value")String search_value,@Param("manager_corp_arr")String[] manager_corp_arr)throws Exception;

    List<DefGoodsMatch> selectMatchByCode(@Param("corp_code")String corp_code,@Param("goods_match_code")String goods_match_code)throws Exception;

    int delMatchByCode(@Param("corp_code")String corp_code,@Param("goods_match_code")String goods_match_code)throws Exception;

    int delMatchById(@Param("id")String id)throws Exception;

    int addMatch(DefGoodsMatch defGoodsMatch)throws Exception;

    int updMatch(DefGoodsMatch defGoodsMatch)throws Exception;

    List<DefGoodsMatch> selectGoodsMatchList(@Param("corp_code")String corp_code, @Param("goods_code") String goods_code, @Param("isactive") String isactive) throws SQLException;

    List<DefGoodsMatch> selGoodsCodeByUpd(@Param("corp_code")String corp_code,@Param("goods_code")String goods_code)throws Exception;

    int updGoodsCode(@Param("goods_code")String goods_code,@Param("id")int id)throws Exception;
}
