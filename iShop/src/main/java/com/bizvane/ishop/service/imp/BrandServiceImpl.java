package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.BrandMapper;
import com.bizvane.ishop.dao.CodeUpdateMapper;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.entity.Area;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.BrandService;
import com.bizvane.sun.v1.common.Data;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/6/4.
 */
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    BrandMapper brandMapper;
    @Autowired
    StoreMapper storeMapper;
    @Autowired
    private CodeUpdateMapper codeUpdateMapper;

    @Override
    public Brand getBrandById(int id) throws SQLException {
        return brandMapper.selectByBrandId(id);
    }

    @Override
    public Brand getBrandByCode(String corp_code, String brand_code) throws SQLException {
        return brandMapper.selectCorpBrand(corp_code, brand_code);
    }

    @Override
    public PageInfo<Brand> getAllBrandByPage(int page_number, int page_size, String corp_code, String search_value) throws SQLException {
        List<Brand> brands;
        PageHelper.startPage(page_number, page_size);
        brands = brandMapper.selectAllBrand(corp_code, search_value);
        for (Brand brand:brands) {
            if(brand.getIsactive().equals("Y")){
                brand.setIsactive("是");
            }else{
                brand.setIsactive("否");
            }
        }
        PageInfo<Brand> page = new PageInfo<Brand>(brands);

        return page;
    }

    @Override
    public List<Brand> getAllBrand(String corp_code) throws SQLException {
        List<Brand> brands;
        brands = brandMapper.selectBrands(corp_code);
        return brands;
    }

    //获得品牌下店铺
    @Override
    public List<Store> getBrandStore(String corp_code, String brand_code) throws SQLException {
        return storeMapper.selectStoreBrandArea(corp_code, "%" + brand_code + "%", "");
    }

    @Override
    @Transactional
    public String insert(String message, String user_id) throws SQLException {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        String brand_code = jsonObject.get("brand_code").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String brand_name = jsonObject.get("brand_name").toString();

        Brand brand = getBrandByCode(corp_code, brand_code);
        Brand brand1 = getBrandByName(corp_code, brand_name);
        if (brand == null && brand1 == null) {
            brand = new Brand();
            Date now = new Date();
            brand.setBrand_code(brand_code);
            brand.setBrand_name(brand_name);
            brand.setCorp_code(corp_code);
            brand.setCreated_date(Common.DATETIME_FORMAT.format(now));
            brand.setCreater(user_id);
            brand.setModified_date(Common.DATETIME_FORMAT.format(now));
            brand.setModifier(user_id);
            brand.setIsactive(jsonObject.get("isactive").toString());
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
    public String insertExecl(Brand brand) {
        brandMapper.insertBrand(brand);
        return "add success";
    }

    @Override
    public int getGoodsCount(String corp_code, String brand_code) throws SQLException {
        return brandMapper.getGoodsCount(corp_code, brand_code);
    }

    @Override
    public int getStoresCount(String corp_code, String brand_code) throws SQLException {
        return brandMapper.getStoresCount(corp_code, brand_code);
    }


    @Override
    @Transactional
    public String update(String message, String user_id) throws SQLException {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        int brand_id = Integer.parseInt(jsonObject.get("id").toString());

        String brand_code = jsonObject.get("brand_code").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String brand_name = jsonObject.get("brand_name").toString();

        Brand brand = getBrandById(brand_id);

        Brand brand1 = getBrandByCode(corp_code, brand_code);
        Brand brand2 = getBrandByCode(corp_code, brand_name);
        if (brand.getCorp_code().equals(corp_code)) {
            if ((brand.getBrand_code().equals(brand_code) || brand1 == null) &&
                    (brand.getBrand_name().equals(brand_name) || brand2 == null)) {
                if (!brand.getBrand_code().equals(brand_code)) {
                    updateCauseCodeChange(corp_code, brand_code, brand.getBrand_code());
                }
                brand = new Brand();
                Date now = new Date();
                brand.setId(brand_id);
                brand.setBrand_code(brand_code);
                brand.setBrand_name(brand_name);
                brand.setCorp_code(corp_code);
                brand.setModified_date(Common.DATETIME_FORMAT.format(now));
                brand.setModifier(user_id);
                brand.setIsactive(jsonObject.get("isactive").toString());
                brandMapper.updateBrand(brand);
                result = Common.DATABEAN_CODE_SUCCESS;
            } else if (!brand.getBrand_code().equals(brand_code) && brand1 != null) {
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
                brand.setModified_date(Common.DATETIME_FORMAT.format(now));
                brand.setModifier(user_id);
                brand.setIsactive(jsonObject.get("isactive").toString());
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
    public int delete(int id) throws SQLException {
        return brandMapper.deleteByBrandId(id);
    }

    @Override
    public Brand getBrandByName(String corp_code, String brand_name) {
        Brand brand = brandMapper.selectByBrandName(corp_code, brand_name);
        return brand;
    }


    @Override
    public PageInfo<Brand> getAllBrandScreen(int page_number, int page_size, String corp_code, Map<String, String> map) {
        List<Brand> brands;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);

        params.put("map", map);
        brands = brandMapper.selectAllBrandScreen(params);
        for (Brand brand:brands) {
            if(brand.getIsactive().equals("Y")){
                brand.setIsactive("是");
            }else{
                brand.setIsactive("否");
            }
        }
        PageInfo<Brand> page = new PageInfo<Brand>(brands);
        return page;
    }

    /**
     * 更改品牌编号时
     * 级联更改关联此编号的店铺，商品列表
     */
    @Transactional
    void updateCauseCodeChange(String corp_code, String new_brand_code, String old_brand_code) {
        //店铺列表级联修改
        codeUpdateMapper.updateGoods("", corp_code, new_brand_code, old_brand_code);

        //商品列表级联修改
        codeUpdateMapper.updateStore("", corp_code, new_brand_code, old_brand_code, "", "");
    }
}
