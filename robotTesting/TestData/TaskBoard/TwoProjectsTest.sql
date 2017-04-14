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
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.account 的資料：3 rows
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
REPLACE INTO `account` (`id`, `username`, `nick_name`, `email`, `password`, `enable`, `create_time`, `update_time`) VALUES
	(1, 'admin', 'admin', 'example@ezScrum.tw', '21232f297a57a5a743894a0e4a801fc3', 1, 1379910191599, 1379910191599),
	(2, 'admin2', 'admin2', 'admin2@gmail.com', 'c84258e9c39059a89ab77d846ddab909', 1, 1489670865621, 1489670865621),
	(3, 'admin3', 'admin3', 'admin3@gmail.com', '32cacb2f994f6b42183a1300d9a3e8d6', 1, 1489670902123, 1489670902123);
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
) ENGINE=MyISAM AUTO_INCREMENT=204 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.history 的資料：203 rows
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
REPLACE INTO `history` (`id`, `issue_id`, `issue_type`, `type`, `old_value`, `new_value`, `create_time`) VALUES
	(1, 1, 3, 1, '', '', 1489670956068),
	(2, 2, 3, 1, '', '', 1489671019349),
	(3, 2, 3, 16, '', '1', 1489671019349),
	(4, 3, 3, 1, '', '', 1489671037196),
	(5, 3, 3, 16, '', '1', 1489671037196),
	(6, 1, 1, 1, '', '', 1489671063862),
	(7, 1, 1, 16, '', '2', 1489671063871),
	(8, 2, 3, 17, '', '1', 1489671063873),
	(9, 2, 1, 1, '', '', 1489671084917),
	(10, 2, 1, 16, '', '2', 1489671084923),
	(11, 2, 3, 17, '', '2', 1489671084924),
	(12, 1, 1, 19, '', '1', 1489671092718),
	(13, 3, 1, 1, '', '', 1489671102948),
	(14, 3, 1, 16, '', '2', 1489671102953),
	(15, 2, 3, 17, '', '3', 1489671102962),
	(16, 4, 1, 1, '', '', 1489671109060),
	(17, 4, 1, 16, '', '2', 1489671109065),
	(18, 2, 3, 17, '', '4', 1489671109067),
	(19, 5, 1, 1, '', '', 1489671116098),
	(20, 5, 1, 16, '', '2', 1489671116105),
	(21, 2, 3, 17, '', '5', 1489671116106),
	(22, 6, 1, 1, '', '', 1489671134283),
	(23, 6, 1, 16, '', '3', 1489671134288),
	(24, 3, 3, 17, '', '6', 1489671134289),
	(25, 1, 1, 2, 'SprintStoryTask1', 'SprintStoryTask11', 1489671142959),
	(26, 2, 1, 2, 'SprintStoryTask2', 'SprintStoryTask12', 1489671149320),
	(27, 3, 1, 2, 'SprintStoryTask3', 'SprintStoryTask13', 1489671154423),
	(28, 4, 1, 2, 'SprintStoryTask4', 'SprintStoryTask14', 1489671159622),
	(29, 5, 1, 2, 'SprintStoryTask5', 'SprintStoryTask15', 1489671165397),
	(30, 7, 1, 1, '', '', 1489671175099),
	(31, 7, 1, 16, '', '3', 1489671175105),
	(32, 3, 3, 17, '', '7', 1489671175106),
	(33, 8, 1, 1, '', '', 1489671184287),
	(34, 8, 1, 16, '', '3', 1489671184293),
	(35, 3, 3, 17, '', '8', 1489671184295),
	(36, 9, 1, 1, '', '', 1489671194605),
	(37, 9, 1, 16, '', '3', 1489671194612),
	(38, 3, 3, 17, '', '9', 1489671194613),
	(39, 4, 3, 1, '', '', 1489671244877),
	(40, 4, 3, 16, '', '2', 1489671244877),
	(41, 10, 1, 1, '', '', 1489671259147),
	(42, 10, 1, 16, '', '4', 1489671259153),
	(43, 4, 3, 17, '', '10', 1489671259155),
	(44, 11, 1, 1, '', '', 1489671267019),
	(45, 11, 1, 16, '', '4', 1489671267025),
	(46, 4, 3, 17, '', '11', 1489671267027),
	(47, 12, 1, 1, '', '', 1489671276028),
	(48, 12, 1, 16, '', '4', 1489671276033),
	(49, 4, 3, 17, '', '12', 1489671276034),
	(50, 9, 1, 13, '-1', '2', 1489671290392),
	(51, 8, 1, 13, '-1', '3', 1489671294540),
	(52, 7, 1, 13, '-1', '3', 1489671298726),
	(53, 6, 1, 13, '-1', '2', 1489671302344),
	(54, 5, 1, 13, '-1', '3', 1489671306870),
	(55, 4, 1, 13, '-1', '2', 1489671317952),
	(56, 3, 1, 13, '-1', '3', 1489671322383),
	(57, 2, 1, 13, '-1', '3', 1489671327653),
	(58, 1, 1, 13, '-1', '2', 1489671332654),
	(59, 9, 1, 12, '1', '2', 1489671340676),
	(60, 7, 1, 12, '1', '2', 1489671344699),
	(61, 4, 1, 12, '1', '2', 1489671350690),
	(62, 4, 1, 13, '2', '1', 1489671350690),
	(63, 1, 1, 12, '1', '2', 1489671355635),
	(64, 1, 1, 13, '2', '3', 1489671355635),
	(65, 2, 1, 12, '1', '2', 1489671365547),
	(66, 2, 1, 13, '3', '2', 1489671365547),
	(67, 11, 1, 12, '1', '2', 1489671426779),
	(68, 11, 1, 13, '-1', '1', 1489671426779),
	(69, 12, 1, 13, '-1', '3', 1489671431102),
	(70, 11, 1, 13, '1', '3', 1489671438360),
	(71, 11, 1, 13, '3', '1', 1489671440652),
	(72, 10, 1, 13, '-1', '2', 1489671445783),
	(73, 10, 1, 12, '1', '2', 1489671459547),
	(74, 10, 1, 13, '2', '1', 1489671459547),
	(75, 5, 3, 1, '', '', 1489671535055),
	(76, 6, 3, 1, '', '', 1489671917182),
	(77, 6, 3, 16, '', '3', 1489671917182),
	(78, 7, 3, 1, '', '', 1489671928052),
	(79, 7, 3, 16, '', '3', 1489671928052),
	(80, 13, 1, 1, '', '', 1489671955364),
	(81, 13, 1, 16, '', '6', 1489671955371),
	(82, 6, 3, 17, '', '13', 1489671955373),
	(83, 14, 1, 1, '', '', 1489671966451),
	(84, 14, 1, 16, '', '6', 1489671966458),
	(85, 6, 3, 17, '', '14', 1489671966460),
	(86, 15, 1, 1, '', '', 1489671973755),
	(87, 15, 1, 16, '', '6', 1489671973762),
	(88, 6, 3, 17, '', '15', 1489671973764),
	(89, 16, 1, 1, '', '', 1489671986861),
	(90, 16, 1, 16, '', '6', 1489671986867),
	(91, 6, 3, 17, '', '16', 1489671986869),
	(92, 17, 1, 1, '', '', 1489671995452),
	(93, 17, 1, 16, '', '6', 1489671995457),
	(94, 6, 3, 17, '', '17', 1489671995459),
	(95, 18, 1, 1, '', '', 1489672007443),
	(96, 18, 1, 16, '', '7', 1489672007450),
	(97, 7, 3, 17, '', '18', 1489672007452),
	(98, 19, 1, 1, '', '', 1489672014829),
	(99, 19, 1, 16, '', '7', 1489672014835),
	(100, 7, 3, 17, '', '19', 1489672014837),
	(101, 20, 1, 1, '', '', 1489672033339),
	(102, 20, 1, 16, '', '7', 1489672033344),
	(103, 7, 3, 17, '', '20', 1489672033346),
	(104, 21, 1, 1, '', '', 1489672045172),
	(105, 21, 1, 16, '', '7', 1489672045178),
	(106, 7, 3, 17, '', '21', 1489672045180),
	(107, 8, 3, 1, '', '', 1489672082499),
	(108, 8, 3, 16, '', '4', 1489672082499),
	(109, 22, 1, 1, '', '', 1489672106627),
	(110, 22, 1, 16, '', '8', 1489672106633),
	(111, 8, 3, 17, '', '22', 1489672106636),
	(112, 23, 1, 1, '', '', 1489672121300),
	(113, 23, 1, 16, '', '8', 1489672121306),
	(114, 8, 3, 17, '', '23', 1489672121308),
	(115, 24, 1, 1, '', '', 1489672139320),
	(116, 24, 1, 16, '', '8', 1489672139329),
	(117, 8, 3, 17, '', '24', 1489672139331),
	(118, 9, 1, 2, 'SprintStoryTask24', 'SprintStoryTaskFour4', 1489672301685),
	(119, 9, 1, 19, '24', '4', 1489672301685),
	(120, 9, 1, 13, '2', '1', 1489672301685),
	(121, 7, 1, 2, 'SprintStoryTask22', 'SprintStoryTasktwo2', 1489672305023),
	(122, 7, 1, 19, '22', '2', 1489672305023),
	(123, 21, 1, 13, '-1', '2', 1489672323777),
	(124, 20, 1, 13, '-1', '3', 1489672328658),
	(125, 19, 1, 13, '-1', '2', 1489672333313),
	(126, 18, 1, 13, '-1', '2', 1489672338065),
	(127, 17, 1, 13, '-1', '3', 1489672342993),
	(128, 16, 1, 13, '-1', '3', 1489672348787),
	(129, 15, 1, 13, '-1', '3', 1489672353907),
	(130, 14, 1, 13, '-1', '2', 1489672358257),
	(131, 13, 1, 13, '-1', '2', 1489672364491),
	(132, 9, 1, 13, '1', '3', 1489672382286),
	(133, 7, 1, 13, '3', '2', 1489672389798),
	(134, 20, 1, 13, '3', '2', 1489672449248),
	(135, 18, 1, 13, '2', '3', 1489672457714),
	(136, 17, 1, 13, '3', '2', 1489672523905),
	(137, 15, 1, 13, '3', '2', 1489672537848),
	(138, 4, 1, 2, 'SprintStoryTask14', 'SprintStoryTaskfour', 1489672573350),
	(139, 4, 1, 13, '1', '2', 1489672573350),
	(140, 2, 1, 2, 'SprintStoryTask12', 'SprintStoryTaskTwo', 1489672593985),
	(141, 2, 1, 13, '2', '1', 1489672593985),
	(142, 1, 1, 2, 'SprintStoryTask11', 'SprintStoryTaskOne', 1489672599957),
	(143, 1, 1, 13, '3', '1', 1489672599957),
	(144, 24, 1, 13, '-1', '2', 1489672634313),
	(145, 11, 1, 2, 'SprintStoryTask32', 'SprintStoryTaskThree2', 1489672678413),
	(146, 11, 1, 13, '1', '2', 1489672678413),
	(147, 10, 1, 2, 'SprintStoryTask31', 'SprintStoryTaskThreeOne', 1489672682069),
	(148, 10, 1, 13, '1', '3', 1489672682069),
	(149, 9, 1, 13, '3', '1', 1489672738000),
	(150, 7, 1, 13, '2', '1', 1489672755622),
	(151, 4, 1, 13, '2', '1', 1489672764495),
	(152, 2, 1, 13, '1', '2', 1489672778397),
	(153, 1, 1, 13, '1', '3', 1489672782262),
	(154, 23, 1, 13, '-1', '3', 1489672852987),
	(155, 22, 1, 13, '-1', '2', 1489672856785),
	(156, 11, 1, 13, '2', '1', 1489672921758),
	(157, 10, 1, 13, '3', '2', 1489672943510),
	(158, 7, 1, 13, '1', '3', 1489673034478),
	(159, 8, 1, 2, 'SprintStoryTask23', 'SprintStoryTaskThree3', 1489673060032),
	(160, 8, 1, 19, '23', '3', 1489673060032),
	(161, 8, 1, 12, '1', '2', 1489673060032),
	(162, 8, 1, 12, '2', '1', 1489673065198),
	(163, 8, 1, 13, '3', '-1', 1489673065198),
	(164, 7, 1, 13, '3', '1', 1489673071062),
	(165, 8, 1, 12, '1', '2', 1489673083870),
	(166, 8, 1, 13, '-1', '1', 1489673083870),
	(167, 6, 1, 2, 'SprintStoryTask21', 'SprintStoryTaskOne1', 1489673180910),
	(168, 6, 1, 19, '21', '11', 1489673180910),
	(169, 6, 1, 12, '1', '2', 1489673180910),
	(170, 6, 1, 13, '2', '1', 1489673180910),
	(171, 3, 1, 2, 'SprintStoryTask13', 'SprintStoryTaskThree', 1489673260079),
	(172, 3, 1, 12, '1', '2', 1489673260079),
	(173, 3, 1, 13, '3', '1', 1489673260079),
	(174, 2, 1, 13, '2', '1', 1489673261718),
	(175, 11, 1, 4, '32', '0', 1489766285409),
	(176, 11, 1, 12, '2', '3', 1489766285409),
	(177, 8, 1, 4, '23', '0', 1489766681420),
	(178, 8, 1, 12, '2', '3', 1489766681420),
	(179, 3, 1, 4, '3', '0', 1489766690194),
	(180, 3, 1, 12, '2', '3', 1489766690194),
	(181, 21, 1, 12, '1', '2', 1489766714942),
	(182, 21, 1, 13, '2', '3', 1489766714942),
	(183, 20, 1, 12, '1', '2', 1489766718042),
	(184, 19, 1, 12, '1', '2', 1489766725800),
	(185, 19, 1, 4, '2', '0', 1489766731121),
	(186, 19, 1, 12, '2', '3', 1489766731121),
	(187, 18, 1, 12, '1', '2', 1489766733921),
	(188, 18, 1, 13, '3', '1', 1489766733921),
	(189, 17, 1, 12, '1', '2', 1489766737401),
	(190, 17, 1, 13, '2', '1', 1489766737401),
	(191, 16, 1, 12, '1', '2', 1489766739633),
	(192, 16, 1, 13, '3', '1', 1489766739633),
	(193, 14, 1, 12, '1', '2', 1489766744752),
	(194, 14, 1, 13, '2', '3', 1489766744752),
	(195, 16, 1, 4, '4', '0', 1489766746649),
	(196, 16, 1, 12, '2', '3', 1489766746649),
	(197, 13, 1, 12, '1', '2', 1489766751152),
	(198, 23, 1, 12, '1', '2', 1489766759080),
	(199, 23, 1, 13, '3', '1', 1489766759080),
	(200, 23, 1, 4, '32', '0', 1489766761129),
	(201, 23, 1, 12, '2', '3', 1489766761129),
	(202, 22, 1, 12, '1', '2', 1489766764498),
	(203, 22, 1, 13, '2', '3', 1489766764498);
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
	(1, 'FirstCreateProject', 'FirstCreateProject', '', '', 2, 1489670834616, 1489670834616),
	(2, 'SecondCreateProject', 'SecondCreateProject', '', '', 2, 1489671517398, 1489671517398);
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
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.project_role 的資料：5 rows
/*!40000 ALTER TABLE `project_role` DISABLE KEYS */;
REPLACE INTO `project_role` (`id`, `project_id`, `account_id`, `role`, `create_time`, `update_time`) VALUES
	(1, 1, 3, 2, 1489670915809, 1489670915809),
	(2, 1, 2, 2, 1489670921985, 1489670921985),
	(3, 1, 1, 0, 1489670929594, 1489670929594),
	(4, 2, 2, 2, 1489672266017, 1489672266017),
	(5, 2, 3, 2, 1489672272281, 1489672272281);
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
	(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1489670834617, 1489670834617),
	(2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1489670834625, 1489670834625),
	(3, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 2, 1489670834626, 1489670834626),
	(4, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 3, 1489670834627, 1489670834627),
	(5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 4, 1489670834627, 1489670834627),
	(6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1489671517399, 1489671517399),
	(7, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1489671517400, 1489671517400),
	(8, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 1489671517400, 1489671517400),
	(9, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 3, 1489671517401, 1489671517401),
	(10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 1489671517402, 1489671517402);
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
	(1, 1, 0, 2, 4, 12, 0, 0),
	(2, 2, 0, 2, 4, 12, 0, 0);
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
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.sprint 的資料：4 rows
/*!40000 ALTER TABLE `sprint` DISABLE KEYS */;
REPLACE INTO `sprint` (`id`, `serial_id`, `goal`, `interval`, `team_size`, `available_hours`, `focus_factor`, `start_date`, `end_date`, `demo_date`, `demo_place`, `daily_info`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 1, 'Sprint1', 2, 4, 4, 4, NOW(), NOW() + INTERVAL 13 DAY, NOW() + INTERVAL 13 DAY, '', '', 1, 1489670977879, 1489670977879),
	(2, 2, 'Sprint2', 3, 4, 5, 5, NOW() + INTERVAL 14 DAY, NOW() + INTERVAL 34 DAY, NOW() + INTERVAL 34 DAY, '', '', 1, 1489670996009, 1489768745505),
	(3, 1, 'SprintStoryTwo', 2, 4, 4, 4, NOW(), NOW() + INTERVAL 13 DAY, NOW() + INTERVAL 13 DAY, '', '', 2, 1489671560391, 1489671560391),
	(4, 2, 'SprintStoryThree', 3, 4, 4, 4, NOW() + INTERVAL 14 DAY, NOW() + INTERVAL 34 DAY, NOW() + INTERVAL 34 DAY, '', '', 2, 1489671591405, 1489768942304);
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
	(1, 1, 1, -1, 'First1', 1, 1, 1, 1, '1', '', 1489670956068, 1489670956068),
	(2, 1, 2, 1, 'SprintStory1', 1, 1, 1, 1, '1', '', 1489671019349, 1489671019349),
	(3, 1, 3, 1, 'SprintStory2', 1, 2, 2, 2, '2', '', 1489671037196, 1489671037196),
	(4, 1, 4, 2, 'SprintStory3', 1, 3, 3, 3, '3', '', 1489671244877, 1489671244877),
	(5, 2, 1, -1, 'Second1', 1, 1, 1, 1, '1', '', 1489671535055, 1489671535055),
	(6, 2, 2, 3, 'SprintStoryOne', 1, 1, 1, 1, '1', '', 1489671917182, 1489671917182),
	(7, 2, 3, 3, 'SprintStoryTwo', 1, 2, 2, 2, '2', '', 1489671928052, 1489671928052),
	(8, 2, 4, 4, 'SprintStoryThree', 1, 3, 3, 3, '3', '', 1489672082499, 1489672082499);
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
  `actual` int(11) NOT NULL DEFAULT '0',
  `notes` text,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

-- 正在導出表  ezscrum_180.task 的資料：24 rows
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
REPLACE INTO `task` (`id`, `serial_id`, `project_id`, `story_id`, `name`, `handler_id`, `status`, `estimate`, `remain`, `actual`, `notes`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 2, 'SprintStoryTaskOne', 3, 2, 1, 1, 0, '1', 1489671063862, 1489672782262),
	(2, 2, 1, 2, 'SprintStoryTaskTwo', 1, 2, 2, 2, 0, '2', 1489671084917, 1489673261718),
	(3, 3, 1, 2, 'SprintStoryTaskThree', 1, 3, 3, 0, 0, '3', 1489671102948, 1489766690194),
	(4, 4, 1, 2, 'SprintStoryTaskfour', 1, 2, 4, 4, 0, '4', 1489671109060, 1489673265358),
	(5, 5, 1, 2, 'SprintStoryTask15', 3, 1, 5, 5, 0, '5', 1489671116098, 1489671306870),
	(6, 6, 1, 3, 'SprintStoryTaskOne1', 1, 2, 21, 21, 0, '11', 1489671134283, 1489673180910),
	(7, 7, 1, 3, 'SprintStoryTasktwo2', 1, 2, 22, 22, 0, '2', 1489671175099, 1489673257046),
	(8, 8, 1, 3, 'SprintStoryTaskThree3', 1, 3, 23, 0, 0, '3', 1489671184287, 1489766681420),
	(9, 9, 1, 3, 'SprintStoryTaskFour4', 1, 2, 24, 24, 0, '4', 1489671194605, 1489673156079),
	(10, 10, 1, 4, 'SprintStoryTaskThreeOne', 2, 2, 31, 31, 0, '31', 1489671259147, 1489672943510),
	(11, 11, 1, 4, 'SprintStoryTaskThree2', 1, 3, 32, 0, 0, '32', 1489671267019, 1489766285409),
	(12, 12, 1, 4, 'SprintStoryTask33', 3, 1, 33, 33, 0, '33', 1489671276028, 1489671431102),
	(13, 1, 2, 6, 'SprintStoryTaskOne', 2, 2, 1, 1, 0, '1', 1489671955364, 1489766751152),
	(14, 2, 2, 6, 'SprintStoryTaskTwo', 3, 2, 2, 2, 0, '2', 1489671966451, 1489766744752),
	(15, 3, 2, 6, 'SprintStoryTaskThree', 2, 1, 3, 3, 0, '3', 1489671973755, 1489672537848),
	(16, 4, 2, 6, 'SprintStoryTaskfour', 1, 3, 4, 0, 0, '4', 1489671986861, 1489766746649),
	(17, 5, 2, 6, 'SprintStoryTaskfive', 1, 2, 5, 5, 0, '5', 1489671995452, 1489766737401),
	(18, 6, 2, 7, 'SprintStoryTaskOne1', 1, 2, 11, 11, 0, '11', 1489672007443, 1489766733921),
	(19, 7, 2, 7, 'SprintStoryTasktwo2', 2, 3, 2, 0, 0, '2', 1489672014829, 1489766731121),
	(20, 8, 2, 7, 'SprintStoryTaskThree3', 2, 2, 3, 3, 0, '3', 1489672033339, 1489766718042),
	(21, 9, 2, 7, 'SprintStoryTaskFour4', 3, 2, 4, 4, 0, '4', 1489672045172, 1489766714942),
	(22, 10, 2, 8, 'SprintStoryTaskThreeOne', 3, 2, 31, 31, 0, '31', 1489672106627, 1489766764498),
	(23, 11, 2, 8, 'SprintStoryTaskThree2', 1, 3, 32, 0, 0, '32', 1489672121300, 1489766761129),
	(24, 12, 2, 8, 'SprintStoryTaskThreeThree', 2, 1, 33, 33, 0, '33', 1489672139320, 1489672634313);
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
