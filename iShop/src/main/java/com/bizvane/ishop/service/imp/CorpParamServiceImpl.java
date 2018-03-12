package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.controller.CorpParamController;
import com.bizvane.ishop.dao.CorpMapper;
import com.bizvane.ishop.dao.CorpParamMapper;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.CorpParam;
import com.bizvane.ishop.service.CorpParamService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by ZhouZhou on 2016/8/11.
 */

@Service
public class CorpParamServiceImpl implements CorpParamService {
    @Autowired
    CorpParamMapper corpParamMapper;
    @Autowired
    CorpMapper corpMapper;
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CorpParamServiceImpl.class);
    /**
     * 根据区域id
     * 获取某区域信息
     */
    @Override
    public CorpParam selectById(int id) throws Exception {
        CorpParam corpParam = corpParamMapper.selectById(id);
        String corp_code = corpParam.getCorp_code();
        if (corp_code.equals("all")){
            corpParam.setCorp_name("全部");
        }else {
            String corp_name = corpMapper.selectByCorpId(0,corp_code,Common.IS_ACTIVE_Y).getCorp_name();
            corpParam.setCorp_name(corp_name);
        }
        return corpParam;
    }

    public List<CorpParam> selectByCorpParam(String corp_code, String param_id,String isactive) throws Exception {
        return corpParamMapper.selectByCorpParam(corp_code, param_id,isactive);
    }

    public List<CorpParam> selectByParamId(String param_id) throws Exception {
        return corpParamMapper.selectByParamId(param_id);
    }

    /**
     * 分页显示区域
     */
    @Override
    public PageInfo<CorpParam> selectAllParam(int page_number, int page_size, String search_value) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<CorpParam> list = corpParamMapper.selectAllParam(search_value);
        for (CorpParam corpParam:list) {
            String corp_code = corpParam.getCorp_code();
            if (corp_code.equals("all")){
                corpParam.setCorp_name("全部");
            }else {
                Corp corp=corpMapper.selectByCorpId(0,corp_code,Common.IS_ACTIVE_Y);
                if(corp!=null){
                    String corp_name = corpMapper.selectByCorpId(0,corp_code,Common.IS_ACTIVE_Y).getCorp_name();

                    corpParam.setCorp_name(corp_name);
                }else{
                    corpParam.setCorp_name("");
                }
            }
            corpParam.setIsactive(CheckUtils.CheckIsactive(corpParam.getIsactive()));
        }
        PageInfo<CorpParam> page = new PageInfo<CorpParam>(list);
        return page;
    }

    @Override
    @Transactional
    public String insert(String message, String user_code) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = JSONObject.parseObject(message);
        String remark = jsonObject.get("remark").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String param_id = jsonObject.get("param_id").toString();
        String param_value = jsonObject.get("param_value").toString();
        List<CorpParam> corpParams = selectByCorpParam(corp_code, param_id,Common.IS_ACTIVE_Y);
        if (corpParams.size() > 0) {
            result = "该企业参数配置已存在";
        } else {
            CorpParam corpParam = new CorpParam();
            Date now = new Date();
            corpParam.setRemark(remark);
            corpParam.setParam_id(param_id);
            corpParam.setParam_value(param_value);
            corpParam.setCorp_code(corp_code);
            corpParam.setCreated_date(Common.DATETIME_FORMAT.format(now));
            corpParam.setCreater(user_code);
            corpParam.setModified_date(Common.DATETIME_FORMAT.format(now));
            corpParam.setModifier(user_code);
            corpParam.setIsactive(jsonObject.get("isactive").toString());
            corpParamMapper.insert(corpParam);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }

    @Override
    @Transactional
    public String update(String message, String user_code) throws Exception {
        String result = "";
        JSONObject jsonObject = JSONObject.parseObject(message);
        int id = Integer.parseInt(jsonObject.get("id").toString());
        String remark = jsonObject.get("remark").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String param_id = jsonObject.get("param_id").toString();

        String param_value = jsonObject.get("param_value").toString();
        List<CorpParam> corpParams = selectByCorpParam(corp_code, param_id,Common.IS_ACTIVE_Y);

        if (corpParams.size() == 0 || corpParams.get(0).getId() == id) {
            CorpParam corpParam = new CorpParam();
            Date now = new Date();
            corpParam.setId(id);
            corpParam.setRemark(remark);
            corpParam.setParam_id(param_id);
            corpParam.setParam_value(param_value);
            corpParam.setCorp_code(corp_code);
            corpParam.setModified_date(Common.DATETIME_FORMAT.format(now));
            corpParam.setModifier(user_code);
            corpParam.setIsactive(jsonObject.get("isactive").toString());
            corpParamMapper.update(corpParam);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else {
            result = "该企业参数配置已存在";
        }
        return result;
    }

    @Override
    @Transactional
    public int delete(int id) throws Exception {
        return corpParamMapper.deleteById(id);
    }

    @Override
    public PageInfo<CorpParam> selectAllParamScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        List<CorpParam> corpParams;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        corpParams = corpParamMapper.selectAllParamScreen(params);
        for (CorpParam corpParam:corpParams) {
            String corp_code1 = corpParam.getCorp_code();

            if (corp_code1.equals("all")){
                corpParam.setCorp_name("全部");
            }else {
                Corp corp=corpMapper.selectByCorpId(0,corp_code1,Common.IS_ACTIVE_Y);
                if(corp!=null){
                    String corp_name =corp.getCorp_name();
                    logger.info("============corp==================="+corp);
                    corpParam.setCorp_name(corp_name);
                }else{

                    logger.info("============corp=============null======");
                }

            }
            corpParam.setIsactive(CheckUtils.CheckIsactive(corpParam.getIsactive()));
        }
        PageInfo<CorpParam> page = new PageInfo<CorpParam>(corpParams);
        return page;
    }

    @Override
    public CorpParam selectByParamName(String param_name, String isactive) throws Exception {
        return corpParamMapper.selectByParamName(param_name,isactive);
    }

    public List<CorpParam> selectParamByName(String corp_code,String param_name) throws Exception {
        return corpParamMapper.selectParamByName(corp_code,param_name);
    }
}
