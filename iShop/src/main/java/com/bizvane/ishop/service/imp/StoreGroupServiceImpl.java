package com.bizvane.ishop.service.imp;


import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.StoreGroupMapper;
import com.bizvane.ishop.dao.CodeUpdateMapper;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.entity.StoreGroup;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.StoreGroupService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/6/4.
 */

@Service
public class StoreGroupServiceImpl implements StoreGroupService {
    @Autowired
    StoreGroupMapper storeGroupMapper;
    @Autowired
    StoreMapper storeMapper;

    @Autowired
    CodeUpdateMapper codeUpdateMapper;

    /**
     * 根据区域id
     * 获取某区域信息
     */
    @Override
    public StoreGroup getAreaById(int id) throws Exception {
        return storeGroupMapper.selectByAreaId(id);
    }

    /**
     * 分页显示区域
     */
    @Override
    public PageInfo<StoreGroup> getAllAreaByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<StoreGroup> storeGroups;
        PageHelper.startPage(page_number, page_size);
        storeGroups = storeGroupMapper.selectAllArea(corp_code, search_value);
        for (StoreGroup storeGroup : storeGroups) {
            storeGroup.setIsactive(CheckUtils.CheckIsactive(storeGroup.getIsactive()));
        }
        PageInfo<StoreGroup> page = new PageInfo<StoreGroup>(storeGroups);

        return page;
    }

    @Override
    @Transactional
    public String insert(String message, String user_id) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        String area_code = jsonObject.get("area_code").toString().trim();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String area_name = jsonObject.get("area_name").toString().trim();
        StoreGroup storeGroup = getAreaByCode(corp_code, area_code, Common.IS_ACTIVE_Y);
        StoreGroup storeGroup1 = getAreaByName(corp_code, area_name, Common.IS_ACTIVE_Y);
        if (storeGroup == null && storeGroup1 == null) {
            storeGroup = new StoreGroup();
            Date now = new Date();
            storeGroup.setStore_group_code(area_code);
            storeGroup.setStore_group_name(area_name);
            storeGroup.setCorp_code(corp_code);
            storeGroup.setCreated_date(Common.DATETIME_FORMAT.format(now));
            storeGroup.setCreater(user_id);
            storeGroup.setModified_date(Common.DATETIME_FORMAT.format(now));
            storeGroup.setModifier(user_id);
            storeGroup.setIsactive(jsonObject.get("isactive").toString());
            storeGroupMapper.insertArea(storeGroup);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else if (storeGroup != null) {
            result = "区域编号已存在";
        } else {
            result = "区域名称已存在";
        }
        return result;
    }

    @Override
    @Transactional
    public String update(String message, String user_id) throws Exception {
        String old_area_code = null;
        String new_area_code = null;
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        int area_id = Integer.parseInt(jsonObject.get("id").toString().trim());

        String area_code = jsonObject.get("area_code").toString().trim();
        new_area_code = area_code;
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String area_name = jsonObject.get("area_name").toString().trim();

        StoreGroup old_storeGroup = getAreaById(area_id);
        old_area_code = old_storeGroup.getStore_group_code();
        if (old_storeGroup.getCorp_code().trim().equals(corp_code)) {
            StoreGroup storeGroupByCode = getAreaByCode(corp_code, area_code, Common.IS_ACTIVE_Y);
            StoreGroup storeGroupByName = getAreaByName(corp_code, area_code, Common.IS_ACTIVE_Y);
            if (storeGroupByCode != null && storeGroupByCode.getId() != area_id) {
                result = "区域编号已存在";
            } else if (storeGroupByName != null && storeGroupByName.getId() != area_id) {
                result = "区域名称已存在";
            } else {
                old_storeGroup = new StoreGroup();
                Date now = new Date();
                old_storeGroup.setId(area_id);
                old_storeGroup.setStore_group_code(area_code);
                old_storeGroup.setStore_group_name(area_name);
                old_storeGroup.setCorp_code(corp_code);
                old_storeGroup.setModified_date(Common.DATETIME_FORMAT.format(now));
                old_storeGroup.setModifier(user_id);
                old_storeGroup.setIsactive(jsonObject.get("isactive").toString());
                if (storeGroupMapper.updateArea(old_storeGroup) > 0 && !new_area_code.equals(old_area_code)) {
                    updateAreaCode(corp_code, new_area_code, old_area_code);
                }
                result = Common.DATABEAN_CODE_SUCCESS;
            }
        } else {
            StoreGroup storeGroupByCode = getAreaByCode(corp_code, area_code, Common.IS_ACTIVE_Y);
            StoreGroup storeGroupByName = getAreaByName(corp_code, area_code, Common.IS_ACTIVE_Y);
            if (storeGroupByCode == null
                    && storeGroupByName == null) {
                old_storeGroup = new StoreGroup();
                Date now = new Date();
                old_storeGroup.setId(area_id);
                old_storeGroup.setStore_group_code(area_code);
                old_storeGroup.setStore_group_name(area_name);
                old_storeGroup.setCorp_code(corp_code);
                old_storeGroup.setModified_date(Common.DATETIME_FORMAT.format(now));
                old_storeGroup.setModifier(user_id);
                old_storeGroup.setIsactive(jsonObject.get("isactive").toString());
                storeGroupMapper.updateArea(old_storeGroup);
                result = Common.DATABEAN_CODE_SUCCESS;
            } else if (storeGroupByCode != null) {
                result = "区域编号已存在";
            } else {
                result = "区域名称已存在";
            }
        }

//        if ((old_storeGroup.getStore_group_code().equals(area_code) || areaByCode == null)
//                && (old_storeGroup.getStore_group_name().equals(area_name) || areaByName == null)) {
//            old_storeGroup = new StoreGroup();
//            Date now = new Date();
//            old_storeGroup.setId(area_id);
//            old_storeGroup.setStore_group_code(area_code);
//            old_storeGroup.setStore_group_name(area_name);
//            old_storeGroup.setCorp_code(corp_code);
//            old_storeGroup.setModified_date(Common.DATETIME_FORMAT.format(now));
//            old_storeGroup.setModifier(user_id);
//            old_storeGroup.setIsactive(jsonObject.get("isactive").toString());
//            if (storeGroupMapper.updateArea(old_storeGroup) > 0 && !new_area_code.equals(old_area_code)) {
//                updateAreaCode(corp_code, new_area_code, old_area_code);
//            }
//            result = Common.DATABEAN_CODE_SUCCESS;
//        } else if (!old_storeGroup.getStore_group_code().equals(area_code) && areaByCode != null) {
//            result = "区域编号已存在";
//        } else {
//            result = "区域名称已存在";
//        }
        return result;
    }

    private void updateAreaCode(String corp_code, String new_area_code, String old_area_code) throws Exception {
        codeUpdateMapper.updateUser("", corp_code, "", "", "", "", Common.SPECIAL_HEAD+new_area_code+",", Common.SPECIAL_HEAD+old_area_code+",");
        codeUpdateMapper.updateStore("", corp_code, "", "", Common.SPECIAL_HEAD+new_area_code+",", Common.SPECIAL_HEAD+old_area_code+",");
    }

    @Override
    @Transactional
    public int delete(int id) throws Exception {
        return storeGroupMapper.deleteByAreaId(id);
    }

    /**
     * 根据区域编号
     * 获取区域信息
     */
    @Override
    public StoreGroup getAreaByCode(String corp_code, String area_code, String isactive) throws Exception {
        return storeGroupMapper.selectAreaByCode(corp_code, area_code, isactive);
    }

    @Override
    public StoreGroup getAreaByName(String corp_code, String area_name, String isactive) throws Exception {
        StoreGroup storeGroup = this.storeGroupMapper.selectAreaByName(corp_code, area_name, isactive);
        return storeGroup;
    }

    @Override
    public String insertExecl(StoreGroup storeGroup) throws Exception {
        storeGroupMapper.insertArea(storeGroup);
        return "add success";
    }

    @Override
    public PageInfo<StoreGroup> getAllAreaScreen(int page_number, int page_size, String corp_code, String area_codes, Map<String, String> map) throws Exception {
        String[] areaArray = null;
        if (null != area_codes && !area_codes.isEmpty()) {
            areaArray = area_codes.split(",");
            for (int i = 0; areaArray != null && i < areaArray.length; i++) {
                areaArray[i] = areaArray[i].substring(1, areaArray[i].length());
            }
        }
        List<StoreGroup> storeGroups;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("area_codes", areaArray);
        params.put("corp_code", corp_code);
        params.put("map", map);
        storeGroups = storeGroupMapper.selectAllAreaScreen(params);
        for (StoreGroup storeGroup : storeGroups) {
            storeGroup.setIsactive(CheckUtils.CheckIsactive(storeGroup.getIsactive()));
        }
        PageInfo<StoreGroup> page = new PageInfo<StoreGroup>(storeGroups);
        return page;
    }


    @Override
    public PageInfo<StoreGroup> selectByAreaCode(int page_number, int page_size, String corp_code, String area_codes, String search_value) throws Exception {
        String[] areaArray = null;
        if (null != area_codes && !area_codes.isEmpty()) {
            if (area_codes.contains(Common.SPECIAL_HEAD))
                area_codes = area_codes.replace(Common.SPECIAL_HEAD, "");
            areaArray = area_codes.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_codes", areaArray);
        params.put("search_value", search_value);
        PageHelper.startPage(page_number, page_size);
        List<StoreGroup> storeGroups = storeGroupMapper.selectByAreaCodeSearch(params);
        for (StoreGroup storeGroup : storeGroups) {
            storeGroup.setIsactive(CheckUtils.CheckIsactive(storeGroup.getIsactive()));
        }
        PageInfo<StoreGroup> page = new PageInfo<StoreGroup>(storeGroups);
        return page;
    }

    @Override
    public List<StoreGroup> selectArea(String corp_code, String area_codes) throws SQLException {
        String[] areaArray = null;
        if (null != area_codes && !area_codes.isEmpty()) {
            if (area_codes.contains(Common.SPECIAL_HEAD))
                area_codes = area_codes.replace(Common.SPECIAL_HEAD, "");
                areaArray = area_codes.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_codes", areaArray);
        List<StoreGroup> storeGroups = storeGroupMapper.selectArea(params);
        return storeGroups;
    }

    @Override
    public PageInfo<StoreGroup> selAreaByCorpCode(int page_number, int page_size, String corp_code, String area_codes, String store_code, String search_value) throws SQLException {
        String[] areaArray = null;
        if (null != area_codes && !area_codes.isEmpty()) {
            area_codes = area_codes.replace(Common.SPECIAL_HEAD,"");
            areaArray = area_codes.split(",");
        }

        String[] storeArray = null;
        if (null != store_code && !store_code.isEmpty()) {
            store_code = store_code.replace(Common.SPECIAL_HEAD,"");
            storeArray = store_code.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_codes", areaArray);
        params.put("store_code", storeArray);
        params.put("search_value", search_value);
        PageHelper.startPage(page_number, page_size);
        List<StoreGroup> storeGroups = storeGroupMapper.selAreaByCorpCode(params);
        for (StoreGroup storeGroup : storeGroups) {
            storeGroup.setIsactive(CheckUtils.CheckIsactive(storeGroup.getIsactive()));
        }
        PageInfo<StoreGroup> page = new PageInfo<StoreGroup>(storeGroups);
        return page;
    }

    @Override
    public List<StoreGroup> selAreaByCorpCode(String corp_code, String area_codes, String store_code) throws Exception {
        String[] areaArray = null;
        if (null != area_codes && !area_codes.isEmpty()) {
            area_codes = area_codes.replace(Common.SPECIAL_HEAD,"");
            areaArray = area_codes.split(",");
        }

        String[] storeArray = null;
        if (null != store_code && !store_code.isEmpty()) {
            store_code = store_code.replace(Common.SPECIAL_HEAD,"");
            storeArray = store_code.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_codes", areaArray);
        params.put("store_code", storeArray);
        params.put("search_value", "");
        List<StoreGroup> storeGroups = storeGroupMapper.selAreaByCorpCode(params);

        return storeGroups;
    }

    @Override
    public PageInfo<Store> getAllStoresByCorpCode(int page_number, int page_size, String corp_code, String search_value, String area_code) throws Exception {

        PageHelper.startPage(page_number, page_size);
        List<Store> stores = storeMapper.selectAllStoresByCorpCode(corp_code, search_value);
        PageInfo<Store> page = new PageInfo<Store>(stores);
        List<Store> stores1 = page.getList();
        for (int i = 0; i < stores1.size(); i++) {
            Store store = stores1.get(i);
            if (store.getStore_group_code() != null) {
                String area = store.getStore_group_code();
                if (area.contains(Common.SPECIAL_HEAD+area_code+",")){
                    store.setIs_this_area("Y");
                }
                area = area.replace(Common.SPECIAL_HEAD,"");
                if (area.endsWith(","))
                    area = area.substring(0,area.length()-1);
                store.setStore_group_code(area);
            } else {
                if (store.getStore_group_code() == null) {
                    store.setStore_group_code("");
                }
                store.setIs_this_area("N");
            }
        }
        page.setList(stores1);
        return page;

    }


}
