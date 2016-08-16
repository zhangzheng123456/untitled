package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.CorpParam;
import com.bizvane.ishop.service.CorpParamService;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.TableManagerService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.impl.xb.xsdschema.ListDocument;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/8/11.
 */
@Controller
@RequestMapping("/corpParam")
public class CorpParamController {
    String id;
    @Autowired
    private CorpParamService corpParamService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private CorpService corpService;
    private static final Logger logger = Logger.getLogger(CorpParamController.class);

    /**
     * 企业参数配置列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String corpParamManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            String group_code = request.getSession().getAttribute("group_code").toString();
            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONArray actions = functionService.selectActionByFun(corp_code, user_code, group_code, role_code, function_code);
            JSONObject result = new JSONObject();
            PageInfo<CorpParam> list = null;
            list=corpParamService.selectAllParam(page_number, page_size, corp_code, "");
            result.put("list", JSON.toJSONString(list));
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
    /**
     * 根据企业编号拉取企业参数
     * @param request
     * @return
     */
    @RequestMapping(value = "/selParamByCorpCode", method = RequestMethod.POST)
    @ResponseBody
    public String selAreaByCorpCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String param_id = jsonObject.get("param_id").toString();
         List<CorpParam> list=corpParamService.selectByCorpParam(corp_code,param_id);
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }

        return dataBean.getJsonStr();

    }
    /**
     * 添加企业参数配置
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addCorpParam(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--corpParam add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result=corpParamService.insert(message,user_code);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add corpParam success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }

        return dataBean.getJsonStr();
    }

    /**
     * 编辑企业参数配置
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editCorpParam(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--corpParam edit-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result=corpParamService.update(message,user_code);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("corpParam edit success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }

        return dataBean.getJsonStr();
    }

    /**
     * 删除企业参数配置
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        try {
            String jsString = request.getParameter("param");
            logger.info("json-corpParam-delete-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corpParam_id = jsonObject.get("id").toString();
            String[] ids = corpParam_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                CorpParam corpParam=corpParamService.selectById(Integer.valueOf(ids[i]));
                if (corpParam != null) {
                    corpParamService.delete(Integer.valueOf(ids[i]));
                }   else{
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage(message);
                    return dataBean.getJsonStr();
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 选择企业参数
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");
            logger.info("json-corpParanm select-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corpParam_id = jsonObject.get("id").toString();
            data = JSON.toJSONString(corpParamService.selectById(Integer.parseInt(corpParam_id)));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(data);
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("企业参数配置信息异常");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 搜索企业参数
     * @param request
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String corp_code = request.getSession().getAttribute("corp_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<CorpParam> list = null;
            list = corpParamService.selectAllParam(page_number, page_size, corp_code, search_value);
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }

        return dataBean.getJsonStr();
    }

    /**
     * 筛选企业参数
     * @param request
     * @return
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id="";

        try {
            String jsString = request.getParameter("param");
            logger.info("json-------corpParam--------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            JSONObject result = new JSONObject();
            PageInfo<CorpParam> list = null;
           // String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            list = corpParamService.selectAllParamScreen(page_number, page_size, "", map);
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }

        return dataBean.getJsonStr();
    }


}
