package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.AppIconCfg;
import com.bizvane.ishop.entity.CorpWechat;
import com.bizvane.ishop.entity.MsgChannelCfg;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/15.
 */
public interface MsgChannelCfgService {

    PageInfo<MsgChannelCfg> selectAllMsgChannelCfg(int page_num, int page_size, String corp_code, String search_value) throws Exception;

    PageInfo<MsgChannelCfg> selectAllMsgChannelCfg(int page_num, int page_size, String corp_code,String search_value,String manager_corp) throws Exception;

    int delete(int id) throws Exception;
    String insert(MsgChannelCfg msgChannelCfg) throws Exception;

    String update(MsgChannelCfg msgChannelCfg) throws Exception;

    MsgChannelCfg getMsgChannelCfgById(int id) throws Exception;

    List<MsgChannelCfg> getMsgChannelCfgByCorp(String corp_code, String isactive) throws Exception;

   List<MsgChannelCfg> getMsgChannelCfgByType(String corp_code,String type, String isactive) throws Exception;

    MsgChannelCfg checkAccount(String corp_code,String type,String channel_name,String channel_account,String child,String sign, String isactive) throws Exception;

    List<MsgChannelCfg> selectByCorpBrand(String corp_code, String brand_code) throws Exception;

    List<MsgChannelCfg> selectByCorpBrandForProduction(String corp_code, String brand_code) throws Exception;


    MsgChannelCfg  checkSign(String corp_code, String sign,String isactive)throws Exception;

}
