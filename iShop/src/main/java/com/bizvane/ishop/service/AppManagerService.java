package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.AppManager;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by PC on 2017/2/9.
 */
public interface AppManagerService {
    List<AppManager> getFunctionList(String corp_az) throws Exception;

    List<AppManager> getActionList(String app_function,String corp_az, String corp_jnby) throws Exception;

    String getObtainEvents(JSONObject object)throws Exception;

    String getObtainEventTable(JSONObject object)throws Exception;

    String getCommodityAttention(JSONObject object)throws Exception;

    String getCommodityAttentionTable(JSONObject object)throws Exception;

    String getCommodityAttentionByXiuDa(JSONObject jsonObject,HttpServletRequest request)throws Exception;

    String getCommodityAttentionTableByXiuDa(JSONObject jsonObject,HttpServletRequest request)throws Exception;
}
