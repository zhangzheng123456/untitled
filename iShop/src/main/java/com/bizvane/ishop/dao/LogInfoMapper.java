package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.LogInfo;
import org.apache.ibatis.annotations.Param;

public interface LogInfoMapper {
    int deleteByLogId(int id);

    int insertLoginLog(LogInfo record);

    LogInfo selectByLogId(@Param("log_id")int log_id,@Param("phone") String phone);

    int updateByLogId(LogInfo record);

}