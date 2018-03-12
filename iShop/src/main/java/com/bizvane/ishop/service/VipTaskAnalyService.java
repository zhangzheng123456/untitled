package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by gyy on 2017/11/23.
 */
public interface VipTaskAnalyService {
    JSONObject getShareAndRegistCount(String message) throws Exception;
}
