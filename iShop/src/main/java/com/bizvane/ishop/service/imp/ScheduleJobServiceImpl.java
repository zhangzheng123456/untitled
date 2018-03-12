package com.bizvane.ishop.service.imp;


import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.ScheduleJobMapper;
import com.bizvane.ishop.entity.ScheduleJob;
import com.bizvane.ishop.service.ScheduleJobService;
import com.bizvane.ishop.utils.ScheduleUtils;
import com.dexcoder.dal.JdbcDao;
import com.dexcoder.dal.build.Criteria;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.smartcardio.CommandAPDU;
import java.util.Date;
import java.util.List;

/**
 * Bizvane
 * createTime : 2016-08-04
 * description : 定时任务服务实现
 * version : 1.0
 */
@Service
public class ScheduleJobServiceImpl implements ScheduleJobService {

    /**
     * 调度工厂Bean
     */
    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    public void initScheduleJob() throws Exception{
//        List<ScheduleJob> scheduleJobList = jdbcDao.queryList(Criteria.select(ScheduleJob.class));

        List<ScheduleJob> scheduleJobList = scheduleJobMapper.selectAllUnJob();

        if (CollectionUtils.isEmpty(scheduleJobList)) {
            return;
        }
        for (ScheduleJob scheduleJob : scheduleJobList) {

            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getJob_name(), scheduleJob.getJob_group());

            //不存在，创建一个
            if (cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            } else {
                //已存在，那么更新相应的定时设置
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
    }

    public List<ScheduleJob> selectAll() throws Exception {
        List<ScheduleJob> scheduleJobList = scheduleJobMapper.selectAllScheduleJob();
        return scheduleJobList;
    }

    public int insert(ScheduleJob scheduleJob) throws Exception{
        Date a = ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
        if (a == null){
            scheduleJob.setDescription("创建失败");
        }else {
            scheduleJob.setGmt_create(Common.DATETIME_FORMAT.format(a));
        }
        return scheduleJobMapper.insertScheduleJob(scheduleJob);
    }

    public void update(ScheduleJob scheduleJob) throws Exception{
        ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
        scheduleJobMapper.updateScheduleJob(scheduleJob);
    }

    public void updateSchedule(String job_name,String job_group) {
        try {
            ScheduleJob scheduleJob = selectScheduleByJob(job_name,job_group);
            if (scheduleJob != null){
                scheduleJobMapper.updateStatus(job_name, job_group);
                ScheduleUtils.deleteScheduleJob(scheduler, job_name, job_group);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void delUpdate(ScheduleJob scheduleJob) throws Exception{
        //先删除
        ScheduleUtils.deleteScheduleJob(scheduler, scheduleJob.getJob_name(), scheduleJob.getJob_group());
        //再创建
        ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
        //数据库直接更新即可
        scheduleJobMapper.updateScheduleJob(scheduleJob);

    }

    public void delete(String job_name,String job_group) throws Exception{
        ScheduleJob scheduleJob = scheduleJobMapper.selectScheduleByJob(job_name,job_group);
        if (scheduleJob != null){
            //删除运行的任务
            ScheduleUtils.deleteScheduleJob(scheduler, scheduleJob.getJob_name(), scheduleJob.getJob_group());
            //删除数据
            scheduleJobMapper.deleteScheduleJob(scheduleJob.getSchedule_job_id());
        }
    }

    public void runOnce(int scheduleJobId) throws Exception{
        ScheduleJob scheduleJob = scheduleJobMapper.selectScheduleJobById(scheduleJobId);

        ScheduleUtils.runOnce(scheduler, scheduleJob.getJob_name(), scheduleJob.getJob_group());
    }

    public void pauseJob(int scheduleJobId) throws Exception{
        ScheduleJob scheduleJob = scheduleJobMapper.selectScheduleJobById(scheduleJobId);

        ScheduleUtils.pauseJob(scheduler, scheduleJob.getJob_name(), scheduleJob.getJob_group());
    }

    public void resumeJob(int scheduleJobId) throws Exception{
        ScheduleJob scheduleJob = scheduleJobMapper.selectScheduleJobById(scheduleJobId);

        ScheduleUtils.resumeJob(scheduler, scheduleJob.getJob_name(), scheduleJob.getJob_group());
    }

    public ScheduleJob get(int scheduleJobId)throws Exception {
        ScheduleJob scheduleJob = scheduleJobMapper.selectScheduleJobById(scheduleJobId);

        return scheduleJob;
    }

    public ScheduleJob selectScheduleByJob(String job_name, String job_group)throws Exception {
        ScheduleJob scheduleJob = scheduleJobMapper.selectScheduleByJob(job_name,job_group);
        return scheduleJob;
    }

    public int deleteScheduleByName(String job_name)throws Exception {
        return scheduleJobMapper.deleteScheduleByName(job_name);
    }

    public int deleteScheduleByGroup(String job_group)throws Exception {
        List<ScheduleJob> scheduleJobs = scheduleJobMapper.selectJobByGroup(job_group);
        for (int i = 0; i < scheduleJobs.size(); i++) {
            //删除运行的任务
            ScheduleUtils.deleteScheduleJob(scheduler, scheduleJobs.get(i).getJob_name(), scheduleJobs.get(i).getJob_group());
            //删除数据
            scheduleJobMapper.deleteScheduleJob(scheduleJobs.get(i).getSchedule_job_id());
        }
        return scheduleJobs.size();
    }

    /**
     * 获取运行中的job列表
     */
//    public List<ScheduleJobVo> queryExecutingJobList() {
//        try {
//            // 存放结果集
//            List<ScheduleJobVo> jobList = new ArrayList<ScheduleJobVo>();
//
//            // 获取scheduler中的JobGroupName
//            for (String group : scheduler.getJobGroupNames()) {
//                // 获取JobKey 循环遍历JobKey
//                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(group))) {
//                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
//                    JobDataMap jobDataMap = jobDetail.getJobDataMap();
//                    ScheduleJob scheduleJob = (ScheduleJob) jobDataMap.get(ScheduleJobVo.JOB_PARAM_KEY);
//                    ScheduleJobVo scheduleJobVo = new ScheduleJobVo();
//                    BeanConverter.convert(scheduleJobVo, scheduleJob);
//                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
//                    Trigger trigger = triggers.iterator().next();
//                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
//                    scheduleJobVo.setJob_trigger(trigger.getKey().getName());
//                    scheduleJobVo.setStatus(triggerState.name());
//                    if (trigger instanceof CronTrigger) {
//                        CronTrigger cronTrigger = (CronTrigger) trigger;
//                        String cronExpression = cronTrigger.getCron_expression();
//                        scheduleJobVo.setCron_expression(cronExpression);
//                    }
//                    // 获取正常运行的任务列表
//                    if (triggerState.name().equals("NORMAL")) {
//                        jobList.add(scheduleJobVo);
//                    }
//                }
//            }
//
//            /** 非集群环境获取正在执行的任务列表 */
//            /**
//             List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
//             List<ScheduleJobVo> jobList = new ArrayList<ScheduleJobVo>(executingJobs.size());
//             for (JobExecutionContext executingJob : executingJobs) {
//             ScheduleJobVo job = new ScheduleJobVo();
//             JobDetail jobDetail = executingJob.getJobDetail();
//             JobKey jobKey = jobDetail.getKey();
//             Trigger trigger = executingJob.getTrigger();
//             job.setJob_name(jobKey.getName());
//             job.setJob_group(jobKey.getGroup());
//             job.setJob_trigger(trigger.getKey().getName());
//             Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
//             job.setStatus(triggerState.name());
//             if (trigger instanceof CronTrigger) {
//             CronTrigger cronTrigger = (CronTrigger) trigger;
//             String cronExpression = cronTrigger.getCron_expression();
//             job.setCron_expression(cronExpression);
//             }
//             jobList.add(job);
//             }*/
//
//            return jobList;
//        } catch (SchedulerException e) {
//            return null;
//        }
//
//    }
}
