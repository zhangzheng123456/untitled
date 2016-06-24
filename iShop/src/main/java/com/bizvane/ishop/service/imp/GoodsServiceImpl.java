package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.GoodsMapper;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.service.GoodsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lixiang on 2016/5/30.
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GoodsServiceImpl.class);


    public GoodsServiceImpl() {
    }

    @Override
    public Goods getGoodsById(int id) throws SQLException {
        Goods goods = this.goodsMapper.selectByPrimaryKey(id);
        String jsString = goods.getGoods_image();
        org.json.JSONObject jsonObject = new org.json.JSONObject(jsString);

        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        result.put("test", JSON.toJSONString(jsonObject));
        System.out.println(result.toString());
        Iterator<String> it = jsonObject.keySet().iterator();
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
        goods.setGoods_image(temp1);
        return goods;
    }

    @Override
    public int insert(Goods goods) throws SQLException {
        return goodsMapper.insert(goods);
    }

    @Override
    public int update(Goods goods) throws SQLException {
        return goodsMapper.updateByPrimaryKey(goods);
    }

    @Override
    public int delete(int id) throws SQLException {
        return goodsMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<Goods> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        List<Goods> list;
        PageHelper.startPage(page_number, page_size);
        list = goodsMapper.selectAllGoods(corp_code, search_value);
        for (int i = 0; list != null && i < list.size(); i++) {
            Transter(list.get(i));
        }
        PageInfo<Goods> page = new PageInfo<Goods>(list);
        return page;
    }

    /**
     * 将商品图片转换为前台能够读取的JSON格式
     *
     * @param goods ： 商品对象
     */
    private void Transter(Goods goods) {
        try {
            String jsString = goods.getGoods_image();
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsString);
//            System.out.println(jsonObject.get("1"));
//            System.out.println(jsonObject.get("2"));
            com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
            result.put("test", JSON.toJSONString(jsonObject));
            System.out.println(result.toString());
            Iterator<String> it = jsonObject.keySet().iterator();
            org.json.JSONObject tempObj = new org.json.JSONObject();
            List<String> list = new ArrayList<String>();
            while (it.hasNext()) {
                String key = it.next();
                String value = jsonObject.get(key).toString();
                //System.out.println(key + ":" + value);
                tempObj.put(key, value);
                list.add(value);
            }
            GsonBuilder gb = new GsonBuilder();
            gb.disableHtmlEscaping();
            String temp1 = gb.create().toJson(list);
            goods.setGoods_image(temp1);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public Goods getGoodsByCode(String corp_code, String goods_code) {
        Goods goods = this.goodsMapper.getGoodsByCode(corp_code, goods_code);
        Transter(goods);
        return goods;
    }

    @Override
    public String goodsCodeExist(String corp_code, String goods_code) {
        Goods good = goodsMapper.getGoodsByCode(corp_code, goods_code);
        String result = Common.DATABEAN_CODE_ERROR;
        if (good == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }

    @Override
    public String goodsNameExist(String corp_code, String goods_name) {
        Goods good = goodsMapper.getGoodsByName(goods_name, corp_code);
        String result = Common.DATABEAN_CODE_ERROR;
        if (good == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }
}
