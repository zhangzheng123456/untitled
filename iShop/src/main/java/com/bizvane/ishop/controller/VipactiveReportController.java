package com.bizvane.ishop.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/activeReport")
public class VipactiveReportController {
    private static final org.apache.log4j.Logger logger = Logger.getLogger(VipactiveReportController.class);


    @RequestMapping("/initData")
    public String initData(){

        return "";
    }
}
