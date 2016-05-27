package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.ValidateCode;
import org.apache.ibatis.annotations.Param;

public interface ValidataCodeMapper {
    int deleteByCodeId(int id);

    int insertValidateCode(ValidateCode record);

    ValidateCode selectByCodeId(@Param("code_id")int code_id,@Param("phone") String phone);

    int updateByCodeId(ValidateCode record);

}