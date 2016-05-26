package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.CorpInfo;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

public interface CorpInfoMapper {

    List<CorpInfo> selectAllCorp(@Param("search_value") String search_value);

    CorpInfo selectByCorpId(@Param("corp_id") int corp_id,@Param("corp_code") String corp_code);

    String selectMaxCorpCOde();

    int insertCorp(CorpInfo record);

    int updateByCorpId(CorpInfo record);

    int deleteByCorpId(Integer id);

}