package com.flink.streaming.web.model.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author zhuhuipei
 * @date 2020-07-10
 * @time 00:03
 */
@Data
public class User {

    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * @see com.flink.streaming.web.enums.UserStatusEnum
     * 1:启用 0: 停用
     */
    private Integer stauts;

    /**
     * 1:删除 0: 未删除
     */
    private Integer isDeleted;

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

}
