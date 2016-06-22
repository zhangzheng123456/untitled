package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.MessageTypeMapper;
import com.bizvane.ishop.entity.Message_type;
import com.bizvane.ishop.service.MessageTypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/22.
 *
 * @@version
 */
public class MessageTypeServiceImpl implements MessageTypeService {

    @Autowired
    private MessageTypeMapper messageTypeMapper;

    @Override
    public Message_type getMessageTypeById(int id) throws SQLException {
        return messageTypeMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(Message_type messageType) throws SQLException {
        return this.messageTypeMapper.insert(messageType);
    }

    @Override
    public int update(Message_type messageType) throws SQLException {
        return this.messageTypeMapper.updateByPrimaryKey(messageType);
    }

    @Override
    public PageInfo<Message_type> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        PageHelper.startPage(page_number, page_size);
        //List<Message_type> list = this.messageTypeMapper.selectBySearch(corp_code, search_value);
        List<Message_type> list = this.messageTypeMapper.selectAllMessage_type(corp_code, search_value);
        PageInfo<Message_type> page = new PageInfo<Message_type>(list);
        return page;
    }

    @Override
    public String MessageTypeCodeExist(String type_code, String corp_code) {

        Message_type message_type = this.messageTypeMapper.selectCode(corp_code, type_code);
        String result = Common.DATABEAN_CODE_ERROR;
        if (message_type == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }

    @Override
    public String MessageTypeNameExist(String type_name, String corp_code) {

        Message_type message_type = this.messageTypeMapper.selectName(corp_code, type_name);
        String result = Common.DATABEAN_CODE_ERROR;
        if (message_type == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }
}