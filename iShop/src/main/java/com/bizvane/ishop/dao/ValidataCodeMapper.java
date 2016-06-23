package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.ValidateCode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ValidataCodeMapper {
    int deleteByCodeId(int id);

    int insertValidateCode(ValidateCode record);

    ValidateCode selectByCodeId(@Param("code_id")int code_id,@Param("phone") String phone,@Param("isactive") String isactive);

    int updateByCodeId(ValidateCode record);

    ValidateCode selValidateCodeById(@Param("id")int id);

    List<ValidateCode> selectAllValidateCode(@Param("search_value")String search_value);
}