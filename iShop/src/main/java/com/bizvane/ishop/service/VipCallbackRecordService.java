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
    VipCallbackRecord getVipCallbackRecord(int id) throws SQLException;

    int insert(VipCallbackRecord vipCallbackRecord) throws SQLException;

    int update(VipCallbackRecord vipCallbackRecord) throws SQLException;

    int delete(int id) throws SQLException;

    PageInfo<VipCallbackRecord> selectBySearch(int page_number, int page_size, String corp_code, String search_value);


}
