package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.controller.StoreAchvGoalController;
import com.bizvane.ishop.dao.StoreAchvGoalMapper;
import com.bizvane.ishop.entity.StoreAchvGoal;
import com.bizvane.ishop.service.StoreAchvGoalService;
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
public class StoreAchvGoalServiceImpl implements StoreAchvGoalService {

    @Autowired
    StoreAchvGoalMapper storeAchvGoalMapper = null;
    private static final Logger log = Logger.getLogger(StoreServiceImpl.class);
    String[] arg = new String[]{""};


    @Override
    public int updateById(StoreAchvGoal storeAchvGoal) {


        return storeAchvGoalMapper.update(storeAchvGoal);
    }

    public PageInfo<StoreAchvGoal> selectBySearch(int page_number, int page_size, String storeAchvGoalId, String search_value)
            throws SQLException {
        List<StoreAchvGoal> storeAchvGoals;
        if (search_value == null || search_value.isEmpty()) {
            PageHelper.startPage(page_number, page_size);
            storeAchvGoals = storeAchvGoalMapper.selectUsersBySearch(storeAchvGoalId, search_value);
        } else {
            PageHelper.startPage(page_number, page_size);
            storeAchvGoals = storeAchvGoalMapper.selectUsersBySearch(storeAchvGoalId, search_value);
        }
        PageInfo<StoreAchvGoal> page = (PageInfo<StoreAchvGoal>) storeAchvGoals;
        return page;
    }

    @Override
    public int deleteById(int id) {
        return this.storeAchvGoalMapper.deleteById(id);
    }

    @Override
    public int insertById(StoreAchvGoal storeAchvGoal) {
        return this.storeAchvGoalMapper.insert(storeAchvGoal);
    }

    @Override
    public StoreAchvGoal selectlById(int id) {
        return this.storeAchvGoalMapper.selectlById(id);
    }
}
