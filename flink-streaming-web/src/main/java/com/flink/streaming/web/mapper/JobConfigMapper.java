package com.flink.streaming.web.mapper;

import com.flink.streaming.web.model.entity.JobConfig;
import com.flink.streaming.web.model.param.JobConfigParam;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobConfigMapper {


    int insert(JobConfig record);


    long selectCountByJobName(@Param("jobName") String jobName, @Param("id") Long id);


    JobConfig selectByPrimaryKey(Long id);


    int updateByPrimaryKeySelective(JobConfig record);


    Page<JobConfig> findJobConfig(JobConfigParam jobConfigParam);


    int deleteById(@Param("id") Long id, @Param("userName") String userName);

    List<JobConfig> findJobConfigByStatus(@Param("statusList") List<Integer> statusList);


}
