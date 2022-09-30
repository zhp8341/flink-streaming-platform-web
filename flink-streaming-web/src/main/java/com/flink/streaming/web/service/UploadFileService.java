package com.flink.streaming.web.service;

import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.dto.UploadFileDTO;
import com.flink.streaming.web.model.param.UploadFileParam;

public interface UploadFileService {

  void addFile(UploadFileDTO uploadFileDTO);

  void deleteFile(Long id);

  PageModel<UploadFileDTO> queryUploadFile(UploadFileParam uploadFileParam);

  UploadFileDTO getUploadFileByFileName(String fileName);

}
