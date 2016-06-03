package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.entity.UserAchvGoal;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.UserAchvGoalService;
import com.github.pagehelper.PageInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
@Controller
@RequestMapping("/userAchvGoal")
public class UserAchvGoalControl {
    private static org.slf4j.Logger logger=  LoggerFactory.getLogger(UserAchvGoalControl.class);

    @Autowired
    private UserAchvGoalService userAchvGoalService =null;
    @Autowired
    private FunctionService functionService=null;

    /**
     * 用户管理
     * @param request
     * @return
     */
    @RequestMapping(value="/list",method = RequestMethod.GET)
    @ResponseBody
    public String userAchvGoalManage(HttpServletRequest request){
            DataBean dataBean =new DataBean();
            try{
                int  user_id=Integer.parseInt(request.getParameter("user_id").toString());
                String role_code=request.getParameter("role_code").toString();
                String function_code=request.getParameter("funcode");

                int page_number=Integer.parseInt(request.getParameter("pageNumber").toString());
                int page_size =Integer.parseInt(request.getParameter("page_size").toString());
                JSONArray actions=this.functionService.selectActionByFun(user_id,role_code,function_code);
                JSONObject result=new JSONObject();
                PageInfo<UserAchvGoal> page=null;



        }catch(Exception ex){

        }
        return null;
    }


    public UserAchvGoal getUserAchvGoal(int id){
        return this.userAchvGoalService.getUserAchvGoalById(id);
    }

    public int updateUserAchvGoal(UserAchvGoal userAchvGoal){
         return this.userAchvGoalService.updateUserAchvGoal(userAchvGoal);
    }

    public int deleteUserAchvGoalById(int id){
        return this.userAchvGoalService.deleteUserAchvGoalById(id);
    }

    public int insertUserAchvGoal(UserAchvGoal userAchvGoal){
        return this.userAchvGoalService.insert(userAchvGoal);
    }
}
