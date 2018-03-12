package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.SendCoupons;
import com.bizvane.ishop.entity.SendCoupons;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/24.
 */
public interface SendCouponsMapper {

    SendCoupons selectById(int id) throws SQLException;

    List<SendCoupons> selectAllSendCoupons(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    int insertSendCoupons(SendCoupons vipFsend) throws SQLException;

    int updateSendCoupons(SendCoupons vipFsend) throws SQLException;


    int deleteById(int id) throws SQLException;

    List<SendCoupons> selectAllFsendScreen(Map<String, Object> params) throws SQLException;

    SendCoupons selectByCode(@Param("corp_code") String corp_code, @Param("tick_code_ishop") String sms_code)throws SQLException;

    SendCoupons selectByCode1(@Param("tick_code_ishop") String sms_code)throws SQLException;

    List<SendCoupons> getSendCouponsByCode(@Param("corp_code") String corp_code, @Param("tick_code_ishop") String activity_vip_code);

    int delSendByCode(@Param("corp_code") String corp_code, @Param("tick_code_ishop") String activity_vip_code);



}
