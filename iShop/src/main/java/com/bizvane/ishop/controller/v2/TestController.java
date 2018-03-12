package com.bizvane.ishop.controller.v2;

import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.service.CorpService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.BaseService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import org.bson.types.ObjectId;
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
import java.util.concurrent.locks.Condition;
import java.util.regex.Pattern;

/**
 * Created by PC on 2017/3/24.
 */
@Controller
@RequestMapping("/testcopy")
public class TestController {
    @Autowired
    MongoDBClient mongodbClient;
    @Autowired
    private StoreService storeService;
    @Autowired
    private CorpService corpService;

    @RequestMapping(value = "/article_copy", method = RequestMethod.POST)
    @ResponseBody
    public String article_copy(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection("article");

        DBCollection collection = mongoTemplate.getCollection("article_copy");
        try {
            String[] column_names = new String[]{"headerImage"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names, "");
            DBCursor dbCursor = cursor.find(queryCondition);
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                String headerImage = obj.get("headerImage").toString();
                String headerImage_replace = headerImage.replace("images01.exfox.com.cn", "products-image.oss-cn-hangzhou.aliyuncs.com");
                obj.put("headerImage", headerImage_replace);
                collection.insert(obj);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/question_copy", method = RequestMethod.POST)
    @ResponseBody
    public String link_gallery_copy(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection("question");

        DBCollection collection = mongoTemplate.getCollection("question_copy");
        try {
            String[] column_names = new String[]{"title"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names, "");
            DBCursor dbCursor = cursor.find(queryCondition);
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                String headerImage = obj.get("imgs").toString();
                JSONArray array = JSON.parseArray(headerImage);
                JSONArray array_copy = new JSONArray();
                for (int i = 0; i < array.size(); i++) {
                    String img = array.get(i).toString();
                    String img_replace = img.replace("images01.exfox.com.cn", "products-image.oss-cn-hangzhou.aliyuncs.com");
                    array_copy.add(img_replace);
                }
                obj.put("imgs", array_copy);
                collection.insert(obj);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/status_copy", method = RequestMethod.POST)
    @ResponseBody
    public String status_copy(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection("status");

        DBCollection collection = mongoTemplate.getCollection("status_copy");
        try {
            String[] column_names = new String[]{"text"};
            BasicDBObject queryCondition = MongoUtils.orOperation(column_names, "");
            DBCursor dbCursor = cursor.find(queryCondition);
            while (dbCursor.hasNext()) {
                DBObject obj = dbCursor.next();
                String headerImage = obj.get("imgs").toString();
                JSONArray array = JSON.parseArray(headerImage);
                JSONArray array_copy = new JSONArray();
                for (int i = 0; i < array.size(); i++) {
                    String img = array.get(i).toString();
                    String img_replace = img.replace("images01.exfox.com.cn", "products-image.oss-cn-hangzhou.aliyuncs.com");
                    array_copy.add(img_replace);
                }
                obj.put("imgs", array_copy);
                collection.insert(obj);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/message_copy", method = RequestMethod.POST)
    @ResponseBody
    public String message_copy(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection("vip_message_content");

        DBCollection collection = mongoTemplate.getCollection("vip_message_content_copy");
        try {
            List<String> dates = new ArrayList<String>();
            List<String> dates_1 = TimeUtils.getMonthAllDays("2017-01-01");
            List<String> dates_2 = TimeUtils.getMonthAllDays("2017-02-01");
            List<String> dates_3 = TimeUtils.getMonthAllDays("2017-03-01");
            List<String> dates_4 = TimeUtils.getMonthAllDays("2017-04-01");
            dates.addAll(dates_1);
            dates.addAll(dates_2);
            dates.addAll(dates_3);
            dates.addAll(dates_4);
            for (int i = 0; i < dates.size(); i++) {
                BasicDBList value = new BasicDBList();
                value.add(new BasicDBObject("corp_code", "C10055"));
                value.add(new BasicDBObject("message_target", "1"));
//                value.add(new BasicDBObject("message_date", new BasicDBObject(QueryOperators.GTE, "2017-01-01")));
//                value.add(new BasicDBObject("message_date", new BasicDBObject(QueryOperators.LTE, "2017-04-28")));
                Pattern pattern = Pattern.compile("^.*" + dates.get(i) + ".*$", Pattern.CASE_INSENSITIVE);
                value.add(new BasicDBObject("message_date", pattern));
                BasicDBObject queryCondition1 = new BasicDBObject();
                queryCondition1.put("$and", value);
                DBCursor dbCursor = cursor.find(queryCondition1);
                if (dbCursor.count() > 0) {

                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }


    @RequestMapping(value = "/changeStoreCount", method = RequestMethod.GET)
    @ResponseBody
    public String changeStoreCount(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        try {
            List<Store> allStoreByCount = storeService.getAllStoreByCount();
            int count = 0;
            if (allStoreByCount.size() > 0) {
                for (Store store : allStoreByCount) {
                    String isopen = store.getIsopen();
                    String open_date = store.getOpen_date();
                    String close_date = store.getClose_date();
                    String open_date_count = "0";
                    if (null == open_date || "".equals(open_date)) {
                        open_date = Common.DATETIME_FORMAT.format(now);
                    }
                    if ((null == close_date || "".equals(close_date)) && isopen.equals("Y")) {
                        String start_time = open_date;
                        String end_time = Common.DATETIME_FORMAT.format(now);

                        Date date_start = format.parse(start_time);
                        Date date_end = format.parse(end_time);
                        open_date_count = String.valueOf(TimeUtils.getDiscrepantDays(date_start, date_end));

                    } else if ((null != close_date || !close_date.equals("")) && isopen.equals("Y")) {
                        String start_time = close_date;
                        String end_time = Common.DATETIME_FORMAT.format(now);
                        Date date_start = format.parse(start_time);
                        Date date_end = format.parse(end_time);
                        int date_count = TimeUtils.getDiscrepantDays(date_start, date_end);
                        String open_date_count_sql = store.getOpen_date_count();
                        int count1 = date_count + Integer.parseInt(open_date_count_sql);
                        open_date_count = String.valueOf(count1);
                    } else if ((null != close_date || !close_date.equals("")) && isopen.equals("N")) {
                        open_date_count = store.getOpen_date_count();
                    }
                    Store store_new = new Store();
                    store_new.setId(store.getId());
                    store_new.setOpen_date_count(String.valueOf(open_date_count));
                    count += storeService.updateStore(store_new);
                }
            }
            System.out.println("-------店铺天数count---------" + count);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }

    @RequestMapping(value = "/updStoreTime", method = RequestMethod.GET)
    @ResponseBody
    public String updStoreTime(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        int count = 0;
        try {
            List<Corp> list = corpService.selectAllCorp();
            for (Corp corp : list) {
                count += storeService.updStoreTime(corp.getCorp_code());
            }
            System.out.println("-------修改时间---------"+count);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();

    }

}
