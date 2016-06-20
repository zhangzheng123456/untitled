package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.GoodsMapper;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.service.GoodsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/5/30.
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GoodsServiceImpl.class);


    public GoodsServiceImpl() {
    }

    @Override
    public Goods getGoodsById(int id) throws SQLException {
        return this.goodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(Goods goods) throws SQLException {
        return goodsMapper.insert(goods);
    }

    @Override
    public int update(Goods goods) throws SQLException {
        return goodsMapper.updateByPrimaryKey(goods);
    }

    @Override
    public int delete(int id) throws SQLException {
        return goodsMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<Goods> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        List<Goods> list;
        PageHelper.startPage(page_number, page_size);
        list = goodsMapper.selectAllGoods(corp_code, search_value);
        PageInfo<Goods> page = new PageInfo<Goods>(list);
        return page;
    }

    @Override
    public Goods getGoodsByCode(String corp_code, String goods_code) {
        Goods goods = this.goodsMapper.getGoodsByCode(corp_code, goods_code);
        return goods;
    }

    @Override
    public String goodsCodeExist( String corp_code,String goods_code) {
        Goods good = goodsMapper.getGoodsByCode(corp_code, goods_code);
        String result = Common.DATABEAN_CODE_ERROR;
        if (good == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }

    @Override
    public String goodsNameExist(String corp_code,String goods_name) {
        Goods good = goodsMapper.getGoodsByName(goods_name, corp_code);
        String result = Common.DATABEAN_CODE_ERROR;
        if (good == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }
}
