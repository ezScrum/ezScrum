-- --------------------------------------------------------
-- 主機:                           127.0.0.1
-- 服務器版本:                        5.7.8-rc-log - MySQL Community Server (GPL)
-- 服務器操作系統:                      Win64
-- HeidiSQL 版本:                  9.3.0.4984
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 導出  表 ezscrum_180.account 結構
DROP TABLE IF EXISTS `account`;
CREATE TABLE IF NOT EXISTS `account` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `nick_name` varchar(255) DEFAULT NULL,
  `email` text,
  `password` varchar(255) NOT NULL,
  `enable` tinyint(4) NOT NULL DEFAULT '1',
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.account 的資料：1 rows
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` (`id`, `username`, `nick_name`, `email`, `password`, `enable`, `create_time`, `update_time`) VALUES
	(1, 'admin', 'admin', 'example@ezScrum.tw', '21232f297a57a5a743894a0e4a801fc3', 1, 1379910191599, 1379910191599);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;


-- 導出  表 ezscrum_180.attach_file 結構
DROP TABLE IF EXISTS `attach_file`;
CREATE TABLE IF NOT EXISTS `attach_file` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `issue_id` bigint(20) unsigned NOT NULL,
  `issue_type` int(11) NOT NULL,
  `path` text NOT NULL,
  `content_type` text,
  `create_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.attach_file 的資料：0 rows
/*!40000 ALTER TABLE `attach_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `attach_file` ENABLE KEYS */;


-- 導出  表 ezscrum_180.history 結構
DROP TABLE IF EXISTS `history`;
CREATE TABLE IF NOT EXISTS `history` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `issue_id` bigint(20) unsigned NOT NULL,
  `issue_type` int(11) NOT NULL,
  `type` int(11) DEFAULT NULL,
  `old_value` text,
  `new_value` text,
  `create_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.history 的資料：24 rows
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
INSERT INTO `history` (`id`, `issue_id`, `issue_type`, `type`, `old_value`, `new_value`, `create_time`) VALUES
	(1, 1, 3, 1, '', '', 1454379491385),
	(2, 1, 3, 6, '0', '10', 1454379602072),
	(3, 1, 3, 7, '0', '10', 1454379602072),
	(4, 1, 3, 3, '0', '10', 1454379602072),
	(5, 2, 3, 1, '', '', 1454379620134),
	(6, 1, 3, 6, '10', '98', 1454379634824),
	(7, 3, 3, 1, '', '', 1454379650302),
	(8, 3, 3, 6, '0', '98', 1454379663031),
	(9, 3, 3, 3, '0', '10', 1454379663031),
	(10, 1, 3, 16, '', '1', 1454379697775),
	(11, 2, 3, 16, '', '1', 1454379697782),
	(12, 3, 3, 16, '', '1', 1454379697789),
	(13, 1, 1, 1, '', '', 1454379716424),
	(14, 1, 1, 16, '', '1', 1454379716455),
	(15, 1, 3, 17, '', '1', 1454379716457),
	(16, 2, 1, 1, '', '', 1454379730518),
	(17, 2, 1, 16, '', '2', 1454379730531),
	(18, 2, 3, 17, '', '2', 1454379730532),
	(19, 3, 1, 1, '', '', 1454379746750),
	(20, 3, 1, 16, '', '3', 1454379746762),
	(21, 3, 3, 17, '', '3', 1454379746764),
	(22, 1, 2, 1, '', '', 1454379911137),
	(23, 2, 2, 1, '', '', 1454379920011),
	(24, 3, 2, 1, '', '', 1454379928966);
/*!40000 ALTER TABLE `history` ENABLE KEYS */;


-- 導出  表 ezscrum_180.issue_partner_relation 結構
DROP TABLE IF EXISTS `issue_partner_relation`;
CREATE TABLE IF NOT EXISTS `issue_partner_relation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `issue_id` bigint(20) unsigned NOT NULL,
  `issue_type` int(11) NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.issue_partner_relation 的資料：0 rows
/*!40000 ALTER TABLE `issue_partner_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `issue_partner_relation` ENABLE KEYS */;


-- 導出  表 ezscrum_180.project 結構
DROP TABLE IF EXISTS `project`;
CREATE TABLE IF NOT EXISTS `project` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `display_name` varchar(255) NOT NULL,
  `comment` text,
  `product_owner` varchar(255) DEFAULT NULL,
  `attach_max_size` bigint(20) unsigned NOT NULL DEFAULT '2',
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.project 的資料：2 rows
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` (`id`, `name`, `display_name`, `comment`, `product_owner`, `attach_max_size`, `create_time`, `update_time`) VALUES
	(1, 'first', 'first', '', '', 2, 1454379355688, 1454379355688),
	(2, 'second', 'second', '', '', 2, 1454379966920, 1454379966920);
/*!40000 ALTER TABLE `project` ENABLE KEYS */;


-- 導出  表 ezscrum_180.project_role 結構
DROP TABLE IF EXISTS `project_role`;
CREATE TABLE IF NOT EXISTS `project_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) unsigned NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  `role` int(11) NOT NULL,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.project_role 的資料：0 rows
/*!40000 ALTER TABLE `project_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_role` ENABLE KEYS */;


-- 導出  表 ezscrum_180.release 結構
DROP TABLE IF EXISTS `release`;
CREATE TABLE IF NOT EXISTS `release` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `serial_id` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `project_id` bigint(20) unsigned NOT NULL,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.release 的資料：1 rows
/*!40000 ALTER TABLE `release` DISABLE KEYS */;
INSERT INTO `release` (`id`, `serial_id`, `name`, `description`, `start_date`, `end_date`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 1, 'first release', 'description', '2016-02-02 00:00:00', '2016-02-16 00:00:00', 1, 1454379820826, 1454379820826);
/*!40000 ALTER TABLE `release` ENABLE KEYS */;


-- 導出  表 ezscrum_180.retrospective 結構
DROP TABLE IF EXISTS `retrospective`;
CREATE TABLE IF NOT EXISTS `retrospective` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `serial_id` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text,
  `type` varchar(20) NOT NULL,
  `status` varchar(20) NOT NULL,
  `sprint_id` bigint(20) unsigned NOT NULL,
  `project_id` bigint(20) unsigned NOT NULL,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.retrospective 的資料：3 rows
/*!40000 ALTER TABLE `retrospective` DISABLE KEYS */;
INSERT INTO `retrospective` (`id`, `serial_id`, `name`, `description`, `type`, `status`, `sprint_id`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 1, 'retrosepctive1', '', 'Good', 'new', 1, 1, 1454379885431, 1454379885431),
	(2, 2, 'retrosepctive2', '', 'Good', 'new', 1, 1, 1454379890318, 1454379890318),
	(3, 3, 'retrosepctive3', '', 'Good', 'new', 1, 1, 1454379897854, 1454379897854);
/*!40000 ALTER TABLE `retrospective` ENABLE KEYS */;


-- 導出  表 ezscrum_180.scrum_role 結構
DROP TABLE IF EXISTS `scrum_role`;
CREATE TABLE IF NOT EXISTS `scrum_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `access_productBacklog` tinyint(4) NOT NULL DEFAULT '1',
  `access_sprintPlan` tinyint(4) NOT NULL DEFAULT '1',
  `access_taskboard` tinyint(4) NOT NULL DEFAULT '1',
  `access_sprintBacklog` tinyint(4) NOT NULL DEFAULT '1',
  `access_releasePlan` tinyint(4) NOT NULL DEFAULT '1',
  `access_retrospective` tinyint(4) NOT NULL DEFAULT '1',
  `access_unplan` tinyint(4) NOT NULL DEFAULT '1',
  `access_report` tinyint(4) NOT NULL DEFAULT '1',
  `access_editProject` tinyint(4) NOT NULL DEFAULT '1',
  `project_id` bigint(20) unsigned NOT NULL,
  `role` int(11) NOT NULL,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.scrum_role 的資料：10 rows
/*!40000 ALTER TABLE `scrum_role` DISABLE KEYS */;
INSERT INTO `scrum_role` (`id`, `access_productBacklog`, `access_sprintPlan`, `access_taskboard`, `access_sprintBacklog`, `access_releasePlan`, `access_retrospective`, `access_unplan`, `access_report`, `access_editProject`, `project_id`, `role`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1454379355702, 1454379355702),
	(2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1454379355703, 1454379355703),
	(3, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 2, 1454379355703, 1454379355703),
	(4, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 3, 1454379355704, 1454379355704),
	(5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 4, 1454379355704, 1454379355704),
	(6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1454379966920, 1454379966920),
	(7, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1454379966920, 1454379966920),
	(8, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 1454379966921, 1454379966921),
	(9, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 3, 1454379966921, 1454379966921),
	(10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 1454379966922, 1454379966922);
/*!40000 ALTER TABLE `scrum_role` ENABLE KEYS */;


-- 導出  表 ezscrum_180.serial_number 結構
DROP TABLE IF EXISTS `serial_number`;
CREATE TABLE IF NOT EXISTS `serial_number` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) unsigned NOT NULL,
  `release` bigint(20) unsigned NOT NULL,
  `sprint` bigint(20) unsigned NOT NULL,
  `story` bigint(20) unsigned NOT NULL,
  `task` bigint(20) unsigned NOT NULL,
  `unplan` bigint(20) unsigned NOT NULL,
  `retrospective` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.serial_number 的資料：2 rows
/*!40000 ALTER TABLE `serial_number` DISABLE KEYS */;
INSERT INTO `serial_number` (`id`, `project_id`, `release`, `sprint`, `story`, `task`, `unplan`, `retrospective`) VALUES
	(1, 1, 1, 3, 3, 3, 3, 3),
	(2, 2, 0, 3, 0, 0, 0, 0);
/*!40000 ALTER TABLE `serial_number` ENABLE KEYS */;


-- 導出  表 ezscrum_180.sprint 結構
DROP TABLE IF EXISTS `sprint`;
CREATE TABLE IF NOT EXISTS `sprint` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `serial_id` bigint(20) unsigned NOT NULL,
  `goal` text NOT NULL,
  `interval` int(11) NOT NULL,
  `team_size` int(11) NOT NULL,
  `available_hours` int(11) NOT NULL,
  `focus_factor` int(11) NOT NULL DEFAULT '100',
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `demo_date` datetime NOT NULL,
  `demo_place` text,
  `daily_info` text,
  `project_id` bigint(20) unsigned NOT NULL,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.sprint 的資料：5 rows
/*!40000 ALTER TABLE `sprint` DISABLE KEYS */;
INSERT INTO `sprint` (`id`, `serial_id`, `goal`, `interval`, `team_size`, `available_hours`, `focus_factor`, `start_date`, `end_date`, `demo_date`, `demo_place`, `daily_info`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 1, 'first sprint1', 2, 100, 100, 100, '2016-02-02 00:00:00', '2016-02-15 00:00:00', '2016-02-15 00:00:00', '', '', 1, 1454379421551, 1454379547452),
	(2, 2, 'first sprint2', 2, 100, 100, 100, '2016-02-02 00:00:00', '2016-02-15 00:00:00', '2016-02-15 00:00:00', '', '', 1, 1454379443928, 1454379556430),
	(3, 3, 'first sprint3', 2, 100, 100, 100, '2016-02-02 00:00:00', '2016-02-15 00:00:00', '2016-02-15 00:00:00', '', '', 1, 1454379462473, 1454379573525),
	(4, 1, 'second sprint1', 2, 4, 100, 100, '2016-02-29 00:00:00', '2016-03-13 00:00:00', '2016-03-13 00:00:00', '', '', 2, 1455507031999, 1455507031999),
	(6, 3, 'second sprint3', 2, 4, 100, 100, '2016-03-14 00:00:00', '2016-03-27 00:00:00', '2016-03-27 00:00:00', '', '', 2, 1455507308816, 1455507308816);
/*!40000 ALTER TABLE `sprint` ENABLE KEYS */;


-- 導出  表 ezscrum_180.story 結構
DROP TABLE IF EXISTS `story`;
CREATE TABLE IF NOT EXISTS `story` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) unsigned NOT NULL,
  `serial_id` bigint(20) unsigned NOT NULL,
  `sprint_id` bigint(20) DEFAULT NULL,
  `name` text NOT NULL,
  `status` tinyint(3) unsigned NOT NULL,
  `estimate` int(11) NOT NULL DEFAULT '0',
  `importance` int(11) NOT NULL DEFAULT '0',
  `value` int(11) NOT NULL DEFAULT '0',
  `notes` text,
  `how_to_demo` text,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.story 的資料：3 rows
/*!40000 ALTER TABLE `story` DISABLE KEYS */;
INSERT INTO `story` (`id`, `project_id`, `serial_id`, `sprint_id`, `name`, `status`, `estimate`, `importance`, `value`, `notes`, `how_to_demo`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 'story1', 1, 10, 98, 10, '', '', 1454379491385, 1454379697775),
	(2, 1, 2, 1, 'story2', 1, 10, 98, 10, '', '', 1454379620134, 1454379697782),
	(3, 1, 3, 1, 'story3', 1, 10, 98, 10, '', '', 1454379650302, 1454379697789);
/*!40000 ALTER TABLE `story` ENABLE KEYS */;


-- 導出  表 ezscrum_180.story_tag_relation 結構
DROP TABLE IF EXISTS `story_tag_relation`;
CREATE TABLE IF NOT EXISTS `story_tag_relation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tag_id` bigint(20) unsigned NOT NULL,
  `story_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.story_tag_relation 的資料：0 rows
/*!40000 ALTER TABLE `story_tag_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `story_tag_relation` ENABLE KEYS */;


-- 導出  表 ezscrum_180.system 結構
DROP TABLE IF EXISTS `system`;
CREATE TABLE IF NOT EXISTS `system` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_id_UNIQUE` (`account_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.system 的資料：1 rows
/*!40000 ALTER TABLE `system` DISABLE KEYS */;
INSERT INTO `system` (`id`, `account_id`) VALUES
	(1, 1);
/*!40000 ALTER TABLE `system` ENABLE KEYS */;


-- 導出  表 ezscrum_180.tag 結構
DROP TABLE IF EXISTS `tag`;
CREATE TABLE IF NOT EXISTS `tag` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `project_id` bigint(20) unsigned NOT NULL,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.tag 的資料：0 rows
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;


-- 導出  表 ezscrum_180.task 結構
DROP TABLE IF EXISTS `task`;
CREATE TABLE IF NOT EXISTS `task` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `serial_id` bigint(20) unsigned NOT NULL,
  `project_id` bigint(20) unsigned NOT NULL,
  `story_id` bigint(20) DEFAULT NULL,
  `name` text NOT NULL,
  `handler_id` bigint(20) DEFAULT NULL,
  `status` tinyint(3) unsigned NOT NULL,
  `estimate` int(11) NOT NULL DEFAULT '0',
  `remain` int(11) NOT NULL DEFAULT '0',
  `actual` int(11) NOT NULL DEFAULT '0',
  `notes` text,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.task 的資料：3 rows
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
INSERT INTO `task` (`id`, `serial_id`, `project_id`, `story_id`, `name`, `handler_id`, `status`, `estimate`, `remain`, `actual`, `notes`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 'task1', -1, 1, 10, 10, 0, 'note', 1454379716424, 1454379716424),
	(2, 2, 1, 2, 'task2', -1, 1, 10, 10, 0, 'note', 1454379730518, 1454379730518),
	(3, 3, 1, 3, 'task3', -1, 1, 10, 10, 0, 'note', 1454379746750, 1454379746750);
/*!40000 ALTER TABLE `task` ENABLE KEYS */;


-- 導出  表 ezscrum_180.token 結構
DROP TABLE IF EXISTS `token`;
CREATE TABLE IF NOT EXISTS `token` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) unsigned NOT NULL,
  `public_token` text NOT NULL,
  `private_token` text NOT NULL,
  `platform_type` text NOT NULL,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.token 的資料：0 rows
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
/*!40000 ALTER TABLE `token` ENABLE KEYS */;


-- 導出  表 ezscrum_180.unplan 結構
DROP TABLE IF EXISTS `unplan`;
CREATE TABLE IF NOT EXISTS `unplan` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `serial_id` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `handler_id` bigint(20) NOT NULL,
  `estimate` int(11) NOT NULL,
  `actual` int(11) NOT NULL,
  `notes` text NOT NULL,
  `status` tinyint(3) unsigned NOT NULL,
  `project_id` bigint(20) unsigned NOT NULL,
  `sprint_id` bigint(20) unsigned NOT NULL,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.unplan 的資料：3 rows
/*!40000 ALTER TABLE `unplan` DISABLE KEYS */;
INSERT INTO `unplan` (`id`, `serial_id`, `name`, `handler_id`, `estimate`, `actual`, `notes`, `status`, `project_id`, `sprint_id`, `create_time`, `update_time`) VALUES
	(1, 1, 'unplanned1', -1, 0, 0, '', 1, 1, 1, 1454379911137, 1454379911137),
	(2, 2, 'unplanned2', -1, 0, 0, '', 1, 1, 1, 1454379920011, 1454379920011),
	(3, 3, 'unplanned3', -1, 0, 0, '', 1, 1, 1, 1454379928966, 1454379928966);
/*!40000 ALTER TABLE `unplan` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
