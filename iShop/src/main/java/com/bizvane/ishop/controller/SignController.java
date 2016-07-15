package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Interfacers;
import com.bizvane.ishop.entity.Sign;
import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.SignService;
import com.bizvane.ishop.service.TableManagerService;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by yin on 2016/6/23.
 */
@Controller
@RequestMapping("/sign")
public class SignController {
    @Autowired
    private SignService signService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private TableManagerService managerService;
    String id;

    private static final Logger logger = Logger.getLogger(InterfaceController.class);

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    //列表
    public String selectAll(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int user_id = Integer.parseInt(request.getSession().getAttribute("user_id").toString());
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);
            JSONObject result = new JSONObject();
            PageInfo<Sign> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = signService.selectSignAll(page_number, page_size, "", "");
            } else if (role_code.equals(Common.ROLE_GM)) {
                //系统管理员
                list =signService.selectSignAll(page_number, page_size, corp_code, "");
            } else if (role_code.equals(Common.ROLE_SM)) {
                //店长
                String store_code = request.getSession().getAttribute("store_code").toString();
                list = signService.selectSignByInp(page_number, page_size, corp_code, "", store_code,"", role_code);
            }else if (role_code.equals(Common.ROLE_AM)){
                //区经
                String area_code = request.getSession().getAttribute("area_code").toString();
                list = signService.selectSignByInp(page_number, page_size, corp_code, "","",area_code, role_code);
            }else if(role_code.equals(Common.ROLE_STAFF)){
                list=signService.selectByUser(page_number,page_size,corp_code,user_code,"");
            }
          //  System.out.println(list.getList().get(0).getSign_time()+"---"+list.getList().get(0).getUser_code());
            result.put("list", JSON.toJSONString(list));
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    //条件查询
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
            String role_code = request.getSession().getAttribute("role_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Sign> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = signService.selectSignAll(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_GM)) {
                    //企业管理员
                    list = signService.selectSignAll(page_number, page_size, corp_code, search_value);
                } else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    list = signService.selectSignByInp(page_number, page_size, corp_code, search_value, store_code, "", role_code);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    list = signService.selectSignByInp(page_number, page_size, corp_code, search_value, "", area_code, role_code);
                }else if(role_code.equals(Common.ROLE_STAFF)){
                    list=signService.selectByUser(page_number,page_size,corp_code,user_code,search_value);
                }
            }
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
     * 删除(用了事务)
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String inter_id = jsonObject.get("id").toString();
            String[] ids = inter_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                signService.delSignById(Integer.valueOf(ids[i]));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /***
     * 查出要导出的列
     */
    @RequestMapping(value = "/getCols", method = RequestMethod.POST)
    public String selAllByCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String function_code = jsonObject.get("function_code").toString();
            List<TableManager> tableManagers = managerService.selAllByCode(function_code);
            JSONObject result = new JSONObject();
            result.put("tableManagers", JSON.toJSONString(tableManagers));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }
    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean=new DataBean();
        try{
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            PageInfo<Sign> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = signService.selectSignAll(1, 10000, "", "");
            } else if (role_code.equals(Common.ROLE_GM)) {
                //系统管理员
                list =signService.selectSignAll(1, 10000, corp_code, "");
            } else if (role_code.equals(Common.ROLE_SM)) {
                //店长
                String store_code = request.getSession().getAttribute("store_code").toString();
                list = signService.selectSignByInp(1, 10000, corp_code, "", store_code,"", role_code);
            }else if (role_code.equals(Common.ROLE_AM)){
                //区经
                String area_code = request.getSession().getAttribute("area_code").toString();
                list = signService.selectSignByInp(1, 10000, corp_code, "","",area_code, role_code);
            }else if(role_code.equals(Common.ROLE_STAFF)){
                list=signService.selectByUser(1,10000,corp_code,user_code,"");
            }
            List<Sign> signs = list.getList();
            String column_name = jsonObject.get("column_name").toString();
            String[] cols = column_name.split(",");//前台传过来的字段
            OutExeclHelper.OutExecl(signs,cols,response);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("word success");
        }catch (Exception e){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(e.getMessage());
        }
        return dataBean.getJsonStr();
    }
}
