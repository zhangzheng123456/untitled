package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.VipCallbackRecordMapper;
import com.bizvane.ishop.entity.VipCallbackRecord;
import com.bizvane.ishop.service.VipCallbackRecordService;
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
public class VipCallbackRecordServiceImpl implements VipCallbackRecordService {

    @Autowired
    private VipCallbackRecordMapper vipCallbackRecordMapper;


    @Override
    public VipCallbackRecord getVipCallbackRecord(int id) throws SQLException {
        return this.vipCallbackRecordMapper.selecctById(id);
    }

    @Override
    public int insert(VipCallbackRecord vipCallbackRecord) throws SQLException {
        return this.vipCallbackRecordMapper.insert(vipCallbackRecord);
    }

    @Override
    public int update(VipCallbackRecord vipCallbackRecord) throws SQLException {
        return this.vipCallbackRecordMapper.updateByPrimaryKey(vipCallbackRecord);
    }

    @Override
    public int delete(int id) throws SQLException {
        return this.vipCallbackRecordMapper.deleteByPrimary(id);
    }


    @Override
    public PageInfo<VipCallbackRecord> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        List<VipCallbackRecord> list = null;
        if (search_value == null || search_value.isEmpty()) {
            PageHelper.startPage(page_number, page_size);
            list = this.vipCallbackRecordMapper.selectAllVipCallBackRecordInfo(corp_code, "");
        } else {
            PageHelper.startPage(page_number, page_size);
            list = this.vipCallbackRecordMapper.selectAllVipCallBackRecordInfo(corp_code, "%" + search_value + "%");
        }
        PageInfo<VipCallbackRecord> page = new PageInfo<VipCallbackRecord>(list);
        return page;
    }
}
