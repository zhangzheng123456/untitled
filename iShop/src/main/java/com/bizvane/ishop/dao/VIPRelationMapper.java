package com.bizvane.ishop.dao;

import org.apache.ibatis.annotations.Param;

import com.bizvane.ishop.entity.VIPRelation;;

public interface VIPRelationMapper {


	VIPRelation selectVip(@Param("open_id") String open_id, @Param("app_user_name") String app_user_name);
	
}
