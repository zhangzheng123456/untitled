package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VipTagType;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/21.
 *
 * @@version
 */
public interface VipTagTypeService {

    VipTagType getVipTagTypeById(int id) throws SQLException;

    int insert(VipTagType vipTagType) throws SQLException;

    int update(VipTagType vipTagType) throws SQLException;

    PageInfo<VipTagType> selectBySearch(int page_number, int page_size, String corp_code, String search_value);

    String vipTagTypeCodeExist(String type_code, String corp_code);

    String vipTagTypeNameExist(String type_name, String corp_code);

    int deleteById(int id);
}
