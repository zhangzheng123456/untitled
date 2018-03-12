package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.AreaMapper;
import com.bizvane.ishop.dao.BrandMapper;
import com.bizvane.ishop.dao.VipCardTypeMapper;
import com.bizvane.ishop.dao.VipRulesMapper;
import com.bizvane.ishop.entity.Area;
import com.bizvane.ishop.entity.Brand;
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
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    BrandMapper brandMapper;
    @Autowired
    AreaMapper areaMapper;

    @Override
    public VipCardType getVipCardTypeById(int id) throws Exception {

        VipCardType vipCardType = vipCardTypeMapper.selVipCardTypeById(id);
        String brand_code = vipCardType.getBrand_code();
        String area_code = vipCardType.getStore_group_code();
        String corp_code = vipCardType.getCorp_code();
        String brand_name = "";
        String brand_code1 = "";

        if (brand_code != null && !brand_code.equals("")) {
            brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
            String[] ids = brand_code.split(",");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("corp_code", corp_code);
            map.put("brand_code", ids);
            map.put("search_value", "");
            List<Brand> brands = brandMapper.selectBrands(map);
            for (int i = 0; i < brands.size(); i++) {
                Brand brand = brands.get(i);
                if (brand != null) {
                    brand_name = brand_name + brand.getBrand_name() + ",";
                    brand_code1 = brand_code1 + brand.getBrand_code() + ",";
                }
            }
            brand_code = brand_code1.toString();
            if (brand_name.endsWith(","))
                brand_name = brand_name.substring(0, brand_name.length() - 1);
            vipCardType.setBrand_name(brand_name);
            if (brand_code.endsWith(","))
                brand_code = brand_code.substring(0, brand_code.length() - 1);
            vipCardType.setBrand_code(brand_code);
        } else {
            vipCardType.setBrand_name("");
            vipCardType.setBrand_code("");
        }

        String area_name = "";
        String area_code1 = "";
        if (area_code != null && !area_code.equals("")) {
            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
            String[] ids = area_code.split(",");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("corp_code", corp_code);
            map.put("area_codes", ids);
            List<Area> areas = areaMapper.selectArea(map);
            for (int i = 0; i < areas.size(); i++) {
                Area area = areas.get(i);
                if (area != null) {
                    area_name = area_name + area.getArea_name() + ",";
                    area_code1 = area_code1 + area.getArea_code() + ",";
                }
            }
            area_code = area_code1.toString();
            if (area_name.endsWith(","))
                area_name = area_name.substring(0, area_name.length() - 1);
            vipCardType.setStore_group_name(area_name);
            if (area_code.endsWith(","))
                area_code = area_code.substring(0, area_code.length() - 1);
            vipCardType.setStore_group_code(area_code);
        } else {
            vipCardType.setStore_group_name("");
            vipCardType.setStore_group_code("");
        }
        return vipCardType;
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
        JSONObject jsonObject = JSONObject.parseObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String isactive = jsonObject.get("isactive").toString().trim();

        String store_group_code = jsonObject.get("store_group_code").toString().trim();
        String store_group_code1 = "";
        if (!store_group_code.equals("")){
            String[] codes1 = store_group_code.split(",");
            for (int i = 0; i < codes1.length; i++) {
                codes1[i] = Common.SPECIAL_HEAD + codes1[i] + ",";
                store_group_code1 = store_group_code1 + codes1[i];
            }
        }

        String brand_code = jsonObject.get("brand_code").toString().trim();
        String brand_code1 = "";
        if (!brand_code.equals("")){
            String[] codes1 = brand_code.split(",");
            for (int i = 0; i < codes1.length; i++) {
                codes1[i] = Common.SPECIAL_HEAD + codes1[i] + ",";
                brand_code1 = brand_code1 + codes1[i];
            }
        }

        VipCardType vipCardType = WebUtils.JSON2Bean(jsonObject, VipCardType.class);
        vipCardType.setStore_group_code(store_group_code1);
        vipCardType.setBrand_code(brand_code1);
        VipCardType code = getVipCardTypeByCode(vipCardType.getCorp_code(), vipCardType.getVip_card_type_code(), vipCardType.getIsactive());
      //  VipCardType name = getVipCardTypeByName(vipCardType.getCorp_code(), vipCardType.getVip_card_type_name(), vipCardType.getIsactive());

        //if (code == null && name == null) {
        if (code == null ) {
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
        }else {
            status = "该编号已存在";
        }
 //       else if (code != null) {
//            status = "该编号已存在";
//        } else {
//            status = "该名称已存在";
//        }
        return status;
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String status = "";
        JSONObject jsonObject=JSONObject.parseObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String vip_card_type_code = jsonObject.get("vip_card_type_code").toString().trim();
        String vip_card_type_name = jsonObject.get("vip_card_type_name").toString().trim();
        String vip_card_type_id = jsonObject.getString("vip_card_type_id").trim();
//        String brand_code = jsonObject.get("brand_code").toString().trim();
//        String store_group_code = jsonObject.get("store_group_code").toString().trim();

        String degree = jsonObject.get("degree").toString().trim();
        String isactive = jsonObject.get("isactive").toString().trim();
        String id = jsonObject.get("id").toString().trim();
        //获取修改前会员卡类型信息
        VipCardType vipCardType = getVipCardTypeById(Integer.parseInt(id));
        String old_code = vipCardType.getVip_card_type_code().trim();
        String old_corp = vipCardType.getCorp_code().trim();

        VipCardType vipCardType1 = getVipCardTypeByCode(corp_code, vip_card_type_code, Common.IS_ACTIVE_Y);
     //   VipCardType vipCardType2 = getVipCardTypeByName(corp_code, vip_card_type_name, Common.IS_ACTIVE_Y);
        int degree1 = Integer.valueOf(degree);
        //获取会员制度卡类型
        List<VipRules> list1 = vipRulesService.getViprulesByCardTypeCode(old_corp, old_code);
        //获取会员制度高级卡类型
        List<VipRules> list = vipRulesService.selectByCardHighCode(old_corp, old_code);

        if (vipCardType1 != null && !id.equals(vipCardType1.getId())) {
            status = "该编号已存在";
        }
//        else if (vipCardType2 != null && !id.equals(vipCardType2.getId())) {
//            status = "该名称已存在";
//        }
        else {
            String store_group_code = jsonObject.get("store_group_code").toString().trim();
            String store_group_code1 = "";
            if (!store_group_code.equals("")){
                String[] codes1 = store_group_code.split(",");
                for (int i = 0; i < codes1.length; i++) {
                    codes1[i] = Common.SPECIAL_HEAD + codes1[i] + ",";
                    store_group_code1 = store_group_code1 + codes1[i];
                }
            }

            String brand_code = jsonObject.get("brand_code").toString().trim();
            String brand_code1 = "";
            if (!brand_code.equals("")){
                String[] codes1 = brand_code.split(",");
                for (int i = 0; i < codes1.length; i++) {
                    codes1[i] = Common.SPECIAL_HEAD + codes1[i] + ",";
                    brand_code1 = brand_code1 + codes1[i];
                }
            }
            vipCardType.setCorp_code(corp_code);
            vipCardType.setVip_card_type_code(vip_card_type_code);
            vipCardType.setVip_card_type_name(vip_card_type_name);
            vipCardType.setBrand_code(brand_code1);
            vipCardType.setStore_group_code(store_group_code1);
            vipCardType.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipCardType.setModifier(user_id);
            vipCardType.setDegree(degree);
            vipCardType.setIsactive(isactive);
            vipCardType.setVip_card_type_id(vip_card_type_id);

            //修改会员制度中 会员卡类型
            for (int i = 0; i < list1.size(); i++) {
                VipRules vipRules = list1.get(i);

                if (vipRules.getVip_card_type_code().equals(old_code)) {
                    vipRules.setVip_type(vip_card_type_name);
                    vipRules.setVip_card_type_code(vip_card_type_code);
                    vipRules.setCorp_code(corp_code);

                    if(vipRules.getHigh_degree()!=null && !vipRules.getHigh_degree().equals("")){
                        if (degree1 < Integer.valueOf(vipRules.getHigh_degree())) {
                            vipRules.setDegree(degree);
                        } else {
                            status = "该会员卡类型在会员制度里已配置，等级不能高于上级会员类型的等级";
                            return status;
                        }
                    }else{
                        vipRules.setDegree(degree);
                    }
                }
                vipRules.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipRules.setModifier(user_id);
                vipRulesMapper.updateVipRules(vipRules);
            }

            //修改会员制度中 高级会员卡类型
            for (int i = 0; i < list.size(); i++) {
                VipRules vipRules = list.get(i);
                if (vipRules.getHigh_vip_card_type_code().equals(old_code)) {
                    vipRules.setHigh_vip_card_type_code(vip_card_type_code);
                    vipRules.setCorp_code(corp_code);

                    vipRules.setHigh_vip_type(vip_card_type_name);
                    if (degree1 > Integer.valueOf(vipRules.getDegree())) {
                        vipRules.setHigh_degree(degree);
                    } else {
                        status = "该会员卡类型在会员制度里已配置为高级会员，等级不能低于所选会员类型的等级";
                        return status;
                    }
                }
                vipRules.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipRules.setModifier(user_id);
                vipRulesMapper.updateVipRules(vipRules);
            }
            //编辑会员卡类型表成功, 同步更新会员制度里相应的会员卡类型信息
            int num = vipCardTypeMapper.updateVipCardType(vipCardType);
            if (num > 0 ) {
                status = Common.DATABEAN_CODE_SUCCESS;
            } else {
                status = Common.DATABEAN_CODE_ERROR;
            }
        }
        return status;
    }


    @Override
    public int delete(int id) throws Exception {

        return vipCardTypeMapper.delVipCardTypeById(id);
    }

    @Override
    public PageInfo<VipCardType> getAllVipCardTypeScreen(int page_number, int page_size, String corp_code,
                                                         String brand_code, String store_group_code, Map<String, String> map) throws Exception {
        String[] brand_array = null;
        String[] store_group_array = null;
        if (!brand_code.equals(""))
            brand_array = brand_code.split(",");
        if (!store_group_code.equals(""))
            store_group_array = store_group_code.split(",");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        params.put("brand_array",brand_array);
        params.put("store_group_array",store_group_array);
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
    public List<VipCardType> getVipCardTypes(String corp_code, String isactive,String search_value) throws Exception {
        return vipCardTypeMapper.selectByCorp(corp_code, isactive,search_value);
    }

    @Override
    public List<VipCardType> getVipCardByRole(String corp_code, String isactive,String brand_code, String store_group_code) throws Exception {
        String[] brand_array = null;
        String[] store_group_array = null;
        if (!brand_code.equals(""))
            brand_array = brand_code.split(",");
        if (!store_group_code.equals(""))
            store_group_array = store_group_code.split(",");
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("corp_code",corp_code);
        param.put("isactive",isactive);
        param.put("brand_array",brand_array);
        param.put("store_group_array",store_group_array);

        return vipCardTypeMapper.selectByRole(param);
    }

    @Override
    public PageInfo<VipCardType> getVipCardByRole(int pageNum, int pageSize, String corp_code, String isactive,String brand_code, String store_group_code) throws Exception {
        String[] brand_array = null;
        String[] store_group_array = null;
        if (!brand_code.equals(""))
            brand_array = brand_code.split(",");
        if (!store_group_code.equals(""))
            store_group_array = store_group_code.split(",");
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("corp_code",corp_code);
        param.put("isactive",isactive);
        param.put("brand_array",brand_array);
        param.put("store_group_array",store_group_array);

        PageHelper.startPage(pageNum,pageSize);
        List<VipCardType> list = vipCardTypeMapper.selectByRole(param);
        PageInfo<VipCardType> pageInfo = new PageInfo<VipCardType>(list);
        return pageInfo;
    }

    @Transactional()
    public int insertVipCardType(VipCardType vipCardType) throws Exception{
       int statu= vipCardTypeMapper.insertVipCardType(vipCardType);
        return statu;
    }

    @Override
    public VipCardType isExistByType(String corp_code, String vip_card_type_id, String vip_card_type_code, String vip_card_type_name) throws Exception {

        VipCardType vipCardType=vipCardTypeMapper.isExistByType(corp_code,vip_card_type_id,vip_card_type_code,vip_card_type_name);
        return null;
    }


    @Override
    public VipCardType isExistByTypeTwo(String corp_code, String vip_card_type_id, String vip_card_type_code, String vip_card_type_name) throws Exception {

        VipCardType vipCardType=vipCardTypeMapper.isExistByType(corp_code,vip_card_type_id,vip_card_type_code,vip_card_type_name);
        return vipCardType;
    }

}
