package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.MsgChannels;
import com.bizvane.ishop.entity.MsgChannels;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yan on 2016/8/10.
 */
public interface MsgChannelsMapper {
    MsgChannels selectById(int id) throws SQLException;
    List<MsgChannels> selectChannels(@Param("search_value") String search_value) throws SQLException;

    List<MsgChannels> selectAllChannels() throws SQLException;

    int insertChannel(MsgChannels msgChannels) throws SQLException;

    int updateChannel(MsgChannels msgChannels) throws SQLException;

    int deleteByChannelId(int id) throws SQLException;

    List<MsgChannels> selectChannelScreen(Map<String, Object> params) throws SQLException;

}
