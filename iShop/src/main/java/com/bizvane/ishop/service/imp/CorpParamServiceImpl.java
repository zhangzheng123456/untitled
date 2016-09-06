package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.AreaMapper;
import com.bizvane.ishop.dao.CodeUpdateMapper;
import com.bizvane.ishop.dao.CorpParamMapper;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.entity.Area;
import com.bizvane.ishop.entity.CorpParam;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.AreaService;
import com.bizvane.ishop.service.CorpParamService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/8/11.
 */

@Service
public class CorpParamServiceImpl implements CorpParamService {
    @Autowired
    CorpParamMapper corpParamMapper;

    /**
     * 根据区域id
     * 获取某区域信息
     */
    @Override
    public CorpParam selectById(int id) throws Exception {
        return corpParamMapper.selectById(id);
    }

    public List<CorpParam> selectByCorpParam(String corp_code, String param_id) throws Exception {
        return corpParamMapper.selectByCorpParam(corp_code, param_id);
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
        List<CorpParam> areas = corpParamMapper.selectAllParam(search_value);
        PageInfo<CorpParam> page = new PageInfo<CorpParam>(areas);
        return page;
    }

    @Override
    @Transactional
    public String insert(String message, String user_code) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        String remark = jsonObject.get("remark").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String param_id = jsonObject.get("param_id").toString();
        String param_value = jsonObject.get("param_value").toString();
        List<CorpParam> corpParams = selectByCorpParam(corp_code, param_id);
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
        JSONObject jsonObject = new JSONObject(message);
        int id = Integer.parseInt(jsonObject.get("id").toString());
        String remark = jsonObject.get("remark").toString();
        String corp_code = jsonObject.get("corp_code").toString();
        String param_id = jsonObject.get("param_id").toString();

        String param_value = jsonObject.get("param_value").toString();
        List<CorpParam> corpParams = selectByCorpParam(corp_code, param_id);

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
        List<CorpParam> corp_params;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        corp_params = corpParamMapper.selectAllParamScreen(params);
        PageInfo<CorpParam> page = new PageInfo<CorpParam>(corp_params);
        return page;
    }
}
