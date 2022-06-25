package com.flink.streaming.web.mapper;

import com.flink.streaming.web.model.entity.IpStatus;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IpStatusMapper {


  int updateStatusByIp(IpStatus ipStatus);


  int insert(IpStatus ipStatus);


  IpStatus selectByIp(@Param("ip") String ip);


  List<IpStatus> selectAll();


  /**
   * 排序后获得id最的一条记录并且 where is_deleted = 0 AND STATUS = 1   and last_time >= DATE_ADD(NOW(),INTERVAL
   * -61 SECOND)
   *
   * @author zhuhuipei
   * @date 2020-09-22
   * @time 19:50
   */
  IpStatus selectLastIp();

}
