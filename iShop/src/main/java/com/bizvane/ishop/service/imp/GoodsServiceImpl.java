package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.GoodsMapper;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.service.GoodsService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by lixiang on 2016/5/30.
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GoodsServiceImpl.class);



    @Override
    public Goods getGoodsById(int id) throws Exception {
        Goods goods = this.goodsMapper.selectByPrimaryKey(id);
        List<Goods> matchgoods = new ArrayList<Goods>();
        if (goods.getMatch_goods() != null && !goods.getMatch_goods().equals("")){
            String[] match_goods = goods.getMatch_goods().split(",");
            for (int i = 0; i < match_goods.length; i++) {
                Goods match = getGoodsByCode(goods.getCorp_code(),match_goods[i]);
                matchgoods.add(match);
            }
            goods.setMatchgoods(matchgoods);
        }else{
            goods.setMatchgoods(matchgoods);
        }
        transter(goods);
        return goods;
    }

    @Override
    public int insert(Goods goods) throws Exception {
        return goodsMapper.insert(goods);
    }

    @Override
    public String update(Goods goods) throws Exception {
        Goods old = this.goodsMapper.selectByPrimaryKey(goods.getId());
        if (goods.getCorp_code().equals(old.getCorp_code())) {
            if ((!old.getGoods_code().equals(goods.getGoods_code()))
                    && (this.goodsCodeExist(goods.getCorp_code(), goods.getGoods_code()).equals(Common.DATABEAN_CODE_ERROR))) {
                return "编号已经存在";
            } else if (!old.getGoods_name().equals(goods.getGoods_name()) && (this.goodsNameExist(goods.getCorp_code(), goods.getGoods_name()).equals(Common.DATABEAN_CODE_ERROR))) {
                return "名称已经存在";
            } else if (this.goodsMapper.updateByPrimaryKey(goods) >= 0) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        } else {
            if (this.goodsCodeExist(goods.getCorp_code(), goods.getGoods_code()).equals(Common.DATABEAN_CODE_ERROR)) {
                return "编号已经存在";
            } else if (this.goodsNameExist(goods.getCorp_code(), goods.getGoods_name()).equals(Common.DATABEAN_CODE_ERROR)) {
                return "名称已经存在";
            } else if (this.goodsMapper.updateByPrimaryKey(goods) >= 0) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public int delete(int id) throws Exception {
        return goodsMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<Goods> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws Exception{
        List<Goods> list;
        PageHelper.startPage(page_number, page_size);
        list = goodsMapper.selectAllGoods(corp_code, search_value,"");
        for (Goods goods:list) {
            goods.setIsactive(CheckUtils.CheckIsactive(goods.getIsactive()));
        }
        for (int i = 0; list != null && i < list.size(); i++) {
            transter(list.get(i));
        }
        PageInfo<Goods> page = new PageInfo<Goods>(list);
        return page;
    }

    @Override
    public List<Goods> selectBySearch(String corp_code, String search_value) throws Exception{
        List<Goods> list = goodsMapper.selectMatchGoods(corp_code, search_value,Common.IS_ACTIVE_Y);
        for (int i = 0; list != null && i < list.size(); i++) {
            transter(list.get(i));
        }
        return list;
    }

    @Override
    public PageInfo<Goods> selectAllGoodsScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        params.put("corp_code", corp_code);
        List<Goods> labels;
        PageHelper.startPage(page_number, page_size);
        labels = goodsMapper.selectAllGoodsScreen(params);
        for (Goods goods:labels) {
            goods.setIsactive(CheckUtils.CheckIsactive(goods.getIsactive()));
        }
        for (int i = 0; labels != null && i < labels.size(); i++) {
            transter(labels.get(i));
        }
        PageInfo<Goods> page = new PageInfo<Goods>(labels);
        return page;
    }

    /**
     * 将商品图片转换为前台能够读取的JSON格式
     *
     * @param goods ： 商品对象
     */
    private void transter(Goods goods) throws Exception{
        try {
            String jsString = goods.getGoods_image();
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsString);
            Iterator<String> it = jsonObject.keySet().iterator();
            StringBuffer sb = new StringBuffer();
            while (it.hasNext()) {
                String key = it.next();
                String value = jsonObject.get(key).toString();
                sb.append(value + ",");
            }
            if (sb.toString().length() < 1 || goods.toString().isEmpty()) {
                return;
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            goods.setGoods_image(sb.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public Goods getGoodsByCode(String corp_code, String goods_code) throws Exception{
        Goods goods = this.goodsMapper.getGoodsByCode(corp_code, goods_code);
        transter(goods);
        return goods;
    }

    @Override
    public String goodsCodeExist(String corp_code, String goods_code) throws Exception{
        Goods good = goodsMapper.getGoodsByCode(corp_code, goods_code);
        String result = Common.DATABEAN_CODE_ERROR;
        if (good == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }

    @Override
    public String goodsNameExist(String corp_code, String goods_name) throws Exception{
        Goods good = goodsMapper.getGoodsByName(corp_code, goods_name);
        String result = Common.DATABEAN_CODE_ERROR;
        if (good == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }
}
