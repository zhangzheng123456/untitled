package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.entity.Feedback;
import com.bizvane.ishop.entity.Interfacers;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
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
    @Autowired
    private AppversionService appversionService=null;
    @Autowired
    private InterfaceService interfaceService=null;
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
    //成功
    @Test
    public void testUpdate(){
        try {
            Feedback fb=new Feedback();
            fb.setUser_code("呵呵哒");
            fb.setId(1);
            int i = feedbackService.updFeedbackById(fb);
            System.out.println(i+"-----upd----");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //成功
    @Test
    public void testDel(){
        try {
            int i = feedbackService.delFeedbackById(4);
            System.out.println(i+"----del--");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    //成功
    @Test
    public void testAppList(){
        try {
            List<Appversion> appversions = appversionService.selectAllAppversion();
            System.out.println(appversions.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //成功
    @Test
    public void testAppUpd(){
        try {
            Appversion appversion=new Appversion();
             appversion.setId(1);
            appversion.setModified_date("2017/1/2");
            int i = appversionService.updAppversionById(appversion);
            System.out.println(i+"----upd--");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //成功
    @Test
    public void testfaceAdd()  {
        Interfacers interfacers=new Interfacers();
        interfacers.setModified_date("2010/2/4");
        interfacers.setModifier("1");
        interfacers.setCreater("1");
        interfacers.setCreated_date("2010/2/3");
        interfacers.setCrop_code("222");
        interfacers.setIsactive("1");
        interfacers.setVersion("安卓4.7");
        int i = 0;
        try {
            i = interfaceService.addInterface(interfacers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(i+"-----faceAdd-----");
    }
    //成功
    @Test
    public void TestfaceList(){
        try {
            Interfacers interfacers = interfaceService.selInterfaceById(1);
            System.out.println(interfacers.getVersion());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testfaceUpd() throws SQLException {
        Interfacers interfacers=new Interfacers();
        interfacers.setVersion("IOS 10");
        interfacers.setId(1);
        int i = interfaceService.updInterfaceById(interfacers);
        System.out.println(i+"----faceUpd-------");
    }

}
