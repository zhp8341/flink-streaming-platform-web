package com.flink.streaming.web.controller.api;

import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.common.enums.FileTypeEnum;
import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.controller.web.BaseController;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.dto.UploadFileDTO;
import com.flink.streaming.web.model.param.UploadFileParam;
import com.flink.streaming.web.model.vo.PageVO;
import com.flink.streaming.web.service.UploadFileService;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2022/09/19
 */

@RestController
@RequestMapping("/api")
@Slf4j
public class UploadApiController extends BaseController {

  @Autowired
  private UploadFileService uploadFileService;

//  @Autowired
//  private SystemConfigService systemConfigService;


  @ResponseBody
  @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = "multipart/form-data")
  public RestResult<?> upload(@RequestPart MultipartFile file)  {
    try {
      String uploadPath = this.getJarRootPath();
      log.info("uploadPath={}",uploadPath);
      File uploadDir = new File(uploadPath);
      if (!uploadDir.exists()) {
        uploadDir.mkdirs();
      }
      String originalFilename = file.getOriginalFilename();
      File localFile = new File(uploadPath + originalFilename);
      file.transferTo(localFile);
      UploadFileDTO uploadFileDTO = new UploadFileDTO();
      uploadFileDTO.setFileName(originalFilename);
      uploadFileDTO.setFilePath(localFile.getPath());
      uploadFileDTO.setType(FileTypeEnum.JAR.getCode());
      uploadFileDTO.setEditor(this.getUserName());
      uploadFileDTO.setCreator(this.getUserName());
      uploadFileService.addFile(uploadFileDTO);
      return RestResult.success();
    } catch (Exception e) {
      log.error("upload is error", e);
      return RestResult.error("上传失败 : " + e.getMessage());
    }
  }

  @RequestMapping("/deleteFile")
  public RestResult<?> deleteFile(Long id) {
    try {
      uploadFileService.deleteFile(id);
      return RestResult.success();
    } catch (Exception e) {
      log.error("deleteFile is error", e);
      return RestResult.error("deleteFile is  error : " + e.getMessage());
    }
  }

  @RequestMapping(value = "/queryUploadFile")
  public RestResult<?> queryUploadFile(UploadFileParam uploadFileParam) {
    try {
      PageModel<UploadFileDTO> pageModel = uploadFileService.queryUploadFile(uploadFileParam);
      PageVO pageVO = new PageVO();
      pageVO.setPageNum(pageModel.getPageNum());
      pageVO.setPages(pageModel.getPages());
      pageVO.setPageSize(pageModel.getPageSize());
      pageVO.setTotal(pageModel.getTotal());
      pageVO.setData(pageModel);
      return RestResult.success(pageVO);
    } catch (Exception e) {
      log.error("queryUploadFile is error", e);
      return RestResult.error("queryUploadFile is  error : " + e.getMessage());
    }
  }



  private String getJarRootPath() throws Exception {
    return ResourceUtils.getURL("classpath:").getPath() + SystemConstant.JAR_ROOT_PATH;
  }


}
