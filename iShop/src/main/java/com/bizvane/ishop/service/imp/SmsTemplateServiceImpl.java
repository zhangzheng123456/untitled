package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.SmsTemplateMapper;
import com.bizvane.ishop.entity.SmsTemplate;
import com.bizvane.ishop.entity.TemplateType;
import com.bizvane.ishop.entity.VipRecordType;
import com.bizvane.ishop.service.SmsTemplateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

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
    public SmsTemplate getSmsTemplateById(int id) throws SQLException {
        return this.smsTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(SmsTemplate SmsTemplate) throws SQLException {
        return this.smsTemplateMapper.insert(SmsTemplate);
    }

    @Override
    public int delete(int id) throws SQLException {
        return this.smsTemplateMapper.deleteByPrimaryKey(id);
    }

    @Override
    public String update(SmsTemplate SmsTemplate) throws SQLException {
        SmsTemplate old = this.smsTemplateMapper.selectByPrimaryKey(SmsTemplate.getId());
        if ((!old.getTemplate_code().equals(SmsTemplate.getTemplate_code()))
                && this.SmsTemplateNameExist(SmsTemplate.getCorp_code(), SmsTemplate.getTemplate_code()).equals(Common.DATABEAN_CODE_ERROR)
                ) {
            return "编号已经存在！！！！";
        } else if (!old.getTemplate_name().equals(SmsTemplate.getTemplate_name()) &&
                (this.SmsTemplateNameExist(SmsTemplate.getCorp_code(), SmsTemplate.getTemplate_name()).equals(Common.DATABEAN_CODE_ERROR))) {
            return "名称已经存在！！！！";
        } else if (this.smsTemplateMapper.updateByPrimaryKey(SmsTemplate) >= 0) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public PageInfo<SmsTemplate> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        PageHelper.startPage(page_number, page_size);
        List<SmsTemplate> list = this.smsTemplateMapper.selectBySearch(corp_code, search_value);
        PageInfo<SmsTemplate> page = new PageInfo<SmsTemplate>(list);
        return page;
    }

    @Override
    public String SmsTemplateExist(String corp_code, String tem_code) throws SQLException {
        List<SmsTemplate> list = this.smsTemplateMapper.selectByCode(corp_code, tem_code);
        if (list == null || list.size() < 1) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public String SmsTemplateNameExist(String corp_code, String tem_name) throws SQLException {
        //SmsTemplateNameExist
        List<SmsTemplate> list = this.smsTemplateMapper.selectByName(tem_name, corp_code);
        if (list == null || list.size() < 1) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public List<TemplateType> getTypes() {
        List<TemplateType> list = this.smsTemplateMapper.getTypes();
        return list;
    }

//    @Override
//    public List<VipRecordType> getMessageTypeByCorp(String corp_code, String s) {
//        return null;
//    }


}