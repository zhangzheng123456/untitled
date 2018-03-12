package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.ShopMatchType;
import com.github.pagehelper.PageInfo;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 2016/12/28.
 */
public interface ShopMatchService {
    String getGoodsByWx(String corp_code, String pageSize, String pageIndex, String categoryId, String row_num, String productName,String user_id,String store_id,String brand_code,String searchType) throws Exception;

    String getProductCategoryByWx(String corp_code,String brand_code) throws Exception;


    void insert(String corp_code, String d_match_code, String d_match_title, String d_match_image, String d_match_desc, String productUrl,JSONArray r_match_goods, String user_code,String isactive,String d_match_show_wx,String d_match_category) throws Exception;

    void addRelByType(String corp_code, String d_match_code, String operate_userCode, String operate_type, String status, String comment_text) throws Exception;

    void updRelByType(String corp_code, String d_match_code, String operate_userCode, String operate_type) throws Exception;

    DBObject selectByCode(String corp_code, String d_match_code) throws Exception;

    void deleteAll(String corp_code,String d_match_code)throws  Exception;

     ArrayList dbCursorToList_shop(DBCursor dbCursor) ;

     void deleteByBatch(String d_match_codes)throws  Exception;

    PageInfo<ShopMatchType> selectAllMatchType(int page_number, int page_size,String corp_code, String search_value,String role_type) throws Exception;

    PageInfo<ShopMatchType> selectAllMatchType(int page_number, int page_size,String corp_code, String search_value,String role_type,String manager_corp) throws Exception;

    int addShopMatchType(ShopMatchType shopMatchType) throws Exception;

    String updShopMatchType(ShopMatchType shopMatchType) throws Exception;

    int delShopMatchTypeById(int id) throws Exception;

    ShopMatchType selShopMatchTypeById( int id) throws Exception;

    List<ShopMatchType> checkName(String corp_code, String shopmatch_type) throws Exception;

    String AddShopMatchPageViewsLog(String corp_code,String user_id,String d_match_code,String headImg,String nickName,String open_id,String app_id,String store_id)throws Exception;

    String EditShopMatchByColmn(String corp_code ,String user_id,String d_match_code,String column,String value)throws Exception;

     int getCollect(String corp_code,String user_code)throws Exception;

}
