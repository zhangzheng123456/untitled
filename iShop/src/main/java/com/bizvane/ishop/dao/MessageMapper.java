package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MessageMapper {

    Message selectByPrimaryKey(@Param("id") Integer id);

    int deleteByPrimaryKey(Integer id);

    int insert(Message record);

    List<Message> selectAllMessage(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

   }
