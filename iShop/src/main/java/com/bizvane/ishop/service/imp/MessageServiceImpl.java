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
    public int delete(int id) throws SQLException {
        return messageMapper.deleteByPrimaryKey(id);
    }


    @Override
    public PageInfo<Message> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        PageHelper.startPage(page_number, page_size);
        List<Message> list = this.messageMapper.selectAllMessage(corp_code, search_value);
        PageInfo<Message> page = new PageInfo<Message>(list);
        return page;
    }

}
