//package com.bizvane.ishop.controller.v2;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.bizvane.ishop.bean.DataBean;
//import com.bizvane.ishop.constant.Common;
//import com.bizvane.ishop.constant.CommonValue;
//import com.bizvane.ishop.entity.Store;
//import com.bizvane.ishop.entity.User;
//import com.bizvane.ishop.service.BaseService;
//import com.bizvane.ishop.service.CorpAuthService;
//import com.bizvane.ishop.service.StoreService;
//import com.bizvane.ishop.service.UserService;
//import com.bizvane.ishop.service.imp.CorpAuthServiceImpl;
//import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
//import com.bizvane.ishop.utils.MongoUtils;
//import com.bizvane.ishop.utils.OutExeclHelper;
//import com.bizvane.ishop.utils.TimeUtils;
//import com.bizvane.ishop.utils.WebUtils;
//import com.bizvane.sun.common.service.mongodb.MongoDBClient;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mongodb.*;
//import org.apache.log4j.Logger;
//import org.bson.types.ObjectId;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.stereotype.Controller;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.regex.Pattern;
//
///**
// * Created by zhou on 2017/7/31.
// */
//@Controller
//@RequestMapping("/Auth")
//public class JDAuthController {
//    @Autowired
//    private CorpAuthService corpAuthService;
//
//    String id;
//
//    private static final Logger logger = Logger.getLogger(CorpAuthServiceImpl.class);
//
//
//    /**
//     * 页面查找
//     */
//    @RequestMapping(value = "/JD/authorize", method = RequestMethod.GET)
//    public void JDAuthorize(HttpServletRequest request,HttpServletResponse response) {
//        try {
//            String code = request.getParameter("code");
//            logger.info("=========code:"+code);
//            corpAuthService.getAccessToken(code);
//
//            response.sendRedirect("/login.html");
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * 页面查找
//     */
//    @RequestMapping(value = "/JD/api", method = RequestMethod.POST)
//    @ResponseBody
//    public String JDApi(HttpServletRequest request,HttpServletResponse response) {
//        DataBean dataBean = new DataBean();
//        try {
//            String jsString = request.getParameter("param");
//            logger.info("======="+jsString);
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject msg_obj = JSONObject.parseObject(message);
//            String method = msg_obj.getString("method");
//            String u_id = msg_obj.getString("uid");
//            String param = "";
//            if (msg_obj.containsKey("param"))
//                param = msg_obj.get("param").toString();
//
//            JSONObject obj =  corpAuthService.JDApi(method, u_id,param);
//
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setMessage(obj.toString());
//        }catch (Exception e){
//            e.printStackTrace();
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage("");
//        }
//        return dataBean.getJsonStr();
//    }
//
//
//}
