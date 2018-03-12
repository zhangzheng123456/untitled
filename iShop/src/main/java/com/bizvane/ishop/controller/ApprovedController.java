package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Approved;
import com.bizvane.ishop.service.ApprovedService;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * Created by yanyadong on 2017/4/17.
 */
@Controller
@RequestMapping("/approved")
public class ApprovedController {

    @Autowired
    ApprovedService approvedService;
    @Autowired
    private BaseService baseService;

    String id="";

    @RequestMapping(value = "/select",method = RequestMethod.POST)
    @ResponseBody
    public String selectApproved(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            int approved_id=Integer.parseInt(jsonObject.get("id").toString());
            Approved approved=approvedService.selectById(approved_id);
            if(approved.getApproved_type().equals("Y")){
                String[] cycle=approved.getApproved_cycle().split(" ");
                approved.setApproved_cycle(cycle[4]+"-"+cycle[3]);
            }else{
                String[] cycle=approved.getApproved_cycle().split(" ");
                approved.setApproved_cycle(cycle[3]);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(JSON.toJSONString(approved));
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/search",method = RequestMethod.POST)
    @ResponseBody
    public String selectAllApproved(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();  //取公司code
            String role_code=request.getSession().getAttribute("role_code").toString();  //取会员code
            String jsString=request.getParameter("param").toString();   //取前端发过来的param值
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();       //取id值
            String message=jsonObject1.get("message").toString();   //取前端发过来message的值
            JSONObject jsonObject=JSON.parseObject(message);        //把message转换成JSONObject

//            int  page_number=Integer.parseInt(jsonObject.getString("pageNumber").toString());    //取出页数
//            int page_size=Integer.parseInt(jsonObject.getString("pageSize").toString());         //取出页数
            String search_value=jsonObject.getString("searchValue").toString();                  //取出搜索值
            PageInfo<Approved> list=null;

            if(role_code.equals(Common.ROLE_SYS)){          //判断是否是系统管理员
                list=approvedService.selectAll(1,Common.EXPORTEXECLCOUNT,"",search_value);

            }else{
                list=approvedService.selectAll(1,Common.EXPORTEXECLCOUNT,corp_code,search_value);

            }
            list=approvedService.switchApproved(list);
            JSONObject result=new JSONObject();
            result.put("list",JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/approvalDetails",method = RequestMethod.POST)
    @ResponseBody
    public String approvalDetails(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String jsString=request.getParameter("param").toString();   //取前端发过来的param值
        JSONObject jsonObject1= JSON.parseObject(jsString);
        String message=jsonObject1.get("message").toString();   //取前端发过来message的值
        JSONObject jsonObject=JSON.parseObject(message);
        String corp_code = jsonObject.getString("corp_code");
        int  approved_id=Integer.parseInt(jsonObject.getString("approved_id").toString());
        int  page_number=Integer.parseInt(jsonObject.getString("pageNumber").toString());    //取出页数
        int   page_size=Integer.parseInt(jsonObject.getString("pageSize").toString());         //取出页数
        String created_date = jsonObject.getString("created_date");
        //approved_id":"100","corp_code":"C10000","created_date":"2017-06-03","pageNumber":1,"pageSize":10
        try{
            JSONObject result=approvedService.approvalDetails(corp_code,approved_id,page_size,page_number,created_date);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }



    @RequestMapping(value = "/screenApprovedList",method = RequestMethod.POST)
    @ResponseBody
    public  String  screenApprovedList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=approvedService.screenApprovedList(message,role_code,corp_code);

            // JSONObject result=new JSONObject();
            //result.put("list",list);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(jsonObject.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }



    @RequestMapping(value = "/approvedList",method = RequestMethod.POST)
    @ResponseBody
    public String approvedList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String corp_code=request.getSession().getAttribute("corp_code").toString();
        String role_code=request.getSession().getAttribute("role_code").toString();  //取会员code
        if(role_code.equals(Common.ROLE_SYS)){
            corp_code="";
        }
        String jsString=request.getParameter("param").toString();   //取前端发过来的param值
        JSONObject jsonObject1= JSON.parseObject(jsString);
        String message=jsonObject1.get("message").toString();   //取前端发过来message的值
        JSONObject jsonObject=JSON.parseObject(message);
        try{
            JSONObject jsonObject2=approvedService.approvedList(jsonObject,corp_code);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(jsonObject2.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }

        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public String deleteApproved(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            String approved_ids=jsonObject.get("id").toString();
            String[] ids=approved_ids.split(",");
            for(int i=0;i<ids.length;i++){
                Approved approved=approvedService.selectById(Integer.valueOf(ids[i]));
                String corp=approved.getCorp_code();
                String name=approved.getApproved_name();
                String cycle=approved.getApproved_cycle();
                String remarks=approved.getRemarks();
                //----------------行为日志开始------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "会员管理_核准计划";
                String action = Common.ACTION_DEL;
                String t_corp_code = corp;
                String t_code = name;
                String t_name = cycle;
                String remark = remarks;
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

                approvedService.deleteById(Integer.parseInt(ids[i]));
            }
            // JSONObject result=new JSONObject();
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("success");


        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();

    }

    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    @ResponseBody
    public String insertApproved(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String user_code=request.getSession().getAttribute("user_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            Approved approved=approvedService.selectByApprovedName(jsonObject.getString("approved_name").toString(),
                    jsonObject.get("corp_code").toString());
            if(approved!=null){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("该核准计划的名称已存在");
                return dataBean.getJsonStr();
            }

            Approved approved1=new Approved();
            approved1.setApproved_name(jsonObject.get("approved_name").toString());
            approved1.setCorp_code(jsonObject.get("corp_code").toString());
            String quartz_value="";
            if(jsonObject.get("approved_type").toString().equals("Y")){
                String date=jsonObject.get("approved_cycle").toString();
                String[] dates=date.split("-");
                quartz_value="0 0 0"+" "+dates[1]+" "+dates[0]+" ?"+" *";
            }else if(jsonObject.get("approved_type").toString().equals("M")){
                String date=jsonObject.get("approved_cycle").toString();
                quartz_value="0 0 0"+" "+date+" "+"*"+" ?";
            }
            approved1.setApproved_cycle(quartz_value);
            approved1.setApproved_type(jsonObject.get("approved_type").toString());
            approved1.setRemarks(jsonObject.get("remarks").toString());
            //修改时间
            approved1.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            approved1.setModifier(user_code);
            //创建时间
            approved1.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
            approved1.setCreater(user_code);
            approved1.setIsactive(jsonObject.get("isactive").toString());

            approvedService.insertApproved(approved1);
            JSONObject result=new JSONObject();
            Approved approved2= approvedService.selectByApprovedName(jsonObject.getString("approved_name").toString(),jsonObject.get("corp_code").toString());
            result.put("id",approved2.getId());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

            //----------------行为日志------------------------------------------
            /**
             * mongodb插入用户操作记录
             * @param operation_corp_code 操作者corp_code
             * @param operation_user_code 操作者user_code
             * @param function 功能
             * @param action 动作
             * @param corp_code 被操作corp_code
             * @param code 被操作code
             * @param name 被操作name
             * @throws Exception
             */
            com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
            String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
            String operation_user_code = request.getSession().getAttribute("user_code").toString();
            String function = "会员管理_核准计划";
            String action = Common.ACTION_ADD;
            String t_corp_code = action_json.get("corp_code").toString();
            String t_code = action_json.get("approved_name").toString();
            String t_name = action_json.get("approved_cycle").toString();
            String remark = action_json.get("remarks").toString();
            baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
            //-------------------行为日志结束----

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public String updateApproved(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String user_code=request.getSession().getAttribute("user_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            Approved approved=approvedService.selectById(Integer.parseInt(jsonObject.get("id").toString()));

            if(!approved.getApproved_name().equals(jsonObject.get("approved_name").toString())||
                    !approved.getCorp_code().equals(jsonObject.get("corp_code").toString())){
                Approved approved1=approvedService.selectByApprovedName(jsonObject.get("approved_name").toString(),
                        jsonObject.get("corp_code").toString());
                if(approved1!=null){
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("该核准计划的名称已存在");
                    return dataBean.getJsonStr();
                }
            }

            Approved approved2=new Approved();
            approved2.setId(Integer.parseInt(jsonObject.get("id").toString()));
            approved2.setCorp_code(jsonObject.get("corp_code").toString());
            approved2.setApproved_name(jsonObject.get("approved_name").toString());
            String quartz_value="";
            if(jsonObject.get("approved_type").toString().equals("Y")){
                String date=jsonObject.get("approved_cycle").toString();
                String[] dates=date.split("-");
                quartz_value="0 0 0"+" "+dates[1]+" "+dates[0]+" ?"+" *";
            }else if(jsonObject.get("approved_type").toString().equals("M")){
                String date=jsonObject.get("approved_cycle").toString();
                quartz_value="0 0 0"+" "+date+" "+"*"+" ?";
            }
            approved2.setApproved_cycle(quartz_value);
            approved2.setApproved_type(jsonObject.get("approved_type").toString());
            approved2.setRemarks(jsonObject.get("remarks").toString());
            //获取当前时间
           approved2.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            approved2.setModifier(user_code);
            approved2.setIsactive(jsonObject.get("isactive").toString());
            approvedService.updateApproved(approved2);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("success");

            //----------------行为日志开始------------------------------------------
            /**
             * mongodb插入用户操作记录
             * @param operation_corp_code 操作者corp_code
             * @param operation_user_code 操作者user_code
             * @param function 功能
             * @param action 动作
             * @param corp_code 被操作corp_code
             * @param code 被操作code
             * @param name 被操作name
             * @throws Exception
             */
            com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
            String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
            String operation_user_code = request.getSession().getAttribute("user_code").toString();
            String function = "会员管理_核准计划 ";
            String action = Common.ACTION_UPD;
            String t_corp_code =  action_json.get("corp_code").toString();
            String t_code = action_json.get("approved_name").toString();
            String t_name = action_json.get("approved_cycle").toString();
            String remark = action_json.get("remarks").toString();
            baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
            //-------------------行为日志结束-----------------------------------------------------------------------------------

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/screen",method = RequestMethod.POST)
    @ResponseBody
    public  String  screenApproved(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            int  page_number=Integer.parseInt(jsonObject.getString("pageNumber").toString());
            int page_size=Integer.parseInt(jsonObject.getString("pageSize").toString());
            Map<String,Object> map = WebUtils.Json2Map(jsonObject);
            PageInfo<Approved> list=null;
            if(role_code.equals(Common.ROLE_SYS)){
                list=approvedService.selectAllScreen(page_number,page_size,"",map);
            }else{
                list=approvedService.selectAllScreen(page_number,page_size,corp_code,map);
            }
            list=approvedService.switchApproved(list);
            JSONObject result=new JSONObject();
            result.put("list",JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }
}
