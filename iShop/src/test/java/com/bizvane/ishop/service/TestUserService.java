package com.bizvane.ishop.service;

import com.bizvane.ishop.dao.*;
import com.bizvane.ishop.entity.*;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.System;
import java.sql.SQLException;
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


//    @Autowired
//    private PrivilegeInfoMapper privilegeInfoMapper=null;

    @Autowired
    private GroupMapper groupMapper;

    @Test
    public void testInsert() {
        User user = new User();
        user.setCreater("1111");
        user.setCreated_date(new Date().toString());
        user.setAvatar("111");
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
