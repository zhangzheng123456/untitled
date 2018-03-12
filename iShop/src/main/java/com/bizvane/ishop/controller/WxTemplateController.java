package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.WxTemplate;
import com.bizvane.ishop.service.WxTemplateService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yanyadong on 2017/6/22.
 */
@Controller
@RequestMapping("/wxTemplate")
public class WxTemplateController {

    @Autowired
    WxTemplateService wxTemplateService;
    String id="";

    @RequestMapping(value = "/select",method = RequestMethod.POST)
    @ResponseBody
    public String selectWxTemplate(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            int template_id=Integer.parseInt(jsonObject.get("id").toString());
            WxTemplate wxTemplate=wxTemplateService.getTemplateById(template_id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(JSON.toJSONString(wxTemplate));
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
    public String selectAllWxTemplate(HttpServletRequest request){
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
            PageInfo<WxTemplate> list;
            if(role_code.equals(Common.ROLE_SYS)){
                list=wxTemplateService.selectAllWxTemplate(page_number,page_size,"",search_value);
            }else{
                list=wxTemplateService.selectAllWxTemplate(page_number,page_size,corp_code,search_value);
            }
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
    public String deleteWxTemplate(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            String integral_id=jsonObject.get("id").toString();
            String[] ids=integral_id.split(",");
            for (int i = 0; i <ids.length; i++) {
                wxTemplateService.deleteWxTemplate(Integer.parseInt(ids[i]));
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
    public  String  screenWxTemplate(HttpServletRequest request){
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
            Map<String,String> map = WebUtils.Json2Map(jsonObject);
            PageInfo<WxTemplate> list;
            if(role_code.equals(Common.ROLE_SYS)){
                list=wxTemplateService.selectWxTemplateAllScreen(page_number,page_size,"",map);
            }else{
                list=wxTemplateService.selectWxTemplateAllScreen(page_number,page_size,corp_code,map);
            }
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
    public String insertWxTemplate(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String user_id=request.getSession().getAttribute("user_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String template_id=jsonObject.getString("template_id");
            String template_name=jsonObject.getString("template_name");
            String corp_code=jsonObject.getString("corp_code");
            String app_id=jsonObject.getString("app_id");
            WxTemplate wx1=wxTemplateService.selectByIdAndName(app_id,template_id,"");
            if(wx1!=null){
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("模板ID已存在");
                return  dataBean.getJsonStr();
            }
            WxTemplate wx2=wxTemplateService.selectByIdAndName(app_id,"",template_name);
            if(wx2!=null){
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("标题已存在");
                return  dataBean.getJsonStr();
            }
            WxTemplate wx3= WebUtils.JSON2Bean(jsonObject, WxTemplate.class);
            String template_content_data=wx3.getTemplate_content_data();
            template_content_data=template_content_data.replaceAll("\\{\\{","(").replaceAll("}}",")");
            Pattern pattern= Pattern.compile("(\\([^\\)]+\\))");
            Matcher match= pattern.matcher(template_content_data);
            String data="";
            while (match.find()){
                String value=match.group();
                String param= value.replaceAll("\\(","").replaceAll("\\)","").split("\\.")[0];
                data+=param+",";
            }
            if (data.endsWith(",")){
                data = data.substring(0, data.length() - 1);
            }
            wx3.setTemplate_content(data);
            wx3.setCorp_code(corp_code);
            wx3.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            wx3.setCreater(user_id);
            wx3.setModifier(user_id);
            wx3.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
            wxTemplateService.insertWxTemplate(wx3);
            WxTemplate wxTemplate= wxTemplateService.selectByIdAndName(app_id,template_id,template_name);
            JSONObject result=new JSONObject();
            result.put("id",wxTemplate.getId());
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

    //修改
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public String updateWxTemplate(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String user_id=request.getSession().getAttribute("user_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String template_id=jsonObject.getString("template_id");
            String template_name=jsonObject.getString("template_name");
            String corp_code=jsonObject.getString("corp_code");
            String app_id=jsonObject.getString("app_id");
            int id1=Integer.parseInt(jsonObject.getString("id"));
            WxTemplate wx1=wxTemplateService.getTemplateById(id1);
            if(!(wx1.getTemplate_id().equals(template_id)&&wx1.getApp_id().equals(app_id))){
                WxTemplate wx2= wxTemplateService.selectByIdAndName(app_id,template_id,"");
                if(wx2!=null){
                    dataBean.setId("1");
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("模板ID已存在");
                    return  dataBean.getJsonStr();
                }
            }
            if(!(wx1.getTemplate_name().equals(template_name)&&wx1.getApp_id().equals(app_id))){
                WxTemplate wx3 = wxTemplateService.selectByIdAndName(app_id, "", template_name);
                if (wx3 != null) {
                    dataBean.setId("1");
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("模板名称已存在");
                    return dataBean.getJsonStr();
                }
            }

            WxTemplate wx4= WebUtils.JSON2Bean(jsonObject, WxTemplate.class);
            String template_content_data=wx4.getTemplate_content_data();
            template_content_data=template_content_data.replaceAll("\\{\\{","(").replaceAll("}}",")");
            Pattern pattern= Pattern.compile("(\\([^\\)]+\\))");
            Matcher match= pattern.matcher(template_content_data);
            String data="";
            while (match.find()){
               String value=match.group();
               String param= value.replaceAll("\\(","").replaceAll("\\)","").split("\\.")[0];
                data+=param+",";
            }
            if (data.endsWith(",")){
                data = data.substring(0, data.length() - 1);
            }
            wx4.setTemplate_content(data);
            wx4.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            wx4.setModifier(user_id);
            wxTemplateService.updateWxTemplate(wx4);
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

    @RequestMapping(value = "/selectName",method = RequestMethod.GET)
    @ResponseBody
    public String selectName(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            JSONObject jsonObject=wxTemplateService.getAllTemplateName();
            dataBean.setId("1");
            dataBean.setMessage(jsonObject.toJSONString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/selectAllWx",method = RequestMethod.POST)
    @ResponseBody
    public String selectAllWx(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String corp_code=jsonObject.getString("corp_code");
            String app_id=jsonObject.getString("app_id");
            List<WxTemplate> list=wxTemplateService.selectByCorpCode(corp_code,app_id);
            HashSet h = new HashSet(list);
            list.clear();
            list.addAll(h);
            List<HashMap<String,Object>> array=new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i <list.size() ; i++) {
                WxTemplate wxT=list.get(i);
                HashMap<String,Object> map=new HashMap<String, Object>();
                map.put("template_name",wxT.getTemplate_name());
                map.put("template_id",wxT.getTemplate_id());
                array.add(map);
            }
            JSONObject result=new JSONObject();
            result.put("list",array);
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

    @RequestMapping(value = "/selectAllTemlates",method = RequestMethod.POST)
    @ResponseBody
    public String selectAllTemlates(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String corp_code=jsonObject.getString("corp_code");
            String app_id=jsonObject.getString("app_id");
            List<WxTemplate> list=wxTemplateService.selectByCorpCode(corp_code,app_id);
            List<HashMap<String,Object>> array=new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i <list.size() ; i++) {
                WxTemplate wxT=list.get(i);
                HashMap<String,Object> map=new HashMap<String, Object>();
                map.put("template_name",wxT.getTemplate_name());
                map.put("template_id",wxT.getTemplate_id());
                String content = wxT.getTemplate_content_data();
                JSONArray array1 = new JSONArray();
                String[] contents = content.split("\n");
                for (int j = 0; j < contents.length; j++) {
                    String cont = contents[j];
                    if (cont.contains("：")){
                        String name = cont.split("：")[0];
                        String key = cont.split("：")[1].replace("{","").replace("}","").replace(".DATA","");
                        JSONObject content_obj2 = new JSONObject();
                        content_obj2.put("key",key);
                        content_obj2.put("value",name);
                        array1.add(content_obj2);
                    }else if (cont.contains("first")){
                        JSONObject content_obj2 = new JSONObject();
                        content_obj2.put("key","first");
                        content_obj2.put("value","导航语");
                        array1.add(content_obj2);
                    }else if (cont.contains("remark")){
                        JSONObject content_obj2 = new JSONObject();
                        content_obj2.put("key","remark");
                        content_obj2.put("value","备注");
                        array1.add(content_obj2);
                    }
                }
                map.put("content_data",array1);

                array.add(map);
            }
            JSONObject result=new JSONObject();
            result.put("list",array);
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
