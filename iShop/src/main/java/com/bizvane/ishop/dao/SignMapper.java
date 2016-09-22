package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Sign;
import com.bizvane.ishop.entity.User;
import org.apache.ibatis.annotations.Param;
import org.w3c.dom.stylesheets.LinkStyle;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/23.
 */
public interface SignMapper{
        List<Sign> selectSignByInp(Map<String, Object> params) throws SQLException;

        int delSignById(@Param("id")int id) throws SQLException;

        int delSignByUser(@Param("user_code")String user_code,@Param("corp_code")String corp_code) throws SQLException;

        List<Sign> selectByUser(@Param("corp_code")String corp_code,@Param("user_code")String user_code,@Param("search_value") String search_value) throws SQLException;

        List<Sign> selectSignAllScreen(Map<String, Object> params) throws SQLException;

        List<Sign> selectSignAllScreenUser(Map<String, Object> params) throws SQLException;

        int insert(Sign sign) throws SQLException;

        List<Sign> selectUserRecord(@Param("corp_code")String corp_code,@Param("user_code")String user_code,@Param("today") String today,@Param("status") String status) throws SQLException;

}
