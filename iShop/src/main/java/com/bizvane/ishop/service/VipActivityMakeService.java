package com.bizvane.ishop.service;

/**
 * Created by PC on 2017/1/12.
 */
public interface VipActivityMakeService {
    int addOrUpdateTask(String message,String user_code)throws Exception;

    int addOrUpdateSend(String message,String user_code)throws Exception;

}
