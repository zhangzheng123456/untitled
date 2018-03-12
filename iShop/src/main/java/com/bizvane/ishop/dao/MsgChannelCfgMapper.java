package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.AppIcon;
import com.bizvane.ishop.entity.AppIconCfg;
import com.bizvane.ishop.entity.MsgChannelCfg;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by zhou on 2017/1/5.
 */
public interface MsgChannelCfgMapper {
    List<MsgChannelCfg> selectAllMsgChannelCfg(@Param("corp_code") String corp_code,  @Param("search_value") String search_value,@Param("manager_corp_arr") String[] manager_corp_arr) throws SQLException;

    MsgChannelCfg selMsgChannelCfgById(int id)throws SQLException;

    int updateMsgChannelCfg(MsgChannelCfg msgChannelCfg) throws SQLException;

    int insertMsgChannelCfg(MsgChannelCfg msgChannelCfg) throws SQLException;

    List<MsgChannelCfg> selMsgChannelCfgByCorp(@Param("corp_code") String corp_code, @Param("isactive") String isactive)throws SQLException;

    int delMsgChannelCfgById(int id);

    MsgChannelCfg selMsgChannelCfgByAccount(@Param("corp_code") String corp_code,@Param("type") String type,@Param("channel_name") String channel_name,@Param("channel_account") String channel_account,@Param("channel_child") String channel_child,@Param("channel_sign") String channel_sign, @Param("isactive") String isactive)throws SQLException;

    MsgChannelCfg selMsgChannelCfgBySign(@Param("corp_code") String corp_code,@Param("channel_sign") String channel_sign, @Param("isactive") String isactive)throws SQLException;


    List<MsgChannelCfg>  selMsgChannelCfgByType(@Param("corp_code") String corp_code,@Param("type") String type, @Param("isactive") String isactive)throws SQLException;

    List<MsgChannelCfg> selectByBrand(Map<String, Object> params) throws SQLException;

    List<MsgChannelCfg> selectByBrandForProduction(Map<String, Object> params) throws SQLException;



}
