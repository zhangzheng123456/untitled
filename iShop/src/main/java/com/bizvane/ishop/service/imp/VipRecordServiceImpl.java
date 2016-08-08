package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.VipRecordMapper;
import com.bizvane.ishop.entity.VipLabel;
import com.bizvane.ishop.entity.VipRecord;
import com.bizvane.ishop.service.VipRecordService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public PageInfo<VipRecord> selectAllVipRecordScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        params.put("corp_code",corp_code);
        List<VipRecord> records;
        PageHelper.startPage(page_number, page_size);
        records = this.VipRecordMapper.selectAllVipRecordScreen(params);
        for (VipRecord vipRecord:records) {
            if (vipRecord.getIsactive().equals("Y")) {
                vipRecord.setIsactive("是");
            } else {
                vipRecord.setIsactive("否");
            }
        }
        PageInfo<VipRecord> page = new PageInfo<VipRecord>(records);
        return page;
    }

    @Override
    public VipRecord getVipRecord(int id) throws Exception {
        return this.VipRecordMapper.selecctById(id);
    }

    @Override
    public int insert(VipRecord VipRecord) throws Exception {
        return this.VipRecordMapper.insert(VipRecord);
    }

    @Override
    public int update(VipRecord VipRecord) throws Exception {
        return this.VipRecordMapper.updateByPrimaryKey(VipRecord);
    }

    @Override
    public int delete(int id) throws Exception {
        return this.VipRecordMapper.deleteByPrimary(id);
    }


    @Override
    public PageInfo<VipRecord> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws Exception{
        PageHelper.startPage(page_number, page_size);
        List<VipRecord> list = this.VipRecordMapper.selectAllVipRecordInfo(corp_code, search_value);
        for (VipRecord vipRecord:list) {
            if (vipRecord.getIsactive().equals("Y")) {
                vipRecord.setIsactive("是");
            } else {
                vipRecord.setIsactive("否");
            }
        }
        PageInfo<VipRecord> page = new PageInfo<VipRecord>(list);
        return page;
    }
}
