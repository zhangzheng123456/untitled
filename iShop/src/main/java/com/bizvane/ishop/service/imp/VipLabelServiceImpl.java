package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipLabelMapper;
import com.bizvane.ishop.entity.RelViplabel;
import com.bizvane.ishop.entity.ValidateCode;
import com.bizvane.ishop.entity.VipLabel;
import com.bizvane.ishop.entity.ViplableGroup;
import com.bizvane.ishop.service.VipLabelService;
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
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
@Service
public class VipLabelServiceImpl implements VipLabelService {

    @Autowired
    private VipLabelMapper vipLabelMapper;
    @Autowired
    private ViplableGroupService viplableGroupService;
    @Override
    public VipLabel getVipLabelById(int id) throws Exception {
        VipLabel vipLabel = vipLabelMapper.selectByPrimaryKey(id);
        String lable_g_Code="";
        if(vipLabel.getLabel_group_code()==null){
            lable_g_Code="";
        }else{
            lable_g_Code=vipLabel.getLabel_group_code();
        }
        List<ViplableGroup> viplableGroups = viplableGroupService.checkCodeOnly(vipLabel.getCorp_code(), lable_g_Code, Common.IS_ACTIVE_Y);
        if(viplableGroups.size()>0) {
            ViplableGroup viplableGroup = viplableGroups.get(0);
            vipLabel.setLabel_group_code(viplableGroup.getLabel_group_code());
            vipLabel.setLabel_group_name(viplableGroup.getLabel_group_name());
        }else {
            vipLabel.setLabel_group_code("");
            vipLabel.setLabel_group_name("");
        }
        return vipLabel;
    }

    @Override
    public String insert(VipLabel vipLabel) throws Exception {
        if (this.VipLabelNameExist(vipLabel.getCorp_code(), vipLabel.getLabel_name()).equals(Common.DATABEAN_CODE_ERROR)) {
            return "名称已经存在";
        } else if (vipLabelMapper.insert(vipLabel) >= 0) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public int delete(int id) throws Exception {
        return vipLabelMapper.deleteByPrimaryKey(id);
    }

    @Override
    public String update(VipLabel vipLabel) throws Exception {
        VipLabel old = this.vipLabelMapper.selectByPrimaryKey(vipLabel.getId());
        if (old.getCorp_code().equals(vipLabel.getCorp_code())) {
            if (!old.getLabel_name().equals(vipLabel.getLabel_name()) && (this.VipLabelNameExist(vipLabel.getCorp_code(), vipLabel.getLabel_name()).equals(Common.DATABEAN_CODE_ERROR))) {
                return "名称已经存在";
            } else if (this.vipLabelMapper.updateByPrimaryKey(vipLabel) >= 0) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        } else {
            if (this.VipLabelNameExist(vipLabel.getCorp_code(), vipLabel.getLabel_name()).equals(Common.DATABEAN_CODE_ERROR)) {
                return "名称已经存在";
            } else if (this.vipLabelMapper.updateByPrimaryKey(vipLabel) >= 0) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public PageInfo<VipLabel> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws Exception{
        List<VipLabel> list = null;
        PageHelper.startPage(page_number, page_size);
        list = vipLabelMapper.selectAllVipLabel(corp_code, search_value);
        for (VipLabel vipLabel:list) {
            vipLabel.setIsactive(CheckUtils.CheckIsactive(vipLabel.getIsactive()));
            VipLabel vipLabel1 = countLable(vipLabel.getCorp_code(), vipLabel.getId() + "");
            if(vipLabel1==null) {
                vipLabel.setCountlable("0");
            }else{
                vipLabel.setCountlable(vipLabel1.getCountlable());
            }
            String lable_g_Code="";
            if(vipLabel.getLabel_group_code()==null){
                lable_g_Code="";
            }else{
                lable_g_Code=vipLabel.getLabel_group_code();
            }
            List<ViplableGroup> viplableGroups = viplableGroupService.checkCodeOnly(vipLabel.getCorp_code(), lable_g_Code, Common.IS_ACTIVE_Y);
            if(viplableGroups.size()>0) {
                ViplableGroup viplableGroup = viplableGroups.get(0);
                vipLabel.setLabel_group_code(viplableGroup.getLabel_group_code());
                vipLabel.setLabel_group_name(viplableGroup.getLabel_group_name());
            }else {
                vipLabel.setLabel_group_code("");
                vipLabel.setLabel_group_name("");
            }
            if(vipLabel.getLabel_type()==null||vipLabel.getLabel_type().equals("")){
                vipLabel.setLabel_type("");
            }else if(vipLabel.getLabel_type().equals("user")){
                vipLabel.setLabel_type("用户");
            }else if(vipLabel.getLabel_type().equals("sys")){
                vipLabel.setLabel_type("系统");
            }else if(vipLabel.getLabel_type().equals("org")){
                vipLabel.setLabel_type("企业");
            }
        }
        PageInfo<VipLabel> page = new PageInfo<VipLabel>(list);
        return page;
    }

    @Override
    public PageInfo<VipLabel> selectAllVipScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        params.put("corp_code", corp_code);
        List<VipLabel> labels;
        PageHelper.startPage(page_number, page_size);
        labels = vipLabelMapper.selectAllViplabelScreen(params);
        for (VipLabel vipLabel:labels) {
            vipLabel.setIsactive(CheckUtils.CheckIsactive(vipLabel.getIsactive()));
            VipLabel vipLabel1 = countLable(vipLabel.getCorp_code(), vipLabel.getId() + "");
            if(vipLabel1==null) {
                vipLabel.setCountlable("0");
            }else{
                vipLabel.setCountlable(vipLabel1.getCountlable());
            }
            String lable_g_Code="";
            if(vipLabel.getLabel_group_code()==null){
                lable_g_Code="";
            }else{
                lable_g_Code=vipLabel.getLabel_group_code();
            }
            List<ViplableGroup> viplableGroups = viplableGroupService.checkCodeOnly(vipLabel.getCorp_code(), lable_g_Code, Common.IS_ACTIVE_Y);
            if(viplableGroups.size()>0) {
                ViplableGroup viplableGroup = viplableGroups.get(0);
                vipLabel.setLabel_group_code(viplableGroup.getLabel_group_code());
                vipLabel.setLabel_group_name(viplableGroup.getLabel_group_name());
            }else {
                vipLabel.setLabel_group_code("");
                vipLabel.setLabel_group_name("");
            }
            if(vipLabel.getLabel_type()==null||vipLabel.getLabel_type().equals("")){
                vipLabel.setLabel_type("");
            }else if(vipLabel.getLabel_type().equals("user")){
                vipLabel.setLabel_type("用户");
            }else if(vipLabel.getLabel_type().equals("sys")){
                vipLabel.setLabel_type("系统");
            }else if(vipLabel.getLabel_type().equals("org")){
                vipLabel.setLabel_type("企业");
            }
        }
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
    public List<VipLabel> VipLabelNameExist(String corp_code, String tag_name) throws Exception {
         return vipLabelMapper.selectVipLabelName(corp_code, tag_name);

    }

    @Override
    public VipLabel countLable(String corp_code, String label_id)throws Exception {
        return vipLabelMapper.countLable(corp_code,label_id);
    }

    @Override
    public List<VipLabel> lableList(String corp_code, String label_group_code)throws Exception  {
        return vipLabelMapper.lableList(corp_code,label_group_code);
    }

    @Override
    public List<VipLabel> selectLabelByVip(String corp_code, String vip_code) throws Exception {
        return vipLabelMapper.selectLabelByVip(corp_code,vip_code);
    }

    @Override
    public List<VipLabel> findHotViplabel(String corp_code)throws Exception {
        return vipLabelMapper.findHotViplabel(corp_code);
    }

    @Override
    public PageInfo<VipLabel> findViplabelByType(int page_number, int page_size,String corp_code, String label_type, String search_value)throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<VipLabel> viplabels = vipLabelMapper.findViplabelByType(corp_code,label_type,search_value);
        PageInfo<VipLabel> page = new PageInfo<VipLabel>(viplabels);
        return page;
    }

    @Override
    public List<RelViplabel> checkRelViplablel(String corp_code, String vip_code, String label_id) throws Exception {
        return vipLabelMapper.checkRelViplablel(corp_code,vip_code,label_id);
    }

    @Override
    public int delRelViplabel(String rid) throws Exception {
        return vipLabelMapper.delRelViplabel(rid);
    }

    @Override
    public int addRelViplabel(RelViplabel relViplabel) throws Exception {
        return vipLabelMapper.addRelViplabel(relViplabel);
    }

    @Override
    public List<VipLabel> findViplabelID(String corp_code, String label_name) throws Exception {
        return vipLabelMapper.findViplabelID(corp_code,label_name);
    }
}
