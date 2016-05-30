package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.GoodsMapper;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/5/30.
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    private static  final org.apache.log4j.Logger log= org.apache.log4j.Logger.getLogger(GoodsServiceImpl.class);


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
}
