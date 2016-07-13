package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.MessageType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by lixiang on 2016/6/21.
 *
 * @@version
 */
public interface MessageTypeMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(MessageType message_type);

    MessageType selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(MessageType message_type);

    List<MessageType> selectAllMessage_type(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    MessageType selectCode(@Param("corp_code") String corp_code, @Param("type_code") String type_code);

    MessageType selectName(@Param("corp_code") String corp_code, @Param("type_name") String type_name);

    List<MessageType> selectAllMessageType();

    String messageTypeCodeExist(@Param("type_code") String type_code, @Param("corp_code") String corp_code);

    int selectUserAchvCount(@Param("corp_code") String corp_code, @Param("user_code") String user_code);

//    List<MessageType> selectBySearch(String corp_code, String search_value);
}
