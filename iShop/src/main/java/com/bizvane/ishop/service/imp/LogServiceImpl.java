package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.LogInfoMapper;
import com.bizvane.ishop.entity.LogInfo;
import com.bizvane.ishop.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2016/5/23.
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogInfoMapper logInfoMapper;

    public int insertLoginLog(LogInfo log) {
        return logInfoMapper.insertLoginLog(log);
    }

    public LogInfo selectLog(int log_id, String phone) {
        return logInfoMapper.selectByLogId(log_id, phone);
    }

    public int updateLoginLog(LogInfo log) {
        return logInfoMapper.updateByLogId(log);
    }
}
