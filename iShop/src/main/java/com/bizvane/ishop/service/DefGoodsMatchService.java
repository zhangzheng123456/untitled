package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.DefGoodsMatch;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by PC on 2016/11/24.
 */
public interface DefGoodsMatchService {
    List<DefGoodsMatch> selectMatchGoods(String corp_code)throws Exception;

    List<DefGoodsMatch> selectMatchGoods(String corp_code,String manager_corp)throws Exception;


    List<DefGoodsMatch> selMatchBySeach(String corp_code,String search_value)throws Exception;

    List<DefGoodsMatch> selMatchBySeach(String corp_code,String search_value,String manager_corp)throws Exception;


    List<DefGoodsMatch> selectMatchByCode(String corp_code,String goods_match_code)throws Exception;

    int delMatchByCode(String corp_code,String goods_match_code)throws Exception;

    int delMatchById(String id)throws Exception;

    int addMatch(DefGoodsMatch defGoodsMatch)throws Exception;

    int updMatch(DefGoodsMatch defGoodsMatch)throws Exception;

    List<DefGoodsMatch> selectGoodsMatchList(String corp_code, String goods_code, String isactive) throws Exception;

    List<DefGoodsMatch> selGoodsCodeByUpd(String corp_code,String goods_code)throws Exception;

    int updGoodsCode(String goods_code,int id)throws Exception;
}
