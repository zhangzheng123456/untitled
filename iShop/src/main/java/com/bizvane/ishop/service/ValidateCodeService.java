package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.ValidateCode;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/23.
 */
public interface ValidateCodeService {
    int insertValidateCode(ValidateCode code)throws Exception;

    ValidateCode selectPhoneExist(String platform,String phone,String isactive)throws Exception;

    int updateValidateCode(ValidateCode code)throws Exception;

    int deleteValidateCode(int code_id)throws Exception;

    ValidateCode selValidateCodeById(int id)throws Exception;

    List<ValidateCode> selectAll()throws Exception;

    PageInfo<ValidateCode> selectAllValidateCode(int page_number, int page_size, String search_value)throws Exception;

    PageInfo<ValidateCode> selectValidateCodeByCorp(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    PageInfo<ValidateCode> selectValidateCodeByCorp(int page_number, int page_size, String corp_code, String search_value,String manager_corp) throws Exception;


    PageInfo<ValidateCode> selectAllScreen(int page_number, int page_size, Map<String,String> map)throws Exception;

    PageInfo<ValidateCode> selectByCorpScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception;

    PageInfo<ValidateCode> selectByCorpScreen(int page_number, int page_size, String corp_code, Map<String, String> map,String manager_corp) throws Exception;


    ValidateCode selectByPhone(String phone,String validate_code) throws Exception;
}
