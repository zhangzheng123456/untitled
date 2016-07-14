package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.MessageMapper;
import com.bizvane.ishop.entity.Message;
import com.bizvane.ishop.entity.MessageInfo;
import com.bizvane.ishop.entity.MessageType;
import com.bizvane.ishop.service.MessageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Date;
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


    /**
     * 消息列表显示
     * 系统管理员/企业管理员
     */
    @Override
    public PageInfo<Message> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        PageHelper.startPage(page_number, page_size);
        List<Message> list = this.messageMapper.selectAllMessage(corp_code, search_value);
        PageInfo<Message> page = new PageInfo<Message>(list);
        return page;
    }

    /**
     * 消息列表显示
     * 区经/店长/导购
     */
    public PageInfo<Message> selectPartBySearch(int page_number, int page_size, String corp_code, String search_value) {
        PageHelper.startPage(page_number, page_size);
        List<Message> list = this.messageMapper.selectAllMessage(corp_code, search_value);
        PageInfo<Message> page = new PageInfo<Message>(list);
        return page;
    }

    @Override
    public Message getMessageById(int id) throws SQLException {
        return messageMapper.selectByPrimaryKey(id);
    }

    @Override
    public String insert(String message,String user_code) {
        try {
            JSONObject json = new JSONObject(message);
            String corp_code = json.get("corp_code").toString();
            String receiver_type = json.get("receiver_type").toString();
            String message_receiver = json.get("message_receiver").toString();
            String message_title = json.get("message_title").toString();
            String message_content = json.get("message_content").toString();
            String isactive = json.get("isactive").toString();
            String message_type = json.get("message_type").toString();

            //调用ice接口，发送消息

            Date now = new Date();
            //插入消息接收人关系
            String message_code1 = corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(now);
            Message msg = new Message();
            msg.setMessage_code(message_code1);
            msg.setReceiver_type(receiver_type);
            msg.setMessage_receiver(message_receiver);
            msg.setModified_date(Common.DATETIME_FORMAT.format(now));
            msg.setModifier(user_code);
            msg.setCreated_date(Common.DATETIME_FORMAT.format(now));
            msg.setCreater(user_code);
            msg.setIsactive(isactive);
            messageMapper.insertMessage(msg);

            //插入消息内容
            MessageInfo msg_info = new MessageInfo();
            msg_info.setMessage_code(message_code1);
            msg_info.setMessage_title(message_title);
            msg_info.setMessage_content(message_content);
            msg_info.setCorp_code(corp_code);
            msg_info.setMessage_sender(user_code);
            msg_info.setMessage_type(message_type);
            msg_info.setModified_date(Common.DATETIME_FORMAT.format(now));
            msg_info.setModifier(user_code);
            msg_info.setCreated_date(Common.DATETIME_FORMAT.format(now));
            msg_info.setCreater(user_code);
            msg_info.setIsactive(isactive);
            messageMapper.insertMessageInfo(msg_info);
            return Common.DATABEAN_CODE_SUCCESS;

        }catch (Exception ex){

        }
        return Common.DATABEAN_CODE_ERROR;
    }


    @Override
    public int delete(int id) throws SQLException {
        return messageMapper.deleteByPrimaryKey(id);
    }


    public List<MessageType> selectAllMessageType() throws SQLException{
        return messageMapper.selectAllMessageType();
    }
}
