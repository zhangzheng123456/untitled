package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.SignMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.SignService;
import com.bizvane.ishop.service.StoreService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
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
            List<Store> stores1 = storeService.selectByAreaCode(corp_code, areas, "");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < stores1.size(); i++) {
                sb.append(stores1.get(i).getStore_code()).append(",");
            }
            stores = sb.toString().split(",");
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
            if(sign.getIsactive().equals("Y")){
                sign.setIsactive("是");
            }else{
                sign.setIsactive("否");
            }
            //0是签到，-1是签退
            if(sign.getStatus().equals("0")){
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
            if(sign.getIsactive().equals("Y")){
                sign.setIsactive("是");
            }else{
                sign.setIsactive("否");
            }
            //0是签到，-1是签退
            if(sign.getStatus().equals("0")){
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
            List<Store> stores1 = storeService.selectByAreaCode(corp_code, areas, "");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < stores1.size(); i++) {
                sb.append(stores1.get(i).getStore_code()).append(",");
            }
            stores = sb.toString().split(",");
        }
        params.put("array", stores);
        params.put("corp_code", corp_code);
        params.put("store_code", store_code);
        params.put("role_code", role_code);
        params.put("map", map);
        List<Sign> signs;
        PageHelper.startPage(page_number, page_size);
        signs = signMapper.selectSignAllScreen(params);
        for (Sign sign:signs) {
            if(sign.getIsactive().equals("Y")){
                sign.setIsactive("是");
            }else{
                sign.setIsactive("否");
            }
            //0是签到，-1是签退
            if(sign.getStatus().equals("0")){
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
        params.put("corp_code", corp_code);
        params.put("user_code", user_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<Sign> list = signMapper.selectSignAllScreenUser(params);
        for (Sign sign:list) {
            if(sign.getIsactive().equals("Y")){
                sign.setIsactive("是");
            }else{
                sign.setIsactive("否");
            }
            //0是签到，-1是签退
            if(sign.getStatus().equals("0")){
                sign.setStatus("签到");
            }else{
                sign.setStatus("签退");
            }
        }
        PageInfo<Sign> page = new PageInfo<Sign>(list);
        return page;
    }
}
