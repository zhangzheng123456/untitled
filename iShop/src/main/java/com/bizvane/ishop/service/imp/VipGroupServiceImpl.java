package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipGroupMapper;
import com.bizvane.ishop.entity.VipGroup;
import com.bizvane.ishop.service.VipGroupService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by nanji on 2016/8/31.
 */
@Service
public class VipGroupServiceImpl implements VipGroupService{
    @Autowired
    VipGroupMapper vipGroupMapper;

    /**
     * 根据id
     * 获取会员分组信息
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
        PageInfo<VipGroup> page = new PageInfo<VipGroup>(vipGroups);
        return page;
    }

    @Override
    public List<VipGroup> getAllVipGroup(String corp_code) throws Exception {
        List<VipGroup> vipGroups;
        vipGroups = vipGroupMapper.selectVipGroups(corp_code);
        return vipGroups;
    }
    public List<VipGroup> selectVipGroupByCorp(String corp_code, String id) throws Exception {
        return vipGroupMapper.selectVipGroupByCorp(corp_code,id);
    }
    @Override
    public String insert(String message, String user_id) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        String vip_group_code = jsonObject.get("vip_group_code").toString();
        String vip_group_name = jsonObject.get("vip_group_name").toString();
        String vipGroup_id = jsonObject.get("id").toString();
        String remark = jsonObject.get("remark").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        List<VipGroup> vipGroups = selectVipGroupByCorp(corp_code,vipGroup_id);
        if (vipGroups.size() >0) {
            result = "该会员分组已存在";
        }else {
            VipGroup vipGroup = new VipGroup();
            Date now = new Date();

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
        int id=Integer.parseInt(vipGroup_id);
        String vip_group_code = jsonObject.get("vip_group_code").toString();
        String vip_group_name = jsonObject.get("vip_group_name").toString();
        String remark = jsonObject.get("remark").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        List<VipGroup> vipGroups = selectVipGroupByCorp(corp_code,vipGroup_id);

        if (vipGroups.size() == 0 || vipGroups.get(0).getId() == id) {
            VipGroup vipGroup = new VipGroup();
            Date now = new Date();
            vipGroup.setId(id);
            vipGroup.setRemark(remark);
            vipGroup.setVip_group_code(vip_group_code);
            vipGroup.setVip_group_name(vip_group_name);
            vipGroup.setCorp_code(corp_code);
            vipGroup.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipGroup.setCreater(user_id);
            vipGroup.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipGroup.setModifier(user_id);
            vipGroup.setIsactive(jsonObject.get("isactive").toString());
            vipGroupMapper.updateVipGroup(vipGroup);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else {
            result = "该会员分组已存在";
        }
        return result;
    }

    @Override
    @Transactional
    public int delete(int id) throws Exception {
        return vipGroupMapper.deleteVipGroupById(id);
    }
    @Override
   public  VipGroup   getVipGroupByName(String corp_code, String name,String isactive) throws Exception{
        VipGroup vipGroup = this.vipGroupMapper.selectByVipGroupName(corp_code,name,isactive);
        return vipGroup;
    }

}
