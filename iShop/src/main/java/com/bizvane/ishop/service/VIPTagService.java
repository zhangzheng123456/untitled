package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VIPtag;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
public interface VIPTagService {
    VIPtag getVIPTagById(int id) throws SQLException;

    int insert(VIPtag vipTag) throws SQLException;

    int delete(int id) throws SQLException;

    int update(VIPtag vipTag);

    PageInfo<VIPtag> selectBySearch(int page_number, int page_size, String corp_code, String search_value);

    String vipExist(String tag_code, String corp_code) throws SQLException;


}
