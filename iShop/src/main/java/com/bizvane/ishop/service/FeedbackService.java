package com.bizvane.ishop.service;


import com.bizvane.ishop.entity.Feedback;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by yin on 2016/6/20.
 */
public interface FeedbackService {
    //查询全部
    List<Feedback> selectAllFeedback() throws SQLException;
    //分页查询
    PageInfo<Feedback> selectAllFeedback(int page_number, int page_size, String search_value) throws SQLException;
    //根据ID查询
    Feedback selFeedbackById(int id)throws SQLException;
    //根据ID删除
    int delFeedbackById(int id)throws  SQLException;
    //修改
    int updFeedbackById(Feedback feedback)throws  SQLException;
    //增加
    int addFeedback(Feedback feedback)throws  SQLException;


}
