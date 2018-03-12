package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.MsgChannelsMapper;
import com.bizvane.ishop.dao.MsgChannelsMapper;
import com.bizvane.ishop.entity.MsgChannels;
import com.bizvane.ishop.entity.MsgChannels;
import com.bizvane.ishop.service.MsgChannelsService;
import com.bizvane.ishop.service.MsgChannelsService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yan on 2016/8/10.
 */
@Service
public class MsgChannelsServiceImpl implements MsgChannelsService {
    @Autowired
    MsgChannelsMapper msgChannelsMapper;


    @Override
    public MsgChannels getChannelsById(int id) throws SQLException {
        return msgChannelsMapper.selectById(id);
    }

    @Override
    public PageInfo<MsgChannels> getAllChannelsByPage(int page_number, int page_size, String search_value) throws Exception {
        List<MsgChannels> msgChannelses;
        PageHelper.startPage(page_number, page_size);
        msgChannelses = msgChannelsMapper.selectChannels(search_value);
        String result = "";
        for (MsgChannels msgChannels : msgChannelses) {
            msgChannels.setIsactive(CheckUtils.CheckIsactive(msgChannels.getIsactive()));
        }
        PageInfo<MsgChannels> page = new PageInfo<MsgChannels>(msgChannelses);
        return page;
    }

    @Override
    public List<MsgChannels> getAllChannels() throws Exception {
        List<MsgChannels> msgChannelses;
        msgChannelses = msgChannelsMapper.selectAllChannels();
        return msgChannelses;
    }

    @Override
    public String insert(String message, String user_id) throws Exception {
       return "";
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        return "";

    }

    @Override
    public int delete(int id) throws Exception {
        return msgChannelsMapper.deleteByChannelId(id);
    }

    @Override
    public PageInfo<MsgChannels> selectChannelsScreen(int page_number, int page_size, Map<String, String> map) throws Exception {
        return null;
    }

}
