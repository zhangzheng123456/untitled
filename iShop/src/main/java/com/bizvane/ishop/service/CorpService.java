package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.Store;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface CorpService {

    Corp selectByCorpId(int corp_id, String corp_code) throws Exception;

    String insert(String message,String user_id) throws Exception;

    String update(String message,String user_id) throws Exception;

    int deleteByCorpId(int id) throws Exception;

    PageInfo<Corp> selectAllCorp(int page_number, int page_size, String search_value) throws Exception;

    List<Corp> selectAllCorp() throws Exception;

    String selectMaxCorpCode()throws Exception;

    String getCorpByCorpName(String corp_name) throws Exception;

    int getAreaCount(String corp_code) throws Exception;

    int getBranCount(String corp_code)  throws Exception;

    int getGoodCount(String corp_code)  throws Exception;

    Corp getCorpByAppUserName(String app_user_name)  throws Exception;

    int getGroupCount(String corp_code)  throws Exception;

    int getGoodsCount(String corp_code)  throws Exception;

    int getMessagesTypeCount(String corp_code)  throws Exception;

    int selectCount(String create_date)  throws Exception;

    String insertExecl(Corp corp)  throws Exception;

    PageInfo<Corp> selectAllCorpScreen(int page_number, int page_size, Map<String, String> map)  throws Exception;
}
