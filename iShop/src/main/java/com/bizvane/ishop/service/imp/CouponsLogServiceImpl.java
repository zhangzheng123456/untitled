package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.controller.VIPRecordController;
import com.bizvane.ishop.dao.CorpMapper;
import com.bizvane.ishop.dao.SendCouponsMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static org.joni.Config.log;

/**
 * Created by nanji on 2017/11/28.
 */
@Service
public class CouponsLogServiceImpl implements CouponsLogService {
    @Autowired
    CorpMapper corpMapper;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    SendCouponsMapper sendCouponsMapper;
    @Autowired
    CorpService corpService;
    @Autowired
    UserService userService;
    @Autowired
    StoreService storeService;
    @Autowired
    BaseService baseService;
    @Autowired
    SendCouponsService sendCouponsService;

    private static final Logger log = Logger.getLogger(CouponsLogServiceImpl.class);

    @Override
    public JSONArray transLog(DBCursor dbCursor) throws Exception {
//爱秀app和CRM单独判断
        JSONArray array = new JSONArray();
        while (dbCursor.hasNext()) {
            // System.out.println("---有值---");
            DBObject obj = dbCursor.next();
            JSONObject object = new JSONObject();
            String id = obj.get("_id").toString();
            object.put("id", id);
            String corp_code = "";
            if (obj.containsField("corp_code")) {
                corp_code = obj.get("corp_code").toString();
                object.put("corp_code", corp_code);
            } else {
                if (obj.containsField("appid")) {
                    String appid=obj.get("appid").toString();
                    CorpWechat wechats= corpMapper.selectWByApp(appid);
                    if(wechats!=null){
                       corp_code= wechats.getCorp_code();
                        object.put("corp_code", corp_code);
                    }

                }
            }
            object.put("description", "");
            object.put("vip_name", "");
            object.put("vip_card_no", "");
            object.put("send_time", "");
            object.put("ticketno", "");
            object.put("user_name", "");
            object.put("ticketno", "");
            object.put("platform", "");
            object.put("coupon_name", "");
     log.info("===============object================="+object.toJSONString());
            //platform
            //发券时间
            if (obj.containsField("send_time")) {
                object.put("send_time", obj.get("send_time").toString());
            } else if (obj.containsField("T_CR")) {
                object.put("send_time", obj.get("T_CR").toString());
            }

            //状态
            if (obj.containsField("is_active")) {
                String action = obj.get("is_active").toString();
                if (action.equals("Y")) {
                    object.put("is_active", "发送成功");
                } else {
                    object.put("is_active", "发送失败");
                }
            } else {
                object.put("is_active", "发送失败");
            }
            if (obj.containsField("params")) {
                JSONObject info = JSON.parseObject(obj.get("params").toString());

                if (info.containsKey("couponName")) {
                    String couponName = info.getString("couponName");
                    object.put("coupon_name", couponName);
                }

                if (info.containsKey("vip_name")) {
                    String vip_name = info.getString("vip_name");
                    object.put("vip_name", vip_name);
                    if (vip_name == null) {
                        object.put("vip_name", "");
                    }
                }

                if (info.containsKey("cardno")) {
                    String vip_card_no = info.getString("cardno");
                    if (null == info.get("cardno")) {
                        object.put("vip_card_no", "");
                    }else{
                        object.put("vip_card_no", vip_card_no);
                    }
                } else {
                    object.put("vip_card_no", "");
                }
            }
            if (obj.containsField("platform") && obj.get("platform").toString().equals("爱秀")) {

                if (obj.containsField("ticketno")) {
                    String ticketno = obj.get("ticketno").toString();
                    object.put("ticketno", ticketno);
                } else {
                    object.put("ticketno", "");
                }
                object.put("platform", "爱秀");
                //send_user发券人
                if (obj.containsField("send_user")) {
                    User user = userService.selectUserByCode(corp_code, obj.get("send_user").toString(), Common.IS_ACTIVE_Y);
                    if (null != user) {
                        object.put("user_name", user.getUser_name());
                    } else {
                        object.put("user_name", "");
                    }

                } else {
                    //  object.put("user_id", "");
                    object.put("user_name", "");
                }
                if (obj.containsField("params")) {
                    JSONObject info = JSON.parseObject(obj.get("params").toString());
                    if (info.containsKey("ticket_code_ishop")) {
                        String ticket_code_ishop = info.getString("ticket_code_ishop");
                        SendCoupons sendCoupons = sendCouponsService.getSendCouponsInfoByCode(corp_code, ticket_code_ishop);
                        if (sendCoupons != null) {
                            String description = sendCoupons.getSend_coupon_title();
                            object.put("description", description);
                        } else {
                            object.put("description", "");
                        }
                    }
                }
            } else {


                object.put("user_name", "系统");

                if (obj.containsField("params")) {
                    JSONObject info = JSON.parseObject(obj.get("params").toString());

                    if (info.containsKey("description")) {
                        String description = info.getString("description");
                        object.put("description", description);
                    }
                    if (info.containsKey("cardno")) {
                        String vip_card_no = info.getString("cardno");
                        if (null == info.get("cardno")) {
                            object.put("vip_card_no", "");
                        }else{
                            object.put("vip_card_no", vip_card_no);
                        }
                    } else {
                        object.put("vip_card_no", "");
                    }
                }
                //merchandise
                if (obj.containsField("merchandise")) {
                    String merchandise = obj.get("merchandise").toString();
                    if (merchandise.startsWith("{")) {
                        JSONObject obj_info = JSON.parseObject(merchandise);
                        if (obj_info.containsKey("code")) {
                            if (obj_info.getString("code").equals("0")) {
                                if(obj_info.containsKey("result")){
                                    String message = obj_info.getString("result");
                                    JSONObject obj_result = JSON.parseObject(message);
                                    if(obj_result.containsKey("ticketno")){
                                        String ticketno = obj_result.getString("ticketno");
                                        object.put("ticketno", ticketno);
                                    }else{
                                        object.put("ticketno", "");
                                    }
                                }else{
                                    object.put("ticketno", "");
                                }

                            } else {
                                object.put("ticketno", "");
                            }
                        }
                    } else {
                        object.put("ticketno", "");
                    }

                }

                object.put("platform", "CRM");

            }

            array.add(object);
        }
        return array;
    }

    @Override
    public BasicDBObject getScreen(JSONArray screen, String corp_code) throws Exception {
        BasicDBObject queryCondition = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        for (int i = 0; i < screen.size(); i++) {
            String info = screen.get(i).toString();
            JSONObject json = JSONObject.parseObject(info);
            String screen_key = json.get("screen_key").toString();
            String screen_value = json.get("screen_value").toString();
            //params

                BasicDBObject db_corp_code = new BasicDBObject("corp_code", corp_code);
                values.add(db_corp_code);

            //会员卡号
            if (screen_key.equals("vip_card_no") && !screen_value.equals("")) {
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);

                BasicDBObject db1 = new BasicDBObject("params.cardno", pattern);
                values.add(db1);
            }
            //会员卡号
            if (screen_key.equals("is_active") && !screen_value.equals("")) {

                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);

                BasicDBObject db2 = new BasicDBObject("is_active", pattern);
                values.add(db2);
            }


            //姓名
            if (screen_key.equals("vip_name") && !screen_value.equals("")) {
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                BasicDBObject db3 = new BasicDBObject("params.vip_name", pattern);
                values.add(db3);
            }


            if (screen_key.equals("coupon_name") && !screen_value.equals("")) {
                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                BasicDBObject db3 = new BasicDBObject("params.couponName", pattern);
                values.add(db3);
            }


            if (screen_key.equals("platform") && !screen_value.equals("")) {

                if (screen_value.equals("CRM")) {
                    // Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                    BasicDBObject db4 = new BasicDBObject("platform", null);
                    values.add(db4);
                } else {
                    Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                    BasicDBObject db4 = new BasicDBObject("platform", pattern);
                    values.add(db4);
                }


            }
            if (screen_key.equals("send_time")) {
                JSONObject date = JSON.parseObject(screen_value);
                String start = date.get("start").toString();
                String end = date.get("end").toString();
                if (!start.equals("") && start != null) {
                    BasicDBObject oo = new BasicDBObject();
                    BasicDBList list = new BasicDBList();
                    list.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.GTE, start + " 00:00:00")));
                    list.add(new BasicDBObject("T_CR", new BasicDBObject(QueryOperators.GTE, start + " 00:00:00")));
                    oo.put("$or", list);
                    values.add(oo);
                }
                if (!end.equals("") && end != null) {
                    BasicDBObject oo = new BasicDBObject();
                    BasicDBList list = new BasicDBList();
                    list.add(new BasicDBObject(screen_key, new BasicDBObject(QueryOperators.LTE, end + " 23:59:59")));
                    list.add(new BasicDBObject("T_CR", new BasicDBObject(QueryOperators.LTE, end + " 23:59:59")));
                    oo.put("$or", list);
                    values.add(oo);
                }
            }
            if (screen_key.equals("user_name") && !screen_value.equals("")) {

                Pattern pattern = Pattern.compile("^.*" + screen_value + ".*$", Pattern.CASE_INSENSITIVE);
                BasicDBObject db5 = new BasicDBObject("send_user", pattern);
                values.add(db5);
            }
        }
        if (values.size() > 0)
            queryCondition.put("$and", values);

        log.info("==========queryCondition" + JSON.toJSONString(queryCondition));
        return queryCondition;
    }






}





