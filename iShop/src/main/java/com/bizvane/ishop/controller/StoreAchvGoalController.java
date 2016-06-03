package com.bizvane.ishop.controller;

import com.bizvane.ishop.dao.StoreAchvGoalMapper;
import com.bizvane.ishop.entity.StoreAchvGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
@Controller
@RequestMapping("/storeAchvGoal")
public class StoreAchvGoalController {

    private static Logger logger = LoggerFactory.getLogger((UserController.class));

    @Autowired
    StoreAchvGoalMapper storeAchvGoalMapper=null;

    /**
     * 用户管理
     * @param request
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public String getAllUser(HttpRequest request){
        return null;
    }

    public StoreAchvGoal getStoreAchvGoalById(int id ){
        return storeAchvGoalMapper.selectlById(id);
    }

    public int insert(StoreAchvGoal storeAchvGoal){
        return storeAchvGoalMapper.insert(storeAchvGoal);
    }

    public int delete( int id ){
        return storeAchvGoalMapper.deleteById(id);
    }

    public int update(StoreAchvGoal storeAchvGoal){
        return storeAchvGoalMapper.update(storeAchvGoal);
    }


}
