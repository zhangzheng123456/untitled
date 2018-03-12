package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.service.IceInterfaceAPIService;
import com.bizvane.sun.api.client.Client;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.Status;
import com.bizvane.sun.v1.common.ValueType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhou on 2016/7/6.
 *
 * @@version
 */

@Service
public class IceInterfaceAPIServiceImpl implements IceInterfaceAPIService {

    String[] arg = new String[]{"--Ice.Config=clientAPI.config"};
    Client apiClient = new Client(arg);


    //api接口（数据类）
    public DataBox iceInterfaceAPI(String method ,Map datalist) throws Exception{
        String methods = "com.bizvane.sun.api.method."+method;
        DataBox dataBox1 = new DataBox("1", Status.ONGOING, "", methods, datalist, null, null, System.currentTimeMillis());
        DataBox dataBox = apiClient.put(dataBox1);

        return dataBox;
    }

    //获取会员积分流水记录
    public DataBox getVipPointsList(String corp_code,String vip_id,String vip_phone, String vip_card_no, String page_num,String page_size) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_vip_phone = new Data("vip_phone", vip_phone, ValueType.PARAM);
        Data data_vip_card_no = new Data("vip_card_no", vip_card_no, ValueType.PARAM);
        Data data_page_num = new Data("page_now", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_vip_phone.key, data_vip_phone);
        datalist.put(data_vip_card_no.key, data_vip_card_no);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);

        DataBox dataBox = iceInterfaceAPI("InfoIntegeral", datalist);
        return dataBox;
    }


    //活动 优惠券分析
    @Override
    public DataBox activityAnalyCoupon(String corp_code, String type_code, String batch_no, String start_time, String end_time,String store_id,String page_num,String page_size,String coupon_list_js,String chart) throws Exception {
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_type_code = new Data("type_code", type_code, ValueType.PARAM);
        Data data_batch_no = new Data("batch_no", batch_no, ValueType.PARAM);
        Data data_start_time= new Data("start_time", start_time, ValueType.PARAM);
        Data data_end_time= new Data("end_time", end_time, ValueType.PARAM);
        Data data_store_id= new Data("store_id", store_id, ValueType.PARAM);
        Data data_page_num=new Data("page_num",page_num,ValueType.PARAM);
        Data data_page_size=new Data("page_size",page_size,ValueType.PARAM);
        Data data_coupon_list_js=new Data("coupon_list_js",coupon_list_js,ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_type_code.key, data_type_code);
        datalist.put(data_batch_no.key, data_batch_no);
        datalist.put(data_start_time.key, data_start_time);
        datalist.put(data_end_time.key, data_end_time);
        datalist.put(data_coupon_list_js.key,data_coupon_list_js);


        DataBox dataBox = new DataBox();
        if (chart.equals("rate")){
            dataBox = iceInterfaceAPI("voucher.VoucherRateV1", datalist);
        }else if (chart.equals("chart")){
            dataBox = iceInterfaceAPI("voucher.VoucherChart", datalist);
        }else if (chart.equals("list")){
            datalist.put(data_store_id.key, data_store_id);
            datalist.put(data_page_num.key, data_page_num);
            datalist.put(data_page_size.key, data_page_size);
            dataBox = iceInterfaceAPI("voucher.VoucherList", datalist);
        }
        return dataBox;
    }

    //活动 业绩占比
    @Override
    public DataBox activityAnalySalesRate(String type, String store_id, String type_code, String batch_no,String start_time, String end_time,String corp_code,String activity_code,String run_mode, String page_now,String page_size,String screen,String chart) throws Exception {
        Data data_type = new Data("type", type, ValueType.PARAM);
        Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
        Data data_type_code = new Data("type_code", type_code, ValueType.PARAM);
        Data data_batch_no= new Data("batch_no", batch_no, ValueType.PARAM);
        Data data_start_time= new Data("start_time", start_time, ValueType.PARAM);
        Data data_end_time=new Data("end_time",end_time,ValueType.PARAM);
        Data data_corp_code=new Data("corp_code",corp_code,ValueType.PARAM);
        Data data_activity_code=new Data("activity_code",activity_code,ValueType.PARAM);
        Data data_page_now=new Data("page_now",page_now,ValueType.PARAM);
        Data data_page_size=new Data("page_size",page_size,ValueType.PARAM);
        Data data_screen=new Data("screen",screen,ValueType.PARAM);
        Data data_run_mode=new Data("run_mode",run_mode,ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_type.key,data_type);
        datalist.put(data_store_id.key, data_store_id);
        datalist.put(data_type_code.key, data_type_code);
        datalist.put(data_batch_no.key, data_batch_no);
        datalist.put(data_start_time.key, data_start_time);
        datalist.put(data_end_time.key, data_end_time);
        datalist.put(data_corp_code.key,data_corp_code);
        datalist.put(data_activity_code.key,data_activity_code);
        datalist.put(data_page_now.key,data_page_now);
        datalist.put(data_page_size.key,data_page_size);
        datalist.put(data_screen.key,data_screen);
        datalist.put(data_run_mode.key,data_run_mode);

        DataBox dataBox = new DataBox();
        if (chart.equals("rate")){
            dataBox = iceInterfaceAPI("voucher.VoucherSalesRate", datalist);
        }else if (chart.equals("chart")){
            dataBox = iceInterfaceAPI("voucher.VoucherSalesChart", datalist);
        }else if (chart.equals("list")){
            dataBox = iceInterfaceAPI("voucher.VoucherSalesList", datalist);
        }
        return dataBox;
    }


    //已结束活动，同期比(去年/活动前期)
    public DataBox lastAchvRate(String corp_code,String activity_code,String start_time,String end_time,String type,String chart) throws Exception{
        Data data_activity_code = new Data("activity_code", activity_code, ValueType.PARAM);
        Data data_start_time = new Data("start_time", start_time, ValueType.PARAM);
        Data data_end_time = new Data("end_time", end_time, ValueType.PARAM);
        Data data_type = new Data("type", type, ValueType.PARAM);
        Data data_corp_code=new Data("corp_code",corp_code,ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_activity_code.key, data_activity_code);
        datalist.put(data_start_time.key, data_start_time);
        datalist.put(data_end_time.key, data_end_time);
        datalist.put(data_type.key, data_type);
        datalist.put(data_corp_code.key,data_corp_code);

        DataBox dataBox;
        if (chart.equals("lastYear")) {
            //去年同期比
            dataBox = iceInterfaceAPI("voucher.ActivityLastYearAnalyContrast", datalist);
        }else  if (chart.equals("beforeTime")) {
            //活动前同时段比
            dataBox = iceInterfaceAPI("voucher.ActivityAgoContrast", datalist);
        }else {
            //件单价，客单价
            dataBox = iceInterfaceAPI("voucher.ActivityPriceOneAnaly", datalist);
        }

        return dataBox;
    }

    //已结束活动，贡献度分析、连带率
    public DataBox vipContribution(String corp_code,String activity_code,String chart,String start_time,String end_time) throws Exception{
        Data data_activity_code = new Data("activity_code", activity_code, ValueType.PARAM);
        Data data_corp_code=new Data("corp_code",corp_code,ValueType.PARAM);
        Data data_start_time = new Data("start_time", start_time, ValueType.PARAM);
        Data data_end_time = new Data("end_time", end_time, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_activity_code.key, data_activity_code);
        datalist.put(data_corp_code.key,data_corp_code);
        datalist.put(data_start_time.key, data_start_time);
        datalist.put(data_end_time.key, data_end_time);
        DataBox dataBox;
        if (chart.equals("vipContribution")) {
            //贡献度分析
            dataBox = iceInterfaceAPI("voucher.ActivityACHVAnaly", datalist);
        }else {
            //连带率
            dataBox = iceInterfaceAPI("voucher.ActivityJointRateAnaly", datalist);
        }
        return dataBox;
    }


    public DataBox getSku(String corp_code,String page_num,String page_size,String screen,String type) throws Exception{
        Data data_param = new Data("param", screen, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
        Data data_type = new Data("type", type, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_param.key, data_param);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_type.key, data_type);

        DataBox dataBox = iceInterfaceAPI("product.ProductSearch",datalist);
        return  dataBox;
    }

    //会员信息（消费次数，消费金额，最高客单价）
    public DataBox VipDetail(String corp_code,String vip_id,String vip_card_no, String vip_phone,String start_time,String end_time) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_vip_card_no = new Data("vip_card_no", vip_card_no, ValueType.PARAM);
        Data data_vip_phone = new Data("vip_phone", vip_phone, ValueType.PARAM);
        Data data_start_time = new Data("start_time", start_time, ValueType.PARAM);
        Data data_end_time = new Data("end_time", end_time, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_vip_card_no.key, data_vip_card_no);
        datalist.put(data_vip_phone.key, data_vip_phone);
        datalist.put(data_start_time.key, data_start_time);
        datalist.put(data_end_time.key, data_end_time);

        DataBox dataBox = iceInterfaceAPI("vip.query.VipDetail", datalist);

        return dataBox;
    }

    public DataBox getSkuAnalysis(String corp_code,String page_num,String page_size,String screen,String store_code,String time_type,String time_value) throws Exception{
        Data data_param = new Data("param", screen, ValueType.PARAM);
        Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
        Data data_time_type = new Data("time_type", time_type, ValueType.PARAM);
        Data data_time_value = new Data("time_value", time_value, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_param.key, data_param);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_time_type.key, data_time_type);
        datalist.put(data_time_value.key, data_time_value);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        DataBox dataBox = iceInterfaceAPI("product.ProductAnalysis",datalist);
        return  dataBox;
    }

    public DataBox getSkuSalesDetail(String corp_code,String page_num,String page_size,String time_type,String time_value,
                                     String sku_id,String store_code,String query_type,String row_key) throws Exception{
        Data data_sku_id = new Data("sku_id", sku_id, ValueType.PARAM);
        Data data_time_type = new Data("time_type", time_type, ValueType.PARAM);
        Data data_time_value = new Data("time_value", time_value, ValueType.PARAM);
        Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);

        Data data_query_type = new Data("query_type", query_type, ValueType.PARAM);
        Data data_row_key = new Data("row_key", row_key, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_sku_id.key, data_sku_id);
        datalist.put(data_time_type.key, data_time_type);
        datalist.put(data_time_value.key, data_time_value);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_query_type.key, data_query_type);
        datalist.put(data_row_key.key, data_row_key);

        DataBox dataBox = iceInterfaceAPI("product.ProductSalesDetail",datalist);
        return  dataBox;
    }

    //根据卡号，获取会员
    public DataBox getVipInfoByCard(String corp_code, String vip_card_no,String vip_phone) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_card_no = new Data("vip_card_no", vip_card_no, ValueType.PARAM);
        Data data_vip_phone = new Data("vip_phone", vip_phone, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_card_no.key, data_vip_card_no);
        datalist.put(data_vip_phone.key, data_vip_phone);

        DataBox dataBox = iceInterfaceAPI("vip.query.VipInfoDetail", datalist);
        return dataBox;
    }


    public DataBox couponSearch(String corp_code,String page_num,String page_size,String screen,String search_value) throws Exception{
        Data data_param = new Data("param", screen, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_page_num = new Data("page_num", page_num, ValueType.PARAM);
        Data data_page_size = new Data("page_size", page_size, ValueType.PARAM);
        Data data_search_value = new Data("search_value", search_value, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_param.key, data_param);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_page_num.key, data_page_num);
        datalist.put(data_page_size.key, data_page_size);
        datalist.put(data_search_value.key, data_search_value);

        DataBox dataBox = iceInterfaceAPI("voucher.CouponSearch",datalist);
        return  dataBox;
    }


    //========================================================================================

    //新增会员
    public DataBox addNewVip(String corp_code,String vip_id,String vip_name,String sex,String birthday,String phone,
                             String vip_card_type,String card_no,String store_code,String user_code,String streets) throws Exception{
        String province = "";
        String city = "";
        String area = "";
        String[] street = streets.split("/");
        if (street.length>=1){
            province = street[0];
        }
        if (street.length>=2){
            city = street[1];
        }
        if (street.length>=3){
            area = street[2];
        }

        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_vip_name = new Data("vip_name", vip_name, ValueType.PARAM);
        Data data_sex = new Data("sex", sex, ValueType.PARAM);
        Data data_phone = new Data("phone", phone, ValueType.PARAM);
        Data data_birthday = new Data("birthday", birthday, ValueType.PARAM);
        Data data_vip_card_type = new Data("vip_card_type", vip_card_type, ValueType.PARAM);
        Data data_card_no = new Data("vip_card_no", card_no, ValueType.PARAM);
        Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
        Data data_user_code = new Data("user_code", user_code, ValueType.PARAM);
        Data data_province = new Data("province", province, ValueType.PARAM);
        Data data_city = new Data("city", city, ValueType.PARAM);
        Data data_area = new Data("area", area, ValueType.PARAM);
        Data data_is_old_vip = new Data("is_old_vip", "N", ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_vip_name.key, data_vip_name);
        datalist.put(data_sex.key, data_sex);
        datalist.put(data_phone.key, data_phone);
        datalist.put(data_birthday.key, data_birthday);
        datalist.put(data_vip_card_type.key, data_vip_card_type);
        datalist.put(data_card_no.key, data_card_no);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_user_code.key, data_user_code);
        datalist.put(data_province.key, data_province);
        datalist.put(data_city.key, data_city);
        datalist.put(data_area.key, data_area);
        datalist.put(data_is_old_vip.key, data_is_old_vip);

        DataBox dataBox = iceInterfaceAPI("AddNewVip", datalist);
        return dataBox;
    }


    //修改会员资料
    public DataBox vipProfileBackup(String corp_code,String vip_id,String new_vip_card_no,String phone,String vip_name,String birthday,String gender,String fr_active,
                                    String province,String city,String area,String address,String user_code,String store_code,String operator_id,String vip_type_code) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_card_no = new Data("new_vip_card_no", new_vip_card_no, ValueType.PARAM);
        Data data_vip_phone = new Data("mobile", phone, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_name = new Data("name", vip_name, ValueType.PARAM);
        Data data_birthday = new Data("birthday", birthday, ValueType.PARAM);
        Data data_gender = new Data("gender", gender, ValueType.PARAM);
        Data data_vip_type_id = new Data("vip_type_code", vip_type_code, ValueType.PARAM);

        Data data_fr_active = new Data("fr_active", fr_active, ValueType.PARAM);
        Data data_province = new Data("province", province, ValueType.PARAM);
        Data data_city = new Data("city", city, ValueType.PARAM);
        Data data_area = new Data("area", area, ValueType.PARAM);
        Data data_address = new Data("address", address, ValueType.PARAM);
        Data data_user_code = new Data("user_code", user_code, ValueType.PARAM);
        Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
        Data data_operator_id = new Data("operator_id", operator_id, ValueType.PARAM);
        Data data_source = new Data("source", "CRM", ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_card_no.key, data_vip_card_no);
        datalist.put(data_vip_phone.key, data_vip_phone);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_name.key, data_name);
        datalist.put(data_birthday.key, data_birthday);
        datalist.put(data_gender.key, data_gender);
        datalist.put(data_vip_type_id.key, data_vip_type_id);
        datalist.put(data_fr_active.key, data_fr_active);
        datalist.put(data_province.key, data_province);
        datalist.put(data_city.key, data_city);
        datalist.put(data_area.key, data_area);
        datalist.put(data_address.key, data_address);
        datalist.put(data_user_code.key, data_user_code);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_operator_id.key, data_operator_id);
        datalist.put(data_source.key, data_source);

        DataBox dataBox = iceInterfaceAPI("VipProfileBackup", datalist);

        return dataBox;
    }

    //更改会员所属导购
    public DataBox vipAssort(String corp_code,String vip_id,String user_code,String store_code,String operator_id) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_user_id = new Data("user_id", user_code, ValueType.PARAM);
        Data data_store_code = new Data("store_id", store_code, ValueType.PARAM);
        Data data_operator_id = new Data("operator_id", operator_id, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_user_id.key, data_user_id);
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_operator_id.key, data_operator_id);

        DataBox dataBox = iceInterfaceAPI("vip.modify.VipAssort", datalist);
        return dataBox;
    }


    //会员批量调店
    public DataBox vipTransferStore(String corp_code,String vip_id,String store_code, String operator_id) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_store_code = new Data("store_code", store_code, ValueType.PARAM);
        Data data_operation_id = new Data("operator_id", operator_id, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_store_code.key, data_store_code);
        datalist.put(data_operation_id.key, data_operation_id);

        DataBox dataBox = iceInterfaceAPI("VipTransferStore", datalist);
        return dataBox;
    }


    /**
     * 积分清理(清理，提醒)
     * @param corp_code 必填
     * @param target_vips 必填
     * @param integral_duration 必填
     * @param bill_no 必填
     * @param operate_type  notice:提醒；clean:清理
     * @param template_id notice必填
     * @param app_id notice必填
     * @param clean_time notice必填
     * @return
     * @throws Exception
     */
    public DataBox vipPointsClean(String corp_code,String target_vips,String integral_duration,String bill_no,String operate_type,String template_id,String app_id,String clean_time,String clean_date) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_target_vips = new Data("target_vips", target_vips, ValueType.PARAM);
        Data data_integral_duration = new Data("integral_duration", integral_duration, ValueType.PARAM);
        Data data_bill_no = new Data("bill_no", bill_no, ValueType.PARAM);
        Data data_operate_type = new Data("operate_type", operate_type, ValueType.PARAM);
        Data data_template_id = new Data("template_id", template_id, ValueType.PARAM);
        Data data_app_id = new Data("app_id", app_id, ValueType.PARAM);
        Data data_clean_time = new Data("clean_time", clean_time, ValueType.PARAM);
        Data data_clean_date = new Data("clean_date", clean_date.replace("-",""), ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_target_vips.key, data_target_vips);
        datalist.put(data_integral_duration.key, data_integral_duration);
        datalist.put(data_bill_no.key, data_bill_no);
        datalist.put(data_operate_type.key, data_operate_type);
        datalist.put(data_template_id.key, data_template_id);
        datalist.put(data_app_id.key, data_app_id);
        datalist.put(data_clean_time.key, data_clean_time);
        datalist.put(data_clean_date.key, data_clean_date);

        DataBox dataBox = iceInterfaceAPI("vip.modify.VipPointsClean", datalist);

        return dataBox;
    }

    //赠送优惠券
    public DataBox sendCoupons(String corp_code,String vip_id,String coupon_code,String coupon_name,String app_id, String open_id,String description,String activity_code,String batch_no,String uid) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_app_id = new Data("app_id", app_id, ValueType.PARAM);
        Data data_couponCode = new Data("couponCode", coupon_code, ValueType.PARAM);
        Data data_couponName = new Data("couponName", coupon_name, ValueType.PARAM);
        Data data_openid = new Data("openid", open_id, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_description = new Data("description", description, ValueType.PARAM);
        Data data_batchno = new Data("batch_no", batch_no, ValueType.PARAM);
        Data data_activity_code = new Data("activity_code", activity_code, ValueType.PARAM);
        Data data_uid = new Data("uid", uid, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_app_id.key, data_app_id);
        datalist.put(data_couponCode.key, data_couponCode);
        datalist.put(data_couponName.key, data_couponName);
        datalist.put(data_openid.key, data_openid);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_description.key, data_description);
        datalist.put(data_batchno.key, data_batchno);
        datalist.put(data_activity_code.key, data_activity_code);
        datalist.put(data_uid.key, data_uid);

        DataBox dataBox = new DataBox();
            dataBox = iceInterfaceAPI("voucher.SendCoupons", datalist);
        return dataBox;
    }

    //赠送积分
    public DataBox sendPoints(String corp_code,String vip_id, String points,String uid) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_points = new Data("points", points, ValueType.PARAM);
        Data data_uid = new Data("uid", uid, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_points.key, data_points);
        datalist.put(data_uid.key, data_uid);

        DataBox dataBox = new DataBox();
            dataBox = iceInterfaceAPI("vip.modify.ModifyVipPoints", datalist);

        return dataBox;
    }

    //纪念日补发券
    public DataBox VipActivityCop(String corp_code,String activity_code, String activity_id,String target_vip,String time_type,String present_point,String coupon_type,String store_id) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_activity_code = new Data("activity_code", activity_code, ValueType.PARAM);
        Data data_activity_id = new Data("activity_id", activity_id, ValueType.PARAM);
        Data data_target_vip = new Data("target_vip", target_vip, ValueType.PARAM);
        Data data_time_type = new Data("time_type", time_type, ValueType.PARAM);
        Data data_present_point = new Data("present_point", present_point, ValueType.PARAM);
        Data data_coupon_type = new Data("coupon_type", coupon_type, ValueType.PARAM);
        Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_activity_code.key, data_activity_code);
        datalist.put(data_activity_id.key, data_activity_id);
        datalist.put(data_target_vip.key, data_target_vip);
        datalist.put(data_time_type.key, data_time_type);
        datalist.put(data_present_point.key, data_present_point);
        datalist.put(data_coupon_type.key, data_coupon_type);
        datalist.put(data_store_id.key, data_store_id);

        DataBox dataBox = iceInterfaceAPI("VipActivityCop", datalist);
        return dataBox;
    }

    //会员参与活动
    public DataBox DisposeActivityData(String corp_code,String vip_id, String activity_code,String run_mode) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_activity_code = new Data("activity_code", activity_code, ValueType.PARAM);
        Data data_run_mode = new Data("run_mode", run_mode, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_activity_code.key, data_activity_code);
        datalist.put(data_run_mode.key, data_run_mode);

        DataBox dataBox = iceInterfaceAPI("DisposeActivityData", datalist);

        return dataBox;
    }

    //会员参与任务
    public DataBox DisposeTaskData(String corp_code,String vip_id, String task_code) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
        Data data_task_code = new Data("task_code", task_code, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_id.key, data_vip_id);
        datalist.put(data_task_code.key, data_task_code);

        DataBox dataBox = iceInterfaceAPI("DisposeTaskData", datalist);

        return dataBox;
    }


    //会员任务
    public DataBox getVipTaskInfo(String corp_code,String file_path, String app_id,String task_code,String type) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_file_path = new Data("file_path", file_path, ValueType.PARAM);
        Data data_app_id = new Data("app_id", app_id, ValueType.PARAM);
        Data data_task_code = new Data("task_code", task_code, ValueType.PARAM);
        Data data_type = new Data("type", type, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_file_path.key, data_file_path);
        datalist.put(data_app_id.key, data_app_id);
        datalist.put(data_task_code.key, data_task_code);
        datalist.put(data_type.key,data_type);
        DataBox dataBox = iceInterfaceAPI("VipTaskInfo", datalist);
        return dataBox;
    }

    //调整积分
    public DataBox adjustVipPoints(String bill_code,String corp_code) throws Exception{
        Data data_bill_code = new Data("bill_code", bill_code, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Map datalist = new HashMap<String, Data>();
        datalist.put(data_bill_code.key, data_bill_code);
        datalist.put(data_corp_code.key, data_corp_code);
        DataBox dataBox = iceInterfaceAPI("VipPointAdjust", datalist);
        return dataBox;
    }

    //批量发券
    public DataBox batchSendCoupons(String corp_code,String app_id,String activity_code,String vip_condition,String coupon_type,String point) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_app_id = new Data("app_id", app_id, ValueType.PARAM);
        Data data_activity_code = new Data("activity_code", activity_code, ValueType.PARAM);
        Data data_vip_condition = new Data("vip_condition", vip_condition, ValueType.PARAM);
        Data data_coupon = new Data("coupon", coupon_type, ValueType.PARAM);
        Data data_point = new Data("point", point, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_app_id.key, data_app_id);
        datalist.put(data_activity_code.key, data_activity_code);
        datalist.put(data_vip_condition.key, data_vip_condition);
        datalist.put(data_coupon.key, data_coupon);
        datalist.put(data_point.key, data_point);

        DataBox dataBox = iceInterfaceAPI("voucher.SendBatchCoupon2", datalist);
        return dataBox;
    }


    //纪念日发券
    public DataBox activityAnniversaryAct(String corp_code,String run_time_type) throws Exception{
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_run_time_type = new Data("run_time_type", run_time_type, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_run_time_type.key, data_run_time_type);

        DataBox dataBox = iceInterfaceAPI("voucher.ActivityAnniversaryAct", datalist);
        return dataBox;
    }

    public  DataBox getVipByCardInfo(String card_param,String corp_code) throws Exception{
        Data data_card_param = new Data("card_param", card_param, ValueType.PARAM);
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);

        Map datalist = new HashMap<String, Data>();
        datalist.put(data_card_param.key, data_card_param);
        datalist.put(data_corp_code.key, data_corp_code);

        DataBox dataBox = iceInterfaceAPI("GetVipFromCard", datalist);
        return dataBox;
    }


}
