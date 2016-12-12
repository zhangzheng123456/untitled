package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.UserAchvGoal;
import com.bizvane.ishop.entity.UserQrcode;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by maoweidong on 2016/2/15.
 */

public interface UserService {
    List<User> getCanloginByCode(String corp_code);

    User getUserById(int id) throws Exception;

    User selectUserById(int id) throws Exception;

    User getById(int id) throws Exception;

    String insert(User user) throws Exception;

    String update(User user) throws Exception;

    void updateUser(User user) throws Exception;

    int delete(int id,String user_code,String corp_code) throws Exception;

    JSONObject login(HttpServletRequest request, String phone, String password) throws Exception;

    JSONObject noPasswdlogin(HttpServletRequest request, String corp_code, String user_code,String password) throws Exception;

    PageInfo<User> selectBySearch(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value) throws Exception;

    List<User> selectBySearch(String corp_code) throws Exception;

    PageInfo<User> selectBySearchPart(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_store, String area_code, String role_code) throws Exception;

    PageInfo<User> selUserByStoreCode(int page_number, int page_size, String corp_code, String search_value, String store_code, String[] area,String role_code) throws Exception;

    List<User> selUserByStoreCode(String corp_code, String search_value, String store_code, String[] area, String role_code) throws Exception;

    PageInfo<User> selectUsersByUserCode(int page_number, int page_size, String corp_code, String search_value, String user_code) throws Exception;

    PageInfo<User> selectUsersByRole(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String[] areas,String role_code) throws Exception;

    List<User> selectSMByStoreCode(String corp_code, String store_code,String store_id,String role_code, String search_value) throws Exception;

    PageInfo<User> selectGroupUser(int page_number, int page_size, String corp_code, String group_code,String search_value) throws Exception;

    int selectGroupUser(String corp_code, String group_code) throws Exception;

    List<User> userPhoneExist(String phone) throws Exception;

    List<User> userEmailExist(String email) throws Exception;

    List<User> userIdExist(String user_id,String corp_code) throws Exception;

    List<User> userCodeExist(String user_code, String corp_code,String isactive) throws Exception;

    List<User> selUserByUserId(String user_id, String corp_code,String isactive) throws Exception;
    String register(String message) throws Exception;

    String getAuthCode(String phone, String platform)throws Exception;

//    void ProcessCodeToSpecial(User user) throws Exception;

    List<UserAchvGoal> selectUserAchvCount(String corp_code, String user_code) throws Exception;

    int selectCount(String created_date) throws Exception;

    PageInfo<User> getScreenPart(int page_number, int page_size, String corp_code, Map<String,String> map, String store_code,String area_store, String area_code, String role_code) throws Exception;

    PageInfo<User> getScreenPart2(int page_number, int page_size, String corp_code, Map<String,String> map, String store_code,String area_store, String area_code, String role_code,List<Store> storeList) throws Exception;

    PageInfo<User> getAllUserScreen(int page_number, int page_size, String corp_code,Map<String,String> map) throws Exception;

    PageInfo<User> getAllUserScreen2(int page_number, int page_size, String corp_code,Map<String,String> map,List<Store> storeList) throws Exception;

    List<UserQrcode> selectQrcodeByUser(String corp_code, String user_code) throws Exception;

    List<UserQrcode> selectQrcodeByUserApp(String corp_code, String user_code, String app_id) throws Exception;

    int insertUserQrcode(UserQrcode userQrcode) throws Exception;

    int deleteUserQrcode(String corp_code,String user_code) throws Exception;

    int deleteUserQrcodeOne(String corp_code, String user_code,String app_id) throws Exception;

    String creatUserQrcode(String corp_code,String user_code,String auth_appid,String user_id) throws Exception;

    void signIn(JSONObject jsonObject, String user_code) throws Exception;

    void signOut(JSONObject jsonObject, String user_code) throws Exception;

    List<String> getBrandCodeByUser(int userId,String corp_code) throws Exception;
}