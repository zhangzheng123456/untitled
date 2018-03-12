package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.sun.v1.common.DataBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yanyadong on 2017/3/20.
 */
@Controller
@RequestMapping("/solr")
public class OperationSolrController {
    @Autowired
    IceInterfaceService iceInterfaceService;

    @RequestMapping(value = "/opera",method = RequestMethod.POST)
    @ResponseBody
    public String opera(HttpServletRequest request){
        DataBean dataBean=new DataBean();
      try{
          String jsString=  request.getParameter("message");
          JSONObject jsonObject= JSON.parseObject(jsString);
          String type= jsonObject.get("type").toString();
          String param= jsonObject.get("param").toString();

          System.out.println("param....."+param);

          DataBox dataBox=iceInterfaceService.operationBySolr(type,param);
          dataBean.setMessage(dataBox.data.get("message").value);

      }catch (Exception e){
          e.printStackTrace();
          dataBean.setMessage(e.getMessage());
      }
        return dataBean.getJsonStr();
    }
}
