package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.GoodsService;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private FunctionService functionService;

    @Autowired
    private GoodsService goodsService;

    SimpleDateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);

    /**
     * 商品培训
     */
    @RequestMapping(value = "/fab/list", method = RequestMethod.GET)
    @ResponseBody
    public String goodsTrainManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession(false).getAttribute("group_code").toString();
            String funcCode = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            com.alibaba.fastjson.JSONArray actions = functionService.selectActionByFun(user_id, role_code, funcCode, group_code);
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<Goods> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.goodsService.selectBySearch(page_number, page_size, "", "");
            } else {
                //   String corp_code = request.getParameter("corp_code");
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = goodsService.selectBySearch(page_number, page_size, corp_code, "");
            }
            result.put("list", JSON.toJSONString(list));
            result.put("actions", actions);
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 商品培训
     * 添加
     */
    @RequestMapping(value = "/fab/add", method = RequestMethod.POST)
    @ResponseBody
    public String addGoodsTrain(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_id").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            // String user_code = jsonObject.get("user_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Goods goods = WebUtils.JSON2Bean(jsonObject, Goods.class);
            //goods.setGoods_time(sdf.parse);
            Date now = new Date();
            goods.setModified_date(now);
            goods.setModifier(user_id);
            goods.setCreated_date(now);
            goods.setCreater(user_id);
            this.goodsService.insert(goods);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("add success!!!");
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 商品编辑之前
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/fab/select", method = RequestMethod.POST)
    @ResponseBody
    public String selectGoodsTrain(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int goods_id = Integer.parseInt(jsonObject.getString("id"));
            //   Goods goods = this.goodsService.getGoodsByCode(corp_code, goods_code);
            Goods goods = this.goodsService.getGoodsById(goods_id);
            org.json.JSONObject result = new org.json.JSONObject();
            result.put("goods", JSON.toJSONString(goods));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 商品培训
     * 编辑
     */
    @RequestMapping(value = "/fab/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editGoodsTrain(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String user_id = request.getSession(false).toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            Goods goods = WebUtils.JSON2Bean(jsonObject, Goods.class);
            Date now = new Date();
            goods.setModified_date(now);
            goods.setModifier(user_id);
            this.goodsService.update(goods);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("edit success !!! ");
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("edit error !!! ");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 商品培训
     * 查找
     */
    @RequestMapping(value = "/fab/find", method = RequestMethod.POST)
    @ResponseBody
    public String findGoodsTrain(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            PageInfo<Goods> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = this.goodsService.selectBySearch(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = this.goodsService.selectBySearch(page_number, page_size, corp_code, search_value);
            }
            org.json.JSONObject result = new org.json.JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 商品培训
     * 删除
     */
    @RequestMapping(value = "/fab/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteGoodsTrain(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String goods_id = jsonObject.getString("id");
            String ids[] = goods_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                this.goodsService.delete(Integer.parseInt(ids[i]));
            }
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 秀搭管理
     */
    @RequestMapping(value = "/xiuda/list", method = RequestMethod.GET)
    @ResponseBody
    public String showMatchManage(HttpServletRequest request) {
        return "xiuda";
    }

}
