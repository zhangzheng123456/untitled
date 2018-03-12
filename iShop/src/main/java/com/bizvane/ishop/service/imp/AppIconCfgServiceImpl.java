package com.bizvane.ishop.service.imp;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.AppIconCfgMapper;
import com.bizvane.ishop.dao.AppIconMapper;

import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by nanji on 2016/11/15.
 */

@Service
public class AppIconCfgServiceImpl implements AppIconCfgService {

    @Autowired
    private AppIconCfgMapper appIconCfgMapper;
    @Autowired
    private AppIconMapper appIconMapper;

    @Override
    public PageInfo<AppIconCfg> selectAllIconCfg(int page_num, int page_size, String corp_code, String search_value) throws Exception {
        List<AppIconCfg> AppIconCfgs;
        PageHelper.startPage(page_num, page_size);
        AppIconCfgs = appIconCfgMapper.selectAllIconCfg(corp_code, search_value,null);

        for (AppIconCfg AppIconCfg : AppIconCfgs) {
            AppIconCfg.setIsactive(CheckUtils.CheckIsactive(AppIconCfg.getIsactive()));
            String order = AppIconCfg.getIcon_order();
            JSONArray arr = JSON.parseArray(order);
            JSONArray arr_icon=new JSONArray();
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String isactive = obj.getString("isactive");
                String icon_name = obj.getString("icon_name");
                if (isactive.equals("Y")) {
                    arr_icon.add(icon_name);
                }

            }
            AppIconCfg.setIcon_order(arr_icon.toJSONString());

        }
        PageInfo<AppIconCfg> page = new PageInfo<AppIconCfg>(AppIconCfgs);

        return page;
    }

    @Override
    public PageInfo<AppIconCfg> selectAllIconCfg(int page_num, int page_size, String corp_code, String search_value,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        List<AppIconCfg> AppIconCfgs;
        PageHelper.startPage(page_num, page_size);
        AppIconCfgs = appIconCfgMapper.selectAllIconCfg(corp_code, search_value,manager_corp_arr);

        for (AppIconCfg AppIconCfg : AppIconCfgs) {
            AppIconCfg.setIsactive(CheckUtils.CheckIsactive(AppIconCfg.getIsactive()));
            String order = AppIconCfg.getIcon_order();
            JSONArray arr = JSON.parseArray(order);
            JSONArray arr_icon=new JSONArray();
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String isactive = obj.getString("isactive");
                String icon_name = obj.getString("icon_name");
                if (isactive.equals("Y")) {
                    arr_icon.add(icon_name);
                }

            }
            AppIconCfg.setIcon_order(arr_icon.toJSONString());

        }
        PageInfo<AppIconCfg> page = new PageInfo<AppIconCfg>(AppIconCfgs);

        return page;
    }

    @Override
    public PageInfo<AppIconCfg> selectIconCfgAllScreen(int page_num, int page_size, String corp_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        PageHelper.startPage(page_num, page_size);
        List<AppIconCfg> list1 = appIconCfgMapper.selectIconCfgScreen(params);
        for (AppIconCfg AppIconCfg : list1) {
            AppIconCfg.setIsactive(CheckUtils.CheckIsactive(AppIconCfg.getIsactive()));
            String order = AppIconCfg.getIcon_order();
            JSONArray arr = JSON.parseArray(order);
            JSONArray arr_icon=new JSONArray();
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String isactive = obj.getString("isactive");
                String icon_name = obj.getString("icon_name");
                if (isactive.equals("Y")) {
                    arr_icon.add(icon_name);
                }

            }
            AppIconCfg.setIcon_order(arr_icon.toJSONString());
        }
        PageInfo<AppIconCfg> page = new PageInfo<AppIconCfg>(list1);
        return page;
    }


    @Override
    public int delete(int id) throws Exception {
        return appIconCfgMapper.delIconCfgById(id);
    }

    @Override
    public String insert(AppIconCfg appIconCfg) throws Exception {
        String result = null;

        int m = appIconCfgMapper.insertIconCfg(appIconCfg);
        if (m > 0) {
            result = Common.DATABEAN_CODE_SUCCESS;
        } else {
            result = Common.DATABEAN_CODE_ERROR;
        }

        return result;
    }

    @Override
    public String update(AppIconCfg appIconCfg) throws Exception {
        String status = "";


        String corp_code = appIconCfg.getCorp_code();
        AppIconCfg appIconCfg1 = getIconCfgByCorp(corp_code, Common.IS_ACTIVE_Y);
        if (appIconCfg1 == null || appIconCfg.getId().equals(appIconCfg1.getId())) {

            int num = appIconCfgMapper.updateIconCfg(appIconCfg);
            if (num > 0) {
                status = Common.DATABEAN_CODE_SUCCESS;
            } else {
                status = Common.DATABEAN_CODE_ERROR;
            }
        } else {
            int k = delete(Integer.parseInt(appIconCfg1.getId()));
            if (k > 0) {
                appIconCfg.setId(appIconCfg.getId());
                int num = appIconCfgMapper.updateIconCfg(appIconCfg);
                if (num > 0) {
                    status = Common.DATABEAN_CODE_SUCCESS;
                } else {
                    status = Common.DATABEAN_CODE_ERROR;
                }
            }
        }
        return status;
    }

    @Override
    public AppIconCfg getIconCfgById(int id) throws Exception {
        return appIconCfgMapper.selActivityById(id);
    }

    @Override
    public AppIconCfg getIconCfgByCorp(String corp_code, String isactive) throws Exception {
        return appIconCfgMapper.selActivityByCorp(corp_code, isactive);
    }


}

