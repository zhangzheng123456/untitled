package com.bizvane.ishop.dao;


import com.bizvane.ishop.entity.Appversion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yin on 2016/6/21.
 */
public interface AppversionMapper {

    List<Appversion> selectAllAppversion(@Param("search_value") String search_value);

    int addAppversion(Appversion appversion);


    int updAppversionById(Appversion appversion);

    int delAppversionById(int id);

    Appversion selAppversionById(@Param("id") int id);
}
