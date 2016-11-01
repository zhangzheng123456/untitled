package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.entity.Feedback;
import com.bizvane.ishop.entity.Interfacers;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/22.
 */
public interface InterfaceMapper {
    List<Interfacers> selectAllInterface(@Param("search_value") String search_value) throws SQLException;

    List<Interfacers> selectAllScreen(Map<String,Object> params) throws SQLException;

    int addInterface(Interfacers interfacers) throws SQLException;

    int updInterfaceById(Interfacers interfacers) throws SQLException;

    int delInterfaceById(int id) throws SQLException;

    Interfacers selInterfaceById(@Param("id") int id) throws SQLException;
    Interfacers
}
