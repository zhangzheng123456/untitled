package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.VipRecordMapper;
import com.bizvane.ishop.entity.VipRecord;
import com.bizvane.ishop.service.VipRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
@Service
public class VipRecordServiceImpl implements VipRecordService {

    @Autowired
    private VipRecordMapper VipRecordMapper;


    @Override
    public VipRecord getVipRecord(int id) throws SQLException {
        return this.VipRecordMapper.selecctById(id);
    }

    @Override
    public int insert(VipRecord VipRecord) throws SQLException {
        return this.VipRecordMapper.insert(VipRecord);
    }

    @Override
    public int update(VipRecord VipRecord) throws SQLException {
        return this.VipRecordMapper.updateByPrimaryKey(VipRecord);
    }

    @Override
    public int delete(int id) throws SQLException {
        return this.VipRecordMapper.deleteByPrimary(id);
    }


    @Override
    public PageInfo<VipRecord> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        List<VipRecord> list = null;
        PageHelper.startPage(page_number, page_size);
        if (search_value == null || search_value.isEmpty()) {
            list = this.VipRecordMapper.selectAllVipRecordInfo(corp_code, "");
        } else {
            list = this.VipRecordMapper.selectAllVipRecordInfo(corp_code, search_value);
        }
        PageInfo<VipRecord> page = new PageInfo<VipRecord>(list);
        return page;
    }
}
