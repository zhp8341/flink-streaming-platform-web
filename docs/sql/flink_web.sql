
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for alart_log
-- ----------------------------
DROP TABLE IF EXISTS `alart_log`;
CREATE TABLE `alart_log` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `job_config_id` bigint(11) NOT NULL COMMENT 'job_config的id  如果0代表的是测试,',
  `job_name` varchar(255) DEFAULT NULL,
  `message` varchar(512) DEFAULT NULL COMMENT '消息内容',
  `type` tinyint(1) NOT NULL COMMENT '1:钉钉',
  `status` tinyint(1) NOT NULL COMMENT '1:成功 0:失败',
  `fail_log` text COMMENT '失败原因',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `edit_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(32) DEFAULT 'sys',
  `editor` varchar(32) DEFAULT 'sys',
  PRIMARY KEY (`id`),
  KEY `index_job_config_id` (`job_config_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='告警发送情况日志';

-- ----------------------------
-- Table structure for ip_status
-- ----------------------------
DROP TABLE IF EXISTS `ip_status`;
CREATE TABLE `ip_status` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `ip` varchar(64) NOT NULL COMMENT 'ip',
  `status` int(11) NOT NULL COMMENT '1:运行 -1:停止 ',
  `last_time` datetime DEFAULT NULL COMMENT '最后心跳时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(32) NOT NULL DEFAULT 'sys',
  `editor` varchar(32) NOT NULL DEFAULT 'sys',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_uk_ip` (`ip`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='web服务运行ip';

-- ----------------------------
-- Table structure for job_config
-- ----------------------------
DROP TABLE IF EXISTS `job_config`;
CREATE TABLE `job_config` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `deploy_mode` varchar(64) NOT NULL COMMENT '提交模式: standalone 、yarn 、yarn-session ',
  `flink_run_config` varchar(512) NOT NULL COMMENT 'flink运行配置',
  `flink_sql` MEDIUMTEXT NOT NULL COMMENT 'sql语句',
  `flink_checkpoint_config` varchar(512) DEFAULT NULL COMMENT 'checkPoint配置',
  `job_id` varchar(64) DEFAULT NULL COMMENT '运行后的任务id',
  `is_open` tinyint(1) NOT NULL COMMENT '1:开启 0: 关闭',
  `status` tinyint(1) NOT NULL COMMENT '1:运行中 0: 停止中 -1:运行失败',
  `ext_jar_path` varchar(2048) DEFAULT NULL COMMENT 'udf地址已经连接器jar 如http://xxx.xxx.com/flink-streaming-udf.jar',
  `last_start_time` datetime DEFAULT NULL COMMENT '最后一次启动时间',
  `last_run_log_id` bigint(11) DEFAULT NULL COMMENT '最后一次日志',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '更新版本号 用于乐观锁',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `edit_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(32) DEFAULT 'sys',
  `editor` varchar(32) DEFAULT 'sys',
  PRIMARY KEY (`id`),
  KEY `uk_index` (`job_name`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='flink任务配置表';

ALTER TABLE job_config add `job_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务类型 0:sql 1:自定义jar' AFTER version ;
ALTER TABLE job_config add `custom_args` varchar(128)  DEFAULT NULL COMMENT '启动jar可能需要使用的自定义参数' AFTER job_type;
ALTER TABLE job_config add `custom_main_class` varchar(128)  DEFAULT NULL COMMENT '程序入口类' AFTER custom_args;
ALTER TABLE job_config add `custom_jar_url` varchar(128)  DEFAULT NULL   COMMENT'自定义jar的http地址 如:http://ccblog.cn/xx.jar' AFTER custom_main_class;


-- ----------------------------
-- Table structure for job_config_history
-- ----------------------------
CREATE TABLE `job_config_history` (
                                      `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
                                      `job_config_id` bigint(11) NOT NULL COMMENT 'job_config主表Id',
                                      `job_name` varchar(64) NOT NULL COMMENT '任务名称',
                                      `deploy_mode` varchar(64) NOT NULL COMMENT '提交模式: standalone 、yarn 、yarn-session ',
                                      `flink_run_config` varchar(512) NOT NULL COMMENT 'flink运行配置',
                                      `flink_sql` mediumtext NOT NULL COMMENT 'sql语句',
                                      `flink_checkpoint_config` varchar(512) DEFAULT NULL COMMENT 'checkPoint配置',
                                      `ext_jar_path` varchar(2048) DEFAULT NULL COMMENT 'udf地址及连接器jar 如http://xxx.xxx.com/flink-streaming-udf.jar',
                                      `version` int(11) NOT NULL DEFAULT '0' COMMENT '更新版本号',
                                      `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `edit_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                      `creator` varchar(32) DEFAULT 'sys',
                                      `editor` varchar(32) DEFAULT 'sys',
                                      PRIMARY KEY (`id`),
                                      KEY `index_job_config_id` (`job_config_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='flink任务配置历史变更表';



-- ----------------------------
-- Table structure for job_run_log
-- ----------------------------
DROP TABLE IF EXISTS `job_run_log`;
CREATE TABLE `job_run_log` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `job_config_id` bigint(11) NOT NULL,
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `deploy_mode` varchar(64) NOT NULL COMMENT '提交模式: standalone 、yarn 、yarn-session ',
  `job_id` varchar(64) DEFAULT NULL COMMENT '运行后的任务id',
  `local_log` mediumtext COMMENT '启动时本地日志',
  `remote_log_url` varchar(128) DEFAULT NULL COMMENT '远程日志url的地址',
  `start_time` datetime DEFAULT NULL COMMENT '启动时间',
  `end_time` datetime DEFAULT NULL COMMENT '启动时间',
  `job_status` varchar(32) DEFAULT NULL COMMENT '任务状态',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `edit_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(32) DEFAULT 'sys',
  `editor` varchar(32) DEFAULT 'sys',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='运行任务日志';

ALTER TABLE job_run_log add `run_ip`  varchar(64) DEFAULT NULL COMMENT '任务运行所在的机器' AFTER local_log ;


-- ----------------------------
-- Table structure for savepoint_backup
-- ----------------------------
DROP TABLE IF EXISTS `savepoint_backup`;
CREATE TABLE `savepoint_backup` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `job_config_id` bigint(11) NOT NULL,
  `savepoint_path` varchar(2048) NOT NULL COMMENT '地址',
  `backup_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '备份时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `edit_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(32) DEFAULT 'sys',
  `editor` varchar(32) DEFAULT 'sys',
  PRIMARY KEY (`id`),
  KEY `index` (`job_config_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='savepoint备份地址';



-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(128) NOT NULL COMMENT 'key值',
  `val` varchar(512) NOT NULL COMMENT 'value',
  `type` varchar(12) NOT NULL COMMENT '类型 如:sys  alarm',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `edit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(32) NOT NULL DEFAULT 'sys',
  `editor` varchar(32) NOT NULL DEFAULT 'sys',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置';



-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '密码',
  `stauts` tinyint(1) NOT NULL COMMENT '1:启用 0: 停用',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:删除 0: 未删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `edit_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(32) COLLATE utf8mb4_bin DEFAULT 'sys',
  `editor` varchar(32) COLLATE utf8mb4_bin DEFAULT 'sys',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_uk` (`username`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;



CREATE TABLE `job_alarm_config`
(
    `id`          bigint(11) unsigned NOT NULL AUTO_INCREMENT,
    `job_id`      bigint(11) unsigned NOT NULL COMMENT 'job_config主表id',
    `type`        tinyint(1)          NOT NULL COMMENT '类型 1:钉钉告警 2:url回调 3:异常自动拉起任务',
    `version`     int(11)             NOT NULL DEFAULT '0' COMMENT '更新版本号  ',
    `is_deleted`  tinyint(1)          NOT NULL DEFAULT '0',
    `create_time` datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `edit_time`   datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `creator`     varchar(32)                  DEFAULT 'sys',
    `editor`      varchar(32)                  DEFAULT 'sys',
    PRIMARY KEY (`id`),
    KEY `uk_index_job_id` (`job_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='告警辅助配置表';

-- ----------------------------
-- Records of user 默认密码是 123456
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, 0, '2020-07-10 22:15:04', '2020-07-24 22:21:35', 'sys', 'sys');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
