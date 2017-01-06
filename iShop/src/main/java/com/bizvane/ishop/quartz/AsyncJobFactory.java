package com.bizvane.ishop.quartz;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.ScheduleJob;
import com.bizvane.ishop.service.VipFsendService;
import com.bizvane.ishop.service.imp.VipFsendServiceImpl;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Bizvane
 * createTime : 2016-08-04
 * description : 异步任务工厂
 * version : 1.0
 */
public class AsyncJobFactory extends QuartzJobBean {

    /* 日志对象 */
    private static final Logger LOG = LoggerFactory.getLogger(AsyncJobFactory.class);


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
//        LOG.info("AsyncJobFactory execute");
//        ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get(CommonValue.JOB_PARAM_KEY);
//        System.out.println("jobName:" + scheduleJob.getJob_name() + "  " + scheduleJob);
        JobKey jobKey = context.getJobDetail().getKey();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

        VipFsendServiceImpl vipFsendService = new VipFsendServiceImpl();
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String func = data.get("func").toString();
        if (func.equals("test1")){
            vipFsendService.test1();
        }else if (func.equals("test2")){
            vipFsendService.test2();
        }else {
            System.out.println("it's test2 " + Common.DATETIME_FORMAT.format(new Date()));
        }

        System.out.println("集群列子1："+ jobKey + " 在 " + dateFormat.format(new Date())+" 时运行");
    }
}
