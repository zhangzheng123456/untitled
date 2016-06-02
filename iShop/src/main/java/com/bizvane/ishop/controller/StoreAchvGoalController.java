package com.bizvane.ishop.controller;

import com.bizvane.ishop.dao.StoreAchvGoalMapper;
import com.bizvane.ishop.entity.StoreAchvGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public class StoreAchvGoalController {

    private static Logger logger = LoggerFactory.getLogger((UserController.class));

    @Autowired
    StoreAchvGoalMapper storeAchvGoalMapper=null;

    public StoreAchvGoal getStoreAchvGoalById(int id ){
        return storeAchvGoalMapper.selectlById(id);
    }

    public int insert(StoreAchvGoal storeAchvGoal){
        return storeAchvGoalMapper.insert(storeAchvGoal);
    }

    public int delete( int id ){
        return storeAchvGoalMapper.deleteById(id);
    }

    public int update(StoreAchvGoal storeAchvGoal){
        return storeAchvGoalMapper.update(storeAchvGoal);
    }


}
