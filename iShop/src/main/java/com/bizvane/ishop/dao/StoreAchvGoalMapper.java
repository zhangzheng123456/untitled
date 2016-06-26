package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.StoreAchvGoal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public interface StoreAchvGoalMapper {

    /**
     * 获取相应的店铺业绩信息
     *
     * @return
     */
    StoreAchvGoal selectById(int id);

    /**
     * 插入店铺业绩目标信息
     *
     * @param storeAchvGoal
     * @return
     */
    int insert(StoreAchvGoal storeAchvGoal);

    /**
     * 更改店铺业绩目标信息
     *
     * @param storeAchvGoal
     * @return
     */
    int update(StoreAchvGoal storeAchvGoal);

    /**
     * 删除店铺业绩目标信息
     *
     * @param id
     * @return
     */
    int deleteById(@Param("id") int id);


    /**
     * 搜寻值
     *
     * @param search_value
     * @return
     */
    List<StoreAchvGoal> selectUsersBySearch(@Param("corp_code") String store_code, @Param("search_value") String search_value);


    /**
     * @param corp_code
     * @param store_code
     * @return
     */
    StoreAchvGoal selectByCorpAndUserCode(@Param("corp_code") String corp_code, @Param("store_code") String store_code);


}
