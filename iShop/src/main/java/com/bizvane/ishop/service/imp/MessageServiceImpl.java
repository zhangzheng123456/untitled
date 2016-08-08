package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.MessageMapper;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.MessageService;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
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
    private UserMapper userMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    IceInterfaceService iceInterfaceService;

    private static final Logger logger = Logger.getLogger(MessageServiceImpl.class);


    /**
     * 消息列表显示
     *
     */
    @Override
    public PageInfo<MessageInfo> selectBySearch(int page_number, int page_size, String corp_code, String user_code, String search_value) throws Exception{
        PageHelper.startPage(page_number, page_size);
        List<MessageInfo> list = this.messageMapper.selectAllMessageInfo(corp_code, user_code, search_value);
        for (MessageInfo message:list) {
            if(message.getIsactive().equals("Y")){
                message.setIsactive("是");
            }else{
                message.setIsactive("否");
            }
        }
        PageInfo<MessageInfo> page = new PageInfo<MessageInfo>(list);
        return page;
    }

    @Override
    public MessageInfo getMessageById(int id) throws Exception {
        return messageMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Message> getMessageDetail(String message_code) throws Exception {
        return messageMapper.selectMessageDetail(message_code);
    }

    @Override
    public String insert(String message, String user_code) throws Exception {
        String result = "";
        try {
            JSONObject json = new JSONObject(message);
            String corp_code = json.get("corp_code").toString();
            String receiver_type = json.get("receiver_type").toString();
            String message_receiver = json.get("message_receiver").toString();
            String message_title = json.get("message_title").toString();
            String message_content = json.get("message_content").toString();
            String isactive = json.get("isactive").toString();
            String message_type = json.get("message_type").toString();

            List<User> users = userMapper.selectUserCode(user_code, corp_code);
            User user =users.get(0);
            String avater = user.getAvatar();
            String user_name = user.getUser_name();
            //调用ice接口，发送消息

            Data data_user_id = new Data("user_id", message_receiver, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_message_content = new Data("message_content", message_content, ValueType.PARAM);
            Data data_message_title = new Data("message_title", message_title, ValueType.PARAM);
            Data data_message_type = new Data("message_type", message_type, ValueType.PARAM);
            Data headimgurl = new Data("headimgurl", avater, ValueType.PARAM);
            Data nickname = new Data("nickname", user_name, ValueType.PARAM);
            Data send_type = new Data("send_type", "1", ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_user_id.key, data_user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_message_content.key, data_message_content);
            datalist.put(data_message_title.key, data_message_title);
            datalist.put(data_message_type.key, data_message_type);
            datalist.put(headimgurl.key, headimgurl);
            datalist.put(nickname.key, nickname);
            datalist.put(send_type.key, send_type);

            DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.ChatToUser", datalist);
            logger.info(dataBox.data.get("message").value);
            String messgage1 = dataBox.data.get("message").value;
            if (messgage1.contains("发送成功")) {
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
            } else {
                result = "发送失败";
            }
        } catch (Exception ex) {
            result = ex.getMessage();
        }
        return result;
    }


    @Override
    public int delete(int id) throws Exception {
        return messageMapper.deleteByPrimaryKey(id);
    }


    public List<MessageType> selectAllMessageType() throws Exception {
        return messageMapper.selectAllMessageType();
    }
}
