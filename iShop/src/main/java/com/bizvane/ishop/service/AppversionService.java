package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Appversion;

import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by yin on 2016/6/21.
 */
public interface AppversionService {
    //查询全部
    List<Appversion> selectAllAppversion() throws SQLException;
    //分页查询
    PageInfo<Appversion> selectAllAppversion(int page_number, int page_size, String search_value) throws SQLException;
    //根据ID查询
    Appversion selAppversionById(int id)throws SQLException;
    //根据ID删除
    int delAppversionById(int id)throws  SQLException;
    //修改
    int updAppversionById(Appversion appversion)throws  SQLException;
    //增加
    int addAppversion(Appversion appversion)throws  SQLException;
}
