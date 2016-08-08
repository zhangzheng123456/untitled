package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Feedback;
import com.bizvane.ishop.entity.ValidateCode;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ValidataCodeMapper {
    int deleteByCodeId(int id) throws SQLException;

    int insertValidateCode(ValidateCode record) throws SQLException;

    ValidateCode selectByCodeId(@Param("code_id")int code_id,@Param("phone") String phone,@Param("isactive") String isactive) throws SQLException;

    int updateByCodeId(ValidateCode record) throws SQLException;

    ValidateCode selValidateCodeById(@Param("id")int id) throws SQLException;

    List<ValidateCode> selectAllValidateCode(@Param("search_value")String search_value) throws SQLException;

    List<ValidateCode> selectAllScreen(Map<String,Object> params) throws SQLException;
}