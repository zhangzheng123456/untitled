package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.InterfaceMapper;
import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.entity.Feedback;
import com.bizvane.ishop.entity.Group;
import com.bizvane.ishop.entity.Interfacers;
import com.bizvane.ishop.service.InterfaceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/22.
 */
@Service
public class InterfaceServiceImpl implements InterfaceService{
    @Autowired
    private InterfaceMapper interfaceMapper;

    @Override
    public PageInfo<Interfacers> selectAllScreen(int page_number, int page_size, Map<String, String> map) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<Interfacers> list = interfaceMapper.selectAllScreen(params);
        for (Interfacers interfacers:list) {
            if(interfacers.getIsactive().equals("Y")){
                interfacers.setIsactive("是");
            }else{
                interfacers.setIsactive("否");
            }
        }
        PageInfo<Interfacers> page = new PageInfo<Interfacers>(list);
        return page;
    }

    @Override
    public List<Interfacers> selectAllInterface() throws SQLException {
        return interfaceMapper.selectAllInterface("");
    }

    @Override
    public PageInfo<Interfacers> selectAllInterface(int page_number, int page_size, String search_value) throws SQLException {
        PageHelper.startPage(page_number, page_size);
        List<Interfacers> interfacerses = interfaceMapper.selectAllInterface(search_value);
        for (Interfacers interfacers:interfacerses) {
            if(interfacers.getIsactive().equals("Y")){
                interfacers.setIsactive("是");
            }else{
                interfacers.setIsactive("否");
            }
        }
        PageInfo<Interfacers> page = new PageInfo<Interfacers>(interfacerses);
        return page;
    }

    @Override
    public Interfacers selInterfaceById(int id) throws SQLException {
        return interfaceMapper.selInterfaceById(id);
    }

    @Override
    public int delInterfaceById(int id) throws SQLException {
        return interfaceMapper.delInterfaceById(id);
    }

    @Override
    public int updInterfaceById(Interfacers interfacers) throws SQLException {
        return interfaceMapper.updInterfaceById(interfacers);
    }

    @Override
    public int addInterface(Interfacers interfacers) throws SQLException {
        return interfaceMapper.addInterface(interfacers);
    }
}
