package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VIPtag;

import java.util.List;

/**
 * Created by lixiang on 2016/6/12.
 *
 * @@version
 */
public interface VIPtagMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(VIPtag record);

    VIPtag selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(VIPtag record);

    List<VIPtag> selectAllVipInfo(String corp_code, String search_value);


    VIPtag selectVipTagCode(String tag_code, String corp_code);

}