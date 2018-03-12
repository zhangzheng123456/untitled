package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Appversion;

import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/21.
 */
public interface AppversionService {
    //查询全部
    List<Appversion> selectAllAppversion() throws Exception;
    //分页查询
    PageInfo<Appversion> selectAllAppversion(int page_number, int page_size, String search_value) throws Exception;

    PageInfo<Appversion> selectAllAppversion1(int page_number, int page_size, String search_value) throws Exception;

    PageInfo<Appversion> selectAllScreen(int page_number, int page_size, Map<String,String> map) throws Exception;
    //根据ID查询
    Appversion selAppversionById(int id)throws Exception;
    //根据ID删除
    int delAppversionById(int id)throws Exception;
    //修改
    int updAppversionById(Appversion appversion)throws Exception;
    //增加
    int addAppversion(Appversion appversion)throws Exception;
    //新增查询获取新增对象的id
    Appversion selAppversionForId(String corp_code,String version_id,String paltform)throws  Exception;

    List<Appversion> selLatestVersion() throws Exception;
}
