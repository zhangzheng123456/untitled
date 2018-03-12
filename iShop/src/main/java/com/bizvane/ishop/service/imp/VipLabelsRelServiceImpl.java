package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.dao.VipLabelsRelMapper;
import com.bizvane.ishop.entity.VipLabelsRel;
import com.bizvane.ishop.service.VipLabelsRelService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryOperators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by yanyadong on 2017/2/16.
 */
@Service
public class VipLabelsRelServiceImpl implements VipLabelsRelService {
    @Autowired
    VipLabelsRelMapper vipLabelsRelMapper;

    @Override
    public PageInfo<VipLabelsRel> selectAllLabel(int page_number, int page_size, String corp_code, String search_value) throws  Exception{
        List<VipLabelsRel> labels;
        PageHelper.startPage(page_number, page_size);
        labels= vipLabelsRelMapper.selectAllLabel(corp_code,search_value);
        PageInfo<VipLabelsRel> page = new PageInfo<VipLabelsRel>(labels);
        return page;
    }

    @Override
    public PageInfo<VipLabelsRel> selectAllScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        List<VipLabelsRel> labels;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        Set<String> sets=map.keySet();
        if(sets.contains("created_date")) {
            JSONObject date = JSONObject.parseObject(map.get("created_date"));
            params.put("created_date_start", date.get("start").toString());
            String end = date.get("end").toString();
            if (!end.equals(""))
                end = end + " 23:59:59";
            params.put("created_date_end", end);
            map.remove("created_date");
        }
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        labels= vipLabelsRelMapper.selectAllScreen(params);
        PageInfo<VipLabelsRel> page = new PageInfo<VipLabelsRel>(labels);
        return page;
    }

    @Override
    public int delActivityVipLabelById(int id) throws Exception {
        return vipLabelsRelMapper.delActivityVipLabelById(id);
    }

    @Override
    public VipLabelsRel selectVipLabelsRelById(int id) throws Exception{
        return vipLabelsRelMapper.selectVipLabelsRelById(id);
    }


    //按权限筛选
    @Override
    public PageInfo<VipLabelsRel> selectRoleAllScreen(int page_number, int page_size, String corp_code, String user_code, String store_code,String role_code,Map<String, String> map) throws Exception {
        List<VipLabelsRel> labels;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("role_code",role_code);

        if(!user_code.equals("")||user_code!=null){
            params.put("user_code",user_code);
        }
        if(store_code!=null||!store_code.equals("")){
            String[] stores=store_code.split(",");
            params.put("array",stores);
        }
            Set<String> sets = map.keySet();
            if (sets.contains("created_date")) {
                JSONObject date = JSONObject.parseObject(map.get("created_date"));
                params.put("created_date_start", date.get("start").toString());
                String end = date.get("end").toString();
                // if (!end.equals(""))
                //     end = end + " 23:59:59";
                params.put("created_date_end", end);
                map.remove("created_date");
            }

        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        labels= vipLabelsRelMapper.selectRoleAllScreen(params);
        PageInfo<VipLabelsRel> page = new PageInfo<VipLabelsRel>(labels);
        return page;
    }

    //按权限查询
    @Override
    public PageInfo<VipLabelsRel> selectRoleAllLabel(int page_number, int page_size, String corp_code, String user_code, String store_code, String role_code,String search_value) throws Exception {
        List<VipLabelsRel> labels;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("role_code",role_code);
        if(!user_code.equals("")||user_code!=null){
            params.put("user_code",user_code);
        }
        if(store_code!=null||!store_code.equals("")){
            String[] stores=store_code.split(",");
            params.put("array",stores);
        }
        if(!search_value.equals("")||search_value!=null){
            params.put("search_value",search_value);
        }
        labels= vipLabelsRelMapper.selectRoleAllLabel(params);
        PageInfo<VipLabelsRel> page = new PageInfo<VipLabelsRel>(labels);
        return page;

    }

    @Override
    public List<VipLabelsRel> switchDbCurSor(ArrayList list) throws Exception {
        List<VipLabelsRel> list_rel=new ArrayList<VipLabelsRel>();
        for (int i = 0; i < list.size(); i++) {
            VipLabelsRel vipLabelsRel=new VipLabelsRel();
            Map<String,Object> map= (Map<String, Object>) list.get(i);
            vipLabelsRel.setId(map.get("id").toString());
            vipLabelsRel.setLabel_id(map.get("label_id").toString());
            vipLabelsRel.setVip_code(map.get("vip_id").toString());
            vipLabelsRel.setCorp_code(map.get("corp_code").toString());
            vipLabelsRel.setCreated_date(map.get("created_date").toString());
            vipLabelsRel.setCreater(map.get("operate_user_code").toString());
            vipLabelsRel.setLabel_name(map.get("label_name").toString());
            vipLabelsRel.setUser_name(map.get("operate_user_name").toString());
            String vip=map.get("vip").toString();
            JSONObject json= JSON.parseObject(vip);
            vipLabelsRel.setStore_code(json.getString("store_code"));
            vipLabelsRel.setIsactive(json.getString("fr_active"));
            vipLabelsRel.setVip_name(json.getString("vip_name"));
            vipLabelsRel.setVip_card_no(json.getString("cardno"));
            vipLabelsRel.setStore_name(json.getString("store_name"));
            list_rel.add(vipLabelsRel);
        }
        return list_rel;
    }

    @Override
    public BasicDBObject getScreenObject(JSONObject jsonObject) throws Exception {
        BasicDBList basic_list=new BasicDBList();
        BasicDBObject basic_obj=new BasicDBObject();
        JSONArray jsonArray=jsonObject.getJSONArray("list");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject json_obj = jsonArray.getJSONObject(i);
            String screen_key = json_obj.getString("screen_key");
            String screen_value = json_obj.getString("screen_value");
            Pattern pattern = null;
            if (!screen_key.equals("created_date")) {
                pattern = MongoHelperServiceImpl.screen_valueScreen(screen_value);
//                pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
            }
            if (screen_key.equals("vip_card_no") && !screen_value.equals("")) {
                basic_list.add(new BasicDBObject("vip.cardno", pattern));
            } else if (screen_key.equals("vip_name") && !screen_value.equals("")) {
                basic_list.add(new BasicDBObject("vip.vip_name", pattern));
            } else if (screen_key.equals("label_name") && !screen_value.equals("")) {
                basic_list.add(new BasicDBObject("label_name", pattern));
            } else if (screen_key.equals("store_name") && !screen_value.equals("")) {
                basic_list.add(new BasicDBObject("vip.store_name", pattern));
            } else if (screen_key.equals("user_name") && !screen_value.equals("")) {
                basic_list.add(new BasicDBObject("operate_user_name", pattern));
            } else if (screen_key.equals("created_date")) {
                JSONObject date = JSON.parseObject(screen_value);
                if (!date.getString("start").equals("")) {
                      /*  Pattern pattern = Pattern.compile("^.*" + date.getString("start") + ".*$", Pattern.CASE_INSENSITIVE);*/
                    basic_list.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.GTE, date.getString("start") + " 00:00:00")));
                }
                if (!date.getString("end").equals("")) {
                      /*  Pattern pattern = Pattern.compile("^.*" + date.getString("end") + ".*$", Pattern.CASE_INSENSITIVE);*/
                    basic_list.add(new BasicDBObject("created_date", new BasicDBObject(QueryOperators.LTE, date.getString("end") + " 23:59:59")));
                }
            }
        }
        if(basic_list.size()>0){
            basic_obj.put("$and",basic_list);
        }
        return  basic_obj;
    }

}
