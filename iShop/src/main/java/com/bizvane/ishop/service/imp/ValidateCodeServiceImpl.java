package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.ValidataCodeMapper;
import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.entity.Feedback;
import com.bizvane.ishop.entity.UserAchvGoal;
import com.bizvane.ishop.entity.ValidateCode;
import com.bizvane.ishop.service.ValidateCodeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public PageInfo<ValidateCode> selectAllScreen(int page_number, int page_size, Map<String, String> map) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<ValidateCode> list = validataCodeMapper.selectAllScreen(params);
        for (ValidateCode validateCode:list) {
            if (validateCode.getIsactive().equals("Y")) {
                validateCode.setIsactive("是");
            } else {
                validateCode.setIsactive("否");
            }
        }
        PageInfo<ValidateCode> page = new PageInfo<ValidateCode>(list);
        return page;
    }

    public ValidateCode selectValidateCode(int code_id, String phone, String isactive) {
        return validataCodeMapper.selectByCodeId(code_id, phone,isactive);
    }

    public int updateValidateCode(ValidateCode code) {
        return validataCodeMapper.updateByCodeId(code);
    }

    public int deleteValidateCode(int code_id) {
        return validataCodeMapper.deleteByCodeId(code_id);
    }

    @Override
    public ValidateCode selValidateCodeById(int id) {
        return validataCodeMapper.selValidateCodeById(id);
    }

    @Override
    public List<ValidateCode> selectAll() {
        return validataCodeMapper.selectAllValidateCode("");
    }

    @Override
    public PageInfo<ValidateCode> selectAllValidateCode(int page_number, int page_size, String search_value) throws SQLException{
        PageHelper.startPage(page_number, page_size);
        List<ValidateCode> validateCodes = validataCodeMapper.selectAllValidateCode(search_value);
        for (ValidateCode validateCode:validateCodes) {
            if (validateCode.getIsactive().equals("Y")) {
                validateCode.setIsactive("是");
            } else {
                validateCode.setIsactive("否");
            }
        }
        PageInfo<ValidateCode> page=new PageInfo<ValidateCode>(validateCodes);
        return page;
    }


}
