package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.VipActivityDetailMapper;
import com.bizvane.ishop.entity.VipActivity;
import com.bizvane.ishop.entity.VipActivityDetail;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.service.VipActivityDetailService;
import com.bizvane.ishop.service.VipActivityService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/1/5.
 */
@Service
public class VipActivityDetailServiceImpl implements VipActivityDetailService {
    @Autowired
    VipActivityDetailMapper vipActivityDetailMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private VipActivityService vipActivityService;
    @Override
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
    public PageInfo<VipActivityDetail> selectllScreen(int page_num, int page_size, String corp_code, String user_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("user_code", user_code);

        params.put("map", map);
        PageHelper.startPage(page_num, page_size);
        List<VipActivityDetail> list1 = vipActivityDetailMapper.selectActivityDetailScreen(params);
        for (VipActivityDetail vipActivityDetail : list1) {
            vipActivityDetail.setIsactive(CheckUtils.CheckIsactive(vipActivityDetail.getIsactive()));
        }
        PageInfo<VipActivityDetail> page = new PageInfo<VipActivityDetail>(list1);
        return page;

    }

    @Override
    public int delete(String activity_code) throws Exception {
        return vipActivityDetailMapper.delActivityDetailById(activity_code);
    }


    @Override
    public String insert_new(String message, String user_id) throws Exception {
        String result = null;
        JSONObject jsonObject = JSONObject.parseObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_code = jsonObject.get("activity_code").toString().trim();
        String activity_type = jsonObject.get("activity_type").toString().trim();

        VipActivityDetail vipActivityDetail_new = selActivityDetailByCode(activity_code);
        VipActivityDetail vipActivityDetail=new VipActivityDetail();
        String recruit="";
        String h5_url="";
        String sales_no="";
        String festival_start="";
        String festival_end="";
        String send_coupon_type="";
        String coupon_type="";
        String apply_title="";
        String apply_endtime="";
        String apply_desc="";
        String apply_success_tips="";
        String apply_logo="";
        String apply_qrcode="";
        //会员活动类型
        //招募活动,h5活动,促销,优惠券,线下邀约,节日,
        if(activity_type.equals("recruit")){
            recruit = jsonObject.get("recruit").toString().trim();
        }else if(activity_type.equals("h5")){
            h5_url= jsonObject.get("h5_url").toString().trim();
        }else if(activity_type.equals("sales")){
            sales_no=jsonObject.get("sales_no").toString().trim();
        }else if(activity_type.equals("coupon")){
            if (jsonObject.containsKey("send_coupon_type")){
                send_coupon_type=jsonObject.get("send_coupon_type").toString().trim();
                if(vipActivityDetail_new.getCoupon_type().endsWith(",")) {
                    coupon_type = vipActivityDetail_new.getCoupon_type() + jsonObject.get("coupon_type").toString().trim();
                }else{
                    coupon_type = vipActivityDetail_new.getCoupon_type()+"," + jsonObject.get("coupon_type").toString().trim();
                }
            }
        }else if(activity_type.equals("invite")){
            apply_title=jsonObject.get("apply_title").toString().trim();
            apply_endtime=jsonObject.get("apply_endtime").toString().trim();
            apply_desc=jsonObject.get("apply_desc").toString().trim();
            apply_success_tips=jsonObject.get("apply_success_tips").toString().trim();
            apply_logo=jsonObject.get("apply_logo").toString().trim();
            apply_qrcode=jsonObject.get("apply_qrcode").toString().trim();
        }else if(activity_type.equals("festival")){
            festival_start=jsonObject.get("festival_start").toString().trim();
            festival_end=jsonObject.get("festival_end").toString().trim();
        }
        vipActivityDetail.setCorp_code(corp_code);
        vipActivityDetail.setActivity_code(activity_code);
        vipActivityDetail.setActivity_type(activity_type);
        vipActivityDetail.setRecruit(recruit);
        vipActivityDetail.setH5_url(h5_url);
        vipActivityDetail.setSales_no(sales_no);
        vipActivityDetail.setFestival_start(festival_start);
        vipActivityDetail.setFestival_end(festival_end);
        vipActivityDetail.setSend_coupon_type(send_coupon_type);
        vipActivityDetail.setCoupon_type(coupon_type);
        vipActivityDetail.setApply_qrcode(apply_qrcode);
        vipActivityDetail.setApply_desc(apply_desc);
        vipActivityDetail.setApply_title(apply_title);
        vipActivityDetail.setApply_logo(apply_logo);
        vipActivityDetail.setApply_success_tips(apply_success_tips);
        vipActivityDetail.setApply_endtime(apply_endtime);
        vipActivityDetail.setCreater(user_id);
        vipActivityDetail.setCreated_date(Common.DATETIME_FORMAT.format(now));
        vipActivityDetail.setModifier(user_id);
        vipActivityDetail.setModified_date(Common.DATETIME_FORMAT.format(now));
        int info = 0;
        info = vipActivityDetailMapper.insertActivityDetail(vipActivityDetail);

        if (info > 0) {
            result = Common.DATABEAN_CODE_SUCCESS;
        } else {
            result = "新增失败";

        }
        return result;
    }




    @Override
    public String insert(String message, String user_id) throws Exception {
        String result = null;
        JSONObject jsonObject = JSONObject.parseObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_code = jsonObject.get("activity_code").toString().trim();
        String activity_type = jsonObject.get("activity_type").toString().trim();
        VipActivityDetail vipActivityDetail=new VipActivityDetail();
        String recruit="";
        String h5_url="";
        String sales_no="";
        String festival_start="";
        String festival_end="";
        String send_coupon_type="";
        String coupon_type="";
        String apply_title="";
        String apply_endtime="";
        String apply_desc="";
        String apply_success_tips="";
        String apply_logo="";
        String apply_qrcode="";
        //会员活动类型
        //招募活动,h5活动,促销,优惠券,线下邀约,节日,
        if(activity_type.equals("recruit")){
            recruit = jsonObject.get("recruit").toString().trim();
        }else if(activity_type.equals("h5")){
            h5_url= jsonObject.get("h5_url").toString().trim();
        }else if(activity_type.equals("sales")){
            sales_no=jsonObject.get("sales_no").toString().trim();
        }else if(activity_type.equals("coupon")){
            if (jsonObject.containsKey("send_coupon_type")){
                send_coupon_type=jsonObject.get("send_coupon_type").toString().trim();
                coupon_type=jsonObject.get("coupon_type").toString().trim();
            }
        }else if(activity_type.equals("invite")){
            apply_title=jsonObject.get("apply_title").toString().trim();
            apply_endtime=jsonObject.get("apply_endtime").toString().trim();
            apply_desc=jsonObject.get("apply_desc").toString().trim();
            apply_success_tips=jsonObject.get("apply_success_tips").toString().trim();
            apply_logo=jsonObject.get("apply_logo").toString().trim();
            apply_qrcode=jsonObject.get("apply_qrcode").toString().trim();
        }else if(activity_type.equals("festival")){
            festival_start=jsonObject.get("festival_start").toString().trim();
            festival_end=jsonObject.get("festival_end").toString().trim();
        }
        vipActivityDetail.setCorp_code(corp_code);
        vipActivityDetail.setActivity_code(activity_code);
        vipActivityDetail.setActivity_type(activity_type);
        vipActivityDetail.setRecruit(recruit);
        vipActivityDetail.setH5_url(h5_url);
        vipActivityDetail.setSales_no(sales_no);
        vipActivityDetail.setFestival_start(festival_start);
        vipActivityDetail.setFestival_end(festival_end);
        vipActivityDetail.setSend_coupon_type(send_coupon_type);
        vipActivityDetail.setCoupon_type(coupon_type);
        vipActivityDetail.setApply_qrcode(apply_qrcode);
        vipActivityDetail.setApply_desc(apply_desc);
        vipActivityDetail.setApply_title(apply_title);
        vipActivityDetail.setApply_logo(apply_logo);
        vipActivityDetail.setApply_success_tips(apply_success_tips);
        vipActivityDetail.setApply_endtime(apply_endtime);
        vipActivityDetail.setCreater(user_id);
        vipActivityDetail.setCreated_date(Common.DATETIME_FORMAT.format(now));
        vipActivityDetail.setModifier(user_id);
        vipActivityDetail.setModified_date(Common.DATETIME_FORMAT.format(now));
        int info = 0;
        info = vipActivityDetailMapper.insertActivityDetail(vipActivityDetail);

        if (info > 0) {
            result = Common.DATABEAN_CODE_SUCCESS;
        } else {
            result = "新增失败";

        }
        return result;
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String result = null;
        JSONObject jsonObject = JSONObject.parseObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_code = jsonObject.get("activity_code").toString().trim();
        String activity_type = jsonObject.get("activity_type").toString().trim();

        VipActivityDetail vipActivityDetail=new VipActivityDetail();
        String recruit="";
        String h5_url="";
        String sales_no="";
        String festival_start="";
        String festival_end="";
        String send_coupon_type="";
        String coupon_type="";
        String apply_title="";
        String apply_endtime="";
        String apply_desc="";
        String apply_success_tips="";
        String apply_logo="";
        String apply_qrcode="";
        //会员活动类型
        //招募活动,h5活动,促销,优惠券,线下邀约,节日,
        if(activity_type.equals("recruit")){
            recruit = jsonObject.get("recruit").toString().trim();
        }else if(activity_type.equals("h5")){
            h5_url= jsonObject.get("h5_url").toString().trim();
        }else if(activity_type.equals("sales")){
            sales_no=jsonObject.get("sales_no").toString().trim();
        }else if(activity_type.equals("coupon")){
            if (jsonObject.containsKey("send_coupon_type")){
                send_coupon_type=jsonObject.get("send_coupon_type").toString().trim();
                coupon_type=jsonObject.get("coupon_type").toString().trim();
            }
        }else if(activity_type.equals("invite")){
            apply_title=jsonObject.get("apply_title").toString().trim();
            apply_endtime=jsonObject.get("apply_endtime").toString().trim();
            apply_desc=jsonObject.get("apply_desc").toString().trim();
            apply_success_tips=jsonObject.get("apply_success_tips").toString().trim();
            apply_logo=jsonObject.get("apply_logo").toString().trim();
            apply_qrcode=jsonObject.get("apply_qrcode").toString().trim();
        }else if(activity_type.equals("festival")){
            festival_start=jsonObject.get("festival_start").toString().trim();
            festival_end=jsonObject.get("festival_end").toString().trim();
        }

        vipActivityDetail.setCorp_code(corp_code);
        vipActivityDetail.setActivity_code(activity_code);
        vipActivityDetail.setActivity_type(activity_type);
        vipActivityDetail.setRecruit(recruit);
        vipActivityDetail.setH5_url(h5_url);
        vipActivityDetail.setSales_no(sales_no);
        vipActivityDetail.setFestival_start(festival_start);
        vipActivityDetail.setFestival_end(festival_end);
        vipActivityDetail.setSend_coupon_type(send_coupon_type);
        vipActivityDetail.setCoupon_type(coupon_type);
        vipActivityDetail.setApply_qrcode(apply_qrcode);
        vipActivityDetail.setApply_desc(apply_desc);
        vipActivityDetail.setApply_title(apply_title);
        vipActivityDetail.setApply_logo(apply_logo);
        vipActivityDetail.setApply_success_tips(apply_success_tips);
        vipActivityDetail.setApply_endtime(apply_endtime);
        vipActivityDetail.setCreater(user_id);
        vipActivityDetail.setCreated_date(Common.DATETIME_FORMAT.format(now));
        vipActivityDetail.setModifier(user_id);
        vipActivityDetail.setModified_date(Common.DATETIME_FORMAT.format(now));
        int info = 0;
        info = vipActivityDetailMapper.updateActivityDetail(vipActivityDetail);

        if (info > 0) {
            result = Common.DATABEAN_CODE_SUCCESS;
        } else {
            result = "编辑失败";

        }
        return result;
    }


    @Override
    public VipActivityDetail selectActivityById(int id) throws Exception {
        return vipActivityDetailMapper.selActivityDetailById(id);
    }

    @Override
    public VipActivityDetail selActivityDetailByCode(String activity_code) throws Exception {
        return vipActivityDetailMapper.selActivityDetailByCode(activity_code);
    }

    @Override
    public String creatActivityInviteQrcode(String corp_code, String auth_appid, String activity_code, String user_id) throws Exception {
       //根据活动编号，更新活动详情表
        VipActivityDetail vipActivityDetail = this.selActivityDetailByCode(activity_code);
        String picture = "";
        String rst="";
        String url = CommonValue.wechat_url + "/creatQrcode?auth_appid=" + auth_appid + "&prd=ishop&src=a&emp_id=" + activity_code;
        String result = IshowHttpClient.get(url);
        if (!result.startsWith("{")) {
             rst= Common.DATABEAN_CODE_ERROR;
            return rst;
        }
        JSONObject obj = JSONObject.parseObject(result);
        if (result.contains("errcode")) {
             rst = obj.get("errcode").toString();
            return rst;
        } else {
            Date now = new Date();
            picture = obj.get("picture").toString();
            //String qrcode_url = obj.get("url").toString();
            vipActivityDetail.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipActivityDetail.setApply_qrcode(picture);
            vipActivityDetail.setModifier(user_id);
            int info = 0;
            info = vipActivityDetailMapper.updateActivityDetail(vipActivityDetail);
            if (info>0) {
                rst=Common.DATABEAN_CODE_SUCCESS;
            } else {
                rst=Common.DATABEAN_CODE_ERROR;
            }
        }
        return rst;
    }
}
