package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.dao.QrCodeMapper;
import com.bizvane.ishop.entity.QrCode;
import com.bizvane.ishop.service.QrCodeService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by yanyadong on 2017/3/14.
 */
@Service
public class QrCodeServicImpl implements QrCodeService {
    @Autowired
    QrCodeMapper qrCodeMapper;

    @Override
    public QrCode selectQrCodeById(int id) throws Exception {
        return qrCodeMapper.selectById(id);
    }

    @Override
    public PageInfo<QrCode> selectAll(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<QrCode> qrCodeList;
        PageHelper.startPage(page_number,page_size);
        qrCodeList=qrCodeMapper.selectAll(corp_code,search_value);
        PageInfo<QrCode> pageInfo=new PageInfo<QrCode>(qrCodeList);
        return pageInfo;
    }

    @Override
    public int delQrcodeById(int id) throws Exception {
        return qrCodeMapper.deleteById(id);
    }

    @Override
    public int insertQrcode(QrCode qrCode) throws Exception {
        return qrCodeMapper.insertQrcode(qrCode);
    }

    @Override
    public int updateQrcode(QrCode qrCode) throws Exception {
        return qrCodeMapper.updateQrcode(qrCode);
    }

    @Override
    public PageInfo<QrCode> selectAllScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws  Exception{
        List<QrCode> qrCodeList;
        PageHelper.startPage(page_number,page_size);
        HashMap<String,Object> param=new HashMap<String, Object>();

        Set<String> sets=map.keySet();
        if(sets.contains("created_date")) {
            JSONObject date = JSONObject.parseObject(map.get("created_date"));
            param.put("created_date_start", date.get("start").toString());
            String end = date.get("end").toString();
//             if (!end.equals(""))
//                 end = end + " 23:59:59";
            param.put("created_date_end", end);
            map.remove("created_date");
        }
        param.put("corp_code",corp_code);
        param.put("map",map);
        qrCodeList=qrCodeMapper.selectAllScreen(param);
        PageInfo<QrCode> pageInfo=new PageInfo<QrCode>(qrCodeList);
        return pageInfo;
    }

    @Override
    public QrCode selectQrCodeByQrcodeName(String corp_code, String qrcode_name) throws Exception {
        QrCode qrCode=null;
        qrCode=qrCodeMapper.selectByQrcodeName(corp_code,qrcode_name);
        return qrCode;
    }

    public void trans(List<QrCode> qrCodeList)throws Exception{
        for (QrCode qrcode : qrCodeList) {
            qrcode.setIsactive(CheckUtils.CheckIsactive(qrcode.getIsactive()));
            if (qrcode.getQrcode_type() == null){
                if (qrcode.getQrcode_type().equals("print"))
                    qrcode.setQrcode_type("印刷类");
                if (qrcode.getQrcode_type().equals("material"))
                    qrcode.setQrcode_type("材料类");
                if (qrcode.getQrcode_type().equals("gift"))
                    qrcode.setQrcode_type("礼品类");
            }
        }
    }

    @Override
    public List<QrCode> selectQrcode(String app_id,String[] types) throws  Exception{
        List<QrCode> qrCodeList;
        qrCodeList=qrCodeMapper.selectAllByType(app_id,types);
        return qrCodeList;
    }

    @Override
    public JSONObject getQrcodeScan(DBCollection cursor,String app_id, String qrcode_id,String date) throws Exception {
        //所有关注数
        BasicDBObject ref = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        String[] qrcode_ids=qrcode_id.split(",");
        BasicDBList basDbList=new BasicDBList();
        for (int i = 0; i <qrcode_ids.length ; i++) {
            basDbList.add(qrcode_ids[i]);
        }

        values.add(new BasicDBObject("app_id", app_id));
        values.add(new BasicDBObject("qrcode_id",new BasicDBObject("$in", basDbList)));

        Pattern pattern = Pattern.compile("^.*" + date + ".*$", Pattern.CASE_INSENSITIVE);
        values.add(new BasicDBObject("create_date", pattern));
        ref.put("$and", values);
        DBCursor dbCursor1 = cursor.find(ref);
        //新增会员数
        int vipCount = 0;
        while (dbCursor1.hasNext()){
            DBObject dbObj=dbCursor1.next();
            if(dbObj.get("vip")!=null){
                if(dbObj.get("transVip")!=null){
                    if(dbObj.get("transVip").equals("Y")) {
                        ++vipCount;
                    }
                }
            }
        }
        JSONObject json_data = new JSONObject();
        json_data.put("date", date);
        json_data.put("all", dbCursor1.count());
        json_data.put("newVip", vipCount);

        return json_data;
    }

    @Override
    public JSONObject getEmpQrcodeScan(DBCollection cursor,String app_id, String user_code, String date) throws Exception {
        //所有关注数
        BasicDBObject ref = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        String[] users=user_code.split(",");
        BasicDBList basDbList=new BasicDBList();
        for (int i = 0; i <users.length ; i++) {
            basDbList.add(users[i]);
        }

        values.add(new BasicDBObject("app_id", app_id));
        values.add(new BasicDBObject("user_code",new BasicDBObject("$in", basDbList)));

        Pattern pattern = Pattern.compile("^.*" + date + ".*$", Pattern.CASE_INSENSITIVE);
        values.add(new BasicDBObject("modified_date", pattern));
        ref.put("$and", values);
        DBCursor dbCursor1 = cursor.find(ref);

        //关注会员数
        int vipCount = 0;
        while (dbCursor1.hasNext()){
            DBObject dbObj=dbCursor1.next();
            if(dbObj.get("vip")!=null){
                if(dbObj.get("transVip")!=null){
                    if(dbObj.get("transVip").equals("Y")) {
                        ++vipCount;
                    }
                }
            }
        }
        JSONObject json_data = new JSONObject();
        json_data.put("date", date);
        json_data.put("all", dbCursor1.count());
        json_data.put("newVip", vipCount);

        return json_data;
    }

    //店铺二维码
    @Override
    public JSONObject getStoreQrcodeScan(DBCollection cursor,String app_id, String store_code,String date) throws Exception {
        //所有关注数
        BasicDBObject ref = new BasicDBObject();
        BasicDBList values = new BasicDBList();
        BasicDBList basDbList=new BasicDBList();
        String[] stores=store_code.split(",");
        for (int i = 0; i <stores.length ; i++) {
            basDbList.add(stores[i]);
        }

        values.add(new BasicDBObject("app_id", app_id));
        values.add(new BasicDBObject("store_code",new BasicDBObject("$in", basDbList)));

        Pattern pattern = Pattern.compile("^.*" + date + ".*$", Pattern.CASE_INSENSITIVE);
        values.add(new BasicDBObject("store_modified_date", pattern));
        ref.put("$and", values);
        DBCursor dbCursor1 = cursor.find(ref);
        //新增会员数
        int vipCount = 0;
        while (dbCursor1.hasNext()){
            DBObject dbObj=dbCursor1.next();
            if(dbObj.get("vip")!=null){
                if(dbObj.get("transVip")!=null){
                    if(dbObj.get("transVip").equals("Y")) {
                        ++vipCount;
                    }
                }
            }
        }
        JSONObject json_data = new JSONObject();
        json_data.put("date", date);
        json_data.put("all", dbCursor1.count());
        json_data.put("newVip", vipCount);

        return json_data;
    }


    //转换MongDB渠道二维码
    // type 0 渠道  1导购 2店铺
    public  ArrayList dbCursorToList(DBCursor dbCursor,int type) throws Exception{
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String t_cr = "";
            if(obj.get("transVip")!=null){
                if(obj.get("transVip").equals("Y")){
                    if(obj.get("vip")!=null){
                        String vip=obj.get("vip").toString();
                        JSONObject jsonObject= JSON.parseObject(vip);
                        t_cr = jsonObject.getString("join_date");

                        //转换
                        if(jsonObject.containsKey("NAME_VIP")){
                            jsonObject.put("vip_name",jsonObject.getString("NAME_VIP"));
                        }
                        if(jsonObject.containsKey("MOBILE_VIP")){
                            jsonObject.put("vip_phone",jsonObject.getString("MOBILE_VIP"));
                        }
                        if(jsonObject.containsKey("CARD_NO_VIP")){
                            jsonObject.put("cardno",jsonObject.getString("CARD_NO_VIP"));
                        }
                        if(jsonObject.containsKey("CARD_TYPE_ID")){
                            jsonObject.put("vip_card_type",jsonObject.getString("CARD_TYPE_ID"));
                        }
                        if(jsonObject.containsKey("SEX_VIP")){
                            String sex=jsonObject.getString("SEX_VIP");
                            if("男,M,1".contains(sex)){
                                sex="男";
                            }else if("女,F,W,Y,0".contains(sex)){
                                sex="女";
                            }else{
                                sex="";
                            }
                            jsonObject.put("sex",sex);
                        }
                        if(jsonObject.containsKey("T_CR")){
                            jsonObject.put("join_date",jsonObject.getString("T_CR"));
                            t_cr = jsonObject.getString("T_CR");
                        }

                        if(StringUtils.isBlank(jsonObject.getString("vip_name"))){
                            jsonObject.put("vip_name","无");
                        }else  if(StringUtils.isBlank(jsonObject.getString("vip_phone"))){
                            jsonObject.put("vip_phone","无");
                        }else  if(StringUtils.isBlank(jsonObject.getString("cardno"))){
                            jsonObject.put("cardno","无");
                        }else  if(StringUtils.isBlank(jsonObject.getString("vip_card_type"))){
                            jsonObject.put("vip_card_type","无");
                        }else  if(StringUtils.isBlank(jsonObject.getString("sex"))){
                            jsonObject.put("sex","无");
                        } else if(StringUtils.isBlank(jsonObject.getString("join_date"))){
                            jsonObject.put("join_date","无");
                        }
                        HashMap<String,Object> map=new HashMap<String, Object>();
                        for(String key:jsonObject.keySet()){
                            map.put(key,jsonObject.getString(key));
                        }
                        obj.put("vip",map);
                    }
                }
            }

            String scan_time = "";
            if(type==0) {
                obj.put("attention_time", obj.get("create_date"));
                scan_time = obj.get("create_date") != null?obj.get("create_date").toString():"";
            }else if(type==1){
                obj.put("attention_time",obj.get("modified_date"));
                scan_time = obj.get("modified_date") != null?obj.get("modified_date").toString():"";
            }else if(type==2){
                obj.put("attention_time",obj.get("store_modified_date"));
                scan_time = obj.get("store_modified_date") != null?obj.get("store_modified_date").toString():"";
            }

            if (!scan_time.equals("") && t_cr != null && !t_cr.equals("")&& !t_cr.equals("无")){
                scan_time = scan_time.split(" ")[0];
                if (scan_time.compareTo(t_cr) > 0){
                    obj.put("is_new", "N");
                }else {
                    obj.put("is_new", "Y");
                }
            }

            list.add(obj);

        }
        return list;
    }



    public  ArrayList dbCursorToListForExecl(DBCursor dbCursor,int type) throws Exception{
        ArrayList list = new ArrayList();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            String t_cr = "";

            if(obj.get("transVip")!=null){
                if(obj.get("transVip").equals("Y")){
                    if(obj.get("vip")!=null){
                        String vip=obj.get("vip").toString();
                        JSONObject jsonObject= JSON.parseObject(vip);
                        t_cr = jsonObject.getString("join_date");

                        if(jsonObject.containsKey("NAME_VIP")){
                            jsonObject.put("vip_name",jsonObject.getString("NAME_VIP"));
                        }
                        if(jsonObject.containsKey("MOBILE_VIP")){
                            jsonObject.put("vip_phone",jsonObject.getString("MOBILE_VIP"));
                        }
                        if(jsonObject.containsKey("CARD_NO_VIP")){
                            jsonObject.put("cardno",jsonObject.getString("CARD_NO_VIP"));
                        }
                        if(jsonObject.containsKey("CARD_TYPE_ID")){
                            jsonObject.put("vip_card_type",jsonObject.getString("CARD_TYPE_ID"));
                        }
                        if(jsonObject.containsKey("SEX_VIP")){
                            String sex=jsonObject.getString("SEX_VIP");
                            if("男,M,1".contains(sex)){
                                sex="男";
                            }else if("女,F,W,Y,0".contains(sex)){
                                sex="女";
                            }else{
                                sex="";
                            }
                            jsonObject.put("sex",sex);
                        }
                        if(jsonObject.containsKey("T_CR")){
                            jsonObject.put("join_date",jsonObject.getString("T_CR"));
                            t_cr = jsonObject.getString("T_CR");
                        }

                        if(StringUtils.isBlank(jsonObject.getString("vip_name"))){
                            jsonObject.put("vip_name","无");
                        }else  if(StringUtils.isBlank(jsonObject.getString("vip_phone"))){
                            jsonObject.put("vip_phone","无");
                        }else  if(StringUtils.isBlank(jsonObject.getString("cardno"))){
                            jsonObject.put("cardno","无");
                        }else  if(StringUtils.isBlank(jsonObject.getString("vip_card_type"))){
                            jsonObject.put("vip_card_type","无");
                        }else  if(StringUtils.isBlank(jsonObject.getString("sex"))){
                            jsonObject.put("sex","无");
                        } else if(StringUtils.isBlank(jsonObject.getString("join_date"))){
                            jsonObject.put("join_date","无");
                        }

                        obj.removeField("vip");
                        Set<String> set=jsonObject.keySet();
                        for(String key:set){
                            obj.put(key,jsonObject.get(key));
                        }
                    }
                }
            }


            String scan_time = "";
            if(type==0) {
                obj.put("attention_time", obj.get("create_date"));
                scan_time = obj.get("create_date") != null?obj.get("create_date").toString():"";
            }else if(type==1){
                obj.put("attention_time",obj.get("modified_date"));
                scan_time = obj.get("modified_date") != null?obj.get("modified_date").toString():"";
            }else if(type==2){
                obj.put("attention_time",obj.get("store_modified_date"));
                scan_time = obj.get("store_modified_date") != null?obj.get("store_modified_date").toString():"";
            }

            if (!scan_time.equals("") && t_cr != null && !t_cr.equals("")&& !t_cr.equals("无")){
                scan_time = scan_time.split(" ")[0];
                if (scan_time.compareTo(t_cr) > 0){
                    obj.put("is_new", "N");
                }else {
                    obj.put("is_new", "Y");
                }
            }

            list.add(obj.toMap());
        }
        return list;
    }

}
