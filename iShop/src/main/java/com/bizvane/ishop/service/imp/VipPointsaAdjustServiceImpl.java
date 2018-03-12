package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipPointsAdjustMapper;
import com.bizvane.ishop.entity.VipPointsAdjust;
import com.bizvane.ishop.service.VipPointsAdjustService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by gyy on 2017/11/12.
 */
@Service
public class VipPointsaAdjustServiceImpl implements VipPointsAdjustService {
    @Autowired
    private VipPointsAdjustMapper vipPointsAdjustMapper;

    @Override
    public List<VipPointsAdjust> selectPointsAdjustByNameAndId(String bill_name,String corp_code,String isactive) throws Exception {
        List<VipPointsAdjust> vipPointsAdjust = this.vipPointsAdjustMapper.selectPointsAdjustByNameAndId(bill_name,corp_code, isactive);
        return  vipPointsAdjust;
    }

    @Override
    public VipPointsAdjust selectPointsAdjustById(int id) throws Exception {
        return vipPointsAdjustMapper.selectPointsAdjustById(id);
    }

    @Override
    public VipPointsAdjust selectPointsAdjustByBillCode(String bill_code) throws Exception {
        return vipPointsAdjustMapper.selectPointsAdjustByBillCode(bill_code);
    }

    @Override
    public PageInfo<VipPointsAdjust> selectPointsAdjustAll(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_number,page_size);
        List<VipPointsAdjust> list=vipPointsAdjustMapper.selectPointsAdjustAll(corp_code,search_value);
        PageInfo<VipPointsAdjust> pageInfo=new PageInfo<VipPointsAdjust>(list);
        String  result="";
        String  isactives="";
        String  bill_types="";
        for(VipPointsAdjust vipPointsAdjust:list){
            String bill_state = vipPointsAdjust.getBill_state();
            String bill_type = vipPointsAdjust.getBill_type();
            String isactive = vipPointsAdjust.getIsactive();
            if(isactive==null){
                isactives = "";
            }else if(isactive.equals("Y")){
                isactives="可用";
            }else if(isactive.equals("N")){
                isactives="不可用";
            }else {
                isactives="";
            }
            vipPointsAdjust.setIsactive(isactives);
            if (vipPointsAdjust.getBill_type().equals("0")){
                bill_types="手动调整";
            }else if (vipPointsAdjust.getBill_type().equals("1")){
                bill_types="活动赠送";
            }else if (vipPointsAdjust.getBill_type().equals("2")){
                bill_types="任务赠送";
            }else {
                bill_types="";
            }
              vipPointsAdjust.setBill_type(bill_types);

            if(bill_state==null){
                result = "";
            }else if(bill_state.equals("0")){
                result="未执行";
            }else if(bill_state.equals("1")){
                result="已执行";
            }else {
                result="";
            }
            vipPointsAdjust.setBill_state(result);
        }

        return pageInfo;
    }

    @Override
    public int deletePointsAdjustById(int id) throws Exception {
        return vipPointsAdjustMapper.deletePointsAdjustById(id);
    }

    @Override
    public String insertPointsAdjust(String message, String user_id) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = JSONObject.parseObject(message);
        String bill_name = jsonObject.get("bill_name").toString();
        String bill_code = jsonObject.get("bill_code").toString();
        String corp_code =jsonObject.get("corp_code").toString();
        String remarks = jsonObject.get("remarks").toString();
        String bill_voucher=jsonObject.getString("bill_voucher");
        List<VipPointsAdjust> vipPointsAdjustList = selectPointsAdjustByNameAndId(bill_name,corp_code,"");
        if (vipPointsAdjustList.size()==0) {
            VipPointsAdjust vipPointsAdjust = new VipPointsAdjust();
            Date now = new Date();
            vipPointsAdjust.setBill_name(bill_name);  //每个企业下的名字不要有重复  不然列表有相同的名字 不好区分
            vipPointsAdjust.setBill_code(bill_code);
            vipPointsAdjust.setBill_state("0");
            vipPointsAdjust.setBill_type("0");   //状态是由前端传过来的   你可以告诉前端你想让他传的格式
            vipPointsAdjust.setCorp_code(corp_code);
            vipPointsAdjust.setRemarks(remarks);
            vipPointsAdjust.setIsactive(jsonObject.get("isactive").toString());
            vipPointsAdjust.setBill_voucher(bill_voucher);
            vipPointsAdjust.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipPointsAdjust.setCreater(user_id);
            vipPointsAdjust.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipPointsAdjust.setModifier(user_id);
            vipPointsAdjustMapper.insertPointsAdjust(vipPointsAdjust);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return  result;
    }



    //同 新增  （先判断改的名字该企业中是否有 再做修改）


    @Override
    public String updatePointsAdjust(String message, String user_id) throws Exception {
        String result = "";
        JSONObject jsonObject = JSONObject.parseObject(message);
        int id = Integer.parseInt(jsonObject.get("id").toString());
        String  corp_code =jsonObject.get("corp_code").toString();
        String remarks = jsonObject.get("remarks").toString();
        String bill_name = jsonObject.get("bill_name").toString();
        String bill_voucher=jsonObject.getString("bill_voucher");

        VipPointsAdjust vipPointsAdjust=selectPointsAdjustById(id);
        if (!(vipPointsAdjust.getBill_name().equals(jsonObject.get("bill_name").toString())&&vipPointsAdjust.getCorp_code().equals(jsonObject.getString("corp_code")))){
            List<VipPointsAdjust> vipPointsAdjustList=selectPointsAdjustByNameAndId(bill_name,corp_code,"");
            if (vipPointsAdjustList.size()>0){
                return Common.DATABEAN_CODE_ERROR;
            }
        }
        VipPointsAdjust old_points = new VipPointsAdjust();
        Date now = new Date();
        old_points.setId(id);
        old_points.setCorp_code(corp_code);
        old_points.setRemarks(remarks);
        old_points.setBill_name(bill_name);
        old_points.setIsactive(jsonObject.get("isactive").toString());
        old_points.setBill_voucher(bill_voucher);
        old_points.setModified_date(Common.DATETIME_FORMAT.format(now));
        old_points.setModifier(user_id);
        vipPointsAdjustMapper.updatePointsAdjust(old_points);
        result = Common.DATABEAN_CODE_SUCCESS;

        return result;
    }


    @Override
    public int updatePointsAdjust(VipPointsAdjust vipPointsAdjust) throws Exception {
       int state=vipPointsAdjustMapper.updatePointsAdjust(vipPointsAdjust);
       return state;
    }

    @Override
    public int insertPointsAdjust(VipPointsAdjust vipPointsAdjust) throws Exception {
        int state=vipPointsAdjustMapper.insertPointsAdjust(vipPointsAdjust);
        return state;
    }

    @Override
    public PageInfo<VipPointsAdjust> selectVipPointsAdjustAllScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        List<VipPointsAdjust> pointsAdjusts;
        PageHelper.startPage(page_number,page_size);
        HashMap<String,Object> param=new HashMap<String, Object>();
        Set<String> sets=map.keySet();
        if(sets.contains("adjust_time")) {
            JSONObject date = JSONObject.parseObject(map.get("adjust_time"));
            param.put("adjust_time_start", date.get("start").toString());
            String end = date.get("end").toString();
            if (!end.equals(""))
                end = end + " 23:59:59";
            param.put("adjust_time_end", end);
            map.remove("adjust_time");
        }
        param.put("corp_code",corp_code);
        param.put("map",map);
        pointsAdjusts=vipPointsAdjustMapper.selectVipPointsAdjustAllScreen(param);
        PageInfo<VipPointsAdjust> pageInfo=new PageInfo<VipPointsAdjust>(pointsAdjusts);
        String  result="";
        String  isactives="";
        String  bill_types="";
        for (VipPointsAdjust vipPointsAdjust:pageInfo.getList()){
            if (vipPointsAdjust.getIsactive().equals("Y")){
                isactives="可用";
            }else if (vipPointsAdjust.getIsactive().equals("N")){
                isactives="不可用";
            }
            vipPointsAdjust.setIsactive(isactives);

            if (vipPointsAdjust.getBill_type().equals("0")){
                bill_types="手动调整";
            }else if (vipPointsAdjust.getBill_type().equals("1")){
                bill_types="活动赠送";
            }else if (vipPointsAdjust.getBill_type().equals("2")){
                bill_types="任务赠送";
            }

            vipPointsAdjust.setBill_type(bill_types);

            if (vipPointsAdjust.getBill_state().equals("0")){
                result="未执行";
            }else if (vipPointsAdjust.getBill_state().equals("1")){
                result="已执行";
            }
            vipPointsAdjust.setBill_state(result);
        }
        return pageInfo;
    }

    public  int updateBillState(int id,String bill_voucher,String bill_state,String adjust_time) throws Exception{

        int state=vipPointsAdjustMapper.updateBillState(id,bill_voucher,bill_state,adjust_time);
        return  state;

    }



}
