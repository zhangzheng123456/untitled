package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipLabel;
import com.bizvane.ishop.entity.VipLabel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by lixiang on 2016/6/12.
 *
 * @@version
 */
public interface VipLabelMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(VipLabel record);

    VipLabel selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(VipLabel record);

    List<VipLabel> selectAllVipLabel(@Param("corp_code") String corp_code, @Param("search_value") String search_value);


    VipLabel selectVipLabelName(@Param("corp_code") String corp_code, @Param("lable_name") String tag_name);

    VipLabel selectTypeCodeByName(@Param("corp_code") String corp_code, @Param("type_name") String type_name);
}