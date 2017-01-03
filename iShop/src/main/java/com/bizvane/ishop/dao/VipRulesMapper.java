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

    VipRules selectByVipType(@Param("corp_code") String corp_code,@Param("vip_type") String vip_type,@Param("isactive") String isactive)throws SQLException;


}
