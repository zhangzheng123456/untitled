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
    int insertValidateCode(ValidateCode code);

    ValidateCode selectValidateCode(int code_id,String phone,String isactive);

    int updateValidateCode(ValidateCode code);

    int deleteValidateCode(int code_id);

    ValidateCode selValidateCodeById(int id);

    List<ValidateCode> selectAll();

    PageInfo<ValidateCode> selectAllValidateCode(int page_number, int page_size, String search_value)throws SQLException;

    PageInfo<ValidateCode> selectAllScreen(int page_number, int page_size, Map<String,String> map);

}
