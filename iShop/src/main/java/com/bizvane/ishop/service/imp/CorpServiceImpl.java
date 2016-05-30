package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.CorpMapper;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.service.CorpService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    private CorpMapper corpMapper;

    public Corp selectByCorpId(int corp_id, String corp_code) throws SQLException {
        return corpMapper.selectByCorpId(corp_id, corp_code);
    }

    public int insertCorp(Corp record) throws SQLException {
        return corpMapper.insertCorp(record);
    }

    public int updateByCorpId(Corp record) throws SQLException {
        return corpMapper.updateByCorpId(record);
    }

    public int deleteByCorpId(int id) throws SQLException {
        return corpMapper.deleteByCorpId(id);
    }

    public PageInfo<Corp> selectAllCorp(int page_number, int page_size, String search_value) throws SQLException {
        PageHelper.startPage(page_number, page_size);
        List<Corp> corps = corpMapper.selectAllCorp("%" + search_value + "%");
        PageInfo<Corp>  page = new PageInfo<Corp>(corps);
        return page;
    }

    public String selectMaxCorpCode() {
        return corpMapper.selectMaxCorpCode();
    }
}
