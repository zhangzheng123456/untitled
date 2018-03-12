package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.dao.WxTemplateContentMapper;
import com.bizvane.ishop.entity.WxTemplateContent;
import com.bizvane.ishop.service.WxTemplateContentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yanyadong on 2017/6/27.
 */
@Service
public class WxTemplateContentServiceImpl implements WxTemplateContentService{

    @Autowired
    WxTemplateContentMapper wxTemplateContentMapper;
    @Override
    public WxTemplateContent selectById(int id) throws Exception {
        WxTemplateContent wxTemplateContent=wxTemplateContentMapper.selectById(id);
        return wxTemplateContent;
    }

    @Override
    public PageInfo<WxTemplateContent> selectAllWxTemplateContent(int page_num, int page_size, String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_num,page_size);
        List<WxTemplateContent> list;
        list=wxTemplateContentMapper.selectAllWxTemplateContent(corp_code,search_value);
        PageInfo<WxTemplateContent> pageInfo=new PageInfo<WxTemplateContent>(list);
        return pageInfo;
    }

    @Override
    public PageInfo<WxTemplateContent> selectWxTemplateContentScreen(int page_number, int page_size, String corp_code, Map<String, Object> map) throws Exception {
        List<WxTemplateContent> wxTemplates;
        PageHelper.startPage(page_number,page_size);
        HashMap<String,Object> param=new HashMap<String, Object>();
        Set<String> sets=map.keySet();
        if(sets.contains("created_date")) {
            JSONObject date = JSONObject.parseObject(map.get("created_date").toString());
            param.put("created_date_start", date.get("start").toString());
            String end = date.get("end").toString();
            if (!end.equals(""))
                end = end + " 23:59:59";
            param.put("created_date_end", end);
            map.remove("created_date");
        }
        param.put("corp_code",corp_code);
        param.put("map",map);
        wxTemplates=wxTemplateContentMapper.selectWxTemplateContentScreen(param);
        PageInfo<WxTemplateContent> pageInfo=new PageInfo<WxTemplateContent>(wxTemplates);
        return pageInfo;
    }

    @Override
    public int insertWxTemplateContent(WxTemplateContent wxTemplateContent) throws Exception {
        int status=wxTemplateContentMapper.insertWxTemplateContent(wxTemplateContent);
        return status;
    }

    @Override
    public int updateWxTemplateContent(WxTemplateContent wxTemplateContent) throws Exception {
        int status=wxTemplateContentMapper.updateWxTemplateContent(wxTemplateContent);
        return status;
    }

    @Override
    public int deleteWxTemplateContent(int id) throws Exception {
       int status= wxTemplateContentMapper.deleteWxTemplateContent(id);
        return status;
    }

    @Override
    public List<WxTemplateContent> selectByName(String app_id, String template_name) throws Exception {
        List<WxTemplateContent> wxTemplateContent= wxTemplateContentMapper.selectByName(app_id,template_name);
        return wxTemplateContent;
    }

    @Override
    public List<WxTemplateContent> selectContentById(String app_id, String template_name, String type,String vip_card_type) throws Exception {
        List<WxTemplateContent> wxTemplateContent=wxTemplateContentMapper.selectContentById(app_id,template_name,type,vip_card_type);
        return wxTemplateContent;
    }

    @Override
    public List<WxTemplateContent> selectContentByCardId(String app_id, String template_name, String type,String vip_card_type_id) throws Exception {
        List<WxTemplateContent> wxTemplateContent=wxTemplateContentMapper.selectContentByCardId(app_id,template_name,type,vip_card_type_id);
        return wxTemplateContent;
    }
}
