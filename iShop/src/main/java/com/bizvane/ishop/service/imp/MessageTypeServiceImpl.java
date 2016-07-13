package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.MessageTypeMapper;
import com.bizvane.ishop.entity.MessageType;
import com.bizvane.ishop.service.MessageTypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/22.
 *
 * @@version
 */
@Service
public class MessageTypeServiceImpl implements MessageTypeService {

    @Autowired
    private MessageTypeMapper messageTypeMapper;

    @Override
    public MessageType getMessageTypeById(int id) throws SQLException {
        return messageTypeMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(MessageType messageType) throws SQLException {
        return this.messageTypeMapper.insert(messageType);
    }

    @Override
    public String update(MessageType messageType) throws SQLException {
        // this.messageTypeMapper.updateByPrimaryKey(messageType);
        MessageType old = this.messageTypeMapper.selectByPrimaryKey(messageType.getId());
        if (!old.getType_code().equals(messageType.getType_code())
                && (this.messageTypeCodeExist(messageType.getCorp_code(), messageType.getType_code()).equals(Common.DATABEAN_CODE_ERROR))) {
            return "编号已经存在！！！！";
        } else if (!old.getType_name().equals(messageType.getType_name())
                && (this.messageTypeNameExist(messageType.getCorp_code(), messageType.getType_name()).equals(Common.DATABEAN_CODE_ERROR))) {
            return "名称已经存在！！！！";
        } else if (this.messageTypeMapper.updateByPrimaryKey(messageType) >= 0) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public PageInfo<MessageType> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        PageHelper.startPage(page_number, page_size);
        // List<MessageType> list = this.messageTypeMapper.selectBySearch(corp_code, search_value);
        List<MessageType> list = this.messageTypeMapper.selectAllMessage_type(corp_code, search_value);
        PageInfo<MessageType> page = new PageInfo<MessageType>(list);
        return page;
    }

    @Override
    public String MessageTypeCodeExist(String corp_code, String type_code) {
        MessageType message_type = this.messageTypeMapper.selectCode(corp_code, type_code);
        String result = Common.DATABEAN_CODE_ERROR;
        if (message_type == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }

    @Override
    public String MessageTypeNameExist(String corp_code, String type_name) {

        MessageType message_type = this.messageTypeMapper.selectName(corp_code, type_name);
        String result = Common.DATABEAN_CODE_ERROR;
        if (message_type == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }

    @Override
    public int deleteById(int id) {
        return this.messageTypeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<MessageType> selectAllMessageType() {
        return this.messageTypeMapper.selectAllMessageType();
    }

    @Override
    public List<MessageType> getMessageTypeByCorp(String corp_code, String search_value) {
        return this.messageTypeMapper.selectAllMessage_type(corp_code, search_value);
    }

    @Override
    public String messageTypeNameExist(String corp_code, String type_name) {
        MessageType message_type = messageTypeMapper.selectName(corp_code, type_name);
        String result = Common.DATABEAN_CODE_ERROR;
        if (message_type == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }

    @Override
    public String messageTypeCodeExist(String corp_code, String type_code) {
        MessageType message_type = messageTypeMapper.selectName(corp_code, type_code);
        String result = Common.DATABEAN_CODE_ERROR;
        if (message_type == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }

    @Override
    public int selectMessageTemplateCount(String corp_code, String type_code) {
        return this.messageTypeMapper.selectUserAchvCount(corp_code, type_code);
    }


    @Override
    public List<MessageType> getMessageTypeByCorp(String corp_code) {

        return this.messageTypeMapper.selectAllMessage_type(corp_code, "");
    }


}
