package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Corp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CorpMapper {

    //所有企业
    List<Corp> selectAllCorp(@Param("search_value") String search_value);

    //isactive为Y的企业
    List<Corp> selectCorps(@Param("search_value") String search_value);

    Corp selectByCorpId(@Param("corp_id") int corp_id, @Param("corp_code") String corp_code);

    String selectMaxCorpCode();

    int insertCorp(Corp record);

    int updateByCorpId(Corp record);

    int deleteByCorpId(Integer id);

    List<Corp> selectByCorpName(@Param("corp_name") String corp_name);

    int selectCount(@Param("created_date") String created_date);

    int getAreaCount(@Param("corp_code") String corp_code);

    int getBrandCount(@Param("corp_code") String corp_code);

    Corp selectByAppUserName(@Param("app_user_name") String app_user_name);

    int getGoodCount(@Param("corp_code") String corp_code);

    int getGroupCount(@Param("corp_code") String corp_code);

    int getMessageTypeCount(@Param("corp_code") String corp_code);

    List<Corp> selectAllCorpScreen(Map<String, Object> params);
}