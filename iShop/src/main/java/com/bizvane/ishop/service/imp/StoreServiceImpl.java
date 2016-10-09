package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
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
    public int deleteStoreUser(String user_id, String store_code) throws Exception{
        store_code = Common.SPECIAL_HEAD + store_code + ",";
        return storeMapper.deleteStoreUser(user_id, store_code);
    }


    //根据id获取店铺信息
    @Override
    public Store getStoreById(int id) throws Exception {
        Store store = storeMapper.selectByStoreId(id);
        String corp_code = store.getCorp_code();

        StringBuilder brand_name = new StringBuilder("");
        StringBuilder area_name = new StringBuilder("");
        String brand_code = store.getBrand_code();
        String area_code = store.getArea_code();

        processStoreToSpecial(store);

        if (brand_code != null && !brand_code.equals("")) {
            brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
            String[] ids = brand_code.split(",");
            for (int i = 0; i < ids.length; i++) {
                Brand brand = brandMapper.selectByBrandCode(corp_code, ids[i],Common.IS_ACTIVE_Y);
                if (brand != null) {
                    String brand_name1 = brand.getBrand_name();
                    brand_name.append(brand_name1);
                    if (i != ids.length - 1) {
                        brand_name.append(",");
                    }
                }
            }
            store.setBrand_name(brand_name.toString());
            if (brand_code.endsWith(","))
                brand_code = brand_code.substring(0,brand_code.length()-1);
            store.setBrand_code(brand_code);
        }else {
            store.setBrand_name("");
            store.setBrand_code("");
        }
        if (area_code != null && !area_code.equals("")) {
            area_code = area_code.replace(Common.SPECIAL_HEAD,"");
            String[] ids = area_code.split(",");
            for (int i = 0; i < ids.length; i++) {
                Area area = areaMapper.selectAreaByCode(corp_code,ids[i],Common.IS_ACTIVE_Y);
                if (area != null) {
                    String area_name1 = area.getArea_name();
                    area_name.append(area_name1);
                    if (i != ids.length - 1) {
                        area_name.append(",");
                    }
                }
            }
            store.setArea_name(area_name.toString());
            if (area_code.endsWith(","))
                area_code = area_code.substring(0,area_code.length()-1);
            store.setArea_code(area_code);
        }else {
            store.setArea_code("");
            store.setArea_name("");
        }
        List<StoreQrcode> qrcodeList = storeMapper.selectByStoreCode(corp_code,store.getStore_code());
        store.setQrcodeList(qrcodeList);
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

        for (int i=0;i<shops.size();i++) {
            Store store = getStoreById(shops.get(i).getId());
            if (store.getBrand_name()!=null) {
                shops.get(i).setBrand_name(store.getBrand_name());
            }else {
                shops.get(i).setBrand_name("");
            }
            if (store.getArea_name()!=null) {
                shops.get(i).setArea_name(store.getArea_name());
            }else {
                shops.get(i).setArea_name("");
            }
            List<StoreQrcode> qrcodeList = store.getQrcodeList();
            StringBuilder qrcode = new StringBuilder("");
            for(int j=0;j<qrcodeList.size();j++){
                if(qrcodeList.get(j)!=null){
                    String qrcode1 = qrcodeList.get(j).getQrcode();
                    qrcode.append(qrcode1);
                    if (j != qrcodeList.size() - 1) {
                        qrcode.append("、");
                    }
                }
            }
//            for (StoreQrcode storeQrcode:qrcodeList) {
//                if (storeQrcode != null) {
//                    String qrcode1 = storeQrcode.getQrcode();
//                    qrcode.append(qrcode1);
//                    qrcode.append("、");
//                }
//            }
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
    public PageInfo<Store> selectByUserId(int page_number, int page_size, String store_code, String corp_code, String search_value) throws Exception{
        List<Store> shops;
        store_code = store_code.replace(Common.SPECIAL_HEAD,"");
        String[] ids = store_code.split(",");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("store_codes", ids);
        params.put("corp_code", corp_code);
        params.put("search_value", search_value);
        params.put("isactive", "");
        PageHelper.startPage(page_number, page_size);
        shops = storeMapper.selectByUserId(params);

        for (int i=0;i<shops.size();i++) {
            Store store = getStoreById(shops.get(i).getId());
            if (store.getBrand_name()!=null) {
                shops.get(i).setBrand_name(store.getBrand_name());
            }else {
                shops.get(i).setBrand_name("");
            }
            if (store.getArea_name()!=null) {
                shops.get(i).setArea_name(store.getArea_name());
            }else {
                shops.get(i).setArea_name("");
            }
            List<StoreQrcode> qrcodeList = store.getQrcodeList();
            StringBuilder qrcode = new StringBuilder("");
            for(int j=0;j<qrcodeList.size();j++){
                if(qrcodeList.get(j)!=null){
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
        store_code = store_code.replace(Common.SPECIAL_HEAD,"");
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
    public List<Store> selectAll(String store_code, String corp_code, String isactive) throws Exception{
        if (store_code.contains(Common.SPECIAL_HEAD))
            store_code = store_code.replace(Common.SPECIAL_HEAD,"");
        String[] ids = store_code.split(",");
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
            user = userMapper.selectStoreUser(corp_code, Common.SPECIAL_HEAD +store_code + ",", "", role_code,isactive);
        }
        if (!area_code.equals("")){
            user = userMapper.selectStoreUser(corp_code, "", Common.SPECIAL_HEAD +area_code + ",", role_code,isactive);
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

    @Override
    public Store selStoreByStroeId(String corp_code, String store_id, String isactive) throws Exception {
        return storeMapper.selStoreByStroeId(corp_code,store_id,isactive);
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
        Store store = getStoreByCode(corp_code, store_code, Common.IS_ACTIVE_Y);
        Store store1 = getStoreByName(corp_code, store_name, Common.IS_ACTIVE_Y);
        if (store == null && store1 == null) {
            Store shop = new Store();
            shop.setStore_code(store_code);
            if (store_id.equals("")){
                shop.setStore_id(store_code);
            }else {
                shop.setStore_id(store_id);
            }
            shop.setStore_name(store_name);
            shop.setArea_code(jsonObject.get("area_code").toString().trim());
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
        String brand_code = store.getBrand_code();
        String[] codes = brand_code.split(",");
        String brand_code1 = "";
        for (int i = 0; i < codes.length; i++) {
            codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
            brand_code1 = brand_code1 + codes[i];
        }
        store.setBrand_code(brand_code1);
        storeMapper.insertStore(store);
        return "add success";
    }

    @Override
    public PageInfo<Store> getAllStoreScreen(int page_number, int page_size, String corp_code, String area_codes, String brand_codes,String store_codes, Map<String, String> map) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        String[] areas = null;
        String[] brands = null;
        String[] stores = null;
        if (!area_codes.equals("")) {
            area_codes = area_codes.replace(Common.SPECIAL_HEAD,"");
            areas = area_codes.split(",");
            for (int i = 0; i < areas.length; i++) {
                areas[i] = Common.SPECIAL_HEAD+areas[i]+",";
            }
        }
        if (!brand_codes.equals("")) {
            brand_codes = brand_codes.replace(Common.SPECIAL_HEAD,"");
            brands = brand_codes.split(",");
            for (int i = 0; i < brands.length; i++) {
                brands[i] = Common.SPECIAL_HEAD+brands[i]+",";
            }
        }
        if (!store_codes.equals("")) {
            stores = store_codes.split(",");
            for (int i = 0; stores != null && i < stores.length; i++) {
                stores[i] = stores[i].substring(1, stores[i].length());
            }
        }
        params.put("corp_code", corp_code);
        params.put("area_codes", areas);
        params.put("brand_codes", brands);
        params.put("store_codes", stores);
        params.put("map", map);

        PageHelper.startPage(page_number, page_size);
        List<Store> shops = storeMapper.selectAllStoreScreen(params);


        for (int i=0;i<shops.size();i++) {
            Store store = getStoreById(shops.get(i).getId());
            if (store.getBrand_name()!=null) {
                shops.get(i).setBrand_name(store.getBrand_name());
            }else {
                shops.get(i).setBrand_name("");
            }
            if (store.getArea_name()!=null) {
                shops.get(i).setArea_name(store.getArea_name());
            }else {
                shops.get(i).setArea_name("");
            }
            List<StoreQrcode> qrcodeList = store.getQrcodeList();
            StringBuilder qrcode = new StringBuilder("");
            for(int j=0;j<qrcodeList.size();j++){
                if(qrcodeList.get(j)!=null){
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
        int store_id = Integer.valueOf(jsonObject.get("id").toString().trim());
        String store_code = jsonObject.get("store_code").toString().trim();
        String store_id1 = jsonObject.get("store_id").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String store_name = jsonObject.get("store_name").toString().trim();

        Store store = getStoreById(store_id);
        Store store1 = getStoreByCode(corp_code, store_code, Common.IS_ACTIVE_Y);
        Store store2 = getStoreByName(corp_code, store_name,Common.IS_ACTIVE_Y);
        if (store.getCorp_code().trim().equalsIgnoreCase(corp_code)) {
            if (store1 != null && store_id != store1.getId()){
                result = "店铺编号已存在";
            }else if (store2 != null && store_id != store2.getId()) {
                result = "店铺名称已存在";
            }else {
                if (!store.getStore_code().trim().equalsIgnoreCase(store_code)) {
                    updateCauseCodeChange(corp_code, store_code, store.getStore_code());
                }
                store = new Store();
                store.setId(store_id);
                if (store_id1.equals("")) {
                    store.setStore_id(store_code);
                }else {
                    store.setStore_id(store_id1);
                }
                store.setStore_code(store_code);
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
                Date now = new Date();
                store.setModified_date(Common.DATETIME_FORMAT.format(now));
                store.setModifier(user_id);
                store.setIsactive(jsonObject.get("isactive").toString());
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
                }else {
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
    public Store storeIdExist(String corp_code, String store_id) throws Exception {
        Store store = this.storeMapper.selStoreByStroeId(corp_code, store_id,Common.IS_ACTIVE_Y);
        return store;
    }

    @Override
    public int selectAchCount(String corp_code, String store_code) throws Exception {
        return this.storeMapper.selectAchCount(corp_code, store_code);
    }

    @Override
    public PageInfo<Store> selectByAreaBrand(int page_number, int page_size, String corp_code, String[] area_code, String[] brand_code, String search_value) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", area_code);
        params.put("brand_code", brand_code);
        params.put("search_value", search_value);
        params.put("isactive", "");
        PageHelper.startPage(page_number, page_size);
        List<Store> shops = storeMapper.selectByAreaBrand(params);

        for (int i=0;i<shops.size();i++) {
            Store store = getStoreById(shops.get(i).getId());
            if (store.getBrand_name()!=null) {
                shops.get(i).setBrand_name(store.getBrand_name());
            }else {
                shops.get(i).setBrand_name("");
            }
            if (store.getArea_name()!=null) {
                shops.get(i).setArea_name(store.getArea_name());
            }else {
                shops.get(i).setArea_name("");
            }
            List<StoreQrcode> qrcodeList = store.getQrcodeList();
            StringBuilder qrcode = new StringBuilder("");
            for(int j=0;j<qrcodeList.size();j++){
                if(qrcodeList.get(j)!=null){
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
    public PageInfo<Store> selStoreByAreaBrandCode(int page_number, int page_size, String corp_code, String area_code, String brand_code, String search_value) throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", "");
        params.put("brand_code", "");
        if (!area_code.equals("")){
            String[] areaCodes = area_code.split(",");
            for (int i = 0; i < areaCodes.length; i++) {
                areaCodes[i] = Common.SPECIAL_HEAD +areaCodes[i]+",";
            }
            params.put("area_code", areaCodes);
        }
        if (!brand_code.equals("")){
            String[] brandCodes = brand_code.split(",");
            for (int i = 0; i < brandCodes.length; i++) {
                brandCodes[i] = Common.SPECIAL_HEAD +brandCodes[i]+",";
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
    public List<Store> selStoreByAreaBrandCode(String corp_code, String area_code, String brand_code, String search_value) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", "");
        params.put("brand_code", "");
        if (!area_code.equals("")){
            String[] areaCodes = area_code.split(",");
            for (int i = 0; i < areaCodes.length; i++) {
                areaCodes[i] = Common.SPECIAL_HEAD +areaCodes[i]+",";
            }
            params.put("area_code", areaCodes);
        }
        if (!brand_code.equals("")){
            String[] brandCodes = brand_code.split(",");
            for (int i = 0; i < brandCodes.length; i++) {
                brandCodes[i] = Common.SPECIAL_HEAD +brandCodes[i]+",";
            }
            params.put("brand_code", brandCodes);
        }
        params.put("search_value", search_value);
        params.put("isactive", "Y");
        List<Store> stores = storeMapper.selStoreByAreaBrand(params);
        return stores;
    }

    public List<Store> selectByAreaBrand(String corp_code, String[] area_code, String isactive) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_code", area_code);
        params.put("search_value", "");
        params.put("isactive", isactive);
        List<Store> stores = storeMapper.selectByAreaBrand(params);
        return stores;
    }

    public List<Store> selectStoreCountByArea(String corp_code, String area_code, String isactive) throws Exception{
        String[] area_codes = area_code.split(",");
        for (int i = 0; i < area_codes.length; i++) {
            area_codes[i] = Common.SPECIAL_HEAD+area_codes[i]+",";
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code",corp_code);
        params.put("array",area_codes);
        params.put("isactive",isactive);
        List<Store> stores = storeMapper.selectStoreCountByArea(params);
        return stores;
    }

    public List<Store> selectStoreCountByBrand(String corp_code, String brand_code,String search_value, String isactive) throws Exception{
        String[] brand_codes = brand_code.split(",");
        for (int i = 0; i < brand_codes.length; i++) {
            brand_codes[i] = Common.SPECIAL_HEAD+brand_codes[i]+",";
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code",corp_code);
        params.put("array",brand_codes);
        params.put("search_value",search_value);
        params.put("isactive",isactive);
        List<Store> stores = storeMapper.selectStoreCountByBrand(params);
        return stores;
    }

    public PageInfo<Store> selectStoreByBrand(int page_number, int page_size,String corp_code, String brand_code,String search_value, String isactive) throws Exception{
        String[] brand_codes = brand_code.split(",");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code",corp_code);
        params.put("array",Common.SPECIAL_HEAD +brand_codes+",");
        params.put("search_value",search_value);
        params.put("isactive",isactive);
        PageHelper.startPage(page_number, page_size);
        List<Store> stores = storeMapper.selectStoreCountByBrand(params);
        PageInfo<Store> page = new PageInfo<Store>(stores);
        return page;
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
        String new_store_code1 = Common.SPECIAL_HEAD + new_store_code + ",";
        String old_store_code1 = Common.SPECIAL_HEAD + old_store_code + ",";
        codeUpdateMapper.updateUser("", corp_code, "", "", new_store_code1, old_store_code1, "", "");
        //更新员工详细信息
        codeUpdateMapper.updateStaffDetailInfo("", corp_code, "", "", new_store_code1, old_store_code1);
        //删除二维码
        storeMapper.deleteStoreQrcode(corp_code,old_store_code);
    }


    public int deleteStoreQrcode(String corp_code,String store_code) throws Exception{
        return storeMapper.deleteStoreQrcode(corp_code,store_code);
    }

    public int deleteStoreQrcodeOne(String corp_code, String store_code, String app_id) throws Exception{
        return storeMapper.deleteStoreQrcodeOne(corp_code,store_code,app_id);
    }

    public String creatStoreQrcode(String corp_code,String store_code,String auth_appid,String user_id) throws Exception{
        StoreQrcode storeQrcode = storeMapper.selectByStoreApp(corp_code,store_code,auth_appid);
        String picture ="";
        if (storeQrcode == null) {
            String url = "http://wechat.app.bizvane.com/app/wechat/creatQrcode?auth_appid=" + auth_appid + "&prd=ishop&src=s&store_id=" + store_code;
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
                storeQrcode = new StoreQrcode();
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
        }else {
            picture = storeQrcode.getQrcode();
        }
        return picture;
    }

    @Override
    public List<Store> selectStore(String corp_code, String store_codes) throws SQLException {
        String[] storeArray = null;
        if (null != store_codes && !store_codes.isEmpty()) {
            if (store_codes.contains(Common.SPECIAL_HEAD))
                store_codes = store_codes.replace(Common.SPECIAL_HEAD, "");
            storeArray = store_codes.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("store_codes", storeArray);
        List<Store> stores = storeMapper.selectStore(params);
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
}
