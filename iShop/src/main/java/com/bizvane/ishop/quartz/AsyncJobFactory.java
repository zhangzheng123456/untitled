package com.bizvane.ishop.quartz;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.ScheduleJob;
import com.bizvane.ishop.service.imp.ScheduleExcuteServiceImpl;
import com.bizvane.ishop.utils.SpringBeanFactoryUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LOG.info("AsyncJobFactory execute");
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
        ScheduleExcuteServiceImpl scheduleExcuteService = (ScheduleExcuteServiceImpl) SpringBeanFactoryUtils.getBean("ScheduleExcuteService");
        ScheduleJob scheduleJob =  scheduleExcuteService.checkSchedule(jobKey.getName(),jobKey.getGroup());

        if (scheduleJob == null)
            return;
        if (!scheduleExcuteService.today_execute(jobKey.getName(),jobKey.getGroup(),scheduleJob)){
            return;
        }
        if (method.equals("sendSMS")){
//            if (scheduleExcuteService.today_execute(jobKey.getName(),jobKey.getGroup())){
                scheduleExcuteService.updateSchedule(scheduleJob);
                scheduleExcuteService.fsendActivitySchedule(corp_code,user_code,jobKey.getName(),jobKey.getGroup());
//            }
        }else if (method.equals("vipActivity")){
            scheduleExcuteService.updateSchedule( scheduleJob);
            scheduleExcuteService.vipActivitySchedule(code,user_code,jobKey.getName(),jobKey.getGroup());
        }else if (method.equals("changeStatus")){
            scheduleExcuteService.updateSchedule( scheduleJob);
            scheduleExcuteService.updateStatusSchedule(code,scheduleJob.getJob_name(),scheduleJob.getJob_group());

        }else if (method.equals("sendCoupon")){
            scheduleExcuteService.updateSchedule(scheduleJob);
            scheduleExcuteService.sendCouponSchedule(corp_code,code,user_code);
        }else if (method.equals("sendSMSIntegral")){
//            if (scheduleExcuteService.today_execute(jobKey.getName(),jobKey.getGroup())){
                scheduleExcuteService.updateSchedule(scheduleJob);
                scheduleExcuteService.fsendIntegralSchedule(corp_code,code,user_code,jobKey.getGroup());
//            }
        }else if (method.equals("vipIntegral")){
//            if (scheduleExcuteService.today_execute(jobKey.getName(),jobKey.getGroup())){
                scheduleExcuteService.updateSchedule(scheduleJob);
                scheduleExcuteService.vipIntegralSchedule(corp_code,code,user_code);
//            }
        }else if (method.equals("VipTask")){
            scheduleExcuteService.updateSchedule(scheduleJob);
            scheduleExcuteService.vipTaskSchedule(corp_code,code,jobKey.getName());

        }else if (method.equals("VipBirthNotice")){
//            if (scheduleExcuteService.today_execute(jobKey.getName(),jobKey.getGroup())){
            scheduleExcuteService.updateSchedule(scheduleJob);
            scheduleExcuteService.VipBirthNotice(corp_code,jobKey.getGroup(),jobKey.getName());
//            }
        }else if (method.equals("VipAnniverActi")){
//            if (scheduleExcuteService.today_execute(jobKey.getName(),jobKey.getGroup())){
//                scheduleExcuteService.updateSchedule(scheduleJob);
                scheduleExcuteService.anniverActiRetroative(jobKey.getName(),jobKey.getGroup());
//            }

        }else if(method.equals("changeStoreCount")){
//            System.out.println(method+"...启动店铺使用天数..."+dateFormat.format(new Date()));
//            scheduleExcuteService.changeStore(code,code);
        }
//        else if(method.equals("changeStoreCount")){
//            scheduleExcuteService.changeStoreCount(code,code);
//        }
        else {
            System.out.println("it's test2 " + Common.DATETIME_FORMAT.format(new Date()));
        }

//        System.out.println("集群列子1："+ jobKey + " 在 " + dateFormat.format(new Date())+" 时运行");
    }
}
