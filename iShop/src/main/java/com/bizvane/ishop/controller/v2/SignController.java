package com.bizvane.ishop.controller.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.controller.InterfaceController;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.SignService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by yin on 2016/6/23.
 */
@Controller
@RequestMapping("/sign")
public class SignController {
    @Autowired
    private SignService signService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private BaseService baseService;
    String id;
    @Autowired
   private MongoDBClient mongodbClient;
    private static final Logger logger = Logger.getLogger(InterfaceController.class);


   /**
    * Mongodb
    * 列表
    */
   @RequestMapping(value = "/list", method = RequestMethod.GET)
   @ResponseBody
   public  String selectAll(HttpServletRequest request){
       DataBean dataBean=new DataBean();
       JSONObject result = new JSONObject();
       int pages = 0;
       try{
           String role_code = request.getSession().getAttribute("role_code").toString();
           String group_code = request.getSession().getAttribute("group_code").toString();
           String corp_code = request.getSession().getAttribute("corp_code").toString();
           String user_code = request.getSession().getAttribute("user_code").toString();
           int page_number = Integer.parseInt(request.getParameter("pageNumber"));
           int page_size = Integer.parseInt(request.getParameter("pageSize"));
           MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
           DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sign_content);
           DBCursor dbCursor = null;
           if (role_code.equals(Common.ROLE_SYS)) {
               //系统管理员
               DBCursor  dbCursor1=cursor.find();
               pages = MongoUtils.getPages(dbCursor1,page_size);
               dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);

           } else if (role_code.equals(Common.ROLE_GM)) {
               //企业管理员

               Map keyMap = new HashMap();
               keyMap.put("corp_code", corp_code);
              // keyMap.put("role_code", role_code);
               BasicDBObject ref = new BasicDBObject();
               ref.putAll(keyMap);
               DBCursor dbCursor1 = cursor.find(ref);
               pages = MongoUtils.getPages(dbCursor1,page_size);
               dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);

           } else if (role_code.equals(Common.ROLE_BM)) {
               //品牌管理员
               String brand_code = request.getSession().getAttribute("brand_code").toString();
               brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
               List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
               String store_code = "";
               BasicDBList value = new BasicDBList();

               for (int i = 0; i < stores.size(); i++) {
                   store_code = stores.get(i).getStore_code().toString();
                   value.add(store_code);
               }
               BasicDBObject ba=new BasicDBObject();
               BasicDBList values = new BasicDBList();
               if(value!=null&&value.size()>0) {
                   values.add(new BasicDBObject("corp_code", corp_code));
                   values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
               }else{
                   values.add(new BasicDBObject("corp_code", corp_code));
                   values.add(new BasicDBObject("user_code", user_code));
               }
               values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                ba.put("$and",values);
               DBCursor dbCursor1 = cursor.find(ba);

                   pages = MongoUtils.getPages(dbCursor1, page_size);
                   dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, "modified_date", -1);



           }else if (role_code.equals(Common.ROLE_SM)) {
               //店长
               String store_code = request.getSession().getAttribute("store_code").toString();
               String[] stores = null;
               if (!store_code.equals("")) {
                   store_code = store_code.replace(Common.SPECIAL_HEAD,"");
                   stores = store_code.split(",");
               }
               BasicDBList value = new BasicDBList();

               for (int i = 0; i < stores.length; i++) {
                   //       store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                   store_code = stores[i].toString();
                   value.add(store_code);
               }
               BasicDBObject ba=new BasicDBObject();
               BasicDBList values = new BasicDBList();
               if(value!=null&&value.size()>0) {
                   values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                   values.add(new BasicDBObject("corp_code",corp_code));
               }else{
                   values.add(new BasicDBObject("corp_code", corp_code));
                   values.add(new BasicDBObject("user_code", user_code));
               }
               values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));

               ba.put("$and",values);
               DBCursor dbCursor1 = cursor.find(ba);

               pages = MongoUtils.getPages(dbCursor1,page_size);
               dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);




           } else if (role_code.equals(Common.ROLE_AM)) {
               //区经
               String area_code = request.getSession().getAttribute("area_code").toString();
               String store_code = request.getSession().getAttribute("store_code").toString();
               //添加.......
               BasicDBList value = new BasicDBList();
               BasicDBObject ref=new BasicDBObject();
               //......
               if (!area_code.equals("")) {
                   area_code = area_code.replace(Common.SPECIAL_HEAD,"");
                   String[] areas = area_code.split(",");
                   String[] storeCodes = null;
                   if (!store_code.equals("")){
                       store_code = store_code.replace(Common.SPECIAL_HEAD,"");
                       storeCodes = store_code.split(",");
                   }
                   List<Store> store = storeService.selectByAreaBrand(corp_code, areas,storeCodes,null, "");
                   for (int i = 0; i < store.size(); i++) {
                       store_code = store.get(i).getStore_code().toString();
                       value.add(store_code);
                   }
                   BasicDBList values = new BasicDBList();
                   if(value!=null&&value.size()>0) {
                       values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                       values.add(new BasicDBObject("corp_code", corp_code));
                   }else{

                       values.add(new BasicDBObject("corp_code", corp_code));
                       values.add(new BasicDBObject("user_code", user_code));
                   }
                   values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                   ref.put("$and",values);
                   DBCursor dbCursor1 = cursor.find(ref);

                   pages = MongoUtils.getPages(dbCursor1,page_size);
                   dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);

               }



           } else if (role_code.equals(Common.ROLE_STAFF)) {

               BasicDBList basicDBList=new BasicDBList();
               basicDBList.add(new BasicDBObject("corp_code", corp_code));
               basicDBList.add(new BasicDBObject("user_code",user_code));
               basicDBList.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
               BasicDBObject basicDBObject=new BasicDBObject();
               basicDBObject.put("$and",basicDBList);
               DBCursor dbCursor1 = cursor.find(basicDBObject);
               pages = MongoUtils.getPages(dbCursor1,page_size);
               dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);



           }
           //  System.out.println(list.getList().get(0).getSign_time()+"---"+list.getList().get(0).getUser_code());
           ArrayList list = MongoHelperServiceImpl.dbCursorToList_status(dbCursor);
           result.put("list", list);
           result.put("pages", pages);
           result.put("page_number", page_number);
           result.put("page_size", page_size);
           dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
           dataBean.setId("1");
           dataBean.setMessage(result.toString());

       }catch(Exception ex){
           ex.printStackTrace();
           dataBean.setCode(Common.DATABEAN_CODE_ERROR);
           dataBean.setId("1");
           dataBean.setMessage(ex.getMessage());
           logger.info(ex.getMessage());
       }

       return  dataBean.getJsonStr();
   }


    /**
     *
     * Mongodb条件查询
     *
     *
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {

        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        int pages = 0;
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sign_content);

            String[] column_names = new String[]{"user_code","user_name","corp_name"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names,search_value);
            DBCursor dbCursor = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                DBCursor dbCursor1 = cursor.find(queryCondition);
                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_GM)) {
                    //企业管理员
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    //         value.add(new BasicDBObject("role_code", role_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    DBCursor dbCursor2 = cursor.find(queryCondition1);
                    pages = MongoUtils.getPages(dbCursor2, page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "modified_date", -1);

                } else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = null;

                    stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "", "");

                    //添加.......
                    BasicDBList value = new BasicDBList();
                    //  BasicDBObject ref=new BasicDBObject();
                    String store_code = "";
                    //...
                    for (int i = 0; i < stores.size(); i++) {
                        //       store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                        store_code = stores.get(i).getStore_code().toString();
                        value.add(store_code);
                    }
                    BasicDBObject ba = new BasicDBObject();
                    BasicDBList values = new BasicDBList();
                    if(value!=null&&value.size()>0) {
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                        values.add(new BasicDBObject("corp_code", corp_code));
                    }else{

                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    values.add(queryCondition);
                    ba.put("$and", values);
                    DBCursor dbCursor2 = cursor.find(ba);
                    pages = MongoUtils.getPages(dbCursor2,page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"modified_date",-1);


                } else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    BasicDBList values = new BasicDBList();
                  //  value.add(new BasicDBObject("store_code", store_code));

                    String[] stores = null;
                    if (!store_code.equals("")) {
                        store_code = store_code.replace(Common.SPECIAL_HEAD,"");
                        stores = store_code.split(",");
                    }
                    BasicDBList value = new BasicDBList();

                    for (int i = 0; i < stores.length; i++) {
                        //       store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                        store_code = stores[i].toString();
                        value.add(store_code);
                    }
                    //   value.add(new BasicDBObject("role_code", role_code));
                    values.add(queryCondition);

                    if(value!=null&&value.size()>0) {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                    }else{
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", values);
                    DBCursor dbCursor2 = cursor.find(queryCondition1);
                    pages = MongoUtils.getPages(dbCursor2,page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"modified_date",-1);

                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();
                     //.....
                    if (!area_code.equals("")) {
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        String[] areas = area_code.split(",");
                        String[] storeCodes = null;
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCodes = store_code.split(",");
                        }
                        List<Store> store = null;
                        store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, "");
                        BasicDBList value = new BasicDBList();
                        BasicDBObject ba = new BasicDBObject();

                        for (int i = 0; i < store.size(); i++) {
                            store_code = store.get(i).getStore_code().toString();
                            value.add(store_code);
                        }
                        BasicDBList values = new BasicDBList();
                        if(value.size()>0&&value!=null) {
                            values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                            values.add(new BasicDBObject("corp_code", corp_code));
                        }else{
                            values.add(new BasicDBObject("corp_code", corp_code));
                            values.add(new BasicDBObject("user_code", user_code));
                        }
                        values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                        values.add(queryCondition);
                        ba.put("$and", values);
                        DBCursor dbCursor2 = cursor.find(ba);
                            pages = MongoUtils.getPages(dbCursor2,page_size);
                            dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"modified_date",-1);

                     }

                    } else if (role_code.equals(Common.ROLE_STAFF)) {
                        BasicDBList value = new BasicDBList();
                        value.add(new BasicDBObject("corp_code", corp_code));
                        value.add(new BasicDBObject("user_code", user_code));
                        value.add(queryCondition);
                     value.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                        BasicDBObject queryCondition1 = new BasicDBObject();
                        queryCondition1.put("$and", value);
                        DBCursor dbCursor2 = cursor.find(queryCondition1);
                        pages = MongoUtils.getPages(dbCursor2,page_size);
                        dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"modified_date",-1);

                    }
                }
                ArrayList list = MongoHelperServiceImpl.dbCursorToList_status(dbCursor);
                result.put("list", list);
                result.put("pages", pages);
                result.put("page_number", page_number);
                result.put("page_size", page_size);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(result.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }



    /**
     * Mongodb删除事务
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String inter_id = jsonObject.get("id").toString();
            String[] ids = inter_id.split(",");

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sign_content);

            for (int i = 0; i < ids.length; i++) {
                //logger.info("-------------delete--" + Integer.valueOf(ids[i]));
         //       Sign sign = signService.selSignById(Integer.valueOf(ids[i]));
                //signService.delSignById(Integer.valueOf(ids[i]));

                BasicDBObject deleteRecord = new BasicDBObject();
                deleteRecord.put("_id", new ObjectId(ids[i]));
               //插入到用户操作日志
                DBCursor dbObjects = cursor.find(deleteRecord);
                String t_corp_code = "";
                String t_code = "";
                String t_name = "";
                String modified_date = "";
                String status = "";

                while (dbObjects.hasNext()) {
                    DBObject sign = dbObjects.next();
                    if (sign.containsField("corp_code")) {
                        t_corp_code = sign.get("corp_code").toString();
                    }
                    if (sign.containsField("user_code")) {
                        t_code = sign.get("user_code").toString();
                    }
                    if (sign.containsField("user_name")) {
                        t_name = sign.get("user_name").toString();
                    }
                    if (sign.containsField("modified_date")) {
                        modified_date = sign.get("modified_date").toString();
                    }
                    if (sign.containsField("status")) {
                        status = sign.get("status").toString();
                    }
                }

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
                String function = "员工管理_签到管理";
                String action = Common.ACTION_DEL;
             //   String t_corp_code = sign.getCorp_code();
             //   String t_code = sign.getUser_code();
            //    String t_name = sign.getUser_name();
                String remark = modified_date+"("+status+")";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                cursor.remove(deleteRecord);

            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }





    /***
     * Mongodb导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();

            ArrayList list = new ArrayList();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sign_content);
            DBObject sort_obj = new BasicDBObject("modified_date", -1);

            if (screen.equals("")) {

                String[] column_names = new String[]{"user_code","user_name","corp_name"};
                BasicDBObject queryCondition = MongoUtils.orOperation(column_names,search_value);

                DBCursor dbCursor = null;

                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    dbCursor = cursor.find(queryCondition).sort(sort_obj);
                } else if (role_code.equals(Common.ROLE_GM)) {
                    //系统管理员
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
                    String store_code = "";
                    BasicDBList value = new BasicDBList();
                    for (int i = 0; i < stores.size(); i++) {

                        store_code = stores.get(i).getStore_code().toString();
                        value.add(store_code);
                    }
                    BasicDBObject ba=new BasicDBObject();
                    BasicDBList values = new BasicDBList();
                    if(value!=null&&value.size()>0) {
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                        values.add(new BasicDBObject("corp_code", corp_code));
                    }else{
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    values.add(queryCondition);
                    ba.put("$and",values);
                    dbCursor = cursor.find(ba).sort(sort_obj);

                } else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    BasicDBList values = new BasicDBList();
                    String[] stores = null;
                    if (!store_code.equals("")) {
                        store_code = store_code.replace(Common.SPECIAL_HEAD,"");
                        stores = store_code.split(",");
                    }
                    BasicDBList value = new BasicDBList();

                    for (int i = 0; i < stores.length; i++) {

                        store_code = stores[i].toString();
                        value.add(store_code);
                    }

                    values.add(queryCondition);
                    if(value!=null&&value.size()>0) {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                    }else{
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", values);

                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);

                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();

                    if (!area_code.equals("")) {
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        String[] areas = area_code.split(",");
                        String[] storeCodes = null;
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCodes = store_code.split(",");
                        }

                        BasicDBList value=new BasicDBList();
                        List<Store> store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, "");
                        for (int i = 0; i < store.size(); i++) {
                            store_code = store.get(i).getStore_code().toString();
                            value.add(store_code);
                        }
                        BasicDBList values = new BasicDBList();
                        if(value!=null&&value.size()>0) {
                            values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                            values.add(new BasicDBObject("corp_code", corp_code));
                        }else{
                            values.add(new BasicDBObject("corp_code", corp_code));
                            values.add(new BasicDBObject("user_code", user_code));
                        }
                        values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                        values.add(queryCondition);
                        BasicDBObject queryCondition1 = new BasicDBObject();
                        queryCondition1.put("$and", values);
                        dbCursor = cursor.find(queryCondition1).sort(sort_obj);

                    }

                } else if (role_code.equals(Common.ROLE_STAFF)) {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(new BasicDBObject("user_code", user_code));
                    value.add(queryCondition);
                    value.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);

                }
                list = MongoHelperServiceImpl.dbCursorToList_status(dbCursor);

            }
            else {
                JSONArray array = JSONArray.parseArray(screen);
                BasicDBObject queryCondition = MongoHelperServiceImpl.andSignScreen(array);
                DBCursor dbCursor = null;
                if (role_code.equals(Common.ROLE_SYS)) {
                    dbCursor = cursor.find(queryCondition).sort(sort_obj);
                } else if (role_code.equals(Common.ROLE_GM)) {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                }  else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
                   String store_code = "";
                    BasicDBList value=new BasicDBList();
                    for (int i = 0; i < stores.size(); i++) {
                        //       store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                        store_code = stores.get(i).getStore_code().toString();
                        value.add(store_code);
                    }
                    BasicDBObject ba=new BasicDBObject();
                    BasicDBList values = new BasicDBList();
                    if(value!=null&&value.size()>0) {
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                        values.add(new BasicDBObject("corp_code", corp_code));
                    }else{
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    values.add(queryCondition);
                    ba.put("$and",values);
                    dbCursor = cursor.find(ba).sort(sort_obj);


                }else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    //....
                    if (!area_code.equals("")) {
                        area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                        String[] areas = area_code.split(",");
                        String[] storeCodes = null;
                        if (!store_code.equals("")) {
                            store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                            storeCodes = store_code.split(",");
                        }

                        BasicDBList value=new BasicDBList();
                        List<Store> store = storeService.selectByAreaBrand(corp_code, areas, storeCodes, null, "");
                        //  String a = "";
                        for (int i = 0; i < store.size(); i++) {
                            //      a = a + store.get(i).getStore_code() + ",";
                            store_code = store.get(i).getStore_code().toString();
                            value.add(store_code);
                        }
                        BasicDBList values = new BasicDBList();
                        if(value!=null&&value.size()>0) {
                            values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                            values.add(new BasicDBObject("corp_code", corp_code));
                        }else{
                            values.add(new BasicDBObject("corp_code", corp_code));
                            values.add(new BasicDBObject("user_code", user_code));
                        }
                        values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                        values.add(queryCondition);
                        BasicDBObject queryCondition1 = new BasicDBObject();
                        queryCondition1.put("$and", values);
                        dbCursor = cursor.find(queryCondition1).sort(sort_obj);

                    }

                } else if (role_code.equals(Common.ROLE_SM)) {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    BasicDBList values = new BasicDBList();
                    String[] stores = null;
                    if (!store_code.equals("")) {
                        store_code = store_code.replace(Common.SPECIAL_HEAD,"");
                        stores = store_code.split(",");
                    }
                    BasicDBList value = new BasicDBList();

                    for (int i = 0; i < stores.length; i++) {

                        store_code = stores[i].toString();
                        value.add(store_code);
                    }

                    values.add(queryCondition);
                    if(value!=null&&value.size()>0) {
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                    }else{
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", values);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);

                } else if (role_code.equals(Common.ROLE_STAFF)) {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(new BasicDBObject("user_code", user_code));
                    value.add(queryCondition);
                    value.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                }
                list = MongoHelperServiceImpl.dbCursorToList_status(dbCursor);
            }

            //导出......
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(list);
            if (list.size() >= Common.EXPORTEXECLCOUNT) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, list, map, response, request);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }


    /**
     * Mongodb签到管理
     * 筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String selectAllSignScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        int pages=0;
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObject1 = new JSONObject(jsString);
            id = jsonObject1.getString("id");
            String message = jsonObject1.get("message").toString();
            JSONObject jsonObject2 = new JSONObject(message);
            int page_number = Integer.parseInt(jsonObject2.get("pageNumber").toString());
            int page_size = Integer.parseInt(jsonObject2.get("pageSize").toString());
            String lists = jsonObject2.get("list").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            JSONArray array = JSONArray.parseArray(lists);
            BasicDBObject queryCondition = MongoHelperServiceImpl.andSignScreen(array);
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sign_content);

            DBCursor dbCursor = null;

            if (role_code.equals(Common.ROLE_SYS)) {

                DBCursor dbCursor1 = cursor.find(queryCondition);
                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);

            } else if (role_code.equals(Common.ROLE_GM)) {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);
            } else if (role_code.equals(Common.ROLE_BM)) {
                //品牌管理员
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");

                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
                String store_code = "";
                BasicDBList value = new BasicDBList();

                for (int i = 0; i < stores.size(); i++) {

                    store_code = stores.get(i).getStore_code().toString();
                    value.add(store_code);
                }
                BasicDBList values = new BasicDBList();
                if(value.size()>0&&value!=null) {
                    values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                    values.add(new BasicDBObject("corp_code", corp_code));
                }else{
                    values.add(new BasicDBObject("corp_code", corp_code));
                    values.add(new BasicDBObject("user_code", user_code));
                }
                values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));

                values.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", values);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                    pages = MongoUtils.getPages(dbCursor1,page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);


            }else if (role_code.equals(Common.ROLE_AM)) {
                String area_code = request.getSession(false).getAttribute("area_code").toString();
                String store_code = request.getSession(false).getAttribute("store_code").toString();

                if (!area_code.equals("")) {
                    area_code = area_code.replace(Common.SPECIAL_HEAD,"");
                    String[] areas = area_code.split(",");
                    String[] storeCodes = null;
                    if (!store_code.equals("")){
                        store_code = store_code.replace(Common.SPECIAL_HEAD,"");
                        storeCodes = store_code.split(",");
                    }
                    BasicDBList value = new BasicDBList();
                    List<Store> store = storeService.selectByAreaBrand(corp_code, areas,storeCodes,null, "");

                    for (int i = 0; i < store.size(); i++) {
                        store_code = store.get(i).getStore_code().toString();
                        value.add(store_code);
                    }
                    BasicDBList values = new BasicDBList();
                    if(value!=null&&value.size()>0) {
                        values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                        values.add(new BasicDBObject("corp_code", corp_code));
                    }else{
                        values.add(new BasicDBObject("corp_code", corp_code));
                        values.add(new BasicDBObject("user_code", user_code));
                    }
                    values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                    values.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", values);
                    DBCursor dbCursor1 = cursor.find(queryCondition1);
                        pages = MongoUtils.getPages(dbCursor1,page_size);
                        dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);
                }


            } else if (role_code.equals(Common.ROLE_SM)) {
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                //....
                String[] stores = null;
                if (!store_code.equals("")) {
                    store_code = store_code.replace(Common.SPECIAL_HEAD,"");
                    stores = store_code.split(",");
                }
                BasicDBList value = new BasicDBList();
                for (int i = 0; i < stores.length; i++) {

                    store_code = stores[i].toString();
                    value.add(store_code);
                }
                BasicDBList values = new BasicDBList();
                if(value!=null&&value.size()>0) {
                    values.add(new BasicDBObject("store_code", new BasicDBObject("$in", value)));
                    values.add(new BasicDBObject("corp_code", corp_code));
                }else{
                    values.add(new BasicDBObject("corp_code", corp_code));
                    values.add(new BasicDBObject("user_code", user_code));
                }
                values.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                values.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", values);
                DBCursor dbCursor1 = cursor.find(queryCondition1);

                    pages = MongoUtils.getPages(dbCursor1,page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);


            } else if (role_code.equals(Common.ROLE_STAFF)) {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("user_code", user_code));
                value.add(queryCondition);
                value.add(new BasicDBObject("store_name", new BasicDBObject("$ne", "")));
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                //员工过滤
                    pages = MongoUtils.getPages(dbCursor1,page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"modified_date",-1);


            }
            ArrayList list = MongoHelperServiceImpl.dbCursorToList_status(dbCursor);
            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}
