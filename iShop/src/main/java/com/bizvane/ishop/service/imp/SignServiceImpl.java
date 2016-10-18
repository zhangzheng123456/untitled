package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.SignMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.SignService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/23.
 */
@Service
public class SignServiceImpl implements SignService {
    @Autowired
    private SignMapper signMapper;
    @Autowired
    StoreService storeService;

    @Override
    public PageInfo<Sign> selectSignByInp(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String role_code) throws Exception {
        String[] stores = null;
        if (!store_code.equals("")) {
            store_code = store_code.replace(Common.SPECIAL_HEAD,"");
            stores = store_code.split(",");
//            for (int i = 0; i < stores.length; i++) {
//                if (!stores[i].startsWith(Common.SPECIAL_HEAD)) {
//                    stores[i] = Common.SPECIAL_HEAD + stores[i];
//                }
//                stores[i] = stores[i].substring(1, stores[i].length());
//
//            }
        }
        if (!area_code.equals("")) {
            area_code = area_code.replace(Common.SPECIAL_HEAD,"");
            String[] areas = area_code.split(",");
//            for (int i = 0; i < areas.length; i++) {
//                areas[i] = areas[i].substring(1, areas[i].length());
//            }
            List<Store> store = storeService.selectByAreaBrand(corp_code, areas, "");
            String a = "";
            for (int i = 0; i < store.size(); i++) {
                a = a + store.get(i).getStore_code() + ",";
            }
            stores = a.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
        params.put("search_value", search_value);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        List<Sign> signs;
        PageHelper.startPage(page_number, page_size);
        signs = signMapper.selectSignByInp(params);
        for (Sign sign:signs) {
            sign.setIsactive(CheckUtils.CheckIsactive(sign.getIsactive()));
            //0是签到，-1是签退
            if(sign.getStatus()==null||sign.getStatus().equals("")){
                sign.setStatus("");
            }else if(sign.getStatus().equals(Common.STATUS_SIGN_IN)){
                sign.setStatus("签到");
            }else{
                sign.setStatus("签退");
            }
        }
        PageInfo<Sign> page = new PageInfo<Sign>(signs);
        return page;
    }

    @Override
    public PageInfo<Sign> selectByUser(int page_number, int page_size, String corp_code, String user_code, String search_value) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<Sign> signs = signMapper.selectByUser(corp_code, user_code, search_value);
        for (Sign sign:signs) {
            sign.setIsactive(CheckUtils.CheckIsactive(sign.getIsactive()));
            //0是签到，-1是签退
            if(sign.getStatus()==null||sign.getStatus().equals("")){
                sign.setStatus("");
            }else  if(sign.getStatus().equals("0")){
                sign.setStatus("签到");
            }else{
                sign.setStatus("签退");
            }
        }
        PageInfo<Sign> page = new PageInfo<Sign>(signs);
        return page;
    }

    @Override
    public int delSignById(int id) throws Exception{
        return signMapper.delSignById(id);
    }

    @Override
    public PageInfo<Sign> selectSignAllScreen(int page_number, int page_size, String corp_code, String area_code, String store_code, String role_code, Map<String, String> map) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        String[] stores = null;
        if (!store_code.equals("")) {
            stores = store_code.split(",");
            for (int i = 0; null != stores && i < stores.length; i++) {
                stores[i] = stores[i].substring(1, stores[i].length());
            }
        }
        if (!area_code.equals("")) {
            String[] areas = area_code.split(",");
            for (int i = 0; null != stores && i < stores.length; i++) {
                areas[i] = areas[i].substring(1, areas[i].length());
            }
            List<Store> stores1 = storeService.selectByAreaBrand(corp_code, areas, "");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < stores1.size(); i++) {
                sb.append(stores1.get(i).getStore_code()).append(",");
            }
            stores = sb.toString().split(",");
        }

        JSONObject date = JSONObject.parseObject(map.get("time_bucket"));
        params.put("created_date_start", date.get("start").toString());
        params.put("created_date_end", date.get("end").toString());
        map.remove("time_bucket");

        params.put("array", stores);
        params.put("corp_code", corp_code);
        params.put("store_code", store_code);
        params.put("role_code", role_code);
        params.put("map", map);
        List<Sign> signs;
        PageHelper.startPage(page_number, page_size);
        signs = signMapper.selectSignAllScreen(params);
        for (Sign sign:signs) {
            sign.setIsactive(CheckUtils.CheckIsactive(sign.getIsactive()));
            //0是签到，-1是签退
            if(sign.getStatus()==null||sign.getStatus().equals("")){
                sign.setStatus("");
            }else   if(sign.getStatus().equals("0")){
                sign.setStatus("签到");
            }else{
                sign.setStatus("签退");
            }
        }
        PageInfo<Sign> page = new PageInfo<Sign>(signs);
        return page;
    }

    @Override
    public PageInfo<Sign> selectSignAllScreenByUser(int page_number, int page_size, String corp_code, String user_code, Map<String, String> map) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        JSONObject date = JSONObject.parseObject(map.get("time_bucket"));
        params.put("created_date_start", date.get("start").toString());
        params.put("created_date_end", date.get("end").toString());
        map.remove("time_bucket");

        params.put("corp_code", corp_code);
        params.put("user_code", user_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<Sign> list = signMapper.selectSignAllScreenUser(params);
        for (Sign sign:list) {
            sign.setIsactive(CheckUtils.CheckIsactive(sign.getIsactive()));
            //0是签到，-1是签退
            if(sign.getStatus()==null||sign.getStatus().equals("")){
                sign.setStatus("");
            }else   if(sign.getStatus().equals("0")){
                sign.setStatus("签到");
            }else{
                sign.setStatus("签退");
            }
        }
        PageInfo<Sign> page = new PageInfo<Sign>(list);
        return page;
    }

    @Override
    public int insert(Sign sign) throws Exception{
        return signMapper.insert(sign);
    }

    @Override
    public int deleteByUser(String user_code,String corp_code)throws Exception{
        return signMapper.delSignByUser(user_code,corp_code);
    }

    @Override
    public List<Sign> selectUserRecord(String corp_code, String user_code, String date,String status) throws Exception {
        List<Sign> signs = signMapper.selectUserRecord(corp_code, user_code, date,status);
        return signs;
    }

}
