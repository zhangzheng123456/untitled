package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VIPInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VIPMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(VIPInfo record);

    VIPInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(VIPInfo record);

    List<VIPInfo> selectAllVipInfo(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    VIPInfo selectVipCode(@Param("vip_code") String vip_code, @Param("corp_code") String corp_code);

}