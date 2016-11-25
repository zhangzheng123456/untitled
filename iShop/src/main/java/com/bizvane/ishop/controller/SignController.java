package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.Sign;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.SignService;
import com.bizvane.ishop.service.StoreService;
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

//    //列表
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String selectAll(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        int pages = 0;
//        try {
//            String role_code = request.getSession().getAttribute("role_code").toString();
//            String group_code = request.getSession().getAttribute("group_code").toString();
//            String corp_code = request.getSession().getAttribute("corp_code").toString();
//            String user_code = request.getSession().getAttribute("user_code").toString();
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//            JSONObject result = new JSONObject();
//            PageInfo<Sign> list = null;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                //系统管理员
//                list = signService.selectSignByInp(page_number, page_size, "", "","", "", role_code,"");
//            } else if (role_code.equals(Common.ROLE_GM)) {
//                //企业管理员
//                list = signService.selectSignByInp(page_number, page_size, corp_code, "","", "", role_code,"");
//            } else if (role_code.equals(Common.ROLE_BM)) {
//                //品牌管理员
//                String brand_code = request.getSession().getAttribute("brand_code").toString();
//                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
//                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
//                String store_code = "";
//                for (int i = 0; i < stores.size(); i++) {
//                    store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
//                }
//                list = signService.selectSignByInp(page_number, page_size, corp_code, "", store_code, "", role_code,"");
//            }else if (role_code.equals(Common.ROLE_SM)) {
//                //店长
//                String store_code = request.getSession().getAttribute("store_code").toString();
//                list = signService.selectSignByInp(page_number, page_size, corp_code, "", store_code, "", role_code,"");
//            } else if (role_code.equals(Common.ROLE_AM)) {
//                //区经
//                String area_code = request.getSession().getAttribute("area_code").toString();
//                String store_code = request.getSession().getAttribute("store_code").toString();
//                list = signService.selectSignByInp(page_number, page_size, corp_code, "", "", area_code, role_code,store_code);
//            } else if (role_code.equals(Common.ROLE_STAFF)) {
//                list = signService.selectByUser(page_number, page_size, corp_code, user_code, "");
//            }
//            //  System.out.println(list.getList().get(0).getSign_time()+"---"+list.getList().get(0).getUser_code());
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

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
               dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);

           } else if (role_code.equals(Common.ROLE_GM)) {
               //企业管理员

               Map keyMap = new HashMap();
               keyMap.put("corp_code", corp_code);
              // keyMap.put("role_code", role_code);
               BasicDBObject ref = new BasicDBObject();
               ref.putAll(keyMap);
               DBCursor dbCursor1 = cursor.find(ref);
               dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);

           } else if (role_code.equals(Common.ROLE_BM)) {
               //品牌管理员
               String brand_code = request.getSession().getAttribute("brand_code").toString();
               brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
               List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
               String store_code = "";
               for (int i = 0; i < stores.size(); i++) {
                   store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
               }
               Map keyMap = new HashMap();
       //        keyMap.put("role_code", role_code);
               keyMap.put("corp_code", corp_code);
               keyMap.put("store_code",store_code);
               BasicDBObject ref = new BasicDBObject();
               ref.putAll(keyMap);
               DBCursor dbCursor1 = cursor.find(ref);
               pages = MongoUtils.getPages(dbCursor1,page_size);
               dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);

           }else if (role_code.equals(Common.ROLE_SM)) {
               //店长
               String store_code = request.getSession().getAttribute("store_code").toString();
               Map keyMap = new HashMap();
           //    keyMap.put("role_code", role_code);
               keyMap.put("corp_code", corp_code);
               keyMap.put("store_code",store_code);
               BasicDBObject ref = new BasicDBObject();
               ref.putAll(keyMap);
               DBCursor dbCursor1 = cursor.find(ref);

               pages = MongoUtils.getPages(dbCursor1,page_size);
               dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);

           } else if (role_code.equals(Common.ROLE_AM)) {
               //区经
               String area_code = request.getSession().getAttribute("area_code").toString();
               String store_code = request.getSession().getAttribute("store_code").toString();
               Map keyMap = new HashMap();
               keyMap.put("corp_code", corp_code);
            //   keyMap.put("area_code",area_code);
              keyMap.put("store_code",store_code);
          //     keyMap.put("role_code", role_code);
               BasicDBObject ref = new BasicDBObject();
               ref.putAll(keyMap);
               DBCursor dbCursor1 = cursor.find(ref);

               pages = MongoUtils.getPages(dbCursor1,page_size);
               dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);

           } else if (role_code.equals(Common.ROLE_STAFF)) {
               Map keyMap = new HashMap();
               keyMap.put("corp_code", corp_code);
               keyMap.put("user_code",user_code);
               BasicDBObject ref = new BasicDBObject();
               ref.putAll(keyMap);

               DBCursor dbCursor1 = cursor.find(ref);
               pages = MongoUtils.getPages(dbCursor1,page_size);
               dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);

           }
           //  System.out.println(list.getList().get(0).getSign_time()+"---"+list.getList().get(0).getUser_code());
           ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);
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


    //条件查询
//    @RequestMapping(value = "/search", method = RequestMethod.POST)
//    @ResponseBody
//    public String search(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String jsString = request.getParameter("param");
//            logger.info("json---------------" + jsString);
//            JSONObject jsonObj = new JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject jsonObject = new JSONObject(message);
//            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
//            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
//            String search_value = jsonObject.get("searchValue").toString();
//            String role_code = request.getSession().getAttribute("role_code").toString();
//            String user_code = request.getSession().getAttribute("user_code").toString();
//            JSONObject result = new JSONObject();
//            PageInfo<Sign> list = null;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                //系统管理员
//                list =signService.selectSignByInp(page_number, page_size, "", search_value, "", "", role_code,"");
//            } else {
//                String corp_code = request.getSession().getAttribute("corp_code").toString();
//                if (role_code.equals(Common.ROLE_GM)) {
//                    //企业管理员
//                    list = signService.selectSignByInp(page_number, page_size, corp_code, search_value, "", "", role_code,"");
//                } else if (role_code.equals(Common.ROLE_BM)) {
//                    //品牌管理员
//                    String brand_code = request.getSession().getAttribute("brand_code").toString();
//                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
//                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
//                    String store_code = "";
//                    for (int i = 0; i < stores.size(); i++) {
//                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
//                    }
//                    list = signService.selectSignByInp(page_number, page_size, corp_code, search_value, store_code, "", role_code,"");
//                } else if (role_code.equals(Common.ROLE_SM)) {
//                    //店长
//                    String store_code = request.getSession().getAttribute("store_code").toString();
//                    list = signService.selectSignByInp(page_number, page_size, corp_code, search_value, store_code, "", role_code,"");
//                } else if (role_code.equals(Common.ROLE_AM)) {
//                    //区经
//                    String area_code = request.getSession().getAttribute("area_code").toString();
//                    String store_code = request.getSession().getAttribute("store_code").toString();
//                    list = signService.selectSignByInp(page_number, page_size, corp_code, search_value, "", area_code, role_code,store_code);
//                } else if (role_code.equals(Common.ROLE_STAFF)) {
//                    list = signService.selectByUser(page_number, page_size, corp_code, user_code, search_value);
//                }
//            }
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId(id);
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

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

            String[] column_names = new String[]{"phone","diatance","modified_date","user_name","creater_date","modifier",
                    "isactive","store_id_list", "status","creater","user_id","crop_name","store_name","corp_code","sign_time", "location"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names,search_value);
            DBCursor dbCursor = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                DBCursor dbCursor1 = cursor.find(queryCondition);
                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);
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

                    pages = MongoUtils.getPages(dbCursor2,page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"time",-1);

                } else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
                    String store_code = "";
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                    }
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(new BasicDBObject("store_code", store_code));
             //       value.add(new BasicDBObject("role_code", role_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    DBCursor dbCursor2 = cursor.find(queryCondition1);

                    pages = MongoUtils.getPages(dbCursor2,page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"time",-1);
                } else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(new BasicDBObject("store_code", store_code));
                 //   value.add(new BasicDBObject("role_code", role_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    DBCursor dbCursor2 = cursor.find(queryCondition1);

                    pages = MongoUtils.getPages(dbCursor2,page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"time",-1);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(new BasicDBObject("store_code", store_code));
             //       value.add(new BasicDBObject("area_code", area_code));
              //      value.add(new BasicDBObject("role_code", role_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    DBCursor dbCursor2 = cursor.find(queryCondition1);

                    pages = MongoUtils.getPages(dbCursor2,page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"time",-1);
                } else if (role_code.equals(Common.ROLE_STAFF)) {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
             //       value.add(new BasicDBObject("user_code", user_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    DBCursor dbCursor2 = cursor.find(queryCondition1);
                    pages = MongoUtils.getPages(dbCursor2,page_size);
                    dbCursor = MongoUtils.sortAndPage(dbCursor2,page_number,page_size,"time",-1);
                }
            }
            ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);
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
     * 删除(用了事务)
     */
//    @RequestMapping(value = "/delete", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional
//    public String delete(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            String jsString = request.getParameter("param");
//            logger.info("json--delete-------------" + jsString);
//            JSONObject jsonObj = new JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject jsonObject = new JSONObject(message);
//            String inter_id = jsonObject.get("id").toString();
//            String[] ids = inter_id.split(",");
//            for (int i = 0; i < ids.length; i++) {
//                //logger.info("-------------delete--" + Integer.valueOf(ids[i]));
//                Sign sign = signService.selSignById(Integer.valueOf(ids[i]));
//                signService.delSignById(Integer.valueOf(ids[i]));
//                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                dataBean.setId(id);
//                dataBean.setMessage("success");
//
////----------------行为日志开始------------------------------------------
//                /**
//                 * mongodb插入用户操作记录
//                 * @param operation_corp_code 操作者corp_code
//                 * @param operation_user_code 操作者user_code
//                 * @param function 功能
//                 * @param action 动作
//                 * @param corp_code 被操作corp_code
//                 * @param code 被操作code
//                 * @param name 被操作name
//                 * @throws Exception
//                 */
//                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
//                String operation_user_code = request.getSession().getAttribute("user_code").toString();
//                String function = "员工管理_签到管理";
//                String action = Common.ACTION_DEL;
//                String t_corp_code = sign.getCorp_code();
//                String t_code = sign.getUser_code();
//                String t_name = sign.getUser_name();
//                String remark = sign.getSign_time()+"("+sign.getStatus()+")";
//                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
//            }
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//            return dataBean.getJsonStr();
//        }
//        logger.info("delete-----" + dataBean.getJsonStr());
//        return dataBean.getJsonStr();
//    }
    /**
     * Mongodb删除事务
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
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
                Sign sign = signService.selSignById(Integer.valueOf(ids[i]));
                //signService.delSignById(Integer.valueOf(ids[i]));

                DBObject deleteRecord = new BasicDBObject();
                deleteRecord.put("_id", new ObjectId(ids[i]));
                cursor.remove(deleteRecord);

                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
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
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "员工管理_签到管理";
                String action = Common.ACTION_DEL;
                String t_corp_code = sign.getCorp_code();
                String t_code = sign.getUser_code();
                String t_name = sign.getUser_name();
                String remark = sign.getSign_time()+"("+sign.getStatus()+")";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
            }
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
     * 导出数据
     */
//    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
//    @ResponseBody
//    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
//        DataBean dataBean = new DataBean();
//        String errormessage = "数据异常，导出失败";
//        try {
//            String role_code = request.getSession().getAttribute("role_code").toString();
//            String corp_code = request.getSession().getAttribute("corp_code").toString();
//            String user_code = request.getSession().getAttribute("user_code").toString();
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            String search_value = jsonObject.get("searchValue").toString();
//            String screen = jsonObject.get("list").toString();
//            PageInfo<Sign> list = null;
//            if (screen.equals("")) {
//                if (role_code.equals(Common.ROLE_SYS)) {
//                    //系统管理员
//                    list = signService.selectSignByInp(1, 30000, "", search_value, "", "", role_code,"");
//                } else if (role_code.equals(Common.ROLE_GM)) {
//                    //系统管理员
//                    list = signService.selectSignByInp(1, 30000, corp_code, search_value, "", "", role_code,"");
//                } else if (role_code.equals(Common.ROLE_BM)) {
//                    //品牌管理员
//                    String brand_code = request.getSession().getAttribute("brand_code").toString();
//                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
//                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
//                    String store_code = "";
//                    for (int i = 0; i < stores.size(); i++) {
//                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
//                    }
//                    list = signService.selectSignByInp(1, 30000, corp_code, search_value, store_code, "", role_code,"");
//                } else if (role_code.equals(Common.ROLE_SM)) {
//                    //店长
//                    String store_code = request.getSession().getAttribute("store_code").toString();
//                    list = signService.selectSignByInp(1, 30000, corp_code, search_value, store_code, "", role_code,"");
//                } else if (role_code.equals(Common.ROLE_AM)) {
//                    //区经
//                    String area_code = request.getSession().getAttribute("area_code").toString();
//                    String store_code = request.getSession().getAttribute("store_code").toString();
//                    list = signService.selectSignByInp(1, 30000, corp_code, search_value, "", area_code, role_code,store_code);
//                } else if (role_code.equals(Common.ROLE_STAFF)) {
//                    list = signService.selectByUser(1, 30000, corp_code, user_code, search_value);
//                }
//
//            } else {
//                Map<String, String> map = WebUtils.Json2Map(jsonObject);
//                if (role_code.equals(Common.ROLE_SYS)) {
//                    list = signService.selectSignAllScreen(1, 30000, "", "", "", "", map,"");
//                } else if (role_code.equals(Common.ROLE_GM)) {
//                    list = signService.selectSignAllScreen(1, 30000, corp_code, "", "", "", map,"");
//                }  else if (role_code.equals(Common.ROLE_BM)) {
//                    //品牌管理员
//                    String brand_code = request.getSession().getAttribute("brand_code").toString();
//                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
//                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
//                    String store_code = "";
//                    for (int i = 0; i < stores.size(); i++) {
//                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
//                    }
//                    list = signService.selectSignAllScreen(1, 30000, corp_code, "", store_code, role_code, map,"");
//                }else if (role_code.equals(Common.ROLE_AM)) {
//                    String area_code = request.getSession(false).getAttribute("area_code").toString();
//                    String store_code = request.getSession().getAttribute("store_code").toString();
//                    list = signService.selectSignAllScreen(1, 30000, corp_code, area_code, "", role_code, map,store_code);
//                } else if (role_code.equals(Common.ROLE_SM)) {
//                    String store_code = request.getSession(false).getAttribute("store_code").toString();
//                    list = signService.selectSignAllScreen(1, 30000, corp_code, "", store_code, role_code, map,"");
//                } else if (role_code.equals(Common.ROLE_STAFF)) {
//                    list = signService.selectSignAllScreenByUser(1, 30000, corp_code, user_code, map);
//                }
//            }
//            List<Sign> signs = list.getList();
//            for (Sign sign :signs) {
//                String location = sign.getLocation();
//                String replaceStr = WebUtils.StringFilter(location);
//                sign.setLocation(replaceStr);
//            }
//            if (signs.size() >= 29999) {
//                errormessage = "导出数据过大";
//                int i = 9 / 0;
//            }
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//            String json = mapper.writeValueAsString(signs);
//            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
//            // String column_name1 = "corp_code,corp_name";
//            // String[] cols = column_name.split(",");//前台传过来的字段
//            String pathname = OutExeclHelper.OutExecl(json,signs, map, response, request);
//            JSONObject result = new JSONObject();
//            if (pathname == null || pathname.equals("")) {
//                errormessage = "数据异常，导出失败";
//                int a = 8 / 0;
//            }
//            result.put("path", JSON.toJSONString("lupload/" + pathname));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId(id);
//            dataBean.setMessage(result.toString());
//        } catch (Exception e) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(errormessage);
//        }
//        return dataBean.getJsonStr();
//    }


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
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();

            ArrayList list = new ArrayList();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sign_content);
            DBObject sort_obj = new BasicDBObject("time", -1);

            if (screen.equals("")) {

                String[] column_names = new String[]{"phone","diatance","modified_date","user_name","creater_date","modifier",
                        "isactive","store_id_list", "status","creater","user_id","crop_name","store_name","corp_code","sign_time", "location"};
                BasicDBObject queryCondition = MongoUtils.orOperation(column_names,search_value);

                DBCursor dbCursor = null;

                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    dbCursor = cursor.find(queryCondition).sort(sort_obj);
                } else if (role_code.equals(Common.ROLE_GM)) {
                    //系统管理员
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
             //       value.add(new BasicDBObject("role_code", role_code));
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
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                    }
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
            //        value.add(new BasicDBObject("role_code", role_code));
                    value.add(new BasicDBObject("store_code", store_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                } else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
          //          value.add(new BasicDBObject("role_code", role_code));
                    value.add(new BasicDBObject("store_code", store_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
        //            value.add(new BasicDBObject("role_code", role_code));
                    value.add(new BasicDBObject("store_code", store_code));
        //            value.add(new BasicDBObject("area_code", area_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                } else if (role_code.equals(Common.ROLE_STAFF)) {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(new BasicDBObject("user_code", user_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                }
                list = MongoUtils.dbCursorToList_id(dbCursor);

            }
            //.........................
            else {
                JSONArray array = JSONArray.parseArray(screen);
                BasicDBObject queryCondition = MongoUtils.andOperation(array);
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
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                    }
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code",corp_code));
                   value.add(new BasicDBObject("store_code",store_code));
        //            value.add(new BasicDBObject("role_code", role_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                }else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
        //            value.add(new BasicDBObject("area_code", area_code));
       //             value.add(new BasicDBObject("role_code", role_code));
                    value.add(new BasicDBObject("store_code",store_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                } else if (role_code.equals(Common.ROLE_SM)) {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("store_code", store_code));
                    value.add(new BasicDBObject("role_code", role_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                } else if (role_code.equals(Common.ROLE_STAFF)) {
                    BasicDBList value = new BasicDBList();
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(new BasicDBObject("user_code", user_code));
                    value.add(queryCondition);
                    BasicDBObject queryCondition1 = new BasicDBObject();
                    queryCondition1.put("$and", value);
                    dbCursor = cursor.find(queryCondition1).sort(sort_obj);
                }
                list = MongoUtils.dbCursorToList_id(dbCursor);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            String json = mapper.writeValueAsString(list);
            if (list.size() >= 29999) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
            String pathname = OutExeclHelper.OutExecl(json, list, map, response, request);
            org.json.JSONObject result = new org.json.JSONObject();
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
     * 签到管理
     * 筛选
     */
//    @RequestMapping(value = "/screen", method = RequestMethod.POST)
//    @ResponseBody
//    public String selectAllSignScreen(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObject1 = new org.json.JSONObject(jsString);
//            id = jsonObject1.getString("id");
//            String message = jsonObject1.get("message").toString();
//            JSONObject jsonObject2 = new JSONObject(message);
//            int page_number = Integer.parseInt(jsonObject2.get("pageNumber").toString());
//            int page_size = Integer.parseInt(jsonObject2.get("pageSize").toString());
//            String role_code = request.getSession().getAttribute("role_code").toString();
//            String group_code = request.getSession().getAttribute("group_code").toString();
//            String corp_code = request.getSession().getAttribute("corp_code").toString();
//            String user_code = request.getSession().getAttribute("user_code").toString();
//
//            Map<String, String> map = WebUtils.Json2Map(jsonObject2);
//            JSONObject result = new JSONObject();
//            PageInfo<Sign> list = null;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                list = signService.selectSignAllScreen(page_number, page_size, "", "", "", role_code, map,"");
//            } else if (role_code.equals(Common.ROLE_GM)) {
//                list = signService.selectSignAllScreen(page_number, page_size, corp_code, "", "", role_code, map,"");
//            } else if (role_code.equals(Common.ROLE_BM)) {
//                //品牌管理员
//                String brand_code = request.getSession().getAttribute("brand_code").toString();
//                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
//                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
//                String store_code = "";
//                for (int i = 0; i < stores.size(); i++) {
//                    store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
//                }
//                list = signService.selectSignAllScreen(page_number, page_size, corp_code, "", store_code, role_code, map,"");
//            }else if (role_code.equals(Common.ROLE_AM)) {
//                String area_code = request.getSession(false).getAttribute("area_code").toString();
//                String store_code = request.getSession(false).getAttribute("store_code").toString();
//                list = signService.selectSignAllScreen(page_number, page_size, corp_code, area_code, "", role_code, map,store_code);
//            } else if (role_code.equals(Common.ROLE_SM)) {
//                String store_code = request.getSession(false).getAttribute("store_code").toString();
//                list = signService.selectSignAllScreen(page_number, page_size, corp_code, "", store_code, role_code, map,"");
//            } else if (role_code.equals(Common.ROLE_STAFF)) {
//                list = signService.selectSignAllScreenByUser(page_number, page_size, corp_code, user_code, map);
//            }
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

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
            org.json.JSONObject jsonObject1 = new org.json.JSONObject(jsString);
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
            BasicDBObject queryCondition = MongoUtils.andOperation(array);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_sign_content);

            DBCursor dbCursor = null;

         //   Map<String, String> map = WebUtils.Json2Map(jsonObject2);

        //    PageInfo<Sign> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {

                DBCursor dbCursor1 = cursor.find(queryCondition);
                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);

            } else if (role_code.equals(Common.ROLE_GM)) {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
    //            value.add(new BasicDBObject("role_code", role_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);
            } else if (role_code.equals(Common.ROLE_BM)) {
                //品牌管理员
                String brand_code = request.getSession().getAttribute("brand_code").toString();
                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
                String store_code = "";
                for (int i = 0; i < stores.size(); i++) {
                    store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
                }
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
   //             value.add(new BasicDBObject("role_code", role_code));
                value.add(new BasicDBObject("store_code", store_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);;
            }else if (role_code.equals(Common.ROLE_AM)) {
                String area_code = request.getSession(false).getAttribute("area_code").toString();
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
      //          value.add(new BasicDBObject("role_code", role_code));
                value.add(new BasicDBObject("store_code", store_code));
       //         value.add(new BasicDBObject("area_code", area_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);
            } else if (role_code.equals(Common.ROLE_SM)) {
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
     //           value.add(new BasicDBObject("role_code", role_code));
                value.add(new BasicDBObject("store_code", store_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);
            } else if (role_code.equals(Common.ROLE_STAFF)) {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("user_code", user_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor1,page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1,page_number,page_size,"time",-1);
            }
            ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);
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
