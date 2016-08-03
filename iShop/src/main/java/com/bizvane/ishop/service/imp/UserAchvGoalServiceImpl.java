package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.UserAchvGoalMapper;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.TaskType;
import com.bizvane.ishop.entity.UserAchvGoal;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.UserAchvGoalService;
import com.bizvane.ishop.utils.TimeUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
public class UserAchvGoalServiceImpl implements UserAchvGoalService {

    @Autowired
    private UserAchvGoalMapper userAchvGoalMapper = null;

    @Autowired
    private StoreService storeService = null;

    private static final Logger logger = Logger.getLogger(UserAchvGoalService.class);


    public PageInfo<UserAchvGoal> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws SQLException {

        List<UserAchvGoal> userAchvGoals = null;

        PageHelper.startPage(page_number, page_size);
        userAchvGoals = this.userAchvGoalMapper.selectUserAchvGoalBySearch(corp_code, search_value);
        for (UserAchvGoal userAchvGoal:userAchvGoals) {
            if(userAchvGoal.getIsactive().equals("Y")){
                userAchvGoal.setIsactive("是");
            }else{
                userAchvGoal.setIsactive("否");
            }
            if(userAchvGoal.getTarget_type().equals("D")){
                userAchvGoal.setTarget_type("日");
            }else if(userAchvGoal.getTarget_type().equals("W")){
                userAchvGoal.setTarget_type("周");
            }else if(userAchvGoal.getTarget_type().equals("M")){
                userAchvGoal.setTarget_type("月");
            }else if(userAchvGoal.getTarget_type().equals("Y")){
                userAchvGoal.setTarget_type("年");
            }
        }
        PageInfo<UserAchvGoal> page = new PageInfo<UserAchvGoal>(userAchvGoals);
        return page;

    }

    /**
     * 用户拥有店铺下的员工
     * （属于自己拥有的店铺，且角色级别比自己低）
     */
    public PageInfo<UserAchvGoal> selectBySearchPart(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String role_code) throws SQLException {
        String[] stores = null;
        if (!store_code.equals("")) {
            stores = store_code.split(",");
            for (int i = 0; i < stores.length; i++) {
                stores[i] = stores[i].substring(1, stores[i].length());
            }
        }
        if (!area_code.equals("")) {
            String[] areas = area_code.split(",");
            for (int i = 0; i < areas.length; i++) {
                areas[i] = areas[i].substring(1, areas[i].length());
            }
            List<Store> store = storeService.selectByAreaCode(corp_code, areas, "");
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

        List<UserAchvGoal> users;
        PageHelper.startPage(page_number, page_size);
        users = userAchvGoalMapper.selectPartUserAchvGoalBySearch(params);
        for (UserAchvGoal userAchvGoal:users) {
            if(userAchvGoal.getIsactive().equals("Y")){
                userAchvGoal.setIsactive("是");
            }else{
                userAchvGoal.setIsactive("否");
            }
            if(userAchvGoal.getTarget_type().equals("D")){
                userAchvGoal.setTarget_type("日");
            }else if(userAchvGoal.getTarget_type().equals("W")){
                userAchvGoal.setTarget_type("周");
            }else if(userAchvGoal.getTarget_type().equals("M")){
                userAchvGoal.setTarget_type("月");
            }else if(userAchvGoal.getTarget_type().equals("Y")){
                userAchvGoal.setTarget_type("年");
            }
        }
        PageInfo<UserAchvGoal> page = new PageInfo<UserAchvGoal>(users);
        return page;
    }

    @Override
    public UserAchvGoal getUserAchvGoalById(int id) throws SQLException {
        return this.userAchvGoalMapper.selectById(id);
    }

    @Override
    public String updateUserAchvGoal(UserAchvGoal userAchvGoal) throws Exception {
        int count = -1;
        if (userAchvGoal.getTarget_type().equals(Common.TIME_TYPE_WEEK)) {
            String time = userAchvGoal.getTarget_time();
            String week = TimeUtils.getWeek(time);
            userAchvGoal.setTarget_time(week);
        }
        UserAchvGoal oldUserAchvGoal = userAchvGoalMapper.selectById(userAchvGoal.getId());
        if (oldUserAchvGoal.getCorp_code().equals(userAchvGoal.getCorp_code())
                && oldUserAchvGoal.getUser_code().equals(userAchvGoal.getUser_code())
                && oldUserAchvGoal.getTarget_type().equals(userAchvGoal.getTarget_type())
                && oldUserAchvGoal.getTarget_time().equals(userAchvGoal.getTarget_time())
                ) {
            userAchvGoalMapper.update(userAchvGoal);
            return Common.DATABEAN_CODE_SUCCESS;
        } else {
            count = userAchvGoalMapper.selectUserAchvCountType(userAchvGoal.getCorp_code(), userAchvGoal.getUser_code(), userAchvGoal.getTarget_type(), userAchvGoal.getTarget_time());
            if (count > 0) {
                return Common.DATABEAN_CODE_ERROR;
            }
            userAchvGoalMapper.update(userAchvGoal);
            return Common.DATABEAN_CODE_SUCCESS;
        }
    }

    @Override
    public int deleteUserAchvGoalById(String id) throws SQLException {
        return this.userAchvGoalMapper.delete(Integer.parseInt(id));
    }

    @Override
    public String insert(UserAchvGoal userAchvGoal) throws SQLException {
        int count = -1;
        count = userAchvGoalMapper.selectUserAchvCountType(userAchvGoal.getCorp_code(), userAchvGoal.getUser_code(), userAchvGoal.getTarget_type(), userAchvGoal.getTarget_time());
        if (count > 0) {
            return "用户业绩重复";
        } else {
            userAchvGoalMapper.insert(userAchvGoal);
            return Common.DATABEAN_CODE_SUCCESS;
        }
    }

    public List<UserAchvGoal> userAchvGoalExist(String corp_code, String user_code) throws SQLException {
        return userAchvGoalMapper.selectUserAchvCount(corp_code, user_code);
    }

    @Override
    public PageInfo<UserAchvGoal> getAllUserAchScreen(int page_number, int page_size, String corp_code, String
            area_code, String store_code, String role_code, Map<String, String> map) {
        String[] stores = null;
        if (!store_code.equals("")) {
            stores = store_code.split(",");
            for (int i = 0; i < stores.length; i++) {
                stores[i] = stores[i].substring(1, stores[i].length());
            }
        }
        if (!area_code.equals("")) {
            String[] areas = area_code.split(",");
            for (int i = 0; i < areas.length; i++) {
                areas[i] = areas[i].substring(1, areas[i].length());
            }
            List<Store> store = storeService.selectByAreaCode(corp_code, areas, "");
            String a = "";
            for (int i = 0; i < store.size(); i++) {
                a = a + store.get(i).getStore_code() + ",";
            }
            stores = a.split(",");
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        params.put("corp_code", corp_code);
        params.put("array", stores);
        params.put("role_code", role_code);
        List<UserAchvGoal> userAchvGoals;
        PageHelper.startPage(page_number, page_size);
        userAchvGoals = userAchvGoalMapper.selectAllUserAchvScreen(params);
        for (UserAchvGoal userAchvGoal:userAchvGoals) {
            if(userAchvGoal.getIsactive().equals("Y")){
                userAchvGoal.setIsactive("是");
            }else{
                userAchvGoal.setIsactive("否");
            }
            if(userAchvGoal.getTarget_type().equals("D")){
                userAchvGoal.setTarget_type("日");
            }else if(userAchvGoal.getTarget_type().equals("W")){
                userAchvGoal.setTarget_type("周");
            }else if(userAchvGoal.getTarget_type().equals("M")){
                userAchvGoal.setTarget_type("月");
            }else if(userAchvGoal.getTarget_type().equals("Y")){
                userAchvGoal.setTarget_type("年");
            }
        }
        PageInfo<UserAchvGoal> page = new PageInfo<UserAchvGoal>(userAchvGoals);
        return page;
    }
}
