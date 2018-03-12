package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VipCardType;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/12/29.
 */
public interface VipCardTypeService {

    VipCardType getVipCardTypeById(int id) throws Exception;

    PageInfo<VipCardType> getAllVipCardTypeByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception;

    String insert(String message, String user_id) throws Exception;

    String update(String message, String user_id) throws Exception;

    int delete(int id) throws Exception;

    PageInfo<VipCardType> getAllVipCardTypeScreen(int page_number, int page_size, String corp_code, String brand_code, String store_group_code,Map<String, String> map) throws Exception;

    VipCardType getVipCardTypeByCode(String corp_code,String vip_card_type_code,String isactive)throws Exception;

    VipCardType getVipCardTypeByName(String corp_code,String vip_card_type_name,String isactive)throws Exception;

    List<VipCardType> getVipCardTypes(String corp_code, String isactive,String search_value)throws Exception;

    List<VipCardType> getVipCardByRole(String corp_code, String isactive,String brand_code, String store_group_code) throws Exception;

    PageInfo<VipCardType> getVipCardByRole(int pageNum, int pageSize, String corp_code, String isactive,String brand_code, String store_group_code) throws Exception;

    int insertVipCardType(VipCardType vipCardType) throws Exception;

    VipCardType isExistByType(String corp_code,String vip_card_type_id, String vip_card_type_code,String vip_card_type_name)throws Exception;

    public VipCardType isExistByTypeTwo(String corp_code, String vip_card_type_id, String vip_card_type_code, String vip_card_type_name) throws Exception;
}
