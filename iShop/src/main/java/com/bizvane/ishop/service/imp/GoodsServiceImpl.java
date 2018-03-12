package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.GoodsMapper;
import com.bizvane.ishop.entity.DefGoodsMatch;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.entity.GoodsMatch;
import com.bizvane.ishop.service.DefGoodsMatchService;
import com.bizvane.ishop.service.GoodsService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by lixiang on 2016/5/30.
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private DefGoodsMatchService defGoodsMatchService;
    private static final Logger log = Logger.getLogger(GoodsServiceImpl.class);


    @Override
    public Goods getGoodsById(int id) throws Exception {
        Goods goods = this.goodsMapper.selectByPrimaryKey(id);
//        List<Goods> matchgoods = new ArrayList<Goods>();
//
//        String corp_code = goods.getCorp_code();
//        String goods_code = goods.getGoods_code();
//        List<GoodsMatch> matches1 = goodsMapper.selectMatchGoods1(corp_code,goods_code);
//        List<GoodsMatch> matches2 = goodsMapper.selectMatchGoods2(corp_code,goods_code);
//
//        for (int i = 0; i < matches1.size(); i++) {
//            String goods_code_match = matches1.get(i).getGoods_code_match();
//            Goods match = getGoodsByCode(corp_code,goods_code_match,Common.IS_ACTIVE_Y);
//            if (match != null) {
//                transterGoods(match);
//                matchgoods.add(match);
//            }
//        }
//        for (int i = 0; i < matches2.size(); i++) {
//            String good_code = matches2.get(i).getGoods_code();
//            Goods match = getGoodsByCode(corp_code,good_code,Common.IS_ACTIVE_Y);
//            if (match != null) {
//                transterGoods(match);
//                matchgoods.add(match);
//            }
//        }
//        goods.setMatchgoods(matchgoods);

        transterGoodsImg(goods);
        return goods;
    }

    @Override
    public int insert(Goods goods) throws Exception {
        return goodsMapper.insert(goods);
    }

    @Override
    public int updateExecl(Goods goods) throws Exception {
        return goodsMapper.updateByPrimaryKey(goods);
    }

    @Override
    public int insertGoods(Goods goods, String match_goods) throws Exception {
//        Date now = new Date();
//        if (!match_goods.equals("")) {
//            String[] matches = match_goods.split(",");
//            for (int i = 0; i < matches.length; i++) {
//                String goods_code_match = matches[i];
//                GoodsMatch match = new GoodsMatch();
//                match.setCorp_code(goods.getCorp_code().trim());
//                match.setGoods_code(goods.getGoods_code().trim());
//                match.setGoods_code_match(goods_code_match);
//                match.setModified_date(Common.DATETIME_FORMAT.format(now));
//                match.setModifier(goods.getCreater());
//                match.setCreated_date(Common.DATETIME_FORMAT.format(now));
//                match.setCreater(goods.getCreater());
//                goodsMapper.insertMatch(match);
//            }
//        }

        return goodsMapper.insert(goods);
    }

    @Override
    public String update(Goods goods) throws Exception {
        Goods old_goods = goodsMapper.selectByPrimaryKey(goods.getId());
        Goods new_goods = getGoodsByCode(goods.getCorp_code().trim(), goods.getGoods_code().trim(), Common.IS_ACTIVE_Y);
        if (goods.getCorp_code().trim().equals(old_goods.getCorp_code().trim())) {
            if (new_goods != null && old_goods.getId() != new_goods.getId()) {
                return "编号已经存在";
            } else if (this.goodsMapper.updateByPrimaryKey(goods) >= 0) {
                if (!old_goods.getGoods_code().equals(goods.getGoods_code())){
                    List<DefGoodsMatch> defGoodsMatches = defGoodsMatchService.selGoodsCodeByUpd(old_goods.getCorp_code().trim(), old_goods.getGoods_code());
                    for (DefGoodsMatch defGoodsMatch:defGoodsMatches) {
                        defGoodsMatchService.updGoodsCode(goods.getGoods_code(),defGoodsMatch.getId());
                    }
                }

//                goodsMapper.deleteMatch(goods.getCorp_code().trim(),old_goods.getGoods_code().trim());
//                Date now = new Date();
//                if (!match_goods.equals("")) {
//                    String[] matches = match_goods.split(",");
//                    for (int i = 0; i < matches.length; i++) {
//                        String goods_code_match = matches[i];
//                        GoodsMatch match = new GoodsMatch();
//                        match.setCorp_code(goods.getCorp_code().trim());
//                        match.setGoods_code(goods.getGoods_code().trim());
//                        match.setGoods_code_match(goods_code_match);
//                        match.setModified_date(Common.DATETIME_FORMAT.format(now));
//                        match.setModifier(goods.getModifier());
//                        match.setCreated_date(Common.DATETIME_FORMAT.format(now));
//                        match.setCreater(goods.getModifier());
//                        match.setIsactive(Common.IS_ACTIVE_Y);
//                        goodsMapper.insertMatch(match);
//                    }
//                }
                return Common.DATABEAN_CODE_SUCCESS;
            }
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public int delete(int id) throws Exception {
//        Goods goods = getGoodsById(id);
//        if (goods != null){
//            goodsMapper.deleteMatch(goods.getCorp_code(),goods.getGoods_code());
//        }
        return goodsMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<Goods> selectBySearch(int page_number, int page_size, String corp_code, String search_value, String[] brand_code) throws Exception {
        List<Goods> list;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("corp_code", corp_code);
        map.put("search_value", search_value);
        map.put("brand_code", brand_code);
        map.put("isactive", "");

        PageHelper.startPage(page_number, page_size);
        list = goodsMapper.selectAllGoods(map);
        for (Goods goods : list) {
            goods.setIsactive(CheckUtils.CheckIsactive(goods.getIsactive()));
            transterGoods(goods);
        }
        PageInfo<Goods> page = new PageInfo<Goods>(list);
        return page;
    }

    @Override
    public PageInfo<Goods> selectBySearch(int page_number, int page_size, String corp_code, String search_value, String[] brand_code,String manager_corp) throws Exception {
        List<Goods> list;
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("manager_corp_arr", manager_corp_arr);
        map.put("corp_code", corp_code);
        map.put("search_value", search_value);
        map.put("brand_code", brand_code);
        map.put("isactive", "");
        map.put("brand_code", brand_code);
        PageHelper.startPage(page_number, page_size);
        list = goodsMapper.selectAllGoods(map);
        for (Goods goods : list) {
            goods.setIsactive(CheckUtils.CheckIsactive(goods.getIsactive()));
            transterGoods(goods);
        }
        PageInfo<Goods> page = new PageInfo<Goods>(list);
        return page;
    }
    @Override
    public PageInfo<Goods> selectAllGoodsByBrand(int page_number, int page_size, String corp_code, String search_value, String[] brand_code) throws Exception {
        List<Goods> list;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("corp_code", corp_code);
        map.put("search_value", search_value);
        map.put("brand_code", brand_code);

        PageHelper.startPage(page_number, page_size);
        list = goodsMapper.selectAllGoodsByBrand(map);
        for (Goods goods : list) {
            goods.setIsactive(CheckUtils.CheckIsactive(goods.getIsactive()));
            transterGoods(goods);
        }
        PageInfo<Goods> page = new PageInfo<Goods>(list);
        return page;
    }

    @Override
    public PageInfo<Goods> selectBySearchForApp(int page_number, int page_size, String corp_code, String goods_quarter,
                                                String goods_wave, String brand_code, String time_start, String time_end, String search_value) throws Exception {
        List<Goods> list;
        String[] goods_quarters = null;
        String[] goods_waves = null;
        String[] brand_codes = null;

        Map<String, Object> map = new HashMap<String, Object>();
        if (!goods_quarter.trim().equals("")) {
            goods_quarters = goods_quarter.split(",");
        }
        if (!goods_wave.trim().equals("")) {
            goods_waves = goods_wave.split(",");
        }
        if (!brand_code.trim().equals("")) {
            brand_codes = brand_code.split(",");
        }
        map.put("goods_quarter", goods_quarters);
        map.put("goods_wave", goods_waves);
        map.put("brand_code", brand_codes);
        map.put("corp_code", corp_code);
        map.put("time_start", time_start);
        map.put("time_end", time_end);
        map.put("search_value", search_value);
        map.put("isactive", Common.IS_ACTIVE_Y);

        PageHelper.startPage(page_number, page_size);
        list = goodsMapper.selectAllGoodsForApp(map);
        for (int i = 0; list != null && i < list.size(); i++) {
            transterGoods(list.get(i));
        }
        PageInfo<Goods> page = new PageInfo<Goods>(list);
        return page;
    }

    @Override
    public PageInfo<Goods> matchGoodsList(int page_number, int page_size, String corp_code, String search_value, String goods_code, String brand_code) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<Goods> list = goodsMapper.matchGoodsList(corp_code, search_value, goods_code, brand_code, Common.IS_ACTIVE_Y);
        for (int i = 0; list != null && i < list.size(); i++) {
            transterGoods(list.get(i));
        }
        PageInfo<Goods> page = new PageInfo<Goods>(list);
        return page;
    }

    @Override
    public PageInfo<Goods> selectAllGoodsScreen(int page_number, int page_size, String corp_code, Map<String, String> map, String[] brand_code) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        params.put("corp_code", corp_code);
        params.put("brand_code", brand_code);

        List<Goods> list;
        PageHelper.startPage(page_number, page_size);
        list = goodsMapper.selectAllGoodsScreen(params);
        for (Goods goods : list) {
            goods.setIsactive(CheckUtils.CheckIsactive(goods.getIsactive()));
            transterGoods(goods);
        }
        PageInfo<Goods> page = new PageInfo<Goods>(list);
        return page;
    }

    @Override
    public PageInfo<Goods> selectAllGoodsScreen(int page_number, int page_size, String corp_code, Map<String, String> map, String[] brand_code,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        params.put("corp_code", corp_code);
        params.put("brand_code", brand_code);
        params.put("manager_corp_arr", manager_corp_arr);
        List<Goods> list;
        PageHelper.startPage(page_number, page_size);
        list = goodsMapper.selectAllGoodsScreen(params);
        for (Goods goods : list) {
            goods.setIsactive(CheckUtils.CheckIsactive(goods.getIsactive()));
            transterGoods(goods);
        }
        PageInfo<Goods> page = new PageInfo<Goods>(list);
        return page;
    }

    /**
     * 将商品图片转换为前台能够读取的JSON格式
     *
     * @param goods ： 商品对象
     */
    private void transter(Goods goods) throws Exception {
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
    public Goods getGoodsByCode(String corp_code, String goods_code, String isactive) throws Exception {
        Goods goods = this.goodsMapper.getGoodsByCode(corp_code, goods_code, isactive);
        if (goods != null)
            transter(goods);
        return goods;
    }

    @Override
    public Goods goodsNameExist(String corp_code, String goods_name, String isactive) throws Exception {
        Goods goods = goodsMapper.getGoodsByName(corp_code, goods_name, isactive);
        return goods;
    }

    //获取企业FAB季度
    @Override
    public List<Goods> selectCorpGoodsQuarter(String corp_code) throws Exception {
        return goodsMapper.selectCorpGoodsQuarter(corp_code);
    }

    //获取企业FAB波段
    @Override
    public List<Goods> selectCorpGoodsWave(String corp_code) throws Exception {
        return goodsMapper.selectCorpGoodsWave(corp_code);
    }

    public List<Goods> selectCorpPublicImgs(String corp_code, String brand_code, String search_value) throws Exception {
        List<Goods> goodsList = new ArrayList<Goods>();

        String[] brand_codes = null;
        Map<String, Object> map = new HashMap<String, Object>();
        if (!brand_code.trim().equals("")) {
            brand_codes = brand_code.split(",");
        }
        map.put("goods_quarter", null);
        map.put("goods_wave", null);
        map.put("brand_code", brand_codes);

        map.put("corp_code", corp_code);
        map.put("search_value", search_value);
        map.put("isactive", Common.IS_ACTIVE_Y);
        List<Goods> list = goodsMapper.selectAllGoodsForApp(map);
        for (int i = 0; list != null && i < list.size(); i++) {
            String image = "";
            Goods goods = list.get(i);
            if (goods.getGoods_image() != null && !goods.getGoods_image().equals("")) {
                String goods_image = goods.getGoods_image();
                if (goods_image.startsWith("[{")) {
                    JSONArray array = JSONArray.parseArray(goods_image);
                    for (int j = 0; j < array.size(); j++) {
                        String jstring = array.get(j).toString();
                        JSONObject object = JSONObject.parseObject(jstring);
                        String good_image = object.get("image").toString();
                        String is_public = object.get("is_public").toString();
                        if (is_public.equals("Y")) {
                            image = image + good_image + ",";
                        }
                    }
                }
            }
            if (image.endsWith(",")) {
                goods.setGoods_image(image.substring(0, image.length() - 1));
                goodsList.add(goods);
            }
        }
        return goodsList;
    }

    /**
     * 将商品列表，只显示一张图片
     *
     * @param goods ： 商品对象
     */
    private Goods transterGoods(Goods goods) {
        String image = "";
        if (goods.getGoods_image() != null && !goods.getGoods_image().equals("")) {
            String goods_image = goods.getGoods_image();
            if (goods_image.startsWith("[")) {
                JSONArray array = JSONArray.parseArray(goods_image);
                if (array.size() > 0) {
                    String jstring = array.get(0).toString();
                    JSONObject object = JSONObject.parseObject(jstring);
                    image = object.get("image").toString();
                } else {
                    image = "";
                }
            } else {
                image = goods_image.split(",")[0];
            }
            goods.setGoods_image(image);
        }
        return goods;
    }

    /**
     * FAB详情页
     * 将商品图片转换为前台能够读取的JSON格式
     *
     * @param goods ： 商品对象
     */
    private void transterGoodsImg(Goods goods) throws Exception {
        String image = "11";
        if (goods.getGoods_image() != null && !goods.getGoods_image().equals("")) {
            String goods_image = goods.getGoods_image();
            if (!goods_image.startsWith("[")) {
                JSONArray array = new JSONArray();
                String[] images = goods_image.split(",");
                for (int i = 0; i < images.length; i++) {
                    JSONObject object = new JSONObject();
                    object.put("image", images[i]);
                    object.put("is_public", "N");
                    array.add(object);
                }
                goods.setGoods_image(array.toString());
            }
        }
    }

    @Override
    public PageInfo<Goods> getMatchFab(int page_number, int page_size, String corp_code, String search_value) throws SQLException {
        List<Goods> list;
        PageHelper.startPage(page_number, page_size);
        list = goodsMapper.getMatchFab(corp_code, search_value);
        for (int i = 0; list != null && i < list.size(); i++) {
            transterGoods(list.get(i));
        }
        PageInfo<Goods> page = new PageInfo<Goods>(list);
        return page;
    }

}
