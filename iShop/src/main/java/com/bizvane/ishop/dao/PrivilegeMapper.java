package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Privilege;

public interface PrivilegeMapper {

    int delete(Integer id);

    int insert(Privilege record);
}