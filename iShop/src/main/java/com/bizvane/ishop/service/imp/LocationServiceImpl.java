package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.LocationMapper;
import com.bizvane.ishop.entity.Location;
import com.bizvane.ishop.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Created by ZhouZhou on 2016/10/18.
 */
@Service
public class LocationServiceImpl implements LocationService{

    @Autowired
    LocationMapper locationMapper;

    public List<Location> selectAllProvince() throws Exception{
        return locationMapper.selectAllProvince();
    }

    public List<Location> selectByHigherLevelCode(String higher_level_code) throws Exception{
        return locationMapper.selectByHigherLevelCode(higher_level_code);
    }
}
