package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipRecordType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by lixiang on 2016/6/21.
 *
 * @@version
 */
public interface VipRecordTypeMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(VipRecordType VipRecordType);

    VipRecordType selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(VipRecordType VipRecordType);

    List<VipRecordType> selectAllVipRecordType(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    VipRecordType selectCode(@Param("corp_code") String corp_code, @Param("type_code") String type_code);

    VipRecordType selectName(@Param("corp_code") String corp_code, @Param("type_name") String type_name);
}
