package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.ShopInfoMapper;
import com.bizvane.ishop.entity.ShopInfo;
import com.bizvane.ishop.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by nanji on 2016/5/25.
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopInfoMapper shopInfoMapper;

    @Override
    public ShopInfo getShopInfo(int id) throws SQLException {
        return this.shopInfoMapper.selectByShopInfoId(id);
    }

    public List<ShopInfo> getAllShop(String corp_code, String search_value){
        return shopInfoMapper.selectAllShop(corp_code,"%"+search_value+"%");
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
