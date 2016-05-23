package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.CorpInfo;

public interface CorpInfoMapper {

    CorpInfo selectByCorpId(Integer id);

    int insertCorp(CorpInfo record);

    int updateByCorpId(CorpInfo record);

    int deleteByCorpId(Integer id);

}