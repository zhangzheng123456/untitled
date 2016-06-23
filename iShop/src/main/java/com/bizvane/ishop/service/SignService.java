package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Interfacers;
import com.bizvane.ishop.entity.Sign;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by yin on 2016/6/23.
 */
public interface SignService {
    //分页查询
    PageInfo<Sign> selectAllSign(int page_number, int page_size, String search_value) throws SQLException;

    int delSignById(int id);
}
