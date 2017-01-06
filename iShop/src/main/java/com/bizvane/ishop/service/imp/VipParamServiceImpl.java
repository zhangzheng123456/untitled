package com.bizvane.ishop.service.imp;


import com.bizvane.ishop.constant.Common;

import com.bizvane.ishop.dao.VipParamMapper;
import com.bizvane.ishop.entity.VipParam;
import com.bizvane.ishop.service.VipParamService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/9/7.
 */
@Service
public class VipParamServiceImpl implements VipParamService {
    @Autowired
    VipParamMapper vipParamMapper;

    @Override
    public VipParam selectById(int id) throws Exception {
        return vipParamMapper.selectById(id);
    }

    @Override
    public List<VipParam> selectByParamName(String corp_code, String param_name,String isactive) throws Exception {
        return vipParamMapper.checkParamName(corp_code,param_name,isactive);
    }

    @Override
    public List<VipParam> selectByParamDesc(String corp_code, String param_desc,String isactive) throws Exception {
        return vipParamMapper.checkParamDesc(corp_code,param_desc,isactive);
    }

    @Override
    public PageInfo<VipParam> selectAllParam(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<VipParam> list = vipParamMapper.selectAllParam(corp_code,search_value,"");
        for (VipParam vipParam:list) {
            vipParam.setIsactive(CheckUtils.CheckIsactive(vipParam.getIsactive()));
        }
        PageInfo<VipParam> page = new PageInfo<VipParam>(list);
        return page;
    }

    @Override
    @Transactional
    public String insert(VipParam vipParam) throws Exception {
        List<VipParam> vipParams= selectByParamDesc(vipParam.getCorp_code().trim(), vipParam.getParam_desc().trim(),Common.IS_ACTIVE_Y);
        String result=Common.DATABEAN_CODE_ERROR;
        if(vipParams.size()==0){
            String order = vipParamMapper.selectMaxOrderByCorp(vipParam.getCorp_code().trim());
            vipParam.setShow_order(order);
            vipParamMapper.insert(vipParam);
            result=Common.DATABEAN_CODE_SUCCESS;
        }else if(vipParams.size()>0){
            result="会员参数名称已存在";
        }
        return result;

    }

    @Override
    @Transactional
    public String update(VipParam vipParam) throws Exception {
        String corp_code = vipParam.getCorp_code().trim();
        String param_desc_new = vipParam.getParam_desc().trim();
        List<VipParam> vipParams= selectByParamDesc(corp_code, vipParam.getParam_desc().trim(),Common.IS_ACTIVE_Y);
        String result=Common.DATABEAN_CODE_ERROR;

        if(vipParams.size()==0||vipParams.get(0).getParam_desc().equals(param_desc_new)){
            vipParamMapper.update(vipParam);
            result=Common.DATABEAN_CODE_SUCCESS;
        }else{
            result="会员参数名称已存在";
        }
        return result;
    }

    @Override
    @Transactional
    public void updateShowOrder(int id,String show_order) throws Exception {
        VipParam vipParam = selectById(id);
        if (vipParam != null) {
            vipParam.setShow_order(show_order);
            vipParamMapper.update(vipParam);
        }
    }

    @Override
    @Transactional
    public int delete(int id) throws Exception {
        return vipParamMapper.deleteById(id);
    }

    @Override
    public PageInfo<VipParam> selectAllParamScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        List<VipParam> vipParams;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        vipParams = vipParamMapper.selectAllParamScreen(params);
        for (VipParam vipParam:vipParams) {
            vipParam.setIsactive(CheckUtils.CheckIsactive(vipParam.getIsactive()));
        }
        PageInfo<VipParam> page = new PageInfo<VipParam>(vipParams);
        return page;
    }

    @Override
    public List<VipParam> selectParamByCorp(String corp_code) throws Exception {
        List<VipParam> list = vipParamMapper.selectParamByCorp(corp_code);
        return list;
    }

}
