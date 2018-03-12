package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by ZhouZhou on 2017/8/1.
 */
public interface CorpAuthService {

    void getAccessToken(String code) throws Exception;

    JSONObject JDApi(String method, String u_id, String param) throws Exception;
}
