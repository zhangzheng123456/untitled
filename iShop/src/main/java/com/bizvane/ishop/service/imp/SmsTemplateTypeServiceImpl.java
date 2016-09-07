package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.SmsTemplateTypeMapper;
import com.bizvane.ishop.entity.SmsTemplateType;
import com.bizvane.ishop.service.SmsTemplateTypeService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/9/7.
 */
@Service
public class SmsTemplateTypeServiceImpl implements SmsTemplateTypeService {
    @Autowired
    SmsTemplateTypeMapper smsTemplateTypeMapper;

    @Override
    public SmsTemplateType getSmsTemplateTypeById(int id) throws Exception {
        return null;
    }

    @Override
    public PageInfo<SmsTemplateType> getAllSmsTemplateTypeByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<SmsTemplateType> smsTemplateTypes;
        PageHelper.startPage(page_number, page_size);
        smsTemplateTypes = smsTemplateTypeMapper.selectAllSmsTemplateType(corp_code, search_value);
        for (SmsTemplateType vipGroup : smsTemplateTypes) {
            vipGroup.setIsactive(CheckUtils.CheckIsactive(vipGroup.getIsactive()));
        }
        PageInfo<SmsTemplateType> page = new PageInfo<SmsTemplateType>(smsTemplateTypes);
        return page;
    }

    @Override
    public List<SmsTemplateType> getAllSmsTemplateType(String corp_code) throws Exception {
        return null;
    }

    @Override
    public String insert(String message, String user_id) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        String template_type_code = jsonObject.get("template_type_code").toString();
        String template_type_name = jsonObject.get("template_type_name").toString();
        String corp_code = jsonObject.get("corp_code").toString();
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
            smsTemplateType.setTemplate_type_code(template_type_code);
            smsTemplateType.setTemplate_type_name(template_type_name);
            smsTemplateType.setCreated_date(Common.DATETIME_FORMAT.format(now));
            smsTemplateType.setCreater(user_id);
            smsTemplateType.setModified_date(Common.DATETIME_FORMAT.format(now));
            smsTemplateType.setModifier(user_id);
            smsTemplateType.setIsactive(jsonObject.get("isactive").toString());
            smsTemplateTypeMapper.insertSmsTemplateType(smsTemplateType);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;

    }

    @Override
    public String update(String message, String user_id) throws Exception {
        return null;
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
    public PageInfo<SmsTemplateType> getAllSmsTemplateTypeScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        return null;
    }
}