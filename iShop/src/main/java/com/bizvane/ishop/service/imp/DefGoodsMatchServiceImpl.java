package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.DefGoodsMatchMapper;
import com.bizvane.ishop.entity.DefGoodsMatch;
import com.bizvane.ishop.service.DefGoodsMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by PC on 2016/11/24.
 */
@Service
public class DefGoodsMatchServiceImpl implements DefGoodsMatchService {
    @Autowired
    private DefGoodsMatchMapper defGoodsMatchMapper;
    @Override
    public List<DefGoodsMatch> selectMatchGoods(String corp_code) {
        return defGoodsMatchMapper.selectMatchGoods(corp_code);
    }

    @Override
    public List<DefGoodsMatch> selMatchBySeach(String corp_code, String search_value) {
        return defGoodsMatchMapper.selMatchBySeach(corp_code,search_value);
    }

    @Override
    public List<DefGoodsMatch> selectMatchByCode(String corp_code, String goods_match_code) {
        return defGoodsMatchMapper.selectMatchByCode(corp_code,goods_match_code);
    }

    @Override
    public int delMatchByCode(String corp_code,String goods_match_code) {
        return defGoodsMatchMapper.delMatchByCode(corp_code,goods_match_code);
    }

    @Override
    public int delMatchById(String id) {
        return defGoodsMatchMapper.delMatchById(id);
    }

    @Override
    public int addMatch(DefGoodsMatch defGoodsMatch) {
        return defGoodsMatchMapper.addMatch(defGoodsMatch);
    }

    @Override
    public int updMatch(DefGoodsMatch defGoodsMatch) {
        return 0;
    }
}
