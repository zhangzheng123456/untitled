package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.DefGoodsMatch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by PC on 2016/11/24.
 */
public interface DefGoodsMatchService {
    List<DefGoodsMatch> selectMatchGoods(String corp_code);

    List<DefGoodsMatch> selMatchBySeach(String corp_code,String search_value);

    List<DefGoodsMatch> selectMatchByCode(String corp_code,String goods_match_code);

    int delMatchByCode(String goods_match_code);

    int delMatchById(String id);

    int addMatch(DefGoodsMatch defGoodsMatch);

    int updMatch(DefGoodsMatch defGoodsMatch);
}
