package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.BrandMapper;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ZhouZhou on 2016/6/4.
 */
@Service
public class BrandServiceImpl implements BrandService{
    @Autowired
    BrandMapper brandMapper;
    @Autowired
    StoreMapper storeMapper;

    @Override
    public Brand getBrandById(int id) throws SQLException {
        return brandMapper.selectByBrandId(id);
    }

    @Override
    public Brand getBrandByCode(String corp_code,String brand_code) throws SQLException {
        return brandMapper.selectCorpBrand(corp_code,brand_code);
    }

    @Override
    public PageInfo<Brand> getAllBrandByPage(int page_number, int page_size, String corp_code, String search_value) throws SQLException {
        List<Brand> brands;
        if (search_value.equals("")) {
            PageHelper.startPage(page_number, page_size);
            brands = brandMapper.selectAllBrand(corp_code,"");
        }else {
            PageHelper.startPage(page_number, page_size);
            brands = brandMapper.selectAllBrand(corp_code, "%" + search_value + "%");
        }
        PageInfo<Brand> page = new PageInfo<Brand>(brands);

        return page;
    }

    @Override
    public List<Brand> getAllBrand(String corp_code,String search_value) throws SQLException {
        return brandMapper.selectAllBrand(corp_code,"%"+search_value+"%");
    }

    //获得品牌下店铺
    @Override
    public List<Store> getBrandStore(String corp_code, String brand_code) throws SQLException {
        return storeMapper.selectStoreBrandArea(corp_code,"%"+brand_code+","+"%","");
    }

    @Override
    public int insert(Brand brand) throws SQLException {
        return brandMapper.insertBrand(brand);
    }

    @Override
    public int update(Brand brand) throws SQLException {
        return brandMapper.updateBrand(brand);
    }

    @Override
    public int delete(int id) throws SQLException {
        return brandMapper.deleteByBrandId(id);
    }

}
