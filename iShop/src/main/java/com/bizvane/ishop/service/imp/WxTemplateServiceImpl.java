package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.WeimobMapper;
import com.bizvane.ishop.dao.WxTemplateMapper;
import com.bizvane.ishop.entity.Weimob;
import com.bizvane.ishop.entity.WxTemplate;
import com.bizvane.ishop.service.WeimobService;
import com.bizvane.ishop.service.WxTemplateService;
import com.bizvane.ishop.utils.IshowHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by ZhouZhou on 2016/10/10.
 */

@Service
public class WxTemplateServiceImpl implements WxTemplateService{

    @Autowired
    WxTemplateMapper wxTemplateMapper;

    private static final Logger logger = Logger.getLogger(WxTemplateServiceImpl.class);

    @Override
    public WxTemplate getTemplateById(int id) throws Exception {
        return wxTemplateMapper.selectById(id);
    }

    public List<WxTemplate> selectAllWxTemplate(String corp_code, String search_value) throws Exception {
        return wxTemplateMapper.selectAllWxTemplate(corp_code,search_value);
    }

    public int insertWxTemplate(WxTemplate template) throws Exception{
        return wxTemplateMapper.insertWxTemplate(template);
    }

    public int updateWxTemplate(WxTemplate template) throws Exception{
        return wxTemplateMapper.updateWxTemplate(template);
    }

    public int deleteWxTemplate(int id) throws Exception{
        return wxTemplateMapper.deleteWxTemplate(id);
    }
}
