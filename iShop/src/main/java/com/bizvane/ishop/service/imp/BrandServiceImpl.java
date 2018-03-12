package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.controller.ShopMatchController;
import com.bizvane.ishop.dao.*;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.BrandService;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.MsgChannelCfgService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by ZhouZhou on 2016/6/4.
 */
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    CorpMapper corpMapper;
    @Autowired
    CorpService corpService;
    @Autowired
    MsgChannelCfgService msgChannelCfgService;
    @Autowired
    MsgChannelsMapper msgChannelsMapper;
    @Autowired
    BrandMapper brandMapper;
    @Autowired
    StoreMapper storeMapper;
    @Autowired
    UserMapper userMapper;

    @Autowired
    MsgChannelCfgMapper msgChannelCfgMapper;

    @Autowired
    private CodeUpdateMapper codeUpdateMapper;
    private static final Logger logger = Logger.getLogger(BrandServiceImpl.class);

    @Override
    public Brand getBrandById(int id) throws Exception {
        Brand brand = brandMapper.selectByBrandId(id);
        if (brand != null) {
            String cus_user_code = brand.getCus_user_code();
            List<JSONObject> array_user = new ArrayList<JSONObject>();
            String app_id = "";
            String child = "";
            String app_name = "";
            String app_logo = "";

            String account = "";
            String sign = "";

            if (cus_user_code != null && !cus_user_code.equals("")) {
                String[] cus_user_codes = cus_user_code.split(",");
                for (int i = 0; i < cus_user_codes.length; i++) {
                    String user_code = cus_user_codes[i];
                    List<User> user = userMapper.selectUserCode(user_code, brand.getCorp_code(), Common.IS_ACTIVE_Y);
                    if (user.size() > 0) {
                        JSONObject userObj = new JSONObject();
                        userObj.put("cus_user_code", user_code);
                        userObj.put("cus_user_name", user.get(0).getUser_name());
                        array_user.add(userObj);
                        ;
                    }
                }
            }

            brand.setCus_user(array_user);
            brand.setChannel_production("");
            brand.setChannel_marketing("");
            List<CorpWechat> corpWechats = corpService.selectWByCorpBrand(brand.getCorp_code(), Common.SPECIAL_HEAD + brand.getBrand_code() + ",");

            List<MsgChannelCfg> msgChannelCfgs = msgChannelCfgService.selectByCorpBrand(brand.getCorp_code(), Common.SPECIAL_HEAD + brand.getBrand_code() + ",");
            List<MsgChannels> MsgChannels = msgChannelsMapper.selectAllChannels();
            if (msgChannelCfgs.size() > 0) {


                String type = msgChannelCfgs.get(0).getType();
                String channel_name1 = msgChannelCfgs.get(0).getChannel_name();
                for (MsgChannels msgChannel : MsgChannels) {
                    String name = msgChannel.getChannel_name();
                    String channel = msgChannel.getChannel();
                    if (channel_name1.equals(channel)) {
                        msgChannelCfgs.get(0).setCh_name(name);
                    }
                }

                if (null == msgChannelCfgs.get(0).getChannel_child() || msgChannelCfgs.get(0).getChannel_child().equals("null") || msgChannelCfgs.get(0).getChannel_child().equals("")) {
                    child = "无";
                } else {
                    child = msgChannelCfgs.get(0).getChannel_child();
                }
                String name_market = msgChannelCfgs.get(0).getCh_name();
                account = msgChannelCfgs.get(0).getChannel_account();
                sign = msgChannelCfgs.get(0).getChannel_sign();
                //   logger.info("==============name_market=========================="+name_market+"==="+msgChannelCfgs.get(i).getName());
                String info = account + sign + "(" + child + ")";
                if (type.equals("Marketing")) {
                    brand.setChannel_marketing(info);
                    brand.setMarket_id(msgChannelCfgs.get(0).getId());
                    // brand.setChannel_production("");
                }

            }

            List<MsgChannelCfg> msgChannelCfgs_pro = msgChannelCfgService.selectByCorpBrandForProduction(brand.getCorp_code(), Common.SPECIAL_HEAD + brand.getBrand_code() + ",");
            logger.info("=============msgChannelCfgs===================" + msgChannelCfgs_pro.size());

            if (msgChannelCfgs_pro.size() > 0) {


                String type = msgChannelCfgs_pro.get(0).getType();
                String channel_name1 = msgChannelCfgs_pro.get(0).getChannel_name();
                for (MsgChannels msgChannel : MsgChannels) {
                    String name = msgChannel.getChannel_name();
                    String channel = msgChannel.getChannel();
                    if (channel_name1.equals(channel)) {
                        msgChannelCfgs_pro.get(0).setCh_name(name);
                        //  logger.info("===================name================" + name);
                    }
                }

                if (null == msgChannelCfgs_pro.get(0).getChannel_child() || msgChannelCfgs_pro.get(0).getChannel_child().equals("null") || msgChannelCfgs_pro.get(0).getChannel_child().equals("")) {
                    child = "无";
                } else {
                    child = msgChannelCfgs_pro.get(0).getChannel_child();
                }
                String name_market = msgChannelCfgs_pro.get(0).getCh_name();
                account = msgChannelCfgs_pro.get(0).getChannel_account();
                sign = msgChannelCfgs_pro.get(0).getChannel_sign();
                //logger.info("==============name_market=========================="+name_market+"==="+msgChannelCfgs.get(i).getName());

                String info = account + sign + "(" + child + ")";
                logger.info("=============info=========================" + info);
                brand.setChannel_production(info);
                brand.setProduct_id(msgChannelCfgs_pro.get(0).getId());
                // brand.setChannel_marketing("");
            }


            if (corpWechats.size() > 0) {
                for (int i = 0; i < corpWechats.size(); i++) {
                    String app_id1 = corpWechats.get(i).getApp_id();
                    String app_name1 = corpWechats.get(i).getApp_name();
                    String app_logo1 = corpWechats.get(i).getApp_logo();
                    app_id = app_id + app_id1 + ",";
                    app_name = app_name + app_name1 + ",";
                    app_logo = app_logo + app_logo1 + ",";
                }
                app_id = app_id.substring(0, app_id.length() - 1);
                app_name = app_name.substring(0, app_name.length() - 1);
                app_logo = app_logo.substring(0, app_logo.length() - 1);
            }
            brand.setApp_id(app_id);
            brand.setApp_name(app_name);
            brand.setApp_logo(app_logo);
        }
        return brand;
    }

    @Override
    public Brand getBrandByCode(String corp_code, String brand_code, String isactive) throws SQLException {
        return brandMapper.selectByBrandCode(corp_code, brand_code, isactive);
    }

    @Override
    public Brand getBrandByName(String corp_code, String brand_name, String isactive) throws Exception {
        Brand brand = brandMapper.selectByBrandName(corp_code, brand_name, isactive);
        return brand;
    }

    @Override
    public PageInfo<Brand> getAllBrandByPage(int page_number, int page_size, String corp_code, String search_value, String manager_corp) throws SQLException {
        List<Brand> brands;
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        PageHelper.startPage(page_number, page_size);
        brands = brandMapper.selectAllBrand(corp_code, search_value, manager_corp_arr);
        for (Brand brand : brands) {
            brand.setIsactive(CheckUtils.CheckIsactive(brand.getIsactive()));
        }
        PageInfo<Brand> page = new PageInfo<Brand>(brands);

        return page;
    }

    @Override
    public PageInfo<Brand> getPartBrandByPage(int page_number, int page_size, String corp_code, String[] brand_code, String search_value) throws SQLException {
        List<Brand> brands;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("brand_code", brand_code);
        params.put("search_value", search_value);

        PageHelper.startPage(page_number, page_size);
        brands = brandMapper.selectPartBrand(params);
        for (Brand brand : brands) {
            brand.setIsactive(CheckUtils.CheckIsactive(brand.getIsactive()));
        }
        PageInfo<Brand> page = new PageInfo<Brand>(brands);

        return page;
    }

    @Override
    public List<Brand> getActiveBrand(String corp_code, String search_value, String[] brand_codes) throws Exception {
        String[] brand_code = null;
        if (brand_codes != null && brand_codes.length > 0 && !brand_codes[0].equals("")) {
            brand_code = brand_codes;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("brand_code", brand_code);
        params.put("search_value", search_value);
        List<Brand> brands;
        brands = brandMapper.selectBrands(params);
        return brands;
    }

    @Override
    public List<Brand> getActiveBrand(String corp_code, String search_value, String[] brand_codes, String manager_corp) throws Exception {
        String[] brand_code = null;
        if (brand_codes != null && brand_codes.length > 0 && !brand_codes[0].equals("")) {
            brand_code = brand_codes;
        }
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("manager_corp_arr", manager_corp_arr);
        params.put("corp_code", corp_code);
        params.put("brand_code", brand_code);
        params.put("search_value", search_value);
        List<Brand> brands = brandMapper.selectBrands(params);
        return brands;
    }

    @Override
    @Transactional
    public String insert(String message, String user_code) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = JSONObject.parseObject(message);
        String brand_code = jsonObject.get("brand_code").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String brand_name = jsonObject.get("brand_name").toString().trim();
        String Marketing = jsonObject.get("Marketing").toString().trim();
        String Production = jsonObject.get("Production").toString().trim();
        logger.info("==============brand==add===============================" + Marketing + "=========" + Production);
        //公众号logo
        String app_logo = jsonObject.get("app_logo").toString().trim();
        JSONArray array_logo = JSON.parseArray(app_logo);
        //品牌logo
        String logo = "";
        if (jsonObject.containsKey("logo"))
            logo = jsonObject.get("logo").toString().trim();
        Brand brand = getBrandByCode(corp_code, brand_code, Common.IS_ACTIVE_Y);
        Brand brand1 = getBrandByName(corp_code, brand_name, Common.IS_ACTIVE_Y);
        //验证品牌名称和品牌编号的唯一性
        if (brand == null && brand1 == null) {
            brand = new Brand();
            Date now = new Date();
            brand.setBrand_code(brand_code);
            brand.setBrand_name(brand_name);
            brand.setCorp_code(corp_code);
            if (jsonObject.containsKey("cus_user_code")) {
                brand.setCus_user_code(jsonObject.get("cus_user_code").toString().trim());
            }
            if (jsonObject.containsKey("app_id")) {
                String app_id = jsonObject.get("app_id").toString().trim();

                if (!app_id.equals("")) {
                    String[] app_ids = app_id.split(",");
                    for (int i = 0; i < app_ids.length; i++) {
                        CorpWechat corpWechat = corpMapper.selectWByAppId(corp_code, app_ids[i]);
                        String brand_codes = "";
                        if (corpWechat.getBrand_code() != null)
                            brand_codes = corpWechat.getBrand_code();
                        if (!brand_codes.contains(Common.SPECIAL_HEAD + brand_code + ",")) {
                            brand_codes = brand_codes + Common.SPECIAL_HEAD + brand_code + ",";
                            corpWechat.setBrand_code(brand_codes);
                            corpWechat.setModified_date(Common.DATETIME_FORMAT.format(now));
                            corpWechat.setModifier(user_code);
                            corpMapper.updateCorpWechat(corpWechat);
                        }
                    }

                    //更新公众号logo
                    for (int j = 0; j < array_logo.size(); j++) {
                        JSONObject obj = array_logo.getJSONObject(j);
                        String obj_appid = obj.getString("app_id");
                        String obj_url = obj.getString("logo_url");
                        CorpWechat corpWechat = corpMapper.selectWByAppId(corp_code, obj_appid);
                        if (corpWechat.getBrand_code() != null)
                            corpWechat.setApp_logo(obj_url);
                        corpWechat.setModified_date(Common.DATETIME_FORMAT.format(now));
                        corpWechat.setModifier(user_code);
                        corpMapper.updateCorpWechat(corpWechat);
                    }

                }


            }
            if (null != Marketing && !Marketing.equals("")) {
                MsgChannelCfg msgChannelCfg = msgChannelCfgMapper.selMsgChannelCfgById(Integer.valueOf(Marketing));

                brand.setChannel_marketing(Marketing);
                logger.info("=================Marketing=====================" + brand.getChannel_marketing());
                String brand_codes = "";
                if (msgChannelCfg.getBrand_code() != null) {
                    brand_codes = msgChannelCfg.getBrand_code();
                }
                if (!brand_codes.contains(Common.SPECIAL_HEAD + brand_code + ",")) {
                    brand_codes = brand_codes + Common.SPECIAL_HEAD + brand_code + ",";
                    msgChannelCfg.setBrand_code(brand_codes);
                    msgChannelCfg.setModified_date(Common.DATETIME_FORMAT.format(now));
                    msgChannelCfg.setModifier(user_code);
                    msgChannelCfgMapper.updateMsgChannelCfg(msgChannelCfg);
                }
            } else {
                brand.setChannel_marketing("");
            }

            if (null != Production && !Production.equals("")) {

                MsgChannelCfg msgChannelCfg = msgChannelCfgMapper.selMsgChannelCfgById(Integer.valueOf(Production));
                brand.setChannel_production(Production);
                logger.info("=========Production====" + brand.getChannel_production());

                String brand_codes = "";
                if (msgChannelCfg.getBrand_code() != null)
                    brand_codes = msgChannelCfg.getBrand_code_production();
                if (!brand_codes.contains(Common.SPECIAL_HEAD + brand_code + ",")) {
                    brand_codes = brand_codes + Common.SPECIAL_HEAD + brand_code + ",";
                    msgChannelCfg.setBrand_code_production(brand_codes);
                    msgChannelCfg.setModified_date(Common.DATETIME_FORMAT.format(now));
                    msgChannelCfg.setModifier(user_code);
                    msgChannelCfgMapper.updateMsgChannelCfg(msgChannelCfg);
                }
            } else {
                brand.setChannel_production("");
            }


            brand.setCreated_date(Common.DATETIME_FORMAT.format(now));
            brand.setCreater(user_code);
            brand.setModified_date(Common.DATETIME_FORMAT.format(now));

            brand.setModifier(user_code);
            brand.setIsactive(jsonObject.get("isactive").toString());
            brand.setLogo(logo);
            brandMapper.insertBrand(brand);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else if (brand != null) {
            result = "品牌编号已存在";
        } else {
            result = "品牌名称已存在";
        }


        return result;
    }

    @Override
    @Transactional
    public String insertExecl(Brand brand) throws Exception {
        brandMapper.insertBrand(brand);
        return "add success";
    }

    @Override
    @Transactional
    public String updateExecl(Brand brand) throws Exception {
        brandMapper.updateBrand(brand);
        return "upd success";
    }

    @Override
    public int getGoodsCount(String corp_code, String brand_code) throws Exception {
        return brandMapper.getGoodsCount(corp_code, brand_code);
    }


    @Override
    @Transactional
    public String update(String message, String user_code) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = JSONObject.parseObject(message);
        int brand_id = Integer.parseInt(jsonObject.get("id").toString());

        String brand_code = jsonObject.get("brand_code").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String brand_name = jsonObject.get("brand_name").toString().trim();
        //  公众号logo
        String app_logo = jsonObject.get("app_logo").toString().trim();
        JSONArray array_logo = JSON.parseArray(app_logo);
        //短信通道
        String Marketing = jsonObject.get("Marketing").toString().trim();
        String Production = jsonObject.get("Production").toString().trim();
        String marketing_id = jsonObject.get("marketing_id").toString().trim();
        String production_id = jsonObject.get("production_id").toString().trim();
        logger.info("==============brand==edit==============================" + Marketing + "=========" + Production);
        String logo = "";
        if (jsonObject.containsKey("logo"))
            logo = jsonObject.get("logo").toString().trim();

        Brand brand = getBrandById(brand_id);
        String app_id1 = brand.getApp_id();
        logger.info("=============brand.getChannel_marketing()==========================" + brand.getChannel_marketing());

        Brand brand1 = getBrandByCode(corp_code, brand_code, Common.IS_ACTIVE_Y);
        Brand brand2 = getBrandByCode(corp_code, brand_name, Common.IS_ACTIVE_Y);
        if (brand.getCorp_code().trim().equals(corp_code)) {
            if ((brand.getBrand_code().equals(brand_code) || brand1 == null) && (brand.getBrand_name().trim().equals(brand_name) || brand2 == null)) {
                if (!brand.getBrand_code().trim().equals(brand_code)) {
                    updateCauseCodeChange(corp_code, brand_code, brand.getBrand_code());
                }
//                brand = new Brand();
                Date now = new Date();
                brand.setId(brand_id);
                brand.setBrand_code(brand_code);
                brand.setBrand_name(brand_name);
                brand.setCorp_code(corp_code);

                if (jsonObject.containsKey("cus_user_code")) {
                    brand.setCus_user_code(jsonObject.get("cus_user_code").toString().trim());
                }

                if (app_id1 != null && !app_id1.equals("")) {
                    String[] app_ids = app_id1.split(",");
                    for (int i = 0; i < app_ids.length; i++) {

                        CorpWechat corpWechat = corpMapper.selectWByAppId(corp_code, app_ids[i]);
                        String brand_codes = corpWechat.getBrand_code();
                        brand_codes = brand_codes.replace(Common.SPECIAL_HEAD + brand_code + ",", "");
                        corpWechat.setBrand_code(brand_codes);
                        corpWechat.setModified_date(Common.DATETIME_FORMAT.format(now));
                        corpWechat.setModifier(user_code);
                        corpMapper.updateCorpWechat(corpWechat);
                    }

                }
                if (jsonObject.containsKey("app_id")) {
                    String app_id = jsonObject.get("app_id").toString().trim();
                    if (!app_id.equals("")) {
                        String[] app_ids = app_id.split(",");
                        for (int i = 0; i < app_ids.length; i++) {
                            CorpWechat corpWechat = corpMapper.selectWByAppId(corp_code, app_ids[i]);
                            String brand_codes = "";
                            if (corpWechat.getBrand_code() != null)
                                brand_codes = corpWechat.getBrand_code();
                            if (!brand_codes.contains(Common.SPECIAL_HEAD + brand_code + ",")) {
                                brand_codes = brand_codes + Common.SPECIAL_HEAD + brand_code + ",";
                                corpWechat.setBrand_code(brand_codes);
                                corpWechat.setModified_date(Common.DATETIME_FORMAT.format(now));
                                corpWechat.setModifier(user_code);
                                corpMapper.updateCorpWechat(corpWechat);
                            }
                        }
                        //更新公众号logo
                        for (int j = 0; j < array_logo.size(); j++) {
                            JSONObject obj = array_logo.getJSONObject(j);
                            String obj_appid = obj.getString("app_id");
                            String obj_url = obj.getString("logo_url");
                            CorpWechat corpWechat1 = corpMapper.selectWByAppId(corp_code, obj_appid);
                            if (corpWechat1.getBrand_code() != null)
                                corpWechat1.setApp_logo(obj_url);
                            corpWechat1.setModified_date(Common.DATETIME_FORMAT.format(now));
                            corpWechat1.setModifier(user_code);
                            corpMapper.updateCorpWechat(corpWechat1);
                        }

                    }
                    brand.setModified_date(Common.DATETIME_FORMAT.format(now));
                    brand.setModifier(user_code);
                    brand.setIsactive(jsonObject.get("isactive").toString());
                    brand.setLogo(logo);
                    brandMapper.updateBrand(brand);
                    result = Common.DATABEAN_CODE_SUCCESS;
                }

                //营销通道
                if (null != Marketing && !Marketing.equals("")) {
                    MsgChannelCfg msgChannelCfg = msgChannelCfgMapper.selMsgChannelCfgById(Integer.valueOf(Marketing));
                    if (!marketing_id.equals("")) {
                        MsgChannelCfg old = msgChannelCfgMapper.selMsgChannelCfgById(Integer.valueOf(marketing_id));
                        String code = old.getBrand_code();
                        old.setBrand_code(code.replace(Common.SPECIAL_HEAD + brand_code + ",", ""));

                        old.setModified_date(Common.DATETIME_FORMAT.format(now));
                        old.setModifier(user_code);
                        msgChannelCfgMapper.updateMsgChannelCfg(old);
                    }
                    brand.setChannel_marketing(Marketing);
                    String brand_codes = "";
                    if (msgChannelCfg.getBrand_code() != null)
                        brand_codes = msgChannelCfg.getBrand_code();
                    if (!brand_codes.contains(Common.SPECIAL_HEAD + brand_code + ",")) {
                        brand_codes = brand_codes + Common.SPECIAL_HEAD + brand_code + ",";
                        msgChannelCfg.setBrand_code(brand_codes);
                        msgChannelCfg.setModified_date(Common.DATETIME_FORMAT.format(now));
                        msgChannelCfg.setModifier(user_code);
                        msgChannelCfgMapper.updateMsgChannelCfg(msgChannelCfg);
                    }

                    brand.setModified_date(Common.DATETIME_FORMAT.format(now));
                    brand.setModifier(user_code);
                    brand.setIsactive(jsonObject.get("isactive").toString());
                    brandMapper.updateBrand(brand);
                    result = Common.DATABEAN_CODE_SUCCESS;
                } else {
                    brand.setChannel_marketing("");
                }

                if (null != Production && !Production.equals("")) {
                    MsgChannelCfg msgChannelCfg = msgChannelCfgMapper.selMsgChannelCfgById(Integer.valueOf(Production));
                    if (!production_id.equals("")) {
                        MsgChannelCfg old1 = msgChannelCfgMapper.selMsgChannelCfgById(Integer.valueOf(production_id));
                        String code = old1.getBrand_code_production();
                        old1.setBrand_code_production(code.replace(Common.SPECIAL_HEAD + brand_code + ",", ""));
                        //  logger.info("===================old1=================================="+old1.getBrand_code_production());
                        old1.setModified_date(Common.DATETIME_FORMAT.format(now));
                        old1.setModifier(user_code);
                        msgChannelCfgMapper.updateMsgChannelCfg(old1);
                    }
                    String brand_codes = "";
                    if (msgChannelCfg.getBrand_code() != null)
                        brand_codes = msgChannelCfg.getBrand_code_production();
                    if (!brand_codes.contains(Common.SPECIAL_HEAD + brand_code + ",")) {
                        brand_codes = brand_codes + Common.SPECIAL_HEAD + brand_code + ",";
                        msgChannelCfg.setBrand_code_production(brand_codes);
                        msgChannelCfg.setModified_date(Common.DATETIME_FORMAT.format(now));
                        msgChannelCfg.setModifier(user_code);
                        msgChannelCfgMapper.updateMsgChannelCfg(msgChannelCfg);
                    }
                    brand.setChannel_production(Production);
                    //  logger.info("=======Production=====" + Production + "=====" + brand.getChannel_production());
                    brand.setModified_date(Common.DATETIME_FORMAT.format(now));
                    brand.setModifier(user_code);
                    brand.setIsactive(jsonObject.get("isactive").toString());
                    brandMapper.updateBrand(brand);
                    result = Common.DATABEAN_CODE_SUCCESS;
                } else {
                    brand.setChannel_production("");
                }
            } else if (!brand.getBrand_code().trim().equals(brand_code) && brand1 != null) {
                result = "品牌编号已存在";
                return result;
            } else {
                result = "品牌名称已存在";
                return result;
            }
        } else {
            if (brand1 == null && brand2 == null) {
                brand = new Brand();
                Date now = new Date();
                brand.setId(brand_id);
                brand.setBrand_code(brand_code);
                brand.setBrand_name(brand_name);
                brand.setCorp_code(corp_code);

                if (jsonObject.containsKey("cus_user_code")) {
                    brand.setCus_user_code(jsonObject.get("cus_user_code").toString().trim());
                }
                if (jsonObject.containsKey("app_id")) {
                    String app_id = jsonObject.get("app_id").toString().trim();
                    if (!app_id.equals("")) {
                        String[] app_ids = app_id.split(",");
                        for (int i = 0; i < app_ids.length; i++) {
                            CorpWechat corpWechat = corpMapper.selectWByAppId(corp_code, app_ids[i]);
                            String brand_codes = corpWechat.getBrand_code();
                            if (!brand_codes.contains(Common.SPECIAL_HEAD + brand_code + ",")) {
                                brand_codes = brand_codes + Common.SPECIAL_HEAD + brand_code + ",";
                                corpWechat.setBrand_code(brand_codes);
                                corpWechat.setModified_date(Common.DATETIME_FORMAT.format(now));
                                corpWechat.setModifier(user_code);
                                corpMapper.updateCorpWechat(corpWechat);
                            }
                        }
                        //更新公众号logo
                        for (int j = 0; j < array_logo.size(); j++) {
                            JSONObject obj = array_logo.getJSONObject(j);
                            String obj_appid = obj.getString("app_id");
                            String obj_url = obj.getString("logo_url");
                            CorpWechat corpWechat = corpMapper.selectWByAppId(corp_code, obj_appid);
                            if (corpWechat.getBrand_code() != null)
                                corpWechat.setApp_logo(obj_url);
                            corpWechat.setModified_date(Common.DATETIME_FORMAT.format(now));
                            corpWechat.setModifier(user_code);
                            corpMapper.updateCorpWechat(corpWechat);
                        }
                        brand.setModified_date(Common.DATETIME_FORMAT.format(now));
                        brand.setModifier(user_code);
                        brand.setIsactive(jsonObject.get("isactive").toString());
                        brand.setLogo(logo);
                        brandMapper.updateBrand(brand);
                        result = Common.DATABEAN_CODE_SUCCESS;
                    }
                }

                //营销通道
                if (null != Marketing && !Marketing.equals("")) {
                    MsgChannelCfg msgChannelCfg = msgChannelCfgMapper.selMsgChannelCfgById(Integer.valueOf(Marketing));
                    brand.setChannel_marketing(Marketing);
                    if (!marketing_id.equals("")) {
                        MsgChannelCfg old = msgChannelCfgMapper.selMsgChannelCfgById(Integer.valueOf(marketing_id));
                        String code = old.getBrand_code();
                        //logger.info("===================Marketing==code================================"+code);
                        old.setBrand_code(code.replace(Common.SPECIAL_HEAD + brand_code + ",", ""));
                        // logger.info("===================Marketing=================================="+old.getBrand_code());
                        old.setModified_date(Common.DATETIME_FORMAT.format(now));
                        old.setModifier(user_code);
                        msgChannelCfgMapper.updateMsgChannelCfg(old);
                    }
                    String brand_codes = "";
                    if (msgChannelCfg.getBrand_code() != null)
                        brand_codes = msgChannelCfg.getBrand_code();
                    if (!brand_codes.contains(Common.SPECIAL_HEAD + brand_code + ",")) {
                        brand_codes = brand_codes + Common.SPECIAL_HEAD + brand_code + ",";
                        msgChannelCfg.setBrand_code(brand_codes);
                        msgChannelCfg.setModified_date(Common.DATETIME_FORMAT.format(now));
                        msgChannelCfg.setModifier(user_code);
                        msgChannelCfgMapper.updateMsgChannelCfg(msgChannelCfg);
                    }

                    // logger.info("=======Marketing=====" + Marketing + "=====" + brand.getChannel_marketing());
                    brand.setModified_date(Common.DATETIME_FORMAT.format(now));
                    brand.setModifier(user_code);
                    brand.setIsactive(jsonObject.get("isactive").toString());
                    brandMapper.updateBrand(brand);
                    result = Common.DATABEAN_CODE_SUCCESS;
                } else {
                    brand.setChannel_marketing("");
                }

                if (null != Production && !Production.equals("")) {
                    MsgChannelCfg msgChannelCfg = msgChannelCfgMapper.selMsgChannelCfgById(Integer.valueOf(Production));
                    if (!production_id.equals("")) {
                        MsgChannelCfg old1 = msgChannelCfgMapper.selMsgChannelCfgById(Integer.valueOf(production_id));
                        String code = old1.getBrand_code_production();
                        old1.setBrand_code_production(code.replace(Common.SPECIAL_HEAD + brand_code + ",", ""));
                        old1.setModified_date(Common.DATETIME_FORMAT.format(now));
                        old1.setModifier(user_code);
                        msgChannelCfgMapper.updateMsgChannelCfg(old1);
                    }

                    brand.setChannel_production(Production);
                    String brand_codes = "";
                    if (msgChannelCfg.getBrand_code() != null)
                        brand_codes = msgChannelCfg.getBrand_code();
                    if (!brand_codes.contains(Common.SPECIAL_HEAD + brand_code + ",")) {
                        brand_codes = brand_codes + Common.SPECIAL_HEAD + brand_code + ",";
                        msgChannelCfg.setBrand_code(brand_codes);
                        msgChannelCfg.setModified_date(Common.DATETIME_FORMAT.format(now));
                        msgChannelCfg.setModifier(user_code);
                        msgChannelCfgMapper.updateMsgChannelCfg(msgChannelCfg);
                    }

                    //logger.info("=======Production=====" + Production + "=====" + brand.getChannel_production());
                    brand.setModified_date(Common.DATETIME_FORMAT.format(now));
                    brand.setModifier(user_code);
                    brand.setIsactive(jsonObject.get("isactive").toString());
                    brandMapper.updateBrand(brand);
                    result = Common.DATABEAN_CODE_SUCCESS;
                } else {
                    brand.setChannel_marketing("");
                }

            } else if (!brand.getBrand_code().equals(brand_code) && brand1 != null) {
                result = "品牌编号已存在";
                return result;
            } else {
                result = "品牌名称已存在";
                return result;
            }
        }
        return result;
    }


    @Override
    @Transactional
    public int delete(int id,String user_code) throws Exception {

        return brandMapper.deleteByBrandId(id);
    }

    @Override
    public PageInfo<Brand> getAllBrandScreen(int page_number, int page_size, String corp_code, String[] brand_code, Map<String, String> map, String manager_corp) throws Exception {
        List<Brand> brands;
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("brand_code", brand_code);
        params.put("map", map);
        params.put("manager_corp_arr", manager_corp_arr);
        brands = brandMapper.selectAllBrandScreen(params);
        for (Brand brand : brands) {
            brand.setIsactive(CheckUtils.CheckIsactive(brand.getIsactive()));
        }
        PageInfo<Brand> page = new PageInfo<Brand>(brands);
        return page;
    }

    /**
     * 更改品牌编号时
     * 级联更改关联此编号的店铺，商品列表
     */
    @Transactional
    void updateCauseCodeChange(String corp_code, String new_brand_code, String old_brand_code) throws Exception {
        //商品列表级联修改
        codeUpdateMapper.updateGoods("", corp_code, new_brand_code, old_brand_code);

        //店铺列表级联修改
        codeUpdateMapper.updateStore("", corp_code, Common.SPECIAL_HEAD + new_brand_code + ",", Common.SPECIAL_HEAD + old_brand_code + ",", "", "");

        //员工
        codeUpdateMapper.updateUser("", corp_code, "", "", "", "", "", "", Common.SPECIAL_HEAD + new_brand_code + ",", Common.SPECIAL_HEAD + old_brand_code + ",");

    }

    @Override
    public List<Brand> selectBrandByLabel(String corp_code, String[] brand_code) throws SQLException {
        return brandMapper.selectBrandByLabel(corp_code, brand_code);
    }
}
