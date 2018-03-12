

package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.BrandMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.entity.VipLabel;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/VIP/label")
public class VIPLabelController {

    @Autowired
    private VipLabelService vipLabelService;
    @Autowired
    private ViplableGroupService viplableGroupService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    private BaseService baseService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    IceInterfaceService iceInterfaceService;
    private static final Logger log = Logger.getLogger(VIPLabelController.class);

    String id;

    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            int page_num = Integer.parseInt(jsonObject.get("page_num").toString());
            int page_size = Integer.parseInt(jsonObject.get("page_size").toString());

            String screen = jsonObject.get("list").toString();
            PageInfo<VipLabel> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = vipLabelService.selectBySearch(page_num, page_size, "", search_value);
                } else {
                    list = vipLabelService.selectBySearch(page_num, page_size, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = this.vipLabelService.selectAllVipScreen(page_num, page_size, "", map, null);
                } else {
                    list = this.vipLabelService.selectAllVipScreen(page_num, page_size, corp_code, map, null);
                }
            }
            List<VipLabel> vipLabels = list.getList();
            int count = (int) list.getTotal();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(vipLabels);
            if (vipLabels.size() >= page_size * 2) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            int start_line = (page_num - 1) * page_size + 1;
            int end_line = page_num * page_size;
            if (count < page_num * page_size)
                end_line = count;

            String pathname = OutExeclHelper.OutExecl(json, vipLabels, map, response, request, "标签定义(" + start_line + "-" + end_line + ")");
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
     * 会员标签管理
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addVIPLabel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String corp_code = request.getSession(false).getAttribute("corp_code").toString();
        String role_code = request.getSession(false).getAttribute("role_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            VipLabel vipLabel = WebUtils.JSON2Bean(jsonObject, VipLabel.class);
            Date now = new Date();
            vipLabel.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipLabel.setModifier(user_id);
            vipLabel.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipLabel.setCreater(user_id);
            if (Common.ROLE_SYS.equals(role_code) && corp_code.equals(vipLabel.getCorp_code())) {
                vipLabel.setLabel_type("sys");
            } else if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_BM) || role_code.equals(Common.ROLE_SYS)) {
                vipLabel.setLabel_type("org");
            } else {
                vipLabel.setLabel_type("user");
            }
            String aa = "";
            if (null != vipLabel.getBrand_code() && !vipLabel.getBrand_code().equals("")) {
                String[] brand = vipLabel.getBrand_code().split(",");
                for (int i = 0; i < brand.length; i++) {
                    brand[i] = Common.SPECIAL_HEAD + brand[i] + ",";
                    aa = aa + brand[i];
                }
            }
            vipLabel.setBrand_code(aa);
            List<VipLabel> vipLabelList = vipLabelService.VipLabelNameExist(vipLabel.getCorp_code(), vipLabel.getLabel_name(), vipLabel.getBrand_code());
            if (vipLabelList.size() == 0) {
                List<ViplableGroup> viplableGroups1 = viplableGroupService.checkNameOnly(vipLabel.getCorp_code(), "默认分组", Common.IS_ACTIVE_Y);
                List<ViplableGroup> viplableGroups2 = viplableGroupService.checkCodeOnly(vipLabel.getCorp_code(), "0001", Common.IS_ACTIVE_Y);
                if (viplableGroups1.size() == 0 && viplableGroups2.size() == 0) {
                    ViplableGroup viplableGroup = new ViplableGroup();
                    viplableGroup.setCorp_code(vipLabel.getCorp_code());
                    viplableGroup.setLabel_group_code("0001");
                    viplableGroup.setLabel_group_name("默认分组");
                    viplableGroup.setRemark("默认分组");
                    viplableGroup.setIsactive("Y");
                    Date date = new Date();
                    viplableGroup.setCreated_date(Common.DATETIME_FORMAT.format(date));
                    viplableGroup.setCreater(user_id);
                    viplableGroup.setModified_date(Common.DATETIME_FORMAT.format(date));
                    viplableGroup.setModifier(user_id);
                    viplableGroupService.addViplableGroup(viplableGroup);
                }
            }
            String existInfo = vipLabelService.insert(vipLabel);
            if (existInfo.contains(Common.DATABEAN_CODE_SUCCESS)) {
                List<VipLabel> vipLabels = vipLabelService.selectViplabelByName(vipLabel.getCorp_code(), vipLabel.getLabel_name(), vipLabel.getIsactive());
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(String.valueOf(vipLabels.get(0).getId()));

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
                JSONObject action_json = JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "会员管理_会员标签";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("label_group_code").toString();
                String t_name = action_json.get("label_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("标签名称已存在");
            }

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员标签管理
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editVIPLabel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String role_code = request.getSession(false).getAttribute("role_code").toString();

        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            VipLabel vipLabel = WebUtils.JSON2Bean(jsonObject, VipLabel.class);
            int label_id = vipLabel.getId();
            String label_type = vipLabelService.getVipLabelById(label_id).getLabel_type();

            String aa = "";
            if (null != vipLabel.getBrand_code() && !vipLabel.getBrand_code().equals("")) {
                String[] brand = vipLabel.getBrand_code().split(",");
                for (int i = 0; i < brand.length; i++) {
                    brand[i] = Common.SPECIAL_HEAD + brand[i] + ",";
                    aa = aa + brand[i];
                }
            }
            vipLabel.setBrand_code(aa);
            if (!role_code.equals(Common.ROLE_SYS) && label_type.equals(Common.VIP_LABEL_TYPE_SYS)) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("对不起，您不可以修改系统标签");
            } else {
                Date now = new Date();
                vipLabel.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipLabel.setModifier(user_id);

                String result = vipLabelService.update(vipLabel);
                if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage("更改成功");

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
                    String function = "会员管理_会员标签";
                    String action = Common.ACTION_UPD;
                    String t_corp_code = action_json.get("corp_code").toString();
                    String t_code = action_json.get("label_group_code").toString();
                    String t_name = action_json.get("label_name").toString();
                    String remark = "";
                    baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                    //-------------------行为日志结束-----------------------------------------------------------------------------------
                } else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage(result);
                }
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("会员标签更新失败");
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
//

    /**
     * 编辑标签前，获取数据
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findVipLabelById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int vip_tag_id = Integer.parseInt(jsonObject.getString("id"));
            VipLabel vipLabel = vipLabelService.getVipLabelById(vip_tag_id);
            data = JSON.toJSONString(vipLabel);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(data);
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员标签类型管理
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String findVIPLabelDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String messageType_id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String[] ids = jsonObject.get("id").toString().split(",");
            for (int i = 0; ids != null && i < ids.length; i++) {
                VipLabel vipLabelById = vipLabelService.getVipLabelById(Integer.parseInt(ids[i]));
                if (vipLabelById != null) {
                    vipLabelService.delAllRelViplabel(ids[i]);
                }
                this.vipLabelService.delete(Integer.parseInt(ids[i]));
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
                String function = "会员管理_会员标签";
                String action = Common.ACTION_DEL;
                String t_corp_code = vipLabelById.getCorp_code();
                String t_code = vipLabelById.getLabel_group_code();
                String t_name = vipLabelById.getLabel_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员标签管理
     * 判断企业内标签名称是否唯一
     */
    @RequestMapping(value = "/VipLabelNameExist")
    @ResponseBody
    public String VipLabelNameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObject1 = JSONObject.parseObject(jsString);
            String message = jsonObject1.get("message").toString();
            JSONObject jsonObject2 = JSONObject.parseObject(message);
            String label_name = jsonObject2.getString("label_name");
            String corp_code = jsonObject2.getString("corp_code");
            String brand_code = jsonObject2.getString("brand_code");
            List<VipLabel> existInfo = this.vipLabelService.VipLabelNameExist(corp_code, label_name, brand_code);

            if (existInfo.size() == 0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("标签名称未被使用");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("标签名称已存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员标签管理
     * 查找
     */
    @RequestMapping(value = "/find", method = RequestMethod.POST)
    @ResponseBody
    public String findVIPLabelFind(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_Number = jsonObject.getInteger("pageNumber");
            int page_Size = jsonObject.getInteger("pageSize");
            String search_value = jsonObject.getString("searchValue").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<VipLabel> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.vipLabelService.selectBySearch(page_Number, page_Size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = this.vipLabelService.selectBySearch(page_Number, page_Size, corp_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员标签管理
     * 筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String selectAllVipScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_Number = jsonObject.getInteger("pageNumber");
            int page_Size = jsonObject.getInteger("pageSize");
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();

            JSONObject result = new JSONObject();
//            List<Brand> list1;
            PageInfo<VipLabel> list_label = new PageInfo<VipLabel>();
//            if (role_code.equals(Common.ROLE_SYS)) {
//                PageInfo<Brand> allBrandByPage = brandService.getAllBrandByPage(1, 20, "", "","");
//                list1 = allBrandByPage.getList();
//            }else{
//                String corp_code = request.getSession().getAttribute("corp_code").toString();
//                PageInfo<Brand> allBrandByPage =  brandService.getAllBrandByPage(1,20,corp_code,"","");
//                list1 = allBrandByPage.getList();
//            }
            Set<String> set = map.keySet();
            if (set.contains("brand_name") && !map.get("brand_name").replace("'", "").equals("")) {
                Map<String, Object> map_brand = new HashMap<String, Object>();
                if (role_code.equals(Common.ROLE_SYS)) {
                    map_brand.put("corp_code", null);
                } else {
                    String corp_code = request.getSession().getAttribute("corp_code").toString();
                    map_brand.put("corp_code", corp_code);
                }
                map_brand.put("brand_code", null);
                map_brand.put("search_value", map.get("brand_name").replace("'", ""));
                List<Brand> brands = brandMapper.selectBrands(map_brand);
                System.out.println("------brands-----------" + brands.size());
                if (brands.size() > 0) {
                    String brand_code_json = "";
                    String[] brand = null;
                    for (Brand brand1 : brands) {
                        brand_code_json += brand1.getBrand_code() + ",";
                    }
                    System.out.println("-----brand_code_json-------" + brand_code_json);
                    brand = brand_code_json.split(",");
                    for (int i = 0; i < brand.length; i++) {
                        brand[i] = Common.SPECIAL_HEAD + brand[i] + ",";
                    }
                    if (role_code.equals(Common.ROLE_SYS)) {
                        list_label = this.vipLabelService.selectAllVipScreen(page_Number, page_Size, "", map, brand);
                    } else {
                        String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                        list_label = this.vipLabelService.selectAllVipScreen(page_Number, page_Size, corp_code, map, brand);
                    }
                } else {
                    list_label = this.vipLabelService.selectAllVipScreen(page_Number, page_Size, "23###112##sss", map, null);
                }
            } else {
                if (role_code.equals(Common.ROLE_SYS)) {
                    list_label = this.vipLabelService.selectAllVipScreen(page_Number, page_Size, "", map, null);
                } else {
                    String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                    list_label = this.vipLabelService.selectAllVipScreen(page_Number, page_Size, corp_code, map, null);
                }
            }
            result.put("list", JSON.toJSONString(list_label));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //热门标签
    @RequestMapping(value = "/findHotViplabel", method = RequestMethod.POST)
    @ResponseBody
    public String findHotViplabel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code").toString();
            String vip_id = jsonObject.get("vip_id").toString();
            JSONObject result = new JSONObject();

            String brand_new = "";
            List<Brand> brandByUser = baseService.getBrandByUser(request, corp_code);
            for (int i = 0; i < brandByUser.size(); i++) {
                Brand brand = brandByUser.get(i);
                String brand_code = brand.getBrand_code();
                if (null != brand_code && !"".equals(brand_code)) {
                    brand_code = Common.SPECIAL_HEAD + brand_code + ",";
                    brand_new = brand_new + brand_code;
                }
            }
            System.out.println("======findHotViplabel=======>" + brand_new);
            List<VipLabel> hotViplabel = new ArrayList<VipLabel>();
            if (!brand_new.equals("")) {
                hotViplabel = vipLabelService.findHotViplabel(corp_code, brand_new);
            }
            //  List<VipLabel> vipLabelList = vipLabelService.selectLabelByVip(corp_code, vip_id);
            List<VipLabel> vipLabelList = vipLabelService.selectLabelByVipToHbase(corp_code, vip_id);

            for (VipLabel vipLabel : hotViplabel) {
                vipLabel.setLabel_sign("N");
            }
            for (int i = 0; i < vipLabelList.size(); i++) {
                for (int j = 0; j < hotViplabel.size(); j++) {
                    if (vipLabelList.get(i).getLabel_name().equals(hotViplabel.get(j).getLabel_name())) {
                        hotViplabel.get(j).setLabel_sign("Y");
                    }
                }
            }
            result.put("list", JSON.toJSONString(hotViplabel));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/findViplabelByType", method = RequestMethod.POST)
    @ResponseBody
    public String findViplabelByType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_Number = jsonObject.getInteger("pageNumber");
            int page_Size = 50;
            String search_value = jsonObject.getString("searchValue").toString();
            String type = jsonObject.getString("type").toString();
            String corp_code = jsonObject.getString("corp_code").toString();
            String vip_id = jsonObject.get("vip_id").toString();
            //   List<VipLabel> vipLabelList = vipLabelService.selectLabelByVip(corp_code, vip_id);
            List<VipLabel> vipLabelList = vipLabelService.selectLabelByVipToHbase(corp_code, vip_id);

            JSONObject result = new JSONObject();
            PageInfo<VipLabel> list = null;
            String brand_new = "";
            List<Brand> brandByUser = baseService.getBrandByUser(request, corp_code);
            for (int i = 0; i < brandByUser.size(); i++) {
                Brand brand = brandByUser.get(i);
                String brand_code = brand.getBrand_code();
                if (null != brand_code && !"".equals(brand_code)) {
                    brand_code = Common.SPECIAL_HEAD + brand_code + ",";
                    brand_new = brand_new + brand_code;
                }
            }

            //搜索框(1)  企业标签（2）用户（3）
            if (type.equals("1")) {
                list = vipLabelService.findViplabelByType(page_Number, page_Size, corp_code, "", search_value, brand_new);
            } else if (type.equals("2")) {
                list = vipLabelService.findViplabelByType(page_Number, page_Size, corp_code, "org", "", brand_new);
                for (VipLabel vipLabel : list.getList()) {
                    vipLabel.setLabel_sign("N");
                }
                for (int i = 0; i < vipLabelList.size(); i++) {
                    for (int j = 0; j < list.getList().size(); j++) {
                        if (vipLabelList.get(i).getLabel_name().equals(list.getList().get(j).getLabel_name())) {
                            list.getList().get(j).setLabel_sign("Y");
                        }
                    }
                }
            } else if (type.equals("3")) {
                list = vipLabelService.findViplabelByType(page_Number, page_Size, corp_code, "user", "", brand_new);
                for (VipLabel vipLabel : list.getList()) {
                    vipLabel.setLabel_sign("N");
                }
                for (int i = 0; i < vipLabelList.size(); i++) {
                    for (int j = 0; j < list.getList().size(); j++) {
                        if (vipLabelList.get(i).getLabel_name().equals(list.getList().get(j).getLabel_name())) {
                            list.getList().get(j).setLabel_sign("Y");
                        }
                    }
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
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/addBatchRelViplabel", method = RequestMethod.POST)
    @ResponseBody
    public String addBitchRelViplabel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code1 = request.getSession().getAttribute("corp_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String vip_code_list = jsonObject.get("list").toString();
            JSONArray jsonArray = JSON.parseArray(vip_code_list);
            String store_code = "";
            String vip_code = "";
            for (int i = 0; i < jsonArray.size(); i++) {
                String str = jsonArray.get(i).toString();
                JSONObject object = JSON.parseObject(str);
                String store_code_object = object.get("store_code").toString();
                String vip_code_object = object.get("vip_code").toString();

                store_code += store_code_object + ",";
                vip_code += vip_code_object + ",";
            }
            String[] split_store_code = store_code.split(",");
            String[] split_vip_code = vip_code.split(",");

            String brand_new = "";
            List<Brand> brandByUser = baseService.getBrandByUser(request, corp_code);
            for (int i = 0; i < brandByUser.size(); i++) {
                Brand brand = brandByUser.get(i);
                String brand_code = brand.getBrand_code();
                if (null != brand_code && !"".equals(brand_code)) {
                    brand_code = Common.SPECIAL_HEAD + brand_code + ",";
                    brand_new = brand_new + brand_code;
                }
            }

            String label_name = jsonObject.getString("label_name").toString();
            String user_id = request.getSession().getAttribute("user_code").toString();
            JSONObject result = new JSONObject();
            List<VipLabel> vipLabelList = vipLabelService.VipLabelNameExist(corp_code, label_name, brand_new);
            if (vipLabelList.size() == 0) {
                VipLabel vipLabel = WebUtils.JSON2Bean(jsonObject, VipLabel.class);
                vipLabel.setLabel_group_code("0001");
                Date now = new Date();
                vipLabel.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipLabel.setModifier(user_id);
                vipLabel.setCreated_date(Common.DATETIME_FORMAT.format(now));
                vipLabel.setCreater(user_id);

                String role_code = request.getSession(false).getAttribute("role_code").toString();
                if (Common.ROLE_SYS.equals(role_code) && corp_code1.equals(corp_code)) {
                    vipLabel.setLabel_type("sys");
                } else if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_BM) || role_code.equals(Common.ROLE_SYS)) {
                    vipLabel.setLabel_type("org");
                } else {
                    vipLabel.setLabel_type("user");
                }
                vipLabel.setBrand_code(brand_new);
                String existInfo2 = vipLabelService.insert(vipLabel);
                int count = 0;
                if (existInfo2.contains(Common.DATABEAN_CODE_SUCCESS)) {
                    for (int i = 0; i < split_vip_code.length; i++) {
                        List<VipLabel> viplabelID = vipLabelService.findViplabelID(corp_code, label_name);
                        String label_id2 = viplabelID.get(0).getId() + "";
                        vipLabelService.addRelViplabelToHbase(label_id2, corp_code, split_vip_code[i], split_store_code[i], user_id);
                        // vipLabelService.addRelViplabel(label_id2, corp_code, split_vip_code[i], split_store_code[i], user_id);
                    }
                    result.put("list", "新增成功:" + count);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage(result.toString());
                }
            } else {
                VipLabel vipLabel = vipLabelList.get(0);
                if (!vipLabel.getIsactive().equals(Common.IS_ACTIVE_Y)) {
                    vipLabel.setIsactive(Common.IS_ACTIVE_Y);
                    vipLabelService.update(vipLabel);
                }
                String label_id = String.valueOf(vipLabel.getId());
                for (int i = 0; i < split_vip_code.length; i++) {
                    int relViplabels = vipLabelService.checkRelViplablelToHbase(corp_code, split_vip_code[i], label_id);
                    if (relViplabels == 0) {
                        vipLabelService.addRelViplabelToHbase(label_id, corp_code, split_vip_code[i], split_store_code[i], user_id);
                        //vipLabelService.addRelViplabel(label_id, corp_code, split_vip_code[i], split_store_code[i], user_id);
                    }
                }
                result.put("list", "新增成功");
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(result.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/addRelViplabel", method = RequestMethod.POST)
    @ResponseBody
    public String checkRelViplablel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code1 = request.getSession().getAttribute("corp_code").toString();
        String user_id = request.getSession().getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code").toString();
            String vip_code = jsonObject.getString("vip_code").toString();
            String label_name = jsonObject.getString("label_name").toString();
            String store_code = jsonObject.getString("store_code").toString();
            JSONObject result = new JSONObject();
            String result_add = "";
            String brand_new = "";
            List<Brand> brandByUser = baseService.getBrandByUser(request, corp_code);
            for (int i = 0; i < brandByUser.size(); i++) {
                Brand brand = brandByUser.get(i);
                String brand_code = brand.getBrand_code();
                if (null != brand_code && !"".equals(brand_code)) {
                    brand_code = Common.SPECIAL_HEAD + brand_code + ",";
                    brand_new = brand_new + brand_code;
                }
            }
            List<VipLabel> vipLabelList = vipLabelService.VipLabelNameExist(corp_code, label_name, brand_new);
            System.out.println(brand_new + "-----------" + vipLabelList.size());
            if (vipLabelList.size() == 0) {
                VipLabel vipLabel = WebUtils.JSON2Bean(jsonObject, VipLabel.class);
                vipLabel.setLabel_group_code("0001");
                Date now = new Date();
                vipLabel.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipLabel.setModifier(user_id);
                vipLabel.setCreated_date(Common.DATETIME_FORMAT.format(now));
                vipLabel.setCreater(user_id);

                String role_code = request.getSession(false).getAttribute("role_code").toString();
                if (Common.ROLE_SYS.equals(role_code) && corp_code1.equals(corp_code)) {
                    vipLabel.setLabel_type("sys");
                } else if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_BM) || role_code.equals(Common.ROLE_SYS)) {
                    vipLabel.setLabel_type("org");
                } else {
                    vipLabel.setLabel_type("user");
                }
                vipLabel.setBrand_code(brand_new);
                String existInfo2 = vipLabelService.insert(vipLabel);
                if (existInfo2.contains(Common.DATABEAN_CODE_SUCCESS)) {
                    List<VipLabel> viplabelID = vipLabelService.findViplabelID(corp_code, label_name);
                    String label_id2 = viplabelID.get(0).getId() + "";
                    vipLabelService.addRelViplabelToHbase(label_id2, corp_code, vip_code, store_code, user_id);

                    //vipLabelService.addRelViplabel(label_id2, corp_code, vip_code, store_code, user_id);
//                    List<RelViplabel> relViplabels1 = vipLabelService.checkRelViplablel(corp_code, vip_code, label_id2);
//                    result_add = String.valueOf(relViplabels1.get(0).getId());
                    JSONObject object = new JSONObject();
                    object.put("corp_code", corp_code);
                    object.put("label_id", label_id2);
                    object.put("vip_id", vip_code);
                    result.put("list", object.toJSONString());

                    //      result.put("list", result_add);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage(result.toString());
                }
            } else {
                VipLabel vipLabel = vipLabelList.get(0);
                if (!vipLabel.getIsactive().equals(Common.IS_ACTIVE_Y)) {
                    vipLabel.setIsactive(Common.IS_ACTIVE_Y);
                    vipLabelService.update(vipLabel);
                }
                String label_id = String.valueOf(vipLabel.getId());
                int relViplabels = vipLabelService.checkRelViplablelToHbase(corp_code, vip_code, label_id);
                if (relViplabels > 0) {
                    result_add = "该会员标签已存在";
                    result.put("list", JSON.toJSONString(result_add));
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage(result.toString());
                } else {
                    vipLabelService.addRelViplabelToHbase(label_id, corp_code, vip_code, store_code, user_id);
                    //  vipLabelService.addRelViplabel(label_id, corp_code, vip_code, store_code, user_id);
//                    List<RelViplabel> relViplabels1 = vipLabelService.checkRelViplablel(corp_code, vip_code, label_id);
//                    result_add = String.valueOf(relViplabels1.get(0).getId());
                    JSONObject object = new JSONObject();
                    object.put("corp_code", corp_code);
                    object.put("label_id", label_id);
                    object.put("vip_id", vip_code);
                    result.put("list", object.toJSONString());
                    //  result.put("list", result_add);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage(result.toString());
                }
            }

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/delRelViplabel", method = RequestMethod.POST)
    @ResponseBody
    public String delRelViplabel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String rid = jsonObject.getString("rid").toString();
            JSONObject result = new JSONObject();
            String result_add = "";
            int i = vipLabelService.delRelViplabel(rid);
            if (i > 0) {
                result_add = "删除成功";
            }
            result.put("list", JSON.toJSONString(result_add));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/delRelViplabelToHbase", method = RequestMethod.POST)
    @ResponseBody
    public String delRelViplabelToHbase(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code").toString();
            String vip_id = jsonObject.getString("vip_id").toString();
            String label_id = jsonObject.getString("label_id").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            JSONObject result = new JSONObject();
            String result_add = "";
            Map datalist = new HashMap<String, Data>();
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_vip_code = new Data("vip_id", vip_id, ValueType.PARAM);
            Data data_label_id = new Data("label_id", label_id, ValueType.PARAM);

            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_vip_code.key, data_vip_code);
            datalist.put(data_label_id.key, data_label_id);


            DataBox dataBox = iceInterfaceService.iceInterfaceV3("DelLabelByVip", datalist);
            String del = dataBox.data.get("message").value;

            Data data_user_id= new Data("user_id", user_code, ValueType.PARAM);
            datalist.put(data_user_id.key, data_user_id);
            Data data_type= new Data("type", "删除", ValueType.PARAM);
            datalist.put(data_type.key, data_type);
            DataBox dataBox2 = iceInterfaceService.iceInterfaceV3("AddLabelToMongo", datalist);
            result_add = del;
            result.put("list", JSON.toJSONString(result_add));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}
