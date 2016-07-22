package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.entity.VIPEmpRelation;
import com.bizvane.ishop.entity.VIPStoreRelation;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.GoodsService;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.service.WebService;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhouying on 2016-04-20.
 */


@Controller
public class WebController {

    private static Logger logger = LoggerFactory.getLogger((WebController.class));

    private static long NETWORK_DELAY_SECONDS = 1000 * 60 * 10;// 10 mininutes

    private static String APP_KEY = "Fghz1Fhp6pM1XajBMjXM";

    @Autowired
    WebService webService;
    @Autowired
    UserService userService;
    @Autowired
    CorpService corpService;
    @Autowired
    GoodsService goodsService;

    /**
     *
    */
    @RequestMapping(value = "/api/getviprelation", method = RequestMethod.POST)
    @ResponseBody
    public String vipRelation(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        logger.debug("--------/ishop/vipcard ");
        try {
            String app_user_name = request.getParameter("app_user_name");
            String open_id = request.getParameter("open_id");
            String app_key = request.getParameter("app_key");
            logger.info("input key=" + app_key + "app_user_name=" + app_user_name + "open_id" + open_id);

            if (app_key == null || app_key.equals("")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("need app_key");
            } else if (app_user_name == null || app_user_name.equals("")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("need app_user_name");
            } else if (open_id == null || open_id.equals("")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("need open_id");
            } else if (!app_key.equals(APP_KEY)) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("app_key Invalid签名无效");
            } else {
                VIPEmpRelation entity = webService.selectEmpVip(app_user_name, open_id);
                if (entity == null) {

                    VIPStoreRelation relation = webService.selectStoreVip(app_user_name,open_id);
                    if (relation == null){
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setMessage("客户未绑定");
                    }else {
                        JSONObject result = new JSONObject();
                        String store_id = relation.getStore_id();
                        JSONArray array = new JSONArray();
                        array.add(store_id);
                        JSONObject obj = new JSONObject();
                        obj.put("store_code",array);
                        obj.put("emp_code","");

                        result.put("code",Common.DATABEAN_CODE_SUCCESS);
                        result.put("message",obj);
                        return result.toString();
                    }
                } else {
                    JSONObject result = new JSONObject();
                    String emp_id = entity.getEmp_id();
                    String corp_code = corpService.getCorpByAppUserName(app_user_name).getCorp_code();
                    String store_code = userService.userCodeExist(emp_id,corp_code).getStore_code();
                    String[] ids = store_code.split(",");
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < ids.length; i++) {
                        ids[i] = ids[i].substring(1, ids[i].length());
                        array.add(i,ids[i]);
                    }
                    JSONObject obj = new JSONObject();
                    obj.put("emp_code",emp_id);
                    obj.put("store_code",array);
                    result.put("code",Common.DATABEAN_CODE_SUCCESS);
                    result.put("message",obj);
                    return result.toString();
                }
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/app/fab", method = RequestMethod.POST)
    @ResponseBody
    public String fab(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.parseInt(jsonObject.get("pageNumber").toString());
            int page_size = Integer.parseInt(jsonObject.get("pageSize").toString());
            String corp_code = jsonObject.get("corp_code").toString();

            JSONObject result = new JSONObject();
            PageInfo<Goods> list;
                list = goodsService.selectBySearch(page_number, page_size, corp_code, "");
            for (int i = 0; list.getList() != null && list.getList().size() > i; i++) {
                String goods_image = list.getList().get(i).getGoods_image();
                if (goods_image != null && !goods_image.isEmpty()) {
                    list.getList().get(i).setGoods_image(goods_image.split(",")[0]);
                }
            }
            result.put("list", JSON.toJSONString(list));
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

}
