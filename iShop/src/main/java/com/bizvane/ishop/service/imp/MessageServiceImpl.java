package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.MessageMapper;
import com.bizvane.ishop.entity.Message;
import com.bizvane.ishop.service.MessageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lixiang on 2016/7/4.
 *
 * @@version
 */
@Service
public class MessageServiceImpl implements MessageService {


    @Autowired
    private MessageMapper messageMapper;

    @Override
    public Message getMessageById(int id) throws SQLException {
        return messageMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(Message message) throws SQLException {
        return messageMapper.insert(message);
    }


    @Override
    public int update(Message message) throws SQLException {
        return messageMapper.updateByPrimaryKey(message);
    }

    @Override
    public int delete(int id) throws SQLException {
        return messageMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Message getMessageByCode(String corp_code, String message_code) {
        return messageMapper.selectByCode(corp_code, message_code);
    }

    @Override
    public PageInfo<Message> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        PageHelper.startPage(page_number, page_size);
        List<Message> list = this.messageMapper.selectAllMessage(corp_code, search_value);
        PageInfo<Message> page = new PageInfo<Message>(list);
        return page;
    }

    @Override
    public PageInfo<Message> selectByUser(int page_number, int page_size, String corp_code, String user_code) throws SQLException {
        PageHelper.startPage(page_number, page_size);
        List<Message> list = messageMapper.selectByUser(corp_code, user_code);
        PageInfo<Message> page = new PageInfo<Message>(list);
        return page;
    }

    @Override
    public PageInfo<Message> selectBySearchPart(int page_number, int page_size, String corp_code, String search_values, String store_code, String role_code) {
        String[] stores = store_code.split(",");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
        params.put("search_value", search_values);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        List<Message> messages;
        PageHelper.startPage(page_number, page_size);
        messages = messageMapper.selectPartMessage(params);
        PageInfo<Message> page = new PageInfo<Message>(messages);
        return page;
    }
}
