package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.CorpParam;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.entity.ShopMatchType;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Multimaps;
import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by nanji on 2016/8/24.
 */
@Controller
public class ShopMatchController {
    String id;

    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    private BaseService baseService;
    @Autowired
    ShopMatchService shopMatchService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    private CorpParamService corpParamService;
    @Autowired
    IceInterfaceService iceInterfaceService;


    private static final Logger logger = Logger.getLogger(ShopMatchController.class);

    //获取侧边的秀搭类型
    @RequestMapping(value = "/api/shopMatch/shopTypeList", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopTypeList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_def);
        String user_code = request.getSession().getAttribute("user_code") + "";
        String corp_code = request.getSession().getAttribute("corp_code") + "";
        String role_code = request.getSession().getAttribute("role_code") + "";
//            System.out.println("======corp============"+request.getParameter("corp_code"));
//            System.out.println("======user============"+request.getParameter("user_code"));
        if (!role_code.trim().equals("null") && role_code.equals(Common.ROLE_SYS)) {
            corp_code = "";
        } else if (role_code.trim().equals("null")) {
            corp_code = request.getParameter("corp_code");
        }
        try {
            BasicDBList value = new BasicDBList();
            Pattern pattern = Pattern.compile("^.*" + corp_code + ".*$", Pattern.CASE_INSENSITIVE);
            value.add(new BasicDBObject("corp_code", pattern));
            //   value.add(new BasicDBObject("isactive", "Y"));

            BasicDBObject queryCondition1 = new BasicDBObject();
            queryCondition1.put("$and", value);
            DBCursor dbCursor = cursor.find(queryCondition1);


            Set<String> set = new HashSet<String>();

            //将游标中返回的结果记录到list集合中
            while (dbCursor.hasNext()) {
                DBObject object = dbCursor.next();
                if (object.containsField("d_match_type") && null != object.get("d_match_type")) {
                    if (!"".equals(object.get("d_match_type").toString())) {
                        set.add(object.get("d_match_type").toString());
                    }
                }
            }
            List<String> list = new ArrayList<String>();
            list.addAll(set);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(JSON.toJSONString(list));
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //获取侧边的秀搭类型
    @RequestMapping(value = "/api/shopMatch/getGoodsTypeByCorp", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getGoodsTypeByCorp(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String corp_code = request.getSession().getAttribute("corp_code") + "";
            String role_code = request.getSession().getAttribute("role_code") + "";
//            System.out.println("======corp============"+request.getParameter("corp_code"));
//            System.out.println("======user============"+request.getParameter("user_code"));
            if (!role_code.trim().equals("null") && role_code.equals(Common.ROLE_SYS)) {
                corp_code = request.getParameter("corp_code");
            } else if (role_code.trim().equals("null")) {
                corp_code = request.getParameter("corp_code");
            }
            String type = "ALL";
            CorpParam select_goods_source = corpParamService.selectByParamName("SELECT_GOODS_SOURCE", Common.IS_ACTIVE_Y);
            //    System.out.println( corp_code+"--------------"+select_goods_source.getId());
            if (null != select_goods_source) {
                List<CorpParam> corpParams = corpParamService.selectByCorpParam(corp_code, select_goods_source.getId() + "", Common.IS_ACTIVE_Y);
                if (corpParams.size() > 0) {
                    type = corpParams.get(0).getParam_value();
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(type);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    //拉取评论列表
    @RequestMapping(value = "/api/shopMatch/commentList", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopCommentList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        int pages = 0;
        try {
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_rel);

            String jsString = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            String corp_code = jsonObject.getString("corp_code");
            String pageNumber = jsonObject.getString("pageNumber");
            String pageSize = jsonObject.getString("pageSize");
            String d_match_code = jsonObject.getString("d_match_code");
            String operate_type = "comment";

            int page_number = Integer.valueOf(pageNumber);
            int page_size = Integer.valueOf(pageSize);


            BasicDBList value = new BasicDBList();
            value.add(new BasicDBObject("d_match_code", d_match_code));
            value.add(new BasicDBObject("corp_code", corp_code));
            value.add(new BasicDBObject("operate_type", operate_type));
            BasicDBObject queryCondition1 = new BasicDBObject();
            queryCondition1.put("$and", value);
            DBCursor dbCursor2 = cursor.find(queryCondition1);
            pages = MongoUtils.getPages(dbCursor2, page_size);
            DBCursor dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
            ArrayList list = MongoUtils.dbCursorToList_id(dbCursor);

            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            result.put("total", dbCursor2.size());
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


    //拉取我的收藏或评论列表
    @RequestMapping(value = "/api/shopMatch/selectCollect", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopCollectList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        int pages = 0;
        try {
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_rel);

            String jsString = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            String corp_code = jsonObject.getString("corp_code");
            String pageNumber = jsonObject.getString("pageNumber");
            String pageSize = jsonObject.getString("pageSize");
            String user_code = jsonObject.getString("user_code");
            String operate_type = jsonObject.getString("operate_type");

            int page_number = Integer.valueOf(pageNumber);
            int page_size = Integer.valueOf(pageSize);

            BasicDBList value = new BasicDBList();
            value.add(new BasicDBObject("corp_code", corp_code));
            value.add(new BasicDBObject("operate_userCode", user_code));
            value.add(new BasicDBObject("operate_type", operate_type));
            BasicDBObject queryCondition1 = new BasicDBObject();
            queryCondition1.put("$and", value);
            DBCursor dbCursor2 = cursor.find(queryCondition1);
            pages = MongoUtils.getPages(dbCursor2, page_size);
            DBCursor dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
            ArrayList list = shopMatchService.dbCursorToList_shop(dbCursor);

            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            result.put("total", dbCursor2.size());
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

    /**
     * 秀搭列表
     */
    @RequestMapping(value = "/api/shopMatch/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getShopMatchList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        int pages = 0;
        try {
            String user_code = request.getSession().getAttribute("user_code") + "";
            String corp_code = request.getSession().getAttribute("corp_code") + "";
            String role_code = request.getSession().getAttribute("role_code") + "";
//            System.out.println("======corp============"+request.getParameter("corp_code"));
//            System.out.println("======user============"+request.getParameter("user_code"));
            if (!role_code.trim().equals("null") && role_code.equals(Common.ROLE_SYS)) {
                corp_code = "";
            } else if (role_code.trim().equals("null")) {
                corp_code = request.getParameter("corp_code");
            }
            if (user_code.trim().equals("null")) {
                user_code = request.getParameter("user_code");
            }
            String pageNumber = request.getParameter("pageNumber");
            String pageSize = request.getParameter("pageSize");

            String type = request.getParameter("type");
            int page_number = Integer.valueOf(pageNumber);
            int page_size = Integer.valueOf(pageSize);


            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_def);

            String sort_key = request.getParameter("sort_key");
            String sort_ = "new";
            if (null != sort_key && !sort_key.equals("")) {
                if (sort_key.equals("like")) {
                    sort_ = "pageViews";
                } else if (sort_key.equals("new")) {
                    sort_ = "created_date";
                } else if (sort_key.equals("shopCount")) {
                    sort_ = "shopCount";
                } else if (sort_key.equals("collect")) {
                    sort_ = "d_match_collectCount";
                } else if (sort_key.equals("comment")) {
                    sort_ = "d_match_commentCount";
                }
            }

            String sort_type = request.getParameter("sort_type");
            String sort_2 = "-1";
            if (null != sort_type && !sort_type.equals("")) {
                if (sort_type.equals("-1")) {
                    sort_2 = "-1";
                } else if (sort_type.equals("1")) {
                    sort_2 = "1";
                }
            }

            // System.out.println(type+"===========拉取列表接口corp_code============" + corp_code + "----" + user_code);
            DBCursor dbCursor = null;
            if (type.equals("rec")) {
                BasicDBList value = new BasicDBList();
                String search_value = request.getParameter("search_value");

                if (null != search_value) {
                    String[] column_names = new String[]{"d_match_title", "d_match_desc", "r_match_goods.r_match_goodsName", "r_match_goods.r_match_goodsCode", "creater"};
                    BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);
                    value.add(queryCondition);
                }
                String d_match_type = request.getParameter("d_match_type");
                System.out.println(search_value + "-----d_match_type------" + d_match_type);
                if (null != d_match_type && !d_match_type.equals("")) {
                    BasicDBObject queryCondition = new BasicDBObject();
                    String[] split = d_match_type.split(",");
                    BasicDBList value2 = new BasicDBList();
                    for (int i = 0; i < split.length; i++) {
                        String d_match_type_i = split[i];
                        Pattern pattern = Pattern.compile("^.*" + d_match_type_i + ".*$", Pattern.CASE_INSENSITIVE);
                        value2.add(new BasicDBObject("d_match_type", pattern));
                    }
                    queryCondition.put("$or", value2);
                    value.add(queryCondition);
                    // value.add(new BasicDBObject("d_match_type", new BasicDBObject("$in", value2)));
                }

                String d_match_category = request.getParameter("d_match_category");
                //     System.out.println("-----d_match_category------" + d_match_category);
                if (null != d_match_category && !d_match_category.equals("")) {
                    value.add(new BasicDBObject("d_match_category", d_match_category));
                }
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("isactive", "Y"));
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor2, page_size);

                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, sort_, Integer.parseInt(sort_2));
                result.put("total", dbCursor2.count());
            } else if (type.equals("wx_show")) {
                BasicDBList value = new BasicDBList();
                String search_value = request.getParameter("search_value");
                if (null != search_value && !search_value.equals("")) {
                    String[] column_names = new String[]{"d_match_title", "d_match_desc", "r_match_goods.r_match_goodsName", "r_match_goods.r_match_goodsCode", "creater"};
                    BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);
                    value.add(queryCondition);
                }
                corp_code = request.getParameter("corp_code");
                //          System.out.println(request.getParameter("corp_code") + "-----wx_show333------" + corp_code);
                String d_match_type = request.getParameter("d_match_type");
                if (null != d_match_type && !d_match_type.equals("")) {
                    BasicDBObject queryCondition = new BasicDBObject();
                    String[] split = d_match_type.split(",");
                    BasicDBList value2 = new BasicDBList();
                    for (int i = 0; i < split.length; i++) {
                        String d_match_type_i = split[i];
                        Pattern pattern = Pattern.compile("^.*" + d_match_type_i + ".*$", Pattern.CASE_INSENSITIVE);
                        value2.add(new BasicDBObject("d_match_type", pattern));
                    }
                    queryCondition.put("$or", value2);
                    value.add(queryCondition);
                    // value.add(new BasicDBObject("d_match_type", new BasicDBObject("$in", value2)));
                }
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("isactive", "Y"));
                //  value.add(new BasicDBObject("d_match_show_wx", "Y"));
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                result.put("total", dbCursor2.count());
            } else if (type.equals("web")) {
                String search_value = request.getParameter("search_value");
                //       System.out.println("-------web-----" + search_value);
                String[] column_names = new String[]{"d_match_title", "d_match_desc", "r_match_goods.r_match_goodsName", "r_match_goods.r_match_goodsCode", "creater"};
                BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);

                BasicDBList value = new BasicDBList();
                BasicDBObject queryCondition1 = new BasicDBObject();
                if (role_code.equals(Common.ROLE_CM)) {
//                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                    System.out.println("manager_corp=====>" + manager_corp);
//                    String[] split = manager_corp.split(",");
//                    BasicDBList manager_corp_arr = new BasicDBList();
//                    for (int i = 0; i < split.length; i++) {
//                        manager_corp_arr.add(split[i]);
//                    }
//                    if (manager_corp_arr.size() > 0) {
//                        value.add(new BasicDBObject("corp_code", new BasicDBObject("$in", manager_corp_arr)));
//                    }
//                    value.add(queryCondition);
                    String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                    System.out.println("manager_corp=====>" + manager_corp);
                    corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                    System.out.println("getCorpCodeByCm=====>" + corp_code);
                    Pattern pattern = Pattern.compile("^.*" + corp_code + ".*$", Pattern.CASE_INSENSITIVE);
                    value.add(new BasicDBObject("corp_code", pattern));
                    value.add(queryCondition);
                } else if (!corp_code.equals("")) {
                    value.add(new BasicDBObject("corp_code", corp_code));
                    value.add(queryCondition);
                } else {
                    Pattern pattern = Pattern.compile("^.*" + corp_code + ".*$", Pattern.CASE_INSENSITIVE);
                    value.add(new BasicDBObject("corp_code", pattern));
                    value.add(queryCondition);
                }
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, sort_, Integer.parseInt(sort_2));
                //  dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                result.put("total", dbCursor2.count());
            } else if (type.equals("collect")) {
                DBCollection cursor_rel = mongoTemplate.getCollection(CommonValue.table_shop_match_rel);
                BasicDBList value_rel = new BasicDBList();
                value_rel.add(new BasicDBObject("corp_code", corp_code));
                value_rel.add(new BasicDBObject("operate_userCode", user_code));
                value_rel.add(new BasicDBObject("operate_type", "collect"));
                BasicDBObject queryCondition_rel = new BasicDBObject();
                queryCondition_rel.put("$and", value_rel);
                DBCursor dbCursor_rel = cursor_rel.find(queryCondition_rel);

                BasicDBList value = new BasicDBList();
                String search_value = request.getParameter("search_value");
                if (null != search_value) {
                    String[] column_names = new String[]{"d_match_title", "d_match_desc", "r_match_goods.r_match_goodsName", "r_match_goods.r_match_goodsCode", "creater"};
                    BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);
                    value.add(queryCondition);
                }
                // System.out.println(request.getParameter("corp_code") + "-----wx_show333------" + corp_code);
                String d_match_type = request.getParameter("d_match_type");
                if (null != d_match_type && !d_match_type.equals("")) {
                    BasicDBObject queryCondition = new BasicDBObject();
                    String[] split = d_match_type.split(",");
                    BasicDBList value2 = new BasicDBList();
                    for (int i = 0; i < split.length; i++) {
                        String d_match_type_i = split[i];
                        Pattern pattern = Pattern.compile("^.*" + d_match_type_i + ".*$", Pattern.CASE_INSENSITIVE);
                        value2.add(new BasicDBObject("d_match_type", pattern));
                    }
                    queryCondition.put("$or", value2);
                    value.add(queryCondition);
                    // value.add(new BasicDBObject("d_match_type", new BasicDBObject("$in", value2)));
                }
                String d_match_category = request.getParameter("d_match_category");
                //   System.out.println("-----d_match_category------" + d_match_category);
                if (null != d_match_category && !d_match_category.equals("")) {
                    value.add(new BasicDBObject("d_match_category", d_match_category));
                }
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("isactive", "Y"));

                BasicDBObject queryCondition = new BasicDBObject();
                BasicDBList value2 = new BasicDBList();
                while (dbCursor_rel.hasNext()) {
                    DBObject obj = dbCursor_rel.next();
                    String d_match_code = obj.get("d_match_code").toString();
                    value2.add(new BasicDBObject("d_match_code", d_match_code));
                }
                if (dbCursor_rel.count() > 0) {
                    queryCondition.put("$or", value2);
                    value.add(queryCondition);
                }
                if (dbCursor_rel.count() == 0) {
                    value.add(new BasicDBObject("corp_code", "==不存在=="));
                }
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                result.put("total", dbCursor2.count());


                //=================my_count=======================
                BasicDBList value_user = new BasicDBList();
                value_user.add(new BasicDBObject("corp_code", corp_code));
                value_user.add(new BasicDBObject("isactive", "Y"));
                value_user.add(new BasicDBObject("creater", user_code));

                BasicDBObject queryCondition_user = new BasicDBObject();
                queryCondition_user.put("$and", value_user);
                DBCursor dbCursor_user = cursor.find(queryCondition_user);
                result.put("my_count", dbCursor_user.count());
                //=================collect_count=======================
                result.put("collect_count", shopMatchService.getCollect(corp_code,user_code));
            } else {
                BasicDBList value = new BasicDBList();
                String search_value = request.getParameter("search_value");
                if (null != search_value) {
                    String[] column_names = new String[]{"d_match_title", "d_match_desc", "r_match_goods.r_match_goodsName", "r_match_goods.r_match_goodsCode", "creater"};
                    BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);
                    value.add(queryCondition);
                }
                //     System.out.println(request.getParameter("corp_code") + "-----wx_show333------" + corp_code);
                String d_match_type = request.getParameter("d_match_type");
                if (null != d_match_type && !d_match_type.equals("")) {
                    BasicDBObject queryCondition = new BasicDBObject();
                    String[] split = d_match_type.split(",");
                    BasicDBList value2 = new BasicDBList();
                    for (int i = 0; i < split.length; i++) {
                        String d_match_type_i = split[i];
                        Pattern pattern = Pattern.compile("^.*" + d_match_type_i + ".*$", Pattern.CASE_INSENSITIVE);
                        value2.add(new BasicDBObject("d_match_type", pattern));
                    }
                    queryCondition.put("$or", value2);
                    value.add(queryCondition);
                    // value.add(new BasicDBObject("d_match_type", new BasicDBObject("$in", value2)));
                }
                String d_match_category = request.getParameter("d_match_category");
                //     System.out.println("-----d_match_category------" + d_match_category);
                if (null != d_match_category && !d_match_category.equals("")) {
                    value.add(new BasicDBObject("d_match_category", d_match_category));
                }
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(new BasicDBObject("isactive", "Y"));

                BasicDBObject queryCondition = new BasicDBObject();
//                BasicDBList value2 = new BasicDBList();
//                while (dbCursor_rel.hasNext()) {
//                    DBObject obj = dbCursor_rel.next();
//                    String d_match_code = obj.get("d_match_code").toString();
//                    value2.add(new BasicDBObject("d_match_code", d_match_code));
//                }
                value.add(new BasicDBObject("creater", user_code));
//                queryCondition.put("$or", value2);
                value.add(queryCondition);

                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor2 = cursor.find(queryCondition1);
                pages = MongoUtils.getPages(dbCursor2, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "created_date", -1);
                result.put("total", dbCursor2.count());

                //=================my_count=======================
                BasicDBList value_user = new BasicDBList();
                value_user.add(new BasicDBObject("corp_code", corp_code));
                value_user.add(new BasicDBObject("isactive", "Y"));
                value_user.add(new BasicDBObject("creater", user_code));

                BasicDBObject queryCondition_user = new BasicDBObject();
                queryCondition_user.put("$and", value_user);
                DBCursor dbCursor_user = cursor.find(queryCondition_user);
                result.put("my_count", dbCursor_user.count());
                //=================collect_count=======================
//                DBCollection cursor_rel = mongoTemplate.getCollection(CommonValue.table_shop_match_rel);
//                BasicDBList value_rel = new BasicDBList();
//                value_rel.add(new BasicDBObject("corp_code", corp_code));
//                value_rel.add(new BasicDBObject("operate_userCode", user_code));
//                value_rel.add(new BasicDBObject("operate_type", "collect"));
//                BasicDBObject queryCondition_rel = new BasicDBObject();
//                queryCondition_rel.put("$and", value_rel);
//                DBCursor dbCursor_rel = cursor_rel.find(queryCondition_rel);
                result.put("collect_count", shopMatchService.getCollect(corp_code,user_code));
            }
            ArrayList list = MongoHelperServiceImpl.dbCursorToList_shop(dbCursor, user_code);
            System.out.println("===========拉取列表接口============" + list.size());
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


    //筛选
    @RequestMapping(value = "/api/shopMatch/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        DataBean dataBean = new DataBean();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code") + "";
            String jsString = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String lists = jsonObject.get("list").toString();
            String sort_key = String.valueOf(jsonObject.get("sort_key"));
            String sort_ = "new";
            if (null != sort_key && !sort_key.equals("") && !sort_key.equals("null")) {
                if (sort_key.equals("like")) {
                    sort_ = "d_match_likeCount";
                } else if (sort_key.equals("new")) {
                    sort_ = "created_date";
                } else if (sort_key.equals("shopCount")) {
                    sort_ = "shopCount";
                } else if (sort_key.equals("collect")) {
                    sort_ = "d_match_collectCount";
                } else if (sort_key.equals("comment")) {
                    sort_ = "d_match_commentCount";
                }
            }

            String sort_type = String.valueOf(jsonObject.get("sort_type"));
            String sort_2 = "-1";
            if (null != sort_type && !sort_type.equals("") && !sort_type.equals("null")) {
                if (sort_type.equals("-1")) {
                    sort_2 = "-1";
                } else if (sort_type.equals("1")) {
                    sort_2 = "1";
                }
            }
            JSONArray array = JSONArray.parseArray(lists);
            BasicDBObject queryCondition = MongoHelperServiceImpl.andLoginlogScreen(array);

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_def);

            // DBCollection cursor_share = mongoTemplate.getCollection(CommonValue.table_log_production_share);
            DBCursor dbCursor = null;
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                DBCursor dbCursor1 = cursor.find(queryCondition);

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, sort_, Integer.parseInt(sort_2));
                result.put("total", dbCursor1.count());
            } else if (role_code.equals(Common.ROLE_CM)) {
//                BasicDBList value = new BasicDBList();
//                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                System.out.println("manager_corp=====>" + manager_corp);
//                String[] split = manager_corp.split(",");
//                BasicDBList manager_corp_arr = new BasicDBList();
//                for (int i = 0; i < split.length; i++) {
//                    manager_corp_arr.add(split[i]);
//                }
//                if (manager_corp_arr.size() > 0) {
//                    value.add(new BasicDBObject("corp_code", new BasicDBObject("$in", manager_corp_arr)));
//                }
//                value.add(queryCondition);
//                BasicDBObject queryCondition1 = new BasicDBObject();
//                queryCondition1.put("$and", value);
//                DBCursor dbCursor1 = cursor.find(queryCondition1);
//                pages = MongoUtils.getPages(dbCursor1, page_size);
//                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, sort_, Integer.parseInt(sort_2));
//                result.put("total", dbCursor1.count());

                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, sort_, Integer.parseInt(sort_2));
                result.put("total", dbCursor1.count());
            } else {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", corp_code));
                value.add(queryCondition);
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor1 = cursor.find(queryCondition1);

                pages = MongoUtils.getPages(dbCursor1, page_size);
                dbCursor = MongoUtils.sortAndPage(dbCursor1, page_number, page_size, sort_, Integer.parseInt(sort_2));
                result.put("total", dbCursor1.count());
            }
            ArrayList list = MongoHelperServiceImpl.dbCursorToList_shop(dbCursor, user_code);
            System.out.println("===========拉取筛选接口============" + list.size());
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


    @RequestMapping(value = "/api/shopMatch/selectById", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getfindById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String corp_code = request.getParameter("corp_code");
            String d_match_code = request.getParameter("d_match_code");
            // String id = request.getParameter("id");
            String user_code = request.getParameter("user_code");

            request.getParameter("shopCount");
            System.out.println("==============秀搭详情d_match_code==============" + d_match_code);
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_shop_match_def);
            DBCollection cursor_share = mongoTemplate.getCollection(CommonValue.table_log_production_share);
            DBObject deleteRecord = new BasicDBObject();
            deleteRecord.put("corp_code", corp_code);
            deleteRecord.put("d_match_code", d_match_code);
            deleteRecord.put("isactive", "Y");
            DBCursor dbObjects = cursor.find(deleteRecord);
            DBObject dbObject = new BasicDBObject();

//            String corp_code = dbObject.get("corp_code").toString();
//            String d_match_code = dbObject.get("d_match_code").toString();
            DBObject object = MongoHelperServiceImpl.selectByCode(corp_code, d_match_code, user_code, "like");
            String like_status = "N";
            String collect_status = "N";
            if (object != null) {
                like_status = object.get("status").toString();
            }
            DBObject object2 = MongoHelperServiceImpl.selectByCode(corp_code, d_match_code, user_code, "collect");
            if (object2 != null) {
                collect_status = object2.get("status").toString();
            }
            String type = "N";
            CorpParam is_show_xiuda_shop = corpParamService.selectByParamName("IS_SHOW_XIUDA_SHOP", Common.IS_ACTIVE_Y);
            //    System.out.println( corp_code+"--------------"+select_goods_source.getId());
            if (null != is_show_xiuda_shop) {
                List<CorpParam> corpParams = corpParamService.selectByCorpParam(corp_code, is_show_xiuda_shop.getId() + "", Common.IS_ACTIVE_Y);
                if (corpParams.size() > 0) {
                    type = corpParams.get(0).getParam_value();
                }
            }
            while (dbObjects.hasNext()) {
                dbObject = dbObjects.next();
                dbObject.removeField("_id");
                dbObject.put("like_status", like_status);
                dbObject.put("collect_status", collect_status);

                if (null == dbObject.get("shopMoney") || "".equals(dbObject.get("shopMoney"))) {
                    dbObject.put("shopMoney", 0);
                } else {
                    dbObject.put("shopMoney", String.valueOf(dbObject.get("shopMoney")));
                }
                if (null == dbObject.get("pageViews") || "".equals(dbObject.get("pageViews"))) {
                    dbObject.put("pageViews", 0);
                } else {
                    dbObject.put("pageViews", String.valueOf(dbObject.get("pageViews")));
                }
                if (null == dbObject.get("shopCount") || "".equals(dbObject.get("shopCount"))) {
                    dbObject.put("shopCount", 0);
                } else {
                    dbObject.put("shopCount", String.valueOf(dbObject.get("shopCount")));
                }
                if (null == dbObject.get("shareCount") || "".equals(dbObject.get("shareCount"))) {
                    dbObject.put("shareCount", 0);
                } else {
                    dbObject.put("shareCount", String.valueOf(dbObject.get("shareCount")));
                }
                if (null != request.getParameter("shopMoney") && !"".equals(request.getParameter("shopMoney"))) {
                    dbObject.put("shopMoney", String.valueOf(request.getParameter("shopMoney")));
                }
                if (null != request.getParameter("shopCount") && !"".equals(request.getParameter("shopMoney"))) {
                    dbObject.put("shopCount", String.valueOf(request.getParameter("shopCount")));
                }
                BasicDBObject queryCondition_2 = new BasicDBObject();
                BasicDBList values = new BasicDBList();
                Pattern pattern = Pattern.compile("^.*" + d_match_code + ".*$", Pattern.CASE_INSENSITIVE);
                values.add(new BasicDBObject("product_url", pattern));
                values.add(new BasicDBObject("corp_code", corp_code));
                values.add(new BasicDBObject("source", "Xiuda"));
                queryCondition_2.put("$and", values);
                DBCursor dbObjects_2 = cursor_share.find(queryCondition_2);
                int count = dbObjects_2.count();
                dbObject.put("shareCount", count);
                dbObject.put("is_show_xiuda_shop", type);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(dbObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("信息异常");
        }
        return dataBean.getJsonStr();
    }

    //微商城列表
    @RequestMapping(value = "/api/shopMatch/getProductCategoryByWx", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getProductCategoryByWx(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String corp_code = request.getParameter("corp_code");
            String brand_code = request.getParameter("brand_code");
            String goodsByWx = shopMatchService.getProductCategoryByWx(corp_code, brand_code);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(goodsByWx.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("信息异常");
        }
        return dataBean.getJsonStr();
    }


    //微商城列表
    @RequestMapping(value = "/api/shopMatch/getGoodsByWx", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getGoodsByWx(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String corp_code = request.getParameter("corp_code");
            String pageSize = request.getParameter("pageSize");
            String pageIndex = request.getParameter("pageIndex");
            String categoryId = request.getParameter("categoryId");
            String row_num = request.getParameter("row_num");
            String productName = request.getParameter("productName");
            String store_id = request.getParameter("store_id");
            String brand_code = request.getParameter("brand_code");
            String searchType = request.getParameter("searchType");
            String user_code = request.getSession().getAttribute("user_code") + "";
            //     System.out.println(request.getParameter("user_id")+"-----11111------"+user_code);
            if (user_code.trim().equals("null")) {
                user_code = request.getParameter("user_id");
            }
            System.out.println("------微商城列表---store_id------" + store_id);
            String goodsByWx = shopMatchService.getGoodsByWx(corp_code, pageSize, pageIndex, categoryId, row_num, productName, user_code, store_id, brand_code, searchType);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(goodsByWx.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("信息异常");
        }
        return dataBean.getJsonStr();
    }

    /**
     * 新增秀搭
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/api/shopMatch/addGoodsByWx", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addGoodsByWx(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            String user_code = request.getSession().getAttribute("user_code") + "";
            String corp_code = request.getSession().getAttribute("corp_code") + "";
            String role_code = request.getSession().getAttribute("role_code") + "";
            if (!role_code.trim().equals("null") && role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.getString("corp_code");
            } else if (role_code.trim().equals("null")) {
                corp_code = jsonObject.getString("corp_code");
            } else if (role_code.equals(Common.ROLE_CM)) {
                corp_code = jsonObject.getString("corp_code");
            }
            if (user_code.trim().equals("null")) {
                user_code = jsonObject.getString("user_code");
            }
            String d_match_show_wx = "N";
            if (jsonObject.containsKey("d_match_show_wx")) {
                d_match_show_wx = jsonObject.get("d_match_show_wx").toString();
            }
            String d_match_category = "个人";
            if (role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_CM) ||
                    role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_BM) || role_code.equals(Common.ROLE_AM)) {
                d_match_category = "企业";
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String d_match_code = "P" + sdf.format(new Date()) + Math.round(Math.random() * 9) + Math.round(Math.random() * 9) + Math.round(Math.random() * 9);

            String d_match_title = jsonObject.getString("d_match_title");
            String d_match_image = jsonObject.getString("d_match_image");
            String d_match_desc = jsonObject.getString("d_match_desc");
            String d_match_type = jsonObject.getString("d_match_type");
            JSONArray r_match_goods = JSON.parseArray(jsonObject.getString("r_match_goods"));
            String isactive = "Y";
            if (jsonObject.containsKey("isactive")) {
                isactive = jsonObject.getString("isactive");
            } else {
                isactive = "Y";
            }
            shopMatchService.insert(corp_code, d_match_code, d_match_title, d_match_image, d_match_desc, d_match_type, r_match_goods, user_code, isactive, d_match_show_wx, d_match_category);

            com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
            result.put("corp_code", corp_code);
            result.put("d_match_code", d_match_code);
            result.put("user_code", user_code);
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toJSONString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        response.setHeader("Content-Type", "application/json;charset=UTF-8");

        return dataBean.getJsonStr();
    }

    /**
     * 编辑
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/api/shopMatch/updGoodsByWx", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updGoodsByWx(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();

        DBCollection collection_def = mongoTemplate.getCollection(CommonValue.table_shop_match_def);
        try {
            Date now = new Date();
            String jsString = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            String user_code = request.getSession().getAttribute("user_code") + "";
            String corp_code = request.getSession().getAttribute("corp_code") + "";
            String role_code = request.getSession().getAttribute("role_code") + "";
            if (!role_code.trim().equals("null") && role_code.equals(Common.ROLE_SYS)) {
                corp_code = jsonObject.getString("corp_code");
            } else if (role_code.trim().equals("null")) {
                corp_code = jsonObject.getString("corp_code");
            } else if (role_code.equals(Common.ROLE_CM)) {
                corp_code = jsonObject.getString("corp_code");
            }
            if (user_code.trim().equals("null")) {
                user_code = jsonObject.getString("user_code");
            }
            String d_match_show_wx = "N";
            if (jsonObject.containsKey("d_match_show_wx")) {
                d_match_show_wx = jsonObject.get("d_match_show_wx").toString();
            }
            //   SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String d_match_code = jsonObject.getString("d_match_code");

            String d_match_title = jsonObject.getString("d_match_title");
            String d_match_image = jsonObject.getString("d_match_image");
            String d_match_desc = jsonObject.getString("d_match_desc");
            String d_match_type = jsonObject.getString("d_match_type");
            JSONArray r_match_goods = JSON.parseArray(jsonObject.getString("r_match_goods"));

            String isactive = "Y";
            if (jsonObject.containsKey("isactive")) {
                isactive = jsonObject.getString("isactive");
            } else {
                isactive = "Y";
            }

            BasicDBObject queryCondition = new BasicDBObject();
            BasicDBList values = new BasicDBList();
            values.add(new BasicDBObject("corp_code", corp_code));
            values.add(new BasicDBObject("d_match_code", d_match_code));
            queryCondition.put("$and", values);

            DBObject updatedValue = new BasicDBObject();
            updatedValue.put("d_match_title", d_match_title);
            updatedValue.put("d_match_image", d_match_image);
            updatedValue.put("d_match_desc", d_match_desc);
            updatedValue.put("d_match_type", d_match_type);
            updatedValue.put("r_match_goods", r_match_goods);
            updatedValue.put("modified_date", Common.DATETIME_FORMAT.format(now));
            updatedValue.put("modifier", user_code);
            updatedValue.put("isactive", isactive);
            updatedValue.put("d_match_show_wx", d_match_show_wx);
            DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
            collection_def.update(queryCondition, updateSetValue);

            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        response.setHeader("Content-Type", "application/json;charset=UTF-8");

        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/shopMatch/addRelByType", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addRelByType(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();

        DBCollection collection_def = mongoTemplate.getCollection(CommonValue.table_shop_match_def);
        try {

            String jsString = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            String corp_code = jsonObject.getString("corp_code");
            String d_match_code = jsonObject.getString("d_match_code");
            String operate_userCode = jsonObject.getString("operate_userCode");
            String operate_type = jsonObject.getString("operate_type");
            String status = jsonObject.getString("status");
            String comment_text = jsonObject.getString("comment_text");
            String count = "0";
            DBObject dbObject = shopMatchService.selectByCode(corp_code, d_match_code);
            if (operate_type.equals("like")) {
                shopMatchService.addRelByType(corp_code, d_match_code, operate_userCode, operate_type, status, comment_text);
                int d_match_likeCount = 0;
                if (null == dbObject.get("d_match_likeCount") || dbObject.get("d_match_likeCount").toString().equals("")) {
                    d_match_likeCount = 0;
                } else {
                    d_match_likeCount = Integer.parseInt(dbObject.get("d_match_likeCount").toString());
                }
                d_match_likeCount = d_match_likeCount + 1;
                if (d_match_likeCount < 0) {
                    d_match_likeCount = 0;
                }
                System.out.println("-------点赞数--------------------------------" + d_match_likeCount);
                BasicDBObject queryCondition = new BasicDBObject();
                BasicDBList values = new BasicDBList();
                values.add(new BasicDBObject("corp_code", corp_code));
                values.add(new BasicDBObject("d_match_code", d_match_code));
                queryCondition.put("$and", values);

                DBObject updatedValue = new BasicDBObject();
                updatedValue.put("d_match_likeCount", d_match_likeCount);
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                collection_def.update(queryCondition, updateSetValue);

                DBObject result_dbObject = shopMatchService.selectByCode(corp_code, d_match_code);
                count = result_dbObject.get("d_match_likeCount").toString();
            } else if (operate_type.equals("collect")) {
                shopMatchService.addRelByType(corp_code, d_match_code, operate_userCode, operate_type, status, comment_text);

                int d_match_collectCount = 0;
                if (null == dbObject.get("d_match_collectCount") || dbObject.get("d_match_collectCount").toString().equals("")) {
                    d_match_collectCount = 0;
                } else {
                    d_match_collectCount = Integer.parseInt(dbObject.get("d_match_collectCount").toString());
                }

                d_match_collectCount = d_match_collectCount + 1;

                if (d_match_collectCount < 0) {
                    d_match_collectCount = 0;
                }
                BasicDBObject queryCondition = new BasicDBObject();
                BasicDBList values = new BasicDBList();
                values.add(new BasicDBObject("corp_code", corp_code));
                values.add(new BasicDBObject("d_match_code", d_match_code));
                queryCondition.put("$and", values);

                DBObject updatedValue = new BasicDBObject();
                updatedValue.put("d_match_collectCount", d_match_collectCount);
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                collection_def.update(queryCondition, updateSetValue);

                DBObject result_dbObject = shopMatchService.selectByCode(corp_code, d_match_code);
                count = result_dbObject.get("d_match_collectCount").toString();
            } else if (operate_type.equals("comment")) {
                shopMatchService.addRelByType(corp_code, d_match_code, operate_userCode, operate_type, status, comment_text);
                int d_match_commentCount = 0;
                if (null == dbObject.get("d_match_commentCount") || dbObject.get("d_match_commentCount").toString().equals("")) {
                    d_match_commentCount = 0;
                } else {
                    d_match_commentCount = Integer.parseInt(dbObject.get("d_match_commentCount").toString());
                }

                d_match_commentCount = d_match_commentCount + 1;

                if (d_match_commentCount < 0) {
                    d_match_commentCount = 0;
                }
                BasicDBObject queryCondition = new BasicDBObject();
                BasicDBList values = new BasicDBList();
                values.add(new BasicDBObject("corp_code", corp_code));
                values.add(new BasicDBObject("d_match_code", d_match_code));
                queryCondition.put("$and", values);

                DBObject updatedValue = new BasicDBObject();
                updatedValue.put("d_match_commentCount", d_match_commentCount);
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                collection_def.update(queryCondition, updateSetValue);

                DBObject result_dbObject = shopMatchService.selectByCode(corp_code, d_match_code);
                count = result_dbObject.get("d_match_commentCount").toString();
            } else if (operate_type.equals("dislike")) {
                int d_match_likeCount = 0;
                shopMatchService.updRelByType(corp_code, d_match_code, operate_userCode, "like");
                if (null == dbObject.get("d_match_likeCount") || dbObject.get("d_match_likeCount").toString().equals("")) {
                    d_match_likeCount = 0;
                } else {
                    d_match_likeCount = Integer.parseInt(dbObject.get("d_match_likeCount").toString());
                }
                d_match_likeCount = d_match_likeCount - 1;
                if (d_match_likeCount < 0) {
                    d_match_likeCount = 0;
                }
                System.out.println("-------取消点赞数--------------------------------" + d_match_likeCount);
                BasicDBObject queryCondition = new BasicDBObject();
                BasicDBList values = new BasicDBList();
                values.add(new BasicDBObject("corp_code", corp_code));
                values.add(new BasicDBObject("d_match_code", d_match_code));
                queryCondition.put("$and", values);

                DBObject updatedValue = new BasicDBObject();
                updatedValue.put("d_match_likeCount", d_match_likeCount);
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                collection_def.update(queryCondition, updateSetValue);

                DBObject result_dbObject = shopMatchService.selectByCode(corp_code, d_match_code);
                count = result_dbObject.get("d_match_likeCount").toString();
            } else if (operate_type.equals("discollect")) {
                int d_match_collectCount = 0;
                shopMatchService.updRelByType(corp_code, d_match_code, operate_userCode, "collect");
                if (null == dbObject.get("d_match_collectCount") || dbObject.get("d_match_collectCount").toString().equals("")) {
                    d_match_collectCount = 0;
                } else {
                    d_match_collectCount = Integer.parseInt(dbObject.get("d_match_collectCount").toString());
                }
                d_match_collectCount = d_match_collectCount - 1;
                if (d_match_collectCount < 0) {
                    d_match_collectCount = 0;
                }
                BasicDBObject queryCondition = new BasicDBObject();
                BasicDBList values = new BasicDBList();
                values.add(new BasicDBObject("corp_code", corp_code));
                values.add(new BasicDBObject("d_match_code", d_match_code));
                queryCondition.put("$and", values);

                DBObject updatedValue = new BasicDBObject();
                updatedValue.put("d_match_collectCount", d_match_collectCount);
                DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
                collection_def.update(queryCondition, updateSetValue);

                DBObject result_dbObject = shopMatchService.selectByCode(corp_code, d_match_code);
                count = result_dbObject.get("d_match_collectCount").toString();
            }
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(count);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        response.setHeader("Content-Type", "application/json;charset=UTF-8");

        return dataBean.getJsonStr();
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/api/shopMatch/deleteByBatch", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String callbackDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String d_match_code = jsonObject.get("d_match_code").toString();

            shopMatchService.deleteByBatch(d_match_code);

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess");

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/shopMatch/delete", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String corp_code = request.getParameter("corp_code");
            String d_match_code = request.getParameter("d_match_code");
            shopMatchService.deleteAll(corp_code, d_match_code);
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("成功");
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("信息异常");
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/api/shopMatch/deleteByApp", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteByApp(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection collection_def = mongoTemplate.getCollection(CommonValue.table_shop_match_def);
            String corp_code = request.getParameter("corp_code");
            String d_match_code = request.getParameter("d_match_code");
            BasicDBObject queryCondition = new BasicDBObject();
            BasicDBList values = new BasicDBList();
            values.add(new BasicDBObject("corp_code", corp_code));
            values.add(new BasicDBObject("d_match_code", d_match_code));
            queryCondition.put("$and", values);

            DBObject updatedValue = new BasicDBObject();
            updatedValue.put("isactive", "N");
            DBObject updateSetValue = new BasicDBObject("$set", updatedValue);
            collection_def.update(queryCondition, updateSetValue);
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("成功");
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage("信息异常");
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/api/shopMatch/getGoodsDetails", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getGoodsDetails(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        try {

            String jsString = request.getParameter("param");
            com.alibaba.fastjson.JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(message);

            String corp_code = jsonObject.getString("corp_code");
            String r_match_goodsType = jsonObject.getString("r_match_goodsType");
            String r_match_goodsCode = jsonObject.getString("r_match_goodsCode");
            String result = "";
            if (r_match_goodsType.equals("wx")) {
                logger.info("==========wx==========================================");
                result = corp_code;
            } else {
                logger.info("======fab==============================" + r_match_goodsCode);
                Goods goods = goodsService.getGoodsByCode(corp_code, r_match_goodsCode, Common.IS_ACTIVE_Y);
                int goos_id = goods.getId();
                result = "" + goos_id;

            }

            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        response.setHeader("Content-Type", "application/json;charset=UTF-8");

        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/api/shopMatch/getUserId", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getUserId(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        try {
            String corp_code = request.getParameter("corp_code");
            String user_id = request.getParameter("user_id");


            Map datalist = new HashMap<String, Data>();
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);

            System.out.println(corp_code + "-----getUserId------------" + user_id);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_user_id.key, data_user_id);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetUserId4Web", datalist);

            String result = dataBox.data.get("message").value;

            System.out.println("-----GetUserId4Web------------" + result);
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        response.setHeader("Content-Type", "application/json;charset=UTF-8");

        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/shopMatch/type/searchByWeb", method = RequestMethod.POST)
    @ResponseBody
    //条件查询
    public String search_type(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            //-------------------------------------------------------
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String role_type = "sys";
            String manager_corp = "";
            if (role_code.equals(Common.ROLE_SYS)) {
                role_type = "sys";
            }
//            else if (role_code.equals(Common.ROLE_CM)) {
//                role_type = "cm";
//                manager_corp = request.getSession().getAttribute("manager_corp").toString();
//                System.out.println("manager_corp=====>" + manager_corp);
//            }
            if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp_json = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>" + manager_corp);
                corp_code = WebUtils.getCorpCodeByCm(manager_corp_json, request.getSession().getAttribute("corp_code_cm"));
                System.out.println("getCorpCodeByCm=====>" + corp_code);
            }
            JSONObject result = new JSONObject();
            PageInfo<ShopMatchType> list = shopMatchService.selectAllMatchType(page_number, page_size, corp_code, search_value, role_type, manager_corp);
            for (ShopMatchType matchType : list.getList()) {
                if (null == matchType.getType()) {
                    matchType.setType("");
                }
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/shopMatch/type/searchByApp", method = RequestMethod.POST)
    @ResponseBody
    //条件查询
    public String searchByApp(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            //-------------------------------------------------------
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<ShopMatchType> list = shopMatchService.selectAllMatchType(page_number, page_size, corp_code, search_value, "sys");
            for (ShopMatchType matchType : list.getList()) {
                if (null == matchType.getType()) {
                    matchType.setType("");
                }
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 增加（用了事务）
     */
    @RequestMapping(value = "/api/shopMatch/type/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addShopMatchType(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        try {
            String jsString = request.getParameter("param");
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);

            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            ShopMatchType shopMatchType = WebUtils.JSON2Bean(jsonObject, ShopMatchType.class);
            String shopmatch_type = shopMatchType.getShopmatch_type();
            List<ShopMatchType> shopMatchTypes = shopMatchService.checkName(corp_code, shopmatch_type);
            if (shopMatchTypes.size() == 0) {
                Date date = new Date();
                shopMatchType.setCreated_date(Common.DATETIME_FORMAT.format(date));
                shopMatchType.setCreater(user_id);
                shopMatchType.setModified_date(Common.DATETIME_FORMAT.format(date));
                shopMatchType.setModifier(user_id);

                if (role_code.equals(Common.ROLE_SYS)) {
                    shopMatchType.setCorp_code(corp_code);
                    shopMatchType.setType("默认");
                } else if (role_code.equals(Common.ROLE_CM)) {
                    shopMatchType.setCorp_code(shopMatchType.getCorp_code());
                    shopMatchType.setType("企业");
                } else {
                    shopMatchType.setCorp_code(corp_code);
                    shopMatchType.setType("企业");
                }
                int count = shopMatchService.addShopMatchType(shopMatchType);
                if (count > 0) {
                    List<ShopMatchType> shopMatchTypes1 = shopMatchService.checkName(shopMatchType.getCorp_code(), shopMatchType.getShopmatch_type());
                    if (shopMatchTypes1.size() > 0) {
                        ShopMatchType shopMatchType1 = shopMatchTypes1.get(0);
                        int id = shopMatchType1.getId();
                        dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                        dataBean.setId("1");
                        dataBean.setMessage(id + "");

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
                        String function = "商品管理_秀搭类型";
                        String action = Common.ACTION_ADD;
                        String t_corp_code = action_json.get("corp_code").toString();
                        String t_code = shopmatch_type;
                        String t_name = "";
                        String remark = "";
                        baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                        //-------------------行为日志结束----

                    }
                } else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("-1");
                    dataBean.setMessage("操作失败");
                }
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("名称已存在");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/shopMatch/type/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete_type(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String feed_id = jsonObject.get("id").toString();
            String[] ids = feed_id.split(",");

            for (int i = 0; i < ids.length; i++) {
                ShopMatchType shopMatchType = shopMatchService.selShopMatchTypeById(Integer.valueOf(ids[i]));
                String corp_code = shopMatchType.getCorp_code();
                String type = shopMatchType.getShopmatch_type();


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
                String function = "商品管理_秀搭类型";
                String action = Common.ACTION_DEL;
                String t_corp_code = corp_code;
                String t_code = type;
                String t_name = "";
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------

                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                shopMatchService.delShopMatchTypeById(Integer.valueOf(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 根据ID查询
     */
    @RequestMapping(value = "/api/shopMatch/type/selectById", method = RequestMethod.POST)
    @ResponseBody
    public String selectById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--delete-------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String feed_id = jsonObject.get("id").toString();
            ShopMatchType shopMatchType = shopMatchService.selShopMatchTypeById(Integer.parseInt(feed_id));
            JSONObject result = new JSONObject();
            result.put("shopMatchType", JSON.toJSONString(shopMatchType));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        logger.info("selectById-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 编辑(加了事务)
     */
    @RequestMapping(value = "/api/shopMatch/type/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editCrop(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();

        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            ShopMatchType shopMatchType = WebUtils.JSON2Bean(jsonObject, ShopMatchType.class);
            //------------操作日期-------------
            Date date = new Date();
            shopMatchType.setModified_date(Common.DATETIME_FORMAT.format(date));
            shopMatchType.setModifier(user_id);
            if (role_code.equals(Common.ROLE_SYS)) {
                shopMatchType.setCorp_code(corp_code);
                shopMatchType.setType("默认");
            } else if (role_code.equals(Common.ROLE_CM)) {
                shopMatchType.setCorp_code(shopMatchType.getCorp_code());
                shopMatchType.setType("企业");
            } else {
                shopMatchType.setCorp_code(corp_code);
                shopMatchType.setType("企业");
            }
            String result = shopMatchService.updShopMatchType(shopMatchType);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("edit success");
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
                String function = "商品管理_秀搭类型 ";
                String action = Common.ACTION_UPD;
                String t_corp_code = shopMatchType.getCorp_code();
                String t_code = action_json.get("shopmatch_type").toString();
                String t_name = "";
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name, remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------


            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        logger.info("info--------" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/shopMatch/type/checkName", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String checkName(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String shopmatch_type = jsonObject.get("shopmatch_type").toString();

            List<ShopMatchType> shopMatchTypes = shopMatchService.checkName(corp_code, shopmatch_type);
            if (shopMatchTypes.size() == 0) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("名称可以使用");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("名称已存在");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/shopMatch/getProuctDetails", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String getProuctDetails(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String productId = jsonObject.get("productId").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String store_id = "";
            if (jsonObject.containsKey("store_id")) {
                store_id = jsonObject.get("store_id").toString();
            }

            Map datalist = new HashMap<String, Data>();
            Data data_code = new Data("code", productId, ValueType.PARAM);
            Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            datalist.put(data_code.key, data_code);
            datalist.put(data_store_id.key, data_store_id);
            datalist.put(data_corp_code.key, data_corp_code);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetProuctDetails", datalist);
            String result = dataBox.data.get("message").value;
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage("操作失败");
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/shopMatch/getUrlByWx", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String getUrlByWx(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String user_id = "";
            if (jsonObject.containsKey("user_id")) {
                user_id = jsonObject.get("user_id").toString();
            }
            String store_id = "";
            if (jsonObject.containsKey("store_id")) {
                store_id = jsonObject.get("store_id").toString();
            }

            String[] split_ids = null;
            if (jsonObject.containsKey("ids")) {
                String ids = jsonObject.get("ids").toString();
                split_ids = ids.split(",");
            }


            String[] split_counts = null;
            if (jsonObject.containsKey("counts")) {
                String counts = jsonObject.get("counts").toString();
                split_counts = counts.split(",");
            }
            String show_code = "";
            if (jsonObject.containsKey("d_match_code")) {
                show_code = jsonObject.get("d_match_code").toString();

            }
            Map datalist = new HashMap<String, Data>();
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_store_id = new Data("store_id", store_id, ValueType.PARAM);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_store_id.key, data_store_id);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetAppId", datalist);
            String result = dataBox.data.get("message").value;

            if (null != result) {
                JSONObject object = JSON.parseObject(result);
                String appid = object.get("appid").toString();

                if (!"".equals(appid)) {
//                    url=CommonValue.wei_openId_url;
//                    url = url.replace("@appid@",appid);
//                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                    dataBean.setId(id);
//                    dataBean.setMessage(url);
                    String wx_url_dev = CommonValue.wx_url(split_ids, split_counts, show_code, appid, user_id);
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId(id);
                    dataBean.setMessage(wx_url_dev);
                }
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("获取AppID失败");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage("操作失败");
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/shopMatch/getShopMatchShareLog", method = RequestMethod.POST)
    @ResponseBody
    public String getShopMatchShareLog(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        try {
            int pages = 0;
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection shop_match_share_log = mongoTemplate.getCollection(CommonValue.table_shop_match_share_log);
            DBCollection shop_match_def = mongoTemplate.getCollection(CommonValue.table_shop_match_def);
            String jsString = request.getParameter("param");
            logger.info("getShopMatchShareLog---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String user_id = jsonObject.getString("user_id");
            String pageNumber = jsonObject.getString("pageNumber");
            String pageSize = jsonObject.getString("pageSize");
            int page_number = Integer.valueOf(pageNumber);
            int page_size = Integer.valueOf(pageSize);
            String sort_key = jsonObject.getString("sort_key");//
            String sort_type = jsonObject.getString("sort_type");
            String sort_2 = "-1";
            if (null != sort_type && !sort_type.equals("")) {
                if (sort_type.equals("-1")) {
                    sort_2 = "-1";
                } else if (sort_type.equals("1")) {
                    sort_2 = "1";
                }
            }
            String sort_ = "share_date";
            if (null != sort_key && !sort_key.equals("")) {
                if (sort_key.equals("like")) {
                    sort_ = "pageViews";
                } else if (sort_key.equals("new")) {
                    sort_ = "share_date";
                } else if (sort_key.equals("shopCount")) {
                    sort_ = "shopCount";
                } else if (sort_key.equals("collect")) {
                    sort_ = "d_match_collectCount";
                } else if (sort_key.equals("comment")) {
                    sort_ = "d_match_commentCount";
                }
            }
            BasicDBObject queryCondition1 = new BasicDBObject();
            BasicDBList value = new BasicDBList();
            value.add(new BasicDBObject("corp_code", corp_code));
            value.add(new BasicDBObject("user_id", user_id));


            String search_value = jsonObject.getString("search_value");
            if (null != search_value) {
                String[] column_names = new String[]{"shop_match.d_match_title", "shop_match.d_match_desc", "shop_match.r_match_goods.r_match_goodsName", "shop_match.r_match_goods.r_match_goodsCode", "shop_match.creater"};
                BasicDBObject queryCondition = MongoUtils.orOperation(column_names, search_value);
                value.add(queryCondition);
            }
            System.out.println(jsonObject.getString("corp_code") + "-----wx_show333------" + corp_code);
            String d_match_type = jsonObject.getString("d_match_type");
            if (null != d_match_type && !d_match_type.equals("")) {
                BasicDBObject queryCondition = new BasicDBObject();
                String[] split = d_match_type.split(",");
                BasicDBList value2 = new BasicDBList();
                for (int i = 0; i < split.length; i++) {
                    String d_match_type_i = split[i];
                    Pattern pattern = Pattern.compile("^.*" + d_match_type_i + ".*$", Pattern.CASE_INSENSITIVE);
                    value2.add(new BasicDBObject("shop_match.d_match_type", pattern));
                }
                queryCondition.put("$or", value2);
                value.add(queryCondition);
            }
            String d_match_category = jsonObject.getString("d_match_category");
            System.out.println("-----d_match_category------" + d_match_category);
            if (null != d_match_category && !d_match_category.equals("")) {
                value.add(new BasicDBObject("shop_match.d_match_category", d_match_category));
            }

            queryCondition1.put("$and", value);
            DBCursor dbCursor2 = shop_match_share_log.find(queryCondition1);
            pages = MongoUtils.getPages(dbCursor2, page_size);
            DBCursor dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, sort_, Integer.parseInt(sort_2));
            ArrayList list = new ArrayList();
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                String id = obj.get("_id").toString();
                obj.put("id", id);
                obj.removeField("_id");
                if(obj.containsField("d_match_code")){
                    DBObject deleteRecord = new BasicDBObject();
                    deleteRecord.put("corp_code", corp_code);
                    deleteRecord.put("d_match_code", obj.get("d_match_code").toString());
                    deleteRecord.put("isactive", "Y");
                    DBCursor dbObjects = shop_match_def.find(deleteRecord);
                    if(dbObjects.count()>0){
                        obj.put("isClick","Y");
                    }else{
                        obj.put("isClick","N");
                    }
                }
                list.add(obj.toMap());
            }
            result.put("total", dbCursor2.count());
            result.put("list", list);
            result.put("pages", pages);
            result.put("page_number", page_number);
            result.put("page_size", page_size);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toJSONString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/shopMatch/addShopMatchPageViewsLog", method = RequestMethod.POST)
    @ResponseBody
    public String addShopMatchPageViewsLog(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("addShopMatchPageViewsLog---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String user_id = jsonObject.getString("user_id");
            String d_match_code = jsonObject.getString("d_match_code");
            String headImg = jsonObject.getString("headImg");
            String nickName = jsonObject.getString("nickName");
            String open_id = jsonObject.getString("open_id");
            String app_id = jsonObject.getString("app_id");
            String store_id = jsonObject.getString("store_id");
            String shopMatchTypes = shopMatchService.AddShopMatchPageViewsLog(corp_code, user_id, d_match_code, headImg, nickName, open_id, app_id, store_id);
            System.out.println(shopMatchTypes);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("新增浏览记录成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/shopMatch/getShopMatchPageViewsLog", method = RequestMethod.POST)
    @ResponseBody
    public String getShopMatchPageViewsLog(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection shop_match_pageviews_log = mongoTemplate.getCollection(CommonValue.table_shop_match_pageviews_log);
            String jsString = request.getParameter("param");
            logger.info("addShopMatchPageViewsLog---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String d_match_code = jsonObject.getString("d_match_code");
            String user_type = jsonObject.getString("user_type");
            String query_type = jsonObject.getString("query_type");
            String pageNumber = jsonObject.getString("pageNumber");
            String pageSize = jsonObject.getString("pageSize");
            String is_shop = jsonObject.getString("is_shop");
            String user_id = jsonObject.getString("user_id");
            int page_number = Integer.valueOf(pageNumber);
            int page_size = Integer.valueOf(pageSize);
            int pages = 0;
            BasicDBObject queryCondition = new BasicDBObject();
            BasicDBList values = new BasicDBList();
            values.add(new BasicDBObject("corp_code", corp_code));
            values.add(new BasicDBObject("d_match_code", d_match_code));
            if (user_type.equals("my")) {

                values.add(new BasicDBObject("user_id", user_id));
            }
            if (query_type.equals("VIP")) {
                values.add(new BasicDBObject("visit", "VIP"));
            } else if (query_type.equals("YK")) {
                values.add(new BasicDBObject("visit", "YK"));
            }
            if (is_shop.equals("Y")) {
                values.add(new BasicDBObject("is_shop", "Y"));
            }
            queryCondition.put("$and", values);
            DBCursor dbCursor2 = shop_match_pageviews_log.find(queryCondition);
            pages = MongoUtils.getPages(dbCursor2, page_size);
            DBCursor dbCursor = MongoUtils.sortAndPage(dbCursor2, page_number, page_size, "date", -1);
            ArrayList list = MongoUtils.dbCursorToListPageViewsLog(dbCursor,user_id);


            //=======会员数量=======
            BasicDBObject queryCondition_vip = new BasicDBObject();
            BasicDBList values_vip = new BasicDBList();
            values_vip.add(new BasicDBObject("corp_code", corp_code));
            values_vip.add(new BasicDBObject("d_match_code", d_match_code));
            if (user_type.equals("my")) {
                values_vip.add(new BasicDBObject("user_id", user_id));
            }
            if (is_shop.equals("Y")) {
                values_vip.add(new BasicDBObject("is_shop", "Y"));
            }
            values_vip.add(new BasicDBObject("visit", "VIP"));
            queryCondition_vip.put("$and", values_vip);
            DBCursor dbCursor_vip = shop_match_pageviews_log.find(queryCondition_vip);
            //=======游客数量=======
            BasicDBObject queryCondition_yk = new BasicDBObject();
            BasicDBList values_yk = new BasicDBList();
            values_yk.add(new BasicDBObject("corp_code", corp_code));
            values_yk.add(new BasicDBObject("d_match_code", d_match_code));
            if (user_type.equals("my")) {
                values_yk.add(new BasicDBObject("user_id", user_id));
            }
            if (is_shop.equals("Y")) {
                values_yk.add(new BasicDBObject("is_shop", "Y"));
            }
            values_yk.add(new BasicDBObject("visit", "YK"));
            queryCondition_yk.put("$and", values_yk);
            DBCursor dbCursor_yk = shop_match_pageviews_log.find(queryCondition_yk);


            JSONObject object = new JSONObject();
            object.put("list", list);
            object.put("pages", pages);
            object.put("page_number", page_number);
            object.put("page_size", page_size);
            object.put("yk_count", dbCursor_yk.count());
            object.put("vip_count", dbCursor_vip.count());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(object.toJSONString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/api/shopMatch/checkStaff", method = RequestMethod.POST)
    @ResponseBody
    public String checkStaff(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("addShopMatchPageViewsLog---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String user_id = jsonObject.getString("user_id");
            String vip_id = jsonObject.getString("vip_id");
            Map datalist = new HashMap<String, Data>();
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_vip_id = new Data("vip_id", vip_id, ValueType.PARAM);
            Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_vip_id.key, data_vip_id);
            datalist.put(data_user_id.key, data_user_id);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("CheckStaff", datalist);
            String result = dataBox.data.get("message").value;
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }



    @RequestMapping(value = "/api/shopMatch/getBrandByUser", method = RequestMethod.POST)
    @ResponseBody
    public String GetBrandByUser(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("addShopMatchPageViewsLog---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_code = jsonObject.getString("corp_code");
            String user_id = jsonObject.getString("user_id");

            Map datalist = new HashMap<String, Data>();
            Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
            Data data_user_id = new Data("user_id", user_id, ValueType.PARAM);
            datalist.put(data_corp_code.key, data_corp_code);
            datalist.put(data_user_id.key, data_user_id);
            DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetBrandByUser", datalist);
            String result = dataBox.data.get("message").value;
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

}
