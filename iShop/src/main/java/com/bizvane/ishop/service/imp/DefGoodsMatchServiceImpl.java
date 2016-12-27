package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.dao.DefGoodsMatchMapper;
import com.bizvane.ishop.entity.DefGoodsMatch;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.service.DefGoodsMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by PC on 2016/11/24.
 */
@Service
public class DefGoodsMatchServiceImpl implements DefGoodsMatchService {
    @Autowired
    private DefGoodsMatchMapper defGoodsMatchMapper;
    @Override
    public List<DefGoodsMatch> selectMatchGoods(String corp_code) throws Exception {
        List<DefGoodsMatch> list = defGoodsMatchMapper.selectMatchGoods(corp_code);
        for (int i = 0; list != null && i < list.size(); i++) {
            String goods_image = list.get(i).getGoods_image();
            String new_image = transterGoods(goods_image);
            list.get(i).setGoods_image(new_image);
        }
        return list;
    }
    /**
     * 将商品列表，只显示一张图片
     *
     * @param goodsImage
     */
    private String transterGoods(String goodsImage) {
        String image = "";
        if (goodsImage != null && !goodsImage.equals("")) {
            if (goodsImage.startsWith("[")) {
                JSONArray array = JSONArray.parseArray(goodsImage);
                if (array.size() > 0) {
                    String jstring = array.get(0).toString();
                    JSONObject object = JSONObject.parseObject(jstring);
                    image = object.get("image").toString();
                } else {
                    image = "";
                }
            } else {
                image = goodsImage.split(",")[0];
            }
        }
        return image;
    }
    @Override
    public List<DefGoodsMatch> selMatchBySeach(String corp_code, String search_value) throws Exception {
        List<DefGoodsMatch> list = defGoodsMatchMapper.selMatchBySeach(corp_code, search_value);
        for (int i = 0; list != null && i < list.size(); i++) {
            String goods_image = list.get(i).getGoods_image();
            String new_image = transterGoods(goods_image);
            list.get(i).setGoods_image(new_image);
        }
        return list;
    }

    @Override
    public List<DefGoodsMatch> selectMatchByCode(String corp_code, String goods_match_code) throws Exception {
        List<DefGoodsMatch> list = defGoodsMatchMapper.selectMatchByCode(corp_code, goods_match_code);
        for (int i = 0; list != null && i < list.size(); i++) {
            String goods_image = list.get(i).getGoods_image();
            String new_image = transterGoods(goods_image);
            list.get(i).setGoods_image(new_image);
        }
        return list;
    }

    @Override
    public int delMatchByCode(String corp_code,String goods_match_code)throws Exception  {
        return defGoodsMatchMapper.delMatchByCode(corp_code,goods_match_code);
    }

    @Override
    public int delMatchById(String id)throws Exception  {
        return defGoodsMatchMapper.delMatchById(id);
    }

    @Override
    public int addMatch(DefGoodsMatch defGoodsMatch)throws Exception {
        return defGoodsMatchMapper.addMatch(defGoodsMatch);
    }

    @Override
    public int updMatch(DefGoodsMatch defGoodsMatch)throws Exception  {
        return 0;
    }

    @Override
    public List<DefGoodsMatch> selectGoodsMatchList(String corp_code, String goods_code, String isactive) throws Exception{
        List<DefGoodsMatch> list = defGoodsMatchMapper.selectGoodsMatchList(corp_code, goods_code, isactive);
        for (int i = 0; list != null && i < list.size(); i++) {
            String goods_image = list.get(i).getGoods_image();
            if (goods_image != null && !goods_image.equals("")){
                if (!goods_image.startsWith("[")){
                    JSONArray images_array = new JSONArray();
                    String[] images = goods_image.split(",");
                    for (int j = 0; j < images.length; j++) {
                        JSONObject obj = new JSONObject();
                        obj.put("image",images[j]);
                        obj.put("is_public","N");
                        images_array.add(obj);
                    }
                    list.get(i).setGoods_image(images_array.toJSONString());
                }
            }
        }
        return list;
    }

    @Override
    public List<DefGoodsMatch> selGoodsCodeByUpd(String corp_code, String goods_code) throws Exception {
        return defGoodsMatchMapper.selGoodsCodeByUpd(corp_code,goods_code);
    }

    @Override
    public int updGoodsCode(String goods_code, int id) throws Exception {
        return defGoodsMatchMapper.updGoodsCode(goods_code,id);
    }
}
