package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VIPMapper;
import com.bizvane.ishop.entity.VIPInfo;
import com.bizvane.ishop.service.VipService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/12.
 *
 * @@version
 */
@Service
public class VipServiceImpl implements VipService {

    @Autowired
    private VIPMapper vipMapper;

    @Override
    public VIPInfo getVipInfoById(int id) throws SQLException {
        return vipMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(VIPInfo vipInfo) throws SQLException {
        return vipMapper.insert(vipInfo);
    }

    @Override
    public int update(VIPInfo vipInfo) throws SQLException {
        return vipMapper.updateByPrimaryKey(vipInfo);
    }

    @Override
    public int delete(int id) throws SQLException {
        return vipMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<VIPInfo> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        List<VIPInfo> vipInfos;
        PageHelper.startPage(page_number, page_size);
        vipInfos = vipMapper.selectAllVipInfo(corp_code, search_value);

        PageInfo<VIPInfo> page = new PageInfo<VIPInfo>(vipInfos);
        return page;
    }

    @Override
    public String vipCodeExist(String vip_code, String corp_code) throws SQLException {
        VIPInfo vipInfo = vipMapper.selectVipCode(vip_code, corp_code);
        String result = Common.DATABEAN_CODE_ERROR;
        if (vipInfo == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }
}
