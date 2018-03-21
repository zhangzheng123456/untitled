package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VipActivity;
import com.github.pagehelper.PageInfo;

import java.util.Map;

public interface ActivityService {

    PageInfo<VipActivity> selectAllActivity(int page_number, int page_size, String search_value);
    PageInfo<VipActivity> selectAllCorpScreen(int page_num, int page_size, String corp_code, String user_code, Map<String, String> map);


}
