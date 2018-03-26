
package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.AppLoginLogMapper;
import com.bizvane.ishop.entity.AppLoginLog;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.service.AppLoginLogService;
import com.bizvane.ishop.service.BrandService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/8/24.
 */
@Service
public class AppLoginLogServiceImpl implements AppLoginLogService {
    @Autowired
    private AppLoginLogMapper loginLogMapper;
    @Autowired
    private StoreService storeService;
    @Autowired
    private BrandService brandService;

    @Override
    public PageInfo<AppLoginLog> selectAllAppLoginLog(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<AppLoginLog> appLoginLogs = loginLogMapper.selectAllAppLoginLog(corp_code, search_value);
//        for (AppLoginLog appLoginLog : appLoginLogs) {
//
//            appLoginLog.setIsactive(CheckUtils.CheckIsactive(appLoginLog.getIsactive()));
//        }
        PageInfo<AppLoginLog> page = new PageInfo<AppLoginLog>(appLoginLogs);
        List<AppLoginLog> logs = transFormat(page.getList());
        page.setList(logs);
        return page;
    }

    @Override
    public PageInfo<AppLoginLog> selectAllScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);

        //登陆日期时间段
        JSONObject date = JSONObject.parseObject(map.get("created_date"));
        params.put("created_date_start", date.get("start").toString());
        params.put("created_date_end", date.get("end").toString());
        map.remove("created_date");

        //登陆次数
        JSONObject time_count = JSONObject.parseObject(map.get("time"));
        String type = time_count.get("type").toString();
        String value = time_count.get("value").toString();
        if (type.equals("gt")) {
            //大于
            params.put("type", "gt");
            params.put("time_count", value);
        } else if (type.equals("lt")) {
            //小于
            params.put("type", "lt");
            params.put("time_count", value);
        } else if (type.equals("between")) {
            //介于
            JSONObject values = JSONObject.parseObject(value);
            params.put("type", "between");
            params.put("count_start", values.get("start").toString());
            params.put("count_end", values.get("end").toString());
        } else if (type.equals("eq")) {
            //等于
            params.put("type", "eq");
            params.put("time_count", value);
        } else if (type.equals("all")) {
            params.put("type", "all");
            params.put("time_count", value);
        }
        map.remove("time");

        //在职状态
        String user_can_login = map.get("user_can_login");
        if (user_can_login.replace("'", "").equals(Common.IS_ACTIVE_N)) {
            params.put("user_no_login", "N");
        } else {
            params.put("user_can_login", "Y");
        }
        map.remove("user_can_login");

        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<AppLoginLog> list = loginLogMapper.selectAllScreen(params);
        for (AppLoginLog appLoginLog : list) {
            appLoginLog.setIsactive(CheckUtils.CheckIsactive(appLoginLog.getIsactive()));
        }
        PageInfo<AppLoginLog> page = new PageInfo<AppLoginLog>(list);
        List<AppLoginLog> logs = transFormat(page.getList());
        page.setList(logs);
        return page;
    }


    @Override
    public int delAppLoginlogById(int id) throws Exception {
        return loginLogMapper.delAppLoginlogById(id);
    }


    @Override
    public AppLoginLog selByLogId(int id) throws Exception {
        return loginLogMapper.selByLogId(id);
    }

    public List<AppLoginLog> transFormat(List<AppLoginLog> appLoginLogs) throws Exception {
        List<AppLoginLog> appLoginLogs1 = new ArrayList<AppLoginLog>();
        for (AppLoginLog appLoginLog : appLoginLogs) {
            appLoginLog.setIsactive(CheckUtils.CheckIsactive(appLoginLog.getIsactive()));
            appLoginLog.setBrand_name("");
            if (appLoginLog.getStore_name() != null && !appLoginLog.getStore_name().equals("") && appLoginLog.getCorp_code() != null
                    && !appLoginLog.getCorp_code().equals("")) {
//                List<Store> stores = storeService.getStoreByName(appLoginLog.getCorp_code(), appLoginLog.getStore_name(), Common.IS_ACTIVE_Y);
//                if (stores.size() > 0) {
//                    String store_code = stores.get(0).getStore_code();
//                    String brand_code = stores.get(0).getBrand_code();
                String store_code = appLoginLog.getStore_code();
                String brand_code = appLoginLog.getBrand_code();
                if (brand_code != null && !brand_code.equals("")) {
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    String[] ids = brand_code.split(",");
                    String brand_name = "";
                    for (int i = 0; i < ids.length; i++) {
                        Brand brand = brandService.getBrandByCode(appLoginLog.getCorp_code(), ids[i], Common.IS_ACTIVE_Y);
                        if (brand != null) {
                            String brand_name1 = brand.getBrand_name();
                            brand_name = brand_name + brand_name1 + "、";
                        }
                    }
                    if (brand_name.endsWith("、"))
                        brand_name = brand_name.substring(0, brand_name.length() - 1);
                    appLoginLog.setBrand_name(brand_name);
                }
                appLoginLog.setStore_code(store_code);
                // }
            }
            String can_login = appLoginLog.getUser_can_login();
            String isactive = appLoginLog.getUser_isactive();
            if (can_login.equals(Common.IS_ACTIVE_N) || isactive.equals(Common.IS_ACTIVE_N)) {
                appLoginLog.setUser_can_login("离职");
            } else {
                appLoginLog.setUser_can_login("在职");
            }
            appLoginLogs1.add(appLoginLog);
        }
        return appLoginLogs1;
    }


}
