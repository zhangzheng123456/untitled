package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Store;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by lixiang on 2016/5/31.
 *
 * @@version
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml",
        "classpath:spring-mybatis.xml" })
public class testStoreService {

    @Autowired
    private StoreService storeService;
    @Test
    public void testdeleteStoreByUser_id(){
            storeService.deleteStoreByUser_id("9","001");
    }

    @Test
    public void testgetStoresByUserId(){
          List<Store> list=  storeService.selectByUserId("9");
        for (Store store: list) {
            System.out.println(store.getStore_name()+"....."+store.getStore_code());
        }
    }


}
