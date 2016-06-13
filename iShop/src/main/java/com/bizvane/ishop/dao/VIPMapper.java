package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VIPInfo;

import java.util.List;

public interface VIPMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(VIPInfo record);

    VIPInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(VIPInfo record);

    List<VIPInfo> selectAllVipInfo(String corp_code, String search_value);

    VIPInfo selectVipCode(String vip_code, String corp_code);

}