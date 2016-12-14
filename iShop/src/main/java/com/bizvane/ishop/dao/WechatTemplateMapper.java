package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.WechatTemplate;

import java.sql.SQLException;

/**
 * Created by nanji on 2016/12/14.
 */
public interface WechatTemplateMapper {
    WechatTemplate selectById(int id) throws SQLException;
}
