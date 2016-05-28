package com.bizvane.ishop.service;

import com.bizvane.ishop.bean.PageBean;
import com.bizvane.ishop.entity.UserInfo;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface UserService {

    UserInfo getUserById(int id) throws SQLException;

    int insert(UserInfo userInfo) throws SQLException;

    int update(UserInfo userInfo) throws SQLException;

    int delete(int id) throws SQLException;

    JSONObject login(HttpServletRequest request, String phone, String password) throws SQLException;

    UserInfo phoneExist(String phone) throws SQLException;

    PageBean<UserInfo> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws SQLException;

    String userCodeExist(String user_code,String corp_coded) throws SQLException;

    String register(String message) throws Exception;

    String getAuthCode(String phone,String platform);
}