package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.AreaMapper;
import com.bizvane.ishop.dao.BrandMapper;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.StoreService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.glass.ui.Application;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by nanji on 2016/5/25.
 */
@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private AreaMapper areaMapper;

    /**
     * 通过用户ID和制定的店仓来删除用户的店仓
     *
     * @param user_id    ： 用户ID
     * @param store_code ： 店仓ID
     * @return 执行结果
     */
    @Override
    public int deleteStoreUser(String user_id, String store_code) {
        return storeMapper.deleteStoreByUserid(user_id, store_code);
    }


    //根据id获取店铺信息
    @Override
    public Store getStoreById(int id) throws SQLException {
        Store store = storeMapper.selectByStoreId(id);
        String corp_code = store.getCorp_code();
        String brand_name = "";
        System.out.println(store.getBrand_code());
        String[] ids = store.getBrand_code().split(",");
        for (int i = 0; i < ids.length; i++) {
            Brand brand = brandMapper.selectCorpBrand(corp_code, ids[i]);
            String brand_name1 = brand.getBrand_name();
            brand_name = brand_name + brand_name1;
            if (i != ids.length - 1) {
                brand_name = brand_name + ",";
            }
        }
        store.setBrand_name(brand_name);
        return store;
    }

    //list获取企业店铺
    public List<Store> getCorpStore(String corp_code) throws SQLException {
        List<Store> stores = storeMapper.selectStores(corp_code);
        return stores;
    }


    //分页显示所有店铺
    public PageInfo<Store> getAllStore(HttpServletRequest request,int page_number, int page_size, String corp_code, String search_value) {
        List<Store> shops;

        PageHelper.startPage(page_number, page_size);
        shops = storeMapper.selectAllStore(corp_code, search_value);
        //报表调用
        request.getSession().setAttribute("size",shops.size());

        PageInfo<Store> page = new PageInfo<Store>(shops);

        return page;
    }

    /**
     * 获取用户的店仓信息
     */
    @Override
    public PageInfo<Store> selectByUserId(int page_number, int page_size, String user_id, String corp_code, String search_value) {
        List<Store> shops;
        PageHelper.startPage(page_number, page_size);
        shops = storeMapper.selectByUserId(user_id, corp_code, search_value);
        PageInfo<Store> page = new PageInfo<Store>(shops);

        return page;
    }

    /***
     * 获取页面的所有数据
     */
    @Override
    public List<Store> selectAll(String user_id, String corp_code) {
        return storeMapper.selectByUserId(user_id, corp_code, "");
    }

    //店铺下所属用户
    public List<User> getStoreUser(String corp_code, String store_code) {
        List<User> user = userMapper.selectStoreUser(corp_code, store_code);
        return user;
    }


    //根据企业，店铺编号,查找店铺
    public Store getStoreByCode(String corp_code, String store_code, String isactive) {
        Store store = storeMapper.selectByCode(corp_code, store_code, "");

        return store;
    }

    //新增店铺
    @Override
    public String insert(String message, String user_id) throws SQLException {
        JSONObject jsonObject = new JSONObject(message);
        String result = Common.DATABEAN_CODE_ERROR;
        String store_code = jsonObject.get("store_code").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String store_name = jsonObject.get("store_name").toString();
        Store store = getStoreByCode(corp_code, store_code, "");
        Store store1 = getStoreByName(corp_code, store_name);
        if (store == null && store1 == null) {
            Store shop = new Store();
            shop.setStore_code(store_code);
            shop.setStore_name(store_name);
            shop.setArea_code(jsonObject.get("area_code").toString());
            shop.setCorp_code(corp_code);
            shop.setBrand_code(jsonObject.get("brand_code").toString());
            shop.setFlg_tob(jsonObject.get("flg_tob").toString());
            Date now = new Date();
            shop.setCreated_date(Common.DATETIME_FORMAT.format(now));
            shop.setCreater(user_id);
            shop.setModified_date(Common.DATETIME_FORMAT.format(now));
            shop.setModifier(user_id);
            shop.setIsactive(jsonObject.get("isactive").toString());
            storeMapper.insertStore(shop);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else if (store != null) {
            result = "店铺编号已存在";
        } else {
            result = "店铺名称已存在";
        }
        return result;

    }

    //修改店铺
    @Override
    public String update(String message, String user_id) throws SQLException {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        int store_id = Integer.valueOf(jsonObject.get("id").toString());
        String store_code = jsonObject.get("store_code").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String store_name = jsonObject.get("store_name").toString();

        Store store = getStoreById(store_id);
        Store store1 = getStoreByCode(corp_code, store_code, "");
        Store store2 = getStoreByName(corp_code, store_name);

        if ((store.getStore_code().equals(store_code) || store1 == null)
                && (store.getStore_name().equals(store_name) || store2 == null)) {
            store = new Store();
            store.setId(store_id);
            store.setStore_code(store_code);
            store.setStore_name(store_name);
            store.setArea_code(jsonObject.get("area_code").toString());
            store.setCorp_code(corp_code);
            store.setBrand_code(jsonObject.get("brand_code").toString());
            store.setFlg_tob(jsonObject.get("flg_tob").toString());
            Date now = new Date();
            store.setModified_date(Common.DATETIME_FORMAT.format(now));
            store.setModifier(user_id);
            store.setIsactive(jsonObject.get("isactive").toString());
            storeMapper.updateStore(store);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else if (!store.getStore_code().equals(store_code) && store1 != null) {
            result = "店铺编号已存在";
        } else {
            result = "店铺名称已存在";
        }
        return result;
    }

    public int updateStore(Store store) throws SQLException {
        return storeMapper.updateStore(store);
    }

    //删除店铺
    @Override
    public int delete(int id) throws SQLException {
        return this.storeMapper.deleteByStoreId(id);
    }

    @Override
    public Store getStoreByName(String corp_code, String store_name) throws SQLException {
        Store store = this.storeMapper.selectByStoreName(corp_code, store_name);
        return store;
    }

    @Override
    public int selectAchCount(String store_code) throws SQLException {
        return this.storeMapper.selectAchCount(store_code);
    }
    @Override
    public PageInfo<Store> selectByAreaCode(int page_number, int page_size, String corp_code, String area_code, String search_value) {
        PageHelper.startPage(page_number, page_size);
        List<Store> stores = storeMapper.selectByAreaCode(corp_code, area_code, search_value);
        PageInfo<Store> page=new PageInfo<Store>(stores);
        return page;
    }

}
