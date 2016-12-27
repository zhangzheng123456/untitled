package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.AESUtils;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhouying on 2016-04-20.
 */


@Controller
public class WebController {

    private static long NETWORK_DELAY_SECONDS = 1000 * 60 * 10;// 10 mininutes

    private static String APP_KEY = "Fghz1Fhp6pM1XajBMjXM";

    private static String SIGN = "41bfa82252f31bef46ccffca4ec22b5e";

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
    ActivityVipService activityVipService;

    private static final Logger logger = Logger.getLogger(WebController.class);

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
                            area_code = area_code.replace(Common.SPECIAL_HEAD,"");
                            String[] areaCodes = area_code.split(",");
                            String[] ids = new String[]{areaCodes[0]};
                            List<Store> list = storeService.selectByAreaBrand(corp_code, ids,null, null,Common.IS_ACTIVE_Y);
                            array.add(list.get(0).getStore_code());
                        } else if (role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_SYS)) {
                            String store_code = storeService.getCorpStore(corp_code).get(0).getStore_code();
                            array.add(store_code);
                        } else {
                            String store_code = user.getStore_code();
                            store_code = store_code.replace(Common.SPECIAL_HEAD,"");
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
                list = goodsService.selectBySearch(1 + rowno / 20, 20, corp_code, "",null);
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
            String corp_code = goods.getCorp_code();
            String goods_code = goods.getGoods_code();
            List<DefGoodsMatch> matchgoods = defGoodsMatchService.selectGoodsMatchList(corp_code,goods_code,Common.IS_ACTIVE_Y);
            goods.setMatchgoods(matchgoods);
            JSONObject result = new JSONObject();
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
                brands = brandService.getActiveBrand(corp_code, "",null);
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
    @RequestMapping(value = "/api/fab/publicImg", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
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
            logger.info("--------corp_code:"+corp_code+"user_code:"+user_code+"search_value:"+search_value+"----------- ");

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
            logger.info("--------brand_code:"+brand_code+"----------- ");

            List<Goods> list = goodsService.selectCorpPublicImgs(corp_code,brand_code,search_value);
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
     * app获取商品列表（微盟桃花季）
     */
    @RequestMapping(value = "/api/weimob/goods", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
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
                goodList = weimobService.getSearchClassify(accessToken, request.getParameter("brand_id"),rowno);
                logger.info("handleWeimob Brand_id ->" + request.getParameter("brand_id"));
            }else if (request.getParameter("key") != null && !request.getParameter("key").equals("")) {
                logger.info("-------------22222222222------------------");
                goodList = weimobService.getSearchTitle(accessToken, request.getParameter("key"),rowno);
                logger.info("handleWeimob Key ->" + request.getParameter("key"));
            }else {
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
    @RequestMapping(value = "/api/weimob/classify", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
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
    @RequestMapping(value = "/api/weimob/auth", method = RequestMethod.GET)
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
     * 活动
     * 获取活动富文本
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/produceActivityUrl", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String produceActivityUrl(HttpServletRequest request) throws Exception {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String code = request.getParameter("code");
            ActivityVip activityVip = this.activityVipService.selActivityByCode(code);
            String content = activityVip.getActivity_content();

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(content);
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 点击登录
     */
    @RequestMapping(value = "/api/login", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public void login(HttpServletRequest request,HttpServletResponse response) {
        logger.info("------------starttime" + new Date());
        String id = "";
        String msg = "请求失败";
        String status = "failed";
        String data = "";
        try {
            String sign= request.getParameter("sign");
            String corp_code= request.getParameter("corp_code");
            String account= request.getParameter("account");
            String password= request.getParameter("password");

            if (sign == null || sign.equals("")) {
                msg = "request_sign";
            }else if (corp_code == null || corp_code.equals("")) {
                msg = "request_corp_code";
            }else if (account == null || account.equals("")) {
                msg = "request_account";
            }else if (password == null || password.equals("")){
                msg = "request_password";
            } else {
                password = AESUtils.Decryptor(password);
                String[] aa = password.split("&&");
                if (aa.length == 2){
                    String timestamp = password.split("&&")[0];
                    password = password.split("&&")[1];

                    long epoch = Long.valueOf(timestamp);
                    logger.debug(" range test:" + System.currentTimeMillis());
                    if (!sign.equals(SIGN)){
                        msg = "param sign Invalid";
                    }else if (System.currentTimeMillis() - epoch < -NETWORK_DELAY_SECONDS || System.currentTimeMillis() - epoch > NETWORK_DELAY_SECONDS) {
                        msg = "timestamp time_out";
                    }else {
                        org.json.JSONObject user_info = userService.selectLoginByUserCode(request, corp_code, account, password);

                        if (user_info == null || user_info.getString("status").contains(Common.DATABEAN_CODE_ERROR)) {
                            msg = user_info.getString("error");
                        } else {
                            response.sendRedirect("/navigation_bar.html?url=/vip/vip.html&func_code=F0040");
                        }
                    }
                }else {
                    msg = "password_without_&&";
                }
            }
            response.sendRedirect(msg.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 新增员工
     */
    @RequestMapping(value = "/api/addUser", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String addUser(HttpServletRequest request,HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String id = "";
        String msg = "";
        String status = "failed";
        try{
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String post_data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(post_data);
            id = jsonObj.containsKey("id")?jsonObj.get("id").toString():"";
            if (!jsonObj.containsKey("sign")){
                msg = "request_sign";
            }else if (!jsonObj.containsKey("timestamp")){
                msg = "request_timestamp";
            }else if (!jsonObj.containsKey("data")){
                msg = "request_data";
            }else {
                String sign= jsonObj.getString("sign");
                String timestamp = jsonObj.getString("timestamp");
                long epoch = Long.valueOf(timestamp);
                if (!sign.equals(SIGN)){
                    msg = "param sign Invalid";
                }else if (System.currentTimeMillis() - epoch < -NETWORK_DELAY_SECONDS || System.currentTimeMillis() - epoch > NETWORK_DELAY_SECONDS) {
                    msg = "timestamp time_out";
                }else {
                    String data = jsonObj.getString("data");
                    org.json.JSONObject data_obj = new org.json.JSONObject(data);
                    if (!data_obj.has("corp_code")) {
                        msg = "request corp_code";
                    } else if (!data_obj.has("user_code")) {
                        msg = "request user_code";
                    } else if (!data_obj.has("phone")) {
                        msg = "request phone";
                    }else {
                        String corp_code = data_obj.getString("corp_code");
//                        String phone = data_obj.getString("phone");
                        List<Group> groups = groupService.selectByCorpRole(corp_code,Common.ROLE_STAFF);
                        User user = WebUtils.JSON2Bean(data_obj, User.class);
                        if (data_obj.has("store_code") && !data_obj.getString("store_code").equals("")){
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
                        if (msg.equals(Common.DATABEAN_CODE_SUCCESS)){
                            result.put("id",id);
                            result.put("status","success");
                            result.put("message","");
                            return result.toString();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            msg = ex.getMessage();
        }
        result.put("id",id);
        result.put("status",status);
        result.put("message",msg);
        return result.toString();
    }

    /**
     * 修改员工信息
     */
    @RequestMapping(value = "/api/editUser", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
    @ResponseBody
    public String editUser(HttpServletRequest request,HttpServletResponse response) {
        JSONObject result = new JSONObject();
        String id = "";
        String msg = "";
        String status = "failed";
        try{
            InputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuffer.append(buffer);
            }
            String post_data = stringBuffer.toString();
            JSONObject jsonObj = JSONObject.parseObject(post_data);
            id = jsonObj.containsKey("id")?jsonObj.get("id").toString():"";
            if (!jsonObj.containsKey("sign")){
                msg = "request_sign";
            }else if (!jsonObj.containsKey("timestamp")){
                msg = "request_timestamp";
            }else if (!jsonObj.containsKey("data")){
                msg = "request_data";
            }else {
                String sign= jsonObj.getString("sign");
                String timestamp = jsonObj.getString("timestamp");
                long epoch = Long.valueOf(timestamp);
                if (!sign.equals(SIGN)){
                    msg = "param sign Invalid";
                }else if (System.currentTimeMillis() - epoch < -NETWORK_DELAY_SECONDS || System.currentTimeMillis() - epoch > NETWORK_DELAY_SECONDS) {
                    msg = "timestamp time_out";
                }else {
                    String data = jsonObj.getString("data");
                    org.json.JSONObject data_obj = new org.json.JSONObject(data);
                    if (!data_obj.has("corp_code")) {
                        msg = "request corp_code";
                    } else if (!data_obj.has("user_code")) {
                        msg = "request user_code";
                    } else if (!data_obj.has("user_code")) {
                        msg = "request user_code";
                    }else {
                        String corp_code = data_obj.getString("corp_code");
                        String user_code = data_obj.getString("user_code");
                        List<User> users = userService.userCodeExist(user_code,corp_code,Common.IS_ACTIVE_Y);
                        User user = users.get(0);
                        String sex = data_obj.has("sex")?data_obj.get("sex").toString():"";
                        String user_name = data_obj.has("user_name")?data_obj.get("user_name").toString():"";
                        String position = data_obj.has("position")?data_obj.get("position").toString():"";
                        String password = data_obj.has("password")?data_obj.get("password").toString():"";
                        if (!sex.equals(""))
                            user.setSex(sex);
                        if (!user_name.equals(""))
                            user.setUser_name(user_name);
                        if (!position.equals(""))
                            user.setPosition(position);
                        if (!password.equals(""))
                            user.setPassword(password);
                        userService.updateUser(user);
                        result.put("id",id);
                        result.put("status","success");
                        result.put("message","");
                        return result.toString();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            msg = ex.getMessage();
        }
        result.put("id",id);
        result.put("status",status);
        result.put("message",msg);
        return result.toString();
    }
}
