package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.QrCode;
import com.github.pagehelper.PageInfo;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/3/14.
 */
public interface QrCodeService {

    QrCode selectQrCodeById(int id) throws  Exception;

    PageInfo<QrCode> selectAll(int page_number, int page_size,String corp_code,String search_value) throws  Exception;

    int delQrcodeById(int id) throws  Exception;

    int insertQrcode(QrCode qrCode) throws  Exception;

    int  updateQrcode(QrCode qrCode) throws  Exception;

    PageInfo<QrCode> selectAllScreen(int page_number, int page_size,String corp_code, Map<String,String> map) throws  Exception;

    QrCode selectQrCodeByQrcodeName(String corp_code, String qrcode_name) throws  Exception;

    public List<QrCode> selectQrcode(String app_id,String[] types) throws  Exception;

    JSONObject getQrcodeScan(DBCollection cursor, String app_id, String qrcode_id, String date) throws Exception;

    JSONObject getEmpQrcodeScan(DBCollection cursor,String app_id, String user_code, String date) throws Exception;

    JSONObject getStoreQrcodeScan(DBCollection cursor,String app_id, String store_code,String date) throws Exception;

    ArrayList dbCursorToList(DBCursor dbCursor, int type) throws Exception;

    public  ArrayList dbCursorToListForExecl(DBCursor dbCursor,int type) throws Exception;
}
