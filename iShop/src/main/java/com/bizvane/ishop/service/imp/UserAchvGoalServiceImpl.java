package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.UserAchvGoalMapper;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.UserAchvGoal;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.UserAchvGoalService;
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
        PageInfo<UserAchvGoal> page = new PageInfo<UserAchvGoal>(users);
        return page;
    }

    @Override
    public UserAchvGoal getUserAchvGoalById(int id) throws SQLException {
        return this.userAchvGoalMapper.selectById(id);
    }

    @Override
    public int updateUserAchvGoal(UserAchvGoal userAchvGoal) throws SQLException {
        return this.userAchvGoalMapper.update(userAchvGoal);
    }

    @Override
    public int deleteUserAchvGoalById(String id) throws SQLException {
        return this.userAchvGoalMapper.delete(Integer.parseInt(id));
    }

    @Override
    public int insert(UserAchvGoal userAchvGoal) throws SQLException {
        return this.userAchvGoalMapper.insert(userAchvGoal);
    }

    public List<UserAchvGoal> userAchvGoalExist(String corp_code, String user_code) throws SQLException {
        return userAchvGoalMapper.selectUserAchvCount(corp_code, user_code);
    }

    @Override
    public PageInfo<UserAchvGoal> getAllUserAchScreen(int page_number, int page_size, String corp_code, String role_code, Map<String, String> map) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        List<UserAchvGoal> userAchvGoals;
        PageHelper.startPage(page_number, page_size);
        userAchvGoals = userAchvGoalMapper.selectAllUserAchvScreen(params);
        PageInfo<UserAchvGoal> page = new PageInfo<UserAchvGoal>(userAchvGoals);
        return page;
    }
}
