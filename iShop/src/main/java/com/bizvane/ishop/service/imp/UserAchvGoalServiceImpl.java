package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.UserAchvGoalMapper;
import com.bizvane.ishop.entity.UserAchvGoal;
import com.bizvane.ishop.service.UserAchvGoalService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
@Service
public class UserAchvGoalServiceImpl implements UserAchvGoalService {

    @Autowired
    private UserAchvGoalMapper userAchvGoalMapper = null;

    private static final Logger logger = Logger.getLogger(UserAchvGoalService.class);


    public PageInfo<UserAchvGoal> selectBySearch(int page_number, int page_size, String userAchvGoalId, String search_value) throws SQLException {

        List<UserAchvGoal> userAchvGoals = null;
        if (search_value == null || search_value.isEmpty()) {
            PageHelper.startPage(page_number, page_size);
            userAchvGoals = this.userAchvGoalMapper.selectUserAchvGoalBySearch(userAchvGoalId, "");
        } else {
            PageHelper.startPage(page_number, page_size);
            userAchvGoals = this.userAchvGoalMapper.selectUserAchvGoalBySearch(userAchvGoalId, search_value);
        }
        //PageInfo<UserAchvGoal> page = (PageInfo<UserAchvGoal>) userAchvGoals;
        PageInfo<UserAchvGoal> page = new PageInfo<UserAchvGoal>(userAchvGoals);
        return page;

    }

    @Override
    public String userAchvGoalExist(String user_code) throws SQLException {

        //UserAchvGoal userAchvGoal = this.userAchvGoalMapper.selectByUser_code(user_code);
        List<UserAchvGoal> list = this.userAchvGoalMapper.selectUserAchvGoalBySearch(user_code, "");

        //UserAchvGoal userAchvGoal = this.userAchvGoalMapper.selectUserAchvGoalBySearch(user_code, "").get(0);
        if (list == null || list.size() < 1) {
            return Common.DATABEAN_CODE_ERROR;
        }
        return Common.DATABEAN_CODE_SUCCESS;

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
    public int deleteUserAchvGoalById(String user_code) throws SQLException {
        return this.userAchvGoalMapper.delelteByUser_code(user_code);

    }

    @Override
    public int insert(UserAchvGoal userAchvGoal) throws SQLException {
        return this.userAchvGoalMapper.insert(userAchvGoal);

    }
}
