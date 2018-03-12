package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VipActivityDetail;
import com.bizvane.ishop.entity.VipActivityDetailAnniversary;
import com.bizvane.ishop.entity.VipActivityDetailApply;
import com.bizvane.ishop.entity.VipActivityDetailConsume;
import com.github.pagehelper.PageInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by nanji on 2017/1/5.
 */
public interface VipActivityDetailService {

    PageInfo<VipActivityDetail> selectAllActivityDetail(int page_num, int page_size, String corp_code, String user_code, String search_value) throws Exception;

    int delete(String activity_code) throws Exception;

    String insertOrUpdate(HttpServletRequest request,String message, String user_id, String server_name) throws Exception;

    VipActivityDetail selActivityDetailByCode(String activity_code) throws Exception;

    List<VipActivityDetailConsume> selActivityDetailConsume(String activity_code) throws Exception;

    int updateDetailConsume(VipActivityDetailConsume detailConsume) throws Exception;

    List<VipActivityDetailAnniversary> selActivityDetailAnniversary(String activity_code) throws Exception;

    List<VipActivityDetailAnniversary> selCorpAnniversary(String corp_code) throws Exception;

    int updateDetailAnniversary(VipActivityDetailAnniversary detailAnniversary) throws Exception;

    VipActivityDetail selActivityByCodeAndName(String activity_code) throws Exception;

    /**线上报名活动**/
    VipActivityDetailApply selectActivityApplyById(int id) throws Exception;

    List<VipActivityDetailApply> selectActivityApplyByCode(String activity_code) throws  Exception;

    int insertActivityApply(VipActivityDetailApply vipActivityDetailApply) throws Exception;

    int updateActivityApply(VipActivityDetailApply vipActivityDetailApply) throws  Exception;

    int deleteActivityApplyById(int id)throws Exception;

    int deleteActivityApplyByCode(String activity_code) throws  Exception;

    int updateDetailApply(VipActivityDetailApply vipActivityDetailApply) throws Exception;
}
