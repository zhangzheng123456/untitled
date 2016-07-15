package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
public interface VipRecordMapper {
    VipRecord selecctById(int id);

    int deleteByPrimary(Integer id);

    int insert(VipRecord VipRecord);

    int updateByPrimaryKey(VipRecord VipRecord);

    List<VipRecord> selectAllVipRecordInfo(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

    List<VipRecord> selectAllVipRecordScreen(Map<String,Object> map);

}
