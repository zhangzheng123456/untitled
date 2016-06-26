package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.LoginLog;
import com.bizvane.ishop.entity.VIPInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LoginLogMapper {
    int deleteByLogId(int id);

    int insertLoginLog(LoginLog record);

    LoginLog selectByLogId(@Param("log_id") int log_id, @Param("phone") String phone);

    int updateByLogId(LoginLog record);

}