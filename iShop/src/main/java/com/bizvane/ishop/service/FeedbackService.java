package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.Feedback;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by yin on 2016/6/20.
 */
public interface FeedbackService {
    List<Feedback> selectAllFeedback() throws SQLException;

    PageInfo<Feedback> selectAllFeedback(int page_number, int page_size, String search_value) throws SQLException;

}
