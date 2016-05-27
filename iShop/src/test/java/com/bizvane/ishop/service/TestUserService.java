package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.LoginLog;
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
    @Autowired
    private FunctionService functionService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private LoginLogService validateCodeService;



    @Test
    public void testQueryById1() throws SQLException{
        System.out.println("11111111");
        //	LoginLog logInfo = logService.selectLog(0,"222");
//        Date now = new Date();
//        UserInfo user = new UserInfo();
//        user.setUser_name("222");
//        user.setPhone("222");
//        user.setPassword("333");
//        user.setCreated_date(now);
//        user.setModified_date(now);
//        logService.insert(user);;
        LoginLog code = validateCodeService.selectLoginLog(0,"000");
        Date now = new Date();
        if(code==null) {
            code = new LoginLog();
            code.setContent("123456");
            code.setPhone("111");
            code.setPlatform("web register");
            code.setCreated_date(now);
            code.setModified_date(now);
            code.setCreater("root");
            code.setModifier("root");
            validateCodeService.insertLoginLog(code);
        }else{
            code.setContent("111111");
            code.setModified_date(now);
            code.setModifier("root");
            code.setPlatform("网页注册");
            validateCodeService.updateLoginLog(code);
        }
        System.out.println("-----"+code.getPhone());
//        List<CorpInfo> corpInfo = corpService.selectAllCorp("1");
//        for (int i=0;i<corpInfo.size();i++) {
//            System.out.println(corpInfo.get(i).getCorp_code());
//        }

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
