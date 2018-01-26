-- --------------------------------------------------------
-- 主機:                           127.0.0.1
-- 服務器版本:                        5.6.28-log - MySQL Community Server (GPL)
-- 服務器操作系統:                      Win64
-- HeidiSQL 版本:                  9.3.0.4984
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 導出 ezscrum_180 的資料庫結構
CREATE DATABASE IF NOT EXISTS `ezscrum_180` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ezscrum_180`;


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
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.account 的資料：6 rows
DELETE FROM `account`;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` (`id`, `username`, `nick_name`, `email`, `password`, `enable`, `create_time`, `update_time`) VALUES
	(1, 'admin', 'admin', 'example@ezScrum.tw', '21232f297a57a5a743894a0e4a801fc3', 1, 1379910191599, 1379910191599),
	(2, 'account2', 'account2', 'account2@gmail.com', '93189e2c4c7b1a2c7b16a24d5daa98a9', 1, 1464860058277, 1464860058277),
	(3, 'account1', 'account1', 'account1@gmail.com', '809d7aea9eacf339b2e35e3c8ae0a57c', 1, 1464860058335, 1464860058335),
	(4, 'account4', 'account4', 'account4@gmail.com', '201e9991afe90c65e13b08b53fb695de', 1, 1464860058340, 1464860058340),
	(5, 'account3', 'account3', 'account3@gmail.com', 'fbcd0ff0529a3dd9b733884b30941297', 1, 1464860058346, 1464860058346),
	(6, 'account5', 'account5', 'account5@gmail.com', '7ea4950f63c983360c863bd5d1608944', 1, 1464860058352, 1464860058352);
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
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.attach_file 的資料：10 rows
DELETE FROM `attach_file`;
/*!40000 ALTER TABLE `attach_file` DISABLE KEYS */;
INSERT INTO `attach_file` (`id`, `name`, `issue_id`, `issue_type`, `path`, `content_type`, `create_time`) VALUES
	(1, 'png測試.png', 1, 3, '\\AttachFile\\Project01\\1464860058754_png測試.png', 'application/octet-stream', 1464860058790),
	(2, 'pdf測試.pdf', 1, 3, '\\AttachFile\\Project01\\1464860058826_pdf測試.pdf', 'application/octet-stream', 1464860058828),
	(3, 'jpeg測試.jpg', 1, 3, '\\AttachFile\\Project01\\1464860058837_jpeg測試.jpg', 'image/jpeg', 1464860058838),
	(4, 'doc測試.doc', 1, 3, '\\AttachFile\\Project01\\1464860058851_doc測試.doc', 'application/octet-stream', 1464860058852),
	(5, 'docx測試.docx', 1, 3, '\\AttachFile\\Project01\\1464860058862_docx測試.docx', 'application/octet-stream', 1464860058864),
	(6, 'xml測試.xml', 1, 1, '\\AttachFile\\Project01\\1464860058906_xml測試.xml', 'text/xml', 1464860058908),
	(7, 'xls測試.xls', 1, 1, '\\AttachFile\\Project01\\1464860058920_xls測試.xls', 'application/octet-stream', 1464860058922),
	(8, 'xlsx測試.xlsx', 1, 1, '\\AttachFile\\Project01\\1464860058932_xlsx測試.xlsx', 'application/octet-stream', 1464860058933),
	(9, 'txt測試.txt', 1, 1, '\\AttachFile\\Project01\\1464860058942_txt測試.txt', 'application/octet-stream', 1464860058950),
	(10, 'rar測試.rar', 1, 1, '\\AttachFile\\Project01\\1464860058959_rar測試.rar', 'application/octet-stream', 1464860058960);
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
) ENGINE=MyISAM AUTO_INCREMENT=80 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.history 的資料：63 rows
DELETE FROM `history`;
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
INSERT INTO `history` (`id`, `issue_id`, `issue_type`, `type`, `old_value`, `new_value`, `create_time`) VALUES
	(43, 1, 3, 1, '', '', 1452134915000),
	(42, 1, 3, 17, '', '4', 1452096000000),
	(51, 1, 1, 3, '0', '3', 1452096000000),
	(50, 1, 1, 16, '', '1', 1452096000000),
	(41, 1, 3, 17, '', '3', 1452096000000),
	(57, 1, 2, 1, '', '', 1452135165000),
	(61, 2, 2, 1, '', '', 1452135184000),
	(64, 3, 2, 1, '', '', 1452135203000),
	(19, 2, 3, 1, '', '', 1452134935000),
	(27, 2, 1, 3, '0', '5', 1452096000000),
	(26, 2, 1, 16, '', '2', 1452096000000),
	(18, 2, 3, 17, '', '6', 1452096000000),
	(31, 3, 1, 3, '0', '13', 1452096000000),
	(30, 3, 1, 16, '', '2', 1452096000000),
	(17, 2, 3, 17, '', '5', 1452096000000),
	(34, 4, 1, 16, '', '1', 1452096000000),
	(20, 2, 3, 16, '', '1', 1452134935000),
	(21, 2, 3, 7, '0', '0', 1452134935000),
	(22, 2, 3, 16, '', '1', 1452134935000),
	(23, 2, 3, 3, '0', '8', 1452134935000),
	(24, 2, 3, 6, '0', '70', 1452134935000),
	(25, 2, 3, 18, '', '1', 1452135043000),
	(28, 2, 1, 4, '0', '5', 1452096000000),
	(29, 2, 1, 1, '', '', 1452134985000),
	(32, 3, 1, 4, '0', '13', 1452096000000),
	(33, 3, 1, 1, '', '', 1452134994000),
	(35, 4, 1, 3, '0', '13', 1452096000000),
	(36, 4, 1, 4, '0', '13', 1452096000000),
	(37, 4, 1, 1, '', '', 1452134968000),
	(38, 4, 1, 12, '1', '2', 1452135028000),
	(39, 4, 1, 12, '2', '1', 1452135040000),
	(40, 4, 1, 18, '', '1', 1452135040000),
	(44, 1, 3, 16, '', '1', 1452134915000),
	(45, 1, 3, 7, '0', '0', 1452134915000),
	(46, 1, 3, 16, '', '1', 1452134915000),
	(47, 1, 3, 3, '0', '13', 1452134915000),
	(48, 1, 3, 6, '0', '80', 1452134915000),
	(49, 1, 3, 15, '', '4', 1452135040000),
	(52, 1, 1, 4, '0', '3', 1452096000000),
	(53, 1, 1, 1, '', '', 1452134959000),
	(54, 1, 1, 12, '1', '2', 1452135013000),
	(55, 1, 1, 4, '3', '0', 1452135016000),
	(56, 1, 1, 12, '2', '3', 1452135017000),
	(58, 1, 2, 12, '1', '2', 1452135165000),
	(59, 1, 2, 23, '0', '1', 1452135165000),
	(60, 1, 2, 3, '0', '3', 1452135165000),
	(62, 2, 2, 23, '0', '1', 1452135184000),
	(63, 2, 2, 3, '0', '8', 1452135184000),
	(65, 3, 2, 12, '1', '2', 1452135203000),
	(66, 3, 2, 23, '0', '2', 1452135203000),
	(67, 3, 2, 3, '0', '8', 1452135203000),
	(68, 3, 2, 12, '2', '3', 1452135210000),
	(69, 3, 3, 1, '', '', 1464918858187),
	(70, 3, 3, 16, '', '3', 1464918858187),
	(71, 5, 1, 1, '', '', 1464918868167),
	(72, 5, 1, 16, '', '3', 1464918868204),
	(73, 3, 3, 17, '', '5', 1464918868205),
	(74, 6, 1, 1, '', '', 1464918876826),
	(75, 6, 1, 16, '', '3', 1464918876834),
	(76, 3, 3, 17, '', '6', 1464918876836),
	(77, 7, 1, 1, '', '', 1464918885682),
	(78, 7, 1, 16, '', '3', 1464918885690),
	(79, 3, 3, 17, '', '7', 1464918885691);
/*!40000 ALTER TABLE `history` ENABLE KEYS */;


-- 導出  表 ezscrum_180.issue_partner_relation 結構
DROP TABLE IF EXISTS `issue_partner_relation`;
CREATE TABLE IF NOT EXISTS `issue_partner_relation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `issue_id` bigint(20) unsigned NOT NULL,
  `issue_type` int(11) NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.issue_partner_relation 的資料：2 rows
DELETE FROM `issue_partner_relation`;
/*!40000 ALTER TABLE `issue_partner_relation` DISABLE KEYS */;
INSERT INTO `issue_partner_relation` (`id`, `issue_id`, `issue_type`, `account_id`) VALUES
	(1, 3, 2, 5),
	(2, 4, 1, 5);
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
DELETE FROM `project`;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` (`id`, `name`, `display_name`, `comment`, `product_owner`, `attach_max_size`, `create_time`, `update_time`) VALUES
	(1, 'Project01', 'Project01', 'Comment in Project01', 'PO in Project01', 2, 1452134601460, 1452134601460),
	(2, 'Project02', 'Project02', 'Comment in Project02', 'PO in Project02', 2, 1452134636448, 1452134636448);
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
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.project_role 的資料：8 rows
DELETE FROM `project_role`;
/*!40000 ALTER TABLE `project_role` DISABLE KEYS */;
INSERT INTO `project_role` (`id`, `project_id`, `account_id`, `role`, `create_time`, `update_time`) VALUES
	(1, 1, 3, 0, 1464860058479, 1464860058479),
	(2, 1, 2, 1, 1464860058484, 1464860058484),
	(3, 1, 5, 2, 1464860058489, 1464860058490),
	(4, 1, 4, 2, 1464860058495, 1464860058495),
	(5, 2, 2, 0, 1464860059801, 1464860059801),
	(6, 2, 5, 1, 1464860059806, 1464860059806),
	(7, 2, 4, 2, 1464860059810, 1464860059810),
	(8, 2, 6, 2, 1464860059814, 1464860059814);
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
DELETE FROM `release`;
/*!40000 ALTER TABLE `release` DISABLE KEYS */;
INSERT INTO `release` (`id`, `serial_id`, `name`, `description`, `start_date`, `end_date`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 1, 'Release01', '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', '2016-01-07 00:00:00', '2016-02-03 00:00:00', 1, 1464860059223, 1464860059223);
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
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.retrospective 的資料：4 rows
DELETE FROM `retrospective`;
/*!40000 ALTER TABLE `retrospective` DISABLE KEYS */;
INSERT INTO `retrospective` (`id`, `serial_id`, `name`, `description`, `type`, `status`, `sprint_id`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 1, 'Good01', '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', 'Good', 'new', 1, 1, 1464860059057, 1464860059057),
	(2, 2, 'Improvement', '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', 'Improvement', 'closed', 1, 1, 1464860059066, 1464860059066),
	(3, 3, 'Good02', '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', 'Good', 'resolved', 2, 1, 1464860059098, 1464860059098),
	(4, 4, 'Improvement02', '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', 'Improvement', 'assigned', 2, 1, 1464860059106, 1464860059106);
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
DELETE FROM `scrum_role`;
/*!40000 ALTER TABLE `scrum_role` DISABLE KEYS */;
INSERT INTO `scrum_role` (`id`, `access_productBacklog`, `access_sprintPlan`, `access_taskboard`, `access_sprintBacklog`, `access_releasePlan`, `access_retrospective`, `access_unplan`, `access_report`, `access_editProject`, `project_id`, `role`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1464860058433, 1464860058472),
	(2, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1464860058433, 1464860058472),
	(3, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 2, 1464860058434, 1464860058473),
	(4, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 3, 1464860058434, 1464860058473),
	(5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 4, 1464860058435, 1464860058473),
	(6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1464860059785, 1464860059795),
	(7, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1, 1464860059786, 1464860059795),
	(8, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 1464860059786, 1464860059795),
	(9, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 3, 1464860059787, 1464860059796),
	(10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 1464860059787, 1464860059796);
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
DELETE FROM `serial_number`;
/*!40000 ALTER TABLE `serial_number` DISABLE KEYS */;
INSERT INTO `serial_number` (`id`, `project_id`, `release`, `sprint`, `story`, `task`, `unplan`, `retrospective`) VALUES
	(1, 1, 1, 3, 3, 7, 3, 4),
	(2, 2, 0, 0, 0, 0, 0, 0);
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
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.sprint 的資料：3 rows
DELETE FROM `sprint`;
/*!40000 ALTER TABLE `sprint` DISABLE KEYS */;
INSERT INTO `sprint` (`id`, `serial_id`, `goal`, `interval`, `team_size`, `available_hours`, `focus_factor`, `start_date`, `end_date`, `demo_date`, `demo_place`, `daily_info`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 1, 'Sprint01', 2, 4, 100, 80, '2016-01-07 00:00:00', '2016-01-20 00:00:00', '2016-01-20 00:00:00', 'Lab1321', '17:00@Lab1321', 1, 1464860058581, 1464860058581),
	(2, 2, 'Sprint02', 2, 4, 100, 87, '2016-01-21 00:00:00', '2016-02-03 00:00:00', '2016-02-03 00:00:00', 'Lab1321', '17:00@Lab1321', 1, 1464860059073, 1464860059073),
	(3, 3, 'Sprint03', 2, 5, 100, 100, '2016-06-03 00:00:00', '2016-06-16 00:00:00', '2016-06-16 00:00:00', '', '', 1, 1464918840174, 1464918840174);
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
DELETE FROM `story`;
/*!40000 ALTER TABLE `story` DISABLE KEYS */;
INSERT INTO `story` (`id`, `project_id`, `serial_id`, `sprint_id`, `name`, `status`, `estimate`, `importance`, `value`, `notes`, `how_to_demo`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 'Story01', 1, 13, 80, 0, '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', 1464860058647, 1464860058695),
	(2, 1, 2, -1, 'Story02', 1, 8, 70, 0, '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', 1464860059247, 1464860059273),
	(3, 1, 3, 3, 'Story01', 1, 8, 80, 0, '', '', 1464918858187, 1464918858187);
/*!40000 ALTER TABLE `story` ENABLE KEYS */;


-- 導出  表 ezscrum_180.story_tag_relation 結構
DROP TABLE IF EXISTS `story_tag_relation`;
CREATE TABLE IF NOT EXISTS `story_tag_relation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tag_id` bigint(20) unsigned NOT NULL,
  `story_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.story_tag_relation 的資料：2 rows
DELETE FROM `story_tag_relation`;
/*!40000 ALTER TABLE `story_tag_relation` DISABLE KEYS */;
INSERT INTO `story_tag_relation` (`id`, `tag_id`, `story_id`) VALUES
	(1, 1, 1),
	(2, 2, 2);
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
DELETE FROM `system`;
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
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.tag 的資料：3 rows
DELETE FROM `tag`;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` (`id`, `name`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 'tag01', 1, 1464860058525, 1464860058525),
	(2, 'tag02', 1, 1464860058531, 1464860058531),
	(3, 'tag03', 1, 1464860058538, 1464860058538);
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
  /*`actual` int(11) NOT NULL DEFAULT '0',*/
  `notes` text,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.task 的資料：7 rows
DELETE FROM `task`;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
INSERT INTO `task` (`id`, `serial_id`, `project_id`, `story_id`, `name`, `handler_id`, `status`, `estimate`, `remain`, /*`actual`,*/ `notes`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 'Task01', 5, 3, 3, 0, /*0,*/ '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', 1464860058880, 1464860058880),
	(2, 2, 1, 2, 'Task03', -1, 1, 5, 5, /*0,*/ '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', 1464860059280, 1464860059280),
	(3, 3, 1, 2, 'Task04', -1, 1, 13, 13, /*0,*/ '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', 1464860059293, 1464860059293),
	(4, 4, 1, -1, 'Task02', -1, 1, 13, 13, /*0,*/ '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', 1464860059308, 1464860059308),
	(5, 5, 1, 3, 'Task01', -1, 1, 8, 8, /*0,*/ '', 1464918868167, 1464918868167),
	(6, 6, 1, 3, 'Task02', -1, 1, 13, 13, /*0,*/ '', 1464918876826, 1464918876826),
	(7, 7, 1, 3, 'Task03', -1, 1, 5, 5, /*0,*/ '', 1464918885682, 1464918885682);
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
DELETE FROM `token`;
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
  /*`actual` int(11) NOT NULL,*/
  `notes` text NOT NULL,
  `status` tinyint(3) unsigned NOT NULL,
  `project_id` bigint(20) unsigned NOT NULL,
  `sprint_id` bigint(20) unsigned NOT NULL,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.unplan 的資料：3 rows
DELETE FROM `unplan`;
/*!40000 ALTER TABLE `unplan` DISABLE KEYS */;
INSERT INTO `unplan` (`id`, `serial_id`, `name`, `handler_id`, `estimate`, /*`actual`,*/ `notes`, `status`, `project_id`, `sprint_id`, `create_time`, `update_time`) VALUES
	(1, 1, 'Unplan01', 5, 3, /*0,*/ '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', 2, 1, 1, 1464860058990, 1464860058990),
	(2, 2, 'Unplan02', -1, 8, /*0,*/ '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', 1, 1, 1, 1464860059003, 1464860059003),
	(3, 3, 'Unplan03', 4, 8, /*0,*/ '\\n\\t\\r\\b\\f\n\\n\n\\t\n\\r\n\\b\n\\f', 3, 1, 2, 1464860059085, 1464860059085);
/*!40000 ALTER TABLE `unplan` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
