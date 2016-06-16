package com.bizvane.ishop.service;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.*;
import com.bizvane.ishop.entity.*;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.SimpleAutowireCandidateResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.System;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 创建时间
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml",
        "classpath:spring-mybatis.xml"})
public class TestUserService {

    @Autowired
    private UserService userService = null;

    @Autowired
    private RoleService roleService = null;
    @Autowired
    private RoleMapper role = null;
    @Autowired
    private UserMapper userMapper = null;

    private SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);


//    @Autowired
//    private PrivilegeInfoMapper privilegeInfoMapper=null;

    @Autowired
    private GroupMapper groupMapper;

    @Test
    public void testInsert() {
        User user = new User();
        user.setUser_code("2222");
        user.setAvatar("22");
        user.setSex("n");
        user.setPhone("1232321343344");
        user.setEmail("185009439@qq.com");
        user.setPassword("123");
        user.setBirthday(sdf.format(new Date()));
        user.setCorp_code("C00001");
        user.setStore_code("11111");
    //    user.setGroup("22222");
        user.setLogin_time_recently(sdf.format(new Date()));
        user.setModified_date(sdf.format(new Date()));
        user.setModifier("1111");
        user.setCreated_date(sdf.format(new Date()));
        user.setCreater("1111");
        user.setIsactive("n");
        this.userMapper.insertUser(user);
    }

    @Test
    public void testSelectALlRole() {
        try {
            List<Role> list = role.selectAllRole("");
            System.out.print(list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
