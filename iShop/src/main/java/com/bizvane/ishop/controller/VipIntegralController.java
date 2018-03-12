package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.VipIntegral;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.ScheduleJobService;
import com.bizvane.ishop.service.VipGroupService;
import com.bizvane.ishop.service.VipIntegralService;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by yanyadong on 2017/4/7.
 */
@Controller
@RequestMapping("/vipIntegral")
public class VipIntegralController {
    @Autowired
    VipIntegralService vipIntegralService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private MongoDBClient mongoDBClient;
    @Autowired
    ScheduleJobService scheduleJobService;
    @Autowired
    VipGroupService vipGroupService;

    String id="";

    private static final Logger logger = Logger.getLogger(VipIntegralController.class);

    @RequestMapping(value = "/select",method = RequestMethod.POST)
    @ResponseBody
    public String selectIntegral(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            int integral_id=Integer.parseInt(jsonObject.get("id").toString());
            VipIntegral vipIntegral=vipIntegralService.selectIntegralById(integral_id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(JSON.toJSONString(vipIntegral));
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
    public String selectAllIntegral(HttpServletRequest request){
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
            String search_value=jsonObject.getString("searchValue").toString();
            PageInfo<VipIntegral> list;
            if(role_code.equals(Common.ROLE_SYS)){
                list=vipIntegralService.selectIntegralAll(page_number,page_size,"",search_value);
            }else{
                list=vipIntegralService.selectIntegralAll(page_number,page_size,corp_code,search_value);
            }
            list=vipIntegralService.switchList(list);

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

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public String deleteIntegral(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            String integral_id=jsonObject.get("id").toString();
            String[] ids=integral_id.split(",");

            for(int i=0;i<ids.length;i++){
                VipIntegral vipIntegral=vipIntegralService.selectIntegralById(Integer.parseInt(ids[i]));
                if (vipIntegral != null){
                    scheduleJobService.deleteScheduleByGroup(vipIntegral.getBill_no());

                    vipIntegralService.deleteIntegralById(Integer.parseInt(ids[i]));
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
                    String function = "会员管理_积分清零";
                    String action = Common.ACTION_DEL;
                    String t_corp_code = vipIntegral.getCorp_code();
                    String t_code = "";
                    String t_name = vipIntegral.getIntegral_name();
                    String remark = "";
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                    //-------------------行为日志结束-----------------------------------------------------------------------------------
                }
            }
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

    @RequestMapping(value = "/screen",method = RequestMethod.POST)
    @ResponseBody
    public  String  screenIntegral(HttpServletRequest request){
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
            PageInfo<VipIntegral> list;
            if(role_code.equals(Common.ROLE_SYS)){
                list= vipIntegralService.selectIntegralAllScreen(page_number,page_size,"",map);
            }else{
                list=vipIntegralService.selectIntegralAllScreen(page_number,page_size,corp_code,map);
            }
            list=vipIntegralService.switchList(list);

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

    //插入
    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    @ResponseBody
    public String insertIntegral(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String role_code=request.getSession().getAttribute("role_code").toString();
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            String area_code = request.getSession().getAttribute("area_code").toString();
            String store_code = request.getSession().getAttribute("store_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            VipIntegral vipIntegral=vipIntegralService.selectIntegralByName(jsonObject.getString("corp_code").toString(),
                    jsonObject.get("integral_name").toString());
            if(vipIntegral!=null){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("该名称已存在");
                return dataBean.getJsonStr();
            }

            VipIntegral newVipIntegral=new VipIntegral();
            newVipIntegral.setCorp_code(jsonObject.getString("corp_code").toString());
            newVipIntegral.setIntegral_name(jsonObject.getString("integral_name").toString());
            //生成bill_no
            String corpCode=jsonObject.getString("corp_code").toString();
            String bill_no="VM"+corpCode+Common.DATETIME_FORMAT.format(new Date());
              bill_no=bill_no.replaceAll("-","").replaceAll(" ","").replaceAll(":","");
            newVipIntegral.setBill_no(bill_no);

            String target_vips = jsonObject.get("target_vips").toString();
            newVipIntegral.setTarget_vips(target_vips);
            JSONArray array = JSONArray.parseArray(target_vips);
            array = vipGroupService.vipScreen2ArrayNew(array,corpCode,role_code,brand_code,area_code,store_code,user_code);
            newVipIntegral.setTarget_vips_(JSON.toJSONString(array));
            newVipIntegral.setTarget_vip_type("part_new");
            if (target_vips.equals("[]")){
                newVipIntegral.setTarget_vip_type("all");
            }
            newVipIntegral.setIntegral_duration(jsonObject.get("integral_duration").toString());
            newVipIntegral.setClear_cycle(jsonObject.get("clear_cycle").toString());
            newVipIntegral.setRemind(jsonObject.get("remind").toString());
            newVipIntegral.setRemarks(jsonObject.get("remarks").toString());
            //修改时间
            newVipIntegral.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            newVipIntegral.setModifier(user_code);
            //创建时间
            newVipIntegral.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
            newVipIntegral.setCreater(user_code);
            newVipIntegral.setIsactive(jsonObject.get("isactive").toString());
            newVipIntegral.setClear_type(jsonObject.get("clear_type").toString());
            newVipIntegral.setApp_id(jsonObject.get("app_id").toString());

            String status = vipIntegralService.insertVipIntegral(newVipIntegral,user_code,group_code,role_code);
            if (status.equals(Common.DATABEAN_CODE_SUCCESS)){
                JSONObject result=new JSONObject();
                VipIntegral vipIntegral1= vipIntegralService.selectIntegralByName(jsonObject.getString("corp_code").toString(),jsonObject.get("integral_name").toString());
                result.put("id",vipIntegral1.getId());
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
                String function = "会员管理_积分清零";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = "";
                String t_name = action_json.get("integral_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束----

            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage(status);
            }
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    //修改
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public String updateIntegral(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String role_code=request.getSession().getAttribute("role_code").toString();
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            String area_code = request.getSession().getAttribute("area_code").toString();
            String store_code = request.getSession().getAttribute("store_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            VipIntegral vipIntegral=vipIntegralService.selectIntegralById(Integer.parseInt(jsonObject.get("id").toString()));

            if(!vipIntegral.getIntegral_name().equals(jsonObject.get("integral_name").toString())){
                VipIntegral vipIntegral1=vipIntegralService.selectIntegralByName(vipIntegral.getCorp_code(),
                        jsonObject.get("integral_name").toString());
                if(vipIntegral1!=null){
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("该名称已存在");
                    return dataBean.getJsonStr();
                }
            }

            String old_reminds = vipIntegral.getRemind();
            JSONArray old_array = JSONArray.parseArray(old_reminds);

            String new_reminds = jsonObject.get("remind").toString();
            JSONArray new_array = JSONArray.parseArray(new_reminds);

            JSONArray array = new JSONArray();
            if (new_array.size() > old_array.size()){
                for (int i = 0; i < new_array.size(); i++) {
                    if (i < old_array.size()){
                        array.add(new_array.get(i));
                    }else {
                        JSONObject obj = new_array.getJSONObject(i);
                        obj.put("new","");
                        array.add(obj);
                    }
                }
            }else {
                array = new_array;
            }

            String corpCode = jsonObject.get("corp_code").toString();

            String target_vips = jsonObject.get("target_vips").toString();
            JSONArray array_vip = JSONArray.parseArray(target_vips);
            array_vip = vipGroupService.vipScreen2ArrayNew(array_vip,corpCode,role_code,brand_code,area_code,store_code,user_code);
            vipIntegral.setTarget_vips(target_vips);
            vipIntegral.setTarget_vips_(JSON.toJSONString(array_vip));

            vipIntegral.setCorp_code(corpCode);
            vipIntegral.setIntegral_name(jsonObject.get("integral_name").toString());
            vipIntegral.setRemarks(jsonObject.get("remarks").toString());
            //获取当前时间
            vipIntegral.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            vipIntegral.setModifier(user_code);
            vipIntegral.setIsactive(jsonObject.get("isactive").toString());
            vipIntegral.setIntegral_duration(jsonObject.get("integral_duration").toString());
            vipIntegral.setClear_cycle(jsonObject.get("clear_cycle").toString());
            vipIntegral.setRemind(array.toJSONString());
            vipIntegral.setClear_type(jsonObject.get("clear_type").toString());
            vipIntegral.setApp_id(jsonObject.get("app_id").toString());

            vipIntegralService.updateVipIntegral(vipIntegral,user_code,group_code,role_code);
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
            String function = "会员管理_积分清零 ";
            String action = Common.ACTION_UPD;
            String t_corp_code = action_json.get("corp_code").toString();
            String t_code = "";
            String t_name = action_json.get("integral_name").toString();
            String remark = "";
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

    //清理记录
    @RequestMapping(value = "/cleanLog",method = RequestMethod.POST)
    @ResponseBody
    public String cleanLog(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String user_code=request.getSession().getAttribute("user_code").toString();
        MongoTemplate template = mongoDBClient.getMongoTemplate();
        DBCollection collection = template.getCollection(CommonValue.table_vip_integral_modify_log);
        try{
            JSONObject result = new JSONObject();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String bill_no = jsonObject.getString("bill_no");
            String corp_code = jsonObject.getString("corp_code");
            int page_num = jsonObject.getInteger("page_num");
            int page_size = jsonObject.getInteger("page_size");
            String search_value = jsonObject.getString("search_value");

            String[] column_names = new String[]{"vip.vip_name","vip.cardno","vip.vip_phone"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names,search_value);

            int pages = 0;
            BasicDBObject dbObject = new BasicDBObject();
            BasicDBList value = new BasicDBList();
            value.add(new BasicDBObject("bill_no",bill_no));
            value.add(queryCondition);
            dbObject.put("$and", value);
            DBCursor coursor = collection.find(dbObject);
            int total = coursor.count();
            pages = MongoUtils.getPages(coursor,page_size);
            DBCursor dbCursor = MongoUtils.sortAndPage(coursor,page_num,page_size,"operation_time",-1);

            ArrayList list = MongoUtils.dbCursorToList(dbCursor);

            result.put("list",list);
            result.put("page_num",page_num);
            result.put("page_size",page_size);
            result.put("pages",pages);
            result.put("total",total);

            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
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
