package com.flink.streaming.web.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author
 */
@Data
public class IpStatus implements Serializable {

    private Long id;

    /**
     * ip
     */
    private String ip;

    /**
     * 1:运行 2:停止
     *
     * @see com.flink.streaming.web.enums.IpStatusEnum
     */
    private Integer status;

    /**
     * 最后一次启动时间
     */
    private Date lastTime;


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

    private static final long serialVersionUID = 1L;

}
