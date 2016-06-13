package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VIPInfo;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/12.
 *
 * @@version
 */
public interface VipService {
    VIPInfo getVipInfoById(int id)throws SQLException;

    int insert(VIPInfo vipInfo) throws  SQLException;

    int update(VIPInfo vipInfo) throws SQLException;

    int delete(int id) throws  SQLException;

    PageInfo<VIPInfo> selectBySearch(int page_number,int page_size,String corp_code,String search_value);

    String vipCodeExist(String vip_code,String corp_code)throws SQLException;





}
