package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.*;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.StoreService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.json.JSONObject;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private CodeUpdateMapper codeUpdateMapper;

    /**
     * 通过用户ID和制定的店仓来删除用户的店仓
     *
     * @param user_id    ： 用户ID
     * @param store_code ： 店仓ID
     * @return 执行结果
     */
    @Override
    public int deleteStoreUser(String user_id, String store_code) {
        store_code = Common.STORE_HEAD + store_code + ",";
        return storeMapper.deleteStoreByUserid(user_id, store_code);
    }


    //根据id获取店铺信息
    @Override
    public Store getStoreById(int id) throws SQLException {
        Store store = storeMapper.selectByStoreId(id);
        String corp_code = store.getCorp_code();
        String brand_name = "";
        String brand_code = store.getBrand_code();
        if (brand_code != null && !brand_code.equals("")) {
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
        }
        return store;
    }

    public Store getById(int id) throws SQLException {
        return storeMapper.selectByStoreId(id);
    }

    //list获取企业店铺
    public List<Store> getCorpStore(String corp_code) throws SQLException {
        List<Store> stores = storeMapper.selectStores(corp_code);
        return stores;
    }


    //分页显示所有店铺
    public PageInfo<Store> getAllStore(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value) {
        List<Store> shops;

        PageHelper.startPage(page_number, page_size);
        shops = storeMapper.selectAllStore(corp_code, search_value);
        //报表调用
        request.getSession().setAttribute("size", shops.size());

        PageInfo<Store> page = new PageInfo<Store>(shops);

        return page;
    }

    /**
     * 获取用户的店仓信息
     */
    @Override
    public PageInfo<Store> selectByUserId(int page_number, int page_size, String store_code, String corp_code, String search_value) {
        List<Store> shops;
        String[] ids = store_code.split(",");
        for (int i = 0; i < ids.length; i++) {
            ids[i] = ids[i].substring(1, ids[i].length());
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_codes", ids);
        params.put("corp_code", corp_code);
        params.put("search_value", search_value);
        params.put("isactive", "");
        PageHelper.startPage(page_number, page_size);
        shops = storeMapper.selectByUserId(params);
        PageInfo<Store> page = new PageInfo<Store>(shops);

        return page;
    }

    /***
     * 获取页面的所有数据
     */
    @Override
    public List<Store> selectAll(String store_code, String corp_code, String isactive) {
        List<Store> shops;
        String[] ids = store_code.split(",");
        for (int i = 0; i < ids.length; i++) {
            ids[i] = ids[i].substring(1, ids[i].length());
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_codes", ids);
        params.put("corp_code", corp_code);
        params.put("search_value", "");
        params.put("isactive", isactive);
        return storeMapper.selectByUserId(params);
    }

    //店铺下所属用户
    public List<User> getStoreUser(String corp_code, String store_code, String role_code, String user_id) {
        List<User> user = userMapper.selectStoreUser(corp_code, Common.STORE_HEAD + store_code + ",", role_code, user_id);
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

    public String insertExecl(Store store) {
        storeMapper.insertStore(store);
        return "add success";
    }

    @Override
    public PageInfo<Store> getAllStoreScreen(int page_number, int page_size, String corp_code, String area_codes, String store_codes, Map<String, String> map) {
        Map<String, Object> params = new HashMap<String, Object>();
        String[] areas = null;
        String[] stores = null;
        if (!area_codes.isEmpty()) {
            areas = area_codes.split(",");
            for (int i = 0; areas != null && i < areas.length; i++) {
                areas[i] = areas[i].substring(1, areas[i].length());
            }
        }
        if (!store_codes.isEmpty()) {
            stores = store_codes.split(",");
            for (int i = 0; stores != null && i < stores.length; i++) {
                stores[i] = stores[i].substring(1, stores[i].length());
            }
        }
        params.put("corp_code", corp_code);
        params.put("area_codes", area_codes);
        params.put("store_codes", store_codes);
        params.put("map", map);
        List<Store> list = storeMapper.selectAllStoreScreen(params);
        PageInfo<Store> page = new PageInfo<Store>(list);
        return page;
    }
//
//    @Override
//    public PageInfo<Store> getAllStoreScreen(int page_number, int page_size, String corp_code, String[] area_codes, Map<String, String> map) {
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("corp_code", corp_code);
//        params.put("area_codes", area_codes);
//        params.put("map", map);
//        PageHelper.startPage(page_number, page_size);
//        List<Store> list = storeMapper.selectAllStoreScreen(params);
//        PageInfo<Store> page = new PageInfo<Store>(list);
//        return page;
    //}


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
        if (store.getCorp_code().equals(corp_code)) {
            if ((store.getStore_code().equals(store_code) || store1 == null)
                    && (store.getStore_name().equals(store_name) || store2 == null)) {
                if (!store.getStore_code().equals(store_code)) {
                    updateCauseCodeChange(corp_code, store_code, store.getStore_code());
                }
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
        } else {
            if (store1 == null && store2 == null) {
                if (!store.getStore_code().equals(store_code)) {
                    updateCauseCodeChange(corp_code, store_code, store.getStore_code());
                }
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
    public int selectAchCount(String corp_code, String store_code) throws SQLException {
        return this.storeMapper.selectAchCount(corp_code, store_code);
    }

    @Override
    public PageInfo<Store> selectByAreaCode(int page_number, int page_size, String corp_code, String[] area_code, String search_value) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", area_code);
        params.put("search_value", search_value);
        params.put("isactive", "");
        PageHelper.startPage(page_number, page_size);
        List<Store> stores = storeMapper.selectByAreaCode(params);
        PageInfo<Store> page = new PageInfo<Store>(stores);
        return page;
    }

    public List<Store> selectByAreaCode(String corp_code, String[] area_code, String isactive) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", area_code);
        params.put("search_value", "");
        params.put("isactive", isactive);
        List<Store> stores = storeMapper.selectByAreaCode(params);
        return stores;
    }

    public int selectCount(String created_date) {
        return this.storeMapper.selectCount(created_date);
    }

    /**
     * 更改店铺编号时
     * 级联更改关联此编号的员工，员工业绩目标，店铺业绩目标，签到列表
     */
    @Transactional
    void updateCauseCodeChange(String corp_code, String new_store_code, String old_store_code) {

        //更新签到列表
        codeUpdateMapper.updateSign("", corp_code, new_store_code, old_store_code, "", "");
        //更新店铺业绩目标
        codeUpdateMapper.updateStoreAchvGoal("", corp_code, new_store_code, old_store_code);
        //更新员工业绩目标
        codeUpdateMapper.updateUserAchvGoal("", corp_code, new_store_code, old_store_code, "", "");
        //更新会员标签关系
        codeUpdateMapper.updateRelVipLabel("", corp_code, new_store_code, old_store_code);
        //更新员工
        new_store_code = Common.STORE_HEAD + new_store_code + ",";
        old_store_code = Common.STORE_HEAD + old_store_code + ",";
        codeUpdateMapper.updateUser("", corp_code, "", "", new_store_code, old_store_code, "", "");
        //更新员工详细信息
        codeUpdateMapper.updateStaffDetailInfo("", corp_code, "", "", new_store_code, old_store_code);

    }
}
