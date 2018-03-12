package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.CorpWechat;
import com.bizvane.ishop.entity.Store;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface CorpService {

    Corp selectByCorpId(int corp_id, String corp_code,String isactive) throws Exception;

    String insert(String message,String user_id) throws Exception;

    String update(String message,String user_id) throws Exception;

    int deleteByCorpId(int id) throws Exception;

    PageInfo<Corp> selectAllCorp(int page_number, int page_size, String search_value) throws Exception;

    PageInfo<Corp> selectAllCorp(int page_number, int page_size, String search_value,String manager_corp) throws Exception;


    List<Corp> selectAllCorp() throws Exception;

    String getCorpByCorpName(String corp_name,String isactive) throws Exception;

    int getAreaCount(String corp_code) throws Exception;

    int getBranCount(String corp_code)  throws Exception;

    int getGoodCount(String corp_code)  throws Exception;

    int getGroupCount(String corp_code)  throws Exception;

    int getGoodsCount(String corp_code)  throws Exception;

    int getMessagesTypeCount(String corp_code)  throws Exception;

    int selectCount(String create_date)  throws Exception;

    String insertExecl(Corp corp)  throws Exception;

    PageInfo<Corp> selectAllCorpScreen(int page_number, int page_size, Map<String, String> map)  throws Exception;

    CorpWechat getCorpByAppUserName(String app_user_name)  throws Exception;

    CorpWechat getCorpByApp(String app_id)  throws Exception;

    CorpWechat getCorpByAppId(String corp_code,String app_id)  throws Exception;

    List<CorpWechat> getWByCorp(String corp_code) throws Exception;

    List<CorpWechat> getWAuthByCorp(String corp_code) throws Exception;

    List<CorpWechat> selectWByCorpBrand(String corp_code,String brand_code) throws Exception;

    int deleteCorpWechat(String app_id,String corp_code) throws Exception;

    String updateCorpWechat(JSONArray wechat, String corp_code, String user_code) throws Exception;
}
