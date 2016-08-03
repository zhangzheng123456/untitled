package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.FeedbackMapper;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.Feedback;
import com.bizvane.ishop.service.FeedbackService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/20.
 */
@Service
public class FeedbackServiceImpl implements FeedbackService{
    @Autowired
    private FeedbackMapper feedbackMapper;

    /***
     *
     *分页
     * @throws SQLException
     */
    @Override
    public PageInfo<Feedback> selectAllFeedback(int page_number, int page_size, String search_value) throws SQLException {
        PageHelper.startPage(page_number, page_size);
        List<Feedback> feedbacks = feedbackMapper.selectAllFeedback(search_value);
        for (Feedback feedback:feedbacks) {
            if(feedback.getIsactive().equals("Y")){
                feedback.setIsactive("是");
            }else{
                feedback.setIsactive("否");
            }
        }
        PageInfo<Feedback> page = new PageInfo<Feedback>(feedbacks);
        return page;
    }
//查询全部
    @Override
    public List<Feedback> selectAllFeedback() throws SQLException {
      return feedbackMapper.selectAllFeedback("");

    }

@Override
public PageInfo<Feedback> selectAllScreen(int page_number, int page_size, Map<String, String> map) {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("map", map);
    PageHelper.startPage(page_number, page_size);
    List<Feedback> list = feedbackMapper.selectAllScreen(params);
    for (Feedback feedback:list) {
        if(feedback.getIsactive().equals("Y")){
            feedback.setIsactive("是");
        }else{
            feedback.setIsactive("否");
        }
    }
    PageInfo<Feedback> page = new PageInfo<Feedback>(list);
    return page;
}
    //根据ID查询
@Override
    public Feedback selFeedbackById(int id) throws SQLException {
        return feedbackMapper.selFeedbackById(id);
    }
//删除
    @Override
    public int delFeedbackById(int id) throws SQLException {
        return feedbackMapper.delFeedbackById(id);
    }
    //修改
    @Override
    public int updFeedbackById(Feedback feedback) throws SQLException {
        return feedbackMapper.updFeedbackById(feedback);
    }
    //增加
    @Override
    //事务注解
    @Transactional
    public int addFeedback(Feedback feedback) throws SQLException {
        int i = feedbackMapper.addFeedback(feedback);
        //事务测试代码
//        int j=5/0;
//        if(i==1) {
//            System.out.println("------aaaaaa--------");
//        }else{
//            System.out.println("========bbbbbb=========");
//        }
        return i;
    }
}
