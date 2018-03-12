package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.StoreAchvGoal;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
    String update(StoreAchvGoal storeAchvGoal) throws Exception;

    /**
     * 删除短噗业绩目标，通过ID
     *
     * @param id
     * @return
     */
    int deleteById(int id) throws Exception;

    /**
     * 插入商品业绩目标
     *
     * @param storeAchvGoal
     * @return
     */
    String insert(StoreAchvGoal storeAchvGoal) throws Exception;

    /**
     * 获取商品业绩目标，通过ID
     *
     * @param id
     * @return
     */
    StoreAchvGoal selectlById(int id) throws Exception;

    /**
     * 获取某公司的短噗业绩目标列表： 通过查询字段匹配
     *
     * @param corp_code    ： 所在公司的编号
     * @param search_value ： 查询字段
     * @return
     */
    List<StoreAchvGoal> selectUsersBySearch(String corp_code, String search_value) throws Exception;

    /**
     * 分页获取商品业绩列表
     *
     * @param page_number  ： 起始页码
     * @param page_size    ： 分页大小
     * @param corp_code    ： 公司编号
     * @param search_value ： 查询字段
     * @return
     * @throws SQLException
     */
    PageInfo<StoreAchvGoal> selectBySearch(int page_number, int page_size, String corp_code, String area_code, String user_id, String search_value,String area_store_code)
            throws Exception;

    PageInfo<StoreAchvGoal> selectBySearch(int page_number, int page_size, String corp_code, String area_code,
                                           String user_id, String search_value,String area_store_code,String manager_corp)
            throws Exception;
    /**
     * 判断商品编号是否存在
     *
     * @param corp_code  ： 企业编号
     * @param store_code ： 店铺编号
     * @return
     */
    String storeAchvExist(String corp_code, String store_code) throws Exception;


    StoreAchvGoal getStoreAchvForID(String corp_code, String store_code,String target_time) throws Exception;

    PageInfo<StoreAchvGoal> getAllStoreAchvScreen(int page_number, int page_size, String corp_code, String area_code, String store_code, Map<String, String> map,String area_store_code) throws Exception;

    PageInfo<StoreAchvGoal> getAllStoreAchvScreen(int page_number, int page_size, String corp_code,
                                                  String area_code, String store_code, Map<String, String> map,String area_store_code,String manager_corp) throws Exception;


    int getAchExist(String corp_code,String store_code,String time_type,String target_time,String isactive) throws Exception;

}
