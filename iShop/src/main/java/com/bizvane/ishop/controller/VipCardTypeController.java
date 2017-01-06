package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.VipCardType;
import com.bizvane.ishop.entity.VipRules;
import com.bizvane.ishop.service.VipCardTypeService;
import com.bizvane.ishop.service.VipRulesService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            List<VipRules> list = null;
            for (int i = 0; i < ids.length; i++) {
                VipCardType vipCardType = vipCardTypeService.getVipCardTypeById(Integer.parseInt(ids[i]));
                if (vipCardType != null) {
                    list = vipRulesService.getViprulesList(vipCardType.getCorp_code(), Common.IS_ACTIVE_Y);

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

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<VipCardType> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = vipCardTypeService.getAllVipCardTypeByPage(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = this.vipCardTypeService.getAllVipCardTypeByPage(page_number, page_size, "", search_value);
                } else {
                    list = vipCardTypeService.getAllVipCardTypeByPage(page_number, page_size, corp_code, search_value);
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
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<VipCardType> list = null;
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipCardTypeService.getAllVipCardTypeScreen(page_number, page_size, "", map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = vipCardTypeService.getAllVipCardTypeScreen(page_number, page_size, corp_code, map);
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
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_card_type_name = jsonObject.get("vip_card_type_name").toString().trim();
            String corp_code = jsonObject.get("corp_code").toString();
            VipCardType vipRules = vipCardTypeService.getVipCardTypeByName(corp_code, vip_card_type_name, Common.IS_ACTIVE_Y);
            if (vipRules != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("当前企业下该会员类型名称已存在");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("当前企业下该会员类型名称不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/getVipCardTypes", method = RequestMethod.POST)
    @ResponseBody
    public String getVipCardType(HttpServletRequest request) {
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
            JSONObject result = new JSONObject();
            List<VipCardType> list = vipCardTypeService.getVipCardTypes(corp_code, Common.IS_ACTIVE_Y);
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
            JSONObject result = new JSONObject();
            List<VipCardType> list = vipCardTypeService.getVipCardTypes(corp_code, Common.IS_ACTIVE_Y);

            List<VipCardType> list1 = new ArrayList<VipCardType>();
            for (int i = 0; i < list.size(); i++) {
                if (Integer.valueOf(list.get(i).getDegree()) > Integer.valueOf(degree)) {
                    list1.add(list.get(i));
                }
            }
            result.put("list", JSON.toJSONString(list1));
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
