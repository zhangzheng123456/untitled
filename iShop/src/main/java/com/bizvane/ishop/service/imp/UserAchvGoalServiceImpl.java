package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.UserAchvGoalMapper;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.UserAchvGoal;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.UserAchvGoalService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.TimeUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public PageInfo<UserAchvGoal> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws Exception {

        List<UserAchvGoal> userAchvGoals = null;

        PageHelper.startPage(page_number, page_size);
        userAchvGoals = this.userAchvGoalMapper.selectUserAchvGoalBySearch(corp_code, search_value);
        for (UserAchvGoal userAchvGoal : userAchvGoals) {
            userAchvGoal.setIsactive(CheckUtils.CheckIsactive(userAchvGoal.getIsactive()));
            if (userAchvGoal.getTarget_type() == null || userAchvGoal.getTarget_type().equals("")) {
                userAchvGoal.setTarget_type("未设定");
            } else if (userAchvGoal.getTarget_type().equals("D")) {
                userAchvGoal.setTarget_type("日");
            } else if (userAchvGoal.getTarget_type().equalsIgnoreCase("W")) {
                userAchvGoal.setTarget_type("周");
            } else if (userAchvGoal.getTarget_type().equalsIgnoreCase("M")) {
                userAchvGoal.setTarget_type("月");
            } else if (userAchvGoal.getTarget_type().equalsIgnoreCase("Y")) {
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
    public PageInfo<UserAchvGoal> selectBySearchPart(int page_number, int page_size, String corp_code, String search_value, String store_code,
                                                     String area_code, String role_code) throws Exception {
        String[] stores = null;
        if (!store_code.equals("")) {
            if (store_code.contains(Common.SPECIAL_HEAD))
                store_code = store_code.replace(Common.SPECIAL_HEAD, "");
            stores = store_code.split(",");
        }
        if (!area_code.equals("")) {
            if (area_code.contains(Common.SPECIAL_HEAD))
                area_code = area_code.replace(Common.SPECIAL_HEAD, "");

            String[] areas = area_code.split(",");
            List<Store> store = storeService.selectByAreaBrand(corp_code, areas, null, "");
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

        PageHelper.startPage(page_number, page_size);
        List<UserAchvGoal> users = userAchvGoalMapper.selectPartUserAchvGoalBySearch(params);
        for (UserAchvGoal userAchvGoal : users) {
            userAchvGoal.setIsactive(CheckUtils.CheckIsactive(userAchvGoal.getIsactive()));
            if (userAchvGoal.getTarget_type() == null || userAchvGoal.getTarget_type().equals("")) {
                userAchvGoal.setTarget_type("未设定");
            } else if (userAchvGoal.getTarget_type().equals("D")) {
                userAchvGoal.setTarget_type("日");
            } else if (userAchvGoal.getTarget_type().equalsIgnoreCase("W")) {
                userAchvGoal.setTarget_type("周");
            } else if (userAchvGoal.getTarget_type().equalsIgnoreCase("M")) {
                userAchvGoal.setTarget_type("月");
            } else if (userAchvGoal.getTarget_type().equalsIgnoreCase("Y")) {
                userAchvGoal.setTarget_type("年");
            }
        }
        PageInfo<UserAchvGoal> page = new PageInfo<UserAchvGoal>(users);
        return page;
    }

    @Override
    public UserAchvGoal getUserAchvGoalById(int id) throws Exception {
        return this.userAchvGoalMapper.selectById(id);
    }

    @Override
    public String updateUserAchvGoal(UserAchvGoal userAchvGoal) throws Exception {
        int count = -1;
        if (userAchvGoal.getTarget_type().equalsIgnoreCase(Common.TIME_TYPE_WEEK)) {
            String time = userAchvGoal.getTarget_time();
            String week = TimeUtils.getWeek(time);
            userAchvGoal.setTarget_time(week);
        }
        UserAchvGoal oldUserAchvGoal = userAchvGoalMapper.selectById(userAchvGoal.getId());
        if (oldUserAchvGoal.getCorp_code().equalsIgnoreCase(userAchvGoal.getCorp_code())
                && oldUserAchvGoal.getUser_code().equalsIgnoreCase(userAchvGoal.getUser_code())
                && oldUserAchvGoal.getTarget_type().equalsIgnoreCase(userAchvGoal.getTarget_type())
                && oldUserAchvGoal.getTarget_time().equalsIgnoreCase(userAchvGoal.getTarget_time())
                && oldUserAchvGoal.getIsactive().equalsIgnoreCase(userAchvGoal.getIsactive())
                && oldUserAchvGoal.getStore_code().equalsIgnoreCase(userAchvGoal.getStore_code())

                ) {
            userAchvGoalMapper.update(userAchvGoal);
            return Common.DATABEAN_CODE_SUCCESS;
        } else {
            count = userAchvGoalMapper.selectUserAchvCountType(userAchvGoal.getCorp_code(), userAchvGoal.getUser_code(), userAchvGoal.getTarget_type(), userAchvGoal.getTarget_time(), userAchvGoal.getIsactive(), userAchvGoal.getStore_code());
            if (count > 0) {
                return Common.DATABEAN_CODE_ERROR;
            }
            userAchvGoalMapper.update(userAchvGoal);
            return Common.DATABEAN_CODE_SUCCESS;
        }
    }

    @Override
    public int deleteUserAchvGoalById(String id) throws Exception {
        return this.userAchvGoalMapper.delete(Integer.parseInt(id));
    }

    public int checkUserAchvGoal(UserAchvGoal userAchvGoal) throws Exception {
        return userAchvGoalMapper.selectUserAchvCountType(userAchvGoal.getCorp_code(), userAchvGoal.getUser_code(), userAchvGoal.getTarget_type(), userAchvGoal.getTarget_time(), userAchvGoal.getIsactive(), userAchvGoal.getStore_code());

    }

    @Override
    public UserAchvGoal getUserAchvForId(String corp_code, String user_code, String user_target, String target_type, String target_time) throws Exception {
        return userAchvGoalMapper.getUserAchvForId(corp_code, user_code, user_target,target_type, target_time);
    }


    @Override
    public String insert(UserAchvGoal userAchvGoal) throws Exception {
        int count = -1;
        count = userAchvGoalMapper.selectUserAchvCountType(userAchvGoal.getCorp_code(), userAchvGoal.getUser_code(), userAchvGoal.getTarget_type(), userAchvGoal.getTarget_time(), userAchvGoal.getIsactive(), userAchvGoal.getStore_code());
        if (count > 0) {
            return "用户业绩重复";
        } else {
            userAchvGoalMapper.insert(userAchvGoal);
            return Common.DATABEAN_CODE_SUCCESS;
        }
    }

    public List<UserAchvGoal> userAchvGoalExist(String corp_code, String user_code) throws Exception {
        return userAchvGoalMapper.selectUserAchvCount(corp_code, user_code);
    }

    @Override
    public PageInfo<UserAchvGoal> getAllUserAchScreen(int page_number, int page_size, String corp_code, String
            area_code, String store_code, String role_code, Map<String, String> map) throws Exception {
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
            List<Store> store = storeService.selectByAreaBrand(corp_code, areas, null, "");
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
        for (UserAchvGoal userAchvGoal : userAchvGoals) {
            userAchvGoal.setIsactive(CheckUtils.CheckIsactive(userAchvGoal.getIsactive()));
            if (userAchvGoal.getTarget_type() == null || userAchvGoal.getTarget_type().equals("")) {
                userAchvGoal.setTarget_type("未设定");
            } else if (userAchvGoal.getTarget_type().equals("D")) {
                userAchvGoal.setTarget_type("日");
            } else if (userAchvGoal.getTarget_type().equals("W")) {
                userAchvGoal.setTarget_type("周");
            } else if (userAchvGoal.getTarget_type().equals("M")) {
                userAchvGoal.setTarget_type("月");
            } else if (userAchvGoal.getTarget_type().equals("Y")) {
                userAchvGoal.setTarget_type("年");
            }
        }
        PageInfo<UserAchvGoal> page = new PageInfo<UserAchvGoal>(userAchvGoals);
        return page;
    }
}
