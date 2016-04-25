package com.bizvane.ishop.service;

import com.bizvane.ishop.bean.UserInfo;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * 创建时间：2015-1-27 下午10:45:38
 * 
 * @author andy
 * @version 2.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml",
		"classpath:spring-mybatis.xml" })
public class TestUserService {

	private static final Logger LOGGER = Logger
			.getLogger(TestUserService.class);

	@Autowired
	private UserService userService;

	/*
	 * @Test public void testQueryById() { ApplicationContext ac = new
	 * ClassPathXmlApplicationContext( new String[] { "spring.xml",
	 * "spring-mybatis.xml" }); UserService userService = (UserService)
	 * ac.getBean("userService"); UserInfo userInfo =
	 * userService.getUserById(1); System.out.println(userInfo.getUname()); }
	 */

	@Test
	public void testQueryById1() {
		System.out.println("11111111");
		UserInfo userInfo = userService.getUserById(1);
	}

	@Test
	public void testQueryAll() {
		System.out.println("22222222");
		List<UserInfo> userInfos = userService.getUsers();
	}

	@Test
	public void testInsert() {
		System.out.println("333333333");
		UserInfo userInfo = new UserInfo();
		userInfo.setUname("xiaoming");
		userInfo.setUnumber(4);
		int result = userService.insert(userInfo);
		System.out.println(result);
	}
}
