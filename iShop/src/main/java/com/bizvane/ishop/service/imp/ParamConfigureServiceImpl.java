package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.ParamConfigureMapper;
import com.bizvane.ishop.entity.ParamConfigure;
import com.bizvane.ishop.service.ParamConfigureService;
import com.bizvane.ishop.utils.CheckUtils;
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
    public ParamConfigure getParamByKey(String param_name) throws Exception {
        ParamConfigure paramConfigure=this.paramConfigureMapper.selectParamByKey(param_name);
        return  paramConfigure;
    }

    @Override
    public ParamConfigure getParamByName(String param_desc) throws Exception {
        ParamConfigure paramConfigure=this.paramConfigureMapper.selectParamByName(param_desc);
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
        paramConfigures = paramConfigureMapper.selectParams("");
        return paramConfigures;
    }

    @Override
    public String insert(String message) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
//        String area_code = jsonObject.get("area_code").toString();
//        String corp_code = jsonObject.get("corp_code").toString();
//        String area_name = jsonObject.get("area_name").toString();

        String param_name = jsonObject.get("param_name").toString();
        String param_desc = jsonObject.get("param_desc").toString();
        String remark = jsonObject.get("remark").toString();

        ParamConfigure paramConfigure = getParamByKey(param_name);
//        ParamConfigure paramConfigure1 = getParamByName(param_desc);

        if (paramConfigure == null ) {
            paramConfigure = new ParamConfigure();
            Date now = new Date();
            paramConfigure.setParam_name(param_name);
            paramConfigure.setParam_desc(param_desc);
            paramConfigure.setRemark(remark);
            paramConfigureMapper.insertParam(paramConfigure);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else {
            result = "参数名已存在";
        }
        return result;
    }

    @Override
    public String update(String message) throws Exception {
        String result = "";
        JSONObject jsonObject = new JSONObject(message);
        int param_id = Integer.parseInt(jsonObject.get("id").toString());

        String param_name = jsonObject.get("param_name").toString();
        String param_type = jsonObject.get("param_type").toString();
        String param_values = jsonObject.get("param_values").toString();
        String param_desc = jsonObject.get("param_desc").toString();
        String remark = jsonObject.get("remark").toString();

        ParamConfigure paramByKey = getParamByKey(param_name);
//        ParamConfigure paramByName = getParamByName(param_desc);

        if (paramByKey != null && paramByKey.getId() != param_id) {
            result = "参数已存在";
//        } else if (paramByName != null && paramByName.getId() != param_id) {
//            result = "参数名称已存在";
        } else {
            ParamConfigure old_param = new ParamConfigure();
            old_param.setId(param_id);
            old_param.setParam_name(param_name);
            old_param.setParam_type(param_type);
            old_param.setParam_values(param_values);
            old_param.setParam_desc(param_desc);
            old_param.setRemark(remark);
            paramConfigureMapper.updateParam(old_param);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;

    }

    @Override
    public int delete(int id) throws Exception {
        return paramConfigureMapper.deleteByParamId(id);
    }

    @Override
    public PageInfo<ParamConfigure> selectByParamSearch(int page_number, int page_size ,String search_value) throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("search_value", search_value);
        PageHelper.startPage(page_number, page_size);
        List<ParamConfigure> paramConfigures = paramConfigureMapper.selectAllParam(search_value);
        PageInfo<ParamConfigure> page = new PageInfo<ParamConfigure>(paramConfigures);
        return page;
    }

    @Override
   public  PageInfo<ParamConfigure> selectParamScreen(int page_number, int page_size, Map<String, String> map) throws Exception{
        String[] paramArray = null;
        List<ParamConfigure> names;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        names = paramConfigureMapper.selectParamScreen(params);
        PageInfo<ParamConfigure> page = new PageInfo<ParamConfigure>(names);
        return page;

    }



}
