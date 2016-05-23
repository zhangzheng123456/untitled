package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.CorpInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface CorpService {

    CorpInfo selectByCorpId(int corp_id,String corp_code)throws SQLException;

    int insertCorp(CorpInfo corpInfo)throws SQLException;

    int updateByCorpId(CorpInfo corpInfo)throws SQLException;

    int deleteByCorpId(int id)throws SQLException;

    List<CorpInfo> selectAllCorp(String search_value)throws SQLException;


}
