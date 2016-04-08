package com.cookcookie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by iBo on 15/12/28.
 */

@Controller
public class HelloController {
    @RequestMapping(value = "/greeting",method = RequestMethod.GET)
    public String greeting(@RequestParam(value="name", defaultValue="World") String name,Model ModelAndView) {

        System.out.println("Hello " + name);
        ModelAndView.addAttribute("message", name);
        return "hellouser";
    }
}


