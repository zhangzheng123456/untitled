package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.VipRecordTypeMapper;
import com.bizvane.ishop.entity.VipRecord;
import com.bizvane.ishop.entity.VipRecordType;
import com.bizvane.ishop.service.VipRecordTypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by nanji on 2016/7/7.
 */
@Service
public class VipRecordTypeServiceImpl implements VipRecordTypeService {

    @Autowired
    private VipRecordTypeMapper vipRecordTypeMapper;

    @Override
    public VipRecordType getVipRecordTypeById(int id) throws SQLException {
        return vipRecordTypeMapper.selectByPrimaryKey(id);
    }

    @Override
    public VipRecordType getVipRecordTypeByName(String corp_code, String type_name) throws SQLException {
        return vipRecordTypeMapper.selectCode(corp_code, type_name);
    }

    @Override
    public int insert(VipRecordType vipRecordType) throws SQLException {
        return vipRecordTypeMapper.insert(vipRecordType);
    }

    @Override
    public int update(VipRecordType vipRecordType) throws SQLException {
        return vipRecordTypeMapper.updateByPrimaryKey(vipRecordType);
    }

    @Override
    public PageInfo<VipRecordType> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        PageInfo<VipRecordType> pageInfo = null;
        PageHelper.startPage(page_number, page_size);
        List<VipRecordType> list = this.vipRecordTypeMapper.selectAllVipRecordType(corp_code, search_value);
        for (VipRecordType vipRecordType:list) {
            if (vipRecordType.getIsactive().equals("Y")) {
                vipRecordType.setIsactive("是");
            } else {
                vipRecordType.setIsactive("否");
            }
        }
        pageInfo = new PageInfo<VipRecordType>(list);
        return pageInfo;
    }

//    @Override
//    public String VipRecordTypeCodeExist(String type_code, String corp_code) {
//        return this;
//    }

    @Override
    public String VipRecordTypeNameExist(String type_name, String corp_code) {
        return null;
    }

    @Override
    public int deleteById(int id) {
        return this.vipRecordTypeMapper.deleteByPrimaryKey(id);
    }

//    @Override
//    public List<VipRecordType> getAllVipRecordType(String corp_code, String search_value) {
//        return null;
//    }
}
