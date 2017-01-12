package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.*;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.CorpWechat;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.BrandService;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by ZhouZhou on 2016/6/4.
 */
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    CorpMapper corpMapper;
    @Autowired
    CorpService corpService;
    @Autowired
    BrandMapper brandMapper;
    @Autowired
    StoreMapper storeMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    private CodeUpdateMapper codeUpdateMapper;

    @Override
    public Brand getBrandById(int id) throws Exception {
        Brand brand = brandMapper.selectByBrandId(id);
        if (brand != null) {
            String cus_user_code = brand.getCus_user_code();
            List<JSONObject> array_user = new ArrayList<JSONObject>();
            String app_id = "";
            String app_name = "";

            if (cus_user_code != null && !cus_user_code.equals("")) {
                String[] cus_user_codes = cus_user_code.split(",");
                for (int i = 0; i < cus_user_codes.length; i++) {
                    String user_code = cus_user_codes[i];
                    List<User> user = userMapper.selectUserCode(user_code, brand.getCorp_code(), Common.IS_ACTIVE_Y);
                    if (user.size() > 0) {
                        JSONObject userObj = new JSONObject();
                        userObj.put("cus_user_code", user_code);
                        userObj.put("cus_user_name", user.get(0).getUser_name());
                        array_user.add(userObj);
                    }
                }
            }
            brand.setCus_user(array_user);
            List<CorpWechat> corpWechats = corpService.selectWByCorpBrand(brand.getCorp_code(), Common.SPECIAL_HEAD+brand.getBrand_code()+",");
            if (corpWechats.size() > 0) {
                for (int i = 0; i < corpWechats.size(); i++) {
                    String app_id1 = corpWechats.get(i).getApp_id();
                    String app_name1 = corpWechats.get(i).getApp_name();
                    app_id = app_id + app_id1 + ",";
                    app_name = app_name + app_name1 + ",";
                }
                app_id = app_id.substring(0, app_id.length() - 1);
                app_name = app_name.substring(0, app_name.length() - 1);
            }
            brand.setApp_id(app_id);
            brand.setApp_name(app_name);
        }
        return brand;
    }

    @Override
    public Brand getBrandByCode(String corp_code, String brand_code,String isactive) throws SQLException {
        return brandMapper.selectByBrandCode(corp_code, brand_code,isactive);
    }

    @Override
    public Brand getBrandByName(String corp_code, String brand_name,String isactive) throws Exception{
        Brand brand = brandMapper.selectByBrandName(corp_code, brand_name,isactive);
        return brand;
    }

    @Override
    public PageInfo<Brand> getAllBrandByPage(int page_number, int page_size, String corp_code, String search_value) throws SQLException {
        List<Brand> brands;
        PageHelper.startPage(page_number, page_size);
        brands = brandMapper.selectAllBrand(corp_code, search_value);
        for (Brand brand:brands) {
            brand.setIsactive(CheckUtils.CheckIsactive(brand.getIsactive()));
        }
        PageInfo<Brand> page = new PageInfo<Brand>(brands);

        return page;
    }

    @Override
    public PageInfo<Brand> getPartBrandByPage(int page_number, int page_size, String corp_code,String[] brand_code, String search_value) throws SQLException {
        List<Brand> brands;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code",corp_code);
        params.put("brand_code",brand_code);
        params.put("search_value",search_value);

        PageHelper.startPage(page_number, page_size);
        brands = brandMapper.selectPartBrand(params);
        for (Brand brand:brands) {
            brand.setIsactive(CheckUtils.CheckIsactive(brand.getIsactive()));
        }
        PageInfo<Brand> page = new PageInfo<Brand>(brands);

        return page;
    }

    @Override
    public List<Brand> getActiveBrand(String corp_code, String search_value, String[] brand_codes) throws Exception {
        String[] brand_code = null;
        if (brand_codes != null && brand_codes.length>0 && !brand_codes[0].equals("")){
            brand_code = brand_codes;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code",corp_code);
        params.put("brand_code",brand_code);
        params.put("search_value",search_value);
        List<Brand> brands;
        brands = brandMapper.selectBrands(params);
        return brands;
    }

    @Override
    @Transactional
    public String insert(String message, String user_code) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = JSONObject.parseObject(message);
        String brand_code = jsonObject.get("brand_code").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String brand_name = jsonObject.get("brand_name").toString().trim();
        String logo=jsonObject.get("logo").toString().trim();


        Brand brand = getBrandByCode(corp_code, brand_code,Common.IS_ACTIVE_Y);
        Brand brand1 = getBrandByName(corp_code, brand_name,Common.IS_ACTIVE_Y);
        if (brand == null && brand1 == null) {
            brand = new Brand();
            Date now = new Date();
            brand.setBrand_code(brand_code);
            brand.setBrand_name(brand_name);
            brand.setCorp_code(corp_code);
            if (jsonObject.containsKey("cus_user_code")){
                brand.setCus_user_code(jsonObject.get("cus_user_code").toString().trim());
            }
            if (jsonObject.containsKey("app_id")){
                String app_id = jsonObject.get("app_id").toString().trim();
                if (!app_id.equals("")) {
                    String[] app_ids = app_id.split(",");
                    for (int i = 0; i < app_ids.length; i++) {
                        CorpWechat corpWechat = corpMapper.selectWByAppId(app_ids[i]);
                        String brand_codes = "";
                        if (corpWechat.getBrand_code() != null)
                            brand_codes = corpWechat.getBrand_code();
                        if (!brand_codes.contains(Common.SPECIAL_HEAD+brand_code+",")){
                            brand_codes = brand_codes + Common.SPECIAL_HEAD+brand_code+",";
                            corpWechat.setBrand_code(brand_codes);
                            corpWechat.setModified_date(Common.DATETIME_FORMAT.format(now));
                            corpWechat.setModifier(user_code);
                            corpMapper.updateCorpWechat(corpWechat);
                        }
                    }
                }
            }
            brand.setCreated_date(Common.DATETIME_FORMAT.format(now));
            brand.setCreater(user_code);
            brand.setModified_date(Common.DATETIME_FORMAT.format(now));
            brand.setModifier(user_code);
            brand.setIsactive(jsonObject.get("isactive").toString());
            brand.setLogo(logo);
            brandMapper.insertBrand(brand);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else if (brand != null) {
            result = "品牌编号已存在";
        } else {
            result = "品牌名称已存在";
        }
        return result;
    }

    @Override
    @Transactional
    public String insertExecl(Brand brand) throws Exception{
        brandMapper.insertBrand(brand);
        return "add success";
    }

    @Override
    public int getGoodsCount(String corp_code, String brand_code) throws Exception {
        return brandMapper.getGoodsCount(corp_code, brand_code);
    }


    @Override
    @Transactional
    public String update(String message, String user_code) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = JSONObject.parseObject(message);
        int brand_id = Integer.parseInt(jsonObject.get("id").toString());

        String brand_code = jsonObject.get("brand_code").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String brand_name = jsonObject.get("brand_name").toString().trim();
        String logo=jsonObject.get("logo").toString().trim();

        Brand brand = getBrandById(brand_id);
        String app_id1 = brand.getApp_id();
        Brand brand1 = getBrandByCode(corp_code, brand_code,Common.IS_ACTIVE_Y);
        Brand brand2 = getBrandByCode(corp_code, brand_name,Common.IS_ACTIVE_Y);
        if (brand.getCorp_code().trim().equals(corp_code)) {
            if ((brand.getBrand_code().equals(brand_code) || brand1 == null) && (brand.getBrand_name().trim().equals(brand_name) || brand2 == null)) {
                if (!brand.getBrand_code().trim().equals(brand_code)) {
                    updateCauseCodeChange(corp_code, brand_code, brand.getBrand_code());
                }
//                brand = new Brand();
                Date now = new Date();
                brand.setId(brand_id);
                brand.setBrand_code(brand_code);
                brand.setBrand_name(brand_name);
                brand.setCorp_code(corp_code);
                if (jsonObject.containsKey("cus_user_code")){
                    brand.setCus_user_code(jsonObject.get("cus_user_code").toString().trim());
                }
                if (app_id1 != null && !app_id1.equals("")){
                    String[] app_ids = app_id1.split(",");
                    for (int i = 0; i < app_ids.length; i++) {
                        CorpWechat corpWechat = corpMapper.selectWByAppId(app_ids[i]);
                        String brand_codes = corpWechat.getBrand_code();
                        brand_codes = brand_codes.replace(Common.SPECIAL_HEAD+brand_code+",","");
                        corpWechat.setBrand_code(brand_codes);
                        corpWechat.setModified_date(Common.DATETIME_FORMAT.format(now));
                        corpWechat.setModifier(user_code);
                        corpMapper.updateCorpWechat(corpWechat);
                    }
                }
                if (jsonObject.containsKey("app_id")){
                    String app_id = jsonObject.get("app_id").toString().trim();
                    if (!app_id.equals("")) {
                        String[] app_ids = app_id.split(",");
                        for (int i = 0; i < app_ids.length; i++) {
                            CorpWechat corpWechat = corpMapper.selectWByAppId(app_ids[i]);
                            String brand_codes = "";
                            if (corpWechat.getBrand_code() != null)
                                brand_codes = corpWechat.getBrand_code();
                            if (!brand_codes.contains(Common.SPECIAL_HEAD+brand_code+",")){
                                brand_codes = brand_codes + Common.SPECIAL_HEAD+brand_code+",";
                                corpWechat.setBrand_code(brand_codes);
                                corpWechat.setModified_date(Common.DATETIME_FORMAT.format(now));
                                corpWechat.setModifier(user_code);
                                corpMapper.updateCorpWechat(corpWechat);
                            }
                        }
                    }
                }
                brand.setModified_date(Common.DATETIME_FORMAT.format(now));
                brand.setModifier(user_code);
                brand.setIsactive(jsonObject.get("isactive").toString());
                brand.setLogo(logo);
                brandMapper.updateBrand(brand);
                result = Common.DATABEAN_CODE_SUCCESS;
            } else if (!brand.getBrand_code().trim().equals(brand_code) && brand1 != null) {
                result = "品牌编号已存在";
            } else {
                result = "品牌名称已存在";
            }
        } else {
            if (brand1 == null && brand2 == null) {
                brand = new Brand();
                Date now = new Date();
                brand.setId(brand_id);
                brand.setBrand_code(brand_code);
                brand.setBrand_name(brand_name);
                brand.setCorp_code(corp_code);
                if (jsonObject.containsKey("cus_user_code")){
                    brand.setCus_user_code(jsonObject.get("cus_user_code").toString().trim());
                }
                if (jsonObject.containsKey("app_id")){
                    String app_id = jsonObject.get("app_id").toString().trim();
                    if (!app_id.equals("")) {
                        String[] app_ids = app_id.split(",");
                        for (int i = 0; i < app_ids.length; i++) {
                            CorpWechat corpWechat = corpMapper.selectWByAppId(app_ids[i]);
                            String brand_codes = corpWechat.getBrand_code();
                            if (!brand_codes.contains(Common.SPECIAL_HEAD+brand_code+",")){
                                brand_codes = brand_codes + Common.SPECIAL_HEAD+brand_code+",";
                                corpWechat.setBrand_code(brand_codes);
                                corpWechat.setModified_date(Common.DATETIME_FORMAT.format(now));
                                corpWechat.setModifier(user_code);
                                corpMapper.updateCorpWechat(corpWechat);
                            }
                        }
                    }
                }
                brand.setModified_date(Common.DATETIME_FORMAT.format(now));
                brand.setModifier(user_code);
                brand.setIsactive(jsonObject.get("isactive").toString());
                brand.setLogo(logo);
                brandMapper.updateBrand(brand);
                result = Common.DATABEAN_CODE_SUCCESS;
            } else if (!brand.getBrand_code().equals(brand_code) && brand1 != null) {
                result = "品牌编号已存在";
            } else {
                result = "品牌名称已存在";
            }
        }
        return result;
    }

    @Override
    @Transactional
    public int delete(int id) throws Exception {
        return brandMapper.deleteByBrandId(id);
    }

    @Override
    public PageInfo<Brand> getAllBrandScreen(int page_number, int page_size, String corp_code,String[] brand_code,Map<String, String> map) throws Exception{
        List<Brand> brands;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("brand_code", brand_code);
        params.put("map", map);
        brands = brandMapper.selectAllBrandScreen(params);
        for (Brand brand:brands) {
            brand.setIsactive(CheckUtils.CheckIsactive(brand.getIsactive()));
        }
        PageInfo<Brand> page = new PageInfo<Brand>(brands);
        return page;
    }

    /**
     * 更改品牌编号时
     * 级联更改关联此编号的店铺，商品列表
     */
    @Transactional
    void updateCauseCodeChange(String corp_code, String new_brand_code, String old_brand_code) throws Exception{
        //商品列表级联修改
        codeUpdateMapper.updateGoods("", corp_code, new_brand_code, old_brand_code);

        //店铺列表级联修改
        codeUpdateMapper.updateStore("", corp_code, Common.SPECIAL_HEAD+new_brand_code+",", Common.SPECIAL_HEAD+old_brand_code+",", "", "");

        //员工
        codeUpdateMapper.updateUser("", corp_code, "","","","","","",Common.SPECIAL_HEAD+new_brand_code+",", Common.SPECIAL_HEAD+old_brand_code+",");

    }
}
