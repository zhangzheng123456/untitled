package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Corp;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface CorpService {

    Corp selectByCorpId(int corp_id, String corp_code) throws SQLException;

    String insert(String message,String user_id) throws SQLException;

    String update(String message,String user_id) throws SQLException;

    int deleteByCorpId(int id) throws SQLException;

    PageInfo<Corp> selectAllCorp(int page_number, int page_size, String search_value) throws SQLException;

    List<Corp> selectAllCorp() throws SQLException;

    String selectMaxCorpCode()throws SQLException;

    String getCorpByCorpName(String corp_name) throws SQLException;

    int getAreaCount(String corp_code);

    int getBranCount(String corp_code);

    int getGoodCount(String corp_code);

    Corp getCorpByAppUserName(String app_user_name);

    int getGroupCount(String corp_code);

    int getGoodsCount(String corp_code);

    int getMessagesTypeCount(String corp_code);

    int selectCount(String create_date);
}
