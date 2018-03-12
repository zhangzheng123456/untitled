package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.VipLabelsRel;
import com.bizvane.ishop.service.VipLabelsRelService;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by yanyadong on 2017/2/16.
 */
@Controller
@RequestMapping("/vipLabelsRel")
public class VipLabelsRelController {

    @Autowired
    VipLabelsRelService vipLabelsRelService;

    @Autowired
    MongoDBClient mongodbClient;


    @RequestMapping(value = "/search",method = RequestMethod.POST)
    @ResponseBody
    public String selectAllLabel(HttpServletRequest request, HttpServletResponse response){
        DataBean dataBean = new DataBean();
        int total=0;
        int pages=0;
        try{
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code =request.getSession().getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj=JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject=JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();

         /*   PageInfo<VipLabelsRel> list = null;
            if(role_code.equals(Common.ROLE_SYS)) {
                list = vipLabelsRelService.selectAllLabel(page_number, page_size, "", search_value);
            }else{
                list = vipLabelsRelService.selectAllLabel(page_number, page_size, corp_code, search_value);
            }*/
         /*****************************从MongoDB查询***************************************/
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_log);
            DBCursor dbCursor=null;
            BasicDBList basicDBList=new BasicDBList();
            BasicDBObject basicDBObject=new BasicDBObject();
            if(!role_code.equals(Common.ROLE_SYS)){
             basicDBList.add(new BasicDBObject("corp_code",corp_code));
            }
            if(StringUtils.isNotBlank(search_value)){
                Pattern pattern = Pattern.compile("^.*" + search_value + ".*$", Pattern.CASE_INSENSITIVE);
                BasicDBList search_list=new BasicDBList();
                BasicDBObject search_obj=new BasicDBObject();
                search_list.add(new BasicDBObject("vip.cardno",pattern));
                search_list.add(new BasicDBObject("vip.vip_name",pattern));
                search_list.add(new BasicDBObject("label_name",pattern));
                search_obj.put("$or",search_list);
                basicDBList.add(search_obj);
            }
            if(basicDBList.size()>0){
                basicDBObject.put("$and",basicDBList);
            }
            dbCursor=cursor.find(basicDBObject);
            total = dbCursor.count();
            pages = MongoUtils.getPages(dbCursor, page_size);
            DBCursor  dbCursor1 = MongoUtils.sortAndPage(dbCursor, page_number, page_size, "created_date", -1);
            ArrayList list= MongoUtils.dbCursorToList_id(dbCursor1);
            List<VipLabelsRel> list_rel=vipLabelsRelService.switchDbCurSor(list);
            JSONObject result = new JSONObject();
            JSONObject json = new JSONObject();
            json.put("pageNum",page_number);
            json.put("pageSize",page_size);
            json.put("pages",pages);
            json.put("total",total);
            json.put("list",list_rel);
            result.put("list",json.toString());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch(Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }

        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public String delActivityVipLabelById(HttpServletRequest request, HttpServletResponse response){
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String id = jsonObject.get("id").toString();
            String[] ids=id.split(",");
           /* for(int i=0;i<ids.length;i++){
                vipLabelsRelService.delActivityVipLabelById(Integer.parseInt(ids[i]));
            }*/
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_log);
            for (int i = 0; i <ids.length ; i++) {
                cursor.remove(new BasicDBObject("_id",new ObjectId(ids[i])));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        }catch(Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(e.getMessage());
        }

        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/screen",method = RequestMethod.POST)
    @ResponseBody
    public String selectAllScreen(HttpServletRequest request,HttpServletResponse response){
        DataBean dataBean = new DataBean();
        int total=0;
        int pages=0;
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code =request.getSession().getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_log);
            DBCursor dbCursor=null;
            BasicDBObject basic_obj=vipLabelsRelService.getScreenObject(jsonObject);

            BasicDBList basicDBList=new BasicDBList();
            BasicDBObject basicDBObject=new BasicDBObject();
            if(!role_code.equals(Common.ROLE_SYS)){
                basicDBList.add(new BasicDBObject("corp_code",corp_code));
            }
            basicDBList.add(basic_obj);
            basicDBObject.put("$and",basicDBList);
            dbCursor=cursor.find(basicDBObject);

            total = dbCursor.count();
            pages = MongoUtils.getPages(dbCursor, page_size);
            DBCursor  dbCursor1 = MongoUtils.sortAndPage(dbCursor, page_number, page_size, "created_date", -1);
            ArrayList list= MongoUtils.dbCursorToList_id(dbCursor1);
            List<VipLabelsRel> list_rel=vipLabelsRelService.switchDbCurSor(list);
            JSONObject result = new JSONObject();
            JSONObject json=new JSONObject();
            json.put("pageNum",page_number);
            json.put("pageSize",page_size);
            json.put("pages",pages);
            json.put("total",total);
            json.put("list",list_rel);
            result.put("list",json.toString());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch(Exception e){
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
            int page_num = Integer.parseInt(jsonObject.get("page_num").toString());
            int page_size = Integer.parseInt(jsonObject.get("page_size").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            /*PageInfo<VipLabelsRel> list;
            if(role_code.equals(Common.ROLE_SYS)) {
                if (screen.equals("")) {
                    list = vipLabelsRelService.selectAllLabel(page_num, page_size, "", search_value);
                } else {
                    Map<String, String> map = WebUtils.Json2Map(jsonObject);
                    list = vipLabelsRelService.selectAllScreen(page_num, page_size, "", map);

                }
            }else{
                if (screen.equals("")) {
                    list = vipLabelsRelService.selectAllLabel(page_num, page_size, corp_code, search_value);
                } else {
                    Map<String, String> map = WebUtils.Json2Map(jsonObject);
                    list = vipLabelsRelService.selectAllScreen(page_num, page_size, corp_code, map);

                }

            }*/
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_label_log);
            DBCursor dbCursor=null;
            BasicDBList basicDBList=new BasicDBList();
            BasicDBObject basicDBObject=new BasicDBObject();
            if(!role_code.equals(Common.ROLE_SYS)){
                basicDBList.add(new BasicDBObject("corp_code",corp_code));
            }
            if(!screen.equals("")){
                BasicDBObject basic_obj=vipLabelsRelService.getScreenObject(jsonObject);
                basicDBList.add(basic_obj);
            }else{
                if(StringUtils.isNotBlank(search_value)){
                    Pattern pattern = Pattern.compile("^.*" + search_value + ".*$", Pattern.CASE_INSENSITIVE);
                    BasicDBList search_list=new BasicDBList();
                    BasicDBObject search_obj=new BasicDBObject();
                    search_list.add(new BasicDBObject("vip.cardno",pattern));
                    search_list.add(new BasicDBObject("vip.vip_name",pattern));
                    search_list.add(new BasicDBObject("label_name",pattern));
                    search_obj.put("$or",search_list);
                    basicDBList.add(search_obj);
                }
            }
            if(basicDBList.size()>0){
                basicDBObject.put("$and",basicDBList);
            }
            dbCursor=cursor.find(basicDBObject);
            DBCursor  dbCursor1 = MongoUtils.sortAndPage(dbCursor, page_num, page_size, "created_date", -1);
            ArrayList list= MongoUtils.dbCursorToList_id(dbCursor1);
            List<VipLabelsRel> list_rel=vipLabelsRelService.switchDbCurSor(list);
            /**
             * 导出操作..................................................
             */
 //           List<VipLabelsRel> labels = list.getList();
 //           int count = (int)list.getTotal();
            int count=list_rel.size();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(list_rel);
            if (list_rel.size() >= page_size * 2) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            int start_line = (page_num-1) * page_size + 1;
            int end_line = page_num*page_size;
            if (count < page_num*page_size)
                end_line = count;
            String pathname = OutExeclHelper.OutExecl(json, list_rel, map, response, request,"会员标签("+start_line+"-"+end_line+")");
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
