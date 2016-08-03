package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.AreaMapper;
import com.bizvane.ishop.dao.CodeUpdateMapper;
import com.bizvane.ishop.dao.CorpMapper;
import com.bizvane.ishop.entity.Appversion;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.service.CorpService;
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
 * Created by Administrator on 2016/5/23.
 */
@Service
public class CorpServiceImpl implements CorpService {
    @Autowired
    private CorpMapper corpMapper;

    @Autowired
    private CodeUpdateMapper codeUpdateMapper;

    @Autowired
    private AreaMapper areaMapper;

    public Corp selectByCorpId(int corp_id, String corp_code) throws SQLException {
        return corpMapper.selectByCorpId(corp_id, corp_code);
    }

    @Transactional
    public String insert(String message, String user_id) throws SQLException {

        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        String corp_code = jsonObject.get("corp_code").toString();
        String corp_name = jsonObject.get("corp_name").toString();
        Corp corp = selectByCorpId(0, corp_code);
        String exist = getCorpByCorpName(corp_name);
        if (corp == null && exist.equals(Common.DATABEAN_CODE_SUCCESS)) {
            corp = new Corp();

            corp.setCorp_code(corp_code);
            corp.setCorp_name(corp_name);
            corp.setAddress(jsonObject.get("address").toString());
            corp.setContact(jsonObject.get("contact").toString());
            corp.setContact_phone(jsonObject.get("phone").toString());
            corp.setApp_id(jsonObject.get("app_id").toString());
            corp.setIs_authorize("N");
            Date now = new Date();
            corp.setCreated_date(Common.DATETIME_FORMAT.format(now));
            corp.setCreater(user_id);
            corp.setModified_date(Common.DATETIME_FORMAT.format(now));
            corp.setModifier(user_id);
            corp.setIsactive(jsonObject.get("isactive").toString());
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
    public String update(String message, String user_id) throws SQLException {
        String new_code = null;
        String old_code = null;
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        int corp_id = Integer.parseInt(jsonObject.get("id").toString());

        String corp_code = jsonObject.get("corp_code").toString();
        new_code = corp_code;
        String corp_name = jsonObject.get("corp_name").toString();

        Corp old_corp = selectByCorpId(corp_id, "");
        old_code = old_corp.getCorp_code();
        Corp corp1 = selectByCorpId(0, corp_code);
        String exist = getCorpByCorpName(corp_name);

        if ((old_corp.getCorp_code().equals(corp_code) || corp1 == null)
                && (old_corp.getCorp_name().equals(corp_name) || exist.equals(Common.DATABEAN_CODE_SUCCESS))) {
            old_corp = new Corp();
            old_corp.setId(corp_id);
            old_corp.setCorp_code(corp_code);
            old_corp.setCorp_name(corp_name);
            old_corp.setAddress(jsonObject.get("address").toString());
            old_corp.setContact(jsonObject.get("contact").toString());
            old_corp.setContact_phone(jsonObject.get("phone").toString());
            old_corp.setAvater(jsonObject.get("avater").toString());
            old_corp.setApp_id(jsonObject.get("app_id").toString());
            old_corp.setIsactive(jsonObject.get("isactive").toString());
            Date now = new Date();
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
    private void updateCorpcode(String old_corp_code, String new_corp_code) {
        if (old_corp_code.equals(new_corp_code)) {
            return;
        }
        codeUpdateMapper.updateAppVersion(new_corp_code, old_corp_code);
        //   codeUpdateMapper.updateCache(new_corp_code, old_corp_code);
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
        codeUpdateMapper.updateUser(new_corp_code, old_corp_code, "", "", "", "", "", "");
        codeUpdateMapper.updateUserAchvGoal(new_corp_code, old_corp_code, "", "", "", "");
        codeUpdateMapper.updateUserMessage(new_corp_code, old_corp_code);
        codeUpdateMapper.updateVipAlbum(new_corp_code, old_corp_code, "", "");
        codeUpdateMapper.updateVipMessage(new_corp_code, old_corp_code, "", "", "", "");
        codeUpdateMapper.updateVipRecord(new_corp_code, old_corp_code, "", "", "", "");
        codeUpdateMapper.updateVipRecordType(new_corp_code, old_corp_code);
        codeUpdateMapper.updateVipLabel(new_corp_code, old_corp_code);
    }

    @Transactional
    public int deleteByCorpId(int id) throws SQLException {
        return corpMapper.deleteByCorpId(id);
    }

    /**
     * 分页显示所有企业
     */
    public PageInfo<Corp> selectAllCorp(int page_number, int page_size, String search_value) throws SQLException {
        PageHelper.startPage(page_number, page_size);
        List<Corp> corps = corpMapper.selectAllCorp(search_value);
        for (Corp corp:corps) {
            if(corp.getIsactive().equals("Y")){
                corp.setIsactive("是");
            }else{
                corp.setIsactive("否");
            }
        }
        PageInfo<Corp> page = new PageInfo<Corp>(corps);
        return page;
    }

    @Override
    public String insertExecl(Corp corp) {
        corpMapper.insertCorp(corp);
        return "add success";
    }

    @Override
    public PageInfo<Corp> selectAllCorpScreen(int page_number, int page_size, Map<String, String> map) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<Corp> list = corpMapper.selectAllCorpScreen(params);
        for (Corp corp:list) {
            if(corp.getIsactive().equals("Y")){
                corp.setIsactive("是");
            }else{
                corp.setIsactive("否");
            }
        }
        PageInfo<Corp> page = new PageInfo<Corp>(list);
        return page;
    }

    /**
     * 显示所有企业
     */
    public List<Corp> selectAllCorp() throws SQLException {
        List<Corp> list = corpMapper.selectCorps("");
        return list;
    }

    /**
     * 查找最大的corp_code
     * 以便新增企业时
     * 自动生成corp_code
     */
    public String selectMaxCorpCode() throws SQLException {
        return corpMapper.selectMaxCorpCode();
    }

    /**
     * 校验企业名称是否唯一
     *
     * @param corp_name
     * @return
     * @throws SQLException
     */
    @Override
    public String getCorpByCorpName(String corp_name) throws SQLException {
        List<Corp> corps = corpMapper.selectByCorpName(corp_name);
        if (corps == null || corps.size() < 1) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public int getAreaCount(String corp_code) {
        return this.corpMapper.getAreaCount(corp_code);
    }

    @Override
    public int getBranCount(String corp_code) {
        return this.corpMapper.getBrandCount(corp_code);
    }

    @Override
    public int getGoodCount(String corp_code) {
        return this.corpMapper.getGoodCount(corp_code);
    }

    public Corp getCorpByAppUserName(String app_user_name) {
        return corpMapper.selectByAppUserName(app_user_name);
    }

    @Override
    public int getGroupCount(String corp_code) {
        return corpMapper.getGroupCount(corp_code);
    }

    @Override
    public int getGoodsCount(String corp_code) {
        return corpMapper.getGoodCount(corp_code);
    }

    @Override
    public int getMessagesTypeCount(String corp_code) {
        return corpMapper.getMessageTypeCount(corp_code);
    }

    public int selectCount(String create_date) {
        return corpMapper.selectCount(create_date);
    }
}
