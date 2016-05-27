package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.controller.LoginController;
import com.bizvane.ishop.entity.CorpInfo;
import com.bizvane.ishop.entity.UserInfo;
import com.bizvane.ishop.dao.UserInfoMapper;
import com.bizvane.ishop.entity.ValidateCode;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.service.ValidateCodeService;
import com.bizvane.sun.app.client.Client;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.Status;
import com.bizvane.sun.v1.common.ValueType;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by maoweidong on 2016/2/15.
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    FunctionService functionService;
    @Autowired
    CorpService corpService;
    @Autowired
    ValidateCodeService validateCodeService;

    private static final Logger log = Logger.getLogger(UserServiceImpl.class);
    String[] arg = new String[]{"--Ice.Config=client.config"};
    Client client = new Client(arg);

    public UserInfo getUserById(int id) throws SQLException {
        return userInfoMapper.selectByUserId(id,"");
    }
    /**
     * 验证手机号是否已存在
     */
    public UserInfo phoneExist(String phone) throws SQLException {
        return userInfoMapper.selectByUserId(0,phone);
    }

    /**
     * 验证企业下用户编号是否已存在
     */
    public String userCodeExist(String user_code,String corp_coded) throws SQLException {
        UserInfo user = userInfoMapper.selectUserCode(user_code,corp_coded);
        String result = Common.DATABEAN_CODE_SUCCESS;
        if (user == null){
            result = Common.DATABEAN_CODE_ERROR;
        }
        return result;
    }

    public int insert(UserInfo userInfo) throws SQLException {
        return userInfoMapper.insertUser(userInfo);
    }

    public int update(UserInfo userInfo) throws SQLException {
        return userInfoMapper.updateByUserId(userInfo);
    }

    public int delete(int id) throws SQLException {
        return userInfoMapper.deleteByUserId(id);
    }

    /**
     * 登录查询
     */
    public JSONObject login(HttpServletRequest request, String phone, String password) throws SQLException {
        System.out.println("---------login--------");
        UserInfo login_user = userInfoMapper.selectLogin(phone, password);
        log.info("------------end search"+new Date());
        JSONObject user_info = new JSONObject();
        if (login_user == null) {
            return null;
        } else {
            int user_id = login_user.getId();
            String corp_code = login_user.getCorp_code();
            String role_code = login_user.getRole_code();

            JSONArray menu = functionService.selectAllFunctions(user_id, role_code);
            request.getSession().setAttribute("user_id", user_id);
            request.getSession().setAttribute("corp_code", corp_code);
            request.getSession().setAttribute("role_code", role_code);
            request.getSession().setAttribute("menu", menu);
            System.out.println(request.getSession().getAttribute("user_id"));
            Date now = new Date();
            login_user.setLogin_time_recently(now);
            update(login_user);

            user_info.put("user_id", user_id);
            user_info.put("menu", menu);
            if (login_user.getRole_code().contains(Common.ROLE_SYS_HEAD)) {
                //系统管理员
                user_info.put("user_type", "admin");
            } else if (login_user.getRole_code().contains(Common.ROLE_GM_HEAD)) {
                //总经理
                user_info.put("user_type", "gm");
            } else if (login_user.getRole_code().contains(Common.ROLE_AM_HEAD)) {
                //区经
                user_info.put("user_type", "am");
            } else if (login_user.getRole_code().contains(Common.ROLE_SM_HEAD)) {
                //店长
                user_info.put("user_type", "sm");
            } else {
                //导购
                user_info.put("user_type", "staff");
            }
        }
        return user_info;
    }

    /**
     * 注册
     */
    public String register(String message) throws SQLException{
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        String phone = jsonObject.get("PHONENUMBER").toString();
        String auth_code = jsonObject.get("PHONECODE").toString();
        String user_name = jsonObject.get("USERNAME").toString();
        String password = jsonObject.get("PASSWORD").toString();
        String corp_name = jsonObject.get("COMPANY").toString();
        String address = jsonObject.get("ADDRESS").toString();

        ValidateCode code = validateCodeService.selectValidateCode(0, phone);
        Date now = new Date();
        Date modified_date = code.getModified_date();
        Date time = modified_date;
        long timediff = (now.getTime() - time.getTime()) / 1000;
        if (auth_code.equals(code.getValidate_code()) && timediff < 3600) {
            System.out.println("---------auth_code----------");
            //插入用户信息
            UserInfo user = new UserInfo();
            user.setUser_name(user_name);
            user.setPhone(phone);
            user.setPassword(password);
            user.setRole_code("R500000");
            user.setCreated_date(now);
            user.setModified_date(now);
            insert(user);
            //拼接corp_code
            String max_corp_code = corpService.selectMaxCorpCode();
            int code_tail = Integer.parseInt(max_corp_code.substring(1, max_corp_code.length())) + 1;
            Integer c = code_tail;
            int length = 5 - c.toString().length();
            String corp_code = "C";
            for (int i = 0; i < length; i++) {
                corp_code = corp_code + "0";
            }
            corp_code = corp_code + code;
            //插入公司信息
            CorpInfo corp = new CorpInfo();
            corp.setCorp_code(corp_code);
            corp.setCorp_name(corp_name);
            corp.setAddress(address);
            corp.setContact(user_name);
            corp.setContact_phone(phone);
            corp.setCreated_date(now);
            corp.setModified_date(now);
            corpService.insertCorp(corp);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }
    /**
     * @param corp_code
     * @param search_value
     */
    public List<UserInfo> selectBySearch(String corp_code, String search_value) throws SQLException {
        return userInfoMapper.selectAllUser(corp_code, "%"+search_value+"%");
    }

    /**
     * 获取验证码
     */
    public String getAuthCode(String phone,String platform){

        String text = "[爱秀]您的注册验证码为：";
        Random r = new Random();
        Double d = r.nextDouble();
        String authcode=d.toString().substring(3,3+6);
        text = text+authcode+",1小时内有效";

        Data data_phone = new Data("phone", phone, ValueType.PARAM);
        Data data_text = new Data("text", text, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_phone.key,data_phone);
        datalist.put(data_text.key,data_text);
        DataBox dataBox1 = new DataBox("1", Status.ONGOING, "", "com.bizvane.sun.app.method.SendSMS", datalist, null, null, System.currentTimeMillis());
        System.out.println(dataBox1.data);

        DataBox dataBox = client.put(dataBox1);
        log.info("SendSMSMethod -->" + dataBox.data.get("message").value);
        System.out.println("CaptchaMethod -->" + dataBox.data.get("message").value);
        String msg = dataBox.data.get("message").value;
        System.out.println("------------"+msg);
        log.info("------------"+msg);
        JSONObject obj = new JSONObject(msg);
        if(obj.get("message").toString().equals("短信发送成功")) {
            //验证码存表
            ValidateCode code = validateCodeService.selectValidateCode(0, phone);
            Date now = new Date();
            if (code == null) {
                code = new ValidateCode();
                code.setValidate_code(authcode);
                code.setPhone(phone);
                code.setPlatform(platform);
                code.setCreated_date(now);
                code.setModified_date(now);
                code.setCreater("root");
                code.setModifier("root");
                code.setIsactive(Common.IS_ACTIVE_Y);
                validateCodeService.insertValidateCode(code);
            } else {
                code.setValidate_code(authcode);
                code.setModified_date(now);
                code.setModifier("root");
                code.setPlatform(platform);
                code.setIsactive(Common.IS_ACTIVE_Y);
                validateCodeService.updateValidateCode(code);
            }
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }
}
