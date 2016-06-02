package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.StoreAchvGoal;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public interface StoreAchvGoalService {
    int updateById(StoreAchvGoal storeAchvGoal);

    int deleteById(int id);

    int insertById(StoreAchvGoal storeAchvGoal);

    StoreAchvGoal selectlById(int id);


}
