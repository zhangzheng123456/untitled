package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.service.imp.WeiMobServiceImpl;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhouying on 2016-04-20.
 */


@Controller
public class WebController {

    private static final Logger logger = Logger.getLogger(WebController.class);

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
    @Autowired
    GroupService groupService;
    @Autowired
    StoreService storeService;
    @Autowired
    BrandService brandService;

    WeiMobServiceImpl weiMobService = new WeiMobServiceImpl();

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
                dataBean.setMessage("app_key Invalid");
            } else {
                List<VIPEmpRelation> entity = webService.selectEmpVip(app_user_name, open_id);
                if (entity.size() != 0) {

                    JSONObject result = new JSONObject();
                    String emp_id = entity.get(0).getEmp_id();
                    String corp_code = corpService.getCorpByAppUserName(app_user_name).getCorp_code();
                    List<User> users = userService.userCodeExist(emp_id, corp_code, Common.IS_ACTIVE_Y);
                    if (users.size() != 0) {
                        User user = users.get(0);
                        String group_code = user.getGroup_code();
                        String role_code = groupService.selectByCode(corp_code, group_code, "").getRole_code();
                        JSONArray array = new JSONArray();

                        if (role_code.equals(Common.ROLE_AM)) {
                            String area_code = user.getArea_code();
                            String[] areaCodes = area_code.split(",");
                            if (areaCodes[0].contains(Common.SPECIAL_HEAD)) {
                                areaCodes[0] = areaCodes[0].substring(1, areaCodes[0].length());
                            }
                            String[] ids = new String[]{areaCodes[0]};
                            List<Store> list = storeService.selectByAreaBrand(corp_code, ids, Common.IS_ACTIVE_Y);
                            array.add(list.get(0).getStore_code());
                        } else if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_SYS)) {
                            String store_code = storeService.getCorpStore(corp_code).get(0).getStore_code();
                            array.add(store_code);
                        } else {
                            String store_code = user.getStore_code();
                            String[] ids = store_code.split(",");
                            for (int i = 0; i < ids.length; i++) {
                                if (ids[i].startsWith(Common.SPECIAL_HEAD)) {
                                    ids[i] = ids[i].substring(1, ids[i].length());
                                }
                                array.add(i, ids[i]);
                            }
                        }
                        JSONObject obj = new JSONObject();
                        obj.put("emp_code", emp_id);
                        obj.put("store_code", array);
                        result.put("code", Common.DATABEAN_CODE_SUCCESS);
                        result.put("message", obj);
                        return result.toString();
                    }
                }
                List<VIPStoreRelation> relation = webService.selectStoreVip(app_user_name, open_id);
                if (relation.size() == 0) {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("the open_id is new");
                } else {
                    JSONObject result = new JSONObject();
                    String store_id = relation.get(0).getStore_id();
                    JSONArray array = new JSONArray();
                    array.add(store_id);
                    JSONObject obj = new JSONObject();
                    obj.put("store_code", array);
                    obj.put("emp_code", "");

                    result.put("code", Common.DATABEAN_CODE_SUCCESS);
                    result.put("message", obj);
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

    /**
     * app获取FAB列表接口
     */
    @RequestMapping(value = "/api/fab", method = RequestMethod.POST)
    @ResponseBody
    public String fab(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int rowno = Integer.parseInt(jsonObject.get("rowno").toString());
            String corp_code = jsonObject.get("corp_code").toString();

            PageInfo<Goods> list;
            if (jsonObject.containsKey("user_id") && !jsonObject.get("user_id").toString().equals("")) {
                String user_code = jsonObject.get("user_id").toString();

                List<User> users = userService.userCodeExist(user_code, corp_code, Common.IS_ACTIVE_Y);
                if (users.size() < 1) {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("用户不存在");
                    return dataBean.getJsonStr();
                }
                List<String> brand_codes = userService.getBrandCodeByUser(users.get(0).getId(), corp_code);
                String brand_code = "";
                for (int i = 0; i < brand_codes.size(); i++) {
                    brand_code = brand_code + brand_codes.get(i).toString() + ",";
                }
                list = goodsService.selectBySearchForApp(1 + rowno / 20, 20, corp_code, "", "", brand_code, "", "", "");
            } else {
                list = goodsService.selectBySearch(1 + rowno / 20, 20, corp_code, "");
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

    /**
     * app获取FAB列表搜索,筛选接口
     */
    @RequestMapping(value = "/api/fab/search", method = RequestMethod.POST)
    @ResponseBody
    public String fabSearch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int rowno = Integer.parseInt(jsonObject.get("rowno").toString());
            String corp_code = jsonObject.get("corp_code").toString();
            String search_value = jsonObject.get("search_value").toString();

            String goods_quarter = "";
            String goods_wave = "";
            String brand_code = "";
            String time_start = "";
            String time_end = "";

            if (jsonObject.containsKey("user_id") && !jsonObject.get("user_id").toString().equals("")) {
                String user_code = jsonObject.get("user_id").toString();
                List<User> users = userService.userCodeExist(user_code, corp_code, Common.IS_ACTIVE_Y);
                if (users.size() < 1) {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("用户不存在");
                    return dataBean.getJsonStr();
                }
                List<String> brand_codes = userService.getBrandCodeByUser(users.get(0).getId(), corp_code);

                for (int i = 0; i < brand_codes.size(); i++) {
                    brand_code = brand_code + brand_codes.get(i).toString() + ",";
                }
            }

            if (jsonObject.containsKey("goods_quarter"))
                goods_quarter = jsonObject.get("goods_quarter").toString();
            if (jsonObject.containsKey("goods_wave"))
                goods_wave = jsonObject.get("goods_wave").toString();
            if (jsonObject.containsKey("brand_code") && !jsonObject.get("brand_code").toString().equals("")) {
                brand_code = jsonObject.get("brand_code").toString();
            }

            JSONObject result = new JSONObject();
            PageInfo<Goods> list = goodsService.selectBySearchForApp(1 + rowno / 20, 20, corp_code, goods_quarter,
                    goods_wave, brand_code, time_start, time_end, search_value);

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

    /**
     * app获取FAB详细接口
     */
    @RequestMapping(value = "/api/fab/select", method = RequestMethod.POST)
    @ResponseBody
    public String fabDetail(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int goods_id = Integer.parseInt(jsonObject.getString("id"));
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
     * app获取FAB筛选侧边接口
     */
    @RequestMapping(value = "/api/fab/screenValue", method = RequestMethod.POST)
    @ResponseBody
    public String fabScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();

            //品牌
            List<Brand> brands = new ArrayList<Brand>();
            if (jsonObject.containsKey("user_id") && !jsonObject.get("user_id").toString().equals("")) {
                String user_code = jsonObject.get("user_id").toString();
                List<User> users = userService.userCodeExist(user_code, corp_code, Common.IS_ACTIVE_Y);
                if (users.size() < 1) {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("用户不存在");
                    return dataBean.getJsonStr();
                }
                List<String> brand_codes = userService.getBrandCodeByUser(users.get(0).getId(), corp_code);
                for (int i = 0; i < brand_codes.size(); i++) {
                    Brand brand = brandService.getBrandByCode(corp_code, brand_codes.get(i).toString(), Common.IS_ACTIVE_Y);
                    if (brand != null)
                        brands.add(brand);
                }
            } else {
                brands = brandService.getAllBrand(corp_code, "");
            }

            JSONObject result = new JSONObject();
            //季度
            List<Goods> quarters = goodsService.selectCorpGoodsQuarter(corp_code);
            //波段
            List<Goods> waves = goodsService.selectCorpGoodsWave(corp_code);

            result.put("quarters", JSON.toJSONString(quarters));
            result.put("waves", JSON.toJSONString(waves));
            result.put("brands", JSON.toJSONString(brands));

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
     * app获取FAB列表接口
     */
    @RequestMapping(value = "/api/fab/publicImg", method = RequestMethod.POST)
    @ResponseBody
    public String fabPublicImg(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String user_code = jsonObject.get("user_id").toString();

            List<User> users = userService.userCodeExist(user_code, corp_code, Common.IS_ACTIVE_Y);
            if (users.size() < 1) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("用户不存在");
                return dataBean.getJsonStr();
            }
            List<String> brand_codes = userService.getBrandCodeByUser(users.get(0).getId(), corp_code);
            String brand_code = "";
            for (int i = 0; i < brand_codes.size(); i++) {
                brand_code = brand_code + brand_codes.get(i).toString() + ",";
            }
            List<Goods> list = goodsService.selectCorpPublicImgs(corp_code,brand_code,"");
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

    /**
     * app获取商品列表（微盟桃花季）
     */
    @RequestMapping(value = "/api/weimob/goods", method = RequestMethod.GET)
    @ResponseBody
    public String handleWeimob(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String accessToken = generateToken(CommonValue.appID, CommonValue.appSecert);
            int rowno = Integer.parseInt(request.getParameter("rowno"));
            JSONArray goodList = weiMobService.getList(accessToken, rowno);
            JSONArray classifyList = weiMobService.getClassify(accessToken);
            JSONArray brandList = weiMobService.getClassifySon(accessToken);

            JSONObject message = new JSONObject();

            if (request.getParameter("brand_id") != null && !request.getParameter("brand_id").equals("")) {
                logger.debug("-------------111111111111-------------------");
                goodList = weiMobService.getSearchClassify(accessToken, request.getParameter("brand_id"));
                logger.debug("handleWeimob Brand_id ->" + request.getParameter("brand_id"));
            }
            if (request.getParameter("key") != null && !request.getParameter("key").equals("")) {
                logger.debug("-------------22222222222------------------");
                goodList = weiMobService.getSearchTitle(accessToken, request.getParameter("key"));
                logger.debug("handleWeimob Key ->" + request.getParameter("key"));
            }

            message.put("goodsList", goodList);
            message.put("brandLists", brandList);
            message.put("classifyList", classifyList);

            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(message.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }

    public String generateToken(String appID, String appSecert) throws Exception {
        String accessToken = weiMobService.accessToken;
        if (accessToken.equals("")) {
            accessToken = weiMobService.getAccessToken(appID, appSecert);
        }
        Date startTime = weiMobService.startTime;
        Date nowtime = new Date();
        long timediff = (nowtime.getTime() - startTime.getTime());
        if (timediff > 120000) {
            accessToken = weiMobService.getAccessToken(appID, appSecert);
        }
        return accessToken;
    }

}
