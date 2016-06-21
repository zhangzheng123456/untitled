package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Feedback;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by yin on 2016/6/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml",
        "classpath:spring-mybatis.xml" })
public class TestFeedbackService {
    @Autowired
    private FeedbackService feedbackService = null;
    //成功
    @Test
    public void testselectAllFeedback() {
        try {
            List<Feedback> flist=feedbackService.selectAllFeedback();
            System.out.println(flist.size());
        }catch (Exception x){
            x.printStackTrace();
        }

    }
    //成功
    @Test
    public void testAddFeedback(){
        try {
            Feedback f=new Feedback();
            f.setUser_code("1");
            f.setIsactive("1");
            f.setModifier("1");
            f.setModified_date("2015/6/3");
            f.setFeedback_content("1");
            f.setPhone("1");
            f.setProcess_state("1");
            f.setCreater("1");
            f.setFeedback_date("2015/6/3");
            f.setCreated_date("2015/6/3");
            feedbackService.addFeedback(f);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //成功
    @Test
    public void testSelFeedback(){
        try {
            Feedback feedback = feedbackService.selFeedbackById(1);
            System.out.println(feedback.getPhone()+"----------");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    }
