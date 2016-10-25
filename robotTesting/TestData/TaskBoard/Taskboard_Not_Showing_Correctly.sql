-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.7.8-rc-log - MySQL Community Server (GPL)
-- Server OS:                    Win32
-- HeidiSQL Version:             9.3.0.5124
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
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
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.account: 1 rows
DELETE FROM `account`;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` (`id`, `username`, `nick_name`, `email`, `password`, `enable`, `create_time`, `update_time`) VALUES
	(1, 'admin', 'admin', 'example@ezScrum.tw', '21232f297a57a5a743894a0e4a801fc3', 1, 1379910191599, 1379910191599);
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

-- Dumping structure for table ezscrum_180.buildresult
CREATE TABLE IF NOT EXISTS `buildresult` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `PROJECT_ID` int(11) DEFAULT NULL,
  `REVISION` int(11) DEFAULT NULL,
  `LABEL` varchar(255) NOT NULL DEFAULT '',
  `BUILDRESULT` tinyint(1) DEFAULT '0',
  `BUILDMESSAGE` text,
  `HASTESTRESULT` tinyint(1) DEFAULT '0',
  `TESTRESULT` tinyint(1) DEFAULT '0',
  `TESTMESSAGE` text,
  `HASCOVERAGERESULT` tinyint(1) DEFAULT '0',
  `CLASSCOVERAGE` varchar(255) NOT NULL DEFAULT '',
  `METHODCOVERAGE` varchar(255) NOT NULL DEFAULT '',
  `BLOCKCOVERAGE` varchar(255) NOT NULL DEFAULT '',
  `LINECOVERAGE` varchar(255) NOT NULL DEFAULT '',
  `BUILDTIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.buildresult: 0 rows
DELETE FROM `buildresult`;
/*!40000 ALTER TABLE `buildresult` DISABLE KEYS */;
/*!40000 ALTER TABLE `buildresult` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.commit_log
CREATE TABLE IF NOT EXISTS `commit_log` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AUTHOR` varchar(255) DEFAULT NULL,
  `CHANGEDFILES` text,
  `LOG` text,
  `REVISION` int(11) DEFAULT NULL,
  `COMMITTIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `PROJECT_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.commit_log: 0 rows
DELETE FROM `commit_log`;
/*!40000 ALTER TABLE `commit_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `commit_log` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.commit_story_relation
CREATE TABLE IF NOT EXISTS `commit_story_relation` (
  `COMMITID` int(11) DEFAULT NULL,
  `ISSUEID` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.commit_story_relation: 0 rows
DELETE FROM `commit_story_relation`;
/*!40000 ALTER TABLE `commit_story_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `commit_story_relation` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.ezkanban_statusorder
CREATE TABLE IF NOT EXISTS `ezkanban_statusorder` (
  `issueID` int(10) NOT NULL DEFAULT '0',
  `order` tinyint(3) NOT NULL DEFAULT '0',
  KEY `issueID` (`issueID`),
  KEY `order` (`order`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.ezkanban_statusorder: 0 rows
DELETE FROM `ezkanban_statusorder`;
/*!40000 ALTER TABLE `ezkanban_statusorder` DISABLE KEYS */;
/*!40000 ALTER TABLE `ezkanban_statusorder` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.ezscrum_story_relation
CREATE TABLE IF NOT EXISTS `ezscrum_story_relation` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `storyID` int(10) unsigned NOT NULL,
  `projectID` int(10) unsigned NOT NULL,
  `releaseID` int(10) DEFAULT NULL,
  `sprintID` int(10) DEFAULT NULL,
  `estimation` int(8) DEFAULT NULL,
  `importance` int(8) DEFAULT NULL,
  `updateDate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `estimation` (`estimation`,`importance`),
  KEY `updateDate` (`sprintID`,`projectID`,`storyID`,`updateDate`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.ezscrum_story_relation: 0 rows
DELETE FROM `ezscrum_story_relation`;
/*!40000 ALTER TABLE `ezscrum_story_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `ezscrum_story_relation` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.ezscrum_tag_relation
CREATE TABLE IF NOT EXISTS `ezscrum_tag_relation` (
  `tag_id` int(10) NOT NULL,
  `story_id` int(10) NOT NULL,
  KEY `tag_id` (`tag_id`),
  KEY `story_id` (`story_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.ezscrum_tag_relation: 0 rows
DELETE FROM `ezscrum_tag_relation`;
/*!40000 ALTER TABLE `ezscrum_tag_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `ezscrum_tag_relation` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.ezscrum_tag_table
CREATE TABLE IF NOT EXISTS `ezscrum_tag_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` int(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`,`project_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.ezscrum_tag_table: 0 rows
DELETE FROM `ezscrum_tag_table`;
/*!40000 ALTER TABLE `ezscrum_tag_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `ezscrum_tag_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.eztrack_combofield
CREATE TABLE IF NOT EXISTS `eztrack_combofield` (
  `TypeFieldID` int(10) unsigned NOT NULL,
  `ComboName` varchar(40) NOT NULL DEFAULT '',
  KEY `TypeFieldID` (`TypeFieldID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.eztrack_combofield: 0 rows
DELETE FROM `eztrack_combofield`;
/*!40000 ALTER TABLE `eztrack_combofield` DISABLE KEYS */;
/*!40000 ALTER TABLE `eztrack_combofield` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.eztrack_issuerelation
CREATE TABLE IF NOT EXISTS `eztrack_issuerelation` (
  `IssueID_src` int(10) unsigned NOT NULL,
  `IssueID_des` int(10) unsigned NOT NULL,
  `Type` int(2) NOT NULL DEFAULT '1' COMMENT 'Relation的關係'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.eztrack_issuerelation: 0 rows
DELETE FROM `eztrack_issuerelation`;
/*!40000 ALTER TABLE `eztrack_issuerelation` DISABLE KEYS */;
/*!40000 ALTER TABLE `eztrack_issuerelation` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.eztrack_issuetype
CREATE TABLE IF NOT EXISTS `eztrack_issuetype` (
  `IssueTypeID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ProjectID` int(10) unsigned NOT NULL DEFAULT '0',
  `IssueTypeName` varchar(20) NOT NULL DEFAULT '',
  `IsPublic` tinyint(2) NOT NULL DEFAULT '0',
  `IsKanban` tinyint(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`IssueTypeID`),
  KEY `ProjectID` (`ProjectID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.eztrack_issuetype: 0 rows
DELETE FROM `eztrack_issuetype`;
/*!40000 ALTER TABLE `eztrack_issuetype` DISABLE KEYS */;
/*!40000 ALTER TABLE `eztrack_issuetype` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.eztrack_report
CREATE TABLE IF NOT EXISTS `eztrack_report` (
  `ReportID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ProjectID` int(10) unsigned NOT NULL DEFAULT '0',
  `IssueTypeID` int(10) unsigned NOT NULL DEFAULT '0',
  `ReportDescription` longtext NOT NULL,
  `ReporterName` varchar(50) NOT NULL DEFAULT '',
  `ReporterEmail` varchar(80) NOT NULL DEFAULT '',
  PRIMARY KEY (`ReportID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.eztrack_report: 0 rows
DELETE FROM `eztrack_report`;
/*!40000 ALTER TABLE `eztrack_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `eztrack_report` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.eztrack_typefield
CREATE TABLE IF NOT EXISTS `eztrack_typefield` (
  `TypeFieldID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `IssueTypeID` int(10) unsigned NOT NULL DEFAULT '0',
  `TypeFieldName` varchar(20) NOT NULL DEFAULT '',
  `TypeFieldCategory` varchar(80) NOT NULL DEFAULT '',
  PRIMARY KEY (`TypeFieldID`),
  KEY `IssueTypeID` (`IssueTypeID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.eztrack_typefield: 0 rows
DELETE FROM `eztrack_typefield`;
/*!40000 ALTER TABLE `eztrack_typefield` DISABLE KEYS */;
/*!40000 ALTER TABLE `eztrack_typefield` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.eztrack_typefieldvalue
CREATE TABLE IF NOT EXISTS `eztrack_typefieldvalue` (
  `IssueID` int(10) unsigned NOT NULL DEFAULT '0',
  `TypeFieldID` int(10) unsigned NOT NULL DEFAULT '0',
  `FieldValue` text NOT NULL,
  KEY `IssueID` (`IssueID`),
  KEY `TypeFieldID` (`TypeFieldID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.eztrack_typefieldvalue: 0 rows
DELETE FROM `eztrack_typefieldvalue`;
/*!40000 ALTER TABLE `eztrack_typefieldvalue` DISABLE KEYS */;
/*!40000 ALTER TABLE `eztrack_typefieldvalue` ENABLE KEYS */;

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
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.history: 5 rows
DELETE FROM `history`;
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
INSERT INTO `history` (`id`, `issue_id`, `issue_type`, `type`, `old_value`, `new_value`, `create_time`) VALUES
	(1, 1, 3, 1, '', '', 1467857969195),
	(2, 1, 3, 16, '', '1', 1467857969195),
	(3, 1, 1, 1, '', '', 1467858115609),
	(4, 1, 1, 16, '', '1', 1467858115648),
	(5, 1, 3, 17, '', '1', 1467858115656);
/*!40000 ALTER TABLE `history` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.issue_partner_relation
CREATE TABLE IF NOT EXISTS `issue_partner_relation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `issue_id` bigint(20) unsigned NOT NULL,
  `issue_type` int(11) NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.issue_partner_relation: 0 rows
DELETE FROM `issue_partner_relation`;
/*!40000 ALTER TABLE `issue_partner_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `issue_partner_relation` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_bugnote_table
CREATE TABLE IF NOT EXISTS `mantis_bugnote_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `bug_id` int(10) unsigned NOT NULL DEFAULT '0',
  `reporter_id` int(10) unsigned NOT NULL DEFAULT '0',
  `bugnote_text_id` int(10) unsigned NOT NULL DEFAULT '0',
  `view_state` smallint(6) NOT NULL DEFAULT '10',
  `date_submitted` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `last_modified` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `note_type` int(11) DEFAULT '0',
  `note_attr` varchar(250) DEFAULT '',
  `time_tracking` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_bug` (`bug_id`),
  KEY `idx_last_mod` (`last_modified`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_bugnote_table: 0 rows
DELETE FROM `mantis_bugnote_table`;
/*!40000 ALTER TABLE `mantis_bugnote_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bugnote_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_bugnote_text_table
CREATE TABLE IF NOT EXISTS `mantis_bugnote_text_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `note` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_bugnote_text_table: 0 rows
DELETE FROM `mantis_bugnote_text_table`;
/*!40000 ALTER TABLE `mantis_bugnote_text_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bugnote_text_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_bug_file_table
CREATE TABLE IF NOT EXISTS `mantis_bug_file_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `bug_id` int(10) unsigned NOT NULL DEFAULT '0',
  `title` varchar(250) NOT NULL DEFAULT '',
  `description` varchar(250) NOT NULL DEFAULT '',
  `diskfile` varchar(250) NOT NULL DEFAULT '',
  `filename` varchar(250) NOT NULL DEFAULT '',
  `folder` varchar(250) NOT NULL DEFAULT '',
  `filesize` int(11) NOT NULL DEFAULT '0',
  `file_type` varchar(250) NOT NULL DEFAULT '',
  `date_added` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `content` longblob,
  PRIMARY KEY (`id`),
  KEY `idx_bug_file_bug_id` (`bug_id`),
  KEY `idx_diskfile` (`diskfile`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_bug_file_table: 0 rows
DELETE FROM `mantis_bug_file_table`;
/*!40000 ALTER TABLE `mantis_bug_file_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_file_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_bug_history_table
CREATE TABLE IF NOT EXISTS `mantis_bug_history_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `bug_id` int(10) unsigned NOT NULL DEFAULT '0',
  `date_modified` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `field_name` varchar(64) NOT NULL,
  `old_value` varchar(255) NOT NULL,
  `new_value` varchar(255) NOT NULL,
  `type` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_bug_history_bug_id` (`bug_id`),
  KEY `idx_history_user_id` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_bug_history_table: 0 rows
DELETE FROM `mantis_bug_history_table`;
/*!40000 ALTER TABLE `mantis_bug_history_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_history_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_bug_monitor_table
CREATE TABLE IF NOT EXISTS `mantis_bug_monitor_table` (
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `bug_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`bug_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_bug_monitor_table: 0 rows
DELETE FROM `mantis_bug_monitor_table`;
/*!40000 ALTER TABLE `mantis_bug_monitor_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_monitor_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_bug_relationship_table
CREATE TABLE IF NOT EXISTS `mantis_bug_relationship_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `source_bug_id` int(10) unsigned NOT NULL DEFAULT '0',
  `destination_bug_id` int(10) unsigned NOT NULL DEFAULT '0',
  `relationship_type` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_relationship_source` (`source_bug_id`),
  KEY `idx_relationship_destination` (`destination_bug_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_bug_relationship_table: 0 rows
DELETE FROM `mantis_bug_relationship_table`;
/*!40000 ALTER TABLE `mantis_bug_relationship_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_relationship_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_bug_table
CREATE TABLE IF NOT EXISTS `mantis_bug_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` int(10) unsigned NOT NULL DEFAULT '0',
  `reporter_id` int(10) unsigned NOT NULL DEFAULT '0',
  `handler_id` int(10) unsigned NOT NULL DEFAULT '0',
  `duplicate_id` int(10) unsigned NOT NULL DEFAULT '0',
  `priority` smallint(6) NOT NULL DEFAULT '30',
  `severity` smallint(6) NOT NULL DEFAULT '50',
  `reproducibility` smallint(6) NOT NULL DEFAULT '10',
  `status` smallint(6) NOT NULL DEFAULT '10',
  `resolution` smallint(6) NOT NULL DEFAULT '10',
  `projection` smallint(6) NOT NULL DEFAULT '10',
  `category` varchar(64) NOT NULL DEFAULT '',
  `date_submitted` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `last_updated` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `eta` smallint(6) NOT NULL DEFAULT '10',
  `bug_text_id` int(10) unsigned NOT NULL DEFAULT '0',
  `os` varchar(32) NOT NULL DEFAULT '',
  `os_build` varchar(32) NOT NULL DEFAULT '',
  `platform` varchar(32) NOT NULL DEFAULT '',
  `version` varchar(64) NOT NULL DEFAULT '',
  `fixed_in_version` varchar(64) NOT NULL DEFAULT '',
  `build` varchar(32) NOT NULL DEFAULT '',
  `profile_id` int(10) unsigned NOT NULL DEFAULT '0',
  `view_state` smallint(6) NOT NULL DEFAULT '10',
  `summary` varchar(128) NOT NULL DEFAULT '',
  `sponsorship_total` int(11) NOT NULL DEFAULT '0',
  `sticky` tinyint(4) NOT NULL DEFAULT '0',
  `target_version` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_bug_sponsorship_total` (`sponsorship_total`),
  KEY `idx_bug_fixed_in_version` (`fixed_in_version`),
  KEY `idx_bug_status` (`status`),
  KEY `idx_project` (`project_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_bug_table: 0 rows
DELETE FROM `mantis_bug_table`;
/*!40000 ALTER TABLE `mantis_bug_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_bug_tag_table
CREATE TABLE IF NOT EXISTS `mantis_bug_tag_table` (
  `bug_id` int(10) unsigned NOT NULL DEFAULT '0',
  `tag_id` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `date_attached` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  PRIMARY KEY (`bug_id`,`tag_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_bug_tag_table: 0 rows
DELETE FROM `mantis_bug_tag_table`;
/*!40000 ALTER TABLE `mantis_bug_tag_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_tag_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_bug_text_table
CREATE TABLE IF NOT EXISTS `mantis_bug_text_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `description` longtext NOT NULL,
  `steps_to_reproduce` longtext NOT NULL,
  `additional_information` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_bug_text_table: 0 rows
DELETE FROM `mantis_bug_text_table`;
/*!40000 ALTER TABLE `mantis_bug_text_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_text_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_config_table
CREATE TABLE IF NOT EXISTS `mantis_config_table` (
  `config_id` varchar(64) NOT NULL,
  `project_id` int(11) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `access_reqd` int(11) DEFAULT '0',
  `type` int(11) DEFAULT '90',
  `value` longtext NOT NULL,
  PRIMARY KEY (`config_id`,`project_id`,`user_id`),
  KEY `idx_config` (`config_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_config_table: 1 rows
DELETE FROM `mantis_config_table`;
/*!40000 ALTER TABLE `mantis_config_table` DISABLE KEYS */;
INSERT INTO `mantis_config_table` (`config_id`, `project_id`, `user_id`, `access_reqd`, `type`, `value`) VALUES
	('database_version', 0, 0, 90, 1, '63');
/*!40000 ALTER TABLE `mantis_config_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_custom_field_project_table
CREATE TABLE IF NOT EXISTS `mantis_custom_field_project_table` (
  `field_id` int(11) NOT NULL DEFAULT '0',
  `project_id` int(10) unsigned NOT NULL DEFAULT '0',
  `sequence` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`field_id`,`project_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_custom_field_project_table: 0 rows
DELETE FROM `mantis_custom_field_project_table`;
/*!40000 ALTER TABLE `mantis_custom_field_project_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_custom_field_project_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_custom_field_string_table
CREATE TABLE IF NOT EXISTS `mantis_custom_field_string_table` (
  `field_id` int(11) NOT NULL DEFAULT '0',
  `bug_id` int(11) NOT NULL DEFAULT '0',
  `value` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`field_id`,`bug_id`),
  KEY `idx_custom_field_bug` (`bug_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_custom_field_string_table: 0 rows
DELETE FROM `mantis_custom_field_string_table`;
/*!40000 ALTER TABLE `mantis_custom_field_string_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_custom_field_string_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_custom_field_table
CREATE TABLE IF NOT EXISTS `mantis_custom_field_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL DEFAULT '',
  `type` smallint(6) NOT NULL DEFAULT '0',
  `possible_values` varchar(255) NOT NULL DEFAULT '',
  `default_value` varchar(255) NOT NULL DEFAULT '',
  `valid_regexp` varchar(255) NOT NULL DEFAULT '',
  `access_level_r` smallint(6) NOT NULL DEFAULT '0',
  `access_level_rw` smallint(6) NOT NULL DEFAULT '0',
  `length_min` int(11) NOT NULL DEFAULT '0',
  `length_max` int(11) NOT NULL DEFAULT '0',
  `advanced` tinyint(4) NOT NULL DEFAULT '0',
  `require_report` tinyint(4) NOT NULL DEFAULT '0',
  `require_update` tinyint(4) NOT NULL DEFAULT '0',
  `display_report` tinyint(4) NOT NULL DEFAULT '1',
  `display_update` tinyint(4) NOT NULL DEFAULT '1',
  `require_resolved` tinyint(4) NOT NULL DEFAULT '0',
  `display_resolved` tinyint(4) NOT NULL DEFAULT '0',
  `display_closed` tinyint(4) NOT NULL DEFAULT '0',
  `require_closed` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_custom_field_name` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_custom_field_table: 0 rows
DELETE FROM `mantis_custom_field_table`;
/*!40000 ALTER TABLE `mantis_custom_field_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_custom_field_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_email_table
CREATE TABLE IF NOT EXISTS `mantis_email_table` (
  `email_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(64) NOT NULL DEFAULT '',
  `subject` varchar(250) NOT NULL DEFAULT '',
  `submitted` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `metadata` longtext NOT NULL,
  `body` longtext NOT NULL,
  PRIMARY KEY (`email_id`),
  KEY `idx_email_id` (`email_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_email_table: 0 rows
DELETE FROM `mantis_email_table`;
/*!40000 ALTER TABLE `mantis_email_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_email_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_filters_table
CREATE TABLE IF NOT EXISTS `mantis_filters_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0',
  `project_id` int(11) NOT NULL DEFAULT '0',
  `is_public` tinyint(4) DEFAULT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  `filter_string` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_filters_table: 0 rows
DELETE FROM `mantis_filters_table`;
/*!40000 ALTER TABLE `mantis_filters_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_filters_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_news_table
CREATE TABLE IF NOT EXISTS `mantis_news_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` int(10) unsigned NOT NULL DEFAULT '0',
  `poster_id` int(10) unsigned NOT NULL DEFAULT '0',
  `date_posted` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `last_modified` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `view_state` smallint(6) NOT NULL DEFAULT '10',
  `announcement` tinyint(4) NOT NULL DEFAULT '0',
  `headline` varchar(64) NOT NULL DEFAULT '',
  `body` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_news_table: 0 rows
DELETE FROM `mantis_news_table`;
/*!40000 ALTER TABLE `mantis_news_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_news_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_project_category_table
CREATE TABLE IF NOT EXISTS `mantis_project_category_table` (
  `project_id` int(10) unsigned NOT NULL DEFAULT '0',
  `category` varchar(64) NOT NULL DEFAULT '',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`project_id`,`category`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_project_category_table: 0 rows
DELETE FROM `mantis_project_category_table`;
/*!40000 ALTER TABLE `mantis_project_category_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_project_category_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_project_file_table
CREATE TABLE IF NOT EXISTS `mantis_project_file_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` int(10) unsigned NOT NULL DEFAULT '0',
  `title` varchar(250) NOT NULL DEFAULT '',
  `description` varchar(250) NOT NULL DEFAULT '',
  `diskfile` varchar(250) NOT NULL DEFAULT '',
  `filename` varchar(250) NOT NULL DEFAULT '',
  `folder` varchar(250) NOT NULL DEFAULT '',
  `filesize` int(11) NOT NULL DEFAULT '0',
  `file_type` varchar(250) NOT NULL DEFAULT '',
  `date_added` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `content` longblob,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_project_file_table: 0 rows
DELETE FROM `mantis_project_file_table`;
/*!40000 ALTER TABLE `mantis_project_file_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_project_file_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_project_hierarchy_table
CREATE TABLE IF NOT EXISTS `mantis_project_hierarchy_table` (
  `child_id` int(10) unsigned NOT NULL,
  `parent_id` int(10) unsigned NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_project_hierarchy_table: 0 rows
DELETE FROM `mantis_project_hierarchy_table`;
/*!40000 ALTER TABLE `mantis_project_hierarchy_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_project_hierarchy_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_project_table
CREATE TABLE IF NOT EXISTS `mantis_project_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL DEFAULT '',
  `status` smallint(6) NOT NULL DEFAULT '10',
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  `view_state` smallint(6) NOT NULL DEFAULT '10',
  `access_min` smallint(6) NOT NULL DEFAULT '10',
  `file_path` varchar(250) NOT NULL DEFAULT '',
  `description` longtext NOT NULL,
  `baseLine_velocity` int(10) NOT NULL DEFAULT '50',
  `baseLine_cost_per_storyPoint` int(10) NOT NULL DEFAULT '3',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_project_name` (`name`),
  KEY `idx_project_id` (`id`),
  KEY `idx_project_view` (`view_state`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_project_table: 0 rows
DELETE FROM `mantis_project_table`;
/*!40000 ALTER TABLE `mantis_project_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_project_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_project_user_list_table
CREATE TABLE IF NOT EXISTS `mantis_project_user_list_table` (
  `project_id` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `access_level` smallint(6) NOT NULL DEFAULT '10',
  PRIMARY KEY (`project_id`,`user_id`),
  KEY `idx_project_user` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_project_user_list_table: 0 rows
DELETE FROM `mantis_project_user_list_table`;
/*!40000 ALTER TABLE `mantis_project_user_list_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_project_user_list_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_project_version_table
CREATE TABLE IF NOT EXISTS `mantis_project_version_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(10) unsigned NOT NULL DEFAULT '0',
  `version` varchar(64) NOT NULL DEFAULT '',
  `date_order` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `description` longtext NOT NULL,
  `released` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_project_version` (`project_id`,`version`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_project_version_table: 0 rows
DELETE FROM `mantis_project_version_table`;
/*!40000 ALTER TABLE `mantis_project_version_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_project_version_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_sponsorship_table
CREATE TABLE IF NOT EXISTS `mantis_sponsorship_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bug_id` int(11) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `amount` int(11) NOT NULL DEFAULT '0',
  `logo` varchar(128) NOT NULL DEFAULT '',
  `url` varchar(128) NOT NULL DEFAULT '',
  `paid` tinyint(4) NOT NULL DEFAULT '0',
  `date_submitted` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `last_updated` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  PRIMARY KEY (`id`),
  KEY `idx_sponsorship_bug_id` (`bug_id`),
  KEY `idx_sponsorship_user_id` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_sponsorship_table: 0 rows
DELETE FROM `mantis_sponsorship_table`;
/*!40000 ALTER TABLE `mantis_sponsorship_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_sponsorship_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_tag_table
CREATE TABLE IF NOT EXISTS `mantis_tag_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(100) NOT NULL DEFAULT '',
  `description` longtext NOT NULL,
  `date_created` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `date_updated` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  PRIMARY KEY (`id`,`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_tag_table: 0 rows
DELETE FROM `mantis_tag_table`;
/*!40000 ALTER TABLE `mantis_tag_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_tag_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_tokens_table
CREATE TABLE IF NOT EXISTS `mantis_tokens_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `owner` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `timestamp` datetime NOT NULL,
  `expiry` datetime DEFAULT NULL,
  `value` longtext NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_typeowner` (`type`,`owner`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_tokens_table: 1 rows
DELETE FROM `mantis_tokens_table`;
/*!40000 ALTER TABLE `mantis_tokens_table` DISABLE KEYS */;
INSERT INTO `mantis_tokens_table` (`id`, `owner`, `type`, `timestamp`, `expiry`, `value`) VALUES
	(1, 1, 4, '2010-01-19 05:41:31', '2010-01-19 05:46:31', '1');
/*!40000 ALTER TABLE `mantis_tokens_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_user_pref_table
CREATE TABLE IF NOT EXISTS `mantis_user_pref_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `project_id` int(10) unsigned NOT NULL DEFAULT '0',
  `default_profile` int(10) unsigned NOT NULL DEFAULT '0',
  `default_project` int(10) unsigned NOT NULL DEFAULT '0',
  `advanced_report` tinyint(4) NOT NULL DEFAULT '0',
  `advanced_view` tinyint(4) NOT NULL DEFAULT '0',
  `advanced_update` tinyint(4) NOT NULL DEFAULT '0',
  `refresh_delay` int(11) NOT NULL DEFAULT '0',
  `redirect_delay` tinyint(4) NOT NULL DEFAULT '0',
  `bugnote_order` varchar(4) NOT NULL DEFAULT 'ASC',
  `email_on_new` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_assigned` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_feedback` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_resolved` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_closed` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_reopened` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_bugnote` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_status` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_priority` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_priority_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_status_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_bugnote_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_reopened_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_closed_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_resolved_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_feedback_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_assigned_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_new_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_bugnote_limit` smallint(6) NOT NULL DEFAULT '0',
  `language` varchar(32) NOT NULL DEFAULT 'english',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_user_pref_table: 0 rows
DELETE FROM `mantis_user_pref_table`;
/*!40000 ALTER TABLE `mantis_user_pref_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_user_pref_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_user_print_pref_table
CREATE TABLE IF NOT EXISTS `mantis_user_print_pref_table` (
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `print_pref` varchar(64) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_user_print_pref_table: 0 rows
DELETE FROM `mantis_user_print_pref_table`;
/*!40000 ALTER TABLE `mantis_user_print_pref_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_user_print_pref_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_user_profile_table
CREATE TABLE IF NOT EXISTS `mantis_user_profile_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `platform` varchar(32) NOT NULL DEFAULT '',
  `os` varchar(32) NOT NULL DEFAULT '',
  `os_build` varchar(32) NOT NULL DEFAULT '',
  `description` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_user_profile_table: 0 rows
DELETE FROM `mantis_user_profile_table`;
/*!40000 ALTER TABLE `mantis_user_profile_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_user_profile_table` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.mantis_user_table
CREATE TABLE IF NOT EXISTS `mantis_user_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL DEFAULT '',
  `realname` varchar(64) NOT NULL DEFAULT '',
  `email` varchar(64) NOT NULL DEFAULT '',
  `password` varchar(32) NOT NULL DEFAULT '',
  `date_created` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `last_visit` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  `protected` tinyint(4) NOT NULL DEFAULT '0',
  `access_level` smallint(6) NOT NULL DEFAULT '10',
  `login_count` int(11) NOT NULL DEFAULT '0',
  `lost_password_request_count` smallint(6) NOT NULL DEFAULT '0',
  `failed_login_count` smallint(6) NOT NULL DEFAULT '0',
  `cookie_string` varchar(64) NOT NULL DEFAULT '',
  `Baseline_Velocity` int(11) NOT NULL DEFAULT '50',
  `Baseline_Cost_Per_StoryPoint` int(11) NOT NULL DEFAULT '50',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_cookie_string` (`cookie_string`),
  UNIQUE KEY `idx_user_username` (`username`),
  KEY `idx_enable` (`enabled`),
  KEY `idx_access` (`access_level`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.mantis_user_table: 1 rows
DELETE FROM `mantis_user_table`;
/*!40000 ALTER TABLE `mantis_user_table` DISABLE KEYS */;
INSERT INTO `mantis_user_table` (`id`, `username`, `realname`, `email`, `password`, `date_created`, `last_visit`, `enabled`, `protected`, `access_level`, `login_count`, `lost_password_request_count`, `failed_login_count`, `cookie_string`, `Baseline_Velocity`, `Baseline_Cost_Per_StoryPoint`) VALUES
	(1, 'admin', '', 'root@localhost', '63a9f0ea7bb98050796b649e85481845', '2010-01-19 05:41:23', '2010-01-19 05:41:35', 1, 0, 90, 4, 0, 0, 'b9bec1c98360692f7ae7baecd9736deaa511ea87cfda0be2ddac035e208e1069', 50, 50);
/*!40000 ALTER TABLE `mantis_user_table` ENABLE KEYS */;

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
	(1, 'localtestProject', 'localtestProjectDisplayName', '', '', 2, 1467857909185, 1467857909185);
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
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.project_role: 0 rows
DELETE FROM `project_role`;
/*!40000 ALTER TABLE `project_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_role` ENABLE KEYS */;

-- Dumping structure for table ezscrum_180.query
CREATE TABLE IF NOT EXISTS `query` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `PROJECT_ID` int(11) DEFAULT NULL,
  `USER_ID` int(10) unsigned DEFAULT NULL,
  `NAME` varchar(255) NOT NULL DEFAULT '',
  `PROFILE` text,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.query: 0 rows
DELETE FROM `query`;
/*!40000 ALTER TABLE `query` DISABLE KEYS */;
/*!40000 ALTER TABLE `query` ENABLE KEYS */;

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
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.release: 0 rows
DELETE FROM `release`;
/*!40000 ALTER TABLE `release` DISABLE KEYS */;
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
	(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1477287400091, 1477287400091),
	(2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1477287400094, 1477287400094),
	(3, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 2, 1477287400095, 1477287400095),
	(4, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 3, 1477287400096, 1477287400096),
	(5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 4, 1477287400098, 1477287400098);
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
	(1, 1, 0, 1, 3, 3, 0, 0);
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
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.sprint: 1 rows
DELETE FROM `sprint`;
/*!40000 ALTER TABLE `sprint` DISABLE KEYS */;
INSERT INTO `sprint` (`id`, `serial_id`, `goal`, `interval`, `team_size`, `available_hours`, `focus_factor`, `start_date`, `end_date`, `demo_date`, `demo_place`, `daily_info`, `project_id`, `create_time`, `update_time`) VALUES
	(1, 1, 'Fix Bug', 1, 3, 5, 90, '2016-10-23 00:00:00', '2016-10-29 00:00:00', '2016-10-28 00:00:00', '', '', 1, 1477287431424, 1477287431424);
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
	(1, 1, 1, 1, 'Story ', 1, 1, 3, 1, 'Something', '', 1477290059110, 1477290145174),
	(2, 1, 2, 1, 'Story2', 1, 1, 1, 1, '1', '', 1477290074228, 1477290145159),
	(3, 1, 3, 1, 'Story3', 1, 1, 1, 1, '1', '', 1477290091739, 1477290145167);
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
  `actual` int(11) NOT NULL DEFAULT '0',
  `notes` text,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.task: 3 rows
DELETE FROM `task`;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
INSERT INTO `task` (`id`, `serial_id`, `project_id`, `story_id`, `name`, `handler_id`, `status`, `estimate`, `remain`, `actual`, `notes`, `create_time`, `update_time`) VALUES
	(1, 1, 1, 1, 'Do something story1 task1', -1, 1, 3, 3, 0, '3', 1477290187848, 1477290187848),
	(2, 2, 1, 2, 'something story2task2', -1, 1, 0, 0, 0, '', 1477290206931, 1477290206931),
	(3, 3, 1, 3, 'something story3 task3', -1, 1, 0, 0, 0, '', 1477290263524, 1477290263524);
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
  `actual` int(11) NOT NULL,
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

-- Dumping structure for table ezscrum_180.unplanned
CREATE TABLE IF NOT EXISTS `unplanned` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `serial_id` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `handler_id` bigint(20) NOT NULL,
  `estimate` int(11) NOT NULL,
  `actual` int(11) NOT NULL,
  `notes` text NOT NULL,
  `status` varchar(255) NOT NULL,
  `project_id` bigint(20) unsigned NOT NULL,
  `sprint_id` bigint(20) unsigned NOT NULL,
  `create_time` bigint(20) unsigned NOT NULL,
  `update_time` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table ezscrum_180.unplanned: ~0 rows (approximately)
DELETE FROM `unplanned`;
/*!40000 ALTER TABLE `unplanned` DISABLE KEYS */;
/*!40000 ALTER TABLE `unplanned` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
