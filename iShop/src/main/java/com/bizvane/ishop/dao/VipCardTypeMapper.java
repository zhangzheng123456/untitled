package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipCardType;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/12/29.
 */
public interface VipCardTypeMapper {

    VipCardType selVipCardTypeById(int id) throws SQLException;

    List<VipCardType> selectAllVipCardType(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    int insertVipCardType(VipCardType vipCardType) throws SQLException;

    int updateVipCardType(VipCardType vipCardType) throws SQLException;

    int delVipCardTypeById(int id) throws SQLException;

    List<VipCardType> selectVipCardTypeScreen(Map<String, Object> params) throws SQLException;

    VipCardType selVipCardTypeByCode( @Param("corp_code")String corp_code,@Param("vip_card_type_code")String vip_card_type_code,@Param("isactive") String isactive)throws SQLException;

    VipCardType selVipCardTypeByName( @Param("corp_code")String corp_code,@Param("vip_card_type_name")String vip_card_type_name,@Param("isactive") String isactive)throws SQLException;

    List<VipCardType>  selectByCorp(@Param("corp_code") String corp_code,@Param("isactive") String isactive)throws SQLException;

}
