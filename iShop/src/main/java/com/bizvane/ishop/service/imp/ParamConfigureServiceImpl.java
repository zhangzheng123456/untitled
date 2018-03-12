package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.ParamConfigureMapper;
import com.bizvane.ishop.entity.ParamConfigure;
import com.bizvane.ishop.service.ParamConfigureService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
public class ParamConfigureServiceImpl implements ParamConfigureService {
    @Autowired
    ParamConfigureMapper paramConfigureMapper;

    /**
     * 根据参数id
     * 获取某参数配置信息
     *
     * @param id
     * @return
     * @throws SQLException
     */
    @Override
    public ParamConfigure getParamById(int id) throws SQLException {
        return paramConfigureMapper.selectById(id);
    }

    @Override
    public ParamConfigure getParamByKey(String param_name,String isactive) throws Exception {
        ParamConfigure paramConfigure = this.paramConfigureMapper.selectParamByKey(param_name,isactive);
        return paramConfigure;
    }

    @Override
    public ParamConfigure getParamByName(String param_desc) throws Exception {
        ParamConfigure paramConfigure = this.paramConfigureMapper.selectParamByName(param_desc);
        return paramConfigure;
    }

    /**
     * 分页显示参数配置
     *
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
        String result = "";
        for (ParamConfigure paramConfigure : paramConfigures) {

            String param_type = paramConfigure.getParam_type();
            if (param_type == null) {
                result = "";
            } else if (param_type.equals("switch")) {
                    result = "开关";
            } else if (param_type.equals("list")) {
                result = "选择列表";
            } else if (param_type.equals("custom")) {
                result = "自定义";
            } else {
                result = "";
            }
            paramConfigure.setParam_type(result);

        }

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
    public String insert(String message, String user_id) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = JSONObject.parseObject(message);
        String param_name = jsonObject.get("param_name").toString();
        String param_type = jsonObject.get("param_type").toString();
        String param_values = null;
        String param_desc = jsonObject.get("param_desc").toString();
        String remark = jsonObject.get("remark").toString();
        if (param_type.equals("switch")) {
            param_values = "Y,N";
        } else {
            param_values = jsonObject.get("param_values").toString();
        }
        ParamConfigure paramConfigure = getParamByKey(param_name,Common.IS_ACTIVE_Y);
        if (paramConfigure != null) {
            result = "参数名已存在";

        }else{
            paramConfigure = new ParamConfigure();
            Date now = new Date();
            paramConfigure.setParam_name(param_name);
            paramConfigure.setParam_type(param_type);
            paramConfigure.setParam_values(param_values);
            paramConfigure.setParam_desc(param_desc);
            paramConfigure.setRemark(remark);
            paramConfigure.setCreated_date(Common.DATETIME_FORMAT.format(now));
            paramConfigure.setCreater(user_id);
            paramConfigure.setModified_date(Common.DATETIME_FORMAT.format(now));
            paramConfigure.setModifier(user_id);
            paramConfigure.setIsactive(jsonObject.get("isactive").toString());
            paramConfigureMapper.insertParam(paramConfigure);
            result = Common.DATABEAN_CODE_SUCCESS;
        }

        return result;
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String result = "";
        String param_values = null;
        JSONObject jsonObject = JSONObject.parseObject(message);
        int param_id = Integer.parseInt(jsonObject.get("id").toString());

        String param_name = jsonObject.get("param_name").toString();
        String param_type = jsonObject.get("param_type").toString();
        if (param_type.equals("switch")) {
            param_values = "Y,N";
        } else if (param_type.equals("list")) {
            param_values = jsonObject.get("param_values").toString();
        } else {
            param_values = "";
        }

        String param_desc = jsonObject.get("param_desc").toString();
        String remark = jsonObject.get("remark").toString();

        ParamConfigure paramByKey = getParamByKey(param_name,Common.IS_ACTIVE_Y);

        if (paramByKey != null && paramByKey.getId() != param_id) {
            result = "参数已存在";
        } else {
            ParamConfigure old_param = new ParamConfigure();
            Date now = new Date();
            old_param.setId(param_id);
            old_param.setParam_name(param_name);
            old_param.setParam_type(param_type);
            old_param.setParam_values(param_values);
            old_param.setParam_desc(param_desc);
            old_param.setRemark(remark);
            old_param.setModified_date(Common.DATETIME_FORMAT.format(now));
            old_param.setModifier(user_id);
            old_param.setIsactive(jsonObject.get("isactive").toString());
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
    public PageInfo<ParamConfigure> selectByParamSearch(int page_number, int page_size, String search_value) throws Exception {

        // Map<String, Object> params = new HashMap<String, Object>();
        // params.put("search_value", search_value);
        PageHelper.startPage(page_number, page_size);
        List<ParamConfigure> paramConfigures = paramConfigureMapper.selectAllParam(search_value);
        String result = "";
        for (ParamConfigure paramConfigure : paramConfigures) {

            String param_type = paramConfigure.getParam_type();
            if (param_type == null) {
                result = "";
            } else if (param_type.equals("switch")) {
                result = "开关";
            } else if (param_type.equals("list")) {
                result = "选择列表";
            } else if (param_type.equals("custom")) {
                result = "自定义";
            } else {
                result = "";
            }
            paramConfigure.setParam_type(result);

        }

        PageInfo<ParamConfigure> page = new PageInfo<ParamConfigure>(paramConfigures);
        return page;
    }

    @Override
    public PageInfo<ParamConfigure> selectParamScreen(int page_number, int page_size, Map<String, String> map) throws Exception {
        List<ParamConfigure> names;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);

        String result = "";
        names = paramConfigureMapper.selectParamScreen(params);
        for (ParamConfigure paramConfigure : names) {

            String param_type = paramConfigure.getParam_type();
            if (param_type == null) {
                result = "";
            } else if (param_type.equals("switch")) {
                result = "开关";
            } else if (param_type.equals("list")) {
                result = "选择列表";
            } else if (param_type.equals("custom")) {
                result = "自定义";
            } else {
                result = "";
            }
            paramConfigure.setParam_type(result);
        }
        PageInfo<ParamConfigure> page = new PageInfo<ParamConfigure>(names);
        return page;

    }


}
