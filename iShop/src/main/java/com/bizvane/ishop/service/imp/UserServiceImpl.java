package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.*;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.lang.System;
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


    private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

    /**
     * @param corp_code
     * @param search_value
     */
    public PageInfo<User> selectBySearch(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value) throws Exception {

        List<User> users;
        PageHelper.startPage(page_number, page_size);
        users = userMapper.selectAllUser(corp_code, search_value);
        conversion(users);
        request.getSession().setAttribute("size", users.size());
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    public List<User> selectBySearch(String corp_code) throws Exception {

        List<User> users;
        users = userMapper.selectAllUser(corp_code, "");
        return users;
    }

    /**
     * 用户拥有店铺下的员工
     * （属于自己拥有的店铺，且角色级别比自己低）
     */
    public PageInfo<User> selectBySearchPart(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String role_code) throws Exception {
        String[] stores = null;

        if (!store_code.equals("")) {
            stores = store_code.split(",");
        }
        if (!area_code.equals("")) {
            area_code = area_code.replace(Common.SPECIAL_HEAD,"");
            String[] areas = area_code.split(",");
            List<Store> store = storeService.selectByAreaCode(corp_code, areas,Common.IS_ACTIVE_Y);
            String a = "";
            for (int i = 0; i < store.size(); i++) {
                a = a + Common.SPECIAL_HEAD +store.get(i).getStore_code() + ",";
            }
            stores = a.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
        params.put("search_value", search_value);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
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
            store_code = store_code.replace(Common.SPECIAL_HEAD,"");
            stores = store_code.split(",");
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

    public User getUserById(int id) throws Exception {
        User user = userMapper.selectUserById(id);
        String corp_code = user.getCorp_code();
        String role_code = user.getRole_code();
        if (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_STAFF)) {
            String code = user.getStore_code();
            if (code != null && !code.equals("") && !code.startsWith(Common.SPECIAL_HEAD)) {
                ProcessStoreCode(user);
            }
            String store_name = "";
            String store_code1 = user.getStore_code();
            if (store_code1 != null && !store_code1.equals("")) {
                store_code1 = store_code1.replace(Common.SPECIAL_HEAD,"");
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
            } else {
                user.setStore_name("");
                user.setStore_code("");
            }
        } else {
            user.setStore_name("");
            user.setStore_code("");
        }
        if (role_code.equals(Common.ROLE_AM)) {
            String area_code = user.getArea_code();
            if (area_code != null && !area_code.equals("") && !area_code.startsWith(Common.SPECIAL_HEAD)) {
                ProcessAreaCode(user);
            }
            String area_name = "";
            String area_code1 = user.getArea_code();
            if (area_code1 != null && !area_code1.equals("")) {
                area_code1 = area_code1.replace(Common.SPECIAL_HEAD,"");
                String[] areaCodes = area_code1.split(",");
                String areaCode = "";
                for (int i = 0; i < areaCodes.length; i++) {
                    Area area = areaMapper.selectAreaByCode(corp_code, areaCodes[i],Common.IS_ACTIVE_Y);
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
            } else {
                user.setArea_code("");
                user.setArea_name("");
            }
        } else {
            user.setArea_code("");
            user.setArea_name("");
        }
        List<UserQrcode> qrcodeList = userMapper.selectByUserCode(corp_code, user.getUser_code());
        user.setQrcodeList(qrcodeList);
        return user;
    }

    public User getById(int id) throws Exception {
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
    public int selectGroupUser(String corp_code, String group_code) throws Exception {
        List<User> users = userMapper.selectGroupUser(corp_code, group_code, "");
        int count = users.size();
        return count;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String insert(User user) throws Exception {
        String result = "";
        String phone = user.getPhone();
        String user_code = user.getUser_code();
        String corp_code = user.getCorp_code();
        String email = user.getEmail();
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
        int user_id = user.getId();
        User old_user = getUserById(user_id);
        String[] store_code1 = old_user.getStore_code().split(",");
        List<User> phone_exist = new ArrayList<User>();
        if (!user.getPhone().equals("")) {
            phone_exist = userPhoneExist(user.getPhone());
        }
        List<User> email_exist = userEmailExist(user.getEmail());
        if (old_user.getCorp_code().equalsIgnoreCase(user.getCorp_code())) {
            List<User> code_exist = userCodeExist(user.getUser_code(), user.getCorp_code(), Common.IS_ACTIVE_Y);
            if (phone_exist.size() > 0 && user_id != phone_exist.get(0).getId()) {
                result = "手机号已存在";
            } else if (code_exist.size() > 0 && user_id != code_exist.get(0).getId()) {
                result = "员工编号已存在";
            } else if (!user.getEmail().equals("") && email_exist.size() > 0 && user_id != email_exist.get(0).getId()) {
                result = "邮箱已存在";
            } else {
                if (old_user.getUser_code() != null && !old_user.getUser_code().equalsIgnoreCase(user.getUser_code())) {
                    updateCauseCodeChange(user.getCorp_code(), user.getUser_code(), old_user.getUser_code());
                }
                //若用户修改所属店铺，则删除该店铺员工的业绩目标
                for (int i = 0; i < store_code1.length; i++) {
                    if (!user.getStore_code().contains(store_code1[i])) {
                        userAchvGoalMapper.deleteStoreUserAchv(user.getCorp_code(), store_code1[i], user.getUser_code());
                    }
                }
                userMapper.updateByUserId(user);
                result = Common.DATABEAN_CODE_SUCCESS;
            }
        } else {
            List<User> code_exist = userCodeExist(user.getUser_code(), user.getCorp_code(), Common.IS_ACTIVE_Y);
            if (phone_exist.size() > 0) {
                result = "手机号已存在";
            } else if (code_exist.size() > 0) {
                result = "员工编号已存在";
            } else if (email_exist.size() > 0) {
                result = "邮箱已存在";
            } else {
                //若用户修改所属店铺，则删除该店铺员工的业绩目标
                for (int i = 0; i < store_code1.length; i++) {
                    if (!user.getStore_code().contains(store_code1[i])) {
                        userAchvGoalMapper.deleteStoreUserAchv(user.getCorp_code(), store_code1[i], user.getUser_code());
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
        List<User> user1 = userMapper.selectLogin(phone, password);
        password = CheckUtils.encryptMD5Hash(password);
        List<User> user2 = userMapper.selectLogin(phone, password);
        logger.info("------------end search" + new Date());
        JSONObject user_info = new JSONObject();
        if (user2.size() == 0 && user1.size() == 0) {
            user_info.put("error", "用户名或密码错误");
            user_info.put("status", Common.DATABEAN_CODE_ERROR);
//        } else if (login_user.getIsactive().contains("N")) {
//            user_info.put("error", "当前用户不可用");
//            user_info.put("status", Common.DATABEAN_CODE_ERROR);
        } else {
            User login_user;
            if (user1.size() != 0) {
                login_user = user1.get(0);
            } else {
                login_user = user2.get(0);
            }
            int user_id = login_user.getId();
            String user_code = login_user.getUser_code();
            String corp_code = login_user.getCorp_code();
            String group_code = login_user.getGroup_code();
            String store_code = login_user.getStore_code();
            String area_code = login_user.getArea_code();

            String role_code = groupMapper.selectByCode(corp_code, group_code, "").getRole_code();

            request.getSession().setAttribute("user_id", user_id);
            request.getSession().setAttribute("user_code", user_code);
            request.getSession().setAttribute("corp_code", corp_code);
            request.getSession().setAttribute("role_code", role_code);
            request.getSession().setAttribute("group_code", group_code);
            request.getSession().setAttribute("area_code", "");
            request.getSession().setAttribute("store_code", "");
            Date now = new Date();
            login_user.setLogin_time_recently(Common.DATETIME_FORMAT.format(now));
            userMapper.updateByUserId(login_user);
            String user_type;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                user_type = "admin";
            } else if (role_code.equals(Common.ROLE_GM)) {
                //总经理
                user_type = "gm";
            } else if (role_code.equals(Common.ROLE_AM)) {
                //区经
                user_type = "am";
                request.getSession().setAttribute("area_code", area_code);
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
        }
        return user_info;
    }

    /**
     * 验证手机号是否已注册
     */
    @Override
    public List<User> userPhoneExist(String phone) throws Exception {
        List<User> user = this.userMapper.selectByPhone(phone);

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
    public List<User> userIdExist(String user_id,String corp_code) throws Exception {
        List<User> user = userMapper.selUserByUserId(user_id,corp_code,Common.IS_ACTIVE_Y);
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
            JSONObject jsonObject = new JSONObject(message);
            String phone = jsonObject.get("PHONENUMBER").toString();
            String auth_code = jsonObject.get("PHONECODE").toString();
            String user_name = jsonObject.get("USERNAME").toString();
            String password = jsonObject.get("PASSWORD").toString();
            String corp_name = jsonObject.get("COMPANY").toString();
            String address = jsonObject.get("ADDRESS").toString();
            String corp_code = jsonObject.get("CORPCODE").toString();

            List<User> u = this.userMapper.selectByPhone(phone);
            if (u.size() == 0) {

                ValidateCode code = validateCodeService.selectValidateCode(0, phone, Common.IS_ACTIVE_Y);
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
    public String getAuthCode(String phone, String platform) throws Exception {

        String text = "[爱秀]您的注册验证码为：";
        Random r = new Random();
        Double d = r.nextDouble();
        String authcode = d.toString().substring(3, 3 + 6);
        text = text + authcode + ",1小时内有效";

        Data data_phone = new Data("phone", phone, ValueType.PARAM);
        Data data_text = new Data("text", text, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_phone.key, data_phone);
        datalist.put(data_text.key, data_text);

        DataBox dataBox = iceInterfaceService.iceInterface("SendSMS", datalist);
        logger.info("SendSMSMethod -->" + dataBox.data.get("message").value);
        String msg = dataBox.data.get("message").value;
        System.out.println("------------" + msg);
        logger.info("------------" + msg);
        JSONObject obj = new JSONObject(msg);
        if (obj.get("message").toString().equals("短信发送成功")) {
            //验证码存表
            ValidateCode code = validateCodeService.selectValidateCode(0, phone, "");
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
            return authcode;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    /**
     * 若导入数据
     * 将store_code封装成固定格式
     */
    public void ProcessStoreCode(User user) throws Exception {
        String[] ids = user.getStore_code().split(",");
        String store_code = "";
        for (int i = 0; i < ids.length; i++) {
            store_code = store_code + Common.SPECIAL_HEAD + ids[i] + ",";
        }
        user.setStore_code(store_code);
        userMapper.updateByUserId(user);
    }

    public void ProcessAreaCode(User user) throws Exception {
        String[] ids = user.getArea_code().split(",");
        String area_code = "";
        for (int i = 0; i < ids.length; i++) {
            area_code = area_code + Common.SPECIAL_HEAD + ids[i] + ",";
        }
        user.setArea_code(area_code);
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
    public PageInfo<User> getScreenPart(int page_number, int page_size, String corp_code, Map<String, String> map, String store_code, String area_code, String role_code) throws Exception {
        String[] stores = null;

        if (!store_code.equals("")) {
            stores = store_code.split(",");
        }
        if (!area_code.equals("")) {
            area_code = area_code.replace(Common.SPECIAL_HEAD,"");
            String[] areas = area_code.split(",");
            List<Store> store = storeService.selectByAreaCode(corp_code, areas,Common.IS_ACTIVE_Y);
            String a = "";
            for (int i = 0; i < store.size(); i++) {
                a = a + Common.SPECIAL_HEAD +store.get(i).getStore_code() + ",";
            }
            stores = a.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
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
    public PageInfo<User> getAllUserScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        List<User> users;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        users = userMapper.selectAllUserScreen(params);
        conversion(users);
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
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
            String store = user.getStore_code();
            String area = user.getArea_code();
            String role_code = user.getRole_code();
            if (role_code != null && (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_STAFF)) && store != null) {
                if (store.contains(Common.SPECIAL_HEAD)) {
                    String store_code1 = store.replace(Common.SPECIAL_HEAD, "");
                    store = store_code1.substring(0, store_code1.length() - 1);
                }
                user.setStore_code(store);
            } else {
                user.setStore_code("");
            }
            if (role_code != null && role_code.equals(Common.ROLE_AM) && area != null) {
                if (area.contains(Common.SPECIAL_HEAD)) {
                    String area_code1 = area.replace(Common.SPECIAL_HEAD, "");
                    area = area_code1.substring(0, area_code1.length() - 1);
                }
                user.setArea_code(area);
            } else {
                user.setArea_code("");
            }
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

        codeUpdateMapper.updateStaffMoveLog("", corp_code, new_user_code, old_user_code);

        codeUpdateMapper.updateStaffDetailInfo("", corp_code, new_user_code, old_user_code, "", "");

        //更新员工open_id关系
        List<CorpWechat> corpWechats = corpMapper.selectWByCorp(corp_code);
        for (int i = 0; i < corpWechats.size(); i++) {
            String app_user_name = corpWechats.get(i).getApp_user_name();
            if (app_user_name != null && !app_user_name.equals(""))
                codeUpdateMapper.updateRelVipEmp(new_user_code, old_user_code, app_user_name);
        }

        //删除对应的二维码
        userMapper.deleteUserQrcode(corp_code, old_user_code);
    }

    public List<UserQrcode> selectQrcodeByUser(String corp_code, String user_code) throws Exception {
        return userMapper.selectByUserCode(corp_code, user_code);
    }

    public UserQrcode selectQrcodeByUserApp(String corp_code, String user_code, String app_id) throws Exception {
        return userMapper.selectByUserApp(corp_code, user_code, app_id);

    }

    public int insertUserQrcode(UserQrcode userQrcode) throws Exception {
        return userMapper.insertUserQrcode(userQrcode);
    }

    public int deleteUserQrcode(String corp_code, String user_code) throws Exception {
        return userMapper.deleteUserQrcode(corp_code, user_code);
    }

    public int deleteUserQrcodeOne(String corp_code, String user_code,String app_id) throws Exception {
        return userMapper.deleteUserQrcodeOne(corp_code, user_code,app_id);
    }

    public String creatUserQrcode(String corp_code, String user_code, String auth_appid, String user_id) throws Exception {
        UserQrcode userQrcode = selectQrcodeByUserApp(corp_code, user_code, auth_appid);
        String picture = "";
        if (userQrcode == null) {
            String url = "http://wechat.app.bizvane.com/app/wechat/creatQrcode?auth_appid=" + auth_appid + "&prd=ishop&src=e&emp_id=" + user_code;
            String result = IshowHttpClient.get(url);
            logger.info("------------creatQrcode  result" + result);
            if (!result.startsWith("{")) {
                return Common.DATABEAN_CODE_ERROR;
            }
            JSONObject obj = new JSONObject(result);
            if (result.contains("errcode")) {
                String rst = obj.get("errcode").toString();
                return rst;
            } else {
                picture = obj.get("picture").toString();
                String qrcode_url = obj.get("url").toString();
                userQrcode = new UserQrcode();
                userQrcode.setApp_id(auth_appid);
                userQrcode.setCorp_code(corp_code);
                userQrcode.setUser_code(user_code);
                userQrcode.setQrcode(picture);
                userQrcode.setQrcode_content(qrcode_url);
                Date now = new Date();
                userQrcode.setModified_date(Common.DATETIME_FORMAT.format(now));
                userQrcode.setModifier(user_id);
                userQrcode.setCreated_date(Common.DATETIME_FORMAT.format(now));
                userQrcode.setCreater(user_id);
                userQrcode.setIsactive(Common.IS_ACTIVE_Y);
                userMapper.insertUserQrcode(userQrcode);
            }
        } else {
            picture = userQrcode.getQrcode();
        }
        return picture;
    }

    @Transactional
    public void signIn(JSONObject jsonObject, String user_code) throws Exception{
        String user_id = jsonObject.get("id").toString();
        String[] ids = user_id.split(",");
        Date now = new Date();
        for (int i = 0; i < ids.length; i++) {
            logger.info("-------------delete user--" + Integer.valueOf(ids[i]));
            User user = getById(Integer.parseInt(ids[i]));
            String today = Common.DATETIME_FORMAT_DAY.format(now);
            List<Sign> signs = signService.selectUserRecord(user.getCorp_code(),user.getUser_code(),today,Common.STATUS_SIGN_IN);
            if (signs.size() == 0) {
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
                    sign.setSign_time(Common.DATETIME_FORMAT.format(now));
                    if (user.getStore_code() != null && !user.getStore_code().equals("")) {
                        String[] store_code = user.getStore_code().replace(Common.SPECIAL_HEAD, "").split(",");
                        sign.setStore_code(store_code[0]);
                    }
                    if (user.getArea_code() != null && !user.getArea_code().equals("")) {
                        String[] area_code = user.getArea_code().replace(Common.SPECIAL_HEAD, "").split(",");
                        List<Store> stores = storeService.selectByAreaCode(user.getCorp_code(), area_code, Common.IS_ACTIVE_Y);
                        if (stores.size() > 0)
                            sign.setStore_code(stores.get(0).getStore_code());
                    }
                    sign.setCorp_code(user.getCorp_code());
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
    public void signOut(JSONObject jsonObject, String user_code) throws Exception{
        String user_id = jsonObject.get("id").toString();
        String[] ids = user_id.split(",");
        Date now = new Date();
        for (int i = 0; i < ids.length; i++) {
            logger.info("-------------delete user--" + Integer.valueOf(ids[i]));
            User user = getById(Integer.parseInt(ids[i]));
            String today = Common.DATETIME_FORMAT_DAY.format(now);
            List<Sign> signs = signService.selectUserRecord(user.getCorp_code(), user.getUser_code(), today, Common.STATUS_SIGN_OUT);
            if (signs.size() == 0) {
                if (user.getIsonline() != null && user.getIsonline().equals("Y")) {
                    user.setIsonline("N");
                    user.setModified_date(Common.DATETIME_FORMAT.format(now));
                    user.setModifier(user_code);
                    updateUser(user);
                    Sign sign = new Sign();
                    sign.setUser_code(user.getUser_code());
                    sign.setUser_name(user.getUser_name());
                    sign.setPhone(user.getPhone());
                    sign.setStatus(Common.STATUS_SIGN_OUT);
                    sign.setSign_time(Common.DATETIME_FORMAT.format(now));
                    if (user.getStore_code() != null && !user.getStore_code().equals("")) {
                        String[] store_code = user.getStore_code().replace(Common.SPECIAL_HEAD, "").split(",");
                        sign.setStore_code(store_code[0]);
                    }
                    if (user.getArea_code() != null && !user.getArea_code().equals("")) {
                        String[] area_code = user.getArea_code().replace(Common.SPECIAL_HEAD, "").split(",");
                        List<Store> stores = storeService.selectByAreaCode(user.getCorp_code(), area_code, Common.IS_ACTIVE_Y);
                        if (stores.size() > 0)
                            sign.setStore_code(stores.get(0).getStore_code());
                    }
                    sign.setCorp_code(user.getCorp_code());
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
}
