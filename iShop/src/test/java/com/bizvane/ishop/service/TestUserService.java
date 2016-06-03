package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.LoginLog;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.ValidateCode;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.Date;

/**
 * 创建时间
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml",
        "classpath:spring-mybatis.xml" })
public class TestUserService {

    @Autowired
    private UserService userService=null;

    @Test
    public void testInsert(){
        User user=new User();
        user.setCreater("1111");
        user.setCreated_date(new Date().toString());

        user.setAvatar("111");



    }

}
