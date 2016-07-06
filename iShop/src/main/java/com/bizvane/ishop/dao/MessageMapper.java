package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MessageMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Message record);

    Message selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Message record);


    List<Message> selectAllMessage(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    List<Message> selectByUser(@Param("corp_code") String corp_code, @Param("user_code") String user_code);

    List<Message> selectPartMessage(Map<String, Object> params);

    Message selectByCode(@Param("corp_code") String corp_code, @Param("tem_code") String tem_code);
}