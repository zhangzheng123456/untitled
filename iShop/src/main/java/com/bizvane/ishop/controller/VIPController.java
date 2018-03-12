package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.*;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/vip")
public class VIPController {

    private static final Logger logger = Logger.getLogger(VIPController.class);

    String id;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;
    @Autowired
    VipLabelService vipLabelService;
    @Autowired
    VipParamService vipParamService;
    @Autowired
    StoreService storeService;
    @Autowired
    UserService userService;
    @Autowired
    ParamConfigureService paramConfigureService;
    @Autowired
    CorpParamService corpParamService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    VipRulesService vipRulesService;
    @Autowired
    VipGroupService vipGroupService;
    @Autowired
    VipService vipService;
    @Autowired
    CRMInterfaceService crmInterfaceService;
    @Autowired
    private BaseService baseService;
    @Autowired
    VipRecordService vipRecordService;
    @Autowired
    VipCardTypeService vipCardTypeService;

    /**
     * 新增会员信息
     */
    @RequestMapping(value = "/addVip", method = RequestMethod.POST)
    @ResponseBody
    public String addVip(HttpServletRequest request) {
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();

        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param+"====time:"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date()));
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();
            String vip_name = jsonObject.get("vip_name").toString();
            String phone = jsonObject.get("phone").toString();
            String vip_card_type = jsonObject.get("vip_card_type").toString();

            JSONObject result_obj = vipService.addVip(corp_code,jsonObject);
            String result = result_obj.getString("code");
            if (result.equals("0")){


                //----------------行为日志开始------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String operation_user_name = request.getSession().getAttribute("user_name").toString();
                String function = "会员档案_新增会员";
                String action = Common.ACTION_ADD;
                String t_corp_code = corp_code;
                String t_code = result_obj.getString("message");
                String t_name = vip_name;
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code+"&&"+operation_user_name, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("SUCCESS");
            }else {
                String error_message = "新增失败";
                if (result_obj.getString("message") != null && !result_obj.getString("message").equals("")){
                    error_message = result_obj.getString("message");
                }

                if (error_message.contains("no data found") || error_message.contains("手机号已存在")){
                    JSONObject vip_info = vipService.getVip(corp_code,phone,vip_card_type);
                    if (!vip_info.isEmpty()){
                        dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                        dataBean.setId(id);
                        dataBean.setMessage(error_message);
                        dataBean.setRemark(vip_info.toString());
                        return dataBean.getJsonStr();
                    }
                }
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(error_message);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 更新会员信息
     */
    @RequestMapping(value = "/updateVip", method = RequestMethod.POST)
    @ResponseBody
    public String updateVip(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String operator_id = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = "C10000";
            String store_code = jsonObject.get("store_code").toString();


            //---------可修改字段
            String card_no = jsonObject.containsKey("card_no")&&!jsonObject.get("card_no").toString().equals("")?jsonObject.get("card_no").toString():"";
            String phone = jsonObject.containsKey("phone")&&!jsonObject.get("phone").toString().equals("")?jsonObject.get("phone").toString():"";
            String user_name = jsonObject.containsKey("user_name")&&!jsonObject.get("user_name").toString().equals("")?jsonObject.get("user_name").toString():"";
            String user_code = jsonObject.containsKey("user_code")&&!jsonObject.get("user_code").toString().equals("")?jsonObject.get("user_code").toString():"";
            String vip_name = jsonObject.containsKey("vip_name")&&!jsonObject.get("vip_name").toString().equals("")?jsonObject.get("vip_name").toString():"";
            String birthday = jsonObject.containsKey("birthday")&&!jsonObject.get("birthday").toString().equals("")?jsonObject.get("birthday").toString():"";
            String sex = jsonObject.containsKey("sex")&&!jsonObject.get("sex").toString().equals("")?jsonObject.get("sex").toString():"";
            String vip_card_type = jsonObject.containsKey("vip_card_type")&&!jsonObject.get("vip_card_type").toString().equals("")?jsonObject.get("vip_card_type").toString():"";
            String vip_type_code = jsonObject.containsKey("vip_card_type_code")&&!jsonObject.get("vip_card_type_code").toString().equals("")?jsonObject.get("vip_card_type_code").toString():"";
            String store_name = jsonObject.containsKey("store_name")&&!jsonObject.get("store_name").toString().equals("")?jsonObject.get("store_name").toString():"";
            String address = jsonObject.containsKey("address")&&!jsonObject.get("address").toString().equals("")?jsonObject.get("address").toString():"";
            String street = jsonObject.containsKey("street")&&!jsonObject.get("street").toString().equals("")?jsonObject.get("street").toString():"";

            String province = "";
            String city = "";
            String area = "";
            if (!address.isEmpty()){
                String[] addr = address.split("/");
                province = addr[0];
                if (addr.length>1)
                    city = addr[1];
                if (addr.length>2)
                    area = addr[2];
            }

            if (user_name.equals("无")){
                user_name = "";
            }

            DataBox data = iceInterfaceService.getVipExtendInfo(corp_code,vip_id,"");
            if (data.status.toString().equals("SUCCESS")){
                String list=data.data.get("message").value;
                JSONObject list_obj = JSONObject.parseObject(list);
                JSONArray vips_array = list_obj.getJSONArray("message");
                JSONObject vip_json=vips_array.getJSONObject(0);
                String gender = vip_json.getString("sex");

                if (gender.equals("M") || gender.equals("1") || gender.equals("男")){
                    gender = "男";
                }else {
                    gender = "女";
                }
                if (gender.equals(sex))
                    sex = "";
                if (vip_name.equals(vip_json.getString("vip_name")))
                    vip_name = "";
                if (phone.equals(vip_json.getString("vip_phone")))
                    phone = "";
                if (birthday.replace("-","").equals(vip_json.getString("vip_birthday").replace("-","")))
                    birthday = "";
                if (card_no.equals(vip_json.getString("cardno")))
                    card_no = "";
                if (vip_card_type.equals(vip_json.getString("vip_card_type")))
                    vip_card_type = "";
            }

            HashMap<String,Object> vipInfo = new HashMap<String, Object>();
            vipInfo.put("id",vip_id);
            if (!vip_name.equals(""))
                vipInfo.put("VIPNAME",vip_name);
            if (!birthday.equals(""))
                vipInfo.put("BIRTHDAY",birthday.replace("-",""));
            if (!card_no.equals(""))
                vipInfo.put("CARDNO",card_no);
            if (!sex.equals("")){
                String gender = "W";
                if (sex.equals("男"))
                    gender = "M";
                vipInfo.put("SEX",gender);
            }

//            if (!user_name.equals(""))
//                vipInfo.put("SALEHREMP_ID__NAME",user_name); //所属导购
            if (!phone.equals(""))
                vipInfo.put("MOBIL",phone);

            if (!vip_card_type.equals("")){
                vipInfo.put("C_VIPTYPE_ID__NAME",vip_card_type);
            }
            if (!store_name.equals("")){
                Store store = storeService.getStoreByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
                String dealer = store.getDealer();

                vipInfo.put("C_STORE_ID__NAME",store_name); //店铺
                vipInfo.put("C_CUSTOMER_ID__NAME",dealer); //经销商
                vipInfo.put("SALESREP_ID__NAME",user_name); //开卡人
            }

            //更改会员所属导购
            if (!user_name.equals("") && !store_code.equals("") && store_code.equals("无")) {
                DataBox dataBox1 = iceInterfaceService.vipAssort(corp_code, vip_id, user_code, store_code, operator_id);
                logger.info("--------------vipAssort-" + dataBox1.status.toString());

            }
            if (vipInfo.size() > 1) {
                String result = crmInterfaceService.modInfoVip(corp_code, vipInfo);
                JSONObject result_obj = JSONObject.parseObject(result);
                String code = result_obj.getString("code");
                if (code.equals("0")) {
                    String gender = "M";
                    if (sex.equals("女"))
                        gender = "F";
                    //修改会员资料
                    DataBox dataBox = iceInterfaceAPIService.vipProfileBackup(corp_code, vip_id,  card_no, phone, vip_name, birthday, gender,"",province,city,area,street,user_code,"",operator_id,vip_type_code);
                    logger.info("--------------vipProfileBackup-" + dataBox.status.toString());


                    //----------------行为日志开始------------------------------------------
                    String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                    String operation_user_code = request.getSession().getAttribute("user_code").toString();
                    String operation_user_name = request.getSession().getAttribute("user_name").toString();
                    String function = "会员档案_编辑会员";
                    String action = Common.ACTION_UPD;
                    String t_corp_code = corp_code;
                    String t_code = card_no;
                    String t_name = vip_name;
                    String remark = "";
                    if (vipInfo.containsKey("VIPNAME"))
                        remark = remark + "姓名："+vipInfo.get("VIPNAME").toString()+",";
                    if (vipInfo.containsKey("BIRTHDAY"))
                        remark = remark + "出生日期："+vipInfo.get("BIRTHDAY").toString()+",";
                    if (vipInfo.containsKey("SEX"))
                        remark = remark + "性别："+vipInfo.get("SEX").toString()+",";
                    if (vipInfo.containsKey("MOBIL"))
                        remark = remark + "手机号："+vipInfo.get("MOBIL").toString()+",";
                    if (vipInfo.containsKey("CARDNO"))
                        remark = remark + "会员卡号："+vipInfo.get("CARDNO").toString()+",";
                    if (vipInfo.containsKey("C_VIPTYPE_ID__NAME"))
                        remark = remark + "会员类型："+vipInfo.get("C_VIPTYPE_ID__NAME").toString()+",";
                    if (vipInfo.containsKey("SALEHREMP_ID__NAME"))
                        remark = remark + "所属导购："+vipInfo.get("SALEHREMP_ID__NAME").toString()+",";
                    if (remark.endsWith(","))
                        remark = remark.substring(0,remark.length()-1);
                    //"姓名|性别|手机号|生日|VIP类型|所属导购:"
//                    String remark = vip_name + "*" + sex + "*" + phone + "*" + birthday + "*" + vip_card_type + "*" + user_name;
                    baseService.insertUserOperation(operation_corp_code, operation_user_code + "&&" + operation_user_name, function, action, t_corp_code, t_code, t_name, remark);
                    //-------------------行为日志结束-----------------------------------------------------------------------------------
                } else {
                    String error_message = "编辑失败";
                    if (result_obj.getString("message") != null && !result_obj.getString("message").equals("")){
                        error_message = result_obj.getString("message");
                    }
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage(error_message);
                    return dataBean.getJsonStr();
                }
            }else if (!address.equals("") || !street.equals("")){
                DataBox dataBox = iceInterfaceAPIService.vipProfileBackup(corp_code, vip_id, card_no, phone, vip_name, birthday, "","",province,city,area,street,user_code,"",operator_id,vip_type_code);
                logger.info("--------------vipProfileBackup-" + dataBox.status.toString());

                //----------------行为日志开始------------------------------------------
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String operation_user_name = request.getSession().getAttribute("user_name").toString();
                String function = "会员档案_编辑会员";
                String action = Common.ACTION_UPD;
                String t_corp_code = corp_code;
                String t_code = card_no;
                String t_name = vip_name;
                String remark = "";
                if (!address.equals(""))
                    remark = remark + "所属地区："+address+",";
                if (!street.equals(""))
                    remark = remark + "详细地址："+street+",";
                if (remark.endsWith(","))
                    remark = remark.substring(0,remark.length()-1);
                //"姓名|性别|手机号|生日|VIP类型|所属导购:"
//                    String remark = vip_name + "*" + sex + "*" + phone + "*" + birthday + "*" + vip_card_type + "*" + user_name;
                baseService.insertUserOperation(operation_corp_code, operation_user_code + "&&" + operation_user_name, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("SUCCESS");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("编辑失败");
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员信息
     * 相册+标签+备忘
     */
    @RequestMapping(value = "/vipConsumCount", method = RequestMethod.POST)
    @ResponseBody
    public String vipConsumCount(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject obj = new JSONObject();
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();

//            List<VipAlbum> vipAlbumList = new ArrayList<VipAlbum>();
            List<VipLabel> vipLabelList = new ArrayList<VipLabel>();
            String album = "[]";
            String memo = "[]";

            DBCursor dbCursor = vipService.findVipByMongo(corp_code, vip_id);
            if (jsonObject.containsKey("type")) {
                if (jsonObject.get("type").equals("1")) {
                    //相册
                    while (dbCursor.hasNext()) {
                        DBObject vip = dbCursor.next();
                        if (vip.containsField("album")) {
                            album = obj.get("album").toString();
                        }
                    }
                } else if (jsonObject.get("type").equals("2")) {
                    //标签
                   // vipLabelList = vipLabelService.selectLabelByVip(corp_code, vip_id);
                    vipLabelList = vipLabelService.selectLabelByVipToHbase(corp_code, vip_id);

                } else if (jsonObject.get("type").equals("3")) {
                    //备忘
                    while (dbCursor.hasNext()) {
                        DBObject vip = dbCursor.next();
                        if (vip.containsField("memo")) {
                            memo = vip.get("memo").toString();
                        }
                    }
                }
            } else {
                //相册
                while (dbCursor.hasNext()) {
                    DBObject vip = dbCursor.next();
                    if (vip.containsField("album")) {
                        album = vip.get("album").toString();
                    }
                    if (vip.containsField("memo")) {
                        memo = vip.get("memo").toString();
                    }
                }
                //标签
              //  vipLabelList = vipLabelService.selectLabelByVip(corp_code, vip_id);
                  vipLabelList = vipLabelService.selectLabelByVipToHbase(corp_code, vip_id);

            }
            obj.put("Album", album);
            obj.put("Memo", memo);
            obj.put("Label", JSON.toJSONString(vipLabelList));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(obj.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员信息
     * 会员详细资料+扩展信息+备注
     */
    @RequestMapping(value = "/vipInfo", method = RequestMethod.POST)
    @ResponseBody
    public String vipInfo(HttpServletRequest request) {
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();

            String extend_info = "";
            String remark = "";
            String avatar = "";
            JSONArray extend = new JSONArray();
            List<VipParam> vipParams = vipParamService.selectParamByCorp(corp_code);
            String cust_col = "";
            for (int i = 0; i < vipParams.size(); i++) {
                cust_col = cust_col + vipParams.get(i).getParam_name() + ",";
                JSONObject extend_obj = new JSONObject();
                extend_obj.put("name", vipParams.get(i).getParam_desc());
                extend_obj.put("key", vipParams.get(i).getParam_name());
                extend_obj.put("type", vipParams.get(i).getParam_type());
                extend_obj.put("values", vipParams.get(i).getParam_values());
                extend_obj.put("required", vipParams.get(i).getRequired());
                extend_obj.put("class", vipParams.get(i).getParam_class());
                extend_obj.put("show_order", vipParams.get(i).getShow_order());
                extend_obj.put("param_attribute",vipParams.get(i).getParam_attribute());
                extend.add(extend_obj);
            }

            DBCursor dbCursor = vipService.findVipByMongo(corp_code,vip_id);
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                if (obj.containsField("remark"))
                    remark = obj.get("remark").toString();
                if (obj.containsField("avatar"))
                    avatar = obj.get("avatar").toString();
            }

            String month = "12";
            if (corp_code.equals("C10016"))
                month = "3";

            String protect_points = "--";
            DataBox dataBox = iceInterfaceService.getVipInfo(corp_code,vip_id,cust_col,month);
            if (dataBox.status.toString().equals("SUCCESS")){
                String vip_info = dataBox.data.get("message").value;
                JSONObject vip = JSONObject.parseObject(vip_info);
                vip.put("vip_avatar", avatar);
                vip.put("month", month);
                if (vip.containsKey("total_amount") && NumberUtils.isNumber(vip.getString("total_amount"))){
                    vip.put("total_amount", NumberUtil.keepPrecision(vip.getString("total_amount")));
                }
                if (vip.containsKey("consume_times")){
                    vip.put("consume_times", vip.getString("consume_times").split("\\.")[0]);
                }
                //保护积分
                if (vip.containsKey("protect_points")){
                    protect_points = vip.get("protect_points").toString();
                }
                vip.put("protect_points", protect_points);
                //拓展信息
                if (vip.containsKey("custom") || !vip.get("custom").toString().equals("null")){
                    extend_info = vip.get("custom").toString();
                    JSONObject extend_info_obj=JSON.parseObject(extend_info);
                    Set<Map.Entry<String, Object>> entries= extend_info_obj.entrySet();
                    Iterator<Map.Entry<String,Object>> iterator=entries.iterator();

                    JSONObject new_extend_info_obj=new JSONObject();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Object> entry = iterator.next();
                        String param_name = entry.getKey();
                        String value = entry.getValue().toString().trim();
                        List<VipParam> vipParamList = vipParamService.selectByParamName(corp_code, param_name, "Y");
                        if (vipParamList.size() > 0) {
                            VipParam vipParam = vipParamList.get(0);
                            String param_attribute = vipParam.getParam_attribute();
                            if ("sex".equals(param_attribute)) {
                                if (StringUtils.isNotBlank(value)&&"男,M,1".contains(value)) {
                                    value = "男";
                                } else if (StringUtils.isNotBlank(value)&&"女,F,W,Y,0".contains(value)) {
                                    value = "女";
                                }
                            }
                            new_extend_info_obj.put(param_name, value);
                        }
                    }
                    extend_info=new_extend_info_obj.toString();
                    vip.remove("custom");
                }
                if (vip.containsKey("province")&&vip.containsKey("city")&&vip.containsKey("area")&&vip.containsKey("address")){
                    String province = vip.get("province").toString();
                    String city = vip.get("city").toString();
                    String area = vip.get("area").toString();
                    String address = vip.get("address").toString();
                    if (StringUtils.isBlank(province)&& StringUtils.isBlank(city)
                            &&StringUtils.isBlank(area)&&StringUtils.isBlank(address)){
                        String store_code = vip.get("store_code").toString();
                        Store store = storeService.getStoreByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
                        if (store != null && store.getProvince()!=null && store.getStreet() != null){
                            vip.put("province", store.getProvince());
                            vip.put("city", store.getCity());
                            vip.put("area", store.getArea());
                            vip.put("address", store.getStreet());
                        }
                    }
                }
                JSONObject result = new JSONObject();
                result.put("list", vip);
                result.put("extend", extend);
                result.put("extend_info", extend_info);
                result.put("remark", remark);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result.toString());
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("信息错误");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExeclvipPoints", method = RequestMethod.POST)
    @ResponseBody
    public String exportExeclvipPoints(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        String errormessage = "数据异常，导出失败";
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String cardno = jsonObject.get("card_num").toString();

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();

            String goods_code = "";
            String goods_name = "";
            String order_id = "";
            String time_start = "";
            String time_end = "";
            if (jsonObject.containsKey("goods_code")) {
                goods_code = jsonObject.get("goods_code").toString();
            }
            if (jsonObject.containsKey("goods_name")) {
                goods_name = jsonObject.get("goods_name").toString();
            }
            if (jsonObject.containsKey("order_id")) {
                order_id = jsonObject.get("order_id").toString();
            }
            if (jsonObject.containsKey("time_start")) {
                time_start = jsonObject.get("time_start").toString();
            }
            if (jsonObject.containsKey("time_end")) {
                time_end = jsonObject.get("time_end").toString();
            }
            String type = jsonObject.get("type").toString();

            String pathname = "";
            if(type.equals("point")){
                DataBox dataBox_points = iceInterfaceAPIService.getVipPointsList(corp_code,vip_id,"","","0","1000");
                String points = dataBox_points.data.get("message").value;
                JSONObject result_points = JSONObject.parseObject(points);
                JSONArray list_points = result_points.getJSONArray("vip_integral_watercourse");
                String list_p = list_points.toJSONString();
                JSONArray jsonArray=JSONArray.parseArray(list_p);
                List list = WebUtils.Json2List2(jsonArray);
//                LinkedHashMap<String,String> cols =new LinkedHashMap<String,String>();
//                cols.put("point","积分");
//                cols.put("time","日期");
//                cols.put("is_valis","是否生效");
//                cols.put("validdate","生效日期");
//                cols.put("memo","备注");

                LinkedHashMap<String, String> cols = WebUtils.Json2ShowName(jsonObject);

                pathname = OutExeclHelper.OutExecl_vip(jsonArray,list,cols,response,request,"积分流水_"+cardno);
            }else {
                Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
                Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                Data data_goods_code = new Data("good_code", goods_code, ValueType.PARAM);
                Data data_goods_name = new Data("good_name", goods_name, ValueType.PARAM);
                Data data_order_id = new Data("order_id", order_id, ValueType.PARAM);
                Data data_time_start = new Data("time_start", time_start, ValueType.PARAM);
                Data data_time_end = new Data("time_end", time_end, ValueType.PARAM);

                Map datalist = new HashMap<String, Data>();
                datalist.put(data_vip_id.key, data_vip_id);
                datalist.put(data_corp_code.key, data_corp_code);
                datalist.put(data_goods_code.key, data_goods_code);
                datalist.put(data_goods_name.key, data_goods_name);
                datalist.put(data_order_id.key, data_order_id);
                datalist.put(data_time_start.key, data_time_start);
                datalist.put(data_time_end.key, data_time_end);

                DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipMoneyRecord", datalist);
                String result = dataBox.data.get("message").value;
                JSONObject result_wardrobes = JSONObject.parseObject(result);
                JSONArray list_wardrobe = result_wardrobes.getJSONArray("list_wardrobe");
                for (int i = 0; i < list_wardrobe.size(); i++) {
                    JSONObject orders = list_wardrobe.getJSONObject(i);
                    String goods_sug = orders.getString("goods_sug");
                    String goods_price = orders.getString("goods_price");
                    goods_sug = NumberUtil.keepPrecision(goods_sug);
                    goods_price = NumberUtil.keepPrecision(goods_price);

                    orders.put("goods_sug", goods_sug);
                    orders.put("goods_price", goods_price);
                }
                String list_w = list_wardrobe.toJSONString();
                JSONArray jsonArray=JSONArray.parseArray(list_w);
                List list = WebUtils.Json2List2(jsonArray);

                LinkedHashMap<String, String> cols = WebUtils.Json2ShowName(jsonObject);

                pathname = OutExeclHelper.OutExecl_vip(jsonArray,list,cols,response,request,"消费记录_"+cardno);
            }

            JSONObject result2 = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result2.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result2.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }
    /**
     * 会员信息
     * 衣橱+积分
     */
    @RequestMapping(value = "/vipPoints", method = RequestMethod.POST)
    @ResponseBody
    public String vipPoints(HttpServletRequest request) {
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();

            String goods_code = "";
            String goods_name = "";
            String order_id = "";
            String time_start = "";
            String time_end = "";
            String row_num = "";
            if (jsonObject.containsKey("goods_code")) {
                goods_code = jsonObject.get("goods_code").toString();
            }
            if (jsonObject.containsKey("goods_name")) {
                goods_name = jsonObject.get("goods_name").toString();
            }
            if (jsonObject.containsKey("order_id")) {
                order_id = jsonObject.get("order_id").toString();
            }
            if (jsonObject.containsKey("time_start")) {
                time_start = jsonObject.get("time_start").toString();
            }
            if (jsonObject.containsKey("time_end")) {
                time_end = jsonObject.get("time_end").toString();
            }
            if (jsonObject.containsKey("row_num")) {
                row_num = jsonObject.get("row_num").toString();
            }
            Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_goods_code = new Data("good_code", goods_code, ValueType.PARAM);
            Data data_goods_name = new Data("good_name", goods_name, ValueType.PARAM);
            Data data_order_id = new Data("order_id", order_id, ValueType.PARAM);
            Data data_time_start = new Data("time_start", time_start, ValueType.PARAM);
            Data data_time_end = new Data("time_end", time_end, ValueType.PARAM);
            Data data_row_num = new Data("row_num", row_num, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_vip_id.key, data_vip_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_goods_code.key, data_goods_code);
            datalist.put(data_goods_name.key, data_goods_name);
            datalist.put(data_order_id.key, data_order_id);
            datalist.put(data_time_start.key, data_time_start);
            datalist.put(data_time_end.key, data_time_end);
            datalist.put(data_row_num.key, data_row_num);

            JSONObject result_points = new JSONObject();
            JSONObject result_wardrobes = new JSONObject();
            String total_amount = "0.0";
            String consume_times = "0";
            if (jsonObject.containsKey("type")) {
                //获取积分列表
                if (jsonObject.get("type").equals("1")) {
                    DataBox dataBox_points = iceInterfaceAPIService.getVipPointsList(corp_code,vip_id,"","","0","1000");
                    String points = dataBox_points.data.get("message").value;
                    result_points = JSONObject.parseObject(points);
                } else if (jsonObject.get("type").equals("2")) {
                    //获取衣橱列表
                    DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipMoneyRecord", datalist);
//                    logger.info("-------AnalysisVipMoneyRecord:" + dataBox.data.get("message").value);
                    String result = dataBox.data.get("message").value;
                    result_wardrobes = JSONObject.parseObject(result);

                    total_amount = result_wardrobes.getString("total_amount");
                    total_amount = NumberUtil.keepPrecision(total_amount);
                    consume_times = result_wardrobes.getString("consume_times");
                }
            } else {
                //衣橱信息
                DataBox dataBox_wardrobes = iceInterfaceService.iceInterfaceV2("AnalysisVipMoneyRecord", datalist);
//                logger.info("-------AnalysisVipMoneyRecord:" + dataBox_wardrobes.data.get("message").value);
                String wardrobes = dataBox_wardrobes.data.get("message").value;
                result_wardrobes = JSONObject.parseObject(wardrobes);

                DataBox dataBox_points = iceInterfaceAPIService.getVipPointsList(corp_code,vip_id,"","","0","1000");
                String points = dataBox_points.data.get("message").value;
                result_points = JSONObject.parseObject(points);

                total_amount = result_wardrobes.getString("total_amount");
                total_amount = NumberUtil.keepPrecision(total_amount);
                consume_times = result_wardrobes.getString("consume_times");
            }

            result_wardrobes.put("total_amount", total_amount);
            result_wardrobes.put("consume_times", consume_times.split("\\.")[0]);

            if (result_wardrobes.containsKey("list_wardrobe")){
                JSONArray list_wardrobe = result_wardrobes.getJSONArray("list_wardrobe");
                for (int i = 0; i < list_wardrobe.size(); i++) {
                    JSONObject orders = list_wardrobe.getJSONObject(i);
                    if (orders.containsKey("goods_sug")){
                        String goods_sug = orders.getString("goods_sug");
                        goods_sug = NumberUtil.keepPrecision(goods_sug);
                        orders.put("goods_sug", goods_sug);
                    }
                    if (orders.containsKey("goods_price")) {
                        String goods_price = orders.getString("goods_price");
                        goods_price = NumberUtil.keepPrecision(goods_price);
                        orders.put("goods_price", goods_price);
                    }
                }
            }

            if (result_points.containsKey("vip_integral_watercourse")){
                JSONArray list_interger = result_points.getJSONArray("vip_integral_watercourse");
                for (int i = 0; i < list_interger.size(); i++) {
                    JSONObject orders = list_interger.getJSONObject(i);
                    if (orders.containsKey("validdate")){
                        String validdate = orders.getString("validdate");
                        orders.put("validdate", validdate.split(" ")[0]);
                    }
                    if (orders.containsKey("time")){
                        String time = orders.getString("time");
                        orders.put("time", time.split(" ")[0]);
                    }
                }
            }


            JSONObject result = new JSONObject();
            result.put("result_points", result_points);
            result.put("result_consumn", result_wardrobes);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表（目前只支持姓名，手机号，会员卡号）
     * 搜索
     */
    @RequestMapping(value = "/vipSearch", method = RequestMethod.POST)
    @ResponseBody
    public String vipSearch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            String search_value = jsonObject.get("searchValue").toString();
            logger.info("json-----555555555555555555---------corp_code-" + corp_code);

            Map datalist = iceInterfaceService.vipBasicMethod2(page_num, page_size,corp_code, request,sort_key,sort_value);
            Data data_search_value = new Data("phone_or_id", search_value, ValueType.PARAM);
            datalist.put(data_search_value.key, data_search_value);
            DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipSearch3", datalist);
            String result = dataBox.data.get("message").value;

            String field = "";
            if (jsonObject.containsKey("field")){
                field = jsonObject.getString("field");
            }
            if (field.equals("fsend")){
                result = vipService.vipLastSendTime(corp_code,result);
            }else {
                result = vipService.vipAvatar(corp_code,result);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表
     * 筛选
     */
    @RequestMapping(value = "/vipScreen", method = RequestMethod.POST)
    @ResponseBody
    public String vipScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String screens = jsonObject.get("screen").toString();
            JSONArray screen = JSONArray.parseArray(screens);

            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            DataBox dataBox = vipGroupService.vipScreenBySolr(screen,corp_code,page_num,page_size,role_code,brand_code,area_code,store_code,user_code,sort_key,sort_value);
            if (dataBox.status.toString().equals("SUCCESS")){
                String result = dataBox.data.get("message").value;
                String field = "";
                if (jsonObject.containsKey("field")){
                    field = jsonObject.getString("field");
                }
                if (field.equals("fsend")){
                    result = vipService.vipLastSendTime(corp_code,result);
                }else {
                    result = vipService.vipAvatar(corp_code,result);
                }
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result);
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("筛选失败");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表
     * 筛选
     */
    @RequestMapping(value = "/newScreen", method = RequestMethod.POST)
    @ResponseBody
    public String newScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.get("corp_code").toString();
            }
            String page_num = jsonObject.get("pageNumber").toString();
            String page_size = jsonObject.get("pageSize").toString();
            String screens = jsonObject.get("screen").toString();
            JSONArray screen = JSONArray.parseArray(screens);

            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            DataBox dataBox = vipGroupService.vipScreenBySolrNew(screen,corp_code,page_num,page_size,role_code,brand_code,area_code,store_code,user_code,sort_key,sort_value);
            if (dataBox.status.toString().equals("SUCCESS")){
                String result = dataBox.data.get("message").value;
                String field = "";
                if (jsonObject.containsKey("field")){
                    field = jsonObject.getString("field");
                }
                if (field.equals("fsend")){
                    result = vipService.vipLastSendTime(corp_code,result);
                }else {
                    result = vipService.vipAvatar(corp_code,result);
                }
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result);
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("筛选失败");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        String role_code = request.getSession().getAttribute("role_code").toString();
        String user_corp_code = request.getSession().getAttribute("corp_code").toString();
        String brand_code = request.getSession().getAttribute("brand_code").toString();
        String area_code = request.getSession().getAttribute("area_code").toString();
        String store_code = request.getSession().getAttribute("store_code").toString();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String sort_key = "join_date";
            String sort_value = "desc";
            if (jsonObject.containsKey("sort_key")){
                sort_key = jsonObject.getString("sort_key");
                sort_value = jsonObject.getString("sort_value");
            }
            JSONObject jsonObj2 = JSONObject.parseObject(param);
            String output_message = jsonObj2.get("message").toString();
            JSONObject output_message_object = JSONObject.parseObject(output_message);
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(output_message_object);

            String corp_code = user_corp_code;
            if (role_code.equals(Common.ROLE_SYS)) {
                corp_code = "C10000";
            }
            logger.info("json--------------corp_code-" + corp_code);

            String jlist = jsonObject.get("tablemanager").toString();
            JSONArray array = JSONArray.parseArray(jlist);
            String cust_cols = "";
            for (int i = 0; i < array.size(); i++) {
                JSONObject json = array.getJSONObject(i);
                String column_name = json.getString("column_name");
                if (column_name.startsWith("CUST_"))
                    cust_cols = cust_cols + column_name + ",";
                if (column_name.equals("user_name"))
                    cust_cols = cust_cols + "user_code,";
                if (column_name.equals("store_name"))
                    cust_cols = cust_cols + "store_code,";
                if (column_name.equals("act_store"))
                    cust_cols = cust_cols + "act_store,";
            }

            String pathname = "";
            JSONArray jsonArray;
            JSONObject result2 = new JSONObject();
            if (jsonObject.containsKey("ids") && !jsonObject.get("ids").equals("")){
                String ids = jsonObject.get("ids").toString().trim();

                //根据vip_id，获取会员资料(包括拓展信息)
                Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
                Data data_vip_id = new Data("vip_ids", ids, ValueType.PARAM);
                Data data_cust_cols = new Data("cust_cols", cust_cols, ValueType.PARAM);

                Map datalist = new HashMap<String, Data>();
                datalist.put(data_corp_code.key, data_corp_code);
                datalist.put(data_vip_id.key, data_vip_id);
                datalist.put(data_cust_cols.key, data_cust_cols);

                DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetVipExtendInfo", datalist);

                String result = dataBox.data.get("message").value;
                JSONObject object = JSONObject.parseObject(result);
                jsonArray = JSONArray.parseArray(object.get("message").toString());

                List list = WebUtils.Json2List2(jsonArray);
                pathname = OutExeclHelper.OutExecl_vip(jsonArray, list, map, response, request,"会员档案");
                result2.put("path", JSON.toJSONString("lupload/" + pathname));

            }else if (jsonObject.get("searchValue") != null && !jsonObject.get("searchValue").equals("")){
                String page_num = jsonObject.getString("page_num");
                String page_size = jsonObject.getString("page_size");
                String searchValue = jsonObject.get("searchValue").toString().trim();

                Map datalist = iceInterfaceService.vipBasicMethod2(page_num,page_size,corp_code,request,sort_key,sort_value);
                Data data_search_value = new Data("phone_or_id", searchValue, ValueType.PARAM);
                datalist.put(data_search_value.key, data_search_value);
                DataBox dataBox = iceInterfaceService.iceInterfaceV2("AnalysisVipSearch3", datalist);
                String result = dataBox.data.get("message").value;
                JSONObject object = JSONObject.parseObject(result);
                jsonArray = JSONArray.parseArray(object.get("all_vip_list").toString());
                int count = object.getInteger("count");

                int pageNum = Integer.parseInt(page_num);
                int pageSize = Integer.parseInt(page_size);
                int start_line = (pageNum-1) * pageSize + 1;
                int end_line = pageNum*pageSize;
                if (count < pageNum*pageSize)
                    end_line = count;
                List list = WebUtils.Json2List2(jsonArray);
                pathname = OutExeclHelper.OutExecl_vip(jsonArray, list, map, response, request,"会员档案("+start_line+"-"+end_line+")");

                result2.put("path", JSON.toJSONString("lupload/" + pathname));
            }else {
                String page_num = jsonObject.getString("page_num");
                String page_size = jsonObject.getString("page_size");
                String screen_message = jsonObject.get("screen_message").toString();

                String columnName = "";
                String showName = "";
                for (int i = 0; i < array.size(); i++) {
                    JSONObject json = array.getJSONObject(i);
                    columnName += json.get("column_name").toString()+",";
                    showName += json.get("show_name").toString()+",";
                }
                if (columnName.endsWith(","))
                    columnName = columnName.substring(0,columnName.length()-1);
                if (showName.endsWith(","))
                    showName = showName.substring(0,showName.length()-1);
                JSONArray post_array = new JSONArray();
                if(screen_message.equals("")){
                    post_array = vipGroupService.vipScreen2ArrayNew(new JSONArray(),corp_code,role_code,brand_code,area_code,store_code,user_code);
                }else if(!screen_message.equals("")){
                    JSONArray screen = JSON.parseArray(screen_message);
                    post_array = vipGroupService.vipScreen2ArrayNew(screen,corp_code,role_code,brand_code,area_code,store_code,user_code);
                }
                DataBox dataBox = iceInterfaceService.vipScreen2ExeclMethod(page_num,page_size,corp_code,JSON.toJSONString(post_array),sort_key,sort_value,cust_cols,columnName,showName,user_corp_code+"&&"+user_code);
                String result = dataBox.data.get("message").value;
                JSONObject object = JSONObject.parseObject(result);
                pathname = object.get("oss_url").toString();
                result2.put("path", object.get("oss_url").toString());
                result2.put("path_type","oss");
            }
            logger.info("=======cust_cols:"+cust_cols);
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
//            result2.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result2.toString());
        }catch (Ice.MemoryLimitException im){
            System.out.println("===============ice异常========================================");
            errormessage = "导出数据过大,请筛选后导出";
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
            im.printStackTrace();
        }catch (Exception ex) {
            System.out.println("===============总异常========================================");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员信息(头像，拓展信息，备注，相册，备忘)
     * 保存mongodb
     */
    @RequestMapping(value = "/vipSaveInfo", method = RequestMethod.POST)
    @ResponseBody
    public String vipSaveInfo(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        Date now = new Date();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            JSONObject vip = vipService.saveVipInfo(jsonObject,now);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(vip.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员列表，批量分配会员
     */
    @RequestMapping(value = "/changeVipsUser", method = RequestMethod.POST)
    @ResponseBody
    public String changeVipsUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String operator_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id1 = jsonObject.get("vip_id").toString();
            String user_code = jsonObject.get("user_code").toString();
            String user_name = jsonObject.get("user_name").toString();
            String store_code = jsonObject.get("store_code").toString();

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();

            String[] vip_ids = vip_id1.split(",");
            String msg = "0";
            String code = "0";
//            for (int i = 0; i < vip_ids.length; i++) {
//                String vip_id = vip_ids[i];
//                JSONObject result_obj = new JSONObject();
//                if (corp_code.equals("C10016")){
//                    Store store = storeService.getStoreByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
//                    HashMap<String,Object> vipInfo = new HashMap<String, Object>();
//                    vipInfo.put("id",vip_id);
//                    vipInfo.put("SALEHREMP_ID__NAME",user_name);
//                    vipInfo.put("C_STORE_ID__NAME",store.getStore_name());
//
//                    String result = crmInterfaceService.modInfoVip(corp_code,vipInfo);
//                    result_obj = JSONObject.parseObject(result);
//                    code = result_obj.getString("code");
//                    if (!code.equals("0")){
//                        msg = result_obj.getString("message");
//                        break;
//                    }
//                }
//            }
            if (code.equals("0")){
                DataBox dataBox = iceInterfaceAPIService.vipAssort(corp_code, vip_id1,user_code,store_code,operator_id);
                logger.info("-------分配导购" + dataBox.status.toString());
                if (!dataBox.status.toString().equals("SUCCESS")){
                    msg = dataBox.msg;
                }
            }
            if (!msg.equals("0")){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage(msg);
            }else {
                DataBox dataBox = iceInterfaceService.getVipInfo(corp_code,vip_id1);
                String list = dataBox.data.get("message").value;

                JSONObject list_obj = JSONObject.parseObject(list);
                JSONArray vips_array = list_obj.getJSONArray("vip_info");
                for (int i = 0; i < vips_array.size(); i++) {
                    JSONObject vip_info = vips_array.getJSONObject(i);
                    //----------------行为日志开始------------------------------------------
                    /**
                     * mongodb插入用户操作记录
                     * @param operation_corp_code 操作者corp_code
                     * @param operation_user_code 操作者user_code
                     * @param function 功能
                     * @param action 动作
                     * @param corp_code 被操作corp_code
                     * @param code 被操作code
                     * @param name 被操作name
                     * @throws Exception
                     */
                    String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                    String operation_user_code = request.getSession().getAttribute("user_code").toString();
                    String operation_user_name = request.getSession().getAttribute("user_name").toString();
                    String function = "会员档案_编辑会员";
                    String action = Common.ACTION_UPD;
                    String t_corp_code = corp_code;
                    String t_code = vip_info.getString("cardno");
                    String t_name = vip_info.getString("vip_name");
                    String remark = "批量分配导购："+user_name;
                    baseService.insertUserOperation(operation_corp_code, operation_user_code+"&&"+operation_user_name, function, action, t_corp_code, t_code, t_name,remark);
                }

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage("success");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("分配失败");
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表，批量调店
     */
    @RequestMapping(value = "/changeVipsStore", method = RequestMethod.POST)
    @ResponseBody
    public String changeVipsStore(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String operator_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id1 = jsonObject.get("vip_id").toString();
            String store_code = jsonObject.get("store_code").toString();
            String store_name = jsonObject.get("store_name").toString();

            //根据企业编号查找店铺
            Store store = storeService.getStoreByCode(corp_code,store_code,Common.IS_ACTIVE_Y);
            String dealer = store.getDealer();

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();

            String[] vip_ids = vip_id1.split(",");
            String msg = "0";
            String code = "0";
            for (int i = 0; i < vip_ids.length; i++) {
                String vip_id = vip_ids[i];
                JSONObject result_obj = new JSONObject();
                if (corp_code.equals("C10016")){
                    HashMap<String,Object> vipInfo = new HashMap<String, Object>();
                    vipInfo.put("id",vip_id);
                    vipInfo.put("C_STORE_ID__NAME",store_name);
                    vipInfo.put("C_CUSTOMER_ID__NAME",dealer);

                    logger.info("===========changeVipsStore="+vipInfo.toString());
                    //修改店铺信息
                    String result = crmInterfaceService.modInfoVip(corp_code,vipInfo);
                    result_obj = JSONObject.parseObject(result);
                    code = result_obj.getString("code");
                    if (!code.equals("0")){
                        msg = result_obj.getString("message");
                        break;
                    }
                }
            }
            if (code.equals("0")){
                DataBox dataBox = iceInterfaceAPIService.vipTransferStore(corp_code, vip_id1,store_code,operator_id);
                logger.info("-------分配店铺" + dataBox.status.toString());
                if (!dataBox.status.toString().equals("SUCCESS")){
                    msg = dataBox.msg;
                }
            }
            if (!msg.equals("0")){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage(msg);
            }else {

                DataBox dataBox = iceInterfaceService.getVipInfo(corp_code,vip_id1);
                String list = dataBox.data.get("message").value;

                JSONObject list_obj = JSONObject.parseObject(list);
                JSONArray vips_array = list_obj.getJSONArray("vip_info");
                for (int i = 0; i < vips_array.size(); i++) {
                    JSONObject vip_info = vips_array.getJSONObject(i);
                    //----------------行为日志开始------------------------------------------
                    /**
                     * mongodb插入用户操作记录
                     * @param operation_corp_code 操作者corp_code
                     * @param operation_user_code 操作者user_code
                     * @param function 功能
                     * @param action 动作
                     * @param corp_code 被操作corp_code
                     * @param code 被操作code
                     * @param name 被操作name
                     * @throws Exception
                     */
                    String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                    String operation_user_code = request.getSession().getAttribute("user_code").toString();
                    String operation_user_name = request.getSession().getAttribute("user_name").toString();
                    String function = "会员档案_编辑会员";
                    String action = Common.ACTION_UPD;
                    String t_corp_code = corp_code;
                    String t_code = vip_info.getString("cardno");
                    String t_name = vip_info.getString("vip_name");
                    String remark = "批量修改所属店仓："+store_name;
                    baseService.insertUserOperation(operation_corp_code, operation_user_code+"&&"+operation_user_name, function, action, t_corp_code, t_code, t_name,remark);
                }

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage("success");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("分配失败");
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表，批量修改会员卡类型
     */
    @RequestMapping(value = "/changeVipsCardType", method = RequestMethod.POST)
    @ResponseBody
    public String changeVipsCardType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String operator_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id1 = jsonObject.get("vip_id").toString();
            String vip_card_type = jsonObject.get("vip_card_type").toString();
            String vip_card_code = jsonObject.get("vip_card_type_code").toString();

            if (role_code.equals(Common.ROLE_SYS)){
                if (jsonObject.containsKey("corp_code")){
                    corp_code = jsonObject.get("corp_code").toString();
                }else {
                    corp_code = "C10000";
                }
            }

            String[] vip_ids = vip_id1.split(",");
            String msg = "0";
            String code = "0";
            for (int i = 0; i < vip_ids.length; i++) {
                String vip_id = vip_ids[i];
                JSONObject result_obj = new JSONObject();
                if (corp_code.equals("C10016")){
                    HashMap<String,Object> vipInfo = new HashMap<String, Object>();
                    vipInfo.put("id",vip_id);
                    vipInfo.put("C_VIPTYPE_ID__NAME",vip_card_type);

                    logger.info("===========changeVipsCardType="+vipInfo.toString());
                    String result = crmInterfaceService.modInfoVip(corp_code,vipInfo);
                    result_obj = JSONObject.parseObject(result);
                    code = result_obj.getString("code");
                    if (!code.equals("0")){
                        msg = result_obj.getString("message");
                        break;
                    }else {
                        DataBox dataBox = iceInterfaceAPIService.vipProfileBackup(corp_code, vip_id,  "", "", "", "", "","","","","","","","",operator_id,vip_card_code);
                        if (!dataBox.status.toString().equals("SUCCESS")){
                            msg = dataBox.msg;
                            break;
                        }
                    }
                }else {
                    DataBox dataBox = iceInterfaceAPIService.vipProfileBackup(corp_code, vip_id,  "", "", "", "", "","","","","","","","",operator_id,vip_card_code);
                    if (!dataBox.status.toString().equals("SUCCESS")){
                        msg = dataBox.msg;
                        break;
                    }
                }
            }
            if (!msg.equals("0")){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage(msg);
            }else {
                DataBox dataBox = iceInterfaceService.getVipInfo(corp_code,vip_id1);
                String list = dataBox.data.get("message").value;

                JSONObject list_obj = JSONObject.parseObject(list);
                JSONArray vips_array = list_obj.getJSONArray("vip_info");
                for (int i = 0; i < vips_array.size(); i++) {
                    JSONObject vip_info = vips_array.getJSONObject(i);
                    //----------------行为日志开始------------------------------------------
                    /**
                     * mongodb插入用户操作记录
                     * @param operation_corp_code 操作者corp_code
                     * @param operation_user_code 操作者user_code
                     * @param function 功能
                     * @param action 动作
                     * @param corp_code 被操作corp_code
                     * @param code 被操作code
                     * @param name 被操作name
                     * @throws Exception
                     */
                    String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                    String operation_user_code = request.getSession().getAttribute("user_code").toString();
                    String operation_user_name = request.getSession().getAttribute("user_name").toString();
                    String function = "会员档案_编辑会员";
                    String action = Common.ACTION_UPD;
                    String t_corp_code = corp_code;
                    String t_code = vip_info.getString("cardno");
                    String t_name = vip_info.getString("vip_name");
                    String remark = "批量修改会员卡类型："+vip_card_type;
                    baseService.insertUserOperation(operation_corp_code, operation_user_code+"&&"+operation_user_name, function, action, t_corp_code, t_code, t_name,remark);
                }

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage("success");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("分配失败");
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /**
     * MongoDB
     * 会员相册删除
     */
    @RequestMapping(value = "/vipAlbumDelete", method = RequestMethod.POST)
    @ResponseBody
    public String vipAlbumDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String time = jsonObject.get("time").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
//            Map keyMap = new HashMap();
//            keyMap.put("_id", corp_code + vip_id);
//            BasicDBObject queryCondition = new BasicDBObject();
//            queryCondition.putAll(keyMap);
//            DBCursor dbCursor1 = cursor.find(queryCondition);
            DBCursor dbCursor1 = vipService.findVipByMongo(corp_code,vip_id);
            if (dbCursor1.size() > 0) {
                DBObject obj = dbCursor1.next();
                String album = obj.get("album").toString();
                JSONArray array = JSONArray.parseArray(album);
                JSONArray new_array = new JSONArray();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject album_one = array.getJSONObject(i);
                    String time_one = album_one.get("time").toString();
                    if (!time_one.equals(time)) {
                        new_array.add(album_one);
                    }
                }
                DBObject updateCondition = new BasicDBObject();
                updateCondition.put("_id", corp_code + vip_id);
                DBObject updatedValue = new BasicDBObject();
                updatedValue.put("album", new_array);
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                cursor.update(updateCondition, updateSetValue);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * MongoDB
     * 会员备忘删除
     */
    @RequestMapping(value = "/vipMemoDelete", method = RequestMethod.POST)
    @ResponseBody
    public String vipMemoDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String memoid = jsonObject.get("memoid").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
//            Map keyMap = new HashMap();
//            keyMap.put("_id", corp_code + vip_id);
//            BasicDBObject queryCondition = new BasicDBObject();
//            queryCondition.putAll(keyMap);
//            DBCursor dbCursor1 = cursor.find(queryCondition);
            DBCursor dbCursor1 = vipService.findVipByMongo(corp_code,vip_id);
            if (dbCursor1.size() > 0) {
                DBObject obj = dbCursor1.next();
                String album = obj.get("memo").toString();
                JSONArray array = JSONArray.parseArray(album);
                JSONArray new_array = new JSONArray();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject memo_obj = array.getJSONObject(i);
                    String memoid1 = memo_obj.get("memoid").toString();
                    if (!memoid.equals(memoid1)) {
                        new_array.add(memo_obj);
                    }
                }
                DBObject updateCondition = new BasicDBObject();
                updateCondition.put("_id", corp_code + vip_id);
                DBObject updatedValue = new BasicDBObject();
                updatedValue.put("memo", new_array);
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                cursor.update(updateCondition, updateSetValue);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员列表，批量导出会员相册
     */
    @RequestMapping(value = "/exportVipAlbums", method = RequestMethod.POST)
    @ResponseBody
    public String exportVipAlbums(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String vip = jsonObject.get("vip").toString();
            JSONArray vip_array = JSONArray.parseArray(vip);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);

            JSONArray array_album = new JSONArray();
            for (int i = 0; i < vip_array.size(); i++) {
                JSONObject vip_obj_new = vip_array.getJSONObject(i);

                JSONObject vip_obj = vip_array.getJSONObject(i);
                String vip_id = vip_obj.get("vip_id").toString();
                String vip_name = vip_obj.get("vip_name").toString();
                String corp_code = vip_obj.get("corp_code").toString();
                String card_no = vip_obj.get("card_no").toString();
                String phone = vip_obj.get("phone").toString();

                Map keyMap = new HashMap();
                keyMap.put("_id", corp_code + vip_id);
                BasicDBObject queryCondition = new BasicDBObject();
                queryCondition.putAll(keyMap);
                DBCursor dbCursor1 = cursor.find(queryCondition);
                if (dbCursor1.hasNext()) {
                    DBObject obj = dbCursor1.next();

                    String album = "";
                    if (obj.containsField("album"))
                        album = obj.get("album").toString();
                    if (!album.equals("")) {
                        JSONArray array1 = JSONArray.parseArray(album);

                        vip_obj_new.put("vip_name",vip_name);
                        vip_obj_new.put("card_no",card_no);
                        vip_obj_new.put("phone",phone);
                        vip_obj_new.put("album",array1);

                    } else {
                        vip_obj_new.put("vip_name",vip_name);
                        vip_obj_new.put("card_no",card_no);
                        vip_obj_new.put("phone",phone);
                        vip_obj_new.put("album","");
                    }
                } else {
                    vip_obj_new.put("vip_name",vip_name);
                    vip_obj_new.put("card_no",card_no);
                    vip_obj_new.put("phone",phone);
                    vip_obj_new.put("album","");
                }
                array_album.add(vip_obj_new);
            }
            String pathname = OutHtmlHelper.OutHtml(array_album, request);

            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("api/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }


    /**
     * 充值或退款
     */
    @RequestMapping(value = "/recharge", method = RequestMethod.POST)
    @ResponseBody
    public String recharge(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        String user_name = request.getSession().getAttribute("user_name").toString();
        String errormessage = "数据异常，操作失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String status = vipService.recharge(jsonObject,user_code,user_name);
            if (!status.equals(Common.DATABEAN_CODE_SUCCESS)){
                if (status.equals(""))
                    status = "操作失败";
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(status);
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("success");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /**
     * 参数控制
     */
    @RequestMapping(value = "/paramController", method = RequestMethod.GET)
    @ResponseBody
    public String paramController(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
//        String user_code = request.getSession().getAttribute("user_code").toString();
//        String store_code = request.getSession().getAttribute("store_code").toString();

        String errormessage = "数据异常，操作失败";
        try {
            String corp_code = request.getParameter("corp_code").toString();

            JSONObject obj = new JSONObject();
            String is_show_billNo = "Y";
            String is_show_cardNo = "N";

            ParamConfigure param = paramConfigureService.getParamByKey(CommonValue.ADD_VIP_CHECK_BILL, Common.IS_ACTIVE_Y);
            ParamConfigure param1 = paramConfigureService.getParamByKey(CommonValue.ADD_VIP_INPUT_CARDNO, Common.IS_ACTIVE_Y);

            String id = String.valueOf(param.getId());
            String id1 = String.valueOf(param1.getId());

            List<CorpParam> corpParams = corpParamService.selectByCorpParam(corp_code, id, Common.IS_ACTIVE_Y);
            List<CorpParam> corpParams1 = corpParamService.selectByCorpParam(corp_code, id1, Common.IS_ACTIVE_Y);

            if (corpParams.size() > 0 && corpParams.get(0).getParam_value().equals("N"))
                is_show_billNo = "N";

            if (corpParams1.size() > 0 && corpParams1.get(0).getParam_value().equals("Y"))
                is_show_cardNo = "Y";
            obj.put("is_show_billNo", is_show_billNo);
            obj.put("is_show_cardNo", is_show_cardNo);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(obj.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 验证单号
     * 获取余额
     */
    @RequestMapping(value = "/checkBillNo", method = RequestMethod.POST)
    @ResponseBody
    public String checkBillNo(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();

        Date now = new Date();
        String errormessage = "数据异常，操作失败";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String type = jsonObject.get("type").toString();
            String corp_code = jsonObject.get("corp_code").toString();

            String result = "";
            if (type.equals("billNo")){
                String billNo = jsonObject.get("billNo").toString();//单据编号
                HashMap<String,Object> map = new HashMap<String, Object>();
                map.put("DOCNO",billNo);
                result = crmInterfaceService.getPrepaidOrder(corp_code,map);

            }else if (type.equals("balances")){
                String vip_id = jsonObject.get("vip_id").toString();
                result = crmInterfaceService.getBalance(corp_code,vip_id);
            }
            JSONObject result_obj = JSONObject.parseObject(result);
            String code = result_obj.getString("code");
            if (code.equals("0")){
                JSONObject obj_re = result_obj.getJSONObject("rows");
                if (type.equals("balances"))
                    obj_re.put("balance",obj_re.getString("AMOUNT_ACTUAL"));
                if (type.equals("billNo")){
                    obj_re.put("price",obj_re.getString("TOT_AMT_ACTUAL"));
                    obj_re.put("pay_price",obj_re.getString("AMOUNT_ACTUAL"));
                    obj_re.put("discount",obj_re.getString("DISCOUNT"));
                }
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(result_obj.getString("rows"));
            }else {
                String msg = result_obj.getString("message");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员信息，会员升/降级
     */
    @RequestMapping(value = "/changeVipType", method = RequestMethod.POST)
    @ResponseBody
    public String changeVipType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String operator_id = request.getSession().getAttribute("user_code").toString();

        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String vip_id = jsonObject.get("vip_id").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String type = jsonObject.get("type").toString();
            String vip_card_type = jsonObject.get("vip_card_type").toString();

            String change_vip_type = vip_card_type;
            if (type.equals("upgrade")){
                List<VipRules> vipRules = vipRulesService.getVipRulesByType(corp_code,vip_card_type,"",Common.IS_ACTIVE_Y);
                if (vipRules.size() > 0) {
                    change_vip_type = vipRules.get(0).getHigh_vip_type();
                }else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("该会员已是最高级别会员");
                    return dataBean.getJsonStr();
                }
            }else {
                List<VipRules> vipRules = vipRulesService.getVipRulesByType(corp_code,"",vip_card_type,Common.IS_ACTIVE_Y);
                if (vipRules.size() > 0) {
                    change_vip_type = vipRules.get(0).getVip_type();
                }else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("该会员已是最低级别会员");
                    return dataBean.getJsonStr();
                }
            }
            DataBox dataBox = iceInterfaceService.changeVipType(corp_code,vip_id, change_vip_type);
            if (dataBox.status.toString().equals("SUCCESS")){
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage("SUCCESS");
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage("升级失败");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 收藏夹
     */
    @RequestMapping(value = "/avorites", method = RequestMethod.POST)
    @ResponseBody
    public String avoritesMethod(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String open_id = jsonObject.get("open_id").toString();
            String vip_card_no = jsonObject.get("vip_card_no").toString();
            String app_id = jsonObject.get("app_id").toString();

            DataBox dataBox = iceInterfaceService.favorites(corp_code,app_id,open_id,vip_card_no);

            String result = dataBox.data.get("message").value;
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }

    /**
     * 收藏夹
     */
    @RequestMapping(value = "/shoppingCarts", method = RequestMethod.POST)
    @ResponseBody
    public String shoppingCart(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String vip_id = jsonObject.get("vip_id").toString();
            String page_num = jsonObject.get("page_num").toString();
            String page_size = jsonObject.get("page_size").toString();

            JSONObject object = new JSONObject();
            //获取会员优惠券
            DataBox dataBox = iceInterfaceService.shoppingCart(corp_code,vip_id,page_size,page_num);
            if (dataBox.status.toString().equals("SUCCESS")){
                object = JSONObject.parseObject(dataBox.data.get("message").value);
                JSONArray list = object.getJSONArray("list");

                JSONArray new_list = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    JSONObject oo = list.getJSONObject(i);
                    Iterator it = oo.keySet().iterator();

                    JSONObject new_oo = new JSONObject();
                    while (it.hasNext()){
                        String key = it.next().toString();
                        new_oo.put(key.replace(";","_"),oo.getString(key));
                    }
                    new_list.add(new_oo);
                }
                object.put("list",new_list);
            }

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(object.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }

    /**
     * 回访反馈
     */
    @RequestMapping(value = "/visitAndFeedback", method = RequestMethod.POST)
    @ResponseBody
    public String visitAndFeedback(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_back_record);
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String vip_id = jsonObject.get("vip_id").toString();
            int page_num = jsonObject.getInteger("page_num");
            int page_size = jsonObject.getInteger("page_size");

            String lists = jsonObject.get("list").toString();

            JSONArray screen_array = JSONArray.parseArray(lists);
            BasicDBObject queryCondition = vipRecordService.getScreen(screen_array,corp_code);

            BasicDBObject dbObject = new BasicDBObject();
            dbObject.put("corp_code",corp_code);
            dbObject.put("vip_id",vip_id);

            BasicDBObject dbObject1 = new BasicDBObject();
            dbObject1.put("CORP_ID",corp_code);
            dbObject1.put("VIP_ID",vip_id);

            BasicDBList list = new BasicDBList();
            list.add(dbObject);
            list.add(dbObject1);

            queryCondition.put("$or",list);

            logger.info("========visitAndFeedback====queryCondition="+queryCondition.toJson());
            DBCursor dbCursor1 = cursor.find(queryCondition);
            int total = dbCursor1.count();
            int pages = MongoUtils.getPages(dbCursor1, page_size);
            DBCursor dbCursor = MongoUtils.sortAndPage(dbCursor1, page_num, page_size, "created_date", -1);

            JSONObject result = new JSONObject();
            JSONArray array = vipRecordService.transFeedBack(dbCursor,corp_code);
            result.put("list", array);
            result.put("pages", pages);
            result.put("page_number", page_num);
            result.put("page_size", page_size);
            result.put("total", total);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }


    /**
     * 优惠券
     */
    @RequestMapping(value = "/coupon", method = RequestMethod.POST)
    @ResponseBody
    public String couponMethod(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String app_id = jsonObject.get("app_id").toString();
            String open_id = jsonObject.get("open_id").toString();
            String phone = jsonObject.get("phone").toString();
            String type = jsonObject.get("type").toString();
            String vip_id = jsonObject.get("vip_id").toString();

            JSONArray array = new JSONArray();
            String result = array.toString();
            if (!open_id.equals("") && !open_id.equals("null") && !open_id.equals("无") &&
                    !app_id.equals("") && !app_id.equals("null") && !app_id.equals("无")){
                DataBox dataBox = iceInterfaceService.coupon(corp_code,vip_id,app_id,open_id,phone,type);
                if (dataBox.status.toString().equals("SUCCESS"))
                    result = dataBox.data.get("message").value;
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }

    /**
     * 发送验证码
     */
    @RequestMapping(value = "/sendAuthcode", method = RequestMethod.POST)
    @ResponseBody
    public String sendAuthcode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String operator_id = request.getSession().getAttribute("user_code").toString();

        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String vip_id = jsonObject.get("vip_id").toString();
            String vip_name = jsonObject.get("vip_name").toString();

            String phone = jsonObject.get("phone").toString();
            String type = jsonObject.get("type").toString();

            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put("id",vip_id);
            map.put("phone",phone);

            String result = "";
                Random r = new Random();
                Double d = r.nextDouble();
                String authcode = d.toString().substring(3, 3 + 4);
                String text = "";
                if (type.equals("1")){
                    // 预存款密码
                    map.put("PASS_WORD",authcode);
                    result = crmInterfaceService.modfiy_passwordVip(corp_code,map);
                    text = "尊敬的#VIP_NAME# :您本次充值消费密码为#AuthCode#";
                }else if (type.equals("2")){
                    //积分付款密码
                    map.put("INTEGRAL_PASSWORD",authcode);
                    result = crmInterfaceService.modIntegral_passwordVip(corp_code,map);
                    text = "尊敬的#VIP_NAME# :您本次积分付款密码为#AuthCode#";
                }else if (type.equals("3")){
                    //积分付款密码
//                    map.put("INTEGRAL_PASSWORD",authcode);
                    result = crmInterfaceService.getVipBirthTicket(corp_code,map);
                    text = "尊敬的#VIP_NAME#:您本次生日券券号#ticketno#，验证码#checkno#，面额#money#;";
                }
                JSONObject result_obj = JSONObject.parseObject(result);
                String code = result_obj.getString("code");
                if (code.equals("0")){
                    text = text.replace("#VIP_NAME#",vip_name);
                    text = text.replace("#AuthCode#",authcode);
                    if (type.equals("3")){
                        JSONArray message_array = result_obj.getJSONArray("result");
                        if (message_array.size() < 1){
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId("1");
                            dataBean.setMessage("当前VIP不存在有效生日券");
                            return dataBean.getJsonStr();
                        }else {
                            for (int i = 0; i < message_array.size(); i++) {
                                JSONObject message_obj = message_array.getJSONObject(i);
                                text = text.replace("#ticketno#",message_obj.getString("ticketno"));
                                text = text.replace("#checkno#",message_obj.getString("checkno"));
                                text = text.replace("#money#",message_obj.getString("money"));
                                DataBox dataBox = iceInterfaceService.sendSmsV2(corp_code,text,phone,operator_id,"发送生日券信息");

                                if (!dataBox.status.toString().equals("SUCCESS")){
                                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                                    dataBean.setId("1");
                                    dataBean.setMessage(dataBox.msg);
                                    return dataBean.getJsonStr();
                                }
                                //----------------行为日志开始------------------------------------------
                                /**
                                 * mongodb插入用户操作记录
                                 * @param operation_corp_code 操作者corp_code
                                 * @param operation_user_code 操作者user_code
                                 * @param function 功能
                                 * @param action 动作
                                 * @param corp_code 被操作corp_code
                                 * @param code 被操作code
                                 * @param name 被操作name
                                 * @throws Exception
                                 */
                                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                                String operation_user_name = request.getSession().getAttribute("user_name").toString();
                                String function = "会员档案_会员信息";
                                String action = "发送验证码";
                                String t_corp_code = corp_code;
                                String t_code = phone;
                                String t_name = vip_name;
                                String remark = text;
                                baseService.insertUserOperation(operation_corp_code, operation_user_code+"&&"+operation_user_name, function, action, t_corp_code, t_code, t_name,remark);
                                //-------------------行为日志结束-----------------------------------------------------------------------------------

                            }
                        }
                    }else {
                        DataBox dataBox = iceInterfaceService.sendSmsV2(corp_code,text,phone,operator_id,"发送验证码");
                        if (!dataBox.status.toString().equals("SUCCESS")){
                            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                            dataBean.setId("1");
                            dataBean.setMessage(dataBox.msg);
                            return dataBean.getJsonStr();
                        }
                        //----------------行为日志开始------------------------------------------
                        /**
                         * mongodb插入用户操作记录
                         * @param operation_corp_code 操作者corp_code
                         * @param operation_user_code 操作者user_code
                         * @param function 功能
                         * @param action 动作
                         * @param corp_code 被操作corp_code
                         * @param code 被操作code
                         * @param name 被操作name
                         * @throws Exception
                         */
                        String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                        String operation_user_code = request.getSession().getAttribute("user_code").toString();
                        String operation_user_name = request.getSession().getAttribute("user_name").toString();
                        String function = "会员档案_会员信息";
                        String action = "发送验证码";
                        String t_corp_code = corp_code;
                        String t_code = phone;
                        String t_name = vip_name;
                        String remark = text;
                        baseService.insertUserOperation(operation_corp_code, operation_user_code+"&&"+operation_user_name, function, action, t_corp_code, t_code, t_name,remark);
                        //-------------------行为日志结束-----------------------------------------------------------------------------------

                    }
                }
//            }
            logger.info("=========sendAuthcode=result"+result);
            dataBean.setCode(result_obj.getString("code"));
            dataBean.setId("1");
            dataBean.setMessage(result_obj.getString("message"));
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info("发送失败");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 当天没有会员的零售单
     */
    @RequestMapping(value = "/dayNoVipBill", method = RequestMethod.POST)
    @ResponseBody
    public String dayNoVipBill(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            if (role_code.equals(Common.ROLE_SYS))
                corp_code = jsonObject.get("corp_code").toString();
            String store_code = jsonObject.get("store_code").toString();

            String time = Common.DATETIME_FORMAT_DAY_NO.format(new Date());

            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_store_code = new Data("store_id", store_code, ValueType.PARAM);
            Data data_time = new Data("time_id", time, ValueType.PARAM);

            Map datalist = new HashMap<String, Data>();
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_store_code.key, data_store_code);
            datalist.put(data_time.key, data_time);

            DataBox box = iceInterfaceService.iceInterfaceV3("QueryOrderIdNoVip",datalist);

            if (box.status.toString().equals("SUCCESS")){
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(box.data.get("message").value);
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("fail");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportZip", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl_zip(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String url = jsonObject.get("url").toString();
            String name = jsonObject.get("name").toString();

            String[] split = url.split(",");
            String pathname = OutHtmlHelper.OutZip_vip(split,name,request);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            if(pathname.equals("空文件夹")){
                errormessage = "压缩文件为空";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("api/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
            logger.info(errormessage);
        }
        return dataBean.getJsonStr();
    }


    /***
     * 废弃会员
     */
    @RequestMapping(value = "/cancelVip", method = RequestMethod.POST)
    @ResponseBody
    public String cancelVip(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "处理失败";
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String operator_id = request.getSession().getAttribute("user_code").toString();
        try {
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject msg_obj = JSONObject.parseObject(message);
            JSONArray array = msg_obj.getJSONArray("vips");
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);

                String vip_id = jsonObject.get("vip_id").toString();
                String vip_name = jsonObject.get("vip_name").toString();
                String card_no = jsonObject.get("card_no").toString();
                if (role_code.equals(Common.ROLE_SYS))  //角色编号为系统管理员
                    corp_code = "C10000";   //企业编号

                HashMap<String,Object> vipInfo = new HashMap<String, Object>();
                vipInfo.put("id",vip_id);
                vipInfo.put("ISACTIVE","N");

                String result = crmInterfaceService.modInfoVip(corp_code, vipInfo);
                JSONObject result_obj = JSONObject.parseObject(result);
                String code = result_obj.getString("code");
                if (code.equals("0")) {
                    DataBox dataBox = iceInterfaceAPIService.vipProfileBackup(corp_code, vip_id,  "", "", "", "", "","N","","","","","","",operator_id,"");
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage("SUCCESS");

                    //----------------行为日志开始------------------------------------------
                    /**
                     * mongodb插入用户操作记录
                     * @param operation_corp_code 操作者corp_code
                     * @param operation_user_code 操作者user_code
                     * @param function 功能
                     * @param action 动作
                     * @param corp_code 被操作corp_code
                     * @param code 被操作code
                     * @param name 被操作name
                     * @throws Exception
                     */
                    String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                    String operation_user_code = request.getSession().getAttribute("user_code").toString();
                    String operation_user_name = request.getSession().getAttribute("user_name").toString();
                    String function = "会员档案_作废会员";
                    String action = Common.ACTION_UPD;
                    String t_corp_code = corp_code;
                    String t_code = card_no;
                    String t_name = vip_name;
                    String remark = "";
                    baseService.insertUserOperation(operation_corp_code, operation_user_code+"&&"+operation_user_name, function, action, t_corp_code, t_code, t_name,remark);
                    //-------------------行为日志结束-----------------------------------------------------------------------------------

                }else {
                    String error_message = "会员"+card_no+"，处理失败";
                    if (result_obj.getString("message") != null && !result_obj.getString("message").equals("")){
                        error_message = result_obj.getString("message");
                    }
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("会员"+card_no+"，"+error_message);
                    return dataBean.getJsonStr();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
            logger.info(errormessage);
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/allCardForVip",method = RequestMethod.POST)
    @ResponseBody
    public  String  getAllCardForVip(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        try {
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String param = request.getParameter("param");
            logger.info("json---------------" + param);
            JSONObject jsonObj = JSONObject.parseObject(param);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            if(role_code.equals(Common.ROLE_SYS)){
                corp_code="C10000";
            }
            String type = jsonObject.getString("type");
            String vip_phone=jsonObject.getString("vip_phone");
            DataBox dataBox=iceInterfaceService.getAllCardForVip(corp_code,vip_phone,type);
            if (dataBox.status.toString().equals("SUCCESS")){
                String value = dataBox.data.get("message").value;
                JSONObject obj = JSON.parseObject(value);
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(obj.toString());
            }else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("获取数据失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
        }
        return dataBean.getJsonStr();
    }



}
