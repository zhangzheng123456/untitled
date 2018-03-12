package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.CodeUpdateMapper;
import com.bizvane.ishop.dao.SmsTemplateMapper;
import com.bizvane.ishop.dao.SmsTemplateTypeMapper;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.SmsTemplate;
import com.bizvane.ishop.entity.SmsTemplateType;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.BrandService;
import com.bizvane.ishop.service.SmsTemplateTypeService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/9/7.
 */
@Service
public class SmsTemplateTypeServiceImpl implements SmsTemplateTypeService {
    @Autowired
    SmsTemplateTypeMapper smsTemplateTypeMapper;
    @Autowired
    SmsTemplateMapper smsTemplateMapper;
    @Autowired
    CodeUpdateMapper codeUpdateMapper;
    @Autowired
    private BrandService brandService;
    @Override
    public SmsTemplateType getSmsTemplateTypeById(int id) throws Exception {
        SmsTemplateType smsTemplateType = smsTemplateTypeMapper.selectSmsTemplateById(id);
        String brand_code = smsTemplateType.getBrand_code();
        String corp_code=smsTemplateType.getCorp_code();
        String brand_name = "";
        if (brand_code != null && !brand_code.equals("")) {
            brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
            String[] brandCodes = brand_code.split(",");
            String brandCode = "";
            for (int i = 0; i < brandCodes.length; i++) {
                Brand brand = brandService.getBrandByCode(corp_code, brandCodes[i], Common.IS_ACTIVE_Y);
                if (brand != null) {
                    String brand_name1 = brand.getBrand_name();
                    brand_name = brand_name + brand_name1;
                    brandCode = brandCode + brandCodes[i];
                    if (i != brandCodes.length - 1) {
                        brand_name = brand_name + ",";
                        brandCode = brandCode + ",";
                    }
                }
            }
            smsTemplateType.setBrand_code(brandCode);
            smsTemplateType.setBrand_name(brand_name);

        }
        return smsTemplateType;

    }

    @Override
    public PageInfo<SmsTemplateType> getAllSmsTemplateTypeByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<SmsTemplateType> smsTemplateTypes;
        PageHelper.startPage(page_number, page_size);
        smsTemplateTypes = smsTemplateTypeMapper.selectAllSmsTemplateType(corp_code, search_value,null);
        for (SmsTemplateType smsTemplateType : smsTemplateTypes) {
            smsTemplateType.setIsactive(CheckUtils.CheckIsactive(smsTemplateType.getIsactive()));
        }
        PageInfo<SmsTemplateType> page = new PageInfo<SmsTemplateType>(smsTemplateTypes);
        return page;
    }

    @Override
    public PageInfo<SmsTemplateType> getAllSmsTemplateTypeByPage(int page_number, int page_size, String corp_code, String search_value,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        List<SmsTemplateType> smsTemplateTypes;
        PageHelper.startPage(page_number, page_size);
        smsTemplateTypes = smsTemplateTypeMapper.selectAllSmsTemplateType(corp_code, search_value,manager_corp_arr);
        for (SmsTemplateType smsTemplateType : smsTemplateTypes) {
            smsTemplateType.setIsactive(CheckUtils.CheckIsactive(smsTemplateType.getIsactive()));
        }
        PageInfo<SmsTemplateType> page = new PageInfo<SmsTemplateType>(smsTemplateTypes);
        return page;
    }

    @Override
    public List<SmsTemplateType> getAllSmsTemplateType(String corp_code) throws Exception {
        List<SmsTemplateType> smsTemplateTypes;
        smsTemplateTypes = smsTemplateTypeMapper.selectSmsTwmplateTypes(corp_code);

        return smsTemplateTypes;
    }

    @Override
    public String insert(String message, String user_id) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        String template_type_code = jsonObject.get("template_type_code").toString().trim();
        String template_type_name = jsonObject.get("template_type_name").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String brand_code = jsonObject.get("brand_codes").toString().trim();
        String[] codes = brand_code.split(",");
        String brand_code1 = "";
        for (int i = 0; i < codes.length; i++) {
            codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
            brand_code1 = brand_code1 + codes[i];
        }

       SmsTemplateType smsTemplateType1=getSmsTemplateTypeByCode(corp_code,template_type_code, Common.IS_ACTIVE_Y);
        SmsTemplateType smsTemplateType2=getSmsTemplateTypeByName(corp_code,template_type_name, Common.IS_ACTIVE_Y);

        if (smsTemplateType1 != null) {
            result = "该消息模板分组编号已存在";
       } else if (smsTemplateType2 != null) {
            result = "该消息模板分组名称已存在";
        } else {
            Date now = new Date();
            SmsTemplateType smsTemplateType = new SmsTemplateType();
            smsTemplateType.setCorp_code(corp_code);
            smsTemplateType.setBrand_code(brand_code1);
            smsTemplateType.setTemplate_type_code(template_type_code);
            smsTemplateType.setTemplate_type_name(template_type_name);
            smsTemplateType.setCreated_date(Common.DATETIME_FORMAT.format(now));
            smsTemplateType.setCreater(user_id);
            smsTemplateType.setModified_date(Common.DATETIME_FORMAT.format(now));
            smsTemplateType.setModifier(user_id);
            smsTemplateType.setIsactive(jsonObject.get("isactive").toString().trim());
            smsTemplateTypeMapper.insertSmsTemplateType(smsTemplateType);
            SmsTemplateType smsTemplateType3=this.getSmsTemplateTypeByCode(smsTemplateType.getCorp_code(),smsTemplateType.getTemplate_type_code(),smsTemplateType.getIsactive());

            result = String.valueOf(smsTemplateType3.getId());
        }
        return result;

    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String result = "";
        JSONObject jsonObject = new JSONObject(message);
        String vipGroup_id = jsonObject.get("id").toString();
        int id = Integer.parseInt(vipGroup_id);
        String template_type_code = jsonObject.get("template_type_code").toString().trim();
        String template_type_name = jsonObject.get("template_type_name").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        SmsTemplateType smsTemplateType1=getSmsTemplateTypeByCode(corp_code,template_type_code, Common.IS_ACTIVE_Y);
        SmsTemplateType smsTemplateType2=getSmsTemplateTypeByName(corp_code,template_type_name, Common.IS_ACTIVE_Y);
        String brand_code = jsonObject.get("brand_codes").toString().trim();
        String[] codes = brand_code.split(",");
        String brand_code1 = "";
        for (int i = 0; i < codes.length; i++) {
            codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
            brand_code1 = brand_code1 + codes[i];
        }
        if (smsTemplateType1 != null && smsTemplateType1.getId() != id) {
            result = "该消息模板分组编号已存在";
        } else if (smsTemplateType2 != null && smsTemplateType2.getId() != id) {
            result = "该消息模板分组名称已存在";
        } else {
            SmsTemplateType smsTemplateType3 = getSmsTemplateTypeById(id);
            if (!smsTemplateType3.getTemplate_type_code().trim().equals(template_type_code)){
                codeUpdateMapper.updateTemplateType(template_type_code,smsTemplateType3.getTemplate_type_code(),smsTemplateType3.getCorp_code());
            }
            SmsTemplateType smsTemplateType = new SmsTemplateType();
            Date now = new Date();
            smsTemplateType.setId(id);
            smsTemplateType.setBrand_code(brand_code1);
            smsTemplateType.setTemplate_type_code(template_type_code);
            smsTemplateType.setTemplate_type_name(template_type_name);
            smsTemplateType.setCorp_code(corp_code);
            smsTemplateType.setModified_date(Common.DATETIME_FORMAT.format(now));
            smsTemplateType.setModifier(user_id);
            smsTemplateType.setIsactive(jsonObject.get("isactive").toString().trim());
            smsTemplateTypeMapper.updateSmsTemplateType(smsTemplateType);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }

    @Override
    public int delete(int id) throws Exception {
        return smsTemplateTypeMapper.deleteSmsTemplateTypeById(id);
    }

    @Override
    public SmsTemplateType getSmsTemplateTypeByCode(String corp_code, String code, String isactive) throws Exception {
        SmsTemplateType smsTemplateType = this.smsTemplateTypeMapper.selectBySmsTemplateTypeCode(corp_code, code, isactive);
        return smsTemplateType;

    }

    @Override
    public SmsTemplateType getSmsTemplateTypeByName(String corp_code, String name, String isactive) throws Exception {
        SmsTemplateType smsTemplateType = this.smsTemplateTypeMapper.selectBySmsTemplateTypeName(corp_code, name, isactive);
        return smsTemplateType;

    }

    @Override
    public PageInfo<SmsTemplateType> getAllSmsTemplateTypeScreen(int page_number, int page_size, String corp_code,String brand_codes, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        String[] brands = null;
        PageHelper.startPage(page_number, page_size);
        if (!brand_codes.equals("")) {
            brand_codes = brand_codes.replace(Common.SPECIAL_HEAD, "");
            brands = brand_codes.split(",");
            for (int i = 0; i < brands.length; i++) {
                brands[i] = Common.SPECIAL_HEAD + brands[i] + ",";
            }
        }
        int flg = 0;
        for (int i = 0; i < map.size(); i++) {
            if (map.containsKey("brand_name") && !map.get("brand_name").equals("")) {
                flg = 1;
            }
        }
        params.put("brand_codes", brands);
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<SmsTemplateType> list1 = smsTemplateTypeMapper.selectSmsTemplateTypeScreen(params);
        for (SmsTemplateType smsTemplateType : list1) {
            smsTemplateType.setIsactive(CheckUtils.CheckIsactive(smsTemplateType.getIsactive()));
        }
        PageInfo<SmsTemplateType> page = new PageInfo<SmsTemplateType>(list1);
        return page;
    }

    @Override
    public PageInfo<SmsTemplateType> getAllSmsTemplateTypeScreen(int page_number, int page_size, String corp_code,String brand_codes, Map<String, String> map,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        String[] brands = null;
        PageHelper.startPage(page_number, page_size);
        if (!brand_codes.equals("")) {
            brand_codes = brand_codes.replace(Common.SPECIAL_HEAD, "");
            brands = brand_codes.split(",");
            for (int i = 0; i < brands.length; i++) {
                brands[i] = Common.SPECIAL_HEAD + brands[i] + ",";
            }
        }
        int flg = 0;
        for (int i = 0; i < map.size(); i++) {
            if (map.containsKey("brand_name") && !map.get("brand_name").equals("")) {
                flg = 1;
            }
        }

        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("manager_corp_arr", manager_corp_arr);
        params.put("corp_code", corp_code);
        params.put("brand_codes", brands);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<SmsTemplateType> list1 = smsTemplateTypeMapper.selectSmsTemplateTypeScreen(params);
        for (SmsTemplateType smsTemplateType : list1) {
            smsTemplateType.setIsactive(CheckUtils.CheckIsactive(smsTemplateType.getIsactive()));
        }
        PageInfo<SmsTemplateType> page = new PageInfo<SmsTemplateType>(list1);
        return page;
    }


    @Override
    public List<SmsTemplate> selectByTemplateType(String corp_code,String template_type) throws Exception {
        List<SmsTemplate> smsTemplates = smsTemplateMapper.selectByTemplateType(corp_code,template_type);

        return smsTemplates;
    }

    @Override
    public List<SmsTemplateType> selectTemplateTypeCountByBrand(String corp_code, String brand_code, String search_value, String isactive) throws Exception {
        String[] brand_codes = brand_code.split(",");
        for (int i = 0; i < brand_codes.length; i++) {
            brand_codes[i] = Common.SPECIAL_HEAD + brand_codes[i] + ",";
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("array", brand_codes);
        params.put("search_value", search_value);
        params.put("isactive", isactive);
        List<SmsTemplateType> list = smsTemplateTypeMapper.selectTemplateTypeCountByBrand(params);
        return list;
    }
}
