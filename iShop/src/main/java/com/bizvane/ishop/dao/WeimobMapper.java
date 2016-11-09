package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Weimob;
import org.apache.ibatis.annotations.Param;
import java.sql.SQLException;

public interface WeimobMapper {


    Weimob selectByCorpId(@Param("corp_code") String corp_code) throws SQLException;

    int insertCorpWeimob(Weimob record) throws SQLException;

    int updateCorpWeimob(Weimob record) throws SQLException;

}