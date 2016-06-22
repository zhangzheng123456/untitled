package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.System;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by zhouying on 2016-04-20.
 */

/**
 * 店铺管理
 */

@Controller
@RequestMapping("/shop")
public class StoreController {

    private static final Logger logger = Logger.getLogger(StoreController.class);


    String id;

    @Autowired
    private StoreService storeService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private FunctionService functionService;

    SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);

    /**
     * 店铺管理
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String shopManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String user_id = request.getSession().getAttribute("user_id").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONArray actions = functionService.selectActionByFun(corp_code+user_code,corp_code+group_code, role_code, function_code);

            JSONObject result = new JSONObject();
            PageInfo<Store> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = storeService.getAllStore(page_number, page_size, "", "");
            } else {
                if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStore(page_number, page_size, corp_code, "");
                } else {
                    list = storeService.selectByUserId(page_number, page_size, user_id, corp_code, "");
                }
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
     * 新增
     */
    @RequestMapping(value = "/corp_exist", method = RequestMethod.POST)
    @ResponseBody
    public String corpExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {

            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject msg = new JSONObject(message);
            String corp_code = msg.get("corp_code").toString();
            Corp corp = corpService.selectByCorpId(0, corp_code);
            if (corp == null) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("该企业编号不存在！");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("企业编号可用");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }

    /**
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addShop(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = storeService.insert(message, user_id);
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
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editShop(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_id").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = storeService.update(message, user_id);
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
            dataBean.setId(id);
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
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String shop_id = jsonObject.get("id").toString();
            String[] ids = shop_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                logger.info("inter---------------" + Integer.valueOf(ids[i]));
                Store store = storeService.getStoreById(Integer.valueOf(ids[i]));
                String store_code = store.getStore_code();
                String corp_code = store.getCorp_code();
                List<User> user = storeService.getStoreUser(corp_code, store_code);
                if (user.size() == 0) {
                    storeService.delete(Integer.valueOf(ids[i]));
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage("success");
                } else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("店铺"+store_code+"下有所属员工，请先处理店铺下员工再删除！");
                    return dataBean.getJsonStr();
                }
            }
        } catch (Exception ex) {
            //	return "Error deleting the user:" + ex.toString();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 选择店铺
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String shop_id = jsonObject.get("id").toString();
            data = JSON.toJSONString(storeService.getStoreById(Integer.parseInt(shop_id)));
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
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            String user_id = request.getSession().getAttribute("user_id").toString();
            JSONObject result = new JSONObject();
            PageInfo<Store> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = storeService.getAllStore(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_GM)) {
                    list = storeService.getAllStore(page_number, page_size, corp_code, search_value);
                } else {
                    list = storeService.selectByUserId(page_number, page_size, user_id, corp_code, search_value);
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

    @RequestMapping(value = "/brand", method = RequestMethod.POST)
    @ResponseBody
    public String getBrand(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            List<Brand> brand = brandService.getAllBrand(corp_code);
            JSONArray array = new JSONArray();
            JSONObject brands = new JSONObject();
            for (int i = 0; i < brand.size(); i++) {
                Brand brand1 = brand.get(i);
                String brand_code = brand1.getBrand_code();
                String brand_name = brand1.getBrand_name();
                JSONObject obj = new JSONObject();
                obj.put("brand_code", brand_code);
                obj.put("brand_name", brand_name);
                array.add(obj);
            }
            brands.put("brands", array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(brands.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/area", method = RequestMethod.POST)
    @ResponseBody
    public String getArea(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            List<Area> area = areaService.getAllArea(corp_code);
            JSONArray array = new JSONArray();
            JSONObject areas = new JSONObject();
            for (int i = 0; i < area.size(); i++) {
                Area area1 = area.get(i);
                String area_code = area1.getArea_code();
                String area_name = area1.getArea_name();
                JSONObject obj = new JSONObject();
                obj.put("area_code", area_code);
                obj.put("area_name", area_name);
                array.add(obj);
            }
            areas.put("areas", array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(areas.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/staff", method = RequestMethod.POST)
    @ResponseBody
    public String getStaff(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String store_code = jsonObject.get("store_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();

            List<User> user = storeService.getStoreUser(corp_code, store_code);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(JSON.toJSONString(user));
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/Store_CodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String Store_CodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String store_code = jsonObject.get("store_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            //         Area area = areaService.getAreaByName(corp_code, store_code);
            Store store = storeService.getStoreByCode(corp_code, store_code,"");

            if (store != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("店铺编号已被使用！！！");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("店铺编号不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/Store_NameExist", method = RequestMethod.POST)
    @ResponseBody
    public String Store_NameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String store_name = jsonObject.get("store_name").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            //         Area area = areaService.getAreaByName(corp_code, store_code);
            Store store = storeService.getStoreByName(corp_code, store_name);

            if (store != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("店铺名称已被使用！！！");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("店铺名称不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }
}
