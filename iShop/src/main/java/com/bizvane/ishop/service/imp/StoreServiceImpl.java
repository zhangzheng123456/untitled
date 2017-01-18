package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.*;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.apache.log4j.Logger;
import org.json.JSONObject;
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
    private LocationMapper locationMapper;
    @Autowired
    private CodeUpdateMapper codeUpdateMapper;

    private static final Logger logger = Logger.getLogger(StoreServiceImpl.class);

    /**
     * 通过用户ID和制定的店仓来删除用户的店仓
     *
     * @param user_id    ： 用户ID
     * @param store_code ： 店仓ID
     * @return 执行结果
     */
    @Override
    public int deleteStoreUser(String user_id, String store_code) throws Exception {
        store_code = Common.SPECIAL_HEAD + store_code + ",";
        return storeMapper.deleteStoreUser(user_id, store_code);
    }


    /**
     * 根据id获取店铺信息
     * 品牌，区域，二维码
     */
    @Override
    public Store getStoreById(int id) throws Exception {
        Store store = storeMapper.selectByStoreId(id);
        String corp_code = store.getCorp_code();

        StringBuilder brand_name = new StringBuilder("");
        StringBuilder area_name = new StringBuilder("");
        StringBuilder brand_code1 = new StringBuilder("");
        StringBuilder area_code1 = new StringBuilder("");
        String brand_code = store.getBrand_code();
        String area_code = store.getArea_code();

        processStoreToSpecial(store);
        if (brand_code != null && !brand_code.equals("")) {
            brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
            String[] ids = brand_code.split(",");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("corp_code", corp_code);
            map.put("brand_code", ids);
            map.put("search_value", "");
            List<Brand> brands = brandMapper.selectBrands(map);
            for (int i = 0; i < brands.size(); i++) {
                Brand brand = brands.get(i);
                if (brand != null) {
                    String brand_name1 = brand.getBrand_name();
                    brand_name.append(brand_name1 + ",");
                    brand_code1.append(ids[i] + ",");
                }
            }
            String brand_name1 = brand_name.toString();
            brand_code = brand_code1.toString();
            if (brand_name1.endsWith(","))
                brand_name1 = brand_name1.substring(0, brand_name1.length() - 1);
            store.setBrand_name(brand_name1);
            if (brand_code.endsWith(","))
                brand_code = brand_code.substring(0, brand_code.length() - 1);
            store.setBrand_code(brand_code);
        } else {
            store.setBrand_name("");
            store.setBrand_code("");
        }
        if (area_code != null && !area_code.equals("")) {
            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
            String[] ids = area_code.split(",");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("corp_code", corp_code);
            map.put("area_codes", ids);
            List<Area> areas = areaMapper.selectArea(map);
            for (int i = 0; i < areas.size(); i++) {
                Area area = areas.get(i);
                if (area != null) {
                    String area_name1 = area.getArea_name();
                    area_name.append(area_name1 + ",");
                    area_code1.append(area.getArea_code() + ",");
                }
            }
            String area_name1 = area_name.toString();
            area_code = area_code1.toString();
            if (area_name1.endsWith(","))
                area_name1 = area_name1.substring(0, area_name1.length() - 1);
            store.setArea_name(area_name1);
            if (area_code.endsWith(","))
                area_code = area_code.substring(0, area_code.length() - 1);
            store.setArea_code(area_code);
        } else {
            store.setArea_code("");
            store.setArea_name("");
        }
        List<StoreQrcode> qrcodeList = storeMapper.selectByStoreCode(corp_code, store.getStore_code());
        store.setQrcodeList(qrcodeList);
        String lng = store.getLng();
        String lat = store.getLat();
        if (lng != null && lat != null && !lng.equals("") && !lat.equals("")) {
            store.setStore_location(lat + "," + lng);
        } else {
            store.setStore_location("");
        }
        return store;
    }

    public Store getById(int id) throws Exception {
        return storeMapper.selectByStoreId(id);
    }

    //list获取企业店铺
    public List<Store> getCorpStore(String corp_code) throws Exception {
        List<Store> stores = storeMapper.selectByCorp(corp_code);
        return stores;
    }


    //分页显示所有店铺
    public PageInfo<Store> getAllStore(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value, String isactive, String search_area_code) throws Exception {
        List<Store> shops;
        PageHelper.startPage(page_number, page_size);
        if (!search_area_code.equals(""))
            search_area_code = Common.SPECIAL_HEAD + search_area_code + ",";
        shops = storeMapper.selectAllStore(corp_code, search_value, isactive, search_area_code);

        for (int i = 0; i < shops.size(); i++) {
            Store store = getStoreById(shops.get(i).getId());
            if (store.getBrand_name() != null) {
                shops.get(i).setBrand_name(store.getBrand_name());
            } else {
                shops.get(i).setBrand_name("");
            }
            if (store.getArea_name() != null) {
                shops.get(i).setArea_name(store.getArea_name());
            } else {
                shops.get(i).setArea_name("");
            }
            List<StoreQrcode> qrcodeList = store.getQrcodeList();
            StringBuilder qrcode = new StringBuilder("");
            for (int j = 0; j < qrcodeList.size(); j++) {
                if (qrcodeList.get(j) != null) {
                    String qrcode1 = qrcodeList.get(j).getQrcode();
                    qrcode.append(qrcode1);
                    if (j != qrcodeList.size() - 1) {
                        qrcode.append("、");
                    }
                }
            }
            shops.get(i).setQrcode(qrcode.toString());
            shops.get(i).setIsactive(CheckUtils.CheckIsactive(shops.get(i).getIsactive()));
        }
        PageInfo<Store> page = new PageInfo<Store>(shops);
        return page;
    }

    /**
     * 获取用户的店仓信息
     */
    @Override
    public PageInfo<Store> selectByUserId(int page_number, int page_size, String store_code, String corp_code, String search_value) throws Exception {
        List<Store> shops;
        store_code = store_code.replace(Common.SPECIAL_HEAD, "");
        String[] ids = store_code.split(",");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_codes", ids);
        params.put("corp_code", corp_code);
        params.put("search_value", search_value);
        params.put("isactive", "");
        PageHelper.startPage(page_number, page_size);
        shops = storeMapper.selectByStoreCodes(params);

        for (int i = 0; i < shops.size(); i++) {
            Store store = getStoreById(shops.get(i).getId());
            if (store.getBrand_name() != null) {
                shops.get(i).setBrand_name(store.getBrand_name());
            } else {
                shops.get(i).setBrand_name("");
            }
            if (store.getArea_name() != null) {
                shops.get(i).setArea_name(store.getArea_name());
            } else {
                shops.get(i).setArea_name("");
            }
            List<StoreQrcode> qrcodeList = store.getQrcodeList();
            StringBuilder qrcode = new StringBuilder("");
            for (int j = 0; j < qrcodeList.size(); j++) {
                if (qrcodeList.get(j) != null) {
                    String qrcode1 = qrcodeList.get(j).getQrcode();
                    qrcode.append(qrcode1);
                    if (j != qrcodeList.size() - 1) {
                        qrcode.append("、");
                    }
                }
            }
            shops.get(i).setQrcode(qrcode.toString());
            shops.get(i).setIsactive(CheckUtils.CheckIsactive(shops.get(i).getIsactive()));
        }
        PageInfo<Store> page = new PageInfo<Store>(shops);

        return page;
    }

    /**
     * 根据店铺编号拉取多个店铺
     * 支持按店铺名称搜索
     */
    @Override
    public PageInfo<Store> selStoreByStoreCodes(int page_number, int page_size, String store_code, String corp_code, String search_value) throws Exception {
        List<Store> shops;
        //去掉特殊字符
        store_code = store_code.replace(Common.SPECIAL_HEAD, "");
        String[] ids = store_code.split(",");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_codes", ids);
        params.put("corp_code", corp_code);
        params.put("search_value", search_value);
        params.put("isactive", "Y");
        PageHelper.startPage(page_number, page_size);
        shops = storeMapper.selStoreByStoreCodes(params);
        PageInfo<Store> page = new PageInfo<Store>(shops);
        return page;
    }

    /***
     * 获取页面的所有数据
     */
    @Override
    public List<Store> selectByStoreCodes(String store_code, String corp_code, String isactive) throws Exception {
        if (store_code.contains(Common.SPECIAL_HEAD))
            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
        String[] ids = store_code.split(",");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_codes", ids);
        params.put("corp_code", corp_code);
        params.put("search_value", "");
        params.put("isactive", isactive);
        return storeMapper.selectByStoreCodes(params);
    }

    //店铺下所属用户
    public List<User> getStoreUser(String corp_code, String store_code, String area_code, String role_code, String isactive) throws Exception {
        List<User> user = new ArrayList<User>();

        if (!store_code.equals("")) {
            user = userMapper.selectStoreUser(corp_code, Common.SPECIAL_HEAD + store_code + ",", "", role_code, isactive);
        }
        if (!area_code.equals("")) {
            user = userMapper.selectStoreUser(corp_code, "", Common.SPECIAL_HEAD + area_code + ",", role_code, isactive);
        }
        for (User user1 : user) {
            user1.setIsactive(CheckUtils.CheckIsactive(user1.getIsactive()));
            if (user1.getSex() == null || user1.getSex().equals("")) {
                user1.setSex("未知");
            } else if (user1.getSex().equals("F")) {
                user1.setSex("女");
            } else {
                user1.setSex("男");
            }
        }
        return user;
    }


    //根据企业，店铺编号,查找店铺
    public Store getStoreByCode(String corp_code, String store_code, String isactive) throws Exception {
        Store store = storeMapper.selectByCode(corp_code, store_code, isactive);

        return store;
    }

    @Override
    public Store selStoreByStroeId(String corp_code, String store_id, String isactive) throws Exception {
        return storeMapper.selStoreByStroeId(corp_code, store_id, isactive);
    }

    //新增店铺
    @Override
    public String insert(String message, String user_id) throws Exception {
        JSONObject jsonObject = new JSONObject(message);
        String result = Common.DATABEAN_CODE_ERROR;
        String store_code = jsonObject.get("store_code").toString().trim();
        String store_id = jsonObject.get("store_id").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String store_name = jsonObject.get("store_name").toString().trim();
        String logo = "";
        if (jsonObject.has("logo"))
            logo = jsonObject.get("logo").toString().trim();
        Store store = getStoreByCode(corp_code, store_code, Common.IS_ACTIVE_Y);
        List<Store> store1 = getStoreByName(corp_code, store_name, Common.IS_ACTIVE_Y);
        if (store == null && store1.size() < 1) {
            Store shop = new Store();
            shop.setStore_code(store_code);
            shop.setStore_id(store_id);
            shop.setStore_name(store_name);
            String area_code = jsonObject.get("area_code").toString().trim();
            String[] codes1 = area_code.split(",");
            String area_code1 = "";
            for (int i = 0; i < codes1.length; i++) {
                codes1[i] = Common.SPECIAL_HEAD + codes1[i] + ",";
                area_code1 = area_code1 + codes1[i];
            }
            shop.setArea_code(area_code1);
            shop.setCorp_code(corp_code);
            String brand_code = jsonObject.get("brand_code").toString().trim();
            String[] codes = brand_code.split(",");
            String brand_code1 = "";
            for (int i = 0; i < codes.length; i++) {
                codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
                brand_code1 = brand_code1 + codes[i];
            }
            shop.setBrand_code(brand_code1);
            shop.setFlg_tob(jsonObject.get("flg_tob").toString().trim());
            shop.setProvince(jsonObject.get("province").toString().trim());
            shop.setCity(jsonObject.get("city").toString().trim());
            shop.setArea(jsonObject.get("area").toString().trim());
            shop.setStreet(jsonObject.get("street").toString().trim());
            if (jsonObject.has("store_location") && !jsonObject.get("store_location").toString().equals("")) {
                String location = jsonObject.get("store_location").toString().trim();
                if (location != null && !location.equals("")) {
                    shop.setLat(location.split(",")[0]);
                    shop.setLng(location.split(",")[1]);
                }
            }
            Date now = new Date();
            shop.setCreated_date(Common.DATETIME_FORMAT.format(now));
            shop.setCreater(user_id);
            shop.setModified_date(Common.DATETIME_FORMAT.format(now));
            shop.setModifier(user_id);
            shop.setIsactive(jsonObject.get("isactive").toString());
            shop.setLogo(logo);
            storeMapper.insertStore(shop);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else if (store != null) {
            result = "店铺编号已存在";
        } else {
            result = "店铺名称已存在";
        }
        return result;

    }

    public String insertExecl(Store store) throws Exception {
        String brand_code = store.getBrand_code();
        String[] codes = brand_code.split(",");
        String brand_code1 = "";
        for (int i = 0; i < codes.length; i++) {
            codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
            brand_code1 = brand_code1 + codes[i];
        }
        store.setBrand_code(brand_code1);

        String area_code = store.getArea_code();
        String[] codes1 = area_code.split(",");
        String area_code1 = "";
        for (int i = 0; i < codes1.length; i++) {
            codes1[i] = Common.SPECIAL_HEAD + codes1[i] + ",";
            area_code1 = area_code1 + codes1[i];
        }
        store.setArea_code(area_code1);

        storeMapper.insertStore(store);
        return "add success";
    }

    @Override
    public PageInfo<Store> getAllStoreScreen(int page_number, int page_size, String corp_code, String area_codes, String brand_codes
            , String store_codes, Map<String, String> map, String area_store_codes, String isactive) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        String[] areas = null;
        String[] brands = null;
        String[] stores = null;
        String[] area_stores = null;
        if (!area_codes.equals("")) {
            area_codes = area_codes.replace(Common.SPECIAL_HEAD, "");
            areas = area_codes.split(",");
            for (int i = 0; i < areas.length; i++) {
                areas[i] = Common.SPECIAL_HEAD + areas[i] + ",";
            }
        }
        if (!area_store_codes.equals("")) {
            area_store_codes = area_store_codes.replace(Common.SPECIAL_HEAD, "");
            area_stores = area_store_codes.split(",");
        }
        if (!brand_codes.equals("")) {
            brand_codes = brand_codes.replace(Common.SPECIAL_HEAD, "");
            brands = brand_codes.split(",");
            for (int i = 0; i < brands.length; i++) {
                brands[i] = Common.SPECIAL_HEAD + brands[i] + ",";
            }
        }
        if (!store_codes.equals("")) {
            store_codes = store_codes.replace(Common.SPECIAL_HEAD, "");
            stores = store_codes.split(",");
        }
        int flg = 0;
        for (int i = 0; i < map.size(); i++) {
            if (map.containsKey("area_name") && !map.get("area_name").equals("")) {
                flg = 1;
            }
            if (map.containsKey("brand_name") && !map.get("brand_name").equals("")) {
                flg = 1;
            }
        }
        params.put("corp_code", corp_code);
        params.put("area_codes", areas);
        params.put("brand_codes", brands);
        params.put("store_codes", stores);
        params.put("area_store_codes", area_stores);
        params.put("map", map);
        params.put("isactive", isactive);
        List<Store> shops;
        if (flg == 1) {
            PageHelper.startPage(page_number, page_size);
            shops = storeMapper.selectAllStoreScreen(params);
        } else {
            PageHelper.startPage(page_number, page_size);
            shops = storeMapper.selectAllStoreScreenEasy(params);
        }
        for (int i = 0; i < shops.size(); i++) {
            Store store = getStoreById(shops.get(i).getId());
            if (store.getBrand_name() != null) {
                shops.get(i).setBrand_name(store.getBrand_name());
            } else {
                shops.get(i).setBrand_name("");
            }
            if (store.getArea_name() != null) {
                shops.get(i).setArea_name(store.getArea_name());
            } else {
                shops.get(i).setArea_name("");
            }
            List<StoreQrcode> qrcodeList = store.getQrcodeList();
            StringBuilder qrcode = new StringBuilder("");
            for (int j = 0; j < qrcodeList.size(); j++) {
                if (qrcodeList.get(j) != null) {
                    String qrcode1 = qrcodeList.get(j).getQrcode();
                    qrcode.append(qrcode1);
                    if (j != qrcodeList.size() - 1) {
                        qrcode.append("、");
                    }
                }
            }
            shops.get(i).setQrcode(qrcode.toString());
            shops.get(i).setIsactive(CheckUtils.CheckIsactive(shops.get(i).getIsactive()));
        }
        PageInfo<Store> page = new PageInfo<Store>(shops);
        return page;
    }


    //修改店铺
    @Override
    public String update(String message, String user_id) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        int store_id = Integer.valueOf(jsonObject.get("id").toString().trim());
        String store_code = jsonObject.get("store_code").toString().trim();
        String store_id1 = jsonObject.get("store_id").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String store_name = jsonObject.get("store_name").toString().trim();
        String logo = "";
        if (jsonObject.has("logo"))
            logo = jsonObject.get("logo").toString().trim();

        Store store = getById(store_id);
        Store store1 = getStoreByCode(corp_code, store_code, Common.IS_ACTIVE_Y);
        List<Store> store2 = getStoreByName(corp_code, store_name, Common.IS_ACTIVE_Y);
        if (store.getCorp_code().trim().equalsIgnoreCase(corp_code)) {
            if (store1 != null && store_id != store1.getId()) {
                result = "店铺编号已存在";
            } else if (store2.size() > 0 && store_id != store2.get(0).getId()) {
                result = "店铺名称已存在";
            } else {
                if (!store.getStore_code().trim().equalsIgnoreCase(store_code)) {
                    updateCauseCodeChange(corp_code, store_code, store.getStore_code());
                }
                store = new Store();
                store.setId(store_id);
                store.setStore_id(store_id1);
                store.setStore_code(store_code);
                store.setStore_name(store_name);

                String area_code = jsonObject.get("area_code").toString().trim();
                String[] codes1 = area_code.split(",");
                String area_code1 = "";
                for (int i = 0; i < codes1.length; i++) {
                    codes1[i] = Common.SPECIAL_HEAD + codes1[i] + ",";
                    area_code1 = area_code1 + codes1[i];
                }
                store.setArea_code(area_code1);
//                store.setArea_code(jsonObject.get("area_code").toString().trim());
                store.setCorp_code(corp_code);
                String brand_code = jsonObject.get("brand_code").toString().trim();
                String[] codes = brand_code.split(",");
                String brand_code1 = "";
                for (int i = 0; i < codes.length; i++) {
                    codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
                    brand_code1 = brand_code1 + codes[i];
                }
                store.setBrand_code(brand_code1);
                store.setFlg_tob(jsonObject.get("flg_tob").toString().trim());
                store.setProvince(jsonObject.get("province").toString().trim());
                store.setCity(jsonObject.get("city").toString().trim());
                store.setArea(jsonObject.get("area").toString().trim());
                store.setStreet(jsonObject.get("street").toString().trim());

                if (jsonObject.has("store_location")&& !jsonObject.get("store_location").toString().trim().equals("")){

                    String location = jsonObject.get("store_location").toString().trim();
                    store.setStore_location(location);
                    if (location != null && !location.equals("")) {
                        store.setLat(location.split(",")[0]);
                        store.setLng(location.split(",")[1]);
                    }
                }
                Date now = new Date();
                store.setModified_date(Common.DATETIME_FORMAT.format(now));
                store.setModifier(user_id);
                store.setIsactive(jsonObject.get("isactive").toString());
                store.setLogo(logo);
                storeMapper.updateStore(store);
                result = Common.DATABEAN_CODE_SUCCESS;
            }
        } else {
            if (store1 == null && store2 == null) {
                if (!store.getStore_code().trim().equalsIgnoreCase(store_code)) {
                    updateCauseCodeChange(corp_code, store_code, store.getStore_code());
                }
                store = new Store();
                store.setId(store_id);
                store.setStore_code(store_code);
                if (store_id1.equals("")) {
                    store.setStore_id(store_code);
                } else {
                    store.setStore_id(store_id1);
                }
                store.setStore_name(store_name);
                store.setArea_code(jsonObject.get("area_code").toString().trim());
                store.setCorp_code(corp_code);
                String brand_code = jsonObject.get("brand_code").toString().trim();
                String[] codes = brand_code.split(",");
                String brand_code1 = "";
                for (int i = 0; i < codes.length; i++) {
                    codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
                    brand_code1 = brand_code1 + codes[i];
                }
                store.setBrand_code(brand_code1);
                store.setFlg_tob(jsonObject.get("flg_tob").toString().trim());
                store.setProvince(jsonObject.get("province").toString().trim());
                store.setCity(jsonObject.get("city").toString().trim());
                store.setArea(jsonObject.get("area").toString().trim());
                store.setStreet(jsonObject.get("street").toString().trim());
                if (jsonObject.has("store_location")) {
                    String location = jsonObject.get("store_location").toString().trim();
                    if (location != null && !location.equals("")) {
                        store.setLat(location.split(",")[0]);
                        store.setLng(location.split(",")[1]);
                    }

                }
                Date now = new Date();
                store.setModified_date(Common.DATETIME_FORMAT.format(now));
                store.setModifier(user_id);
                store.setIsactive(jsonObject.get("isactive").toString());
                store.setLogo(logo);
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
    public List<Store> getStoreByName(String corp_code, String store_name, String isactive) throws Exception {
        List<Store> store = this.storeMapper.selectByStoreName(corp_code, store_name, isactive);
        return store;
    }

    @Override
    public Store storeIdExist(String corp_code, String store_id) throws Exception {
        Store store = this.storeMapper.selStoreByStroeId(corp_code, store_id, Common.IS_ACTIVE_Y);
        return store;
    }

    @Override
    public int selectAchCount(String corp_code, String store_code) throws Exception {
        return this.storeMapper.selectAchCount(corp_code, store_code);
    }

    @Override
    public PageInfo<Store> selectByAreaBrand(int page_number, int page_size, String corp_code, String[] area_code, String[] store_codes,
                                             String[] brand_code, String search_value, String isactive, String search_area_code) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        if (!search_area_code.equals(""))
            search_area_code = Common.SPECIAL_HEAD + search_area_code + ",";
        params.put("corp_code", corp_code);
        params.put("area_code", area_code);
        params.put("store_codes", store_codes);
        params.put("brand_code", brand_code);
        params.put("search_value", search_value);
        params.put("isactive", isactive);
        params.put("search_area_code", search_area_code);
        PageHelper.startPage(page_number, page_size);
        List<Store> shops = storeMapper.selectByAreaBrand(params);

        for (int i = 0; i < shops.size(); i++) {
            Store store = getStoreById(shops.get(i).getId());
            if (store.getBrand_name() != null) {
                shops.get(i).setBrand_name(store.getBrand_name());
            } else {
                shops.get(i).setBrand_name("");
            }
            if (store.getArea_name() != null) {
                shops.get(i).setArea_name(store.getArea_name());
            } else {
                shops.get(i).setArea_name("");
            }
            List<StoreQrcode> qrcodeList = store.getQrcodeList();
            StringBuilder qrcode = new StringBuilder("");
            for (int j = 0; j < qrcodeList.size(); j++) {
                if (qrcodeList.get(j) != null) {
                    String qrcode1 = qrcodeList.get(j).getQrcode();
                    qrcode.append(qrcode1);
                    if (j != qrcodeList.size() - 1) {
                        qrcode.append("、");
                    }
                }
            }
            shops.get(i).setQrcode(qrcode.toString());
            shops.get(i).setIsactive(CheckUtils.CheckIsactive(shops.get(i).getIsactive()));
        }
        PageInfo<Store> page = new PageInfo<Store>(shops);
        return page;
    }

    @Override
    public PageInfo<Store> selStoreByAreaBrandCode(int page_number, int page_size, String corp_code, String area_code, String brand_code,
                                                   String search_value, String area_store_code) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", "");
        params.put("store_codes", "");
        params.put("brand_code", "");
        if (!area_store_code.equals("")) {
            area_store_code = area_store_code.replace(Common.SPECIAL_HEAD, "");
            String[] store_codes = area_store_code.split(",");
            params.put("store_codes", store_codes);
        }
        if (!area_code.equals("")) {
            String[] areaCodes = area_code.split(",");
            for (int i = 0; i < areaCodes.length; i++) {
                areaCodes[i] = Common.SPECIAL_HEAD + areaCodes[i] + ",";
            }
            params.put("area_code", areaCodes);
        }
        if (!brand_code.equals("")) {
            String[] brandCodes = brand_code.split(",");
            for (int i = 0; i < brandCodes.length; i++) {
                brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
            }
            params.put("brand_code", brandCodes);
        }
        params.put("search_value", search_value);
        params.put("isactive", "Y");
        PageHelper.startPage(page_number, page_size);
        List<Store> stores = storeMapper.selStoreByAreaBrand(params);
        PageInfo<Store> page = new PageInfo<Store>(stores);
        return page;
    }

    @Override
    public PageInfo<Store> selStoreByAreaBrandCity(int page_number, int page_size, String corp_code, String area_code, String brand_code,
                                                   String search_value, String area_store_code, String city) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", "");
        params.put("store_codes", "");
        params.put("brand_code", "");
        params.put("city", "");
        if (!city.equals("")) {
            String[] citys = city.split(",");
            params.put("city", citys);
        }
        if (!area_store_code.equals("")) {
            area_store_code = area_store_code.replace(Common.SPECIAL_HEAD, "");
            String[] store_codes = area_store_code.split(",");
            params.put("store_codes", store_codes);
        }
        if (!area_code.equals("")) {
            String[] areaCodes = area_code.split(",");
            for (int i = 0; i < areaCodes.length; i++) {
                areaCodes[i] = Common.SPECIAL_HEAD + areaCodes[i] + ",";
            }
            params.put("area_code", areaCodes);
        }
        if (!brand_code.equals("")) {
            String[] brandCodes = brand_code.split(",");
            for (int i = 0; i < brandCodes.length; i++) {
                brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
            }
            params.put("brand_code", brandCodes);
        }
        params.put("search_value", search_value);
        params.put("isactive", "Y");
        PageHelper.startPage(page_number, page_size);
        List<Store> stores = storeMapper.selStoreByAreaBrand(params);
        PageInfo<Store> page = new PageInfo<Store>(stores);
        return page;
    }

    @Override
    public List<Store> selStoreByAreaBrandCode(String corp_code, String area_code, String brand_code,
                                               String search_value, String area_store_code) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", "");
        params.put("store_codes", "");
        params.put("brand_code", "");
        if (!area_store_code.equals("")) {
            area_store_code = area_store_code.replace(Common.SPECIAL_HEAD, "");
            String[] store_codes = area_store_code.split(",");
            params.put("store_codes", store_codes);
        }
        if (!area_code.equals("")) {
            String[] areaCodes = area_code.split(",");
            for (int i = 0; i < areaCodes.length; i++) {
                areaCodes[i] = Common.SPECIAL_HEAD + areaCodes[i] + ",";
            }
            params.put("area_code", areaCodes);
        }
        if (!brand_code.equals("")) {
            String[] brandCodes = brand_code.split(",");
            for (int i = 0; i < brandCodes.length; i++) {
                brandCodes[i] = Common.SPECIAL_HEAD + brandCodes[i] + ",";
            }
            params.put("brand_code", brandCodes);
        }
        params.put("search_value", search_value);
        params.put("isactive", "Y");
        List<Store> stores = storeMapper.selStoreByAreaBrand(params);
        return stores;
    }

    public List<Store> selectByAreaBrand(String corp_code, String[] area_code, String[] store_codes, String[] brand_code, String isactive) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", area_code);
        params.put("store_codes", store_codes);
        params.put("brand_code", brand_code);
        params.put("search_value", "");
        params.put("isactive", isactive);
        List<Store> stores = storeMapper.selectByAreaBrand(params);
        return stores;
    }

    public List<Store> selectStoreCountByArea(String corp_code, String area_code, String isactive) throws Exception {
        String[] area_codes = area_code.split(",");
        for (int i = 0; i < area_codes.length; i++) {
            area_codes[i] = Common.SPECIAL_HEAD + area_codes[i] + ",";
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("array", area_codes);
        params.put("isactive", isactive);
        List<Store> stores = storeMapper.selectStoreCountByArea(params);
        return stores;
    }

    public List<Store> selectStoreCountByBrand(String corp_code, String brand_code, String search_value, String isactive) throws Exception {
        String[] brand_codes = brand_code.split(",");
        for (int i = 0; i < brand_codes.length; i++) {
            brand_codes[i] = Common.SPECIAL_HEAD + brand_codes[i] + ",";
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("array", brand_codes);
        params.put("search_value", search_value);
        params.put("isactive", isactive);
        List<Store> stores = storeMapper.selectStoreCountByBrand(params);
        return stores;
    }


    public int selectCount(String created_date) throws Exception {
        return this.storeMapper.selectCount(created_date);
    }

    public List<Store> selectStoreCity(String corp_code, String search_value) throws Exception {
        return storeMapper.selectStoreCity(corp_code, search_value);
    }

    /**
     * 更改店铺编号时
     * 级联更改关联此编号的员工，员工业绩目标，店铺业绩目标，签到列表
     */
    @Transactional
    void updateCauseCodeChange(String corp_code, String new_store_code, String old_store_code) throws Exception {

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
                codeUpdateMapper.updateRelVipStore(new_store_code, old_store_code, app_user_name);
        }

        //更新员工
        String new_store_code1 = Common.SPECIAL_HEAD + new_store_code + ",";
        String old_store_code1 = Common.SPECIAL_HEAD + old_store_code + ",";
        codeUpdateMapper.updateUser("", corp_code, "", "", new_store_code1, old_store_code1, "", "", "", "");
        //更新员工详细信息
        codeUpdateMapper.updateStaffDetailInfo("", corp_code, "", "", new_store_code1, old_store_code1);
        //删除二维码
        storeMapper.deleteStoreQrcode(corp_code, old_store_code);
    }


    public int deleteStoreQrcode(String corp_code, String store_code) throws Exception {
        return storeMapper.deleteStoreQrcode(corp_code, store_code);
    }

    public int deleteStoreQrcodeOne(String corp_code, String store_code, String app_id) throws Exception {
        return storeMapper.deleteStoreQrcodeOne(corp_code, store_code, app_id);
    }

    public String creatStoreQrcode(String corp_code, String store_code, String auth_appid, String user_id) throws Exception {
        List<StoreQrcode> storeQrcodes = storeMapper.selectByStoreApp(corp_code, store_code, auth_appid);
        String picture = "";
        if (storeQrcodes.size() != 1) {
            deleteStoreQrcodeOne(corp_code, store_code, auth_appid);
            String url = CommonValue.wechat_url + "/creatQrcode?auth_appid=" + auth_appid + "&prd=ishop&src=s&store_id=" + store_code;
            String result = IshowHttpClient.get(url);
            logger.info("------------creatQrcode  result" + result);
            if (!result.startsWith("{")) {
                return Common.DATABEAN_CODE_ERROR;
            }
            JSONObject obj = new JSONObject(result);
            if (result.contains("errcode")) {
                String rst = obj.get("errcode").toString();
                return rst;
            } else {
                picture = obj.get("picture").toString();
                String qrcode_url = obj.get("url").toString();
                StoreQrcode storeQrcode = new StoreQrcode();
                storeQrcode.setApp_id(auth_appid);
                storeQrcode.setCorp_code(corp_code);
                storeQrcode.setStore_code(store_code);
                storeQrcode.setQrcode(picture);
                storeQrcode.setQrcode_content(qrcode_url);
                Date now = new Date();
                storeQrcode.setModified_date(Common.DATETIME_FORMAT.format(now));
                storeQrcode.setModifier(user_id);
                storeQrcode.setCreated_date(Common.DATETIME_FORMAT.format(now));
                storeQrcode.setCreater(user_id);
                storeQrcode.setIsactive(Common.IS_ACTIVE_Y);
                storeMapper.insertStoreQrcode(storeQrcode);
            }
        } else {
            picture = storeQrcodes.get(0).getQrcode();
        }
        return picture;
    }

    @Override
    public List<Store> selectStore(String corp_code, String store_codes) throws SQLException {
        String[] storeArray = null;
        List<Store> stores = new ArrayList<Store>();
        if (null != store_codes && !store_codes.isEmpty()) {
            store_codes = store_codes.replace(Common.SPECIAL_HEAD, "");
            storeArray = store_codes.split(",");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("corp_code", corp_code);
            params.put("store_codes", storeArray);
            stores = storeMapper.selectStore(params);
        }
        return stores;
    }

    /**
     * 若导入数据
     * 将brand_code封装成固定格式
     */
    public void processStoreToSpecial(Store store) throws Exception {
        String brand_code = store.getBrand_code();
        String area_code = store.getArea_code();
        if (brand_code != null && !brand_code.equals("")) {
            if (!brand_code.startsWith(Common.SPECIAL_HEAD)) {
                String[] ids = brand_code.split(",");
                String brand_code1 = "";
                for (int i = 0; i < ids.length; i++) {
                    brand_code1 = brand_code1 + Common.SPECIAL_HEAD + ids[i] + ",";
                }
                store.setBrand_code(brand_code1);
            }
        }
        if (area_code != null && !area_code.equals("")) {
            if (!area_code.startsWith(Common.SPECIAL_HEAD)) {
                String[] ids = area_code.split(",");
                String area_code1 = "";
                for (int i = 0; i < ids.length; i++) {
                    area_code1 = area_code1 + Common.SPECIAL_HEAD + ids[i] + ",";
                }
                store.setArea_code(area_code1);
            }
        }
        storeMapper.updateStore(store);
    }


    public List<Store> getStoreByBrandCode(String corp_code, String area_codes, String brand_codes
            , String store_codes, Map<String, String> map, String area_store_codes, String isactive) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        String[] areas = null;
        String[] brands = null;
        String[] stores = null;
        String[] area_stores = null;
        if (!area_codes.equals("")) {
            area_codes = area_codes.replace(Common.SPECIAL_HEAD, "");
            areas = area_codes.split(",");
            for (int i = 0; i < areas.length; i++) {
                areas[i] = Common.SPECIAL_HEAD + areas[i] + ",";
            }
        }
        if (!area_store_codes.equals("")) {
            area_store_codes = area_store_codes.replace(Common.SPECIAL_HEAD, "");
            area_stores = area_store_codes.split(",");
        }
        if (!brand_codes.equals("")) {
            brand_codes = brand_codes.replace(Common.SPECIAL_HEAD, "");
            brands = brand_codes.split(",");
            for (int i = 0; i < brands.length; i++) {
                brands[i] = Common.SPECIAL_HEAD + brands[i] + ",";
            }
        }
        if (!store_codes.equals("")) {
            store_codes = store_codes.replace(Common.SPECIAL_HEAD, "");
            stores = store_codes.split(",");
        }
        int flg = 0;
        for (int i = 0; i < map.size(); i++) {
            if (map.containsKey("area_name") && !map.get("area_name").equals("")) {
                flg = 1;
            }
            if (map.containsKey("brand_name") && !map.get("brand_name").equals("")) {
                flg = 1;
            }
        }
        params.put("corp_code", corp_code);
        params.put("area_codes", areas);
        params.put("brand_codes", brands);
        params.put("store_codes", stores);
        params.put("area_store_codes", area_stores);
        params.put("map", map);
        params.put("isactive", isactive);
        List<Store> shops;
        if (flg == 1) {
            shops = storeMapper.selectAllStoreScreen(params);
        } else {
            shops = storeMapper.selectAllStoreScreenEasy(params);
        }
        for (int i = 0; i < shops.size(); i++) {
            Store store = getStoreById(shops.get(i).getId());
            if (store.getBrand_name() != null) {
                shops.get(i).setBrand_name(store.getBrand_name());
            } else {
                shops.get(i).setBrand_name("");
            }
            if (store.getArea_name() != null) {
                shops.get(i).setArea_name(store.getArea_name());
            } else {
                shops.get(i).setArea_name("");
            }
            List<StoreQrcode> qrcodeList = store.getQrcodeList();
            StringBuilder qrcode = new StringBuilder("");
            for (int j = 0; j < qrcodeList.size(); j++) {
                if (qrcodeList.get(j) != null) {
                    String qrcode1 = qrcodeList.get(j).getQrcode();
                    qrcode.append(qrcode1);
                    if (j != qrcodeList.size() - 1) {
                        qrcode.append("、");
                    }
                }
            }
            shops.get(i).setQrcode(qrcode.toString());
            shops.get(i).setIsactive(CheckUtils.CheckIsactive(shops.get(i).getIsactive()));
        }
        return shops;
    }

    public PageInfo<Store> selectAllOrderByCity(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<Store> shops = storeMapper.selectAllOrderByCity(corp_code, search_value);
        PageInfo<Store> page = new PageInfo<Store>(shops);
        return page;
    }

    public List<Store> selectNearByStore(String corp_code, String lng, String lat, String distance) throws Exception {
        List<Store> shops = storeMapper.selectNearByStore(corp_code, lng, lat, distance);
        return shops;
    }
}
