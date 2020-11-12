package com.flink.streaming.web.mapper;

import com.flink.streaming.web.model.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {

    User selectByUsername(@Param("username") String username);

    int insert(User user);

    int updateByPrimaryKeySelective(User user);

    List<User> findAll();

}
