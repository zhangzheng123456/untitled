package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.VipActivityDetail;
import com.bizvane.ishop.entity.VipActivityDetailAnniversary;
import com.bizvane.ishop.entity.VipActivityDetailApply;
import com.bizvane.ishop.entity.VipActivityDetailConsume;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by nanji on 2017/1/5.
 */
public interface VipActivityDetailMapper {
    List<VipActivityDetail> selectAllActivityDetail(@Param("corp_code") String corp_code, @Param("user_code") String user_code, @Param("search_value") String search_value) throws SQLException;

    int insertActivityDetail(VipActivityDetail activityDetail) throws SQLException;

    int updateActivityDetail(VipActivityDetail activityDetail) throws SQLException;

    int delActivityDetailById(@Param("activity_code")String  activity_code) throws SQLException;

    VipActivityDetail selActivityDetailByCode(@Param("activity_code") String activity_code)throws SQLException;

    VipActivityDetail selActivityByCodeAndName(@Param("activity_code") String activity_code)throws SQLException;

    //======consume====
    int insertDetailConsume(VipActivityDetailConsume detailConsume) throws SQLException;

    int updateDetailConsume(VipActivityDetailConsume detailConsume) throws SQLException;

    int delDetailConsume(@Param("activity_code")String  activity_code) throws SQLException;

    List<VipActivityDetailConsume> selActivityDetailConsume(@Param("activity_code") String activity_code)throws SQLException;

    //======anniversary====
    int insertDetailAnniversary(VipActivityDetailAnniversary detailAnniversary) throws SQLException;

    int updateDetailAnniversary(VipActivityDetailAnniversary detailAnniversary) throws SQLException;

    int delDetailAnniversary(@Param("activity_code")String  activity_code) throws SQLException;

    List<VipActivityDetailAnniversary> selActivityDetailAnniversary(@Param("activity_code") String activity_code)throws SQLException;

    List<VipActivityDetailAnniversary> selCorpAnniversary(@Param("corp_code") String corp_code)throws SQLException;

    /**线上报名活动**/
    VipActivityDetailApply selectActivityApplyById(int id) throws SQLException;

    List<VipActivityDetailApply> selectActivityApplyByCode(@Param("activity_code") String activity_code) throws  SQLException;

    int insertActivityApply(VipActivityDetailApply vipActivityDetailApply) throws SQLException;

    int updateActivityApply(VipActivityDetailApply vipActivityDetailApply) throws  SQLException;

    int deleteActivityApplyById(int id)throws SQLException;

    int deleteActivityApplyByCode(@Param("activity_code") String activity_code) throws  SQLException;

    int updateDetailApply(VipActivityDetailApply vipActivityDetailApply) throws  SQLException;

}


