package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.ExamineConfigureMapper;
import com.bizvane.ishop.entity.ExamineConfigure;
import com.bizvane.ishop.service.ExamineConfigureService;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ExamineConfigureServiceImpl implements ExamineConfigureService {
    @Autowired
    ExamineConfigureMapper examineConfigureMapper;
    @Autowired
    MongoDBClient mongoDBClient;

    @Override
    public ExamineConfigure selectById(int id) throws Exception {
        ExamineConfigure examineConfigure=examineConfigureMapper.selectById(id);
        return examineConfigure;
    }

    @Override
    public PageInfo<ExamineConfigure> selectAll(String corp_code,String search_value, int page_num, int page_size) throws Exception {
        PageHelper.startPage(page_num,page_size);
        List<ExamineConfigure> examineConfigureList=examineConfigureMapper.selectAll(corp_code,search_value);
        PageInfo<ExamineConfigure> pageInfo=new PageInfo<ExamineConfigure>(examineConfigureList);
        return pageInfo;
    }

    @Override
    public PageInfo<ExamineConfigure> selectAllScreen(String corp_code, Map<String, Object> map, int page_num, int page_size) throws Exception {
        PageHelper.startPage(page_num,page_size);
        HashMap<String,Object> param=new HashMap<String, Object>();
        Set<String> sets=map.keySet();
        if(sets.contains("modified_date")) {
            JSONObject date = JSONObject.parseObject(map.get("modified_date").toString());
            param.put("modified_date_start", date.get("start").toString());
            String end = date.get("end").toString();
            if (!end.equals(""))
                end = end + " 23:59:59";
            param.put("modified_date_end", end);
            map.remove("modified_date");
        }
        param.put("corp_code",corp_code);
        param.put("map",map);
        List<ExamineConfigure> examineConfigureList=examineConfigureMapper.selectAllScreen(param);
        PageInfo<ExamineConfigure> pageInfo=new PageInfo<ExamineConfigure>(examineConfigureList);
        return pageInfo;
    }

    @Override
    public String deleteById(int id) throws Exception {
        String statu = Common.DATABEAN_CODE_SUCCESS;
        ExamineConfigure examineConfigure = selectById(id);
        if (examineConfigure != null){
            MongoTemplate mongoTemplate = this.mongoDBClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_function_check_schedule);

            String examine_type = "";
            String name = examineConfigure.getFunction_bill_name();
            if (name.equals("会员任务")){
                examine_type = "vipTask";
            }else if (name.equals("会员活动")){
                examine_type = "vipActivity";
            }else if (name.equals("积分调整")){
                examine_type = "vipPointAdjust";
            }
            BasicDBObject basicDBObject = new BasicDBObject();
            if(examine_type.equals("vipTask")){
                basicDBObject.put("vipTask.corp_code",examineConfigure.getCorp_code());
            }
            if(examine_type.equals("vipActivity")){
                basicDBObject.put("activity.corp_code",examineConfigure.getCorp_code());
            }

            basicDBObject.put("examine_type",examine_type);
            basicDBObject.put("status","2");
            DBCursor dbObjects = cursor.find(basicDBObject);
            if (dbObjects.count() > 0){
                statu = "功能正在使用中，不可以删除";
            }else {
                examineConfigureMapper.deleteById(id);
            }
        }
        return statu;
    }

    @Override
    public ExamineConfigure selectByName(String corp_code, String function_bill_name) throws Exception {
        ExamineConfigure examineConfigure=examineConfigureMapper.selectByName(corp_code,function_bill_name);
        return examineConfigure;
    }

    @Override
    public int insertExamine(ExamineConfigure examineConfigure) throws Exception {
       int  statu=examineConfigureMapper.insertExamine(examineConfigure);
        return statu;
    }

    @Override
    public String updateExamine(ExamineConfigure examineConfigure) throws Exception {
        String statu = Common.DATABEAN_CODE_SUCCESS;
        ExamineConfigure old_examineConfigure1 = selectById(examineConfigure.getId());
        String old_examine_group = old_examineConfigure1.getExamine_group();
        String new_examine_group = examineConfigure.getExamine_group();
        if (!old_examine_group.equals(new_examine_group)){
//            JSONArray old_array = JSONArray.parseArray(old_examine_group);
//            JSONArray new_array = JSONArray.parseArray(new_examine_group);
//            if (old_array.size() != new_array.size()){
//
//            }
//            for (int i = 0; i < old_array.size(); i++) {
//                JSONArray old_array_i = old_array.getJSONArray(i);
//                for (int j = 0; j < new_array.size(); j++) {
//
//                }
//            }
            MongoTemplate mongoTemplate = this.mongoDBClient.getMongoTemplate();
            DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_function_check_schedule);

            String examine_type = "";
            String name = examineConfigure.getFunction_bill_name();
            if (name.equals("会员任务")){
                examine_type = "vipTask";
            }else if (name.equals("会员活动")){
                examine_type = "vipActivity";
            }else if (name.equals("积分调整")){
                examine_type = "vipPointAdjust";
            }
            BasicDBObject basicDBObject = new BasicDBObject();
            if(examine_type.equals("vipTask")){
                basicDBObject.put("vipTask.corp_code",examineConfigure.getCorp_code());
            }
            if(examine_type.equals("vipActivity")){
                basicDBObject.put("activity.corp_code",examineConfigure.getCorp_code());
            }
            basicDBObject.put("examine_type",examine_type);
            basicDBObject.put("status","2");
            DBCursor dbObjects = cursor.find(basicDBObject);
            if (dbObjects.count() > 0){
                statu = "功能正在使用中，不可以修改审核人";
            }else {
                examineConfigureMapper.updateExamine(examineConfigure);
            }
        }else {
            examineConfigureMapper.updateExamine(examineConfigure);
        }
        return statu;
    }

}
