package com.bizvane.ishop.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.*;
import com.bizvane.sun.common.service.http.HttpClient;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import okhttp3.Response;
import org.apache.hadoop.hdfs.security.token.block.DataEncryptionKey;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Created by zhouying on 2016-04-20.
 */


@Controller
public class WebController {

    private static long NETWORK_DELAY_SECONDS = 1000 * 60 * 10;// 10 mininutes

    private static String APP_KEY = "Fghz1Fhp6pM1XajBMjXM";

    private static String SIGN = "41bfa82252f31bef46ccffca4ec22b5e";

    String id;

    private static final Logger logger = Logger.getLogger(WebController.class);

    @Autowired
    ShopMatchService shopMatchService;
    @Autowired
    WebService webService;
    @Autowired
    UserService userService;
    @Autowired
    CorpService corpService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    DefGoodsMatchService defGoodsMatchService;
    @Autowired
    GroupService groupService;
    @Autowired
    StoreService storeService;
    @Autowired
    BrandService brandService;
    @Autowired
    WeimobService weimobService;
    @Autowired
    VipActivityService vipActivityService;
    @Autowired
    VipActivityDetailService vipActivityDetailService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    ScheduleJobService scheduleJobService;
    @Autowired
    VipGroupService vipGroupService;
    @Autowired
    CRMInterfaceService crmInterfaceService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    BaseService baseService;
    @Autowired
    VipTaskService vipTaskService;
    @Autowired
    VipFsendService vipFsendService;
    @Autowired
    WxTemplateService wxTemplateService;
    @Autowired
    VipPointsAdjustService vipPointsAdjustService;
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
                DBCursor entity = webService.selectEmpVip(app_user_name, open_id);
                if (entity.size() > 0) {
                    DBObject dbobj = entity.next();
                    JSONObject result = new JSONObject();
                    String emp_id = dbobj.get("user_code").toString();
                    String corp_code = corpService.getCorpByAppUserName(app_user_name).getCorp_code();
                    List<User> users = userService.userCodeExist(emp_id, corp_code, Common.IS_ACTIVE_Y);
                    if (users.size() != 0) {
                        User user = users.get(0);
                        String group_code = user.getGroup_code();
                        String role_code = groupService.selectByCode(corp_code, group_code, "").getRole_code();
                        JSONArray array = new JSONArray();

                        if (role_code.equals(Common.ROLE_AM)) {
                            String area_code = user.getArea_code();
                            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                            String[] areaCodes = area_code.split(",");
                            String[] ids = new String[]{areaCodes[0]};
                            List<Store> list = storeService.selectByAreaBrand(corp_code, ids, null, null, Common.IS_ACTIVE_Y);
                            array.add(list.get(0).getStore_code());
                        } else if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_SYS)) {
                            String store_code = storeService.getCorpStore(corp_code).get(0).getStore_code();
                            array.add(store_code);
                        } else {
                            String store_code = user.getStore_code();
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            String[] ids = store_code.split(",");
                            for (int i = 0; i < ids.length; i++) {
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
    @RequestMapping(value = "/api/fab", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
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
            String user_code = "";
            if (jsonObject.containsKey("user_id") && !jsonObject.get("user_id").toString().equals("")) {
                user_code = jsonObject.get("user_id").toString();

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
                list = goodsService.selectBySearch(1 + rowno / 20, 20, corp_code, "", null);
            }
            Map datalist = new HashMap<String, Data>();

            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_user_code = new Data("user_code", user_code, ValueType.PARAM);

            datalist.put(data_user_code.key, data_user_code);
            datalist.put(data_corp_code.key, data_corp_code);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetStoreByRole", datalist);

            String result_store = dataBox.data.get("message").value;
            System.out.println("---result_store---" + result_store);
            List<Goods> goodsList = list.getList();
            for (Goods goods : goodsList) {
                String goods_code = goods.getGoods_code();
                String corp_code1 = goods.getCorp_code();
                if (null == result_store && "".equals(result_store)) {
                    goods.setNum_sales("0");
                    goods.setNum_stocks("0");
                } else {
                    Map datalist_goods = new HashMap<String, Data>();
                    Data data_product_id_goods = new Data("product_id", goods_code, ValueType.PARAM);
                    Data data_corp_code_goods = new Data("corp_code", corp_code1, ValueType.PARAM);
                    Data data_store_id_goods = new Data("store_id", result_store, ValueType.PARAM);

                    datalist_goods.put(data_product_id_goods.key, data_product_id_goods);
                    datalist_goods.put(data_corp_code_goods.key, data_corp_code_goods);
                    datalist_goods.put(data_store_id_goods.key, data_store_id_goods);

                    DataBox dataBox_goods = iceInterfaceService.iceInterfaceV3("GetStockAndSale", datalist_goods);
                    String result_goods = dataBox_goods.data.get("message").value;
                    JSONObject object = JSON.parseObject(result_goods);
                    goods.setNum_sales(object.get("num_sales").toString());
                    goods.setNum_stocks(object.get("num_stocks").toString());
                }
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * app获取FAB列表搜索,筛选接口
     */
    @RequestMapping(value = "/api/fab/search", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
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
            String user_code = "";
            if (jsonObject.containsKey("user_id") && !jsonObject.get("user_id").toString().equals("")) {
                user_code = jsonObject.get("user_id").toString();
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
            Map datalist = new HashMap<String, Data>();

            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_user_code = new Data("user_code", user_code, ValueType.PARAM);

            datalist.put(data_user_code.key, data_user_code);
            datalist.put(data_corp_code.key, data_corp_code);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetStoreByRole", datalist);

            String result_store = dataBox.data.get("message").value;
            System.out.println("---result_store---" + result_store);
            JSONObject result = new JSONObject();
            PageInfo<Goods> list = goodsService.selectBySearchForApp(1 + rowno / 20, 20, corp_code, goods_quarter,
                    goods_wave, brand_code, time_start, time_end, search_value);
            List<Goods> goodsList = list.getList();
            for (Goods goods : goodsList) {
                String goods_code = goods.getGoods_code();
                String corp_code1 = goods.getCorp_code();
                if (null == result_store && "".equals(result_store)) {
                    goods.setNum_sales("0");
                    goods.setNum_stocks("0");
                } else {
                    Map datalist_goods = new HashMap<String, Data>();
                    Data data_product_id_goods = new Data("product_id", goods_code, ValueType.PARAM);
                    Data data_corp_code_goods = new Data("corp_code", corp_code1, ValueType.PARAM);
                    Data data_store_id_goods = new Data("store_id", result_store, ValueType.PARAM);

                    datalist_goods.put(data_product_id_goods.key, data_product_id_goods);
                    datalist_goods.put(data_corp_code_goods.key, data_corp_code_goods);
                    datalist_goods.put(data_store_id_goods.key, data_store_id_goods);

                    DataBox dataBox_goods = iceInterfaceService.iceInterfaceV3("GetStockAndSale", datalist_goods);
                    String result_goods = dataBox_goods.data.get("message").value;
                    JSONObject object = JSON.parseObject(result_goods);
                    goods.setNum_sales(object.get("num_sales").toString());
                    goods.setNum_stocks(object.get("num_stocks").toString());
                }
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * app获取FAB详细接口
     */
    @RequestMapping(value = "/api/fab/select", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String fabDetail(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int goods_id = Integer.parseInt(jsonObject.getString("id"));
            String corp_code_json = jsonObject.get("corp_code").toString();
            String user_code_json = jsonObject.get("user_id").toString();
            Goods goods = this.goodsService.getGoodsById(goods_id);
            Map datalist = new HashMap<String, Data>();

            Data data_corp_code = new Data("corp_code", corp_code_json, ValueType.PARAM);
            Data data_user_code = new Data("user_code", user_code_json, ValueType.PARAM);

            datalist.put(data_user_code.key, data_user_code);
            datalist.put(data_corp_code.key, data_corp_code);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetStoreByRole", datalist);

            String result_store = dataBox.data.get("message").value;
            System.out.println("---result_store---" + result_store);
            String goods_code = goods.getGoods_code();
            String corp_code1 = goods.getCorp_code();
            if (null == result_store && "".equals(result_store)) {
                goods.setNum_sales("0");
                goods.setNum_stocks("0");
            } else {
                Map datalist_goods = new HashMap<String, Data>();
                Data data_product_id_goods = new Data("product_id", goods_code, ValueType.PARAM);
                Data data_corp_code_goods = new Data("corp_code", corp_code1, ValueType.PARAM);
                Data data_store_id_goods = new Data("store_id", result_store, ValueType.PARAM);

                datalist_goods.put(data_product_id_goods.key, data_product_id_goods);
                datalist_goods.put(data_corp_code_goods.key, data_corp_code_goods);
                datalist_goods.put(data_store_id_goods.key, data_store_id_goods);

                DataBox dataBox_goods = iceInterfaceService.iceInterfaceV3("GetStockAndSale", datalist_goods);
                String result_goods = dataBox_goods.data.get("message").value;
                JSONObject object = JSON.parseObject(result_goods);
                goods.setNum_sales(object.get("num_sales").toString());
                goods.setNum_stocks(object.get("num_stocks").toString());
            }

            List<DefGoodsMatch> matchgoods = defGoodsMatchService.selectGoodsMatchList(corp_code1, goods_code, Common.IS_ACTIVE_Y);
            goods.setMatchgoods(matchgoods);
            JSONObject result = new JSONObject();
            result.put("goods", JSON.toJSONString(goods));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * app获取FAB筛选侧边接口
     */
    @RequestMapping(value = "/api/fab/screenValue", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
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
                brands = brandService.getActiveBrand(corp_code, "", null);
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
     * app获取FAB公开图片接口
     */
    @RequestMapping(value = "/api/fab/publicImg", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String fabPublicImg(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(data);

            String corp_code = jsonObj.getString("corp_code");
            String user_code = jsonObj.getString("user_code");
            String search_value = jsonObj.getString("search_value");
            logger.info("--------corp_code:" + corp_code + "user_code:" + user_code + "search_value:" + search_value + "----------- ");

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
            logger.info("--------brand_code:" + brand_code + "----------- ");

            List<Goods> list = goodsService.selectCorpPublicImgs(corp_code, brand_code, search_value);
//            logger.info("--------list:"+JSON.toJSONString(list)+"----------- ");

            result.put("list", JSON.toJSONString(list));
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        System.out.print("222");
        return dataBean.getJsonStr();
    }


    /**
     * app获取秀搭筛选
     */
    @RequestMapping(value = "/api/match/screen", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String matchScreen(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(data);
            int page_number = Integer.valueOf(jsonObj.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObj.get("pageSize").toString());
            String search_value = jsonObj.get("searchValue").toString();
            String corp_code = jsonObj.get("corp_code").toString();
            PageInfo<ShopMatchType> list = shopMatchService.selectAllMatchType(page_number, page_size, corp_code, search_value, "sys");
            for (ShopMatchType matchType : list.getList()) {
                if (null == matchType.getType()) {
                    matchType.setType("");
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
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        System.out.print("222");
        return dataBean.getJsonStr();
    }




    @RequestMapping(value = "/api/getBrandByUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getBrandByUser(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(data);

            String corp_code = jsonObj.getString("corp_code");
            String user_code = jsonObj.getString("user_code");
            String search_value = jsonObj.getString("search_value");
            logger.info("--------corp_code:" + corp_code + "user_code:" + user_code + "search_value:" + search_value + "----------- ");

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
            logger.info("--------brand_code:" + brand_code + "----------- ");

            //        List<Goods> list = goodsService.selectCorpPublicImgs(corp_code, brand_code, search_value);
//            logger.info("--------list:"+JSON.toJSONString(list)+"----------- ");

            result.put("brand_code", brand_code);
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        System.out.print("222");
        return dataBean.getJsonStr();
    }


    /**
     * app获取商品列表（微盟桃花季）
     */
    @RequestMapping(value = "/api/weimob/goods", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String handleWeimobGoods(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String accessToken = weimobService.generateToken(CommonValue.CLIENT_ID, CommonValue.CLIENT_SECRET);
            int rowno = Integer.parseInt(request.getParameter("rowno"));
            JSONArray goodList = new JSONArray();
//            JSONArray classifyList = weimobService.goodsclassifyGet(accessToken);
//            JSONArray brandList = weimobService.goodsclassifyGetSon(accessToken);

            JSONObject message = new JSONObject();
            if (request.getParameter("brand_id") != null && !request.getParameter("brand_id").equals("")) {
                logger.info("-------------111111111111-------------------");
                goodList = weimobService.getSearchClassify(accessToken, request.getParameter("brand_id"), rowno);
                logger.info("handleWeimob Brand_id ->" + request.getParameter("brand_id"));
            } else if (request.getParameter("key") != null && !request.getParameter("key").equals("")) {
                logger.info("-------------22222222222------------------");
                goodList = weimobService.getSearchTitle(accessToken, request.getParameter("key"), rowno);
                logger.info("handleWeimob Key ->" + request.getParameter("key"));
            } else {
                goodList = weimobService.getList(accessToken, rowno);
            }

            message.put("goodsList", goodList);
//            message.put("brandLists", brandList);
//            message.put("classifyList", classifyList);

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

    /**
     * app获取商品列表（微盟桃花季）
     */
    @RequestMapping(value = "/api/weimob/classify", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String handleWeimobClassify(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject message = new JSONObject();
        try {
            String accessToken = weimobService.generateToken(CommonValue.CLIENT_ID, CommonValue.CLIENT_SECRET);
            JSONArray classifyList = weimobService.goodsclassifyGet(accessToken);
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

    /**
     * app获取商品列表（微盟桃花季）
     */
    @RequestMapping(value = "/api/weimob/auth", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String weimobAuth(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String code = request.getParameter("code");
            Weimob weimob = weimobService.selectByCorpId("C10116");
            weimob.setCode(code);
            weimobService.update(weimob);
            weimobService.getAccessTokenByCode(CommonValue.CLIENT_ID, CommonValue.CLIENT_SECRET);
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("3Q");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 获取微信js_ticket
     */
    @RequestMapping(value = "/api/wechat/jsTicket", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String jsTicket(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String app_id = request.getParameter("app_id");
            String url = CommonValue.wechat_url + "/jsApiTicket?app_id=" + app_id;
            String result = IshowHttpClient.get(url);

            return result;
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 点击登录
     */
    @RequestMapping(value = "/api/login", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        logger.info("------------starttime" + new Date());
        String id = "";
        String msg = "请求失败";
        String status = "failed";
        String data = "";
        try {
            String sign = request.getParameter("sign");
            String corp_code = request.getParameter("corp_code");
            String account = request.getParameter("account");
            String password = request.getParameter("password");

            if (sign == null || sign.equals("")) {
                msg = "request_sign";
            } else if (corp_code == null || corp_code.equals("")) {
                msg = "request_corp_code";
            } else if (account == null || account.equals("")) {
                msg = "request_account";
            } else if (password == null || password.equals("")) {
                msg = "request_password";
            } else {
                password = AESUtils.Decryptor(password);
                String[] aa = password.split("&&");
                if (aa.length == 2) {
                    String timestamp = password.split("&&")[0];
                    password = password.split("&&")[1];

                    long epoch = Long.valueOf(timestamp);
                    logger.debug(" range test:" + System.currentTimeMillis());
                    if (!sign.equals(SIGN)) {
                        msg = "param sign Invalid";
                    } else if (System.currentTimeMillis() - epoch < -NETWORK_DELAY_SECONDS || System.currentTimeMillis() - epoch > NETWORK_DELAY_SECONDS) {
                        msg = "timestamp time_out";
                    } else {
                        JSONObject user_info = userService.selectLoginByUserCode(request, corp_code, account, password);

                        if (user_info == null || user_info.getString("status").contains(Common.DATABEAN_CODE_ERROR)) {
                            msg = user_info.getString("error");
                        } else {
                            response.sendRedirect("/navigation_bar.html?url=/vip/vip.html&func_code=F0010");
                            return;
                        }
                    }
                } else {
                    msg = "password_without_&&";
                }
            }
            response.sendRedirect(msg.toString());
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 新增员工
     */
    @RequestMapping(value = "/api/addUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addUser(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String id = "";
        String msg = "";
        String status = "failed";
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String post_data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(post_data);
            id = jsonObj.containsKey("id") ? jsonObj.get("id").toString() : "";
            if (!jsonObj.containsKey("sign")) {
                msg = "request_sign";
            } else if (!jsonObj.containsKey("timestamp")) {
                msg = "request_timestamp";
            } else if (!jsonObj.containsKey("data")) {
                msg = "request_data";
            } else {
                String sign = jsonObj.getString("sign");
                String timestamp = jsonObj.getString("timestamp");
                long epoch = Long.valueOf(timestamp);
                if (!sign.equals(SIGN)) {
                    msg = "param sign Invalid";
                } else if (System.currentTimeMillis() - epoch < -NETWORK_DELAY_SECONDS || System.currentTimeMillis() - epoch > NETWORK_DELAY_SECONDS) {
                    msg = "timestamp time_out";
                } else {
                    String data = jsonObj.getString("data");
                    JSONObject data_obj = JSONObject.parseObject(data);
                    if (!data_obj.containsKey("corp_code")) {
                        msg = "request corp_code";
                    } else if (!data_obj.containsKey("user_code")) {
                        msg = "request user_code";
                    } else if (!data_obj.containsKey("phone")) {
                        msg = "request phone";
                    } else {
                        String corp_code = data_obj.getString("corp_code");
//                        String phone = data_obj.getString("phone");
                        List<Group> groups = groupService.selectByCorpRole(corp_code, Common.ROLE_STAFF);
                        User user = WebUtils.JSON2Bean(data_obj, User.class);
                        if (data_obj.containsKey("store_code") && !data_obj.getString("store_code").equals("")) {
                            String store_code = data_obj.getString("store_code");
                            String[] store_codes = store_code.split(",");
                            String code = "";
                            for (int i = 0; i < store_codes.length; i++) {
                                code = Common.SPECIAL_HEAD + store_codes[i] + ",";
                            }
                            user.setStore_code(code);
                        }
//                        String password = CheckUtils.encryptMD5Hash(phone);
//                        user.setPassword(password);
                        user.setGroup_code(groups.get(0).getGroup_code());
                        user.setCan_login(Common.IS_ACTIVE_Y);
                        user.setIsactive(Common.IS_ACTIVE_Y);
                        msg = userService.insert(user);
                        if (msg.equals(Common.DATABEAN_CODE_SUCCESS)) {
                            result.put("id", id);
                            result.put("status", "success");
                            result.put("message", "请求成功");
                            return result.toString();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            msg = ex.getMessage();
        }
        result.put("id", id);
        result.put("status", status);
        result.put("message", msg);
        return result.toString();
    }

    /**
     * 修改员工信息
     */
    @RequestMapping(value = "/api/editUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String editUser(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String id = "";
        String msg = "";
        String status = "failed";
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String post_data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(post_data);
            id = jsonObj.containsKey("id") ? jsonObj.get("id").toString() : "";
            if (!jsonObj.containsKey("sign")) {
                msg = "request_sign";
            } else if (!jsonObj.containsKey("timestamp")) {
                msg = "request_timestamp";
            } else if (!jsonObj.containsKey("data")) {
                msg = "request_data";
            } else {
                String sign = jsonObj.getString("sign");
                String timestamp = jsonObj.getString("timestamp");
                long epoch = Long.valueOf(timestamp);
                if (!sign.equals(SIGN)) {
                    msg = "param sign Invalid";
                } else if (System.currentTimeMillis() - epoch < -NETWORK_DELAY_SECONDS || System.currentTimeMillis() - epoch > NETWORK_DELAY_SECONDS) {
                    msg = "timestamp time_out";
                } else {
                    String data = jsonObj.getString("data");
                    org.json.JSONObject data_obj = new org.json.JSONObject(data);
                    if (!data_obj.has("corp_code")) {
                        msg = "request corp_code";
                    } else if (!data_obj.has("user_code")) {
                        msg = "request user_code";
                    } else if (!data_obj.has("user_code")) {
                        msg = "request user_code";
                    } else {
                        String corp_code = data_obj.getString("corp_code");
                        String user_code = data_obj.getString("user_code");
                        List<User> users = userService.userCodeExist(user_code, corp_code, Common.IS_ACTIVE_Y);
                        User user = users.get(0);
                        String sex = data_obj.has("sex") ? data_obj.get("sex").toString() : "";
                        String user_name = data_obj.has("user_name") ? data_obj.get("user_name").toString() : "";
                        String position = data_obj.has("position") ? data_obj.get("position").toString() : "";
                        String password = data_obj.has("password") ? data_obj.get("password").toString() : "";
                        if (!sex.equals(""))
                            user.setSex(sex);
                        if (!user_name.equals(""))
                            user.setUser_name(user_name);
                        if (!position.equals(""))
                            user.setPosition(position);
                        if (!password.equals(""))
                            user.setPassword(password);
                        userService.updateUser(user);
                        result.put("id", id);
                        result.put("status", "success");
                        result.put("message", "请求成功");
                        return result.toString();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            msg = ex.getMessage();
        }
        result.put("id", id);
        result.put("status", status);
        result.put("message", msg);
        return result.toString();
    }

    /**
     * 获取附件门店
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/nearbyStore", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String nearbyStore(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        String msg = "";
        String status = "failed";
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String post_data = stringBuffer.toString();
            logger.info("===========nearbyStore==post_data:" + post_data);

            JSONObject jsonObj = JSONObject.parseObject(post_data);
            String corp_code = jsonObj.get("corp_code").toString();
            String latitude = jsonObj.get("latitude").toString();
            String longitude = jsonObj.get("longitude").toString();

            JSONArray array = BaiduMapUtils.ConvertCoords(longitude + "," + latitude, "1", "5");
            if (array.size() > 0) {
                JSONObject coords = array.getJSONObject(0);
                longitude = coords.getString("x");
                latitude = coords.getString("y");
            }
            logger.info("===========nearbyStore==baiduMap:" + longitude + "," + latitude);
            List<Store> storeList = storeService.selectNearByStore(corp_code, longitude, latitude, "5000");

            for (int i = 0; i < storeList.size(); i++) {
                Store store = storeList.get(i);
                String lng = store.getLng();
                String lat = store.getLat();
                store.setStore_location(lat + "," + lng);
                String distance = store.getDistance();
                distance = NumberUtil.keepPrecision(distance);
                store.setIs_this_area(distance);
                store.setDistance(distance);
            }
            logger.info("===========storeList==size:" + storeList.size());
            result.put("list", JSON.toJSONString(storeList));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 获取所有门店
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/allStore", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String allStore(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        String msg = "";
        String status = "failed";
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String post_data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(post_data);
            int page_size = jsonObj.getInteger("pageSize");
            int page_num = jsonObj.getInteger("pageNumber");
            String corp_code = jsonObj.get("corp_code").toString();
            String search_value = jsonObj.get("searchValue").toString();

            PageInfo<Store> stores = storeService.selectAllOrderByCity(page_num, page_size, corp_code, search_value);
            result.put("list", JSON.toJSONString(stores));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 获取店铺下员工
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/getStoreUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getStoreUser(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String post_data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(post_data);
            String corp_code = jsonObj.get("corp_code").toString();
            String store_code = jsonObj.get("store_code").toString();

            List<User> users = storeService.getStoreUser(corp_code, store_code, "", Common.ROLE_AM, Common.IS_ACTIVE_Y);

            result.put("list", JSON.toJSONString(users));
        } catch (Exception ex) {

        }
        return result.toString();
    }

    /**
     * 会员活动线下报名
     */
    @RequestMapping(value = "/api/activityApply", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String activityApply(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String post_data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(post_data);
            String activity_code = jsonObj.get("activity_code").toString();

            VipActivity activity = vipActivityService.getActivityByCode(activity_code);
            VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(activity_code);
            if (activity != null && detail != null) {
                String corp_code = detail.getCorp_code();
                String phone = "";
                String vip_id = "";
                if (jsonObj.containsKey("phone"))
                    phone = jsonObj.getString("phone");
                if (jsonObj.containsKey("vip_id"))
                    vip_id = jsonObj.getString("vip_id");
                String result = vipActivityService.activityApply(detail, phone, vip_id, corp_code);
                return result;
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("活动失效");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员活动 打开推广链接
     *
     * @Param activity_code
     * @Param user_code
     * @Param store_code
     * @Param open_id
     * @Param app_id
     */
    @RequestMapping(value = "/api/activity/openUrl", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String activityOpenUrl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        try {
            String activity_code = request.getParameter("activity_code");
            String user_code = request.getParameter("user_code");
            String store_code = request.getParameter("store_code");
            String open_id = request.getParameter("open_id");
            String app_id = request.getParameter("app_id");

            VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(activity_code);
            String activity_url = detail.getActivity_url();
            String activity_type = detail.getActivity_type();
            String corp_code = detail.getCorp_code();

            JSONObject url = new JSONObject();
            if (activity_url.equals("") && !activity_type.equals("invite")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("该活动未设置链接");
            } else {
                if (!activity_url.equals("")) {
                    url.put("url", activity_url);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId("1");
                    dataBean.setMessage(url.toString());
                } else if (activity_url.equals("") && activity_type.equals("invite")) {
                    activity_url = CommonValue.ishop_url + "/goods/mobile/apply.html?activity_code=" + activity_code + "&open_id=" + open_id + "&user_code=" + user_code + "&store_code=" + store_code;
                    url.put("url", activity_url);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId("1");
                    dataBean.setMessage(url.toString());
                }
                if (open_id != null && !open_id.equals(""))
                    vipActivityService.insertOpenUrlRecord(activity_code, user_code, store_code, open_id, corp_code, app_id);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("链接错误");
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员调店
     */
    @RequestMapping(value = "/api/changeVipStore", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String changeVipStore(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String post_data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(post_data);
            String vip_id = jsonObj.get("vip_id").toString();
            String store_code = jsonObj.get("store_code").toString();
            String store_name = jsonObj.get("store_name").toString();
            String corp_code = jsonObj.get("corp_code").toString();
            String operator_id = jsonObj.get("operator_id").toString();
            String cardno = jsonObj.get("cardno").toString();

            String msg = "0";
            String code = "0";
            if (corp_code.equals("C10016")) {
                HashMap<String, Object> vipInfo = new HashMap<String, Object>();
                vipInfo.put("id", vip_id);
                vipInfo.put("C_STORE_ID__NAME", store_name);
                String result = crmInterfaceService.modInfoVip(corp_code, vipInfo);
                JSONObject result_obj = JSONObject.parseObject(result);
                code = result_obj.getString("code");
                if (!code.equals("0")) {
                    msg = result_obj.getString("message");
                }
            }
            if (code.equals("0")) {
                DataBox dataBox = iceInterfaceAPIService.vipProfileBackup(corp_code, vip_id,  "", "", "", "", "", "", "", "", "", "", "", store_code, operator_id,"");
                logger.info("-------分配店铺" + dataBox.status.toString());
                if (!dataBox.status.toString().equals("SUCCESS")) {
                    msg = "分配失败";
                    if (dataBox.data.containsKey("message"))
                        msg = dataBox.data.get("message").toString();
                }
            }
            if (!msg.equals("0")) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage(msg);
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage("success");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 新增会员，判断会员是否是粉丝转会员
     */
    @RequestMapping(value = "/api/newVip", method = RequestMethod.POST)
    @ResponseBody
    public String newVip(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常";
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String post_data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(post_data);

            final String open_id = jsonObj.get("open_id").toString();
            final String app_id = jsonObj.get("app_id").toString();
            String vipInfo = jsonObj.get("vipInfo").toString();
            logger.info("-------newVip--app_id:" + app_id + "--open_id:" + open_id + "-----vipInfo:" + vipInfo);
            String corp_code = jsonObj.get("corp_code").toString();

            String invite_open_id = "";
            final JSONObject vipInfo_obj = JSONObject.parseObject(vipInfo);

            BasicDBObject queryCondition = new BasicDBObject();
            queryCondition.put("open_id", open_id);
            queryCondition.put("app_id", app_id);

            if (jsonObj.containsKey("invite_open_id")) {
                invite_open_id = jsonObj.get("invite_open_id").toString();
                logger.info("-------invite_open_id" + invite_open_id);
                //会员任务，邀请注册
                DBCollection cursor2 = mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
                queryCondition.put("open_id", invite_open_id);
                queryCondition.put("task.task_type", "invite_registration");
                queryCondition.put("task.task_status", "1");
                queryCondition.put("status", "0");
                DBCursor dbCursor2 = cursor2.find(queryCondition);
                if (dbCursor2.hasNext()) {
                    DBObject dbObject = dbCursor2.next();
                    JSONArray array = new JSONArray();
                    String task_code = dbObject.get("task_code").toString();
                    logger.info("===============task_code" + task_code);

                    JSONObject task_obj = JSONObject.parseObject(dbObject.get("task").toString());
                    VipTask vipTask = WebUtils.JSON2Bean(task_obj, VipTask.class);
                    int target_count = Integer.parseInt(dbObject.get("target_count").toString());
                    if (dbObject.containsField("schedule")) {
                        String schedule1 = dbObject.get("schedule").toString();
                        array = JSONArray.parseArray(schedule1);
                    }
                    int count = 0;
                    for (int i = 0; i < array.size(); i++) {
                        if (array.getJSONObject(i).get("open_id").equals(open_id)) {
                            count = -1;
                            break;
                        }
                    }
                    if (count == 0) {
                        String vip_id = dbObject.get("vip_id").toString();

                        DBObject updateCondition = new BasicDBObject();
                        updateCondition.put("open_id", invite_open_id);
                        updateCondition.put("app_id", app_id);
                        updateCondition.put("task_code", task_code);

                        JSONObject schedule = new JSONObject();
                        schedule.put("share_time", Common.DATETIME_FORMAT.format(new Date()));
                        schedule.put("open_id", open_id);
                        array.add(schedule);
                        DBObject updatedValue = new BasicDBObject();
                        updatedValue.put("schedule", array);
                        updatedValue.put("modified_date", Common.DATETIME_FORMAT.format(new Date()));
                        int flag = 0;
                        if (array.size() >= target_count) {
                            updatedValue.put("status", "1");
                            DataBox dataBox = iceInterfaceAPIService.DisposeTaskData(corp_code,vip_id,task_code);
                            flag = 1;
                        }
                        if (flag == 1) {
                            vipTaskService.sendPresent(vipTask, vipTask.getCorp_code(), vip_id, app_id, invite_open_id, "邀请注册任务赠送");
                            updatedValue.put("is_send", "Y");
                        }
                        DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                        cursor2.update(updateCondition, updateSetValue);
                    }

                }
            }
            webService.processNewVip(corp_code, app_id, open_id, vipInfo_obj, invite_open_id);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("SUCCESS");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 微商城分享商品(未使用)
     */
    @RequestMapping(value = "/api/vipTask/shareGoods", method = RequestMethod.POST)
    @ResponseBody
    public String shareGoods(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常";
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String post_data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(post_data);
            String open_id = jsonObj.get("open_id").toString();
            String app_id = jsonObj.get("app_id").toString();

            logger.info("-------newVip" + app_id + "--" + open_id);

            BasicDBObject queryCondition = new BasicDBObject();
            //会员任务，邀请注册
            DBCollection cursor2 = mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
            queryCondition.put("open_id", jsonObj.get("invite_open_id").toString());
            queryCondition.put("app_id", app_id);
            queryCondition.put("task.task_type", "share_goods");
            queryCondition.put("task.task_status", "1");
            DBCursor dbCursor2 = cursor2.find(queryCondition);
            if (dbCursor2.hasNext()) {
                DBObject dbObject = dbCursor2.next();
                JSONArray array = new JSONArray();
                String task_code = dbObject.get("task_code").toString();
                JSONObject task_obj = JSONObject.parseObject(dbObject.get("task").toString());
                VipTask vipTask = WebUtils.JSON2Bean(task_obj, VipTask.class);
                int target_count = Integer.parseInt(dbObject.get("target_count").toString());
                if (dbObject.containsField("schedule")) {
                    String schedule1 = dbObject.get("schedule").toString();
                    array = JSONArray.parseArray(schedule1);
                }
                String vip_id = dbObject.get("vip_id").toString();

                DBObject updateCondition = new BasicDBObject();
                updateCondition.put("open_id", jsonObj.get("invite_open_id").toString());
                updateCondition.put("app_id", app_id);
                updateCondition.put("task_code", task_code);

                DBObject updatedValue = new BasicDBObject();
                JSONObject schedule = new JSONObject();
                schedule.put("share_time", Common.DATETIME_FORMAT.format(new Date()));
                schedule.put("open_id", open_id);
                array.add(schedule);
                updatedValue.put("schedule", array);
                int flag = 0;
                if (array.size() >= target_count) {
                    updatedValue.put("status", "1");
                    flag = 1;
                }
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                cursor2.update(updateCondition, updateSetValue);
                if (flag == 1) {
                    vipTaskService.sendPresent(vipTask, vipTask.getCorp_code(), vip_id, app_id, open_id, "分享商品任务赠送");
                }
            }

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("SUCCESS");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }


    /**
     * 发送微信模板消息，获取消息详情
     */
    @RequestMapping(value = "/api/wxTempMsgDetail", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String wxTempMsgDetail(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        try {
            String sms_code = request.getParameter("sms_code");

            String vip_id = "";
            if (sms_code.contains("vv")) {
                String[] ss = sms_code.split("vv");
                sms_code = ss[0];
                vip_id = ss[1];
            }
            VipFsend vipFsend = vipFsendService.getVipFsendInfoByCode("", sms_code);
            if (vipFsend != null) {
                JSONObject obj = new JSONObject();
                String content = vipFsend.getContent();
                String pic = "";
                String title = "";
                String templ_url = "";
                if (CheckUtils.checkJson(content)) {
                    JSONObject content_obj = JSONObject.parseObject(content);
                    pic = content_obj.getString("picture_url");
                    title = content_obj.getString("title");
                    content = content_obj.getString("info_content");
                    templ_url = content_obj.getString("details_url");
                }

                if (!vip_id.isEmpty()) {
                    DataBox dataBox = iceInterfaceService.getVipInfo(vipFsend.getCorp_code(), vip_id);
                    logger.info("------vipFsend群发消息-vip列表" + dataBox.status.toString());
                    if (dataBox.status.toString().equals("SUCCESS")) {
                        String message1 = dataBox.data.get("message").value;
                        JSONObject msg_obj = JSONObject.parseObject(message1);
                        JSONArray vips = msg_obj.getJSONArray("vip_info");
                        if (vips.size() > 0) {
                            JSONObject vip_info = vips.getJSONObject(0);
                            title = vipFsendService.textReplace(title, vip_info);
                            content = vipFsendService.textReplace(content, vip_info);
                        }
                    }
                }
                obj.put("image", pic);
                obj.put("title", title);
                obj.put("content", content);
                obj.put("templ_url", templ_url);

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(obj.toString());
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("消息已过期");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 发送会员卡类型变更提醒
     */
    @RequestMapping(value = "/api/vipCardTypeChangeNotice", method = RequestMethod.POST)
    @ResponseBody
    public String vipCardTypeChangeNotice(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {

            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String post_data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(post_data);
            String open_id = jsonObj.get("open_id").toString();
            String app_id = jsonObj.get("app_id").toString();
            String card_no = jsonObj.get("card_no").toString();
            String card_type = jsonObj.get("new_card_type").toString();
            String old_card_type = "";
            if (jsonObj.containsKey("old_card_type"))
                old_card_type = jsonObj.get("old_card_type").toString();
            String vip_name = jsonObj.get("vip_name").toString();

            String change_time = jsonObj.get("change_time").toString();
            String point = jsonObj.get("point").toString();
            String desc = "";
            if (jsonObj.containsKey("desc"))
                desc = jsonObj.get("desc").toString();
            logger.info("-------open_id" + open_id + "--app_id" + app_id + "--card_no" + card_no + "--new_card_type" + card_type + "--change_time" + change_time + "--point" + point);


//            List<WxTemplate> wxTemplates = wxTemplateService.selectTempByAppId(app_id,"",Common.TEMPLATE_NAME_5);
//            if (wxTemplates.size() > 0) {
//                String template_id = wxTemplates.get(0).getTemplate_id();
//                JSONObject msg_content = new JSONObject();
//                msg_content.put("first", "亲爱的会员您好，您的新会员周期已经开始，有效期一年");
//                msg_content.put("keyword1", change_time);
//                msg_content.put("keyword2", card_no);
//                msg_content.put("keyword3", card_type);
//                msg_content.put("keyword4", point);
//                if (desc.equals("DOWN")){
//                    msg_content.put("remark", "点开本消息链接查看您的会员权益");
//                }else {
//                    msg_content.put("remark", "恭喜您获得了两张双倍积分券，进入我的优惠券可以使用。点开本消息链接查看您的会员权益");
//                }
//
//                if (wxTemplates.get(0).getCorp_code().equals("C10183")){
//                    msg_content.put("remark", "点击了解更多会员权益。");
//                    if (desc.equals("DOWN")){
//                        msg_content.put("first", "亲爱的VIP，您的会员卡等级由"+old_card_type+"等级降至"+card_type+"等级。");
//                    }else {
//                        msg_content.put("first", "亲爱的VIP，您的会员卡等级由"+old_card_type+"等级升至"+card_type+"等级。");
//                    }
//                }
//                String template_url = CommonValue.wei_rule_url.replace("@appid@", app_id);
//                wxTemplateService.sendTemplateMsg(app_id, open_id, template_id, msg_content, template_url);
//                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                dataBean.setId(id);
//                dataBean.setMessage("SUCCESS");
//            }
            JSONObject notice = wxTemplateService.vipCardTypeChangeNotice(app_id, card_no, old_card_type, card_type, change_time, point, desc, vip_name);
            if (notice != null) {
                String template_id = notice.getString("template_id");
                JSONObject template_content = notice.getJSONObject("template_content");
                String template_url = notice.getString("template_url");

                wxTemplateService.sendTemplateMsg(app_id, open_id, template_id, template_content, template_url);

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("SUCCESS");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /**
     * rel_vip_emp save
     */
    @RequestMapping(value = "/api/createSchedule", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String createSchedule(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String job_name = request.getParameter("job_name");
            String job_group = request.getParameter("job_group");
            String func = request.getParameter("func");
            String corn = request.getParameter("corn");

            ScheduleJob schedule = new ScheduleJob();
            schedule.setJob_name(job_name);
            schedule.setJob_group(job_group);
            schedule.setStatus("N");
            schedule.setFunc(func);
            schedule.setCron_expression(corn);
            scheduleJobService.insert(schedule);
        } catch (Exception ex) {
            System.out.println("===============总异常========================================");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 根据store_id获取open_id
     */
    @RequestMapping(value = "/api/getOpenId", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getOpenId(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("根据store_id获取open_id---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String store_id = jsonObject.getString("store_id");
            String match_value = jsonObject.getString("match_value");
            match_value = URLEncoder.encode(match_value, "UTF-8");
            Map datalist = new HashMap<String, Data>();
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_store_id.key, data_store_id);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetAppId", datalist);
            String result = dataBox.data.get("message").value;
            String url = "";
            if (null != result) {
                JSONObject object = JSON.parseObject(result);
                String appid = object.get("appid").toString();
                if (!appid.equals("")) {
                    url = CommonValue.wei_openId_url;
                    url = url.replace("@appid@", appid);
                    url = url.replace("@match_value@", match_value);
                    System.out.println("=====url=======" + url);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage(url);
                }else{
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("未授权公众号");
                }
            }else{
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("获取失败");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("获取失败");
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 转连接
     */
    @RequestMapping(value = "/api/getXdShareUrl", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getXdShareUrl(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String store_id = jsonObject.getString("store_id");

            Map datalist = new HashMap<String, Data>();
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_store_id.key, data_store_id);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetAppId", datalist);
            String result = dataBox.data.get("message").value;

            String shareUrl = jsonObject.getString("share_url");
            String encode = URLEncoder.encode(shareUrl, "UTF-8");//编码
            //    String decode_url = URLDecoder.decode(url, "UTF-8");//解码
            String url = "";
            if (null != result) {
                JSONObject object = JSON.parseObject(result);
                String appid = object.get("appid").toString();
                if (!appid.equals("")) {
                    url = CommonValue.wx_shopmatch_url;
                    url = url.replace("@appid@", appid);
                    url = url.replace("@url@", encode);
                    System.out.println("=====url=======" + url);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage(url);
                } else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("品牌未授权公众号");
                }

            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("获取失败");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("获取失败");
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /**
     * 获取wxuser
     */
    @RequestMapping(value = "/api/getWxUserInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getWxUserInfo(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        HttpClient httpClient = new HttpClient();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String code = jsonObject.getString("code");
            String appId = jsonObject.getString("appId");

            String wxUserUrl = CommonValue.wx_getWxUserinfo_url;
            wxUserUrl = wxUserUrl.replace("@code@", code);
            wxUserUrl = wxUserUrl.replace("@appid@", appId);

            Response response = httpClient.get(wxUserUrl);
            String result = response.body().string();
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("获取失败");
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 发送会员卡类型变更提醒
     */
    @RequestMapping(value = "/api/signApplyActivity", method = RequestMethod.POST)
    @ResponseBody
    public String signApplyActivity(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor2 = mongoTemplate.getCollection(CommonValue.table_vip_points_adjust);
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String post_data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(post_data);
            String open_id = jsonObj.get("open_id").toString();
            String app_id = jsonObj.get("app_id").toString();
            String activity_code = jsonObj.get("activity_code").toString();
            String vip_id = jsonObj.get("vip_id").toString();
            JSONObject vip_info = jsonObj.getJSONObject("vip_info");
            String corp_code = jsonObj.get("corp_code").toString();


            VipActivityDetail detail = vipActivityDetailService.selActivityDetailByCode(activity_code);
            String present_time = detail.getPresent_time();
            JSONObject present_time_obj = JSONObject.parseObject(present_time);
            if (present_time_obj.getString("type").equals("timely")){
                String present_coupon = detail.getCoupon_type();
                String present_point = detail.getPresent_point();

                if (present_coupon != null && !present_coupon.equals("")){
                    JSONArray coupon_array = JSONArray.parseArray(present_coupon);
                    for (int i = 0; i < coupon_array.size(); i++) {
                        JSONObject coupon_obj = coupon_array.getJSONObject(i);
                        String coupon_code = coupon_obj.getString("coupon_code");
                        String coupon_name = coupon_obj.getString("coupon_name");

                        DataBox dataBox1 = iceInterfaceAPIService.sendCoupons(corp_code,vip_id,coupon_code,coupon_name,app_id,open_id,"报名活动赠送","","",activity_code+"#"+coupon_code+i+vip_id);
                    }
                }
                if (present_point != null && !present_point.equals("")){
                    logger.info("=================报名活动赠送积分=======");
                    DataBox dataBox1 = iceInterfaceAPIService.sendPoints(corp_code,vip_id,present_point,activity_code+"#"+vip_id);

                    String result = "{\"code\":\"0\",\"data\":\"\",\"message\":\"积分赠送成功\"}";
                    String state = "Y";
                    if (!dataBox1.status.toString().equals("SUCCESS")){
                        logger.info("=================报名活动赠送积分======="+dataBox1.status.toString()+dataBox1.msg);

                        result = "{\"code\":\"-1\",\"data\":\"\",\"message\":\"积分赠送失败\"}";
                        state = "N";
                    }
                    VipPointsAdjust vipPointsAdjust = vipPointsAdjustService.selectPointsAdjustByBillCode(activity_code);
                    if (vipPointsAdjust != null){
                        JSONObject adjust_obj= WebUtils.bean2JSONObject(vipPointsAdjust);
                        BasicDBObject dbObject = new BasicDBObject();
                        dbObject.put("vip",vip_info);
                        dbObject.put("adJustInfo",adjust_obj);//单据信息
                        dbObject.put("corp_code",corp_code);
                        dbObject.put("sendPoints", present_point);
                        dbObject.put("modified_date",Common.DATETIME_FORMAT.format(new Date())); //调整时间
                        dbObject.put("state",state);//未提交
                        dbObject.put("vip_bill_code",activity_code+vip_id);//线下单据
                        dbObject.put("state_info",result);
                        cursor2.save(dbObject);
                    }
                }
            }
            iceInterfaceAPIService.DisposeActivityData(corp_code,vip_id,activity_code,"online_apply");
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("签到成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("失败");
        }
        return dataBean.getJsonStr();
    }


}
