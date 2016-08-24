package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.AppLoginLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yin on 2016/8/23.
 */
public interface AppLoginLogMapper {
    List<AppLoginLog> selectAllAppLoginLog(@Param("corp_code")String corp_code, @Param("search_value")String search_value);

}
