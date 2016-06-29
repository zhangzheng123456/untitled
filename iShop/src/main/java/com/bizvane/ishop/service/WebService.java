package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VIPRelation;

import java.sql.SQLException;

/**
 * Created by zhou on 2016/6/1.
 *
 * @@version
 */
public interface WebService {

    VIPRelation selectVip(String app_user_name, String open_id) throws SQLException;

}
