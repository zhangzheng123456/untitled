package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Corp;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface CorpService {

    Corp selectByCorpId(int corp_id, String corp_code)throws SQLException;

    int insertCorp(Corp corp)throws SQLException;

    int updateByCorpId(Corp corp)throws SQLException;

    int deleteByCorpId(int id)throws SQLException;

    PageInfo<Corp> selectAllCorp(int page_number, int page_size, String search_value)throws SQLException;

    List<Corp> selectAllCorp()throws SQLException;


    String selectMaxCorpCode();







}
