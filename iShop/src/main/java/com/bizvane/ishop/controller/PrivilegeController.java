package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Function;
import com.bizvane.ishop.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Administrator on 2016/6/1.
 */
@Controller
@RequestMapping("/privilege")
public class PrivilegeController {

    @Autowired
    private FunctionService functionService;
    @RequestMapping(value = "/list" , method = RequestMethod.GET)
    public String showPrivilege(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String user_id = request.getSession().getAttribute("user_id").toString();

            JSONArray function = functionService.selectAllFunctions(Integer.parseInt(user_id),role_code);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(function.toString());
        }catch (Exception ex){
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

}
