package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Approved;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/4/17.
 */
public interface ApprovedMapper {

    Approved selectById(int id) throws Exception;

    Approved selectByApprovedName(@Param("approved_name")String approved_name,@Param("corp_code")String corp_code)throws Exception;

    List<Approved> selectAll(@Param("corp_code")String corp_code, @Param("search_value")String search_value) throws Exception;

    int deleteById(int id) throws Exception;

    int insertApproved(Approved approved) throws Exception;

    int updateApproved(Approved approved)throws Exception;

    List<Approved> selectAllScreen(Map<String, Object> params) throws  Exception;



}
