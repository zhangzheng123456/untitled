package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.VipFsend;
import com.bizvane.ishop.service.VipFsendService;
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
 * Created by nanji on 2016/11/25.
 */
@Controller
@RequestMapping("/vipFsend")
public class VipFsendController {
    @Autowired
    private VipFsendService vipFsendService;

    private static final Logger logger = Logger.getLogger(VipFsendController.class);

    String id;


    /**
     * 群发消息
     * <p>
     * 发送消息
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addFsend(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            int tem_id=1;
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);

            VipFsend vipFsend = WebUtils.JSON2Bean(jsonObject, VipFsend.class);

            String result = this.vipFsendService.insert(vipFsend,role_code,brand_code,area_code,store_code, user_code);

            if (result.equals("0")) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("发送成功");
            }else{
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
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
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String vip_fsend_id = jsonObject.get("id").toString();
            String[] ids = vip_fsend_id.split(",");

            for (int i = 0; i < ids.length; i++) {
                vipFsendService.delete(Integer.parseInt(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");

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
     * 群发消息
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VipFsend> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = vipFsendService.getAllVipFsendByPage(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = this.vipFsendService.getAllVipFsendByPage(page_number, page_size, "", search_value);
                } else {
                    list = vipFsendService.getAllVipFsendByPage(page_number, page_size, corp_code, search_value);
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
     * 群发消息
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
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VipFsend> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipFsendService.getAllVipFsendScreen(page_number, page_size, "", map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = vipFsendService.getAllVipFsendScreen(page_number, page_size, corp_code, map);
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
     * 查看选择接收短信的会员信息
     * 以及模板消息的已读状态
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/checkVipInfo", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");

           logger.info("json-select-------------" + jsString);
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String vipFsend_id = jsonObject.get("id").toString().trim();
            //String send_type=jsonObject.get("send_type").toString().trim();
            String info = vipFsendService.getVipFsendById(Integer.valueOf(vipFsend_id));
            if (info != null) {
                bean.setCode(Common.DATABEAN_CODE_SUCCESS);
                bean.setId("1");
                bean.setMessage(info);
            } else {
                bean.setCode(Common.DATABEAN_CODE_ERROR);
                bean.setId("1");
                bean.setMessage("查看失败");
            }

        } catch (Exception e) {
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage(e.toString());
        }

        return bean.getJsonStr();
    }

    /**
     * 根据id查看详情
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String select(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");

            logger.info("json-select-------------" + jsString);
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String vipFsend_id = jsonObject.get("id").toString().trim();

            VipFsend info = vipFsendService.getVipFsendInfoById(Integer.valueOf(vipFsend_id));
            data=JSON.toJSONString(info);

                bean.setCode(Common.DATABEAN_CODE_SUCCESS);
                bean.setId("1");
                bean.setMessage(data);

        } catch (Exception e) {
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage(e.toString());
        }

        return bean.getJsonStr();
    }

}
