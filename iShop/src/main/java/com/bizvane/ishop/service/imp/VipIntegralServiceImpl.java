package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipIntegralMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.sun.v1.common.DataBox;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yanyadong on 2017/4/7.
 */
@Service
public class VipIntegralServiceImpl implements VipIntegralService {
    @Autowired
    VipIntegralMapper vipIntegralMapper;
    @Autowired
    VipFsendService vipFsendService;
    @Autowired
    ScheduleJobService scheduleJobService;
    @Autowired
    WxTemplateService wxTemplateService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    CorpService corpService;

    @Override
    public VipIntegral selectIntegralById(int id) throws Exception {
        VipIntegral vipIntegral=vipIntegralMapper.selectIntegralById(id);
        vipIntegral.setApp_name("");
        String app_id = vipIntegral.getApp_id();
        String corp_code = vipIntegral.getCorp_code();
        if (!app_id.isEmpty()){
            CorpWechat corpWechat = corpService.getCorpByAppId(corp_code,app_id);
            if (corpWechat != null)
                vipIntegral.setApp_name(corpWechat.getApp_name());
        }
        return vipIntegral;
    }

    @Override
    public VipIntegral selectIntegralByBillno(String bill_no) throws Exception {
        VipIntegral vipIntegral=vipIntegralMapper.selectIntegralByBillno(bill_no);
        return vipIntegral;
    }

    @Override
    public PageInfo<VipIntegral> selectIntegralAll(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_number,page_size);
        List<VipIntegral> list=vipIntegralMapper.selectIntegralAll(corp_code,search_value);
        PageInfo<VipIntegral> pageInfo=new PageInfo<VipIntegral>(list);
        return pageInfo;
    }

    @Override
    public int deleteIntegralById(int id) throws Exception {
        int status=vipIntegralMapper.deleteIntegralById(id);
        return status;
    }

    @Override
    public String insertVipIntegral(VipIntegral vipIntegral,String user_code,String group_code,String role_code) throws Exception {
        String status = Common.DATABEAN_CODE_SUCCESS;
        String reminds = vipIntegral.getRemind();
        String corp_code = vipIntegral.getCorp_code();
        String target_vips_ = vipIntegral.getTarget_vips_();
        String clear_cycle = vipIntegral.getClear_cycle();
        String bill_no = vipIntegral.getBill_no();
        String app_id = vipIntegral.getApp_id();
        DataBox dataBox= iceInterfaceService.getSolrParam(target_vips_,corp_code,"new");
        if (dataBox.status.toString().equals("SUCCESS")){
            String message=JSONObject.parseObject(dataBox.data.get("message").value).getString("message");
            vipIntegral.setTarget_vips_condition(message);
        }

        JSONArray array = JSONArray.parseArray(reminds);
        if (reminds.contains("wxTemp")){
            List<WxTemplate> template = wxTemplateService.selectTempByAppId(app_id,"",Common.TEMPLATE_NAME_4);
            if (template.size() < 1){
                return "未设置微信模板，无法群发微信模板消息";
            }
        }

        String sms_code = "";
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String send_type = obj.getString("type");
            String time = obj.getString("time");

            VipFsend vipFsend = new VipFsend();
            String sms_code1 = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());

            if (!send_type.equals("sms")){
                send_type = "wxmass";
            }
            vipFsend.setApp_id(app_id);
            vipFsend.setCorp_code(corp_code);
            vipFsend.setSms_code(sms_code1);
            vipFsend.setSend_type(send_type);
            vipFsend.setSend_scope("vip_condition_new");
            vipFsend.setSms_vips(vipIntegral.getTarget_vips());
            vipFsend.setSms_vips_(target_vips_);
            vipFsend.setSend_time(time);
            vipFsend.setContent(obj.getString("content"));
            vipFsend.setCheck_status("Y");
            vipFsend.setActivity_vip_code("INTEGRAL"+bill_no);

            Date now = new Date();
            vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipFsend.setCreater(user_code);
            vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipFsend.setModifier(user_code);
            vipFsend.setIsactive("Y");
            vipFsendService.insertSend(vipFsend,user_code,group_code,role_code);

            sms_code = sms_code + sms_code1 + ",";
        }
        vipIntegral.setSms_code(sms_code);
        vipIntegralMapper.insertVipIntegral(vipIntegral);

        ScheduleJob schedule = new ScheduleJob();
        JSONObject func = new JSONObject();
        func.put("method","vipIntegral");
        func.put("corp_code",corp_code);
        func.put("user_code",user_code);
        func.put("code",bill_no);
        schedule.setJob_name(bill_no);
        schedule.setJob_group(bill_no);
        schedule.setStatus("N");
        schedule.setFunc(func.toString());
        schedule.setCron_expression(clear_cycle);
        scheduleJobService.insert(schedule);
        return status;
    }

    @Override
    public String updateVipIntegral(VipIntegral vipIntegral,String user_code,String group_code,String role_code) throws Exception {

        String status = Common.DATABEAN_CODE_SUCCESS;
        String reminds = vipIntegral.getRemind();
        String corp_code = vipIntegral.getCorp_code();
        String target_vips_ = vipIntegral.getTarget_vips_();
        String bill_no = vipIntegral.getBill_no();
        String sms_code = vipIntegral.getSms_code();

        String app_id = "";
        if (reminds.contains("wxTemp")){
            List<WxTemplate> template = wxTemplateService.selectAllWxTemplate(corp_code,"");
            if (template.size() < 1){
                return "未设置微信模板，无法群发微信模板消息";
            }
            app_id = template.get(0).getApp_id();
        }
        JSONArray array = JSONArray.parseArray(reminds);
        JSONArray new_array = new JSONArray();
        //过滤原有的提醒，保留编辑新增的提醒
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (obj.containsKey("new")) {
                obj.remove("new");
                new_array.add(obj);
            }
        }

        for (int i = 0; i < new_array.size(); i++) {
            JSONObject obj = new_array.getJSONObject(i);
            String send_type = obj.getString("type");
            String time = obj.getString("time");

            VipFsend vipFsend = new VipFsend();
            String sms_code1 = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());

            if (!send_type.equals("sms")){
                send_type = "wxmass";
                vipFsend.setApp_id(app_id);
            }
            vipFsend.setApp_id(app_id);
            vipFsend.setCorp_code(corp_code);
            vipFsend.setSms_code(sms_code1);
            vipFsend.setSend_type(send_type);
            vipFsend.setSend_scope("vip_condition_new");
            vipFsend.setSms_vips(vipIntegral.getTarget_vips());
            vipFsend.setSms_vips_(target_vips_);
            vipFsend.setSend_time(time);
            vipFsend.setContent(obj.getString("content"));
            vipFsend.setCheck_status("Y");
            vipFsend.setActivity_vip_code("INTEGRAL"+bill_no);

            Date now = new Date();
            vipFsend.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipFsend.setCreater(user_code);
            vipFsend.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipFsend.setModifier(user_code);
            vipFsend.setIsactive("Y");
            vipFsendService.insertSend(vipFsend,user_code,group_code,role_code);

            sms_code = sms_code + sms_code1 + ",";
        }
        DataBox dataBox= iceInterfaceService.getSolrParam(target_vips_,corp_code,"new");
        if (dataBox.status.toString().equals("SUCCESS")){
            String message=JSONObject.parseObject(dataBox.data.get("message").value).getString("message");
            vipIntegral.setTarget_vips_condition(message);
        }
        vipIntegral.setSms_code(sms_code);
        vipIntegral.setRemind(JSON.toJSONString(array));
        vipIntegralMapper.updateVipIntegral(vipIntegral);
        return status;
    }

    @Override
    public void updateVipIntegral(VipIntegral vipIntegral) throws Exception {
        vipIntegralMapper.updateVipIntegral(vipIntegral);
        return ;
    }

    @Override
    public PageInfo<VipIntegral> selectIntegralAllScreen(int page_number, int page_size, String corp_code, Map<String, Object> params) throws Exception {
        PageHelper.startPage(page_number,page_size);
        HashMap<String,Object> map=new HashMap<String, Object>();
        Set<String> sets=params.keySet();
        if(sets.contains("recent_clean_time")) {
            JSONObject date = JSONObject.parseObject(params.get("recent_clean_time").toString());
            map.put("recent_clean_start", date.get("start").toString());
            String end = date.get("end").toString();
             if (!end.equals(""))
                 end = end + " 23:59:59";
            map.put("recent_clean_end", end);
            params.remove("recent_clean_time");
        }
        map.put("corp_code",corp_code);
        map.put("map",params);
      List<VipIntegral> vipIntegralList=vipIntegralMapper.selectIntegralAllScreen(map);
        PageInfo<VipIntegral> pageInfo=new PageInfo<VipIntegral>(vipIntegralList);
        return pageInfo;
    }

    @Override
    public VipIntegral selectIntegralByName(String corp_code, String integral_name) throws Exception {

        VipIntegral vipIntegral=vipIntegralMapper.selectIntegralByName(corp_code,integral_name);
        return vipIntegral;
    }

    @Override
    public List<VipIntegral> selectIntegralScreen(String corp_code, Map<String, Object> params) throws Exception {
        HashMap<String,Object> map=new HashMap<String, Object>();
        Set<String> sets=params.keySet();
        if(sets.contains("created_date")) {
            JSONObject date = JSONObject.parseObject(params.get("created_date").toString());
            map.put("created_date_start", date.get("start").toString());
            String end = date.get("end").toString();
//             if (!end.equals(""))
//                 end = end + " 23:59:59";
            map.put("created_date_end", end);
            params.remove("created_date");
        }
        map.put("corp_code",corp_code);
        map.put("map",params);
        List<VipIntegral> vipIntegralList=vipIntegralMapper.selectIntegralAllScreen(map);
        return vipIntegralList;
    }

    @Override
    public PageInfo<VipIntegral> switchList(PageInfo<VipIntegral> list) throws Exception {

        for(int i=0;i<list.getList().size();i++){
            VipIntegral vipIntegral=list.getList().get(i);
            String clear_type=vipIntegral.getClear_type();
            String target_vip_type = vipIntegral.getTarget_vip_type();
            if(clear_type.equals("Y")){
                vipIntegral.setClear_type("每年");
            }else if(clear_type.equals("M")){
                vipIntegral.setClear_type("每月");

            }else if(clear_type.equals("once")){
                vipIntegral.setClear_type("一次性");
            }
            if (target_vip_type.equals("all")){
                vipIntegral.setTarget_vip_type("所有会员");
            }else {
                vipIntegral.setTarget_vip_type("部分会员");
            }
            vipIntegral.setIntegral_duration(vipIntegral.getIntegral_duration()+"月");
        }
        return list;
    }
}
