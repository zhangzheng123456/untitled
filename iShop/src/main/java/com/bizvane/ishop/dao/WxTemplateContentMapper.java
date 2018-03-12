package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.WxTemplateContent;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yanyadong on 2017/6/26.
 */
public interface WxTemplateContentMapper {

    WxTemplateContent selectById(int id) throws SQLException;

    List<WxTemplateContent> selectAllWxTemplateContent(@Param("corp_code") String corp_code,@Param("search_value")String search_value) throws SQLException;

    List<WxTemplateContent> selectWxTemplateContentScreen(HashMap<String,Object>map) throws SQLException;

    int insertWxTemplateContent(WxTemplateContent wxTemplateContent) throws SQLException;

    int updateWxTemplateContent(WxTemplateContent wxTemplateContent) throws  SQLException;

    int deleteWxTemplateContent(@Param("id") int id) throws  SQLException;

    List<WxTemplateContent> selectByName(@Param("app_id")String app_id,@Param("template_name") String template_name) throws Exception;

    List<WxTemplateContent> selectContentById(@Param("app_id")String app_id,@Param("template_name")String template_name,@Param("type") String type,@Param("vip_card_type") String vip_card_type) throws Exception;

    List<WxTemplateContent> selectContentByCardId(@Param("app_id")String app_id,@Param("template_name")String template_name,@Param("type") String type,@Param("vip_card_type_id") String vip_card_type_id) throws Exception;

}
