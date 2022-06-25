package com.flink.streaming.web.model.dto;

import com.flink.streaming.web.model.entity.User;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author zhuhuipei
 * @date 2020-07-10
 * @time 00:03
 */
@Data
public class UserDTO {

  /**
   * 用户编号
   */
  private Integer id;

  /**
   * 用户帐号
   */
  private String username;

  /**
   * 用户名称
   */
  private String name;

  /**
   * 密码
   */
  private String password;

  /**
   * @see com.flink.streaming.web.enums.UserStatusEnum 1:启用 0: 停用
   */
  private Integer status;

  /**
   * 创建时间
   */
  private Date createTime;

  /**
   * 修改时间
   */
  private Date editTime;

  private String creator;

  private String editor;


  public static UserDTO toDTO(User user) {
    if (user == null) {
      return null;
    }
    UserDTO userDTO = new UserDTO();
    userDTO.setId(user.getId());
    userDTO.setUsername(user.getUsername());
    userDTO.setName(user.getName());
    userDTO.setPassword(user.getPassword());
    userDTO.setStatus(user.getStatus());
    userDTO.setCreateTime(user.getCreateTime());
    userDTO.setEditTime(user.getEditTime());
    userDTO.setCreator(user.getCreator());
    userDTO.setEditor(user.getEditor());
    return userDTO;
  }


  public static List<UserDTO> toListDTO(List<User> userList) {
    if (CollectionUtils.isEmpty(userList)) {
      return Collections.EMPTY_LIST;
    }
    List<UserDTO> list = new ArrayList<>();
    for (User user : userList) {
      if (user == null) {
        continue;
      }
      list.add(UserDTO.toDTO(user));
    }
    return list;
  }

}
