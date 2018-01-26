-- --------------------------------------------------------
-- 主機:                           127.0.0.1
-- 服務器版本:                        5.7.8-rc-log - MySQL Community Server (GPL)
-- 服務器操作系統:                      Win64
-- HeidiSQL 版本:                  8.3.0.4694
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
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
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.account 的資料：3 rows
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
REPLACE INTO `account` (`id`, `username`, `nick_name`, `email`, `password`, `enable`, `create_time`, `update_time`) VALUES
	(1, 'admin', 'admin', 'example@ezScrum.tw', '21232f297a57a5a743894a0e4a801fc3', 1, 1379910191599, 1379910191599),
	(2, 'admin2', 'admin2', 'admin2@ezScrum.tw', '21232f297a57a5a743894a0e4a801fc3', 1, 1489767744486, 1489767744486),
	(3, 'admin3', 'admin3', 'admin3@ezScrum.tw', '21232f297a57a5a743894a0e4a801fc3', 1, 1489767822825, 1489767822825);
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
) ENGINE=MyISAM AUTO_INCREMENT=164 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.history 的資料：163 rows
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
REPLACE INTO `history` (`id`, `issue_id`, `issue_type`, `type`, `old_value`, `new_value`, `create_time`) VALUES
	(1, 1, 3, 1, '', '', 1489766531693),
	(2, 1, 3, 16, '', '1', 1489766531693),
	(3, 2, 3, 1, '', '', 1489766613364),
	(4, 2, 3, 16, '', '1', 1489766613364),
	(5, 1, 1, 1, '', '', 1489766654548),
	(6, 1, 1, 16, '', '1', 1489766654562),
	(7, 1, 3, 17, '', '1', 1489766654564),
	(8, 1, 1, 2, 'TaskTest01', 'TaskTestone01', 1489766690287),
	(9, 2, 1, 1, '', '', 1489766715747),
	(10, 2, 1, 16, '', '1', 1489766715754),
	(11, 1, 3, 17, '', '2', 1489766715756),
	(12, 3, 1, 1, '', '', 1489766729487),
	(13, 3, 1, 16, '', '1', 1489766729496),
	(14, 1, 3, 17, '', '3', 1489766729498),
	(15, 2, 1, 3, '0', '2', 1489766788520),
	(16, 4, 1, 1, '', '', 1489766805828),
	(17, 4, 1, 16, '', '1', 1489766805835),
	(18, 1, 3, 17, '', '4', 1489766805836),
	(19, 5, 1, 1, '', '', 1489766822889),
	(20, 5, 1, 16, '', '2', 1489766822897),
	(21, 2, 3, 17, '', '5', 1489766822899),
	(22, 6, 1, 1, '', '', 1489766843705),
	(23, 6, 1, 16, '', '2', 1489766843712),
	(24, 2, 3, 17, '', '6', 1489766843713),
	(25, 5, 1, 2, 'TaskTesttwo01', 'TaskTestTwo01', 1489766851368),
	(26, 4, 1, 2, 'TaskTeston04', 'TaskTestOne04', 1489766862866),
	(27, 3, 1, 2, 'TaskTestone03', 'TaskTestOne03', 1489766868914),
	(28, 2, 1, 2, 'TaskTestone02', 'TaskTestOne02', 1489766875989),
	(29, 1, 1, 2, 'TaskTestone01', 'TaskTestOne01', 1489766883260),
	(30, 7, 1, 1, '', '', 1489767013717),
	(31, 7, 1, 16, '', '2', 1489767013726),
	(32, 2, 3, 17, '', '7', 1489767013728),
	(33, 8, 1, 1, '', '', 1489767041988),
	(34, 8, 1, 16, '', '2', 1489767041994),
	(35, 2, 3, 17, '', '8', 1489767041996),
	(36, 9, 1, 1, '', '', 1489767055402),
	(37, 9, 1, 16, '', '2', 1489767055408),
	(38, 2, 3, 17, '', '9', 1489767055410),
	(39, 3, 1, 12, '1', '2', 1489767093101),
	(40, 3, 1, 13, '-1', '1', 1489767093101),
	(41, 1, 1, 12, '1', '2', 1489767103131),
	(42, 1, 1, 13, '-1', '1', 1489767103131),
	(43, 1, 1, 5, '0', '12', 1489767111007),
	(44, 1, 1, 4, '3', '0', 1489767111007),
	(45, 1, 1, 12, '2', '3', 1489767111007),
	(46, 2, 1, 12, '1', '2', 1489767114482),
	(47, 2, 1, 13, '-1', '1', 1489767114482),
	(48, 8, 1, 12, '1', '2', 1489767124860),
	(49, 8, 1, 13, '-1', '1', 1489767124860),
	(50, 7, 1, 12, '1', '2', 1489767126848),
	(51, 7, 1, 13, '-1', '1', 1489767126848),
	(52, 3, 3, 1, '', '', 1489767196594),
	(53, 3, 3, 16, '', '2', 1489767196594),
	(54, 4, 3, 1, '', '', 1489767226832),
	(55, 4, 3, 16, '', '2', 1489767226832),
	(56, 10, 1, 1, '', '', 1489767254975),
	(57, 10, 1, 16, '', '4', 1489767254980),
	(58, 4, 3, 17, '', '10', 1489767254981),
	(59, 11, 1, 1, '', '', 1489767272021),
	(60, 11, 1, 16, '', '4', 1489767272027),
	(61, 4, 3, 17, '', '11', 1489767272028),
	(62, 12, 1, 1, '', '', 1489767293423),
	(63, 12, 1, 16, '', '4', 1489767293430),
	(64, 4, 3, 17, '', '12', 1489767293432),
	(65, 13, 1, 1, '', '', 1489767323275),
	(66, 13, 1, 16, '', '3', 1489767323280),
	(67, 3, 3, 17, '', '13', 1489767323282),
	(68, 14, 1, 1, '', '', 1489767339774),
	(69, 14, 1, 16, '', '3', 1489767339777),
	(70, 3, 3, 17, '', '14', 1489767339777),
	(71, 15, 1, 1, '', '', 1489767352056),
	(72, 15, 1, 16, '', '3', 1489767352063),
	(73, 3, 3, 17, '', '15', 1489767352064),
	(74, 16, 1, 1, '', '', 1489767573607),
	(75, 16, 1, 16, '', '3', 1489767573614),
	(76, 3, 3, 17, '', '16', 1489767573616),
	(77, 11, 1, 12, '1', '2', 1489767582683),
	(78, 11, 1, 13, '-1', '1', 1489767582683),
	(79, 15, 1, 12, '1', '2', 1489767589347),
	(80, 15, 1, 13, '-1', '1', 1489767589347),
	(81, 14, 1, 12, '1', '2', 1489767591620),
	(82, 14, 1, 13, '-1', '1', 1489767591620),
	(83, 5, 3, 1, '', '', 1489768204063),
	(84, 5, 3, 16, '', '3', 1489768204063),
	(85, 6, 3, 1, '', '', 1489768218754),
	(86, 6, 3, 16, '', '3', 1489768218754),
	(87, 17, 1, 1, '', '', 1489768230903),
	(88, 17, 1, 16, '', '5', 1489768230910),
	(89, 5, 3, 17, '', '17', 1489768230912),
	(90, 18, 1, 1, '', '', 1489768245143),
	(91, 18, 1, 16, '', '5', 1489768245151),
	(92, 5, 3, 17, '', '18', 1489768245153),
	(93, 19, 1, 1, '', '', 1489768254733),
	(94, 19, 1, 16, '', '5', 1489768254740),
	(95, 5, 3, 17, '', '19', 1489768254742),
	(96, 20, 1, 1, '', '', 1489768300472),
	(97, 20, 1, 16, '', '5', 1489768300480),
	(98, 5, 3, 17, '', '20', 1489768300482),
	(99, 21, 1, 1, '', '', 1489768318520),
	(100, 21, 1, 16, '', '6', 1489768318530),
	(101, 6, 3, 17, '', '21', 1489768318531),
	(102, 22, 1, 1, '', '', 1489768328782),
	(103, 22, 1, 16, '', '6', 1489768328788),
	(104, 6, 3, 17, '', '22', 1489768328790),
	(105, 23, 1, 1, '', '', 1489768339040),
	(106, 23, 1, 16, '', '6', 1489768339047),
	(107, 6, 3, 17, '', '23', 1489768339050),
	(108, 24, 1, 1, '', '', 1489768349876),
	(109, 24, 1, 16, '', '6', 1489768349883),
	(110, 6, 3, 17, '', '24', 1489768349884),
	(111, 25, 1, 1, '', '', 1489768359905),
	(112, 25, 1, 16, '', '6', 1489768359911),
	(113, 6, 3, 17, '', '25', 1489768359912),
	(114, 7, 3, 1, '', '', 1489768774929),
	(115, 7, 3, 16, '', '4', 1489768774929),
	(116, 8, 3, 1, '', '', 1489768794875),
	(117, 8, 3, 16, '', '4', 1489768794875),
	(118, 26, 1, 1, '', '', 1489768809783),
	(119, 26, 1, 16, '', '7', 1489768809789),
	(120, 7, 3, 17, '', '26', 1489768809791),
	(121, 27, 1, 1, '', '', 1489768818851),
	(122, 27, 1, 16, '', '7', 1489768818856),
	(123, 7, 3, 17, '', '27', 1489768818858),
	(124, 28, 1, 1, '', '', 1489768899172),
	(125, 28, 1, 16, '', '7', 1489768899179),
	(126, 7, 3, 17, '', '28', 1489768899181),
	(127, 29, 1, 1, '', '', 1489768955336),
	(128, 29, 1, 16, '', '8', 1489768955343),
	(129, 8, 3, 17, '', '29', 1489768955344),
	(130, 30, 1, 1, '', '', 1489768972376),
	(131, 30, 1, 16, '', '8', 1489768972384),
	(132, 8, 3, 17, '', '30', 1489768972386),
	(133, 31, 1, 1, '', '', 1489768986808),
	(134, 31, 1, 16, '', '8', 1489768986813),
	(135, 8, 3, 17, '', '31', 1489768986815),
	(136, 32, 1, 1, '', '', 1489768996505),
	(137, 32, 1, 16, '', '8', 1489768996511),
	(138, 8, 3, 17, '', '32', 1489768996513),
	(139, 19, 1, 12, '1', '2', 1489769020480),
	(140, 19, 1, 13, '-1', '1', 1489769020480),
	(141, 18, 1, 12, '1', '2', 1489769022611),
	(142, 18, 1, 13, '-1', '1', 1489769022611),
	(143, 17, 1, 12, '1', '2', 1489769026621),
	(144, 17, 1, 13, '-1', '1', 1489769026621),
	(145, 17, 1, 4, '2', '0', 1489769029316),
	(146, 17, 1, 12, '2', '3', 1489769029316),
	(147, 24, 1, 12, '1', '2', 1489769036116),
	(148, 24, 1, 13, '-1', '1', 1489769036116),
	(149, 23, 1, 12, '1', '2', 1489769038147),
	(150, 23, 1, 13, '-1', '1', 1489769038147),
	(151, 27, 1, 12, '1', '2', 1489769973263),
	(152, 27, 1, 13, '-1', '1', 1489769973263),
	(153, 31, 1, 12, '1', '2', 1489769980335),
	(154, 31, 1, 13, '-1', '1', 1489769980335),
	(155, 30, 1, 12, '1', '2', 1489769984885),
	(156, 30, 1, 13, '-1', '1', 1489769984885),
	(157, 33, 1, 1, '', '', 1489770054407),
	(158, 33, 1, 16, '', '7', 1489770054413),
	(159, 7, 3, 17, '', '33', 1489770054414),
	(160, 32, 1, 18, '', '8', 1489770129985),
	(161, 8, 3, 15, '', '32', 1489770129985),
	(162, 28, 1, 12, '1', '2', 1489770159621),
	(163, 28, 1, 13, '-1', '1', 1489770159621);
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
	(1, 'Project01', 'Project01', '', '', 2, 1489766129651, 1489766129651),
	(2, 'Project02', 'Project02', '', '', 2, 1489767930279, 1489767930279);
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
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.project_role 的資料：4 rows
/*!40000 ALTER TABLE `project_role` DISABLE KEYS */;
REPLACE INTO `project_role` (`id`, `project_id`, `account_id`, `role`, `create_time`, `update_time`) VALUES
	(1, 1, 2, 2, 1489767799662, 1489767799662),
	(2, 1, 3, 2, 1489767838571, 1489767838571),
	(3, 2, 2, 2, 1489769057251, 1489769057251),
	(4, 2, 3, 2, 1489769066940, 1489769066940);
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
	(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1489766129654, 1489766129654),
	(2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1489766129655, 1489766129655),
	(3, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 2, 1489766129656, 1489766129656),
	(4, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 3, 1489766129656, 1489766129656),
	(5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 4, 1489766129657, 1489766129657),
	(6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1489767930279, 1489767930279),
	(7, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1489767930280, 1489767930280),
	(8, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 1489767930280, 1489767930280),
	(9, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 3, 1489767930280, 1489767930280),
	(10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 1489767930280, 1489767930280);
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
	(1, 1, 0, 2, 4, 16, 0, 0),
	(2, 2, 0, 3, 4, 17, 0, 0);
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
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.sprint 的資料：4 rows
/*!40000 ALTER TABLE `sprint` DISABLE KEYS */;
REPLACE INTO `sprint` (`id`, `serial_id`, `goal`, `interval`, `team_size`, `available_hours`, `focus_factor`, `start_date`, `end_date`, `demo_date`, `demo_place`, `daily_info`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 1, 'SprintTest01', 2, 3, 40, 20, NOW(), NOW() + INTERVAL 13 DAY, NOW() + INTERVAL 13 DAY, '', '', 1, 1489766486240, 1489766486240),
	(2, 2, 'SprintTest02', 4, 3, 60, 30, NOW() + INTERVAL 14 DAY, NOW() + INTERVAL 41 DAY, NOW() + INTERVAL 41 DAY, '', '', 1, 1489766511552, 1489766511552),
	(3, 1, 'Sprint01', 2, 3, 40, 30, NOW(), NOW() + INTERVAL 13 DAY, NOW() + INTERVAL 13 DAY, '', '', 2, 1489767988021, 1489767988021),
	(4, 2, 'Sprint02', 3, 3, 50, 40, NOW() + INTERVAL 14 DAY, NOW() + INTERVAL 34 DAY, NOW() + INTERVAL 34 DAY, '', '', 2, 1489768013628, 1489768013628);
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
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.story 的資料：8 rows
/*!40000 ALTER TABLE `story` DISABLE KEYS */;
REPLACE INTO `story` (`id`, `project_id`, `serial_id`, `sprint_id`, `name`, `status`, `estimate`, `importance`, `value`, `notes`, `how_to_demo`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 'StoryTest01', 1, 8, 90, 0, '', '', 1489766531693, 1489766531693),
	(2, 1, 2, 1, 'StoryTest02', 1, 5, 80, 0, '', '', 1489766613364, 1489766613364),
	(3, 1, 3, 2, 'StoryTest11', 1, 9, 70, 0, '', '', 1489767196594, 1489767196594),
	(4, 1, 4, 2, 'StoryTest12', 1, 6, 75, 1, '', '', 1489767226832, 1489767226832),
	(5, 2, 1, 3, 'Story01', 1, 12, 80, 0, '', '', 1489768204063, 1489768204063),
	(6, 2, 2, 3, 'Story02', 1, 8, 75, 0, '', '', 1489768218754, 1489768218754),
	(7, 2, 3, 4, 'Story11', 1, 7, 80, 0, '', '', 1489768774929, 1489768774929),
	(8, 2, 4, 4, 'Story12', 1, 9, 90, 0, '', '', 1489768794875, 1489768794875);
/*!40000 ALTER TABLE `story` ENABLE KEYS */;


-- 導出  表 ezscrum_180.story_tag_relation 結構
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
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.tag 的資料：0 rows
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
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
  /*`actual` int(11) NOT NULL DEFAULT '0',*/
  `notes` text,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.task 的資料：33 rows
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
REPLACE INTO `task` (`id`, `serial_id`, `project_id`, `story_id`, `name`, `handler_id`, `status`, `estimate`, `remain`, /*`actual`,*/ `notes`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 'TaskTestOne01', 1, 3, 3, 0, /*12,*/ '', 1489766654548, 1489767111007),
	(2, 2, 1, 1, 'TaskTestOne02', 1, 2, 2, 0, /*0,*/ '', 1489766715747, 1489767114482),
	(3, 3, 1, 1, 'TaskTestOne03', 1, 2, 4, 4, /*0,*/ '', 1489766729487, 1489767093101),
	(4, 4, 1, 1, 'TaskTestOne04', -1, 1, 5, 5, /*0,*/ '', 1489766805828, 1489766862866),
	(5, 5, 1, 2, 'TaskTestTwo01', -1, 1, 5, 5, /*0,*/ '', 1489766822889, 1489766851368),
	(6, 6, 1, 2, 'TaskTestTwo02', -1, 1, 3, 3, /*0,*/ '', 1489766843705, 1489766843705),
	(7, 7, 1, 2, 'TaskTestTwo03', 1, 2, 4, 4, /*0,*/ '', 1489767013717, 1489767126848),
	(8, 8, 1, 2, 'TaskTestTwo04', 1, 2, 2, 2, /*0,*/ '', 1489767041988, 1489767124860),
	(9, 9, 1, 2, 'TaskTestTwo05', -1, 1, 3, 3, /*0,*/ '', 1489767055402, 1489767055402),
	(10, 10, 1, 4, 'TaskTestTwo12', -1, 1, 5, 5, /*0,*/ '', 1489767254975, 1489767254975),
	(11, 11, 1, 4, 'TaskTestTwo11', 1, 2, 3, 3, /*0,*/ '', 1489767272021, 1489767582683),
	(12, 12, 1, 4, 'TaskTestTwo13', -1, 1, 3, 3, /*0,*/ '', 1489767293423, 1489767293423),
	(13, 13, 1, 3, 'TaskTestOne11', -1, 1, 3, 3, /*0,*/ '', 1489767323275, 1489767323275),
	(14, 14, 1, 3, 'TaskTestOne12', 1, 2, 2, 2, /*0,*/ '', 1489767339774, 1489767591620),
	(15, 15, 1, 3, 'TaskTestOne13', 1, 2, 4, 4, /*0,*/ '', 1489767352056, 1489767589347),
	(16, 16, 1, 3, 'TaskTestOne14', -1, 1, 6, 6, /*0,*/ '', 1489767573607, 1489767573607),
	(17, 1, 2, 5, 'Task01', 1, 3, 2, 0, /*0,*/ '', 1489768230903, 1489769029316),
	(18, 2, 2, 5, 'Task02', 1, 2, 2, 2, /*0,*/ '', 1489768245143, 1489769022611),
	(19, 3, 2, 5, 'Task03', 1, 2, 2, 2, /*0,*/ '', 1489768254733, 1489769020480),
	(20, 4, 2, 5, 'Task04', -1, 1, 3, 3, /*0,*/ '', 1489768300472, 1489768300472),
	(21, 5, 2, 6, 'Task01', -1, 1, 3, 3, /*0,*/ '', 1489768318520, 1489768318520),
	(22, 6, 2, 6, 'Task02', -1, 1, 1, 1, /*0,*/ '', 1489768328782, 1489768328782),
	(23, 7, 2, 6, 'Task03', 1, 2, 5, 5, /*0,*/ '', 1489768339040, 1489769038147),
	(24, 8, 2, 6, 'Task04', 1, 2, 3, 3, /*0,*/ '', 1489768349876, 1489769036116),
	(25, 9, 2, 6, 'Task05', -1, 1, 3, 3, /*0,*/ '', 1489768359905, 1489768359905),
	(26, 10, 2, 7, 'Task11', -1, 1, 8, 8, /*0,*/ '', 1489768809783, 1489768809783),
	(27, 11, 2, 7, 'Task12', 1, 2, 12, 12, /*0,*/ '', 1489768818851, 1489769973263),
	(28, 12, 2, 7, 'Task13', 1, 2, 2, 2, /*0,*/ '', 1489768899172, 1489770159621),
	(29, 13, 2, 8, 'TaskTwo11', -1, 1, 1, 1, /*0,*/ '', 1489768955336, 1489768955336),
	(30, 14, 2, 8, 'TaskTwo12', 1, 2, 3, 3, /*0,*/ '', 1489768972376, 1489769984885),
	(31, 15, 2, 8, 'TaskTwo13', 1, 2, 4, 4, /*0,*/ '', 1489768986808, 1489769980335),
	(32, 16, 2, -1, 'TaskTwo14', -1, 1, 2, 2, /*0,*/ '', 1489768996505, 1489770129985),
	(33, 17, 2, 7, 'Task14', -1, 1, 2, 2, /*0,*/ '', 1489770054407, 1489770054407);
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
  /*`actual` int(11) NOT NULL,*/
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
