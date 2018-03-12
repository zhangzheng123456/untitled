package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Questionnaire;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.QuestionnaireService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static jxl.biff.FormatRecord.logger;

/**
 * Created by yanyadong on 2017/8/29.
 */
@Controller
@RequestMapping("/questionnaire")
public class QuestionnaireController {

    @Autowired
    QuestionnaireService questionnaireService;

    @Autowired
    BaseService baseService;

    String id="";

    @RequestMapping(value = "/select",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String  selectQtnaire(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
       //     String corp_code=request.getSession().getAttribute("corp_code").toString();
       //     String role_code=request.getSession().getAttribute("role_code").toString();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            int question_id=Integer.parseInt(jsonObject.get("id").toString());
            Questionnaire questionnaire=questionnaireService.selectById(question_id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(JSON.toJSONString(questionnaire));
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/search",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String searchQtNaire(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            int  page_number=Integer.parseInt(jsonObject.getString("page_num").toString());
            int page_size=Integer.parseInt(jsonObject.getString("page_size").toString());
            String search_value=jsonObject.getString("searchValue").toString();
            PageInfo<Questionnaire> list=null;
            if(role_code.equals(Common.ROLE_SYS)){
                list=questionnaireService.selectAll(page_number,page_size,"",search_value);
            }else{
                list=questionnaireService.selectAll(page_number,page_size,corp_code,search_value);
            }
            //转换字段
            list= (PageInfo<Questionnaire>) questionnaireService.switchQtNaire(list);
            //获取答卷数量
            list= (PageInfo<Questionnaire>) questionnaireService.getQtNaireNum(list,new JSONObject());
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

    @RequestMapping(value = "/delete",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String deleteQtnaire(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            String question_id=jsonObject.get("id").toString();
            String[] ids=question_id.split(",");
            for(int i=0;i<ids.length;i++){
                Questionnaire questionnaire= questionnaireService.selectById(Integer.valueOf(ids[i]));

                if(questionnaire.getIsrelease().equals("Y")){
                    /**查询其相对应的会员任务是否存在,若不存在则可以删除，若存在提示用户先删除会员任务**/
                  List<String> idsList= questionnaireService.getIdsFromVipTask(questionnaire.getCorp_code(),"questionnaire");
                    for (int j = 0; j < idsList.size(); j++) {
                        if(idsList.contains(ids[i])){
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId(id);
                            dataBean.setMessage("删除已发布的问卷前，请先删除其相对应的所有的会员任务");
                            return dataBean.getJsonStr();
                        }
                    }
                }
                String corp=questionnaire.getCorp_code();
                String title= questionnaire.getTitle();
                String explain= questionnaire.getIllustrate();
                String template= questionnaire.getTemplate();
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
                String function = "会员管理_问卷调查";
                String action = Common.ACTION_DEL;
                String t_corp_code = corp;
                String t_title = title;
                String t_explain = explain;
                String t_template = template;
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_title, t_explain,t_template);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

                questionnaireService.deleteById(Integer.parseInt(ids[i]));
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



    @RequestMapping(value = "/insert",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String insertQtnaire(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String user_code=request.getSession().getAttribute("user_code").toString();
            String jsString=request.getParameter("param").toString();
            logger.info("json---------------" + jsString);
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            Questionnaire questionnaire=questionnaireService.selectByQtnaireName(jsonObject.getString("corp_code").toString(),
                    jsonObject.get("title").toString());
            if(questionnaire!=null){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("该问卷名称已存在");
                return dataBean.getJsonStr();
            }

            List<Object> stringList=new ArrayList<Object>();
            String template=jsonObject.getString("template");
            JSONArray templateArray=JSON.parseArray(template);
            for (int i = 0; i < templateArray.size(); i++) { //遍历问卷
                JSONObject template_obj = templateArray.getJSONObject(i);
                if(StringUtils.isNotBlank(template_obj.getString("name"))){
                    String name =  template_obj.getString("name");
                    stringList.add(name);
                }
            }
            int size1=stringList.size();
            List<Object> stringList2= CheckUtils.removeDuplicate(stringList);
            int size2=stringList2.size();
            if(size1!=size2){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("不允许存在相同的题目");
                return dataBean.getJsonStr();
            }

            Questionnaire questionnaire2=WebUtils.JSON2Bean(jsonObject,Questionnaire.class);
            questionnaire2.setCorp_code(jsonObject.getString("corp_code").toString());
            questionnaire2.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            questionnaire2.setCreater(user_code);
            questionnaire2.setModifier(user_code);
            questionnaire2.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
            System.out.println("===question===="+JSON.toJSONString(questionnaire2));
            questionnaireService.insertQtnaire(questionnaire2);
            JSONObject result=new JSONObject();
            Questionnaire questionnaire3= questionnaireService.selectByQtnaireName(jsonObject.getString("corp_code").toString(),jsonObject.get("title").toString());
            result.put("id",questionnaire3.getId());
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
            String function = "会员管理_问卷调查";
            String action = Common.ACTION_ADD;
            String t_corp_code = action_json.get("corp_code").toString();
            String t_title = action_json.get("title").toString();
            String t_explain = action_json.get("illustrate").toString();
            String t_template = action_json.get("template").toString();
            baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_title, t_explain,t_template);
            //-------------------行为日志结束----

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String updateQtnaire(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String user_code=request.getSession().getAttribute("user_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            logger.info("json---------------" + jsString);
            Questionnaire questionnaire=questionnaireService.selectById(Integer.parseInt(jsonObject.get("id").toString()));
            if(questionnaire.getIsrelease().equals("Y")){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("已发布的问卷不允许编辑");
                return dataBean.getJsonStr();
            }
            if(!(questionnaire.getTitle().equals(jsonObject.get("title").toString())&&questionnaire.getCorp_code().equals(jsonObject.getString("corp_code")))){

                Questionnaire questionnaire2=questionnaireService.selectByQtnaireName(jsonObject.getString("corp_code"),
                        jsonObject.get("title").toString());
                if(questionnaire2!=null){
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("该问卷名称已存在");
                    return dataBean.getJsonStr();
                }
            }

            List<Object> stringList=new ArrayList<Object>();
            String template=jsonObject.getString("template");
            JSONArray templateArray=JSON.parseArray(template);
            for (int i = 0; i < templateArray.size(); i++) { //遍历问卷
                JSONObject template_obj = templateArray.getJSONObject(i);
                if(StringUtils.isNotBlank(template_obj.getString("name"))){
                    String name =  template_obj.getString("name");
                    stringList.add(name);
                }
            }
            int size1=stringList.size();
            List<Object> stringList2= CheckUtils.removeDuplicate(stringList);
            int size2=stringList2.size();
            if(size1!=size2){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("不允许存在相同的题目");
                return dataBean.getJsonStr();
            }

            Questionnaire questionnaire3=WebUtils.JSON2Bean(jsonObject,Questionnaire.class);
            questionnaire3.setCorp_code(jsonObject.getString("corp_code").toString());
            questionnaire3.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            questionnaire3.setModifier(user_code);
            questionnaireService.updateQtnaire(questionnaire3);
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
            String function = "会员管理_问卷调查 ";
            String action = Common.ACTION_UPD;
            String t_corp_code = action_json.get("corp_code").toString();
            String t_title = action_json.get("title").toString();
            String t_explain = action_json.get("illustrate").toString();
            String t_template = action_json.get("template").toString();
            baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_title, t_explain,t_template);
            //-------------------行为日志结束-----------------------------------------------------------------------------------

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/screen",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public  String  screenQtnaire(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        String qtNaire_num="";
        JSONObject time_obj=new JSONObject();
        int end_row=0;
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            logger.info("json---------------" + jsString);
            int  page_num=Integer.parseInt(jsonObject.getString("page_num").toString());
            int  page_size=Integer.parseInt(jsonObject.getString("page_size").toString());
            Map<String,String> map = WebUtils.Json2Map(jsonObject);

            Set<String> sets=map.keySet();
            if(sets.contains("qtNaire_num")){
                if(StringUtils.isNotBlank(map.get("qtNaire_num").toString().trim())){
                    qtNaire_num=map.get("qtNaire_num").toString();
                    qtNaire_num=qtNaire_num.replace("\'","");
                    boolean flag=CheckUtils.isNumeric_V2(qtNaire_num);
                    if(flag==false){
                        dataBean.setId("1");
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setMessage("输入信息有误，仅允许输入数字");
                        return  dataBean.getJsonStr();
                    }
                }
                map.remove("qtNaire_num");
            }
            //对答卷时间进行筛选
            if(sets.contains("qtNaire_time")){
                String qtNaire_time=map.get("qtNaire_time");
                time_obj=JSON.parseObject(qtNaire_time);
                map.remove("qtNaire_time");
            }

            List<Questionnaire> list=null;
            if(role_code.equals(Common.ROLE_SYS)){
                list=questionnaireService.selectAllScreen("",map);
            }else{
                list=questionnaireService.selectAllScreen(corp_code,map);
            }
            list= (List<Questionnaire>) questionnaireService.switchQtNaire(list);
            list= (List<Questionnaire>) questionnaireService.getQtNaireNum(list,time_obj);
            //筛选答卷数量
            List<Questionnaire> list1=new ArrayList<Questionnaire>();

            System.out.println("====qtNaire_num===="+qtNaire_num);

            if(StringUtils.isNotBlank(qtNaire_num.trim())){
                for (int i = 0; i < list.size(); i++) {
                    Questionnaire qtNa=list.get(i);
                        if(qtNa.getQtNaire_num()==Integer.parseInt(qtNaire_num.trim())){
                            list1.add(qtNa);
                        }
                }
            }else{
                list1=list;
            }

            int total=list1.size();
            int pages = 0;
            if (total % page_size == 0) {
                pages = total / page_size;
            } else {
                pages = total / page_size + 1;
            }

            //筛选后的集合
            if (list1.size() > page_num*page_size){
                end_row = page_num*page_size;
            }else {
                end_row = list1.size();
            }
            List<Questionnaire> list2=new ArrayList<Questionnaire>();
            for (int i = (page_num-1)*page_size ; i < end_row; i++) {
                list2.add(list1.get(i));
            }
            JSONObject json=new JSONObject();
            json.put("total",list1.size());
            json.put("pageNum",page_num);
            json.put("pageSize",page_size);
            json.put("pages",pages);
            json.put("list",list2);
            JSONObject result=new JSONObject();
            result.put("list",JSON.toJSONString(json));
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

    /**
     *
     * @param request
     * @return 获取企业下所有的问卷
     */
    @RequestMapping(value = "/allQtNaire",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String getAllQtNaire(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject json_obj=JSON.parseObject(message);
            String corp_code=json_obj.getString("corp_code");
            List<Questionnaire> list=null;
            list=questionnaireService.selectAllQtNaire(corp_code);
            JSONArray arr=new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                Questionnaire question=list.get(i);
                JSONObject jsonObj=new JSONObject();
                jsonObj.put("id",question.getId());
                jsonObj.put("title",question.getTitle());
                arr.add(jsonObj);
            }
            JSONObject result=new JSONObject();
            result.put("list",arr);
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

    //详情接口
    @RequestMapping(value = "/qtNaireInfo",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String getQtNaireInfo(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONArray jsonArray=new JSONArray();
        try {
            String jsString = request.getParameter("param").toString();
            JSONObject jsonObject1 = JSON.parseObject(jsString);
            id = jsonObject1.get("id").toString();
            String message = jsonObject1.get("message").toString();
            JSONObject json_obj = JSON.parseObject(message);
            String qt_id = json_obj.getString("id");
            Questionnaire questionnaire= questionnaireService.selectById(Integer.parseInt(qt_id));
            JSONObject result =questionnaireService.getQtNaireInfo(questionnaire,json_obj);
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return dataBean.getJsonStr();
    }



    //问答题详情
    @RequestMapping(value = "/qtNaireAnswer",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String getQtNaireAnswer(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONArray jsonArray=new JSONArray();
        try {
            String jsString = request.getParameter("param").toString();
            JSONObject jsonObject1 = JSON.parseObject(jsString);
            id = jsonObject1.get("id").toString();
            String message = jsonObject1.get("message").toString();
            JSONObject json_obj = JSON.parseObject(message);
            String qt_id = json_obj.getString("id");
            Questionnaire questionnaire= questionnaireService.selectById(Integer.parseInt(qt_id));
            JSONObject result=questionnaireService.getQtNaireAnswer(questionnaire,json_obj);
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //会员答卷详情
    @RequestMapping(value = "/qtNaireVipAnswer",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String getQtNaireVipAnswer(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONArray jsonArray=new JSONArray();
        try {
            String jsString = request.getParameter("param").toString();
            JSONObject jsonObject1 = JSON.parseObject(jsString);
            id = jsonObject1.get("id").toString();
            String message = jsonObject1.get("message").toString();
            JSONObject json_obj = JSON.parseObject(message);
            String qt_id = json_obj.getString("id");
            Questionnaire questionnaire= questionnaireService.selectById(Integer.parseInt(qt_id));
            jsonArray=questionnaireService.getQtNaireVipAnswer(questionnaire,json_obj);
            JSONObject result=new JSONObject();
            result.put("list",jsonArray);
            result.put("info",JSON.toJSONString(questionnaire));
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return dataBean.getJsonStr();
    }



    @RequestMapping(value = "/exportExeclAnswer",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String exportExeclAnswer(HttpServletRequest request, HttpServletResponse response){
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try{
            String jsString = request.getParameter("param").toString();
            JSONObject jsonObject1 = JSON.parseObject(jsString);
            id = jsonObject1.get("id").toString();
            String message = jsonObject1.get("message").toString();
            JSONObject json_obj = JSON.parseObject(message);
            int page_num=Integer.parseInt(json_obj.getString("page_num"));
            int page_size=Integer.parseInt(json_obj.getString("page_size"));
            String name=json_obj.getString("name");
            String qt_id = json_obj.getString("id");
            Questionnaire questionnaire= questionnaireService.selectById(Integer.parseInt(qt_id));
            JSONObject result_obj=questionnaireService.getQtNaireAnswer(questionnaire,json_obj);
            JSONArray jsonArray=result_obj.getJSONArray("list");
            /**
             * 导出操作..................................................
             */
            int count=jsonArray.size();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(jsonArray);
            if (jsonArray.size() >= page_size * 2) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String,String> map=new LinkedHashMap<String, String>();
            map.put("vip_name","会员姓名");
            map.put("cardno","会员卡号");
            map.put("vip_phone","会员手机号");
            map.put("info","回答内容");

            int start_line = (page_num-1) * page_size + 1;
            int end_line = page_num*page_size;
            if (count < page_num*page_size)
                end_line = count;
            String pathname = OutExeclHelper.OutExecl2(json, jsonArray, map, response, request,questionnaire.getTitle()+"(填空题)"+"("+start_line+"-"+end_line+")");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
            //导出操作结束.........................................................
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/exportExeclVipAnswer",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String exportExeclVipAnswer(HttpServletRequest request, HttpServletResponse response){
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try{
            String jsString = request.getParameter("param").toString();
            JSONObject jsonObject1 = JSON.parseObject(jsString);
            id = jsonObject1.get("id").toString();
            String message = jsonObject1.get("message").toString();
            JSONObject json_obj = JSON.parseObject(message);
            String qt_id = json_obj.getString("id");
            int page_num=Integer.parseInt(json_obj.getString("page_num"));
            int page_size=Integer.parseInt(json_obj.getString("page_size"));

            Questionnaire questionnaire= questionnaireService.selectById(Integer.parseInt(qt_id));
            JSONObject result1 =questionnaireService.getQtNaireInfo(questionnaire,json_obj);
            JSONArray jsonArray=result1.getJSONArray("list");

            /**
             * 导出操作..................................................
             */
            int count=jsonArray.size();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(jsonArray);
            if (jsonArray.size() >= page_size * 2) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String,String> map=new LinkedHashMap<String, String>();
            map.put("vip_name","会员姓名");
            map.put("cardno","会员卡号");
            map.put("vip_phone","会员手机号");
            map.put("answer_num","答题数");
            map.put("modified_date","提交时间");
            String template=questionnaire.getTemplate();
            JSONArray templateArray=JSON.parseArray(template);

            for (int i = 0; i < templateArray.size(); i++) { //遍历问卷
                JSONObject template_obj = templateArray.getJSONObject(i);
                String name = template_obj.getString("name");
                String qt_type = template_obj.getString("type");
                String check = template_obj.getString("check");
                if(qt_type.equals("title")){
                    continue;
                }
                map.put(name,name);
            }

            int start_line = (page_num-1) * page_size + 1;
            int end_line = page_num*page_size;
            if (count < page_num*page_size)
                end_line = count;
            String pathname = OutExeclHelper.OutExecl2(json, jsonArray, map, response, request,questionnaire.getTitle()+"("+start_line+"-"+end_line+")");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
            //导出操作结束.........................................................
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return  dataBean.getJsonStr();
    }

}
