package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.VipLabelMapper;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.BrandService;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.VipLabelService;
import com.bizvane.ishop.service.ViplableGroupService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.sun.v1.common.Data;
import com.bizvane.sun.v1.common.DataBox;
import com.bizvane.sun.v1.common.ValueType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
@Service
public class VipLabelServiceImpl implements VipLabelService {

    @Autowired
    private VipLabelMapper vipLabelMapper;
    @Autowired
    private ViplableGroupService viplableGroupService;
    @Autowired
    private IceInterfaceService iceInterfaceService;
    @Autowired
    private BrandService brandService;

    @Override
    public VipLabel getVipLabelById(int id) throws Exception {
        VipLabel vipLabel = vipLabelMapper.selectByPrimaryKey(id);
        String lable_g_Code = "";
        if (vipLabel.getLabel_group_code() == null) {
            lable_g_Code = "";
        } else {
            lable_g_Code = vipLabel.getLabel_group_code();
        }
        String[] brand = null;
        String brand_code = vipLabel.getBrand_code();
        String brand_code_json = "";
        String brand_name_json = "";
        if (null != brand_code && !"".equals(brand_code)) {
            if (brand_code.contains("§")) {
                brand_code = brand_code.replace("§", "");
            }
            brand = brand_code.split(",");
            List<Brand> brandList = brandService.selectBrandByLabel(vipLabel.getCorp_code(), brand);
            for (Brand brand1 : brandList) {
                brand_code_json += brand1.getBrand_code() + ",";
                brand_name_json += brand1.getBrand_name() + ",";
            }
        }
        if (brand_code_json.endsWith(",")) {
            brand_code_json = brand_code_json.substring(0, brand_code_json.length() - 1);
        }
        if (brand_name_json.endsWith(",")) {
            brand_name_json = brand_name_json.substring(0, brand_name_json.length() - 1);
        }
        vipLabel.setBrand_code(brand_code_json);
        vipLabel.setBrand_name(brand_name_json);
        List<ViplableGroup> viplableGroups = viplableGroupService.checkCodeOnly(vipLabel.getCorp_code(), lable_g_Code, Common.IS_ACTIVE_Y);
        if (viplableGroups.size() > 0) {
            ViplableGroup viplableGroup = viplableGroups.get(0);
            vipLabel.setLabel_group_code(viplableGroup.getLabel_group_code());
            vipLabel.setLabel_group_name(viplableGroup.getLabel_group_name());
        } else {
            vipLabel.setLabel_group_code("");
            vipLabel.setLabel_group_name("");
        }
        return vipLabel;
    }

    @Override
    public String insert(VipLabel vipLabel) throws Exception {
        if (this.VipLabelNameExist(vipLabel.getCorp_code().trim(), vipLabel.getLabel_name().trim(), vipLabel.getBrand_code()).size() > 0) {
            return "名称已经存在";
        } else if (vipLabelMapper.insert(vipLabel) >= 0) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public int delete(int id) throws Exception {
        return vipLabelMapper.deleteByPrimaryKey(id);
    }

    @Override
    public String update(VipLabel vipLabel) throws Exception {
        VipLabel old = this.vipLabelMapper.selectByPrimaryKey(vipLabel.getId());
        if (old.getCorp_code().trim().equals(vipLabel.getCorp_code().trim())) {
            if (!old.getLabel_name().equals(vipLabel.getLabel_name()) && (this.VipLabelNameExist(vipLabel.getCorp_code(), vipLabel.getLabel_name(), vipLabel.getBrand_code()).size() > 0)) {
                return "标签名称已存在";
            } else if (this.vipLabelMapper.updateByPrimaryKey(vipLabel) >= 0) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        } else {
            if (this.VipLabelNameExist(vipLabel.getCorp_code().trim(), vipLabel.getLabel_name().trim(), vipLabel.getBrand_code()).size() > 0) {
                return "标签名称已存在";
            } else if (this.vipLabelMapper.updateByPrimaryKey(vipLabel) >= 0) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public PageInfo<VipLabel> selectBySearch(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<VipLabel> list = null;
        PageHelper.startPage(page_number, page_size);
        list = vipLabelMapper.selectAllVipLabel(corp_code, search_value);
        for (VipLabel vipLabel : list) {
            vipLabel.setIsactive(CheckUtils.CheckIsactive(vipLabel.getIsactive()));
            vipLabel.setCountlable(vipLabel.getCount());
//            VipLabel vipLabel1 = countLable(vipLabel.getCorp_code(), vipLabel.getId() + "");
//            if (vipLabel1 == null) {
//                vipLabel.setCountlable("0");
//            } else {
//                vipLabel.setCountlable(vipLabel1.getCountlable());
//            }
//            String lable_g_Code = "";
//            if (vipLabel.getLabel_group_code() == null) {
//                lable_g_Code = "";
//            } else {
//                lable_g_Code = vipLabel.getLabel_group_code();
//            }
//            List<ViplableGroup> viplableGroups = viplableGroupService.checkCodeOnly(vipLabel.getCorp_code(), lable_g_Code, Common.IS_ACTIVE_Y);
//            if (viplableGroups.size() > 0) {
//                ViplableGroup viplableGroup = viplableGroups.get(0);
//                vipLabel.setLabel_group_code(viplableGroup.getLabel_group_code());
//                vipLabel.setLabel_group_name(viplableGroup.getLabel_group_name());
//            } else {
//                vipLabel.setLabel_group_code("");
//                vipLabel.setLabel_group_name("");
//            }
            if (vipLabel.getLabel_type() == null || vipLabel.getLabel_type().equals("")) {
                vipLabel.setLabel_type("");
            } else if (vipLabel.getLabel_type().equals("user")) {
                vipLabel.setLabel_type("用户");
            } else if (vipLabel.getLabel_type().equals("sys")) {
                vipLabel.setLabel_type("系统");
            } else if (vipLabel.getLabel_type().equals("org")) {
                vipLabel.setLabel_type("企业");
            }

            String[] brand = null;
            String brand_code = vipLabel.getBrand_code();
            String brand_code_json = "";
            String brand_name_json = "";
            if (null != brand_code && !"".equals(brand_code)) {
                if (brand_code.contains("§")) {
                    brand_code = brand_code.replace("§", "");
                }
                brand = brand_code.split(",");
                List<Brand> brandList = brandService.selectBrandByLabel(vipLabel.getCorp_code(), brand);
                for (Brand brand1 : brandList) {
                    brand_code_json += brand1.getBrand_code() + ",";
                    brand_name_json += brand1.getBrand_name() + ",";
                }
            }
            if (brand_code_json.endsWith(",")) {
                brand_code_json = brand_code_json.substring(0, brand_code_json.length() - 1);
            }
            if (brand_name_json.endsWith(",")) {
                brand_name_json = brand_name_json.substring(0, brand_name_json.length() - 1);
            }
            vipLabel.setBrand_code(brand_code_json);
            vipLabel.setBrand_name(brand_name_json);

        }
        PageInfo<VipLabel> page = new PageInfo<VipLabel>(list);
        return page;
    }

    @Override
    public PageInfo<VipLabel> selectAllVipScreen(int page_number, int page_size, String corp_code, Map<String, String> map, String[] brand) throws Exception {
        map.remove("brand_name");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        params.put("corp_code", corp_code);
        params.put("brandList", brand);
        PageHelper.startPage(page_number, page_size);
        List<VipLabel> labels = vipLabelMapper.selectAllViplabelScreen(params);
        for (VipLabel vipLabel : labels) {
            vipLabel.setIsactive(CheckUtils.CheckIsactive(vipLabel.getIsactive()));
            vipLabel.setCountlable(vipLabel.getCount());
//            VipLabel vipLabel1 = countLable(vipLabel.getCorp_code(), vipLabel.getId() + "");
//            if (vipLabel1 == null) {
//                vipLabel.setCountlable("0");
//            } else {
//                vipLabel.setCountlable(vipLabel1.getCountlable());
//            }
//            String lable_g_Code = "";
//            if (vipLabel.getLabel_group_code() == null) {
//                lable_g_Code = "";
//            } else {
//                lable_g_Code = vipLabel.getLabel_group_code();
//            }
//            List<ViplableGroup> viplableGroups = viplableGroupService.checkCodeOnly(vipLabel.getCorp_code(), lable_g_Code, Common.IS_ACTIVE_Y);
//            if (viplableGroups.size() > 0) {
//                ViplableGroup viplableGroup = viplableGroups.get(0);
//                vipLabel.setLabel_group_code(viplableGroup.getLabel_group_code());
//                vipLabel.setLabel_group_name(viplableGroup.getLabel_group_name());
//            } else {
//                vipLabel.setLabel_group_code("");
//                vipLabel.setLabel_group_name("");
//            }
            if (vipLabel.getLabel_type() == null || vipLabel.getLabel_type().equals("")) {
                vipLabel.setLabel_type("");
            } else if (vipLabel.getLabel_type().equals("user")) {
                vipLabel.setLabel_type("用户");
            } else if (vipLabel.getLabel_type().equals("sys")) {
                vipLabel.setLabel_type("系统");
            } else if (vipLabel.getLabel_type().equals("org")) {
                vipLabel.setLabel_type("企业");
            }

            String[] brand_len = null;
            String brand_code = vipLabel.getBrand_code();
            String brand_code_json = "";
            String brand_name_json = "";
            if (null != brand_code && !"".equals(brand_code)) {
                if (brand_code.contains("§")) {
                    brand_code = brand_code.replace("§", "");
                }
                brand_len = brand_code.split(",");
                List<Brand> brandList = brandService.selectBrandByLabel(vipLabel.getCorp_code(), brand_len);
                for (Brand brand1 : brandList) {
                    brand_code_json += brand1.getBrand_code() + ",";
                    brand_name_json += brand1.getBrand_name() + ",";
                }
            }
            if (brand_code_json.endsWith(",")) {
                brand_code_json = brand_code_json.substring(0, brand_code_json.length() - 1);
            }
            if (brand_name_json.endsWith(",")) {
                brand_name_json = brand_name_json.substring(0, brand_name_json.length() - 1);
            }
            vipLabel.setBrand_code(brand_code_json);
            vipLabel.setBrand_name(brand_name_json);

        }
        PageInfo<VipLabel> page = new PageInfo<VipLabel>(labels);
        return page;

    }

    @Override
    public List<VipLabel> VipLabelNameExist(String corp_code, String tag_name, String brand_code) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        if (null != brand_code && !"".equals(brand_code)) {
            String[] brand = null;
            if (brand_code.contains("§")) {
                brand_code = brand_code.replace("§", "");
            }
            brand = brand_code.split(",");
            for (int i = 0; i < brand.length; i++) {
                brand[i] = Common.SPECIAL_HEAD + brand[i] + ",";
            }
            params.put("brandList", brand);
        } else {
            params.put("brandList", null);
        }
        params.put("corp_code", corp_code);
        params.put("label_name", tag_name);
        return vipLabelMapper.selectVipLabelName(params);
    }

    @Override
    public VipLabel countLable(String corp_code, String label_id) throws Exception {
        return vipLabelMapper.countLable(corp_code, label_id);
    }

    @Override
    public List<VipLabel> lableList(String corp_code, String label_group_code) throws Exception {
        return vipLabelMapper.lableList(corp_code, label_group_code);
    }

    @Override
    public List<VipLabel> selectLabelByVip(String corp_code, String vip_code) throws Exception {
        return vipLabelMapper.selectLabelByVip(corp_code, vip_code);
    }


    @Override
    public List<VipLabel> selectLabelByVipToHbase(String corp_code, String vip_code) throws Exception {
        List<VipLabel> vipLabels = new ArrayList<VipLabel>();
        Map datalist = new HashMap<String, Data>();
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_code = new Data("vip_id", vip_code, ValueType.PARAM);

        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_code.key, data_vip_code);

        DataBox dataBox = iceInterfaceService.iceInterfaceV3("GetLabelByVip", datalist);
        String result = dataBox.data.get("message").value;
        if (result != null) {
            JSONObject object = JSONObject.parseObject(result);
            String labels = object.getString("labels");
            JSONArray array = JSONArray.parseArray(labels);
            for (int i = 0; i < array.size(); i++) {
                VipLabel vipLabel = new VipLabel();
                JSONObject jsonObject = array.getJSONObject(i);
                String id = jsonObject.getString("ID");
                String label_name = jsonObject.getString("LABEL_NAME");
                String label_type = jsonObject.getString("LABEL_TYPE");
                vipLabel.setId(Integer.parseInt(id));
                vipLabel.setLabel_name(label_name);
                vipLabel.setLabel_type(label_type);
                vipLabels.add(vipLabel);
            }
        }
        return vipLabels;
    }

    @Override
    public List<VipLabel> findHotViplabel(String corp_code, String brand_code) throws Exception {
        String[] brand = null;
        if (!brand_code.equals("")) {
            if (brand_code.contains("§")) {
                brand_code = brand_code.replace("§", "");
            }
            brand = brand_code.split(",");
            for (int i = 0; i < brand.length; i++) {
                brand[i] = Common.SPECIAL_HEAD + brand[i] + ",";
            }
        }
        return vipLabelMapper.findHotViplabel(corp_code, brand);
    }

    @Override
    public PageInfo<VipLabel> findViplabelByType(int page_number, int page_size, String corp_code, String label_type, String search_value, String brand_code) throws Exception {
        PageHelper.startPage(page_number, page_size);
        String[] brand = null;
        if (!brand_code.equals("")) {
            if (brand_code.contains("§")) {
                brand_code = brand_code.replace("§", "");
            }
            brand = brand_code.split(",");
            for (int i = 0; i < brand.length; i++) {
                brand[i] = Common.SPECIAL_HEAD + brand[i] + ",";
            }
        }
        List<VipLabel> viplabels = vipLabelMapper.findViplabelByType(corp_code, label_type, search_value, brand);
        PageInfo<VipLabel> page = new PageInfo<VipLabel>(viplabels);
        return page;
    }

    @Override
    public List<RelViplabel> checkRelViplablel(String corp_code, String vip_code, String label_id) throws Exception {
        return vipLabelMapper.checkRelViplablel(corp_code, vip_code, label_id);
    }


    @Override
    public int checkRelViplablelToHbase(String corp_code, String vip_code, String label_id) throws Exception {
        int i=0;
        List<VipLabel> vipLabels = selectLabelByVipToHbase(corp_code, vip_code);
        for (VipLabel vipLabel:vipLabels) {
            if((vipLabel.getId()+"").equals(label_id)){
                i=1;
                break;
            }
        }
       return i;
    }

    @Override
    public int delRelViplabel(String rid) throws Exception {
        return vipLabelMapper.delRelViplabel(rid);
    }

    @Override
    public int delAllRelViplabel(String label_id) throws Exception {
        return vipLabelMapper.delAllRelViplabel(label_id);
    }

    @Override
    public int addRelViplabel(RelViplabel relViplabel) throws Exception {
        return vipLabelMapper.addRelViplabel(relViplabel);
    }

    @Override
    public String addRelViplabel(String label_id, String corp_code, String vip_code, String store_code, String user_id) throws Exception {
        String result = "";
        Date date = new Date();
        RelViplabel relViplabel = new RelViplabel();
        relViplabel.setLabel_id(label_id);
        relViplabel.setCorp_code(corp_code);
        relViplabel.setVip_code(vip_code);
        relViplabel.setStore_code(store_code);
        //------------操作日期-------------
        relViplabel.setCreated_date(Common.DATETIME_FORMAT.format(date));
        relViplabel.setCreater(user_id);
        relViplabel.setModified_date(Common.DATETIME_FORMAT.format(date));
        relViplabel.setModifier(user_id);

        DataBox dataBox = iceInterfaceService.getVipInfo(corp_code, vip_code);
        if (dataBox.status.toString().equals("SUCCESS")) {
            String message1 = dataBox.data.get("message").value;
            JSONObject msg_obj = JSONObject.parseObject(message1);
            JSONArray vip_info = msg_obj.getJSONArray("vip_info");
            JSONObject vip_obj = vip_info.getJSONObject(0);
            relViplabel.setVip_name(vip_obj.getString("vip_name"));
            relViplabel.setVip_card_no(vip_obj.getString("cardno"));
        }
        int i = addRelViplabel(relViplabel);
        String id1 = "";
        if (i > 0) {
            List<RelViplabel> relViplabels1 = checkRelViplablel(corp_code, vip_code, label_id);
            id1 = String.valueOf(relViplabels1.get(0).getId());
            result = id1;
        }
        return result;
    }


    @Override
    public String addRelViplabelToHbase(String label_id, String corp_code, String vip_code, String store_code, String user_id) throws Exception {
        Map datalist = new HashMap<String, Data>();
        Data data_corp_code = new Data("corp_code", corp_code, ValueType.PARAM);
        Data data_vip_code = new Data("vip_id", vip_code, ValueType.PARAM);
        Data data_label_id = new Data("label_id", label_id, ValueType.PARAM);

        datalist.put(data_corp_code.key, data_corp_code);
        datalist.put(data_vip_code.key, data_vip_code);
        datalist.put(data_label_id.key, data_label_id);


        DataBox dataBox = iceInterfaceService.iceInterfaceV3("AddLabelByVip", datalist);
        String result = dataBox.data.get("message").value;
        //=================================
        Data data_user_id= new Data("user_id", user_id, ValueType.PARAM);
        datalist.put(data_user_id.key, data_user_id);
        Data data_type= new Data("type", "新增", ValueType.PARAM);
        datalist.put(data_type.key, data_type);
        DataBox dataBox2 = iceInterfaceService.iceInterfaceV3("AddLabelToMongo", datalist);
        return result;
    }



    @Override
    public List<VipLabel> findViplabelID(String corp_code, String label_name) throws Exception {
        return vipLabelMapper.findViplabelID(corp_code, label_name, Common.IS_ACTIVE_Y);
    }

    @Override
    public List<VipLabel> selectViplabelByName(String corp_code, String label_name, String isactive) throws Exception {
        return vipLabelMapper.findViplabelID(corp_code, label_name, isactive);
    }

    @Override
    public List<VipLabel> selectViplabelByName(String corp_code, String vips) throws Exception {
        String[] vips1 = vips.split(",");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("vips", vips1);
        return vipLabelMapper.selectVipsLabel(params);
    }
}
