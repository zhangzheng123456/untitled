package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipLabelMapper;
import com.bizvane.ishop.entity.VipLabel;
import com.bizvane.ishop.service.VipLabelService;
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
public class VipLabelServiceImpl implements VipLabelService {

    @Autowired
    private VipLabelMapper vipLabelMapper;

    @Override
    public VipLabel getVipLabelById(int id) throws SQLException {
        return this.vipLabelMapper.selectByPrimaryKey(id);
    }

    @Override
    public String insert(VipLabel vipLabel) throws SQLException {
        if (this.VipLabelNameExist(vipLabel.getCorp_code(), vipLabel.getLabel_name()).equals(Common.DATABEAN_CODE_ERROR)) {
            return "名称已经存在！！！！";
        } else if (vipLabelMapper.insert(vipLabel) >= 0) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public int delete(int id) throws SQLException {
        return vipLabelMapper.deleteByPrimaryKey(id);
    }

    @Override
    public String update(VipLabel vipLabel) throws SQLException {
        VipLabel old = this.vipLabelMapper.selectByPrimaryKey(vipLabel.getId());
        if (old.getCorp_code().equals(vipLabel.getCorp_code())) {
            if (!old.getLabel_name().equals(vipLabel.getLabel_name()) && (this.VipLabelNameExist(vipLabel.getCorp_code(), vipLabel.getLabel_name()).equals(Common.DATABEAN_CODE_ERROR))) {
                return "名称已经存在！！！！";
            } else if (this.vipLabelMapper.updateByPrimaryKey(vipLabel) >= 0) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        } else {
            if (this.VipLabelNameExist(vipLabel.getCorp_code(), vipLabel.getLabel_name()).equals(Common.DATABEAN_CODE_ERROR)) {
                return "名称已经存在！！！！";
            } else if (this.vipLabelMapper.updateByPrimaryKey(vipLabel) >= 0) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public PageInfo<VipLabel> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        List<VipLabel> list = null;
        PageHelper.startPage(page_number, page_size);
        list = vipLabelMapper.selectAllVipLabel(corp_code, search_value);
        PageInfo<VipLabel> page = new PageInfo<VipLabel>(list);
        return page;
    }

    @Override
    public PageInfo<VipLabel> selectAllVipScreen(int page_number, int page_size, String corp_code, Map<String, String> map) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        params.put("corp_code", corp_code);
        List<VipLabel> labels;
        PageHelper.startPage(page_number, page_size);
        labels = vipLabelMapper.selectAllViplabelScreen(params);
        PageInfo<VipLabel> page = new PageInfo<VipLabel>(labels);
        return page;

    }
//
//    @Override
//    public String VipLabelCodeExist(String corp_code, String tag_code) throws SQLException {
//        vipLabel vipLabel = vipLabelMapper.selectVipLabelCode(tag_code, corp_code);
//        String result = Common.DATABEAN_CODE_ERROR;
//        if (vipLabel == null) {
//            result = Common.DATABEAN_CODE_SUCCESS;
//        }
//        return result;
//    }

    @Override
    public String VipLabelNameExist(String corp_code, String tag_name) throws SQLException {
        VipLabel VipLabel = vipLabelMapper.selectVipLabelName(corp_code, tag_name);
        String result = Common.DATABEAN_CODE_ERROR;
        if (VipLabel == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }
}
