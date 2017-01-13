package com.bizvane.ishop.quartz;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.ScheduleJob;
import com.bizvane.ishop.service.VipFsendService;
import com.bizvane.ishop.service.imp.VipActivityServiceImpl;
import com.bizvane.ishop.service.imp.VipFsendServiceImpl;
import com.bizvane.ishop.utils.SpringBeanFactoryUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Bizvane
 * createTime : 2016-08-04
 * description : 异步任务工厂
 * version : 1.0
 */
@Component
public class AsyncJobFactory extends QuartzJobBean {

    /* 日志对象 */
    private static final Logger LOG = LoggerFactory.getLogger(AsyncJobFactory.class);

//    @Autowired
//    VipFsendService vipFsendService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
//        LOG.info("AsyncJobFactory execute");
//        ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get(CommonValue.JOB_PARAM_KEY);
//        System.out.println("jobName:" + scheduleJob.getJob_name() + "  " + scheduleJob);
        JobKey jobKey = context.getJobDetail().getKey();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

        JobDataMap data = context.getJobDetail().getJobDataMap();
        String func = data.get("func").toString();
        JSONObject func_obj = JSONObject.parseObject(func);
        String method = func_obj.getString("method");
        String corp_code = func_obj.getString("corp_code");
        String user_code = func_obj.getString("user_code");
        String code = func_obj.getString("code");
        if (method.equals("sendSMS")){
            VipFsendServiceImpl vipFsendService = (VipFsendServiceImpl) SpringBeanFactoryUtils.getBean("vipFsendServiceImpl");
            vipFsendService.fsendSchedule(corp_code,code,user_code);
        }else if (method.equals("changeStatus")){
            VipActivityServiceImpl vipActivityService = (VipActivityServiceImpl) SpringBeanFactoryUtils.getBean("vipActivityServiceImpl");
            vipActivityService.updateStatus(code);
        }else {
            System.out.println("it's test2 " + Common.DATETIME_FORMAT.format(new Date()));
        }

        System.out.println("集群列子1："+ jobKey + " 在 " + dateFormat.format(new Date())+" 时运行");
    }
}
