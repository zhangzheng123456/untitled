package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.ScheduleJob;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

public interface ScheduleJobMapper {

    ScheduleJob selectScheduleJobById(@Param("schedule_job_id") int schedule_job_id) throws SQLException;

    ScheduleJob selectScheduleByJob(@Param("job_name") String job_name,@Param("job_group") String job_group) throws SQLException;

    List<ScheduleJob> selectAllScheduleJob() throws SQLException;

    List<ScheduleJob> selectAllUnJob() throws SQLException;

    List<ScheduleJob> selectJobByGroup(@Param("job_group") String job_group) throws SQLException;

    int insertScheduleJob(ScheduleJob record) throws SQLException;

    int updateScheduleJob(ScheduleJob record) throws SQLException;

    int updateStatus(@Param("job_name") String job_name,@Param("job_group") String job_group);

    int deleteScheduleJob(@Param("schedule_job_id") int schedule_job_id) throws SQLException;

    int deleteScheduleByName(@Param("job_name") String job_name) throws SQLException;

    int deleteScheduleByGroup(@Param("job_group") String job_group) throws SQLException;

}