package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.VipTagTypeMapper;
import com.bizvane.ishop.entity.VipTagType;
import com.bizvane.ishop.service.VipTagTypeService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

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
        return null;
    }

    @Override
    public int insert(VipTagType vipTagType) throws SQLException {
        return 0;
    }

    @Override
    public int update(VipTagType vipTagType) throws SQLException {
        return 0;
    }

    @Override
    public PageInfo<VipTagType> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        return null;
    }

    @Override
    public String vipTagTypeExist(String type_code, String corp_code) {
        return null;
    }

    @Override
    public String vipTagNameExist(String type_name, String corp_code) {
        return null;
    }
}
