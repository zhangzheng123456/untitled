package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.VipCardType;
import com.bizvane.ishop.entity.WxTemplateContent;
import com.bizvane.ishop.service.VipCardTypeService;
import com.bizvane.ishop.service.WxTemplateContentService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/6/27.
 */
@Controller
@RequestMapping("/wxTemplateContent")
public class WxTemplateContentController {
    @Autowired
    WxTemplateContentService wxTemplateContentService;
    @Autowired
    VipCardTypeService vipCardTypeService;
    String id="";

    @RequestMapping(value = "/select",method = RequestMethod.POST)
    @ResponseBody
    public String selectWxTemplateContent(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            int template_id=Integer.parseInt(jsonObject.get("id").toString());
            WxTemplateContent wxTemplateContent=wxTemplateContentService.selectById(template_id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(JSON.toJSONString(wxTemplateContent));
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
    public String selectAllWxTemplateContent(HttpServletRequest request){
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
            PageInfo<WxTemplateContent> list;
            if(role_code.equals(Common.ROLE_SYS)){
                list=wxTemplateContentService.selectAllWxTemplateContent(page_number,page_size,"",search_value);
            }else{
                list=wxTemplateContentService.selectAllWxTemplateContent(page_number,page_size,corp_code,search_value);
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
    public String deleteWxTemplateContent(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            String wx_id=jsonObject.get("id").toString();
            String[] ids=wx_id.split(",");
            for (int i = 0; i <ids.length; i++) {
                wxTemplateContentService.deleteWxTemplateContent(Integer.parseInt(ids[i]));
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
    public  String  screenWxTemplateContent(HttpServletRequest request){
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
            Map<String,Object> map = WebUtils.Json2Map(jsonObject);
            PageInfo<WxTemplateContent> list;
            if(role_code.equals(Common.ROLE_SYS)){
                list=wxTemplateContentService.selectWxTemplateContentScreen(page_number,page_size,"",map);
            }else{
                list=wxTemplateContentService.selectWxTemplateContentScreen(page_number,page_size,corp_code,map);
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
    public String insertWxTemplateContent(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String user_id=request.getSession().getAttribute("user_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String template_name=jsonObject.getString("template_name");
            String corp_code=jsonObject.getString("corp_code");
            String app_id=jsonObject.getString("app_id");
            String type=jsonObject.getString("type");
            String vip_card_type=jsonObject.getString("vip_card_type");
            if(StringUtils.isBlank(type)){
                jsonObject.put("type","");
            }
            if(StringUtils.isBlank(vip_card_type)){
                jsonObject.put("vip_card_type","");
            }

            WxTemplateContent wx3= WebUtils.JSON2Bean(jsonObject, WxTemplateContent.class);


            wx3.setVip_card_type_id("");
            if(StringUtils.isNotBlank(vip_card_type)){
               VipCardType vipCardType= vipCardTypeService.isExistByTypeTwo(corp_code,"",vip_card_type,"");
                wx3.setVip_card_type(vipCardType.getVip_card_type_name());//将卡名字存入
                wx3.setVip_card_type_id(vip_card_type);
            }
            List<WxTemplateContent> wx1=null;
            if(StringUtils.isBlank(type)){
                wx1=wxTemplateContentService.selectContentByCardId(app_id,template_name,null,null);
            }else{
                wx1=wxTemplateContentService.selectContentByCardId(app_id,template_name,wx3.getType(),wx3.getVip_card_type_id());
            }

            if(wx1.size() > 0){
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("标题已存在");
                return  dataBean.getJsonStr();
            }

            wx3.setCorp_code(corp_code);
            wx3.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            wx3.setCreater(user_id);
            wx3.setModifier(user_id);
            wx3.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
            wxTemplateContentService.insertWxTemplateContent(wx3);

            List<WxTemplateContent> wxTemplateContent=null;
            if(StringUtils.isBlank(type)){
                wxTemplateContent=wxTemplateContentService.selectContentByCardId(app_id,template_name,null,null);
            }else{
                wxTemplateContent=wxTemplateContentService.selectContentByCardId(app_id,template_name,wx3.getType(),wx3.getVip_card_type_id());
            }
            JSONObject result=new JSONObject();
            result.put("id",wxTemplateContent.get(0).getId());
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
    public String updateWxTemplateContent(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String user_id=request.getSession().getAttribute("user_code").toString();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String template_name=jsonObject.getString("template_name");
            String corp_code=jsonObject.getString("corp_code");
            int id1=Integer.parseInt(jsonObject.getString("id"));
            String app_id=jsonObject.getString("app_id");
            String type=jsonObject.getString("type")==null?"":jsonObject.getString("type");
            String vip_card_type=jsonObject.getString("vip_card_type")==null?"":jsonObject.getString("vip_card_type");//卡类型编号
            if(type.equals("")){
                jsonObject.put("type","");
            }
            if(vip_card_type.equals("")){
                jsonObject.put("vip_card_type","");
            }
            WxTemplateContent wx1=wxTemplateContentService.selectById(id1);


            WxTemplateContent wx4= WebUtils.JSON2Bean(jsonObject, WxTemplateContent.class);

            wx4.setVip_card_type_id("");
            if(StringUtils.isNotBlank(vip_card_type)){
                VipCardType vipCardType= vipCardTypeService.isExistByTypeTwo(corp_code,"",vip_card_type,"");
                wx4.setVip_card_type(vipCardType.getVip_card_type_name());
                wx4.setVip_card_type_id(vip_card_type);
            }

            if(!(wx1.getTemplate_name().equals(template_name)
                    &&wx1.getApp_id().equals(app_id)&&wx1.getType().equals(type)&&wx1.getVip_card_type_id().equals(vip_card_type))){

                List<WxTemplateContent> wx3=null;

                if(StringUtils.isBlank(type)){
                    wx3= wxTemplateContentService.selectContentByCardId(app_id,template_name,null,null);
                }else{
                    wx3 = wxTemplateContentService.selectContentByCardId(app_id,template_name,wx4.getType(),vip_card_type);
                }

                if(wx3.size() > 0){
                    dataBean.setId("1");
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("标题已存在");
                    return  dataBean.getJsonStr();
                }
            }
            wx4.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            wx4.setModifier(user_id);
            wxTemplateContentService.updateWxTemplateContent(wx4);
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
}
