package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.entity.CorpInfo;
import com.bizvane.ishop.entity.LogInfo;
import com.bizvane.ishop.entity.UserInfo;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.Date;
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
    private UserService logService;

	/*
	 * @Test public void testQueryById()
	 * {
	 * ApplicationContext ac = new
	 * ClassPathXmlApplicationContext( new String[] { "spring.xml",
	 * "spring-mybatis.xml" });
	  * UserService userService = (UserService)
	 * ac.getBean("userService");
	  * UserInfo userInfo =
	 * userService.getUserById(1);
	  * System.out.println(userInfo.getUsername()); }
	 */

    @Test
    public void updateShopInfo(){
        try {
            UserInfo user=this.logService.getUserById(12);
           user.setCreated_date(new Date(1));
            this.logService.update(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void testQueryById1() throws SQLException{
        System.out.println("11111111");
        //	LogInfo logInfo = logService.selectLog(0,"222");
        Date now = new Date();
        UserInfo user = new UserInfo();
        user.setUser_name("222");
        user.setPhone("222");
        user.setPassword("333");
        user.setCreated_date(now);
        user.setModified_date(now);
        logService.insert(user);

//		Gson gson = new Gson();
//		String reslut = gson.toJson(userInfo1);
//		JSONArray array = JSONArray.parseArray(reslut);
//		String i = array.get(0).toString();
        //System.out.println(i);

    }
//	@Test
//	public void queryByPageTest(){
//		System.out.println("11111111");
//		int i = userService.delete(4);
//		System.out.println(i);
//	}

//	@Test
//	public void testInsert() {
//		System.out.println("333333333");
//		String phone = "222";
//		String password = "222";
//		UserInfo user = userService.phoneExist(phone);
//		if(user==null) {
//			user = new UserInfo();
//			user.setPhone(phone);
//			user.setPassword(password);
//			System.out.println("insert---------");
//			int result = userService.insert(user);
//		}
//		else{
//			user.setPhone(phone);
//			user.setPassword(password);
//			System.out.println("update---------");
//			int result = userService.insert(user);
//		}
//		System.out.println(result);
//	}
}
