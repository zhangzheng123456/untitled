package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.MessageMapper;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.MessageService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

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
    private StoreService storeService;
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
        List<MessageInfo> list = this.messageMapper.selectAllMessageInfo(corp_code, user_code, search_value,null);
        for (MessageInfo message : list) {
            message.setIsactive(CheckUtils.CheckIsactive(message.getIsactive()));
        }
        PageInfo<MessageInfo> page = new PageInfo<MessageInfo>(list);
        return page;
    }

    @Override
    public PageInfo<MessageInfo> selectBySearch(int page_number, int page_size, String corp_code, String user_code, String search_value,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        PageHelper.startPage(page_number, page_size);
        List<MessageInfo> list = this.messageMapper.selectAllMessageInfo(corp_code, user_code, search_value,manager_corp_arr);
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
    public List<Message> selectMessageByCode(String message_code) throws Exception {
        return messageMapper.selectMessageByCode(message_code);
    }

    @Override
    public List<Message> getMessageDetail(String message_code) throws Exception {
        MessageInfo messageInfo = messageMapper.selectMessageInfoByCode(message_code);
        String receiver_type = messageInfo.getReceiver_type();
        String corp_code = messageInfo.getCorp_code();
        int id = messageInfo.getId();
        List<Message> messages = new ArrayList<Message>();
        List<Map<String,String>> userList = new ArrayList<Map<String,String>>();
        if (receiver_type.equals("staff")) {
            List<Message> messageLists = messageMapper.selectMessageDetail(message_code);
            for (int i = 0; i < messageLists.size(); i++) {
                Map<String,String> user = new HashMap<String, String>();
                user.put("user_name",messageLists.get(i).getUser_name());
                user.put("user_code",messageLists.get(i).getUser_code());
                userList.add(user);
            }
        } else if (receiver_type.equals("store")) {
                List<Message> messageLists = selectMessageByCode(message_code);
                String store_code = "";
                for (int i = 0; i < messageLists.size(); i++) {
                    store_code = messageLists.get(i).getMessage_receiver();
                    List<User> users = userMapper.selectStoreUser(corp_code, store_code, "", "", Common.IS_ACTIVE_Y,"");
                    //去重
                    for (int j = 0; j <users.size() ; j++) {
                        Map<String,String> user = new HashMap<String, String>();
                        user.put("user_name",users.get(j).getUser_name());
                        user.put("user_code",users.get(j).getUser_code());
                        if (!userList.contains(user)){
                            userList.add(user);
                        }
                    }
                }
            } else if (receiver_type.equals("area")) {
                List<Message> messageLists = selectMessageByCode(message_code);
                String area_code = "";
//                List<User> userList = new ArrayList<User>();
                for (int i = 0; i < messageLists.size(); i++) {
                    String area_code1 = messageLists.get(i).getMessage_receiver();
                    area_code = area_code + area_code1 + ",";
                }
                String[] areas = area_code.split(",");
                List<Store> store = storeService.selectByAreaBrand(corp_code, areas,null, null, Common.IS_ACTIVE_Y);
                for (int i = 0; i < store.size(); i++) {
                    List<User> users = userMapper.selectStoreUser(corp_code, store.get(i).getStore_code(), "", "", Common.IS_ACTIVE_Y,"");
                    for (int j = 0; j <users.size() ; j++) {
                        Map<String,String> user = new HashMap<String, String>();
                        user.put("user_name",users.get(j).getUser_name());
                        user.put("user_code",users.get(j).getUser_code());
                        if (!userList.contains(user)){
                            userList.add(user);
                        }
                    }

                }
            } else if (receiver_type.equals("corp")) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("array", null);
                params.put("search_value", "");
                params.put("role_code", "");
                params.put("corp_code", corp_code);
                //根据areas拉取区经
                params.put("areas", null);
                List<User> users = userMapper.selectUsersByRole(params);
                for (int j = 0; j <users.size() ; j++) {
                    Map<String,String> user = new HashMap<String, String>();
                    user.put("user_name",users.get(j).getUser_name());
                    user.put("user_code",users.get(j).getUser_code());
                    userList.add(user);
                }
            }
            for (int i = 0; i < userList.size(); i++) {
                String user_code = userList.get(i).get("user_code");
                String user_name = userList.get(i).get("user_name");

                Message message = new Message();
                message.setId(i);
                message.setMessage_receiver(user_name);
                message.setStatus("N");
                List<User> users1 = messageMapper.selectMessageStatus(corp_code, user_code, String.valueOf(id));
                if ( users1.size() > 0) {
                    message.setStatus("Y");
                }
                messages.add(message);
            }

        return messages;
    }

    @Override
    @Transactional
    public String insert(String message, String user_id) throws Exception {
        String result = "";
        JSONObject json = new JSONObject();
        json.put("message",message);
        String corp_code = json.get("corp_code").toString();
        String receiver_type = json.get("receiver_type").toString();
        String message_receiver = json.get("message_receiver").toString();
        String message_title = json.get("message_title").toString();
        String message_content = json.get("message_content").toString();
//        String message_type = json.get("message_type").toString();
        String message_type = "后台通知";

        Data data_group_code = new Data("group_code", "", ValueType.PARAM);
        Data data_phone = new Data("phone", "", ValueType.PARAM);

        if (receiver_type.equals("group")) {
            data_group_code = new Data("group_code", message_receiver, ValueType.PARAM);
        } else {
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

        DataBox dataBox = iceInterfaceService.iceInterface("MessageForWeb", datalist);
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
        MessageInfo message = getMessageById(id);
        if (message != null) {
            String message_code = message.getMessage_code();
            messageMapper.deleteMessage(message_code);
        }
        messageMapper.deleteMessageStatus(id);
        return messageMapper.deleteMessageInfo(id);
    }


    public List<MessageType> selectAllMessageType() throws Exception {
        return messageMapper.selectAllMessageType();
    }

    @Override
    public PageInfo<MessageInfo> selectByScreen(int page_number, int page_size, String corp_code, String user_code, Map<String, String> map) throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        if (map.containsKey("modified_date")){
            JSONObject date = JSONObject.parseObject(map.get("modified_date"));
            params.put("created_date_start", date.get("start").toString());
            params.put("created_date_end", date.get("end").toString()+"24:00:00");
            map.remove("modified_date");
        }

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

    @Override
    public PageInfo<MessageInfo> selectByScreen(int page_number, int page_size, String corp_code, String user_code, Map<String, String> map,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        if (map.containsKey("modified_date")){
            JSONObject date = JSONObject.parseObject(map.get("modified_date"));
            params.put("created_date_start", date.get("start").toString());
            params.put("created_date_end", date.get("end").toString()+"24:00:00");
            map.remove("modified_date");
        }
        params.put("manager_corp_arr", manager_corp_arr);
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
