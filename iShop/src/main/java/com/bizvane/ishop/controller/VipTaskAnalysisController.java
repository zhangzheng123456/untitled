package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.service.VipTaskAnalysisService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yanyadong on 2017/11/28.
 */
@Controller
@RequestMapping("/vipTaskAnaly")
public class VipTaskAnalysisController {
    Logger logger=Logger.getLogger(VipTaskAnalysisController.class);
    String id="";

    @Autowired
    VipTaskAnalysisService vipTaskAnalysisService;


    /******************************邀请注册********************************/

    @RequestMapping(value = "/registerRate",method = RequestMethod.POST)
    @ResponseBody
    public String getRegisterRate(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getRegisterRate(message,request);
            JSONObject result=new JSONObject();
            result.put("info",jsonObject);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/registerChart",method = RequestMethod.POST)
    @ResponseBody
    public String getRegisterChart(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getRegisterChart(message);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/registerList",method = RequestMethod.POST)
    @ResponseBody
    public String getRegisterList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getRegisterList(message);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/registerVipInfo",method = RequestMethod.POST)
    @ResponseBody
    public String getRegisterVipInfo(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getRegisterVipInfo(message);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }
    /****************************************完善资料**************************************************************/


    @RequestMapping(value = "/completeRate",method = RequestMethod.POST)
    @ResponseBody
    public String getCompleteRate(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getCompleteRate(message,request);
            JSONObject result=new JSONObject();
            result.put("info",jsonObject);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/completeChart",method = RequestMethod.POST)
    @ResponseBody
    public String getCompleteChart(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getCompleteChart(message);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/completeList",method = RequestMethod.POST)
    @ResponseBody
    public String getCompleteList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getCompleteList(message);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    /****************************************累积消费次数、累积消费金额、客单价******************************************/


    @RequestMapping(value = "/saleInfoRate",method = RequestMethod.POST)
    @ResponseBody
    public String getSaleInfoRate(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getSaleInfoRate(message,request);
            JSONObject result=new JSONObject();
            result.put("info",jsonObject);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/saleInfoChart",method = RequestMethod.POST)
    @ResponseBody
    public String getSaleInfoChart(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getSaleInfoChart(message);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/saleInfoList",method = RequestMethod.POST)
    @ResponseBody
    public String getSaleInfoList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getSaleInfoList(message);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    /************************************************问卷调查************************************************************/

    @RequestMapping(value = "/questionNaireoRate",method = RequestMethod.POST)
    @ResponseBody
    public String getQuestionNaireoRate(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getSaleInfoRate(message,request);
            JSONObject result=new JSONObject();
            result.put("info",jsonObject);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/questionNaireoChart",method = RequestMethod.POST)
    @ResponseBody
    public String getQuestionNaireoChart(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getSaleInfoChart(message);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/questionNaireoList",method = RequestMethod.POST)
    @ResponseBody
    public String getQuestionNaireoList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getQuestionNaireList(message);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    /**************************************************分享次数*************************************************************/

    @RequestMapping(value = "/shareUrlRate",method = RequestMethod.POST)
    @ResponseBody
    public String getShareUrlRate(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getShareUrlRate(message,request);
            JSONObject result=new JSONObject();
            result.put("info",jsonObject);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/shareUrlChart",method = RequestMethod.POST)
    @ResponseBody
    public String getShareUrlChart(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getShareUrlChart(message);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/shareUrlList",method = RequestMethod.POST)
    @ResponseBody
    public String getShareUrlList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject= vipTaskAnalysisService.getShareUrlList(message);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }
}
