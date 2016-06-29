package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.VIPRelation;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.service.WebService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;

import static com.bizvane.ishop.service.imp.WebServiceImpl.*;

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

    @RequestMapping(value = "/api/getviprelation", method = RequestMethod.POST)
    @ResponseBody
    public String vipRelation(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        logger.debug("--------/ishop/vipcard ");
        try {
            String app_user_name = request.getParameter("app_user_name");
            String open_id = request.getParameter("open_id");
            String app_key = request.getParameter("app_key");
            logger.debug("input key=" + app_key + "app_user_name=" + app_user_name + "open_id" + open_id);

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
                VIPRelation entity = webService.selectVip(app_user_name, open_id);
                if (entity == null) {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("客户未绑定");
                } else {
                    String guider_code = entity.getGuider_code();
                    String corp_code = corpService.getCorpByAppUserName(app_user_name).getCorp_code();
                    String store_code = userService.userCodeExist(guider_code,corp_code).getStore_code();
                    String[] ids = store_code.split(",");
                    String code = "";
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < ids.length; i++) {
                        ids[i] = ids[i].substring(1, ids[i].length());
                        array.add(ids[i]);
                    }
                    JSONObject obj = new JSONObject();
                    obj.put("emp_code",guider_code);
                    obj.put("store_code",array);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage(obj.toString());
                }
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }
}
