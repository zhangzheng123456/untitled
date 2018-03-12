package com.bizvane.ishop.dao;


import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.UserQrcode;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface UserMapper {
    List<User> getCanloginByCode(@Param("corp_code")String corp_code,@Param("manager_corp_arr")String[] manager_corp_arr);

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

    List<User> selectAllUser(@Param("corp_code") String corp_code, @Param("search_value") String search_value,@Param("user_back")String user_back,@Param("manager_corp_arr")String[] manager_corp_arr) throws SQLException;

    List<User> selectPartUser(Map<String, Object> params) throws SQLException;

    List<User> selUserByStoreCode(Map<String, Object> params) throws SQLException;

    User selectUserById(String user_id) throws SQLException;

    User selectById(String user_id) throws SQLException;

    List<User> selectStoreUser(@Param("corp_code") String corp_code, @Param("store_code") String store_code,@Param("area_code") String area_code,
                               @Param("role_code") String role_code, @Param("isactive")String isactive,@Param("search_value")String search_value) throws SQLException;

    int selectCount(@Param("created_date") String created_date) throws SQLException;

    List<User> selectPartScreen(Map<String, Object> params) throws SQLException;

    List<User> selectPartScreen2(Map<String, Object> params) throws SQLException;

    List<User> selectAllUserScreen(Map<String, Object> params) throws SQLException;

    List<User> selectAllUserScreen2(Map<String, Object> params) throws SQLException;

    List<User> selectUsersByRole(Map<String, Object> params) throws SQLException;

    List<User> selectUsersOfTask(Map<String, Object> params) throws SQLException;

    List<User> selectUsersByRoleByCm(Map<String, Object> params) throws SQLException;

    List<User> selectUsersByRoleAndGroup(Map<String, Object> params) throws SQLException;


    List<User> selectPartUser2Code(Map<String, Object> params) throws SQLException;

    List<User> selectUsersByUserCode(Map<String, Object> params) throws SQLException;

    List<User> selectSMByStoreCode(@Param("corp_code") String corp_code, @Param("store_code") String store_code,@Param("store_id") String store_id,
                                   @Param("role_code") String role_code, @Param("search_value") String search_value) throws SQLException;

    List<User> selectUserCodeByNameOrCode(@Param("corp_code") String corp_code,@Param("search_value") String search_value) throws SQLException;

    //------------------------UserQrcodeMapper.xml----------

    List<UserQrcode> selectByUserCode(@Param("corp_code") String corp_code, @Param("user_code") String user_code) throws SQLException;

    List<UserQrcode> selectByUserApp(@Param("corp_code") String corp_code, @Param("user_code") String user_code, @Param("app_id") String app_id) throws SQLException;

    int insertUserQrcode(UserQrcode record);

    int updateUserByCode(User user) throws SQLException;

    int deleteUserQrcode(@Param("corp_code") String corp_code, @Param("user_code") String user_code) throws SQLException;

    int deleteUserQrcodeOne(@Param("corp_code") String corp_code, @Param("user_code") String user_code, @Param("app_id") String app_id) throws SQLException;

    int getUserCountByStore(Map<String, Object> params)throws Exception;

    User selectUserByUserCode(@Param("corp_code") String corp_code, @Param("user_code") String user_code,@Param("isactive") String isactive) throws Exception;

    List<User> selectUserByMasterCode(@Param("corp_code")String corp_code,@Param("master_code")String[] masterCodes,@Param("store_code") String[] store_codes) throws SQLException;



}