package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.RoleMapper;
import com.bizvane.ishop.entity.Role;
import com.bizvane.ishop.service.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMapper roleMapper;

    public Role selectByRoleId(int role_id) throws SQLException {
        return roleMapper.selectByRoleId(role_id);
    }

    public int insertRole(Role record) throws SQLException {
        return roleMapper.insertRole(record);
    }

    public int updateByRoleId(Role record) throws SQLException {
        return roleMapper.updateByRoleId(record);
    }

    public int deleteByRoleId(int id) throws SQLException {
        return roleMapper.deleteByRoleId(id);
    }

    public PageInfo<Role> selectAllRole(int page_number, int page_size, String search_value) throws SQLException {
        PageHelper.startPage(page_number, page_size);
        List<Role> roles = roleMapper.selectAllRole("%" + search_value + "%");
        PageInfo<Role> page = new PageInfo<Role>(roles);
        return page;
    }

    public List<Role> selectCorpRole(String role_code) throws SQLException {
        List<Role> roles = roleMapper.selectUserRole(role_code);
        return roles;
    }
}
