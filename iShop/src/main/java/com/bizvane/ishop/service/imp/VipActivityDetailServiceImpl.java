package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipActivityDetailMapper;
import com.bizvane.ishop.entity.VipActivityDetail;
import com.bizvane.ishop.service.UserService;
import com.bizvane.ishop.service.VipActivityDetailService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/1/5.
 */
@Service
public class VipActivityDetailServiceImpl implements VipActivityDetailService{
    @Autowired
    VipActivityDetailMapper vipActivityDetailMapper;
    @Autowired
    private UserService userService;

    @Override
    public PageInfo<VipActivityDetail> selectAllActivityDetail(int page_num, int page_size, String corp_code, String user_code, String search_value) throws Exception {
        List<VipActivityDetail> vipActivityDetails;
        PageHelper.startPage(page_num, page_size);
        vipActivityDetails = vipActivityDetailMapper.selectAllActivityDetail(corp_code, user_code, search_value);
        for (VipActivityDetail vipActivityDetail : vipActivityDetails) {
            vipActivityDetail.setIsactive(CheckUtils.CheckIsactive(vipActivityDetail.getIsactive()));
        }
        PageInfo<VipActivityDetail> page = new PageInfo<VipActivityDetail>(vipActivityDetails);

        return page;
    }


    @Override
    public PageInfo<VipActivityDetail> selectllScreen(int page_num, int page_size, String corp_code, String user_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("user_code", user_code);

        params.put("map", map);
        PageHelper.startPage(page_num, page_size);
        List<VipActivityDetail> list1 = vipActivityDetailMapper.selectActivityDetailScreen(params);
        for (VipActivityDetail vipActivityDetail : list1) {
            vipActivityDetail.setIsactive(CheckUtils.CheckIsactive(vipActivityDetail.getIsactive()));
        }
        PageInfo<VipActivityDetail> page = new PageInfo<VipActivityDetail>(list1);
        return page;

    }

    @Override
    public int delete(String activity_code) throws Exception {
        return vipActivityDetailMapper.delActivityDetailById(activity_code);
    }


    @Override
    public String insert(String message, String user_id) throws Exception {
        String result = null;
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_code = jsonObject.get("activity_code").toString().trim();

        VipActivityDetail vipActivityDetail = WebUtils.JSON2Bean(jsonObject, VipActivityDetail.class);
        vipActivityDetail.setCorp_code(corp_code);
        vipActivityDetail.setActivity_code(activity_code);
        vipActivityDetail.setCreater(user_id);
        vipActivityDetail.setCreated_date(Common.DATETIME_FORMAT.format(now));
        vipActivityDetail.setModifier(user_id);
        vipActivityDetail.setModified_date(Common.DATETIME_FORMAT.format(now));
        int info=0;
        info= vipActivityDetailMapper.insertActivityDetail(vipActivityDetail);

        if (info>0) {
            result=Common.DATABEAN_CODE_SUCCESS;
        } else {
            result="新增失败";

        }
        return result;
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String result = null;
        org.json.JSONObject jsonObject = new org.json.JSONObject(message);
        Date now = new Date();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String activity_code = jsonObject.get("activity_code").toString().trim();

        VipActivityDetail vipActivityDetail = WebUtils.JSON2Bean(jsonObject, VipActivityDetail.class);
        vipActivityDetail.setCorp_code(corp_code);
        vipActivityDetail.setActivity_code(activity_code);
        vipActivityDetail.setCreater(user_id);
        vipActivityDetail.setCreated_date(Common.DATETIME_FORMAT.format(now));
        vipActivityDetail.setModifier(user_id);
        vipActivityDetail.setModified_date(Common.DATETIME_FORMAT.format(now));
        int info=0;
        info= vipActivityDetailMapper.updateActivityDetail(vipActivityDetail);

        if (info>0) {
            result=Common.DATABEAN_CODE_SUCCESS;
        } else {
            result="编辑失败";

        }
        return result;
    }


    @Override
    public VipActivityDetail selectActivityById(int id) throws Exception {
        return vipActivityDetailMapper.selActivityDetailById(id);
    }

    @Override
    public VipActivityDetail selActivityDetailByCode(String activity_code) throws Exception {
        return vipActivityDetailMapper.selActivityDetailByCode(activity_code);
    }
}
