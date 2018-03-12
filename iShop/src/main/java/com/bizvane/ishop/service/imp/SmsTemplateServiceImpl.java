package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.SmsTemplateMapper;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.entity.SmsTemplate;
import com.bizvane.ishop.service.SmsTemplateService;
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
 * Created by lixiang on 2016/6/22.
 *
 * @@version
 */
@Service
public class SmsTemplateServiceImpl implements SmsTemplateService {

    @Autowired
    private SmsTemplateMapper smsTemplateMapper;


    @Override
    public SmsTemplate getSmsTemplateById(int id) throws Exception {
        return this.smsTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(SmsTemplate SmsTemplate) throws Exception {
        return this.smsTemplateMapper.insert(SmsTemplate);
    }

    @Override
    public int delete(int id) throws Exception {
        return this.smsTemplateMapper.deleteByPrimaryKey(id);
    }

    @Override
    public String update(SmsTemplate smsTemplate) throws Exception {
        SmsTemplate old = this.smsTemplateMapper.selectByPrimaryKey(smsTemplate.getId());
        if (old.getCorp_code().trim().equals(smsTemplate.getCorp_code().trim())) {
            if ((!old.getTemplate_code().trim().equals(smsTemplate.getTemplate_code().trim()))
                    && this.SmsTemplateCodeExist(smsTemplate.getCorp_code().trim(), smsTemplate.getTemplate_code().trim()).equals(Common.DATABEAN_CODE_ERROR)
                    ) {
                return "编号已经存在";
            } else if (!old.getTemplate_name().trim().equals(smsTemplate.getTemplate_name().trim()) &&
                    (this.SmsTemplateNameExist(smsTemplate.getCorp_code().trim(), smsTemplate.getTemplate_name().trim()).equals(Common.DATABEAN_CODE_ERROR))) {
                return "名称已经存在";
            } else if (this.smsTemplateMapper.updateByPrimaryKey(smsTemplate) >= 0) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        } else {
            if (this.SmsTemplateCodeExist(smsTemplate.getCorp_code().trim(), smsTemplate.getTemplate_code().trim()).equals(Common.DATABEAN_CODE_ERROR)
                    ) {
                return "编号已经存在";
            } else if (this.SmsTemplateNameExist(smsTemplate.getCorp_code().trim(), smsTemplate.getTemplate_name().trim()).equals(Common.DATABEAN_CODE_ERROR)) {
                return "名称已经存在";
            } else if (this.smsTemplateMapper.updateByPrimaryKey(smsTemplate) >= 0) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        }
//        if ((!old.getTemplate_code().equals(smsTemplate.getTemplate_code()))
//                && this.SmsTemplateCodeExist(smsTemplate.getCorp_code(), smsTemplate.getTemplate_code()).equals(Common.DATABEAN_CODE_ERROR)
//                ) {
//            return "编号已经存在";
//        } else if (!old.getTemplate_name().equals(smsTemplate.getTemplate_name()) &&
//                (this.SmsTemplateNameExist(smsTemplate.getCorp_code(), smsTemplate.getTemplate_name()).equals(Common.DATABEAN_CODE_ERROR))) {
//            return "名称已经存在";
//        } else if (this.smsTemplateMapper.updateByPrimaryKey(smsTemplate) >= 0) {
//            return Common.DATABEAN_CODE_SUCCESS;
//        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public PageInfo<SmsTemplate> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws Exception{
        PageHelper.startPage(page_number, page_size);
        List<SmsTemplate> list = this.smsTemplateMapper.selectBySearch(corp_code, search_value,null);
        for (SmsTemplate smsTemplate:list) {
            smsTemplate.setIsactive(CheckUtils.CheckIsactive(smsTemplate.getIsactive()));
        }
        PageInfo<SmsTemplate> page = new PageInfo<SmsTemplate>(list);
        return page;
    }

    @Override
    public PageInfo<SmsTemplate> selectBySearch(int page_number, int page_size, String corp_code, String search_value,String manager_corp) throws Exception{
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        PageHelper.startPage(page_number, page_size);
        List<SmsTemplate> list = this.smsTemplateMapper.selectBySearch(corp_code, search_value,manager_corp_arr);
        for (SmsTemplate smsTemplate:list) {
            smsTemplate.setIsactive(CheckUtils.CheckIsactive(smsTemplate.getIsactive()));
        }
        PageInfo<SmsTemplate> page = new PageInfo<SmsTemplate>(list);
        return page;
    }

    public String SmsTemplateCodeExist(String corp_code, String tem_code) throws Exception {
        List<SmsTemplate> list = this.smsTemplateMapper.selectByCode(corp_code, tem_code);
        if (list == null || list.size() < 1) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public SmsTemplate getSmsTemplateForId(String corp_code, String template_code) throws Exception {
        return  this.smsTemplateMapper.getSmsTemplateForId(corp_code, template_code);
    }

    @Override
    public String SmsTemplateNameExist(String corp_code, String tem_name) throws Exception {
        //SmsTemplateNameExist
        List<SmsTemplate> list = this.smsTemplateMapper.selectByName(tem_name, corp_code);
        if (list == null || list.size() < 1) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public PageInfo<SmsTemplate> getAllSmsTemplateScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception{
        List<SmsTemplate> smsTemplates;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        smsTemplates = smsTemplateMapper.selectAllSmsTemplateScreen(params);
        for (SmsTemplate smsTemplate:smsTemplates) {
            smsTemplate.setIsactive(CheckUtils.CheckIsactive(smsTemplate.getIsactive()));
        }
        PageInfo<SmsTemplate> page = new PageInfo<SmsTemplate>(smsTemplates);
        return page;
    }

    @Override
    public PageInfo<SmsTemplate> getAllSmsTemplateScreen(int page_number, int page_size, String corp_code, Map<String, String> map,String manager_corp) throws Exception{
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        List<SmsTemplate> smsTemplates;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("manager_corp_arr", manager_corp_arr);
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        smsTemplates = smsTemplateMapper.selectAllSmsTemplateScreen(params);
        for (SmsTemplate smsTemplate:smsTemplates) {
            smsTemplate.setIsactive(CheckUtils.CheckIsactive(smsTemplate.getIsactive()));
        }
        PageInfo<SmsTemplate> page = new PageInfo<SmsTemplate>(smsTemplates);
        return page;
    }
//    @Override
//    public List<TemplateType> getTypes() {
//        List<TemplateType> list = this.smsTemplateMapper.getTypes();
//        return list;
//    }

//    @Override
//    public List<VipRecordType> getMessageTypeByCorp(String corp_code, String s) {
//        return null;
//    }


}
