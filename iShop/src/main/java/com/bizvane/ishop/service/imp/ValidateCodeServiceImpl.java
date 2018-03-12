package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.ValidateCodeMapper;
import com.bizvane.ishop.entity.ValidateCode;
import com.bizvane.ishop.service.ValidateCodeService;
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
 * Created by Administrator on 2016/5/23.
 */
@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {

    @Autowired
    private ValidateCodeMapper validateCodeMapper;

    @Transactional
    public int insertValidateCode(ValidateCode code) throws Exception{
        String phone = code.getPhone();
        String platform = code.getPlatform();
        ValidateCode validateCode = selectPhoneExist(platform,phone, Common.IS_ACTIVE_Y);
        if (null!=validateCode){
            return 1;
        }else {
            validateCodeMapper.insertValidateCode(code);
            return 0;
        }
    }

    @Override
    public PageInfo<ValidateCode> selectAllScreen(int page_number, int page_size, Map<String, String> map) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<ValidateCode> list = validateCodeMapper.selectAllScreen(params);
        for (ValidateCode validateCode:list) {
            validateCode.setIsactive(CheckUtils.CheckIsactive(validateCode.getIsactive()));
        }
        PageInfo<ValidateCode> page = new PageInfo<ValidateCode>(list);
        return page;
    }

    @Override
    public PageInfo<ValidateCode> selectByCorpScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        params.put("corp_code", corp_code);
        PageHelper.startPage(page_number, page_size);
        List<ValidateCode> list = validateCodeMapper.selectByCorpScreen(params);
        for (ValidateCode validateCode:list) {
            validateCode.setIsactive(CheckUtils.CheckIsactive(validateCode.getIsactive()));
        }
        PageInfo<ValidateCode> page = new PageInfo<ValidateCode>(list);
        return page;
    }


    @Override
    public PageInfo<ValidateCode> selectByCorpScreen(int page_number, int page_size, String corp_code, Map<String, String> map,String manager_corp) throws Exception{
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        params.put("manager_corp_arr", manager_corp_arr);
        PageHelper.startPage(page_number, page_size);
        List<ValidateCode> list = validateCodeMapper.selectByCorpScreen(params);
        for (ValidateCode validateCode:list) {
            validateCode.setIsactive(CheckUtils.CheckIsactive(validateCode.getIsactive()));
        }
        PageInfo<ValidateCode> page = new PageInfo<ValidateCode>(list);
        return page;
    }

    public ValidateCode selectPhoneExist(String platform, String phone, String isactive) throws Exception{
        return validateCodeMapper.selectPhoneExist(platform, phone,isactive);
    }

    public int updateValidateCode(ValidateCode code) throws Exception{
        return validateCodeMapper.updateByCodeId(code);
    }

    public int deleteValidateCode(int code_id) throws Exception{
        return validateCodeMapper.deleteByCodeId(code_id);
    }

    @Override
    public ValidateCode selValidateCodeById(int id) throws Exception{
        return validateCodeMapper.selValidateCodeById(id);
    }

    @Override
    public List<ValidateCode> selectAll() throws Exception{
        return validateCodeMapper.selectAllValidateCode("");
    }

    @Override
    public PageInfo<ValidateCode> selectAllValidateCode(int page_number, int page_size, String search_value) throws Exception{
        PageHelper.startPage(page_number, page_size);
        List<ValidateCode> validateCodes = validateCodeMapper.selectAllValidateCode(search_value);
        for (ValidateCode validateCode:validateCodes) {
            validateCode.setIsactive(CheckUtils.CheckIsactive(validateCode.getIsactive()));
        }
        PageInfo<ValidateCode> page=new PageInfo<ValidateCode>(validateCodes);
        return page;
    }

    @Override
    public PageInfo<ValidateCode> selectValidateCodeByCorp(int page_number, int page_size, String corp_code, String search_value) throws Exception{
        PageHelper.startPage(page_number, page_size);
        List<ValidateCode> validateCodes = validateCodeMapper.selectValidateCodeByCorp(corp_code,search_value);
        for (ValidateCode validateCode:validateCodes) {
            validateCode.setIsactive(CheckUtils.CheckIsactive(validateCode.getIsactive()));
        }
        PageInfo<ValidateCode> page=new PageInfo<ValidateCode>(validateCodes);
        return page;
    }

    @Override
    public PageInfo<ValidateCode> selectValidateCodeByCorp(int page_number, int page_size, String corp_code, String search_value,String manager_corp) throws Exception{
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        PageHelper.startPage(page_number, page_size);
        List<ValidateCode> validateCodes = validateCodeMapper.selectValidateCodeByCorp2(search_value,manager_corp_arr);
        for (ValidateCode validateCode:validateCodes) {
            validateCode.setIsactive(CheckUtils.CheckIsactive(validateCode.getIsactive()));
        }
        PageInfo<ValidateCode> page=new PageInfo<ValidateCode>(validateCodes);
        return page;
    }

    @Override
    public ValidateCode selectByPhone(String phone,String validate_code) throws Exception{
        return validateCodeMapper.selectByPhone(phone,validate_code);
    }
}
