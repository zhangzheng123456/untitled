package com.bizvane.ishop.service.imp;

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

    public List<Group> selectUserGroup(String corp_code,String role_code) throws SQLException {
        return groupMapper.selectUserGroup(corp_code,role_code);
    }

    public List<Group> selectByRole(String role_code) throws SQLException {
        return groupMapper.selectByRole(role_code);
    }

    public Group selectCorpGroup(String corp_code,String group_code) throws SQLException {
        return groupMapper.selectCorpGroup(corp_code,group_code);
    }

    public PageInfo<Group> getGroupAll(int page_number, int page_size, String corp_code, String search_value) throws SQLException {
        List<Group> groups;
        if (search_value.equals("")) {
            PageHelper.startPage(page_number, page_size);
            groups = groupMapper.selectAllGroup(corp_code, "");
        } else {
            PageHelper.startPage(page_number, page_size);
            groups = groupMapper.selectAllGroup(corp_code, "%" + search_value + "%");
        }
        PageInfo<Group> page = new PageInfo<Group>(groups);
        return page;
    }

    public String selectMaxCode(){
        return groupMapper.selectMaxCode();
    }
    public int insertGroup(Group group) throws SQLException {
        return groupMapper.insertGroup(group);
    }

    public int updateGroup(Group group) throws SQLException {
        return groupMapper.updateGroup(group);
    }

    public int deleteGroup(int id) throws SQLException {
        return groupMapper.deleteByGroupId(id);
    }
}
