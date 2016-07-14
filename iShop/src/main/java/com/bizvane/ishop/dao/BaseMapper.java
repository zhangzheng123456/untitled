package com.bizvane.ishop.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/7/14.
 */
public interface BaseMapper {
    //mybatis都会默认的先将查询回的值放入一个hashMap中（如果返回的值不止一条就是一个包含hashMap的list）
    List<HashMap<String,Object>> queryMetaList(Map<String, Object> params);
}
