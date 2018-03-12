package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.CorpWechat;
import com.bizvane.ishop.entity.VipRules;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.VipActivityService;
import com.bizvane.ishop.service.VipRulesService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/12/19.
 */
@Controller
@RequestMapping("/vipRules")
public class VipRulesController {

    @Autowired
    VipRulesService vipRulesService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    VipActivityService vipActivityService;
    @Autowired
    CorpService corpService;

    private static final Logger logger = Logger.getLogger(VipRulesController.class);
    String id;

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
            String result = this.vipRulesService.insert(message, user_id);

            if (result.equals("该企业已存在该会员类型")) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            } else if (result.equals(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            }  else if (result.equals("该企业会员类型对应的高级会员类型已存在")) {
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
            String result = vipRulesService.update(message, user_id);
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
            String vip_fsend_id = jsonObject.get("id").toString();

            String[] ids = vip_fsend_id.split(",");

            for (int i = 0; i < ids.length; i++) {
                vipRulesService.delete(Integer.parseInt(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);

            dataBean.setId(id);
            dataBean.setMessage("delete success");

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
            PageInfo<VipRules> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = vipRulesService.getAllVipRulesByPage(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = this.vipRulesService.getAllVipRulesByPage(page_number, page_size, "", search_value);
                } else {
                    list = vipRulesService.getAllVipRulesByPage(page_number, page_size, corp_code, search_value);
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
            PageInfo<VipRules> list = null;
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipRulesService.getAllVipRulesScreen(page_number, page_size, "", map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = vipRulesService.getAllVipRulesScreen(page_number, page_size, corp_code, map);
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
           VipRules vipRules= vipRulesService.getVipRulesById(Integer.parseInt(vipRules_id));
     //      CorpWechat corpWechat=corpService.getCorpByAppId(vipRules.getApp_id());
      //      vipRules.setApp_name(corpWechat.getApp_name());
            data = JSON.toJSONString(vipRules);
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
     * 获取优惠券类型(发券)
     * @param request
     * @return
     */
    @RequestMapping(value = "/getCoupon", method = RequestMethod.POST)
    @ResponseBody
    public String getCoupon(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code=request.getSession(false).getAttribute("role_code").toString();
            String corp_code=request.getSession(false).getAttribute("corp_code").toString();
            String corpCode=jsonObject.getString("corp_code");
            if(role_code.equals(Common.ROLE_SYS)){
                if(StringUtils.isNotBlank(corpCode)){
                   corp_code=corpCode;
                }else{
                    corp_code="C10000";
                }
            }

            JSONArray array = new JSONArray();
            if (jsonObject.containsKey("app_id") && !jsonObject.getString("app_id").equals("")){
                String app_id = jsonObject.getString("app_id");
                String coupon_info = iceInterfaceService.getCouponInfo(corp_code,app_id);
                if (coupon_info.equals(Common.DATABEAN_CODE_ERROR)) {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("获取优惠券失败");
                    return dataBean.getJsonStr();
                }
                array = JSONArray.parseArray(coupon_info);
            }else {
                List<CorpWechat> corpWechat = corpService.getWAuthByCorp(corp_code);
                logger.info("========corpWechat.size"+corpWechat.size());
                for (int i = 0; i < corpWechat.size(); i++) {
                    String app_id1 = corpWechat.get(i).getApp_id();
                    String coupon_info = iceInterfaceService.getCouponInfo(corp_code,app_id1);
                    if (coupon_info.equals(Common.DATABEAN_CODE_ERROR)) {
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId(id);
                        dataBean.setMessage("获取优惠券失败");
                        return dataBean.getJsonStr();
                    }
                    array.addAll(JSONArray.parseArray(coupon_info));
                }
            }

//            if (!coupon_info.equals(Common.DATABEAN_CODE_ERROR)){
//                JSONArray array = JSONArray.parseArray(coupon_info);

                JSONArray new_array = new JSONArray();
                //根据活动选择门店，过滤显示优惠券
                if (jsonObject.containsKey("store_code")){
                    String store_code = jsonObject.getString("store_code");
                    String[] store_codes = store_code.split(",");
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject coupon = array.getJSONObject(i);
                        if (coupon.containsKey("store_filter") && !coupon.getString("store_filter").equals("")){
                            String store = coupon.getString("store_filter");
                            for (int j = 0; j < store_codes.length; j++) {
                                if (store.contains(store_codes[j])){
                                    new_array.add(array.getJSONObject(i));
                                    break;
                                }
                            }
                        }else {
                            new_array.add(array.getJSONObject(i));
                        }
                    }
                }else {
                    new_array = array;
                }

                //过滤线下发放的券
                JSONArray new_array1 = new JSONArray();
                for (int i = 0; i < new_array.size(); i++) {
                    JSONObject coupon = new_array.getJSONObject(i);
                    if ( corp_code.equals("C10183") && coupon.containsKey("shortname") && coupon.get("shortname") != null && !coupon.get("shortname").equals(""))
                        coupon.put("name",coupon.get("shortname"));
//                    if (coupon.containsKey("send_type")){
//                        if (coupon.getString("send_type").equals("1"))
//                            new_array1.add(new_array.getJSONObject(i));
//                    }else {
//                        new_array1.add(new_array.getJSONObject(i));
//                    }
                    new_array1.add(new_array.getJSONObject(i));
                }
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(JSON.toJSONString(new_array1));

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /**
     * 获取优惠券类型
     * @param request
     * @return
     */
    @RequestMapping(value = "/getAllCoupon", method = RequestMethod.POST)
    @ResponseBody
    public String getAllCoupon(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String role_code=request.getSession(false).getAttribute("role_code").toString();
            String corp_code=request.getSession(false).getAttribute("corp_code").toString();
            String corpCode=jsonObject.getString("corp_code");
            if(role_code.equals(Common.ROLE_SYS)){
                if(StringUtils.isNotBlank(corpCode)){
                    corp_code=corpCode;
                }else{
                    corp_code="C10000";
                }
            }

            JSONArray array = new JSONArray();
            if (jsonObject.containsKey("app_id") && !jsonObject.getString("app_id").equals("")){
                String app_id = jsonObject.getString("app_id");
                CorpWechat corpWechat = corpService.getCorpByAppId(corp_code,app_id);
                if (corpWechat != null){
                    String access_key = corpWechat.getAccess_key();
                    array = vipRulesService.getAllCoupon(app_id, access_key);
                }
            }else {
                List<CorpWechat> corpWechat = corpService.getWAuthByCorp(corp_code);
                logger.info("========corpWechat.size"+corpWechat.size());
                for (int i = 0; i < corpWechat.size(); i++) {
                    String app_id = corpWechat.get(i).getApp_id();
                    String access_key = corpWechat.get(i).getAccess_key();
                    array.addAll(vipRulesService.getAllCoupon(app_id, access_key));
                }
            }
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(JSON.toJSONString(array));

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }
}
