package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.WechatTemplate;

/**
 * Created by nanji on 2016/12/14.
 */
public interface WechatTemplateService {
    WechatTemplate getTemplateById(int id) throws Exception;
}
