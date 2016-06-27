package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.MessageTemplateMapper;
import com.bizvane.ishop.entity.MessageTemplate;
import com.bizvane.ishop.service.MessageTemplateService;
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
public class MessageTemplateServiceImpl implements MessageTemplateService {

    @Autowired
    private MessageTemplateMapper messageTemplateMapper;


    @Override
    public MessageTemplate getMessageTemplateById(int id) throws SQLException {
        return this.messageTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(MessageTemplate messageTemplate) throws SQLException {
        return this.messageTemplateMapper.insert(messageTemplate);
    }

    @Override
    public int delete(int id) throws SQLException {
        return this.messageTemplateMapper.deleteByPrimaryKey(id);
    }

    @Override
    public String update(MessageTemplate messageTemplate) throws SQLException {
        MessageTemplate old = this.messageTemplateMapper.selectByPrimaryKey(messageTemplate.getId());
        if ((!old.getTem_code().equals(messageTemplate.getTem_code()))
                && this.messageTemplateNameExist(messageTemplate.getCorp_code(), messageTemplate.getTem_code()).equals(Common.DATABEAN_CODE_ERROR)
                ) {
            return "编号已经存在！！！！";
        } else if (!old.getTem_name().equals(messageTemplate.getTem_name()) &&
                (this.messageTemplateNameExist(messageTemplate.getCorp_code(), messageTemplate.getTem_name()).equals(Common.DATABEAN_CODE_ERROR))) {
            return "名称已经存在！！！！";
        } else if (this.messageTemplateMapper.updateByPrimaryKey(messageTemplate) >= 0) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public PageInfo<MessageTemplate> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        PageHelper.startPage(page_number, page_size);
        List<MessageTemplate> list = this.messageTemplateMapper.selectBySearch(corp_code, search_value);
        PageInfo<MessageTemplate> page = new PageInfo<MessageTemplate>(list);
        return page;
    }

    @Override
    public String messageTemplateExist(String corp_code, String tem_code) throws SQLException {
        List<MessageTemplate> list = this.messageTemplateMapper.selectByCode(corp_code, tem_code);
        if (list == null || list.size() < 1) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public String messageTemplateNameExist(String corp_code, String tem_name) throws SQLException {
        //messageTemplateNameExist
        List<MessageTemplate> list = this.messageTemplateMapper.selectByName(tem_name, corp_code);
        if (list == null || list.size() < 1) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }
}
