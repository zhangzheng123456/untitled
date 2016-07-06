package com.bizvane.ishop.service;

import IceInternal.Ex;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.*;
import com.bizvane.ishop.entity.*;
import com.github.pagehelper.PageInfo;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.System;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lixiang on 2016/6/21.
 *
 * @@version
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml",
        "classpath:spring-mybatis.xml"})
public class testLixixi {

    @Autowired
    private StoreService storeService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void test_7_6() {
        try {
            Message message = messageMapper.selectByPrimaryKey(1);
            System.out.println(message.getTem_content());
        } catch (Exception ex) {
            System.out.println(ex.toString());
            System.out.println(ex.getMessage());
        }
    }


    @Test
    public void test11() {
        try {
            Store store = storeService.getStoreById(112122);
            System.out.println(store.getStore_name());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        try {
            PageInfo<MessageTemplate> messageTemplates = this.messageTemplateService.selectBySearch(1, 10, "", "");
            System.out.println(messageTemplates.getSize());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void test2() {
        try {

        } catch (Exception ex) {
            System.out.println("Errorlixixi : " + ex.getMessage());
        }
    }

    @Test
    public void test3() {
        try {
            MessageTemplate messageTemplate = this.messageTemplateService.getMessageTemplateById(1);
            messageTemplate.setId(2);
            messageTemplate.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
            messageTemplate.setTem_content("11111111111111111");
            //  this.messageTemplateService.insert(messageTemplate);
            this.messageTemplateService.update(messageTemplate);
            System.out.println(messageTemplate.getCreater());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    @Autowired
    private VIPtagMapper viPtagMapper;

    @Test
    public void testdeleteStoreByUser_id() {
        VIPtag viPtag = viPtagMapper.selectByPrimaryKey(111);
        System.out.println(viPtag.getCreater());
        viPtag.setCreater("222");
        this.viPtagMapper.updateByPrimaryKey(viPtag);
    }

    @Test
    public void testgetStoresByUserId() {

    }

    @Autowired
    private GoodsService goodsService;

    @Test
    public void test5() {
        try {
            Goods goods = goodsService.getGoodsById(1);
            String jsString = goods.getGoods_image();
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsString);
            System.out.println(jsonObject.get("1"));
            System.out.println(jsonObject.get("2"));
            com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
            result.put("test", JSON.toJSONString(jsonObject));
            System.out.println(result.toString());
            Iterator<String> it = jsonObject.keySet().iterator();
            //  com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
            org.json.JSONObject tempObj = new org.json.JSONObject();
            while (it.hasNext()) {
                String key = it.next();
                String value = jsonObject.get(key).toString();
                System.out.println(key + ":" + value);
                tempObj.put(key, value);
            }
            GsonBuilder gb = new GsonBuilder();
            gb.disableHtmlEscaping();
            String temp1 = gb.create().toJson(tempObj);
            System.out.println(temp1);


        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    @Test
    public void test6() throws SQLException {
        Goods goods = goodsService.getGoodsById(1);
        System.out.println(goods.getGoods_image());

    }

    @Autowired
    private VipCallbackRecordMapper vipCallbackRecordMapper;

    @Autowired
    private VipCallbackRecordService vipCallbackRecordService;

    @Test
    public void test7() {
//        VipCallbackRecord vipCallbackRecord = vipCallbackRecordMapper.selecctById(1);
//        System.out.println(vipCallbackRecord.toString());
//        vipCallbackRecord.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
//        vipCallbackRecord.setModified_date("22");
//        vipCallbackRecordMapper.insert(vipCallbackRecord);
        PageInfo<VipCallbackRecord> pageInfo = vipCallbackRecordService.selectBySearch(1, 10, "C00001", "");
        List<VipCallbackRecord> list = pageInfo.getList();
        System.out.println(list.size());


    }

    @Autowired
    private VipTagTypeMapper vipTagTypeMapper;

    @Test
    public void test8() {
        VipTagType vipTagType = vipTagTypeMapper.selectByPrimaryKey(1);
        vipTagType.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
        vipTagType.setId(2);
        vipTagType.setCorp_code("test77777777777");
        vipTagTypeMapper.updateByPrimaryKey(vipTagType);
    }

    @Autowired
    public MessageTypeService messageTypeService;

    @Test
    public void test9() throws SQLException {
        try {
            String corp_code = "C00000";
            String tem_name = "testr34";
            this.messageTemplateService.messageTemplateNameExist(corp_code, tem_name);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void test911() {
        try {
            brandService.delete(61);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}
