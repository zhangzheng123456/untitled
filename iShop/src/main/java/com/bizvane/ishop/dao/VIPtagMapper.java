package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VIPtag;
import org.apache.ibatis.annotations.Param;

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

    List<VIPtag> selectAllVipTag(@Param("corp_code") String corp_code, @Param("search_value") String search_value);


    VIPtag selectVipTagCode(@Param("tag_code") String tag_code, @Param("corp_code") String corp_code);

}