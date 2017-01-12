package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.entity.VipActivity;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.WebUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by PC on 2017/1/12.
 */
@Service
public class VipActivityMakeServiceImpl implements VipActivityMakeService{
    @Autowired
    private TaskService taskService;
    @Autowired
    private VipFsendService vipFsendService;
    @Autowired
    private VipActivityService vipActivityService;
    @Autowired
    VipGroupService vipGroupService;
    @Autowired
    private VipActivityDetailService vipActivityDetailService;

    @Transactional
    public int addOrUpdateTask(String message, String user_code) throws Exception {
        JSONObject jsonObject = new JSONObject(message);
        String activity_vip_code = jsonObject.get("activity_vip_code").toString();

        String tasklist = jsonObject.get("tasklist").toString();
        JSONArray jsonArray_task = JSON.parseArray(tasklist);

        int count = 0;
        String task_code_actvie = "";
        VipActivity vipActivity = vipActivityService.selActivityByCode(activity_vip_code);
        String corp_code = vipActivity.getCorp_code();
        taskService.delTaskByActivityCode(corp_code, activity_vip_code);
        for (int i = 0; i < jsonArray_task.size(); i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String task_code = "T" + sdf.format(new Date()) + Math.round(Math.random() * 9);
            Thread.sleep(1);
            task_code_actvie = task_code_actvie + task_code + ",";

            JSONObject task_obj = new JSONObject(jsonArray_task.get(i).toString());
            Task task = WebUtils.JSON2Bean(task_obj, Task.class);
            task.setActivity_vip_code(activity_vip_code);


            task.setTask_code(task_code);
            task.setCorp_code(corp_code);

            Date now = new Date();
            task.setCreated_date(Common.DATETIME_FORMAT.format(now));
            task.setCreater(user_code);
            task.setModified_date(Common.DATETIME_FORMAT.format(now));
            task.setModifier(user_code);
            task.setIsactive("Y");

            count += taskService.insertTask(task);

        }
        count += vipActivityService.updActiveCodeByType("task_code", task_code_actvie, corp_code, activity_vip_code);

        int i=5/0;

        System.out.println("====================测试报错=================================");
        return count;
    }
}
