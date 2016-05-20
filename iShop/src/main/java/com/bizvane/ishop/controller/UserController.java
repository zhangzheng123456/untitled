package com.bizvane.ishop.controller;

import com.bizvane.ishop.service.UserService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by maoweidong on 2016/2/15.
 */

/*
*用户及权限
*/
@Controller
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger((UserController.class));

    @Autowired
    private UserService userService;
    /**
     * 用户管理
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public String userManage(HttpServletRequest request) {
        return "user";
    }

    /**
     * 用户管理
     * 新增
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addUser(HttpServletRequest request) {
        return "user_add";
    }

    /**
     * 用户管理
     * 编辑
     */
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    @ResponseBody
    public String editUser(HttpServletRequest request) {
        return "user_edit";
    }

    /**
     * 用户管理
     * 查找
//     */
//    @RequestMapping(value = "/find",method = RequestMethod.POST)
//    @ResponseBody
//    public String findUser(HttpServletRequest request) {
//        return "";
//    }

//        @RequestMapping("/showInfo/{userId}")
//        public String showUserInfo(ModelMap modelMap, @PathVariable int userId){
//            UserInfo userInfo = userService.getUserById(userId);
//            modelMap.addAttribute("userInfo", userInfo);
//            return "";
//        }
//
//        @RequestMapping("/showInfos")
//        public @ResponseBody Object showUserInfos(){
//           List<UserInfo> userInfo = userService.getUsers();
//            return userInfo;
//        }

}
