package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipIntegral;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/4/7.
 */
public interface VipIntegralMapper {
    VipIntegral selectIntegralById(int id) throws Exception;

    VipIntegral selectIntegralByBillno(@Param("bill_no")String bill_no) throws Exception;

    List<VipIntegral> selectIntegralAll(@Param("corp_code")String corp_code, @Param("search_value")String search_value) throws  Exception;

    int deleteIntegralById(int id) throws  Exception;

    int insertVipIntegral(VipIntegral vipIntegral) throws Exception;

    int updateVipIntegral(VipIntegral vipIntegral)throws  Exception;

    List<VipIntegral> selectIntegralAllScreen(Map<String, Object> params)throws Exception;

    VipIntegral selectIntegralByName(@Param("corp_code")String corp_code,@Param("integral_name")String integral_name) throws  Exception;

}
