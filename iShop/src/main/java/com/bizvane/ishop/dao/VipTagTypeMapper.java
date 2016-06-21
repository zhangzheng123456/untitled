package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipTagType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by lixiang on 2016/6/21.
 *
 * @@version
 */
public interface VipTagTypeMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(VipTagType vipTagType);

    VipTagType selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(VipTagType vipTagType);

    List<VipTagType> selectAllVipTagType(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    VipTagType selectCode(@Param("corp_code") String corp_code, @Param("type_code") String type_code);

    VipTagType selectName(@Param("corp_code") String corp_code, @Param("type_name") String type_name);
}
