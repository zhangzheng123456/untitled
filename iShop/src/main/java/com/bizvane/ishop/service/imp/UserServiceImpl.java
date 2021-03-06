package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.*;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * Created by maoweidong on 2016/2/15.
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    CorpMapper corpMapper;
    @Autowired
    StoreService storeService;
    @Autowired
    RoleService roleService;
    @Autowired
    SignService signService;
    @Autowired
    ValidateCodeService validateCodeService;
    @Autowired
    GroupMapper groupMapper;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    UserAchvGoalMapper userAchvGoalMapper;
    @Autowired
    CodeUpdateMapper codeUpdateMapper;
    @Autowired
    private PrivilegeMapper privilegeMapper;
    @Autowired
    private BrandService brandService;
    @Autowired
    AppversionService appversionService;
    @Autowired
    BaseService baseService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    CorpService corpService;
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

    /**
     * @param corp_code
     * @param search_value
     */
    public PageInfo<User> selectBySearch(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value, String user_back) throws Exception {

        List<User> users;
        PageHelper.startPage(page_number, page_size);
        users = userMapper.selectAllUser(corp_code, search_value, user_back,null);
        conversion(users);
        request.getSession().setAttribute("size", users.size());
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    public PageInfo<User> selectBySearch(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value, String user_back,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        List<User> users;
        PageHelper.startPage(page_number, page_size);
        users = userMapper.selectAllUser(corp_code, search_value, user_back,manager_corp_arr);
        conversion(users);
        request.getSession().setAttribute("size", users.size());
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }
    public List<User> selectBySearch(String corp_code) throws Exception {

        List<User> users;
        users = userMapper.selectAllUser(corp_code, "", "",null);
        return users;
    }


    /**
     * 用户拥有店铺下的员工(包括区经)
     * （属于自己拥有的店铺，且角色级别比自己低）
     */
    public PageInfo<User> selectUsersByRole(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String[] areas, String role_code) throws Exception {
        String[] stores = null;
//        String[] areas = null;
        if (!store_code.equals("")) {
            stores = store_code.split(",");
            for (int i = 0; i < stores.length; i++) {
                stores[i] = Common.SPECIAL_HEAD + stores[i] + ",";
            }
        }
        if (!area_code.equals("")) {
            String[] area_codes = area_code.split(",");
            List<Store> store = storeService.selectByAreaBrand(corp_code, area_codes, null, null, Common.IS_ACTIVE_Y);
            String a = "";
            if (store.size() > 0) {
                for (int i = 0; i < store.size(); i++) {
                    a = a + Common.SPECIAL_HEAD + store.get(i).getStore_code() + ",";
                }
            } else {
                a = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz11223344";
            }
            stores = a.split(",");
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
        params.put("search_value", search_value);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        //根据areas拉取区经
        params.put("areas", areas);
        PageHelper.startPage(page_number, page_size);
        List<User> users = userMapper.selectUsersByRole(params);
//        conversion(users);

        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    @Override
    public PageInfo<User> selectUsersByRoleAndGroup(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String[] areas, String role_code) throws Exception {
        String[] stores = null;
//        String[] areas = null;
        if (!store_code.equals("")) {
            stores = store_code.split(",");
            for (int i = 0; i < stores.length; i++) {
                stores[i] = Common.SPECIAL_HEAD + stores[i] + ",";
            }
        }
        if (!area_code.equals("")) {
            String[] area_codes = area_code.split(",");
            List<Store> store = storeService.selectByAreaBrand(corp_code, area_codes, null, null, Common.IS_ACTIVE_Y);
            String a = "";
            if (store.size() > 0) {
                for (int i = 0; i < store.size(); i++) {
                    a = a + Common.SPECIAL_HEAD + store.get(i).getStore_code() + ",";
                }
            } else {
                a = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz11223344";
            }
            stores = a.split(",");
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
        params.put("search_value", search_value);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        //根据areas拉取区经
        params.put("areas", areas);
        PageHelper.startPage(page_number, page_size);
        List<User> users = userMapper.selectUsersByRoleAndGroup(params);
//        conversion(users);

        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }


    /**
     * 用户拥有店铺下的员工(包括区经)
     * （属于自己拥有的店铺，且角色级别比自己低）
     * 任务列表查看详情
     * 所属群组
     */
    public PageInfo<User> selectUsersOfTask(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String[] areas, String role_code,String group_code) throws Exception {
        String[] stores = null;
//        String[] areas = null;
        if (!store_code.equals("")) {
            stores = store_code.split(",");
            for (int i = 0; i < stores.length; i++) {
                stores[i] = Common.SPECIAL_HEAD + stores[i] + ",";
            }
        }
        if (!area_code.equals("")) {
            String[] area_codes = area_code.split(",");
            List<Store> store = storeService.selectByAreaBrand(corp_code, area_codes, null, null, Common.IS_ACTIVE_Y);
            String a = "";
            if (store.size() > 0) {
                for (int i = 0; i < store.size(); i++) {
                    a = a + Common.SPECIAL_HEAD + store.get(i).getStore_code() + ",";
                }
            } else {
                a = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz11223344";
            }
            stores = a.split(",");
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
        params.put("search_value", search_value);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        params.put("group_code", group_code);
        //根据areas拉取区经
        params.put("areas", areas);
        PageHelper.startPage(page_number, page_size);
        List<User> users = userMapper.selectUsersOfTask(params);
//        conversion(users);

        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    public PageInfo<User> selectUsersByRole(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String[] areas, String role_code,String manager_corp) throws Exception {
        String[] stores = null;
//        String[] areas = null;
        if (!store_code.equals("")) {
            stores = store_code.split(",");
            for (int i = 0; i < stores.length; i++) {
                stores[i] = Common.SPECIAL_HEAD + stores[i] + ",";
            }
        }
        if (!area_code.equals("")) {
            String[] area_codes = area_code.split(",");
            List<Store> store = storeService.selectByAreaBrand(corp_code, area_codes, null, null, Common.IS_ACTIVE_Y);
            String a = "";
            if (store.size() > 0) {
                for (int i = 0; i < store.size(); i++) {
                    a = a + Common.SPECIAL_HEAD + store.get(i).getStore_code() + ",";
                }
            } else {
                a = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz11223344";
            }
            stores = a.split(",");
        }
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("manager_corp_arr", manager_corp_arr);
        params.put("array", stores);
        params.put("search_value", search_value);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        //根据areas拉取区经
        params.put("areas", areas);
        PageHelper.startPage(page_number, page_size);
        List<User> users = userMapper.selectUsersByRole(params);
//        conversion(users);

        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }
    @Override
    public List<User> selectPartUser2Code(String corp_code, String search_value, String store_code, String area_store, String area_code, String role_code) throws Exception {
        String[] stores = null;

        if (!store_code.equals("")) {
            stores = store_code.split(",");
        } else {
            store_code = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz" + Common.SPECIAL_HEAD + Common.SPECIAL_HEAD;
            stores = store_code.split(",");
        }
        if (!area_code.equals("")) {
            String[] storeCodes = null;
            if (!area_store.equals("")) {
                area_store = area_store.replace(Common.SPECIAL_HEAD, "");
                storeCodes = area_store.split(",");
            }
            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
            String[] areas = area_code.split(",");
            List<Store> store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, Common.IS_ACTIVE_Y);
            String a = "";
            for (int i = 0; i < store.size(); i++) {
                a = a + Common.SPECIAL_HEAD + store.get(i).getStore_code() + ",";
            }
            if (a.equals("")) {
                a = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz" + Common.SPECIAL_HEAD + Common.SPECIAL_HEAD;
                stores = a.split(",");
            } else {
                stores = a.split(",");

            }
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
        params.put("search_value", search_value);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);

        List<User> users = userMapper.selectPartUser(params);
        return users;
    }


    /**
     * 用户拥有店铺下的员工
     * （属于自己拥有的店铺，且角色级别比自己低）
     */
    public PageInfo<User> selectBySearchPart(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_store, String area_code, String role_code, String user_back) throws Exception {
        String[] stores = null;
        System.out.println("=========属于自己拥有的店铺，且角色级别比自己低=============>");
        if (!store_code.equals("")) {
            stores = store_code.split(",");
        } else {
            store_code = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz" + Common.SPECIAL_HEAD + Common.SPECIAL_HEAD;
            stores = store_code.split(",");
        }
        String a = "";
        if (!area_code.equals("")) {
            String[] storeCodes = null;
            if (!area_store.equals("")) {
                area_store = area_store.replace(Common.SPECIAL_HEAD, "");
                storeCodes = area_store.split(",");
            }
            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
            String[] areas = area_code.split(",");
            List<Store> store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, Common.IS_ACTIVE_Y);

            for (int i = 0; i < store.size(); i++) {
                a = a + Common.SPECIAL_HEAD + store.get(i).getStore_code() + ",";
            }

            if (a.equals("")) {
                a = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz" + Common.SPECIAL_HEAD + Common.SPECIAL_HEAD;
                stores = a.split(",");
            } else {
                stores = a.split(",");

            }
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
       // System.out.println("=========转换前a=========>"+a);
        String[] split = a.split(",");
        if(!a.equals("") && split.length>1000){
            a=a.replace(",","|");
            if (a.startsWith("|") || a.startsWith(",") || a.startsWith("，")) {
                a = a.substring(1);
            }
            if (a.endsWith("|") || a.endsWith(",") || a.endsWith("，")) {
                a = a.substring(0, a.length() - 1);
            }
            a="'"+a+"'";
            params.put("store_regexp",a);
            params.put("array",null);
        }else{
            params.put("store_regexp","");
        }
        //System.out.println("=========转换后a=========>"+a);
        params.put("search_value", search_value);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        params.put("user_back", user_back);
        PageHelper.startPage(page_number, page_size);
        List<User> users = userMapper.selectPartUser(params);
        conversion(users);

        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    /**
     * 根据店铺拉取员工
     */
    @Override
    public PageInfo<User> selUserByStoreCode(int page_number, int page_size, String corp_code, String search_value, String store_code, String[] area, String role_code) throws Exception {
        String[] stores = null;
        if (!store_code.equals("")) {
            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
            stores = store_code.split(",");
        }
        if (stores != null) {
            for (int i = 0; i < stores.length; i++) {
                stores[i] = Common.SPECIAL_HEAD + stores[i] + ",";
            }
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
        params.put("search_value", search_value);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        params.put("areas", area);
        PageHelper.startPage(page_number, page_size);
        List<User> users = userMapper.selUserByStoreCode(params);
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    /**
     * 根据店铺拉取员工
     */
    @Override
    public PageInfo<User> selUserByStoreCode(int page_number, int page_size, String corp_code, String search_value, String store_code, String[] area, String role_code,String manager_corp) throws Exception {
        String[] stores = null;
        if (!store_code.equals("")) {
            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
            stores = store_code.split(",");
        }
        if (stores != null) {
            for (int i = 0; i < stores.length; i++) {
                stores[i] = Common.SPECIAL_HEAD + stores[i] + ",";
            }
        }
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
        params.put("search_value", search_value);
        params.put("manager_corp_arr", manager_corp_arr);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        params.put("areas", area);
        PageHelper.startPage(page_number, page_size);
        List<User> users = userMapper.selUserByStoreCode(params);
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    /**
     * 根据店铺拉取员工
     */
    @Override
    public List<User> selUserByStoreCode(String corp_code, String search_value, String store_code, String[] area, String role_code,String can_login) throws Exception {
        String[] stores = null;
        if (!store_code.equals("")) {
            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
            stores = store_code.split(",");
        }
        if (stores != null) {
            for (int i = 0; i < stores.length; i++) {
                stores[i] = Common.SPECIAL_HEAD + stores[i] + ",";
            }
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
        params.put("search_value", search_value);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        params.put("areas", area);
        if(StringUtils.isNotBlank(can_login)){
            params.put("can_login",can_login);
        }
        List<User> users = userMapper.selUserByStoreCode(params);
        return users;
    }

    public User getUserById(String id) throws Exception {
        User user = userMapper.selectUserById(id);
        String corp_code = user.getCorp_code();
        String role_code = user.getRole_code();
        String area_code1 = user.getArea_code();
        String store_code1 = user.getStore_code();
        String brand_code = user.getBrand_code();
        ProcessCodeToSpecial(user);
        user.setStore_name("");
        user.setStore_code("");
        user.setArea_code("");
        user.setArea_name("");
        user.setBrand_code("");
        user.setBrand_name("");
        if (role_code.equals(Common.ROLE_CM)) {
            String manager_corp_name = "";
            String manager_corp = user.getManager_corp();
            if(StringUtils.isNotBlank(manager_corp)) {
                String[] split = manager_corp.split(",");
                for (int i = 0; i < split.length; i++) {
                    Corp corp = corpService.selectByCorpId(0, split[i], Common.IS_ACTIVE_Y);
                    if (i == split.length - 1) {
                        manager_corp_name += corp.getCorp_name();
                    } else {
                        manager_corp_name += corp.getCorp_name() + ",";
                    }
                }
            }
            user.setManager_corp_name(manager_corp_name);
        }
        if (role_code.equals(Common.ROLE_AM)) {
            String area_name = "";
            String store_name = "";

            if (area_code1 != null && !area_code1.equals("")) {
                area_code1 = area_code1.replace(Common.SPECIAL_HEAD, "");
                String[] areaCodes = area_code1.split(",");
                String areaCode = "";
                for (int i = 0; i < areaCodes.length; i++) {
                    Area area = areaMapper.selectAreaByCode(corp_code, areaCodes[i], Common.IS_ACTIVE_Y);
                    if (area != null) {
                        String area_name1 = area.getArea_name();
                        area_name = area_name + area_name1;
                        areaCode = areaCode + areaCodes[i];
                        if (i != areaCodes.length - 1) {
                            area_name = area_name + ",";
                            areaCode = areaCode + ",";
                        }
                    }
                }
                user.setArea_code(areaCode);
                user.setArea_name(area_name);
            }
            if (store_code1 != null && !store_code1.equals("")) {
                store_code1 = store_code1.replace(Common.SPECIAL_HEAD, "");
                String[] ids = store_code1.split(",");
                String store_code = "";
                for (int i = 0; i < ids.length; i++) {
                    Store store = storeService.getStoreByCode(corp_code, ids[i], Common.IS_ACTIVE_Y);
                    if (store != null) {
                        String store_name1 = store.getStore_name();
                        store_name = store_name + store_name1;
                        store_code = store_code + ids[i];
                        if (i != ids.length - 1) {
                            store_name = store_name + ",";
                            store_code = store_code + ",";
                        }
                    }
                }
                user.setStore_name(store_name);
                user.setStore_code(store_code);
            }
        }
        if (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_STAFF)) {
            String store_name = "";
            //   System.out.println("====store_code1store_code1store_code1========"+store_code1);
            if (store_code1 != null && !store_code1.equals("")) {
                store_code1 = store_code1.replace(Common.SPECIAL_HEAD, "");
                String[] ids = store_code1.split(",");
                String store_code = "";
                for (int i = 0; i < ids.length; i++) {
                    Store store = storeService.getStoreByCode(corp_code, ids[i], Common.IS_ACTIVE_Y);
                    if (store != null) {
                        String store_name1 = store.getStore_name();
                        store_name = store_name + store_name1;
                        store_code = store_code + ids[i];
                        if (i != ids.length - 1) {
                            store_name = store_name + ",";
                            store_code = store_code + ",";
                        }
                    }
                }
                user.setStore_name(store_name);
                user.setStore_code(store_code);
            }
        }
        if (role_code.equals(Common.ROLE_BM)) {
            String area_name = "";
            if (area_code1 != null && !area_code1.equals("")) {
                area_code1 = area_code1.replace(Common.SPECIAL_HEAD, "");
                String[] areaCodes = area_code1.split(",");
                String areaCode = "";
                for (int i = 0; i < areaCodes.length; i++) {
                    Area area = areaMapper.selectAreaByCode(corp_code, areaCodes[i], Common.IS_ACTIVE_Y);
                    if (area != null) {
                        String area_name1 = area.getArea_name();
                        area_name = area_name + area_name1;
                        areaCode = areaCode + areaCodes[i];
                        if (i != areaCodes.length - 1) {
                            area_name = area_name + ",";
                            areaCode = areaCode + ",";
                        }
                    }
                }
                user.setArea_code(areaCode);
                user.setArea_name(area_name);
            }
            String brand_name = "";
            if (brand_code != null && !brand_code.equals("")) {
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                String[] brandCodes = brand_code.split(",");
                String brandCode = "";
                for (int i = 0; i < brandCodes.length; i++) {
                    Brand brand = brandService.getBrandByCode(corp_code, brandCodes[i], Common.IS_ACTIVE_Y);
                    if (brand != null) {
                        String brand_name1 = brand.getBrand_name();
                        brand_name = brand_name + brand_name1;
                        brandCode = brandCode + brandCodes[i];
                        if (i != brandCodes.length - 1) {
                            brand_name = brand_name + ",";
                            brandCode = brandCode + ",";
                        }
                    }
                }
                user.setBrand_code(brandCode);
                user.setBrand_name(brand_name);
            }
        }
        List<UserQrcode> qrcodeList = userMapper.selectByUserCode(corp_code, user.getUser_code());
        user.setQrcodeList(qrcodeList);
        String user_back = user.getUser_back();
        if (null == user_back || "".equals(user_back)) {
            JSONObject object_user = new JSONObject();
            object_user.put("sms", "Y");
            object_user.put("wx", "Y");
            object_user.put("call", "Y");
            user.setUser_back(object_user.toJSONString());
        }
        return user;
    }

    public User selectUserById(String id) throws Exception {
        User user = userMapper.selectUserById(id);
        List<UserQrcode> qrcodeList = userMapper.selectByUserCode(user.getCorp_code(), user.getUser_code());
        user.setQrcodeList(qrcodeList);
        return user;
    }

    public User getById(String id) throws Exception {
        return userMapper.selectById(id);
    }

    /**
     * 群组管理
     * 查看用户名单
     */
    public PageInfo<User> selectGroupUser(int page_number, int page_size, String corp_code, String group_code, String search_value) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<User> users = userMapper.selectGroupUser(corp_code, group_code, search_value);
        conversion(users);
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    /**
     * 群组管理
     * 查看用户名单
     */
    public List<User> selectGroupUser(String corp_code, String group_code) throws Exception {
        List<User> users = userMapper.selectGroupUser(corp_code, group_code, "");
        return users;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String insert(User user) throws Exception {
        String result = "";
        String phone = user.getPhone().trim();
        String user_code = user.getUser_code().trim();
        String corp_code = user.getCorp_code().trim();
        String email = user.getEmail().trim();
        List<User> phone_exist = new ArrayList<User>();
        if (!phone.equals("")) {
            phone_exist = userPhoneExist(phone);
        }
        List<User> code_exist = userCodeExist(user_code, corp_code, Common.IS_ACTIVE_Y);
        List<User> email_exist = userEmailExist(email);
        if (user_code.equals("")) {
            result = "员工编号不能为空";
        } else if (phone_exist.size() > 0) {
            result = "手机号已存在";
        } else if (code_exist.size() > 0) {
            result = "员工编号已存在";
        } else if (!email.equals("") && email_exist.size() > 0) {
            result = "邮箱已存在";
        } else {
            userMapper.insertUser(user);
            result = Common.DATABEAN_CODE_SUCCESS;
        }

        return result;
    }

    @Transactional
    public String update(User user) throws Exception {
        String result = "";
        String user_id = user.getId();
        User old_user = getUserById(user_id);
        String[] store_code1 = old_user.getStore_code().split(",");
        List<User> phone_exist = new ArrayList<User>();
        if (!user.getPhone().trim().equals("")) {
            phone_exist = userPhoneExist(user.getPhone().trim());
        }
        List<User> email_exist = userEmailExist(user.getEmail().trim());
        if (old_user.getCorp_code().trim().equalsIgnoreCase(user.getCorp_code().trim())) {
            List<User> code_exist = userCodeExist(user.getUser_code().trim(), user.getCorp_code().trim(), Common.IS_ACTIVE_Y);

            if (phone_exist.size() > 0 && !user_id.equals(phone_exist.get(0).getId())) {
                result = "手机号已存在";
            } else if (code_exist.size() > 0 && !user_id.equals(code_exist.get(0).getId())) {
                result = "员工编号已存在";
            } else if (!user.getEmail().trim().equals("") && email_exist.size() > 0 && !user_id.equals(email_exist.get(0).getId())) {
                result = "邮箱已存在";
            } else {
                if (old_user.getUser_code().trim() != null && !old_user.getUser_code().trim().equalsIgnoreCase(user.getUser_code().trim())) {
                    updateCauseCodeChange(user.getCorp_code().trim(), user.getUser_code().trim(), old_user.getUser_code().trim());
                }
                //若用户修改所属店铺，则删除该店铺员工的业绩目标
                for (int i = 0; i < store_code1.length; i++) {
                    if (!user.getStore_code().trim().contains(store_code1[i])) {
                        userAchvGoalMapper.deleteStoreUserAchv(user.getCorp_code().trim(), store_code1[i].trim(), user.getUser_code().trim());
                    }
                }
                userMapper.updateByUserId(user);
                result = Common.DATABEAN_CODE_SUCCESS;
            }
            changeVipRelation(user.getCorp_code(), user.getUser_code(), old_user.getUser_code());
        } else {
            List<User> code_exist = userCodeExist(user.getUser_code().trim(), user.getCorp_code().trim(), Common.IS_ACTIVE_Y);
            if (phone_exist.size() > 0) {
                result = "手机号已存在";
            } else if (code_exist.size() > 0) {
                result = "员工编号已存在";
            }
//            else if (email_exist == null && email_exist.size() > 0) {
//                result = "邮箱已存在";
//            }
            else {
                //若用户修改所属店铺，则删除该店铺员工的业绩目标
                for (int i = 0; i < store_code1.length; i++) {
                    if (!user.getStore_code().trim().contains(store_code1[i])) {
                        userAchvGoalMapper.deleteStoreUserAchv(user.getCorp_code().trim(), store_code1[i].trim(), user.getUser_code().trim());
                    }
                }
                userMapper.updateByUserId(user);
                result = Common.DATABEAN_CODE_SUCCESS;
            }
        }
        return result;
    }

    @Transactional
    public void updateUser(User user) throws Exception {
        userMapper.updateByUserId(user);
    }

    @Transactional
    public int delete(int id, String user_code, String corp_code) throws Exception {
        privilegeMapper.delete(corp_code + "U" + user_code);
        return userMapper.deleteByUserId(id);
    }

    /**
     * 登录查询
     */
    @Transactional
    public JSONObject login(HttpServletRequest request, String phone, String password) throws Exception {
        System.out.println("---------login--------");
        password = CheckUtils.encryptMD5Hash(password);
        logger.info("------------end search" + new Date());
        JSONObject user_info = new JSONObject();

        List<User> users = userMapper.selectByLogin(phone);
        if (users.size() > 1 || users.size() < 1 || users.get(0).getCan_login().equals("N")) {
            user_info.put("error", "account error");
            user_info.put("status", Common.DATABEAN_CODE_ERROR);
        } else {
            String user_password = users.get(0).getPassword();
            if (!password.equals(CheckUtils.encryptMD5Hash(user_password))) {
                user_info.put("error", "password error");
                user_info.put("status", Common.DATABEAN_CODE_ERROR);
            } else {
                User login_user = users.get(0);
                String user_id = login_user.getId();
                String user_code = login_user.getUser_code().trim();
                String user_name = login_user.getUser_name();
                String corp_code = login_user.getCorp_code().trim();
                String group_code = login_user.getGroup_code().trim();
                String store_code = login_user.getStore_code().trim();
                String area_code = login_user.getArea_code().trim();

                String role_code = groupMapper.selectByCode(corp_code, group_code, "").getRole_code().trim();

                request.getSession().setAttribute("user_id", user_id);
                request.getSession().setAttribute("user_name", user_name);
                request.getSession().setAttribute("user_code", user_code);
                request.getSession().setAttribute("phone", phone);
                request.getSession().setAttribute("corp_code", corp_code);
                request.getSession().setAttribute("role_code", role_code);
                request.getSession().setAttribute("group_code", group_code);
                request.getSession().setAttribute("area_code", "");
                request.getSession().setAttribute("store_code", "");
                request.getSession().setAttribute("brand_code", "");
                request.getSession().setAttribute("logout", "Y");

                String user_type;
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    user_type = "admin";
                } else if (role_code.equals(Common.ROLE_GM)) {
                    //总经理
                    user_type = "gm";
                } else if (role_code.equals(Common.ROLE_CM)) {
                    user_type = "cm";
                    if (null != login_user.getManager_corp() && !"".equals(login_user.getManager_corp())) {
                        request.getSession().setAttribute("manager_corp", login_user.getManager_corp());
                        request.getSession().setAttribute("corp_code", login_user.getManager_corp().split(",")[0]);
                    } else {
                        request.getSession().setAttribute("manager_corp", "");
                    }
                } else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    user_type = "bm";
                    if (login_user.getBrand_code() != null) {
                        String brand_code = login_user.getBrand_code().trim();
                        request.getSession().setAttribute("brand_code", brand_code);
                        request.getSession().setAttribute("area_code", area_code);
                    }
                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    user_type = "am";
                    request.getSession().setAttribute("area_code", area_code);
                    request.getSession().setAttribute("store_code", store_code);
                } else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    user_type = "sm";
                    request.getSession().setAttribute("store_code", store_code);
                } else {
                    //导购
                    user_type = "staff";
                    request.getSession().setAttribute("store_code", store_code);
                }
                request.getSession().setAttribute("user_type", user_type);
                user_info.put("user_type", user_type);
                user_info.put("user_id", user_id);
                user_info.put("role_code", role_code);
                user_info.put("status", Common.DATABEAN_CODE_SUCCESS);

                Date now = new Date();
                login_user.setLogin_time_recently(Common.DATETIME_FORMAT.format(now));
                userMapper.updateByUserId(login_user);
            }
        }
        return user_info;
    }


    /**
     * 免登录
     */
    @Transactional
    public JSONObject selectLoginByUserCode(HttpServletRequest request, String corp_code, String phone, String password) throws Exception {
        System.out.println("---------login--------");
        password = CheckUtils.encryptMD5Hash(password);
        logger.info("------------end search" + new Date());
        JSONObject user_info = new JSONObject();

        List<User> users = userMapper.selectLoginByUserCode(phone, corp_code);
        if (users.size() > 1 || users.size() < 1 || users.get(0).getCan_login().equals("N")) {
            user_info.put("error", "account error");
            user_info.put("status", Common.DATABEAN_CODE_ERROR);
        } else {
            String user_password = users.get(0).getPassword();
            if (!password.equals(CheckUtils.encryptMD5Hash(user_password))) {
                user_info.put("error", "password error");
                user_info.put("status", Common.DATABEAN_CODE_ERROR);
            } else {
                User login_user = users.get(0);
                String user_id = login_user.getId();
                String user_code = login_user.getUser_code().trim();
                String user_name = login_user.getUser_name();
                corp_code = login_user.getCorp_code().trim();
                String group_code = login_user.getGroup_code().trim();
                String store_code = login_user.getStore_code().trim();
                String area_code = login_user.getArea_code().trim();

                String role_code = groupMapper.selectByCode(corp_code, group_code, "").getRole_code().trim();

                request.getSession().setAttribute("user_id", user_id);
                request.getSession().setAttribute("user_name", user_name);
                request.getSession().setAttribute("user_code", user_code);
                request.getSession().setAttribute("phone", phone);
                request.getSession().setAttribute("corp_code", corp_code);
                request.getSession().setAttribute("role_code", role_code);
                request.getSession().setAttribute("group_code", group_code);
                request.getSession().setAttribute("area_code", "");
                request.getSession().setAttribute("store_code", "");
                request.getSession().setAttribute("brand_code", "");
                request.getSession().setAttribute("logout", "N");

                String user_type;
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    user_type = "admin";
                } else if (role_code.equals(Common.ROLE_GM)) {
                    //总经理
                    user_type = "gm";
                } else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    user_type = "bm";
                    if (login_user.getBrand_code() != null) {
                        String brand_code = login_user.getBrand_code().trim();
                        request.getSession().setAttribute("brand_code", brand_code);
                        request.getSession().setAttribute("area_code", area_code);
                    }
                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    user_type = "am";
                    request.getSession().setAttribute("area_code", area_code);
                    request.getSession().setAttribute("store_code", store_code);
                } else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    user_type = "sm";
                    request.getSession().setAttribute("store_code", store_code);
                } else {
                    //导购
                    user_type = "staff";
                    request.getSession().setAttribute("store_code", store_code);
                }
                request.getSession().setAttribute("user_type", user_type);
                user_info.put("user_type", user_type);
                user_info.put("user_id", user_id);
                user_info.put("role_code", role_code);
                user_info.put("status", Common.DATABEAN_CODE_SUCCESS);

                Date now = new Date();
                login_user.setLogin_time_recently(Common.DATETIME_FORMAT.format(now));
                userMapper.updateByUserId(login_user);
            }
        }
        return user_info;
    }

    /**
     * 验证手机号是否已注册
     */
    @Override
    public List<User> userPhoneExist(String phone) throws Exception {
        List<User> user = this.userMapper.selectByPhone(phone);
        //  List<User> user = new ArrayList<User>();
        return user;
    }

    /**
     * 验证手机号是否已注册
     */
    @Override
    public List<User> userPhoneExist2(String phone) throws Exception {
        List<User> user = this.userMapper.selectByPhone(phone);
        //  List<User> user = new ArrayList<User>();
        return user;
    }

    /**
     * 验证邮箱是否已注册
     */
    @Override
    public List<User> userEmailExist(String email) throws Exception {
        List<User> user = null;
        if (!email.equals("")) {
            user = this.userMapper.userEmailExist(email);
        }
        return user;
    }

    /**
     * 验证企业下用户id是否已存在
     */
    @Override
    public List<User> userIdExist(String user_id, String corp_code) throws Exception {
        List<User> user = userMapper.selUserByUserId(user_id, corp_code, Common.IS_ACTIVE_Y);
        return user;
    }

    /**
     * 验证企业下用户编号是否已存在
     */
    public List<User> userCodeExist(String user_code, String corp_code, String isactive) throws Exception {
        List<User> user = userMapper.selectUserCode(user_code, corp_code, isactive);
        return user;
    }

    @Override
    public List<User> selUserByUserId(String user_id, String corp_code, String isactive) throws Exception {
        return userMapper.selUserByUserId(user_id, corp_code, isactive);
    }

    /**
     * 注册
     */
    @Transactional
    public String register(String message) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        try {
            JSONObject jsonObject = JSONObject.parseObject(message);
            String phone = jsonObject.get("PHONENUMBER").toString();
            String auth_code = jsonObject.get("PHONECODE").toString();
            String user_name = jsonObject.get("USERNAME").toString();
            String password = jsonObject.get("PASSWORD").toString();
            String corp_name = jsonObject.get("COMPANY").toString();
            String address = jsonObject.get("ADDRESS").toString();
            String corp_code = jsonObject.get("CORPCODE").toString();

            List<User> u = this.userMapper.selectByPhone(phone);
            if (u.size() == 0) {

                ValidateCode code = validateCodeService.selectPhoneExist("web", phone, Common.IS_ACTIVE_Y);
                Date now = new Date();
                String modified_date = code.getModified_date();
                Date time = Common.DATETIME_FORMAT.parse(modified_date);
                long timediff = (now.getTime() - time.getTime()) / 1000;
                if (auth_code.equals(code.getValidate_code()) && timediff < 3600) {
                    System.out.println("---------auth_code----------");

                    //拼接corp_code
//                    String max_corp_code = corpMapper.selectMaxCorpCode();
//                    int code_tail = Integer.parseInt(max_corp_code.substring(1, max_corp_code.length())) + 1;
//                    Integer c = code_tail;
//                    int length = 5 - c.toString().length();
//                    String corp_code = "C";
//                    for (int i = 0; i < length; i++) {
//                        corp_code = corp_code + "0";
//                    }
//                    corp_code = corp_code + code_tail;
//                    logger.info("----------corp_code" + corp_code);

                    //插入用户信息
                    User user = new User();
                    user.setUser_code("SY0001");
                    user.setUser_name(user_name);
                    user.setPhone(phone);
                    user.setAvatar("../img/a3.jpg");
                    user.setLogin_time_recently("");
                    user.setSex("M");
                    user.setPassword(password);
                    user.setGroup_code("G0001");
                    user.setCorp_code(corp_code);
                    user.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    user.setCreater("root");
                    user.setModified_date(Common.DATETIME_FORMAT.format(now));
                    user.setModifier("root");
                    user.setIsactive(Common.IS_ACTIVE_Y);
                    user.setCan_login(Common.IS_ACTIVE_Y);
                    userMapper.insertUser(user);

                    //插入公司信息
                    Corp corp = new Corp();
                    corp.setCorp_code(corp_code);
                    corp.setCorp_name(corp_name);
                    corp.setAddress(address);
                    corp.setContact(user_name);
                    corp.setContact_phone(phone);
                    corp.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    corp.setCreater("root");
                    corp.setModified_date(Common.DATETIME_FORMAT.format(now));
                    corp.setModifier("root");
                    corp.setIsactive(Common.IS_ACTIVE_Y);
                    logger.info("----------register corp" + corp.toString());
                    corpMapper.insertCorp(corp);

                    //插入群组
                    Group group = new Group();
                    group.setGroup_code("G0001");
                    group.setCorp_code(corp_code);
                    group.setRole_code(Common.ROLE_GM);
                    group.setGroup_name("企业管理员");
                    group.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    group.setCreater("root");
                    group.setModified_date(Common.DATETIME_FORMAT.format(now));
                    group.setModifier("root");
                    group.setIsactive(Common.IS_ACTIVE_Y);
                    groupMapper.insertGroup(group);

                    result = Common.DATABEAN_CODE_SUCCESS;
                } else {
                    result = "验证码错误";
                }
            } else {
                result = "该手机号码已注册";
            }
        } catch (Exception ex) {
            result = ex.getMessage();
        }
        return result;
    }

    /**
     * 获取验证码
     */
    @Transactional
    public String getAuthCode(String phone, String corp_code) throws Exception {
        String text = "您的验证码为：";
        Random r = new Random();
        Double d = r.nextDouble();
        String authcode = d.toString().substring(3, 3 + 6);
        text = text + authcode + ",1小时内有效";

        if (corp_code.equals("")) {
            Data data_phone = new Data("phone", phone, ValueType.PARAM);
            Data data_text = new Data("text", text, ValueType.PARAM);
            Map datalist = new HashMap<String, Data>();
            datalist.put(data_phone.key, data_phone);
            datalist.put(data_text.key, data_text);

            iceInterfaceService.iceInterface("SendSMS", datalist);
        } else {
            iceInterfaceService.sendSmsV2(corp_code, text, phone,"","忘记密码");
        }
        return authcode;
    }

    /**
     * 保存验证码
     */
    @Transactional
    public String saveAuthCode(String phone, String authcode, String platform) throws Exception {
        //验证码存表
        ValidateCode code = validateCodeService.selectPhoneExist(platform, phone, "");
        Date now = new Date();
        if (code == null) {
            code = new ValidateCode();
            code.setValidate_code(authcode);
            code.setPhone(phone);
            code.setPlatform(platform);
            code.setCreated_date(Common.DATETIME_FORMAT.format(now));
            code.setModified_date(Common.DATETIME_FORMAT.format(now));
            code.setCreater("root");
            code.setModifier("root");
            code.setIsactive(Common.IS_ACTIVE_Y);
            validateCodeService.insertValidateCode(code);
        } else {
            code.setValidate_code(authcode);
            code.setModified_date(Common.DATETIME_FORMAT.format(now));
            code.setModifier("root");
            code.setPlatform(platform);
            code.setIsactive(Common.IS_ACTIVE_Y);
            validateCodeService.updateValidateCode(code);
        }
        return CheckUtils.encryptMD5Hash(authcode);
    }

    /**
     * 若导入数据
     * 将store_code封装成固定格式
     */
    public void ProcessCodeToSpecial(User user) throws Exception {
        String role_code = user.getRole_code();
        if (role_code.equals(Common.ROLE_AM)) {
            String code = user.getArea_code();
            if (code != null && !code.equals("") && !code.startsWith(Common.SPECIAL_HEAD)) {
                String[] ids = code.split(",");
                String area_code = "";
                for (int i = 0; i < ids.length; i++) {
                    area_code = area_code + Common.SPECIAL_HEAD + ids[i] + ",";
                }
                user.setArea_code(area_code);
            }
        }
        if (role_code.equals(Common.ROLE_BM)) {
            String code = user.getBrand_code();
            if (code != null && !code.equals("") && !code.startsWith(Common.SPECIAL_HEAD)) {
                String[] ids = code.split(",");
                String brand_code = "";
                for (int i = 0; i < ids.length; i++) {
                    brand_code = brand_code + Common.SPECIAL_HEAD + ids[i] + ",";
                }
                user.setBrand_code(brand_code);
            }
        }
        if (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_STAFF)) {
            String code = user.getStore_code();
            if (code != null && !code.equals("") && !code.startsWith(Common.SPECIAL_HEAD)) {
                String[] ids = code.split(",");
                String store_code = "";
                for (int i = 0; i < ids.length; i++) {
                    store_code = store_code + Common.SPECIAL_HEAD + ids[i] + ",";
                }
                user.setStore_code(store_code);
            }
        }
        userMapper.updateByUserId(user);
    }

    @Override
    public List<UserAchvGoal> selectUserAchvCount(String corp_code, String user_code) throws Exception {
        return this.userAchvGoalMapper.selectUserAchvCount(corp_code, user_code);
        //return this.selectUserAchvCount(corp_code, user_code);
    }

    public int selectCount(String created_date) throws Exception {
        return userMapper.selectCount(created_date);
    }

    @Override
    public PageInfo<User> getScreenPart(int page_number, int page_size, String corp_code, Map<String, String> map, String store_code, String area_store, String area_code, String role_code) throws Exception {
        String[] stores = null;
        map.remove("brand_name");
        String a = "";
        if (!store_code.equals("")) {
            stores = store_code.split(",");
        } else {
            store_code = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz" + Common.SPECIAL_HEAD + Common.SPECIAL_HEAD;
            stores = store_code.split(",");
        }
        if (!area_code.equals("")) {
            String[] storeCodes = null;
            if (!area_store.equals("")) {
                area_store = area_store.replace(Common.SPECIAL_HEAD, "");
                storeCodes = area_store.split(",");
            }
            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
            String[] areas = area_code.split(",");
            List<Store> store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, Common.IS_ACTIVE_Y);

            for (int i = 0; i < store.size(); i++) {
                a = a + Common.SPECIAL_HEAD + store.get(i).getStore_code() + ",";
            }
            if (a.equals("")) {
                a = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz" + Common.SPECIAL_HEAD + Common.SPECIAL_HEAD;
                stores = a.split(",");
            } else {
                stores = a.split(",");

            }
        }
        Map<String, Object> params = new HashMap<String, Object>();
        String user_back = map.get("user_back");
        params.putAll(conver_user_back(user_back));
        map.remove("user_back");

        params.put("array", stores);
        // System.out.println("=========转换前a=========>"+a);
        String[] split = a.split(",");
        if(!a.equals("") && split.length>1000){
            a=a.replace(",","|");
            if (a.startsWith("|") || a.startsWith(",") || a.startsWith("，")) {
                a = a.substring(1);
            }
            if (a.endsWith("|") || a.endsWith(",") || a.endsWith("，")) {
                a = a.substring(0, a.length() - 1);
            }
            a="'"+a+"'";
            params.put("store_regexp",a);
            params.put("array",null);
        }else{
            params.put("store_regexp","");
        }
        //System.out.println("=========转换后a=========>"+a);
        params.put("map", map);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        PageHelper.startPage(page_number, page_size);
        List<User> users = userMapper.selectPartScreen(params);
        conversion(users);

        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    @Override
    public PageInfo<User> getScreenPart2(int page_number, int page_size, String corp_code, Map<String, String> map, String store_code, String area_store, String area_code, String role_code, List<Store> storeList) throws Exception {
        String[] stores = null;
        //----------------------------------------
        map.remove("brand_name");
        String store_code2 = "";
        for (Store store : storeList) {
            store_code2 += Common.SPECIAL_HEAD + store.getStore_code() + ",";
        }
        String[] split2 = null;
        if (!store_code2.equals("")) {
            split2 = store_code2.split(",");
        } else {
            store_code2 = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz" + Common.SPECIAL_HEAD + Common.SPECIAL_HEAD;
            split2 = store_code2.split(",");
        }
        for (int i = 0; i < split2.length; i++) {
            split2[i] = split2[i] + ",";
        }
        //---------------------------------------
        if (!store_code.equals("")) {
            stores = store_code.split(",");
        } else {
            store_code = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz" + Common.SPECIAL_HEAD + Common.SPECIAL_HEAD;
            stores = store_code.split(",");
        }
        String a = "";
        if (!area_code.equals("")) {
            String[] storeCodes = null;
            if (!area_store.equals("")) {
                area_store = area_store.replace(Common.SPECIAL_HEAD, "");
                storeCodes = area_store.split(",");
            }
            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
            String[] areas = area_code.split(",");
            List<Store> store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, Common.IS_ACTIVE_Y);

            for (int i = 0; i < store.size(); i++) {
                a = a + Common.SPECIAL_HEAD + store.get(i).getStore_code() + ",";
            }
            if (a.equals("")) {
                a = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz" + Common.SPECIAL_HEAD + Common.SPECIAL_HEAD;
                stores = a.split(",");
            } else {
                stores = a.split(",");

            }
        }
        Map<String, Object> params = new HashMap<String, Object>();
        String user_back = map.get("user_back");
        params.putAll(conver_user_back(user_back));
        map.remove("user_back");
        params.put("array", stores);
        // System.out.println("=========转换前a=========>"+a);
        String[] split = a.split(",");
        if(!a.equals("") && split.length>1000){
            a=a.replace(",","|");
            if (a.startsWith("|") || a.startsWith(",") || a.startsWith("，")) {
                a = a.substring(1);
            }
            if (a.endsWith("|") || a.endsWith(",") || a.endsWith("，")) {
                a = a.substring(0, a.length() - 1);
            }
            a="'"+a+"'";
            params.put("store_regexp",a);
            params.put("array",null);
        }else{
            params.put("store_regexp","");
        }
        //System.out.println("=========转换后a=========>"+a);
        params.put("map", map);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        params.put("storeList", split);
        PageHelper.startPage(page_number, page_size);
        List<User> users = userMapper.selectPartScreen2(params);
        conversion(users);
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    @Override
    public PageInfo<User> getAllUserScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        System.out.println("-----sys-------");
        List<User> users;
        map.remove("brand_name");
        Map<String, Object> params = new HashMap<String, Object>();
        String user_back = map.get("user_back");
        params.putAll(conver_user_back(user_back));
        map.remove("user_back");
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        users = userMapper.selectAllUserScreen(params);
        conversion(users);
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    @Override
    public PageInfo<User> getAllUserScreen(int page_number, int page_size, String corp_code, Map<String, String> map,String manager_corp) throws Exception {
        System.out.println("-----sys-------");
        List<User> users;
        map.remove("brand_name");
        Map<String, Object> params = new HashMap<String, Object>();
        String user_back = map.get("user_back");
        params.putAll(conver_user_back(user_back));
        map.remove("user_back");
        params.put("corp_code", corp_code);
        params.put("map", map);
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        params.put("manager_corp_arr", manager_corp_arr);
        PageHelper.startPage(page_number, page_size);
        users = userMapper.selectAllUserScreen(params);
        conversion(users);
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }
    @Override
    public PageInfo<User> getAllUserScreen2(int page_number, int page_size, String corp_code, Map<String, String> map, List<Store> storeList) throws Exception {
        List<User> users;
        Map<String, Object> params = new HashMap<String, Object>();
        map.remove("brand_name");

        String user_back = map.get("user_back");
        params.putAll(conver_user_back(user_back));
        map.remove("user_back");
        String store_code = "";
        for (Store store : storeList) {
            store_code += Common.SPECIAL_HEAD + store.getStore_code() + ",";
        }
        String[] split = null;
        if (!store_code.equals("")) {
            split = store_code.split(",");
        } else {
            store_code = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz" + Common.SPECIAL_HEAD + Common.SPECIAL_HEAD;
            split = store_code.split(",");
        }
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i] + ",";
        }

        params.put("corp_code", corp_code);
        params.put("map", map);
        params.put("storeList", split);
        PageHelper.startPage(page_number, page_size);
        users = userMapper.selectAllUserScreen2(params);
        conversion(users);
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    @Override
    public PageInfo<User> getAllUserScreen2(int page_number, int page_size, String corp_code, Map<String, String> map, List<Store> storeList,String manager_corp) throws Exception {
        List<User> users;
        Map<String, Object> params = new HashMap<String, Object>();
        map.remove("brand_name");

        String user_back = map.get("user_back");
        params.putAll(conver_user_back(user_back));
        map.remove("user_back");
        String store_code = "";
        for (Store store : storeList) {
            store_code += Common.SPECIAL_HEAD + store.getStore_code() + ",";
        }
        String[] split = null;
        if (!store_code.equals("")) {
            split = store_code.split(",");
        } else {
            store_code = Common.SPECIAL_HEAD + Common.SPECIAL_HEAD + "zxcvbnmmnbvcxz" + Common.SPECIAL_HEAD + Common.SPECIAL_HEAD;
            split = store_code.split(",");
        }
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i] + ",";
        }
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        params.put("manager_corp_arr", manager_corp_arr);
        params.put("corp_code", corp_code);
        params.put("map", map);
        params.put("storeList", split);
        PageHelper.startPage(page_number, page_size);
        users = userMapper.selectAllUserScreen2(params);
        conversion(users);
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    public Map<String, Object> conver_user_back(String user_back) throws Exception {
        System.out.println("---user_back------" + user_back + "");
        Map<String, Object> params = new HashMap<String, Object>();
        if (null != user_back && !"".equals(user_back)) {
            JSONObject back_obj = JSON.parseObject(user_back);
            String wx = back_obj.get("wx").toString();
            String call = back_obj.get("call").toString();
            String sms = back_obj.get("sms").toString();

            if ("Y".equals(wx) && "N".equals(call) && "N".equals(sms)) {
                params.put("wx", "Y");
                params.put("call", "N");
                params.put("sms", "N");
            } else if ("N".equals(wx) && "Y".equals(call) && "N".equals(sms)) {
                System.out.println("----wx--" + wx + "--call--" + call + "--sms--" + sms);
                params.put("wx", "N");
                params.put("call", "Y");
                params.put("sms", "N");
            } else if ("N".equals(wx) && "N".equals(call) && "Y".equals(sms)) {
                params.put("wx", "N");
                params.put("call", "N");
                params.put("sms", "Y");
            } else if ("Y".equals(wx) && "Y".equals(call) && "N".equals(sms)) {
                params.put("wx", "Y");
                params.put("call", "Y");
                params.put("sms", "N");
            } else if ("N".equals(wx) && "Y".equals(call) && "Y".equals(sms)) {
                params.put("wx", "N");
                params.put("call", "Y");
                params.put("sms", "Y");
            } else if ("Y".equals(wx) && "N".equals(call) && "Y".equals(sms)) {
                params.put("wx", "Y");
                params.put("call", "N");
                params.put("sms", "Y");
            } else if ("Y".equals(wx) && "Y".equals(call) && "Y".equals(sms)) {
                params.put("wx", "Y");
                params.put("call", "Y");
                params.put("sms", "Y");
            } else if ("N".equals(wx) && "N".equals(call) && "N".equals(sms)) {
                params.put("wx", "N");
                params.put("call", "N");
                params.put("sms", "N");
            }
            params.put("exist_user_back", "Y");
        } else {
            params.put("exist_user_back", "N");
        }
        return params;
    }

    /**
     * 列表显示数据转换
     */
    public void conversion(List<User> users) throws Exception {
        for (User user : users) {
            user.setIsactive(CheckUtils.CheckIsactive(user.getIsactive()));
            if (user.getSex() == null || user.getSex().equals("")) {
                user.setSex("未知");
            } else if (user.getSex().equals("F")) {
                user.setSex("女");
            } else {
                user.setSex("男");
            }
            if (user.getIsonline() == null || user.getIsonline().equals("")) {
                user.setIsonline("");
            } else if (user.getIsonline().equals("Y")) {
                user.setIsonline("已签到");
            } else {
                user.setIsonline("已签退");
            }
            String store = user.getStore_code();
            String area = user.getArea_code();
            String role_code = user.getRole_code();
            if (role_code != null && (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_STAFF)) && store != null) {
                String store_code1 = store.replace(Common.SPECIAL_HEAD, "");
                if (store_code1.endsWith(",")) {
                    store = store_code1.substring(0, store_code1.length() - 1);
                }
                user.setStore_code(store);
            } else {
                user.setStore_code("");
            }
            if (role_code != null && role_code.equals(Common.ROLE_AM) && area != null) {
                String area_code1 = area.replace(Common.SPECIAL_HEAD, "");
                String store_code1 = store.replace(Common.SPECIAL_HEAD, "");
                if (area_code1.endsWith(",")) {
                    area = area_code1.substring(0, area_code1.length() - 1);
                }
                user.setArea_code(area);
                if (store_code1.endsWith(",")) {
                    store = store_code1.substring(0, store_code1.length() - 1);
                }
                user.setStore_code(store);
            } else {
                user.setArea_code("");
            }
            if (user.getCan_login() == null || user.getCan_login().equals("")) {
                user.setCan_login("");
            } else if (user.getCan_login().equals("Y")) {
                user.setCan_login("是");
            } else {
                user.setCan_login("否");
            }

            User userById = selectUserById(user.getId());

            List<UserQrcode> qrcodeList = userById.getQrcodeList();
            StringBuilder qrcode = new StringBuilder("");
            for (int j = 0; j < qrcodeList.size(); j++) {
                if (qrcodeList.get(j) != null) {
                    String qrcode1 = qrcodeList.get(j).getQrcode();
                    qrcode.append(qrcode1);
                    if (j != qrcodeList.size() - 1) {
                        qrcode.append("、");
                    }
                }
            }

            user.setQrcode(qrcode.toString());
        }
    }

    /**
     * 更改员工编号时
     * 级联更改关联此编号的回访记录，员工业绩目标，签到，权限列表
     */
    @Transactional
    void updateCauseCodeChange(String corp_code, String new_user_code, String old_user_code) throws Exception {
        //若修改员工编号，对应修改回访记录中关联的用户编号
        codeUpdateMapper.updateVipRecord("", corp_code, "", "", new_user_code, old_user_code);

        //若修改员工编号，对应修改员工业绩目标中关联的用户编号
        codeUpdateMapper.updateUserAchvGoal("", corp_code, "", "", new_user_code, old_user_code);

        //若修改员工编号，对应修改签到中关联的用户编号
        codeUpdateMapper.updateSign("", corp_code, "", "", new_user_code, old_user_code);

        //若修改员工编号，对应修改权限中关联的用户编号
        codeUpdateMapper.updatePrivilege(corp_code + new_user_code, corp_code + old_user_code);

        codeUpdateMapper.updateVipMessage("", corp_code, "", "", new_user_code, old_user_code);

//        codeUpdateMapper.updateStaffMoveLog("", corp_code, new_user_code, old_user_code);

        codeUpdateMapper.updateStaffDetailInfo("", corp_code, new_user_code, old_user_code, "", "");

        //删除对应的二维码
        userMapper.deleteUserQrcode(corp_code, old_user_code);
    }

    public void changeVipRelation(String corp_code, String new_user_code, String old_user_code) throws Exception {
        //更新员工open_id关系
        List<CorpWechat> corpWechats = corpMapper.selectWByCorp(corp_code);
        logger.info("============" + corp_code + "=" + new_user_code + "=" + old_user_code);
        for (int i = 0; i < corpWechats.size(); i++) {
            String app_user_name = corpWechats.get(i).getApp_user_name();
            if (app_user_name != null && !app_user_name.equals("")) {
                List<User> user = userMapper.selectUserCode(new_user_code, corp_code, Common.IS_ACTIVE_Y);
                JSONObject user_obj = new JSONObject();
                if (user.size() > 0) {
                    user_obj = WebUtils.bean2JSONObject(user.get(0));
                }

                MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
                DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);

                Map keyMap = new HashMap();
                keyMap.put("app_user_name", app_user_name);
                keyMap.put("user_code", old_user_code);

                BasicDBObject queryCondition = new BasicDBObject();
                queryCondition.putAll(keyMap);
                DBCursor dbCursor = cursor.find(queryCondition);
                while (dbCursor.hasNext()) {
                    DBObject obj = dbCursor.next();
                    String id = obj.get("_id").toString();

                    DBObject updateCondition = new BasicDBObject();
                    updateCondition.put("_id", id);
                    DBObject updatedValue = new BasicDBObject();
                    updatedValue.put("user_code", new_user_code);
                    updatedValue.put("user", user_obj);

                    DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                    cursor.update(updateCondition, updateSetValue);
                }
            }
        }
    }

    public List<UserQrcode> selectQrcodeByUser(String corp_code, String user_code) throws Exception {
        return userMapper.selectByUserCode(corp_code, user_code);
    }

    public List<UserQrcode> selectQrcodeByUserApp(String corp_code, String user_code, String app_id) throws Exception {
        return userMapper.selectByUserApp(corp_code, user_code, app_id);

    }

    public int insertUserQrcode(UserQrcode userQrcode) throws Exception {
        return userMapper.insertUserQrcode(userQrcode);
    }

    @Transactional
    public int deleteUserQrcode(String corp_code, String user_code) throws Exception {
        userMapper.deleteUserQrcode(corp_code, user_code);

        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);

        BasicDBObject queryCondition = new BasicDBObject();
        queryCondition.put("corp_code", corp_code);
        queryCondition.put("user_code", user_code);
        cursor.remove(queryCondition);
        return 0;
    }

    @Transactional
    public int deleteUserQrcodeOne(String corp_code, String user_code, String app_id) throws Exception {
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);

        BasicDBObject queryCondition = new BasicDBObject();
        queryCondition.put("corp_code", corp_code);
        queryCondition.put("user_code", user_code);
        queryCondition.put("app_id", app_id);
        cursor.remove(queryCondition);
        return userMapper.deleteUserQrcodeOne(corp_code, user_code, app_id);
    }

    public String creatUserQrcode(String corp_code, String user_code, String auth_appid, String user_id) throws Exception {
        List<UserQrcode> userQrcodes = selectQrcodeByUserApp(corp_code, user_code, auth_appid);
        String picture = "";
        Date now = new Date();
        if (userQrcodes.size() < 1) {
            deleteUserQrcodeOne(corp_code, user_code, auth_appid);
            String url = CommonValue.wechat_url + "/creatQrcode?auth_appid=" + auth_appid + "&prd=ishop&src=e&emp_id=" + user_code;
            String result = IshowHttpClient.get(url);
            logger.info("------------666666newcreatQrcode  result" + result);
            if (!result.startsWith("{")) {
                return Common.DATABEAN_CODE_ERROR;
            }
            JSONObject obj = JSONObject.parseObject(result);
            if (result.contains("errcode")) {
                String rst = obj.get("errcode").toString();
                return rst;
            } else {
                picture = obj.get("picture").toString();
                String qrcode_url = obj.get("url").toString();
                UserQrcode userQrcode = new UserQrcode();
                userQrcode.setApp_id(auth_appid);
                userQrcode.setCorp_code(corp_code);
                userQrcode.setUser_code(user_code);
                userQrcode.setQrcode(picture);
                userQrcode.setQrcode_content(qrcode_url);

                userQrcode.setModified_date(Common.DATETIME_FORMAT.format(now));
                userQrcode.setModifier(user_id);
                userQrcode.setCreated_date(Common.DATETIME_FORMAT.format(now));
                userQrcode.setCreater(user_id);
                userQrcode.setIsactive(Common.IS_ACTIVE_Y);
                userMapper.insertUserQrcode(userQrcode);
            }
        } else {
            picture = userQrcodes.get(0).getQrcode();
        }
        User user=new User();
        user.setCorp_code(corp_code);
        user.setUser_code(user_code);
        user.setModified_date(Common.DATETIME_FORMAT.format(now));
        user.setModifier(user_id);
        userMapper.updateUserByCode(user);
        return picture;
    }

    @Transactional
    public void signIn(JSONObject jsonObject, String user_code) throws Exception {
        String user_id = jsonObject.get("id").toString();
        String[] ids = user_id.split(",");
        Date now = new Date();
        for (int i = 0; i < ids.length; i++) {
            logger.info("-------------delete user--" + Integer.valueOf(ids[i]));
            User user = getById(ids[i]);
            String today = Common.DATETIME_FORMAT_DAY.format(now);
            DBCursor signs = signService.selectUserRecord(user.getCorp_code(), user.getUser_code(), today, Common.STATUS_SIGN_IN);

            if (signs.hasNext() == false) {
                if (user.getIsonline() == null || user.getIsonline().equals("") || user.getIsonline().equals("N")) {
                    user.setIsonline("Y");
                    user.setModified_date(Common.DATETIME_FORMAT.format(now));
                    user.setModifier(user_code);
                    updateUser(user);
                    Sign sign = new Sign();
                    sign.setUser_code(user.getUser_code());
                    sign.setUser_name(user.getUser_name());
                    sign.setPhone(user.getPhone());
                    sign.setStatus(Common.STATUS_SIGN_IN);
                    sign.setDistance("");
                    sign.setLocation("");
                    sign.setSign_time(Common.DATETIME_FORMAT.format(now));
                    if ((user.getStore_code() == null && user.getArea_code() == null) || (user.getStore_code().equals("") && user.getArea_code().equals(""))) {
                        sign.setStore_name("");
                        sign.setStore_code("");

                    }
                    if (user.getStore_code() != null && !user.getStore_code().equals("")) {
                        String[] store_code = user.getStore_code().replace(Common.SPECIAL_HEAD, "").split(",");
                        sign.setStore_code(store_code[0]);
                        Store storeByCode = storeService.getStoreByCode(user.getCorp_code(), store_code[0], "");
                        if (storeByCode == null || storeByCode.getStore_name() == null || storeByCode.getStore_name().equals("")) {
                            sign.setStore_name("");
                        } else {
                            sign.setStore_name(storeByCode.getStore_name());
                        }

                    }
                    if (user.getArea_code() != null && !user.getArea_code().equals("")) {
                        String[] area_code = user.getArea_code().replace(Common.SPECIAL_HEAD, "").split(",");
                        List<Store> stores = storeService.selectByAreaBrand(user.getCorp_code(), area_code, null, null, Common.IS_ACTIVE_Y);
                        if (stores.size() > 0) {
                            sign.setStore_code(stores.get(0).getStore_code());
                            Store storeByCode = storeService.getStoreByCode(user.getCorp_code(), stores.get(0).getStore_code(), "");
                            if (storeByCode == null || storeByCode.getStore_name() == null || storeByCode.getStore_name().equals("")) {
                                sign.setStore_name("");
                            } else {
                                sign.setStore_name(storeByCode.getStore_name());
                            }
                        } else {
                            sign.setStore_code("");
                            sign.setStore_name("");
                        }


                    }
                    sign.setCorp_code(user.getCorp_code());
                    Corp corp = baseService.selectByCorpcode(sign.getCorp_code());
                    if (corp != null) {
                        sign.setCorp_name(corp.getCorp_name());
                    } else {
                        sign.setCorp_name("");
                    }
                    sign.setModified_date(Common.DATETIME_FORMAT.format(now));
                    sign.setModifier(user_code);
                    sign.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    sign.setCreater(user_code);
                    sign.setIsactive(Common.IS_ACTIVE_Y);
                    signService.insert(sign);
                }
            }
        }
    }

    @Transactional
    public void signOut(JSONObject jsonObject, String user_code) throws Exception {
        String user_id = jsonObject.get("id").toString();
        String[] ids = user_id.split(",");
        Date now = new Date();
        for (int i = 0; i < ids.length; i++) {
            logger.info("-------------delete user--" + Integer.valueOf(ids[i]));
            User user = getById(ids[i]);
            String today = Common.DATETIME_FORMAT_DAY.format(now);
            DBCursor signs = signService.selectUserRecord(user.getCorp_code(), user.getUser_code(), today, Common.STATUS_SIGN_OUT);
            if (signs.hasNext() == false) {
                if (user.getIsonline() != null && user.getIsonline().equals("Y")) {
                    user.setIsonline("N");
                    user.setModified_date(Common.DATETIME_FORMAT.format(now));
                    user.setModifier(user_code);
                    updateUser(user);
                    Sign sign = new Sign();
                    sign.setUser_code(user.getUser_code());
                    sign.setUser_name(user.getUser_name());
                    sign.setPhone(user.getPhone());
                    sign.setDistance("");
                    sign.setStatus(Common.STATUS_SIGN_OUT);
                    sign.setLocation("");
                    sign.setSign_time(Common.DATETIME_FORMAT.format(now));
                    if ((user.getStore_code() == null && user.getArea_code() == null) || (user.getStore_code().equals("") && user.getArea_code().equals(""))) {
                        sign.setStore_name("");
                        sign.setStore_code("");
                    }
                    if (user.getStore_code() != null && !user.getStore_code().equals("")) {
                        String[] store_code = user.getStore_code().replace(Common.SPECIAL_HEAD, "").split(",");
                        sign.setStore_code(store_code[0]);
                        Store storeByCode = storeService.getStoreByCode(user.getCorp_code(), store_code[0], "");
                        if (storeByCode == null || storeByCode.getStore_name() == null || storeByCode.getStore_name().equals("")) {
                            sign.setStore_name("");
                        } else {
                            sign.setStore_name(storeByCode.getStore_name());
                        }

                    }
                    if (user.getArea_code() != null && !user.getArea_code().equals("")) {
                        String[] area_code = user.getArea_code().replace(Common.SPECIAL_HEAD, "").split(",");
                        List<Store> stores = storeService.selectByAreaBrand(user.getCorp_code(), area_code, null, null, Common.IS_ACTIVE_Y);
                        if (stores.size() > 0) {
                            sign.setStore_code(stores.get(0).getStore_code());
                            Store storeByCode = storeService.getStoreByCode(user.getCorp_code(), stores.get(0).getStore_code(), "");
                            if (storeByCode == null || storeByCode.getStore_name() == null || storeByCode.getStore_name().equals("")) {
                                sign.setStore_name("");
                            } else {
                                sign.setStore_name(storeByCode.getStore_name());
                            }
                        } else {
                            sign.setStore_code("");
                            sign.setStore_name("");
                        }

                    }
                    sign.setCorp_code(user.getCorp_code());
                    Corp corp = baseService.selectByCorpcode(sign.getCorp_code());
                    if (corp != null) {
                        sign.setCorp_name(corp.getCorp_name());
                    } else {
                        sign.setCorp_name("");
                    }
                    sign.setModified_date(Common.DATETIME_FORMAT.format(now));
                    sign.setModifier(user_code);
                    sign.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    sign.setCreater(user_code);
                    sign.setIsactive(Common.IS_ACTIVE_Y);
                    signService.insert(sign);
                }
            }
        }
    }

    public List<String> getBrandCodeByUser(String userId, String corp_code) throws Exception {
        List<String> brand_codes = new ArrayList<String>();
        User user = userMapper.selectUserById(userId);
        String role_code = user.getRole_code();
        List<Store> stores = new ArrayList<Store>();
        if (role_code.equals(Common.ROLE_STAFF) || role_code.equals(Common.ROLE_SM)) {
            String store_code = user.getStore_code();
            stores = storeService.selectByStoreCodes(store_code, corp_code, Common.IS_ACTIVE_Y);
            for (int i = 0; i < stores.size(); i++) {
                String brand_code = stores.get(i).getBrand_code();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                String[] brands = brand_code.split(",");
                for (int j = 0; j < brands.length; j++) {
                    if (!brand_codes.contains(brands[j])) {
                        brand_codes.add(brands[j]);
                    }
                }
            }
        } else if (role_code.equals(Common.ROLE_AM)) {
            String store_code = user.getStore_code();
            String area_code = user.getArea_code();
            area_code = area_code.replace(Common.SPECIAL_HEAD, "");
            String[] areas = area_code.split(",");
            String[] storeCodes = null;
            if (!store_code.equals("")) {
                store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                storeCodes = store_code.split(",");
            }
            stores = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, Common.IS_ACTIVE_Y);
            for (int i = 0; i < stores.size(); i++) {
                String brand_code = stores.get(i).getBrand_code();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                String[] brands = brand_code.split(",");
                for (int j = 0; j < brands.length; j++) {
                    if (!brand_codes.contains(brands[j])) {
                        brand_codes.add(brands[j]);
                    }
                }
            }
        } else if (role_code.equals(Common.ROLE_BM)) {
            String brand_code = user.getBrand_code();
            brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
            String[] codes = brand_code.split(",");
            for (int i = 0; i < codes.length; i++) {
                brand_codes.add(codes[i]);
            }
        } else {
            List<Brand> brands = brandService.getActiveBrand(corp_code, "", null);
            for (int i = 0; i < brands.size(); i++) {
                if (!brand_codes.contains(brands.get(i).getBrand_code())) {
                    brand_codes.add(brands.get(i).getBrand_code());
                }
            }
        }
        return brand_codes;
    }

    @Override
    public List<User> getCanloginByCode(String corp_code) {
        return userMapper.getCanloginByCode(corp_code,null);
    }

    @Override
    public List<User> getCanloginByCode(String corp_code ,String manager_corp) {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        return userMapper.getCanloginByCode(corp_code,manager_corp_arr);
    }

    public int getUserCountByStore(String corp_code, String[] storeList) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("storeList", storeList);
        int userCountByStore = userMapper.getUserCountByStore(params);
        return userCountByStore;
    }

    public User selectUserByCode(String corp_code, String user_code, String isactive) throws Exception {
        User user = userMapper.selectUserByUserCode(corp_code, user_code, isactive);
        return user;
    }

    @Override
    public List<User> selectUserCodeByNameOrCode(String corp_code,String search_value) throws Exception {
        List<User> list=userMapper.selectUserCodeByNameOrCode(corp_code,search_value);
        return  list;
    }

    @Override
    public List<User> selectUserByMasterCode(String corp_code,String[] matserCodes,String[] storeCodes) throws Exception {
       // PageHelper.startPage(page_num,page_size);
        List<User> userList=userMapper.selectUserByMasterCode(corp_code,matserCodes,storeCodes);
       // PageInfo<User> pageInfo=new PageInfo<User>(userList);
        return userList;
    }


}
