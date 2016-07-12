package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.AreaMapper;
import com.bizvane.ishop.dao.CorpMapper;
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
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
@Service
public class CorpServiceImpl implements CorpService {
    @Autowired
    private CorpMapper corpMapper;

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
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        int corp_id = Integer.parseInt(jsonObject.get("id").toString());

        String corp_code = jsonObject.get("corp_code").toString();
        String corp_name = jsonObject.get("corp_name").toString();

        Corp corp = selectByCorpId(corp_id, "");
        Corp corp1 = selectByCorpId(0, corp_code);
        String exist = getCorpByCorpName(corp_name);

        if ((corp.getCorp_code().equals(corp_code) || corp1 == null)
                && (corp.getCorp_name().equals(corp_name) || exist.equals(Common.DATABEAN_CODE_SUCCESS))) {
            corp = new Corp();
            corp.setId(corp_id);
            corp.setCorp_code(corp_code);
            corp.setCorp_name(corp_name);
            corp.setAddress(jsonObject.get("address").toString());
            corp.setContact(jsonObject.get("contact").toString());
            corp.setContact_phone(jsonObject.get("phone").toString());
            corp.setAvater(jsonObject.get("avater").toString());
            corp.setApp_id(jsonObject.get("app_id").toString());
            corp.setIsactive(jsonObject.get("isactive").toString());
            corpMapper.updateByCorpId(corp);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else if (!corp.getCorp_code().equals(corp_code) || corp1 != null) {
            result = "企业编号已存在";
        } else {
            result = "企业名称已存在";
        }
        return result;
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
        PageInfo<Corp> page = new PageInfo<Corp>(corps);
        return page;
    }

    @Override
    public String insertExecl(Corp corp) {
        corpMapper.insertCorp(corp);
        return "add success";
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

    public int selectCount(String create_date){
        return corpMapper.selectCount(create_date);
    }
}
