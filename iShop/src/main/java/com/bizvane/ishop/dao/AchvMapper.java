package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Achv;

public interface AchvMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Achv record);

    Achv selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Achv record);

}