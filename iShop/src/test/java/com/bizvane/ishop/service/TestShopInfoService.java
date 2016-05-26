package com.bizvane.ishop.service;


import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.CorpInfo;
import com.bizvane.ishop.entity.LogInfo;
import com.bizvane.ishop.entity.UserInfo;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.bizvane.ishop.entity.ShopInfo;

import java.sql.SQLException;

import java.util.*;

/**
 * Created by nanji on 2016/5/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml",
        "classpath:spring-mybatis.xml"})
public class TestShopInfoService {
    private static final Logger LOGGER = Logger.getLogger(TestUserService.class);
    @Autowired
    private ShopInfoService logService;


    @Test
    public void testInsert1() {
        ShopInfo shopInfo = new ShopInfo();
        shopInfo.setStore_code("1111111111");
        shopInfo.setStore_name("test1");
        shopInfo.setBrand_code("test_code");
        shopInfo.setBrand_name("test_name");
        shopInfo.setStore_area("test_china");
        shopInfo.setFlg_tob("1");
        shopInfo.setCorp_code("test_corp_code");
        shopInfo.setModified_date(new Date());
        shopInfo.setModifier("test_modifier");
        shopInfo.setCreated_date(new Date());
        shopInfo.setCreater("test_creater");
        shopInfo.setIsactive("1");
        try {
            logService.insert(shopInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void query1() {
        ShopInfo shopInfo = null;
        try {
            shopInfo = logService.getShopInfo(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (shopInfo == null) {
            System.out.println("tiankong");
        } else {
            System.out.println("Id" + shopInfo.getId() + " name" + shopInfo.getBrand_name());
        }
    }

    @Test
    public void update() {
        try {

            ShopInfo shopInfo = logService.getShopInfo(1);
            shopInfo.setBrand_name("test22222");
            shopInfo.setModifier("tiankong");
            logService.update(shopInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void delele(){
        try{
            // this.logService.delete(1);
            System.out.println( this.logService.delete(1));
        }catch(Exception e){
            e.printStackTrace();;
        }
    }


}
