package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.bizvane.ishop.dao.CorpMapper;
import com.bizvane.ishop.dao.StoreMapper;
import com.bizvane.ishop.dao.UserMapper;
import com.bizvane.ishop.entity.Feedback;
import com.bizvane.ishop.service.FeedbackService;
import com.bizvane.ishop.service.HomeService;
import com.bizvane.ishop.utils.TimeUtils;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    CorpMapper corpMapper;
    @Autowired
    StoreMapper storeMapper;
    @Autowired
    UserMapper userMapper;

    @Autowired
    FeedbackService feedbackService;

    public String sysHome() {
        JSONObject dashboard = new JSONObject();

        try {
            int corp_count = corpMapper.selectCount("");
            int store_count = storeMapper.selectCount("");
            int user_count = userMapper.selectCount("");

            String yesterday = TimeUtils.beforDays(1);
            int corp_count_yest = corpMapper.selectCount(yesterday);
            int store_count_yest = storeMapper.selectCount(yesterday);
            int user_count_yest = userMapper.selectCount(yesterday);

            JSONObject count = new JSONObject();
            for (int i = 0; i < 7; i++) {
                String date = TimeUtils.beforDays(i);
                int user_count_day = userMapper.selectCount(date);
                count.put(String.valueOf(i),user_count_day);
            }
            PageInfo<Feedback> feedback = feedbackService.selectAllFeedback(1, 6, "");
            dashboard.put("corp_count", corp_count);
            dashboard.put("store_count", store_count);
            dashboard.put("user_count", user_count);
            dashboard.put("feedback", JSON.toJSONString(feedback.getList()));
        }catch (Exception ex){

        }
        return "";
    }
}
