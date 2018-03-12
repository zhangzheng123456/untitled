package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.*;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.GroupService;
import com.bizvane.ishop.service.VipActivityDetailService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhouZhou on 2016/6/6.
 */
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private CodeUpdateMapper codeUpdateMapper;

    @Autowired
    private PrivilegeMapper privilegeMapper;
    @Autowired
    private VipActivityMapper vipActivityMapper;
    @Autowired
    private VipActivityDetailMapper vipActivityDetailMapper;

    public Group getGroupById(int id) throws Exception {
        return groupMapper.selectByGroupId(id);
    }

    public List<Group> selectUserGroup(String corp_code, String role_code) throws Exception {
        return groupMapper.selectUserGroup(corp_code, role_code);
    }

    public List<Group> selectByRole(String role_code) throws Exception {
        return groupMapper.selectByRole(role_code);
    }

    public Group selectByCode(String corp_code, String group_code, String isactive) throws Exception {
        return groupMapper.selectByCode(corp_code, group_code, isactive);
    }

    @Override
    public Group selectByName(String corp_code, String group_name, String isactive) throws Exception {
        return groupMapper.selectByName(corp_code, group_name, isactive);
    }

    public PageInfo<Group> getGroupAll(int page_number, int page_size, String corp_code, String role_code, String search_value) throws SQLException {
        List<Group> groups;
        PageHelper.startPage(page_number, page_size);
        groups = groupMapper.selectAllGroup(corp_code, role_code, search_value,null);
        for (Group group : groups) {
            group.setIsactive(CheckUtils.CheckIsactive(group.getIsactive()));
        }
        PageInfo<Group> page = new PageInfo<Group>(groups);
        return page;
    }


    public PageInfo<Group> getGroupAll(int page_number, int page_size, String corp_code, String role_code, String search_value,String manager_corp) throws SQLException {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        List<Group> groups;
        PageHelper.startPage(page_number, page_size);
        groups = groupMapper.selectAllGroup(corp_code, role_code, search_value,manager_corp_arr);
        for (Group group : groups) {
            group.setIsactive(CheckUtils.CheckIsactive(group.getIsactive()));
        }
        PageInfo<Group> page = new PageInfo<Group>(groups);
        return page;
    }

    public List<Group> getGroupAll(String corp_code, String role_code) throws SQLException {
        List<Group> groups = groupMapper.selectAllGroup(corp_code, role_code, "",null);
        return groups;
    }

    @Override
    public PageInfo<Group> getAllGroupScreen(int page_number, int page_size, String corp_code, String role_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        List<Group> groups;
        PageHelper.startPage(page_number, page_size);
        groups = groupMapper.selectAllGroupScreen(params);
        for (Group group : groups) {
            group.setIsactive(CheckUtils.CheckIsactive(group.getIsactive()));
        }
        PageInfo<Group> page = new PageInfo<Group>(groups);
        return page;
    }


    @Override
    public PageInfo<Group> getAllGroupScreen(int page_number, int page_size, String corp_code, String role_code, Map<String, String> map,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("manager_corp_arr", manager_corp_arr);
        params.put("map", map);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        List<Group> groups;
        PageHelper.startPage(page_number, page_size);
        groups = groupMapper.selectAllGroupScreen(params);
        for (Group group : groups) {
            group.setIsactive(CheckUtils.CheckIsactive(group.getIsactive()));
        }
        PageInfo<Group> page = new PageInfo<Group>(groups);
        return page;
    }

    public String selectMaxCode() throws Exception {
        return groupMapper.selectMaxCode();
    }

    public String insertGroup(Group group) throws Exception {
        String result = "";
        String group_code = group.getGroup_code().trim();
        String corp_code = group.getCorp_code().trim();
        Group group1 = selectByCode(corp_code, group_code, "");
        if (group1 == null) {
            groupMapper.insertGroup(group);
            result = Common.DATABEAN_CODE_SUCCESS;
        } else {
            result = "该群组编号已存在";
        }
        return result;
    }

    @Transactional
    public String updateGroup(Group group) throws Exception {
        String result = "";
        int id = group.getId();
        String group_code = group.getGroup_code().trim();
        String corp_code = group.getCorp_code().trim();
        Group old_group = getGroupById(id);
        Group group1 = selectByCode(corp_code, group_code, "");
        if (old_group.getCorp_code().equals(group.getCorp_code().trim())) {
            if (old_group.getGroup_code().equals(group_code) || group1 == null) {
                if (!old_group.getGroup_code().trim().equals(group_code)) {
                    updateCauseCodeChange(corp_code, group_code, old_group.getGroup_code().trim());
                }
                groupMapper.updateGroup(group);
                result = Common.DATABEAN_CODE_SUCCESS;
            } else {
                result = "该群组编号已存在";
            }
        } else {
            if (group1 == null) {
//                if (!old_group.getGroup_code().equals(group_code)) {
//                    updateCauseCodeChange(corp_code, group_code, old_group.getGroup_code());
//                }
                groupMapper.updateGroup(group);
                result = Common.DATABEAN_CODE_SUCCESS;
            } else {
                result = "该群组编号已存在";
            }
        }
        return result;
    }

    public int deleteGroup(int id, String group_code, String corp_code) throws Exception {
        privilegeMapper.delete(corp_code + "G" + group_code);
        return groupMapper.deleteByGroupId(id);
    }

    /**
     * 更改群组编号时
     * 级联更改关联此编号的员工，权限列表
     */
    @Transactional
    void updateCauseCodeChange(String corp_code, String new_group_code, String old_group_code) throws Exception {
        //若修改群组编号，对应修改员工信息中关联的群组编号
        codeUpdateMapper.updateUser("", corp_code, new_group_code, old_group_code, "", "", "", "","","");

        //若修改群组编号，对应修改权限中关联的群组编号
        codeUpdateMapper.updatePrivilege(corp_code + new_group_code, corp_code + old_group_code);
    }

    @Override
    public String selRoleByGroupCode(String corp_code, String group_code) throws Exception {
        Group group = groupMapper.selectByCode(corp_code, group_code, "");
        return group.getRole_code();
    }

    @Override
    public List<Group> selectByCorpRole(String corp_code, String role_code) throws Exception {
        List<Group> group = groupMapper.selectByCorpRole(corp_code, role_code);
        return group;
    }

    public int insert(VipActivity vipActivity) throws Exception {

        return vipActivityMapper.insertActivity(vipActivity);
    }

    public int insertDetail(VipActivityDetail detail) throws Exception {

        return vipActivityDetailMapper.insertActivityDetail(detail);
    }

    public int insertDetailAnniversary(VipActivityDetailAnniversary detailAnniversary) throws Exception{
        return vipActivityDetailMapper.insertDetailAnniversary(detailAnniversary);
    }

    public int updateDetail(VipActivityDetail detail) throws Exception {

        return vipActivityDetailMapper.updateActivityDetail(detail);
    }
}
