package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipAlbum;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/8/31.
 */
public interface VipAlbumMapper {

    List<VipAlbum> selectAllAlbum(@Param("corp_code")String corp_code,@Param("search_value")String search_value) throws SQLException;

    int insertVipAlbum(VipAlbum vipAlbum) throws SQLException;

    int deleteVipAlbum(int id) throws SQLException;

    List<VipAlbum> selectAlbumScreen(Map<String, Object> params) throws SQLException;

    List<VipAlbum> selectAlbumByVip(@Param("corp_code")String corp_code,@Param("vip_code")String vip_code) throws SQLException;

}
