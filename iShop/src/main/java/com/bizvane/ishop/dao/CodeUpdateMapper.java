package com.bizvane.ishop.dao;

import org.apache.ibatis.annotations.Param;


public interface CodeUpdateMapper {

    //员工
    int updateUser(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                   @Param("new_group_code") String new_group_code, @Param("old_group_code") String old_group_code,
                   @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code,
                   @Param("new_area_code") String new_area_code, @Param("old_area_code") String old_area_code);

    //店铺
    int updateStore(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                    @Param("new_brand_code") String new_brand_code, @Param("old_brand_code") String old_brand_code,
                    @Param("new_area_code") String new_area_code, @Param("old_area_code") String old_area_code);

    //员工业绩目标
    int updateUserAchvGoal(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                           @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code,
                           @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code);

    //店铺业绩目标
    int updateStoreAchvGoal(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                            @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code);

    //签到记录
    int updateSign(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                   @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code,
                   @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code);

    //fab
    int updateGoods(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                    @Param("new_brand_code") String new_brand_code, @Param("old_brand_code") String old_brand_code);

    //回访记录
    int updateCalllback(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                        @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code);

    //权限
    int updatePrivilege(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                        @Param("new_master_code") String new_master_code, @Param("old_master_code") String old_master_code);

    //app版本
    int updateAppVersion(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code);

    //缓存
    int updateCache(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code);

    //群组
    int updateGroup(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code, @Param("new_role_code") String new_role_code, @Param("old_role_code") String old_role_code);

    //接口
    int updateInterface(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code);

    //消息
    int updateMessage(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code);

    //
    int updatePraise(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                     @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code);

    //消息模板
    int updateSms_template(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code);

    //
    int updateStaff_detailInfo(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                               @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code,
                               @Param("new_store_code") String new_store_code, @Param("old_store_code") String old_store_code);

    //
    int updateStaff_move_log(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                             @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code);

    //用户消息
    int updateUser_message(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code);

    //用户照片
    int updateVipAlbum(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                       @Param("new_vip_code") String new_vip_code, @Param("old_vip_code") String old_vip_code);

    //用户消息
    int updateVipMessage(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                         @Param("new_vip_code") String new_vip_code, @Param("old_vip_code") String old_vip_code,
                         @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code);

    //y用户的记录
    int updateVipRecord(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code,
                        @Param("new_vip_code") String new_vip_code, @Param("old_vip_code") String old_vip_code,
                        @Param("new_user_code") String new_user_code, @Param("old_user_code") String old_user_code);

    //用户的记录类型
    int updateVipRecordType(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code);

    //用户标签
    int updateVipLabel(@Param("new_corp_code") String new_corp_code, @Param("old_corp_code") String old_corp_code);
}
