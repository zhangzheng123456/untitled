package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipLabelMapper;
import com.bizvane.ishop.dao.ViplableGroupMapper;
import com.bizvane.ishop.entity.AppLoginLog;
import com.bizvane.ishop.entity.ViplableGroup;
import com.bizvane.ishop.service.ViplableGroupService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.omg.CORBA.COMM_FAILURE;
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
    @Autowired
    private VipLabelMapper vipLabelMapper;
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
    public String addViplableGroup(ViplableGroup viplableGroup) throws SQLException {
        List<ViplableGroup> viplableGroups1= checkCodeOnly(viplableGroup.getCorp_code().trim(), viplableGroup.getLabel_group_code().trim(), Common.IS_ACTIVE_Y);
        List<ViplableGroup> viplableGroups2 = checkNameOnly(viplableGroup.getCorp_code().trim(), viplableGroup.getLabel_group_name().trim(), Common.IS_ACTIVE_Y);
        String result=Common.DATABEAN_CODE_ERROR;
        if(viplableGroups1.size()==0 && viplableGroups2.size()==0){
            viplableGroupMapper.addViplableGroup(viplableGroup);
            result=Common.DATABEAN_CODE_SUCCESS;
        }else if(viplableGroups1.size()>0){
            result="会员标签分组编号已存在";
        }else if(viplableGroups2.size()>0){
            result="会员标签分组名称已存在";
        }
        return result;
    }

    @Override
    public String updViplableGroupById(ViplableGroup viplableGroup) throws SQLException {
        List<ViplableGroup> viplableGroups1= checkCodeOnly(viplableGroup.getCorp_code().trim(), viplableGroup.getLabel_group_code().trim(), Common.IS_ACTIVE_Y);
        List<ViplableGroup> viplableGroups2 = checkNameOnly(viplableGroup.getCorp_code().trim(), viplableGroup.getLabel_group_name().trim(), Common.IS_ACTIVE_Y);
        String result=Common.DATABEAN_CODE_ERROR;
        ViplableGroup viplableGroup1 = selectViplableGroupById(viplableGroup.getId());
        if((viplableGroups1.size()==0||viplableGroup1.getLabel_group_code().trim().equals(viplableGroup.getLabel_group_code().trim()))
                && (viplableGroups2.size()==0||viplableGroup1.getLabel_group_name().trim().equals(viplableGroup.getLabel_group_name().trim()))){
            vipLabelMapper.updViplableBycode(viplableGroup.getLabel_group_code().trim(),viplableGroup.getCorp_code().trim(),viplableGroup1.getLabel_group_code().trim());
            viplableGroupMapper.updViplableGroupById(viplableGroup);
            result=Common.DATABEAN_CODE_SUCCESS;
        }else if(viplableGroups1.size()>0){
            result="会员标签分组编号已存在";
        }else if(viplableGroups2.size()>0){
            result="会员标签分组名称已存在";
        }
        return result;
    }

    @Override
    public ViplableGroup selectViplableGroupById(int id) throws SQLException {
        return viplableGroupMapper.selectViplableGroupById(id);
    }

    @Override
    public List<ViplableGroup> checkCodeOnly(String corp_code, String label_group_code, String isactive) throws SQLException {
        return viplableGroupMapper.checkCodeOnly(corp_code,label_group_code,isactive);
    }

    @Override
    public List<ViplableGroup> checkNameOnly(String corp_code, String label_group_name, String isactive) throws SQLException {
        return viplableGroupMapper.checkNameOnly(corp_code,label_group_name,isactive);
    }

    @Override
    public List<ViplableGroup> selectViplabGroupList(String corp_code) throws SQLException {
        return viplableGroupMapper.selectViplabGroupList(corp_code);
    }
}
