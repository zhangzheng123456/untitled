package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.LoginLogMapper;
import com.bizvane.ishop.entity.LoginLog;
import com.bizvane.ishop.service.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2016/5/23.
 */
@Service
public class LoginLogServiceImpl implements LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

    public int insertLoginLog(LoginLog log){
        return loginLogMapper.insertLoginLog(log);
    }

    public LoginLog selectLoginLog(int log_id, String phone){
        return loginLogMapper.selectByLogId(log_id,phone);
    }

    public int updateLoginLog(LoginLog log){
        return loginLogMapper.updateByLogId(log);
    }

    public int deleteLoginLog(int log_id){
        return loginLogMapper.deleteByLogId(log_id);
    }
}
