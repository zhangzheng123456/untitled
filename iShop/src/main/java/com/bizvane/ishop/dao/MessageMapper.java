package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Message;
import com.bizvane.ishop.entity.MessageInfo;
import com.bizvane.ishop.entity.MessageType;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MessageMapper {

    Message selectByPrimaryKey(@Param("id") Integer id);

    int deleteByPrimaryKey(Integer id);

    int insertMessageInfo(MessageInfo record);

    int insertMessage(Message record);

    List<Message> selectAllMessage(@Param("corp_code") String corp_code,@Param("user_code") String user_code, @Param("search_value") String search_value);

    List<MessageType> selectAllMessageType();

}
