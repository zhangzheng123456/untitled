package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.Function;

import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public interface FunctionService {
    JSONArray selectAllActions(int user_id,String role_code);

    JSONArray selectAllFunctions(int user_id, String role_code);
}
