package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Task;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yin on 2016/7/21.
 */
public interface TaskMapper {
        List<Task> selectAllTask(@Param("corp_code")String corp_code,@Param("role_ident")String role_ident,@Param("user_code")String user_code,@Param("search_value")String search_value);



}
