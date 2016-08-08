package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Corp;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CorpMapper {

    //所有企业
    List<Corp> selectAllCorp(@Param("search_value") String search_value) throws SQLException;

    //isactive为Y的企业
    List<Corp> selectCorps(@Param("search_value") String search_value) throws SQLException;

    Corp selectByCorpId(@Param("corp_id") int corp_id, @Param("corp_code") String corp_code) throws SQLException;

    String selectMaxCorpCode() throws SQLException;

    int insertCorp(Corp record) throws SQLException;

//    int insertCorpWechatRelation(CorpWechatRelation record);

    int updateByCorpId(Corp record) throws SQLException;

//    int updateCorpWechatRelation(CorpWechatRelation record);

    int deleteByCorpId(Integer id) throws SQLException;

    List<Corp> selectByCorpName(@Param("corp_name") String corp_name) throws SQLException;

    Corp selectByAppUserName(@Param("app_user_name") String app_user_name) throws SQLException;

//    CorpWechatRelation selectByAppid(@Param("app_id") String app_id);

//    List<CorpWechatRelation> selectRelationByCode(@Param("corp_code") String corp_code);

    int selectCount(@Param("created_date") String created_date) throws SQLException;

    int getAreaCount(@Param("corp_code") String corp_code) throws SQLException;

    int getBrandCount(@Param("corp_code") String corp_code) throws SQLException;

    int getGoodCount(@Param("corp_code") String corp_code) throws SQLException;

    int getGroupCount(@Param("corp_code") String corp_code) throws SQLException;

    int getMessageTypeCount(@Param("corp_code") String corp_code) throws SQLException;

    List<Corp> selectAllCorpScreen(Map<String, Object> params) throws SQLException;
}