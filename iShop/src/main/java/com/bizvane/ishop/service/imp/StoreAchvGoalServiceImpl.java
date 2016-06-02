package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.controller.StoreAchvGoalController;
import com.bizvane.ishop.entity.StoreAchvGoal;
import com.bizvane.ishop.service.StoreAchvGoalService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public class StoreAchvGoalServiceImpl implements StoreAchvGoalService{

    @Autowired
    StoreAchvGoalController storeAchvGoalController;

    @Override
    public int updateById(StoreAchvGoal storeAchvGoal) {
        return storeAchvGoalController.update(storeAchvGoal);
    }

    @Override
    public int deleteById(int id) {
        return this.storeAchvGoalController.delete(id);
    }

    @Override
    public int insertById(StoreAchvGoal storeAchvGoal) {
        return this.storeAchvGoalController.insert(storeAchvGoal);
    }

    @Override
    public StoreAchvGoal selectlById(int id) {
        return this.storeAchvGoalController.getStoreAchvGoalById(id);
    }
}
