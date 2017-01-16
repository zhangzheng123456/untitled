package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yanyadong on 2016/11/23.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml",
        "classpath:spring-mybatis.xml"})
public class Test01 {


      @Autowired
      UserService service;
    @Autowired
    IceInterfaceService iceInterfaceService;

    @Test
    public  void showUser(){
        try {
//            User user = service.getUserById(10219);
//            System.out.println(user.getUser_code()+"  "+user.getPassword());

            JSONArray array = new JSONArray();

            JSONObject obj = new JSONObject();
            obj.put("corp_code","11");
            obj.put("vip_id","22");
            obj.put("vip_iname","22");
            array.add(obj);

            obj.put("corp_code","11");
            obj.put("vip_id","22");
            obj.put("vip_iname","22");
            array.add(obj);

            System.out.println("======"+ JSON.toJSONString(array));


        }
        catch(Exception e){
            e.printStackTrace();
        }
    }



}
