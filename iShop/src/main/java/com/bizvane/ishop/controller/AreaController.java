package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Area;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.AreaService;
import com.bizvane.ishop.service.FunctionService;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/area")
public class AreaController {

    String id;

    @Autowired
    private AreaService areaService;
    @Autowired
    private FunctionService functionService;

    SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);

    private static final Logger logger = Logger.getLogger(AreaController.class);


    /**
     * 品牌列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String areaManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            String group_code = request.getSession().getAttribute("group_code").toString();
            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONArray actions = functionService.selectActionByFun(corp_code+user_code,corp_code+group_code, role_code, function_code);
            JSONObject result = new JSONObject();
            PageInfo<Area> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = areaService.getAllAreaByPage(page_number, page_size, "", "");
            } else {
                list = areaService.getAllAreaByPage(page_number, page_size, corp_code, "");
            }
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
     * 品牌新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addArea(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--area add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            Area area = new Area();
            Date now = new Date();
            area.setArea_code(jsonObject.get("area_code").toString());
            area.setArea_name(jsonObject.get("area_name").toString());
            area.setCorp_code(jsonObject.get("corp_code").toString());
            area.setCreated_date(sdf.format(now));
            area.setCreater(user_id);
            area.setModified_date(sdf.format(now));
            area.setModifier(user_id);
            area.setIsactive(jsonObject.get("isactive").toString());
            areaService.insert(area);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("add success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 品牌编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editArea(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--area edit-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            Area area = new Area();
            Date now = new Date();
            area.setId(Integer.parseInt(jsonObject.get("id").toString()));
            area.setArea_code(jsonObject.get("area_code").toString());
            area.setArea_name(jsonObject.get("area_name").toString());
            area.setCorp_code(jsonObject.get("corp_code").toString());
            area.setModifier(user_id);
            area.setModified_date(sdf.format(now));
            area.setIsactive(jsonObject.get("isactive").toString());
            areaService.update(area);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
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
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String area_id = jsonObject.get("id").toString();
            String[] ids = area_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                Area area = areaService.getAreaById(Integer.valueOf(ids[i]));
                String area_code = area.getArea_code();
                String corp_code = area.getCorp_code();
                List<Store> stores = areaService.getAreaStore(corp_code, area_code);
                if (stores.size() == 0) {
                    areaService.delete(Integer.valueOf(ids[i]));
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage("success");
                } else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("区域"+area_code+"下有所属店铺，请先处理区域下店铺再删除！");
                    return dataBean.getJsonStr();
                }
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
     * 区域管理
     * 选择区域
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");

            logger.info("json-select-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_id = jsonObject.get("id").toString();
            data = JSON.toJSONString(areaService.getAreaById(Integer.parseInt(user_id)));
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

    /**
     * 页面查找
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

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Area> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = areaService.getAllAreaByPage(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                list = areaService.getAllAreaByPage(page_number, page_size, corp_code, search_value);
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


    @RequestMapping(value = "Area_codeExist", method = RequestMethod.POST)
    @ResponseBody
    public String Corp_codeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String area_code = jsonObject.get("area_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Area area = areaService.getAreaByCode(corp_code, area_code);
            if (area != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("区域编号已被使用！！！");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("区域编号不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "Area_nameExist", method = RequestMethod.POST)
    @ResponseBody
    public String Area_nameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String area_name = jsonObject.get("area_name").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Area area = areaService.getAreaByName(corp_code, area_name);
            if (area != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("区域编号已被使用！！！");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("区域编号不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

//
//    @RequestMapping(value = "", method = RequestMethod.POST)
//    @ResponseBody
//    public String getAreaByCorp(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            String corp_code = jsonObject.getString("corp_code");
//            List<Area> list = this.areaService.getAreaByCorp(corp_code);
//            com.alibaba.fastjson.JSONArray array = new com.alibaba.fastjson.JSONArray();
//            com.alibaba.fastjson.JSONObject json = null;
//            for (int i = 0; list != null && i < list.size(); i++) {
//                String area_code = list.get(i).getArea_code();
//                String area_name = list.get(i).getArea_name();
//                json = new com.alibaba.fastjson.JSONObject();
//                json.put("area_code", area_code);
//                json.put("area_name", area_name);
//                array.add(json);
//            }
//            org.json.JSONObject result = new org.json.JSONObject();
//            result.put("areas", array);
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setMessage("success!!");
//        } catch (Exception ex) {
//
//        }
//        return "";
//    }

}
