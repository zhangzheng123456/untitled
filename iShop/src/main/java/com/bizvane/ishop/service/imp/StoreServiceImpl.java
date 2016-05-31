package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.StoreService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private UserMapper userMapper;

    SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);

    //根据id获取店铺信息
    @Override
    public Store getStoreById(int id) throws SQLException {
        return this.storeMapper.selectByStoreId(id);
    }

    //分页显示所有店铺
    public PageInfo<Store> getAllStore(int page_number, int page_size, String corp_code, String search_value) {
        List<Store> shops;
        if (search_value.equals("")) {
            PageHelper.startPage(page_number, page_size);
            shops = storeMapper.selectAllStore(corp_code,"");
        }else {
            PageHelper.startPage(page_number, page_size);
            shops = storeMapper.selectAllStore(corp_code, "%" + search_value + "%");
        }
        PageInfo<Store> page = new PageInfo<Store>(shops);

        return page;
    }

    //分页显示店铺下所属用户
    public List<User> getStoreUser(String corp_code,String store_code){
        List<User> user = userMapper.selectStoreUser(corp_code,"%"+store_code+"%");
        return user;
    }

    //获取用户store_code对应店铺
    public Store getUserStore(String corp_code,String store_code) throws SQLException {
        Store store = storeMapper.selectByUserStore(corp_code, store_code);

        return store;
    }

    //获取企业,store_code对应店铺
    public List<Store> getCorpStore(String corp_code) throws SQLException {
        List<Store> stores = storeMapper.selectAllStore(corp_code,"");
        return stores;
    }

    //新增店铺
    @Override
    public String insert(String message, String user_id){
        JSONObject jsonObject = new JSONObject(message);
        try {

            String store_code = jsonObject.get("store_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Store shop = getStoreByCode(corp_code,store_code);
            if (shop == null) {
                shop = new Store();
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
                storeMapper.insertStore(shop);

                return Common.DATABEAN_CODE_SUCCESS;
            }
        }catch (Exception ex){
            return ex.getMessage();
        }
        return "该企业店铺编号已经存在!";
    }

    //修改店铺
    @Override
    public String update(String message, String user_id){
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(message);
            Store store = new Store();
            store.setId(Integer.valueOf(jsonObject.get("id").toString()));
            store.setStore_code(jsonObject.get("store_code").toString());
            store.setStore_name(jsonObject.get("store_name").toString());
            store.setStore_area(jsonObject.get("store_area").toString());
            store.setCorp_code(jsonObject.get("corp_code").toString());
            store.setBrand_code(jsonObject.get("brand_code").toString());
            store.setBrand_name(jsonObject.get("brand_name").toString());
            store.setFlg_tob(jsonObject.get("flg_tob").toString());
            Date now = new Date();
            store.setModified_date(sdf.format(now));
            store.setModifier(user_id);
            store.setIsactive(jsonObject.get("isactive").toString());
            storeMapper.updateStore(store);
            result = Common.DATABEAN_CODE_SUCCESS;
        }catch (Exception ex){
            return ex.getMessage();
        }
        return result;
    }

    //删除店铺
    @Override
    public int delete(int id) throws SQLException {
        return this.storeMapper.deleteByStoreId(id);
    }

    //根据企业，店铺编号,查找店铺
    public Store getStoreByCode(String corp_code,String store_code){
        Store store = storeMapper.selectStoreCode(store_code, corp_code);
        return store;
    }
}