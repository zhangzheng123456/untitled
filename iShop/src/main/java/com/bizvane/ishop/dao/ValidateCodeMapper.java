package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.ValidateCode;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ValidateCodeMapper {
    int deleteByCodeId(int id) throws SQLException;

    int insertValidateCode(ValidateCode record) throws SQLException;

    ValidateCode selectPhoneExist(@Param("platform")String platform,@Param("phone") String phone,@Param("isactive") String isactive) throws SQLException;

    int updateByCodeId(ValidateCode record) throws SQLException;

    ValidateCode selValidateCodeById(@Param("id")int id) throws SQLException;

    List<ValidateCode> selectAllValidateCode(@Param("search_value")String search_value) throws SQLException;

    List<ValidateCode> selectValidateCodeByCorp(@Param("corp_code")String corp_code,@Param("search_value")String search_value) throws SQLException;

    List<ValidateCode> selectValidateCodeByCorp2(@Param("search_value")String search_value,@Param("manager_corp_arr")String[] manager_corp_arr) throws SQLException;


    List<ValidateCode> selectAllScreen(Map<String,Object> params) throws SQLException;

    List<ValidateCode> selectAllScreen2(Map<String,Object> params) throws SQLException;


    List<ValidateCode> selectByCorpScreen(Map<String,Object> params) throws SQLException;

    ValidateCode selectByPhone(@Param("phone") String phone,@Param("validate_code") String validate_code) throws SQLException;

}