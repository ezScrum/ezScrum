-- MySQL dump 10.13  Distrib 5.1.41, for debian-linux-gnu (i486)
--
-- Host: localhost    Database: ezScrum
-- ------------------------------------------------------
-- Server version	5.1.41-3ubuntu12.10

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `buildresult`
--

DROP TABLE IF EXISTS `buildresult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `buildresult` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `buildresult`
--

LOCK TABLES `buildresult` WRITE;
/*!40000 ALTER TABLE `buildresult` DISABLE KEYS */;
/*!40000 ALTER TABLE `buildresult` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commit_log`
--

DROP TABLE IF EXISTS `commit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commit_log` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AUTHOR` varchar(255) DEFAULT NULL,
  `CHANGEDFILES` text,
  `LOG` text,
  `REVISION` int(11) DEFAULT NULL,
  `COMMITTIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `PROJECT_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commit_log`
--

LOCK TABLES `commit_log` WRITE;
/*!40000 ALTER TABLE `commit_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `commit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commit_story_relation`
--

DROP TABLE IF EXISTS `commit_story_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commit_story_relation` (
  `COMMITID` int(11) DEFAULT NULL,
  `ISSUEID` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commit_story_relation`
--

LOCK TABLES `commit_story_relation` WRITE;
/*!40000 ALTER TABLE `commit_story_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `commit_story_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ezkanban_statusorder`
--

DROP TABLE IF EXISTS `ezkanban_statusorder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ezkanban_statusorder` (
  `issueID` int(10) NOT NULL DEFAULT '0',
  `order` tinyint(3) NOT NULL DEFAULT '0',
  KEY `issueID` (`issueID`),
  KEY `order` (`order`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ezkanban_statusorder`
--

LOCK TABLES `ezkanban_statusorder` WRITE;
/*!40000 ALTER TABLE `ezkanban_statusorder` DISABLE KEYS */;
/*!40000 ALTER TABLE `ezkanban_statusorder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ezscrum_story_relation`
--

DROP TABLE IF EXISTS `ezscrum_story_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ezscrum_story_relation` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ezscrum_story_relation`
--

LOCK TABLES `ezscrum_story_relation` WRITE;
/*!40000 ALTER TABLE `ezscrum_story_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `ezscrum_story_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ezscrum_tag_relation`
--

DROP TABLE IF EXISTS `ezscrum_tag_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ezscrum_tag_relation` (
  `tag_id` int(10) NOT NULL,
  `story_id` int(10) NOT NULL,
  KEY `tag_id` (`tag_id`),
  KEY `story_id` (`story_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ezscrum_tag_relation`
--

LOCK TABLES `ezscrum_tag_relation` WRITE;
/*!40000 ALTER TABLE `ezscrum_tag_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `ezscrum_tag_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ezscrum_tag_table`
--

DROP TABLE IF EXISTS `ezscrum_tag_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ezscrum_tag_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` int(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`,`project_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ezscrum_tag_table`
--

LOCK TABLES `ezscrum_tag_table` WRITE;
/*!40000 ALTER TABLE `ezscrum_tag_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `ezscrum_tag_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eztrack_combofield`
--

DROP TABLE IF EXISTS `eztrack_combofield`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eztrack_combofield` (
  `TypeFieldID` int(10) unsigned NOT NULL,
  `ComboName` varchar(40) NOT NULL DEFAULT '',
  KEY `TypeFieldID` (`TypeFieldID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eztrack_combofield`
--

LOCK TABLES `eztrack_combofield` WRITE;
/*!40000 ALTER TABLE `eztrack_combofield` DISABLE KEYS */;
/*!40000 ALTER TABLE `eztrack_combofield` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eztrack_issuerelation`
--

DROP TABLE IF EXISTS `eztrack_issuerelation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eztrack_issuerelation` (
  `IssueID_src` int(10) unsigned NOT NULL,
  `IssueID_des` int(10) unsigned NOT NULL,
  `Type` int(2) NOT NULL DEFAULT '1' COMMENT 'Relation的關係'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eztrack_issuerelation`
--

LOCK TABLES `eztrack_issuerelation` WRITE;
/*!40000 ALTER TABLE `eztrack_issuerelation` DISABLE KEYS */;
/*!40000 ALTER TABLE `eztrack_issuerelation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eztrack_issuetype`
--

DROP TABLE IF EXISTS `eztrack_issuetype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eztrack_issuetype` (
  `IssueTypeID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ProjectID` int(10) unsigned NOT NULL DEFAULT '0',
  `IssueTypeName` varchar(20) NOT NULL DEFAULT '',
  `IsPublic` tinyint(2) NOT NULL DEFAULT '0',
  `IsKanban` tinyint(2) NOT NULL DEFAULT '0',
  PRIMARY KEY (`IssueTypeID`),
  KEY `ProjectID` (`ProjectID`)
) ENGINE=MyISAM AUTO_INCREMENT=70 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eztrack_issuetype`
--

LOCK TABLES `eztrack_issuetype` WRITE;
/*!40000 ALTER TABLE `eztrack_issuetype` DISABLE KEYS */;
/*!40000 ALTER TABLE `eztrack_issuetype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eztrack_report`
--

DROP TABLE IF EXISTS `eztrack_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eztrack_report` (
  `ReportID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ProjectID` int(10) unsigned NOT NULL DEFAULT '0',
  `IssueTypeID` int(10) unsigned NOT NULL DEFAULT '0',
  `ReportDescription` longtext NOT NULL,
  `ReporterName` varchar(50) NOT NULL DEFAULT '',
  `ReporterEmail` varchar(80) NOT NULL DEFAULT '',
  PRIMARY KEY (`ReportID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eztrack_report`
--

LOCK TABLES `eztrack_report` WRITE;
/*!40000 ALTER TABLE `eztrack_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `eztrack_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eztrack_typefield`
--

DROP TABLE IF EXISTS `eztrack_typefield`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eztrack_typefield` (
  `TypeFieldID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `IssueTypeID` int(10) unsigned NOT NULL DEFAULT '0',
  `TypeFieldName` varchar(20) NOT NULL DEFAULT '',
  `TypeFieldCategory` varchar(80) NOT NULL DEFAULT '',
  PRIMARY KEY (`TypeFieldID`),
  KEY `IssueTypeID` (`IssueTypeID`)
) ENGINE=MyISAM AUTO_INCREMENT=193 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eztrack_typefield`
--

LOCK TABLES `eztrack_typefield` WRITE;
/*!40000 ALTER TABLE `eztrack_typefield` DISABLE KEYS */;
/*!40000 ALTER TABLE `eztrack_typefield` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eztrack_typefieldvalue`
--

DROP TABLE IF EXISTS `eztrack_typefieldvalue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `eztrack_typefieldvalue` (
  `IssueID` int(10) unsigned NOT NULL DEFAULT '0',
  `TypeFieldID` int(10) unsigned NOT NULL DEFAULT '0',
  `FieldValue` text NOT NULL,
  KEY `IssueID` (`IssueID`),
  KEY `TypeFieldID` (`TypeFieldID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eztrack_typefieldvalue`
--

LOCK TABLES `eztrack_typefieldvalue` WRITE;
/*!40000 ALTER TABLE `eztrack_typefieldvalue` DISABLE KEYS */;
/*!40000 ALTER TABLE `eztrack_typefieldvalue` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_bug_file_table`
--

DROP TABLE IF EXISTS `mantis_bug_file_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_bug_file_table` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_bug_file_table`
--

LOCK TABLES `mantis_bug_file_table` WRITE;
/*!40000 ALTER TABLE `mantis_bug_file_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_file_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_bug_history_table`
--

DROP TABLE IF EXISTS `mantis_bug_history_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_bug_history_table` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_bug_history_table`
--

LOCK TABLES `mantis_bug_history_table` WRITE;
/*!40000 ALTER TABLE `mantis_bug_history_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_history_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_bug_monitor_table`
--

DROP TABLE IF EXISTS `mantis_bug_monitor_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_bug_monitor_table` (
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `bug_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`bug_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_bug_monitor_table`
--

LOCK TABLES `mantis_bug_monitor_table` WRITE;
/*!40000 ALTER TABLE `mantis_bug_monitor_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_monitor_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_bug_relationship_table`
--

DROP TABLE IF EXISTS `mantis_bug_relationship_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_bug_relationship_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `source_bug_id` int(10) unsigned NOT NULL DEFAULT '0',
  `destination_bug_id` int(10) unsigned NOT NULL DEFAULT '0',
  `relationship_type` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_relationship_source` (`source_bug_id`),
  KEY `idx_relationship_destination` (`destination_bug_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_bug_relationship_table`
--

LOCK TABLES `mantis_bug_relationship_table` WRITE;
/*!40000 ALTER TABLE `mantis_bug_relationship_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_relationship_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_bug_table`
--

DROP TABLE IF EXISTS `mantis_bug_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_bug_table` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_bug_table`
--

LOCK TABLES `mantis_bug_table` WRITE;
/*!40000 ALTER TABLE `mantis_bug_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_bug_tag_table`
--

DROP TABLE IF EXISTS `mantis_bug_tag_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_bug_tag_table` (
  `bug_id` int(10) unsigned NOT NULL DEFAULT '0',
  `tag_id` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `date_attached` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  PRIMARY KEY (`bug_id`,`tag_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_bug_tag_table`
--

LOCK TABLES `mantis_bug_tag_table` WRITE;
/*!40000 ALTER TABLE `mantis_bug_tag_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_tag_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_bug_text_table`
--

DROP TABLE IF EXISTS `mantis_bug_text_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_bug_text_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `description` longtext NOT NULL,
  `steps_to_reproduce` longtext NOT NULL,
  `additional_information` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_bug_text_table`
--

LOCK TABLES `mantis_bug_text_table` WRITE;
/*!40000 ALTER TABLE `mantis_bug_text_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bug_text_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_bugnote_table`
--

DROP TABLE IF EXISTS `mantis_bugnote_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_bugnote_table` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_bugnote_table`
--

LOCK TABLES `mantis_bugnote_table` WRITE;
/*!40000 ALTER TABLE `mantis_bugnote_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bugnote_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_bugnote_text_table`
--

DROP TABLE IF EXISTS `mantis_bugnote_text_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_bugnote_text_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `note` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_bugnote_text_table`
--

LOCK TABLES `mantis_bugnote_text_table` WRITE;
/*!40000 ALTER TABLE `mantis_bugnote_text_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_bugnote_text_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_config_table`
--

DROP TABLE IF EXISTS `mantis_config_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_config_table` (
  `config_id` varchar(64) NOT NULL,
  `project_id` int(11) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `access_reqd` int(11) DEFAULT '0',
  `type` int(11) DEFAULT '90',
  `value` longtext NOT NULL,
  PRIMARY KEY (`config_id`,`project_id`,`user_id`),
  KEY `idx_config` (`config_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_config_table`
--

LOCK TABLES `mantis_config_table` WRITE;
/*!40000 ALTER TABLE `mantis_config_table` DISABLE KEYS */;
INSERT INTO `mantis_config_table` VALUES ('database_version',0,0,90,1,'63');
/*!40000 ALTER TABLE `mantis_config_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_custom_field_project_table`
--

DROP TABLE IF EXISTS `mantis_custom_field_project_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_custom_field_project_table` (
  `field_id` int(11) NOT NULL DEFAULT '0',
  `project_id` int(10) unsigned NOT NULL DEFAULT '0',
  `sequence` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`field_id`,`project_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_custom_field_project_table`
--

LOCK TABLES `mantis_custom_field_project_table` WRITE;
/*!40000 ALTER TABLE `mantis_custom_field_project_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_custom_field_project_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_custom_field_string_table`
--

DROP TABLE IF EXISTS `mantis_custom_field_string_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_custom_field_string_table` (
  `field_id` int(11) NOT NULL DEFAULT '0',
  `bug_id` int(11) NOT NULL DEFAULT '0',
  `value` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`field_id`,`bug_id`),
  KEY `idx_custom_field_bug` (`bug_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_custom_field_string_table`
--

LOCK TABLES `mantis_custom_field_string_table` WRITE;
/*!40000 ALTER TABLE `mantis_custom_field_string_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_custom_field_string_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_custom_field_table`
--

DROP TABLE IF EXISTS `mantis_custom_field_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_custom_field_table` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_custom_field_table`
--

LOCK TABLES `mantis_custom_field_table` WRITE;
/*!40000 ALTER TABLE `mantis_custom_field_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_custom_field_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_email_table`
--

DROP TABLE IF EXISTS `mantis_email_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_email_table` (
  `email_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(64) NOT NULL DEFAULT '',
  `subject` varchar(250) NOT NULL DEFAULT '',
  `submitted` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `metadata` longtext NOT NULL,
  `body` longtext NOT NULL,
  PRIMARY KEY (`email_id`),
  KEY `idx_email_id` (`email_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_email_table`
--

LOCK TABLES `mantis_email_table` WRITE;
/*!40000 ALTER TABLE `mantis_email_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_email_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_filters_table`
--

DROP TABLE IF EXISTS `mantis_filters_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_filters_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0',
  `project_id` int(11) NOT NULL DEFAULT '0',
  `is_public` tinyint(4) DEFAULT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  `filter_string` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_filters_table`
--

LOCK TABLES `mantis_filters_table` WRITE;
/*!40000 ALTER TABLE `mantis_filters_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_filters_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_news_table`
--

DROP TABLE IF EXISTS `mantis_news_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_news_table` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_news_table`
--

LOCK TABLES `mantis_news_table` WRITE;
/*!40000 ALTER TABLE `mantis_news_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_news_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_project_category_table`
--

DROP TABLE IF EXISTS `mantis_project_category_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_project_category_table` (
  `project_id` int(10) unsigned NOT NULL DEFAULT '0',
  `category` varchar(64) NOT NULL DEFAULT '',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`project_id`,`category`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_project_category_table`
--

LOCK TABLES `mantis_project_category_table` WRITE;
/*!40000 ALTER TABLE `mantis_project_category_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_project_category_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_project_file_table`
--

DROP TABLE IF EXISTS `mantis_project_file_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_project_file_table` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_project_file_table`
--

LOCK TABLES `mantis_project_file_table` WRITE;
/*!40000 ALTER TABLE `mantis_project_file_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_project_file_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_project_hierarchy_table`
--

DROP TABLE IF EXISTS `mantis_project_hierarchy_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_project_hierarchy_table` (
  `child_id` int(10) unsigned NOT NULL,
  `parent_id` int(10) unsigned NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_project_hierarchy_table`
--

LOCK TABLES `mantis_project_hierarchy_table` WRITE;
/*!40000 ALTER TABLE `mantis_project_hierarchy_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_project_hierarchy_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_project_table`
--

DROP TABLE IF EXISTS `mantis_project_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_project_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL DEFAULT '',
  `status` smallint(6) NOT NULL DEFAULT '10',
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  `view_state` smallint(6) NOT NULL DEFAULT '10',
  `access_min` smallint(6) NOT NULL DEFAULT '10',
  `file_path` varchar(250) NOT NULL DEFAULT '',
  `description` longtext NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_project_name` (`name`),
  KEY `idx_project_id` (`id`),
  KEY `idx_project_view` (`view_state`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_project_table`
--

LOCK TABLES `mantis_project_table` WRITE;
/*!40000 ALTER TABLE `mantis_project_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_project_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_project_user_list_table`
--

DROP TABLE IF EXISTS `mantis_project_user_list_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_project_user_list_table` (
  `project_id` int(10) unsigned NOT NULL DEFAULT '0',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `access_level` smallint(6) NOT NULL DEFAULT '10',
  PRIMARY KEY (`project_id`,`user_id`),
  KEY `idx_project_user` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_project_user_list_table`
--

LOCK TABLES `mantis_project_user_list_table` WRITE;
/*!40000 ALTER TABLE `mantis_project_user_list_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_project_user_list_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_project_version_table`
--

DROP TABLE IF EXISTS `mantis_project_version_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_project_version_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(10) unsigned NOT NULL DEFAULT '0',
  `version` varchar(64) NOT NULL DEFAULT '',
  `date_order` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `description` longtext NOT NULL,
  `released` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_project_version` (`project_id`,`version`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_project_version_table`
--

LOCK TABLES `mantis_project_version_table` WRITE;
/*!40000 ALTER TABLE `mantis_project_version_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_project_version_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_sponsorship_table`
--

DROP TABLE IF EXISTS `mantis_sponsorship_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_sponsorship_table` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_sponsorship_table`
--

LOCK TABLES `mantis_sponsorship_table` WRITE;
/*!40000 ALTER TABLE `mantis_sponsorship_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_sponsorship_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_tag_table`
--

DROP TABLE IF EXISTS `mantis_tag_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_tag_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(100) NOT NULL DEFAULT '',
  `description` longtext NOT NULL,
  `date_created` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  `date_updated` datetime NOT NULL DEFAULT '1970-01-01 00:00:01',
  PRIMARY KEY (`id`,`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_tag_table`
--

LOCK TABLES `mantis_tag_table` WRITE;
/*!40000 ALTER TABLE `mantis_tag_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_tag_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_tokens_table`
--

DROP TABLE IF EXISTS `mantis_tokens_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_tokens_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `owner` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `timestamp` datetime NOT NULL,
  `expiry` datetime DEFAULT NULL,
  `value` longtext NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_typeowner` (`type`,`owner`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_tokens_table`
--

LOCK TABLES `mantis_tokens_table` WRITE;
/*!40000 ALTER TABLE `mantis_tokens_table` DISABLE KEYS */;
INSERT INTO `mantis_tokens_table` VALUES (1,1,4,'2010-01-19 05:41:31','2010-01-19 05:46:31','1');
/*!40000 ALTER TABLE `mantis_tokens_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_user_pref_table`
--

DROP TABLE IF EXISTS `mantis_user_pref_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_user_pref_table` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_user_pref_table`
--

LOCK TABLES `mantis_user_pref_table` WRITE;
/*!40000 ALTER TABLE `mantis_user_pref_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_user_pref_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_user_print_pref_table`
--

DROP TABLE IF EXISTS `mantis_user_print_pref_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_user_print_pref_table` (
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `print_pref` varchar(64) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_user_print_pref_table`
--

LOCK TABLES `mantis_user_print_pref_table` WRITE;
/*!40000 ALTER TABLE `mantis_user_print_pref_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_user_print_pref_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_user_profile_table`
--

DROP TABLE IF EXISTS `mantis_user_profile_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_user_profile_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `platform` varchar(32) NOT NULL DEFAULT '',
  `os` varchar(32) NOT NULL DEFAULT '',
  `os_build` varchar(32) NOT NULL DEFAULT '',
  `description` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_user_profile_table`
--

LOCK TABLES `mantis_user_profile_table` WRITE;
/*!40000 ALTER TABLE `mantis_user_profile_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantis_user_profile_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantis_user_table`
--

DROP TABLE IF EXISTS `mantis_user_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mantis_user_table` (
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_cookie_string` (`cookie_string`),
  UNIQUE KEY `idx_user_username` (`username`),
  KEY `idx_enable` (`enabled`),
  KEY `idx_access` (`access_level`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantis_user_table`
--

LOCK TABLES `mantis_user_table` WRITE;
/*!40000 ALTER TABLE `mantis_user_table` DISABLE KEYS */;
INSERT INTO `mantis_user_table` VALUES (1,'administrator','','root@localhost','63a9f0ea7bb98050796b649e85481845','2010-01-19 05:41:23','2010-01-19 05:41:35',1,0,90,4,0,0,'b9bec1c98360692f7ae7baecd9736deaa511ea87cfda0be2ddac035e208e1069');
/*!40000 ALTER TABLE `mantis_user_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `query`
--

DROP TABLE IF EXISTS `query`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `query` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `PROJECT_ID` int(11) DEFAULT NULL,
  `USER_ID` int(10) unsigned DEFAULT NULL,
  `NAME` varchar(255) NOT NULL DEFAULT '',
  `PROFILE` text,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `query`
--

LOCK TABLES `query` WRITE;
/*!40000 ALTER TABLE `query` DISABLE KEYS */;
/*!40000 ALTER TABLE `query` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review_log_relation`
--

DROP TABLE IF EXISTS `review_log_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `review_log_relation` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ISSUE_ID` int(11) NOT NULL COMMENT '對應至ezScrum的IssueID',
  `SOURCE_COMMIT_LOG_ID` int(11) NOT NULL COMMENT '程式碼在Commit_Log表的ID',
  `REVIEWFILE_COMMIT_LOG_ID` int(11) NOT NULL COMMENT '檢閱檔案在Commit_Log表的ID',
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review_log_relation`
--

LOCK TABLES `review_log_relation` WRITE;
/*!40000 ALTER TABLE `review_log_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `review_log_relation` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-07-20 15:11:57
