package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.VipParamMapper;
import com.bizvane.ishop.entity.VipParam;
import com.bizvane.ishop.service.VipParamService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/9/7.
 */
@Service
public class VipParamServiceImpl implements VipParamService {
    @Autowired
    VipParamMapper vipParamMapper;
    @Autowired
    MongoDBClient mongodbClient;

    @Override
    public VipParam selectById(int id) throws Exception {
        return vipParamMapper.selectById(id);
    }

    @Override
    public List<VipParam> checkParamName(String corp_code, String param_name) throws Exception {
        return vipParamMapper.checkParamName(corp_code,param_name,Common.IS_ACTIVE_Y);
    }
    @Override
    public List<VipParam> selectByParamName(String corp_code, String param_name,String isactive) throws Exception {
        return vipParamMapper.checkParamName(corp_code,param_name,isactive);
    }

    @Override
    public PageInfo<VipParam> selectAllParam(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<VipParam> list = vipParamMapper.selectAllParam(corp_code,search_value,"");
        for (VipParam vipParam:list) {
            vipParam.setIsactive(CheckUtils.CheckIsactive(vipParam.getIsactive()));
        }
        PageInfo<VipParam> page = new PageInfo<VipParam>(list);
        return page;
    }

    @Override
    @Transactional
    public String insert(VipParam vipParam) throws Exception {
        List<VipParam> vipParams= checkParamName(vipParam.getCorp_code().trim(), vipParam.getParam_name().trim());
        String result=Common.DATABEAN_CODE_ERROR;
        if(vipParams.size()==0){
            String order = vipParamMapper.selectMaxOrderByCorp(vipParam.getCorp_code().trim());
            vipParam.setShow_order(order);
            vipParamMapper.insert(vipParam);
            result=Common.DATABEAN_CODE_SUCCESS;
        }else if(vipParams.size()>0){
            result="会员参数名称已存在";
        }
        return result;

    }

    @Override
    @Transactional
    public String update(VipParam vipParam) throws Exception {
        String corp_code = vipParam.getCorp_code().trim();
        String param_name_new = vipParam.getParam_name().trim();

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);

        List<VipParam> vipParams= checkParamName(corp_code, param_name_new);
        String result=Common.DATABEAN_CODE_ERROR;
        VipParam vipParam1 = selectById(vipParam.getId());
        String param_name_old = vipParam1.getParam_name();

        if(vipParams.size()==0||vipParam1.getParam_name().equals(param_name_new)){
            if (!vipParam1.getParam_name().equals(param_name_new)){

                Map keyMap = new HashMap();
                keyMap.put("corp_code", corp_code);
                BasicDBObject queryCondition = new BasicDBObject();
                queryCondition.putAll(keyMap);
                DBCursor dbCursor1 = cursor.find(queryCondition);
                while (dbCursor1.hasNext()){
                    DBObject obj = dbCursor1.next();

                    String extend = "";
                    if (obj.containsField("extend"))
                        extend = obj.get("extend").toString();
                    if (!extend.equals("")){
                        JSONObject obj_extend = JSONObject.parseObject(extend);
                        if (obj_extend.containsKey(param_name_old)){
                            String _id = obj.get("_id").toString();

                            DBObject updateCondition=new BasicDBObject();
                            DBObject updatedValue=new BasicDBObject();
                            updateCondition.put("_id", _id);

                            String value = obj_extend.get(param_name_old).toString();
                            obj_extend.remove(param_name_old);
                            obj_extend.put(param_name_new,value);
                            extend = obj_extend.toString();
                            updatedValue.put("extend", extend);
                            DBObject updateSetValue=new BasicDBObject("$set",updatedValue);
                            cursor.update(updateCondition, updateSetValue);
                        }
                    }
                }
            }
            vipParamMapper.update(vipParam);
            result=Common.DATABEAN_CODE_SUCCESS;
        }else if(vipParams.size()>0){
            result="会员参数名称已存在";
        }
        return result;
    }

    @Override
    @Transactional
    public void updateShowOrder(int id,String show_order) throws Exception {
        VipParam vipParam = selectById(id);
        if (vipParam != null) {
            vipParam.setShow_order(show_order);
            vipParamMapper.update(vipParam);
        }
    }

    @Override
    @Transactional
    public int delete(int id) throws Exception {
        return vipParamMapper.deleteById(id);
    }

    @Override
    public PageInfo<VipParam> selectAllParamScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        List<VipParam> vipParams;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        vipParams = vipParamMapper.selectAllParamScreen(params);
        for (VipParam vipParam:vipParams) {
            vipParam.setIsactive(CheckUtils.CheckIsactive(vipParam.getIsactive()));
        }
        PageInfo<VipParam> page = new PageInfo<VipParam>(vipParams);
        return page;
    }

    @Override
    public List<VipParam> selectParamByCorp(String corp_code) throws Exception {
        List<VipParam> list = vipParamMapper.selectParamByCorp(corp_code);
        return list;
    }

}
