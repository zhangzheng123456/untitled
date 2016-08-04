package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.*;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.Status;
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
import java.sql.SQLException;
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
    public PageInfo<User> selectBySearch(HttpServletRequest request, int page_number, int page_size, String corp_code, String search_value) throws SQLException {

        List<User> users;
        PageHelper.startPage(page_number, page_size);
        users = userMapper.selectAllUser(corp_code, search_value);
        for (User user : users) {
            if (user.getIsactive().equals("Y")) {
                user.setIsactive("是");
            } else {
                user.setIsactive("否");
            }
            if(user.getSex()==null){
                user.setSex("男");
            }else if(user.getSex().equals("F")){
                user.setSex("女");
            }else{
                user.setSex("男");
            }
        }
        request.getSession().setAttribute("size", users.size());
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    /**
     * 用户拥有店铺下的员工
     * （属于自己拥有的店铺，且角色级别比自己低）
     */
    public PageInfo<User> selectBySearchPart(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String role_code) throws SQLException {
        String[] stores = null;

        if (!store_code.equals("")) {
            stores = store_code.split(",");
            for (int i = 0; i < stores.length; i++) {
                if (!stores[i].startsWith(Common.STORE_HEAD)) {
                    stores[i] = Common.STORE_HEAD + stores[i];
                }
                stores[i] = stores[i].substring(1, stores[i].length());
                System.out.println("--区域：---" + stores[i]);
            }
        }
        if (!area_code.equals("")) {
            String[] areas = area_code.split(",");
            for (int i = 0; i < areas.length; i++) {
                areas[i] = areas[i].substring(1, areas[i].length());
            }
            List<Store> store = storeService.selectByAreaCode(corp_code, areas, "");
            String a = "";
            for (int i = 0; i < store.size(); i++) {
                a = a + store.get(i).getStore_code() + ",";
            }
            stores = a.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
        params.put("search_value", search_value);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        PageHelper.startPage(page_number, page_size);
        List<User>  users = userMapper.selectPartUser(params);
        for (User user : users) {
            if (user.getIsactive().equals("Y")) {
                user.setIsactive("是");
            } else {
                user.setIsactive("否");
            }
            if(user.getSex()==null){
                user.setSex("男");
            }else if(user.getSex().equals("F")){
                user.setSex("女");
            }else{
                user.setSex("男");
            }
        }
        System.out.println("--大小：-----"+users.size());
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    /**
     *根据店铺拉取员工
     */
    @Override
    public PageInfo<User> selUserByStoreCode(int page_number, int page_size, String corp_code, String search_value, String store_code, String role_code) throws SQLException {
        String[] stores = null;
        if (!store_code.equals("")) {
            stores = store_code.split(",");
            for (int i = 0; i < stores.length; i++) {
                if (!stores[i].startsWith(Common.STORE_HEAD)) {
                    stores[i] = Common.STORE_HEAD + stores[i];
                }
                stores[i] = stores[i].substring(1, stores[i].length());
            }
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
        params.put("search_value", search_value);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        PageHelper.startPage(page_number, page_size);
        List<User>  users = userMapper.selUserByStoreCode(params);
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    public User getUserById(int id) throws SQLException {
        User user = userMapper.selectUserById(id);
        String corp_code = user.getCorp_code();

        String role_code = groupMapper.selectByCode(corp_code,user.getGroup_code(),"").getRole_code();
        if (role_code.equals(Common.ROLE_SM) || role_code.equals(Common.ROLE_STAFF)) {
            if (!user.getStore_code().startsWith(Common.STORE_HEAD)) {
                ProcessStoreCode(user);
            }
            String store_name = "";
            String[] ids = user.getStore_code().split(",");
            String store_code = "";
            for (int i = 0; i < ids.length; i++) {
                ids[i] = ids[i].substring(1, ids[i].length());
                Store store = storeService.getStoreByCode(corp_code, ids[i], "");
                String store_name1 = store.getStore_name();
                store_name = store_name + store_name1;
                store_code = store_code + ids[i];
                if (i != ids.length - 1) {
                    store_name = store_name + ",";
                    store_code = store_code + ",";
                }
            }
            user.setStore_name(store_name);
            user.setStore_code(store_code);
        }else {
            user.setStore_name("");
            user.setStore_code("");
        }
        if (role_code.equals(Common.ROLE_AM)) {
            if (!user.getArea_code().startsWith(Common.STORE_HEAD)) {
                ProcessAreaCode(user);
            }
            String area_name = "";
            String[] areaCodes = user.getArea_code().split(",");
            String areaCode = "";
            for (int i = 0; i < areaCodes.length; i++) {
                areaCodes[i] = areaCodes[i].substring(1, areaCodes[i].length());
                Area area = areaMapper.selAreaByCorp(corp_code, areaCodes[i], "");
                String area_name1 = area.getArea_name();
                area_name = area_name + area_name1;
                areaCode = areaCode + areaCodes[i];
                if (i != areaCodes.length - 1) {
                    area_name = area_name + ",";
                    areaCode = areaCode + ",";
                }
            }
            user.setArea_code(areaCode);
            user.setArea_name(area_name);
        }else {
            user.setArea_code("");
            user.setArea_name("");
        }
        return user;
    }

    public User getById(int id) throws SQLException {
        return userMapper.selectById(id);
    }

    /**
     * 群组管理
     * 查看用户名单
     */
    public PageInfo<User> selectGroupUser(int page_number, int page_size, String corp_code, String group_code) throws SQLException {
        PageHelper.startPage(page_number, page_size);
        List<User> users = userMapper.selectGroupUser(corp_code, group_code);
        for (User user : users) {
            if (user.getIsactive().equals("Y")) {
                user.setIsactive("是");
            } else {
                user.setIsactive("否");
            }
            if(user.getSex()==null){
                user.setSex("男");
            }else if(user.getSex().equals("F")){
                user.setSex("女");
            }else{
                user.setSex("男");
            }
        }
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    /**
     * 群组管理
     * 查看用户名单
     */
    public int selectGroupUser(String corp_code, String group_code) throws SQLException {
        List<User> users = userMapper.selectGroupUser(corp_code, group_code);
        int count = users.size();
        return count;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String insert(User user) throws SQLException {
        String result = "";
        String phone = user.getPhone();
        String user_code = user.getUser_code();
        String corp_code = user.getCorp_code();
        String email = user.getEmail();
        String phone_exist = userPhoneExist(phone);
        User code_exist = userCodeExist(user_code, corp_code);
        String email_exist = userEmailExist(email);
        if (phone.equals("")) {
            result = "手机号不能为空";
        } else if (user_code.equals("")) {
            result = "员工编号不能为空";
        } else if (!phone_exist.equals(Common.DATABEAN_CODE_SUCCESS)) {
            result = "手机号已存在";
        } else if (code_exist != null) {
            result = "员工编号已存在";
        } else if (!email.equals("") && !email_exist.equals(Common.DATABEAN_CODE_SUCCESS)) {
            result = "邮箱已存在";
        } else {
            userMapper.insertUser(user);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        System.out.println(result + "-----");
        return result;
    }

    @Transactional
    public String update(User user) throws SQLException {
        String result = "";
        int user_id = user.getId();
//        String phone = user.getPhone();
//        String user_code = user.getUser_code();
//        String corp_code = user.getCorp_code();
//        String email = user.getEmail();
//        String store_code = user.getStore_code();
        User old_user = getUserById(user_id);
        String[] store_code1 = old_user.getStore_code().split(",");
        String phone_exist = userPhoneExist(user.getPhone());
        //User code_exist = userCodeExist(user.getUser_code(), user.getCorp_code());
        String emails = userEmailExist(user.getEmail());
        if (old_user.getCorp_code().equals(user.getCorp_code())) {
            User code_exist = userCodeExist(user.getUser_code(), user.getCorp_code());
            if (!old_user.getPhone().equals(user.getPhone()) && !phone_exist.equals(Common.DATABEAN_CODE_SUCCESS)) {
                result = "手机号已存在";
            } else if (!old_user.getUser_code().equals(user.getUser_code()) && code_exist != null) {
                result = "员工编号已存在";
            } else if (!user.getEmail().equals("") && old_user.getEmail() != null && (!old_user.getEmail().equals(user.getEmail()) && emails.equals(Common.DATABEAN_CODE_ERROR))) {
                result = "邮箱已存在";
            } else {
                if (!old_user.getUser_code().equals(user.getUser_code())) {
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
            User code_exist = userCodeExist(user.getUser_code(), user.getCorp_code());
            if (!phone_exist.equals(Common.DATABEAN_CODE_SUCCESS)) {
                result = "手机号已存在";
            } else if (code_exist != null) {
                result = "员工编号已存在";
            } else if (!user.getEmail().equals("") && old_user.getEmail() != null && emails.equals(Common.DATABEAN_CODE_ERROR)) {
                result = "邮箱已存在";
            } else {
//                if (!old_user.getUser_code().equals(user_code)) {
//                    updateCauseCodeChange(corp_code, user_code, old_user.getUser_code());
//                }
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
    public void updateUser(User user) throws SQLException {
        userMapper.updateByUserId(user);
    }

    @Transactional
    public int delete(int id, String user_code, String corp_code) throws SQLException {
        privilegeMapper.delete(corp_code + user_code);
        return userMapper.deleteByUserId(id);
    }

    /**
     * 登录查询
     */
    @Transactional
    public JSONObject login(HttpServletRequest request, String phone, String password) throws Exception {
        System.out.println("---------login--------");
        User login_user1 = userMapper.selectLogin(phone, password);
        password = CheckUtils.encryptMD5Hash(password);
        User login_user2 = userMapper.selectLogin(phone, password);
        logger.info("------------end search" + new Date());
        JSONObject user_info = new JSONObject();
        if (login_user2 == null && login_user1 == null) {
            user_info.put("error", "用户名或密码错误");
            user_info.put("status", Common.DATABEAN_CODE_ERROR);
//        } else if (login_user.getIsactive().contains("N")) {
//            user_info.put("error", "当前用户不可用");
//            user_info.put("status", Common.DATABEAN_CODE_ERROR);
        } else {
            User login_user;
            if (login_user1 != null) {
                login_user = login_user1;
            } else {
                login_user = login_user2;
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
    public String userPhoneExist(String phone) {
        User user = this.userMapper.selectByPhone(phone);
        String result = Common.DATABEAN_CODE_ERROR;
        if (user == null) {
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }

    /**
     * 验证邮箱是否已注册
     */
    @Override
    public String userEmailExist(String email) {
        String result = Common.DATABEAN_CODE_SUCCESS;
        if (!email.equals("")) {
            List<User> user = this.userMapper.userEmailExist(email);
            if (user.size() > 0) {
                result = Common.DATABEAN_CODE_ERROR;
            }
        }
        return result;
    }

    /**
     * 验证企业下用户编号是否已存在
     */
    public User userCodeExist(String user_code, String corp_code) throws SQLException {
        User user = userMapper.selectUserCode(user_code, corp_code);
        return user;
    }


    /**
     * 注册
     */
    @Transactional
    public String register(String message) {
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

            User u = this.userMapper.selectByPhone(phone);
            if (u == null) {

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
                    user.setQrcode("");
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
    public String getAuthCode(String phone, String platform) {

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

        DataBox dataBox = iceInterfaceService.iceInterface("com.bizvane.sun.app.method.SendSMS", datalist);
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

    public List<User> selectGroup(String corp_code, String group_code) throws SQLException {
        return userMapper.selectGroupUser(corp_code, group_code);
    }

    /**
     * 若导入数据
     * 将store_code封装成固定格式
     */
    public void ProcessStoreCode(User user) {
        String[] ids = user.getStore_code().split(",");
        String store_code = "";
        for (int i = 0; i < ids.length; i++) {
            store_code = store_code + Common.STORE_HEAD + ids[i] + ",";
        }
        user.setStore_code(store_code);
        userMapper.updateByUserId(user);
    }

    public void ProcessAreaCode(User user) {
        String[] ids = user.getArea_code().split(",");
        String area_code = "";
        for (int i = 0; i < ids.length; i++) {
            area_code = area_code + Common.STORE_HEAD + ids[i] + ",";
        }
        user.setArea_code(area_code);
        userMapper.updateByUserId(user);
    }


    @Override
    public List<UserAchvGoal> selectUserAchvCount(String corp_code, String user_code) {
        return this.userAchvGoalMapper.selectUserAchvCount(corp_code, user_code);
        //return this.selectUserAchvCount(corp_code, user_code);
    }

    public int selectCount(String created_date) {
        return userMapper.selectCount(created_date);
    }

    @Override
    public PageInfo<User> getAllUserScreen(int page_number, int page_size, String corp_code, String area_code, String store_code, String role_code, Map<String, String> map) {
        String area_codes[] = null;
        String store_codes[] = null;
        if (!store_code.equals("")) {
            store_codes = store_code.split(",");
            for (int i = 0; store_codes != null && i < store_codes.length; i++) {
                store_codes[i] = store_codes[i].substring(1, store_codes[i].length());
            }
        }
        if (!area_code.equals("")) {
            area_codes = area_code.split(",");
            for (int i = 0; area_code != null && i < area_code.length(); i++) {
                area_codes[i] = area_codes[i].substring(1, store_codes[i].length());
            }
            List<Store> stores = storeService.selectByAreaCode(corp_code, area_codes, "");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < stores.size(); i++) {
                sb.append(stores.get(i).getStore_code() + ",");
            }
            store_codes = sb.toString().split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", store_codes);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        params.put("map", map);
        List<User> users;
        PageHelper.startPage(page_number, page_size);
        users = userMapper.selectAllUserScreen(params);
        for (User user : users) {
            if (user.getIsactive().equals("Y")) {
                user.setIsactive("是");
            } else {
                user.setIsactive("否");
            }
            if(user.getSex()==null){
                user.setSex("男");
            }else if(user.getSex().equals("F")){
                user.setSex("女");
            }else{
                user.setSex("男");
            }
        }
        PageInfo<User> page = new PageInfo<User>(users);
        return page;
    }

    /**
     * 更改员工编号时
     * 级联更改关联此编号的回访记录，员工业绩目标，签到，权限列表
     */
    @Transactional
    void updateCauseCodeChange(String corp_code, String new_user_code, String old_user_code) {
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

    }
}
