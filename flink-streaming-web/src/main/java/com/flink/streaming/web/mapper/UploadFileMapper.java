package com.flink.streaming.web.mapper;


import com.flink.streaming.web.model.entity.UploadFile;
import com.flink.streaming.web.model.param.UploadFileParam;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadFileMapper {

  int deleteById(@Param("id") Long id);

  UploadFile getFileByName(@Param("fileName") String fileName);

  UploadFile getFileById(@Param("id") Long id);

  int insert(UploadFile uploadFile);

  Page<UploadFile> findFilesByPage(UploadFileParam uploadFileParam);

}