package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VIPStoreRelation;
import org.apache.ibatis.annotations.Param;

import com.bizvane.ishop.entity.VIPEmpRelation;;

public interface VIPRelationMapper {


	VIPEmpRelation selectEmpVip(@Param("open_id") String open_id, @Param("app_user_name") String app_user_name);

	VIPStoreRelation selectStoreVip(@Param("open_id") String open_id, @Param("app_user_name") String app_user_name);

}
