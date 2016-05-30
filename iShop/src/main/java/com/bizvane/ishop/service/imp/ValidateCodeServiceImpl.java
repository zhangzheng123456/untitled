package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.ValidataCodeMapper;
import com.bizvane.ishop.entity.ValidateCode;
import com.bizvane.ishop.service.ValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2016/5/23.
 */
@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {

    @Autowired
    private ValidataCodeMapper validataCodeMapper;

    public int insertValidateCode(ValidateCode code) {
        return validataCodeMapper.insertValidateCode(code);
    }

    public ValidateCode selectValidateCode(int code_id, String phone,String isactive) {
        return validataCodeMapper.selectByCodeId(code_id, phone,isactive);
    }

    public int updateValidateCode(ValidateCode code) {
        return validataCodeMapper.updateByCodeId(code);
    }

    public int deleteValidateCode(int code_id) {
        return validataCodeMapper.deleteByCodeId(code_id);
    }
}
