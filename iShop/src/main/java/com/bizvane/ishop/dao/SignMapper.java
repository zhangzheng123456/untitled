package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Sign;
import org.apache.ibatis.annotations.Param;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

/**
 * Created by yin on 2016/6/23.
 */
public interface SignMapper {
        List<Sign> selectSignface(@Param("search_value")String search_value);

        Sign selectById(@Param("id")int id);

        int delSignById(@Param("id")int id);
}
