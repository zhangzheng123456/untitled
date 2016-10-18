package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.Function;
import com.bizvane.ishop.entity.Location;

import java.util.List;

/**
 * Created by ZhouZhou on 2016/10/18.
 */
public interface LocationService {

    List<Location> selectAllProvince() throws Exception;

    List<Location> selectByHigherLevelCode(String higher_level_code) throws Exception;
}
