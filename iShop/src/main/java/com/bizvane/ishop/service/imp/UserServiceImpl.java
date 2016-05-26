package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.UserInfo;
import com.bizvane.ishop.dao.UserInfoMapper;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by maoweidong on 2016/2/15.
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    FunctionService functionService;

    public UserInfo getUserById(int id) throws SQLException {
        return userInfoMapper.selectByUserId(id);
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
            if (login_user.getRole_code().contains("R10")) {
                //系统管理员
                user_info.put("user_type", "admin");
            } else if (login_user.getRole_code().contains("R50")) {
                //总经理
                user_info.put("user_type", "gm");
            } else if (login_user.getRole_code().contains("R20")) {
                //区经
                user_info.put("user_type", "am");
            } else if (login_user.getRole_code().contains("R30")) {
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
     * 验证手机号是否已存在
     */
    public UserInfo phoneExist(String phone) throws SQLException {
        return userInfoMapper.selectByPhone(phone);
    }

    /**
     * @param corp_code
     * @param search_value
     */
    public List<UserInfo> selectBySearch(String corp_code, String search_value) throws SQLException {
        return userInfoMapper.selectAllUser(corp_code, "%" + search_value + "%");
    }

}
