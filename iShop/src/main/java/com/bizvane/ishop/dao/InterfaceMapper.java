package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.entity.Feedback;
import com.bizvane.ishop.entity.Interfacers;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/22.
 */
public interface InterfaceMapper {
    List<Interfacers> selectAllInterface(@Param("search_value") String search_value);

    List<Interfacers> selectAllScreen(Map<String,Object> params);

    int addInterface(Interfacers interfacers);


    int updInterfaceById(Interfacers interfacers);

    int delInterfaceById(int id);

    Interfacers selInterfaceById(@Param("id") int id);
}
