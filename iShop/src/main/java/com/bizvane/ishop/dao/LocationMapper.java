package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Location;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ZhouZhou on 2016/10/18.
 */
public interface LocationMapper {

    public List<Location> selectAllProvince() throws SQLException;

    public List<Location> selectByHigherLevelCode(@Param("higher_level_code")String higher_level_code) throws SQLException;

    public Location selectByLocationCode(@Param("location_code")String location_code) throws SQLException;

}
