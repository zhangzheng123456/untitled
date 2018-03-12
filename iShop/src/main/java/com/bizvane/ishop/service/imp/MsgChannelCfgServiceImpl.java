package com.bizvane.ishop.service.imp;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.controller.BrandController;
import com.bizvane.ishop.dao.AppIconCfgMapper;
import com.bizvane.ishop.dao.AppIconMapper;
import com.bizvane.ishop.dao.MsgChannelCfgMapper;
import com.bizvane.ishop.dao.MsgChannelsMapper;
import com.bizvane.ishop.entity.AppIconCfg;
import com.bizvane.ishop.entity.MsgChannelCfg;
import com.bizvane.ishop.entity.MsgChannels;
import com.bizvane.ishop.service.AppIconCfgService;
import com.bizvane.ishop.service.MsgChannelCfgService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/11/15.
 */

@Service
public class MsgChannelCfgServiceImpl implements MsgChannelCfgService {
    private static final Logger logger = Logger.getLogger(MsgChannelCfgServiceImpl.class);

    @Autowired
    private MsgChannelCfgMapper msgChannelCfgMapper;
    @Autowired
    private MsgChannelsMapper msgChannelsMapper;

    @Override
    public PageInfo<MsgChannelCfg> selectAllMsgChannelCfg(int page_num, int page_size, String corp_code, String search_value) throws Exception {
        List<MsgChannelCfg> MsgChannelCfgs;
        PageHelper.startPage(page_num, page_size);
        MsgChannelCfgs = msgChannelCfgMapper.selectAllMsgChannelCfg(corp_code, search_value, null);

        for (MsgChannelCfg MsgChannelCfg : MsgChannelCfgs) {
            MsgChannelCfg.setIsactive(CheckUtils.CheckIsactive(MsgChannelCfg.getIsactive()));
        }
        PageInfo<MsgChannelCfg> page = new PageInfo<MsgChannelCfg>(MsgChannelCfgs);

        return page;
    }

    @Override
    public PageInfo<MsgChannelCfg> selectAllMsgChannelCfg(int page_num, int page_size, String corp_code, String search_value, String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        List<MsgChannelCfg> MsgChannelCfgs;
        PageHelper.startPage(page_num, page_size);
        MsgChannelCfgs = msgChannelCfgMapper.selectAllMsgChannelCfg(corp_code, search_value, manager_corp_arr);

        for (MsgChannelCfg MsgChannelCfg : MsgChannelCfgs) {
            MsgChannelCfg.setIsactive(CheckUtils.CheckIsactive(MsgChannelCfg.getIsactive()));
        }
        PageInfo<MsgChannelCfg> page = new PageInfo<MsgChannelCfg>(MsgChannelCfgs);

        return page;
    }

    @Override
    public int delete(int id) throws Exception {

        return msgChannelCfgMapper.delMsgChannelCfgById(id);
    }

    @Override
    public String insert(MsgChannelCfg msgChannelCfg) throws Exception {
        String result = "";
        int m = msgChannelCfgMapper.insertMsgChannelCfg(msgChannelCfg);
        if (m > 0) {
            result = Common.DATABEAN_CODE_SUCCESS;
        } else {
            result = Common.DATABEAN_CODE_ERROR;
        }
        return result;
    }

    @Override
    public String update(MsgChannelCfg msgChannelCfg) throws Exception {
        String status = "";
        String corp_code = msgChannelCfg.getCorp_code();
        String type = msgChannelCfg.getType();
        String name = msgChannelCfg.getChannel_name();
        String account = msgChannelCfg.getChannel_account();
        String child=msgChannelCfg.getChannel_child();
        String sign=msgChannelCfg.getChannel_sign();
        msgChannelCfg.getRandom_code();
        MsgChannelCfg msgChannelCfg1 = checkAccount(corp_code, type, name, account,child, sign,Common.IS_ACTIVE_Y);

        if (msgChannelCfg1 == null || msgChannelCfg.getId().equals(msgChannelCfg1.getId())) {
           // logger.info("==================================");
            int num = msgChannelCfgMapper.updateMsgChannelCfg(msgChannelCfg);
            if (num > 0) {
                status = Common.DATABEAN_CODE_SUCCESS;
            } else {
                status = Common.DATABEAN_CODE_ERROR;
            }
        } else {
            status = Common.DATABEAN_CODE_ERROR;
        }
        return status;
    }

    @Override
    public MsgChannelCfg getMsgChannelCfgById(int id) throws Exception {
        return msgChannelCfgMapper.selMsgChannelCfgById(id);
    }

    @Override
    public List<MsgChannelCfg> getMsgChannelCfgByCorp(String corp_code, String isactive) throws Exception {

        List<MsgChannelCfg> MsgChannelCfgs;
        List<MsgChannels> MsgChannels;
        MsgChannelCfgs = msgChannelCfgMapper.selMsgChannelCfgByCorp(corp_code, isactive);
        MsgChannels = msgChannelsMapper.selectAllChannels();

        for (MsgChannelCfg MsgChannelCfg : MsgChannelCfgs) {
            String en_name = MsgChannelCfg.getChannel_name();
            for (MsgChannels msgChannel : MsgChannels) {
                String name = msgChannel.getChannel_name();
                String channel = msgChannel.getChannel();
                if (en_name.equals(channel)) {
                    MsgChannelCfg.setCh_name(name);
                }
            }
            MsgChannelCfg.setType(CheckUtils.ChannelType(MsgChannelCfg.getType()));
            MsgChannelCfg.setIsactive(CheckUtils.CheckIsactive(MsgChannelCfg.getIsactive()));
        }
        return MsgChannelCfgs;
    }

    @Override
    public List<MsgChannelCfg> getMsgChannelCfgByType(String corp_code, String type, String isactive) throws Exception {
        List<MsgChannelCfg> MsgChannelCfgs;
        List<MsgChannels> MsgChannels;
        MsgChannelCfgs = msgChannelCfgMapper.selMsgChannelCfgByType(corp_code, type, isactive);
        MsgChannels = msgChannelsMapper.selectAllChannels();

        for (MsgChannelCfg MsgChannelCfg : MsgChannelCfgs) {
            String en_name = MsgChannelCfg.getChannel_name();
            for (MsgChannels msgChannel : MsgChannels) {
                String name = msgChannel.getChannel_name();
                String channel = msgChannel.getChannel();
                if (en_name.equals(channel)) {
                    MsgChannelCfg.setCh_name(name);
                }
            }
            MsgChannelCfg.setType(CheckUtils.ChannelType(MsgChannelCfg.getType()));
            MsgChannelCfg.setIsactive(CheckUtils.CheckIsactive(MsgChannelCfg.getIsactive()));
        }
        return MsgChannelCfgs;
    }

    @Override
    public MsgChannelCfg checkAccount(String corp_code, String type, String channel_name, String channel_account, String child,String sign,String isactive) throws Exception {
        return msgChannelCfgMapper.selMsgChannelCfgByAccount(corp_code, type, channel_name, channel_account,child, sign,isactive);

    }


    public List<MsgChannelCfg> selectByCorpBrand(String corp_code, String brand_codes) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        String[] brand_code = brand_codes.split(",");
        for (int i = 0; i < brand_code.length; i++) {
            brand_code[i] = brand_code[i] + ",";
        }
        params.put("corp_code", corp_code);
        params.put("brand_code", brand_code);
        List<MsgChannelCfg> MsgChannelCfgs = msgChannelCfgMapper.selectByBrand(params);
        List<MsgChannels> MsgChannels = msgChannelsMapper.selectAllChannels();

        for (MsgChannelCfg MsgChannelCfg : MsgChannelCfgs) {
            String en_name = MsgChannelCfg.getChannel_name();
            for (MsgChannels msgChannel : MsgChannels) {
                String name = msgChannel.getChannel_name();
                String channel = msgChannel.getChannel();
                if (en_name.equals(channel)) {
                    MsgChannelCfg.setCh_name(name);
                }
            }

        }
        return MsgChannelCfgs;
    }

    @Override
    public List<MsgChannelCfg> selectByCorpBrandForProduction(String corp_code, String brand_codes) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        String[] brand_code = brand_codes.split(",");
        for (int i = 0; i < brand_code.length; i++) {
            brand_code[i] = brand_code[i] + ",";
        }
        params.put("corp_code", corp_code);
        params.put("brand_code", brand_code);
        List<MsgChannelCfg> MsgChannelCfgs = msgChannelCfgMapper.selectByBrandForProduction(params);
        List<MsgChannels> MsgChannels = msgChannelsMapper.selectAllChannels();

        for (MsgChannelCfg MsgChannelCfg : MsgChannelCfgs) {
            String en_name = MsgChannelCfg.getChannel_name();
            for (MsgChannels msgChannel : MsgChannels) {
                String name = msgChannel.getChannel_name();
                String channel = msgChannel.getChannel();
                if (en_name.equals(channel)) {
                    MsgChannelCfg.setCh_name(name);
                }
            }

        }
        return MsgChannelCfgs;
    }

    @Override
    public MsgChannelCfg checkSign(String corp_code, String sign, String isactive) throws Exception {
        return msgChannelCfgMapper.selMsgChannelCfgBySign(corp_code,sign,isactive);
    }
}

