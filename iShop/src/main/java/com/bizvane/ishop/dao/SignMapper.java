package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Sign;
import com.bizvane.ishop.entity.User;
import org.apache.ibatis.annotations.Param;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/23.
 */
public interface SignMapper {
        List<Sign> selectSignAll(@Param("corp_code") String corp_code, @Param("search_value") String search_value);

        List<Sign> selectSignByInp(Map<String, Object> params);

        int delSignById(@Param("id")int id);

        List<Sign> selectByUser(@Param("corp_code")String corp_code,@Param("user_code")String user_code,@Param("search_value") String search_value);

        List<Sign> selectSignAllScreen(Map<String, Object> params);

        List<Sign> selectSignAllScreenUser(Map<String, Object> params);
}
