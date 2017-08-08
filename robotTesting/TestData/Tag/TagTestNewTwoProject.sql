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
DROP DATABASE IF EXISTS `ezscrum_180`;
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
) ENGINE=MyISAM AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.history 的資料：30 rows
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
REPLACE INTO `history` (`id`, `issue_id`, `issue_type`, `type`, `old_value`, `new_value`, `create_time`) VALUES
	(1, 1, 3, 1, '', '', 1496499702160),
	(2, 2, 3, 1, '', '', 1496500119378),
	(3, 3, 3, 1, '', '', 1496500144031),
	(4, 4, 3, 1, '', '', 1496500166353),
	(5, 5, 3, 1, '', '', 1496500847909),
	(6, 6, 3, 1, '', '', 1496500997965),
	(7, 7, 3, 1, '', '', 1496501022534),
	(8, 7, 3, 2, 'Story04', 'Story03', 1496501036632),
	(9, 8, 3, 1, '', '', 1496501065461),
	(10, 9, 3, 1, '', '', 1496501095661),
	(11, 10, 3, 1, '', '', 1496501120766),
	(12, 11, 3, 1, '', '', 1496501157731),
	(13, 12, 3, 1, '', '', 1496501375593),
	(14, 13, 3, 1, '', '', 1496501416024),
	(15, 14, 3, 1, '', '', 1496501445552),
	(16, 15, 3, 1, '', '', 1496501523158),
	(17, 16, 3, 1, '', '', 1496501721466),
	(18, 17, 3, 1, '', '', 1496501768633),
	(19, 18, 3, 1, '', '', 1496501825026),
	(20, 19, 3, 1, '', '', 1496501843207),
	(21, 10, 3, 16, '', '1', 1496503061547),
	(22, 3, 3, 16, '', '1', 1496503061550),
	(23, 14, 3, 16, '', '1', 1496503061553),
	(24, 1, 3, 16, '', '1', 1496503061555),
	(25, 13, 3, 16, '', '1', 1496503061558),
	(26, 9, 3, 16, '', '1', 1496503061561),
	(27, 11, 3, 16, '', '1', 1496503061564),
	(28, 12, 3, 16, '', '1', 1496503061567),
	(29, 2, 3, 16, '', '1', 1496503061569),
	(30, 4, 3, 16, '', '1', 1496503061572);
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
	(1, 'Project01', 'Project01', '', '', 2, 1496498869143, 1496498869143),
	(2, 'Project02', 'Project02', '', '', 2, 1496500198901, 1496500198901);
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
	(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1496498869143, 1496498869143),
	(2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1496498869144, 1496498869144),
	(3, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 2, 1496498869144, 1496498869144),
	(4, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 3, 1496498869144, 1496498869144),
	(5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 4, 1496498869144, 1496498869144),
	(6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1496500198901, 1496500198901),
	(7, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1496500198901, 1496500198901),
	(8, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 1496500198901, 1496500198901),
	(9, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 3, 1496500198902, 1496500198902),
	(10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 1496500198902, 1496500198902);
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
	(1, 1, 0, 1, 10, 0, 0, 0),
	(2, 2, 0, 0, 9, 0, 0, 0);
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
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.sprint 的資料：1 rows
/*!40000 ALTER TABLE `sprint` DISABLE KEYS */;
REPLACE INTO `sprint` (`id`, `serial_id`, `goal`, `interval`, `team_size`, `available_hours`, `focus_factor`, `start_date`, `end_date`, `demo_date`, `demo_place`, `daily_info`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 1, '123', 2, 4, 60, 70, NOW(), NOW() + INTERVAL 14 DAY,  NOW() + INTERVAL 14 DAY, '', '', 1, 1496503026897, 1496503042630);
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
) ENGINE=MyISAM AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.story 的資料：19 rows
/*!40000 ALTER TABLE `story` DISABLE KEYS */;
REPLACE INTO `story` (`id`, `project_id`, `serial_id`, `sprint_id`, `name`, `status`, `estimate`, `importance`, `value`, `notes`, `how_to_demo`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 'Story01', 1, 13, 80, 10, '', '', 1496499702160, 1496503061555),
	(2, 1, 2, 1, 'Story02', 1, 8, 90, 5, '', '', 1496500119378, 1496503061569),
	(3, 1, 3, 1, 'Story03', 1, 5, 70, 2, '', '', 1496500144031, 1496503061550),
	(4, 1, 4, 1, 'Story04', 1, 9, 95, 6, '', '', 1496500166353, 1496503061572),
	(5, 2, 1, -1, 'Story01', 1, 11, 75, 10, '', '', 1496500847909, 1496500847909),
	(6, 2, 2, -1, 'Story02', 1, 4, 60, 5, '', '', 1496500997965, 1496500997965),
	(7, 2, 3, -1, 'Story03', 1, 7, 90, 8, '', '', 1496501022534, 1496501036632),
	(8, 2, 4, -1, 'Story04', 1, 8, 85, 9, '', '', 1496501065461, 1496501065461),
	(9, 1, 5, 1, 'Story05', 1, 13, 85, 7, '', '', 1496501095661, 1496503061561),
	(10, 1, 6, 1, 'Story06', 1, 5, 50, 3, '', '', 1496501120766, 1496503061547),
	(11, 1, 7, 1, 'Story07', 1, 10, 85, 8, '', '', 1496501157731, 1496503061564),
	(12, 1, 8, 1, 'Story08', 1, 8, 88, 8, '', '', 1496501375593, 1496503061567),
	(13, 1, 9, 1, 'Story09', 1, 13, 80, 5, '', '', 1496501416024, 1496503061558),
	(14, 1, 10, 1, 'Story10', 1, 10, 70, 10, '', '', 1496501445552, 1496503061553),
	(15, 2, 5, -1, 'Story05', 1, 9, 20, 8, '', '', 1496501523158, 1496501523158),
	(16, 2, 6, -1, 'Story06', 1, 9, 75, 7, '', '', 1496501721466, 1496501721466),
	(17, 2, 7, -1, 'Story07', 1, 7, 85, 9, '', '', 1496501768633, 1496501768633),
	(18, 2, 8, -1, 'Story08', 1, 5, 75, 6, '', '', 1496501825026, 1496501825026),
	(19, 2, 9, -1, 'Story09', 1, 7, 75, 4, '', '', 1496501843207, 1496501843207);
/*!40000 ALTER TABLE `story` ENABLE KEYS */;

-- 導出  表 ezscrum_180.story_tag_relation 結構
CREATE TABLE IF NOT EXISTS `story_tag_relation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tag_id` bigint(20) unsigned NOT NULL,
  `story_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.story_tag_relation 的資料：33 rows
/*!40000 ALTER TABLE `story_tag_relation` DISABLE KEYS */;
REPLACE INTO `story_tag_relation` (`id`, `tag_id`, `story_id`) VALUES
	(1, 1, 2),
	(2, 2, 2),
	(3, 3, 3),
	(4, 6, 3),
	(5, 7, 5),
	(6, 8, 5),
	(7, 9, 5),
	(8, 10, 5),
	(9, 11, 5),
	(10, 12, 5),
	(11, 8, 6),
	(12, 9, 6),
	(13, 12, 7),
	(14, 4, 9),
	(15, 6, 9),
	(16, 1, 12),
	(17, 2, 12),
	(18, 3, 12),
	(19, 5, 13),
	(20, 1, 14),
	(21, 2, 14),
	(22, 3, 14),
	(23, 4, 14),
	(24, 5, 14),
	(25, 6, 14),
	(26, 11, 15),
	(27, 10, 15),
	(28, 7, 16),
	(29, 8, 16),
	(30, 9, 16),
	(31, 7, 18),
	(32, 8, 18),
	(33, 9, 18);
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
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.tag 的資料：12 rows
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
REPLACE INTO `tag` (`id`, `name`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 'Tag01', 1, 1496499010788, 1496499010788),
	(2, 'Tag02', 1, 1496499016072, 1496499016072),
	(3, 'Tag03', 1, 1496499031132, 1496499031132),
	(4, 'P1Tag01', 1, 1496499044684, 1496499044684),
	(5, 'P1Tag02', 1, 1496499057574, 1496499057574),
	(6, 'P1Tag03', 1, 1496499099598, 1496499099598),
	(7, 'Tag01', 2, 1496500325945, 1496500325945),
	(8, 'Tag02', 2, 1496500331020, 1496500331020),
	(9, 'Tag03', 2, 1496500337370, 1496500337370),
	(10, 'P2Tag01', 2, 1496500346240, 1496500346240),
	(11, 'P2Tag02', 2, 1496500354859, 1496500354859),
	(12, 'P2Tag03', 2, 1496500361901, 1496500361901);
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
