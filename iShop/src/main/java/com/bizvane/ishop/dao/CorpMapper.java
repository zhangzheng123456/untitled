package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.CorpWechat;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CorpMapper {

    //所有企业
    List<Corp> selectAllCorp(@Param("search_value") String search_value,@Param("manager_corp_arr")String[] manager_corp_arr) throws SQLException;

    //isactive为Y的企业
    List<Corp> selectCorps(@Param("search_value") String search_value) throws SQLException;

    Corp selectByCorpId(@Param("corp_id") int corp_id, @Param("corp_code") String corp_code,@Param("isactive") String isactive) throws SQLException;

    int insertCorp(Corp record) throws SQLException;

    int updateByCorpId(Corp record) throws SQLException;

    int deleteByCorpId(Integer id) throws SQLException;

    List<Corp> selectByCorpName(@Param("corp_name") String corp_name,@Param("isactive") String isactive) throws SQLException;

    int selectCount(@Param("created_date") String created_date) throws SQLException;

    int getAreaCount(@Param("corp_code") String corp_code) throws SQLException;

    int getBrandCount(@Param("corp_code") String corp_code) throws SQLException;

    int getGoodCount(@Param("corp_code") String corp_code) throws SQLException;

    int getGroupCount(@Param("corp_code") String corp_code) throws SQLException;

    int getMessageTypeCount(@Param("corp_code") String corp_code) throws SQLException;

    List<Corp> selectAllCorpScreen(Map<String, Object> params) throws SQLException;

    CorpWechat selectWByAppUserName(@Param("app_user_name") String app_user_name) throws SQLException;

    CorpWechat selectWByApp(@Param("app_id") String app_id) throws SQLException;


    CorpWechat selectWByAppId(@Param("corp_code") String corp_code,@Param("app_id") String app_id) throws SQLException;

    List<CorpWechat> selectWByCorp(@Param("corp_code") String corp_code) throws SQLException;

    List<CorpWechat> selectWByCorpBrand(Map<String, Object> params) throws SQLException;

    List<CorpWechat> selectWAuthByCorp(@Param("corp_code") String corp_code) throws SQLException;

    int insertCorpWechat(CorpWechat record) throws SQLException;

    int updateCorpWechat(CorpWechat record) throws SQLException;

    int deleteCorpWechat(@Param("app_id") String app_id,@Param("corp_code") String corp_code) throws SQLException;

    Corp selectByCorpcode(@Param("corp_code")String corp_code) throws SQLException;
}