package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yanyadong on 2016/11/23.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml",
        "classpath:spring-mybatis.xml"})
public class Test01 {


      @Autowired
      UserService service;


    @Test
    public  void showUser(){
        try {
            User user = service.getUserById(10219);
            System.out.println(user.getUser_code()+"  "+user.getPassword());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
