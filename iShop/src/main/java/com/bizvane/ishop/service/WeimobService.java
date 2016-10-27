package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.VIPEmpRelation;
import com.bizvane.ishop.entity.VIPStoreRelation;
import com.bizvane.ishop.entity.Weimob;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhou on 2016/6/1.
 *
 * @@version
 */
public interface WeimobService {

    Weimob selectByCorpId(String corp_code) throws Exception;

    int insert(Weimob weimob) throws Exception;

    int update(Weimob weimob) throws Exception;

    JSONArray getList(String accessToken, int rowno) throws Exception;

    JSONArray goodsclassifyGet(String accessToken) throws Exception;

    JSONArray goodsclassifyGetSon(String accessToken) throws Exception;

    JSONArray getSearchClassify(String accessToken,String xx) throws Exception;

    JSONArray getSearchTitle(String accessToken,String xx) throws Exception;

    JSONObject spuFullInfoGet(String accessToken, int isOnsale, int pageNo, int pageSize, boolean includeDescription) throws Exception;

    String getAccessTokenByCode(String client_id,String client_secret) throws Exception;

    String refreshAccessToken(String client_id,String client_secret,String refresh_token) throws Exception;

    String generateToken(String client_id,String client_secret) throws Exception;
}
