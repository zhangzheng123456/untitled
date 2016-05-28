package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.ShopInfoMapper;
import com.bizvane.ishop.entity.ShopInfo;
import com.bizvane.ishop.service.ShopService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by nanji on 2016/5/25.
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopInfoMapper shopInfoMapper;

    SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);

    @Override
    public ShopInfo getShopInfo(int id) throws SQLException {
        return this.shopInfoMapper.selectByShopInfoId(id);
    }

    public List<ShopInfo> getAllShop(String corp_code, String search_value) {
        return shopInfoMapper.selectAllShop(corp_code, "%" + search_value + "%");
    }

    @Override
    public String insert(String message, String user_id) throws SQLException {
        JSONObject jsonObject = new JSONObject(message);
        //     ShopInfo shop = new ShopInfo();
        String store_code = jsonObject.get("store_code").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        ShopInfo shop = shopInfoMapper.selectShopCode(store_code, corp_code);
        if (shop == null) {
            shop = new ShopInfo();
            shop.setStore_code(store_code);
            shop.setStore_name(jsonObject.get("store_name").toString());
            shop.setStore_area(jsonObject.get("store_area").toString());
            shop.setCorp_code(corp_code);
            shop.setBrand_code(jsonObject.get("brand_code").toString());
            shop.setBrand_name(jsonObject.get("brand_name").toString());
            shop.setFlg_tob(jsonObject.get("flg_tob").toString());
            Date now = new Date();
            shop.setCreated_date(sdf.format(now));
            shop.setCreater(user_id);
            shop.setModified_date(sdf.format(now));
            shop.setModifier(user_id);
            shop.setIsactive(jsonObject.get("isactive").toString());
            shopInfoMapper.insertShopInfo(shop);

            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
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
