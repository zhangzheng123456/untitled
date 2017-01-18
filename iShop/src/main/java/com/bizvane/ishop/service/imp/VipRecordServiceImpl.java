package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.service.VipRecordService;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
@Service
public class VipRecordServiceImpl implements VipRecordService {

    @Autowired
    CorpService corpService;
    @Autowired
    UserService userService;

    public JSONArray transRecord(DBCursor dbCursor) throws Exception{
      //  System.out.println("---进入查询---");
        JSONArray array = new JSONArray();
        while (dbCursor.hasNext()) {
           // System.out.println("---有值---");
            DBObject obj = dbCursor.next();
            JSONObject object = new JSONObject();
            String id = obj.get("_id").toString();
            object.put("id",id);
            String corp_code1 = obj.get("corp_code").toString();
            object.put("corp_code",corp_code1);
            String corp_name = "";
            if (obj.containsField("company_name") && obj.get("company_name") != null) {
                corp_name = obj.get("company_name").toString();
            }else {
                Corp corp = corpService.selectByCorpId(0,corp_code1, Common.IS_ACTIVE_Y);
                if (corp != null) {
                    corp_name = corp.getCorp_name();
                }
            }
            object.put("corp_name",corp_name);
            if (obj.containsField("user_code") && obj.get("user_code") != null){
                String user_code = obj.get("user_code").toString();
                object.put("user_code",user_code);
                String user_name = "";
                if (obj.containsField("user_name") && obj.get("user_name") != null){
                    user_name = obj.get("user_name").toString();
                }else {
                    List<User> users = userService.userCodeExist(user_code,corp_code1,Common.IS_ACTIVE_Y);
                    if (users.size()>0){
                        user_name = users.get(0).getUser_name();
                    }
                }
                object.put("user_name",user_name);
            }
            if (obj.containsField("vip_id") && obj.get("vip_id")!=null){
                String vip_id = obj.get("vip_id").toString();
                object.put("vip_id",vip_id);
            }else{
                object.put("vip_id","");
            }
            if (obj.containsField("vip_name") && obj.get("vip_name") != null){
                String vip_name = obj.get("vip_name").toString();
                object.put("vip_name",vip_name);
            }else {
                object.put("vip_name","");
            }
            if (obj.containsField("message_content") && obj.get("message_content") != null){
                String message_content = obj.get("message_content").toString();
                object.put("message_content",message_content);
            }else {
                object.put("message_content","");
            }
            if (obj.containsField("message_date")){
                String created_date = obj.get("message_date").toString();
                object.put("message_date",created_date);
            }
            if (obj.containsField("action")){
                String action = obj.get("action").toString();
               // object.put("action",action);
                if (action.equals("1")){
                    object.put("action","电话");
                }else if (action.equals("2")){
                    object.put("action","短信");
                }else if (action.equals("3")){
                    object.put("action","微信");
                }
            }
            array.add(object);
        }
        return array;
    }
}
