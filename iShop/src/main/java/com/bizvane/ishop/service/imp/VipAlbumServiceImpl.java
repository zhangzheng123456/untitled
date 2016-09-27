package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipAlbumMapper;
import com.bizvane.ishop.entity.VipAlbum;
import com.bizvane.ishop.service.VipAlbumService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/8/31.
 */
@Service
public class VipAlbumServiceImpl implements VipAlbumService {

    @Autowired
    VipAlbumMapper vipAlbumMapper;

    @Override
    public PageInfo<VipAlbum> getAllVipAlbum(int page_num, int page_size, String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_num, page_size);
        List<VipAlbum> vipAlbumList = vipAlbumMapper.selectAllAlbum(corp_code, search_value);
        for (VipAlbum vipAlbum : vipAlbumList) {
            vipAlbum.setIsactive(CheckUtils.CheckIsactive(vipAlbum.getIsactive()));
        }
        PageInfo<VipAlbum> vip_albums = new PageInfo<VipAlbum>(vipAlbumList);
        return vip_albums;
    }

    @Override
    public int insertVipAlbum(JSONObject obj, String user_code) throws Exception {
        String vip_code = obj.get("vip_code").toString();
        String vip_name = obj.get("vip_name").toString();
        String cardno = obj.get("cardno").toString();
        String image_url = obj.get("image_url").toString();
        String corp_code = obj.get("corp_code").toString();
//        String isactive = obj.get("isactive").toString();
        Date now = new Date();
        VipAlbum vipAlbum = new VipAlbum();
        vipAlbum.setCorp_code(corp_code);
        vipAlbum.setVip_code(vip_code);
        vipAlbum.setVip_name(vip_name);
        vipAlbum.setCardno(cardno);
        vipAlbum.setImage_url(image_url);
        vipAlbum.setCreated_date(Common.DATETIME_FORMAT.format(now));
        vipAlbum.setModified_date(Common.DATETIME_FORMAT.format(now));
        vipAlbum.setCreater(user_code);
        vipAlbum.setModifier(user_code);
        vipAlbum.setIsactive(Common.IS_ACTIVE_Y);
        int i = vipAlbumMapper.insertVipAlbum(vipAlbum);
        return i;
    }

    @Override
    public int deleteVipAlbum(int id) throws Exception {
        return vipAlbumMapper.deleteVipAlbum(id);
    }

    @Override
    public PageInfo<VipAlbum> getAllVipAlbumScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {

        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        List<VipAlbum> vipAlbumList = vipAlbumMapper.selectAlbumScreen(params);
        for (VipAlbum vipAlbum : vipAlbumList) {
            vipAlbum.setIsactive(CheckUtils.CheckIsactive(vipAlbum.getIsactive()));
        }
        PageInfo<VipAlbum> page = new PageInfo<VipAlbum>(vipAlbumList);
        return page;
    }

    @Override
    public List<VipAlbum> selectAlbumByVip(String corp_code, String vip_code) throws Exception {
        List<VipAlbum> vipAlbumList = vipAlbumMapper.selectAlbumByVip(corp_code, vip_code);

        return vipAlbumList;
    }

    @Override
    public VipAlbum selectAlbumByUrl(String image_url) throws Exception {
        VipAlbum vipAlbum = vipAlbumMapper.selectAlbumByUrl(image_url);
        return vipAlbum;
    }
}
