package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipCardTypeMapper;
import com.bizvane.ishop.dao.VipRulesMapper;
import com.bizvane.ishop.entity.VipCardType;
import com.bizvane.ishop.entity.VipRules;
import com.bizvane.ishop.service.VipCardTypeService;
import com.bizvane.ishop.service.VipRulesService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/12/29.
 */
@Service
public class VipCardTypeServiceImpl implements VipCardTypeService {
    @Autowired
    VipCardTypeMapper vipCardTypeMapper;
    @Autowired
    VipRulesService vipRulesService;
    @Autowired
    VipRulesMapper vipRulesMapper;

    @Override
    public VipCardType getVipCardTypeById(int id) throws Exception {
        return vipCardTypeMapper.selVipCardTypeById(id);
    }

    @Override
    public PageInfo<VipCardType> getAllVipCardTypeByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<VipCardType> vipRules;
        PageHelper.startPage(page_number, page_size);
        vipRules = vipCardTypeMapper.selectAllVipCardType(corp_code, search_value);
        for (VipCardType vipRules1 : vipRules) {
            vipRules1.setIsactive(CheckUtils.CheckIsactive(vipRules1.getIsactive()));

        }
        PageInfo<VipCardType> page = new PageInfo<VipCardType>(vipRules);

        return page;
    }

    @Override
    public String insert(String message, String user_id) throws Exception {
        String status = "";
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String isactive = jsonObject.get("isactive").toString().trim();

        VipCardType vipCardType = WebUtils.JSON2Bean(jsonObject, VipCardType.class);
        VipCardType code = getVipCardTypeByCode(vipCardType.getCorp_code(), vipCardType.getVip_card_type_code(), vipCardType.getIsactive());
        VipCardType name = getVipCardTypeByName(vipCardType.getCorp_code(), vipCardType.getVip_card_type_name(), vipCardType.getIsactive());
        List<VipCardType> list = getVipCardTypes(corp_code, Common.IS_ACTIVE_Y);

        if (list == null || code == null && name == null) {
            vipCardType.setCorp_code(corp_code);
            vipCardType.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipCardType.setCreater(user_id);
            vipCardType.setModifier(user_id);
            vipCardType.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipCardType.setIsactive(isactive);
            int num = 0;
            num = vipCardTypeMapper.insertVipCardType(vipCardType);
            if (num > 0) {
                VipCardType vipCardType1 = getVipCardTypeByCode(vipCardType.getCorp_code(), vipCardType.getVip_card_type_code(), vipCardType.getIsactive());
                status = vipCardType1.getId();
            } else {
                status = Common.DATABEAN_CODE_ERROR;
            }
        } else if (code != null) {
            status = "该编号已存在";
        } else {
            status = "该名称已存在";
        }
        return status;
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String status = "";
        JSONObject jsonObject=JSON.parseObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String vip_card_type_code = jsonObject.get("vip_card_type_code").toString().trim();
        String vip_card_type_name = jsonObject.get("vip_card_type_name").toString().trim();
        String degree = jsonObject.get("degree").toString().trim();
        String isactive = jsonObject.get("isactive").toString().trim();
        String id = jsonObject.get("id").toString().trim();
        VipCardType vipCardType = getVipCardTypeById(Integer.parseInt(id));
        String old_code = vipCardType.getVip_card_type_code();
        String old_corp = vipCardType.getCorp_code();
        VipCardType vipCardType1 = getVipCardTypeByCode(corp_code, vip_card_type_code, Common.IS_ACTIVE_Y);
        VipCardType vipCardType2 = getVipCardTypeByName(corp_code, vip_card_type_name, Common.IS_ACTIVE_Y);
        List<VipCardType> list = getVipCardTypes(corp_code, Common.IS_ACTIVE_Y);
        int degree1 = Integer.valueOf(degree);
        List<VipRules> list1 = vipRulesService.getViprulesByCardTypeCode(old_corp, old_code);

        if (list.size() > 0) {

            if (vipCardType1 != null && !id.equals(vipCardType1.getId())) {
                status = "该编号已存在";
            } else if (vipCardType2 != null && !id.equals(vipCardType2.getId())) {
                status = "该名称已存在";
            } else {

                vipCardType.setCorp_code(corp_code);
                vipCardType.setVip_card_type_code(vip_card_type_code);
                vipCardType.setVip_card_type_name(vip_card_type_name);
                vipCardType.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipCardType.setModifier(user_id);
                vipCardType.setDegree(degree);
                vipCardType.setIsactive(isactive);
                int m = 0;
                for (int i = 0; i < list1.size(); i++) {
                    VipRules vipRules = list1.get(i);
                    if (vipRules.getHigh_vip_card_type_code().equals(old_code)) {
                        vipRules.setHigh_vip_card_type_code(vip_card_type_code);
                        vipRules.setCorp_code(corp_code);
                        //更换企业的情况
//                        if (!corp_code.equals(old_corp)) {
//                            vipRules.setVip_card_type_code("");
//                            vipRules.setVip_type("");
//                        }
                        vipRules.setHigh_vip_type(vip_card_type_name);
                        if (degree1 > Integer.valueOf(vipRules.getDegree())) {
                            vipRules.setHigh_degree(degree);
                            vipRules.setModified_date(Common.DATETIME_FORMAT.format(now));
                            vipRules.setModifier(user_id);
                            m = vipRulesMapper.updateVipRules(vipRules);
                        } else {
                            status = "该会员卡类型在会员制度里已配置为高级会员，等级不能低于所选会员类型的等级";
                            return status;
                        }
                    } else if (vipRules.getVip_card_type_code().equals(old_code)) {
                        vipRules.setVip_type(vip_card_type_name);
                        vipRules.setVip_card_type_code(vip_card_type_code);
                        vipRules.setCorp_code(corp_code);
                        //更换企业的情况
//                        if (!corp_code.equals(old_corp)) {
//                            vipRules.setHigh_vip_card_type_code("");
//                            vipRules.setHigh_vip_type("");
//                        }
                        if (degree1 < Integer.valueOf(vipRules.getHigh_degree())) {
                            vipRules.setDegree(degree);
                            vipRules.setModified_date(Common.DATETIME_FORMAT.format(now));
                            vipRules.setModifier(user_id);
                            m = vipRulesMapper.updateVipRules(vipRules);
                        } else {
                            status = "该会员卡类型在会员制度里已配置，等级不能高于上级会员类型的等级";
                            return status;
                        }
                    }
                }
                int num=0;
                //编辑会员卡类型表成功, 同步更新会员制度里相应的会员卡类型信息
                num = vipCardTypeMapper.updateVipCardType(vipCardType);
                if (num > 0 && m >= 0) {
                    status = Common.DATABEAN_CODE_SUCCESS;
                } else {
                    status = Common.DATABEAN_CODE_ERROR;
                }
            }
        } else {
            int m = 0;
            if (vipCardType1 == null && vipCardType2 == null) {
                vipCardType.setCorp_code(corp_code);
                vipCardType.setVip_card_type_code(vip_card_type_code);
                vipCardType.setVip_card_type_name(vip_card_type_name);
                vipCardType.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipCardType.setModifier(user_id);
                vipCardType.setDegree(degree);
                vipCardType.setIsactive(isactive);
                for (int i = 0; i < list1.size(); i++) {
                    VipRules vipRules = list1.get(i);
                    if (vipRules.getHigh_vip_card_type_code().equals(old_code)) {
                        vipRules.setHigh_vip_card_type_code(vip_card_type_code);
                        vipRules.setCorp_code(corp_code);
                        //更换企业的情况
//                        if (!corp_code.equals(old_corp)) {
//                            vipRules.setVip_card_type_code("");
//                            vipRules.setVip_type("");
//                        }
                        vipRules.setHigh_vip_type(vip_card_type_name);
                        if (degree1 > Integer.valueOf(vipRules.getDegree())) {
                            vipRules.setHigh_degree(degree);
                            vipRules.setModified_date(Common.DATETIME_FORMAT.format(now));
                            vipRules.setModifier(user_id);
                            //更新会员制度
                            m = vipRulesMapper.updateVipRules(vipRules);
                        } else {
                            status = "该会员卡类型在会员制度里已配置为高级会员，等级不能低于所选会员类型的等级";
                            return status;
                        }
                    } else if (vipRules.getVip_card_type_code().equals(old_code)) {
                        vipRules.setVip_type(vip_card_type_name);
                        vipRules.setVip_card_type_code(vip_card_type_code);
                        vipRules.setCorp_code(corp_code);
                        //更换企业的情况
//                        if (!corp_code.equals(old_corp)) {
//                            vipRules.setHigh_vip_card_type_code("");
//                            vipRules.setHigh_vip_type("");
//                        }
                        if (degree1 < Integer.valueOf(vipRules.getHigh_degree())) {
                            vipRules.setDegree(degree);
                            vipRules.setModified_date(Common.DATETIME_FORMAT.format(now));
                            vipRules.setModifier(user_id);
                            //更新会员制度
                            m = vipRulesMapper.updateVipRules(vipRules);
                        } else {
                            status = "该会员卡类型在会员制度里已配置，等级不能高于上级会员类型的等级";
                            return status;
                        }
                    }
                    int num=0;
                    //更新会员卡类型
                    num = vipCardTypeMapper.updateVipCardType(vipCardType);
                    if (num > 0 && m >= 0) {
                        status = Common.DATABEAN_CODE_SUCCESS;
                    } else {
                        status = Common.DATABEAN_CODE_ERROR;
                    }
                }
            } else if (vipCardType1 != null) {
                status = "该编号已存在";
            } else {
                status = "该名称已存在";
            }
        }
        return status;
    }


    @Override
    public int delete(int id) throws Exception {

        return vipCardTypeMapper.delVipCardTypeById(id);
    }

    @Override
    public PageInfo<VipCardType> getAllVipCardTypeScreen(int page_number, int page_size, String
            corp_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<VipCardType> list1 = vipCardTypeMapper.selectVipCardTypeScreen(params);
        for (VipCardType vipRules1 : list1) {
            vipRules1.setIsactive(CheckUtils.CheckIsactive(vipRules1.getIsactive()));
        }
        PageInfo<VipCardType> page = new PageInfo<VipCardType>(list1);
        return page;
    }

    @Override
    public VipCardType getVipCardTypeByCode(String corp_code, String vip_card_type_code, String isactive) throws
            Exception {
        return vipCardTypeMapper.selVipCardTypeByCode(corp_code, vip_card_type_code, isactive);
    }

    @Override
    public VipCardType getVipCardTypeByName(String corp_code, String vip_card_type_name, String isactive) throws
            Exception {
        return vipCardTypeMapper.selVipCardTypeByName(corp_code, vip_card_type_name, isactive);
    }


    @Override
    public List<VipCardType> getVipCardTypes(String corp_code, String isactive) throws Exception {
        return vipCardTypeMapper.selectByCorp(corp_code, isactive);
    }
}
