package com.flink.streaming.web.service;

import com.flink.streaming.common.enums.FileTypeEnum;
import com.flink.streaming.web.base.TestRun;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.dto.UploadFileDTO;
import com.flink.streaming.web.model.param.UploadFileParam;
import java.util.UUID;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2022/09/20
 */
public class TestUploadFileService extends TestRun {

  @Autowired
  private UploadFileService uploadFileService;


  @Test
  public void  add(){
    for (int i = 0; i < 18; i++) {
      UploadFileDTO uploadFileDTO=new UploadFileDTO();
      uploadFileDTO.setFileName(UUID.randomUUID().toString()+".jar");
      uploadFileDTO.setFilePath("/Users/edy/git/flink-streaming-platform-web/test_file/1.jar");
      uploadFileDTO.setType(FileTypeEnum.JAR.getCode());
      uploadFileService.addFile(uploadFileDTO);
    }
  }

  @Test
  public void  deleteFile(){
    System.out.println(System.getProperty("user.dir"));
    uploadFileService.deleteFile(1L);
  }

  @Test
  public void  queryUploadFile(){
    UploadFileParam uploadFileParam=new UploadFileParam();
    uploadFileParam.setFileName("f233aeb6");
    PageModel<UploadFileDTO>  page=uploadFileService.queryUploadFile(uploadFileParam);
    System.out.println(page);
    System.out.println(page.getResult());
  }

}
