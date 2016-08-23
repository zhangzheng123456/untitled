package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.*;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.json.JSONObject;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

import java.util.*;

/**
 * Created by zhou on 2016/5/25.
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
    private CorpMapper corpMapper;
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
    public int deleteStoreUser(String user_id, String store_code) throws Exception{
        store_code = Common.STORE_HEAD + store_code + ",";
        return storeMapper.deleteStoreByUserid(user_id, store_code);
    }


    //根据id获取店铺信息
    @Override
    public Store getStoreById(int id) throws Exception {
        Store store = storeMapper.selectByStoreId(id);
        String corp_code = store.getCorp_code();
        StringBuilder brand_name = new StringBuilder("");
        String brand_code = store.getBrand_code();
        if (brand_code != null && !brand_code.equals("")) {
            String[] ids = store.getBrand_code().split(",");
            for (int i = 0; i < ids.length; i++) {
                Brand brand = brandMapper.selectCorpBrand(corp_code, ids[i]);
                if (brand != null) {
                    String brand_name1 = brand.getBrand_name();
                    brand_name.append(brand_name1);
                    if (i != ids.length - 1) {
                        brand_name.append(",");
                    }
                }
            }
            store.setBrand_name(brand_name.toString());
        }else {
            store.setBrand_name("");
            store.setBrand_code("");
        }
        return store;
    }

    public Store getById(int id) throws Exception {
        return storeMapper.selectByStoreId(id);
    }

    //list获取企业店铺
    public List<Store> getCorpStore(String corp_code) throws Exception {
        List<Store> stores = storeMapper.selectStores(corp_code);
        return stores;
    }


    //分页显示所有店铺
    public PageInfo<Store> getAllStore(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value) throws Exception{
        List<Store> shops;
        PageHelper.startPage(page_number, page_size);
        shops = storeMapper.selectAllStore(corp_code, search_value);
        for (Store store:shops) {
            Store storeBrandName = getStoreById(store.getId());
            if (storeBrandName.getBrand_name()!=null) {
                store.setBrand_name(storeBrandName.getBrand_name());
            }else {
                store.setBrand_name("");
            }
            store.setIsactive(CheckUtils.CheckIsactive(store.getIsactive()));
        }
        PageInfo<Store> page = new PageInfo<Store>(shops);
        return page;
    }

    /**
     * 获取用户的店仓信息
     */
    @Override
    public PageInfo<Store> selectByUserId(int page_number, int page_size, String store_code, String corp_code, String search_value) throws Exception{
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
        for (Store store:shops) {
            Store storeBrandName = getStoreById(store.getId());
            store.setBrand_name(storeBrandName.getBrand_name());
            store.setIsactive(CheckUtils.CheckIsactive(store.getIsactive()));
        }
        PageInfo<Store> page = new PageInfo<Store>(shops);

        return page;
    }

    @Override
    public PageInfo<Store> selStoreByUserCode(int page_number, int page_size, String store_code, String corp_code, String search_value) throws Exception {
        List<Store> shops;
        String[] ids = store_code.split(",");
        for (int i = 0; i < ids.length; i++) {
            ids[i] = ids[i].substring(1, ids[i].length());
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_codes", ids);
        params.put("corp_code", corp_code);
        params.put("search_value", search_value);
        params.put("isactive", "Y");
        PageHelper.startPage(page_number, page_size);
        shops = storeMapper.selStoreByUserCode(params);
        PageInfo<Store> page = new PageInfo<Store>(shops);
        return page;
    }

    /***
     * 获取页面的所有数据
     */
    @Override
    public List<Store> selectAll(String store_code, String corp_code, String isactive) throws Exception{
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
    public List<User> getStoreUser(String corp_code, String store_code,String area_code, String role_code,String isactive) throws Exception{
        List<User> user = new ArrayList<User>();

        if (!store_code.equals("")) {
            user = userMapper.selectStoreUser(corp_code, store_code + ",", "", role_code,isactive);
        }
        if (!area_code.equals("")){
            user = userMapper.selectStoreUser(corp_code, "", area_code + ",", role_code,isactive);
        }
        for (User user1:user) {
            user1.setIsactive(CheckUtils.CheckIsactive(user1.getIsactive()));
            if(user1.getSex()==null || user1.getSex().equals("")){
                user1.setSex("未知");
            }else if(user1.getSex().equals("F")){
                user1.setSex("女");
            }else{
                user1.setSex("男");
            }
        }
        return user;
    }


    //根据企业，店铺编号,查找店铺
    public Store getStoreByCode(String corp_code, String store_code, String isactive) throws Exception{
        Store store = storeMapper.selectByCode(corp_code, store_code, isactive);

        return store;
    }

    //新增店铺
    @Override
    public String insert(String message, String user_id) throws Exception {
        JSONObject jsonObject = new JSONObject(message);
        String result = Common.DATABEAN_CODE_ERROR;
        String store_code = jsonObject.get("store_code").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String store_name = jsonObject.get("store_name").toString();
        Store store = getStoreByCode(corp_code, store_code, Common.IS_ACTIVE_Y);
        Store store1 = getStoreByName(corp_code, store_name, Common.IS_ACTIVE_Y);
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

    public String insertExecl(Store store) throws Exception{
        storeMapper.insertStore(store);
        return "add success";
    }

    @Override
    public PageInfo<Store> getAllStoreScreen(int page_number, int page_size, String corp_code, String area_codes, String store_codes, Map<String, String> map) throws Exception{
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
        params.put("area_codes", areas);
        params.put("store_codes", stores);
        params.put("map", map);

        PageHelper.startPage(page_number, page_size);
        List<Store> list1 = storeMapper.selectAllStoreScreen(params);
        for (Store store:list1) {
            Store storeBrandName = getStoreById(store.getId());
            store.setBrand_name(storeBrandName.getBrand_name());
            store.setIsactive(CheckUtils.CheckIsactive(store.getIsactive()));
        }
        PageInfo<Store> page = new PageInfo<Store>(list1);
        return page;
    }

//    private List<Store> ComparaBrandName(List<Store> list, String brand_name) throws Exception{
//        if (brand_name == null || brand_name.isEmpty()) {
//            return list;
//        }
//        List<Store> newList = new ArrayList<Store>();
//        for (int i = 0; list != null && i < list.size(); i++) {
//            Store store = list.get(i);
//            //获取店铺的所有品牌实体
//            List<Brand> brands = storeMapper.selectBrandsStore(store.getCorp_code(), store.getBrand_code());
//            boolean isContain = false;
//            //检查品牌实体中是否包含店铺
//            for (int j = 0; brands != null && j < brands.size(); j++) {
//                if (brands.get(j).getBrand_name().contains(brand_name)) {
//                    isContain = true;
//                }
//            }
//            if (isContain) {
//                newList.add(store);
//            }
//            if (newList.size() >= 10) {
//                return newList;
//            }
//        }
//        return newList;
//    }



    //修改店铺
    @Override
    public String update(String message, String user_id) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        int store_id = Integer.valueOf(jsonObject.get("id").toString());
        String store_code = jsonObject.get("store_code").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String store_name = jsonObject.get("store_name").toString();

        Store store = getStoreById(store_id);
        Store store1 = getStoreByCode(corp_code, store_code, Common.IS_ACTIVE_Y);
        Store store2 = getStoreByName(corp_code, store_name,Common.IS_ACTIVE_Y);
        if (store.getCorp_code().equalsIgnoreCase(corp_code)) {
            if (store1 != null && store_id != store1.getId()){
                result = "店铺编号已存在";
            }else if (store2 != null && store_id != store2.getId()) {
                result = "店铺名称已存在";
            }else {
                if (!store.getStore_code().equalsIgnoreCase(store_code)) {
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
            }
        } else {
            if (store1 == null && store2 == null) {
                if (!store.getStore_code().equalsIgnoreCase(store_code)) {
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
            } else if (store1 != null) {
                result = "店铺编号已存在";
            } else {
                result = "店铺名称已存在";
            }
        }
        return result;
    }

    public int updateStore(Store store) throws Exception {
        return storeMapper.updateStore(store);
    }

    //删除店铺
    @Override
    public int delete(int id) throws Exception {
        return this.storeMapper.deleteByStoreId(id);
    }

    @Override
    public Store getStoreByName(String corp_code, String store_name,String isactive) throws Exception {
        Store store = this.storeMapper.selectByStoreName(corp_code, store_name,isactive);
        return store;
    }

    @Override
    public int selectAchCount(String corp_code, String store_code) throws Exception {
        return this.storeMapper.selectAchCount(corp_code, store_code);
    }

    @Override
    public List<Store> selByAreaCodeList(String corp_code, String[] area_code, String search_value) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", area_code);
        params.put("search_value", search_value);
        params.put("isactive", "");
        List<Store> stores = storeMapper.selectByAreaCode(params);
        return stores;
    }

    @Override
    public PageInfo<Store> selectByAreaCode(int page_number, int page_size, String corp_code, String[] area_code, String search_value) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", area_code);
        params.put("search_value", search_value);
        params.put("isactive", "");
        PageHelper.startPage(page_number, page_size);
        List<Store> stores = storeMapper.selectByAreaCode(params);
        for (Store store:stores) {
            Store storeBrandName = getStoreById(store.getId());
            store.setBrand_name(storeBrandName.getBrand_name());
            store.setIsactive(CheckUtils.CheckIsactive(store.getIsactive()));
        }
        PageInfo<Store> page = new PageInfo<Store>(stores);
        return page;
    }

    @Override
    public PageInfo<Store> selStoreByAreaCode(int page_number, int page_size, String corp_code, String[] area_code, String search_value) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", area_code);
        params.put("search_value", search_value);
        params.put("isactive", "Y");
        PageHelper.startPage(page_number, page_size);
        List<Store> stores = storeMapper.selStoreByAreaCode(params);
        PageInfo<Store> page = new PageInfo<Store>(stores);
        return page;
    }

    public List<Store> selectByAreaCode(String corp_code, String[] area_code, String isactive) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", area_code);
        params.put("search_value", "");
        params.put("isactive", isactive);
        List<Store> stores = storeMapper.selectByAreaCode(params);
        return stores;
    }

    public int selectCount(String created_date) throws Exception{
        return this.storeMapper.selectCount(created_date);
    }

    /**
     * 更改店铺编号时
     * 级联更改关联此编号的员工，员工业绩目标，店铺业绩目标，签到列表
     */
    @Transactional
    void updateCauseCodeChange(String corp_code, String new_store_code, String old_store_code) throws Exception{

        //更新签到列表
        codeUpdateMapper.updateSign("", corp_code, new_store_code, old_store_code, "", "");
        //更新店铺业绩目标
        codeUpdateMapper.updateStoreAchvGoal("", corp_code, new_store_code, old_store_code);
        //更新员工业绩目标
        codeUpdateMapper.updateUserAchvGoal("", corp_code, new_store_code, old_store_code, "", "");
        //更新会员标签关系
        codeUpdateMapper.updateRelVipLabel("", corp_code, new_store_code, old_store_code);
        //更新店铺open_id关系
        List<CorpWechat> corpWechats = corpMapper.selectWByCorp(corp_code);
        for (int i = 0; i < corpWechats.size(); i++) {
            String app_user_name = corpWechats.get(i).getApp_user_name();
            if (app_user_name != null && !app_user_name.equals(""))
                codeUpdateMapper.updateRelVipStore(new_store_code,old_store_code,app_user_name);
        }

        //更新员工
        new_store_code = Common.STORE_HEAD + new_store_code + ",";
        old_store_code = Common.STORE_HEAD + old_store_code + ",";
        codeUpdateMapper.updateUser("", corp_code, "", "", new_store_code, old_store_code, "", "");
        //更新员工详细信息
        codeUpdateMapper.updateStaffDetailInfo("", corp_code, "", "", new_store_code, old_store_code);

    }
    public List<Store> selectAllStores(String corp_code, String search_value) throws Exception{
        List<Store> stores = storeMapper.selectAllStore(corp_code, "");
        return stores;
    }
    public JSONArray selectStoresByAreaCode(String corp_code,String search_value) throws Exception{
        JSONArray array = new JSONArray();
        List<Store> stores = storeMapper.selectAllStore(corp_code, "");
        for (int i = 0; i < stores.size(); i++) {
            int id = stores.get(i).getId();
            JSONObject corps = new JSONObject();
            corps.put("id", id);
            array.add(corps);
        }
        return array;
    }
}
