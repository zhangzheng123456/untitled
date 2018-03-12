package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipLabelsRel;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/2/16.
 */
public interface VipLabelsRelMapper {

   //搜索全部
    List<VipLabelsRel> selectAllLabel(@Param("corp_code")String corp_code, @Param("search_value")String search_value) throws SQLException;


    //筛选
    List<VipLabelsRel> selectAllScreen(Map<String,Object> params)throws Exception;

    //删除
    int delActivityVipLabelById(int id)throws Exception;

   //查询
    VipLabelsRel selectVipLabelsRelById(@Param("id") int id)throws  Exception;

    //分权限筛选
    List<VipLabelsRel> selectRoleAllScreen(Map<String,Object> params)throws Exception;

    //分权限查询
    List<VipLabelsRel> selectRoleAllLabel(Map<String,Object> params) throws SQLException;


}
