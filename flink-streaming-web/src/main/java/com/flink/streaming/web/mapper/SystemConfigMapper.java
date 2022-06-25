package com.flink.streaming.web.mapper;

import com.flink.streaming.web.model.entity.SystemConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemConfigMapper {


  int insert(SystemConfig systemConfig);


  List<SystemConfig> selectAllConfig(@Param("type") String type);


  SystemConfig selectConfigByKey(@Param("key") String key);

  int deleteByKey(@Param("key") String key);

  int updateByKey(SystemConfig systemConfig);


}
