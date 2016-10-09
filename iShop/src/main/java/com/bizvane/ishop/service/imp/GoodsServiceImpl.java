package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.GoodsMapper;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.entity.GoodsMatch;
import com.bizvane.ishop.service.GoodsService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
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

    private static final Logger log = Logger.getLogger (GoodsServiceImpl.class);



    @Override
    public Goods getGoodsById(int id) throws Exception {
        Goods goods = this.goodsMapper.selectByPrimaryKey(id);
        List<Goods> matchgoods = new ArrayList<Goods>();

        String corp_code = goods.getCorp_code();
        String goods_code = goods.getGoods_code();
        List<GoodsMatch> matches1 = goodsMapper.selectMatchGoods1(corp_code,goods_code);
        List<GoodsMatch> matches2 = goodsMapper.selectMatchGoods2(corp_code,goods_code);

        for (int i = 0; i < matches1.size(); i++) {
            String goods_code_match = matches1.get(i).getGoods_code_match();
            Goods match = getGoodsByCode(corp_code,goods_code_match,Common.IS_ACTIVE_Y);
            if (match != null) {
                String goods_image = match.getGoods_image();
                if (goods_image != null && !goods_image.isEmpty()) {
                    match.setGoods_image(goods_image.split(",")[0]);
                }
                matchgoods.add(match);
            }
        }
        for (int i = 0; i < matches2.size(); i++) {
            String good_code = matches2.get(i).getGoods_code();
            Goods match = getGoodsByCode(corp_code,good_code,Common.IS_ACTIVE_Y);
            if (match != null) {
                String goods_image = match.getGoods_image();
                if (goods_image != null && !goods_image.isEmpty()) {
                    match.setGoods_image(goods_image.split(",")[0]);
                }
                matchgoods.add(match);
            }
        }
        goods.setMatchgoods(matchgoods);

        transter(goods);
        return goods;
    }

    @Override
    public int insert(Goods goods) throws Exception {
        return goodsMapper.insert(goods);
    }

    @Override
    public int insertGoods(Goods goods,String match_goods) throws Exception {
        Date now = new Date();
        if (!match_goods.equals("")) {
            String[] matches = match_goods.split(",");
            for (int i = 0; i < matches.length; i++) {
                String goods_code_match = matches[i];
                GoodsMatch match = new GoodsMatch();
                match.setCorp_code(goods.getCorp_code().trim());
                match.setGoods_code(goods.getGoods_code().trim());
                match.setGoods_code_match(goods_code_match);
                match.setModified_date(Common.DATETIME_FORMAT.format(now));
                match.setModifier(goods.getCreater());
                match.setCreated_date(Common.DATETIME_FORMAT.format(now));
                match.setCreater(goods.getCreater());
                goodsMapper.insertMatch(match);
            }
        }

        return goodsMapper.insert(goods);
    }

    @Override
    public String update(Goods goods,String match_goods) throws Exception {
        Goods old_goods = goodsMapper.selectByPrimaryKey(goods.getId());
        Goods new_goods = getGoodsByCode(goods.getCorp_code().trim(), goods.getGoods_code().trim(),Common.IS_ACTIVE_Y);
        if (goods.getCorp_code().trim().equals(old_goods.getCorp_code().trim())) {
            if (old_goods.getId() != new_goods.getId() && new_goods != null) {
                return "编号已经存在";
            } else if (this.goodsMapper.updateByPrimaryKey(goods) >= 0) {
                goodsMapper.deleteMatch(goods.getCorp_code().trim(),old_goods.getGoods_code().trim());
                Date now = new Date();
                if (!match_goods.equals("")) {
                    String[] matches = match_goods.split(",");
                    for (int i = 0; i < matches.length; i++) {
                        String goods_code_match = matches[i];
                        GoodsMatch match = new GoodsMatch();
                        match.setCorp_code(goods.getCorp_code().trim());
                        match.setGoods_code(goods.getGoods_code().trim());
                        match.setGoods_code_match(goods_code_match);
                        match.setModified_date(Common.DATETIME_FORMAT.format(now));
                        match.setModifier(goods.getModifier());
                        match.setCreated_date(Common.DATETIME_FORMAT.format(now));
                        match.setCreater(goods.getModifier());
                        match.setIsactive(Common.IS_ACTIVE_Y);
                        goodsMapper.insertMatch(match);
                    }
                }
                return Common.DATABEAN_CODE_SUCCESS;
            }
        }
//        else {
//            if (this.goodsCodeExist(goods.getCorp_code(), goods.getGoods_code()).equals(Common.DATABEAN_CODE_ERROR)) {
//                return "编号已经存在";
//            } else if (this.goodsMapper.updateByPrimaryKey(goods) >= 0) {
//                return Common.DATABEAN_CODE_SUCCESS;
//            }
//        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public int delete(int id) throws Exception {
        Goods goods = getGoodsById(id);
        if (goods != null){
            goodsMapper.deleteMatch(goods.getCorp_code(),goods.getGoods_code());
        }
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
    public PageInfo<Goods> selectBySearchForApp(int page_number, int page_size, String corp_code,String goods_quarter,
                                                String goods_wave,String brand_code, String time_start,String time_end,String search_value) throws Exception{
        List<Goods> list;
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("goods_quarter",goods_quarter);
        map.put("goods_wave",goods_wave);
        map.put("brand_code",brand_code);
        if (!goods_quarter.equals("")) {
            String[] goods_quarters = goods_quarter.split(",");
            map.put("goods_quarter",goods_quarters);
        }
        if (!goods_wave.equals("")) {
            String[] goods_waves = goods_wave.split(",");
            map.put("goods_wave",goods_waves);
        }
        if (!brand_code.equals("")) {
            String[] brand_codes = brand_code.split(",");
            map.put("brand_code",brand_codes);
        }

        map.put("corp_code",corp_code);
        map.put("time_start",time_start);
        map.put("time_end",time_end);
        map.put("search_value",search_value);
        map.put("isactive",Common.IS_ACTIVE_Y);

        PageHelper.startPage(page_number, page_size);
        list = goodsMapper.selectAllGoodsForApp(map);
        for (int i = 0; list != null && i < list.size(); i++) {
            transter(list.get(i));
            String goods_image = list.get(i).getGoods_image();
            if (goods_image != null && !goods_image.isEmpty()) {
                list.get(i).setGoods_image(goods_image.split(",")[0]);
            }
        }
        PageInfo<Goods> page = new PageInfo<Goods>(list);
        return page;
    }


    @Override
    public PageInfo<Goods> matchGoodsList(int page_number, int page_size,String corp_code, String search_value,String goods_code,String brand_code) throws Exception{
        PageHelper.startPage(page_number, page_size);
        List<Goods> list = goodsMapper.matchGoodsList(corp_code, search_value,goods_code,brand_code,Common.IS_ACTIVE_Y);
        for (int i = 0; list != null && i < list.size(); i++) {
            transter(list.get(i));
            String goods_image = list.get(i).getGoods_image();
            if (goods_image != null && !goods_image.isEmpty()) {
                list.get(i).setGoods_image(goods_image.split(",")[0]);
            }
        }
        PageInfo<Goods> page = new PageInfo<Goods>(list);
        return page;
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
            JSONObject jsonObject = JSONObject.parseObject(jsString);
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
    public Goods getGoodsByCode(String corp_code, String goods_code,String isactive) throws Exception{
        Goods goods = this.goodsMapper.getGoodsByCode(corp_code, goods_code,isactive);
        if ( goods!= null)
            transter(goods);
        return goods;
    }

    @Override
    public Goods goodsNameExist(String corp_code, String goods_name,String isactive) throws Exception{
        Goods goods = goodsMapper.getGoodsByName(corp_code, goods_name,isactive);
        return goods;
    }

    //获取企业FAB季度
    @Override
    public List<Goods> selectCorpGoodsQuarter(String corp_code) throws Exception{
        return goodsMapper.selectCorpGoodsQuarter(corp_code);
    }

    //获取企业FAB波段
    @Override
    public List<Goods> selectCorpGoodsWave(String corp_code) throws Exception{
        return goodsMapper.selectCorpGoodsWave(corp_code);
    }
}
