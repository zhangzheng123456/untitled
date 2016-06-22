package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.entity.Interfacers;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yin on 2016/6/22.
 */
public interface InterfaceMapper {
    List<Interfacers> selectAllInterface(@Param("search_value") String search_value);

    int addInterface(Interfacers interfacers);


    int updInterfaceById(Interfacers interfacers);

    int delInterfaceById(int id);

    Interfacers selInterfaceById(@Param("id") int id);
}
