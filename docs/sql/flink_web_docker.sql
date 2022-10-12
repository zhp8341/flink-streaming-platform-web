
CREATE DATABASE IF NOT EXISTS flink_web_docker default charset utf8 COLLATE utf8_general_ci;

use flink_web_docker;


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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警发送情况日志';

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='web服务运行ip';

-- ----------------------------
-- Records of ip_status
-- ----------------------------
BEGIN;
INSERT INTO `ip_status` VALUES (1, '172.17.0.2', 1, '2021-09-24 23:46:00', 0, '2021-09-24 23:38:59', '2021-09-24 23:45:59', 'sys', 'sys');
COMMIT;

-- ----------------------------
-- Table structure for job_alarm_config
-- ----------------------------
DROP TABLE IF EXISTS `job_alarm_config`;
CREATE TABLE `job_alarm_config` (
                                    `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
                                    `job_id` bigint(11) unsigned NOT NULL COMMENT 'job_config主表id',
                                    `type` tinyint(1) NOT NULL COMMENT '类型 1:钉钉告警 2:url回调 3:异常自动拉起任务',
                                    `version` int(11) NOT NULL DEFAULT '0' COMMENT '更新版本号  ',
                                    `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `edit_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `creator` varchar(32) DEFAULT 'sys',
                                    `editor` varchar(32) DEFAULT 'sys',
                                    PRIMARY KEY (`id`),
                                    KEY `uk_index_job_id` (`job_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警辅助配置表';

-- ----------------------------
-- Table structure for job_config
-- ----------------------------
DROP TABLE IF EXISTS `job_config`;
CREATE TABLE `job_config` (
                              `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
                              `job_name` varchar(64) NOT NULL COMMENT '任务名称',
                              `deploy_mode` varchar(64) NOT NULL COMMENT '提交模式: standalone 、yarn 、yarn-session ',
                              `flink_run_config` varchar(512) NOT NULL COMMENT 'flink运行配置',
                              `flink_sql` mediumtext NOT NULL COMMENT 'sql语句',
                              `flink_checkpoint_config` varchar(512) DEFAULT NULL COMMENT 'checkPoint配置',
                              `job_id` varchar(64) DEFAULT NULL COMMENT '运行后的任务id',
                              `is_open` tinyint(1) NOT NULL COMMENT '1:开启 0: 关闭',
                              `status` tinyint(1) NOT NULL COMMENT '1:运行中 0: 停止中 -1:运行失败',
                              `ext_jar_path` varchar(2048) DEFAULT NULL COMMENT 'udf地址已经连接器jar 如http://xxx.xxx.com/flink-streaming-udf.jar',
                              `last_start_time` datetime DEFAULT NULL COMMENT '最后一次启动时间',
                              `last_run_log_id` bigint(11) DEFAULT NULL COMMENT '最后一次日志',
                              `version` int(11) NOT NULL DEFAULT '0' COMMENT '更新版本号 用于乐观锁',
                              `job_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务类型 0:sql 1:自定义jar',
                              `custom_args` varchar(128) DEFAULT NULL COMMENT '启动jar可能需要使用的自定义参数',
                              `custom_main_class` varchar(128) DEFAULT NULL COMMENT '程序入口类',
                              `custom_jar_url` varchar(128) DEFAULT NULL COMMENT '自定义jar的http地址 如:http://ccblog.cn/xx.jar',
                              `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `edit_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                              `creator` varchar(32) DEFAULT 'sys',
                              `editor` varchar(32) DEFAULT 'sys',
                              PRIMARY KEY (`id`),
                              KEY `uk_index` (`job_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='flink任务配置表';
ALTER TABLE job_config ADD COLUMN job_desc VARCHAR(255) NULL COMMENT '任务描述' AFTER job_name;

-- ----------------------------
-- Records of job_config
-- ----------------------------
BEGIN;
INSERT INTO `job_config` VALUES (1, 'test_datagen_simple', '任务表述-test_datagen_simple', 'LOCAL', '', 'CREATE TABLE source_table (\n    f0 INT,\n    f1 INT,\n    f2 STRING\n) WITH (\n    \'connector\' = \'datagen\',\n    \'rows-per-second\'=\'5\'\n);\nCREATE TABLE print_table (\n    f0 INT,\n    f1 INT,\n    f2 STRING\n) WITH (\n    \'connector\' = \'print\'\n);\ninsert into print_table select f0,f1,f2 from source_table;\n', '', '', 1, 0, NULL, '2021-09-24 23:15:21', 227, 77, 0, NULL, NULL, NULL, 0, '2021-04-06 10:24:30', '2021-09-24 23:45:30', NULL, 'admin');
COMMIT;

-- ----------------------------
-- Table structure for job_config_history
-- ----------------------------
DROP TABLE IF EXISTS `job_config_history`;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='flink任务配置历史变更表';
ALTER TABLE job_config_history ADD COLUMN job_desc VARCHAR(255) NULL COMMENT '任务描述' AFTER job_name;
ALTER TABLE job_config_history add `job_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务类型 0:sql 1:自定义jar' AFTER version ;


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
                               `run_ip` varchar(64) DEFAULT NULL COMMENT '任务运行所在的机器',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运行任务日志';
ALTER TABLE job_run_log ADD COLUMN job_desc VARCHAR(255) NULL COMMENT '任务描述' AFTER job_name;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='savepoint备份地址';

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='系统配置';

-- ----------------------------
-- Records of system_config
-- ----------------------------
BEGIN;
INSERT INTO `system_config` VALUES (1, 'flink_home', '/data/projects/flink/', 'SYS', 0, '2020-11-12 10:42:02', '2021-09-24 22:11:01', 'sys', 'sys');
INSERT INTO `system_config` VALUES (2, 'flink_rest_http_address', 'http://127.0.0.1:8081/', 'SYS', 0, '2020-11-04 11:23:49', '2020-12-16 20:32:33', 'sys', 'sys');
INSERT INTO `system_config` VALUES (3, 'flink_streaming_platform_web_home', '/data/projects/flink-streaming-platform-web/', 'SYS', 0, '2020-10-16 16:08:58', '2021-09-24 22:12:42', 'sys', 'sys');
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `username` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '用户帐号',
                        `password` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '密码',
                        `status` tinyint(1) NOT NULL COMMENT '1:启用 0: 停用',
                        `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:删除 0: 未删除',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `edit_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                        `creator` varchar(32) COLLATE utf8mb4_bin DEFAULT 'sys',
                        `editor` varchar(32) COLLATE utf8mb4_bin DEFAULT 'sys',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `index_uk` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
ALTER TABLE user ADD COLUMN `name` VARCHAR(100) NOT NULL COMMENT '用户姓名' AFTER `username`;



-- ----------------------------
-- Records of user 默认密码是 123456
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (1, 'admin', '系统管理员', 'e10adc3949ba59abbe56e057f20f883e', 1, 0, '2020-07-10 22:15:04', '2020-07-24 22:21:35', 'sys', 'sys');
COMMIT;

CREATE TABLE `upload_file` (
                               `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
                               `file_name` varchar(128) DEFAULT NULL COMMENT '文件名字',
                               `file_path` varchar(512) DEFAULT NULL COMMENT '文件路径',
                               `type` int(11) NOT NULL DEFAULT '1' COMMENT '1:jar',
                               `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `edit_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                               `creator` varchar(32) DEFAULT 'sys',
                               `editor` varchar(32) DEFAULT 'sys',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='上传文件管理';