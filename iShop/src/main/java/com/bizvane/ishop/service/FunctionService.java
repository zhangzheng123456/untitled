package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Function;

import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public interface FunctionService {
    String selectAllFunction(String user_id,String role_code);

    String selectAllFunctions(String user_id,String role_code);
}
