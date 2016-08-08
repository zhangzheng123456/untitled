package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.entity.Interfacers;
import com.bizvane.ishop.entity.ValidateCode;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/23.
 */
public interface ValidateCodeService {
    int insertValidateCode(ValidateCode code)throws Exception;

    ValidateCode selectValidateCode(int code_id,String phone,String isactive)throws Exception;

    int updateValidateCode(ValidateCode code)throws Exception;

    int deleteValidateCode(int code_id)throws Exception;

    ValidateCode selValidateCodeById(int id)throws Exception;

    List<ValidateCode> selectAll()throws Exception;

    PageInfo<ValidateCode> selectAllValidateCode(int page_number, int page_size, String search_value)throws Exception;

    PageInfo<ValidateCode> selectAllScreen(int page_number, int page_size, Map<String,String> map)throws Exception;
}
