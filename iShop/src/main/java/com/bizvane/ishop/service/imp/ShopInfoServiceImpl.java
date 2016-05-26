package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.controller.UserController;
import com.bizvane.ishop.dao.ShopInfoMapper;
import com.bizvane.ishop.entity.ShopInfo;
import com.bizvane.ishop.service.ShopInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * Created by nanji on 2016/5/25.
 */
@Service
public class ShopInfoServiceImpl implements ShopInfoService{

    @Autowired
    private ShopInfoMapper shopInfoMapper;

    @Override
    public ShopInfo getShopInfo(int id) throws SQLException {
        return this.shopInfoMapper.selectByShopInfoId(id);
    }

    @Override
    public int insert(ShopInfo shopInfo) throws SQLException {

        return this.shopInfoMapper.insertShopInfo(shopInfo);

    }

    @Override
    public int update(ShopInfo shopInfo) throws SQLException {
        return this.shopInfoMapper.updateShopInfo(shopInfo);
    }

    @Override
    public int delete(int id) throws SQLException {
        return this.shopInfoMapper.deleteByShopInfoId(id);
    }
}
