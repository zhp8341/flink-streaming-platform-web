package com.flink.streaming.web.mapper;

import com.flink.streaming.web.model.entity.JobRunLog;
import com.flink.streaming.web.model.param.JobRunLogParam;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRunLogMapper {

  int insert(JobRunLog record);


  int update(JobRunLog record);

  JobRunLog selectById(@Param("id") Long id);

  Page<JobRunLog> selectByParam(JobRunLogParam jobRunLogParam);

  int deleteByJobConfigId(@Param("jobConfigId") Long jobConfigId);


}
