package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.*;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.mongodb.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

import java.text.SimpleDateFormat;
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
    private VIPRelationMapper vipRelationMapper;
    @Autowired
    private CodeUpdateMapper codeUpdateMapper;
    @Autowired
    MongoDBClient mongodbClient;
    private static final Logger logger = Logger.getLogger(StoreServiceImpl.class);
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    //Date now = new Date();

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

        processStoreToSpecial(store);
        //获取店铺所属品牌，所属群组
        getStoreArea(store);
        getStoreBrand(store);

        List<StoreQrcode> qrcodeList = storeMapper.selectByStoreCode(store.getCorp_code(), store.getStore_code());
        store.setQrcodeList(qrcodeList);
        String lng = store.getLng();
        String lat = store.getLat();
        if (lng != null && lat != null && !lng.equals("") && !lat.equals("")) {
            store.setStore_location(lat + "," + lng);
        } else {
            store.setStore_location("");
        }
        String isopen = store.getIsopen();
        String open_date = store.getOpen_date();
        String close_date = store.getClose_date();
        String open_date_count = "0";
        if ((null == close_date || "".equals(close_date)) && isopen.equals("Y")) {
            String start_time = open_date;
            String end_time = Common.DATETIME_FORMAT.format(new Date());

            Date date_start = format.parse(start_time);
            Date date_end = format.parse(end_time);
            open_date_count = String.valueOf(TimeUtils.getDiscrepantDays(date_start, date_end));

        } else if ((null != close_date || !close_date.equals("")) && isopen.equals("Y")) {
            String start_time = close_date;
            String end_time = Common.DATETIME_FORMAT.format(new Date());
            Date date_start = format.parse(start_time);
            Date date_end = format.parse(end_time);
            int date_count = TimeUtils.getDiscrepantDays(date_start, date_end);
            String open_date_count_sql = store.getOpen_date_count();
            int count1 = date_count + Integer.parseInt(open_date_count_sql);
            open_date_count = String.valueOf(count1);
        } else if ((null != close_date || !close_date.equals("")) && isopen.equals("N")) {
            open_date_count = store.getOpen_date_count();
        }
        store.setOpen_date_count(open_date_count);
        store.setOpen_date(open_date.replace(".0", ""));
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


    //分页显示所有店铺(有二维码)
    public PageInfo<Store> getAllStore(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value, String isactive, String search_area_code, String offline_area, String store_type, String dealer, String find_type) throws Exception {
        List<Store> shops;
        PageHelper.startPage(page_number, page_size);
        if (!search_area_code.equals(""))
            search_area_code = Common.SPECIAL_HEAD + search_area_code + ",";
        shops = storeMapper.selectAllStore(corp_code, search_value, isactive, search_area_code, offline_area, store_type, dealer, find_type,null);

        for (int i = 0; i < shops.size(); i++) {
            Store store = shops.get(i);
            getStoreArea(store);
            getStoreBrand(store);
            getStoreQrcode(store);
            shops.get(i).setIsactive(CheckUtils.CheckIsactive(shops.get(i).getIsactive()));
            if (null == store.getIsopen() || store.getIsopen().equals("")) {
                shops.get(i).setIsopen("N");
            }
        }
        conversion(shops);
        PageInfo<Store> page = new PageInfo<Store>(shops);
        return page;
    }

    //分页显示所有店铺(有二维码)
    public PageInfo<Store> getAllStore(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value, String isactive, String search_area_code, String offline_area, String store_type, String dealer, String find_type,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        List<Store> shops;
        PageHelper.startPage(page_number, page_size);
        if (!search_area_code.equals(""))
            search_area_code = Common.SPECIAL_HEAD + search_area_code + ",";
        shops = storeMapper.selectAllStore(corp_code, search_value, isactive, search_area_code, offline_area, store_type, dealer, find_type,manager_corp_arr);

        for (int i = 0; i < shops.size(); i++) {
            Store store = shops.get(i);
            getStoreArea(store);
            getStoreBrand(store);
            getStoreQrcode(store);
            shops.get(i).setIsactive(CheckUtils.CheckIsactive(shops.get(i).getIsactive()));
            if (null == store.getIsopen() || store.getIsopen().equals("")) {
                shops.get(i).setIsopen("N");
            }
        }
        conversion(shops);
        PageInfo<Store> page = new PageInfo<Store>(shops);
        return page;
    }

    //分页显示所有店铺(没有二维码)
    public PageInfo<Store> getAllStore1(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value, String isactive, String search_area_code, String offline_area, String store_type, String dealer, String find_type) throws Exception {
        List<Store> shops;
        PageHelper.startPage(page_number, page_size);
        if (!search_area_code.equals(""))
            search_area_code = Common.SPECIAL_HEAD + search_area_code + ",";
        shops = storeMapper.selectAllStore(corp_code, search_value, isactive, search_area_code, offline_area, store_type, dealer, find_type,null);

        for (int i = 0; i < shops.size(); i++) {
            Store store = shops.get(i);
            getStoreArea(store);
//            getStoreBrand(store);
        }
        PageInfo<Store> page = new PageInfo<Store>(shops);
        return page;
    }

    /**
     * 获取用户的店仓信息
     */
    @Override
    public PageInfo<Store> selectByUserId(int page_number, int page_size, String store_code, String corp_code, String search_value, String isactive) throws Exception {
        List<Store> shops;
        store_code = store_code.replace(Common.SPECIAL_HEAD, "");
        String[] ids = store_code.split(",");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_codes", ids);
        params.put("corp_code", corp_code);
        params.put("search_value", search_value);
        params.put("isactive", isactive);
        PageHelper.startPage(page_number, page_size);
        shops = storeMapper.selectByStoreCodes(params);

        for (int i = 0; i < shops.size(); i++) {
            Store store = shops.get(i);
            getStoreArea(store);
            getStoreBrand(store);
            getStoreQrcode(store);
            shops.get(i).setIsactive(CheckUtils.CheckIsactive(shops.get(i).getIsactive()));
            if (null == store.getIsopen() || store.getIsopen().equals("")) {
                shops.get(i).setIsopen("N");
            }
        }
        conversion(shops);
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
     * 根据多个店铺编号，获取店铺信息
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

    /***
     * 根据多个店铺编号，获取店铺信息(包括群组)
     */
    @Override
    public List<Store> selectByStoreCodes1(String store_code, String corp_code, String isactive) throws Exception {
        List<Store> stores = selectByStoreCodes(store_code, corp_code, Common.IS_ACTIVE_Y);

        for (int i = 0; i < stores.size(); i++) {
            Store store = stores.get(i);
            String area_code = store.getArea_code();
            String area_name = "";
            String area_code1 = "";
            if (area_code != null && !area_code.equals("")) {
                area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                String[] ids = area_code.split(",");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("corp_code", corp_code);
                map.put("area_codes", ids);
                List<Area> areas = areaMapper.selectArea(map);
                for (int j = 0; j < areas.size(); j++) {
                    Area area = areas.get(j);
                    if (area != null) {
                        String area_name1 = area.getArea_name();
                        area_name = area_name + area_name1 + ",";
                        area_code1 = area_code1 + area.getArea_code() + ",";
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
        }
        return stores;
    }


    //店铺下所属用户
    public List<User> getStoreUser(String corp_code, String store_code, String area_code, String role_code, String isactive) throws Exception {
        List<User> user = new ArrayList<User>();

        if (!store_code.equals("")) {
            user = userMapper.selectStoreUser(corp_code, Common.SPECIAL_HEAD + store_code + ",", "", role_code, isactive,"");
        }
        if (!area_code.equals("")) {
            user = userMapper.selectStoreUser(corp_code, "", Common.SPECIAL_HEAD + area_code + ",", role_code, isactive,"");
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


    //店铺下所属用户
    public PageInfo<User> getStoreUsers(int page_number,int page_size,String corp_code, String store_code, String area_code, String role_code, String isactive,String search_value) throws Exception {
        List<User> user = new ArrayList<User>();
        PageHelper.startPage(page_number, page_size);
        if (!store_code.equals("")) {
            user = userMapper.selectStoreUser(corp_code, Common.SPECIAL_HEAD + store_code + ",", "", role_code, isactive,search_value);
        }
        if (!area_code.equals("")) {
            user = userMapper.selectStoreUser(corp_code, "", Common.SPECIAL_HEAD + area_code + ",", role_code, isactive,search_value);
        }
        PageInfo<User> page = new PageInfo<User>(user);
        return page;
    }


    //根据企业，店铺编号,查找店铺
    public Store getStoreByCode(String corp_code, String store_code, String isactive) throws Exception {
        //System.out.println("===============corp======"+corp_code+store_code+isactive);
        List<Store> store = storeMapper.selectByCode(corp_code, store_code, isactive);
       // System.out.println("===============store======"+store.size());
        if (store.size() > 0){
            return store.get(0);
        }else {
            return null;
        }
    }

    //新增店铺
    @Override
    public String insert(String message, String user_id) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String result = Common.DATABEAN_CODE_ERROR;
        String store_code = jsonObject.get("store_code").toString().trim();
//        String store_id = jsonObject.get("store_id").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String store_name = jsonObject.get("store_name").toString().trim();
        String offline_area = jsonObject.get("offline_area").toString().trim();
        String dealer = "";
        String store_type = "";
        if (jsonObject.containsKey("dealer")) {
            dealer = jsonObject.get("dealer").toString().trim();
        }
        if (jsonObject.containsKey("store_type")) {
            store_type = jsonObject.get("store_type").toString().trim();
        }

        String logo = "";
        if (jsonObject.containsKey("logo"))
            logo = jsonObject.get("logo").toString().trim();
        Store store = getStoreByCode(corp_code, store_code, Common.IS_ACTIVE_Y);
        List<Store> store1 = getStoreByName(corp_code, store_name, Common.IS_ACTIVE_Y);
        if (store == null && store1.size() < 1) {
            Store shop = new Store();
            shop.setStore_code(store_code);
//            shop.setStore_id(store_id);
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

            if (jsonObject.containsKey("store_location") && !jsonObject.get("store_location").toString().equals("")) {
                String location = jsonObject.get("store_location").toString().trim();
                if (location != null && !location.equals("")) {
                    shop.setLat(location.split(",")[0]);
                    shop.setLng(location.split(",")[1]);
                }
            }
            Date now = new Date();
            shop.setCreated_date(Common.DATETIME_FORMAT.format(now));
            shop.setCreater(user_id);
            shop.setOffline_area(offline_area);

            shop.setDealer(dealer);
            shop.setStore_type(store_type);
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

    public String updateExecl(Store store) throws Exception {
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

        storeMapper.updateStore(store);
        return "upd success";
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

    /*
    包括二维码
    */
    @Override
    public PageInfo<Store> getAllStoreScreen(int page_number, int page_size, String corp_code, String area_codes, String brand_codes
            , String store_codes, Map<String, String> map, String area_store_codes, String isactive, String find_type) throws Exception {
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
        if (map != null){
            for (int i = 0; i < map.size(); i++) {
                if (map.containsKey("area_name") && !map.get("area_name").equals("")) {
                    flg = 1;
                }
                if (map.containsKey("brand_name") && !map.get("brand_name").equals("")) {
                    flg = 1;
                }
            }
        }
        params.put("corp_code", corp_code);
        params.put("area_codes", areas);
        params.put("brand_codes", brands);
        params.put("store_codes", stores);
        params.put("area_store_codes", area_stores);
        params.put("map", map);
        params.put("isactive", isactive);
        params.put("find_type", find_type);
        if(find_type.equals("user")){
            params.put("isactive", "Y");
        }
        List<Store> shops;
        if (flg == 1) {
            PageHelper.startPage(page_number, page_size);
            shops = storeMapper.selectAllStoreScreen(params);
        } else {
            PageHelper.startPage(page_number, page_size);
            shops = storeMapper.selectAllStoreScreenEasy(params);
        }
        for (int i = 0; i < shops.size(); i++) {
            Store store = shops.get(i);
            getStoreArea(store);
            getStoreBrand(store);
            getStoreQrcode(store);
            shops.get(i).setIsactive(CheckUtils.CheckIsactive(shops.get(i).getIsactive()));
            if (null == store.getIsopen() || store.getIsopen().equals("")) {
                shops.get(i).setIsopen("N");
            }
        }
        conversion(shops);
        PageInfo<Store> page = new PageInfo<Store>(shops);
        return page;
    }

    @Override
    public PageInfo<Store> getAllStoreScreen(int page_number, int page_size, String corp_code, String area_codes, String brand_codes
            , String store_codes, Map<String, String> map, String area_store_codes, String isactive, String find_type,String manager_corp) throws Exception {
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
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }

        params.put("corp_code", corp_code);
        params.put("area_codes", areas);
        params.put("brand_codes", brands);
        params.put("store_codes", stores);
        params.put("area_store_codes", area_stores);
        params.put("map", map);
        params.put("isactive", isactive);
        params.put("find_type", find_type);
        params.put("manager_corp_arr", manager_corp_arr);
        if(find_type.equals("user")){
            params.put("isactive", "Y");
        }
        List<Store> shops;
        if (flg == 1) {
            PageHelper.startPage(page_number, page_size);
            shops = storeMapper.selectAllStoreScreen(params);
        } else {
            PageHelper.startPage(page_number, page_size);
            shops = storeMapper.selectAllStoreScreenEasy(params);
        }
        for (int i = 0; i < shops.size(); i++) {
            Store store = shops.get(i);
            getStoreArea(store);
            getStoreBrand(store);
            getStoreQrcode(store);
            shops.get(i).setIsactive(CheckUtils.CheckIsactive(shops.get(i).getIsactive()));
            if (null == store.getIsopen() || store.getIsopen().equals("")) {
                shops.get(i).setIsopen("N");
            }
        }
        conversion(shops);
        PageInfo<Store> page = new PageInfo<Store>(shops);
        return page;
    }


    /*
   不包括二维码
   */
    @Override
    public PageInfo<Store> getAllStoreScreen1(int page_number, int page_size, String corp_code, String area_codes, String brand_codes
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
            Store store = shops.get(i);
            getStoreArea(store);
//            getStoreBrand(store);
        }
        PageInfo<Store> page = new PageInfo<Store>(shops);
        return page;
    }


    //修改店铺
    @Override
    public String update(String message, String user_id, String user_name) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = JSONObject.parseObject(message);
        int store_id = Integer.valueOf(jsonObject.get("id").toString().trim());
        String store_code = jsonObject.get("store_code").toString().trim();
//        String store_id1 = jsonObject.get("store_id").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String store_name = jsonObject.get("store_name").toString().trim();
        String offline_area = jsonObject.get("offline_area").toString().trim();
        String isactive_new = jsonObject.get("isactive").toString().trim();


        String dealer = "";
        String store_type = "";
        if (jsonObject.containsKey("dealer")) {
            dealer = jsonObject.get("dealer").toString().trim();
        }
        if (jsonObject.containsKey("store_type")) {
            store_type = jsonObject.get("store_type").toString().trim();
        }
        String logo = "";
        if (jsonObject.containsKey("logo"))
            logo = jsonObject.get("logo").toString().trim();

        Store store = getById(store_id);
        String isactive_old = store.getIsactive();
        String isopen_new = "N";
        if (jsonObject.containsKey("isopen")) {
            isopen_new = jsonObject.get("isopen").toString().trim();
        }
        String close_date_old = store.getClose_date();
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
//                store.setStore_id(store_id1);
                store.setStore_code(store_code);
                store.setStore_name(store_name);
                store.setOffline_area(offline_area);
                store.setDealer(dealer);
                store.setStore_type(store_type);
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

                if (jsonObject.containsKey("store_location") && !jsonObject.get("store_location").toString().trim().equals("")) {

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
                if(!isactive_new.equals(isactive_old) && isactive_new.equals("N")){
                    insertStoreStartOrEnd(store_id, "end", user_id, user_name);
                }else if(!isactive_new.equals(isactive_old) && isactive_new.equals("Y") && isopen_new.equals("Y")){
                    Store store_y = new Store();
                    store_y.setId(store_id);
                    store_y.setIsopen("Y");
                    store_y.setClose_date(Common.DATETIME_FORMAT.format(new Date()));
                    updateStore(store_y);
                    insertStoreStartOrEnd(store_id, "start", user_id, user_name);
                }else{
                    if (jsonObject.containsKey("isopen")) {
                        String isopen = jsonObject.get("isopen").toString().trim();
                        String type = "";
                        if (isopen.equals("Y")) {
                            type = "start";
                        } else {
                            type = "end";
                        }
                        insertStoreStartOrEnd(store_id, type, user_id, user_name);
                    }
                }
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
//                store.setStore_id(store_id1);
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
                if (jsonObject.containsKey("store_location")) {
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
                if(!isactive_new.equals(isactive_old) && isactive_new.equals("N")){
                    if(null!=close_date_old && !"".equals(close_date_old)){
                        Store store_y = new Store();
                        store_y.setId(store_id);
                      //  store_y.setIsopen("N");
                        store_y.setClose_date(Common.DATETIME_FORMAT.format(new Date()));
                        updateStore(store_y);
                    }
                    insertStoreStartOrEnd(store_id, "end", user_id, user_name);
                }else if(!isactive_new.equals(isactive_old) && isactive_new.equals("Y")&& isopen_new.equals("Y")){
                    Store store_y = new Store();
                    store_y.setId(store_id);
                    store_y.setIsopen("Y");
                    store_y.setClose_date(Common.DATETIME_FORMAT.format(new Date()));
                    updateStore(store_y);
                    insertStoreStartOrEnd(store_id, "start", user_id, user_name);
                }else{
                    if (jsonObject.containsKey("isopen")) {
                        String isopen = jsonObject.get("isopen").toString().trim();
                        String type = "";
                        if (isopen.equals("Y")) {
                            type = "start";
                        } else {
                            type = "end";
                        }
                        insertStoreStartOrEnd(store_id, type, user_id, user_name);
                    }
                }
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

    public int deleteStoreRelation(String corp_code, String store_code) throws Exception {
        List<CorpWechat> corpWechats = corpMapper.selectWByCorp(corp_code);
        for (int i = 0; i < corpWechats.size(); i++) {
            vipRelationMapper.deleteStoreVip(store_code, corpWechats.get(i).getApp_user_name());
        }
        return 0;
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
                                             String[] brand_code, String search_value, String isactive, String search_area_code, String offline_area, String store_type, String dealer, String find_type) throws Exception {
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

        params.put("offline_area", offline_area);
        params.put("store_type", store_type);
        params.put("dealer", dealer);
        params.put("find_type", find_type);
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
            if (null == store.getIsopen() || store.getIsopen().equals("")) {
                shops.get(i).setIsopen("N");
            }
        }
        conversion(shops);
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
                                                   String search_value, String area_store_code, String city, String find_type) throws Exception {
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
        params.put("find_type", find_type);
        params.put("isactive", "Y");
        PageHelper.startPage(page_number, page_size);
        List<Store> stores = storeMapper.selStoreByAreaBrand(params);
        PageInfo<Store> page = new PageInfo<Store>(stores);
        return page;
    }

    @Override
    public PageInfo<Store> selStoreByAreaBrandCity(int page_number, int page_size, String corp_code, String area_code, String brand_code,
                                                   String search_value, String area_store_code, String city, String find_type,String manager_corp) throws Exception {
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
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        params.put("manager_corp_arr", manager_corp_arr);
        params.put("search_value", search_value);
        params.put("find_type", find_type);
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

    @Override
    public List<Store> selStoreByAreaBrandCode(String corp_code, String area_code, String brand_code,
                                               String search_value, String area_store_code,String manager_corp) throws Exception {
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
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        params.put("manager_corp_arr", manager_corp_arr);
        params.put("search_value", search_value);
        params.put("isactive", "Y");
        List<Store> stores = storeMapper.selStoreByAreaBrand(params);
        return stores;
    }

    @Override
    public List<Store> selStoreByAreaBrandCode1(String corp_code, String area_code, String brand_code,
                                                String search_value, String area_store_code,String isactive) throws Exception {
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
        params.put("isactive", isactive);
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
        List<CorpWechat> corpWechats = corpMapper.selectWByCorp(corp_code);
        for (int i = 0; i < corpWechats.size(); i++) {
            vipRelationMapper.deleteStoreVip(store_code, corpWechats.get(i).getApp_user_name());
        }
        return storeMapper.deleteStoreQrcode(corp_code, store_code);
    }

    public int deleteStoreQrcodeOne(String corp_code, String store_code, String app_id) throws Exception {
        CorpWechat corpWechat = corpMapper.selectWByAppId(corp_code,app_id);
        if (corpWechat != null) {
            vipRelationMapper.deleteStoreVip(store_code, corpWechat.getApp_user_name());
        }
        return storeMapper.deleteStoreQrcodeOne(corp_code, store_code, app_id);
    }

    public List<StoreQrcode> selectStoreQrcodeByApp(String corp_code, String store_code, String auth_appid) throws Exception{
        List<StoreQrcode> storeQrcodes = storeMapper.selectByStoreApp(corp_code, store_code, auth_appid);
        return  storeQrcodes;
    }


    public String creatStoreQrcode(String corp_code, String store_code, String auth_appid, String user_id) throws Exception {
        List<StoreQrcode> storeQrcodes = storeMapper.selectByStoreApp(corp_code, store_code, auth_appid);
        String picture = "";
        if (storeQrcodes.size() < 1) {
            deleteStoreQrcodeOne(corp_code, store_code, auth_appid);
            String url = CommonValue.wechat_url + "/creatQrcode?auth_appid=" + auth_appid + "&prd=ishop&src=s&store_id=" + store_code;
            String result = IshowHttpClient.get(url);
            logger.info("------------creatQrcode  result" + result);
            if (!result.startsWith("{")) {
                return Common.DATABEAN_CODE_ERROR;
            }
            JSONObject obj = JSONObject.parseObject(result);
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

                Store store=new Store();
                store.setCorp_code(corp_code);
                store.setStore_code(store_code);
                store.setModified_date(Common.DATETIME_FORMAT.format(now));
                store.setModifier(user_id);
                storeMapper.updateStoreByCode(store);
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
        if (map != null){
            for (int i = 0; i < map.size(); i++) {
                if (map.containsKey("area_name") && !map.get("area_name").equals("")) {
                    flg = 1;
                }
                if (map.containsKey("brand_name") && !map.get("brand_name").equals("")) {
                    flg = 1;
                }
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
        return shops;
    }

    public List<Store> getStoreByBrandCode(String corp_code, String area_codes, String brand_codes
            , String store_codes, Map<String, String> map, String area_store_codes, String isactive,String manager_corp) throws Exception {
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
        if (map != null){
            for (int i = 0; i < map.size(); i++) {
                if (map.containsKey("area_name") && !map.get("area_name").equals("")) {
                    flg = 1;
                }
                if (map.containsKey("brand_name") && !map.get("brand_name").equals("")) {
                    flg = 1;
                }
            }
        }
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        params.put("manager_corp_arr", manager_corp_arr);
        params.put("corp_code", corp_code);
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

    @Override
    public List<Store> getStoreByOdsType(String corp_code, String line_code) throws Exception {
        return storeMapper.getStoreByOdsType(corp_code, line_code);
    }

    public void insertStoreStartOrEnd(int id, String type, String user_code, String user_name) throws Exception {
    //    Date now = new Date();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection collection = mongoTemplate.getCollection(CommonValue.table_store_start_end_log);
        Store storeById = this.getStoreById(id);
        String corp_name = storeById.getCorp_name();
        String corp_code = storeById.getCorp_code();
        String store_code = storeById.getStore_code();
        String store_id = storeById.getStore_id();
        String store_name = storeById.getStore_name();
        String isopen = storeById.getIsopen();
        String open_date = storeById.getOpen_date();
        String close_date = storeById.getClose_date();
        String open_date_count = "0";
        if (null == open_date || "".equals(open_date)) {
            open_date = Common.DATETIME_FORMAT.format(new Date());
        }
        if ((null == close_date || "".equals(close_date)) && isopen.equals("Y")) {
        //    System.out.println("-----(null == close_date || \"\".equals(close_date)) && isopen.equals(\"Y\")--open_date--------"+open_date);
            String start_time = open_date;
            String end_time = Common.DATETIME_FORMAT.format(new Date());

            Date date_start = format.parse(start_time);
            Date date_end = format.parse(end_time);
            open_date_count = String.valueOf(TimeUtils.getDiscrepantDays(date_start, date_end));
            System.out.println("------open_date_count---------------"+open_date_count);
        } else if (null != close_date && !close_date.equals("") && isopen.equals("Y")) {
            String start_time = close_date;
            String end_time = Common.DATETIME_FORMAT.format(new Date());
            Date date_start = format.parse(start_time);
            Date date_end = format.parse(end_time);
            int date_count = TimeUtils.getDiscrepantDays(date_start, date_end);
            String open_date_count_sql = storeById.getOpen_date_count();
            int count1 = date_count + Integer.parseInt(open_date_count_sql);
            open_date_count = String.valueOf(count1);
        } else if (null != close_date && !close_date.equals("") && isopen.equals("N")) {
            open_date_count = storeById.getOpen_date_count();
        }
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        values.add(new BasicDBObject("corp_code", corp_code));
        values.add(new BasicDBObject("store_code", store_code));
        values.add(new BasicDBObject("is_open", "Y"));
        queryCondition.put("$and", values);

        DBCursor dbCursor = collection.find(queryCondition);
        if ("start".equals(type)) {
            String start_time = Common.DATETIME_FORMAT_DAY.format(new Date());
            String end_time = "";
            int time_count = 0;
            if (dbCursor.hasNext() == false) {
                DBObject saveData = new BasicDBObject();
                saveData.put("corp_code", corp_code);
                saveData.put("corp_name", corp_name);
                saveData.put("store_code", store_code);
                saveData.put("store_id", store_id);
                saveData.put("store_name", store_name);
                saveData.put("start_time", start_time);
                saveData.put("end_time", end_time);
                saveData.put("time_count", time_count);
                saveData.put("is_open", "Y");
                saveData.put("created_date", Common.DATETIME_FORMAT.format(new Date()));
                saveData.put("creater_code", user_code);
                saveData.put("creater_name", user_name);
                saveData.put("modified_date", Common.DATETIME_FORMAT.format(new Date()));
                saveData.put("modifier_code", user_code);
                saveData.put("modifier_name", user_name);
                collection.insert(saveData);
            }
            Store store = new Store();
            store.setId(id);
            store.setOpen_person(user_code);
            store.setOpen_date(open_date);
            if(null!=close_date && !close_date.equals("")){
                store.setClose_date(Common.DATETIME_FORMAT.format(new Date()));
            }
            store.setIsopen("Y");
            updateStore(store);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


            String start_time = "";
            String end_time = Common.DATETIME_FORMAT.format(new Date());
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                start_time = obj.get("start_time").toString();
            }
            if (start_time.equals("")) {
                start_time = open_date;
            }
            Date date_start = format.parse(start_time);
            Date date_end = format.parse(end_time);
            int time_count = TimeUtils.getDiscrepantDays(date_start, date_end);

            System.out.println(open_date_count + "---店铺使用时间----" + time_count);
            DBObject updatedValue = new BasicDBObject();
            updatedValue.put("end_time", Common.DATETIME_FORMAT_DAY.format(new Date()));
            updatedValue.put("time_count", time_count);
            updatedValue.put("is_open", "N");
            updatedValue.put("modified_date", Common.DATETIME_FORMAT.format(new Date()));
            updatedValue.put("modifier_code", user_code);
            updatedValue.put("modifier_name", user_name);
            DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
            collection.update(queryCondition, updateSetValue);


//            BasicDBObject queryCondition_day = new BasicDBObject();
//            BasicDBList values_day = new BasicDBList();
//            values_day.add(new BasicDBObject("corp_code", corp_code));
//            values_day.add(new BasicDBObject("store_code", store_code));
//            queryCondition_day.put("$and", values_day);
//
//            DBCursor dbCursor_day = collection.find(queryCondition_day);
//            int time_count_day=0;
//            while (dbCursor_day.hasNext()) {
//                DBObject obj = dbCursor.next();
//                time_count_day += Integer.parseInt(obj.get("time_count").toString());
//            }
            Store store = new Store();
            store.setId(id);
            store.setIsopen("N");
            store.setOpen_date_count(String.valueOf(open_date_count));
            store.setClose_date(Common.DATETIME_FORMAT.format(new Date()));
            updateStore(store);
        }
    }


    public void getStoreBrand(Store store) throws Exception {
        /*StringBuilder brand_name = new StringBuilder("");
        StringBuilder brand_code1 = new StringBuilder("");
        String brand_code = store.getBrand_code();
        String corp_code = store.getCorp_code();

        if (brand_code != null && !brand_code.equals("")) {
            brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
            String[] ids = brand_code.split(",");
            if(ids.length>0) {
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
                        brand_code1.append(brand.getBrand_code() + ",");
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
            }else{
                store.setBrand_name("");
                store.setBrand_code("");
            }
        } else {
            store.setBrand_name("");
            store.setBrand_code("");
        }*/
    	
    	 StringBuilder brand_name = new StringBuilder("");
         StringBuilder brand_code1 = new StringBuilder("");
         String brand_code = store.getBrand_code();
         String corp_code = store.getCorp_code();

         if (brand_code != null && !brand_code.equals("")) {
         	//  SPECIAL_HEAD = "§"
         	brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
             String[] ids = brand_code.split(",");
             
             if(ids.length==0) {
             	 store.setBrand_name("");
                  store.setBrand_code("");
                  return;
             }
             
             Map<String, Object> map = new HashMap<String, Object>();
             map.put("corp_code", corp_code);
             map.put("brand_code", ids);
             map.put("search_value", "");
             List<Brand> brands = brandMapper.selectBrands(map);
                                                                    
             //爱帛服饰  单独进行品牌名称的转化
         	if("C10183".equals(corp_code) && CollectionUtils.isEmpty(brands)) {
         		 store.setBrand_name("MO复合店");
                  store.setBrand_code(brand_code);
                  return;
         	}
         		 if(ids.length>0) {
                      for (int i = 0; i < brands.size(); i++) {
                          Brand brand = brands.get(i);
                          if (brand != null) {
                              String brand_name1 = brand.getBrand_name();
                              brand_name.append(brand_name1 + ",");
                              brand_code1.append(brand.getBrand_code() + ",");
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
                      return;
                  }
         		         		        		 	
         } else {
             store.setBrand_name("");
             store.setBrand_code("");
             return;
         }
    	
    	
    }

    public void getStoreArea(Store store) throws Exception {
        StringBuilder area_name = new StringBuilder("");
        StringBuilder area_code1 = new StringBuilder("");
        String area_code = store.getArea_code();
        String corp_code = store.getCorp_code();

        if (area_code != null && !area_code.equals("")) {
            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
            String[] ids = area_code.split(",");
            if(ids.length>0) {
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
            }else{
                store.setArea_code("");
                store.setArea_name("");
            }
        } else {
            store.setArea_code("");
            store.setArea_name("");
        }
    }

    public void getStoreQrcode(Store store) throws Exception {
        List<StoreQrcode> qrcodeList = storeMapper.selectByStoreCode(store.getCorp_code(), store.getStore_code());
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
        store.setQrcode(qrcode.toString());
    }

    public void conversion(List<Store> shops) throws Exception {
        for (int i = 0; i < shops.size(); i++) {
            Store store = shops.get(i);
            getStoreBrand(store);
            getStoreArea(store);

            getStoreQrcode(store);
            shops.get(i).setIsactive(CheckUtils.CheckIsactive(shops.get(i).getIsactive()));
            if (null == store.getIsopen() || store.getIsopen().equals("")) {
                shops.get(i).setIsopen("N");
            }
            String isopen = store.getIsopen();
            String open_date = store.getOpen_date();
            String close_date = store.getClose_date();
            String isactive = store.getIsactive();
            String open_date_count = "0";

            if (null == open_date || "".equals(open_date)) {
                open_date = Common.DATETIME_FORMAT.format(new Date());
            }
            if(isactive.equals("Y")|| isactive.equals("是")) {
                if ((null == close_date || "".equals(close_date)) && isopen.equals("Y")) {
                    String start_time = open_date;
                    String end_time = Common.DATETIME_FORMAT.format(new Date());

                    Date date_start = format.parse(start_time);
                    Date date_end = format.parse(end_time);
                    open_date_count = String.valueOf(TimeUtils.getDiscrepantDays(date_start, date_end));

                } else if ((null != close_date || !close_date.equals("")) && isopen.equals("Y")) {
                    String start_time = close_date;
                    String end_time = Common.DATETIME_FORMAT.format(new Date());
                    Date date_start = format.parse(start_time);
                    Date date_end = format.parse(end_time);
                    int date_count = TimeUtils.getDiscrepantDays(date_start, date_end);
                    String open_date_count_sql = store.getOpen_date_count();
                    int count1 = date_count + Integer.parseInt(open_date_count_sql);
                    open_date_count = String.valueOf(count1);
                } else if ((null != close_date || !close_date.equals("")) && isopen.equals("N")) {
                    open_date_count = store.getOpen_date_count();
                }
            }else{
                open_date_count = store.getOpen_date_count();
            }
            shops.get(i).setOpen_date_count(open_date_count);
            //       System.out.println("------open_date_count-------"+open_date_count);
        }
    }

    public List<Store> getAllStoreByCount() throws Exception {
        return storeMapper.getAllStoreByCount();
    }

    @Override
    public int updStoreDayCount() {
        return storeMapper.updStoreDayCount();
    }

    @Override
    public int updStoreTime(String corp_code) {
        return storeMapper.updStoreTime(corp_code);
    }

    @Override
    public List<Store> getStoreByNameTwo(String corp_code, String store_name, String isactive) throws Exception {
        return storeMapper.selectByStoreNameTwo(corp_code,store_name,isactive);
    }

    // 取品牌和店铺群组的交集A，取A和store_codes的并集
    public List<Store> getStoreAndBrandArea(String corp_code, String area_codes, String brand_codes, String store_codes, String area_store_codes) throws Exception {
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
        params.put("corp_code", corp_code);
        params.put("area_codes", areas);
        params.put("brand_codes", brands);
        params.put("store_codes", stores);
        params.put("area_store_codes", area_stores);
        List<Store> shops = storeMapper.getStoreAndBrandArea(params);

        return shops;
    }

    @Override
    public List<Store> selectCorpCanSearch(String corp_code, String search_value) throws Exception {
        return storeMapper.selectCorpCanSearch(corp_code,search_value);
    }

    @Override
    public List<StoreQrcode> selctStoreQrcode(String corp_code, String store_code, String app_id) throws Exception {
        List<StoreQrcode> list=storeMapper.selctStoreQrcode(corp_code,store_code,app_id);
        return list;
    }

    @Override
    public  StoreQrcode selectAppIdByQrcode(String corp_code, String store_code, String qrcode) throws Exception{

        return storeMapper.selectByStoreQrcode(corp_code,store_code,qrcode);
    }
}
