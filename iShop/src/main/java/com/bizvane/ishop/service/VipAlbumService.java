package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.VipAlbum;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/8/31.
 */
public interface VipAlbumService {

    PageInfo<VipAlbum> getAllVipAlbum(int page_num, int page_size, String corp_code, String search_value) throws Exception;

    int insertVipAlbum(JSONObject obj, String user_code) throws Exception;

    int deleteVipAlbum(int id) throws Exception;

    PageInfo<VipAlbum> getAllVipAlbumScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception;

    List<VipAlbum> selectAlbumByVip(String corp_code, String vip_code) throws Exception;

    VipAlbum selectAlbumByUrl(String image_url) throws Exception;

}
