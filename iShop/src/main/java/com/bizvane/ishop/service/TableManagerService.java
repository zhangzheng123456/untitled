package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.TableManager;

import java.util.List;

/**
 * Created by yin on 2016/6/30.
 */
public interface TableManagerService {
    List<TableManager> selAllByCode(String function_code)throws Exception;

    List<TableManager> selByCode(String function_code)throws Exception;
}
