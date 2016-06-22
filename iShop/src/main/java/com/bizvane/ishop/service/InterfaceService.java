package com.bizvane.ishop.service;

import com.bizvane.ishop.dao.InterfaceMapper;
import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.entity.Interfacers;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by yin on 2016/6/22.
 */

public interface InterfaceService {
    //查询全部
    List<Interfacers> selectAllInterface() throws SQLException;
    //分页查询
    PageInfo<Interfacers> selectAllInterface(int page_number, int page_size, String search_value) throws SQLException;
    //根据ID查询
    Interfacers selInterfaceById(int id)throws SQLException;
    //根据ID删除
    int delInterfaceById(int id)throws  SQLException;
    //修改
    int updInterfaceById(Interfacers interfacers)throws  SQLException;
    //增加
    int addInterface(Interfacers interfacers)throws  SQLException;
}
