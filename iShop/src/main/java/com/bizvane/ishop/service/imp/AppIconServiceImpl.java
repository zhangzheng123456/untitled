package com.bizvane.ishop.service.imp;


import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.AppIconCfgMapper;
import com.bizvane.ishop.dao.AppIconMapper;

import com.bizvane.ishop.entity.*;

import com.bizvane.ishop.service.AppIconService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by nanji on 2016/11/15.
 */
@Service
public class AppIconServiceImpl implements AppIconService {


    @Autowired
    AppIconCfgMapper appIconCfgMapper;
    @Autowired
    AppIconMapper appIconMapper;


    @Override
    public AppIcon getAppIconById(int id) throws Exception {
        return appIconMapper.selAppIconById(id);
    }

    @Override
    public PageInfo<AppIcon> selectAllIcons(int page_num, int page_size, String search_value) throws Exception {
        List<AppIcon> appIcons;
        PageHelper.startPage(page_num, page_size);
        appIcons = appIconMapper.selectAllIconNames(search_value);
//        for (AppIcon appIcon : appIcons) {
//            appIcon.setIsactive(CheckUtils.CheckIsactive(appIcon.getIsactive()));
//
//        }
        PageInfo<AppIcon> page = new PageInfo<AppIcon>(appIcons);
        return page;
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String status = "";
        JSONObject jsonObject = JSONObject.parseObject(message);
        Date now = new Date();
        String icon_name = jsonObject.get("icon_name").toString().trim();
        String isactive = jsonObject.get("isactive").toString().trim();
        String id = jsonObject.get("id").toString().trim();

        AppIcon appIcon = getAppIconById(Integer.parseInt(id));


        AppIcon appIcon1 = getAppIconByName( Common.IS_ACTIVE_Y,icon_name);
        if (appIcon1 != null && !id.equals(appIcon1.getId())) {
            status = "该名称已存在";
        } else {
            appIcon.setId(id);
            appIcon.setIcon_name(icon_name);
            appIcon.setModified_date(Common.DATETIME_FORMAT.format(now));
            appIcon.setModifier(user_id);
            appIcon.setIsactive(isactive);
            int num = appIconMapper.updateAppIcon(appIcon);
            if (num > 0) {
                status = Common.DATABEAN_CODE_SUCCESS;
            } else {
                status = Common.DATABEAN_CODE_ERROR;
            }
        }
        return status;
    }

    @Override
    public AppIcon getAppIconByName(String isactive, String icon_name) throws Exception {
        return appIconMapper.selAppIconByName(Common.IS_ACTIVE_Y,icon_name);
    }

    @Override
    public String insert(String message, String user_id) throws Exception {
        String status = "";
//        JSONObject jsonObject = JSONObject.parseObject(message);
//        Date now = new Date();
//        String isactive = jsonObject.get("isactive").toString().trim();
//        AppIcon appIcon = WebUtils.JSON2Bean(jsonObject, AppIcon.class);
//
//
//        AppIcon name =this.getAppIconByName(appIcon.getIsactive(),appIcon.getIcon_name()) ;
//
//        if ( name == null) {
//            String order = appIconMapper.selectMaxOrder();
//            if (order == null || order.equals("")){
//                order = "0";
//            }else {
//                order = String.valueOf(Integer.parseInt(order)+1);
//            }
//            appIcon.setShow_order(order);
//            appIcon.setModified_date(Common.DATETIME_FORMAT.format(now));
//            appIcon.setCreater(user_id);
//            appIcon.setModifier(user_id);
//            appIcon.setCreated_date(Common.DATETIME_FORMAT.format(now));
//            appIcon.setIsactive(isactive);
//            int num = 0;
//            num = appIconMapper.insertAppIcon(appIcon);
//            if (num > 0) {
//                AppIcon appIcon1 = this.getAppIconByName(appIcon.getIsactive(), appIcon.getIcon_name());
//                status = appIcon1.getId();
//            } else {
//                status = Common.DATABEAN_CODE_ERROR;
//            }
//        }  else {
//            status = "该名称已存在";
//        }
        return status;
    }

    @Override
    public void updateShowOrder(String icon_name, String show_order) throws Exception {
        AppIcon appIcon = this.getAppIconByName(Common.IS_ACTIVE_Y,icon_name);
        if (appIcon != null) {
            appIcon.setShow_order(show_order);
            appIconMapper.updateAppIcon(appIcon);
        }
    }
}

