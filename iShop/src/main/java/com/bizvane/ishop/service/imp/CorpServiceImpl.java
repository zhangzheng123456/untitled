package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.CorpInfoMapper;
import com.bizvane.ishop.entity.CorpInfo;
import com.bizvane.ishop.service.CorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
@Service
public class CorpServiceImpl implements CorpService {
    @Autowired
    private CorpInfoMapper corpInfoMapper;

    public CorpInfo selectByCorpId(int corp_id, String corp_code) throws SQLException {
        return corpInfoMapper.selectByCorpId(corp_id, corp_code);
    }

    public int insertCorp(CorpInfo record) throws SQLException {
        return corpInfoMapper.insertCorp(record);
    }

    public int updateByCorpId(CorpInfo record) throws SQLException {
        return corpInfoMapper.updateByCorpId(record);
    }

    public int deleteByCorpId(int id) throws SQLException {
        return corpInfoMapper.deleteByCorpId(id);
    }

    public List<CorpInfo> selectAllCorp(String search_value) throws SQLException {
        return corpInfoMapper.selectAllCorp("%" + search_value + "%");
    }

    public String selectMaxCorpCode() {
        return corpInfoMapper.selectMaxCorpCOde();
    }
}
