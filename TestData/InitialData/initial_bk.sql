DROP TABLE IF EXISTS `buildresult`;
CREATE TABLE `buildresult` (
  `ID` int(11) NOT NULL auto_increment,
  `PROJECT_ID` int(11) default NULL,
  `REVISION` int(11) default NULL,
  `LABEL` varchar(255) NOT NULL default '',
  `BUILDRESULT` tinyint(1) default '0',
  `BUILDMESSAGE` text,
  `HASTESTRESULT` tinyint(1) default '0',
  `TESTRESULT` tinyint(1) default '0',
  `TESTMESSAGE` text,
  `HASCOVERAGERESULT` tinyint(1) default '0',
  `CLASSCOVERAGE` varchar(255) NOT NULL default '',
  `METHODCOVERAGE` varchar(255) NOT NULL default '',
  `BLOCKCOVERAGE` varchar(255) NOT NULL default '',
  `LINECOVERAGE` varchar(255) NOT NULL default '',
  `BUILDTIME` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `commit_log` (
  `ID` int(11) NOT NULL auto_increment,
  `AUTHOR` varchar(255) default NULL,
  `CHANGEDFILES` text,
  `LOG` text,
  `REVISION` int(11) default NULL,
  `COMMITTIME` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `PROJECT_ID` int(11) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `commit_story_relation` (
  `COMMITID` int(11) default NULL,
  `ISSUEID` int(11) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `ezkanban_statusorder` (
  `issueID` int(10) NOT NULL default '0',
  `order` tinyint(3) NOT NULL default '0',
  KEY `issueID` (`issueID`),
  KEY `order` (`order`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `ezscrum_story_relation` (
  `id` int(8) NOT NULL auto_increment,
  `storyID` int(10) unsigned NOT NULL,
  `projectID` int(10) unsigned NOT NULL,
  `releaseID` int(10) default NULL,
  `sprintID` int(10) default NULL,
  `estimation` int(8) default NULL,
  `importance` int(8) default NULL,
  `updateDate` timestamp NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`),
  KEY `estimation` (`estimation`,`importance`),
  KEY `updateDate` (`sprintID`,`projectID`,`storyID`,`updateDate`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `ezscrum_tag_relation` (
  `tag_id` int(10) NOT NULL,
  `story_id` int(10) NOT NULL,
  KEY `tag_id` (`tag_id`),
  KEY `story_id` (`story_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `ezscrum_tag_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `project_id` int(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY  (`id`,`project_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `eztrack_combofield` (
  `TypeFieldID` int(10) unsigned NOT NULL,
  `ComboName` varchar(40) NOT NULL default '',
  KEY `TypeFieldID` (`TypeFieldID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `eztrack_issuerelation` (
  `IssueID_src` int(10) unsigned NOT NULL,
  `IssueID_des` int(10) unsigned NOT NULL,
  `Type` int(2) NOT NULL default '1' COMMENT 'Relation的關係'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `eztrack_issuetype` (
  `IssueTypeID` int(10) unsigned NOT NULL auto_increment,
  `ProjectID` int(10) unsigned NOT NULL default '0',
  `IssueTypeName` varchar(20) NOT NULL default '',
  `IsPublic` tinyint(2) NOT NULL default '0',
  `IsKanban` tinyint(2) NOT NULL default '0',
  PRIMARY KEY  (`IssueTypeID`),
  KEY `ProjectID` (`ProjectID`)
) ENGINE=MyISAM AUTO_INCREMENT=70 DEFAULT CHARSET=utf8;

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
CREATE TABLE `eztrack_report` (
  `ReportID` int(10) unsigned NOT NULL auto_increment,
  `ProjectID` int(10) unsigned NOT NULL default '0',
  `IssueTypeID` int(10) unsigned NOT NULL default '0',
  `ReportDescription` longtext NOT NULL,
  `ReporterName` varchar(50) NOT NULL default '',
  `ReporterEmail` varchar(80) NOT NULL default '',
  PRIMARY KEY  (`ReportID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `eztrack_typefield` (
  `TypeFieldID` int(10) unsigned NOT NULL auto_increment,
  `IssueTypeID` int(10) unsigned NOT NULL default '0',
  `TypeFieldName` varchar(20) NOT NULL default '',
  `TypeFieldCategory` varchar(80) NOT NULL default '',
  PRIMARY KEY  (`TypeFieldID`),
  KEY `IssueTypeID` (`IssueTypeID`)
) ENGINE=MyISAM AUTO_INCREMENT=193 DEFAULT CHARSET=utf8;

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
CREATE TABLE `eztrack_typefieldvalue` (
  `IssueID` int(10) unsigned NOT NULL default '0',
  `TypeFieldID` int(10) unsigned NOT NULL default '0',
  `FieldValue` text NOT NULL,
  KEY `IssueID` (`IssueID`),
  KEY `TypeFieldID` (`TypeFieldID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_bug_file_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `bug_id` int(10) unsigned NOT NULL default '0',
  `title` varchar(250) NOT NULL default '',
  `description` varchar(250) NOT NULL default '',
  `diskfile` varchar(250) NOT NULL default '',
  `filename` varchar(250) NOT NULL default '',
  `folder` varchar(250) NOT NULL default '',
  `filesize` int(11) NOT NULL default '0',
  `file_type` varchar(250) NOT NULL default '',
  `date_added` datetime NOT NULL default '1970-01-01 00:00:01',
  `content` longblob,
  PRIMARY KEY  (`id`),
  KEY `idx_bug_file_bug_id` (`bug_id`),
  KEY `idx_diskfile` (`diskfile`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_bug_history_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `user_id` int(10) unsigned NOT NULL default '0',
  `bug_id` int(10) unsigned NOT NULL default '0',
  `date_modified` datetime NOT NULL default '1970-01-01 00:00:01',
  `field_name` varchar(64) NOT NULL,
  `old_value` varchar(255) NOT NULL,
  `new_value` varchar(255) NOT NULL,
  `type` smallint(6) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `idx_bug_history_bug_id` (`bug_id`),
  KEY `idx_history_user_id` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_bug_monitor_table` (
  `user_id` int(10) unsigned NOT NULL default '0',
  `bug_id` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`user_id`,`bug_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_bug_relationship_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `source_bug_id` int(10) unsigned NOT NULL default '0',
  `destination_bug_id` int(10) unsigned NOT NULL default '0',
  `relationship_type` smallint(6) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `idx_relationship_source` (`source_bug_id`),
  KEY `idx_relationship_destination` (`destination_bug_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_bug_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `project_id` int(10) unsigned NOT NULL default '0',
  `reporter_id` int(10) unsigned NOT NULL default '0',
  `handler_id` int(10) unsigned NOT NULL default '0',
  `duplicate_id` int(10) unsigned NOT NULL default '0',
  `priority` smallint(6) NOT NULL default '30',
  `severity` smallint(6) NOT NULL default '50',
  `reproducibility` smallint(6) NOT NULL default '10',
  `status` smallint(6) NOT NULL default '10',
  `resolution` smallint(6) NOT NULL default '10',
  `projection` smallint(6) NOT NULL default '10',
  `category` varchar(64) NOT NULL default '',
  `date_submitted` datetime NOT NULL default '1970-01-01 00:00:01',
  `last_updated` datetime NOT NULL default '1970-01-01 00:00:01',
  `eta` smallint(6) NOT NULL default '10',
  `bug_text_id` int(10) unsigned NOT NULL default '0',
  `os` varchar(32) NOT NULL default '',
  `os_build` varchar(32) NOT NULL default '',
  `platform` varchar(32) NOT NULL default '',
  `version` varchar(64) NOT NULL default '',
  `fixed_in_version` varchar(64) NOT NULL default '',
  `build` varchar(32) NOT NULL default '',
  `profile_id` int(10) unsigned NOT NULL default '0',
  `view_state` smallint(6) NOT NULL default '10',
  `summary` varchar(128) NOT NULL default '',
  `sponsorship_total` int(11) NOT NULL default '0',
  `sticky` tinyint(4) NOT NULL default '0',
  `target_version` varchar(64) NOT NULL default '',
  PRIMARY KEY  (`id`),
  KEY `idx_bug_sponsorship_total` (`sponsorship_total`),
  KEY `idx_bug_fixed_in_version` (`fixed_in_version`),
  KEY `idx_bug_status` (`status`),
  KEY `idx_project` (`project_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_bug_tag_table` (
  `bug_id` int(10) unsigned NOT NULL default '0',
  `tag_id` int(10) unsigned NOT NULL default '0',
  `user_id` int(10) unsigned NOT NULL default '0',
  `date_attached` datetime NOT NULL default '1970-01-01 00:00:01',
  PRIMARY KEY  (`bug_id`,`tag_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_bug_text_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `description` longtext NOT NULL,
  `steps_to_reproduce` longtext NOT NULL,
  `additional_information` longtext NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_bugnote_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `bug_id` int(10) unsigned NOT NULL default '0',
  `reporter_id` int(10) unsigned NOT NULL default '0',
  `bugnote_text_id` int(10) unsigned NOT NULL default '0',
  `view_state` smallint(6) NOT NULL default '10',
  `date_submitted` datetime NOT NULL default '1970-01-01 00:00:01',
  `last_modified` datetime NOT NULL default '1970-01-01 00:00:01',
  `note_type` int(11) default '0',
  `note_attr` varchar(250) default '',
  `time_tracking` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `idx_bug` (`bug_id`),
  KEY `idx_last_mod` (`last_modified`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_bugnote_text_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `note` longtext NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_config_table` (
  `config_id` varchar(64) NOT NULL,
  `project_id` int(11) NOT NULL default '0',
  `user_id` int(11) NOT NULL default '0',
  `access_reqd` int(11) default '0',
  `type` int(11) default '90',
  `value` longtext NOT NULL,
  PRIMARY KEY  (`config_id`,`project_id`,`user_id`),
  KEY `idx_config` (`config_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_custom_field_project_table` (
  `field_id` int(11) NOT NULL default '0',
  `project_id` int(10) unsigned NOT NULL default '0',
  `sequence` smallint(6) NOT NULL default '0',
  PRIMARY KEY  (`field_id`,`project_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_custom_field_string_table` (
  `field_id` int(11) NOT NULL default '0',
  `bug_id` int(11) NOT NULL default '0',
  `value` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`field_id`,`bug_id`),
  KEY `idx_custom_field_bug` (`bug_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_custom_field_table` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(64) NOT NULL default '',
  `type` smallint(6) NOT NULL default '0',
  `possible_values` varchar(255) NOT NULL default '',
  `default_value` varchar(255) NOT NULL default '',
  `valid_regexp` varchar(255) NOT NULL default '',
  `access_level_r` smallint(6) NOT NULL default '0',
  `access_level_rw` smallint(6) NOT NULL default '0',
  `length_min` int(11) NOT NULL default '0',
  `length_max` int(11) NOT NULL default '0',
  `advanced` tinyint(4) NOT NULL default '0',
  `require_report` tinyint(4) NOT NULL default '0',
  `require_update` tinyint(4) NOT NULL default '0',
  `display_report` tinyint(4) NOT NULL default '1',
  `display_update` tinyint(4) NOT NULL default '1',
  `require_resolved` tinyint(4) NOT NULL default '0',
  `display_resolved` tinyint(4) NOT NULL default '0',
  `display_closed` tinyint(4) NOT NULL default '0',
  `require_closed` tinyint(4) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `idx_custom_field_name` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_email_table` (
  `email_id` int(10) unsigned NOT NULL auto_increment,
  `email` varchar(64) NOT NULL default '',
  `subject` varchar(250) NOT NULL default '',
  `submitted` datetime NOT NULL default '1970-01-01 00:00:01',
  `metadata` longtext NOT NULL,
  `body` longtext NOT NULL,
  PRIMARY KEY  (`email_id`),
  KEY `idx_email_id` (`email_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_filters_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `user_id` int(11) NOT NULL default '0',
  `project_id` int(11) NOT NULL default '0',
  `is_public` tinyint(4) default NULL,
  `name` varchar(64) NOT NULL default '',
  `filter_string` longtext NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_news_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `project_id` int(10) unsigned NOT NULL default '0',
  `poster_id` int(10) unsigned NOT NULL default '0',
  `date_posted` datetime NOT NULL default '1970-01-01 00:00:01',
  `last_modified` datetime NOT NULL default '1970-01-01 00:00:01',
  `view_state` smallint(6) NOT NULL default '10',
  `announcement` tinyint(4) NOT NULL default '0',
  `headline` varchar(64) NOT NULL default '',
  `body` longtext NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_project_category_table` (
  `project_id` int(10) unsigned NOT NULL default '0',
  `category` varchar(64) NOT NULL default '',
  `user_id` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`project_id`,`category`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_project_file_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `project_id` int(10) unsigned NOT NULL default '0',
  `title` varchar(250) NOT NULL default '',
  `description` varchar(250) NOT NULL default '',
  `diskfile` varchar(250) NOT NULL default '',
  `filename` varchar(250) NOT NULL default '',
  `folder` varchar(250) NOT NULL default '',
  `filesize` int(11) NOT NULL default '0',
  `file_type` varchar(250) NOT NULL default '',
  `date_added` datetime NOT NULL default '1970-01-01 00:00:01',
  `content` longblob,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_project_hierarchy_table` (
  `child_id` int(10) unsigned NOT NULL,
  `parent_id` int(10) unsigned NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_project_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(128) NOT NULL default '',
  `status` smallint(6) NOT NULL default '10',
  `enabled` tinyint(4) NOT NULL default '1',
  `view_state` smallint(6) NOT NULL default '10',
  `access_min` smallint(6) NOT NULL default '10',
  `file_path` varchar(250) NOT NULL default '',
  `description` longtext NOT NULL,
  `baseLine_velocity` int(10) NOT NULL default '50',
  `baseLine_cost_per_storyPoint` int(10) NOT NULL default '3',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_project_name` (`name`),
  KEY `idx_project_id` (`id`),
  KEY `idx_project_view` (`view_state`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_project_user_list_table` (
  `project_id` int(10) unsigned NOT NULL default '0',
  `user_id` int(10) unsigned NOT NULL default '0',
  `access_level` smallint(6) NOT NULL default '10',
  PRIMARY KEY  (`project_id`,`user_id`),
  KEY `idx_project_user` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_project_version_table` (
  `id` int(11) NOT NULL auto_increment,
  `project_id` int(10) unsigned NOT NULL default '0',
  `version` varchar(64) NOT NULL default '',
  `date_order` datetime NOT NULL default '1970-01-01 00:00:01',
  `description` longtext NOT NULL,
  `released` tinyint(4) NOT NULL default '1',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_project_version` (`project_id`,`version`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_sponsorship_table` (
  `id` int(11) NOT NULL auto_increment,
  `bug_id` int(11) NOT NULL default '0',
  `user_id` int(11) NOT NULL default '0',
  `amount` int(11) NOT NULL default '0',
  `logo` varchar(128) NOT NULL default '',
  `url` varchar(128) NOT NULL default '',
  `paid` tinyint(4) NOT NULL default '0',
  `date_submitted` datetime NOT NULL default '1970-01-01 00:00:01',
  `last_updated` datetime NOT NULL default '1970-01-01 00:00:01',
  PRIMARY KEY  (`id`),
  KEY `idx_sponsorship_bug_id` (`bug_id`),
  KEY `idx_sponsorship_user_id` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_tag_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `user_id` int(10) unsigned NOT NULL default '0',
  `name` varchar(100) NOT NULL default '',
  `description` longtext NOT NULL,
  `date_created` datetime NOT NULL default '1970-01-01 00:00:01',
  `date_updated` datetime NOT NULL default '1970-01-01 00:00:01',
  PRIMARY KEY  (`id`,`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_tokens_table` (
  `id` int(11) NOT NULL auto_increment,
  `owner` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `timestamp` datetime NOT NULL,
  `expiry` datetime default NULL,
  `value` longtext NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `idx_typeowner` (`type`,`owner`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_user_pref_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `user_id` int(10) unsigned NOT NULL default '0',
  `project_id` int(10) unsigned NOT NULL default '0',
  `default_profile` int(10) unsigned NOT NULL default '0',
  `default_project` int(10) unsigned NOT NULL default '0',
  `advanced_report` tinyint(4) NOT NULL default '0',
  `advanced_view` tinyint(4) NOT NULL default '0',
  `advanced_update` tinyint(4) NOT NULL default '0',
  `refresh_delay` int(11) NOT NULL default '0',
  `redirect_delay` tinyint(4) NOT NULL default '0',
  `bugnote_order` varchar(4) NOT NULL default 'ASC',
  `email_on_new` tinyint(4) NOT NULL default '0',
  `email_on_assigned` tinyint(4) NOT NULL default '0',
  `email_on_feedback` tinyint(4) NOT NULL default '0',
  `email_on_resolved` tinyint(4) NOT NULL default '0',
  `email_on_closed` tinyint(4) NOT NULL default '0',
  `email_on_reopened` tinyint(4) NOT NULL default '0',
  `email_on_bugnote` tinyint(4) NOT NULL default '0',
  `email_on_status` tinyint(4) NOT NULL default '0',
  `email_on_priority` tinyint(4) NOT NULL default '0',
  `email_on_priority_min_severity` smallint(6) NOT NULL default '10',
  `email_on_status_min_severity` smallint(6) NOT NULL default '10',
  `email_on_bugnote_min_severity` smallint(6) NOT NULL default '10',
  `email_on_reopened_min_severity` smallint(6) NOT NULL default '10',
  `email_on_closed_min_severity` smallint(6) NOT NULL default '10',
  `email_on_resolved_min_severity` smallint(6) NOT NULL default '10',
  `email_on_feedback_min_severity` smallint(6) NOT NULL default '10',
  `email_on_assigned_min_severity` smallint(6) NOT NULL default '10',
  `email_on_new_min_severity` smallint(6) NOT NULL default '10',
  `email_bugnote_limit` smallint(6) NOT NULL default '0',
  `language` varchar(32) NOT NULL default 'english',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_user_print_pref_table` (
  `user_id` int(10) unsigned NOT NULL default '0',
  `print_pref` varchar(64) NOT NULL,
  PRIMARY KEY  (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `mantis_user_print_pref_table`
--

LOCK TABLES `mantis_user_print_pref_table` WRITE;
UNLOCK TABLES;

--
-- Table structure for table `mantis_user_profile_table`
--

DROP TABLE IF EXISTS `mantis_user_profile_table`;
CREATE TABLE `mantis_user_profile_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `user_id` int(10) unsigned NOT NULL default '0',
  `platform` varchar(32) NOT NULL default '',
  `os` varchar(32) NOT NULL default '',
  `os_build` varchar(32) NOT NULL default '',
  `description` longtext NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
CREATE TABLE `mantis_user_table` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `username` varchar(32) NOT NULL default '',
  `realname` varchar(64) NOT NULL default '',
  `email` varchar(64) NOT NULL default '',
  `password` varchar(32) NOT NULL default '',
  `date_created` datetime NOT NULL default '1970-01-01 00:00:01',
  `last_visit` datetime NOT NULL default '1970-01-01 00:00:01',
  `enabled` tinyint(4) NOT NULL default '1',
  `protected` tinyint(4) NOT NULL default '0',
  `access_level` smallint(6) NOT NULL default '10',
  `login_count` int(11) NOT NULL default '0',
  `lost_password_request_count` smallint(6) NOT NULL default '0',
  `failed_login_count` smallint(6) NOT NULL default '0',
  `cookie_string` varchar(64) NOT NULL default '',
  `Baseline_Velocity` int(11) NOT NULL default '50',
  `Baseline_Cost_Per_StoryPoint` int(11) NOT NULL default '50',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_user_cookie_string` (`cookie_string`),
  UNIQUE KEY `idx_user_username` (`username`),
  KEY `idx_enable` (`enabled`),
  KEY `idx_access` (`access_level`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `mantis_user_table`
--

LOCK TABLES `mantis_user_table` WRITE;
/*!40000 ALTER TABLE `mantis_user_table` DISABLE KEYS */;
INSERT INTO `mantis_user_table` VALUES (1,'admin','','root@localhost','63a9f0ea7bb98050796b649e85481845','2010-01-19 05:41:23','2010-01-19 05:41:35',1,0,90,4,0,0,'b9bec1c98360692f7ae7baecd9736deaa511ea87cfda0be2ddac035e208e1069',50,50);
/*!40000 ALTER TABLE `mantis_user_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `query`
--

DROP TABLE IF EXISTS `query`;
CREATE TABLE `query` (
  `ID` int(11) NOT NULL auto_increment,
  `PROJECT_ID` int(11) default NULL,
  `USER_ID` int(10) unsigned default NULL,
  `NAME` varchar(255) NOT NULL default '',
  `PROFILE` text,
  PRIMARY KEY  (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `query`
--

LOCK TABLES `query` WRITE;
/*!40000 ALTER TABLE `query` DISABLE KEYS */;
/*!40000 ALTER TABLE `query` ENABLE KEYS */;
UNLOCK TABLES;

/**
 * the new ezScrum 1.8 table
 * re design the table because old tables is a shit and isn't easy to maintain
 */

DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL,
  `nick_name` VARCHAR(255) NULL,
  `email` TEXT NULL,
  `password` VARCHAR(255) NOT NULL,
  `enable` TINYINT NOT NULL DEFAULT 1,
  `create_time` BIGINT UNSIGNED NOT NULL,
  `update_time` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

LOCK TABLES `account` WRITE;
INSERT INTO `account` VALUES (1, 'admin', 'admin', 'example@ezScrum.tw', '21232f297a57a5a743894a0e4a801fc3', 1, 1379910191599, 1379910191599);
UNLOCK TABLES;

DROP TABLE IF EXISTS `project_role`;
CREATE TABLE `project_role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT UNSIGNED NOT NULL,
  `account_id` BIGINT UNSIGNED NOT NULL,
  `role` INT NOT NULL,
  `create_time` BIGINT UNSIGNED NOT NULL,
  `update_time` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `scrum_role`;
CREATE TABLE `scrum_role` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `access_productBacklog` TINYINT NOT NULL DEFAULT 1,
  `access_sprintPlan` TINYINT NOT NULL DEFAULT 1,
  `access_taskboard` TINYINT NOT NULL DEFAULT 1,
  `access_sprintBacklog` TINYINT NOT NULL DEFAULT 1,
  `access_releasePlan` TINYINT NOT NULL DEFAULT 1,
  `access_retrospective` TINYINT NOT NULL DEFAULT 1,
  `access_unplanned` TINYINT NOT NULL DEFAULT 1,
  `access_report` TINYINT NOT NULL DEFAULT 1,
  `access_editProject` TINYINT NOT NULL DEFAULT 1,
  `project_id` BIGINT UNSIGNED NOT NULL,
  `role` INT NOT NULL,
  `create_time` BIGINT UNSIGNED NOT NULL,
  `update_time` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `display_name` VARCHAR(255) NOT NULL,
  `comment` TEXT NULL,
  `product_owner` VARCHAR(255), 
  `attach_max_size` BIGINT UNSIGNED NOT NULL DEFAULT 2, 
  `create_time` BIGINT UNSIGNED NOT NULL,
  `update_time` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `sprint`;
CREATE TABLE `sprint` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `serial_id` BIGINT UNSIGNED NOT NULL,
  `goal` TEXT NOT NULL,
  `interval` INT NOT NULL,
  `membvers` INT NOT NULL,
  `available_hours` INT NOT NULL,
  `focus_factor` INT NOT NULL DEFAULT 100,
  `start_date` DATETIME NOT NULL,
  `demo_date` DATETIME NOT NULL,
  `demo_place` TEXT NULL,
  `daily_info` TEXT NULL,
  `project_id` BIGINT UNSIGNED NOT NULL,
  `create_time` BIGINT UNSIGNED NOT NULL,
  `update_time` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `story`;
CREATE TABLE `story` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT UNSIGNED NOT NULL,
  `serial_id` BIGINT UNSIGNED NOT NULL,
  `sprint_id` BIGINT NULL,
  `name` TEXT NOT NULL,
  `status` INT UNSIGNED NOT NULL,
  `estimate` INT NOT NULL DEFAULT 0,
  `importance` INT NOT NULL DEFAULT 0,
  `value` INT NOT NULL DEFAULT 0,
  `notes` TEXT NULL,
  `how_to_demo` TEXT NULL,
  `create_time` BIGINT UNSIGNED NOT NULL,
  `update_time` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `serial_id` BIGINT UNSIGNED NOT NULL,
  `project_id` BIGINT UNSIGNED NOT NULL,
  `story_id` BIGINT NULL,
  `name` TEXT NOT NULL,
  `handler_id` BIGINT,
  `status` VARCHAR(255) NULL,
  `estimate` INT NOT NULL DEFAULT 0,
  `remain` INT NOT NULL DEFAULT 0,
  `actual` INT NOT NULL DEFAULT 0,
  `notes` TEXT NULL,
  `create_time` BIGINT UNSIGNED NOT NULL,
  `update_time` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `history`;
CREATE TABLE `history` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `issue_id` BIGINT UNSIGNED NOT NULL,
  `issue_type` INT NOT NULL,
  `type` INT NULL,
  `old_value` TEXT NULL,
  `new_value` TEXT NULL,
  `create_time` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `issue_partner_relation`;
CREATE TABLE `issue_partner_relation` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `issue_id` BIGINT UNSIGNED NOT NULL,
  `issue_type` INT NOT NULL,
  `account_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `project_id` BIGINT UNSIGNED NOT NULL,
  `create_time` BIGINT UNSIGNED NOT NULL,
  `update_time` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `story_tag_relation`;
CREATE TABLE `story_tag_relation` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `tag_id` BIGINT UNSIGNED NOT NULL,
  `story_id` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `release`;
CREATE TABLE `release` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `serial_id` BIGINT UNSIGNED NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT NULL,
  `start_date` DATETIME NOT NULL,
  `demo_date` DATETIME NOT NULL,
  `project_id` BIGINT UNSIGNED NOT NULL,
  `create_time` BIGINT UNSIGNED NOT NULL,
  `update_time` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `retrospective`;
CREATE TABLE `retrospective` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `serial_id` BIGINT UNSIGNED NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT NULL,
  `type` VARCHAR(255) NOT NULL,
  `status` VARCHAR(255) NOT NULL,
  `sprint_id` BIGINT UNSIGNED NOT NULL,
  `project_id` BIGINT UNSIGNED NOT NULL,
  `create_time` BIGINT UNSIGNED NOT NULL,
  `update_time` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `unplanned`;
CREATE TABLE `unplanned` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `serial_id` BIGINT UNSIGNED NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `handler` BIGINT UNSIGNED NOT NULL,
  `estimation` INT NOT NULL,
  `actual` INT NOT NULL,
  `notes` TEXT NOT NULL,
  `status` VARCHAR(255) NOT NULL,
  `project_id` BIGINT UNSIGNED NOT NULL,
  `story_id` BIGINT UNSIGNED NOT NULL,
  `create_time` BIGINT UNSIGNED NOT NULL,
  `update_time` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `attach_file`;
CREATE TABLE `attach_file` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` TEXT NOT NULL,
  `issue_id` BIGINT UNSIGNED NOT NULL,
  `issue_type` INT NOT NULL,
  `path` TEXT NOT NULL,
  `content_type` TEXT,
  `create_time` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `system`;
CREATE TABLE `system` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `account_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `account_id_UNIQUE` (`account_id` ASC))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `serial_number`;
CREATE TABLE `serial_number` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT UNSIGNED NOT NULL,
  `release` BIGINT UNSIGNED NOT NULL,
  `sprint` BIGINT UNSIGNED NOT NULL,
  `story` BIGINT UNSIGNED NOT NULL,
  `task` BIGINT UNSIGNED NOT NULL,
  `unplanned` BIGINT UNSIGNED NOT NULL,
  `retrospective` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB DEFAULT CHARSET = utf8;

LOCK TABLES `system` WRITE;
INSERT INTO `system` VALUES (1, 1);
UNLOCK TABLES;
