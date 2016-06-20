package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.FeedbackMapper;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.Feedback;
import com.bizvane.ishop.service.FeedbackService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by yin on 2016/6/20.
 */
@Service
public class FeedbackServiceImpl implements FeedbackService{
    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public PageInfo<Feedback> selectAllFeedback(int page_number, int page_size, String search_value) throws SQLException {
        PageHelper.startPage(page_number, page_size);
        List<Feedback> feedbacks = feedbackMapper.selectAllFeedback(search_value);
        PageInfo<Feedback> page = new PageInfo<Feedback>(feedbacks);
        return page;
    }

    @Override
    public List<Feedback> selectAllFeedback() throws SQLException {
      return feedbackMapper.selectAllFeedback("");

    }
}
