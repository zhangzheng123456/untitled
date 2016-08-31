package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.ViplableGroupMapper;
import com.bizvane.ishop.entity.AppLoginLog;
import com.bizvane.ishop.entity.ViplableGroup;
import com.bizvane.ishop.service.ViplableGroupService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/8/31.
 */
@Service
public class ViplableGroupServiceImpl implements ViplableGroupService {
    @Autowired
    private ViplableGroupMapper viplableGroupMapper;
    @Override
    public PageInfo<ViplableGroup> selectViplabGroup(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<ViplableGroup> viplableGroups = viplableGroupMapper.selectViplabGroup(corp_code, search_value);
        for (ViplableGroup viplableGroup:viplableGroups) {
            viplableGroup.setIsactive(CheckUtils.CheckIsactive(viplableGroup.getIsactive()));
        }
        PageInfo<ViplableGroup> page = new PageInfo<ViplableGroup>(viplableGroups);
        return page;
    }

    @Override
    public PageInfo<ViplableGroup> selectViplabGroupScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<ViplableGroup> list = viplableGroupMapper.selectViplabGroupScreen(params);
        for (ViplableGroup viplableGroup:list) {
            viplableGroup.setIsactive(CheckUtils.CheckIsactive(viplableGroup.getIsactive()));
        }
        PageInfo<ViplableGroup> page = new PageInfo<ViplableGroup>(list);
        return page;
    }

    @Override
    public int delViplabGroupById(int id) throws Exception {
        return viplableGroupMapper.delViplabGroupById(id);
    }

    @Override
    public int addViplableGroup(ViplableGroup viplableGroup) throws SQLException {
        return viplableGroupMapper.addViplableGroup(viplableGroup);
    }

    @Override
    public int updViplableGroupById(ViplableGroup viplableGroup) throws SQLException {
        return viplableGroupMapper.updViplableGroupById(viplableGroup);
    }

    @Override
    public ViplableGroup selectViplableGroupById(int id) throws SQLException {
        return viplableGroupMapper.selectViplableGroupById(id);
    }
}
