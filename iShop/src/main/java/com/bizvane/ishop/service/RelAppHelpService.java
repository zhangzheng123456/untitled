package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.RelAppHelp;
import com.bizvane.ishop.entity.RelAppHelp;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/2/23.
 */
public interface RelAppHelpService {

    RelAppHelp getRelAppHelpById(int id) throws Exception;

    PageInfo<RelAppHelp> getAllRelAppHelpByPage(int page_number, int page_size, String search_value) throws Exception;

    List<RelAppHelp> getRelAppHelpList(String isactive)throws Exception;

    String insert(String message, String user_id,HttpServletRequest request) throws Exception;

    String update(String message, String user_id,HttpServletRequest request) throws Exception;

    int delete(int id) throws Exception;

    PageInfo<RelAppHelp> getAllRelAppHelpsScreen(int page_number, int page_size, Map<String, String> map) throws Exception;


    RelAppHelp getByHelpTitle(String app_help_code ,String app_help_title,String isactive)throws Exception;

    List<RelAppHelp> getByAppHelpCode(String app_help_code)throws Exception;


}
