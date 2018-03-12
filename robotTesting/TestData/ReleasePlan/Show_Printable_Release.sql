-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.7.8-rc-log - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.3.0.4984
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping database structure for ezscrum_180
CREATE DATABASE IF NOT EXISTS `ezscrum_180` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ezscrum_180`;


-- Dumping structure for table ezscrum_180.account
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
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.account: 2 rows
DELETE FROM `account`;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` (`id`, `username`, `nick_name`, `email`, `password`, `enable`, `create_time`, `update_time`) VALUES
	(1, 'admin', 'admin', 'example@ezScrum.tw', '21232f297a57a5a743894a0e4a801fc3', 1, 1379910191599, 1379910191599),
	(2, 'TestUser', 'TestUser', 'TestUser@gmail.com', '7a95dec218ffaaf8992bb48b4bd94367', 1, 1477295704472, 1477295704472);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.attach_file
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

-- Dumping data for table ezscrum_180.attach_file: 0 rows
DELETE FROM `attach_file`;
/*!40000 ALTER TABLE `attach_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `attach_file` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.history
CREATE TABLE IF NOT EXISTS `history` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `issue_id` bigint(20) unsigned NOT NULL,
  `issue_type` int(11) NOT NULL,
  `type` int(11) DEFAULT NULL,
  `old_value` text,
  `new_value` text,
  `create_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.history: 48 rows
DELETE FROM `history`;
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
INSERT INTO `history` (`id`, `issue_id`, `issue_type`, `type`, `old_value`, `new_value`, `create_time`) VALUES
	(1, 1, 3, 1, '', '', 1477295620079),
	(2, 1, 3, 16, '', '1', 1477295620079),
	(3, 2, 3, 1, '', '', 1477295650623),
	(4, 2, 3, 16, '', '1', 1477295650623),
	(5, 3, 3, 1, '', '', 1477295762160),
	(6, 3, 3, 16, '', '2', 1477295762160),
	(7, 1, 1, 1, '', '', 1477295805856),
	(8, 1, 1, 16, '', '1', 1477295805860),
	(9, 1, 3, 17, '', '1', 1477295805860),
	(10, 2, 1, 1, '', '', 1477295823015),
	(11, 2, 1, 16, '', '2', 1477295823022),
	(12, 2, 3, 17, '', '2', 1477295823022),
	(13, 3, 1, 1, '', '', 1477295839375),
	(14, 3, 1, 16, '', '3', 1477295839378),
	(15, 3, 3, 17, '', '3', 1477295839379),
	(16, 4, 1, 1, '', '', 1477295852039),
	(17, 4, 1, 16, '', '3', 1477295852042),
	(18, 3, 3, 17, '', '4', 1477295852043),
	(19, 2, 1, 12, '1', '2', 1477295914375),
	(20, 2, 1, 13, '-1', '1', 1477295914375),
	(21, 2, 1, 21, '', '2', 1477295914375),
	(22, 2, 1, 4, '22', '0', 1477295918847),
	(23, 2, 1, 12, '2', '3', 1477295918847),
	(24, 2, 1, 12, '3', '2', 1477295927232),
	(25, 2, 1, 22, '', '2', 1477295927232),
	(26, 2, 1, 5, '0', '22', 1477295934606),
	(27, 2, 1, 12, '2', '3', 1477295934606),
	(28, 2, 1, 12, '3', '2', 1477295942887),
	(29, 2, 1, 12, '2', '1', 1477295946600),
	(30, 2, 1, 13, '1', '-1', 1477295946600),
	(31, 2, 1, 12, '1', '2', 1477295953095),
	(32, 2, 1, 13, '-1', '1', 1477295953095),
	(33, 2, 1, 21, '', '2', 1477295953095),
	(34, 2, 1, 12, '2', '3', 1477295964975),
	(35, 1, 1, 12, '1', '2', 1477295970279),
	(36, 1, 1, 13, '-1', '2', 1477295970279),
	(37, 1, 1, 5, '0', '10', 1477295975447),
	(38, 1, 1, 4, '11', '0', 1477295975447),
	(39, 1, 1, 12, '2', '3', 1477295975447),
	(40, 4, 1, 12, '1', '2', 1477296011791),
	(41, 4, 1, 13, '-1', '1', 1477296011791),
	(42, 4, 1, 21, '', '2', 1477296011791),
	(43, 4, 1, 4, '44', '0', 1477296014287),
	(44, 4, 1, 12, '2', '3', 1477296014287),
	(45, 3, 1, 12, '1', '2', 1477296019462),
	(46, 3, 1, 13, '-1', '2', 1477296019462),
	(47, 3, 1, 4, '33', '0', 1477296022839),
	(48, 3, 1, 12, '2', '3', 1477296022839);
/*!40000 ALTER TABLE `history` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.issue_partner_relation
CREATE TABLE IF NOT EXISTS `issue_partner_relation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `issue_id` bigint(20) unsigned NOT NULL,
  `issue_type` int(11) NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.issue_partner_relation: 2 rows
DELETE FROM `issue_partner_relation`;
/*!40000 ALTER TABLE `issue_partner_relation` DISABLE KEYS */;
INSERT INTO `issue_partner_relation` (`id`, `issue_id`, `issue_type`, `account_id`) VALUES
	(2, 2, 1, 2),
	(3, 4, 1, 2);
/*!40000 ALTER TABLE `issue_partner_relation` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.project
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
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.project: 1 rows
DELETE FROM `project`;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` (`id`, `name`, `display_name`, `comment`, `product_owner`, `attach_max_size`, `create_time`, `update_time`) VALUES
	(1, 'Test Project', 'Test Project', '', '', 2, 1477295514784, 1477295514784);
/*!40000 ALTER TABLE `project` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.project_role
CREATE TABLE IF NOT EXISTS `project_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) unsigned NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  `role` int(11) NOT NULL,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.project_role: 1 rows
DELETE FROM `project_role`;
/*!40000 ALTER TABLE `project_role` DISABLE KEYS */;
INSERT INTO `project_role` (`id`, `project_id`, `account_id`, `role`, `create_time`, `update_time`) VALUES
	(1, 1, 2, 2, 1477295726391, 1477295726391);
/*!40000 ALTER TABLE `project_role` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.release
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

-- Dumping data for table ezscrum_180.release: 1 rows
DELETE FROM `release`;
/*!40000 ALTER TABLE `release` DISABLE KEYS */;
INSERT INTO `release` (`id`, `serial_id`, `name`, `description`, `start_date`, `end_date`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 1, 'Release One', 'Description for Release One', '2016-10-24 00:00:00', '2016-11-13 00:00:00', 1, 1477295884913, 1477295884913);
/*!40000 ALTER TABLE `release` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.retrospective
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

-- Dumping data for table ezscrum_180.retrospective: 0 rows
DELETE FROM `retrospective`;
/*!40000 ALTER TABLE `retrospective` DISABLE KEYS */;
/*!40000 ALTER TABLE `retrospective` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.scrum_role
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
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.scrum_role: 5 rows
DELETE FROM `scrum_role`;
/*!40000 ALTER TABLE `scrum_role` DISABLE KEYS */;
INSERT INTO `scrum_role` (`id`, `access_productBacklog`, `access_sprintPlan`, `access_taskboard`, `access_sprintBacklog`, `access_releasePlan`, `access_retrospective`, `access_unplan`, `access_report`, `access_editProject`, `project_id`, `role`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1477295514784, 1477295514784),
	(2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1477295514785, 1477295514785),
	(3, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 2, 1477295514785, 1477295514785),
	(4, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 3, 1477295514785, 1477295514785),
	(5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 4, 1477295514786, 1477295514786);
/*!40000 ALTER TABLE `scrum_role` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.serial_number
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
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.serial_number: 1 rows
DELETE FROM `serial_number`;
/*!40000 ALTER TABLE `serial_number` DISABLE KEYS */;
INSERT INTO `serial_number` (`id`, `project_id`, `release`, `sprint`, `story`, `task`, `unplan`, `retrospective`) VALUES
	(1, 1, 1, 2, 3, 4, 0, 0);
/*!40000 ALTER TABLE `serial_number` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.sprint
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
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.sprint: 2 rows
DELETE FROM `sprint`;
/*!40000 ALTER TABLE `sprint` DISABLE KEYS */;
INSERT INTO `sprint` (`id`, `serial_id`, `goal`, `interval`, `team_size`, `available_hours`, `focus_factor`, `start_date`, `end_date`, `demo_date`, `demo_place`, `daily_info`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 1, 'Sprint One', 1, 1, 10, 10, '2016-10-24 00:00:00', '2016-10-30 00:00:00', '2016-10-28 00:00:00', '', '', 1, 1477295534152, 1477295534152),
	(2, 2, 'Sprint Two', 2, 2, 20, 20, '2016-10-31 00:00:00', '2016-11-13 00:00:00', '2016-11-11 00:00:00', '', '', 1, 1477295565943, 1477295565943);
/*!40000 ALTER TABLE `sprint` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.story
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

-- Dumping data for table ezscrum_180.story: 3 rows
DELETE FROM `story`;
/*!40000 ALTER TABLE `story` DISABLE KEYS */;
INSERT INTO `story` (`id`, `project_id`, `serial_id`, `sprint_id`, `name`, `status`, `estimate`, `importance`, `value`, `notes`, `how_to_demo`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 'Story One', 1, 11, 81, 1, 'Notes for Story One', 'Demo for Story One', 1477295620079, 1477295620079),
	(2, 1, 2, 1, 'Story Two', 1, 22, 82, 2, 'Notes for Story Two', 'Demo for Story Two', 1477295650623, 1477295650623),
	(3, 1, 3, 2, 'Story Three', 1, 33, 83, 3, 'Notes for Story Three', 'Demo for Story Three', 1477295762160, 1477295762160);
/*!40000 ALTER TABLE `story` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.story_tag_relation
CREATE TABLE IF NOT EXISTS `story_tag_relation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tag_id` bigint(20) unsigned NOT NULL,
  `story_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.story_tag_relation: 0 rows
DELETE FROM `story_tag_relation`;
/*!40000 ALTER TABLE `story_tag_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `story_tag_relation` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.system
CREATE TABLE IF NOT EXISTS `system` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_id_UNIQUE` (`account_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.system: 1 rows
DELETE FROM `system`;
/*!40000 ALTER TABLE `system` DISABLE KEYS */;
INSERT INTO `system` (`id`, `account_id`) VALUES
	(1, 1);
/*!40000 ALTER TABLE `system` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.tag
CREATE TABLE IF NOT EXISTS `tag` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `project_id` bigint(20) unsigned NOT NULL,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.tag: 0 rows
DELETE FROM `tag`;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.task
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
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.task: 4 rows
DELETE FROM `task`;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
INSERT INTO `task` (`id`, `serial_id`, `project_id`, `story_id`, `name`, `handler_id`, `status`, `estimate`, `remain`, `actual`, `notes`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 'Task One', 2, 3, 11, 0, 10, 'Notes for Task One', 1477295805856, 1477295975447),
	(2, 2, 1, 2, 'Task Two', 1, 3, 22, 0, 22, 'Notes for Task Two', 1477295823015, 1477295964975),
	(3, 3, 1, 3, 'Task Three', 2, 3, 33, 0, 0, 'Notes for Task Three', 1477295839375, 1477296022839),
	(4, 4, 1, 3, 'Task Four', 1, 3, 44, 0, 0, 'Notes for Task Four', 1477295852039, 1477296014287);
/*!40000 ALTER TABLE `task` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.token
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

-- Dumping data for table ezscrum_180.token: 0 rows
DELETE FROM `token`;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
/*!40000 ALTER TABLE `token` ENABLE KEYS */;


-- Dumping structure for table ezscrum_180.unplan
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

-- Dumping data for table ezscrum_180.unplan: 0 rows
DELETE FROM `unplan`;
/*!40000 ALTER TABLE `unplan` DISABLE KEYS */;
/*!40000 ALTER TABLE `unplan` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
