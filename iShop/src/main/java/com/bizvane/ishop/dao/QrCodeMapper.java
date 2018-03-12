package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.QrCode;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/3/14.
 */
public interface QrCodeMapper {

    QrCode selectById(int id) throws  Exception;

    List<QrCode> selectAll(@Param("corp_code")String corp_code,@Param("search_value")String search_value) throws  Exception;

    int deleteById(int id) throws  Exception;

    int insertQrcode(QrCode qrCode) throws Exception;

    int updateQrcode(QrCode qrCode)throws  Exception;

    List<QrCode> selectAllScreen(Map<String, Object> params)throws Exception;

    QrCode selectByQrcodeName(@Param("corp_code")String corp_code,@Param("qrcode_name")String qrcode_name) throws  Exception;

    List<QrCode> selectAllByType(@Param("app_id")String app_id,@Param("array") String[] types) throws Exception;


}
