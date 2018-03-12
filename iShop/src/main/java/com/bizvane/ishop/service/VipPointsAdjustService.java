package com.bizvane.ishop.service;


import com.bizvane.ishop.entity.VipPointsAdjust;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by gyy on 2017/11/12.
 */
public interface VipPointsAdjustService {
    List<VipPointsAdjust> selectPointsAdjustByNameAndId(String bill_name, String corp_code, String isactive) throws Exception;

    VipPointsAdjust selectPointsAdjustById(int id) throws Exception;

    VipPointsAdjust selectPointsAdjustByBillCode(String bill_code) throws Exception;

    PageInfo<VipPointsAdjust> selectPointsAdjustAll(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    int deletePointsAdjustById(int id) throws Exception;

    String insertPointsAdjust(String message, String user_id) throws Exception;

    String updatePointsAdjust(String message, String user_id) throws Exception;

    PageInfo<VipPointsAdjust> selectVipPointsAdjustAllScreen(int page_number, int page_size, String corp_code, Map<String,String> map) throws Exception;

    int updatePointsAdjust(VipPointsAdjust vipPointsAdjust) throws Exception;

    int insertPointsAdjust(VipPointsAdjust vipPointsAdjust) throws Exception;

    int updateBillState(int id,String bill_voucher,String bill_state,String adjust_time) throws Exception;
}
