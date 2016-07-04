package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.VIPRelationMapper;
import com.bizvane.ishop.entity.VIPEmpRelation;
import com.bizvane.ishop.entity.VIPStoreRelation;
import com.bizvane.ishop.service.WebService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.sql.SQLException;

/**
 * Created by zhou on 2016/6/1.
 *
 * @@version
 */

@Service
public class WebServiceImpl implements WebService {

//    @Override
//    public Achv getAchvById(int id) throws SQLException {
//        return null;
//    }

    @Autowired
    VIPRelationMapper vipRelationMapper;


    public VIPEmpRelation selectEmpVip(String app_user_name, String open_id) throws SQLException {
        return vipRelationMapper.selectEmpVip(open_id, app_user_name);
    }

    public VIPStoreRelation selectStoreVip(String app_user_name, String open_id) throws SQLException {
        return vipRelationMapper.selectStoreVip(open_id, app_user_name);
    }

    public static String encryptMD5Hash(String s) throws Exception {

        if (s == null) {
            return null;
        }
        MessageDigest digest;
        StringBuffer hasHexString;

        digest = MessageDigest.getInstance("MD5");
        digest.update(s.getBytes(), 0, s.length());
        byte messageDigest[] = digest.digest();
        hasHexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            String hex = Integer.toHexString(0xFF & messageDigest[i]);
            if (hex.length() == 1)
                hasHexString.append('0');
            hasHexString.append(hex);
        }
        return hasHexString.toString();

    }

    public static String toJSon(int i, String message){
        JSONObject jss = new JSONObject();
        jss.put("code", i);
        jss.put("message", message);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jss);
        return jsonArray.toString();
    }
}
