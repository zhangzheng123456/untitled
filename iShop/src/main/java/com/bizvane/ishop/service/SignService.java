package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Interfacers;
import com.bizvane.ishop.entity.Sign;
import com.bizvane.ishop.entity.User;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/23.
 */
public interface SignService {
    //分页查询
    PageInfo<Sign> selectSignAll(int page_number, int page_size, String corp_code, String search_value) throws SQLException;


    PageInfo<Sign> selectSignByInp(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String role_code) throws SQLException;

    PageInfo<Sign> selectByUser(int page_number, int page_size, String corp_code, String user_code, String search_value) throws SQLException;

    int delSignById(int id);


    PageInfo<Sign> selectSignAllScreen(int page_number, int page_size, String corp_code, String area_code, String store_code, String role_code, String user_code, Map<String, String> map);
}
