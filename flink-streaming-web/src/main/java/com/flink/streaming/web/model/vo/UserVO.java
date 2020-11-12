package com.flink.streaming.web.model.vo;

import cn.hutool.core.collection.CollectionUtil;
import com.flink.streaming.web.common.util.DateFormatUtils;
import com.flink.streaming.web.enums.UserStatusEnum;
import com.flink.streaming.web.model.dto.UserDTO;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020/11/12
 * @time 23:40
 */
@Data
public class UserVO {

    /**
     * 用户名
     */
    private String username;

    /**
     * @see com.flink.streaming.web.enums.UserStatusEnum
     * 1:启用 0: 停用
     */
    private Integer stauts;


    /**
     * 状态描述
     */
    private String stautsDesc;


    /**
     * 创建时间
     */
    private String createTimeStr;

    /**
     * 修改时间
     */
    private String editTimeStr;


    public static UserVO toVO(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        userVO.setUsername(userDTO.getUsername());
        userVO.setStauts(userDTO.getStauts());
        userVO.setStautsDesc(UserStatusEnum.getStatus(userDTO.getStauts()).getDesc());
        userVO.setCreateTimeStr(DateFormatUtils.toFormatString(userDTO.getCreateTime()));
        userVO.setEditTimeStr(DateFormatUtils.toFormatString(userDTO.getEditTime()));
        return userVO;
    }

    public static List<UserVO> toListVO(List<UserDTO> userDTOList) {
        if (CollectionUtil.isEmpty(userDTOList)) {
            return Collections.EMPTY_LIST;
        }
        List<UserVO> list = CollectionUtil.newArrayList();

        for (UserDTO userDTO : userDTOList) {
            if (userDTO == null) {
                continue;
            }
            list.add(UserVO.toVO(userDTO));
        }
        return list;
    }


}
