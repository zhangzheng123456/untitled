package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.UserAchvGoal;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface UserService {

    User getUserById(int id) throws SQLException;

    User getById(int id) throws SQLException;

    String insert(User user) throws SQLException;

    String update(User user) throws SQLException;

    void updateUser(User user) throws SQLException;

    int delete(int id,String user_code,String corp_code) throws SQLException;

    JSONObject login(HttpServletRequest request, String phone, String password) throws Exception;

    PageInfo<User> selectBySearch(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value) throws SQLException;

    PageInfo<User> selectBySearchPart(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String role_code) throws SQLException;

    PageInfo<User> selUserByStoreCode(int page_number, int page_size, String corp_code, String search_value, String store_code, String role_code) throws SQLException;


    PageInfo<User> selectGroupUser(int page_number, int page_size, String corp_code, String group_code) throws SQLException;

    int selectGroupUser(String corp_code, String group_code) throws SQLException;

    String userPhoneExist(String phone);

    String userEmailExist(String email);

    User userCodeExist(String user_code, String corp_code) throws SQLException;

    String register(String message) throws Exception;

    String getAuthCode(String phone, String platform);

    List<User> selectGroup(String corp_code, String group_code) throws SQLException;

//    String userNameExist(String user_name, String corp_code);

    void ProcessStoreCode(User user);

    List<UserAchvGoal> selectUserAchvCount(String corp_code, String user_code);

    int selectCount(String created_date);

    PageInfo<User> getScreenPart(int page_number, int page_size, String corp_code, Map<String,String> map, String store_code, String area_code, String role_code) throws SQLException;

    PageInfo<User> getAllUserScreen(int page_number, int page_size, String corp_code,Map<String,String> map);
}