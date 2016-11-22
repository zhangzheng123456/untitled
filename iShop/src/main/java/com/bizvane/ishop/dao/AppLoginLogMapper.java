package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.AppLoginLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/8/23.
 */
public interface AppLoginLogMapper {
    //查询全部
    List<AppLoginLog> selectAllAppLoginLog(@Param("corp_code")String corp_code, @Param("search_value")String search_value);
    //筛选
    List<AppLoginLog> selectAllScreen(Map<String,Object> params);
    //删除
    int delAppLoginlogById(int id);

    AppLoginLog selByLogId(int id);
}
