package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.StoreGroup;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/area")
public class StoreGroupController {

    String id;

    @Autowired
    private StoreGroupService storeGroupService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private CorpService corpService;
    private static final Logger logger = Logger.getLogger(StoreGroupController.class);

    /**
     * 根据企业拉取店铺分组
     * @param request
     * @return
     */
    @RequestMapping(value = "/selAreaByCorpCode", method = RequestMethod.POST)
    @ResponseBody
    public String selAreaByCorpCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();

            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            String searchValue = jsonObject.get("searchValue").toString();
            PageInfo<StoreGroup> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                if (jsonObject.has("corp_code") && !jsonObject.get("corp_code").toString().equals("")) {
                    corp_code = jsonObject.get("corp_code").toString();
                }
                list = storeGroupService.selAreaByCorpCode(page_number, page_size, corp_code, "","", searchValue);
            } else {
                if (role_code.equals(Common.ROLE_GM)) {
                    list = storeGroupService.selAreaByCorpCode(page_number, page_size, corp_code, "","", searchValue);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    list = storeGroupService.selAreaByCorpCode(page_number, page_size, corp_code, area_code,"", searchValue);
                }else{
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeGroupService.selAreaByCorpCode(page_number, page_size, corp_code, "",store_code, searchValue);
                }
            }
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
     * session企业拉取店铺分组
     * @param request
     * @return
     */
    @RequestMapping(value = "/findAreaByCorpCode", method = RequestMethod.POST)
    @ResponseBody
    public String findAreaByCorpCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String corp_code =request.getSession().getAttribute("corp_code").toString();
            String searchValue = jsonObject.get("searchValue").toString();
            PageInfo<StoreGroup> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                if (jsonObject.has("corp_code"))
                    corp_code = jsonObject.get("corp_code").toString();
                list = storeGroupService.selAreaByCorpCode(page_number, page_size, corp_code, "","", searchValue);
            } else {
                if (role_code.equals(Common.ROLE_GM)) {
                    list = storeGroupService.selAreaByCorpCode(page_number, page_size, corp_code, "","", searchValue);
                    List<StoreGroup> storeGroups = new ArrayList<StoreGroup>();
                    StoreGroup storeGroup = new StoreGroup();
                    storeGroup.setStore_group_code("");
                    storeGroup.setStore_group_name("");
                    storeGroup.setCorp_code("");
                    storeGroup.setCorp_name("");
                    storeGroup.setCreated_date("");
                    storeGroup.setCreater("");
                    storeGroup.setId(0);
                    storeGroup.setIsactive("");
                    storeGroup.setModified_date("");
                    storeGroup.setModifier("");
                    storeGroups.add(0, storeGroup);
                    storeGroups.addAll(list.getList());
                    list.setList(storeGroups);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    list = storeGroupService.selAreaByCorpCode(page_number, page_size, corp_code, area_code,"", searchValue);
                }else{
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = storeGroupService.selAreaByCorpCode(page_number, page_size, corp_code, "",store_code, searchValue);
                }
            }

            JSONObject result = new JSONObject();
            result.put("role_code", JSON.toJSONString(role_code));
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
     * 店铺分组列表(区经面板)
     */
    @RequestMapping(value = "/findArea", method = RequestMethod.GET)
    @ResponseBody
    public String selectArea(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject result = new JSONObject();
            String area_code = request.getSession(false).getAttribute("area_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            List<StoreGroup> list = storeGroupService.selectArea( corp_code, area_code);
            result.put("list", JSON.toJSONString(list));
            result.put("role_code", JSON.toJSONString(role_code));
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
     * 店铺分组列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String areaManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();

            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONObject result = new JSONObject();
            PageInfo<StoreGroup> list = new PageInfo<StoreGroup>();
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = storeGroupService.getAllAreaByPage(page_number, page_size, "", "");
            } else {
                if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_BM) ) {
                    list = storeGroupService.selectByAreaCode(page_number, page_size, corp_code, "", "");
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    list = storeGroupService.selectByAreaCode(page_number, page_size, corp_code, area_code, "");
                }else {
                    List<StoreGroup> list1 = new ArrayList<StoreGroup>();
                    list.setList(list1);
                }
            }
            result.put("list", JSON.toJSONString(list));
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
     * 店铺分组新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addArea(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--area add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();

            String result = storeGroupService.insert(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add success");
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
     * 店铺分组编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editArea(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--area edit-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = storeGroupService.update(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("edit success");
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
            String msg = "";
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                StoreGroup storeGroup = storeGroupService.getAreaById(Integer.valueOf(ids[i]));
                if (storeGroup != null) {
                    String area_code = storeGroup.getStore_group_code();
                    String corp_code = storeGroup.getCorp_code();
                    List<Store> stores = storeService.selectStoreCountByArea(corp_code,area_code,"");
                    if (stores.size() > 0) {
                        msg = "店铺分组" + area_code + "下有所属店铺，请先处理店铺分组下店铺再删除";
                        break;
                    }
                }

            }
            if (!msg.equals("")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            } else {
                for (int i = 0; i < ids.length; i++) {
                    storeGroupService.delete(Integer.parseInt(ids[i]));
                }
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
     * 店铺分组管理
     * 选择店铺分组
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
            String area_id = jsonObject.get("id").toString();
            StoreGroup storeGroup = storeGroupService.getAreaById(Integer.parseInt(area_id));
            String area_code = storeGroup.getStore_group_code();
            int count = storeService.selectStoreCountByArea(storeGroup.getCorp_code(),area_code,Common.IS_ACTIVE_Y).size();
            storeGroup.setStore_count(String.valueOf(count));
            data = JSON.toJSONString(storeGroup);

            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(data);
        } catch (Exception e) {
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage("店铺分组信息异常");
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
            PageInfo<StoreGroup> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = storeGroupService.getAllAreaByPage(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_GM)) {
                    list = storeGroupService.selectByAreaCode(page_number, page_size, corp_code, "", search_value);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    // list = storeGroupService.getAllAreaByPage(page_number, page_size, corp
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    list = storeGroupService.selectByAreaCode(page_number, page_size, corp_code, area_code, search_value);
                }else {
                    List<StoreGroup> list1 = new ArrayList<StoreGroup>();
                    list.setList(list1);
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


    @RequestMapping(value = "Area_codeExist", method = RequestMethod.POST)
    @ResponseBody
    public String areaCodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String area_code = jsonObject.get("area_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            StoreGroup storeGroup = storeGroupService.getAreaByCode(corp_code, area_code,Common.IS_ACTIVE_Y);
            if (storeGroup != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("店铺分组编号已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("店铺分组编号不存在");
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
            StoreGroup storeGroup = storeGroupService.getAreaByName(corp_code, area_name,Common.IS_ACTIVE_Y);
            if (storeGroup != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("店铺分组编号已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("店铺分组编号不存在");
            }
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
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<StoreGroup> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = storeGroupService.getAllAreaByPage(1, 30000, "", search_value);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    list = storeGroupService.selectByAreaCode(1, 30000, corp_code, area_code, search_value);
                }else {
                    list = storeGroupService.getAllAreaByPage(1, 30000, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = storeGroupService.getAllAreaScreen(1, 30000, "", "", map);
                }else if(role_code.equals(Common.ROLE_AM)) {
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    list = storeGroupService.getAllAreaScreen(1, 30000, corp_code, area_codes, map);
                }else {
                    list = storeGroupService.getAllAreaScreen(1, 30000, corp_code, "", map);
                }
            }
            List<StoreGroup> storeGroups = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(storeGroups);
            if (storeGroups.size() >= 29999) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }

            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json, storeGroups, map, response, request);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /***
     * Execl增加
     */
    @RequestMapping(value = "/addByExecl", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional()
    public String addByExecl(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException {
        DataBean dataBean = new DataBean();
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String result = "";
        Workbook rwb = null;
        try {
            rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = 4;//得到所有的列
            int rows= rs.getRows();//得到所有的行
      //      int actualRows = LuploadHelper.getRightRows(rs);
//            if(actualRows != rows){
//                if(rows-actualRows==1){
//                    result = "：第"+rows+"行存在空白行,请删除";
//                }else{
//                    result = "：第"+(actualRows+1)+"行至第"+rows+"存在空白行,请删除";
//                }
//                int i = 5 / 0;
//            }
            if (rows < 4) {
                result = "：请从模板第4行开始插入正确数据";
                int i = 5 / 0;
            }
            if (rows > 9999) {
                result = "：数据量过大，导入失败";
                int i = 5 / 0;
            }
            Cell[] column3 = rs.getColumn(0);
            Pattern pattern1 = Pattern.compile("C\\d{5}");
            if (!role_code.equals(Common.ROLE_SYS)) {
                for (int i = 3; i < column3.length; i++) {
                    if(column3[i].getContents().toString().trim().equals("")){
                        continue;
                    }
                    if (!column3[i].getContents().toString().trim().equals(corp_code)) {
                        result = "：第" + (i + 1) + "行企业编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                    Matcher matcher = pattern1.matcher(column3[i].getContents().toString().trim());
                    if (matcher.matches() == false) {
                        result = "：第" + (i + 1) + "行企业编号格式有误";
                        int b = 5 / 0;
                        break;
                    }
                }
            }
                for (int i = 3; i < column3.length; i++) {
                    if(column3[i].getContents().toString().trim().equals("")){
                        continue;
                    }
                    Matcher matcher = pattern1.matcher(column3[i].getContents().toString().trim());
                    if (matcher.matches() == false) {
                        result = "：第" + (i + 1) + "行企业编号格式有误";
                        int b = 5 / 0;
                        break;
                    }
                    Corp corp = corpService.selectByCorpId(0, column3[i].getContents().toString().trim(),Common.IS_ACTIVE_Y);
                    if (corp == null) {
                        result = "：第" + (i + 1) + "行企业编号不存在";
                        int b = 5 / 0;
                        break;
                    }

                }



            String onlyCell1 = LuploadHelper.CheckOnly(rs.getColumn(1));
            if(onlyCell1.equals("存在重复值")){
                result = "：Execl中店铺分组编号存在重复值";
                int b = 5 / 0;
            }
            String onlyCell2 = LuploadHelper.CheckOnly(rs.getColumn(2));
            if(onlyCell2.equals("存在重复值")){
                result = "：Execl中店铺分组名称存在重复值";
                int b = 5 / 0;
            }
          //  Pattern pattern = Pattern.compile("A\\d{4}");
            Cell[] column = rs.getColumn(1);
            for (int i = 3; i < column.length; i++) {
//                Matcher matcher = pattern.matcher(column[i].getContents().toString());
//                if (matcher.matches() == false) {
//                    result = "：第" + (i + 1) + "行店铺分组编号格式有误";
//                    int b = 5 / 0;
//                    break;
//                }
                if(column[i].getContents().toString().trim().equals("")){
                    continue;
                }
                StoreGroup storeGroup = storeGroupService.getAreaByCode(column3[i].getContents().toString().trim(), column[i].getContents().toString().trim(),Common.IS_ACTIVE_Y);
                if (storeGroup != null) {
                    result = "：第" + (i + 1) + "行店铺分组编号已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column1 = rs.getColumn(2);
            for (int i = 3; i < column1.length; i++) {
                if(column1[i].getContents().toString().trim().equals("")){
                    continue;
                }
                StoreGroup storeGroup = storeGroupService.getAreaByName(column3[i].getContents().toString().trim(), column1[i].getContents().toString().trim(),Common.IS_ACTIVE_Y);
                if (storeGroup != null) {
                    result = "：第" + (i + 1) + "行店铺分组名称已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            ArrayList<StoreGroup> storeGroups =new ArrayList<StoreGroup>();
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    StoreGroup storeGroup = new StoreGroup();
                    String cellCorp = rs.getCell(j++, i).getContents().toString().trim();
                    String area_code = rs.getCell(j++, i).getContents().toString().trim();
                    String area_name = rs.getCell(j++, i).getContents().toString().trim();
                    String isactive = rs.getCell(j++, i).getContents().toString().trim();
//                    if(cellCorp.equals("")  && area_code.equals("") && area_name.equals("") && isactive.equals("")){
//                        result = "：第"+(i+1)+"行存在空白行,请删除";
//                        int a=5/0;
//                    }
                    if(cellCorp.equals("") && area_code.equals("") && area_name.equals("")){
                        continue;
                    }
                    if(cellCorp.equals("") || area_code.equals("") || area_name.equals("")){
                        result = "：第"+(i+1)+"行信息不完整,请参照Execl中对应的批注";
                        int a=5/0;
                    }
                    if(!role_code.equals(Common.ROLE_SYS)){
                        storeGroup.setCorp_code(corp_code);
                    }else{
                        storeGroup.setCorp_code(cellCorp);
                    }
                    storeGroup.setStore_group_code(area_code);
                    storeGroup.setStore_group_name(area_name);
                    if (isactive.toUpperCase().equals("N")) {
                        storeGroup.setIsactive("N");
                    } else {
                        storeGroup.setIsactive("Y");
                    }
                    Date now = new Date();
                    storeGroup.setCreater(user_id);
                    storeGroup.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    storeGroup.setModified_date(Common.DATETIME_FORMAT.format(now));
                    storeGroup.setModifier(user_id);
                    storeGroups.add(storeGroup);
                  //  result = storeGroupService.insertExecl(storeGroup);
                }
            }
            for (StoreGroup storeGroup : storeGroups) {
                result = storeGroupService.insertExecl(storeGroup);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } finally {
            if (rwb != null) {
                rwb.close();
            }
            System.gc();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺分组管理
     * 筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
//            String screen = jsonObject.get("screen").toString();
//            JSONObject jsonScreen = new JSONObject(screen);
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<StoreGroup> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = storeGroupService.getAllAreaScreen(page_number, page_size, "", "", map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_GM)) {
                    list = storeGroupService.getAllAreaScreen(page_number, page_size, corp_code, "", map);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_codes = request.getSession(false).getAttribute("area_code").toString();
                    list = storeGroupService.getAllAreaScreen(page_number, page_size, corp_code, area_codes, map);
                }else {
                    List<StoreGroup> list1 = new ArrayList<StoreGroup>();
                    list.setList(list1);
                }
            }
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

    /**
     * 店铺分组分配多个店铺
     * @param request
     * @return
     */
    @RequestMapping(value = "/stores/check", method = RequestMethod.POST)
    @ResponseBody
    public String checkStores(HttpServletRequest request)throws Exception {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json-----stores/check----------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = "";
            if (jsonObject.has("searchValue")) {
                search_value = jsonObject.get("searchValue").toString();
            }
            String area_code = jsonObject.get("area_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            PageInfo<Store> list;
            list= storeGroupService.getAllStoresByCorpCode( page_number, page_size, corp_code, search_value,area_code);
            JSONObject result = new JSONObject();
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


    @RequestMapping(value = "/stores/save", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String saveStores(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        Date now = new Date();
        try {
            String user_id = request.getSession().getAttribute("user_code").toString();

            String jsString = request.getParameter("param");
            logger.info("json-------stores/save--------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String area_code=jsonObject.get("area_code").toString();

            String choose_id = "";
            String quit_id = "";
            if (jsonObject.has("choose"))
                choose_id = jsonObject.get("choose").toString();
            if (jsonObject.has("quit"))
                quit_id = jsonObject.get("quit").toString();

            if(!choose_id.equals("")) {
                String[] choose_ids = choose_id.split(",");
                for (int i = 0; i < choose_ids.length; i++) {
                    logger.info("--------check-------" + Integer.valueOf(choose_ids[i]));
                    Store store = storeService.getById(Integer.valueOf(choose_ids[i]));
                    if (store != null) {
                        String area_code1 = store.getStore_group_code();
//                    String[] codes = area_code1.split(",");
                        if (!area_code1.contains(Common.SPECIAL_HEAD + area_code + ",")) {
                            area_code1 = area_code1 + Common.SPECIAL_HEAD + area_code + ",";
                            store.setStore_group_code(area_code1);
                            store.setModified_date(Common.DATETIME_FORMAT.format(now));
                            store.setModifier(user_id);
                            storeService.updateStore(store);
//                            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                            dataBean.setId(id);
//                            dataBean.setMessage("success");
                        } else {
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId(id);
                            dataBean.setMessage(store.getStore_name() + "已在该店铺分组，请勿重复选择");
                            return dataBean.getJsonStr();
                        }
                    }
                }
            }

            if(!quit_id.equals("")) {
                String[] quit_ids = quit_id.split(",");
                for (int i = 0; i < quit_ids.length; i++) {
                    logger.info("--------check-------" + Integer.valueOf(quit_ids[i]));
                    Store store = storeService.getById(Integer.valueOf(quit_ids[i]));
                    if (store != null) {
                        String area_code1 = store.getStore_group_code();
//                    String[] codes = area_code1.split(",");
                        if (area_code1.contains(Common.SPECIAL_HEAD + area_code + ",")) {
                            area_code1 = area_code1.replace(Common.SPECIAL_HEAD + area_code + ",", "");
                            store.setStore_group_code(area_code1);
                            store.setModified_date(Common.DATETIME_FORMAT.format(now));
                            store.setModifier(user_id);
                            storeService.updateStore(store);
//                            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                            dataBean.setId(id);
//                            dataBean.setMessage("success");
                        } else {
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId(id);
                            dataBean.setMessage(store.getStore_name() + "不在该店铺分组");
                            return dataBean.getJsonStr();
                        }
                    }
                }
            }

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}
