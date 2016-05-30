package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.ValidateCode;

/**
 * Created by Administrator on 2016/5/23.
 */
public interface ValidateCodeService {
    int insertValidateCode(ValidateCode code);

    ValidateCode selectValidateCode(int code_id,String phone,String isactive);

    int updateValidateCode(ValidateCode code);

    int deleteValidateCode(int code_id);

}
