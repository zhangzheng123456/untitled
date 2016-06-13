package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.VIPtagMapper;
import com.bizvane.ishop.entity.VIPtag;
import com.bizvane.ishop.service.VIPTagService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
@Service
public class VIPTagServiceImpl implements VIPTagService {

    @Autowired
    private VIPtagMapper viPtagMapper;


    @Override
    public VIPtag getVIPTagById(int id) throws SQLException {
        return this.viPtagMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(VIPtag viPtag) throws SQLException {
        return viPtagMapper.insert(viPtag);
    }

    @Override
    public int delete(int id) throws SQLException {
        return viPtagMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<VIPtag> selectBySearch(int page_number, int page_size, String corp_code, String search_value) {
        List<VIPtag> list = null;
        if (search_value == null || search_value.isEmpty()) {
            list = viPtagMapper.selectAllVipInfo(corp_code, "");
        } else {
            list = viPtagMapper.selectAllVipInfo(corp_code, search_value);
        }
        PageInfo<VIPtag> page = new PageInfo<VIPtag>(list);
        return page;
    }

    @Override
    public String vipExist(String tag_code, String corp_code) throws SQLException {
        //viPtagMapper.selectVIpTag_code
        return null;
    }
}