package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.dao.VipGroupMapper;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.entity.VipGroup;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.StoreService;
import com.bizvane.ishop.service.TableManagerService;
import com.bizvane.ishop.service.VipGroupService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.sun.v1.common.DataBox;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
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
    public String insert(VipGroup vipGroup, String user_code) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        String vip_group_name = vipGroup.getVip_group_name().trim();
        String corp_code = vipGroup.getCorp_code();

        VipGroup vipGroup2 = getVipGroupByName(corp_code, vip_group_name, Common.IS_ACTIVE_Y);
        if (vipGroup2 != null) {
            result = "该会员分组名称已存在";
        } else {
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
    public String update(VipGroup vipGroup, String user_code) throws Exception {
        String result = "";
        int id = vipGroup.getId();
        String vip_group_name = vipGroup.getVip_group_name().trim();
        String corp_code = vipGroup.getCorp_code();

        VipGroup vipGroup2 = getVipGroupByName(corp_code, vip_group_name, Common.IS_ACTIVE_Y);

        if (vipGroup2 != null && vipGroup2.getId() != id) {
            result = "该会员分组名称已存在";
        } else {
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

    /**
     * Solr筛选
     * 区分角色
     * @param screen
     * @param corp_code
     * @param page_num
     * @param page_size
     * @param request
     * @return
     * @throws Exception
     */
    public DataBox vipScreenBySolr(JSONArray screen,String corp_code,String page_num,String page_size,HttpServletRequest request) throws Exception{
        String role_code = request.getSession().getAttribute("role_code").toString();

//        List<TableManager> tableManagers = tableManagerService.selVipScreenValue();
        String brand_code = "";
        String area_code = "";
        String store_code = "";
        String user_code = "";
        String store_code_key = "14";
        String user_code_key = "15";
        JSONArray post_array = new JSONArray();
        for (int i = 0; i < screen.size(); i++) {
            JSONObject screen_obj = screen.getJSONObject(i);
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
                JSONObject post_obj = new JSONObject();
                post_obj.put("key",key);
                post_obj.put("type",type);
                post_obj.put("value",value);
                if (screen_obj.containsKey("date")){
                    post_obj.put("date",screen_obj.getString("date"));
                }
                post_array.add(post_obj);
                //根据key值，找出其对应name
                if (key.equals(store_code_key))
                    store_code = value;
                if (key.equals(user_code_key))
                    user_code = value;
            }
        }
        if (store_code.equals("") && !role_code.equals(Common.ROLE_SYS) && !role_code.equals(Common.ROLE_GM)){
            if ((!area_code.equals("") || !brand_code.equals(""))){
                //若选择了区域和品牌，记住品牌区域下的store_code
                List<Store> storeList = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                for (int i = 0; i < storeList.size(); i++) {
                    store_code = store_code + storeList.get(i).getStore_code() + ",";
                }
                JSONObject post_obj = new JSONObject();
                post_obj.put("key",store_code_key);
                post_obj.put("type","text");
                post_obj.put("value",store_code);
                post_array.add(post_obj);
            }else {
                //没选择区域和品牌，传自身拥有的店铺
                if (role_code.equals(Common.ROLE_BM)){
                    brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,"",brand_code,"","");
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + stores.get(i).getStore_code() + ",";
                    }
                }else if (role_code.equals(Common.ROLE_AM)){
                    String area_code1 = request.getSession().getAttribute("area_code").toString();
                    String area_store_code = request.getSession().getAttribute("store_code").toString();
                    area_code1 = area_code1.replace(Common.SPECIAL_HEAD,"");
                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code,area_code1,"","",area_store_code);
                    for (int i = 0; i < stores.size(); i++) {
                        store_code = store_code + stores.get(i).getStore_code() + ",";
                    }
                }else{
                    store_code = request.getSession().getAttribute("store_code").toString();
                    store_code = store_code.replace(Common.SPECIAL_HEAD,"");
                }
                JSONObject post_obj = new JSONObject();
                post_obj.put("key",store_code_key);
                post_obj.put("type","text");
                post_obj.put("value",store_code);
                post_array.add(post_obj);
            }
        }
        if (role_code.equals(Common.ROLE_STAFF) && user_code.equals("")){
            user_code = request.getSession().getAttribute("user_code").toString();
            JSONObject post_obj = new JSONObject();
            post_obj.put("key",user_code_key);
            post_obj.put("type","text");
            post_obj.put("value",user_code);
            post_array.add(post_obj);
        }

        logger.info("-------VipScreen:" + JSON.toJSONString(post_array));
        DataBox dataBox;
        if (post_array.size()>0) {
            dataBox = iceInterfaceService.vipScreenMethod2(page_num, page_size, corp_code,JSON.toJSONString(post_array));
        }else {
            Map datalist = iceInterfaceService.vipBasicMethod(page_num, page_size, corp_code,request);
            dataBox = iceInterfaceService.iceInterfaceV2("AnalysisAllVip", datalist);
        }
        return dataBox;
    }
}
