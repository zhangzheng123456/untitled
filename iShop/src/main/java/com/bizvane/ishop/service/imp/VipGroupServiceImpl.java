package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.dao.VipGroupMapper;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.entity.VipGroup;
import com.bizvane.ishop.service.VipGroupService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/8/31.
 */
@Service
public class VipGroupServiceImpl implements VipGroupService {
    @Autowired
    VipGroupMapper vipGroupMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    MongoDBClient mongodbClient;
    /**
     * 根据id
     * 获取会员分组信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public VipGroup getVipGroupById(int id) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);
        int vip_count = 0;
        VipGroup vipGroup = vipGroupMapper.selectVipGroupById(id);
        if (vipGroup != null) {
            String corp_code = vipGroup.getCorp_code();
            String user_code = vipGroup.getUser_code();
            String vip_group_code = vipGroup.getVip_group_code();
            //分组所属导购
            if (user_code != null && !user_code.equals("")){
                List<User> users = userMapper.selectUserCode(user_code,corp_code,Common.IS_ACTIVE_Y);
                if (users.size()>0) {
                    vipGroup.setUser_name(users.get(0).getUser_name());
                }
                else {
                    vipGroup.setUser_name("");
                }
            } else {
                vipGroup.setUser_name("");
            }
            //分组所拥有的会员个数
            BasicDBObject dbObject=new BasicDBObject();
            dbObject.put("vip_group_code",vip_group_code);
            dbObject.put("corp_code",corp_code);
            DBCursor dbCursor= cursor.find(dbObject);
            vip_count = dbCursor.size();
            vipGroup.setVip_count(vip_count);
        }

        return vipGroup;
    }


    /**
     * 分页显示会员分组
     *
     * @param page_number
     * @param page_size
     * @param corp_code
     * @param search_value
     * @return
     * @throws Exception
     */
    @Override
    public PageInfo<VipGroup> getAllVipGroupByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<VipGroup> vipGroups;
        PageHelper.startPage(page_number, page_size);
        vipGroups = vipGroupMapper.selectAllVipGroup(corp_code, search_value);
        for (VipGroup vipGroup : vipGroups) {
            String corp_code1 = vipGroup.getCorp_code();
            String user_code = vipGroup.getUser_code();
            //分组所属导购
            if (user_code != null && !user_code.equals("")){
                List<User> users = userMapper.selectUserCode(user_code,corp_code1,Common.IS_ACTIVE_Y);
                if (users.size()>0) {
                    vipGroup.setUser_name(users.get(0).getUser_name());
                }
                else {
                    vipGroup.setUser_name("");
                }
            } else {
                vipGroup.setUser_name("");
            }
            vipGroup.setIsactive(CheckUtils.CheckIsactive(vipGroup.getIsactive()));
        }
        PageInfo<VipGroup> page = new PageInfo<VipGroup>(vipGroups);
        return page;
    }

    @Override
    public List<VipGroup> selectCorpVipGroups(String corp_code,String search_value) throws Exception {
        List<VipGroup> vipGroups;
        vipGroups = vipGroupMapper.selectCorpVipGroups(corp_code,search_value);

        return vipGroups;
    }

    @Override
    public String insert(String message, String user_id) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        String vip_group_code = jsonObject.get("vip_group_code").toString().trim();
        String vip_group_name = jsonObject.get("vip_group_name").toString().trim();
        String user_code = jsonObject.get("user_code").toString().trim();
        String remark = jsonObject.get("remark").toString();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        VipGroup vipGroup1 = getVipGroupByCode(corp_code, vip_group_code, Common.IS_ACTIVE_Y);
        VipGroup vipGroup2 = getVipGroupByName(corp_code, vip_group_name, Common.IS_ACTIVE_Y);

        if (vipGroup1 != null) {
            result = "该会员分组编号已存在";
        } else if (vipGroup2 != null) {
            result = "该会员分组名称已存在";
        } else {
            Date now = new Date();
            VipGroup vipGroup = new VipGroup();
            vipGroup.setRemark(remark);
            vipGroup.setVip_group_code(vip_group_code);
            vipGroup.setVip_group_name(vip_group_name);
            vipGroup.setUser_code(user_code);
            vipGroup.setCorp_code(corp_code);
            vipGroup.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipGroup.setCreater(user_id);
            vipGroup.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipGroup.setModifier(user_id);
            vipGroup.setIsactive(jsonObject.get("isactive").toString().trim());
            vipGroupMapper.insertVipGroup(vipGroup);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;

    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String result = "";
        JSONObject jsonObject = new JSONObject(message);
        String vipGroup_id = jsonObject.get("id").toString().trim();
        int id = Integer.parseInt(vipGroup_id);
        String vip_group_code = jsonObject.get("vip_group_code").toString().trim();
        String vip_group_name = jsonObject.get("vip_group_name").toString().trim();
        String user_code = jsonObject.get("user_code").toString().trim();
        String remark = jsonObject.get("remark").toString();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        VipGroup vipGroup1 = getVipGroupByCode(corp_code, vip_group_code, Common.IS_ACTIVE_Y);
        VipGroup vipGroup2 = getVipGroupByName(corp_code, vip_group_name, Common.IS_ACTIVE_Y);

        if (vipGroup1 != null && vipGroup1.getId() != id) {
            result = "该会员分组编号已存在";
        } else if (vipGroup2 != null && vipGroup2.getId() != id) {
            result = "该会员分组名称已存在";
        } else {
            VipGroup vipGroup = new VipGroup();
            Date now = new Date();
            vipGroup.setId(id);
            vipGroup.setRemark(remark);
            vipGroup.setVip_group_code(vip_group_code);
            vipGroup.setVip_group_name(vip_group_name);
            vipGroup.setUser_code(user_code);
            vipGroup.setCorp_code(corp_code);
            vipGroup.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipGroup.setModifier(user_id);
            vipGroup.setIsactive(jsonObject.get("isactive").toString().trim());
            vipGroupMapper.updateVipGroup(vipGroup);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;

    }

    @Override
    @Transactional
    public int delete(int id) throws Exception {
        return vipGroupMapper.deleteVipGroupById(id);
    }

    @Override
    public VipGroup getVipGroupByCode(String corp_code, String code, String isactive) throws Exception {
        return vipGroupMapper.selectByVipGroupCode(corp_code, code, isactive);
    }

    @Override
    public VipGroup getVipGroupByName(String corp_code, String name, String isactive) throws Exception {
        VipGroup vipGroup = this.vipGroupMapper.selectByVipGroupName(corp_code, name, isactive);
        return vipGroup;
    }

    public PageInfo<VipGroup> getAllVipGrouScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<VipGroup> list1 = vipGroupMapper.selectAllVipGroupScreen(params);
        for (VipGroup vipGroup : list1) {
            String corp_code1 = vipGroup.getCorp_code();
            String user_code = vipGroup.getUser_code();
            //分组所属导购
            if (user_code != null && !user_code.equals("")){
                List<User> users = userMapper.selectUserCode(user_code,corp_code1,Common.IS_ACTIVE_Y);
                if (users.size()>0) {
                    vipGroup.setUser_name(users.get(0).getUser_name());
                }
                else {
                    vipGroup.setUser_name("");
                }
            } else {
                vipGroup.setUser_name("");
            }
            vipGroup.setIsactive(CheckUtils.CheckIsactive(vipGroup.getIsactive()));
        }
        PageInfo<VipGroup> page = new PageInfo<VipGroup>(list1);
        return page;
    }

    public JSONArray findVipsGroup(JSONArray array) throws Exception {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_info);

        JSONArray new_array = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            com.alibaba.fastjson.JSONObject vip = com.alibaba.fastjson.JSONObject.parseObject(array.get(i).toString());
//            String vip_id = vip.get("vip_id").toString();
            String corp_code = vip.get("corp_code").toString();
            String cardno = vip.get("cardno").toString();

            BasicDBObject dbObject=new BasicDBObject();
            dbObject.put("_id",corp_code+cardno);
//                dObject.put("corp_code",corp_code);
            DBCursor dbCursor= cursor.find(dbObject);

            String vip_group_code = "";
            String vip_group_name = "";
            while (dbCursor.hasNext()) {
                DBObject object = dbCursor.next();
                if (object.containsField("vip_group_code"))
                    vip_group_code = object.get("vip_group_code").toString();
            }
            if (!vip_group_code.equals("")){
                VipGroup vipGroup = getVipGroupByCode(corp_code,vip_group_code,Common.IS_ACTIVE_Y);
                if (vipGroup != null){
                    vip_group_name = vipGroup.getVip_group_name();
                }
            }
            vip.put("vip_group_code",vip_group_code);
            vip.put("vip_group_name",vip_group_name);
            new_array.add(vip);
        }
        return new_array;
    }

}
