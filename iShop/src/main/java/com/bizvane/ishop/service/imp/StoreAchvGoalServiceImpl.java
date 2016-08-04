package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.StoreAchvGoalMapper;
import com.bizvane.ishop.entity.Interfacers;
import com.bizvane.ishop.entity.StoreAchvGoal;
import com.bizvane.ishop.service.StoreAchvGoalService;
import com.bizvane.ishop.utils.TimeUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.ognl.ObjectElementsAccessor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
@Service
public class StoreAchvGoalServiceImpl implements StoreAchvGoalService {

    @Autowired
    StoreAchvGoalMapper storeAchvGoalMapper = null;
    private static final Logger log = Logger.getLogger(StoreServiceImpl.class);
    String[] arg = new String[]{""};


    @Override
    public String  update(StoreAchvGoal storeAchvGoal) throws Exception{


        int count = -1;
        if (storeAchvGoal.getTarget_time().equals(Common.TIME_TYPE_WEEK)) {
            String time = storeAchvGoal.getTarget_time();
            String week = TimeUtils.getWeek(time);
            storeAchvGoal.setTarget_time(week);
        }
        StoreAchvGoal oldStoreAchvGoal = storeAchvGoalMapper.selectById(storeAchvGoal.getId());
        if (oldStoreAchvGoal.getCorp_code().equalsIgnoreCase(storeAchvGoal.getCorp_code())
                && oldStoreAchvGoal.getStore_code().equalsIgnoreCase(storeAchvGoal.getStore_code())
                && oldStoreAchvGoal.getTime_type().equalsIgnoreCase(storeAchvGoal.getTime_type())
                && oldStoreAchvGoal.getTarget_time().equalsIgnoreCase(storeAchvGoal.getTarget_time())
                ) {
            storeAchvGoalMapper.update(storeAchvGoal);
            return Common.DATABEAN_CODE_SUCCESS;
        } else {
            count=storeAchvGoalMapper.selectStoreAchvCountType(storeAchvGoal.getCorp_code(),storeAchvGoal.getStore_code(),storeAchvGoal.getTime_type(),storeAchvGoal.getTarget_time());
            if (count > 0) {
                return Common.DATABEAN_CODE_ERROR;
            }
            storeAchvGoalMapper.update(storeAchvGoal);
            return Common.DATABEAN_CODE_SUCCESS;
        }
    }

    @Override
    public PageInfo<StoreAchvGoal> selectBySearch(int page_number, int page_size, String corp_code, String area_code, String store_code, String search_value)
            throws SQLException {
        String[] area_codes = null;
        String[] store_codes = null;

        if (!area_code.equals("")) {
            area_codes = area_code.split(",");
            for (int i = 0; i < area_codes.length; i++) {
                area_codes[i] = area_codes[i].substring(1, area_codes[i].length());
            }
        }
        if (!store_code.equals("")) {
            store_codes = store_code.split(",");
            for (int i = 0; i < store_codes.length; i++) {
                store_codes[i] = store_codes[i].substring(1, store_codes[i].length());
            }
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_codes", area_codes);
        params.put("store_codes", store_codes);
        params.put("search_value", search_value);
        List<StoreAchvGoal> storeAchvGoals;
        PageHelper.startPage(page_number, page_size);
        storeAchvGoals = storeAchvGoalMapper.selectBySearch(params);
        for (StoreAchvGoal storeAchvGoal:storeAchvGoals) {
            if(storeAchvGoal.getIsactive().equals("Y")){
                storeAchvGoal.setIsactive("是");
            }else{
                storeAchvGoal.setIsactive("否");
            }
            if(storeAchvGoal.getTime_type()==null) {
                storeAchvGoal.setTime_type("未设定");
            }else if(storeAchvGoal.getTime_type().equals("D")){
                storeAchvGoal.setTime_type("日");
            }else if(storeAchvGoal.getTime_type().equals("W")){
                storeAchvGoal.setTime_type("周");
            }else if(storeAchvGoal.getTime_type().equals("M")){
                storeAchvGoal.setTime_type("月");
            }else if(storeAchvGoal.getTime_type().equals("Y")){
                storeAchvGoal.setTime_type("年");
            }
        }
        PageInfo<StoreAchvGoal> page = new PageInfo<StoreAchvGoal>(storeAchvGoals);
        return page;
    }

    @Override
    public String storeAchvExist(String corp_code, String store_code) {
        //this.storeAchvGoalMapper.selectById(1);
        try {
            if (null != this.storeAchvGoalMapper.selectByCorpAndUserCode(corp_code, store_code)) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        } catch (Exception ex) {
            return Common.DATABEAN_CODE_ERROR;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public PageInfo<StoreAchvGoal> getAllStoreAchvScreen(int page_number, int page_size, String corp_code, String area_code, String store_code, Map<String, String> map) {
        String[] area_codes = null;
        String[] store_codes = null;
        if (!area_code.equals("")) {
            area_codes = area_code.split(",");
            for (int i = 0; area_codes != null && area_codes.length > i; i++) {
                area_codes[i] = area_codes[i].substring(1, area_codes.length);
            }
        }
        if (!store_code.equals("")) {
            store_codes = store_code.split(",");
            for (int i = 0; store_codes != null && i < area_codes.length; i++) {
                store_codes[i] = store_codes[i].substring(1, store_codes[i].length());
            }
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_codes", area_codes);
        params.put("store_codes", store_codes);
        params.put("map", map);
        List<StoreAchvGoal> storeAchvGoals;
        PageHelper.startPage(page_number, page_size);
        storeAchvGoals = storeAchvGoalMapper.selectAllStoreAchvScreen(params);
        for (StoreAchvGoal storeAchvGoal:storeAchvGoals) {
            if(storeAchvGoal.getIsactive().equals("Y")){
                storeAchvGoal.setIsactive("是");
            }else{
                storeAchvGoal.setIsactive("否");
            }
            if(storeAchvGoal.getTime_type()==null) {
                storeAchvGoal.setTime_type("未设定");
            }else if(storeAchvGoal.getTime_type().equals("D")){
                storeAchvGoal.setTime_type("日");
            }else if(storeAchvGoal.getTime_type().equals("W")){
                storeAchvGoal.setTime_type("周");
            }else if(storeAchvGoal.getTime_type().equals("M")){
                storeAchvGoal.setTime_type("月");
            }else if(storeAchvGoal.getTime_type().equals("Y")){
                storeAchvGoal.setTime_type("年");
            }
        }
        PageInfo<StoreAchvGoal> page = new PageInfo<StoreAchvGoal>(storeAchvGoals);
        return page;
    }


//    @Override
//    public PageInfo<StoreAchvGoal> getAllStoreAchvScreen(int page_number, int page_size, String corp_code, String area_code, String store_code, String search_value,Map<String, String> map) {
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("map", map);
//        params.put("role_code", role_code);
//        params.put("corp_code", corp_code);
//        List<StoreAchvGoal> storeAchvGoals;
//        PageHelper.startPage(page_number, page_size);
//        storeAchvGoals = storeAchvGoalMapper.selectAllStoreAchvScreen(params);
//        PageInfo<StoreAchvGoal> page = new PageInfo<StoreAchvGoal>(storeAchvGoals);
//        return page;
//    }

    @Override
    public int deleteById(int id) {
        return this.storeAchvGoalMapper.deleteById(id);
    }

    @Override
    public String insert(StoreAchvGoal storeAchvGoal) {
        int count = -1;

        count=storeAchvGoalMapper.selectStoreAchvCountType(storeAchvGoal.getCorp_code(),storeAchvGoal.getStore_code(),storeAchvGoal.getTime_type(),storeAchvGoal.getTarget_time());
        if (count > 0) {
            return "店铺业绩重复";
        } else {
            storeAchvGoalMapper.insert(storeAchvGoal);
            return Common.DATABEAN_CODE_SUCCESS;
        }
    }

    @Override
    public StoreAchvGoal selectlById(int id) {
        return this.storeAchvGoalMapper.selectById(id);
    }

    @Override
    public List<StoreAchvGoal> selectUsersBySearch(String corp_code, String search_value) {
        return null;
    }
}
