package com.flink.streaming.web.model.dto;

import com.flink.streaming.web.common.util.DateFormatUtils;
import com.flink.streaming.web.model.entity.UploadFile;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2022/09/19
 */
@Data
public class UploadFileDTO {

  private Long id;
  /**
   * 文件名字
   */
  private String fileName;

  /**
   * 文件路径
   */
  private String filePath;

  /**
   * 1:jar
   */
  private Integer type;

  /**
   * 创建时间
   */
  private Date createTime;

  private String createTimeStr;

  private String creator;

  private String editor;

  public static UploadFile toEntity(UploadFileDTO uploadFileDTO) {
    if (uploadFileDTO == null) {
      return null;
    }
    UploadFile uploadFile = new UploadFile();
    uploadFile.setId(uploadFileDTO.getId());
    uploadFile.setFileName(uploadFileDTO.getFileName());
    uploadFile.setFilePath(uploadFileDTO.getFilePath());
    uploadFile.setType(uploadFileDTO.getType());
    uploadFile.setCreateTime(uploadFileDTO.getCreateTime());
    uploadFile.setCreator(uploadFileDTO.getCreator());
    uploadFile.setEditor(uploadFileDTO.getEditor());
    return uploadFile;
  }


  public static UploadFileDTO toDTO(UploadFile uploadFile) {
    if (uploadFile == null) {
      return null;
    }
    UploadFileDTO uploadFileDTO = new UploadFileDTO();
    uploadFileDTO.setId(uploadFile.getId());
    uploadFileDTO.setFileName(uploadFile.getFileName());
    uploadFileDTO.setFilePath(uploadFile.getFilePath());
    uploadFileDTO.setType(uploadFile.getType());
    if (uploadFile.getCreateTime() != null) {
      uploadFileDTO.setCreateTime(uploadFile.getCreateTime());
      uploadFileDTO.setCreateTimeStr(DateFormatUtils.toFormatString(uploadFile.getCreateTime()));
    }
    return uploadFileDTO;
  }

  public static List<UploadFileDTO> toDTOList(List<UploadFile> uploadFileList) {
    if (CollectionUtils.isEmpty(uploadFileList)) {
      return Collections.emptyList();
    }
    List<UploadFileDTO> list = Lists.newArrayList();
    for (UploadFile uploadFile : uploadFileList) {
      list.add(toDTO(uploadFile));
    }
    return list;
  }


}
