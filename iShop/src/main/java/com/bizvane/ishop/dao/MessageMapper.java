package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Message;
import com.bizvane.ishop.entity.MessageInfo;
import com.bizvane.ishop.entity.MessageType;
import com.bizvane.ishop.entity.User;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface MessageMapper {

    MessageInfo selectByPrimaryKey(@Param("id") Integer id) throws SQLException;

    MessageInfo selectMessageInfoByCode(@Param("message_code") String message_code) throws SQLException;

    List<Message> selectMessageByCode(@Param("message_code") String message_code) throws SQLException;

    int deleteMessageInfo(@Param("id") Integer id) throws SQLException;

    int deleteMessage(@Param("message_code") String message_code) throws SQLException;

    int deleteMessageStatus(@Param("id") Integer id) throws SQLException;
//    int insertMessageInfo(MessageInfo record) throws SQLException;
//
//    int insertMessage(Message record) throws SQLException;

    List<MessageInfo> selectAllMessageInfo(@Param("corp_code") String corp_code, @Param("user_code") String user_code, @Param("search_value") String search_value,@Param("manager_corp_arr") String[] manager_corp_arr) throws SQLException;

    List<Message> selectMessageDetail(@Param("message_code") String message_code) throws SQLException;

    List<MessageType> selectAllMessageType() throws SQLException;

    List<MessageInfo> selectAllMessageInfoScreen(Map<String, Object> params) throws SQLException;

    List<User> selectMessageStatus(@Param("corp_code") String corp_code, @Param("user_code") String user_code, @Param("message_id") String message_id) throws SQLException;
}
