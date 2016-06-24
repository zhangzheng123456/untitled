package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.StoreAchvGoal;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/1.
 * 商品业绩目标操作类
 *
 * @@version
 */
public interface StoreAchvGoalService {
    /**
     * 更新商品业绩目标：通过ID
     *
     * @param storeAchvGoal
     * @return
     */
    int update(StoreAchvGoal storeAchvGoal);

    /**
     * 删除短噗业绩目标，通过ID
     * @param id
     * @return
     */
    int deleteById(int id);

    /**
     * 插入商品业绩目标
     * @param storeAchvGoal
     * @return
     */
    int insert(StoreAchvGoal storeAchvGoal);

    /**
     * 获取商品业绩目标，通过ID
     * @param id
     * @return
     */
    StoreAchvGoal selectlById(int id);

    /**
     * 获取某公司的短噗业绩目标列表： 通过查询字段匹配
     * @param corp_code ： 所在公司的编号
     * @param search_value ： 查询字段
     * @return
     */
    List<StoreAchvGoal> selectUsersBySearch(String corp_code, String search_value);

    /**
     *  分页获取商品业绩列表
     * @param page_number ： 起始页码
     * @param page_size ： 分页大小
     * @param corp_code ： 公司编号
     * @param search_value ： 查询字段
     * @return
     * @throws SQLException
     */
    PageInfo<StoreAchvGoal> selectBySearch(int page_number, int page_size, String corp_code, String search_value)
            throws SQLException;

    /**
     * 判断商品编号是否存在
     * @param corp_code ： 企业编号
     * @param store_code ： 店铺编号
     * @return
     */
    String storeAchvExist(String corp_code, String store_code);


}
