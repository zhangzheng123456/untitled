package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.MessageQuickReply;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/9/10.
 */
public interface MessageQuickReplyMapper {
    MessageQuickReply selectQuickReplyById(int id) throws SQLException;



    List<MessageQuickReply> selectMessageQuickReplys(@Param("corp_code") String corp_code) throws SQLException;

    List<MessageQuickReply> selectAllMessageQuickReply(@Param("corp_code") String corp_code, @Param("search_value") String search_value,@Param("manager_corp_arr") String[] manager_corp_arr) throws SQLException;


    int insertMessageQuickReply(MessageQuickReply messageQuickReply) throws SQLException;

    int updateMessageQuickReply(MessageQuickReply messageQuickReply) throws SQLException;

    int deleteMessageQuickReplyById(int id) throws SQLException;

    MessageQuickReply selectByMessageQuickReplyCode(@Param("corp_code") String corp_code, @Param("content") String content, @Param("isactive") String isactive) throws SQLException;

    List<MessageQuickReply> selectQuickReplyScreen(Map<String, Object> params) throws SQLException;

    List<MessageQuickReply> selQuickReplyCountByBrand(Map<String, Object> params) throws SQLException;
}
