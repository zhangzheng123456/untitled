package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipRules;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/12/19.
 */
public interface VipRulesMapper {


    VipRules selectById(int id) throws SQLException;

    List<VipRules> selectAllVipRules(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    int insertVipRules(VipRules vipFsend) throws SQLException;

    int updateVipRules(VipRules vipFsend) throws SQLException;

    int deleteById(int id) throws SQLException;

    List<VipRules> selectVipRulesScreen(Map<String, Object> params) throws SQLException;

    List<VipRules> selectByVipType(@Param("corp_code") String corp_code,@Param("vip_type") String vip_type,@Param("high_vip_type") String high_vip_type,@Param("isactive") String isactive)throws SQLException;

    List<VipRules>  selectByCorp(@Param("corp_code") String corp_code,@Param("isactive") String isactive)throws SQLException;

    List<VipRules> selectByCardTypeCode (@Param("corp_code") String corp_code,@Param("vip_card_type_code") String vip_card_type_code)throws SQLException;

    List<VipRules> selectByCardHighCode (@Param("corp_code") String corp_code,@Param("high_vip_card_type_code") String high_vip_card_type_code)throws SQLException;

    int deleteByActivity(@Param("activity_code") String activity_code) throws SQLException;

    List<VipRules> selectDegradeByVipType(@Param("corp_code") String corp_code,@Param("vip_type") String vip_type,@Param("degrade_vip_name") String degrade_vip_name,@Param("isactive") String isactive)throws SQLException;

    List<VipRules> selectByVipCardTypeCode(@Param("corp_code") String corp_code,@Param("vip_card_type_code") String vip_card_type_code,@Param("high_vip_card_type_code") String high_vip_card_type_code,@Param("isactive") String isactive)throws SQLException;


}
