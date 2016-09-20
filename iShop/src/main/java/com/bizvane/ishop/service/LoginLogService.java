package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.LoginLog;
import com.bizvane.ishop.entity.ValidateCode;

/**
 * Created by Administrator on 2016/5/23.
 */
public interface LoginLogService {
    int insertLoginLog(LoginLog log)throws Exception;

    LoginLog selectLoginLog(int log_id, String phone)throws Exception;

    int updateLoginLog(LoginLog log)throws Exception;

    int deleteLoginLog(int log_id)throws Exception;

}
