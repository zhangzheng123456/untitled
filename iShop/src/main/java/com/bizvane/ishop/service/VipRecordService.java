package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VIPInfo;
import com.bizvane.ishop.entity.VipRecord;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.Map;

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
    VipRecord getVipRecord(int id) throws Exception;

    /**
     * 插入回访记录
     * @param VipRecord
     * @return
     * @throws SQLException
     */
    int insert(VipRecord VipRecord) throws Exception;

    /**
     * 更改回访记录
     * @param VipRecord
     * @return
     * @throws SQLException
     */
    int update(VipRecord VipRecord) throws Exception;

    /**
     * 删除回访记录
     * @param id
     * @return
     * @throws SQLException
     */
    int delete(int id) throws Exception;

    /**
     * 获取回访记录的分页信息
     * @param page_number ： 起始页码
     * @param page_size ： 分页大小
     * @param corp_code ： 公司编号
     * @param search_value ： 搜索数据
     * @return
     */
    PageInfo<VipRecord> selectBySearch(int page_number, int page_size, String corp_code, String search_value)throws Exception;

    PageInfo<VipRecord> selectAllVipRecordScreen(int page_number, int page_size, String corp_code, Map<String,String> map) throws Exception;
}
