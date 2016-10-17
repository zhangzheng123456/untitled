
package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.dao.AppLoginLogMapper;
import com.bizvane.ishop.entity.AppLoginLog;
import com.bizvane.ishop.service.AppLoginLogService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Override
    public PageInfo<AppLoginLog> selectAllAppLoginLog(int page_number, int page_size,String corp_code, String search_value) {
        PageHelper.startPage(page_number, page_size);
        List<AppLoginLog> appLoginLogs = loginLogMapper.selectAllAppLoginLog(corp_code, search_value);
        for (AppLoginLog appLoginLog:appLoginLogs) {
            appLoginLog.setIsactive(CheckUtils.CheckIsactive(appLoginLog.getIsactive()));
        }
        PageInfo<AppLoginLog> page = new PageInfo<AppLoginLog>(appLoginLogs);
        return page;
    }

    @Override
    public PageInfo<AppLoginLog> selectAllScreen(int page_number, int page_size, String corp_code,Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        JSONObject date = JSONObject.parseObject(map.get("time_bucket"));
//        JSONObject time_count = JSONObject.parseObject(map.get("time_count"));
//        String type=time_count.get("type").toString();
//        String value=time_count.get("value").toString();
        params.put("created_date_start", date.get("start").toString());
        params.put("created_date_end", date.get("end").toString());
        params.put("corp_code", corp_code);
        map.remove("time_bucket");
//        if(type.equals("gt")){
//            //大于
//            params.put("type", "gt");
//            params.put("time_count",value);
//        }else if(type.equals("lt")){
//            //小于
//            params.put("type", "lt");
//            params.put("time_count", value);
//        }else if(type.equals("between")){
//            //介于
//            JSONObject values = JSONObject.parseObject(value);
//            params.put("type", "between");
//            params.put("count_start", values.get("start").toString());
//            params.put("count_end", values.get("end").toString());
//        }else if(type.equals("eq")){
//            //等于
//            params.put("type", "eq");
//            params.put("time_count", value);
//        }
        map.remove("time_count");
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<AppLoginLog> list = loginLogMapper.selectAllScreen(params);
        for (AppLoginLog appLoginLog:list) {
            appLoginLog.setIsactive(CheckUtils.CheckIsactive(appLoginLog.getIsactive()));
        }
        PageInfo<AppLoginLog> page = new PageInfo<AppLoginLog>(list);
        return page;
    }

    @Override
    public int delAppLoginlogById(int id) {
        return loginLogMapper.delAppLoginlogById(id);
    }
}
