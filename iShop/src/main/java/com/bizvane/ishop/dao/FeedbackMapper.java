package com.bizvane.ishop.dao;


import com.bizvane.ishop.entity.Feedback;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/20.
 */
public interface FeedbackMapper {

    List<Feedback> selectAllFeedback(@Param("search_value") String search_value) throws SQLException;

    List<Feedback> selectAllScreen(Map<String,Object> params) throws SQLException;

    int addFeedback(Feedback feedback) throws SQLException;

    int updFeedbackById(Feedback feedback) throws SQLException;

    int delFeedbackById(int id) throws SQLException;

    Feedback selFeedbackById(@Param("id") int id) throws SQLException;
}
