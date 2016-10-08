package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipGroupMapper;
import com.bizvane.ishop.entity.VipGroup;
import com.bizvane.ishop.service.VipGroupService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/8/31.
 */
@Service
public class VipGroupServiceImpl implements VipGroupService {
    @Autowired
    VipGroupMapper vipGroupMapper;

    /**
     * 根据id
     * 获取会员分组信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public VipGroup getVipGroupById(int id) throws Exception {
        return vipGroupMapper.selectVipGroupById(id);
    }


    /**
     * 分页显示会员分组
     *
     * @param page_number
     * @param page_size
     * @param corp_code
     * @param search_value
     * @return
     * @throws Exception
     */
    @Override
    public PageInfo<VipGroup> getAllVipGroupByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<VipGroup> vipGroups;
        PageHelper.startPage(page_number, page_size);
        vipGroups = vipGroupMapper.selectAllVipGroup(corp_code, search_value);
        for (VipGroup vipGroup : vipGroups) {
            vipGroup.setIsactive(CheckUtils.CheckIsactive(vipGroup.getIsactive()));
        }
        PageInfo<VipGroup> page = new PageInfo<VipGroup>(vipGroups);
        return page;
    }

    @Override
    public List<VipGroup> selectCorpVipGroups(String corp_code) throws Exception {
        List<VipGroup> vipGroups;
        vipGroups = vipGroupMapper.selectCorpVipGroups(corp_code);

        return vipGroups;
    }


    @Override
    public String insert(String message, String user_id) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        String vip_group_code = jsonObject.get("vip_group_code").toString();
        String vip_group_name = jsonObject.get("vip_group_name").toString();
        String remark = jsonObject.get("remark").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        VipGroup vipGroup1 = getVipGroupByCode(corp_code, vip_group_code, Common.IS_ACTIVE_Y);
        VipGroup vipGroup2 = getVipGroupByName(corp_code, vip_group_name, Common.IS_ACTIVE_Y);

        if (vipGroup1 != null) {
            result = "该会员分组编号已存在";
        } else if (vipGroup2 != null) {
            result = "该会员分组名称已存在";
        } else {
            Date now = new Date();
            VipGroup vipGroup = new VipGroup();
            vipGroup.setRemark(remark);
            vipGroup.setVip_group_code(vip_group_code);
            vipGroup.setVip_group_name(vip_group_name);
            vipGroup.setCorp_code(corp_code);
            vipGroup.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipGroup.setCreater(user_id);
            vipGroup.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipGroup.setModifier(user_id);
            vipGroup.setIsactive(jsonObject.get("isactive").toString());
            vipGroupMapper.insertVipGroup(vipGroup);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;

    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String result = "";
        JSONObject jsonObject = new JSONObject(message);
        String vipGroup_id = jsonObject.get("id").toString();
        int id = Integer.parseInt(vipGroup_id);
        String vip_group_code = jsonObject.get("vip_group_code").toString();
        String vip_group_name = jsonObject.get("vip_group_name").toString();
        String remark = jsonObject.get("remark").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        VipGroup vipGroup1 = getVipGroupByCode(corp_code, vip_group_code, Common.IS_ACTIVE_Y);
        VipGroup vipGroup2 = getVipGroupByName(corp_code, vip_group_name, Common.IS_ACTIVE_Y);

        if (vipGroup1 != null && vipGroup1.getId() != id) {
            result = "该会员分组编号已存在";
        } else if (vipGroup2 != null && vipGroup2.getId() != id) {
            result = "该会员分组名称已存在";
        } else {
            VipGroup vipGroup = new VipGroup();
            Date now = new Date();
            vipGroup.setId(id);
            vipGroup.setRemark(remark);
            vipGroup.setVip_group_code(vip_group_code);
            vipGroup.setVip_group_name(vip_group_name);
            vipGroup.setCorp_code(corp_code);
            vipGroup.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipGroup.setModifier(user_id);
            vipGroup.setIsactive(jsonObject.get("isactive").toString());
            vipGroupMapper.updateVipGroup(vipGroup);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;

    }

    @Override
    @Transactional
    public int delete(int id) throws Exception {
        return vipGroupMapper.deleteVipGroupById(id);
    }

    @Override
    public VipGroup getVipGroupByCode(String corp_code, String code, String isactive) throws Exception {
        return vipGroupMapper.selectByVipGroupCode(corp_code, code, isactive);
    }

    @Override
    public VipGroup getVipGroupByName(String corp_code, String name, String isactive) throws Exception {
        VipGroup vipGroup = this.vipGroupMapper.selectByVipGroupName(corp_code, name, isactive);
        return vipGroup;
    }

    public PageInfo<VipGroup> getAllVipGrouScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<VipGroup> list1 = vipGroupMapper.selectAllVipGroupScreen(params);
        for (VipGroup vipGroup : list1) {
            vipGroup.setIsactive(CheckUtils.CheckIsactive(vipGroup.getIsactive()));
        }
        PageInfo<VipGroup> page = new PageInfo<VipGroup>(list1);
        return page;
    }


}
