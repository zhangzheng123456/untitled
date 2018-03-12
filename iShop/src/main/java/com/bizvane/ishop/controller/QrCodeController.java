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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yanyadong on 2017/3/14.
 */
@Controller
@RequestMapping("/qrCode")
public class QrCodeController {

    @Autowired
    QrCodeService qrCodeService;
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserService userService;
    @Autowired
    private  CorpService corpService;
    @Autowired
    private StoreService storeService;
    private static final Logger logger = Logger.getLogger(QrCodeController.class);

    String id="";

    @RequestMapping(value = "/select",method = RequestMethod.POST)
    @ResponseBody
    public String selectQrcode(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            int qrcode_id=Integer.parseInt(jsonObject.get("id").toString());
            QrCode qrCode=qrCodeService.selectQrCodeById(qrcode_id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(JSON.toJSONString(qrCode));
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/search",method = RequestMethod.POST)
    @ResponseBody
    public String selectAllQrcode(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
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
            PageInfo<QrCode> list=null;
            if(role_code.equals(Common.ROLE_SYS)){
                list=qrCodeService.selectAll(page_number,page_size,"",search_value);
            }else{
                list=qrCodeService.selectAll(page_number,page_size,corp_code,search_value);
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

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public String deleteQrCode(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            String qrcode_id=jsonObject.get("id").toString();
            String[] ids=qrcode_id.split(",");
            for(int i=0;i<ids.length;i++){
               QrCode qrCode= qrCodeService.selectQrCodeById(Integer.valueOf(ids[i]));
                String corp=qrCode.getCorp_code();
               String qrcode_name= qrCode.getQrcode_name();
              String app_name= qrCode.getApp_name();
                String remark= qrCode.getRemark();
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
                String function = "会员管理_渠道二维码";
                String action = Common.ACTION_DEL;
                String t_corp_code = corp;
                String t_code = qrcode_name;
                String t_name = app_name;
                String remark1 = remark;
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark1);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

                qrCodeService.delQrcodeById(Integer.parseInt(ids[i]));
            }
           // JSONObject result=new JSONObject();
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("success");
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();

    }

    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    @ResponseBody
    public String insertQrcode(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String user_code=request.getSession().getAttribute("user_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            QrCode qrCode1=qrCodeService.selectQrCodeByQrcodeName(jsonObject.getString("corp_code").toString(),
                    jsonObject.get("qrcode_name").toString());
            if(qrCode1!=null){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("该二维码名已存在");
                return dataBean.getJsonStr();
            }

            QrCode qrCode=new QrCode();
            qrCode.setCorp_code(jsonObject.getString("corp_code").toString());
            qrCode.setApp_id(jsonObject.getString("app_id").toString());
            qrCode.setApp_name(jsonObject.getString("app_name").toString());
            qrCode.setApp_user_name(jsonObject.get("app_user_name").toString());
            qrCode.setQrcode_type(jsonObject.get("qrcode_type").toString());
            qrCode.setAging(jsonObject.get("aging").toString());
            qrCode.setQrcode_name(jsonObject.get("qrcode_name").toString());

            qrCode.setRemark(jsonObject.get("remark").toString());
            //修改时间
            qrCode.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            qrCode.setModifier(user_code);
            //创建时间
            qrCode.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
            qrCode.setCreater(user_code);
            qrCode.setIsactive(jsonObject.get("isactive").toString());

               qrCodeService.insertQrcode(qrCode);
            JSONObject result=new JSONObject();
           QrCode qrCode2= qrCodeService.selectQrCodeByQrcodeName(jsonObject.getString("corp_code").toString(),jsonObject.get("qrcode_name").toString());
            result.put("id",qrCode2.getId());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

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
            com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
            String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
            String operation_user_code = request.getSession().getAttribute("user_code").toString();
            String function = "会员管理_渠道二维码";
            String action = Common.ACTION_ADD;
            String t_corp_code = action_json.get("corp_code").toString();
            String t_code = action_json.get("qrcode_name").toString();
            String t_name = action_json.get("app_name").toString();
            String remark = action_json.get("remark").toString();
            baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
            //-------------------行为日志结束----

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public String updateQrCode(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String user_code=request.getSession().getAttribute("user_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            QrCode qrCode1=qrCodeService.selectQrCodeById(Integer.parseInt(jsonObject.get("id").toString()));
            String corp=qrCode1.getCorp_code();

            if(!qrCode1.getQrcode_name().equals(jsonObject.get("qrcode_name").toString())){

                QrCode qrCode2=qrCodeService.selectQrCodeByQrcodeName(qrCode1.getCorp_code(),
                        jsonObject.get("qrcode_name").toString());
                if(qrCode2!=null){
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("该二维码名已存在");
                    return dataBean.getJsonStr();
                }
            }

            QrCode qrCode=new QrCode();
            qrCode.setId(Integer.parseInt(jsonObject.get("id").toString()));
            qrCode.setQrcode_name(jsonObject.get("qrcode_name").toString());
            qrCode.setRemark(jsonObject.get("remark").toString());
            //获取当前时间
            qrCode.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            qrCode.setModifier(user_code);
            qrCode.setIsactive(jsonObject.get("isactive").toString());

            qrCodeService.updateQrcode(qrCode);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("success");

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
            com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
            String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
            String operation_user_code = request.getSession().getAttribute("user_code").toString();
            String function = "会员管理_渠道二维码 ";
            String action = Common.ACTION_UPD;
            String t_corp_code = corp;
            String t_code = action_json.get("qrcode_name").toString();
            String t_name = action_json.get("app_name").toString();
            String remark = action_json.get("remark").toString();
            baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
            //-------------------行为日志结束-----------------------------------------------------------------------------------

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/screen",method = RequestMethod.POST)
    @ResponseBody
    public  String  screenQrCode(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            int  page_number=Integer.parseInt(jsonObject.getString("pageNumber").toString());
            int page_size=Integer.parseInt(jsonObject.getString("pageSize").toString());
            Map<String,String> map = WebUtils.Json2Map(jsonObject);
            PageInfo<QrCode> list=null;
            if(role_code.equals(Common.ROLE_SYS)){
                list=qrCodeService.selectAllScreen(page_number,page_size,"",map);
            }else{
                list=qrCodeService.selectAllScreen(page_number,page_size,corp_code,map);
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

    @RequestMapping(value = "/exportExecl",method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response){
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try{

            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code =request.getSession().getAttribute("corp_code").toString();

            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<QrCode> list;
            if(role_code.equals(Common.ROLE_SYS)) {
                if (screen.equals("")) {
                    list = qrCodeService.selectAll(1, Common.EXPORTEXECLCOUNT, "", search_value);
                } else {
                    Map<String, String> map = WebUtils.Json2Map(jsonObject);
                    list = qrCodeService.selectAllScreen(1, Common.EXPORTEXECLCOUNT, "", map);
                }
            }else{
                if (screen.equals("")) {
                    list = qrCodeService.selectAll(1, Common.EXPORTEXECLCOUNT, corp_code, search_value);
                } else {
                    Map<String, String> map = WebUtils.Json2Map(jsonObject);
                    list = qrCodeService.selectAllScreen(1, Common.EXPORTEXECLCOUNT, corp_code, map);

                }

            }

            /**
             * 导出操作..................................................
             */
            List<QrCode> qrCodes = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(qrCodes);
            if (qrCodes.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json, qrCodes, map, response, request,"会员标签");
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
            //导出操作结束.........................................................
        }catch (Exception e){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/creatQrcode",method = RequestMethod.POST)
    @ResponseBody
    public String creatQrcode(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String user_code=request.getSession().getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String qrcodeId = jsonObject.get("id").toString();
            QrCode qrCode = qrCodeService.selectQrCodeById(Integer.parseInt(qrcodeId));

            String prd = "ishop";
            String aging = qrCode.getAging();
            String src = qrCode.getQrcode_type();
            String app_id = qrCode.getApp_id();

            if (aging.contains("永久")){
                aging = "";
            }else {
                aging = "temp";
            }
            String url = CommonValue.wechat_url+"/creatQrcode?auth_appid="+app_id+"&prd="+prd+"&src="+src+"&qrcodeId="+qrcodeId+"&aging="+aging;
            String result = IshowHttpClient.get(url);

            logger.info("------------creatQrcode  result" + result);
            JSONObject result_obj = new JSONObject();

            int msg = 0;
            if (!result.startsWith("{")) {
                msg = -1;
            }else{
                JSONObject obj = JSONObject.parseObject(result);
                if (result.contains("errcode")) {
                    msg = -1;
                } else {
                    String picture = obj.get("picture").toString();
                    String qrcode_url = obj.get("url").toString();
                    qrCode.setQrcode(picture);
                    qrCode.setQrcode_content(qrcode_url);
                    qrCode.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
                    qrCode.setModifier(user_code);
                    qrCode.setCreate_qrcode_time(Common.DATETIME_FORMAT.format(new Date()));
                    qrCodeService.updateQrcode(qrCode);
                    result_obj.put("qrcode",picture);
                    result_obj.put("qrcode_content",qrcode_url);
                }
            }
            if (msg == 0){
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result_obj.toString());
            }else {
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("生成二维码失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/qrcodeList",method = RequestMethod.POST)
    @ResponseBody
    public String qrcodeList(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String app_id = jsonObject.getString("app_id");
            String type = jsonObject.getString("type");
            String[] types=type.split(",");
            List<QrCode> list = qrCodeService.selectQrcode(app_id,types);

            List<QrCode> list1 = new ArrayList<QrCode>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getQrcode() != null && !list.get(i).getQrcode().equals("") && list.get(i).getIsactive().equals("Y"))
                    list1.add(list.get(i));
            }
            JSONObject result=new JSONObject();
            result.put("list",JSON.toJSONString(list1));
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


    //..............................修改...........................................................

    /**
     * 渠道二维码分析 关注人数,新增会员折线图
     */
    @RequestMapping(value = "/analy/view", method = RequestMethod.POST)
    @ResponseBody
    public String getView1(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_qrcode_relation);
        try {
            JSONObject result = new JSONObject();
            JSONArray array = new JSONArray();

            String param = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String date_screen=jsonObject.get("date").toString();
            JSONObject screen_json= JSON.parseObject(date_screen);
            String start_date=screen_json.getString("start");
            String end_date=screen_json.getString("end");
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
            if(start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(new Date());
                start_date=simpleDateFormat.format(TimeUtils.getNextDay(end_date,30));
            }else if(start_date.equals("")&&!end_date.equals("")) {
                start_date = simpleDateFormat.format(TimeUtils.getNextDay(end_date, 30));
            }else  if(!start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(TimeUtils.getNextDay(start_date,-30));
            }

            String app_id = jsonObject.get("app_id").toString();
            String qrcode_id = jsonObject.get("qrcode_id").toString();
            if (!qrcode_id.equals("")){
                List<String> dates = TimeUtils.getBetweenDates(start_date, end_date);
                for (int i = 0; i < dates.size(); i++) {
                    JSONObject json_data = qrCodeService.getQrcodeScan(cursor, app_id, qrcode_id, dates.get(i));
                    array.add(json_data);
                }
            }
            result.put("analyView",array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 二维码分析 列表
     */
    @RequestMapping(value = "/analy/list", method = RequestMethod.POST)
    @ResponseBody
    public String getList1(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_qrcode_relation);
        String corp_code = request.getSession().getAttribute("corp_code").toString();

        DecimalFormat df = new DecimalFormat("#.##");
        String param = request.getParameter("param");
        JSONObject jsonObj = JSONObject.parseObject(param);
        id = jsonObj.get("id").toString();
        String message = jsonObj.get("message").toString();
        JSONObject jsonObject = JSONObject.parseObject(message);
        String app_id = jsonObject.get("app_id").toString();
        String qrcode_id = jsonObject.get("qrcode_id").toString();

        int page_num=Integer.parseInt(jsonObject.getString("page_num"));
        int page_size=Integer.parseInt(jsonObject.getString("page_size"));

        String date_screen=jsonObject.get("date").toString();
        JSONObject screen_json= JSON.parseObject(date_screen);
        String start_date=screen_json.getString("start");
        String end_date=screen_json.getString("end");
        try {
           SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
            if(start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(new Date());
                start_date=simpleDateFormat.format(TimeUtils.getNextDay(end_date,30));
            }else if(start_date.equals("")&&!end_date.equals("")) {
                start_date = simpleDateFormat.format(TimeUtils.getNextDay(end_date, 30));
            }else  if(!start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(TimeUtils.getNextDay(start_date,-30));
            }

            List<HashMap<String,Object>> list=new ArrayList<HashMap<String, Object>>();
            String[] qrcodes=qrcode_id.split(",");
            //......分页......
            int end_row = 0;
            if (qrcodes.length > page_num*page_size){
                end_row = page_num*page_size;
            }else {
                end_row = qrcodes.length;
            }
            for (int i = (page_num-1)*page_size ; i < end_row; i++) {
                HashMap<String,Object> map=new HashMap<String, Object>();
                BasicDBList basicDBList=new BasicDBList();
                BasicDBObject dbObject=new BasicDBObject();
                BasicDBObject dbObject1=new BasicDBObject();
                //二维码信息
                QrCode qrcode=qrCodeService.selectQrCodeById(Integer.parseInt(qrcodes[i]));
                String qrcode_name_info=qrcode.getQrcode_name();
                if (qrcode.getQrcode_type().equals("print"))
                    qrcode.setQrcode_type("印刷类");
                if (qrcode.getQrcode_type().equals("material"))
                    qrcode.setQrcode_type("材料类");
                if (qrcode.getQrcode_type().equals("gift"))
                    qrcode.setQrcode_type("礼品类");
                String qrcode_type_info=qrcode.getQrcode_type();
                String create_date=qrcode.getCreated_date().split(" ")[0];

                List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                basicDBList.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
                basicDBList.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) +" 23:59:59")));
                basicDBList.add(new BasicDBObject("app_id", app_id));
                basicDBList.add(new BasicDBObject("qrcode_id", qrcodes[i]));
                dbObject.put("$and", basicDBList);
                DBCursor dbCursor=cursor.find(dbObject);
                int allCount=dbCursor.count();
                basicDBList.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
                basicDBList.add(new BasicDBObject("transVip", "Y"));
                dbObject1.put("$and", basicDBList);
                DBCursor dbCursor1 = cursor.find(dbObject1);
                int newVipCount=dbCursor1.count();
                map.put("qrcode_name",qrcode_name_info);
                map.put("qrcode_type",qrcode_type_info);
                map.put("allCount",allCount);
                map.put("newVipCount",newVipCount);
                map.put("rate",df.format((double) newVipCount/allCount));
                map.put("create_date",create_date);
                list.add(map);
            }

            //总数
            int allAnaly=0;
            int allVip=0;
            BasicDBList basicDBList_qr=new BasicDBList();
            for (int i = 0 ; i < qrcodes.length; i++) {
                basicDBList_qr.add(qrcodes[i]);
            }
                BasicDBList basicDBList_all=new BasicDBList();
                BasicDBObject dbObject_all=new BasicDBObject();
                BasicDBObject dbObject1_all=new BasicDBObject();

                List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                basicDBList_all.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
                basicDBList_all.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) +" 23:59:59")));
                basicDBList_all.add(new BasicDBObject("app_id", app_id));
                basicDBList_all.add(new BasicDBObject("qrcode_id", new BasicDBObject("$in",basicDBList_qr)));
                dbObject_all.put("$and", basicDBList_all);
                DBCursor dbCursor=cursor.find(dbObject_all);
                allAnaly=dbCursor.count();
                basicDBList_all.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
                basicDBList_all.add(new BasicDBObject("transVip", "Y"));
                dbObject1_all.put("$and", basicDBList_all);
                DBCursor dbCursor1 = cursor.find(dbObject1_all);
                allVip=dbCursor1.count();

            int count=qrcodes.length;
            int pages = 0;
            if (count % page_size == 0) {
                pages = count / page_size;
            } else {
                pages = count / page_size + 1;
            }
            JSONObject result=new JSONObject();
            result.put("list",list);
            result.put("pages",pages);
            result.put("count",count);
            result.put("page_num",page_num);
            result.put("page_size",page_size);
            result.put("allAnaly",allAnaly);
            result.put("allVip",allVip);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 二维码分析 会员列表
     */
    @RequestMapping(value = "/analy/vipList", method = RequestMethod.POST)
    @ResponseBody
    public String getVipList1(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_qrcode_relation);
        String corp_code = request.getSession().getAttribute("corp_code").toString();

        String param = request.getParameter("param");
        JSONObject jsonObj = JSONObject.parseObject(param);
        id = jsonObj.get("id").toString();
        String message = jsonObj.get("message").toString();
        JSONObject jsonObject = JSONObject.parseObject(message);
        String app_id = jsonObject.get("app_id").toString();
        String qrcode_id = jsonObject.get("qrcode_id").toString();

        int page_num=Integer.parseInt(jsonObject.getString("page_num"));
        int page_size=Integer.parseInt(jsonObject.getString("page_size"));

        String date_screen=jsonObject.get("date").toString();
        JSONObject screen_json= JSON.parseObject(date_screen);
        String start_date=screen_json.getString("start");
        String end_date=screen_json.getString("end");


        //筛选条件
        String screen=jsonObject.getString("screen");
        JSONObject screen_obj=JSON.parseObject(screen);
        String nick_name=screen_obj.getString("nick_name");
        String vip_name=screen_obj.getString("vip_name");
        String cardno=screen_obj.getString("cardno");
        String vip_phone=screen_obj.getString("vip_phone");
        String user_name=screen_obj.getString("user_name");
        String store_name=screen_obj.getString("store_name");
        String join_date_screen=screen_obj.getString("join_date_screen");
        JSONObject jsonObject1=JSON.parseObject(join_date_screen);
        String join_start=jsonObject1.getString("start");
        String join_end=jsonObject1.getString("end");
        String is_new=screen_obj.getString("is_new");
        try {
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
            if(start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(new Date());
                start_date=simpleDateFormat.format(TimeUtils.getNextDay(end_date,30));
            }else if(start_date.equals("")&&!end_date.equals("")) {
                start_date = simpleDateFormat.format(TimeUtils.getNextDay(end_date, 30));
            }else  if(!start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(TimeUtils.getNextDay(start_date,-30));
            }

            List list=new ArrayList<HashMap<String, Object>>();
            String[] qrcodes=qrcode_id.split(",");
            for (int i = 0; i < qrcodes.length; i++) {
                BasicDBObject dbObject1=new BasicDBObject();
                List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                BasicDBList basicDBList=new BasicDBList();
                basicDBList.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
                basicDBList.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) +" 23:59:59")));
                basicDBList.add(new BasicDBObject("app_id", app_id));
                basicDBList.add(new BasicDBObject("qrcode_id", qrcodes[i]));
                basicDBList.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
                basicDBList.add(new BasicDBObject("transVip", "Y"));
                if(StringUtils.isNotBlank(nick_name)){
                    basicDBList.add(new BasicDBObject("nick_name",new BasicDBObject("$regex",nick_name)));
                }
                if(StringUtils.isNotBlank(vip_name)){
                    basicDBList.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
                }
                if(StringUtils.isNotBlank(cardno)){
                    basicDBList.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",cardno)));
                }
                if(StringUtils.isNotBlank(vip_phone)){
                    basicDBList.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
                }
                if(StringUtils.isNotBlank(user_name)){
                    basicDBList.add(new BasicDBObject("vip.user_name",new BasicDBObject("$regex",user_name)));
                }
                if(StringUtils.isNotBlank(store_name)){
                    basicDBList.add(new BasicDBObject("vip.store_name",new BasicDBObject("$regex",store_name)));
                }
                if(StringUtils.isNotBlank(join_start)){
                    basicDBList.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.GTE, join_start)));
                }
                if(StringUtils.isNotBlank(join_end)){
                    basicDBList.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.LTE, join_end)));
                }
                dbObject1.put("$and", basicDBList);
                DBCursor dbCursor1 = cursor.find(dbObject1).sort(new BasicDBObject("create_date",-1));
                ArrayList arrayList=qrCodeService.dbCursorToList(dbCursor1,0);

                ArrayList arrayList1 = new ArrayList();
                if (is_new != null && !is_new.equals("")){
                    for (int j = 0; j < arrayList.size(); j++) {
                        DBObject dbObject = (DBObject)arrayList.get(j);
                        if (is_new.equals(dbObject.get("is_new"))){
                            arrayList1.add(dbObject);
                        }
                    }
                }else {
                    arrayList1 = arrayList;
                }
                list.addAll(arrayList1);
            }
            //......分页......
            int end_row = 0;
            if (list.size() > page_num*page_size){
                end_row = page_num*page_size;
            }else {
                end_row = list.size();
            }
            List pageList=new ArrayList();
            for (int i = (page_num-1)*page_size ; i < end_row; i++) {
                pageList.add(list.get(i));
            }

            int count=list.size();
            int pages = 0;
            if (count % page_size == 0) {
                pages = count / page_size;
            } else {
                pages = count / page_size + 1;
            }


            //总数
            int allAnaly=0;
            int allVip=0;
            BasicDBList basicDBList_qr=new BasicDBList();
            for (int i = 0 ; i < qrcodes.length; i++) {
                basicDBList_qr.add(qrcodes[i]);
            }
            BasicDBList basicDBList_all=new BasicDBList();
            BasicDBObject dbObject_all=new BasicDBObject();
            BasicDBObject dbObject1_all=new BasicDBObject();

            //筛选
            if(StringUtils.isNotBlank(nick_name)){
                basicDBList_all.add(new BasicDBObject("nick_name",new BasicDBObject("$regex",nick_name)));
            }
            if(StringUtils.isNotBlank(vip_name)){
                basicDBList_all.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
            }
            if(StringUtils.isNotBlank(cardno)){
                basicDBList_all.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",cardno)));
            }
            if(StringUtils.isNotBlank(vip_phone)){
                basicDBList_all.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
            }
            if(StringUtils.isNotBlank(user_name)){
                basicDBList_all.add(new BasicDBObject("vip.user_name",new BasicDBObject("$regex",user_name)));
            }
            if(StringUtils.isNotBlank(store_name)){
                basicDBList_all.add(new BasicDBObject("vip.store_name",new BasicDBObject("$regex",store_name)));
            }
            if(StringUtils.isNotBlank(join_start)){
                basicDBList_all.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.GTE, join_start)));
            }
            if(StringUtils.isNotBlank(join_end)){
                basicDBList_all.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.LTE, join_end)));
            }

            List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
            basicDBList_all.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
            basicDBList_all.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) +" 23:59:59")));
            basicDBList_all.add(new BasicDBObject("app_id", app_id));
            basicDBList_all.add(new BasicDBObject("qrcode_id", new BasicDBObject("$in",basicDBList_qr)));
            dbObject_all.put("$and", basicDBList_all);
            DBCursor dbCursor=cursor.find(dbObject_all);
            allAnaly=dbCursor.count();
            basicDBList_all.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
            basicDBList_all.add(new BasicDBObject("transVip", "Y"));
            dbObject1_all.put("$and", basicDBList_all);
            DBCursor dbCursor1 = cursor.find(dbObject1_all);
            allVip=dbCursor1.count();


            JSONObject result=new JSONObject();
            result.put("pages",pages);
            result.put("count",count);
            result.put("page_num",page_num);
            result.put("page_size",page_size);
            result.put("list",pageList);
            result.put("allAnaly",allAnaly);
            result.put("allVip",allVip);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //导购二维码  分析 关注人数,新增会员折线图

    @RequestMapping(value = "/analyEmp/view", method = RequestMethod.POST)
    @ResponseBody
    public String getEmpView1(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);
        try {
            JSONObject result = new JSONObject();
            JSONArray array = new JSONArray();

            String param = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String date_screen=jsonObject.get("date").toString();
            JSONObject screen_json= JSON.parseObject(date_screen);
            String start_date=screen_json.getString("start");
            String end_date=screen_json.getString("end");
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
            if(start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(new Date());
                start_date=simpleDateFormat.format(TimeUtils.getNextDay(end_date,30));
            }else if(start_date.equals("")&&!end_date.equals("")) {
                start_date = simpleDateFormat.format(TimeUtils.getNextDay(end_date, 30));
            }else  if(!start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(TimeUtils.getNextDay(start_date,-30));
            }

            String app_id = jsonObject.get("app_id").toString();
            String user_code = jsonObject.get("user_code").toString();
            List<String>dates = TimeUtils.getBetweenDates(start_date,end_date);
            for (int i = 0; i < dates.size(); i++) {
                JSONObject json_data = qrCodeService.getEmpQrcodeScan(cursor,app_id,user_code,dates.get(i));
                array.add(json_data);
            }
            result.put("analyView",array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    //导购二维码列表
    @RequestMapping(value = "/analyEmp/list", method = RequestMethod.POST)
    @ResponseBody
    public String getEmpList1(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);
//        String corp_code = request.getSession().getAttribute("corp_code").toString();
//        String role_code= request.getSession().getAttribute("role_code").toString();
//        if(role_code.equals(Common.ROLE_SYS)){
//            corp_code="C10000";
//        }
        try {
            String param = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String app_id = jsonObject.get("app_id").toString();
            CorpWechat corpWechat=corpService.getCorpByApp(app_id);
            String corp_code=corpWechat.getCorp_code();
            String user_code = jsonObject.get("user_code").toString();

            int page_num=Integer.parseInt(jsonObject.getString("page_num"));
            int page_size=Integer.parseInt(jsonObject.getString("page_size"));

            String date_screen=jsonObject.get("date").toString();
            JSONObject screen_json= JSON.parseObject(date_screen);
            String start_date=screen_json.getString("start");
            String end_date=screen_json.getString("end");
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
            if(start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(new Date());
                start_date=simpleDateFormat.format(TimeUtils.getNextDay(end_date,30));
            }else if(start_date.equals("")&&!end_date.equals("")) {
                start_date = simpleDateFormat.format(TimeUtils.getNextDay(end_date, 30));
            }else  if(!start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(TimeUtils.getNextDay(start_date,-30));
            }

            ArrayList<HashMap<String,Object>> list=new ArrayList();
            String[] userCodes=user_code.split(",");
            //......分页......
            int end_row = 0;
            if (userCodes.length > page_num*page_size){
                end_row = page_num*page_size;
            }else {
                end_row = userCodes.length;
            }
            for (int i = (page_num-1)*page_size ; i < end_row; i++){
                HashMap<String,Object> map=new HashMap<String, Object>();
                //导购信息
                User user=userService.selectUserByCode(corp_code,userCodes[i],"Y");
                String user_code_info=user.getUser_code();
                String user_name_info=user.getUser_name();
                BasicDBList basicDBList=new BasicDBList();
                BasicDBObject dbObject=new BasicDBObject();
                BasicDBObject dbObject1=new BasicDBObject();
                List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
                basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1)+" 23:59:59")));
                basicDBList.add(new BasicDBObject("app_id", app_id));
                //basicDBList.add(new BasicDBObject("corp_code", corp_code));
                basicDBList.add(new BasicDBObject("user_code", userCodes[i]));

                dbObject.put("$and", basicDBList);
                DBCursor dbCursor = cursor.find(dbObject);
                int allCount=dbCursor.count();
                //生成时间
//                BasicDBObject cre_basic=new BasicDBObject();
//                cre_basic.put("app_id",app_id);
//                cre_basic.put("user_code",userCodes[i]);
//                DBObject db_create=cursor.findOne(cre_basic);
//                String create_date="";
//                if(db_create!=null){
//                    create_date =db_create.get("create_date")==null?"":db_create.get("create_date").toString().split(" ")[0];
//                }
                List<UserQrcode> userQrcodes=userService.selectQrcodeByUserApp(corp_code,userCodes[i],app_id);
                String create_date="未生成";
                if(userQrcodes.size()>0){
                    if(StringUtils.isNotBlank(userQrcodes.get(0).getCreated_date())){
                        create_date=userQrcodes.get(0).getCreated_date().toString();
                    }
                }

                basicDBList.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
                basicDBList.add(new BasicDBObject("transVip", "Y"));
                dbObject1.put("$and", basicDBList);
                DBCursor dbCursor1 = cursor.find(dbObject1);
                int newVipCount=dbCursor1.count();

                map.put("user_code",user_code_info);
                map.put("user_name",user_name_info);
                map.put("allCount",allCount);
                map.put("newVipCount",newVipCount);
                map.put("create_date",create_date);
                list.add(map);
            }
            //总数
            int allAnaly=0;
            int allVip=0;
            BasicDBList basicDBList_us=new BasicDBList();
            for (int i = 0 ; i < userCodes.length; i++) {
                basicDBList_us.add(userCodes[i]);
            }
            BasicDBList basicDBList_all=new BasicDBList();
            BasicDBObject dbObject_all=new BasicDBObject();
            BasicDBObject dbObject1_all=new BasicDBObject();

            List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
            basicDBList_all.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
            basicDBList_all.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) +" 23:59:59")));
            basicDBList_all.add(new BasicDBObject("app_id", app_id));
           // basicDBList_all.add(new BasicDBObject("corp_code", corp_code));
            basicDBList_all.add(new BasicDBObject("user_code", new BasicDBObject("$in",basicDBList_us)));
            dbObject_all.put("$and", basicDBList_all);
            DBCursor dbCursor=cursor.find(dbObject_all);
            allAnaly=dbCursor.count();
            basicDBList_all.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
            basicDBList_all.add(new BasicDBObject("transVip", "Y"));
            dbObject1_all.put("$and", basicDBList_all);
            DBCursor dbCursor1 = cursor.find(dbObject1_all);
            allVip=dbCursor1.count();


            int count=userCodes.length;
            int pages = 0;
            if (count % page_size == 0) {
                pages = count / page_size;
            } else {
                pages = count / page_size + 1;
            }
            JSONObject result=new JSONObject();
            result.put("pages",pages);
            result.put("count",count);
            result.put("page_num",page_num);
            result.put("page_size",page_size);
            result.put("list",list);
            result.put("allAnaly",allAnaly);
            result.put("allVip",allVip);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    //导购二维码会员列表
    @RequestMapping(value = "/analyEmp/VipList", method = RequestMethod.POST)
    @ResponseBody
    public String getEmpVipList1(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);
        String corp_code = request.getSession().getAttribute("corp_code").toString();

        try {
            String param = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String app_id = jsonObject.get("app_id").toString();
            String user_code = jsonObject.get("user_code").toString();
            int page_num=Integer.parseInt(jsonObject.getString("page_num"));
            int page_size=Integer.parseInt(jsonObject.getString("page_size"));

            String date_screen=jsonObject.get("date").toString();
            JSONObject screen_json= JSON.parseObject(date_screen);
            String start_date=screen_json.getString("start");
            String end_date=screen_json.getString("end");
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
            if(start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(new Date());
                start_date=simpleDateFormat.format(TimeUtils.getNextDay(end_date,30));
            }else if(start_date.equals("")&&!end_date.equals("")) {
                start_date = simpleDateFormat.format(TimeUtils.getNextDay(end_date, 30));
            }else  if(!start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(TimeUtils.getNextDay(start_date,-30));
            }


            //筛选条件
            String screen=jsonObject.getString("screen");
            JSONObject screen_obj=JSON.parseObject(screen);
            String nick_name=screen_obj.getString("nick_name");
            String vip_name=screen_obj.getString("vip_name");
            String cardno=screen_obj.getString("cardno");
            String vip_phone=screen_obj.getString("vip_phone");
            String user_name=screen_obj.getString("user_name");
            String store_name=screen_obj.getString("store_name");
            String join_date_screen=screen_obj.getString("join_date_screen");
            JSONObject jsonObject1=JSON.parseObject(join_date_screen);
            String join_start=jsonObject1.getString("start");
            String join_end=jsonObject1.getString("end");
            String is_new=screen_obj.getString("is_new");

            ArrayList list=new ArrayList();
            String[] userCodes=user_code.split(",");
            for (int i = 0; i <userCodes.length ; i++) {
                BasicDBList basicDBList=new BasicDBList();
                BasicDBObject dbObject1=new BasicDBObject();
                List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
                basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1)+" 23:59:59")));
                basicDBList.add(new BasicDBObject("app_id", app_id));
                basicDBList.add(new BasicDBObject("user_code", userCodes[i]));
              //  basicDBList.add(new BasicDBObject("corp_code",corp_code));
                basicDBList.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
                basicDBList.add(new BasicDBObject("transVip", "Y"));

                if(StringUtils.isNotBlank(nick_name)){
                    basicDBList.add(new BasicDBObject("nick_name",new BasicDBObject("$regex",nick_name)));
                }
                if(StringUtils.isNotBlank(vip_name)){
                    basicDBList.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
                }
                if(StringUtils.isNotBlank(cardno)){
                    basicDBList.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",cardno)));
                }
                if(StringUtils.isNotBlank(vip_phone)){
                    basicDBList.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
                }
                if(StringUtils.isNotBlank(user_name)){
                    basicDBList.add(new BasicDBObject("vip.user_name",new BasicDBObject("$regex",user_name)));
                }
                if(StringUtils.isNotBlank(store_name)){
                    basicDBList.add(new BasicDBObject("vip.store_name",new BasicDBObject("$regex",store_name)));
                }
                if(StringUtils.isNotBlank(join_start)){
                    basicDBList.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.GTE, join_start)));
                }
                if(StringUtils.isNotBlank(join_end)){
                    basicDBList.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.LTE, join_end)));
                }

                dbObject1.put("$and", basicDBList);
                DBCursor dbCursor1 = cursor.find(dbObject1).sort(new BasicDBObject("modified_date",-1));
                ArrayList list1=qrCodeService.dbCursorToList(dbCursor1,1);

                ArrayList arrayList1 = new ArrayList();
                if (is_new != null && !is_new.equals("")){
                    for (int j = 0; j < list1.size(); j++) {
                        DBObject dbObject = (DBObject)list1.get(j);
                        if (is_new.equals(dbObject.get("is_new"))){
                            arrayList1.add(dbObject);
                        }
                    }
                }else {
                    arrayList1 = list1;
                }
                list.addAll(arrayList1);
            }

            //......分页......
            int end_row = 0;
            if (list.size() > page_num*page_size){
                end_row = page_num*page_size;
            }else {
                end_row = list.size();
            }
            List pageList=new ArrayList();
            for (int i = (page_num-1)*page_size ; i < end_row; i++) {
                pageList.add(list.get(i));
            }

            int count=list.size();
            int pages = 0;
            if (count % page_size == 0) {
                pages = count / page_size;
            } else {
                pages = count / page_size + 1;
            }


            //总数
            int allAnaly=0;
            int allVip=0;
            BasicDBList basicDBList_us=new BasicDBList();
            for (int i = 0 ; i < userCodes.length; i++) {
                basicDBList_us.add(userCodes[i]);
            }
            BasicDBList basicDBList_all=new BasicDBList();
            BasicDBObject dbObject_all=new BasicDBObject();
            BasicDBObject dbObject1_all=new BasicDBObject();

            //筛选
            if(StringUtils.isNotBlank(nick_name)){
                basicDBList_all.add(new BasicDBObject("nick_name",new BasicDBObject("$regex",nick_name)));
            }
            if(StringUtils.isNotBlank(vip_name)){
                basicDBList_all.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
            }
            if(StringUtils.isNotBlank(cardno)){
                basicDBList_all.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",cardno)));
            }
            if(StringUtils.isNotBlank(vip_phone)){
                basicDBList_all.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
            }
            if(StringUtils.isNotBlank(user_name)){
                basicDBList_all.add(new BasicDBObject("vip.user_name",new BasicDBObject("$regex",user_name)));
            }
            if(StringUtils.isNotBlank(store_name)){
                basicDBList_all.add(new BasicDBObject("vip.store_name",new BasicDBObject("$regex",store_name)));
            }
            if(StringUtils.isNotBlank(join_start)){
                basicDBList_all.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.GTE, join_start)));
            }
            if(StringUtils.isNotBlank(join_end)){
                basicDBList_all.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.LTE, join_end)));
            }

            List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
            basicDBList_all.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
            basicDBList_all.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) +" 23:59:59")));
            basicDBList_all.add(new BasicDBObject("app_id", app_id));
           // basicDBList_all.add(new BasicDBObject("corp_code", corp_code));
            basicDBList_all.add(new BasicDBObject("user_code", new BasicDBObject("$in",basicDBList_us)));
            dbObject_all.put("$and", basicDBList_all);
            DBCursor dbCursor=cursor.find(dbObject_all);
            allAnaly=dbCursor.count();
            basicDBList_all.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
            basicDBList_all.add(new BasicDBObject("transVip", "Y"));
            dbObject1_all.put("$and", basicDBList_all);
            DBCursor dbCursor1 = cursor.find(dbObject1_all);
            allVip=dbCursor1.count();

            JSONObject result=new JSONObject();
            result.put("pages",pages);
            result.put("count",count);
            result.put("page_num",page_num);
            result.put("page_size",page_size);
            result.put("list",pageList);
            result.put("allAnaly",allAnaly);
            result.put("allVip",allVip);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //店铺二维码  分析 关注人数,新增会员折线图

    @RequestMapping(value = "/analyStore/view", method = RequestMethod.POST)
    @ResponseBody
    public String getStoreView1(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);
        String corp_code = request.getSession().getAttribute("corp_code").toString();

        try {
            JSONObject result = new JSONObject();
            JSONArray array = new JSONArray();

            String param = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String date_screen=jsonObject.get("date").toString();
            JSONObject screen_json= JSON.parseObject(date_screen);
            String start_date=screen_json.getString("start");
            String end_date=screen_json.getString("end");
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
            if(start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(new Date());
                start_date=simpleDateFormat.format(TimeUtils.getNextDay(end_date,30));
            }else if(start_date.equals("")&&!end_date.equals("")) {
                start_date = simpleDateFormat.format(TimeUtils.getNextDay(end_date, 30));
            }else  if(!start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(TimeUtils.getNextDay(start_date,-30));
            }

            String app_id = jsonObject.get("app_id").toString();
            String store_code = jsonObject.get("store_code").toString();
            List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                for (int i = 0; i < dates.size(); i++) {
                    JSONObject json_data = qrCodeService.getStoreQrcodeScan(cursor, app_id, store_code,dates.get(i));
                    array.add(json_data);
                }
            result.put("analyView",array);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    //店铺二维码列表
    @RequestMapping(value = "/analyStore/list", method = RequestMethod.POST)
    @ResponseBody
    public String getStoreList1(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);
//        String corp_code = request.getSession().getAttribute("corp_code").toString();
//        String role_code= request.getSession().getAttribute("role_code").toString();
//        if(role_code.equals(Common.ROLE_SYS)){
//            corp_code="C10000";
//        }
        try {
            String param = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String app_id = jsonObject.get("app_id").toString();
            CorpWechat corpWechat=corpService.getCorpByApp(app_id);
            String corp_code=corpWechat.getCorp_code();
            String store_code = jsonObject.get("store_code").toString();

            int page_num = Integer.parseInt(jsonObject.getString("page_num"));
            int page_size = Integer.parseInt(jsonObject.getString("page_size"));

            String date_screen = jsonObject.get("date").toString();
            JSONObject screen_json = JSON.parseObject(date_screen);
            String start_date = screen_json.getString("start");
            String end_date = screen_json.getString("end");
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
            if(start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(new Date());
                start_date=simpleDateFormat.format(TimeUtils.getNextDay(end_date,30));
            }else if(start_date.equals("")&&!end_date.equals("")) {
                start_date = simpleDateFormat.format(TimeUtils.getNextDay(end_date, 30));
            }else  if(!start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(TimeUtils.getNextDay(start_date,-30));
            }

            ArrayList<HashMap<String,Object>> list = new ArrayList();
            String[] storeCodes=store_code.split(",");

            //......分页......
            int end_row = 0;
            if (storeCodes.length > page_num*page_size){
                end_row = page_num*page_size;
            }else {
                end_row = storeCodes.length;
            }
            for (int i = (page_num-1)*page_size ; i < end_row; i++){
                HashMap<String,Object> map=new HashMap<String, Object>();
                //店铺信息
                Store store=storeService.getStoreByCode(corp_code,storeCodes[i],"Y");
                String strore_code_info=store.getStore_code();
                String store_name_info=store.getStore_name();
                BasicDBList basicDBList = new BasicDBList();
                BasicDBObject dbObject = new BasicDBObject();
                BasicDBObject dbObject1 = new BasicDBObject();
                List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                basicDBList.add(new BasicDBObject("store_modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0) + " 00:00:00")));
                basicDBList.add(new BasicDBObject("store_modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size()-1) + " 23:59:59")));
                basicDBList.add(new BasicDBObject("app_id", app_id));
               // basicDBList.add(new BasicDBObject("corp_code", corp_code));
                basicDBList.add(new BasicDBObject("store_code", storeCodes[i]));

                dbObject.put("$and", basicDBList);
                DBCursor dbCursor = cursor.find(dbObject);
                int allCount=dbCursor.count();

                //生成时间
//                BasicDBObject cre_basic=new BasicDBObject();
//                cre_basic.put("app_id",app_id);
//                cre_basic.put("store_code",storeCodes[i]);
//                DBObject db_create=cursor.findOne(cre_basic);
//                String create_date="";
//                if(db_create!=null){
//                    create_date =db_create.get("store_create_date")==null?"":db_create.get("store_create_date").toString().split(" ")[0];
//                }
                List<StoreQrcode> storeQrcodes=storeService.selctStoreQrcode(corp_code,storeCodes[i],app_id);
                String create_date="未生成";
                if(storeQrcodes.size()>0){
                    if(StringUtils.isNotBlank(storeQrcodes.get(0).getCreated_date())){
                        create_date=storeQrcodes.get(0).getCreated_date().toString();
                    }
                }
                basicDBList.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
                basicDBList.add(new BasicDBObject("transVip", "Y"));
                dbObject1.put("$and", basicDBList);
                DBCursor  dbCursor1 = cursor.find(dbObject1);
                int newVipCount=dbCursor1.count();

                map.put("store_code",strore_code_info);
                map.put("store_name",store_name_info);
                map.put("allCount",allCount);
                map.put("newVipCount",newVipCount);
                map.put("create_date",create_date);
                list.add(map);
            }

            //总数
            int allAnaly=0;
            int allVip=0;
            BasicDBList basicDBList_st=new BasicDBList();
            for (int i = 0 ; i < storeCodes.length; i++) {
                basicDBList_st.add(storeCodes[i]);
            }
            BasicDBList basicDBList_all=new BasicDBList();
            BasicDBObject dbObject_all=new BasicDBObject();
            BasicDBObject dbObject1_all=new BasicDBObject();

            List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
            basicDBList_all.add(new BasicDBObject("store_modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
            basicDBList_all.add(new BasicDBObject("store_modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) +" 23:59:59")));
            basicDBList_all.add(new BasicDBObject("app_id", app_id));
           // basicDBList_all.add(new BasicDBObject("corp_code", corp_code));
            basicDBList_all.add(new BasicDBObject("store_code", new BasicDBObject("$in",basicDBList_st)));
            dbObject_all.put("$and", basicDBList_all);
            DBCursor dbCursor=cursor.find(dbObject_all);
            allAnaly=dbCursor.count();
            basicDBList_all.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
            basicDBList_all.add(new BasicDBObject("transVip", "Y"));
            dbObject1_all.put("$and", basicDBList_all);
            DBCursor dbCursor1 = cursor.find(dbObject1_all);
            allVip=dbCursor1.count();

            int count=storeCodes.length;
            int pages = 0;
            if (count % page_size == 0) {
                pages = count / page_size;
            } else {
                pages = count / page_size + 1;
            }
            JSONObject result=new JSONObject();
            result.put("pages",pages);
            result.put("count",count);
            result.put("page_num",page_num);
            result.put("page_size",page_size);
            result.put("list",list);
            result.put("allAnaly",allAnaly);
            result.put("allVip",allVip);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    //店铺二维码列表
    @RequestMapping(value = "/analyStore/VipList", method = RequestMethod.POST)
    @ResponseBody
    public String getStoreVipList1(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);
        try {
            String param = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);

            String app_id = jsonObject.get("app_id").toString();
            CorpWechat corpWechat=corpService.getCorpByApp(app_id);
            String corp_code=corpWechat.getCorp_code();
            String store_code = jsonObject.get("store_code").toString();

            int page_num = Integer.parseInt(jsonObject.getString("page_num"));
            int page_size = Integer.parseInt(jsonObject.getString("page_size"));

            String date_screen = jsonObject.get("date").toString();
            JSONObject screen_json = JSON.parseObject(date_screen);
            String start_date = screen_json.getString("start");
            String end_date = screen_json.getString("end");
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
            if(start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(new Date());
                start_date=simpleDateFormat.format(TimeUtils.getNextDay(end_date,30));
            }else if(start_date.equals("")&&!end_date.equals("")) {
                start_date = simpleDateFormat.format(TimeUtils.getNextDay(end_date, 30));
            }else  if(!start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(TimeUtils.getNextDay(start_date,-30));
            }

            //筛选条件
            String screen=jsonObject.getString("screen");
            JSONObject screen_obj=JSON.parseObject(screen);
            String nick_name=screen_obj.getString("nick_name");
            String vip_name=screen_obj.getString("vip_name");
            String cardno=screen_obj.getString("cardno");
            String vip_phone=screen_obj.getString("vip_phone");
            String user_name=screen_obj.getString("user_name");
            String store_name=screen_obj.getString("store_name");
            String join_date_screen=screen_obj.getString("join_date_screen");
            JSONObject jsonObject1=JSON.parseObject(join_date_screen);
            String join_start=jsonObject1.getString("start");
            String join_end=jsonObject1.getString("end");
            String is_new=screen_obj.getString("is_new");

            ArrayList list = new ArrayList();
            String[] storeCodes=store_code.split(",");
            for (int i = 0; i < storeCodes.length; i++) {
                BasicDBList basicDBList = new BasicDBList();
                BasicDBObject dbObject1 = new BasicDBObject();
                List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                basicDBList.add(new BasicDBObject("store_modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0) + " 00:00:00")));
                basicDBList.add(new BasicDBObject("store_modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size()-1) + " 23:59:59")));
                basicDBList.add(new BasicDBObject("app_id", app_id));
                basicDBList.add(new BasicDBObject("store_code", storeCodes[i]));
                basicDBList.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
                basicDBList.add(new BasicDBObject("transVip", "Y"));

                if(StringUtils.isNotBlank(nick_name)){
                    basicDBList.add(new BasicDBObject("nick_name",new BasicDBObject("$regex",nick_name)));
                }
                if(StringUtils.isNotBlank(vip_name)){
                    basicDBList.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
                }
                if(StringUtils.isNotBlank(cardno)){
                    basicDBList.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",cardno)));
                }
                if(StringUtils.isNotBlank(vip_phone)){
                    basicDBList.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
                }
                if(StringUtils.isNotBlank(user_name)){
                    basicDBList.add(new BasicDBObject("vip.user_name",new BasicDBObject("$regex",user_name)));
                }
                if(StringUtils.isNotBlank(store_name)){
                    basicDBList.add(new BasicDBObject("vip.store_name",new BasicDBObject("$regex",store_name)));
                }
                if(StringUtils.isNotBlank(join_start)){
                    basicDBList.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.GTE, join_start)));
                }
                if(StringUtils.isNotBlank(join_end)){
                    basicDBList.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.LTE, join_end)));
                }

                dbObject1.put("$and", basicDBList);
                DBCursor  dbCursor1 = cursor.find(dbObject1).sort(new BasicDBObject("store_modified_date",-1));
                ArrayList arrayList=qrCodeService.dbCursorToList(dbCursor1,2);

                ArrayList arrayList1 = new ArrayList();
                if (is_new != null && !is_new.equals("")){
                    for (int j = 0; j < arrayList.size(); j++) {
                        DBObject dbObject = (DBObject)arrayList.get(j);
                        if (is_new.equals(dbObject.get("is_new"))){
                            arrayList1.add(dbObject);
                        }
                    }
                }else {
                    arrayList1 = arrayList;
                }
                list.addAll(arrayList1);
            }
            //......分页......
            int end_row = 0;
            if (list.size() > page_num*page_size){
                end_row = page_num*page_size;
            }else {
                end_row = list.size();
            }
            List pageList=new ArrayList();
            for (int i = (page_num-1)*page_size ; i < end_row; i++) {
                pageList.add(list.get(i));
            }

            int count=list.size();
            int pages = 0;
            if (count % page_size == 0) {
                pages = count / page_size;
            } else {
                pages = count / page_size + 1;
            }


            //总数
            int allAnaly=0;
            int allVip=0;
            BasicDBList basicDBList_st=new BasicDBList();
            for (int i = 0 ; i < storeCodes.length; i++) {
                basicDBList_st.add(storeCodes[i]);
            }
            BasicDBList basicDBList_all=new BasicDBList();
            BasicDBObject dbObject_all=new BasicDBObject();
            BasicDBObject dbObject1_all=new BasicDBObject();

            //筛选
            if(StringUtils.isNotBlank(nick_name)){
                basicDBList_all.add(new BasicDBObject("nick_name",new BasicDBObject("$regex",nick_name)));
            }
            if(StringUtils.isNotBlank(vip_name)){
                basicDBList_all.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
            }
            if(StringUtils.isNotBlank(cardno)){
                basicDBList_all.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",cardno)));
            }
            if(StringUtils.isNotBlank(vip_phone)){
                basicDBList_all.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
            }
            if(StringUtils.isNotBlank(user_name)){
                basicDBList_all.add(new BasicDBObject("vip.user_name",new BasicDBObject("$regex",user_name)));
            }
            if(StringUtils.isNotBlank(store_name)){
                basicDBList_all.add(new BasicDBObject("vip.store_name",new BasicDBObject("$regex",store_name)));
            }
            if(StringUtils.isNotBlank(join_start)){
                basicDBList_all.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.GTE, join_start)));
            }
            if(StringUtils.isNotBlank(join_end)){
                basicDBList_all.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.LTE, join_end)));
            }

            List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
            basicDBList_all.add(new BasicDBObject("store_modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
            basicDBList_all.add(new BasicDBObject("store_modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) +" 23:59:59")));
            basicDBList_all.add(new BasicDBObject("app_id", app_id));
           // basicDBList_all.add(new BasicDBObject("corp_code", corp_code));
            basicDBList_all.add(new BasicDBObject("store_code", new BasicDBObject("$in",basicDBList_st)));
            dbObject_all.put("$and", basicDBList_all);
            DBCursor dbCursor=cursor.find(dbObject_all);
            allAnaly=dbCursor.count();
            basicDBList_all.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
            basicDBList_all.add(new BasicDBObject("transVip", "Y"));
            dbObject1_all.put("$and", basicDBList_all);
            DBCursor dbCursor1 = cursor.find(dbObject1_all);
            allVip=dbCursor1.count();

            JSONObject result=new JSONObject();
            result.put("pages",pages);
            result.put("count",count);
            result.put("page_num",page_num);
            result.put("page_size",page_size);
            result.put("list",pageList);
            result.put("allAnaly",allAnaly);
            result.put("allVip",allVip);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    //导出关注度分析表
    @RequestMapping(value = "/exportExeclAnaly",method = RequestMethod.POST)
    @ResponseBody
    public String exportExeclAnaly(HttpServletRequest request, HttpServletResponse response){
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        String exportmessage="";
        try{
            MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_qrcode_relation);
            DBCollection cursor_user_store = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);
            String param = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String date_screen=jsonObject.get("date").toString();
            JSONObject screen_json= JSON.parseObject(date_screen);
            String start_date=screen_json.getString("start");
            String end_date=screen_json.getString("end");
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
            if(start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(new Date());
                start_date=simpleDateFormat.format(TimeUtils.getNextDay(end_date,30));
            }else if(start_date.equals("")&&!end_date.equals("")) {
                start_date = simpleDateFormat.format(TimeUtils.getNextDay(end_date, 30));
            }else  if(!start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(TimeUtils.getNextDay(start_date,-30));
            }

            String app_id = jsonObject.get("app_id").toString();
            String corp_code = jsonObject.getString("corp_code");
            if(StringUtils.isBlank(corp_code)){
                CorpWechat corpWechat=corpService.getCorpByApp(app_id);
                corp_code=corpWechat.getCorp_code();
            }
            String type=jsonObject.getString("type");

            /**
             * 导出操作..................................................
             */
            List<HashMap<String,Object>> list=new ArrayList<HashMap<String, Object>>();
            if(type.equals("qrcode")){
                exportmessage="渠道二维码关注度分析表";
                String qrcode_id = jsonObject.get("qrcode_id").toString();
                String[] qrcodes=qrcode_id.split(",");
                for (int i = 0; i < qrcodes.length; i++) {
                    HashMap<String,Object> map=new HashMap<String, Object>();
                    BasicDBList basicDBList=new BasicDBList();
                    BasicDBObject dbObject=new BasicDBObject();
                    BasicDBObject dbObject1=new BasicDBObject();
                    //二维码信息
                    QrCode qrcode=qrCodeService.selectQrCodeById(Integer.parseInt(qrcodes[i]));
                    String qrcode_name_info=qrcode.getQrcode_name();
                    if (qrcode.getQrcode_type().equals("print"))
                        qrcode.setQrcode_type("印刷类");
                    if (qrcode.getQrcode_type().equals("material"))
                        qrcode.setQrcode_type("材料类");
                    if (qrcode.getQrcode_type().equals("gift"))
                        qrcode.setQrcode_type("礼品类");
                    String create_date=qrcode.getCreated_date().split(" ")[0];

                    List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                    basicDBList.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
                    basicDBList.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) +" 23:59:59")));
                    basicDBList.add(new BasicDBObject("app_id", app_id));
                    basicDBList.add(new BasicDBObject("qrcode_id", qrcodes[i]));
                    dbObject.put("$and", basicDBList);
                    DBCursor dbCursor=cursor.find(dbObject);
                    int allCount=dbCursor.count();
                    basicDBList.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
                    basicDBList.add(new BasicDBObject("transVip", "Y"));
                    dbObject1.put("$and", basicDBList);
                    DBCursor dbCursor1 = cursor.find(dbObject1);
                    int newVipCount=dbCursor1.count();
                    map.put("qrcode_name",qrcode_name_info);
                    map.put("allCount",allCount);
                    map.put("newVipCount",newVipCount);
                    map.put("qrcode_type",qrcode.getQrcode_type());
                    map.put("create_date",create_date);
                    list.add(map);
                }
            }else if(type.equals("user")){
                exportmessage="导购二维码关注度分析表";
                String user_code = jsonObject.get("user_code").toString();
                String[] userCodes=user_code.split(",");
                for (int i = 0; i < userCodes.length; i++){
                    HashMap<String,Object> map=new HashMap<String, Object>();
                    //导购信息
                    User user=userService.selectUserByCode(corp_code,userCodes[i],"Y");
                    String user_code_info=user.getUser_code();
                    String user_name_info=user.getUser_name();
                    BasicDBList basicDBList=new BasicDBList();
                    BasicDBObject dbObject=new BasicDBObject();
                    BasicDBObject dbObject1=new BasicDBObject();
                    List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1)+" 23:59:59")));
                    basicDBList.add(new BasicDBObject("app_id", app_id));
                    //basicDBList.add(new BasicDBObject("corp_code", corp_code));
                    basicDBList.add(new BasicDBObject("user_code", userCodes[i]));

                    dbObject.put("$and", basicDBList);
                    DBCursor dbCursor = cursor_user_store.find(dbObject);
                    int allCount=dbCursor.count();

                    //生成时间
//                    BasicDBObject cre_basic=new BasicDBObject();
//                    cre_basic.put("app_id",app_id);
//                    cre_basic.put("user_code",userCodes[i]);
//                    DBObject db_create=cursor_user_store.findOne(cre_basic);
//                    String create_date="";
//                    if(db_create!=null){
//                        create_date =db_create.get("create_date")==null?"":db_create.get("create_date").toString().split(" ")[0];
//                    }
                    List<UserQrcode> userQrcodes=userService.selectQrcodeByUserApp(corp_code,userCodes[i],app_id);
                    String create_date="未生成";
                    if(userQrcodes.size()>0){
                        if(StringUtils.isNotBlank(userQrcodes.get(0).getCreated_date())){
                            create_date=userQrcodes.get(0).getCreated_date().toString();
                        }
                    }

                    basicDBList.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
                    basicDBList.add(new BasicDBObject("transVip", "Y"));
                    dbObject1.put("$and", basicDBList);
                    DBCursor dbCursor1 = cursor_user_store.find(dbObject1);
                    int newVipCount=dbCursor1.count();

                    map.put("user_code",user_code_info);
                    map.put("user_name",user_name_info);
                    map.put("allCount",allCount);
                    map.put("newVipCount",newVipCount);
                    map.put("create_date",create_date);
                    list.add(map);
                }

            }else if(type.equals("store")){
                exportmessage="店铺二维码关注度分析表";
                String store_code = jsonObject.get("store_code").toString();
                String[] storeCodes=store_code.split(",");
                for (int i = 0 ; i < storeCodes.length; i++){
                    HashMap<String,Object> map=new HashMap<String, Object>();
                    //店铺信息
                    Store store=storeService.getStoreByCode(corp_code,storeCodes[i],"Y");
                    String strore_code_info=store.getStore_code();
                    String store_name_info=store.getStore_name();
                    BasicDBList basicDBList = new BasicDBList();
                    BasicDBObject dbObject = new BasicDBObject();
                    BasicDBObject dbObject1 = new BasicDBObject();
                    List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                    basicDBList.add(new BasicDBObject("store_modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0) + " 00:00:00")));
                    basicDBList.add(new BasicDBObject("store_modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size()-1) + " 23:59:59")));
                    basicDBList.add(new BasicDBObject("app_id", app_id));
                   // basicDBList.add(new BasicDBObject("corp_code", corp_code));
                    basicDBList.add(new BasicDBObject("store_code", storeCodes[i]));

                    dbObject.put("$and", basicDBList);
                    DBCursor dbCursor = cursor_user_store.find(dbObject);
                    int allCount=dbCursor.count();

                    //生成时间
//                    BasicDBObject cre_basic=new BasicDBObject();
//                    cre_basic.put("app_id",app_id);
//                    cre_basic.put("store_code",storeCodes[i]);
//                    DBObject db_create=cursor_user_store.findOne(cre_basic);
//                    String create_date="";
//                    if(db_create!=null){
//                        create_date =db_create.get("create_date")==null?"":db_create.get("create_date").toString().split(" ")[0];
//                    }
                    List<StoreQrcode> storeQrcodes=storeService.selctStoreQrcode(corp_code,storeCodes[i],app_id);
                    String create_date="未生成";
                    if(storeQrcodes.size()>0){
                        if(StringUtils.isNotBlank(storeQrcodes.get(0).getCreated_date())){
                            create_date=storeQrcodes.get(0).getCreated_date().toString();
                        }
                    }

                    basicDBList.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
                    basicDBList.add(new BasicDBObject("transVip", "Y"));
                    dbObject1.put("$and", basicDBList);
                    DBCursor  dbCursor1 = cursor_user_store.find(dbObject1);
                    int newVipCount=dbCursor1.count();

                    map.put("store_code",strore_code_info);
                    map.put("store_name",store_name_info);
                    map.put("allCount",allCount);
                    map.put("newVipCount",newVipCount);
                    map.put("create_date",create_date);
                    list.add(map);
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(list);
            if (list.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, list, map, response, request,exportmessage);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
            //导出操作结束.........................................................
        }catch (Exception e){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return  dataBean.getJsonStr();
    }

    //导出新增会员分析表
    @RequestMapping(value = "/exportExeclNewVip",method = RequestMethod.POST)
    @ResponseBody
    public String exportExeclNewVip(HttpServletRequest request, HttpServletResponse response){
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        String exportmessage="";
        try{
            MongoTemplate mongoTemplate = mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_qrcode_relation);
            DBCollection cursor_user_store = mongoTemplate.getCollection(CommonValue.table_vip_emp_relation);
            String param = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String date_screen=jsonObject.get("date").toString();
            JSONObject screen_json= JSON.parseObject(date_screen);
            String start_date=screen_json.getString("start");
            String end_date=screen_json.getString("end");
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
            if(start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(new Date());
                start_date=simpleDateFormat.format(TimeUtils.getNextDay(end_date,30));
            }else if(start_date.equals("")&&!end_date.equals("")) {
                start_date = simpleDateFormat.format(TimeUtils.getNextDay(end_date, 30));
            }else  if(!start_date.equals("")&&end_date.equals("")){
                end_date=simpleDateFormat.format(TimeUtils.getNextDay(start_date,-30));
            }

            String app_id = jsonObject.get("app_id").toString();

            String type=jsonObject.getString("type");


            //筛选条件
            String screen=jsonObject.getString("screen");
            JSONObject screen_obj=JSON.parseObject(screen);
            String nick_name=screen_obj.getString("nick_name");
            String vip_name=screen_obj.getString("vip_name");
            String cardno=screen_obj.getString("cardno");
            String vip_phone=screen_obj.getString("vip_phone");
            String user_name=screen_obj.getString("user_name");
            String store_name=screen_obj.getString("store_name");
            String join_date_screen=screen_obj.getString("join_date_screen");
            JSONObject jsonObject1=JSON.parseObject(join_date_screen);
            String join_start=jsonObject1.getString("start");
            String join_end=jsonObject1.getString("end");
            String is_new=screen_obj.getString("is_new");

            /**
             * 导出操作..................................................
             */
            List list=new ArrayList();
            if(type.equals("qrcode")){

                exportmessage="渠道二维码明细列表";
                String qrcode_id = jsonObject.get("qrcode_id").toString();
                String[] qrcodes=qrcode_id.split(",");
                for (int i = 0; i < qrcodes.length; i++) {
                    BasicDBObject dbObject1=new BasicDBObject();
                    List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                    BasicDBList basicDBList=new BasicDBList();
                    basicDBList.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
                    basicDBList.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1) +" 23:59:59")));
                    basicDBList.add(new BasicDBObject("app_id", app_id));
                    basicDBList.add(new BasicDBObject("qrcode_id", qrcodes[i]));
                    basicDBList.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
                    basicDBList.add(new BasicDBObject("transVip", "Y"));

                    if(StringUtils.isNotBlank(nick_name)){
                        basicDBList.add(new BasicDBObject("nick_name",new BasicDBObject("$regex",nick_name)));
                    }
                    if(StringUtils.isNotBlank(vip_name)){
                        basicDBList.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
                    }
                    if(StringUtils.isNotBlank(cardno)){
                        basicDBList.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",cardno)));
                    }
                    if(StringUtils.isNotBlank(vip_phone)){
                        basicDBList.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
                    }
                    if(StringUtils.isNotBlank(user_name)){
                        basicDBList.add(new BasicDBObject("vip.user_name",new BasicDBObject("$regex",user_name)));
                    }
                    if(StringUtils.isNotBlank(store_name)){
                        basicDBList.add(new BasicDBObject("vip.store_name",new BasicDBObject("$regex",store_name)));
                    }
                    if(StringUtils.isNotBlank(join_start)){
                        basicDBList.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.GTE, join_start)));
                    }
                    if(StringUtils.isNotBlank(join_end)){
                        basicDBList.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.LTE, join_end)));
                    }

                    dbObject1.put("$and", basicDBList);
                    DBCursor dbCursor1 = cursor.find(dbObject1).sort(new BasicDBObject("create_date",-1));
                    ArrayList arrayList=qrCodeService.dbCursorToListForExecl(dbCursor1,0);
                    ArrayList arrayList1 = new ArrayList();
                    if (is_new != null && !is_new.equals("")){
                        for (int j = 0; j < arrayList.size(); j++) {
                            DBObject dbObject = (DBObject)arrayList.get(j);
                            if (is_new.equals(dbObject.get("is_new"))){
                                arrayList1.add(dbObject);
                            }
                        }
                    }else {
                        arrayList1 = arrayList;
                    }
                    list.addAll(arrayList1);
                }
            }else if(type.equals("user")){
                exportmessage="导购二维码明细列表";
                String user_code = jsonObject.get("user_code").toString();
                String[] userCodes=user_code.split(",");
                for (int i = 0; i <userCodes.length ; i++) {
                    BasicDBList basicDBList=new BasicDBList();
                    BasicDBObject dbObject1=new BasicDBObject();
                    List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0)+" 00:00:00")));
                    basicDBList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size() - 1)+" 23:59:59")));
                    basicDBList.add(new BasicDBObject("app_id", app_id));
                    basicDBList.add(new BasicDBObject("user_code", userCodes[i]));
                    basicDBList.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
                    basicDBList.add(new BasicDBObject("transVip", "Y"));

                    if(StringUtils.isNotBlank(nick_name)){
                        basicDBList.add(new BasicDBObject("nick_name",new BasicDBObject("$regex",nick_name)));
                    }
                    if(StringUtils.isNotBlank(vip_name)){
                        basicDBList.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
                    }
                    if(StringUtils.isNotBlank(cardno)){
                        basicDBList.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",cardno)));
                    }
                    if(StringUtils.isNotBlank(vip_phone)){
                        basicDBList.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
                    }
                    if(StringUtils.isNotBlank(user_name)){
                        basicDBList.add(new BasicDBObject("vip.user_name",new BasicDBObject("$regex",user_name)));
                    }
                    if(StringUtils.isNotBlank(store_name)){
                        basicDBList.add(new BasicDBObject("vip.store_name",new BasicDBObject("$regex",store_name)));
                    }
                    if(StringUtils.isNotBlank(join_start)){
                        basicDBList.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.GTE, join_start)));
                    }
                    if(StringUtils.isNotBlank(join_end)){
                        basicDBList.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.LTE, join_end)));
                    }

                    dbObject1.put("$and", basicDBList);
                    DBCursor dbCursor1 = cursor_user_store.find(dbObject1).sort(new BasicDBObject("modified_date",-1));
                    ArrayList arrayList=qrCodeService.dbCursorToListForExecl(dbCursor1,1);
                    ArrayList arrayList1 = new ArrayList();
                    if (is_new != null && !is_new.equals("")){
                        for (int j = 0; j < arrayList.size(); j++) {
                            DBObject dbObject = (DBObject)arrayList.get(j);
                            if (is_new.equals(dbObject.get("is_new"))){
                                arrayList1.add(dbObject);
                            }
                        }
                    }else {
                        arrayList1 = arrayList;
                    }
                    list.addAll(arrayList1);
                }
            }else if(type.equals("store")){
                exportmessage="店铺二维码明细列表";
                String store_code = jsonObject.get("store_code").toString();
                String[] storeCodes=store_code.split(",");
                for (int i = 0; i < storeCodes.length; i++) {
                    BasicDBList basicDBList = new BasicDBList();
                    BasicDBObject dbObject1 = new BasicDBObject();
                    List<String> dates = TimeUtils.getBetweenDates(start_date,end_date);
                    basicDBList.add(new BasicDBObject("store_modified_date", new BasicDBObject(QueryOperators.GTE, dates.get(0) + " 00:00:00")));
                    basicDBList.add(new BasicDBObject("store_modified_date", new BasicDBObject(QueryOperators.LTE, dates.get(dates.size()-1) + " 23:59:59")));
                    basicDBList.add(new BasicDBObject("app_id", app_id));
                    basicDBList.add(new BasicDBObject("store_code", storeCodes[i]));
                    basicDBList.add(new BasicDBObject("vip", new BasicDBObject("$ne", null)));
                    basicDBList.add(new BasicDBObject("transVip", "Y"));

                    if(StringUtils.isNotBlank(nick_name)){
                        basicDBList.add(new BasicDBObject("nick_name",new BasicDBObject("$regex",nick_name)));
                    }
                    if(StringUtils.isNotBlank(vip_name)){
                        basicDBList.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
                    }
                    if(StringUtils.isNotBlank(cardno)){
                        basicDBList.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",cardno)));
                    }
                    if(StringUtils.isNotBlank(vip_phone)){
                        basicDBList.add(new BasicDBObject("vip.vip_phone",new BasicDBObject("$regex",vip_phone)));
                    }
                    if(StringUtils.isNotBlank(user_name)){
                        basicDBList.add(new BasicDBObject("vip.user_name",new BasicDBObject("$regex",user_name)));
                    }
                    if(StringUtils.isNotBlank(store_name)){
                        basicDBList.add(new BasicDBObject("vip.store_name",new BasicDBObject("$regex",store_name)));
                    }
                    if(StringUtils.isNotBlank(join_start)){
                        basicDBList.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.GTE, join_start)));
                    }
                    if(StringUtils.isNotBlank(join_end)){
                        basicDBList.add(new BasicDBObject("vip.join_date", new BasicDBObject(QueryOperators.LTE, join_end)));
                    }

                    dbObject1.put("$and", basicDBList);
                    DBCursor  dbCursor1 = cursor_user_store.find(dbObject1).sort(new BasicDBObject("store_modified_date",-1));;
                    ArrayList arrayList=qrCodeService.dbCursorToListForExecl(dbCursor1,2);
                    ArrayList arrayList1 = new ArrayList();
                    if (is_new != null && !is_new.equals("")){
                        for (int j = 0; j < arrayList.size(); j++) {
                            DBObject dbObject = (DBObject)arrayList.get(j);
                            if (is_new.equals(dbObject.get("is_new"))){
                                arrayList1.add(dbObject);
                            }
                        }
                    }else {
                        arrayList1 = arrayList;
                    }
                    list.addAll(arrayList1);
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(list);
            if (list.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, list, map, response, request,exportmessage);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
            //导出操作结束.........................................................
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return  dataBean.getJsonStr();
    }
}
