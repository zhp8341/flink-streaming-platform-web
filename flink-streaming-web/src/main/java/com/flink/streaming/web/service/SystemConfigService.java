package com.flink.streaming.web.service;

import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.SysConfigEnumType;
import com.flink.streaming.web.model.dto.SystemConfigDTO;

import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-20
 * @time 01:06
 */
public interface SystemConfigService {


    /**
     * 新增或者修改配置
     *
     * @author zhuhuipei
     * @date 2020-08-06
     * @time 23:48
     */
    void addOrUpdateConfigByKey(String key, String value);


    /**
     * 查询配置
     *
     * @author zhuhuipei
     * @date 2020-07-20
     * @time 01:11
     */
    List<SystemConfigDTO> getSystemConfig(SysConfigEnumType sysConfigEnumType);


    /**
     * 删除一个配置
     *
     * @author zhuhuipei
     * @date 2020-08-06
     * @time 20:18
     */
    void deleteConfigByKey(String key);


    /**
     * 根据key获取配置的值
     *
     * @author zhuhuipei
     * @date 2020-08-06
     * @time 20:21
     */
    String getSystemConfigByKey(String key);


    /**
     * 获取yarn的rm Http地址
     *
     * @author zhuhuipei
     * @date 2020-09-18
     * @time 01:23
     */
    String getYarnRmHttpAddress();


    /**
     * 获取flink地址
     *
     * @author zhuhuipei
     * @date 2020/11/4
     * @time 10:52
     */
    String getFlinkHttpAddress(DeployModeEnum deployModeEnum);


    /**
     * 检查配置是否存在
     *
     * @author zhuhuipei
     * @date 2020-10-14
     * @time 21:41
     */
    boolean isExist(String key);


    /**
     * 是否自动开启savepoint （默认是true）
     * @return
     */
    boolean autoSavepoint();

}
