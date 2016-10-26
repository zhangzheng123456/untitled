package com.bizvane.ishop.service;


import com.alibaba.fastjson.JSONObject;
import com.bizvane.sun.v1.common.DataBox;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by zhou on 2016/7/6.
 *
 * @@version
 */
public interface IceInterfaceService {
    DataBox iceInterface(String method , Map datalist) throws Exception;

    DataBox iceInterfaceV2(String method ,Map datalist) throws Exception;

    DataBox iceInterfaceV3(String method ,Map datalist) throws Exception;


    Map vipBasicMethod(JSONObject jsonObject, HttpServletRequest request) throws Exception;

    Map vipAnalysisBasicMethod(JSONObject jsonObject, HttpServletRequest request) throws Exception;
}
