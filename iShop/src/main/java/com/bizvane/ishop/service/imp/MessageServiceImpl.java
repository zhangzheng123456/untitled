package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.MessageMapper;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.MessageService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     */
    @Override
    public PageInfo<MessageInfo> selectBySearch(int page_number, int page_size, String corp_code, String user_code, String search_value) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<MessageInfo> list = this.messageMapper.selectAllMessageInfo(corp_code, user_code, search_value);
        for (MessageInfo message : list) {
            message.setIsactive(CheckUtils.CheckIsactive(message.getIsactive()));
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
    @Transactional
    public String insert(String message, String user_id) throws Exception {
        String result = "";
        JSONObject json = new JSONObject(message);
        String corp_code = json.get("corp_code").toString();
        String receiver_type = json.get("receiver_type").toString();
        String message_receiver = json.get("message_receiver").toString();
        String message_title = json.get("message_title").toString();
        String message_content = json.get("message_content").toString();
        String message_type = json.get("message_type").toString();

        Data data_group_code = new Data("group_code", "", ValueType.PARAM);
        Data data_phone = new Data("phone", "", ValueType.PARAM);

        if (receiver_type.equals("group")) {
            data_group_code = new Data("group_code", message_receiver, ValueType.PARAM);
        }else {
            String phone = json.get("phone").toString();
            data_phone = new Data("phone", phone, ValueType.PARAM);
        }
        Data data_user_id = new Data("user_id", "", ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_store_code = new Data("store_id", "", ValueType.PARAM);
        Data data_area_code = new Data("area_code", "", ValueType.PARAM);
        Data data_message_content = new Data("message_content", message_content, ValueType.PARAM);
        Data data_message_title = new Data("message_title", message_title, ValueType.PARAM);
        Data data_message_type = new Data("message_type", message_type, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_area_code.key, data_area_code);
        datalist.put(data_phone.key, data_phone);
        datalist.put(data_message_content.key, data_message_content);
        datalist.put(data_message_title.key, data_message_title);
        datalist.put(data_message_type.key, data_message_type);
        datalist.put(data_group_code.key, data_group_code);

        DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.MessageForWeb", datalist);
        String messgage1 = dataBox.data.get("message").value;
        if (messgage1.contains("发送成功")) {
            return Common.DATABEAN_CODE_SUCCESS;
        } else {
            result = "发送失败";
        }
        return result;
    }


    @Override
    @Transactional
    public int delete(int id) throws Exception {
        String message_code = getMessageById(id).getMessage_code();
        messageMapper.deleteMessage(message_code);
        return messageMapper.deleteMessageInfo(id);
    }


    public List<MessageType> selectAllMessageType() throws Exception {
        return messageMapper.selectAllMessageType();
    }

    @Override
    public PageInfo<MessageInfo> selectByScreen(int page_number, int page_size, String corp_code, String user_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("user_code", user_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<MessageInfo> list = this.messageMapper.selectAllMessageInfoScreen(params);
        for (MessageInfo message : list) {
            message.setIsactive(CheckUtils.CheckIsactive(message.getIsactive()));
        }
        PageInfo<MessageInfo> page = new PageInfo<MessageInfo>(list);
        return page;
    }

}
