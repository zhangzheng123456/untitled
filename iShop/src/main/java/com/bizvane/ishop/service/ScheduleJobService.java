package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.ScheduleJob;

/**
 * Bizvane
 * createTime : 2016-08-04
 * description : 定时任务服务
 * version : 1.0
 */
public interface ScheduleJobService {

    /**
     * 初始化定时任务
     */
    public void initScheduleJob() throws Exception;

    /**
     * 新增
     * 
     * @param scheduleJob
     * @return
     */
    public int insert(ScheduleJob scheduleJob) throws Exception;

    /**
     * 直接修改 只能修改运行的时间，参数、同异步等无法修改
     * 
     * @param scheduleJob
     */
    public void update(ScheduleJob scheduleJob) throws Exception;

    /**
     * 删除重新创建方式
     * 
     * @param scheduleJob
     */
    public void delUpdate(ScheduleJob scheduleJob) throws Exception;

    /**
     * 删除
     *
     * @param job_name
     * @param job_group
     */
    public void delete(String job_name,String job_group) throws Exception;

    /**
     * 运行一次任务
     *
     * @param scheduleJobId the schedule job id
     * @return
     */
    public void runOnce(int scheduleJobId) throws Exception;

    /**
     * 暂停任务
     *
     * @param scheduleJobId the schedule job id
     * @return
     */
    public void pauseJob(int scheduleJobId) throws Exception;

    /**
     * 恢复任务
     *
     * @param scheduleJobId the schedule job id
     * @return
     */
    public void resumeJob(int scheduleJobId) throws Exception;

    /**
     * 获取任务对象
     * 
     * @param scheduleJobId
     * @return
     */
    public ScheduleJob get(int scheduleJobId) throws Exception;


    ScheduleJob selectScheduleByJob(String job_name, String job_group)throws Exception;
    /**
     * 查询任务列表
     * 
     * @param scheduleJobVo
     * @return
     */
//    public List<ScheduleJobVo> queryList(ScheduleJobVo scheduleJobVo);

    /**
     * 获取运行中的任务列表
     *
     * @return
     */
//    public List<ScheduleJobVo> queryExecutingJobList();

}
