package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.AppHelpMapper;
import com.bizvane.ishop.entity.AppHelp;
import com.bizvane.ishop.entity.VipCardType;
import com.bizvane.ishop.entity.VipParam;
import com.bizvane.ishop.service.AppHelpService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/2/21.
 */
@Service
public class AppHelpServiceImpl implements AppHelpService {
    @Autowired
    AppHelpMapper appHelpMapper;


    @Override
    public AppHelp getAppHelpById(int id) throws Exception {
        AppHelp appHelp = appHelpMapper.selAppHelpById(id);
        return appHelp;
    }

    @Override
    public PageInfo<AppHelp> getAppHelpByPage(int page_number, int page_size, String search_value) throws Exception {
        List<AppHelp> appHelps;
        PageHelper.startPage(page_number, page_size);
        appHelps = appHelpMapper.selectAllHelp( search_value);
        for (AppHelp appHelps1 : appHelps) {
            appHelps1.setIsactive(CheckUtils.CheckIsactive(appHelps1.getIsactive()));
        }
        PageInfo<AppHelp> page = new PageInfo<AppHelp>(appHelps);

        return page;
    }

    @Override
    public String insert(String message, String user_id) throws Exception {
        String status = "";
        JSONObject jsonObject = JSONObject.parseObject(message);
        Date now = new Date();
        String isactive = jsonObject.get("isactive").toString().trim();
        String app_help_code = "AH"+Common.DATETIME_FORMAT_DAY_NUM.format(now);
        AppHelp appHelp = WebUtils.JSON2Bean(jsonObject, AppHelp.class);


        AppHelp name = getAppHelpByName( appHelp.getApp_help_name(), appHelp.getIsactive());

        if ( name == null) {
            String order = appHelpMapper.selectMaxOrder();
            if (order == null || order.equals("")){
                order = "0";
            }else {
                order = String.valueOf(Integer.parseInt(order)+1);
            }
            appHelp.setApp_help_code(app_help_code);
            appHelp.setShow_order(order);
            appHelp.setModified_date(Common.DATETIME_FORMAT.format(now));
            appHelp.setCreater(user_id);
            appHelp.setModifier(user_id);
            appHelp.setCreated_date(Common.DATETIME_FORMAT.format(now));
            appHelp.setIsactive(isactive);
            int num = 0;
            num = appHelpMapper.insertAppHelp(appHelp);
            if (num > 0) {
                AppHelp appHelp1 = getAppHelpByName( appHelp.getApp_help_name(), appHelp.getIsactive());
                status = appHelp1.getId();
            } else {
                status = Common.DATABEAN_CODE_ERROR;
            }
        }  else {
            status = "该名称已存在";
        }
        return status;
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String status = "";
        JSONObject jsonObject = JSONObject.parseObject(message);
        Date now = new Date();
        String app_help_name = jsonObject.get("app_help_name").toString().trim();

        String isactive = jsonObject.get("isactive").toString().trim();
        String id = jsonObject.get("id").toString().trim();

        AppHelp appHelp = getAppHelpById(Integer.parseInt(id));


        AppHelp appHelp2 = getAppHelpByName( app_help_name, Common.IS_ACTIVE_Y);
        if (appHelp2 != null && !id.equals(appHelp2.getId())) {
            status = "该名称已存在";
        } else {
             appHelp.setId(id);

            appHelp.setApp_help_name(app_help_name);
            appHelp.setModified_date(Common.DATETIME_FORMAT.format(now));
            appHelp.setModifier(user_id);
            appHelp.setIsactive(isactive);
            int num = appHelpMapper.updateAppHelp(appHelp);
            if (num > 0) {
                status = Common.DATABEAN_CODE_SUCCESS;
            } else {
                status = Common.DATABEAN_CODE_ERROR;
            }
        }
        return status;
    }

    @Override
    public int delete(int id) throws Exception {
        return appHelpMapper.delAppHelpById(id);
    }

    @Override
    public PageInfo<AppHelp> getAppHelpScreen(int page_number, int page_size, Map<String, String> map) throws Exception {
         Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<AppHelp> list1 = appHelpMapper.selectAppHelpScreen(params);
        for (AppHelp appHelp : list1) {
            appHelp.setIsactive(CheckUtils.CheckIsactive(appHelp.getIsactive()));
        }
        PageInfo<AppHelp> page = new PageInfo<AppHelp>(list1);
        return page;
    }

    @Override
    @Transactional
    public void updateShowOrder(int id,String show_order) throws Exception {
        AppHelp appHelp = getAppHelpById(id);
        if (appHelp != null) {
            appHelp.setShow_order(show_order);
            appHelpMapper.updateAppHelp(appHelp);
        }
    }

    @Override
    public AppHelp getAppHelpByName( String app_help_name, String isactive) throws Exception {
        return appHelpMapper.selAppHelpByName( app_help_name, isactive);
    }

    @Override
    public List<AppHelp> getAppHelps( String isactive) throws Exception {
        return appHelpMapper.selectHelps(isactive);
    }
}

