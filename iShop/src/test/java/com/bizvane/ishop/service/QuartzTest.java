package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.dao.CorpMapper;
import com.bizvane.ishop.entity.ScheduleJob;
import com.bizvane.ishop.quartz.AsyncJobFactory;
import com.bizvane.ishop.utils.IshowHttpClient;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.common.service.redis.RedisClient;
import com.mongodb.MongoClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ZhouZhou on 2016/8/30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml",
        "classpath:spring-mybatis.xml"})
public class QuartzTest {

    @Autowired
    ScheduleJobService scheduleJobService;

    @Autowired
    private Scheduler scheduler;

    //成功
    @Test
    public void run() throws Exception {
        System.out.println("------- 初始化 -------------------");

        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();

        System.out.println("------- 初始化完成 --------");

        System.out.println("------- 向Scheduler加入Job ----------------");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

        //构建一个作业实例
        JobDetail job = JobBuilder.newJob(AsyncJobFactory.class).withIdentity("job1", "group2").build();

        CronTrigger trigger = (CronTrigger)TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group2")
                //设置Cron表达式
                //从0秒开始 每隔20秒执行一次
                .withSchedule(CronScheduleBuilder.cronSchedule("0/20 * * * * ?"))
                .build();

        Date ft = sched.scheduleJob(job, trigger);
        System.out.println(job.getKey() + " 已经在 : " + dateFormat.format(ft) + " 时运行，Cron表达式：" + trigger.getCronExpression());

        job = JobBuilder.newJob(AsyncJobFactory.class).withIdentity("job2", "group2").build();

        trigger = (CronTrigger)TriggerBuilder.newTrigger()
                .withIdentity("trigger2", "group2")
                //从0分开始 每隔2分15秒执行一次
                .withSchedule(CronScheduleBuilder.cronSchedule("15 0/2 * * * ?"))
                .build();

        ft = sched.scheduleJob(job, trigger);
        System.out.println(job.getKey() + " 已经在 : " + dateFormat.format(ft) + " 时运行，Cron表达式：" + trigger.getCronExpression());

        job = JobBuilder.newJob(AsyncJobFactory.class).withIdentity("job3", "group2").build();

        trigger = (CronTrigger)TriggerBuilder.newTrigger()
                .withIdentity("trigger3", "group2")
                //每天的8点到17点 从0分开始 每隔2分钟执行一次
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 8-17 * * ?"))
                .build();

        ft = sched.scheduleJob(job, trigger);
        System.out.println(job.getKey() + " 已经在 : " + dateFormat.format(ft) + " 时运行，Cron表达式：" + trigger.getCronExpression());

        job = JobBuilder.newJob(AsyncJobFactory.class).withIdentity("job4", "group2").build();

        trigger = (CronTrigger)TriggerBuilder.newTrigger()
                .withIdentity("trigger4", "group2")
                //每天的17点到23点 从0分开始 每隔3分钟执行一次
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/3 17-23 * * ?"))
                .build();

        ft = sched.scheduleJob(job, trigger);
        System.out.println(job.getKey() + " 已经在 : " + dateFormat.format(ft) + " 时运行，Cron表达式：" + trigger.getCronExpression());

        System.out.println("------- 开始Scheduler ----------------");
        sched.start();

        System.out.println("------- Scheduler调用job结束 -----------------");

        System.out.println("------- 等待5分钟... ------------");
        try
        {
            Thread.sleep(300000L);
        }
        catch (Exception e)
        {
        }

        System.out.println("------- Scheduler关闭 -----------------");
        sched.shutdown(true);
        System.out.println("------- Scheduler完成 -----------------");
    }

    public void insertSchedule() throws Exception{
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJob_name("job1");
        scheduleJob.setJob_group("group1");
        scheduleJob.setFunc("test1");
        scheduleJob.setCron_expression("0/20 * * * * ?");
        scheduleJob.setGmt_create(new Date());
        scheduleJob.setGmt_modify(new Date());
        scheduleJobService.insert(scheduleJob);
    }
    @Test
    public void test(){
        try {
            System.out.println("------- 开始Scheduler ----------------");
            scheduler.start();
            insertSchedule();

            try
            {
                Thread.sleep(300000L);
            }
            catch (Exception e)
            {
            }

            System.out.println("------- Scheduler关闭 -----------------");
            scheduler.shutdown(true);
            System.out.println("------- Scheduler完成 -----------------");
        }catch (Exception e){

            e.printStackTrace();
        }
    }
}
