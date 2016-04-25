package com.bizvane.ishop.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
    private static final String SUCCESS = "success";
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String printWelcome() {
        return "daogou_index";
    }






//    @RequestMapping("/user")
//    public ModelAndView Login(UserInfo user,HttpSession session) {
//      ModelAndView mav=new ModelAndView();
//      if (!(user.getCode().equalsIgnoreCase(session.getAttribute("code").toString()))) {  //忽略验证码大小写
//        mav.setViewName("error");
//        mav.addObject("msg","验证码不正确");
//        return mav;
//      }else {
//        user.setId(UUID.randomUUID().toString());
//        try {
//          mav.setViewName("success");
//          mav.addObject("user", user);
//          mav.addObject("msg", "登录成功");
//          return mav;
//        } catch (Exception e) {
//          mav.setViewName("menu");
//          mav.addObject("user", null);
//          mav.addObject("msg", "登录成功");
//          return mav;
//        }
//      }
//    }
}