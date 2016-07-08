package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VIPInfo;
import com.bizvane.ishop.entity.VipRecord;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
public interface VipRecordService {

    /**
     * 获取回访记录通过记录的ID
     * @param id
     * @return
     * @throws SQLException
     */
    VipRecord getVipRecord(int id) throws SQLException;

    /**
     * 插入回访记录
     * @param VipRecord
     * @return
     * @throws SQLException
     */
    int insert(VipRecord VipRecord) throws SQLException;

    /**
     * 更改回访记录
     * @param VipRecord
     * @return
     * @throws SQLException
     */
    int update(VipRecord VipRecord) throws SQLException;

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
    PageInfo<VipRecord> selectBySearch(int page_number, int page_size, String corp_code, String search_value);


}
