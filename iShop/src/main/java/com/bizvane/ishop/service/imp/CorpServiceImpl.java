package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.AreaMapper;
import com.bizvane.ishop.dao.CodeUpdateMapper;
import com.bizvane.ishop.dao.CorpMapper;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.CorpWechat;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.CorpService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by Administrator on 2016/5/23.
 */
@Service
public class CorpServiceImpl implements CorpService {
    @Autowired
    private CorpMapper corpMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CodeUpdateMapper codeUpdateMapper;

    @Autowired
    private AreaMapper areaMapper;

    public Corp selectByCorpId(int corp_id, String corp_code, String isactive) throws Exception {
        Corp corp = corpMapper.selectByCorpId(corp_id, corp_code, isactive);
        if (corp != null) {
            List<JSONObject> array_user = new ArrayList<JSONObject>();
            String cus_user_code = corp.getCus_user_code();
            if (cus_user_code != null && !cus_user_code.equals("")) {
                String[] cus_user_codes = cus_user_code.split(",");
                for (int i = 0; i < cus_user_codes.length; i++) {
                    String user_code = cus_user_codes[i];
                    List<User> user = userMapper.selectUserCode(user_code, corp.getCorp_code(), Common.IS_ACTIVE_Y);
                    if (user.size() > 0) {
                        JSONObject userObj = new JSONObject();
                        userObj.put("cus_user_code", user_code);
                        userObj.put("cus_user_name", user.get(0).getUser_name());
                        array_user.add(userObj);
                    }
                }
            }
            corp.setCus_user(array_user);
        }
        return corp;
    }

    @Transactional
    public String insert(String message, String user_id) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = JSONObject.parseObject(message);
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String corp_name = jsonObject.get("corp_name").toString().trim();
        Corp corp = selectByCorpId(0, corp_code, Common.IS_ACTIVE_Y);
        String exist = getCorpByCorpName(corp_name, Common.IS_ACTIVE_Y);
        if (corp == null && exist.equals(Common.DATABEAN_CODE_SUCCESS)) {
            corp = new Corp();
            corp.setCorp_code(corp_code);
            corp.setCorp_name(corp_name);
//            corp.setAvatar(jsonObject.get("avatar").toString().trim());
            corp.setAddress(jsonObject.get("address").toString().trim());
            corp.setUse_offline(jsonObject.get("use_offline").toString().trim());
            corp.setContact(jsonObject.get("contact").toString().trim());
            corp.setContact_phone(jsonObject.get("phone").toString().trim());
            if (jsonObject.containsKey("cus_user_code")){
                corp.setCus_user_code(jsonObject.get("cus_user_code").toString().trim());
            }
            JSONArray wechat = JSONArray.parseArray(jsonObject.get("wechat").toString().trim());
            for (int i = 0; i < wechat.size(); i++) {
                JSONObject object = JSONObject.parseObject(wechat.get(i).toString().trim());
                String app_id = object.get("app_id").toString().trim();
                String access_key ="";
                if(null!=object.get("access_key")){
                    access_key = object.get("access_key").toString().trim();
                }
                if (!app_id.equals("")) {
                    CorpWechat corpWechat = getCorpByAppId("",app_id);
                    if (corpWechat == null) {
                        corpWechat = new CorpWechat();
                        corpWechat.setApp_id(app_id);
                        corpWechat.setApp_name(object.get("app_name").toString().trim());
                        corpWechat.setCorp_code(corp_code);
                        corpWechat.setIs_authorize(Common.IS_AUTHORIZE_N);
                        Date now = new Date();
                        corpWechat.setCreated_date(Common.DATETIME_FORMAT.format(now));
                        corpWechat.setCreater(user_id);
                        corpWechat.setModified_date(Common.DATETIME_FORMAT.format(now));
                        corpWechat.setModifier(user_id);
                        corpWechat.setIsactive(Common.IS_ACTIVE_Y);
                        corpWechat.setAccess_key(access_key);
                        corpMapper.insertCorpWechat(corpWechat);
                    } else {
                        result = "app_id" + app_id + "已存在";
                        return result;
                    }
                }
            }
            Date now = new Date();
            corp.setCreated_date(Common.DATETIME_FORMAT.format(now));
            corp.setCreater(user_id);
            corp.setModified_date(Common.DATETIME_FORMAT.format(now));
            corp.setModifier(user_id);
            corp.setIsactive(jsonObject.get("isactive").toString().trim());
            corpMapper.insertCorp(corp);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else if (corp != null) {
            result = "企业编号已存在";
        } else {
            result = "企业名称已存在";
        }
        return result;
    }

    @Transactional
    public String update(String message, String user_id) throws Exception {
        String new_code = null;
        String old_code = null;
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = JSONObject.parseObject(message);
        int corp_id = Integer.parseInt(jsonObject.get("id").toString().trim());

        String corp_code = jsonObject.get("corp_code").toString().trim();
        new_code = corp_code;
        String corp_name = jsonObject.get("corp_name").toString().trim();

        Corp old_corp = selectByCorpId(corp_id, "", "");
        old_code = old_corp.getCorp_code();
        Corp corp1 = selectByCorpId(0, corp_code, Common.IS_ACTIVE_Y);
        String exist = getCorpByCorpName(corp_name, Common.IS_ACTIVE_Y);

        if ((old_corp.getCorp_code().equals(corp_code) || corp1 == null)
                && (old_corp.getCorp_name().equals(corp_name) || exist.equals(Common.DATABEAN_CODE_SUCCESS))) {
            old_corp = new Corp();
            old_corp.setId(corp_id);
            old_corp.setCorp_code(corp_code);
            old_corp.setCorp_name(corp_name);
            old_corp.setAddress(jsonObject.get("address").toString().trim());
            old_corp.setUse_offline(jsonObject.get("use_offline").toString().trim());
            old_corp.setContact(jsonObject.get("contact").toString().trim());
            old_corp.setContact_phone(jsonObject.get("phone").toString().trim());
            old_corp.setAvatar(jsonObject.get("avatar").toString().trim());
            if (jsonObject.containsKey("cus_user_code")){
                old_corp.setCus_user_code(jsonObject.get("cus_user_code").toString().trim());
            }
            Date now = new Date();
            JSONArray wechat = JSONArray.parseArray(jsonObject.get("wechat").toString().trim());
            result = updateCorpWechat(wechat,corp_code,user_id);
            if (!result.equals(Common.DATABEAN_CODE_SUCCESS)){
                return result;
            }
            old_corp.setIsactive(jsonObject.get("isactive").toString().trim());
            old_corp.setModified_date(Common.DATETIME_FORMAT.format(now));
            old_corp.setModifier(user_id);

            if (corpMapper.updateByCorpId(old_corp) > 0 && (!new_code.equals(old_code))) {
                updateCorpcode(old_code, new_code);
            }
            result = Common.DATABEAN_CODE_SUCCESS;
        } else if (!old_corp.getCorp_code().equals(corp_code) || corp1 != null) {
            result = "企业编号已存在";
        } else {
            result = "企业名称已存在";
        }
        return result;
    }

    /**
     * 更改其他表中的corp_code
     * def_app_version
     * def_area
     * def_brand
     * def_cache
     * def_corp
     * def_corp_cfg
     *
     * @param old_corp_code
     * @param new_corp_code
     */
    @Transactional
    public void updateCorpcode(String old_corp_code, String new_corp_code) throws Exception {
        if (old_corp_code.equals(new_corp_code)) {
            return;
        }
        codeUpdateMapper.updateRelCorpWechat(new_corp_code, old_corp_code);

        codeUpdateMapper.updateAppVersion(new_corp_code, old_corp_code);
        codeUpdateMapper.updateGoods(new_corp_code, old_corp_code, "", "");
        codeUpdateMapper.updateGroup(new_corp_code, old_corp_code, "", "");
        codeUpdateMapper.updateInterface(new_corp_code, old_corp_code);
        //   codeUpdateMapper.updateMessage(new_corp_code, old_corp_code);
        codeUpdateMapper.updatePraise(new_corp_code, old_corp_code, "", "");
        codeUpdateMapper.updateSmsTemplate(new_corp_code, old_corp_code);
        codeUpdateMapper.updateStaffDetailInfo(new_corp_code, old_corp_code, "", "", "", "");
        codeUpdateMapper.updateStaffMoveLog(new_corp_code, old_corp_code, "", "");
        codeUpdateMapper.updateStore(new_corp_code, old_corp_code, "", "", "", "");
        codeUpdateMapper.updateStoreAchvGoal(new_corp_code, old_corp_code, "", "");
        codeUpdateMapper.updateUser(new_corp_code, old_corp_code, "", "", "", "", "", "","","");
        codeUpdateMapper.updateUserAchvGoal(new_corp_code, old_corp_code, "", "", "", "");
        codeUpdateMapper.updateUserMessage(new_corp_code, old_corp_code);
        codeUpdateMapper.updateVipAlbum(new_corp_code, old_corp_code, "", "");
        codeUpdateMapper.updateVipMessage(new_corp_code, old_corp_code, "", "", "", "");
        codeUpdateMapper.updateVipRecord(new_corp_code, old_corp_code, "", "", "", "");
        codeUpdateMapper.updateVipRecordType(new_corp_code, old_corp_code);
        codeUpdateMapper.updateVipLabel(new_corp_code, old_corp_code);
    }

    @Transactional
    public int deleteByCorpId(int id) throws Exception {
        return corpMapper.deleteByCorpId(id);
    }

    /**
     * 分页显示所有企业
     */
    public PageInfo<Corp> selectAllCorp(int page_number, int page_size, String search_value) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<Corp> corps = corpMapper.selectAllCorp(search_value,null);
        for (Corp corp : corps) {
            corp.setIsactive(CheckUtils.CheckIsactive(corp.getIsactive()));
        }
        PageInfo<Corp> page = new PageInfo<Corp>(corps);
        return page;
    }

    /**
     * 分页显示所有企业
     */
    public PageInfo<Corp> selectAllCorp(int page_number, int page_size, String search_value,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        PageHelper.startPage(page_number, page_size);
        List<Corp> corps = corpMapper.selectAllCorp(search_value,manager_corp_arr);
        for (Corp corp : corps) {
            corp.setIsactive(CheckUtils.CheckIsactive(corp.getIsactive()));
        }
        PageInfo<Corp> page = new PageInfo<Corp>(corps);
        return page;
    }

    @Override
    public String insertExecl(Corp corp) throws Exception {
        corpMapper.insertCorp(corp);
        return "add success";
    }

    @Override
    public PageInfo<Corp> selectAllCorpScreen(int page_number, int page_size, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<Corp> list = corpMapper.selectAllCorpScreen(params);
        for (Corp corp : list) {
            corp.setIsactive(CheckUtils.CheckIsactive(corp.getIsactive()));
        }
        PageInfo<Corp> page = new PageInfo<Corp>(list);
        return page;
    }

    /**
     * 显示所有企业
     */
    public List<Corp> selectAllCorp() throws Exception {
        List<Corp> list = corpMapper.selectCorps("");
        return list;
    }


    /**
     * 校验企业名称是否唯一
     *
     * @param corp_name
     * @return
     * @throws SQLException
     */
    @Override
    public String getCorpByCorpName(String corp_name, String isactive) throws Exception {
        List<Corp> corps = corpMapper.selectByCorpName(corp_name, isactive);
        if (corps == null || corps.size() < 1) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public int getAreaCount(String corp_code) throws Exception {
        return this.corpMapper.getAreaCount(corp_code);
    }

    @Override
    public int getBranCount(String corp_code) throws Exception {
        return this.corpMapper.getBrandCount(corp_code);
    }

    @Override
    public int getGoodCount(String corp_code) throws Exception {
        return this.corpMapper.getGoodCount(corp_code);
    }

    @Override
    public int getGroupCount(String corp_code) throws Exception {
        return corpMapper.getGroupCount(corp_code);
    }

    @Override
    public int getGoodsCount(String corp_code) throws Exception {
        return corpMapper.getGoodCount(corp_code);
    }

    @Override
    public int getMessagesTypeCount(String corp_code) throws Exception {
        return corpMapper.getMessageTypeCount(corp_code);
    }

    public int selectCount(String create_date) throws Exception {
        return corpMapper.selectCount(create_date);
    }

    public CorpWechat getCorpByAppUserName(String app_user_name) throws Exception {
        return corpMapper.selectWByAppUserName(app_user_name);
    }

    @Override
    public CorpWechat getCorpByApp(String app_id) throws Exception {
        return corpMapper.selectWByApp(app_id);
    }


    public CorpWechat getCorpByAppId(String corp_code,String app_id) throws Exception {
        return corpMapper.selectWByAppId(corp_code,app_id);
    }

    public List<CorpWechat> getWByCorp(String corp_code) throws Exception {
        return corpMapper.selectWByCorp(corp_code);
    }

    public List<CorpWechat> getWAuthByCorp(String corp_code) throws Exception {
        return corpMapper.selectWAuthByCorp(corp_code);
    }
    public List<CorpWechat> selectWByCorpBrand(String corp_code,String brand_codes) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        String[] brand_code = brand_codes.split(",");
        for (int i = 0; i < brand_code.length; i++) {
            brand_code[i] = brand_code[i]+",";
        }
        params.put("corp_code",corp_code);
        params.put("brand_code",brand_code);
        return corpMapper.selectWByCorpBrand(params);
    }

    public int deleteCorpWechat(String app_id,String corp_code) throws Exception{
        return corpMapper.deleteCorpWechat(app_id,corp_code);
    }

    public String updateCorpWechat(JSONArray wechat,String corp_code,String user_code) throws Exception{
        String app_ids = "";
        String result = Common.DATABEAN_CODE_SUCCESS;
        Date now = new Date();
        for (int i = 0; i < wechat.size(); i++) {
            JSONObject object = JSONObject.parseObject(wechat.get(i).toString());
            String app_id = object.get("app_id").toString();
            String access_key ="";
            if(null!=object.get("access_key")){
                access_key = object.get("access_key").toString().trim();
            }
            if (!app_id.equals("")) {
                app_ids = app_ids + app_id + ",";
                CorpWechat corpWechat = getCorpByAppId("",app_id);
                if (corpWechat == null) {
                    corpWechat = new CorpWechat();
                    corpWechat.setApp_id(app_id);
                    corpWechat.setApp_name(object.get("app_name").toString());
                    corpWechat.setCorp_code(corp_code);
                    corpWechat.setIs_authorize(Common.IS_AUTHORIZE_N);
                    corpWechat.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    corpWechat.setCreater(user_code);
                    corpWechat.setModified_date(Common.DATETIME_FORMAT.format(now));
                    corpWechat.setModifier(user_code);
                    corpWechat.setIsactive(Common.IS_ACTIVE_Y);
                    corpWechat.setAccess_key(access_key);
                    corpMapper.insertCorpWechat(corpWechat);
                } else {
                    if (corpWechat.getCorp_code().equals(corp_code)) {
                        corpWechat.setApp_name(object.get("app_name").toString());
                        corpWechat.setModified_date(Common.DATETIME_FORMAT.format(now));
                        corpWechat.setModifier(user_code);
                        corpWechat.setAccess_key(access_key);
                        corpMapper.updateCorpWechat(corpWechat);
                    } else {
                        result = "app_id:" + app_id + "已存在";
                        return result;
                    }
                }
            }
        }
        List<CorpWechat> corpWechats = corpMapper.selectWByCorp(corp_code);
        for (int i = 0; i < corpWechats.size(); i++) {
            String ids = corpWechats.get(i).getApp_id();
            if (!app_ids.contains(ids+",")) {
                corpMapper.deleteCorpWechat(ids,"");
            }
        }
        return result;
    }
}
