-- --------------------------------------------------------
-- 主機:                           127.0.0.1
-- 服務器版本:                        5.7.8-rc-log - MySQL Community Server (GPL)
-- 服務器操作系統:                      Win64
-- HeidiSQL 版本:                  9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- 導出 ezscrum_180 的資料庫結構
CREATE DATABASE IF NOT EXISTS `ezscrum_180` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ezscrum_180`;

-- 導出  表 ezscrum_180.account 結構
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
REPLACE INTO `account` (`id`, `username`, `nick_name`, `email`, `password`, `enable`, `create_time`, `update_time`) VALUES
	(1, 'admin', 'admin', 'example@ezScrum.tw', '21232f297a57a5a743894a0e4a801fc3', 1, 1379910191599, 1379910191599);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;

-- 導出  表 ezscrum_180.attach_file 結構
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
CREATE TABLE IF NOT EXISTS `history` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `issue_id` bigint(20) unsigned NOT NULL,
  `issue_type` int(11) NOT NULL,
  `type` int(11) DEFAULT NULL,
  `old_value` text,
  `new_value` text,
  `create_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.history 的資料：6 rows
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
REPLACE INTO `history` (`id`, `issue_id`, `issue_type`, `type`, `old_value`, `new_value`, `create_time`) VALUES
	(1, 1, 3, 1, '', '', 1496888088804),
	(2, 2, 3, 1, '', '', 1496888125984),
	(3, 3, 3, 1, '', '', 1496888139344),
	(4, 4, 3, 1, '', '', 1496888173158),
	(5, 5, 3, 1, '', '', 1496888180470),
	(6, 6, 3, 1, '', '', 1496888191430);
/*!40000 ALTER TABLE `history` ENABLE KEYS */;

-- 導出  表 ezscrum_180.issue_partner_relation 結構
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
REPLACE INTO `project` (`id`, `name`, `display_name`, `comment`, `product_owner`, `attach_max_size`, `create_time`, `update_time`) VALUES
	(1, 'Project01', 'Project01', '', '', 2, 1496888057923, 1496888057923),
	(2, 'Project02', 'Project02', '', '', 2, 1496888155458, 1496888155458);
/*!40000 ALTER TABLE `project` ENABLE KEYS */;

-- 導出  表 ezscrum_180.project_role 結構
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
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.release 的資料：0 rows
/*!40000 ALTER TABLE `release` DISABLE KEYS */;
/*!40000 ALTER TABLE `release` ENABLE KEYS */;

-- 導出  表 ezscrum_180.retrospective 結構
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
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.retrospective 的資料：0 rows
/*!40000 ALTER TABLE `retrospective` DISABLE KEYS */;
/*!40000 ALTER TABLE `retrospective` ENABLE KEYS */;

-- 導出  表 ezscrum_180.scrum_role 結構
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
REPLACE INTO `scrum_role` (`id`, `access_productBacklog`, `access_sprintPlan`, `access_taskboard`, `access_sprintBacklog`, `access_releasePlan`, `access_retrospective`, `access_unplan`, `access_report`, `access_editProject`, `project_id`, `role`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1496888057991, 1496888057991),
	(2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1496888058005, 1496888058005),
	(3, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 2, 1496888058006, 1496888058006),
	(4, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 3, 1496888058006, 1496888058006),
	(5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 4, 1496888058007, 1496888058007),
	(6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1496888155459, 1496888155459),
	(7, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1496888155460, 1496888155460),
	(8, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 1496888155461, 1496888155461),
	(9, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 3, 1496888155462, 1496888155462),
	(10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 1496888155462, 1496888155462);
/*!40000 ALTER TABLE `scrum_role` ENABLE KEYS */;

-- 導出  表 ezscrum_180.serial_number 結構
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
REPLACE INTO `serial_number` (`id`, `project_id`, `release`, `sprint`, `story`, `task`, `unplan`, `retrospective`) VALUES
	(1, 1, 0, 0, 3, 0, 0, 0),
	(2, 2, 0, 0, 3, 0, 0, 0);
/*!40000 ALTER TABLE `serial_number` ENABLE KEYS */;

-- 導出  表 ezscrum_180.sprint 結構
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
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.sprint 的資料：0 rows
/*!40000 ALTER TABLE `sprint` DISABLE KEYS */;
/*!40000 ALTER TABLE `sprint` ENABLE KEYS */;

-- 導出  表 ezscrum_180.story 結構
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
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.story 的資料：6 rows
/*!40000 ALTER TABLE `story` DISABLE KEYS */;
REPLACE INTO `story` (`id`, `project_id`, `serial_id`, `sprint_id`, `name`, `status`, `estimate`, `importance`, `value`, `notes`, `how_to_demo`, `create_time`, `update_time`) VALUES
	(1, 1, 1, -1, 'Story01', 1, 2, 2, 2, '', '', 1496888088804, 1497324948781),
	(2, 1, 2, -1, 'Story02', 1, 2, 2, 2, '', '', 1496888125984, 1497320047327),
	(3, 1, 3, -1, 'Story03', 1, 2, 2, 2, '', '', 1496888139344, 1497320041959),
	(4, 2, 1, -1, 'Story01', 1, 0, 0, 0, '', '', 1496888173158, 1497320105289),
	(5, 2, 2, -1, 'Story02', 1, 0, 0, 0, '', '', 1496888180470, 1497319675704),
	(6, 2, 3, -1, 'Story03', 1, 0, 0, 0, '', '', 1496888191430, 1497319670000);
/*!40000 ALTER TABLE `story` ENABLE KEYS */;

-- 導出  表 ezscrum_180.story_tag_relation 結構
CREATE TABLE IF NOT EXISTS `story_tag_relation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tag_id` bigint(20) unsigned NOT NULL,
  `story_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.story_tag_relation 的資料：0 rows
/*!40000 ALTER TABLE `story_tag_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `story_tag_relation` ENABLE KEYS */;

-- 導出  表 ezscrum_180.system 結構
CREATE TABLE IF NOT EXISTS `system` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_id_UNIQUE` (`account_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.system 的資料：1 rows
/*!40000 ALTER TABLE `system` DISABLE KEYS */;
REPLACE INTO `system` (`id`, `account_id`) VALUES
	(1, 1);
/*!40000 ALTER TABLE `system` ENABLE KEYS */;

-- 導出  表 ezscrum_180.tag 結構
CREATE TABLE IF NOT EXISTS `tag` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `project_id` bigint(20) unsigned NOT NULL,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.tag 的資料：16 rows
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
REPLACE INTO `tag` (`id`, `name`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 'cat', 2, 1496888250458, 1496888250458),
	(2, 'dog', 2, 1496888254449, 1496888254449),
	(3, 'milk', 2, 1496888263432, 1496888263432),
	(4, 'cat', 1, 1496888280079, 1496888280079),
	(5, 'dog', 1, 1496888285095, 1496888285095),
	(6, 'juice', 1, 1496888291871, 1496888291871),
	(7, 'potato', 1, 1496888310503, 1496888310503),
	(8, 'tomato', 2, 1496888322362, 1496888322362),
	(9, 'cow', 1, 1497492842023, 1497492842023),
	(10, 'popcorn', 1, 1497492870962, 1497492870962),
	(17, 'coffee', 1, 1497493427666, 1497493427666),
	(12, 'sheep', 1, 1497492913138, 1497492913138),
	(13, 'cow', 2, 1497492928596, 1497492928596),
	(14, 'sheep', 2, 1497492934235, 1497492934235),
	(15, 'pineapple', 2, 1497492944162, 1497492944162),
	(16, 'tea', 2, 1497492961881, 1497492961881);
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;

-- 導出  表 ezscrum_180.task 結構
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
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.task 的資料：0 rows
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
/*!40000 ALTER TABLE `task` ENABLE KEYS */;

-- 導出  表 ezscrum_180.token 結構
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
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.unplan 的資料：0 rows
/*!40000 ALTER TABLE `unplan` DISABLE KEYS */;
/*!40000 ALTER TABLE `unplan` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
