-- 老版本升级需要的sql
ALTER TABLE job_config ADD COLUMN job_desc VARCHAR(255) NULL COMMENT '任务描述' AFTER job_name;
ALTER TABLE job_config_history ADD COLUMN job_desc VARCHAR(255) NULL COMMENT '任务描述' AFTER job_name;
ALTER TABLE job_config_history add `job_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '任务类型 0:sql 1:自定义jar' AFTER version ;
ALTER TABLE job_run_log ADD COLUMN job_desc VARCHAR(255) NULL COMMENT '任务描述' AFTER job_name;
ALTER TABLE user ADD COLUMN `name` VARCHAR(100) NOT NULL COMMENT '用户姓名' AFTER `username`;
ALTER TABLE `user` ADD COLUMN `status` tinyint(1) NOT NULL COMMENT '1:启用 0: 停用' AFTER stauts; -- 修正status字段命名，兼容处理，只增加字段
ALTER TABLE `user` modify COLUMN `stauts` tinyint(1) NOT NULL DEFAULT 0 COMMENT '1:启用 0: 停用';
ALTER TABLE `user` modify COLUMN `username` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '用户帐号';
update user set name='系统管理员' where id=1;
update user set status=1 where id=1;
update job_config_history a, job_config b set a.job_type=b.job_type where a.job_config_id=b.id;