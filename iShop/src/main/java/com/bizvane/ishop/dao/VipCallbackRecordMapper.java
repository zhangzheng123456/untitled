package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipCallbackRecord;

import java.util.List;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
public interface VipCallbackRecordMapper {
    VipCallbackRecord selecctById(int id);

    int deleteByPrimary(Integer id);

    int insert(VipCallbackRecord vipCallbackRecord);

    int updateByPrimaryKey(VipCallbackRecord vipCallbackRecord);

    List<VipCallbackRecord> selectAllVipCallBackRecordInfo(String corp_code, String search_value);

}
