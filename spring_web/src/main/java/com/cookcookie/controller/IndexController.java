package com.cookcookie.controller;

/**
 * Created by maoweidong on 2016/2/24.
 */

import com.cookcookie.bean.Person;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class IndexController {

    @RequestMapping("/index")
    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("hello");

        Person p1 = new Person();
        p1.setId(1);
        p1.setName("胡楠");
        p1.setPassword("123456");
        p1.setAge(24);
        p1.setBirthday(new Date());

        List<Person> list = new ArrayList<Person>();
        for (int i = 0; i < 10; i++) {
            Person p = new Person();
            p.setId(1000 + i);
            p.setName("胡楠" + i);
            p.setPassword("123456" + i);
            p.setAge(24 + i);
            p.setBirthday(new Date());
            list.add(p);
        }
        //集合
        mav.addObject("persons", list);
        //对象
        mav.addObject("person", p1);
        //request范围数据
        request.setAttribute("requestData", "hunan");
        //session范围数据
        request.getSession().setAttribute("sessionData", "123456");
        return mav;
    }

}
