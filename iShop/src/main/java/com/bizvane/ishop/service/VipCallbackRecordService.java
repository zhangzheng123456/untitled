package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VIPInfo;
import com.bizvane.ishop.entity.VipCallbackRecord;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
public interface VipCallbackRecordService {

    /**
     * 获取回访记录通过记录的ID
     * @param id
     * @return
     * @throws SQLException
     */
    VipCallbackRecord getVipCallbackRecord(int id) throws SQLException;

    /**
     * 插入回访记录
     * @param vipCallbackRecord
     * @return
     * @throws SQLException
     */
    int insert(VipCallbackRecord vipCallbackRecord) throws SQLException;

    /**
     * 更改回访记录
     * @param vipCallbackRecord
     * @return
     * @throws SQLException
     */
    int update(VipCallbackRecord vipCallbackRecord) throws SQLException;

    /**
     * 删除回访记录
     * @param id
     * @return
     * @throws SQLException
     */
    int delete(int id) throws SQLException;

    /**
     * 获取回访记录的分页信息
     * @param page_number ： 起始页码
     * @param page_size ： 分页大小
     * @param corp_code ： 公司编号
     * @param search_value ： 搜索数据
     * @return
     */
    PageInfo<VipCallbackRecord> selectBySearch(int page_number, int page_size, String corp_code, String search_value);


}
