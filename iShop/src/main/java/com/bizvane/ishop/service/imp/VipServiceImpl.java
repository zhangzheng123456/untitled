package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VIPMapper;
import com.bizvane.ishop.entity.VIPInfo;
import com.bizvane.ishop.entity.VipRecordType;
import com.bizvane.ishop.service.VipService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
    public String update(VIPInfo vipInfo) throws SQLException {
        VIPInfo old = this.vipMapper.selectByPrimaryKey(vipInfo.getId());
        if (old.getCorp_code().equals(vipInfo.getCorp_code())) {
            if ((!old.getVip_code().equals(vipInfo.getVip_code()))
                    && (this.vipCodeExist(vipInfo.getCorp_code(), vipInfo.getVip_code()).equals(Common.DATABEAN_CODE_ERROR))) {
                return "编号已经存在";
            } else if (!old.getVip_name().equals(vipInfo.getVip_name()) && (this.vipNameExist(vipInfo.getCorp_code(), vipInfo.getVip_name()).equals(Common.DATABEAN_CODE_ERROR))) {
                return "名称已经存在";
            } else if (this.vipMapper.updateByPrimaryKey(vipInfo) >= 0) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        } else {
            if (this.vipCodeExist(vipInfo.getCorp_code(), vipInfo.getVip_code()).equals(Common.DATABEAN_CODE_ERROR)) {
                return "编号已经存在";
            } else if (this.vipNameExist(vipInfo.getCorp_code(), vipInfo.getVip_name()).equals(Common.DATABEAN_CODE_ERROR)) {
                return "名称已经存在";
            } else if (this.vipMapper.updateByPrimaryKey(vipInfo) >= 0) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        }
        return Common.DATABEAN_CODE_ERROR;
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
        for (VIPInfo vipInfo:vipInfos) {
            if (vipInfo.getIsactive().equals("Y")) {
                vipInfo.setIsactive("是");
            } else {
                vipInfo.setIsactive("否");
            }
            if(vipInfo.getSex()==null){
                vipInfo.setSex("男");
            }else if(vipInfo.getSex().equals("F")){
                vipInfo.setSex("女");
            }else{
                vipInfo.setSex("男");
            }
        }
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

    @Override
    public String vipNameExist(String vip_name, String corp_code) {
        //VIPInfo vipInfo = vipMapper.selectVipCode(vip_code, corp_code);
        VIPInfo vipInfo = vipMapper.selectVipName(vip_name, corp_code);
        String result = Common.DATABEAN_CODE_ERROR;
        if (vipInfo == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }
}
