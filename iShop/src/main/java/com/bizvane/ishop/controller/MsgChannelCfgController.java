package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.MsgChannelCfg;
import com.bizvane.ishop.entity.MsgChannels;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.MsgChannelCfgService;
import com.bizvane.ishop.service.MsgChannelsService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.apache.commons.validator.Msg;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jy on 2017/5/4.
 */

@Controller
@RequestMapping("/msgChannelCfg")
public class MsgChannelCfgController {


    @Autowired
    MsgChannelCfgService msgChannelCfgService;

    @Autowired
    MsgChannelsService msgChannelsService;

    @Autowired
    private BaseService baseService;
    private static final Logger logger = Logger.getLogger(MsgChannelCfgController.class);

    String id;


    /**
     * 企业信息配置短信通道
     * 添加
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addChannelCfg(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String id = "";
        String code = "";
        String result = "";
        try {
            Date now = new Date();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

            String type = jsonObject.get("type").toString();
            String channel_name = jsonObject.get("channel_name").toString();
            String channel_account = jsonObject.get("channel_account").toString();
            String password = jsonObject.get("password").toString();
            String channel_price = jsonObject.get("channel_price").toString();
            String channel_sign = jsonObject.get("channel_sign").toString();
            String channel_code = jsonObject.get("channel_code").toString();
            String channel_child = jsonObject.get("channel_child").toString();
            String is_forced = jsonObject.get("is_forced").toString();
            String isactive = Common.IS_ACTIVE_Y;
            String random_code = "C" + corp_code + sdf.format(new Date()) + Math.round(Math.random() * 9);
            MsgChannelCfg msgChannelCfg = msgChannelCfgService.checkAccount(corp_code, type, channel_name, channel_account, channel_child, channel_sign, Common.IS_ACTIVE_Y);
            if (msgChannelCfg == null) {
                MsgChannelCfg msg1 = new MsgChannelCfg();
                msg1.setRandom_code(random_code);
                msg1.setType(type);
                msg1.setChannel_account(channel_account);
                msg1.setChannel_child(channel_child);
                msg1.setChannel_price(channel_price);
                msg1.setChannel_name(channel_name);
                msg1.setChannel_code(channel_code);
                msg1.setChannel_sign(channel_sign);
                msg1.setBrand_code("");
                msg1.setBrand_code_production("");
                msg1.setPassword(password);
                msg1.setCorp_code(corp_code);
                msg1.setIs_forced(is_forced);
                msg1.setCreater(user_id);
                msg1.setCreated_date(Common.DATETIME_FORMAT.format(now));
                msg1.setModifier(user_id);
                msg1.setModified_date(Common.DATETIME_FORMAT.format(now));
                msg1.setIsactive(isactive);
                result = msgChannelCfgService.insert(msg1);
                logger.info("========result=============" + result);
                if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                    MsgChannelCfg msgChannelCfg1 = msgChannelCfgService.checkSign(corp_code, channel_sign, Common.IS_ACTIVE_Y);
                    if (msgChannelCfg1 != null) {
                        dataBean.setId(id);
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setMessage(msgChannelCfg1.getId());
                    }
                } else {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("error");
                }
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("企业已存在该短信通道");
            }


            if (dataBean.getCode().equals(Common.DATABEAN_CODE_SUCCESS)) {
                //----------------行为日志------------------------------------------
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
                String function = "企业管理_新增短信通道";
                String action = Common.ACTION_ADD;
                String t_corp_code = code;
                String t_code = "";
                String t_name = "";
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 企业信息配置短信通道
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editChannelCfg(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String id = "";
        String code = "";
        String result = "";
        try {
            Date now = new Date();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String msg_id = jsonObject.get("id").toString();
            String type = jsonObject.get("type").toString();
            String channel_name = jsonObject.get("channel_name").toString();
            String channel_account = jsonObject.get("channel_account").toString();
            String password = jsonObject.get("password").toString();
            String channel_price = jsonObject.get("channel_price").toString();
            String channel_sign = jsonObject.get("channel_sign").toString();
            String channel_code = jsonObject.get("channel_code").toString();
            String channel_child = jsonObject.get("channel_child").toString();
            String is_forced = jsonObject.get("is_forced").toString();
            MsgChannelCfg msgChannelCfg1 = msgChannelCfgService.getMsgChannelCfgById(Integer.valueOf(msg_id));
            MsgChannelCfg msgChannelCfg = msgChannelCfgService.checkAccount(corp_code, type, channel_name, channel_account, channel_child, channel_sign, Common.IS_ACTIVE_Y);
            String isactive = Common.IS_ACTIVE_Y;
            String random_code = "C" + corp_code + sdf.format(new Date()) + Math.round(Math.random() * 9);

            if (msgChannelCfg == null || msgChannelCfg.getId().equals(msg_id)) {

                MsgChannelCfg msg1 = new MsgChannelCfg();
                msg1.setRandom_code(random_code);
                msg1.setType(type);
                msg1.setId(msg_id);
                msg1.setChannel_account(channel_account);
                msg1.setChannel_child(channel_child);
                msg1.setChannel_price(channel_price);
                msg1.setChannel_name(channel_name);
                msg1.setChannel_code(channel_code);
                msg1.setChannel_sign(channel_sign);
                msg1.setPassword(password);
                msg1.setCorp_code(corp_code);
                msg1.setIs_forced(is_forced);
                msg1.setBrand_code_production(msgChannelCfg1.getBrand_code_production());
                msg1.setBrand_code(msgChannelCfg1.getBrand_code());
                msg1.setModifier(user_id);
                msg1.setModified_date(Common.DATETIME_FORMAT.format(now));
                msg1.setIsactive(isactive);
                result = msgChannelCfgService.update(msg1);
                if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage("edit success");

                }else{
                    dataBean.setId(id);
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("edit error");
                }

            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("企业已存在该短信通道");
            }


            if (dataBean.getCode().equals(Common.DATABEAN_CODE_SUCCESS)) {
                //----------------行为日志------------------------------------------
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
                String function = "企业管理_编辑短信通道";
                String action = Common.ACTION_ADD;
                String t_corp_code = code;
                String t_code = "";
                String t_name = "";
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/selChannels", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String role_code = request.getSession(false).getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            PageInfo<MsgChannelCfg> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = msgChannelCfgService.selectAllMsgChannelCfg(page_number, page_size, "", search_value);
            } else if (role_code.contains(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                list = msgChannelCfgService.selectAllMsgChannelCfg(page_number, page_size, "", search_value, manager_corp);
            } else if (role_code.equals(Common.ROLE_GM)) {
                list = msgChannelCfgService.selectAllMsgChannelCfg(page_number, page_size, corp_code, search_value);

            }
            JSONObject result = new JSONObject();

            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("search error");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 根据企业拉通道列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/selChannelByCorp", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String selChannelByCorp(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            List<MsgChannelCfg> list = msgChannelCfgService.getMsgChannelCfgByCorp(corp_code, Common.IS_ACTIVE_Y);
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("search error");
        }
        return dataBean.getJsonStr();
    }


    /**
     * 根据企业，类型拉通道列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/selChannelByType", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String selChannelByType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String type = jsonObject.get("type").toString();

            String corp_code = jsonObject.get("corp_code").toString();
            List<MsgChannelCfg> list = msgChannelCfgService.getMsgChannelCfgByType(corp_code, type, Common.IS_ACTIVE_Y);
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("search error");
        }
        return dataBean.getJsonStr();
    }


    /**
     * 删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String area_id = jsonObject.get("id").toString();
            String info="";
            String[] ids = area_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                MsgChannelCfg msgChannelCfg = msgChannelCfgService.getMsgChannelCfgById(Integer.valueOf(ids[i]));
                String is_cfg = msgChannelCfg.getBrand_code();
                String is_cfg1 = msgChannelCfg.getBrand_code_production();
                info=is_cfg+is_cfg1;

            }
           if(!info.equals("")){
               dataBean.setCode(Common.DATABEAN_CODE_ERROR);
               dataBean.setId(id);
               dataBean.setMessage("该企业已有品牌配置过该短信通道");
           }else{
               for (int i = 0; i <ids.length ; i++) {
                   MsgChannelCfg msgChannelCfg = msgChannelCfgService.getMsgChannelCfgById(Integer.valueOf(ids[i]));

                   String corp = msgChannelCfg.getCorp_code();
                   //  logger.info("-------------delete--" + Integer.valueOf(ids[i]));

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
                   String function = "企业管理_删除短信通道";
                   String action = Common.ACTION_DEL;
                   String t_corp_code = msgChannelCfg.getCorp_code();
                   String t_code = "";
                   String t_name = "";
                   String remark = "";
                   baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                   //-------------------行为日志结束-----------------------------------------------------------------------------------
                   msgChannelCfgService.delete(Integer.valueOf(ids[i]));

               }
               dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
               dataBean.setId(id);
               dataBean.setMessage("success");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }


    /**
     * 获取详细信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String selectActivity(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String cfg_id = jsonObject.get("id").toString();
            MsgChannelCfg msgChannelCfg = msgChannelCfgService.getMsgChannelCfgById(Integer.valueOf(cfg_id));

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(JSON.toJSONString(msgChannelCfg));
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /***
     * 拉取系统所有可用的短信通道
     * @param request
     * @return
     */
    @RequestMapping(value = "/getChannelsInfo", method = RequestMethod.POST)
    @ResponseBody
    public String getChannelsInfo(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            JSONObject params = new JSONObject();
            JSONArray array = new JSONArray();
            List<MsgChannels> list = msgChannelsService.getAllChannels();
            for (int i = 0; i < list.size(); i++) {
                MsgChannels msgChannels = list.get(i);
                //中文名称
                String info = msgChannels.getChannel_name();
                //英文名
                String name = msgChannels.getChannel();

                JSONObject obj = new JSONObject();
                obj.put("channel_name", info);
                obj.put("channel", name);
                array.add(obj);
            }
            params.put("info", array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(params.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }

        return dataBean.getJsonStr();
    }


    /**
     * 验证账号唯一性
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/accountExist", method = RequestMethod.POST)
    @ResponseBody
    public String accountExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String type = jsonObject.get("type").toString();
            String channel_name = jsonObject.get("channel_name").toString();
            String channel_account = jsonObject.get("channel_account").toString();
            String channel_child = jsonObject.get("channel_child").toString();
            String sign = "";
            MsgChannelCfg msgChannelCfg = msgChannelCfgService.checkAccount(corp_code, type, channel_name, channel_account, channel_child, sign, Common.IS_ACTIVE_Y);
            if (msgChannelCfg == null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("该企业不存在该子账号");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("该企业已存在该子账号");
            }
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(JSON.toJSONString(msgChannelCfg));
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 验证签名
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/signExiist", method = RequestMethod.POST)
    @ResponseBody
    public String signExiist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String sign = jsonObject.get("channel_sign").toString();
            MsgChannelCfg msgChannelCfg = msgChannelCfgService.checkSign(corp_code, sign, Common.IS_ACTIVE_Y);
            if (msgChannelCfg == null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("该签名不存在");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);

                dataBean.setMessage("该企业已存在该签名");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


}
