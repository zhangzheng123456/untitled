package com.bizvane.ishop.dao;

import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;


public interface CodeUpdateMapper {

    //员工
    int updateUser(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                   @Param("new_group_code") String new_group_code, @Param("old_group_code") String old_group_code,
                   @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code,
                   @Param("new_area_code") String new_area_code, @Param("old_area_code") String old_area_code,
                   @Param("new_brand_code") String new_brand_code, @Param("old_brand_code") String old_brand_code) throws SQLException;

    //店铺
    int updateStore(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                    @Param("new_brand_code") String new_brand_code, @Param("old_brand_code") String old_brand_code,
                    @Param("new_area_code") String new_area_code, @Param("old_area_code") String old_area_code) throws SQLException;

    //员工业绩目标
    int updateUserAchvGoal(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                           @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code,
                           @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code) throws SQLException;

    //店铺业绩目标
    int updateStoreAchvGoal(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                            @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code) throws SQLException;

    //签到记录
    int updateSign(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                   @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code,
                   @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code) throws SQLException;

    //fab
    int updateGoods(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                    @Param("new_brand_code") String new_brand_code, @Param("old_brand_code") String old_brand_code) throws SQLException;

    //权限
    int updatePrivilege(@Param("new_master_code") String new_master_code, @Param("old_master_code") String old_master_code) throws SQLException;

    //app版本
    int updateAppVersion(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code) throws SQLException;

    //群组
    int updateGroup(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                    @Param("new_role_code") String new_role_code, @Param("old_role_code") String old_role_code) throws SQLException;

    //接口
    int updateInterface(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code) throws SQLException;

    //点赞
    int updatePraise(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                     @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code) throws SQLException;

    //消息模板
    int updateSmsTemplate(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code) throws SQLException;

    //
    int updateStaffDetailInfo(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                               @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code,
                               @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code) throws SQLException;

    //
    int updateStaffMoveLog(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                             @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code) throws SQLException;

    //用户消息
    int updateUserMessage(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code) throws SQLException;

    //会员相册
    int updateVipAlbum(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                       @Param("new_vip_code") String new_vip_code, @Param("old_vip_code") String old_vip_code) throws SQLException;

    //会员消息
    int updateVipMessage(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                         @Param("new_vip_code") String new_vip_code, @Param("old_vip_code") String old_vip_code,
                         @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code) throws SQLException;

    //回访记录
    int updateVipRecord(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                        @Param("new_vip_code") String new_vip_code, @Param("old_vip_code") String old_vip_code,
                        @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code) throws SQLException;

    //回访记录类型
    int updateVipRecordType(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code) throws SQLException;

    //会员标签
    int updateVipLabel(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code) throws SQLException;

    //会员标签关系
    int updateRelVipLabel(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                          @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code) throws SQLException;

    //open_id店铺关系
    int updateRelVipStore(@Param("new_store_id") String new_store_id,@Param("old_store_id") String old_store_id,
                          @Param("app_user_name") String app_user_name) throws SQLException;

    //app_id企业关系
    int updateRelCorpWechat(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code) throws SQLException;

    //消息模板类型
    int updateTemplateType(@Param("new_template_type") String new_template_type, @Param("old_template_type") String old_template_type
                        , @Param("corp_code") String corp_code) throws SQLException;
}
