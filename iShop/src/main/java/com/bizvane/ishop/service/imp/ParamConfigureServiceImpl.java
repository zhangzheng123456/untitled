package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.ParamConfigureMapper;
import com.bizvane.ishop.entity.ParamConfigure;
import com.bizvane.ishop.service.ParamConfigureService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yan on 2016/8/10.
 */
@Service
public class ParamConfigureServiceImpl implements ParamConfigureService{
    @Autowired
    ParamConfigureMapper paramConfigureMapper;

    /**
     * 根据参数id
     * 获取某参数配置信息
     * @param id
     * @return
     * @throws SQLException
     */
    @Override
    public ParamConfigure getParamById(int id) throws SQLException {
        return paramConfigureMapper.selectById(id);
    }

    @Override
    public ParamConfigure getParamByKey(String param_key) throws Exception {
        ParamConfigure paramConfigure=this.paramConfigureMapper.selectParamByKey(param_key);
        return  paramConfigure;
    }

    @Override
    public ParamConfigure getParamByName(String param_name) throws Exception {
        ParamConfigure paramConfigure=this.paramConfigureMapper.selectParamByName(param_name);
        return  paramConfigure;
    }

    /**
     * 分页显示参数配置
     * @param page_number
     * @param page_size
     * @param search_value
     * @return
     * @throws Exception
     */
    @Override
    public PageInfo<ParamConfigure> getAllParamByPage(int page_number, int page_size, String search_value) throws Exception {
        List<ParamConfigure> paramConfigures;
        PageHelper.startPage(page_number, page_size);
        paramConfigures = paramConfigureMapper.selectAllParam(search_value);
        PageInfo<ParamConfigure> page = new PageInfo<ParamConfigure>(paramConfigures);
        return page;
    }

    @Override
    public List<ParamConfigure> getAllParams() throws Exception {
        List<ParamConfigure> paramConfigures;
        paramConfigures = paramConfigureMapper.selectParams();
        return paramConfigures;
    }

    @Override
    public String insert(String message) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
//        String area_code = jsonObject.get("area_code").toString();
//        String corp_code = jsonObject.get("corp_code").toString();
//        String area_name = jsonObject.get("area_name").toString();

        String param_key = jsonObject.get("param_key").toString();
        String param_name = jsonObject.get("param_name").toString();
        String param_value = jsonObject.get("param_value").toString();
        String remark = jsonObject.get("remark").toString();

        ParamConfigure paramConfigure = getParamByKey(param_key);
        ParamConfigure paramConfigure1 = getParamByName(param_name);

        if (paramConfigure == null && paramConfigure1 == null) {
            paramConfigure = new ParamConfigure();
            Date now = new Date();
            paramConfigure.setParam_key(param_key);
            paramConfigure.setParam_name(param_name);
            paramConfigure.setParam_value(param_value);
            paramConfigure.setRemark(remark);
            paramConfigureMapper.insertParam(paramConfigure);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else if (paramConfigure != null) {
            result = "参数key已存在";
        } else {
            result = "参数名称已存在";
        }
        return result;
    }

    @Override
    public String update(String message) throws Exception {
        String old_param_key = null;
        String new_param_key = null;
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        int param_id = Integer.parseInt(jsonObject.get("id").toString());

        String param_key = jsonObject.get("param_key").toString();
        new_param_key = param_key;
        String param_name = jsonObject.get("param_name").toString();
        String param_value = jsonObject.get("param_value").toString();
        String remark = jsonObject.get("remark").toString();
        ParamConfigure old_param = getParamById(param_id);
        old_param_key = old_param.getParam_key();

            ParamConfigure paramByKey = getParamByKey(param_key);
            ParamConfigure paramByName = getParamByName(param_name);
            if (paramByKey == null
                    && paramByName == null) {
                old_param = new ParamConfigure();
                Date now = new Date();
                old_param.setId(param_id);
                old_param.setParam_key(param_key);
                old_param.setParam_name(param_name);
                old_param.setParam_value(param_value);
                old_param.setRemark(remark);
                paramConfigureMapper.updateParam(old_param);
                result = Common.DATABEAN_CODE_SUCCESS;
            } else if (paramByKey != null) {
                result = "参数key已存在";
            } else {
                result = "参数名称已存在";
            }


        return result;
    }

    @Override
    public int delete(int id) throws Exception {
        return paramConfigureMapper.deleteByParamId(id);
    }

    @Override
    public PageInfo<ParamConfigure> selectByParamSearch(int page_number, int page_size, String param_keys, String search_value) throws Exception {
        String[] paramArray = null;
        if (null != param_keys && !param_keys.isEmpty()) {
            paramArray = param_keys.split(",");
            for (int i = 0; paramArray != null && i < paramArray.length; i++) {
                paramArray[i] = paramArray[i].substring(1, paramArray[i].length());
            }
        }
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("param_keys", paramArray);
        params.put("search_value", search_value);
        PageHelper.startPage(page_number, page_size);
        List<ParamConfigure> paramConfigures = paramConfigureMapper.selectByParamSearch(params);
        PageInfo<ParamConfigure> page = new PageInfo<ParamConfigure>(paramConfigures);
        return page;
    }
}