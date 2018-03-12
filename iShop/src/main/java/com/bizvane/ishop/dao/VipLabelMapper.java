package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.RelViplabel;
import com.bizvane.ishop.entity.VIPInfo;
import com.bizvane.ishop.entity.VipLabel;
import com.bizvane.ishop.entity.VipLabel;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by lixiang on 2016/6/12.
 *
 * @@version
 */
public interface VipLabelMapper {

    int deleteByPrimaryKey(Integer id) throws SQLException;

    int insert(VipLabel record) throws SQLException;

    VipLabel selectByPrimaryKey(Integer id) throws SQLException;

    int updateByPrimaryKey(VipLabel record) throws SQLException;

    List<VipLabel> selectAllVipLabel(@Param("corp_code") String corp_code, @Param("search_value") String search_value) throws SQLException;

    List<VipLabel> selectAllViplabelScreen(Map<String, Object> params) throws SQLException;

    List<VipLabel> selectVipLabelName(Map<String, Object> params) throws SQLException;

    // VipLabel selectTypeCodeByName(@Param("corp_code") String corp_code, @Param("type_name") String type_name);

    VipLabel countLable(@Param("corp_code")String corp_code,@Param("label_id")String label_id)throws Exception;

    List<VipLabel> lableList(@Param("corp_code")String corp_code,@Param("label_group_code")String label_group_code);

    int updViplableBycode(@Param("label_group_code_old")String label_group_code_old,@Param("corp_code")String corp_code,@Param("label_group_code_new")String label_group_code_new);

    List<VipLabel> selectLabelByVip(@Param("corp_code")String corp_code,@Param("vip_code")String vip_code);

    List<VipLabel> findHotViplabel(@Param("corp_code")String corp_code,@Param("brandList")String[] brandList);

    List<VipLabel> findViplabelByType(@Param("corp_code")String corp_code,@Param("label_type")String label_type,@Param("search_value")String search_value,@Param("brandList")String[] brandList);

    List<RelViplabel>  checkRelViplablel(@Param("corp_code")String corp_code,@Param("vip_code")String vip_code,@Param("label_id")String label_id);

    int addRelViplabel(RelViplabel relViplabel);

    int delRelViplabel(@Param("rid")String rid);

    int delAllRelViplabel(@Param("label_id")String label_id);

    List<VipLabel> findViplabelID(@Param("corp_code")String corp_code,@Param("label_name")String label_name,@Param("isactive")String isactive);

    List<VipLabel> selectVipsLabel(Map<String, Object> params);

}