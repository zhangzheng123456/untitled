package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.User;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface UserService {

    User getUserById(int id) throws SQLException;

    int insert(User user) throws SQLException;

    int update(User user) throws SQLException;

    int delete(int id) throws SQLException;

    JSONObject login(HttpServletRequest request, String phone, String password) throws SQLException;

    User phoneExist(String phone) throws SQLException;

    PageInfo<User> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws SQLException;

    String userCodeExist(String user_code,String corp_coded) throws SQLException;

    String register(String message) throws Exception;

    String getAuthCode(String phone,String platform);
}