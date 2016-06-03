package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Corp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CorpMapper {

    List<Corp> selectAllCorp(@Param("search_value") String search_value);

    Corp selectByCorpId(@Param("corp_id") int corp_id, @Param("corp_code") String corp_code);

    String selectMaxCorpCode();

    int insertCorp(Corp record);

    int updateByCorpId(Corp record);

    int deleteByCorpId(Integer id);

    List<Corp>selectAllCorp();


    Corp selectCorpInfoByUserId(String user_id);
}