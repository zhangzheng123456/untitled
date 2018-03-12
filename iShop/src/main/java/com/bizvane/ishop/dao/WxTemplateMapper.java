package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.WxTemplate;
import com.bizvane.ishop.entity.WxTemplateContent;
import org.apache.ibatis.annotations.Param;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public interface WxTemplateMapper {

    WxTemplate selectById(int id) throws SQLException;

    List<WxTemplate> selectAllWxTemplate(@Param("corp_code") String corp_code,@Param("search_value") String search_value) throws SQLException;

    int insertWxTemplate(WxTemplate record) throws SQLException;

    int updateWxTemplate(WxTemplate record) throws SQLException;

    int deleteWxTemplate(@Param("id") int id) throws SQLException;

    List<WxTemplate> selectTempByAppId(@Param("app_id") String app_id,@Param("app_user_name") String app_user_name,@Param("template_name") String template_name) throws SQLException;

    List<WxTemplate> selectWxTemplateAllScreen(HashMap<String,Object>map) throws  Exception;

    WxTemplate selectByIdAndName(@Param("app_id") String corp_code,@Param("template_id") String template_id,@Param("template_name") String template_name) throws Exception;

    WxTemplateContent selectContentById(@Param("app_id") String app_id, @Param("template_name") String template_name, @Param("type") String type) throws Exception;

    List<WxTemplate> selectByCorpCode(@Param("corp_code")String corp_code,@Param("app_id")String app_id) throws Exception;

}