package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.VIPStoreRelation;
import com.mongodb.DBCursor;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhou on 2016/6/1.
 *
 * @@version
 */
public interface WebService {

    DBCursor selectEmpVip(String app_user_name, String open_id) throws SQLException;

    List<VIPStoreRelation> selectStoreVip(String app_user_name, String open_id) throws SQLException;

    JSONArray getCouponAndPresent(String open_id, String app_id) throws Exception;

    void processNewVip(String corp_code,String app_id,String open_id,JSONObject vipInfo_obj,String invite_open_id) throws Exception;

    void anniverActiRetroative(String corp_code, String vip_id, String cust_cols)throws Exception;
}
