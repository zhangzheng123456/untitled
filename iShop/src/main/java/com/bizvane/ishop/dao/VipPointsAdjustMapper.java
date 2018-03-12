package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipPointsAdjust;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gyy on 2017/11/10.
 */
public interface VipPointsAdjustMapper {
    List<VipPointsAdjust> selectPointsAdjustByNameAndId(@Param("bill_name") String bill_name,@Param("corp_code") String corp_code, @Param("isactive") String isactive) throws SQLException;

    VipPointsAdjust selectPointsAdjustById (int id) throws Exception;

    VipPointsAdjust selectPointsAdjustByBillCode (@Param("bill_code") String bill_code) throws Exception;

    List<VipPointsAdjust> selectPointsAdjustAll(@Param("corp_code")String corp_code, @Param("search_value")String search_value) throws  Exception;

    int deletePointsAdjustById(int id) throws  Exception;

    int insertPointsAdjust(VipPointsAdjust vipPointsAdjust) throws Exception;

    int updatePointsAdjust(VipPointsAdjust vipPointsAdjust)throws  Exception;

    List<VipPointsAdjust> selectVipPointsAdjustAllScreen(HashMap<String,Object> map) throws  Exception;

    int updateBillState(@Param("id")int id,@Param("bill_voucher") String bill_voucher,@Param("bill_state")String bill_state,@Param("adjust_time")String adjust_time) throws SQLException;
}
