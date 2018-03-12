package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Interfacers;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.InterfaceService;
import com.bizvane.ishop.service.TableManagerService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * Created by yin on 2016/6/22.
 */
@Controller
@RequestMapping("/interfacers")
public class InterfaceController {
    @Autowired
    private InterfaceService interfaceService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private TableManagerService managerService;
    String id;

    private static final Logger logger = Logger.getLogger(InterfaceController.class);

//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    //列表
//    public String selectAll(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//
//            JSONObject result = new JSONObject();
//            PageInfo<Interfacers> list = interfaceService.selectAllInterface(page_number, page_size, "");
//
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId(id);
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    //条件查询
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            //-------------------------------------------------------
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            JSONObject result = new JSONObject();
            PageInfo<Interfacers> list = interfaceService.selectAllInterface(page_number, page_size, search_value);
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

    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    //条件查询
    public String selectByScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
//            String screen = jsonObject.get("screen").toString();
//            JSONObject jsonScreen = new JSONObject(screen);
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Interfacers> list = interfaceService.selectAllScreen(page_number, page_size, map);
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }
    /**
     * 增加（用了事务）
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addInterface(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            Interfacers interfacers = WebUtils.JSON2Bean(jsonObject, Interfacers.class);            //------------操作日期-------------
            Date date=new Date();
            interfacers.setCreated_date(Common.DATETIME_FORMAT.format(date));
            interfacers.setCreater(user_id);
            interfacers.setModified_date(Common.DATETIME_FORMAT.format(date));
            interfacers.setModifier(user_id);
            interfaceService.addInterface(interfacers);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            Interfacers interfacers1=interfaceService.selectForId(interfacers.getCorp_code(),interfacers.getVersion());
            dataBean.setId(id);
            dataBean.setMessage(String.valueOf(interfacers1.getId()));
        }catch (Exception ex){
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String inter_id = jsonObject.get("id").toString();
            String[] ids = inter_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                interfaceService.delInterfaceById(Integer.valueOf(ids[i]));
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
    /**
     * 根据ID查询
     */
    @RequestMapping(value = "/selectById", method = RequestMethod.POST)
    @ResponseBody
    public String selectById(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String app_id = jsonObject.get("id").toString();
            final Interfacers interfacers = interfaceService.selInterfaceById(Integer.parseInt(app_id));
            JSONObject result = new JSONObject();
            result.put("interfacers", JSON.toJSONString(interfacers));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("selectById-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 编辑(加了事务)
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editInterface(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            Interfacers interfacers = WebUtils.JSON2Bean(jsonObject, Interfacers.class);
            //------------操作日期-------------
            Date date=new Date();
            interfacers.setModified_date(Common.DATETIME_FORMAT.format(date));
            interfacers.setModifier(user_id);
            interfaceService.updInterfaceById(interfacers);
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
//    /***
//     * 查出要导出的列
//     */
//    @RequestMapping(value = "/getCols", method = RequestMethod.POST)
//    @ResponseBody
//    public String selAllByCode(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String jsString = request.getParameter("param");
//             JSONObject jsonObj = JSONObject.parseObject(jsString);
//            String message = jsonObj.get("message").toString();
//             JSONObject jsonObject = JSONObject.parseObject(message);
//            String function_code = jsonObject.get("function_code").toString();
//            List<TableManager> tableManagers = managerService.selAllByCode(function_code);
//            JSONObject result = new JSONObject();
//            result.put("tableManagers", JSON.toJSONString(tableManagers));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//        }
//        return dataBean.getJsonStr();
//    }
//    /***
//     * 导出数据
//     */
//    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
//    @ResponseBody
//    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
//        DataBean dataBean = new DataBean();
//        String errormessage = "数据异常，导出失败";
//        try {
//            String jsString = request.getParameter("param");
//             JSONObject jsonObj = JSONObject.parseObject(jsString);
//            String message = jsonObj.get("message").toString();
//             JSONObject jsonObject = JSONObject.parseObject(message);
//            //系统管理员(官方画面)
//            String search_value = jsonObject.get("searchValue").toString();
//            String screen = jsonObject.get("list").toString();
//            PageInfo<Interfacers> corpInfo = null;
//            if (screen.equals("")) {
//                corpInfo= interfaceService.selectAllInterface(1, 30000, search_value);
//            } else {
//                Map<String, String> map = WebUtils.Json2Map(jsonObject);
//                corpInfo = interfaceService.selectAllScreen(1, 30000, map);
//            }
//            List<Interfacers> feedbacks = corpInfo.getList();
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//            String json = mapper.writeValueAsString(feedbacks);
//            if (feedbacks.size() >= 29999) {
//                errormessage = "导出数据过大";
//                int i = 9 / 0;
//            }
//            LinkedHashMap<String,String> map = WebUtils.Json2ShowName(jsonObject);
//            // String column_name1 = "corp_code,corp_name";
//            // String[] cols = column_name.split(",");//前台传过来的字段
//            String pathname = OutExeclHelper.OutExecl(json,feedbacks, map, response, request);
//            JSONObject result = new JSONObject();
//            if (pathname == null || pathname.equals("")) {
//                errormessage = "数据异常，导出失败";
//                int a = 8 / 0;
//            }
//            result.put("path", JSON.toJSONString("lupload/" + pathname));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId(id);
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("-1");
//            dataBean.setMessage(errormessage);
//        }
//        return dataBean.getJsonStr();
//    }
}
