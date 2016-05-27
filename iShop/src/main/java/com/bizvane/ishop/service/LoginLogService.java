package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.LoginLog;
import com.bizvane.ishop.entity.ValidateCode;

/**
 * Created by Administrator on 2016/5/23.
 */
public interface LoginLogService {
    int insertLoginLog(LoginLog log);

    LoginLog selectLoginLog(int log_id, String phone);

    int updateLoginLog(LoginLog log);

    int deleteLoginLog(int log_id);

}
