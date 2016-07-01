package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Message_type;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by lixiang on 2016/6/21.
 *
 * @@version
 */
public interface MessageTypeMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Message_type message_type);

    Message_type selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Message_type message_type);

    List<Message_type> selectAllMessage_type(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    Message_type selectCode(@Param("corp_code") String corp_code, @Param("type_code") String type_code);

    Message_type selectName(@Param("corp_code") String corp_code, @Param("type_name") String type_name);

    List<Message_type> selectAllMessageType();

    String messageTypeCodeExist(@Param("type_code") String type_code, @Param("corp_code") String corp_code);

    int selectUserAchvCount(@Param("corp_code") String corp_code, @Param("user_code") String user_code);
}
