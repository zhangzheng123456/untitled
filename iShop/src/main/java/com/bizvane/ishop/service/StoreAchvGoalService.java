package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.StoreAchvGoal;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public interface StoreAchvGoalService {
    int update(StoreAchvGoal storeAchvGoal);

    int deleteById(int id);

    int insert(StoreAchvGoal storeAchvGoal);

    StoreAchvGoal selectlById(int id);

    List<StoreAchvGoal> selectUsersBySearch(String corp_code, String search_value);

    PageInfo<StoreAchvGoal> selectBySearch(int page_number, int page_size, String corp_code, String search_value)
            throws SQLException;

    //storeAchvGoalService.storeAchvExist(corp,user_code);
    String storeAchvExist(String corp_code, String store_code);


}
