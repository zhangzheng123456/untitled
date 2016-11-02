package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.PrivilegeMapper;
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
//    @Autowired
//    private RoleMapper roleMapper;

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PrivilegeMapper privilegeMapper;

    public Role selectByRoleId(int role_id) throws Exception {
        return roleMapper.selectByRoleId(role_id);
    }

    @Override
    public String insertRole(Role record) throws Exception {
        if (this.roleCodeExist(record.getRole_code()).equals(Common.DATABEAN_CODE_ERROR)) {
            return "角色编号已存在";
        } else if (this.roleNameExist(record.getRole_name()).equals(Common.DATABEAN_CODE_ERROR)) {
            return "角色名称已存在";
        } else if (roleMapper.insertRole(record) >= 0) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }


    @Override
    public String roleCodeExist(String role_code) throws Exception{
        if (roleMapper.countRoleCode(role_code) > 0) {
            return Common.DATABEAN_CODE_ERROR;
        }
        return Common.DATABEAN_CODE_SUCCESS;
    }

    @Override
    public String roleNameExist(String role_name) throws Exception {
        if (roleMapper.countRoleName(role_name) > 0) {
            return Common.DATABEAN_CODE_ERROR;
        }
        return Common.DATABEAN_CODE_SUCCESS;
    }


    public String updateByRoleId(Role record) throws Exception {
        Role old = this.roleMapper.selectByRoleId(record.getId());
        if ((!old.getRole_code().equals(record.getRole_code()))
                && (this.roleCodeExist(record.getRole_code()).equals(Common.DATABEAN_CODE_ERROR))) {
            return "角色编号已存在";
        } else if (!old.getRole_name().equals(record.getRole_name()) && (this.roleNameExist(record.getRole_name()).equals(Common.DATABEAN_CODE_ERROR))) {
            return "角色名称已存在";
        } else if (roleMapper.updateByRoleId(record) >= 0) {
            return Common.DATABEAN_CODE_SUCCESS;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    public int deleteByRoleId(int id,String role_code) throws Exception {
        privilegeMapper.delete(role_code);

        return roleMapper.deleteByRoleId(id);
    }

    public PageInfo<Role> selectAllRole(int page_number, int page_size, String search_value) throws Exception {
        PageHelper.startPage(page_number, page_size);
        List<Role> roles = roleMapper.selectAllRole(search_value);
        PageInfo<Role> page = new PageInfo<Role>(roles);
        return page;
    }

    public List<Role> selectAll(String search_value) throws Exception {
        List<Role> roles = roleMapper.selectAllRole(search_value);
        return roles;
    }

    public List<Role> selectCorpRole(String role_code) throws Exception {
        List<Role> roles = roleMapper.selectUserRole(role_code);
        return roles;
    }

    @Override
    public Role getRoleForID(String role_code) throws Exception {
        return roleMapper.getRoleForID(role_code);
    }
}
