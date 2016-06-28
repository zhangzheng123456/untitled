package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.GroupMapper;
import com.bizvane.ishop.entity.Group;
import com.bizvane.ishop.service.GroupService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ZhouZhou on 2016/6/6.
 */
@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupMapper groupMapper;

    public Group getGroupById(int id) throws SQLException {
        return groupMapper.selectByGroupId(id);
    }

    public List<Group> selectUserGroup(String corp_code, String role_code) throws SQLException {
        return groupMapper.selectUserGroup(corp_code, role_code);
    }

    public List<Group> selectByRole(String role_code) throws SQLException {
        return groupMapper.selectByRole(role_code);
    }

    public Group selectByCode(String corp_code ,String group_code,String isactive) throws SQLException {
        return groupMapper.selectByCode(corp_code,group_code,isactive);
    }

    public PageInfo<Group> getGroupAll(int page_number, int page_size, String corp_code, String role_code, String search_value) throws SQLException {
        List<Group> groups;
        PageHelper.startPage(page_number, page_size);
        groups = groupMapper.selectAllGroup(corp_code,role_code, search_value);
        PageInfo<Group> page = new PageInfo<Group>(groups);
        return page;
    }

    public String selectMaxCode() {
        return groupMapper.selectMaxCode();
    }

    public String insertGroup(Group group) throws SQLException {
        String result = "";
        String group_code = group.getGroup_code();
        String corp_code = group.getCorp_code();
        Group group1 = selectByCode(corp_code,group_code,"");
        if (group1 == null){
            groupMapper.insertGroup(group);
            result = Common.DATABEAN_CODE_SUCCESS;
        }else {
            result = "该群组编号已存在！";
        }
        return result;
    }

    public String updateGroup(Group group) throws SQLException {
        String result = "";
        int id = group.getId();
        String group_code = group.getGroup_code();
        String corp_code = group.getCorp_code();
        Group group2 = getGroupById(id);
        Group group1 = selectByCode(corp_code,group_code,"");
        if (group2.getGroup_code().equals(group_code) || group1 == null){
            groupMapper.updateGroup(group);
            result = Common.DATABEAN_CODE_SUCCESS;
        }else {
            result = "该群组编号已存在！";
        }
        return result;
    }

    public int deleteGroup(int id) throws SQLException {
        return groupMapper.deleteByGroupId(id);
    }
}
