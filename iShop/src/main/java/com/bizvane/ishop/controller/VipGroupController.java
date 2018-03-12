package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.VipGroup;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.VipGroupService;
import com.bizvane.ishop.service.VipService;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by nanji on 2016/9/1.
 */

@Controller
@RequestMapping("/vipGroup")
public class VipGroupController {
    private static final Logger logger = Logger.getLogger(VipGroupController.class);
    @Autowired
    private VipGroupService vipGroupService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private VipService vipService;

    String id;


    /**
     * 根据id查看
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String selectById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String id = jsonObject.get("id").toString();

            VipGroup vipGroup = vipGroupService.getVipGroupById(Integer.parseInt(id));

            data = JSON.toJSONString(vipGroup);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(data);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 新增
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addVipGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_brand_code = request.getSession().getAttribute("brand_code").toString();
        String user_area_code = request.getSession().getAttribute("area_code").toString();
        String user_store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();

        try {
            String jsString = request.getParameter("param");
            logger.info("json--vipGroup add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject group_obj = JSONObject.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = group_obj.getString("corp_code");
            group_obj.put("corp_code",corp_code);

            VipGroup vipGroup = WebUtils.JSON2Bean(group_obj,VipGroup.class);

            String vip_group_code = "VG"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date());
            vipGroup.setVip_group_code(vip_group_code);
            String result = vipGroupService.insert(vipGroup, role_code,user_brand_code,user_area_code,user_store_code,user_code);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                JSONObject jsonObject = JSONObject.parseObject(message);
                VipGroup vipGroup1 = vipGroupService.getVipGroupByCode(corp_code,vip_group_code,jsonObject.get("isactive").toString());
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(String.valueOf(vipGroup1.getId()));


                //----------------行为日志------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "会员管理_会员分组";
                String action = Common.ACTION_ADD;
                String t_corp_code = corp_code;
                String t_code = vip_group_code;
                String t_name = action_json.get("vip_group_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
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
     * 编辑
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String updateVipGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_brand_code = request.getSession().getAttribute("brand_code").toString();
        String user_area_code = request.getSession().getAttribute("area_code").toString();
        String user_store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json------updateVipGroup---------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject group_obj = JSONObject.parseObject(message);
            int group_id = group_obj.getInteger("id");
            String vip_group_code = vipGroupService.getVipGroupById(group_id).getVip_group_code();
            VipGroup vipGroup = WebUtils.JSON2Bean(group_obj,VipGroup.class);
            String result = vipGroupService.update(vipGroup, role_code,user_brand_code,user_area_code,user_store_code,user_code);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("edit success");

                //----------------行为日志开始------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "会员管理_会员分组";
                String action = Common.ACTION_UPD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = vip_group_code;
                String t_name = action_json.get("vip_group_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
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
    public String deleteVipGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--------deleteVipGroup-------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vipGroup_id = jsonObject.get("id").toString();
            String[] ids = vipGroup_id.split(",");
            String msg = null;
            int count = 0;
            for (int i = 0; i < ids.length; i++) {
                logger.info("inter---------------" + Integer.valueOf(ids[i]));
                VipGroup vipGroupById = vipGroupService.getVipGroupById(Integer.valueOf(ids[i]));
                vipGroupService.delete(Integer.valueOf(ids[i]));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");

                //----------------行为日志开始------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "会员管理_会员分组";
                String action = Common.ACTION_DEL;
                String t_corp_code = vipGroupById.getCorp_code();
                String t_code = vipGroupById.getVip_group_code();
                String t_name = vipGroupById.getVip_group_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 验证会员分组编号唯一性
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/vipGroupCodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String vipGroupCodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_group_code = jsonObject.get("vip_group_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            VipGroup vipGroup = vipGroupService.getVipGroupByCode(corp_code, vip_group_code, Common.IS_ACTIVE_Y);
            if (vipGroup != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("会员分组编号已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("会员分组编号不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);

            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);


        }
        return dataBean.getJsonStr();


    }

    /**
     * 验证会员分组名称唯一性
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/vipGroupNameExist", method = RequestMethod.POST)
    @ResponseBody
    public String vipGroupNameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_group_name = jsonObject.get("vip_group_name").toString();
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();

            List<VipGroup> vipGroup = vipGroupService.getVipGroupByName(corp_code, vip_group_name, Common.IS_ACTIVE_Y);
            if (vipGroup.size() > 0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("会员分组名称已被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("会员分组名称不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);

            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);


        }
        return dataBean.getJsonStr();
    }

    /**
     * 搜索
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String searchVipGroup(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            JSONObject result = new JSONObject();
            PageInfo<VipGroup> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = vipGroupService.getAllVipGroupByPage(page_number, page_size,"", search_value);
            }else {
                list = vipGroupService.getAllVipGroupByPage(page_number, page_size, corp_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();

    }

    /**
     * 筛选
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String vipGroupScreen(HttpServletRequest request) {

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

            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();

            JSONObject result = new JSONObject();
            PageInfo<VipGroup> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = vipGroupService.getAllVipGrouScreen(page_number, page_size, "",map);
            }else {
                list = vipGroupService.getAllVipGrouScreen(page_number, page_size,corp_code,map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage() + ex.toString());
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
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<VipGroup> list = null;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = vipGroupService.getAllVipGroupByPage(1, Common.EXPORTEXECLCOUNT,"", search_value);
                }else {
                    list = vipGroupService.getAllVipGroupByPage(1, Common.EXPORTEXECLCOUNT, corp_code,search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = vipGroupService.getAllVipGrouScreen(1, Common.EXPORTEXECLCOUNT, "", map);
                }else {
                    list = vipGroupService.getAllVipGrouScreen(1, Common.EXPORTEXECLCOUNT,corp_code,map);
                }
            }
            List<VipGroup> vipGroups = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(vipGroups);
            if (vipGroups.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json, vipGroups, map, response, request,"会员");
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

    /**
     * 获取企业下季度，品类
     * @param request
     * @return
     */
    @RequestMapping(value = "/getClassQuarter", method = RequestMethod.POST)
    @ResponseBody
    public String getClassQuarter(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
//            String type = jsonObject.getString("type");

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.getString("corp_code");
            Data data_corp_code= new Data("corp_code",corp_code , ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_corp_code.key, data_corp_code);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("DimCrmPrd", datalist);
            String result = dataBox.data.get("message").value;

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();

    }

    /**
     * 获取企业下会员分组
     * @param request
     * @return
     */
    @RequestMapping(value = "/getCorpGroups", method = RequestMethod.POST)
    @ResponseBody
    public String getCorpGroups(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            String search_value = "";
            if (jsonObject.containsKey("search_value"))
                search_value = jsonObject.get("search_value").toString();

            JSONObject result = new JSONObject();
            List<VipGroup> vipGroups ;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                corp_code = jsonObject.getString("corp_code");
            }
            vipGroups = vipGroupService.selectCorpVipGroups(corp_code,search_value);

            result.put("list", JSON.toJSONString(vipGroups));
            result.put("total", vipGroups.size());

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();

    }


    /**
     * 获取企业下会员分组(包括固定分组)
     * @param request
     * @return
     */
    @RequestMapping(value = "/getCorpGroupsAll", method = RequestMethod.POST)
    @ResponseBody
    public String getCorpGroupsAll(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();

            String search_value = "";
            if (jsonObject.containsKey("search_value"))
                search_value = jsonObject.get("search_value").toString();

            JSONObject result = new JSONObject();
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                corp_code = jsonObject.getString("corp_code");
            }
            List<VipGroup> vipGroups = vipGroupService.selectCorpVipGroups(corp_code,search_value);



            VipGroup vipGroup1 = new VipGroup();
            vipGroup1.setVip_group_code("#L01#");
            vipGroup1.setVip_group_name("高忠诚度");
            vipGroups.add(vipGroup1);

            VipGroup vipGroup2 = new VipGroup();
            vipGroup2.setVip_group_code("#L02#");
            vipGroup2.setVip_group_name("一般忠诚度");
            vipGroups.add(vipGroup2);

            VipGroup vipGroup3 = new VipGroup();
            vipGroup3.setVip_group_code("#L03#");
            vipGroup3.setVip_group_name("低忠诚度");
            vipGroups.add(vipGroup3);

            VipGroup vipGroup4 = new VipGroup();
            vipGroup4.setVip_group_code("#L04#");
            vipGroup4.setVip_group_name("流失顾客");
            vipGroups.add(vipGroup4);

            VipGroup vipGroup21 = new VipGroup();
            vipGroup21.setVip_group_code("#A01#");
            vipGroup21.setVip_group_name("活跃顾客");
            vipGroups.add(vipGroup21);

            VipGroup vipGroup22 = new VipGroup();
            vipGroup22.setVip_group_code("#A02#");
            vipGroup22.setVip_group_name("一般活跃");
            vipGroups.add(vipGroup22);

            VipGroup vipGroup23 = new VipGroup();
            vipGroup23.setVip_group_code("#A03#");
            vipGroup23.setVip_group_name("濒临休眠");
            vipGroups.add(vipGroup23);

            VipGroup vipGroup24 = new VipGroup();
            vipGroup24.setVip_group_code("#A04#");
            vipGroup24.setVip_group_name("休眠顾客");
            vipGroups.add(vipGroup24);

            VipGroup vipGroup31 = new VipGroup();
            vipGroup31.setVip_group_code("#CT01#");
            vipGroup31.setVip_group_name("一日客");
            vipGroups.add(vipGroup31);

            VipGroup vipGroup32 = new VipGroup();
            vipGroup32.setVip_group_code("#CT02#");
            vipGroup32.setVip_group_name("新开未消费");
            vipGroups.add(vipGroup32);

            VipGroup vipGroup33 = new VipGroup();
            vipGroup33.setVip_group_code("#CT03#");
            vipGroup33.setVip_group_name("高粘性顾客");
            vipGroups.add(vipGroup33);


            VipGroup vipGroup41 = new VipGroup();
            vipGroup41.setVip_group_code("#C01#");
            vipGroup41.setVip_group_name("重要发展顾客");
            vipGroups.add(vipGroup41);

            VipGroup vipGroup42 = new VipGroup();
            vipGroup42.setVip_group_code("#C02#");
            vipGroup42.setVip_group_name("重要维持顾客");
            vipGroups.add(vipGroup42);

            VipGroup vipGroup43 = new VipGroup();
            vipGroup43.setVip_group_code("#C03#");
            vipGroup43.setVip_group_name("重要联系顾客");
            vipGroups.add(vipGroup43);

            VipGroup vipGroup44 = new VipGroup();
            vipGroup44.setVip_group_code("#C04#");
            vipGroup44.setVip_group_name("重要流失顾客");
            vipGroups.add(vipGroup44);

            result.put("list", JSON.toJSONString(vipGroups));
            result.put("total", vipGroups.size());

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            logger.info(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();

    }

    /**
     * 查看分组下会员
     * @param request
     * @return
     */
    @RequestMapping(value = "/groupVips", method = RequestMethod.POST)
    @ResponseBody
    public String groupVips(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_brand_code = request.getSession().getAttribute("brand_code").toString();
        String user_area_code = request.getSession().getAttribute("area_code").toString();
        String user_store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String type = jsonObject.get("type").toString();
            String type_view=jsonObject.getString("type_view");

            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                corp_code = jsonObject.getString("corp_code");
            }
            JSONArray screen = new JSONArray();
            JSONObject post_obj = new JSONObject();
            post_obj.put("key",Common.VIP_SCREEN_GROUP_KEY);
            post_obj.put("type","text");
            DataBox dataBox = new DataBox();

            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            if (type.equals("list")){
                if (jsonObject.containsKey("id") && !jsonObject.get("id").toString().equals("")) {
                    id = jsonObject.get("id").toString();
                    VipGroup vipGroup = vipGroupService.getVipGroupById(Integer.parseInt(id));
                    String group_type = vipGroup.getGroup_type();
                    if (group_type.equals("define")){
                        String condition = vipGroup.getGroup_condition();
                        JSONObject condition_obj = JSONObject.parseObject(condition);
                        condition_obj = vipGroupService.vipGroupCustom(condition_obj,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
                        dataBox = iceInterfaceService.VipCustomSearchForWeb(page_num,page_size,corp_code,condition_obj.toString(),"","define","");
                    }else if (group_type.equals("define_v2")){
                        String condition = vipGroup.getGroup_condition();
                        JSONArray condition_array = JSONArray.parseArray(condition);
                        condition_array = vipGroupService.vipScreen2ArrayNew(condition_array,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
                        dataBox = iceInterfaceService.VipCustomSearchForWeb(page_num,page_size,corp_code,condition_array.toJSONString(),"","define_v2","");
                    }else {
                        String vip_group_code = vipGroup.getVip_group_code();
                        post_obj.put("value",vip_group_code);
                        screen.add(post_obj);
                        logger.info("json--------------screen-" + JSON.toJSONString(screen));
                        dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,page_num,page_size,role_code,user_brand_code,user_area_code,user_store_code,user_code,sort_key,sort_value);
                    }

                }else if (jsonObject.containsKey("fixed_code") && !jsonObject.get("fixed_code").toString().equals("")){
                    String fixed_code = jsonObject.get("fixed_code").toString();
                    fixed_code = "#"+fixed_code+"#";
                    post_obj.put("value",fixed_code);
                    screen.add(post_obj);
                    dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,page_num,page_size,role_code,user_brand_code,user_area_code,user_store_code,user_code,sort_key,sort_value);
                }
            }else if(type.equals("view")){
                //图表
                if (jsonObject.containsKey("id") && !jsonObject.get("id").toString().equals("")) {
                    id = jsonObject.get("id").toString();
                    VipGroup vipGroup = vipGroupService.getVipGroupById(Integer.parseInt(id));
                    String group_type = vipGroup.getGroup_type();
                    if (group_type.equals("define")){
                        String condition = vipGroup.getGroup_condition();
                        JSONObject condition_obj = JSONObject.parseObject(condition);
                        condition_obj = vipGroupService.vipGroupCustom(condition_obj,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
                        dataBox = iceInterfaceService.vipCustomGroup(corp_code,condition_obj.toString(),"define",type_view);
                    }else if (group_type.equals("define_v2")){
                        String condition = vipGroup.getGroup_condition();
                        JSONArray condition_array = JSONArray.parseArray(condition);
                        condition_array = vipGroupService.vipScreen2ArrayNew(condition_array,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
                        dataBox = iceInterfaceService.vipCustomGroup(corp_code,condition_array.toJSONString(),"define_v2",type_view);
                    }else {
                        //智能分组
                        String vip_group_code = vipGroup.getVip_group_code();
                        dataBox = iceInterfaceService.vipTagSearchForWeb(corp_code,"",vip_group_code,"year",type_view,"");
                    }
                }else if (jsonObject.containsKey("fixed_code") && !jsonObject.get("fixed_code").toString().equals("")){
                    String fixed_code = jsonObject.get("fixed_code").toString();
                  //  fixed_code = "#"+fixed_code+"#";
                    dataBox = iceInterfaceService.vipTagSearchForWeb(corp_code,"",fixed_code,"year",type_view,"");
                }
            }
            if (dataBox.status.toString().equals("SUCCESS")){
                String result = dataBox.data.get("message").value;
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                if(type.equals("view")){
                    result = dataBox.data.get("message").value;
                    JSONObject object = JSON.parseObject(result);
                    JSONObject object1 = JSONObject.parseObject(object.get("message").toString());
                    object1 = vipService.pareseData1(object1);

                    object.put("message",object1);
                    result = object.toString();
                }
                dataBean.setMessage(result);
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("获取数据失败");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage("获取数据失败");
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 筛选分组下会员
     * @param request
     * @return
     */
    @RequestMapping(value = "/groupVipsScreen", method = RequestMethod.POST)
    @ResponseBody
    public String groupVipsScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_brand_code = request.getSession().getAttribute("brand_code").toString();
        String user_area_code = request.getSession().getAttribute("area_code").toString();
        String user_store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            JSONArray screen = jsonObject.getJSONArray("screen");
            String type = jsonObject.get("type").toString();

            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                corp_code = jsonObject.getString("corp_code");
            }
            DataBox dataBox = new DataBox();
            //String group_key = Common.VIP_SCREEN_GROUP_KEY;
            String group_key="VIP_GROUP_CODE";

            JSONObject post_obj = new JSONObject();
            post_obj.put("key",group_key);
            post_obj.put("type","text");

            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            if (type.equals("list")){
                if (jsonObject.containsKey("id") && !jsonObject.get("id").toString().equals("")) {
                    id = jsonObject.get("id").toString();
                    VipGroup vipGroup = vipGroupService.getVipGroupById(Integer.parseInt(id));

                    String group_type = vipGroup.getGroup_type();
                    if (group_type.equals("define")){//递归格式分组
                        String condition = vipGroup.getGroup_condition();
                        JSONObject condition_obj = JSONObject.parseObject(condition);
                        condition_obj = vipGroupService.vipGroupCustom(condition_obj,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
                       // screen = vipGroupService.vipScreen2Array(screen,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
                        screen = vipGroupService.vipScreen2ArrayNew(screen,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
                        dataBox = iceInterfaceService.VipCustomSearchForWeb(page_num,page_size,corp_code,condition_obj.toString(),JSON.toJSONString(screen),"define","");
                    }else if (group_type.equals("define_v2")){//会员筛选格式分组
                        String condition = vipGroup.getGroup_condition();
                        JSONArray condition_array = JSONArray.parseArray(condition);
                        condition_array = vipGroupService.vipScreen2ArrayNew(condition_array,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
                        // screen = vipGroupService.vipScreen2Array(screen,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
                        screen = vipGroupService.vipScreen2ArrayNew(screen,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
                        dataBox = iceInterfaceService.VipCustomSearchForWeb(page_num,page_size,corp_code,condition_array.toJSONString(),JSON.toJSONString(screen),"define_v2","");
                    }else {
                        //智能分组
                        String vip_group_code = vipGroup.getVip_group_code();
                        post_obj.put("value",vip_group_code);
                        screen.add(post_obj);
                        dataBox = vipGroupService.vipScreenBySolrNew(screen,corp_code,page_num,page_size,role_code,user_brand_code,user_area_code,user_store_code,user_code,sort_key,sort_value);
                      //  dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,page_num,page_size,role_code,user_brand_code,user_area_code,user_store_code,user_code,sort_key,sort_value);
                    }
                }else if (jsonObject.containsKey("fixed_code") && !jsonObject.get("fixed_code").toString().equals("")){
                    String fixed_code = jsonObject.get("fixed_code").toString();
                    fixed_code = "#"+fixed_code+"#";
                    post_obj.put("value",fixed_code);
                    screen.add(post_obj);
                    dataBox = vipGroupService.vipScreenBySolrNew(screen,corp_code,page_num,page_size,role_code,user_brand_code,user_area_code,user_store_code,user_code,sort_key,sort_value);
                   // dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,page_num,page_size,role_code,user_brand_code,user_area_code,user_store_code,user_code,sort_key,sort_value);
                }
            }
            if (dataBox.status.toString().equals("SUCCESS")){
                String result = dataBox.data.get("message").value;
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("fail");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();

    }

    /***
     * 导出数据
     */
    @RequestMapping(value = "/groupVip/exportExcel", method = RequestMethod.POST)
    @ResponseBody
    public String groupVipExportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_corp_code = request.getSession().getAttribute("corp_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String screen_message = jsonObject.get("screen_message").toString();
            String page_num = jsonObject.get("page_num").toString();
            String page_size = jsonObject.get("page_size").toString();

            JSONObject output_message_object = JSONObject.parseObject(message);

            //扩展参数
            String jlist = jsonObject.get("tablemanager").toString();
            JSONArray array = JSONArray.parseArray(jlist);
            String cust_cols = "";
            for (int i = 0; i < array.size(); i++) {
                JSONObject json = array.getJSONObject(i);
                String column_name = json.getString("column_name");
                if (column_name.startsWith("CUST_"))
                    cust_cols = cust_cols + column_name + ",";
                if (column_name.equals("user_name"))
                    cust_cols = cust_cols + "user_code,";
                if (column_name.equals("store_name"))
                    cust_cols = cust_cols + "store_code,";
            }

            String corp_code = user_corp_code;
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = "C10000";
            }
            logger.info("json--------------corp_code-" + corp_code);
            DataBox dataBox = null;
            JSONArray screen = new JSONArray();
            JSONObject post_obj = new JSONObject();
           // post_obj.put("key",Common.VIP_SCREEN_GROUP_KEY);
            post_obj.put("key","VIP_GROUP_CODE");
            post_obj.put("type","text");

            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            if (!screen_message.equals("")){
                screen = JSONArray.parseArray(screen_message);
            }
            JSONObject result2 = new JSONObject();
            if (jsonObject.containsKey("id") && !jsonObject.get("id").toString().equals("")) {
                id = jsonObject.get("id").toString();
                VipGroup vipGroup = vipGroupService.getVipGroupById(Integer.parseInt(id));

                String group_type = vipGroup.getGroup_type();
                if (group_type.equals("define")){
                    String condition = vipGroup.getGroup_condition();
                    JSONObject condition_obj = JSONObject.parseObject(condition);
                    condition_obj = vipGroupService.vipGroupCustom(condition_obj,corp_code,role_code,brand_code,area_code,store_code,user_code);

                    screen = vipGroupService.vipScreen2ArrayNew(screen,corp_code,role_code,brand_code,area_code,store_code,user_code);

                    dataBox = iceInterfaceService.VipCustomSearchForWeb(page_num,page_size,corp_code,condition_obj.toString(),screen.toJSONString(),"define",cust_cols);

                    String result = dataBox.data.get("message").value;
                    JSONObject object = JSONObject.parseObject(result);
                    JSONArray jsonArray = JSONArray.parseArray(object.get("all_vip_list").toString());
                    int count = object.getInteger("count");

                    List list = WebUtils.Json2List2(jsonArray);
                    if (list.size() >= 10000) {
                        errormessage = "导出数据过大";
                        int i = 9 / 0;
                    }
                    int pageNum = Integer.parseInt(page_num);
                    int pageSize = Integer.parseInt(page_size);
                    int start_line = (pageNum-1) * pageSize + 1;
                    int end_line = pageNum*pageSize;
                    if (count < pageNum*pageSize)
                        end_line = count;

                    LinkedHashMap<String, String> map = WebUtils.Json2ShowName(output_message_object);
                    String pathname = OutExeclHelper.OutExecl_vip(jsonArray, list, map, response, request,"分组会员("+start_line+"-"+end_line+")");
                    result2.put("path", JSON.toJSONString("lupload/" + pathname));
                }else if (group_type.equals("define_v2")){
                    String condition = vipGroup.getGroup_condition();
                    JSONArray condition_array = JSONArray.parseArray(condition);
                    condition_array = vipGroupService.vipScreen2ArrayNew(condition_array,corp_code,role_code,brand_code,area_code,store_code,user_code);

                    screen = vipGroupService.vipScreen2ArrayNew(screen,corp_code,role_code,brand_code,area_code,store_code,user_code);

                    dataBox = iceInterfaceService.VipCustomSearchForWeb(page_num,page_size,corp_code,condition_array.toJSONString(),screen.toJSONString(),"define_v2",cust_cols);

                    String result = dataBox.data.get("message").value;
                    JSONObject object = JSONObject.parseObject(result);
                    JSONArray jsonArray = JSONArray.parseArray(object.get("all_vip_list").toString());
                    int count = object.getInteger("count");

                    List list = WebUtils.Json2List2(jsonArray);
                    if (list.size() >= 10000) {
                        errormessage = "导出数据过大";
                        int i = 9 / 0;
                    }
                    int pageNum = Integer.parseInt(page_num);
                    int pageSize = Integer.parseInt(page_size);
                    int start_line = (pageNum-1) * pageSize + 1;
                    int end_line = pageNum*pageSize;
                    if (count < pageNum*pageSize)
                        end_line = count;

                    LinkedHashMap<String, String> map = WebUtils.Json2ShowName(output_message_object);
                    String pathname = OutExeclHelper.OutExecl_vip(jsonArray, list, map, response, request,"分组会员("+start_line+"-"+end_line+")");
                    result2.put("path", JSON.toJSONString("lupload/" + pathname));

                }else {
                    //智能分组
                    String columnName = "";
                    String showName = "";
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject json = array.getJSONObject(i);
                        columnName += json.get("column_name").toString()+",";
                        showName += json.get("show_name").toString()+",";
                    }
                    if (columnName.endsWith(","))
                        columnName = columnName.substring(0,columnName.length()-1);
                    if (showName.endsWith(","))
                        showName = showName.substring(0,showName.length()-1);

                    String vip_group_code = vipGroup.getVip_group_code();
                    post_obj.put("value",vip_group_code);
                    screen.add(post_obj);
                    logger.info("json--------------screen-" + JSON.toJSONString(screen));
                  //  dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,page_num,page_size,role_code,brand_code,area_code,store_code,user_code,sort_key,sort_value);
               //      dataBox = vipGroupService.vipScreenBySolrNew(screen,corp_code,page_num,page_size,role_code,brand_code,area_code,store_code,user_code,sort_key,sort_value);

                    JSONArray post_array = vipGroupService.vipScreen2ArrayNew(screen,corp_code,role_code,brand_code,area_code,store_code,user_code);
                    dataBox = iceInterfaceService.vipScreen2ExeclMethod(page_num,page_size,corp_code,JSON.toJSONString(post_array),sort_key,sort_value,cust_cols,columnName,showName,user_corp_code+"&&"+user_code);

                    String result = dataBox.data.get("message").value;
                    JSONObject object = JSONObject.parseObject(result);

                    result2.put("path", object.get("oss_url").toString());
                    result2.put("path_type","oss");
                }

            }else if (jsonObject.containsKey("fixed_code") && !jsonObject.get("fixed_code").toString().equals("")){
                String columnName = "";
                String showName = "";
                for (int i = 0; i < array.size(); i++) {
                    JSONObject json = array.getJSONObject(i);
                    columnName += json.get("column_name").toString()+",";
                    showName += json.get("show_name").toString()+",";
                }
                if (columnName.endsWith(","))
                    columnName = columnName.substring(0,columnName.length()-1);
                if (showName.endsWith(","))
                    showName = showName.substring(0,showName.length()-1);

                String fixed_code = jsonObject.get("fixed_code").toString();
                fixed_code = "#"+fixed_code+"#";
                post_obj.put("value",fixed_code);
                screen.add(post_obj);
               // dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,page_num,page_size,role_code,brand_code,area_code,store_code,user_code,sort_key,sort_value);
          //      dataBox = vipGroupService.vipScreenBySolrNew(screen,corp_code,page_num,page_size,role_code,brand_code,area_code,store_code,user_code,sort_key,sort_value);

                JSONArray post_array = vipGroupService.vipScreen2ArrayNew(screen,corp_code,role_code,brand_code,area_code,store_code,user_code);
                dataBox = iceInterfaceService.vipScreen2ExeclMethod(page_num,page_size,corp_code,JSON.toJSONString(post_array),sort_key,sort_value,cust_cols,columnName,showName,user_corp_code+"&&"+user_code);

                String result = dataBox.data.get("message").value;
                JSONObject object = JSONObject.parseObject(result);

                result2.put("path", object.get("oss_url").toString());
                result2.put("path_type","oss");
            }


            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result2.toString());
        }catch (Ice.MemoryLimitException im){
            System.out.println("===============ice异常========================================");
            errormessage = "导出数据过大,请筛选后导出";
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
            im.printStackTrace();
        }catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }
}
