package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.service.AreaNewService;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.AreaNewMapper;
import com.bizvane.ishop.dao.CodeUpdateMapper;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.entity.AreaNew;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.service.AreaNewService;
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
 * Created by PC on 2016/10/13.
 */

@Service
public class AreaNewServiceImpl implements AreaNewService{
    @Autowired
    AreaNewMapper areaMapper;
    @Autowired
    StoreMapper storeMapper;

    @Autowired
    CodeUpdateMapper codeUpdateMapper;

    /**
     * 根据区域id
     * 获取某区域信息
     */
    @Override
    public AreaNew getAreaById(int id) throws Exception {
        return areaMapper.selectByAreaId(id);
    }

    /**
     * 分页显示区域
     */
    @Override
    public PageInfo<AreaNew> getAllAreaByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<AreaNew> areas;
        PageHelper.startPage(page_number, page_size);
        areas = areaMapper.selectAllArea(corp_code, search_value);
        for (AreaNew area : areas) {
            area.setIsactive(CheckUtils.CheckIsactive(area.getIsactive()));
        }
        PageInfo<AreaNew> page = new PageInfo<AreaNew>(areas);

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
        AreaNew area = getAreaByCode(corp_code, area_code, Common.IS_ACTIVE_Y);
        AreaNew area1 = getAreaByName(corp_code, area_name, Common.IS_ACTIVE_Y);
        if (area == null && area1 == null) {
            area = new AreaNew();
            Date now = new Date();
            area.setArea_code(area_code);
            area.setArea_name(area_name);
            area.setCorp_code(corp_code);
            area.setCreated_date(Common.DATETIME_FORMAT.format(now));
            area.setCreater(user_id);
            area.setModified_date(Common.DATETIME_FORMAT.format(now));
            area.setModifier(user_id);
            area.setIsactive(jsonObject.get("isactive").toString());
            areaMapper.insertArea(area);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else if (area != null) {
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

        AreaNew old_area = getAreaById(area_id);
        old_area_code = old_area.getArea_code();
        if (old_area.getCorp_code().trim().equals(corp_code)) {
            AreaNew areaByCode = getAreaByCode(corp_code, area_code, Common.IS_ACTIVE_Y);
            AreaNew areaByName = getAreaByName(corp_code, area_code, Common.IS_ACTIVE_Y);
            if (areaByCode != null && areaByCode.getId() != area_id) {
                result = "区域编号已存在";
            } else if (areaByName != null && areaByName.getId() != area_id) {
                result = "区域名称已存在";
            } else {
                old_area = new AreaNew();
                Date now = new Date();
                old_area.setId(area_id);
                old_area.setArea_code(area_code);
                old_area.setArea_name(area_name);
                old_area.setCorp_code(corp_code);
                old_area.setModified_date(Common.DATETIME_FORMAT.format(now));
                old_area.setModifier(user_id);
                old_area.setIsactive(jsonObject.get("isactive").toString());
                if (areaMapper.updateArea(old_area) > 0 && !new_area_code.equals(old_area_code)) {
                    updateAreaCode(corp_code, new_area_code, old_area_code);
                }
                result = Common.DATABEAN_CODE_SUCCESS;
            }
        } else {
            AreaNew areaByCode = getAreaByCode(corp_code, area_code, Common.IS_ACTIVE_Y);
            AreaNew areaByName = getAreaByName(corp_code, area_code, Common.IS_ACTIVE_Y);
            if (areaByCode == null
                    && areaByName == null) {
                old_area = new AreaNew();
                Date now = new Date();
                old_area.setId(area_id);
                old_area.setArea_code(area_code);
                old_area.setArea_name(area_name);
                old_area.setCorp_code(corp_code);
                old_area.setModified_date(Common.DATETIME_FORMAT.format(now));
                old_area.setModifier(user_id);
                old_area.setIsactive(jsonObject.get("isactive").toString());
                areaMapper.updateArea(old_area);
                result = Common.DATABEAN_CODE_SUCCESS;
            } else if (areaByCode != null) {
                result = "区域编号已存在";
            } else {
                result = "区域名称已存在";
            }
        }

//        if ((old_area.getArea_code().equals(area_code) || areaByCode == null)
//                && (old_area.getArea_name().equals(area_name) || areaByName == null)) {
//            old_area = new Area();
//            Date now = new Date();
//            old_area.setId(area_id);
//            old_area.setArea_code(area_code);
//            old_area.setArea_name(area_name);
//            old_area.setCorp_code(corp_code);
//            old_area.setModified_date(Common.DATETIME_FORMAT.format(now));
//            old_area.setModifier(user_id);
//            old_area.setIsactive(jsonObject.get("isactive").toString());
//            if (areaMapper.updateArea(old_area) > 0 && !new_area_code.equals(old_area_code)) {
//                updateAreaCode(corp_code, new_area_code, old_area_code);
//            }
//            result = Common.DATABEAN_CODE_SUCCESS;
//        } else if (!old_area.getArea_code().equals(area_code) && areaByCode != null) {
//            result = "区域编号已存在";
//        } else {
//            result = "区域名称已存在";
//        }
        return result;
    }

    private void updateAreaCode(String corp_code, String new_area_code, String old_area_code) throws Exception {
        codeUpdateMapper.updateUser("", corp_code, "", "", "", "", Common.SPECIAL_HEAD+new_area_code+",", Common.SPECIAL_HEAD+old_area_code+",","","");
        codeUpdateMapper.updateStore("", corp_code, "", "", Common.SPECIAL_HEAD+new_area_code+",", Common.SPECIAL_HEAD+old_area_code+",");
    }

    @Override
    @Transactional
    public int delete(int id) throws Exception {
        return areaMapper.deleteByAreaId(id);
    }

    /**
     * 根据区域编号
     * 获取区域信息
     */
    @Override
    public AreaNew getAreaByCode(String corp_code, String area_code, String isactive) throws Exception {
        return areaMapper.selectAreaByCode(corp_code, area_code, isactive);
    }

    @Override
    public AreaNew getAreaByName(String corp_code, String area_name, String isactive) throws Exception {
        AreaNew area = this.areaMapper.selectAreaByName(corp_code, area_name, isactive);
        return area;
    }

    @Override
    public String insertExecl(AreaNew area) throws Exception {
        areaMapper.insertArea(area);
        return "add success";
    }

    @Override
    public PageInfo<AreaNew> getAllAreaScreen(int page_number, int page_size, String corp_code, String area_codes, Map<String, String> map) throws Exception {
        String[] areaArray = null;
        if (null != area_codes && !area_codes.isEmpty()) {
            areaArray = area_codes.split(",");
            for (int i = 0; areaArray != null && i < areaArray.length; i++) {
                areaArray[i] = areaArray[i].substring(1, areaArray[i].length());
            }
        }
        List<AreaNew> areas;
        PageHelper.startPage(page_number, page_size);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("area_codes", areaArray);
        params.put("corp_code", corp_code);
        params.put("map", map);
        areas = areaMapper.selectAllAreaScreen(params);
        for (AreaNew area : areas) {
            area.setIsactive(CheckUtils.CheckIsactive(area.getIsactive()));
        }
        PageInfo<AreaNew> page = new PageInfo<AreaNew>(areas);
        return page;
    }


    @Override
    public PageInfo<AreaNew> selectByAreaCode(int page_number, int page_size, String corp_code, String area_codes, String search_value) throws Exception {
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
        List<AreaNew> areas = areaMapper.selectByAreaCodeSearch(params);
        for (AreaNew area : areas) {
            area.setIsactive(CheckUtils.CheckIsactive(area.getIsactive()));
        }
        PageInfo<AreaNew> page = new PageInfo<AreaNew>(areas);
        return page;
    }

    @Override
    public List<AreaNew> selectArea(String corp_code, String area_codes) throws SQLException {
        String[] areaArray = null;
        if (null != area_codes && !area_codes.isEmpty()) {
            if (area_codes.contains(Common.SPECIAL_HEAD))
                area_codes = area_codes.replace(Common.SPECIAL_HEAD, "");
            areaArray = area_codes.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("area_codes", areaArray);
        List<AreaNew> areas = areaMapper.selectArea(params);
        return areas;
    }

    @Override
    public PageInfo<AreaNew> selAreaByCorpCode(int page_number, int page_size, String corp_code, String area_codes, String store_code, String search_value) throws SQLException {
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
        List<AreaNew> areas = areaMapper.selAreaByCorpCode(params);
        for (AreaNew area : areas) {
            area.setIsactive(CheckUtils.CheckIsactive(area.getIsactive()));
        }
        PageInfo<AreaNew> page = new PageInfo<AreaNew>(areas);
        return page;
    }

    @Override
    public List<AreaNew> selAreaByCorpCode(String corp_code, String area_codes, String store_code) throws Exception {
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
        List<AreaNew> areas = areaMapper.selAreaByCorpCode(params);

        return areas;
    }

    @Override
    public PageInfo<Store> getAllStoresByCorpCode(int page_number, int page_size, String corp_code, String search_value, String area_code) throws Exception {

        PageHelper.startPage(page_number, page_size);
        List<Store> stores = storeMapper.selectAllStoresByCorpCode(corp_code, search_value);
        PageInfo<Store> page = new PageInfo<Store>(stores);
        List<Store> stores1 = page.getList();
        for (int i = 0; i < stores1.size(); i++) {
            Store store = stores1.get(i);
            if (store.getArea_code() != null) {
                String area = store.getArea_code();
                if (area.contains(Common.SPECIAL_HEAD+area_code+",")){
                    store.setIs_this_area("Y");
                }
                area = area.replace(Common.SPECIAL_HEAD,"");
                if (area.endsWith(","))
                    area = area.substring(0,area.length()-1);
                store.setArea_code(area);
            } else {
                if (store.getArea_code() == null) {
                    store.setArea_code("");
                }
                store.setIs_this_area("N");
            }
        }
        page.setList(stores1);
        return page;


    }

}
