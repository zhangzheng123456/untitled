package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.WechatTemplateMapper;
import com.bizvane.ishop.entity.WechatTemplate;
import com.bizvane.ishop.service.WechatTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by nanji on 2016/12/14.
 */
@Service
public class WechatTemplateServiceImpl implements WechatTemplateService {

    @Autowired
    WechatTemplateMapper wechatTemplateMapper;
    @Override
    public WechatTemplate getTemplateById(int id) throws Exception {
        return wechatTemplateMapper.selectById(id);

    }
}
