package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.CorpInfo;
import com.bizvane.ishop.entity.UserInfo;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.FunctionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Created by zhouying on 2016-04-20.
 */


/**
 * 企业管理
 */

@Controller
@RequestMapping("/corp")
public class CorpController {

    private static Logger logger = LoggerFactory.getLogger((CorpController.class));

    String id;

    @Autowired
    private CorpService corpService;
    @Autowired
    private FunctionService functionService;
    /*
    * 列表
    * */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public String cropManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int user_id = Integer.parseInt(request.getSession().getAttribute("user_id").toString());
            String role_code = request.getSession().getAttribute("role_code").toString();
            String function_code = request.getParameter("funcCode");
            JSONArray actions = functionService.selectActionByFun(user_id,role_code,function_code);

            JSONObject info = new JSONObject();
            if(role_code.contains("R1")) {
                //系统管理员(官方画面)
                int page_number = Integer.parseInt(request.getParameter("pageNumber"));
                int page_size = Integer.parseInt(request.getParameter("pageSize"));
                PageHelper.startPage(page_number, page_size);
                List<CorpInfo> corpInfo = corpService.selectAllCorp("");
                PageInfo<CorpInfo> page = new PageInfo<CorpInfo>(corpInfo);
                info.put("corpInfo",page);
            }else{
                //用户画面
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                CorpInfo corpInfo = corpService.selectByCorpId(0,corp_code);
                info.put("corpInfo",corpInfo);
            }
            info.put("actions",actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(info.toString());
        }catch (Exception ex){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 新增
     */
    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @ResponseBody
    public String addCrop(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            CorpInfo corp = new CorpInfo();
            String max_code = corpService.selectMaxCorpCode();
            int code=Integer.parseInt(max_code.substring(1,max_code.length()))+1;
            Integer c = code;
            int length = 5-c.toString().length();
            String corp_code="C";
            for (int i=0;i<length;i++){
                corp_code=corp_code+"0";
            }
            corp_code=corp_code+code;
            corp.setCorp_code(corp_code);
            corp.setCorp_name(jsonObject.get("corp_name").toString());
            corp.setAddress(jsonObject.get("address").toString());
            corp.setContact(jsonObject.get("contact").toString());
            corp.setContact_phone(jsonObject.get("phone").toString());
            Date now = new Date();
            corp.setCreated_date(now);
            corp.setCreater(user_id);
            corp.setModified_date(now);
            corp.setModifier(user_id);
            corp.setIsactive(jsonObject.get("isactive").toString());
            corpService.insertCorp(corp);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("add success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit",method = RequestMethod.GET)
    @ResponseBody
    public String editCrop(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_id").toString();
        try{
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            CorpInfo corp = new CorpInfo();
            corp.setCorp_code(jsonObject.get("corp_code").toString());
            corp.setCorp_name(jsonObject.get("corp_name").toString());
            corp.setAddress(jsonObject.get("address").toString());
            corp.setContact(jsonObject.get("contact").toString());
            corp.setContact_phone(jsonObject.get("phone").toString());
            Date now = new Date();
            corp.setModified_date(now);
            corp.setModifier(user_id);
            corp.setIsactive(jsonObject.get("isactive").toString());
            corpService.updateByCorpId(corp);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        logger.info("info--------" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_id = jsonObject.get("id").toString();
            String type = jsonObject.get("type").toString();
            String[] ids = user_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                CorpInfo corp = new CorpInfo(Integer.valueOf(ids[i]));
                logger.info("inter---------------" + Integer.valueOf(ids[i]));
                corpService.deleteByCorpId(Integer.valueOf(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            //	return "Error deleting the user:" + ex.toString();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 查找
     */
    @RequestMapping("/find/{id}")
    @ResponseBody
    public String findById(@PathVariable Integer corp_id) {
        DataBean bean=new DataBean();
        String data = null;
        try {
            data = JSON.toJSONString(corpService.selectByCorpId(corp_id,""));
            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(data);
        } catch (Exception e) {
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage(e.getMessage());
        }
        logger.info("info-----" + bean.getJsonStr());
        return bean.getJsonStr();
    }

}
