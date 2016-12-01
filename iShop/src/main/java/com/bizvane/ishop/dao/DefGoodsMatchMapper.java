package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.DefGoodsMatch;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by PC on 2016/11/24.
 */
public interface DefGoodsMatchMapper {
    List<DefGoodsMatch> selectMatchGoods(@Param("corp_code")String corp_code);

    List<DefGoodsMatch> selMatchBySeach(@Param("corp_code")String corp_code,@Param("search_value")String search_value);

    List<DefGoodsMatch> selectMatchByCode(@Param("corp_code")String corp_code,@Param("goods_match_code")String goods_match_code);

    int delMatchByCode(@Param("corp_code")String corp_code,@Param("goods_match_code")String goods_match_code);

    int delMatchById(@Param("id")String id);

    int addMatch(DefGoodsMatch defGoodsMatch);

    int updMatch(DefGoodsMatch defGoodsMatch);

    List<DefGoodsMatch> selectGoodsMatchList(@Param("corp_code")String corp_code, @Param("goods_code") String goods_code, @Param("isactive") String isactive) throws SQLException;

}
