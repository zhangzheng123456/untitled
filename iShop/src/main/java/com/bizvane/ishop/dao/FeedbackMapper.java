package com.bizvane.ishop.dao;


import com.bizvane.ishop.entity.Feedback;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/20.
 */
public interface FeedbackMapper {

    List<Feedback> selectAllFeedback(@Param("search_value") String search_value);

    List<Feedback> selectAllScreen(Map<String,Object> params);

    int addFeedback(Feedback feedback);

    int updFeedbackById(Feedback feedback);

    int delFeedbackById(int id);

    Feedback selFeedbackById(@Param("id") int id);


}
