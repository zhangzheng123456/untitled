package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VIPStoreRelation;
import org.apache.ibatis.annotations.Param;

import com.bizvane.ishop.entity.VIPEmpRelation;;import java.sql.SQLException;
import java.util.List;

public interface VIPRelationMapper {

	List<VIPEmpRelation> selectEmpVip(@Param("open_id") String open_id, @Param("app_user_name") String app_user_name) throws SQLException;

	List<VIPStoreRelation> selectStoreVip(@Param("open_id") String open_id, @Param("app_user_name") String app_user_name) throws SQLException;

}
