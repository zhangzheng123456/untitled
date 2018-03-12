package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.dao.VipGroupMapper;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.VipGroup;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.TableManagerService;
import com.bizvane.ishop.service.VipGroupService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.sun.v1.common.DataBox;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    StoreService storeService;
    @Autowired
    IceInterfaceService iceInterfaceService;
    @Autowired
    TableManagerService tableManagerService;

    private static final Logger logger = Logger.getLogger(VipGroupServiceImpl.class);

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
        VipGroup vipGroup = vipGroupMapper.selectVipGroupById(id);
        if (vipGroup != null){
            if (vipGroup.getGroup_type().equals("define")){
                String group_condition = vipGroup.getGroup_condition();
                if (group_condition.startsWith("[")){
                    JSONObject new_condition = new JSONObject();
                    new_condition.put("list",JSONArray.parseArray(group_condition));
                    new_condition.put("operator","AND");
                    vipGroup.setGroup_condition(new_condition.toString());
                    updateVipGroup(vipGroup);
                }
            }
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
            vipGroup.setIsactive(CheckUtils.CheckIsactive(vipGroup.getIsactive()));
            trans(vipGroup);
        }
        PageInfo<VipGroup> page = new PageInfo<VipGroup>(vipGroups);
        return page;
    }

    @Override
    public List<VipGroup> selectCorpVipGroups(String corp_code, String search_value) throws Exception {
        List<VipGroup> vipGroups;
        vipGroups = vipGroupMapper.selectCorpVipGroups(corp_code, search_value);
        return vipGroups;
    }



    @Override
    public String insert(VipGroup vipGroup, String role_code,String user_brand_code,String user_area_code,String user_store_code,String user_code) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        String vip_group_name = vipGroup.getVip_group_name().trim();
        String corp_code = vipGroup.getCorp_code();

        List<VipGroup> vipGroup2 = getVipGroupByName(corp_code, vip_group_name, Common.IS_ACTIVE_Y);
        if (vipGroup2.size() > 0) {
            result = "该会员分组名称已存在";
        } else {
            String group_type = vipGroup.getGroup_type();
//            if (group_type.equals("define")){
//                String group_condition = vipGroup.getGroup_condition();
//                JSONObject condition_obj = JSONObject.parseObject(group_condition);
//                condition_obj = vipGroupCustom1(condition_obj,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
//
//                vipGroup.setGroup_condition(condition_obj.toString());
//            }else if (group_type.equals("define_v2")){
//                String condition = vipGroup.getGroup_condition();
//                JSONArray condition_array = JSONArray.parseArray(condition);
//                condition_array = vipGroupCustomDefineV2(condition_array,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
//                vipGroup.setGroup_condition(condition_array.toJSONString());
//            }
            Date now = new Date();
            vipGroup.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipGroup.setCreater(user_code);
            vipGroup.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipGroup.setModifier(user_code);
            vipGroupMapper.insertVipGroup(vipGroup);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;

    }

    @Override
    public String update(VipGroup vipGroup, String role_code,String user_brand_code,String user_area_code,String user_store_code,String user_code) throws Exception {
        String result = "";
        int id = vipGroup.getId();
        String vip_group_name = vipGroup.getVip_group_name().trim();
        String corp_code = vipGroup.getCorp_code();

        List<VipGroup> vipGroup2 = getVipGroupByName(corp_code, vip_group_name, Common.IS_ACTIVE_Y);

        if (vipGroup2.size() > 0 && vipGroup2.get(0).getId() != id) {
            result = "该会员分组名称已存在";
        } else {
            String group_type = vipGroup.getGroup_type();
//            if (group_type.equals("define")){
//                String group_condition = vipGroup.getGroup_condition();
//                JSONObject condition_obj = JSONObject.parseObject(group_condition);
//                condition_obj = vipGroupCustom1(condition_obj,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
//
//                vipGroup.setGroup_condition(condition_obj.toString());
//            }else if (group_type.equals("define_v2")){
//                String condition = vipGroup.getGroup_condition();
//                JSONArray condition_array = JSONArray.parseArray(condition);
//                condition_array = vipGroupCustomDefineV2(condition_array,corp_code,role_code,user_brand_code,user_area_code,user_store_code,user_code);
//                vipGroup.setGroup_condition(condition_array.toJSONString());
//            }
            Date now = new Date();
            vipGroup.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipGroup.setModifier(user_code);
            vipGroupMapper.updateVipGroup(vipGroup);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;
    }

    public int updateVipGroup(VipGroup vipGroup) throws Exception{
        return vipGroupMapper.updateVipGroup(vipGroup);
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
    public List<VipGroup> getVipGroupByName(String corp_code, String name, String isactive) throws Exception {
        List<VipGroup> vipGroup = this.vipGroupMapper.selectByVipGroupName(corp_code, name, isactive);
        return vipGroup;
    }

    public PageInfo<VipGroup> getAllVipGrouScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<VipGroup> list1 = vipGroupMapper.selectAllVipGroupScreen(params);
        for (VipGroup vipGroup : list1) {
            vipGroup.setIsactive(CheckUtils.CheckIsactive(vipGroup.getIsactive()));
            trans(vipGroup);
        }
        PageInfo<VipGroup> page = new PageInfo<VipGroup>(list1);
        return page;
    }

    void trans(VipGroup vipGroup){
        String group_type = vipGroup.getGroup_type();
        if (group_type.equals("define")){
            vipGroup.setGroup_type("自定义分组");
        }
        if (group_type.equals("define_v2")){
            vipGroup.setGroup_type("自定义分组");
        }
        if (group_type.equals("brand")){
            vipGroup.setGroup_type("品牌喜好分组");
        }
        if (group_type.equals("class")){
            vipGroup.setGroup_type("品类喜好分组");
        }
        if (group_type.equals("discount")){
            vipGroup.setGroup_type("折扣偏好分组");
        }
        if (group_type.equals("season")){
            vipGroup.setGroup_type("季节偏好分组");
        }
    }

    public JSONArray vipScreen2Array(JSONArray screen,String corp_code,String role_code,
                                     String user_brand_code,String user_area_code,String user_store_code,String user_code1)throws Exception{
        String brand_code = "";
        String area_code = "";
        String store_code = "";
        String user_code = "";
        String group_code = "";

        JSONArray post_array = new JSONArray();
        for (int i = 0; i < screen.size(); i++) {
            JSONObject screen_obj = screen.getJSONObject(i);
            if (screen_obj.containsKey("key") && screen_obj.containsKey("type") && screen_obj.containsKey("value")){
                String key = screen_obj.getString("key");
                String type = screen_obj.getString("type");
                String value = screen_obj.getString("value");

                if (type.equals("text") && value.equals("")){
                    //筛选值为空
                    continue;
                }else if (type.equals("json") && value.equals("{}")){
                    //筛选值为空
                    continue;
                }else if (key.equals("brand_code")){
                    //筛选品牌下会员
                    brand_code = value;
                }else if (key.equals("area_code")){
                    //筛选区域下会员
                    area_code = value;
                }else {
//                    JSONObject post_obj = new JSONObject();
//                    post_obj.put("key",key);
//                    post_obj.put("type",type);
//                    post_obj.put("value",value);
                    if (screen_obj.containsKey("date")){
                        screen_obj.put("date",screen_obj.getString("date"));
                    }
                    //根据key值，找出其对应name
                    if (key.equals(Common.VIP_SCREEN_BIRTH_KEY)){
                        //筛选会员生日
                        JSONObject value_obj = JSONObject.parseObject(value);
                        String start = value_obj.getString("start");
                        String end = value_obj.getString("end");
                        if (!start.equals("") && start.split("-").length < 3)
                            value_obj.put("start","2017-"+start);
                        if (!end.equals("") && end.split("-").length < 3)
                            value_obj.put("end","2017-"+end);
                        screen_obj.put("value",value_obj);
                    }
                    if (key.equals(Common.VIP_SCREEN_STORE_KEY))
                        store_code = value;
                    if (key.equals(Common.VIP_SCREEN_USER_KEY))
                        user_code = value;
                    if (key.equals(Common.VIP_SCREEN_GROUP_KEY)){
                        if (!screen_obj.containsKey("target")){
                            group_code = value;
                            String[] group_codes = group_code.split(",");
                            JSONArray code_array = new JSONArray();
                            for (int j = 0; j < group_codes.length; j++) {
                                JSONObject code_obj = new JSONObject();
                                if (group_codes[j].startsWith("#")){
                                    code_obj.put("type","fixed");
                                    code_obj.put("value",group_codes[j]);
                                    code_array.add(code_obj);
                                }else {
                                    VipGroup vipGroup = getVipGroupByCode(corp_code,group_codes[j],Common.IS_ACTIVE_Y);
                                    if (vipGroup.getGroup_type().equals("define")){
                                        code_obj.put("type","define");
                                        String group_condition = vipGroup.getGroup_condition();
                                        JSONObject condition_obj = JSONObject.parseObject(group_condition);
                                        condition_obj = vipGroupCustom(condition_obj,corp_code,"","","","","");

                                        code_obj.put("value",condition_obj);
                                    }else if (vipGroup.getGroup_type().equals("define_v2")){
                                        code_obj.put("type","define_v2");
                                        String group_condition = vipGroup.getGroup_condition();
                                       JSONArray condition_array = JSONArray.parseArray(group_condition);

                                        condition_array = vipScreen2ArrayNew(condition_array,"","","","","","");
                                        code_obj.put("value",condition_array);
                                    }else {
                                        code_obj.put("type","smart");
                                        code_obj.put("value",group_codes[j]);
                                    }
                                    code_array.add(code_obj);
                                }

                            }
//                            JSONObject new_group = new JSONObject();
//                            new_group.put("list",code_array);
//                            new_group.put("operator","OR");

                            screen_obj.put("target","1");
                            screen_obj.put("value",code_array);
                        }
                    }
                    post_array.add(screen_obj);
                }
            }
        }
        if (store_code.equals("")){
            if ((!area_code.equals("") || !brand_code.equals(""))){
                //若选择了区域和品牌，记住品牌区域下的store_code
                List<Store> storeList;
                if (corp_code.equals("C10261")){
                    //不管是否可用，都取
                    storeList = storeService.selStoreByAreaBrandCode1(corp_code, area_code, brand_code, "", "","");
                }else {
                    storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                }
                for (int i = 0; i < storeList.size(); i++) {
                    store_code = store_code + storeList.get(i).getStore_code() + ",";
                }
                if (store_code.endsWith(","))
                    store_code = store_code.substring(0,store_code.length()-1);
                JSONObject post_obj = new JSONObject();
                post_obj.put("key",Common.VIP_SCREEN_STORE_KEY);
                post_obj.put("type","text");
                post_obj.put("value",store_code);
                post_array.add(post_obj);
            }else {
                //没选择区域和品牌，传自身拥有的店铺
                if (!role_code.equals("") && !role_code.equals(Common.ROLE_SYS) && !role_code.equals(Common.ROLE_GM) && !role_code.equals(Common.ROLE_CM)){
                    if (role_code.equals(Common.ROLE_BM)){
                        if (!corp_code.equals("C10183")){ //加个判断，moco的显示所有的
                            user_brand_code = user_brand_code.replace(Common.SPECIAL_HEAD,"");
                            user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
                            List<Store> stores;
                            if (corp_code.equals("C10261")){
                                //不管是否可用，都取
                                stores = storeService.selStoreByAreaBrandCode1(corp_code,user_area_code,user_brand_code,"","","");
                            }else {
                                stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,user_brand_code,"","");
                            }
                            for (int i = 0; i < stores.size(); i++) {
                                store_code = store_code + stores.get(i).getStore_code() + ",";
                            }
                        }
                    }else if (role_code.equals(Common.ROLE_AM)){
                        user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
                        List<Store> stores;
                        if (corp_code.equals("C10261")){
                            //不管是否可用，都取
                            stores = storeService.selStoreByAreaBrandCode1(corp_code,user_area_code,"","",user_store_code,"");
                        }else {
                            stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,"","",user_store_code);
                        }
                        for (int i = 0; i < stores.size(); i++) {
                            store_code = store_code + stores.get(i).getStore_code() + ",";
                        }
                    }else{
                        store_code = user_store_code.replace(Common.SPECIAL_HEAD,"");
                    }
                    if (store_code.endsWith(","))
                        store_code = store_code.substring(0,store_code.length()-1);
                    if (!(role_code.equals(Common.ROLE_BM) && corp_code.equals("C10183"))){
                        JSONObject post_obj = new JSONObject();
                        post_obj.put("key",Common.VIP_SCREEN_STORE_KEY);
                        post_obj.put("type","text");
                        post_obj.put("value",store_code);
                        post_array.add(post_obj);
                    }
                }
            }
        }
        if (role_code.equals(Common.ROLE_STAFF) && user_code.equals("")){
            JSONObject post_obj = new JSONObject();
            post_obj.put("key",Common.VIP_SCREEN_USER_KEY);
            post_obj.put("type","text");
            post_obj.put("value",user_code1);
            post_array.add(post_obj);
        }
        return post_array;
    }

    //会员筛选
//    public JSONArray vipScreen2ArrayNew(JSONArray screen,String corp_code,String role_code, String user_brand_code,String user_area_code,String user_store_code,String user_code1)throws Exception{
//        String brand_code = "";
//        String area_code = "";
//        String store_code = "";
//        String user_code = "";
//
//        JSONArray post_array = new JSONArray();
//        for (int i = 0; i < screen.size(); i++) {
//            JSONObject screen_obj = screen.getJSONObject(i);
//            if (screen_obj.containsKey("key") && screen_obj.containsKey("type") && screen_obj.containsKey("value")){
//                String key = screen_obj.getString("key");
//                String value = screen_obj.getString("value");
//
//              if (key.equals("brand_code")){
//                    //筛选品牌下会员
//                    brand_code = value;
//                }else if (key.equals("area_code")){
//                    //筛选区域下会员
//                    area_code = value;
//                }else {
//                    if (key.equals("store_code"))
//                        store_code = value;
//                    if (key.equals("user_code"))
//                        user_code = value;
//                    if (key.equals("VIP_GROUP_CODE")){
//
//                        String group_code = value;
//                        logger.info("=========value"+value);
//
//                        String[] group_codes = group_code.split(",");
//                        JSONArray code_array = new JSONArray();
//                        for (int j = 0; j < group_codes.length; j++) {
//                            JSONObject code_obj = new JSONObject();
//                            if (group_codes[j].startsWith("#")){
//                                code_obj.put("type","fixed");
//                                code_obj.put("value",group_codes[j]);
//                                code_array.add(code_obj);
//                            }else {
//                                VipGroup vipGroup = getVipGroupByCode(corp_code,group_codes[j],Common.IS_ACTIVE_Y);
//                                logger.info("========"+corp_code+"====="+group_codes[j]);
//                                if (vipGroup.getGroup_type().equals("define")){
//                                    code_obj.put("type","define");
//                                    String group_condition = vipGroup.getGroup_condition();
//                                    JSONObject condition_obj = JSONObject.parseObject(group_condition);
//                                    condition_obj = vipGroupCustom(condition_obj,corp_code,"","","","","");
//
//                                    code_obj.put("value",condition_obj);
//                                } else if (vipGroup.getGroup_type().equals("define_v2")){
//                                    code_obj.put("type","define_v2");
//                                    String group_condition = vipGroup.getGroup_condition();
//                                    JSONArray condition_array = JSONArray.parseArray(group_condition);
//                                    condition_array = vipScreen2ArrayNew(condition_array,"","","","","","");
//                                    code_obj.put("value",condition_array);
//                                }else {
//                                    code_obj.put("type","smart");
//                                    code_obj.put("value",group_codes[j]);
//                                }
//                                code_array.add(code_obj);
//                            }
//                        }
//                        screen_obj.put("value",code_array);
//                    }
//                    post_array.add(screen_obj);
//                }
//            }
//        }
//        String store_name = "";
//        if (store_code.equals("")){
//            if ((!area_code.equals("") || !brand_code.equals(""))){
//                //若选择了区域和品牌，记住品牌区域下的store_code
//                List<Store> storeList;
//                if (corp_code.equals("C10261")){
//                    //不管是否可用，都取
//                    storeList = storeService.selStoreByAreaBrandCode1(corp_code, area_code, brand_code, "", "","");
//                }else {
//                    storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
//                }
//                for (int i = 0; i < storeList.size(); i++) {
//                    store_code = store_code + storeList.get(i).getStore_code() + ",";
//                    store_name = store_name + storeList.get(i).getStore_name() + ",";
//                }
//                JSONObject post_obj = new JSONObject();
//                post_obj.put("key","store_code");
//                post_obj.put("type","text");
//                post_obj.put("value",store_code.substring(0,store_code.length()-1));
//                post_obj.put("name",store_name.substring(0,store_name.length()-1));
//                post_obj.put("groupName","所属店铺");
//                post_array.add(post_obj);
//            }else {
//                //没选择区域和品牌，传自身拥有的店铺
//                if (!role_code.equals("") && !role_code.equals(Common.ROLE_SYS) && !role_code.equals(Common.ROLE_GM) && !role_code.equals(Common.ROLE_CM)){
//                    if (role_code.equals(Common.ROLE_BM)){
//                        if (!corp_code.equals("C10183")){ //加个判断，moco的显示所有的
//                            user_brand_code = user_brand_code.replace(Common.SPECIAL_HEAD,"");
//                            user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
//
//                            List<Store> stores;
//                            if (corp_code.equals("C10261")) {
//                                //不管是否可用，都取
//                                stores = storeService.selStoreByAreaBrandCode1(corp_code,user_area_code,user_brand_code,"","","");
//                            }else {
//                                stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,user_brand_code,"","");
//                            }
//                            for (int i = 0; i < stores.size(); i++) {
//                                store_code = store_code + stores.get(i).getStore_code() + ",";
//                                store_name = store_name + stores.get(i).getStore_name() + ",";
//                            }
//                        }
//                    }else if (role_code.equals(Common.ROLE_AM)){
//                        user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
//                        List<Store> stores;
//                        if (corp_code.equals("C10261")) {
//                            //不管是否可用，都取
//                            stores = storeService.selStoreByAreaBrandCode1(corp_code,user_area_code,"","",user_store_code,"");
//                        }else {
//                            stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,"","",user_store_code);
//                        }
//                        for (int i = 0; i < stores.size(); i++) {
//                            store_code = store_code + stores.get(i).getStore_code() + ",";
//                            store_name = store_name + stores.get(i).getStore_name() + ",";
//                        }
//                    }else{
//                        store_code = user_store_code.replace(Common.SPECIAL_HEAD,"");
//                        List<Store> stores = storeService.selectByStoreCodes(store_code,corp_code,Common.IS_ACTIVE_Y);
//                        for (int i = 0; i < stores.size(); i++) {
//                            store_name = store_name + stores.get(i).getStore_name() + ",";
//                        }
//                    }
//                    if (!(role_code.equals(Common.ROLE_BM) && corp_code.equals("C10183"))){
//                        JSONObject post_obj = new JSONObject();
//                        post_obj.put("key","store_code");
//                        post_obj.put("type","text");
//                        post_obj.put("value",store_code.substring(0,store_code.length()-1));
//                        post_obj.put("name",store_name.substring(0,store_name.length()-1));
//                        post_obj.put("groupName","所属店铺");
//                        post_array.add(post_obj);
//                    }
//                }
//            }
//        }
//        if (role_code.equals(Common.ROLE_STAFF) && user_code.equals("")){
//            JSONObject post_obj = new JSONObject();
//            post_obj.put("key","user_code");
//            post_obj.put("type","text");
//            post_obj.put("value",user_code1);
//            post_array.add(post_obj);
//        }
//        return post_array;
//    }

    //在上面的基础上修改了会员权限内的逻辑
//    public JSONArray vipScreen2ArrayNew(JSONArray screen,String corp_code,String role_code, String user_brand_code,String user_area_code,String user_store_code,String user_code1)throws Exception{
//        String brand_code = "";
//        String area_code = "";
//        String store_code = "";
//        String user_code = "";
//        JSONArray post_array = new JSONArray();
//        for (int i = 0; i < screen.size(); i++) {
//            JSONObject screen_obj = screen.getJSONObject(i);
//            if (screen_obj.containsKey("key") && screen_obj.containsKey("type") && screen_obj.containsKey("value")){
//                String key = screen_obj.getString("key");
//                String value = screen_obj.getString("value");
//
//                if (key.equals("brand_code")){
//                    //筛选品牌下会员
//                    brand_code = value;
//                }else if (key.equals("area_code")){
//                    //筛选区域下会员
//                    area_code = value;
//                }else {
//                    if (key.equals("store_code"))
//                        store_code = value;
//                    if (key.equals("user_code"))
//                        user_code = value;
//                    if (key.equals("VIP_GROUP_CODE")){
//
//                        String group_code = value;
//                        logger.info("=========value"+value);
//
//                        String[] group_codes = group_code.split(",");
//                        JSONArray code_array = new JSONArray();
//                        for (int j = 0; j < group_codes.length; j++) {
//                            JSONObject code_obj = new JSONObject();
//                            if (group_codes[j].startsWith("#")){
//                                code_obj.put("type","fixed");
//                                code_obj.put("value",group_codes[j]);
//                                code_array.add(code_obj);
//                            }else {
//                                VipGroup vipGroup = getVipGroupByCode(corp_code,group_codes[j],Common.IS_ACTIVE_Y);
//                                logger.info("========"+corp_code+"====="+group_codes[j]);
//                                if (vipGroup.getGroup_type().equals("define")){
//                                    code_obj.put("type","define");
//                                    String group_condition = vipGroup.getGroup_condition();
//                                    JSONObject condition_obj = JSONObject.parseObject(group_condition);
//                                    condition_obj = vipGroupCustom(condition_obj,corp_code,"","","","","");
//
//                                    code_obj.put("value",condition_obj);
//                                } else if (vipGroup.getGroup_type().equals("define_v2")){
//                                    code_obj.put("type","define_v2");
//                                    String group_condition = vipGroup.getGroup_condition();
//                                    JSONArray condition_array = JSONArray.parseArray(group_condition);
//                                    condition_array = vipScreen2ArrayNew(condition_array,"","","","","","");
//                                    code_obj.put("value",condition_array);
//                                }else {
//                                    code_obj.put("type","smart");
//                                    code_obj.put("value",group_codes[j]);
//                                }
//                                code_array.add(code_obj);
//                            }
//                        }
//                        screen_obj.put("value",code_array);
//                    }
//                    post_array.add(screen_obj);
//                }
//            }
//        }
//        String store_name = "";
//        if (store_code.equals("")){
//            if (!area_code.equals("") && !brand_code.equals("")){
//                List<Store> stores;
//                if (corp_code.equals("C10261")) {
//                    //不管是否可用，都取
//                    stores = storeService.selStoreByAreaBrandCode1(corp_code, user_area_code, brand_code, "", "", "");
//                } else {
//                    stores = storeService.selStoreByAreaBrandCode(corp_code, user_area_code, brand_code, "", "");
//                }
//                for (int i = 0; i < stores.size(); i++) {
//                    store_code = store_code + stores.get(i).getStore_code() + ",";
//                    store_name = store_name + stores.get(i).getStore_name() + ",";
//                }
//            }else {
//                if (role_code.equals("") || role_code.equals(Common.ROLE_SYS) || role_code.equals(Common.ROLE_GM) || role_code.equals(Common.ROLE_CM)) {
//                    if (!area_code.equals("") || !brand_code.equals("")) {
//                        List<Store> stores;
//                        if (corp_code.equals("C10261")) {
//                            //不管是否可用，都取
//                            stores = storeService.selStoreByAreaBrandCode1(corp_code, user_area_code, brand_code, "", "", "");
//                        } else {
//                            stores = storeService.selStoreByAreaBrandCode(corp_code, user_area_code, brand_code, "", "");
//                        }
//                        for (int i = 0; i < stores.size(); i++) {
//                            store_code = store_code + stores.get(i).getStore_code() + ",";
//                            store_name = store_name + stores.get(i).getStore_name() + ",";
//                        }
//                    }
//                } else {
//                    if (role_code.equals(Common.ROLE_BM)) {
//                        if (brand_code.equals("") && !corp_code.equals("C10183"))
//                            brand_code = user_brand_code.replace(Common.SPECIAL_HEAD, "");
//                        if (area_code.equals(""))
//                            area_code = user_area_code.replace(Common.SPECIAL_HEAD, "");
//                        List<Store> stores;
//                        if (corp_code.equals("C10261")) {
//                            //不管是否可用，都取
//                            stores = storeService.selStoreByAreaBrandCode1(corp_code, area_code, brand_code, "", "", "");
//                        } else {
//                            stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
//                        }
//                        for (int i = 0; i < stores.size(); i++) {
//                            store_code = store_code + stores.get(i).getStore_code() + ",";
//                            store_name = store_name + stores.get(i).getStore_name() + ",";
//                        }
//                    } else if (role_code.equals(Common.ROLE_AM)) {
//                        if (area_code.equals(""))
//                            area_code = user_area_code.replace(Common.SPECIAL_HEAD, "");
//                        List<Store> stores;
//                        if (corp_code.equals("C10261")) {
//                            //不管是否可用，都取
//                            stores = storeService.selStoreByAreaBrandCode1(corp_code, area_code, brand_code, "", user_store_code, "");
//                        } else {
//                            stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", user_store_code);
//                        }
//                        for (int i = 0; i < stores.size(); i++) {
//                            store_code = store_code + stores.get(i).getStore_code() + ",";
//                            store_name = store_name + stores.get(i).getStore_name() + ",";
//                        }
//                    } else {
//                        store_code = user_store_code.replace(Common.SPECIAL_HEAD, "");
//                        List<Store> stores = storeService.selectByStoreCodes(store_code, corp_code, Common.IS_ACTIVE_Y);
//                        for (int i = 0; i < stores.size(); i++) {
//                            store_name = store_name + stores.get(i).getStore_name() + ",";
//                        }
//                    }
//                }
//            }
//            if (!(role_code.equals(Common.ROLE_BM) && corp_code.equals("C10183"))){
//                JSONObject post_obj = new JSONObject();
//                post_obj.put("key","store_code");
//                post_obj.put("type","text");
//                post_obj.put("value",store_code.substring(0,store_code.length()-1));
//                post_obj.put("name",store_name.substring(0,store_name.length()-1));
//                post_obj.put("groupName","所属店铺");
//                post_array.add(post_obj);
//            }
//        }
//        if (role_code.equals(Common.ROLE_STAFF) && user_code.equals("")){
//            JSONObject post_obj = new JSONObject();
//            post_obj.put("key","user_code");
//            post_obj.put("type","text");
//            post_obj.put("value",user_code1);
//            post_array.add(post_obj);
//        }
//        return post_array;
//    }


    //修改了会员权限内的逻辑
//    public JSONArray vipScreen2ArrayNew(JSONArray screen,String corp_code,String role_code, String user_brand_code,String user_area_code,String user_store_code,String user_code1)throws Exception{
//        String brand_code = "";
//        String area_code = "";
//        String store_code = "";
//        String user_code = "";
//
//        JSONArray post_array = new JSONArray();
//        for (int i = 0; i < screen.size(); i++) {
//            JSONObject screen_obj = screen.getJSONObject(i);
//            if (screen_obj.containsKey("key") && screen_obj.containsKey("type") && screen_obj.containsKey("value")){
//                String key = screen_obj.getString("key");
//                String value = screen_obj.getString("value");
//
//                if (key.equals("brand_code")){
//                    //筛选品牌下会员
//                    brand_code = value;
//                }else if (key.equals("area_code")){
//                    //筛选区域下会员
//                    area_code = value;
//                }else {
//                    if (key.equals("store_code"))
//                        store_code = value;
//                    if (key.equals("user_code"))
//                        user_code = value;
//                    if (key.equals("VIP_GROUP_CODE")){
//                        String group_code = value;
//                        String[] group_codes = group_code.split(",");
//                        JSONArray code_array = new JSONArray();
//                        for (int j = 0; j < group_codes.length; j++) {
//                            JSONObject code_obj = new JSONObject();
//                            if (group_codes[j].startsWith("#")){
//                                code_obj.put("type","fixed");
//                                code_obj.put("value",group_codes[j]);
//                                code_array.add(code_obj);
//                            }else {
//                                VipGroup vipGroup = getVipGroupByCode(corp_code,group_codes[j],Common.IS_ACTIVE_Y);
//                                if (vipGroup.getGroup_type().equals("define")){
//                                    code_obj.put("type","define");
//                                    String group_condition = vipGroup.getGroup_condition();
//                                    JSONObject condition_obj = JSONObject.parseObject(group_condition);
//                                    condition_obj = vipGroupCustom(condition_obj,corp_code,"","","","","");
//
//                                    code_obj.put("value",condition_obj);
//                                } else if (vipGroup.getGroup_type().equals("define_v2")){
//                                    code_obj.put("type","define_v2");
//                                    String group_condition = vipGroup.getGroup_condition();
//                                    JSONArray condition_array = JSONArray.parseArray(group_condition);
//                                    condition_array = vipScreen2ArrayNew(condition_array,"","","","","","");
//                                    code_obj.put("value",condition_array);
//                                }else {
//                                    code_obj.put("type","smart");
//                                    code_obj.put("value",group_codes[j]);
//                                }
//                                code_array.add(code_obj);
//                            }
//                        }
//                        screen_obj.put("value",code_array);
//                        screen_obj.put("value1",value);
//
//                    }
//                    post_array.add(screen_obj);
//                }
//            }
//        }
//
//        String store_name = "";
//        if (store_code.equals("")){
//            //筛选品牌
//            if(StringUtils.isNotBlank(brand_code)){
//                JSONObject jsonObject2=new JSONObject();
//                jsonObject2.put("value",brand_code);
//                jsonObject2.put("key","BRAND_ID");
//                jsonObject2.put("type","text");
//                post_array.add(jsonObject2);
//            }else if (role_code.equals(Common.ROLE_BM) && !corp_code.equals("C10183")){
//                brand_code = user_brand_code.replace(Common.SPECIAL_HEAD,"");
//                JSONObject jsonObject2=new JSONObject();
//                jsonObject2.put("value",brand_code);
//                jsonObject2.put("key","BRAND_ID");
//                jsonObject2.put("type","text");
//                post_array.add(jsonObject2);
//            }
//            if (!area_code.equals("")){
//                //若选择了区域和品牌，记住品牌区域下的store_code
//                List<Store> storeList;
//                if (brand_code.equals(""))
//                    brand_code = user_brand_code;
//                if (corp_code.equals("C10261")){
//                    //不管是否可用，都取
//                    storeList = storeService.selStoreByAreaBrandCode1(corp_code, area_code,brand_code, "", "","");
//                }else {
//                    storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
//                }
//                for (int i = 0; i < storeList.size(); i++) {
//                    store_code = store_code + storeList.get(i).getStore_code() + ",";
//                    store_name = store_name + storeList.get(i).getStore_name() + ",";
//                }
//            }else{
//                if (role_code.equals(Common.ROLE_BM)){
//                    if (!corp_code.equals("C10183")){ //加个判断，moco的显示所有的
//                        user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
//                        List<Store> stores;
//                        if (!user_area_code.equals("")){
//                            if (brand_code.equals(""))
//                                brand_code = user_brand_code.replace(Common.SPECIAL_HEAD,"");
//                            if (corp_code.equals("C10261")) {
//                                //不管是否可用，都取
//                                stores = storeService.selStoreByAreaBrandCode1(corp_code,user_area_code,brand_code,"","","");
//                            }else {
//                                stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,brand_code,"","");
//                            }
//                            for (int i = 0; i < stores.size(); i++) {
//                                store_code = store_code + stores.get(i).getStore_code() + ",";
//                                store_name = store_name + stores.get(i).getStore_name() + ",";
//                            }
//                        }
//                    }
//                }else if (role_code.equals(Common.ROLE_AM)){
//                    user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
//                    List<Store> stores;
//                    if (corp_code.equals("C10261")) {
//                        //不管是否可用，都取
//                        stores = storeService.selStoreByAreaBrandCode1(corp_code,user_area_code,brand_code,"",user_store_code,"");
//                    }else {
//                        stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,brand_code,"",user_store_code);
//                    }
//                    for (int i = 0; i < stores.size(); i++) {
//                        store_code = store_code + stores.get(i).getStore_code() + ",";
//                        store_name = store_name + stores.get(i).getStore_name() + ",";
//                    }
//                }else if (role_code.equals(Common.ROLE_SM)){
//                    store_code = user_store_code.replace(Common.SPECIAL_HEAD,"");
//                }
//            }
//            JSONObject post_obj = new JSONObject();
//            post_obj.put("key","store_code");
//            post_obj.put("type","text");
//
//            if(store_code.endsWith(",")){
//                store_code=store_code.substring(0,store_code.length()-1);
//            }
//            if(store_name.endsWith(",")){
//                store_name=store_name.substring(0,store_code.length()-1);
//            }
//            post_obj.put("value",store_code);
//            post_obj.put("name",store_name);
//            post_obj.put("groupName","所属店铺");
//            post_array.add(post_obj);
//        }
//        if (role_code.equals(Common.ROLE_STAFF) && user_code.equals("")){
//            JSONObject post_obj = new JSONObject();
//            post_obj.put("key","user_code");
//            post_obj.put("type","text");
//            post_obj.put("value",user_code1);
//            post_array.add(post_obj);
//        }
//
//        //如果不是企业管理员 则不等于店铺 品牌 和 导购的情况
//        return post_array;
//    }



    //修改了会员权限内的逻辑(将自身权限下的店铺或导购加入条件)
    public JSONArray vipScreen2ArrayNew(JSONArray screen,String corp_code,String role_code, String user_brand_code,String user_area_code,String user_store_code,String user_code1)throws Exception{
        JSONArray post_screen_array = new JSONArray();
        for (int i = 0; i < screen.size(); i++) {
            JSONObject screen_obj = screen.getJSONObject(i);
            String key = screen_obj.getString("key");
            String value = screen_obj.getString("value");
            if (key.equals("VIP_GROUP_CODE")) {
                String group_code = value;
                String[] group_codes = group_code.split(",");
                JSONArray code_array = new JSONArray();
                for (int j = 0; j < group_codes.length; j++) {
                    JSONObject code_obj = new JSONObject();
                    if (group_codes[j].startsWith("#")) {
                        code_obj.put("type", "fixed");
                        code_obj.put("value", group_codes[j]);
                        code_array.add(code_obj);
                    } else {
                        VipGroup vipGroup = getVipGroupByCode(corp_code, group_codes[j], Common.IS_ACTIVE_Y);
                        if (vipGroup.getGroup_type().equals("define")) {
                            code_obj.put("type", "define");
                            String group_condition = vipGroup.getGroup_condition();
                            JSONObject condition_obj = JSONObject.parseObject(group_condition);
                            condition_obj = vipGroupCustom(condition_obj, corp_code, "", "", "", "", "");

                            code_obj.put("value", condition_obj);
                        } else if (vipGroup.getGroup_type().equals("define_v2")) {
                            code_obj.put("type", "define_v2");
                            String group_condition = vipGroup.getGroup_condition();
                            JSONArray condition_array = JSONArray.parseArray(group_condition);
                            condition_array = vipScreen2ArrayNew(condition_array, "", "", "", "", "", "");
                            code_obj.put("value", condition_array);
                        } else {
                            code_obj.put("type", "smart");
                            code_obj.put("value", group_codes[j]);
                        }
                        code_array.add(code_obj);
                    }
                }
                screen_obj.put("value", code_array);
            }

            if(key.equals("brand_code")){
                screen_obj.put("key","BRAND_ID");
            }

            if(key.equals("area_code")){
                HashSet<String> hashSet=new HashSet<String>();
                String area_code=value;
                List<Store> list=new ArrayList<Store>();
                if (corp_code.equals("C10261")){
                    //不管是否可用，都取
                    list = storeService.selStoreByAreaBrandCode1(corp_code, area_code,"", "", "","");
                }else {
                    list = storeService.selStoreByAreaBrandCode(corp_code, area_code, "", "", "");
                }
                for (int j = 0; j <list.size() ; j++) {
                    Store store=list.get(j);
                    if(StringUtils.isNotBlank(store.getStore_code())){
                        hashSet.add(store.getStore_code());
                    }
                }
                String store="";
                Iterator<String> iterator=hashSet.iterator();
                while (iterator.hasNext()){
                    store+=iterator.next() +",";
                }
                if (store.endsWith(","))
                    store = store.substring(0,store.length()-1);
                screen_obj.put("value",store);
                screen_obj.put("key","store_code");
            }
            post_screen_array.add(screen_obj);
        }

        if(role_code.equals(Common.ROLE_SYS)||role_code.equals(Common.ROLE_CM)||role_code.equals(Common.ROLE_GM)){
            //不处理
        }else if(role_code.equals(Common.ROLE_BM)||role_code.equals(Common.ROLE_AM)){
            user_brand_code = user_brand_code.replace(Common.SPECIAL_HEAD,"");
            user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
            user_store_code=user_store_code.replace(Common.SPECIAL_HEAD,"");
            //添加自身权限
            if (!(corp_code.equals("C10183")&&role_code.equals(Common.ROLE_BM))) { //加个判断，moco的显示所有的
                List<Store> stores = new ArrayList<Store>();
                if(role_code.equals(Common.ROLE_BM)) {
                    if (corp_code.equals("C10261")) {
                        //不管是否可用，都取
                        stores = storeService.selStoreByAreaBrandCode1(corp_code, user_area_code, user_brand_code, "", "", "");
                    } else {
                        stores = storeService.selStoreByAreaBrandCode(corp_code, user_area_code, user_brand_code, "", "");
                    }
                }else if(role_code.equals(Common.ROLE_AM)){
                    if (corp_code.equals("C10261")){
                        //不管是否可用，都取
                        stores = storeService.selStoreByAreaBrandCode1(corp_code,user_area_code,"","",user_store_code,"");
                    }else {
                        stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,"","",user_store_code);
                    }
                }
                String store_code = "";
                for (int i = 0; i < stores.size(); i++) {
                    store_code = store_code + stores.get(i).getStore_code() + ",";
                }
                if (store_code.endsWith(","))
                    store_code = store_code.substring(0,store_code.length()-1);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", "store_code");
                jsonObject.put("value", store_code);
                jsonObject.put("type", "text");
                jsonObject.put("logic", "equal");
                post_screen_array.add(jsonObject);
            }
        } else if(role_code.equals(Common.ROLE_SM)||role_code.equals(Common.ROLE_STAFF)){

            if(role_code.equals(Common.ROLE_SM)) {
                String store_code= user_store_code.replace(Common.SPECIAL_HEAD,"");
                List<Store> stores = storeService.selectByStoreCodes(store_code,corp_code,Common.IS_ACTIVE_Y);
                String store_codes="";
                for (int i = 0; i < stores.size(); i++) {
                    store_codes = store_codes + stores.get(i).getStore_code() + ",";
                }
                if (store_codes.endsWith(","))
                    store_codes = store_codes.substring(0,store_codes.length()-1);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", "store_code");
                jsonObject.put("value", store_codes);
                jsonObject.put("type", "text");
                jsonObject.put("logic", "equal");
                post_screen_array.add(jsonObject);
            }

            if(role_code.equals(Common.ROLE_STAFF)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", "user_code");
                jsonObject.put("value", user_code1);
                jsonObject.put("type", "text");
                jsonObject.put("logic", "equal");
                post_screen_array.add(jsonObject);
            }
        }
        return post_screen_array;
    }

    //筛选条件品牌取BRAND_ID(人的筛选)
//    public JSONArray vipScreen2ArrayNewV2(JSONArray screen,String corp_code,String role_code, String user_brand_code,String user_area_code,String user_store_code,String user_code1)throws Exception{
//        String brand_code = "";
//        String area_code = "";
//        String store_code = "";
//        String user_code = "";
//
//        JSONArray post_array = new JSONArray();
//        for (int i = 0; i < screen.size(); i++) {
//            JSONObject screen_obj = screen.getJSONObject(i);
//            if (screen_obj.containsKey("key") && screen_obj.containsKey("type") && screen_obj.containsKey("value")){
//                String key = screen_obj.getString("key");
//                String value = screen_obj.getString("value");
//
//                if (key.equals("brand_code")){
//                    //筛选品牌下会员
//                    brand_code = value;
//                }else if (key.equals("area_code")){
//                    //筛选区域下会员
//                    area_code = value;
//                }else {
//                    if (key.equals("store_code"))
//                        store_code = value;
//                    if (key.equals("user_code"))
//                        user_code = value;
//                    if (key.equals("VIP_GROUP_CODE")){
//                        String group_code = value;
//                        String[] group_codes = group_code.split(",");
//                        JSONArray code_array = new JSONArray();
//                        for (int j = 0; j < group_codes.length; j++) {
//                            JSONObject code_obj = new JSONObject();
//                            if (group_codes[j].startsWith("#")){
//                                code_obj.put("type","fixed");
//                                code_obj.put("value",group_codes[j]);
//                                code_array.add(code_obj);
//                            }else {
//                                VipGroup vipGroup = getVipGroupByCode(corp_code,group_codes[j],Common.IS_ACTIVE_Y);
//                                if (vipGroup.getGroup_type().equals("define")){
//                                    code_obj.put("type","define");
//                                    String group_condition = vipGroup.getGroup_condition();
//                                    JSONObject condition_obj = JSONObject.parseObject(group_condition);
//                                    condition_obj = vipGroupCustom(condition_obj,corp_code,"","","","","");
//
//                                    code_obj.put("value",condition_obj);
//                                } else if (vipGroup.getGroup_type().equals("define_v2")){
//                                    code_obj.put("type","define_v2");
//                                    String group_condition = vipGroup.getGroup_condition();
//                                    JSONArray condition_array = JSONArray.parseArray(group_condition);
//                                    condition_array = vipScreen2ArrayNewV2(condition_array,"","","","","","");
//                                    code_obj.put("value",condition_array);
//                                }else {
//                                    code_obj.put("type","smart");
//                                    code_obj.put("value",group_codes[j]);
//                                }
//                                code_array.add(code_obj);
//                            }
//                        }
//                        screen_obj.put("value",code_array);
//                        screen_obj.put("value1",value);
//
//                    }
//                    post_array.add(screen_obj);
//                }
//            }
//        }
//
//        //筛选品牌
//        if(StringUtils.isNotBlank(brand_code)){
//            JSONObject jsonObject2=new JSONObject();
//            jsonObject2.put("value",brand_code);
//            jsonObject2.put("key","BRAND_ID");
//            jsonObject2.put("type","text");
//            post_array.add(jsonObject2);
//        }
//
//        String store_name = "";
//        if (store_code.equals("")){
//            if (!area_code.equals("")){
//                //若选择了区域和品牌，记住品牌区域下的store_code
//                List<Store> storeList;
//                if (corp_code.equals("C10261")){
//                    //不管是否可用，都取
//                    storeList = storeService.selStoreByAreaBrandCode1(corp_code, area_code,"", "", "","");
//                }else {
//                    storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, "", "", "");
//                }
//                for (int i = 0; i < storeList.size(); i++) {
//                    store_code = store_code + storeList.get(i).getStore_code() + ",";
//                    store_name = store_name + storeList.get(i).getStore_name() + ",";
//                }
//                JSONObject post_obj = new JSONObject();
//                post_obj.put("key","store_code");
//                post_obj.put("type","text");
//                post_obj.put("value",store_code.substring(0,store_code.length()-1));
//                post_obj.put("name",store_name.substring(0,store_name.length()-1));
//                post_obj.put("groupName","所属店铺");
//                post_array.add(post_obj);
//            }else{
//                //没选择区域和品牌，传自身拥有的店铺
//                if (!role_code.equals("") && !role_code.equals(Common.ROLE_SYS) && !role_code.equals(Common.ROLE_GM) && !role_code.equals(Common.ROLE_CM)){
//                    if (role_code.equals(Common.ROLE_BM)){
//                        if (!corp_code.equals("C10183")){ //加个判断，moco的显示所有的
//                            user_brand_code = user_brand_code.replace(Common.SPECIAL_HEAD,"");
//                            user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
//
//                            List<Store> stores;
//                            if (!user_area_code.equals("")){
//                                if (corp_code.equals("C10261")) {
//                                    //不管是否可用，都取
//                                    stores = storeService.selStoreByAreaBrandCode1(corp_code,user_area_code,"","","","");
//                                }else {
//                                    stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,"","","");
//                                }
//                                for (int i = 0; i < stores.size(); i++) {
//                                    store_code = store_code + stores.get(i).getStore_code() + ",";
//                                    store_name = store_name + stores.get(i).getStore_name() + ",";
//                                }
//                            }
//                            if(StringUtils.isBlank(brand_code)) {
//                                if (StringUtils.isNotBlank(user_brand_code)) {
//                                    JSONObject jsonObject2=new JSONObject();
//                                    jsonObject2.put("value", user_brand_code);
//                                    jsonObject2.put("key", "BRAND_ID");
//                                    jsonObject2.put("type", "text");
//                                    post_array.add(jsonObject2);
//                                }
//                            }
//                        }
//                    }else if (role_code.equals(Common.ROLE_AM)){
//                        user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
//                        List<Store> stores;
//                        if (corp_code.equals("C10261")) {
//                            //不管是否可用，都取
//                            stores = storeService.selStoreByAreaBrandCode1(corp_code,user_area_code,"","",user_store_code,"");
//                        }else {
//                            stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,"","",user_store_code);
//                        }
//                        for (int i = 0; i < stores.size(); i++) {
//                            store_code = store_code + stores.get(i).getStore_code() + ",";
//                            store_name = store_name + stores.get(i).getStore_name() + ",";
//                        }
//                    }else{
//                        store_code = user_store_code.replace(Common.SPECIAL_HEAD,"");
//                    }
//                    if (!(role_code.equals(Common.ROLE_BM) && corp_code.equals("C10183"))){
//                        JSONObject post_obj = new JSONObject();
//                        post_obj.put("key","store_code");
//                        post_obj.put("type","text");
//                        post_obj.put("value",store_code.substring(0,store_code.length()-1));
//                        post_obj.put("name",store_name.substring(0,store_name.length()-1));
//                        post_obj.put("groupName","所属店铺");
//                        post_array.add(post_obj);
//                    }
//                }
//            }
//        }
//        if (role_code.equals(Common.ROLE_STAFF) && user_code.equals("")){
//            JSONObject post_obj = new JSONObject();
//            post_obj.put("key","user_code");
//            post_obj.put("type","text");
//            post_obj.put("value",user_code1);
//            post_array.add(post_obj);
//        }
//        return post_array;
//    }



    /**
     * Solr筛选
     * 区分角色
     * @param screen
     * @param corp_code
     * @param page_num
     * @param page_size
     * @return
     * @throws Exception
     */
    public DataBox vipScreenBySolr(JSONArray screen,String corp_code,String page_num,String page_size,String role_code,
                                   String user_brand_code,String user_area_code,String user_store_code,String user_code1,
                                   String sort_key,String sort_value) throws Exception{

        JSONArray post_array = vipScreen2Array(screen, corp_code, role_code, user_brand_code, user_area_code, user_store_code, user_code1);
        DataBox dataBox;
        if (page_size.equals("0") && page_num.equals("0")){
            //自定义分组图表接口
            dataBox = iceInterfaceService.vipCustomGroup(corp_code,JSON.toJSONString(post_array),"define_v2","");
        }else {
            //筛选
            dataBox = iceInterfaceService.vipScreenMethod2(page_num, page_size, corp_code,JSON.toJSONString(post_array),sort_key,sort_value);
        }
        return dataBox;
    }


    public DataBox vipScreenBySolrNew(JSONArray screen,String corp_code,String page_num,String page_size,String role_code,
                                   String user_brand_code,String user_area_code,String user_store_code,String user_code1,
                                   String sort_key,String sort_value) throws Exception{

        logger.info("========="+JSON.toJSONString(screen));
        JSONArray post_array = vipScreen2ArrayNew(screen, corp_code, role_code, user_brand_code, user_area_code, user_store_code, user_code1);
        DataBox dataBox;
        if (page_size.equals("0") && page_num.equals("0")){
            //自定义分组图表接口
            dataBox = iceInterfaceService.vipCustomGroup(corp_code,JSON.toJSONString(post_array),"define_v2","");
        }else {
            //筛选
            dataBox = iceInterfaceService.newStyleVipSearchForWeb(page_num, page_size, corp_code,JSON.toJSONString(post_array),sort_key,sort_value);
        }
        return dataBox;
    }

    public JSONObject groupCustom(JSONObject condition,String corp_code)throws Exception{

        if(condition.containsKey("operator")){
            JSONArray jsonArray =condition.getJSONArray("list");
            //拼接自定义条件
            JSONArray new_array = new JSONArray();
            for(int i=0;i<jsonArray.size();i++){
                JSONObject jsonObject2=jsonArray.getJSONObject(i);
                //递归............
                JSONObject value=groupCustom(jsonObject2,corp_code);
                if (value != null){
                    new_array.add(value);
                }
            }
            condition.put("list",new_array);
            return condition;
        }else{
            //处理所有的jsonObject 处理券，任务递归格式 /*****待处理******/
            if (condition.containsKey("key")){
                String key = condition.get("key").toString();
                String value = condition.getString("value");
                if (key.equals(Common.VIP_SCREEN_BIRTH_KEY) ){
                    //筛选会员生日
                    JSONObject value_obj = JSONObject.parseObject(value);
                    String start = value_obj.getString("start");
                    String end = value_obj.getString("end");
                    if (!start.equals("") && start.split("-").length < 3)
                        value_obj.put("start","2017-"+start);
                    if (!end.equals("") && end.split("-").length < 3)
                        value_obj.put("end","2017-"+end);
                    condition.put("value",value_obj);
                }
                if (key.equals(Common.VIP_SCREEN_GROUP_KEY) || key.equals("VIP_GROUP_CODE")){
                    String group_code = value;
                    String[] group_codes = group_code.split(",");
                    JSONArray code_array = new JSONArray();
                    for (int j = 0; j < group_codes.length; j++) {
                        JSONObject code_obj = new JSONObject();
                        if (group_codes[j].startsWith("#")){
                            code_obj.put("type","fixed");
                            code_obj.put("value",group_codes[j]);
                            code_array.add(code_obj);
                        }else {
                            code_obj.put("type","smart");
                            code_obj.put("value",group_codes[j]);
                            code_array.add(code_obj);
                        }
                    }
                    condition.put("value",code_array);
                }
                if (key.equals("brand_code")){
                    //筛选品牌下会员
//                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, "", value, "", "");
//                    if (storeList.size() > 0){
//                        String store_code = "";
//                        for (int i = 0; i < storeList.size(); i++) {
//                            store_code = store_code + storeList.get(i).getStore_code() + ",";
//                        }
//                        condition.put("key",Common.VIP_SCREEN_STORE_KEY);
//                        condition.put("type","text");
//                        condition.put("value",store_code);
//                    }else {
//                        return null;
//                    }
                    condition.put("key","BRAND_ID");
                    condition.put("type","text");
                    condition.put("value",value);
              }
                if (key.equals("area_code")){
                    //筛选区域下会员
                    List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, value, "", "", "");
                    if (storeList.size() > 0) {
                        String store_code = "";
                        for (int i = 0; i < storeList.size(); i++) {
                            store_code = store_code + storeList.get(i).getStore_code() + ",";
                        }
                        if (store_code.endsWith(","))
                            store_code = store_code.substring(0,store_code.length()-1);
                        condition.put("key",Common.VIP_SCREEN_STORE_KEY);
                        condition.put("type","text");
                        condition.put("value",store_code);
                    }else {
                        return null;
                    }
                }
            }
        }
        return condition;
    }

    public JSONObject vipGroupCustom(JSONObject condition,String corp_code,String role_code,
                                     String user_brand_code,String user_area_code,String user_store_code,String user_code1)throws Exception{

        condition = groupCustom(condition,corp_code);
        JSONObject new_condition = new JSONObject();
        JSONArray post_array = new JSONArray();
        //没选择区域和品牌，传自身拥有的店铺
        if (!role_code.equals(Common.ROLE_SYS) && !role_code.equals(Common.ROLE_GM) && !role_code.equals(Common.ROLE_CM)){
            String store_code = "";
            if (role_code.equals(Common.ROLE_BM)){
                if (!corp_code.equals("C10183")){ //加个判断，moco的显示所有的
                    user_brand_code = user_brand_code.replace(Common.SPECIAL_HEAD,"");
                    user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,user_brand_code,"","");
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + stores.get(i).getStore_code() + ",";
                    }
                }
            }else if (role_code.equals(Common.ROLE_AM)){
                user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,"","",user_store_code);
                for (int i = 0; i < stores.size(); i++) {
                    store_code = store_code + stores.get(i).getStore_code() + ",";
                }
            }else if (role_code.equals(Common.ROLE_SM)){
                store_code = user_store_code.replace(Common.SPECIAL_HEAD,"");
            }else if (role_code.equals(Common.ROLE_STAFF)){
                JSONObject post_obj = new JSONObject();
                post_obj.put("key",Common.VIP_SCREEN_USER_KEY);
                post_obj.put("type","text");
                post_obj.put("value",user_code1);
                post_array.add(post_obj);
            }
            if (store_code.endsWith(","))
                store_code = store_code.substring(0,store_code.length()-1);
            if (!(role_code.equals(Common.ROLE_BM) && corp_code.equals("C10183")) && !store_code.equals("")){
                JSONObject post_obj = new JSONObject();
                post_obj.put("key",Common.VIP_SCREEN_STORE_KEY);
                post_obj.put("type","text");
                post_obj.put("value",store_code);
                post_array.add(post_obj);
            }
        }

        if (post_array.size() > 0){
            post_array.add(condition);
            new_condition.put("list",post_array);
            new_condition.put("operator","AND");
            return new_condition;
        }else {
            return condition;
        }
    }


//    public JSONObject vipGroupCustom1(JSONObject condition,String corp_code,String role_code,
//                                     String user_brand_code,String user_area_code,String user_store_code,String user_code1)throws Exception{
//
//        JSONObject new_condition = new JSONObject();
//        JSONArray post_array = new JSONArray();
//        //没选择区域和品牌，传自身拥有的店铺
//        if (!role_code.equals(Common.ROLE_SYS) && !role_code.equals(Common.ROLE_GM) && !role_code.equals(Common.ROLE_CM)){
//            String store_code = "";
//            if (role_code.equals(Common.ROLE_BM)){
//                if (!corp_code.equals("C10183")){ //加个判断，moco的显示所有的
//                    user_brand_code = user_brand_code.replace(Common.SPECIAL_HEAD,"");
//                    user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
//                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,user_brand_code,"","");
//                    for (int i = 0; i < stores.size(); i++) {
//                        store_code = store_code + stores.get(i).getStore_code() + ",";
//                    }
//                }
//            }else if (role_code.equals(Common.ROLE_AM)){
//                user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
//                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,"","",user_store_code);
//                for (int i = 0; i < stores.size(); i++) {
//                    store_code = store_code + stores.get(i).getStore_code() + ",";
//                }
//            }else if (role_code.equals(Common.ROLE_SM)){
//                store_code = user_store_code.replace(Common.SPECIAL_HEAD,"");
//            }else if (role_code.equals(Common.ROLE_STAFF)){
//                JSONObject post_obj = new JSONObject();
//                post_obj.put("key",Common.VIP_SCREEN_USER_KEY);
//                post_obj.put("type","text");
//                post_obj.put("value",user_code1);
//                post_array.add(post_obj);
//            }
//            if (!(role_code.equals(Common.ROLE_BM) && corp_code.equals("C10183")) && !store_code.equals("")){
//                JSONObject post_obj = new JSONObject();
//                post_obj.put("key",Common.VIP_SCREEN_STORE_KEY);
//                post_obj.put("type","text");
//                post_obj.put("value",store_code);
//                post_array.add(post_obj);
//            }
//        }
//
//        if (post_array.size() > 0){
//            post_array.add(condition);
//            new_condition.put("list",post_array);
//            new_condition.put("operator","AND");
//            return new_condition;
//        }else {
//            return condition;
//        }
//    }
//
//    public JSONArray vipGroupCustomDefineV2(JSONArray screen,String corp_code,String role_code,
//                                      String user_brand_code,String user_area_code,String user_store_code,String user_code1)throws Exception{
//
//        JSONObject new_condition = new JSONObject();
//        JSONArray post_array = new JSONArray();
//        //没选择区域和品牌，传自身拥有的店铺
//        if (!role_code.equals(Common.ROLE_SYS) && !role_code.equals(Common.ROLE_GM) && !role_code.equals(Common.ROLE_CM)){
//            String store_code = "";
//            if (role_code.equals(Common.ROLE_BM)){
//                if (!corp_code.equals("C10183")){ //加个判断，moco的显示所有的
//                    user_brand_code = user_brand_code.replace(Common.SPECIAL_HEAD,"");
//                    user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
//                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,user_brand_code,"","");
//                    for (int i = 0; i < stores.size(); i++) {
//                        store_code = store_code + stores.get(i).getStore_code() + ",";
//                    }
//                }
//            }else if (role_code.equals(Common.ROLE_AM)){
//                user_area_code = user_area_code.replace(Common.SPECIAL_HEAD,"");
//                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,user_area_code,"","",user_store_code);
//                for (int i = 0; i < stores.size(); i++) {
//                    store_code = store_code + stores.get(i).getStore_code() + ",";
//                }
//            }else if (role_code.equals(Common.ROLE_SM)){
//                store_code = user_store_code.replace(Common.SPECIAL_HEAD,"");
//            }else if (role_code.equals(Common.ROLE_STAFF)){
//                JSONObject post_obj = new JSONObject();
//                post_obj.put("key",Common.VIP_SCREEN_USER_KEY);
//                post_obj.put("type","text");
//                post_obj.put("value",user_code1);
//                post_array.add(post_obj);
//            }
//            if (!(role_code.equals(Common.ROLE_BM) && corp_code.equals("C10183")) && !store_code.equals("")){
//                JSONObject post_obj = new JSONObject();
//                post_obj.put("key",Common.VIP_SCREEN_STORE_KEY);
//                post_obj.put("type","text");
//                post_obj.put("value",store_code);
//                post_array.add(post_obj);
//            }
//        }
//
//        if (post_array.size() > 0){
//            screen.addAll(post_array);
//        }
//        return  screen;
//    }

}
