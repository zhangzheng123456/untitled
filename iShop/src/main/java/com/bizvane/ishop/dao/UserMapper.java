package com.bizvane.ishop.dao;


import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.UserQrcode;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface UserMapper {
    List<User> getCanloginByCode(@Param("corp_code")String corp_code);

    int deleteByUserId(int id) throws SQLException;

    int insertUser(User record) throws SQLException;

    int updateByUserId(User record) throws SQLException;

    List<User> selectByPhone(String phone) throws SQLException;

    List<User> selectByLogin(String phone) throws SQLException;

    List<User> selectLoginByUserCode(@Param("phone")String phone,@Param("corp_code")String corp_code) throws SQLException;

    List<User> userEmailExist(@Param("email") String email) throws SQLException;

    List<User> selectUserCode(@Param("user_code")String user_code, @Param("corp_code")String corp_code, @Param("isactive")String isactive) throws SQLException;

    List<User> selUserByUserId(@Param("user_id")String user_id, @Param("corp_code")String corp_code, @Param("isactive")String isactive) throws SQLException;

    List<User> selectGroupUser(@Param("corp_code") String corp_code, @Param("group_code") String group_code, @Param("search_value") String search_value) throws SQLException;

    List<User> selectAllUser(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    List<User> selectPartUser(Map<String, Object> params) throws SQLException;

    List<User> selUserByStoreCode(Map<String, Object> params) throws SQLException;

    User selectUserById(int user_id) throws SQLException;

    User selectById(int user_id) throws SQLException;

    List<User> selectStoreUser(@Param("corp_code") String corp_code, @Param("store_code") String store_code,@Param("area_code") String area_code,
                               @Param("role_code") String role_code, @Param("isactive")String isactive) throws SQLException;

    int selectCount(@Param("created_date") String created_date) throws SQLException;

    List<User> selectPartScreen(Map<String, Object> params) throws SQLException;

    List<User> selectPartScreen2(Map<String, Object> params) throws SQLException;

    List<User> selectAllUserScreen(Map<String, Object> params) throws SQLException;

    List<User> selectAllUserScreen2(Map<String, Object> params) throws SQLException;

    List<User> selectUsersByRole(Map<String, Object> params) throws SQLException;

    List<User> selectUsersByUserCode(Map<String, Object> params) throws SQLException;

    List<User> selectSMByStoreCode(@Param("corp_code") String corp_code, @Param("store_code") String store_code,@Param("store_id") String store_id,
                                   @Param("role_code") String role_code, @Param("search_value") String search_value) throws SQLException;

    //------------------------UserQrcodeMapper.xml----------

    List<UserQrcode> selectByUserCode(@Param("corp_code") String corp_code, @Param("user_code") String user_code) throws SQLException;

    List<UserQrcode> selectByUserApp(@Param("corp_code") String corp_code, @Param("user_code") String user_code, @Param("app_id") String app_id) throws SQLException;

    int insertUserQrcode(UserQrcode record);

    int deleteUserQrcode(@Param("corp_code") String corp_code, @Param("user_code") String user_code) throws SQLException;

    int deleteUserQrcodeOne(@Param("corp_code") String corp_code, @Param("user_code") String user_code, @Param("app_id") String app_id) throws SQLException;


}