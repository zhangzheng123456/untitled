package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.MsgChannels;
import com.bizvane.ishop.entity.MsgChannels;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yan on 2016/8/10.
 */
public interface MsgChannelsService {

    MsgChannels getChannelsById(int id) throws SQLException;

    PageInfo<MsgChannels> getAllChannelsByPage(int page_number, int page_size, String search_value) throws Exception;

    List<MsgChannels> getAllChannels() throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id) throws Exception;

    PageInfo<MsgChannels> selectChannelsScreen(int page_number, int page_size, Map<String, String> map) throws Exception;

}
