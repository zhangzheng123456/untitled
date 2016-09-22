package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import com.sun.corba.se.spi.ior.ObjectId;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.System;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by zhouying on 2016-08-31.
 */


/**
 * 会员相册管理
 */

@Controller
@RequestMapping("/vipAlbum")
public class VipAlbumController {

    private static final Logger logger = Logger.getLogger(VipAlbumController.class);
    String id;

    @Autowired
    private VipAlbumService vipAlbumService;
    @Autowired
    MongoDBClient mongodbClient;
    /**
     * 列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String vipAlbumList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONObject info = new JSONObject();
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员(官方画面)
                PageInfo<VipAlbum> vipAlbums = vipAlbumService.getAllVipAlbum(page_number, page_size, "", "");
                info.put("list", JSON.toJSONString(vipAlbums));
            } else {
                //用户画面
                PageInfo<VipAlbum> vipAlbums = vipAlbumService.getAllVipAlbum(page_number, page_size, corp_code, "");
                info.put("list", JSON.toJSONString(vipAlbums));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(info.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addVipAlbum(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_code = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject obj = JSONObject.parseObject(message);
            String image_url = obj.get("image_url").toString();
            int result = vipAlbumService.insertVipAlbum(obj, user_code);
            if (result > 0) {
                String album_id = vipAlbumService.selectAlbumByUrl(image_url).getId();
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(album_id);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 删除
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteVipAlbum(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String corp_ids = jsonObject.get("id").toString();
            String[] ids = corp_ids.split(",");
            String msg = null;
            for (int i = 0; i < ids.length; i++) {
                vipAlbumService.deleteVipAlbum(Integer.valueOf(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            //	return "Error deleting the user:" + ex.toString();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 选择
     */
//    @RequestMapping(value = "/select", method = RequestMethod.POST)
//    @ResponseBody
//    public String findById(HttpServletRequest request) {
//        DataBean bean = new DataBean();
//        String data = null;
//        try {
//            String jsString = request.getParameter("param");
//            logger.info("json---------------" + jsString);
//            System.out.println("json---------------" + jsString);
//            JSONObject jsonObj = JSONObject.parseObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            JSONObject jsonObject = JSONObject.parseObject(message);
//            String corp_id = jsonObject.get("id").toString();
//            Corp corp = corpService.selectByCorpId(Integer.parseInt(corp_id), "", "");
//            List<CorpWechat> corpWechat = corpService.getWByCorp(corp.getCorp_code());
//            corp.setWechats(corpWechat);
//            data = JSON.toJSONString(corp);
//            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            bean.setId("1");
//            bean.setMessage(data);
//        } catch (Exception e) {
//            bean.setCode(Common.DATABEAN_CODE_ERROR);
//            bean.setId("1");
//            bean.setMessage("企业信息异常");
//        }
//        logger.info("info-----" + bean.getJsonStr());
//        return bean.getJsonStr();
//    }

    /**
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            PageInfo<VipAlbum> vipAlbums;
            JSONObject result = new JSONObject();
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员(官方画面)
                vipAlbums = vipAlbumService.getAllVipAlbum(page_number, page_size, "", search_value);
            } else {
                //用户画面
                vipAlbums = vipAlbumService.getAllVipAlbum(page_number, page_size, corp_code, search_value);
            }
            result.put("list", JSON.toJSONString(vipAlbums));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String screen(HttpServletRequest request) {
        String role_code = request.getSession().getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            JSONObject result = new JSONObject();
            PageInfo<VipAlbum> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipAlbumService.getAllVipAlbumScreen(page_number, page_size, "", map);
            } else {
                list = vipAlbumService.getAllVipAlbumScreen(page_number, page_size, corp_code, map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }






    /**
     * MongDB
     * 会员相册
     * 新增
     */
    @RequestMapping(value = "/vipAlbumAdd", method = RequestMethod.POST)
    @ResponseBody
    public String vipAlbumAdd(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String vip_id = jsonObject.get("vip_id").toString();


            String vip_code = jsonObject.get("vip_code").toString();
            String image_url = jsonObject.get("image_url").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection("log_vip_list");
            BasicDBObject dbObject=new BasicDBObject();
            dbObject.put("vip_code",vip_code);
            dbObject.put("image_url",image_url);
            BasicDBObject dbObject1=new BasicDBObject();
            dbObject1.put("albums",dbObject);
            BasicDBObject dbObject2=new BasicDBObject();
            dbObject1.put("$addToSet",dbObject1);
            //根据vip_code,image_url匹配查询到某条记录中满足要求的会员相册
            BasicDBObject query = new BasicDBObject();
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                query.put("vip_id", vip_id);
            } else {
                query.put("corp_code", corp_code);
                query.put("vip_id", vip_id);
            }

            cursor.update(query,dbObject2);
            DBCursor dbCursor = cursor.find(query);

            ArrayList list = MongoUtils.dbCursorToList(dbCursor);
            result.put("list", list);
            result.put("dbObject",dbObject);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * MongoDB
     * 会员相册删除
     */
    @RequestMapping(value = "/vipAlbumDelete", method = RequestMethod.POST)
    @ResponseBody
    public String vipAlbumDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        JSONObject result = new JSONObject();
        int pages = 0;
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String vip_id = jsonObject.get("vip_id").toString();


            String vip_code = jsonObject.get("vip_code").toString();
            String image_url = jsonObject.get("image_url").toString();

            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection("log_vip_list");
            BasicDBObject dbObject=new BasicDBObject();
            dbObject.put("vip_code",vip_code);
            dbObject.put("image_url",image_url);
            BasicDBObject dbObject1=new BasicDBObject();
            dbObject1.put("albums",dbObject);
            BasicDBObject dbObject2=new BasicDBObject();
            dbObject1.put("$pull",dbObject1);
            //根据vip_code,image_url匹配查询到某条记录中满足要求的会员相册
            BasicDBObject query = new BasicDBObject();
            // 读取数据
            if (role_code.equals(Common.ROLE_SYS)) {
                query.put("vip_id", vip_id);
            } else {
                query.put("corp_code", corp_code);
                query.put("vip_id", vip_id);
            }

            cursor.update(query,dbObject2);
            DBCursor dbCursor = cursor.find(query);

            ArrayList list = MongoUtils.dbCursorToList(dbCursor);
            result.put("list", list);
            result.put("dbObject",dbObject);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

}
