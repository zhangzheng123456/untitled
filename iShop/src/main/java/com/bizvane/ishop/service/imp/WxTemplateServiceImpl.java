package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.WxTemplateMapper;
import com.bizvane.ishop.entity.WxTemplate;
import com.bizvane.ishop.entity.WxTemplateContent;
import com.bizvane.ishop.service.WxTemplateContentService;
import com.bizvane.ishop.service.WxTemplateService;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.bizvane.ishop.constant.WxTemplateEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by ZhouZhou on 2016/10/10.
 */

@Service
public class WxTemplateServiceImpl implements WxTemplateService{

    @Autowired
    WxTemplateMapper wxTemplateMapper;
    @Autowired
    WxTemplateContentService wxTemplateContentService;

    private static final Logger logger = Logger.getLogger(WxTemplateServiceImpl.class);

    @Override
    public WxTemplate getTemplateById(int id) throws Exception {
        return wxTemplateMapper.selectById(id);
    }

    public List<WxTemplate> selectAllWxTemplate(String corp_code, String search_value) throws Exception {
        return wxTemplateMapper.selectAllWxTemplate(corp_code,search_value);
    }

    public int insertWxTemplate(WxTemplate template) throws Exception{
        return wxTemplateMapper.insertWxTemplate(template);
    }

    public int updateWxTemplate(WxTemplate template) throws Exception{
        return wxTemplateMapper.updateWxTemplate(template);
    }

    public int deleteWxTemplate(int id) throws Exception{
        return wxTemplateMapper.deleteWxTemplate(id);
    }

    public List<WxTemplate> selectTempByAppId(String app_id,String app_user_name, String template_name) throws Exception {
        return wxTemplateMapper.selectTempByAppId(app_id,app_user_name,template_name);
    }

    public PageInfo<WxTemplate> selectAllWxTemplate(int page_num,int page_size,String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_num,page_size);
        List<WxTemplate> list=wxTemplateMapper.selectAllWxTemplate(corp_code,search_value);
        PageInfo<WxTemplate> pageInfo=new PageInfo<WxTemplate>(list);
        return pageInfo;
    }

    @Override
    public PageInfo<WxTemplate> selectWxTemplateAllScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception{
        List<WxTemplate> wxTemplates;
        PageHelper.startPage(page_number,page_size);
        HashMap<String,Object> param=new HashMap<String, Object>();
        Set<String> sets=map.keySet();
        if(sets.contains("created_date")) {
            JSONObject date = JSONObject.parseObject(map.get("created_date"));
            param.put("created_date_start", date.get("start").toString());
            String end = date.get("end").toString();
             if (!end.equals(""))
                 end = end + " 23:59:59";
            param.put("created_date_end", end);
            map.remove("created_date");
        }
        param.put("corp_code",corp_code);
        param.put("map",map);
        wxTemplates=wxTemplateMapper.selectWxTemplateAllScreen(param);
        PageInfo<WxTemplate> pageInfo=new PageInfo<WxTemplate>(wxTemplates);
        return pageInfo;
    }

    @Override
    public WxTemplate selectByIdAndName(String app_id, String template_id, String template_name) throws Exception {
        WxTemplate wxTemplate=wxTemplateMapper.selectByIdAndName(app_id,template_id,template_name);
        return wxTemplate;
    }

    @Override
    public List<WxTemplate> selectByCorpCode(String corp_code,String app_id) throws Exception {
        List<WxTemplate> list=wxTemplateMapper.selectByCorpCode(corp_code,app_id);
        return list;
    }

    /**
     * 微信模板消息(通用)
     *
     * @param
     * @return
     * @throws Exception
     */
    public String sendTemplateMsg(String app_id,String open_id,String template_id,JSONObject msg_content, String template_url) throws Exception {
        //调用微信模板消息接口
        JSONObject con = new JSONObject();
        JSONObject template_content = new JSONObject();

        template_content.put("content", con);
        template_content.put("app_id", app_id);
        template_content.put("open_id", open_id);
        template_content.put("temp_id", template_id);
        template_content.put("msg_content", msg_content);
        template_content.put("template_url", template_url);

        String result = IshowHttpClient.post(CommonValue.wechat_url+"/message/sendTemplate",template_content);
        return result;
    }

    public JSONObject inviteRegistralNotice(String app_id,String regist_vip_name,String regist_card_no,JSONObject invite_vipInfo) throws Exception{
        List<WxTemplate> wxTemplates = selectTempByAppId(app_id,"",Common.TEMPLATE_NAME_2);
        if (wxTemplates.size() > 0){
            String now = Common.DATETIME_FORMAT_DAY.format(new Date());
            String[] days = now.split("-");
            JSONObject result = new JSONObject();
            String corp_code = wxTemplates.get(0).getCorp_code();
            String template_id = wxTemplates.get(0).getTemplate_id();
            JSONObject msg_content = new JSONObject();
            String template_url = CommonValue.wei_task_url.replace("@APPID@",app_id);

            List<WxTemplateContent> contents = wxTemplateContentService.selectContentById(app_id,Common.TEMPLATE_NAME_2,"","");
            if (contents.size() > 0){
                String first = contents.get(0).getTemplate_first();
                String remark = contents.get(0).getTemplate_remark();
                template_url = contents.get(0).getTemplate_url();
                String store_name = invite_vipInfo.getString("store_name");
                if (store_name != null && store_name.equals("无"))
                    store_name = "未知";
                first = first.replace("\"#name#\"",invite_vipInfo.getString("vip_name"));
                first = first.replace("\"#birthday#\"",invite_vipInfo.getString("vip_birthday"));
                first = first.replace("\"#join_time#\"",invite_vipInfo.getString("join_date"));
                first = first.replace("\"#sex#\"",invite_vipInfo.getString("sex"));
                first = first.replace("\"#store#\"",store_name);

                remark = remark.replace("\"#name#\"",invite_vipInfo.getString("vip_name"));
                remark = remark.replace("\"#birthday#\"",invite_vipInfo.getString("vip_birthday"));
                remark = remark.replace("\"#join_time#\"",invite_vipInfo.getString("join_date"));
                remark = remark.replace("\"#sex#\"",invite_vipInfo.getString("sex"));
                remark = remark.replace("\"#store#\"",store_name);
                msg_content.put("first",first);
                msg_content.put("keyword1",regist_vip_name);
                msg_content.put("keyword2",regist_card_no);
                msg_content.put("keyword3",days[0]+"年"+days[1]+"月"+days[2]+"日");
                msg_content.put("remark",remark);

            }else if (corp_code.equals("C10238")){
                msg_content.put("first","亲爱的ECCO会员，您已成功邀请好友入会，请查看您获得的积分，感谢您对ECCO的支持。");
                msg_content.put("keyword1",regist_vip_name);
                msg_content.put("keyword2",regist_card_no);
                msg_content.put("keyword3",days[0]+"年"+days[1]+"月"+days[2]+"日");
                msg_content.put("remark","完成更多会员任务获得奖励");
            }else {
                msg_content.put("first","亲爱的会员，您已成功邀请好友入会，请查看您获得的奖励，感谢您的支持。");
                msg_content.put("keyword1",regist_vip_name);
                msg_content.put("keyword2",regist_card_no);
                msg_content.put("keyword3",days[0]+"年"+days[1]+"月"+days[2]+"日");
                msg_content.put("remark","完成更多会员任务获得奖励");
            }


            result.put("template_id",template_id);
            result.put("template_content",msg_content);
            result.put("template_url",template_url);

            return result;
        }else {
            return null;
        }
    }

    public JSONObject newVipTaskNotice(String app_id,String task_title) throws Exception{
        List<WxTemplate> wxTemplates = selectTempByAppId(app_id,"",Common.TEMPLATE_NAME_3);
        if (wxTemplates.size() > 0){
            JSONObject result = new JSONObject();
            JSONObject msg_content = new JSONObject();
            String template_url = CommonValue.wei_task_url.replace("@APPID@",app_id);


            List<WxTemplateContent> contents = wxTemplateContentService.selectContentById(app_id,Common.TEMPLATE_NAME_3,"","");
            if (contents.size() > 0){
                String first = contents.get(0).getTemplate_first();
                String remark = contents.get(0).getTemplate_remark();
                template_url = contents.get(0).getTemplate_url();
                msg_content.put("first",first);
                msg_content.put("keyword1",task_title);
                msg_content.put("keyword2","待办");
                msg_content.put("remark",remark);
            }else {
                msg_content.put("first","您好，您有新的待办任务");
                msg_content.put("keyword1",task_title);
                msg_content.put("keyword2","待办");
                msg_content.put("remark","完成更多会员任务获得奖励");
            }
            String template_id = wxTemplates.get(0).getTemplate_id();

            result.put("template_id",template_id);
            result.put("template_content",msg_content);
            result.put("template_url",template_url);

            return result;
        }else {
            return null;
        }
    }

    public JSONObject vipBirthNotice(String app_id,JSONObject vip) throws Exception{
            JSONObject result = new JSONObject();

            JSONObject msg_content = new JSONObject();
            msg_content.put("keyword1","祝您生日快乐");
            msg_content.put("keyword2","点击领取您的生日八折券和生日礼物");
            List<WxTemplateContent> contents = wxTemplateContentService.selectContentById(app_id,Common.TEMPLATE_NAME_3,"","");
            if (contents.size() > 0){
                String first = contents.get(0).getTemplate_first();
                String remark = contents.get(0).getTemplate_remark();
                String store_name = vip.getString("store_name");
                if (store_name != null && store_name.equals("无"))
                    store_name = "未知";
                first = first.replace("\"#name#\"",vip.getString("vip_name"));
                first = first.replace("\"#birthday#\"",vip.getString("vip_birthday"));
                first = first.replace("\"#join_time#\"",vip.getString("join_date"));
                first = first.replace("\"#sex#\"",vip.getString("sex"));
                first = first.replace("\"#store#\"",store_name);

                remark = remark.replace("\"#name#\"",vip.getString("vip_name"));
                remark = remark.replace("\"#birthday#\"",vip.getString("vip_birthday"));
                remark = remark.replace("\"#join_time#\"",vip.getString("join_date"));
                remark = remark.replace("\"#sex#\"",vip.getString("sex"));
                remark = remark.replace("\"#store#\"",store_name);

                msg_content.put("first",first);
                msg_content.put("remark",remark);

                result.put("template_url",contents.get(0).getTemplate_url());
            }else {
                msg_content.put("first","亲爱的ECCO会员，我们为您准备了专属礼遇，请点击该模板消息查看");
                msg_content.put("remark","常规会员折扣（仅限正价商品）：普卡9折，银卡8.8折，金卡8.5折\n" +
                        "会员消费1元累计1积分，50积分可抵扣1元，直接用于消费。");

                result.put("template_url","");
            }
            result.put("template_content",msg_content);

            return result;
    }

    public JSONObject vipCardTypeChangeNotice(String app_id,String card_no,String old_card_type,String new_card_type,String change_time,String point,String desc,String vip_name) throws Exception{
        List<WxTemplate> wxTemplates = selectTempByAppId(app_id,"",Common.TEMPLATE_NAME_5);
        if (wxTemplates.size() > 0){
            JSONObject result = new JSONObject();
            String corp_code = wxTemplates.get(0).getCorp_code();
            String template_id = wxTemplates.get(0).getTemplate_id();
            JSONObject msg_content = new JSONObject();
            String template_url = CommonValue.wei_rule_url.replace("@appid@",app_id);

            if (desc.equalsIgnoreCase("down")){
                desc = "down";
            }else {
                desc = "up";
            }
            List<WxTemplateContent> contents = wxTemplateContentService.selectContentById(app_id,Common.TEMPLATE_NAME_5,desc,new_card_type);
            if (contents.size() > 0){
                String first = contents.get(0).getTemplate_first();
                String remark = contents.get(0).getTemplate_remark();

                first = first.replace("\"#card_type#\"",old_card_type);
                first = first.replace("\"#new_card_type#\"",new_card_type);
                first = first.replace("\"#card_no#\" ",card_no);
                first = first.replace("\"#name#\"",vip_name);

                remark = remark.replace("\"#card_type#\"",old_card_type);
                remark = remark.replace("\"#new_card_type#\"",new_card_type);
                remark = remark.replace("\"#card_no#\" ",card_no);
                remark = remark.replace("\"#name#\"",vip_name);

                msg_content.put("first",first);
                msg_content.put("keyword1",change_time);
                msg_content.put("keyword2",card_no);
                msg_content.put("keyword3",new_card_type);
                msg_content.put("keyword4",point);
                msg_content.put("remark",remark);
                template_url = contents.get(0).getTemplate_url();
            }else{
                List<WxTemplateContent> contents1 = wxTemplateContentService.selectContentById(app_id,Common.TEMPLATE_NAME_5,desc,null);
                if (contents1.size() > 0){
                    String first = contents1.get(0).getTemplate_first();
                    String remark = contents1.get(0).getTemplate_remark();

                    first = first.replace("\"#card_type#\"",old_card_type);
                    first = first.replace("\"#new_card_type#\"",new_card_type);
                    first = first.replace("\"#card_no#\" ",card_no);
                    first = first.replace("\"#name#\"",vip_name);

                    remark = remark.replace("\"#card_type#\"",old_card_type);
                    remark = remark.replace("\"#new_card_type#\"",new_card_type);
                    remark = remark.replace("\"#card_no#\" ",card_no);
                    remark = remark.replace("\"#name#\"",vip_name);

                    msg_content.put("first",first);
                    msg_content.put("keyword1",change_time);
                    msg_content.put("keyword2",card_no);
                    msg_content.put("keyword3",new_card_type);
                    msg_content.put("keyword4",point);
                    msg_content.put("remark",remark);
                    template_url = contents1.get(0).getTemplate_url();
                }else if (desc.equals("down")){
                    msg_content.put("first","亲爱的会员您好，您的新会员周期已经开始，有效期一年");
                    msg_content.put("keyword1",change_time);
                    msg_content.put("keyword2",card_no);
                    msg_content.put("keyword3",new_card_type);
                    msg_content.put("keyword4",point);
                    msg_content.put("remark","点开本消息链接查看您的会员权益");
                    template_url = CommonValue.wei_rule_url.replace("@appid@",app_id);
                }else{
                    if (corp_code.equals("C10238")){
                        msg_content.put("first","亲爱的会员您好，您的新会员周期已经开始，有效期一年");
                        msg_content.put("keyword1",change_time);
                        msg_content.put("keyword2",card_no);
                        msg_content.put("keyword3",new_card_type);
                        msg_content.put("keyword4",point);
                        msg_content.put("remark","恭喜您获得了两张双倍积分券，进入我的优惠券可以使用。点开本消息链接查看您的会员权益");
                    }else {
                        msg_content.put("first","亲爱的会员您好，您的新会员周期已经开始，有效期一年");
                        msg_content.put("keyword1",change_time);
                        msg_content.put("keyword2",card_no);
                        msg_content.put("keyword3",new_card_type);
                        msg_content.put("keyword4",point);
                        msg_content.put("remark","点开本消息链接查看您的会员权益");
                    }

                }
            }


            result.put("template_id",template_id);
            result.put("template_content",msg_content);
            result.put("template_url",template_url);
            return result;
        }else {
            return null;
        }
    }

    public JSONObject getAllTemplateName() throws  Exception{
        JSONObject jsonObject=new JSONObject();
        WxTemplateEnum wxTemplateEnum= WxTemplateEnum.TEMPLATE_NAME_1;
        jsonObject.put("name",wxTemplateEnum.toStringV2());
        return  jsonObject;
    }
}
