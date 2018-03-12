package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.CorpJD;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

public interface CorpJDMapper {

    //
    List<CorpJD> selectAuthByCorp(@Param("corp_code") String corp_code) throws SQLException;

    CorpJD selectAuthByUid(@Param("u_id") String u_id) throws SQLException;

    int insertCorpJD(CorpJD record) throws SQLException;

    int updateCorpJD(CorpJD record) throws SQLException;

    int deleteCorpJD( @Param("corp_code") String corp_code) throws SQLException;

}