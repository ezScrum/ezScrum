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

-- 導出 notificationms 的資料庫結構
CREATE DATABASE IF NOT EXISTS `notificationms` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `notificationms`;


-- 導出  表 notificationms.filter_model 結構
CREATE TABLE IF NOT EXISTS `filter_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `filter` varchar(255) DEFAULT NULL,
  `subscriber_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在導出表  notificationms.filter_model 的資料：~0 rows (大約)
/*!40000 ALTER TABLE `filter_model` DISABLE KEYS */;
/*!40000 ALTER TABLE `filter_model` ENABLE KEYS */;


-- 導出  表 notificationms.subscriber_model 結構
CREATE TABLE IF NOT EXISTS `subscriber_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- 正在導出表  notificationms.subscriber_model 的資料：~0 rows (大約)
/*!40000 ALTER TABLE `subscriber_model` DISABLE KEYS */;
INSERT INTO `subscriber_model` (`id`, `username`) VALUES
	(1, 'user1');
/*!40000 ALTER TABLE `subscriber_model` ENABLE KEYS */;


-- 導出  表 notificationms.token_model 結構
CREATE TABLE IF NOT EXISTS `token_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- 正在導出表  notificationms.token_model 的資料：~0 rows (大約)
/*!40000 ALTER TABLE `token_model` DISABLE KEYS */;
INSERT INTO `token_model` (`id`, `token`) VALUES
	(1, 'TestToken');
/*!40000 ALTER TABLE `token_model` ENABLE KEYS */;


-- 導出  表 notificationms.token_relation_model 結構
CREATE TABLE IF NOT EXISTS `token_relation_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `logon` bit(1) NOT NULL,
  `subscriberid` bigint(20) DEFAULT NULL,
  `tokenid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- 正在導出表  notificationms.token_relation_model 的資料：~0 rows (大約)
/*!40000 ALTER TABLE `token_relation_model` DISABLE KEYS */;
INSERT INTO `token_relation_model` (`id`, `logon`, `subscriberid`, `tokenid`) VALUES
	(1, b'1', 1, 1);
/*!40000 ALTER TABLE `token_relation_model` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
