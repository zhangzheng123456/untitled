package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Achv;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public interface AchvService {
    Achv getAchvById(int id)throws SQLException;

    int insert(Achv achv)throws  SQLException;

    int update(Achv achv)throws  SQLException;

    int delete(int id)throws SQLException;


}
