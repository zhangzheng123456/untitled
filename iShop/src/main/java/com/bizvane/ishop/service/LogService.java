package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.LogInfo;

/**
 * Created by Administrator on 2016/5/23.
 */
public interface LogService {
    int insertLoginLog(LogInfo log);

    LogInfo selectLog(int log_id,String phone);

    int updateLoginLog(LogInfo log);
}
