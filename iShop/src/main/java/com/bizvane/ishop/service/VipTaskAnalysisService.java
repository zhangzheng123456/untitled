package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yanyadong on 2017/11/28.
 */
public interface VipTaskAnalysisService {

     JSONObject getRegisterRate(String message,HttpServletRequest request) throws Exception;

     JSONObject getRegisterChart(String message) throws Exception;

     JSONObject getRegisterList(String message) throws Exception;

     JSONObject getRegisterVipInfo(String message) throws Exception;

     JSONObject getCompleteRate(String message,HttpServletRequest request) throws Exception;

     JSONObject getCompleteChart(String message)throws Exception;

     JSONObject getCompleteList(String message) throws Exception;

     JSONObject getSaleInfoRate(String message,HttpServletRequest request) throws Exception;

     JSONObject getSaleInfoChart(String message) throws Exception;

     JSONObject getSaleInfoList(String message) throws Exception;

     JSONObject getQuestionNaireList(String message) throws Exception;

     JSONObject getShareUrlRate(String message,HttpServletRequest request) throws Exception;

     JSONObject getShareUrlChart(String message) throws Exception;

     JSONObject getShareUrlList(String message) throws Exception;
}
