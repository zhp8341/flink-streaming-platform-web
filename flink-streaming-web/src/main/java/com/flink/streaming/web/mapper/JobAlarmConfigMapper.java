package com.flink.streaming.web.mapper;

import com.flink.streaming.web.model.entity.JobAlarmConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobAlarmConfigMapper {

    int insertBatch(@Param("list") List<JobAlarmConfig> list);

    List<JobAlarmConfig> selectByJobId(@Param("jobId")Long jobId);

    int deleteByJobId(@Param("jobId")Long jobId);


}
