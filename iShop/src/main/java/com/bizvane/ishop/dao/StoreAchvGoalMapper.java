package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.StoreAchvGoal;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
    StoreAchvGoal selectById(int id) throws SQLException;

    /**
     * 插入店铺业绩目标信息
     *
     * @param storeAchvGoal
     * @return
     */
    int insert(StoreAchvGoal storeAchvGoal) throws SQLException;

    /**
     * 更改店铺业绩目标信息
     *
     * @param storeAchvGoal
     * @return
     */
    int update(StoreAchvGoal storeAchvGoal) throws SQLException;

    /**
     * 删除店铺业绩目标信息
     *
     * @param id
     * @return
     */
    int deleteById(@Param("id") int id) throws SQLException;


    /**
     * 搜寻值
     *
     * @param params
     * @return
     */

    List<StoreAchvGoal> selectBySearch(Map<String, Object> params) throws SQLException;


    /**
     * @param corp_code
     * @param store_code
     * @return
     */
    StoreAchvGoal selectByCorpAndUserCode(@Param("corp_code") String corp_code, @Param("store_code") String store_code) throws SQLException;


    List<StoreAchvGoal> selectAllStoreAchvScreen(Map<String, Object> params) throws SQLException;

    int selectStoreAchvCountType(@Param("corp_code")String corp_code,@Param("store_code")String store_code,@Param("time_type")String time_type,@Param("target_time")String target_time) throws SQLException;

}
