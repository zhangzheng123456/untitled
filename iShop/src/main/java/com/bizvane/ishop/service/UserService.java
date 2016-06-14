package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.User;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

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

    PageInfo<User> selectGroupUser(int page_number,int page_size,String corp_code,String group_code)throws SQLException;

    String userCodeExist(String user_code,String corp_code) throws SQLException;

    String register(String message) throws Exception;

    String getAuthCode(String phone,String platform);

    List<User> selectGroup(String corp_code, String group_code)throws SQLException;

    String userNameExist(String user_name, String corp_code);

    String userPhoneExist(String phone, String corp_code);

    String userEmailExist(String email, String corp_code);
}