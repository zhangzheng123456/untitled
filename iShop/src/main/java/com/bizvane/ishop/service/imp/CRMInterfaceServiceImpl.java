package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.CorpParam;
import com.bizvane.ishop.entity.ParamConfigure;
import com.bizvane.ishop.network.drpapi.burgeon.Rest;
import com.bizvane.ishop.service.CRMInterfaceService;
import com.bizvane.ishop.service.CorpParamService;
import com.bizvane.ishop.service.ParamConfigureService;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yanyadong on 2016/1/13.
 */

@Service
public class CRMInterfaceServiceImpl implements CRMInterfaceService {

    private static final Logger logger = Logger.getLogger(CRMInterfaceServiceImpl.class);

    @Autowired
    ParamConfigureService paramConfigureService;

    @Autowired
    CorpParamService corpParamService;

    //增加会员(C_VIP_IMP)

    /**
     * @param
     * @return 新增字段
     * VIPNAME,SALESREP_ID__NAME,C_VIPTYPE_ID__NAME,DOCNOS,VIPCARDNO__CARDNO,MOBIL,ADDRESS,SEX,BIRTHDAY
     */
    public String addVip(String corp_code, HashMap<String, Object> vipInfomap) throws Exception {

        //企业信息
        List<String> drpList = new ArrayList<String>();

        String vipinfo = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();

        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号

            if (vipInfomap.get("VIPNAME") == null) {

                errorMap.put("message", "缺少VIPNAME");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();
            }
            if (vipInfomap.get("SALESREP_ID__NAME") == null) {

                errorMap.put("message", "缺少SALESREP_ID__NAME");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();
            }
            if (vipInfomap.get("C_VIPTYPE_ID__NAME") == null) {

                errorMap.put("message", "缺少C_VIPTYPE_ID__NAME");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();
            }
            if (vipInfomap.get("DOCNOS") == null) {

                errorMap.put("message", "缺少DOCNOS");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();

            }

            if (vipInfomap.get("MOBIL") == null) {

                errorMap.put("message", "缺少MOBIL");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();

            }

            if (vipInfomap.get("SEX") == null) {

                errorMap.put("message", "缺少SEX");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();

            }
            if (vipInfomap.get("BIRTHDAY") == null) {

                errorMap.put("message", "缺少BIRTHDAY");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();

            }
            String info = Rest.Add("C_VIP", drpList, vipInfomap);
            System.out.println("info...." + info);

            JSONArray jsonArray = new JSONArray(info);
            int code = (Integer) jsonArray.getJSONObject(0).get("code");

            if (code == -1) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                return jsonObject.toString();
            }

            int id = (Integer) jsonArray.getJSONObject(0).get("objectid");

            vipinfo = selVip(drpList, id);

            System.out.println(vipinfo);

        } else {
            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            vipinfo = jsonObject.toString();

        }
        return vipinfo;
    }

    /**
     * @param modVip 修改字段
     * @return VIPNAME, C_CUSTOMER_ID__NAME, SEX, SALESREP_ID__NAME, SALEHREMP_ID__NAME
     */

    //修改信息（C_VIP）
    public String modInfoVip(String corp_code, HashMap<String, Object> modVip) throws Exception {

        List<String> drpList = new ArrayList<String>();
        String info = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();
        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);

            drpList.add(corp_code); //企业编号

            Set<String> keys = modVip.keySet();

            if (!keys.contains("id")) {
                errorMap.put("message", "缺少id");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();
            }
            HashMap<String, Object> modViewVip = new HashMap<String, Object>();
            if (keys.contains("CARDNO")) {
                modViewVip.put("CARDNO", modVip.get("CARDNO"));
                modVip.remove("CARDNO");
            }
            if (keys.contains("C_STORE_ID__NAME")) {
                modViewVip.put("C_CUSTOMER_ID__NAME", modVip.get("C_CUSTOMER_ID__NAME"));
                modViewVip.put("C_STORE_ID__NAME", modVip.get("C_STORE_ID__NAME"));
                modViewVip.put("SALESREP_ID__NAME", modVip.get("SALESREP_ID__NAME"));

                modVip.remove("C_STORE_ID__NAME");
                modVip.remove("C_CUSTOMER_ID__NAME");
                modVip.remove("SALESREP_ID__NAME");
            }
            if (keys.contains("MOBIL")) {
                modViewVip.put("MOBIL", modVip.get("MOBIL"));
                modVip.remove("MOBIL");
            }
            if (keys.contains("BIRTHDAY")) {
                modViewVip.put("BIRTHDAY", modVip.get("BIRTHDAY"));
                modVip.remove("BIRTHDAY");
            }
            if (keys.contains("ISACTIVE")) {
                modViewVip.put("ISACTIVE", modVip.get("ISACTIVE"));
                modVip.remove("ISACTIVE");
            }
            if (modViewVip.size() > 0) {
                modViewVip.put("id", modVip.get("id"));
                String info_view = Rest.modify("C_VIP_VIEW", drpList, modViewVip);
                JSONArray jsonArray_view = new JSONArray(info_view);
                int code_view = (Integer) jsonArray_view.getJSONObject(0).get("code");
                if (code_view == -1) {
                    return jsonArray_view.getJSONObject(0).put("remarks", "C_VIP_VIEW表修改报错").toString();
                } else {
                    info = jsonArray_view.getJSONObject(0).toString();
                }
            }
            if (modVip.size() > 1) {
                info = Rest.modify("C_VIP", drpList, modVip);
                JSONArray jsonArray = new JSONArray(info);
                info = jsonArray.getJSONObject(0).toString();
            }

            if (info.equals("")) {
                errorMap.put("message", "未修改任何属性");
                errorMap.put("code", 0);
                JSONObject jsonObject = new JSONObject(errorMap);
                info = jsonObject.toString();
            }
        } else {
            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", 0);
            JSONObject jsonObject = new JSONObject(errorMap);
            info = jsonObject.toString();
        }

        return info;
    }

    //查询vip

    public String selVip(List<String> drplist, int vipId) throws Exception {

        String infos = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();

        if (drplist.get(3).toString().equals("C10016")) {
            //返回 会员ID 会员类型 会员卡号
            HashMap<String, Object> map = new HashMap<String, Object>();
            //查询
            map.put("table", "C_VIP");
            map.put("columns", new String[]{"id", "C_VIPTYPE_ID", "CARDNO", "MOBIL", "C_STORE_ID"});
            JSONObject expr1 = new JSONObject();
            expr1.put("column", "id");
            expr1.put("condition", vipId);
            map.put("params", expr1);

            String info = Rest.query(drplist, map);

            //System.out.println("cccc"+info);


            HashMap<String, Object> maprows = new HashMap<String, Object>();
            HashMap<String, Object> maprow = new HashMap<String, Object>();

            JSONArray jsonArray = new JSONArray(info);
            int code = (Integer) jsonArray.getJSONObject(0).get("code");
            String message = jsonArray.getJSONObject(0).get("message").toString();
            String id1 = jsonArray.getJSONObject(0).get("id").toString();

            if (code == -1) {

                return jsonArray.getJSONObject(0).toString();

            }
            String rows = jsonArray.getJSONObject(0).get("rows").toString();
            JSONArray jsonArray1 = new JSONArray(rows);

            maprows.put("message", message);
            maprows.put("code", code);
            maprows.put("id", id1);

            if (jsonArray1.length() > 0) {
                JSONArray rowjsonArray = jsonArray1.getJSONArray(0);
                maprow.put("id", rowjsonArray.get(0));
                //查询会员卡的名称
                String cardInfo = getVipTypeCardInfo(drplist.get(3).toString(), Integer.parseInt(rowjsonArray.get(1).toString()));
                JSONObject jsonObject = new JSONObject(cardInfo);
                String card_name = jsonObject.getJSONObject("rows").get("NAME").toString();
                maprow.put("C_VIPTYPE_ID", card_name);
                maprow.put("CARDNO", rowjsonArray.get(2));
                maprow.put("MOBIL", rowjsonArray.get(3));
                maprow.put("C_STORE_ID", rowjsonArray.get(4));
            } else {
                errorMap.put("message", "查询该VIPID时,未获取到符合信息");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                infos = jsonObject.toString();
                return infos;

            }
            maprows.put("rows", maprow);
            JSONObject jsonObject = new JSONObject(maprows);
            infos = jsonObject.toString();

        }
        return infos;
    }

    //查询单据编号+id
    public String selBill(String table, List<String> drplist, int id) throws Exception {

        String infos = "";

        if (drplist.get(3).toString().equals("C10016")) {

            HashMap<String, Object> map = new HashMap<String, Object>();
            //查询
            map.put("table", table);
            map.put("columns", new String[]{"id", "DOCNO"});
            JSONObject expr1 = new JSONObject();
            expr1.put("column", "id");
            expr1.put("condition", id);
            map.put("params", expr1);

            String info = Rest.query(drplist, map);

            System.out.println("cccc" + info);


            HashMap<String, Object> maprows = new HashMap<String, Object>();
            HashMap<String, Object> maprow = new HashMap<String, Object>();

            JSONArray jsonArray = new JSONArray(info);
            int code = (Integer) jsonArray.getJSONObject(0).get("code");
            String message = jsonArray.getJSONObject(0).get("message").toString();
            String id1 = jsonArray.getJSONObject(0).get("id").toString();

            if (code == -1) {

                return jsonArray.getJSONObject(0).toString();

            }
            String rows = jsonArray.getJSONObject(0).get("rows").toString();
            JSONArray jsonArray1 = new JSONArray(rows);

            maprows.put("message", message);
            maprows.put("code", code);
            maprows.put("id", id1);

            if (jsonArray1.length() > 0) {
                JSONArray rowjsonArray = jsonArray1.getJSONArray(0);
                maprow.put("id", rowjsonArray.get(0));
                maprow.put("DOCNO", rowjsonArray.get(1));
            }
            maprows.put("rows", maprow);
            JSONObject jsonObject = new JSONObject(maprows);
            infos = jsonObject.toString();

        }
        return infos;

    }


    //会员密码短信推送,修改密码(C_VIP)

    /**
     * @param
     * @return 修改modfiy_integral_password
     * VIPNAME,C_CUSTOMER_ID__NAME,SEX,SALESREP_ID__NAME,SALEHREMP_ID__NAME,PASS_WORD,INTEGRAL_PASSWORD,id
     */

    //积分付款密码（INTEGRAL_PASSWORD）
    public String modIntegral_passwordVip(String corp_code, HashMap<String, Object> integral_passwordVip) throws Exception {

        List<String> drpList = new ArrayList<String>();
        String info = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();

        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号
            String integral_password = integral_passwordVip.get("INTEGRAL_PASSWORD").toString();
            String vipId = integral_passwordVip.get("id").toString();
            String vipPhone = integral_passwordVip.get("phone").toString();


            //判断id是否存在
            String cardNoJs = selVip(drpList, Integer.parseInt(vipId));
//            JSONObject cardNoJsonoObject=new JSONObject(cardNoJs);
            com.alibaba.fastjson.JSONObject result_obj = com.alibaba.fastjson.JSONObject.parseObject(cardNoJs);
            String code = result_obj.get("code").toString();
            if (code.equals("0")) {
                String result = result_obj.get("rows").toString();
                com.alibaba.fastjson.JSONObject obj = com.alibaba.fastjson.JSONObject.parseObject(result);

                if (obj.get("id") == null || obj.get("id").equals("")) {

                    errorMap.put("message", "不存在该VIP");
                    errorMap.put("code", -1);
                    JSONObject jsonObject = new JSONObject(errorMap);
                    info = jsonObject.toString();
                    return info;
                }

                //判断该id下的会员手机号与传参（手机号）是否一致
                if (!obj.get("MOBIL").equals(vipPhone)) {
                    errorMap.put("message", "手机号不一致");
                    errorMap.put("code", -1);
                    JSONObject jsonObject = new JSONObject(errorMap);
                    info = jsonObject.toString();
                    return info;
                }
            } else {
                return cardNoJs;
            }


            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", "modfiy_integral_password");
            JSONArray jsonArray = new JSONArray();
            //修改的密码
            jsonArray.put(integral_password);
            //传入的vipid (可在C_VIP表查看)
            jsonArray.put(vipId);
            map.put("values", jsonArray);
            info = Rest.excuteSql(drpList, map);
            JSONArray jsonArray1 = new JSONArray(info);
            info = jsonArray1.getJSONObject(0).toString();

        } else {

            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            info = jsonObject.toString();

        }

        return info;

    }


    //预存款密码（PASS_WORD）
    public String modfiy_passwordVip(String corp_code, HashMap<String, Object> passwordMap) throws Exception {


        List<String> drpList = new ArrayList<String>();
        String info = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();

        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号
            String modfiy_password = passwordMap.get("PASS_WORD").toString();
            String vipId = passwordMap.get("id").toString();
            String vipPhone = passwordMap.get("phone").toString();


            String cardNoJs = selVip(drpList, Integer.parseInt(vipId));
//            JSONObject cardNoJsonoObject=new JSONObject(cardNoJs);
            com.alibaba.fastjson.JSONObject result_obj = com.alibaba.fastjson.JSONObject.parseObject(cardNoJs);
            String code = result_obj.get("code").toString();
            if (code.equals("0")) {
                String result = result_obj.get("rows").toString();
                com.alibaba.fastjson.JSONObject obj = com.alibaba.fastjson.JSONObject.parseObject(result);

                if (obj.get("id") == null || obj.get("id").equals("")) {

                    errorMap.put("message", "不存在该VIP");
                    errorMap.put("code", -1);
                    JSONObject jsonObject = new JSONObject(errorMap);
                    info = jsonObject.toString();
                    return info;
                }

                //判断该id下的会员手机号与传参（手机号）是否一致
                if (!obj.get("MOBIL").equals(vipPhone)) {
                    errorMap.put("message", "手机号不一致");
                    errorMap.put("code", -1);
                    JSONObject jsonObject = new JSONObject(errorMap);
                    info = jsonObject.toString();
                    return info;
                }
            } else {
                return cardNoJs;
            }

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", "modfiy_password");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(modfiy_password);
            jsonArray.put(vipId);
            map.put("values", jsonArray);
            info = Rest.excuteSql(drpList, map);
            JSONArray jsonArray1 = new JSONArray(info);
            info = jsonArray1.getJSONObject(0).toString();

        } else {

            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            info = jsonObject.toString();
        }

        return info;

    }

    //券信息获取

    public String couponInfo(String corp_code, HashMap<String, Object> couponMap) throws Exception {

        List<String> drpList = new ArrayList<String>();
        String info = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();
        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号

            String vipId = couponMap.get("id").toString();
            String vipPhone = couponMap.get("phone").toString();

            String cardNoJs = selVip(drpList, Integer.parseInt(vipId));
//            JSONObject cardNoJsonoObject=new JSONObject(cardNoJs);
            com.alibaba.fastjson.JSONObject result_obj = com.alibaba.fastjson.JSONObject.parseObject(cardNoJs);
            String code = result_obj.get("code").toString();
            if (code.equals("0")) {
                String result = result_obj.get("rows").toString();
                com.alibaba.fastjson.JSONObject obj = com.alibaba.fastjson.JSONObject.parseObject(result);

                if (obj.get("id") == null || obj.get("id").equals("")) {

                    errorMap.put("message", "不存在该VIP");
                    errorMap.put("code", -1);
                    JSONObject jsonObject = new JSONObject(errorMap);
                    info = jsonObject.toString();
                    return info;
                }

                //判断该id下的会员手机号与传参（手机号）是否一致
                if (!obj.get("MOBIL").equals(vipPhone)) {
                    errorMap.put("message", "手机号不一致");
                    errorMap.put("code", -1);
                    JSONObject jsonObject = new JSONObject(errorMap);
                    info = jsonObject.toString();
                    return info;
                }
            }
            info = Rest.excuteWebaction(drpList, "C_VIP_TDEFTICKET_GEN", Integer.parseInt(vipId));
            JSONArray jsonArray = new JSONArray(info);
            info = jsonArray.getJSONObject(0).toString();

        } else {
            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            info = jsonObject.toString();

        }

        return info;
    }


    //VIP卡充值单据 新增

    /**
     * @param
     * @return 新增字段
     * BILLDATE,RECHARGE_TYPE,C_VIPMONEY_STORE_ID__NAME,SALESREP_ID__NAME,C_VIP_ID__CARDNO，
     * TOT_AMT_ACTUAL,AMOUNT_ACTUAL,ACTIVE_CONTENT,DESCRIPTION,KVGR1
     */

    public String addPrepaidDocuments(String corp_code, HashMap<String, Object> documentInfo) throws Exception {

        List<String> drpList = new ArrayList<String>();
        String billinfo = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();
        if (corp_code.equals("C10016")) {


            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号
            if (documentInfo.get("BILLDATE") == null) {

                errorMap.put("message", "缺少BILLDATE");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);

                return jsonObject.toString();
            }
            if (documentInfo.get("RECHARGE_TYPE") == null) {

                errorMap.put("message", "缺少RECHARGE_TYPE");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);

                return jsonObject.toString();
            }
            if (documentInfo.get("C_VIPMONEY_STORE_ID__NAME") == null) {

                errorMap.put("message", "缺少C_VIPMONEY_STORE_ID__NAME");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();

            }
            if (documentInfo.get("SALESREP_ID__NAME") == null) {

                errorMap.put("message", "缺少SALESREP_ID__NAME");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();

            }
            if (documentInfo.get("C_VIP_ID__CARDNO") == null) {

                errorMap.put("message", "缺少C_VIP_ID__CARDNO");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();

            }
            if (documentInfo.get("TOT_AMT_ACTUAL") == null) {

                errorMap.put("message", "缺少TOT_AMT_ACTUAL");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();

            }

            if (documentInfo.get("AMOUNT_ACTUAL") == null) {
                errorMap.put("message", "缺少AMOUNT_ACTUAL");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);

                return jsonObject.toString();
            }
            if (documentInfo.get("ACTIVE_CONTENT") == null) {

                errorMap.put("message", "缺少ACTIVE_CONTENT");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();

            }

            String info = Rest.Add("B_VIPMONEY", drpList, documentInfo);

            JSONArray jsonArray = new JSONArray(info);
            JSONObject jsonOb = jsonArray.getJSONObject(0);

            int code = (Integer) jsonOb.get("code");

            if (code == -1) {
                return jsonOb.toString();
            }

            int id = (Integer) jsonOb.get("objectid");

            System.out.println("添加充值单返回的id值..........id:" + id);

            billinfo = selBill("B_VIPMONEY", drpList, id);


            //判断id为空值的情况
            JSONObject billInfoJson = new JSONObject(billinfo);
            JSONObject rowJson = billInfoJson.getJSONObject("rows");
            if (rowJson.get("id").equals("") || rowJson.get("id") == null) {
                billinfo = selBill("B_VIPMONEY", drpList, id);
            }
            //判断单据编号为空值的情况
            if (rowJson.get("DOCNO").equals("") || rowJson.get("DOCNO") == null) {
                billinfo = selBill("B_VIPMONEY", drpList, id);
            }

        } else {

            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            billinfo = jsonObject.toString();

        }

        System.out.println(billinfo);

        return billinfo;
    }


    ////VIP卡充值单据

    public String modPrepaidStatus(String corp_code, HashMap<String, Object> modStatus) throws Exception {


        List<String> drpList = new ArrayList<String>();
        String info = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();
        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号


            Set<String> keys = modStatus.keySet();

            if (!keys.contains("id")) {
                errorMap.put("message", "缺少id");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();
            }
            HashMap<String, Object> subMap = new HashMap<String, Object>();
            subMap.put("id", modStatus.get("id").toString());
            String jsString = getPrepaidOrder(corp_code, subMap);

            JSONObject jsonObject = new JSONObject(jsString);
            if (!jsonObject.get("code").toString().equals("0")) {
                errorMap.put("message", "单据不存在");
                errorMap.put("code", -1);
                JSONObject jsonObject1 = new JSONObject(errorMap);
                info = jsonObject1.toString();
                return info;
            }

            if (keys.contains("bill_date")) {
                modStatus.put("BILLDATE", modStatus.get("bill_date").toString());
                modStatus.remove("bill_date");
            }
            if (keys.contains("recharge_type")) {
                modStatus.put("RECHARGE_TYPE", modStatus.get("recharge_type").toString());
                modStatus.remove("recharge_type");
            }
            if (keys.contains("store_name")) {
                modStatus.put("C_VIPMONEY_STORE_ID__NAME", modStatus.get("store_name").toString());
                modStatus.remove("store_name");
            }
            if (keys.contains("user_name")) {
                modStatus.put("SALESREP_ID__NAME", modStatus.get("user_name").toString());
                modStatus.remove("user_name");
            }
            //.....
            if (keys.contains("card_no")) {
                modStatus.put("C_VIP_ID__CARDNO", modStatus.get("card_no").toString());
                modStatus.remove("card_no");
            }
            if (keys.contains("tag_price")) {
                modStatus.put("TOT_AMT_ACTUAL", modStatus.get("tag_price").toString());
                modStatus.remove("tag_price");
            }
            if (keys.contains("pay_price")) {
                modStatus.put("AMOUNT_ACTUAL", modStatus.get("pay_price").toString());
                modStatus.remove("pay_price");
            }
            if (keys.contains("active_content")) {
                modStatus.put("ACTIVE_CONTENT", modStatus.get("active_content").toString());
                modStatus.remove("active_content");
            }
            if (keys.contains("remark")) {
                modStatus.put("DESCRIPTION", modStatus.get("remark").toString());
                modStatus.remove("remark");
            }

            info = Rest.modify("B_VIPMONEY", drpList, modStatus);
            JSONArray jsonArray = new JSONArray(info);
            info = jsonArray.getJSONObject(0).toString();
        } else {
            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            info = jsonObject.toString();

        }

        return info;
    }


    /**
     * VIP充值退款：
     * 处理方式：CRM调用DRP接口完成充值单据新增、审核（审核状态如何修改）
     * 审核（修改单据状态）
     * 表（B_RET_VIPMONEY）
     */

    //充值退款单据新增
    public String addRefund(String corp_code, HashMap<String, Object> refundInfo) throws Exception {

        List<String> drpList = new ArrayList<String>();

        String billinfo = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();
        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号
            if (refundInfo.get("BILLDATE") == null) {

                errorMap.put("message", "缺少BILLDATE");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);

                return jsonObject.toString();
            }
            if (refundInfo.get("RECHARGE_TYPE") == null) {
                errorMap.put("message", "缺少RECHARGE_TYPE");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();
            }
            if (refundInfo.get("C_VIPMONEY_STORE_ID__NAME") == null) {
                errorMap.put("message", "缺少C_VIPMONEY_STORE_ID__NAME");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);

                return jsonObject.toString();

            }

            String info = Rest.Add("B_RET_VIPMONEY", drpList, refundInfo);

            JSONArray jsonArray = new JSONArray(info);
            JSONObject jsonOb = jsonArray.getJSONObject(0);

            int code = (Integer) jsonOb.get("code");

            if (code == -1) {
                return jsonOb.toString();
            }

            int id = (Integer) jsonOb.get("objectid");
            System.out.println("充值退款:" + id);

            billinfo = selBill("B_RET_VIPMONEY", drpList, id);

        } else {

            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            billinfo = jsonObject.toString();

        }
        return billinfo;

    }


    //修改充值退款单据状态

    public String modRefundStatus(String corp_code, HashMap<String, Object> modStatusRefund) throws Exception {

        List<String> drpList = new ArrayList<String>();

        String info = "";

        HashMap<String, Object> errorMap = new HashMap<String, Object>();
        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号

            Set<String> keys = modStatusRefund.keySet();

            if (!keys.contains("id")) {
                errorMap.put("message", "缺少id");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();
            }
            HashMap<String, Object> subMap = new HashMap<String, Object>();
            subMap.put("id", modStatusRefund.get("id").toString());
            String jsString = getRefundOrder(corp_code, subMap);

            JSONObject jsonObject = new JSONObject(jsString);
            if (!jsonObject.get("code").toString().equals("0")) {
                errorMap.put("message", "单据不存在");
                errorMap.put("code", -1);
                JSONObject jsonObject1 = new JSONObject(errorMap);
                info = jsonObject1.toString();
                return info;
            }
            if (keys.contains("bill_date")) {
                modStatusRefund.put("BILLDATE", modStatusRefund.get("bill_date").toString());
                modStatusRefund.remove("bill_date");
            }
            if (keys.contains("recharge_type")) {
                modStatusRefund.put("RECHARGE_TYPE", modStatusRefund.get("recharge_type").toString());
                modStatusRefund.remove("recharge_type");
            }
            if (keys.contains("store_name")) {
                modStatusRefund.put("C_VIPMONEY_STORE_ID__NAME", modStatusRefund.get("store_name").toString());
                modStatusRefund.remove("store_name");
            }
            if (keys.contains("source_no")) {
                modStatusRefund.put("ORGDOCNO", modStatusRefund.get("source_no").toString());
                modStatusRefund.remove("source_no");
            }
            if (keys.contains("tag_price")) {
                modStatusRefund.put("TOT_AMT_ACTUAL", modStatusRefund.get("tag_price").toString());
                modStatusRefund.remove("tag_price");
            }
            if (keys.contains("remark")) {
                modStatusRefund.put("DESCRIPTION", modStatusRefund.get("remark").toString());
                modStatusRefund.remove("remark");
            }

            info = Rest.modify("B_RET_VIPMONEY", drpList, modStatusRefund);
            JSONArray jsonArray = new JSONArray(info);
            info = jsonArray.getJSONObject(0).toString();

        } else {

            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            info = jsonObject.toString();

        }
        return info;
    }


    //VIP卡充值单据提交
    public String submitPrepaidBill(String corp_code, int id) throws Exception {

        List<String> drpList = new ArrayList<String>();

        String info = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();

        if (corp_code.equals("C10016")) {
            //获取提交状态,若为已提交则直接返回
            HashMap<String, Object> subMap = new HashMap<String, Object>();
            subMap.put("id", id);
            String jsString = getPrepaidOrder(corp_code, subMap);

            JSONObject jsonObject = new JSONObject(jsString);
            if (!jsonObject.get("code").toString().equals("0")) {
                errorMap.put("message", "单据不存在");
                errorMap.put("code", -1);
                JSONObject jsonObject1 = new JSONObject(errorMap);
                info = jsonObject1.toString();
                return info;
            }
            String status = jsonObject.getJSONObject("rows").get("STATUS").toString();
            if (status.equals("提交")) {
                errorMap.put("message", "已提交");
                errorMap.put("code", 0);
                JSONObject jsonObject1 = new JSONObject(errorMap);
                info = jsonObject1.toString();
                return info;
            }
            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", "B_VIPMONEY");
            map.put("id", id);
            info = Rest.submitObject(drpList, map);
            JSONArray jsonArray = new JSONArray(info);
            info = jsonArray.getJSONObject(0).toString();
            //提交后,查询提交后的状态若为1则直接返回成功,若code为-1（则查询状态是否为提交，是返回成功，不是直接返回）
            JSONObject object = new JSONObject(info);
            int code = Integer.parseInt(object.get("code").toString());
            if (code == -1) {
                String jsString_once = getPrepaidOrder(corp_code, subMap);
                JSONObject jsonObject_once = new JSONObject(jsString_once);
                String status_once = jsonObject_once.getJSONObject("rows").get("STATUS").toString();
                if (status_once.equals("提交")) {
                    errorMap.put("message", "已提交");
                    errorMap.put("code", 0);
                    JSONObject jsonObject1 = new JSONObject(errorMap);
                    info = jsonObject1.toString();
                    return info;
                }
            }
        } else {
            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            info = jsonObject.toString();
        }
        return info;
    }

    //VIP卡充值退款单据提交
    public String submitRefundBill(String corp_code, int id) throws Exception {

        List<String> drpList = new ArrayList<String>();

        String info = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();
        if (corp_code.equals("C10016")) {

            //获取提交状态,若为已提交则直接返回
            HashMap<String, Object> subMap = new HashMap<String, Object>();
            subMap.put("id", id);
            String jsString = getRefundOrder(corp_code, subMap);
            JSONObject jsonObject = new JSONObject(jsString);
            if (!jsonObject.get("code").toString().equals("0")) {
                errorMap.put("message", "单据不存在");
                errorMap.put("code", -1);
                JSONObject jsonObject1 = new JSONObject(errorMap);
                info = jsonObject1.toString();
                return info;
            }
            String status = jsonObject.getJSONObject("rows").get("STATUS").toString();
            if (status.equals("提交")) {
                errorMap.put("message", "已提交");
                errorMap.put("code", 0);
                JSONObject jsonObject1 = new JSONObject(errorMap);
                info = jsonObject1.toString();
                return info;
            }
            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", "B_RET_VIPMONEY");
            map.put("id", id);
            info = Rest.submitObject(drpList, map);
            JSONArray jsonArray = new JSONArray(info);
            info = jsonArray.getJSONObject(0).toString();
            //提交后,查询提交后的状态若为1则直接返回成功,若code为-1（则查询状态是否为提交，是返回成功，不是直接返回）
            JSONObject object = new JSONObject(info);
            int code = Integer.parseInt(object.get("code").toString());
            if (code == -1) {
                String jsString_once = getRefundOrder(corp_code, subMap);
                JSONObject jsonObject_once = new JSONObject(jsString_once);
                String status_once = jsonObject_once.getJSONObject("rows").get("STATUS").toString();
                if (status_once.equals("提交")) {
                    errorMap.put("message", "已提交");
                    errorMap.put("code", 0);
                    JSONObject jsonObject1 = new JSONObject(errorMap);
                    info = jsonObject1.toString();
                    return info;
                }
            }
        } else {
            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            info = jsonObject.toString();
        }
        return info;
    }


    //VIP卡充值单据取消提交
    public String canclePrepaidBill(String corp_code, int id) throws Exception {

        List<String> drpList = new ArrayList<String>();

        String info = "";
        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号


            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", "B_VIPMONEY");
            map.put("id", id);
            info = Rest.unSubmitObject(drpList, map);
            JSONArray jsonArray = new JSONArray(info);
            info = jsonArray.getJSONObject(0).toString();
        }
        return info;
    }

    //VIP卡充值退款单据取消提交
    public String cancleRefundBill(String corp_code, int id) throws Exception {

        List<String> drpList = new ArrayList<String>();

        String info = "";
        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", "B_RET_VIPMONEY");
            map.put("id", id);
            info = Rest.unSubmitObject(drpList, map);
            JSONArray jsonArray = new JSONArray(info);
            info = jsonArray.getJSONObject(0).toString();
        }
        return info;
    }


    //充值单详情

    public String getPrepaidOrder(String corp_code, HashMap<String, Object> prepaidMap) throws Exception {

        List<String> drpList = new ArrayList<String>();

        String infos = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();

        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号

            HashMap<String, Object> map = new HashMap<String, Object>();
            //查询
            map.put("table", "B_VIPMONEY");
            String[] columns = new String[]{"id", "AD_CLIENT_ID", "AD_ORG_ID", "DOCNO", "BILLDATE", "RECHARGE_TYPE", "C_VIPMONEY_STORE_ID",
                    "SALESREP_ID", "C_VIP_ID", "TOT_AMT_ACTUAL", "AMOUNT_ACTUAL", "DISCOUNT", "C_MARKBALTYPE_ID", "C_CUSTOMER_ID", "STATUS",
                    "C_STORE_ID", "ACTIVE_CONTENT", "AU_STATE", "DESCRIPTION", "KVGR1", "IS_INSAP", "INSAPTIME", "AU_PI_ID", "OWNERID", "MODIFIERID"
                    , "CREATIONDATE", "MODIFIEDDATE", "STATUSERID", "STATUSTIME", "ISACTIVE"};
            map.put("columns", columns);
            JSONObject expr1 = new JSONObject();

            if (prepaidMap.get("DOCNO") != null) {
                expr1.put("column", "DOCNO");
                expr1.put("condition", prepaidMap.get("DOCNO").toString());
            } else {
                expr1.put("column", "id");
                expr1.put("condition", prepaidMap.get("id").toString());
            }

            map.put("params", expr1);

            String info = Rest.query(drpList, map);

            HashMap<String, Object> maprows = new HashMap<String, Object>();
            HashMap<String, Object> maprow = new HashMap<String, Object>();

            if (info == null || info.equals("")) {
                errorMap.put("message", "查询失败");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                infos = jsonObject.toString();
                return infos;
            }

            JSONArray jsonArray = new JSONArray(info);
            int code = (Integer) jsonArray.getJSONObject(0).get("code");
            String message = jsonArray.getJSONObject(0).get("message").toString();
            String id1 = jsonArray.getJSONObject(0).get("id").toString();

            if (code == -1) {

                return jsonArray.getJSONObject(0).toString();

            }
            String rows = jsonArray.getJSONObject(0).get("rows").toString();
            JSONArray jsonArray1 = new JSONArray(rows);

            maprows.put("message", message);
            maprows.put("code", code);
            maprows.put("id", id1);

            if (jsonArray1.length() > 0) {
                JSONArray rowjsonArray = jsonArray1.getJSONArray(0);
                for (int i = 0; i < rowjsonArray.length(); i++) {
                    maprow.put(columns[i], rowjsonArray.get(i).toString());
                }
            }
            if (maprow.size() == 0) {
                errorMap.put("message", "未能查找到符合信息");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                infos = jsonObject.toString();
                return infos;
            }
            maprows.put("rows", maprow);
            JSONObject jsonObject = new JSONObject(maprows);
            infos = jsonObject.toString();

        } else {

            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            infos = jsonObject.toString();

        }
        return infos;
    }


    //获取充值退款详情
    @Override
    public String getRefundOrder(String corp_code, HashMap<String, Object> refundMap) throws Exception {

        List<String> drpList = new ArrayList<String>();

        String infos = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();

        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号

            HashMap<String, Object> map = new HashMap<String, Object>();
            //查询
            map.put("table", "B_RET_VIPMONEY");

            String[] columns = new String[]{"id", "AD_CLIENT_ID", "AD_ORG_ID", "DOCNO", "BILLDATE", "RECHARGE_TYPE", "C_VIPMONEY_STORE_ID"
                    , "ORGDOCNO", "USER_ID", "C_VIP_ID", "PASS_WORD", "TOT_AMT_ACTUAL", "AMOUNT_ACTUAL", "DISCOUNT", "C_MARKBALTYPE_ID", "C_CUSTOMER_ID"
                    , "C_STORE_ID", "STATUS", "AU_STATE", "DESCRIPTION", "KVGR1", "IS_INSAP", "INSAPTIME", "AU_PI_ID"};

            map.put("columns", columns);
            JSONObject expr1 = new JSONObject();

            if (refundMap.get("DOCNO") != null) {
                expr1.put("column", "DOCNO");
                expr1.put("condition", refundMap.get("DOCNO").toString());
            } else {
                expr1.put("column", "id");
                expr1.put("condition", refundMap.get("id").toString());
            }
            map.put("params", expr1);

            String info = Rest.query(drpList, map);

            //  System.out.println("cccc"+info);


            HashMap<String, Object> maprows = new HashMap<String, Object>();
            HashMap<String, Object> maprow = new HashMap<String, Object>();

            JSONArray jsonArray = new JSONArray(info);
            int code = (Integer) jsonArray.getJSONObject(0).get("code");
            String message = jsonArray.getJSONObject(0).get("message").toString();
            String id1 = jsonArray.getJSONObject(0).get("id").toString();

            if (code == -1) {

                return jsonArray.getJSONObject(0).toString();

            }
            String rows = jsonArray.getJSONObject(0).get("rows").toString();
            JSONArray jsonArray1 = new JSONArray(rows);

            maprows.put("message", message);
            maprows.put("code", code);
            maprows.put("id", id1);

            if (jsonArray1.length() > 0) {
                JSONArray rowjsonArray = jsonArray1.getJSONArray(0);
                for (int i = 0; i < rowjsonArray.length(); i++) {
                    maprow.put(columns[i], rowjsonArray.get(i).toString());
                }
            }
            if (maprow.size() == 0) {
                errorMap.put("message", "未能查找到符合信息");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                infos = jsonObject.toString();
                return infos;
            }
            maprows.put("rows", maprow);
            JSONObject jsonObject = new JSONObject(maprows);
            infos = jsonObject.toString();

        } else {

            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            infos = jsonObject.toString();

        }
        return infos;

    }


    // 获取余额详情  FA_VIPACC

    public String getBalance(String corp_code, String vipId) throws Exception {


        List<String> drpList = new ArrayList<String>();
        String infos = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();

        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号

            HashMap<String, Object> map = new HashMap<String, Object>();
            //查询
            map.put("table", "FA_VIPACC");

            String[] columns = new String[]{"id", "AD_CLIENT_ID", "AD_ORG_ID", "C_VIP_ID", "AMOUNT", "AMOUNT_ACTUAL", "PRE_DISCOUNT"
                    , "AMT_ORDER", "INTEGRAL", "INTEGRAL_RANK", "TOT_AMT_ACTUAL", "TOT_AMT_LIST", "AVG_DISCOUNT", "TIMES", "AVG_TIME_ACTUAL"
                    , "AVG_QTY_ACTUAL", "TOTQTY", "LASTDATE", "LAST_AMT_ACTUAL", "FIRSTDATE", "FIRST_AMT_ACTUAL", "OWNERID", "MODIFIERID"
                    , "CREATIONDATE", "MODIFIEDDATE", "ISACTIVE"};

            map.put("columns", columns);
            JSONObject expr1 = new JSONObject();
            expr1.put("column", "C_VIP_ID");
            expr1.put("condition", vipId);
            map.put("params", expr1);

            String info = Rest.query(drpList, map);

            System.out.println("获取余额信息" + info);


            HashMap<String, Object> maprows = new HashMap<String, Object>();
            HashMap<String, Object> maprow = new HashMap<String, Object>();

            JSONArray jsonArray = new JSONArray(info);
            int code = (Integer) jsonArray.getJSONObject(0).get("code");
            String message = jsonArray.getJSONObject(0).get("message").toString();
            String id1 = jsonArray.getJSONObject(0).get("id").toString();

            if (code == -1) {

                return jsonArray.getJSONObject(0).toString();

            }
            String rows = jsonArray.getJSONObject(0).get("rows").toString();
            JSONArray jsonArray1 = new JSONArray(rows);

            maprows.put("message", message);
            maprows.put("code", code);
            maprows.put("id", id1);

            if (jsonArray1.length() > 0) {
                JSONArray rowjsonArray = jsonArray1.getJSONArray(0);
                for (int i = 0; i < rowjsonArray.length(); i++) {
                    maprow.put(columns[i], rowjsonArray.get(i).toString());
                }
            }

            if (maprow.size() == 0) {
                errorMap.put("message", "未能查找到符合信息");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                infos = jsonObject.toString();
                return infos;
            }
            maprows.put("rows", maprow);
            JSONObject jsonObject = new JSONObject(maprows);
            infos = jsonObject.toString();


        } else {

            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            infos = jsonObject.toString();

        }
        return infos;


    }

    //会员基本信息的修改(添加)

    public String updateBaseInfoVip(String corp_code, HashMap<String, Object> modBaseVip) throws Exception {

        List<String> drpList = new ArrayList<String>();
        String baseInfo = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();
        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);

            drpList.add(corp_code); //企业编号

            if (modBaseVip.get("id") == null) {

                errorMap.put("message", "缺少id");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                return jsonObject.toString();
            }

            baseInfo = Rest.modify("C_V_VIP", drpList, modBaseVip);
            JSONArray jsonArray = new JSONArray(baseInfo);
            baseInfo = jsonArray.getJSONObject(0).toString();
        } else {

            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            baseInfo = jsonObject.toString();
        }
        return baseInfo;
    }

    //vip充值记录明细 (必须已提交)

    public String getVipRechargeRecord(String corp_code, int vipId) throws Exception {

        String recordInfo = "";
        List<String> drpList = new ArrayList<String>();
        HashMap<String, Object> errorMap = new HashMap<String, Object>();

        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号

            //返回 会员ID 会员类型 会员卡号
            HashMap<String, Object> map = new HashMap<String, Object>();
            //查询
            map.put("table", "FA_VIPINTEGRAL_FTP02");
            map.put("columns", new String[]{"id", "AD_CLIENT_ID", "AD_ORG_ID", "CHANGDATE", "C_VIP_ID"
                    , "DOCNO", "AMT_ACTUAL", "VIP_PAYAMT", "VIP_PAYAMT_ACTUAL", "DISCOUNT", "PRE_DISCOUNT", "DESCRIPTION"
                    , "OWNERID", "MODIFIERID", "CREATIONDATE", "MODIFIEDDATE", "ISACTIVE"});
            JSONObject expr1 = new JSONObject();
            expr1.put("column", "C_VIP_ID");
            expr1.put("condition", vipId);
            map.put("params", expr1);

            String info = Rest.query(drpList, map);

            System.out.println("record......" + info);

            HashMap<String, Object> maprows = new HashMap<String, Object>();
            List<HashMap<String, Object>> listRow = new ArrayList<HashMap<String, Object>>();

            JSONArray jsonArray = new JSONArray(info);
            int code = (Integer) jsonArray.getJSONObject(0).get("code");
            String message = jsonArray.getJSONObject(0).get("message").toString();
            String id1 = jsonArray.getJSONObject(0).get("id").toString();

            if (code == -1) {
                return jsonArray.getJSONObject(0).toString();
            }
            String rows = jsonArray.getJSONObject(0).get("rows").toString();

            JSONArray jsonArray1 = new JSONArray(rows);

            maprows.put("message", message);
            maprows.put("code", code);
            maprows.put("id", id1);

            if (jsonArray1.length() > 0) {

                for (int i = 0; i < jsonArray1.length(); i++) {
                    HashMap<String, Object> maprow = new HashMap<String, Object>();
                    JSONArray columnJsonArray = jsonArray1.getJSONArray(i);
                    maprow.put("id", columnJsonArray.get(0));
                    maprow.put("AD_CLIENT_ID", columnJsonArray.get(1));
                    maprow.put("AD_ORG_ID", columnJsonArray.get(2));
                    maprow.put("CHANGDATE", columnJsonArray.get(3));
                    maprow.put("C_VIP_ID", columnJsonArray.get(4));
                    maprow.put("DOCNO", columnJsonArray.get(5));
                    maprow.put("AMT_ACTUAL", columnJsonArray.get(6));
                    maprow.put("VIP_PAYAMT", columnJsonArray.get(7));
                    maprow.put("VIP_PAYAMT_ACTUAL", columnJsonArray.get(8));
                    maprow.put("DISCOUNT", columnJsonArray.get(9));
                    maprow.put("PRE_DISCOUNT", columnJsonArray.get(10));
                    maprow.put("DESCRIPTION", columnJsonArray.get(11));
                    maprow.put("OWNERID", columnJsonArray.get(12));
                    maprow.put("MODIFIERID", columnJsonArray.get(13));
                    maprow.put("CREATIONDATE", columnJsonArray.get(14));
                    maprow.put("MODIFIEDDATE", columnJsonArray.get(15));
                    maprow.put("ISACTIVE", columnJsonArray.get(16));
                    listRow.add(maprow);
                }
            } else {
                errorMap.put("message", "未查到符合信息");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                recordInfo = jsonObject.toString();
                return recordInfo;
            }
            maprows.put("rows", listRow);
            JSONObject jsonObject = new JSONObject(maprows);
            recordInfo = jsonObject.toString();

        } else {

            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            recordInfo = jsonObject.toString();

        }
        return recordInfo;

    }

    //获取生日券
    public String getVipBirthTicket(String corp_code, HashMap<String, Object> couponMap) throws Exception {


        List<String> drpList = new ArrayList<String>();
        String info = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();

        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号
            String vipPhone = couponMap.get("phone").toString();
            //查询参数
            String vipId = couponMap.get("id").toString();
            String cardNoJs = selVip(drpList, Integer.parseInt(vipId));
//            JSONObject cardNoJsonoObject=new JSONObject(cardNoJs);
            com.alibaba.fastjson.JSONObject result_obj = com.alibaba.fastjson.JSONObject.parseObject(cardNoJs);
            String code = result_obj.get("code").toString();
            if (code.equals("0")) {
                String result = result_obj.get("rows").toString();
                com.alibaba.fastjson.JSONObject obj = com.alibaba.fastjson.JSONObject.parseObject(result);

                if (obj.get("id") == null || obj.get("id").equals("")) {

                    errorMap.put("message", "不存在该VIP");
                    errorMap.put("code", -1);
                    JSONObject jsonObject = new JSONObject(errorMap);
                    info = jsonObject.toString();
                    return info;
                }

                //判断该id下的会员手机号与传参（手机号）是否一致
                if (!obj.get("MOBIL").equals(vipPhone)) {
                    errorMap.put("message", "手机号不一致");
                    errorMap.put("code", -1);
                    JSONObject jsonObject = new JSONObject(errorMap);
                    info = jsonObject.toString();
                    return info;
                }
            } else {
                return cardNoJs;
            }

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", "get_vip_birth_ticket");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(vipId);
            map.put("values", jsonArray);
            info = Rest.excuteSql(drpList, map);
            JSONArray infArray = new JSONArray(info);
            JSONObject jsonObject = infArray.getJSONObject(0);
            //处理生日券信息
            String result = jsonObject.get("result").toString();
            JSONArray jsonArray2 = new JSONArray(result);
            List<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
            if (jsonArray2.length() > 0) {
                for (int i = 0; i < jsonArray2.length(); i++) {
                    HashMap<String, String> reslutMap = new HashMap<String, String>();
                    JSONArray jsonArray3 = new JSONArray(jsonArray2.get(i).toString());
                    reslutMap.put("coupon_id", jsonArray3.get(0).toString());
                    reslutMap.put("ticketno", jsonArray3.get(1).toString());
                    reslutMap.put("checkno", jsonArray3.get(2).toString());
                    reslutMap.put("money", jsonArray3.get(3).toString());
                    resultList.add(reslutMap);
                }
                jsonObject.remove("result");
                jsonObject.put("result", resultList);
                return jsonObject.toString();
            } else {
                return jsonObject.toString();
            }

        } else {

            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            info = jsonObject.toString();
        }

        return info;

    }

    //会员卡名称
    public String getVipTypeCardInfo(String corp_code, int card_id) throws Exception {
        List<String> drpList = new ArrayList<String>();

        String infos = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();

        if (corp_code.equals("C10016")) {

            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号

            HashMap<String, Object> map = new HashMap<String, Object>();
            //查询
            map.put("table", "C_VIPTYPE");

            String[] columns = new String[]{"id", "NAME"};

            map.put("columns", columns);
            JSONObject expr1 = new JSONObject();
            expr1.put("column", "id");
            expr1.put("condition", card_id);
            map.put("params", expr1);

            String info = Rest.query(drpList, map);

            HashMap<String, Object> maprows = new HashMap<String, Object>();
            HashMap<String, Object> maprow = new HashMap<String, Object>();

            JSONArray jsonArray = new JSONArray(info);
            int code = (Integer) jsonArray.getJSONObject(0).get("code");
            String message = jsonArray.getJSONObject(0).get("message").toString();
            String id1 = jsonArray.getJSONObject(0).get("id").toString();

            if (code == -1) {

                return jsonArray.getJSONObject(0).toString();

            }
            String rows = jsonArray.getJSONObject(0).get("rows").toString();
            JSONArray jsonArray1 = new JSONArray(rows);

            maprows.put("message", message);
            maprows.put("code", code);
            maprows.put("id", id1);

            if (jsonArray1.length() > 0) {
                JSONArray rowjsonArray = jsonArray1.getJSONArray(0);
                for (int i = 0; i < rowjsonArray.length(); i++) {
                    maprow.put(columns[i], rowjsonArray.get(i).toString());
                }
            }
            if (maprow.size() == 0) {
                errorMap.put("message", "未能查找到符合信息");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                infos = jsonObject.toString();
                return infos;
            }
            maprows.put("rows", maprow);
            JSONObject jsonObject = new JSONObject(maprows);
            infos = jsonObject.toString();

        } else {

            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            infos = jsonObject.toString();

        }
        return infos;

    }

    //删除充值单
    public String delPrepaidBill(String corp_code, int id) throws Exception {
        List<String> drpList = new ArrayList<String>();
        String info = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();
        if (corp_code.equals("C10016")) {
            //删除前查询线下记录是否存在,若不存在则返回code为0
            HashMap<String,Object> prepaidMap=new HashMap<String, Object>();
            prepaidMap.put("id",id);
            String prepaidInfo=getPrepaidOrder(corp_code,prepaidMap);
            int prepaidCode= JSON.parseObject(prepaidInfo).getInteger("code");
            if(prepaidCode==-1){
                String prepaidMessage=JSON.parseObject(prepaidInfo).getString("message");
                if(prepaidMessage.equals("未能查找到符合信息")){
                    errorMap.put("message", "未能查找到符合信息");
                    errorMap.put("code", 0);
                    JSONObject jsonObject = new JSONObject(errorMap);
                    info = jsonObject.toString();
                    return  info;
                }else{
                    return prepaidInfo;
                }
            }
            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", "B_VIPMONEY");
            map.put("id", id);
            info = Rest.delObject(drpList, map);
            JSONArray jsonArray = new JSONArray(info);
            info = jsonArray.getJSONObject(0).toString();
        } else {
            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            info = jsonObject.toString();
        }
        return info;
    }

    //删除充值退款单据
    public String delRefundOrder(String corp_code, int id) throws Exception {
        List<String> drpList = new ArrayList<String>();
        String info = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();
        if (corp_code.equals("C10016")) {
            //删除前查询线下记录是否存在,若不存在则返回code为0
            HashMap<String,Object> refundMap=new HashMap<String, Object>();
            refundMap.put("id",id);
            String refundInfo=getRefundOrder(corp_code,refundMap);
            int refundCode= JSON.parseObject(refundInfo).getInteger("code");
            if(refundCode==-1){
                String refundMessage=JSON.parseObject(refundInfo).getString("message");
                if(refundMessage.equals("未能查找到符合信息")){
                    errorMap.put("message", "未能查找到符合信息");
                    errorMap.put("code", 0);
                    JSONObject jsonObject = new JSONObject(errorMap);
                    info = jsonObject.toString();
                    return  info;
                }else{
                    return refundInfo;
                }
            }
            //获取DRP信息
            drpList = connectionDrpSql(corp_code);
            drpList.add(corp_code); //企业编号
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", "B_RET_VIPMONEY");
            map.put("id", id);
            info = Rest.delObject(drpList, map);
            JSONArray jsonArray = new JSONArray(info);
            info = jsonArray.getJSONObject(0).toString();
        } else {
            errorMap.put("message", "抱歉,暂不支持该企业");
            errorMap.put("code", -1);
            JSONObject jsonObject = new JSONObject(errorMap);
            info = jsonObject.toString();
        }
        return info;
    }


    /**
     * @param corp_code
     * @param hashMap
     * @return
     * @throws Exception 根据C_CUSTOMER_ID	经销商，MOBIL 手机号查询
     */
    public String selVipByVipInfo(String corp_code, HashMap<String, Object> hashMap) throws Exception {

        String infos = "";
        HashMap<String, Object> errorMap = new HashMap<String, Object>();
        List<String> drpList = new ArrayList<String>();

        if (corp_code.equals("C10016")) {
            //获取DRP信息
            drpList = connectionDrpSql(corp_code);

            drpList.add(corp_code); //企业编号

            //返回 会员ID 会员类型 会员卡号
            HashMap<String, Object> map = new HashMap<String, Object>();
            //查询
            map.put("table", "C_VIP");
            map.put("columns", new String[]{"id", "C_VIPTYPE_ID", "CARDNO", "MOBIL", "C_STORE_ID"});
            // 设置 params
            JSONObject queryTransParamParam = new JSONObject();
            queryTransParamParam.put("combine", "and");

            JSONObject expr1 = new JSONObject();
            expr1.put("column", "MOBIL");
            expr1.put("condition", hashMap.get("MOBIL"));
            queryTransParamParam.put("expr1", expr1);

            JSONObject expr2 = new JSONObject();
            expr2.put("column", "C_CUSTOMER_ID");
            expr2.put("condition", hashMap.get("C_CUSTOMER_ID"));
            queryTransParamParam.put("expr2", expr2);

            map.put("params", queryTransParamParam);
            String info = Rest.query(drpList, map);

            HashMap<String, Object> maprows = new HashMap<String, Object>();

            System.out.println("============info=========" + info);

            JSONArray jsonArray = new JSONArray(info);
            int code = (Integer) jsonArray.getJSONObject(0).get("code");
            String message = jsonArray.getJSONObject(0).get("message").toString();
            String id1 = jsonArray.getJSONObject(0).get("id").toString();

            if (code == -1) {
                return jsonArray.getJSONObject(0).toString();
            }
            String rows = jsonArray.getJSONObject(0).get("rows").toString();
            JSONArray jsonArray1 = new JSONArray(rows);

            maprows.put("message", message);
            maprows.put("code", code);
            maprows.put("id", id1);

            List<HashMap<String, Object>> vip_list = new ArrayList<HashMap<String, Object>>();
            if (jsonArray1.length() > 0) {
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONArray rowjsonArray = jsonArray1.getJSONArray(i);
                    HashMap<String, Object> maprow = new HashMap<String, Object>();
                    maprow.put("id", rowjsonArray.get(0));
                    //查询会员卡的名称
                    String cardInfo = getVipTypeCardInfo(drpList.get(3).toString(), Integer.parseInt(rowjsonArray.get(1).toString()));
                    JSONObject jsonObject = new JSONObject(cardInfo);
                    String card_name = jsonObject.getJSONObject("rows").get("NAME").toString();
                    maprow.put("C_VIPTYPE_ID", card_name);
                    maprow.put("CARDNO", rowjsonArray.get(2));
                    maprow.put("MOBIL", rowjsonArray.get(3));
                    maprow.put("C_STORE_ID", rowjsonArray.get(4));
                    vip_list.add(maprow);
                }

            } else {
                errorMap.put("message", "查询该VIP信息时,未获取到符合信息");
                errorMap.put("code", -1);
                JSONObject jsonObject = new JSONObject(errorMap);
                infos = jsonObject.toString();
                return infos;
            }
            maprows.put("rows", vip_list);
            JSONObject jsonObject = new JSONObject(maprows);
            infos = jsonObject.toString();
        }
        return infos;
    }


    public List<String> connectionDrpSql(String corp_code) throws Exception {

        String value = "";
        List<String> drpList = new ArrayList<String>();
        //安正
        try {
            ParamConfigure param = paramConfigureService.getParamByKey(CommonValue.CRM_DB_ACCOUNT, Common.IS_ACTIVE_Y);
            List<CorpParam> corpParams = corpParamService.selectByCorpParam(corp_code, String.valueOf(param.getId()), Common.IS_ACTIVE_Y);
            if (corpParams.size() > 0) {
                value = corpParams.get(0).getParam_value();
                String[] paramvalues = value.split("§§");
                drpList.add(paramvalues[1].toString()); //appkey
                drpList.add(paramvalues[2].toString()); //appsecret
                drpList.add(paramvalues[0].toString()); //url

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return drpList;

    }
}
