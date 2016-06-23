package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.SignMapper;
import com.bizvane.ishop.entity.Interfacers;
import com.bizvane.ishop.entity.Sign;
import com.bizvane.ishop.service.SignService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by yin on 2016/6/23.
 */
@Service
public class SignServiceImpl implements SignService{
    @Autowired
    private SignMapper signMapper;

    @Override
    public PageInfo<Sign> selectAllSign(int page_number, int page_size, String search_value) throws SQLException {
        PageHelper.startPage(page_number, page_size);
        List<Sign> signs = signMapper.selectSignface(search_value);
        PageInfo<Sign> page = new PageInfo<Sign>(signs);
        return page;
    }

    @Override
    public int delSignById(int id) {
        return signMapper.delSignById(id);
    }
}
