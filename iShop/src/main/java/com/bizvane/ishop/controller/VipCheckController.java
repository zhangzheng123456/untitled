package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.VipRules;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhou on 2017/1/5.
 */
@Controller
@RequestMapping("/vipCheck")
public class VipCheckController {


    private static final Logger logger = Logger.getLogger(VipCheckController.class);
    String id;


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


        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
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

            JSONArray array = new JSONArray();
                JSONObject obj = new JSONObject();
                obj.put("billNO","201701043423412");
                obj.put("store_name","商帆演示一店");
                obj.put("user_name","测试");
                obj.put("vip_name","李小芳");
                obj.put("cardno","21152345323");
                obj.put("price","1000");
                obj.put("pay_price","900");
                obj.put("discount","0.9");
                obj.put("date","2017-01-04");
                obj.put("pay_type","直接充值");
                obj.put("check_status","审核通过");
                obj.put("check_type","充值审核");
            array.add(obj);

            JSONObject obj1 = new JSONObject();
            obj1.put("billNO","201701033423412");
            obj1.put("store_name","商帆演示一店");
            obj1.put("user_name","测试");
            obj1.put("vip_name","李小芳");
            obj1.put("cardno","21152345323");
            obj1.put("price","1000");
            obj1.put("pay_price","900");
            obj1.put("discount","0.9");
            obj1.put("date","2017-01-04");
            obj1.put("pay_type","退换转充值");
            obj1.put("check_status","审核通过");
            obj1.put("check_type","充值审核");
            array.add(obj1);

            JSONObject obj2 = new JSONObject();
            obj2.put("billNO","201701033423412");
            obj2.put("store_name","商帆演示一店");
            obj2.put("user_name","测试");
            obj2.put("vip_name","吴德秀");
            obj2.put("cardno","3202112974444");
            obj2.put("price","1000");
            obj2.put("pay_price","900");
            obj2.put("discount","0.9");
            obj2.put("date","2017-01-04");
            obj2.put("pay_type","退换转充值");
            obj2.put("check_status","审核通过");
            obj2.put("check_type","充值审核");
            array.add(obj2);

            JSONObject obj3 = new JSONObject();
            obj3.put("billNO","201701033423412");
            obj3.put("store_name","商帆演示一店");
            obj3.put("user_name","测试");
            obj3.put("vip_name","吴德秀");
            obj3.put("cardno","3202112974444");
            obj3.put("price","1000");
            obj3.put("pay_price","900");
            obj3.put("discount","0.9");
            obj3.put("date","2017-01-04");
            obj3.put("pay_type","退换转充值");
            obj3.put("check_status","待审核");
            obj3.put("check_type","充值审核");
            array.add(obj3);

            JSONObject obj4 = new JSONObject();
            obj4.put("billNO","201701033423412");
            obj4.put("store_name","商帆演示一店");
            obj4.put("user_name","测试");
            obj4.put("vip_name","李小芳");
            obj4.put("cardno","21152345323");
            obj4.put("price","1000");
            obj4.put("pay_price","900");
            obj4.put("discount","0.9");
            obj4.put("date","2017-01-04");
            obj4.put("pay_type","退换转充值");
            obj4.put("check_status","拒绝");
            obj4.put("check_type","充值审核");
            array.add(obj4);

            result.put("pages",1);
            result.put("pageSize",page_size);
            result.put("pageNum",page_number);
            result.put("list", JSON.toJSONString(array));

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
            PageInfo<VipRules> list = null;
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            if (role_code.equals(Common.ROLE_SYS)) {
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
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
        try {
            String jsString = request.getParameter("param");
            logger.info("json-select-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vipRules_id = jsonObject.get("id").toString();

            JSONArray array = new JSONArray();
            JSONObject obj3 = new JSONObject();
            obj3.put("billNO","201701033423412");
            obj3.put("store_name","商帆演示一店");
            obj3.put("user_name","测试");
            obj3.put("vip_name","吴德秀");
            obj3.put("cardno","3202112974444");
            obj3.put("price","1000");
            obj3.put("pay_price","900");
            obj3.put("discount","0.9");
            obj3.put("date","2017-01-04");
            obj3.put("pay_type","退换转充值");
            obj3.put("check_status","待审核");
            obj3.put("check_type","充值审核");
            array.add(obj3);

            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(array));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("信息异常");
        }
        return dataBean.getJsonStr();
    }

}
