package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.VipPointsAdjust;
import com.bizvane.ishop.service.VipPointsAdjustService;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by gyy on 2017/11/12.
 */
@Controller
@RequestMapping("/vipPointsAdjust")
public class VipPointsAdjustController {

    @Autowired
    private VipPointsAdjustService vipPointsAdjustService;
    String id="";

    private static final Logger logger = Logger.getLogger(ParamConfigureController.class);

    /**
     * 添加
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public String addPoints(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String user_id = request.getSession().getAttribute("user_code").toString();
            String result = vipPointsAdjustService.insertPointsAdjust(message,user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                JSONObject jsonObject = JSONObject.parseObject(message);
                String bill_code= jsonObject.get("bill_code").toString();
                VipPointsAdjust vipPointsAdjust = vipPointsAdjustService.selectPointsAdjustByBillCode(bill_code);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(String.valueOf(vipPointsAdjust.getId()));
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("单据名称重复");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }

        return dataBean.getJsonStr();
    }

    /**
     * 编辑参数
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String editPoints(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--param ---- edit-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String user_id = request.getSession().getAttribute("user_code").toString();
            String result = vipPointsAdjustService.updatePointsAdjust(message,user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)){
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("保存成功");
            }else{
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("单据名称重复");
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
     * 删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(HttpServletRequest request) throws SQLException {
        DataBean dataBean = new DataBean();

        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            logger.info("-------------delete--" + id);
            String bill_id = jsonObject.get("id").toString();
            String[] ids = bill_id.split(",");
            String msg = null;
//            for (int i = 0; i < ids.length; i++) {
//                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
//                VipPointsAdjust vipPointsAdjust = vipPointsAdjustService.selectPointsAdjustById(Integer.valueOf(ids[i]));
//                if (paramConfigure != null) {
//                    List<CorpParam> corpParam = corpParamService.selectByParamId(ids[i]);
//                    if (corpParam.size() > 0) {
//                        msg = "有使用参数" + paramConfigure.getParam_name() + "的企业参数，请先删除企业参数";
//                        break;
//                    }
//                }

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
//                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
//                String operation_user_code = request.getSession().getAttribute("user_code").toString();
//                String function = "系统管理_参数定义";
//                String action = Common.ACTION_DEL;
//                String t_corp_code = "";
//                String t_code = paramConfigure.getParam_name();
//                String t_name = paramConfigure.getParam_type();
//                String remark = "";
//                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
//            }
            if (msg == null) {
                for (int i = 0; i < ids.length; i++) {
                    vipPointsAdjustService.deletePointsAdjustById(Integer.valueOf(ids[i]));
                }
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("删除成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            }

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }

        return dataBean.getJsonStr();
    }


    /**
     * 搜索
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            int  page_number=Integer.parseInt(jsonObject.getString("pageNumber").toString());
            int page_size=Integer.parseInt(jsonObject.getString("pageSize").toString());
            String search_value=jsonObject.getString("searchValue").toString();

            PageInfo<VipPointsAdjust> list;
            if(role_code.equals(Common.ROLE_SYS)){
                list=vipPointsAdjustService.selectPointsAdjustAll(page_number,page_size,"",search_value);
            }else{
                list=vipPointsAdjustService.selectPointsAdjustAll(page_number,page_size,corp_code,search_value);
            }
            JSONObject result=new JSONObject();
            result.put("list",JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    /**
     * 筛选
     * @param request
     * @return
     */
    @RequestMapping(value = "/screen",method = RequestMethod.POST)
    @ResponseBody
    public String screen(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            int page_number=Integer.parseInt(jsonObject.getString("pageNumber").toString());
            int page_size=Integer.parseInt(jsonObject.getString("pageSize").toString());
            Map<String,String> map = WebUtils.Json2Map(jsonObject);
            PageInfo<VipPointsAdjust> list;
            if(role_code.equals(Common.ROLE_SYS)){
                list=vipPointsAdjustService.selectVipPointsAdjustAllScreen(page_number,page_size,"",map);
            }else{
                list=vipPointsAdjustService.selectVipPointsAdjustAllScreen(page_number,page_size,corp_code,map);
            }
            JSONObject result=new JSONObject();
            result.put("list",JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/select",method = RequestMethod.POST)
    @ResponseBody
    public String select(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            int id=Integer.parseInt(jsonObject.get("id").toString());
            VipPointsAdjust vipPointsAdjust=vipPointsAdjustService.selectPointsAdjustById(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(JSON.toJSONString(vipPointsAdjust));
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }
}
