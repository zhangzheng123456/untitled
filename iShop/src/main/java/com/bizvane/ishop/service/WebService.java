package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VIPEmpRelation;
import com.bizvane.ishop.entity.VIPStoreRelation;

import java.sql.SQLException;

/**
 * Created by zhou on 2016/6/1.
 *
 * @@version
 */
public interface WebService {

    VIPEmpRelation selectEmpVip(String app_user_name, String open_id) throws SQLException;

    VIPStoreRelation selectStoreVip(String app_user_name, String open_id) throws SQLException;
}
