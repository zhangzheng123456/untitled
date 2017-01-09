package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.VipFsend;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/24.
 */
public interface VipFsendMapper {

    VipFsend selectById(int id) throws SQLException;

    List<VipFsend> selectAllFsend(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    int insertFsend(VipFsend vipFsend) throws SQLException;

    int updateFsend(VipFsend vipFsend) throws SQLException;

    int deleteById(int id) throws SQLException;

    List<VipFsend> selectAllFsendScreen(Map<String, Object> params) throws SQLException;

    VipFsend selectByCode(@Param("corp_code") String corp_code,@Param("sms_code") String sms_code)throws SQLException;

    List<VipFsend> getSendByActivityCode(@Param("corp_code")String corp_code, @Param("activity_vip_code")String activity_vip_code);

    int delSendByActivityCode(@Param("corp_code")String corp_code,@Param("activity_vip_code")String activity_vip_code);

}
