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

    User getUserById(int id) throws Exception;

    User getById(int id) throws Exception;

    String insert(User user) throws Exception;

    String update(User user) throws Exception;

    void updateUser(User user) throws Exception;

    int delete(int id,String user_code,String corp_code) throws Exception;

    JSONObject login(HttpServletRequest request, String phone, String password) throws Exception;

    PageInfo<User> selectBySearch(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value) throws Exception;

    PageInfo<User> selectBySearchPart(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String role_code) throws Exception;

    PageInfo<User> selUserByStoreCode(int page_number, int page_size, String corp_code, String search_value, String store_code, String role_code) throws Exception;

    PageInfo<User> selectGroupUser(int page_number, int page_size, String corp_code, String group_code) throws Exception;

    int selectGroupUser(String corp_code, String group_code) throws Exception;

    String userPhoneExist(String phone) throws Exception;

    String userEmailExist(String email) throws Exception;

    List<User> userCodeExist(String user_code, String corp_code,String isactive) throws Exception;

    String register(String message) throws Exception;

    String getAuthCode(String phone, String platform)throws Exception;

    List<User> selectGroup(String corp_code, String group_code) throws Exception;

//    String userNameExist(String user_name, String corp_code);

    void ProcessStoreCode(User user)throws Exception;;

    List<UserAchvGoal> selectUserAchvCount(String corp_code, String user_code) throws Exception;;

    int selectCount(String created_date) throws Exception;;

    PageInfo<User> getScreenPart(int page_number, int page_size, String corp_code, Map<String,String> map, String store_code, String area_code, String role_code) throws Exception;

    PageInfo<User> getAllUserScreen(int page_number, int page_size, String corp_code,Map<String,String> map) throws Exception;;
}