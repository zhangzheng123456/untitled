package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.VipCardType;
import com.bizvane.ishop.entity.VipRules;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.VipCardTypeService;
import com.bizvane.ishop.service.VipRulesService;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by nanji on 2016/12/30.
 */
@Controller
@RequestMapping("/vipCardType")
public class VipCardTypeController {
    @Autowired
    VipCardTypeService vipCardTypeService;
    @Autowired
    VipRulesService vipRulesService;
    @Autowired
    StoreService storeService;

    String id;
    private static final Logger logger = Logger.getLogger(VipCardTypeController.class);

    /**
     * 会员制度
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addVipRules(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = this.vipCardTypeService.insert(message, user_id);

            if (result.equals("该编号已存在")) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            } else if (result.equals("该名称已存在")) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            } else if (result.equals(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editVipRules(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();

            String result = vipCardTypeService.update(message, user_id);
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
            ex.printStackTrace();
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
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_card_typr_id = jsonObject.get("id").toString();
            String msg = null;
            String[] ids = vip_card_typr_id.split(",");

            for (int i = 0; i < ids.length; i++) {
                VipCardType vipCardType = vipCardTypeService.getVipCardTypeById(Integer.parseInt(ids[i]));
                if (vipCardType != null) {
                    List<VipRules> list = vipRulesService.getViprulesList(vipCardType.getCorp_code(), Common.IS_ACTIVE_Y);

                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).getVip_type().equals(vipCardType.getVip_card_type_name()) || list.get(j).getHigh_vip_type().equals(vipCardType.getVip_card_type_name())) {
                            msg = "会员类型: “" + vipCardType.getVip_card_type_name() + "”已被使用，请先处理后再删除";
                            break;
                        }
                    }
                }
            }
            if (msg == null) {
                for (int i = 0; i < ids.length; i++) {
                    vipCardTypeService.delete(Integer.valueOf(ids[i]));
                }
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("删除成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }

        return dataBean.getJsonStr();
    }

    /**
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession(false).getAttribute("corp_code").toString();

        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            JSONObject result = new JSONObject();
            PageInfo<VipCardType> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = vipCardTypeService.getAllVipCardTypeByPage(page_number, page_size, "", search_value);
            } else {
                if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)) {
                    list = vipCardTypeService.getAllVipCardTypeByPage(page_number, page_size, corp_code, search_value);
                }else {
                    String brand_code = request.getSession(false).getAttribute("brand_code").toString();
                    String store_group_code = request.getSession(false).getAttribute("area_code").toString();
                    list = vipCardTypeService.getVipCardByRole(page_number, page_size,corp_code, Common.IS_ACTIVE_Y,brand_code,store_group_code);
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
     * 筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<VipCardType> list = null;
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipCardTypeService.getAllVipCardTypeScreen(page_number, page_size, "","","", map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)) {
                    list = vipCardTypeService.getAllVipCardTypeScreen(page_number, page_size, corp_code,"","", map);
                }else {
                    String brand_code = request.getSession(false).getAttribute("brand_code").toString();
                    String store_group_code = request.getSession(false).getAttribute("area_code").toString();
                    list = vipCardTypeService.getAllVipCardTypeScreen(page_number, page_size, corp_code,brand_code,store_group_code, map);
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
     * 根据id查看
     *
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
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vipRules_id = jsonObject.get("id").toString();
            data = JSON.toJSONString(vipCardTypeService.getVipCardTypeById(Integer.parseInt(vipRules_id)));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(data);
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("信息异常");
        }


        return dataBean.getJsonStr();
    }

    /**
     * 验证会员类型编号的唯一性
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/vipCardTypeCodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String codeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_card_type_code = jsonObject.get("vip_card_type_code").toString().trim();
            String corp_code = jsonObject.get("corp_code").toString();
            VipCardType vipRules = vipCardTypeService.getVipCardTypeByCode(corp_code, vip_card_type_code, Common.IS_ACTIVE_Y);
            if (vipRules != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("当前企业下该会员类型编号已存在");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("当前企业下该会员类型编号不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 验证会员类型名称的唯一性
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/vipCardTypeNameExist", method = RequestMethod.POST)
    @ResponseBody
    public String nameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
//            String jsString = request.getParameter("param");
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject jsonObject = JSONObject.parseObject(message);
//            String vip_card_type_name = jsonObject.get("vip_card_type_name").toString().trim();
//            String corp_code = jsonObject.get("corp_code").toString();
//            VipCardType vipRules = vipCardTypeService.getVipCardTypeByName(corp_code, vip_card_type_name, Common.IS_ACTIVE_Y);
//            if (vipRules != null) {
//                dataBean.setId(id);
//                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                dataBean.setMessage("当前企业下该会员类型名称已存在");
//            } else {
//                dataBean.setId(id);
//                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                dataBean.setMessage("当前企业下该会员类型名称不存在");
//            }
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("当前企业下该会员类型名称不存在");
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    //获得会员卡类型  白金卡..钻石卡..金卡等等
    @RequestMapping(value = "/getVipCardTypes", method = RequestMethod.POST)
    @ResponseBody
    public String getVipCardType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();

        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSON.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS)) {
                if (jsonObject.containsKey("corp_code")){
                    corp_code = jsonObject.get("corp_code").toString();
                }else {
                    corp_code = "C10000";
                }
            }
            String search_value = "";
            if (jsonObject.containsKey("searchValue"))
                search_value = jsonObject.get("searchValue").toString();

            JSONObject result = new JSONObject();
            List<VipCardType> list = new ArrayList<VipCardType>();
            if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)){
                list = vipCardTypeService.getVipCardTypes(corp_code, Common.IS_ACTIVE_Y,search_value);

            }else if (role_code.equals(Common.ROLE_AM) || role_code.equals(Common.ROLE_BM)){
                String brand_code = request.getSession().getAttribute("brand_code").toString().replace(Common.SPECIAL_HEAD,"");
                String store_group_code = request.getSession().getAttribute("area_code").toString().replace(Common.SPECIAL_HEAD,"");

                list = vipCardTypeService.getVipCardByRole(corp_code, Common.IS_ACTIVE_Y,brand_code,store_group_code);
            }else {
                String store_code = request.getSession().getAttribute("store_code").toString().replace(Common.SPECIAL_HEAD,"");
                Store store = storeService.getStoreByCode(corp_code,store_code.split(",")[0],Common.IS_ACTIVE_Y);
                if (store != null){
                    String brand_code = store.getBrand_code();
                    String store_group_code = store.getArea_code();
                    list = vipCardTypeService.getVipCardByRole(corp_code, Common.IS_ACTIVE_Y,brand_code,store_group_code);
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

    @RequestMapping(value = "/getHighVipCardTypes", method = RequestMethod.POST)
    @ResponseBody
    public String getHighVipCardType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSON.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String degree = jsonObject.get("degree").toString();
            String type=jsonObject.getString("type");
            JSONObject result = new JSONObject();
            List<VipCardType> list = vipCardTypeService.getVipCardTypes(corp_code, Common.IS_ACTIVE_Y,"");

            List<VipCardType> list1 = new ArrayList<VipCardType>();
            if(StringUtils.isNotBlank(type)) {
                if (type.equals("high")) {
                    for (int i = 0; i < list.size(); i++) {
                        if (Integer.valueOf(list.get(i).getDegree()) > Integer.valueOf(degree)) {
                            list1.add(list.get(i));
                        }
                    }
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        if (Integer.valueOf(list.get(i).getDegree()) < Integer.valueOf(degree)) {
                            list1.add(list.get(i));
                        }
                    }
                }
            }else{
                for (int i = 0; i < list.size(); i++) {
                    if (Integer.valueOf(list.get(i).getDegree()) > Integer.valueOf(degree)) {
                        list1.add(list.get(i));
                    }
                }
            }
            result.put("list", JSON.toJSONString(list1));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/getCardTypesByRole", method = RequestMethod.POST)
    @ResponseBody
    public String getCardTypesByRole(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();

        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSON.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }

            List<VipCardType> list = new ArrayList<VipCardType>();
            if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)){
                //显示企业下所有的
                list = vipCardTypeService.getVipCardTypes(corp_code, Common.IS_ACTIVE_Y,"");
            }else {
                //显示经销商拥有的
                String store_code = jsonObject.get("store_code").toString();
                Store store = storeService.getStoreByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
                if (store != null){
                    String brand_code = store.getBrand_code();
                    String store_group_code = store.getArea_code();
                    list = vipCardTypeService.getVipCardByRole(corp_code, Common.IS_ACTIVE_Y,brand_code,store_group_code);
                }
            }


            JSONObject result = new JSONObject();
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

    /***
     * Execl增加
     */
    @RequestMapping(value = "/addByExecl", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @Transactional()
    @ResponseBody()
    public String addByExecl(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException, UnsupportedEncodingException {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导入失败";
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_code").toString();
        String result = "";
        Workbook rwb = null;
        try {
            rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = 8;//得到所有的列
            int rows = rs.getRows();//得到所有的行

            if (rows < 4) {
                result = "：请从模板第4行开始插入正确数据";
                int i = 5 / 0;
            }
            if (rows > 9999) {
                result = "：数据量过大，导入失败";
                int i = 5 / 0;
            }
            String onlyCell1 = LuploadHelper.CheckOnly(rs.getColumn(1));
            if (onlyCell1.equals("存在重复值")) {
                result = "：Execl中会员卡ID存在重复值";
                int b = 5 / 0;
            }
            String onlyCell2 = LuploadHelper.CheckOnly(rs.getColumn(2));
            if (onlyCell2.equals("存在重复值")) {
                result = "：Execl中会员类型编号存在重复值";
                int b = 5 / 0;
            }
            String onlyCell3 = LuploadHelper.CheckOnly(rs.getColumn(3));
            if (onlyCell3.equals("存在重复值")) {
                result = "：Execl中会员类型名称存在重复值";
                int b = 5 / 0;
            }

//            Cell[] column4 = rs.getColumn(4);
//            Pattern pattern4 = Pattern.compile("(^(\\d{3,4}-)?\\d{7,8})$|(1[3,4,5,7,8]{1}\\d{9})");
//            for (int i = 3; i < column4.length; i++) {
//                if (column4[i].getContents().toString().trim().equals("")) {
//                    continue;
//                }
//                Matcher matcher = pattern4.matcher(column4[i].getContents().toString().trim());
//                if (matcher.matches() == false) {
//                    result = "：第" + (i + 1) + "行电话格式有误";
//                    int b = 5 / 0;
//                    break;
//                }
//            }
            ArrayList<VipCardType> typeArrayList = new ArrayList<VipCardType>();
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    String corp_code = rs.getCell(j++, i).getContents().toString().trim();
                    String vip_card_type_id = rs.getCell(j++, i).getContents().toString().trim();
                    String vip_card_type_code = rs.getCell(j++, i).getContents().toString().trim();
                    String vip_card_type_name = rs.getCell(j++, i).getContents().toString().trim();
                    String degree = rs.getCell(j++, i).getContents().toString().trim();
                    String brand_code = rs.getCell(j++, i).getContents().toString().trim();
                    String store_group_code=rs.getCell(j++,i).getContents().toString().trim();
                    String isactive=rs.getCell(j++,i).getContents().toString().trim();

                    String store_group_code1 = "";
                    if (!store_group_code.equals("")){
                        String[] codes1 = store_group_code.split(",");
                        for (int s = 0; s < codes1.length; s++) {
                            codes1[s] = Common.SPECIAL_HEAD + codes1[s] + ",";
                            store_group_code1 = store_group_code1 + codes1[s];
                        }
                    }
                    String brand_code1 = "";
                    if (!brand_code.equals("")){
                        String[] codes1 = brand_code.split(",");
                        for (int b = 0; b < codes1.length; b++) {
                            codes1[b] = Common.SPECIAL_HEAD + codes1[b] + ",";
                            brand_code1 = brand_code1 + codes1[b];
                        }
                    }

                    if (corp_code.equals("") && vip_card_type_id.equals("") && vip_card_type_code.equals("") && vip_card_type_name.equals("") && degree.equals("")&&
                            brand_code.equals("") && store_group_code.equals("")&&isactive.equals("")) {
                        continue;
                    }
                    if (corp_code.equals("") || vip_card_type_id.equals("") || vip_card_type_code.equals("") || vip_card_type_name.equals("") || degree.equals("")||
                            brand_code.equals("")||isactive.equals("")) {
                        result = "：第" + (i + 1) + "行信息不完整,请参照Execl中对应的批注";
                        int a = 5 / 0;
                    }
                    VipCardType vipCardType = new VipCardType();
                    vipCardType.setCorp_code(corp_code);
                    vipCardType.setVip_card_type_id(vip_card_type_id);
                    vipCardType.setVip_card_type_code(vip_card_type_code);
                    vipCardType.setVip_card_type_name(vip_card_type_name);
                    vipCardType.setDegree(degree);
                    vipCardType.setBrand_code(brand_code1);
                    vipCardType.setStore_group_code(store_group_code1);

                    if (isactive.toUpperCase().equals("N")) {
                        vipCardType.setIsactive("N");
                    } else {
                        vipCardType.setIsactive("Y");
                    }
                    vipCardType.setCreater(user_id);
                    Date now = new Date();
                    vipCardType.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    vipCardType.setModified_date(Common.DATETIME_FORMAT.format(now));
                    vipCardType.setModifier(user_id);
                    //判断导入的数据在数据库是否已存在
                    VipCardType vipCardType1= vipCardTypeService.isExistByType(corp_code,vip_card_type_id,"","");
                    if(vipCardType1!=null){
                        errormessage="第"+i+"行的会员卡ID在数据库中已存在";
                        i=9/0;
                    }
                    VipCardType vipCardType2= vipCardTypeService.isExistByType(corp_code,"",vip_card_type_code,"");
                    if(vipCardType2!=null){
                        errormessage="第"+i+"行的会员类型编号在数据库中已存在";
                        i=9/0;
                    }
                    VipCardType vipCardType3= vipCardTypeService.isExistByType(corp_code,"","",vip_card_type_name);
                    if(vipCardType3!=null){
                        errormessage="第"+i+"行的会员类型名称在数据库中已存在";
                        i=9/0;
                    }
                    typeArrayList.add(vipCardType);
                }
            }
            for (VipCardType vipCardType : typeArrayList) {
                 result=vipCardTypeService.insertVipCardType(vipCardType)+"";
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(errormessage);
        } finally {
            if (rwb != null) {
                rwb.close();
            }
            System.gc();
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/exportExecl",method =RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response){
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String screen=jsonObject.get("list").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String brand_code = request.getSession(false).getAttribute("brand_code").toString();
            String store_group_code = request.getSession(false).getAttribute("area_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<VipCardType> list = null;
            if(!screen.equals("")){
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = vipCardTypeService.getAllVipCardTypeScreen(page_number, page_size, "","","", map);
                } else {
                    if (role_code.equals(Common.ROLE_GM)) {
                        list = vipCardTypeService.getAllVipCardTypeScreen(page_number, page_size, corp_code,"","", map);
                    }else {
                        list = vipCardTypeService.getAllVipCardTypeScreen(page_number, page_size, corp_code,brand_code,store_group_code, map);
                    }
                }
            }else {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = vipCardTypeService.getAllVipCardTypeByPage(page_number, page_size, "", search_value);
                } else {
                    if (role_code.equals(Common.ROLE_GM)) {
                        list = vipCardTypeService.getAllVipCardTypeByPage(page_number, page_size, corp_code, search_value);
                    }else {
                        list = vipCardTypeService.getVipCardByRole(page_number, page_size,corp_code, Common.IS_ACTIVE_Y,brand_code,store_group_code);
                    }
                }
            }
            /**
             * 导出操作..................................................
             */
            List<VipCardType> typeList = list.getList();
            int count = (int)list.getTotal();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(typeList);
            if (typeList.size() >= page_size * 2) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            int start_line = (page_number-1) * page_size + 1;
            int end_line = page_number*page_size;
            if (count < page_number*page_size)
                end_line = count;
            String pathname = OutExeclHelper.OutExecl(json, typeList, map, response, request,"会员类型定义("+start_line+"-"+end_line+")");
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
            dataBean.setId(id);
            dataBean.setMessage(errormessage);
        }

        return  dataBean.getJsonStr();
    }
}
