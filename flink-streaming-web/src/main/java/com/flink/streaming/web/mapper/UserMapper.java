package com.flink.streaming.web.mapper;

import com.flink.streaming.web.model.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    User selectByUsername(@Param("username") String username);

}
