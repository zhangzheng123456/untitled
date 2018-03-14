package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipActivityDetailMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.service.VipActivityDetailService;
import com.bizvane.ishop.service.VipActivityService;
import com.bizvane.ishop.utils.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nanji on 2017/1/5.
 */
@Service
public class VipActivityDetailServiceImpl implements VipActivityDetailService {
    @Autowired
    VipActivityDetailMapper vipActivityDetailMapper;
    @Autowired
    CorpService corpService;
    @Autowired
    VipActivityService vipActivityService;

    /**邀请注册活动
     [
     {
     "priority": "1", //优先级
     "number_interval": {   //人数区间
     "start": "1",    //开始人数
     "end": "10"   //结束人数
     },
     "invite": "1",   //每邀请人数
     "present": {     //赠送
     "point": "1",  //积分
     "coupon": [  //优惠券
     {
     "coupon_code": "A317200",     //券编号
     "coupon_name": "新开卡礼券200元"  //券名字
     }
     ]，
     "gift":""
     }
     }
     ]
     */
    public PageInfo<VipActivityDetail> selectAllActivityDetail(int page_num, int page_size, String corp_code, String user_code, String search_value) throws Exception {
        List<VipActivityDetail> vipActivityDetails;
        PageHelper.startPage(page_num, page_size);
        vipActivityDetails = vipActivityDetailMapper.selectAllActivityDetail(corp_code, user_code, search_value);
        for (VipActivityDetail vipActivityDetail : vipActivityDetails) {
            vipActivityDetail.setIsactive(CheckUtils.CheckIsactive(vipActivityDetail.getIsactive()));
        }
        PageInfo<VipActivityDetail> page = new PageInfo<VipActivityDetail>(vipActivityDetails);

        return page;
    }


    @Override
    public int delete(String activity_code) throws Exception {
        vipActivityDetailMapper.delDetailConsume(activity_code);
        vipActivityDetailMapper.delDetailAnniversary(activity_code);
        vipActivityDetailMapper.deleteActivityApplyByCode(activity_code);
        return vipActivityDetailMapper.delActivityDetailById(activity_code);
    }

    @Override
    public String insertOrUpdate(HttpServletRequest request,String message, String user_id, String server_name) throws Exception {
        String result = null;
        JSONObject jsonObject = JSONObject.parseObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_code = jsonObject.get("activity_code").toString().trim();
        String activity_type = jsonObject.get("activity_type").toString().trim();//活动类型
        String activity_url = jsonObject.get("activity_url").toString().trim();//推广链接
        VipActivity vipActivity = vipActivityService.getActivityByCode(activity_code);
        String state = vipActivity.getActivity_state();

        VipActivityDetail vipActivityDetail = new VipActivityDetail();
        String recruit = "";//招募
        String sales_no = "";//促销编号
        String festival_start = "";//节日开始日期
        String festival_end = "";//节日结束日期
        String send_coupon_type = "";//发券类型
        String coupon_type = "";//赠送券类型
        String present_point = "";//赠送积分
        String apply_detail = "";
        String join_count = "";//会员参与总次数
        String register_data="";//注册时间
        String batch_no = "";
        Set<String> batchs = new HashSet<String>();
        //会员活动类型
        //招募活动,h5活动,促销,优惠券,线下邀约,节日,
        if (activity_type.equals("recruit")) {
            recruit = jsonObject.get("recruit").toString().trim();
            JSONArray recruitInfo = JSONArray.parseArray(recruit);
            JSONObject obj1 = recruitInfo.getJSONObject(0);
            String vip_card_type_code = obj1.getString("vip_card_type_code");
            String join_threshold = obj1.getString("join_threshold");

            if (recruitInfo.size() > 1) {
                for (int i = 1; i < recruitInfo.size(); i++) {
                    JSONObject obj2 = recruitInfo.getJSONObject(i);
                    String vip_card_type_code1 = obj2.getString("vip_card_type_code");
                    String join_threshold1 = obj2.getString("join_threshold");
                    if (vip_card_type_code.equals(vip_card_type_code1)) {
                        result = "招募级别不能重复";
                        return result;
                    } else if (join_threshold.equals(join_threshold1)) {
                        result = "招募金额不能重复";
                        return result;
                    }
                }
            }
        } else if (activity_type.equals("h5")) {
            activity_url = jsonObject.get("h5_url").toString().trim();
        } else if (activity_type.equals("sales")) {
            sales_no = jsonObject.get("sales_no").toString().trim();
        } else if (activity_type.equals("coupon")) {
            //send_coupon_type优惠券活动发券的类型
            //batch 批量发券
            if (jsonObject.containsKey("send_coupon_type")) {
                String isactive = "N";
                if (state.equals("1"))
                    isactive = "Y";
                send_coupon_type = jsonObject.get("send_coupon_type").toString().trim();
                coupon_type = jsonObject.get("coupon_type").toString().trim();
                if (jsonObject.containsKey("present_point"))
                    present_point = jsonObject.get("present_point").toString().trim();
                if (jsonObject.containsKey("batch_no")){
                    batchs.add(jsonObject.getString("batch_no"));
                    vipActivityDetail.setBatch_no(jsonObject.getString("batch_no"));
                }
                if (send_coupon_type.equals("card")){
                    JSONArray array = JSON.parseArray(coupon_type);
                    for (int i = 0; i < array.size(); i++) {
                        if (array.getJSONObject(i).containsKey("batch_no"))
                            batchs.add(array.getJSONObject(i).getString("batch_no"));
                    }
                }else if (send_coupon_type.equals("consume")){
                    join_count = jsonObject.get("join_count").toString().trim();
                    delDetailConsume(activity_code);
                    String consume_condition = jsonObject.get("consume_condition").toString().trim();
                    JSONArray array = JSON.parseArray(consume_condition);
                    for (int i = 0; i < array.size(); i++) {
                        VipActivityDetailConsume detailConsume = WebUtils.JSON2Bean(array.getJSONObject(i),VipActivityDetailConsume.class);
                        if(detailConsume.getConsume_goods() == null || detailConsume.getConsume_goods().equals("")){
                            detailConsume.setConsume_goods("CORP_ID:"+corp_code);
                        }
                        String discount_start = detailConsume.getDiscount_start();
                        String discount_end = detailConsume.getDiscount_end();
                        if (!discount_start.equals(""))
                            detailConsume.setDiscount_start(NumberUtil.keepPrecision(Double.parseDouble(discount_start)/10));
                        if (!discount_end.equals(""))
                            detailConsume.setDiscount_end(NumberUtil.keepPrecision(Double.parseDouble(discount_end)/10));
                        detailConsume.setActivity_code(activity_code);
                        detailConsume.setCorp_code(corp_code);
                        detailConsume.setCreater(user_id);
                        detailConsume.setCreated_date(Common.DATETIME_FORMAT.format(now));
                        detailConsume.setModifier(user_id);
                        detailConsume.setModified_date(Common.DATETIME_FORMAT.format(now));
                        detailConsume.setIsactive(isactive);
                        insertDetailConsume(detailConsume);

                        if (array.getJSONObject(i).containsKey("batch_no"))
                            batchs.add(array.getJSONObject(i).getString("batch_no"));
                    }
                }else if (send_coupon_type.equals("anniversary")){
                    delDetailAnniversary(activity_code);
                    JSONArray array = JSON.parseArray(coupon_type);
                    for (int i = 0; i < array.size(); i++) {
                        VipActivityDetailAnniversary detailAnniversary = WebUtils.JSON2Bean(array.getJSONObject(i),VipActivityDetailAnniversary.class);
                        detailAnniversary.setActivity_code(activity_code);
                        detailAnniversary.setRun_time_type(array.getJSONObject(i).getString("anniversary_time"));
                        detailAnniversary.setCorp_code(corp_code);
                        detailAnniversary.setCreater(user_id);
                        detailAnniversary.setCreated_date(Common.DATETIME_FORMAT.format(now));
                        detailAnniversary.setModifier(user_id);
                        detailAnniversary.setModified_date(Common.DATETIME_FORMAT.format(now));
                        detailAnniversary.setIsactive(isactive);
                        insertDetailAnniversary(detailAnniversary);
                        if (array.getJSONObject(i).containsKey("batch_no"))
                            batchs.add(array.getJSONObject(i).getString("batch_no"));
                    }
                }
            }
        } else if (activity_type.equals("invite")) {
            String apply_title = jsonObject.get("apply_title").toString().trim();
            if (!apply_title.equals("")){
                String apply_endtime = jsonObject.get("apply_endtime").toString().trim();
                String apply_desc = jsonObject.get("apply_desc").toString().trim();
                String apply_success_tips = jsonObject.get("apply_success_tips").toString().trim();
                String apply_logo = jsonObject.get("apply_logo").toString().trim();
                String apply_allow_vip = jsonObject.get("apply_allow_vip").toString();
                JSONObject apply_detail1 = new JSONObject();
                apply_detail1.put("apply_title",apply_title);
                apply_detail1.put("apply_endtime",apply_endtime);
                apply_detail1.put("apply_desc",apply_desc);
                apply_detail1.put("apply_success_tips",apply_success_tips);
                apply_detail1.put("apply_logo",apply_logo);
                apply_detail1.put("apply_allow_vip",apply_allow_vip);
                apply_detail = apply_detail1.toString();
            }

        } else if (activity_type.equals("festival")) {
            festival_start = jsonObject.get("festival_start").toString().trim();
            festival_end = jsonObject.get("festival_end").toString().trim();
        }else  if(activity_type.equals("register")){
            register_data=jsonObject.getString("register")==null?"":jsonObject.getString("register");
            if (!register_data.equals("")){
                JSONArray register_array = JSONArray.parseArray(register_data);
                for (int i = 0; i < register_array.size(); i++) {
                    JSONObject jsonObject1 = register_array.getJSONObject(i);
                    if (jsonObject1.containsKey("batch_no"))
                        batchs.add(jsonObject1.getString("batch_no"));
                }
            }
        }else if(activity_type.equals("online_apply")){/**线上报名活动**/
            String td_allow="";//是否允许退订
            String td_end_time=""; //退订截至时间
            String present_time="";//优惠赠送时间(奖励发放)(格式:{"type":"","date":""} timely:"及时",timing:"定时")
            String apply_type="";//报名项目类型
            String apply_condition="";//报名资料
            String apply_agreement="";//报名免责协议
            String apply_type_info="";//报名的项目的详情

            present_point=jsonObject.getString("present_point");
            coupon_type=jsonObject.getString("coupon_type");

            batch_no=jsonObject.getString("batch_no");
            td_allow=jsonObject.getString("td_allow");
            td_end_time=jsonObject.getString("td_end_time");
            present_time=jsonObject.getString("present_time");
            apply_type=jsonObject.getString("apply_type");
            apply_condition=jsonObject.getString("apply_condition");
            apply_agreement=jsonObject.getString("apply_agreement")==null?"":jsonObject.getString("apply_agreement");
            apply_type_info=jsonObject.getString("apply_type_info");
            batchs.add(batch_no);//添加到HaseSet中
            vipActivityDetail.setBatch_no(batch_no);
            vipActivityDetail.setTd_allow(td_allow);
            vipActivityDetail.setTd_end_time(td_end_time);
            vipActivityDetail.setPresent_time(present_time);
            vipActivityDetail.setApply_type(apply_type);
            vipActivityDetail.setApply_condition(apply_condition);

            //转换为txt
            if(StringUtils.isNotBlank(apply_agreement)&&!apply_agreement.contains("encodingBizvane")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

                WebConnection webConnection = new WebConnection();
                String path = request.getSession().getServletContext().getRealPath("lupload");
                String filename=sdf.format(new Date())+".txt";
                String filepath = webConnection.switchEncodingToUTF8(path,filename,apply_agreement);
                //上传文件到oss
                String bucketName = "products-image";
                OssUtils ossUtils = new OssUtils();
                String time = "encodingBizvane" + sdf.format(new Date()) + ".txt";
                ossUtils.putObject(bucketName, time, filepath);

                apply_agreement = "http://" + bucketName + ".oss-cn-hangzhou.aliyuncs.com/" + time;
                //删除文件
                File file=new File(filepath);
                if(file.exists()){
                    file.delete();
                }
            }

            if(StringUtils.isNotBlank(apply_agreement)){
                if(apply_agreement.startsWith("https://products-image.oss-cn-hangzhou.aliyuncs.com") || apply_agreement.startsWith("http://products-image.oss-cn-hangzhou.aliyuncs.com")){
                    apply_agreement=apply_agreement.replace("https://products-image.oss-cn-hangzhou.aliyuncs.com","http://img-oss.bizvane.com").replace("http://products-image.oss-cn-hangzhou.aliyuncs.com","http://img-oss.bizvane.com");
                }
            }

            vipActivityDetail.setApply_agreement(apply_agreement);

            JSONArray applyTypeInfoArray=JSON.parseArray(apply_type_info);
            //处理报名项目

            String isactive = "N";
            if (state.equals("1"))
                isactive = "Y";
            //每次新增前删除所有的信息
            deleteActivityApplyByCode(activity_code);
            for (int i = 0; i <applyTypeInfoArray.size() ; i++) {
                JSONObject applyTypeInfoObj=applyTypeInfoArray.getJSONObject(i);
                VipActivityDetailApply vipActivityDetailApply =WebUtils.JSON2Bean(applyTypeInfoObj, VipActivityDetailApply.class);
                vipActivityDetailApply.setActivity_code(activity_code);
                vipActivityDetailApply.setLast_count(vipActivityDetailApply.getLimit_count());
                vipActivityDetailApply.setCorp_code(corp_code);
                vipActivityDetailApply.setCreater(user_id);
                vipActivityDetailApply.setCreated_date(Common.DATETIME_FORMAT.format(now));
                vipActivityDetailApply.setModifier(user_id);
                vipActivityDetailApply.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipActivityDetailApply.setIsactive(isactive);
                insertActivityApply(vipActivityDetailApply);
            }
        }
        for (String batch:batchs) {
            batch_no += batch+",";
        }
        if (batch_no.endsWith(","))
            batch_no = batch_no.substring(0,batch_no.length()-1);
        vipActivity.setBatch_no(batch_no);
        vipActivityService.updateVipActivity(vipActivity);

        vipActivityDetail.setCorp_code(corp_code);
        vipActivityDetail.setActivity_code(activity_code);
        vipActivityDetail.setActivity_type(activity_type);
        vipActivityDetail.setActivity_url(activity_url);
        vipActivityDetail.setRecruit(recruit);
        vipActivityDetail.setSales_no(sales_no);
        vipActivityDetail.setFestival_start(festival_start);
        vipActivityDetail.setFestival_end(festival_end);
        vipActivityDetail.setSend_coupon_type(send_coupon_type);
        vipActivityDetail.setCoupon_type(coupon_type);
        vipActivityDetail.setPresent_point(present_point);
        vipActivityDetail.setJoin_count(join_count);
        vipActivityDetail.setApply_desc(apply_detail);
        vipActivityDetail.setRegister_data(register_data);

        vipActivityDetail.setModifier(user_id);
        vipActivityDetail.setModified_date(Common.DATETIME_FORMAT.format(now));
        vipActivityDetail.setIsactive(Common.IS_ACTIVE_Y);
        int info = 0;
        VipActivityDetail vipActivityd = selActivityDetailByCode(activity_code);
        if (vipActivityd == null) {
            vipActivityDetail.setCreater(user_id);
            vipActivityDetail.setCreated_date(Common.DATETIME_FORMAT.format(now));
            info = vipActivityDetailMapper.insertActivityDetail(vipActivityDetail);
        } else {
            info = vipActivityDetailMapper.updateActivityDetail(vipActivityDetail);
        }
        if (info > 0) {
            result = Common.DATABEAN_CODE_SUCCESS;
        } else {
            result = "失败";
        }
        return result;
    }


    @Override
    public VipActivityDetail selActivityDetailByCode(String activity_code) throws Exception {
        return vipActivityDetailMapper.selActivityDetailByCode(activity_code);
    }

    public int updateDetailConsume(VipActivityDetailConsume detailConsume) throws Exception{
        return vipActivityDetailMapper.updateDetailConsume(detailConsume);
    }

    public int insertDetailConsume(VipActivityDetailConsume detailConsume) throws Exception{
        return vipActivityDetailMapper.insertDetailConsume(detailConsume);
    }

    public int delDetailConsume(String activity_code) throws Exception {
        return vipActivityDetailMapper.delDetailConsume(activity_code);
    }

    public List<VipActivityDetailConsume> selActivityDetailConsume(String activity_code) throws Exception {
        return vipActivityDetailMapper.selActivityDetailConsume(activity_code);
    }

    public int insertDetailAnniversary(VipActivityDetailAnniversary detailAnniversary) throws Exception{
        return vipActivityDetailMapper.insertDetailAnniversary(detailAnniversary);
    }

    public int updateDetailAnniversary(VipActivityDetailAnniversary detailAnniversary) throws Exception{
        return vipActivityDetailMapper.updateDetailAnniversary(detailAnniversary);
    }

    public int delDetailAnniversary(String activity_code) throws Exception {
        return vipActivityDetailMapper.delDetailAnniversary(activity_code);
    }

    public List<VipActivityDetailAnniversary> selActivityDetailAnniversary(String activity_code) throws Exception {
        return vipActivityDetailMapper.selActivityDetailAnniversary(activity_code);
    }

    public List<VipActivityDetailAnniversary> selCorpAnniversary(String corp_code) throws Exception {
        return vipActivityDetailMapper.selCorpAnniversary(corp_code);
    }



    @Override
    //获取活动下的优惠券编号和内容  czy

    public VipActivityDetail selActivityByCodeAndName(String activity_code) throws Exception {
        VipActivityDetail vipActivityDetail = vipActivityDetailMapper.selActivityByCodeAndName(activity_code);
        return vipActivityDetail;
    }


    /**线上报名活动**/
    @Override
    public VipActivityDetailApply selectActivityApplyById(int id) throws Exception {
        VipActivityDetailApply vipActivityDetailApply = vipActivityDetailMapper.selectActivityApplyById(id);
        return vipActivityDetailApply;
    }

    @Override
    public List<VipActivityDetailApply> selectActivityApplyByCode(String activity_code) throws Exception {
        List<VipActivityDetailApply> list = vipActivityDetailMapper.selectActivityApplyByCode(activity_code);
        return list;
    }

    @Override
    public int insertActivityApply(VipActivityDetailApply vipActivityDetailApply) throws Exception {
        int status= vipActivityDetailMapper.insertActivityApply(vipActivityDetailApply);
        return status;
    }

    @Override
    public int updateActivityApply(VipActivityDetailApply vipActivityDetailApply) throws Exception {
        int status= vipActivityDetailMapper.updateActivityApply(vipActivityDetailApply);
        return status;
    }

    @Override
    public int deleteActivityApplyById(int id) throws Exception {
        int status= vipActivityDetailMapper.deleteActivityApplyById(id);
        return status;
    }

    @Override
    public int deleteActivityApplyByCode(String activity_code) throws Exception {
        int status= vipActivityDetailMapper.deleteActivityApplyByCode(activity_code);
        return status;
    }

    @Override
    public int updateDetailApply(VipActivityDetailApply vipActivityDetailApply) throws Exception {
        int status=vipActivityDetailMapper.updateDetailApply(vipActivityDetailApply);
        return status;
    }
}
