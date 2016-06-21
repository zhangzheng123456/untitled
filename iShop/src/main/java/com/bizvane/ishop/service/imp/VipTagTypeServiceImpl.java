package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipTagTypeMapper;
import com.bizvane.ishop.entity.VipTagType;
import com.bizvane.ishop.service.VipTagTypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/21.
 *
 * @@version
 */
@Service
public class VipTagTypeServiceImpl implements VipTagTypeService {


    @Autowired
    private VipTagTypeMapper vipTagTypeMapper;

    @Override
    public VipTagType getVipTagTypeById(int id) throws SQLException {
        return vipTagTypeMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(VipTagType vipTagType) throws SQLException {
        return this.vipTagTypeMapper.insert(vipTagType);
    }

    @Override
    public int update(VipTagType vipTagType) throws SQLException {
        return this.vipTagTypeMapper.updateByPrimaryKey(vipTagType);
    }

    @Override
    public PageInfo<VipTagType> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        PageInfo<VipTagType> pageInfo = null;
        PageHelper.startPage(page_number, page_size);
        List<VipTagType> list = this.vipTagTypeMapper.selectAllVipTagType(corp_code, search_value);
        pageInfo = new PageInfo<VipTagType>(list);
        return pageInfo;
    }

    @Override
    public String vipTagTypeCodeExist(String corp_code, String type_code) {
        VipTagType vipTagType = this.vipTagTypeMapper.selectCode(corp_code, type_code);
        String result = Common.DATABEAN_CODE_ERROR;
        if (vipTagType == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }

    @Override
    public String vipTagTypeNameExist(String corp_code, String type_name) {
        VipTagType vipTagType = this.vipTagTypeMapper.selectCode(corp_code, type_name);
        String result = Common.DATABEAN_CODE_ERROR;
        if (vipTagType == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }
}
