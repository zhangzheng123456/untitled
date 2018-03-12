//package com.bizvane.ishop.controller;
//
//import com.alibaba.fastjson.JSONObject;
//import com.bizvane.ishop.bean.DataBean;
//import com.bizvane.ishop.constant.Common;
//import com.bizvane.ishop.service.VipTaskAnalyService;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * Created by gyy on 2017/11/23.
// */
//@Controller
//@RequestMapping("/vipTaskAnaly")
//public class VipTaskAnalyController {
//    @Autowired
//    private VipTaskAnalyService vipTaskAnalyService;
//
//    private static final Logger logger = Logger.getLogger(VipTaskAnalyController.class);
//
//    //---------邀请注册任务---------
//    //邀请注册分析占比 (邀请注册成功率) 总分享人数 总分享次数 总注册人数
//    @RequestMapping(value = "/inviteCount",method = RequestMethod.POST)
//    @ResponseBody
//    public String getInviteProportion(HttpServletRequest request){
//        String id="";
//        DataBean dataBean=new DataBean();
//        try{
//            String jsString=request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject result= vipTaskAnalyService.getShareAndRegistCount(message);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        }catch(Exception ex){
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return  dataBean.getJsonStr();
//    }
//    //邀请注册人数统计图表
//    @RequestMapping(value = "/view",method = RequestMethod.POST)
//    @ResponseBody
//    public String getInviteGraph(HttpServletRequest request){
//        DataBean dataBean=new DataBean();
//        String id="";
//        try{
//            String jsString=request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            //总分享人数的折线图
//            JSONObject sharePeople= vipTaskAnalyService.getSharePeopleByDate(message);
//            //总分享次数的折线图
//            JSONObject shareCount= vipTaskAnalyService.getShareCountByDate(message);
//            //总注册人数的折线图
//            JSONObject registPeople= vipTaskAnalyService.getRegistPeopleByDate(message);
//
//            JSONObject result=new JSONObject();
//            result.put("shareCount",shareResult.get("count").toString());
//            result.put("openCount",clickResult.get("count").toString());
//            result.put("shareList",shareResult.get("list"));
//            result.put("clickList",clickResult.get("list"));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//
//        }catch (Exception ex){
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return  dataBean.getJsonStr();
//    }
//    //邀请注册会员列表
//    @RequestMapping(value = "/userList",method = RequestMethod.POST)
//    @ResponseBody
//    public String getUserList(HttpServletRequest request){
//        DataBean dataBean=new DataBean();
//        String id="";
//        try{
//            String jsString=request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject result= vipTaskAnalyService.getUserList(message);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        }catch (Exception ex){
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//    //----------完善资料----------
//    //完善资料任务完成率占比 参与总数 完成总数
//    @RequestMapping(value = "/materialCompletion",method = RequestMethod.POST)
//    @ResponseBody
//    public String getMaterialCompletion(HttpServletRequest request){
//        String id="";
//        DataBean dataBean=new DataBean();
//        try{
//            String jsString=request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject result= vipTaskAnalyService.getMaterialCompletion(message);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        }catch(Exception ex){
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return  dataBean.getJsonStr();
//    }
//    //完善资料完成人数统计图表  完成人数  参与人数
//    @RequestMapping(value = "/materialView",method = RequestMethod.POST)
//    @ResponseBody
//    public String getInviteGraph(HttpServletRequest request){
//        DataBean dataBean=new DataBean();
//        String id="";
//        try{
//            String jsString=request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            //完成人数的折线图
//            JSONObject sharePeople= vipTaskAnalyService.getMaterialOkByDate(message);
//            //参与的折线图
//            JSONObject shareCount= vipTaskAnalyService.getMaterialJoinByDate(message);
//
//            JSONObject result=new JSONObject();
//            result.put("shareCount",shareResult.get("count").toString());
//            result.put("openCount",clickResult.get("count").toString());
//            result.put("shareList",shareResult.get("list"));
//            result.put("clickList",clickResult.get("list"));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//
//        }catch (Exception ex){
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return  dataBean.getJsonStr();
//    }
//    //完善资料列表（参与完善资料活动的会员）
//    @RequestMapping(value = "/materialList",method = RequestMethod.POST)
//    @ResponseBody
//    public String getMaterialList(HttpServletRequest request){
//        DataBean dataBean=new DataBean();
//        String id="";
//        try{
//            String jsString=request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject result= vipTaskAnalyService.getMaterialList(message);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        }catch (Exception ex){
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//    //----------累计消费次数----------
//    //累计消费次数的任务完成率 目标会员数 完成会员数
//    @RequestMapping(value = "/consumeCountCompletion",method = RequestMethod.POST)
//    @ResponseBody
//    public String getConsumeCountCompletion(HttpServletRequest request){
//        String id="";
//        DataBean dataBean=new DataBean();
//        try{
//            String jsString=request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject result= vipTaskAnalyService.getConsumeCountCompletion(message);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        }catch(Exception ex){
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return  dataBean.getJsonStr();
//    }
//    //累计消费次数统计图表  累计达成人数
//    @RequestMapping(value = "/ConsumeCountView",method = RequestMethod.POST)
//    @ResponseBody
//    public String getConsumeCount(HttpServletRequest request){
//        DataBean dataBean=new DataBean();
//        String id="";
//        try{
//            String jsString=request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            //累计完成人数的折线图
//            JSONObject sharePeople= vipTaskAnalyService.getConsumeOkByDate(message);
//            JSONObject result=new JSONObject();
//            result.put("shareCount",shareResult.get("count").toString());
//            result.put("openCount",clickResult.get("count").toString());
//            result.put("shareList",shareResult.get("list"));
//            result.put("clickList",clickResult.get("list"));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//
//        }catch (Exception ex){
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return  dataBean.getJsonStr();
//    }
//    //累计消费次数会员列表（列表显示完成任务的会员信息  没有完成的不显示）
//    //根据任务类型判断显示列表内容，如果有时间范围，“消费次数”显示“时间段内消费次数”
//    @RequestMapping(value = "/consumeCountList",method = RequestMethod.POST)
//    @ResponseBody
//    public String getConsumeCountList(HttpServletRequest request){
//        DataBean dataBean=new DataBean();
//        String id="";
//        try{
//            String jsString=request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject result= vipTaskAnalyService.getConsumeCountList(message);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        }catch (Exception ex){
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//
//
//
//}
