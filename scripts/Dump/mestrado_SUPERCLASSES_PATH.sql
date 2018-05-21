CREATE DATABASE  IF NOT EXISTS `mestrado` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `mestrado`;
-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: 127.0.0.1    Database: mestrado
-- ------------------------------------------------------
-- Server version	5.7.20

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
-- Table structure for table `SUPERCLASSES_PATH`
--

DROP TABLE IF EXISTS `SUPERCLASSES_PATH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SUPERCLASSES_PATH` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `IDNODE_CLASS_TYPE_RELATIONSHIP` int(11) NOT NULL,
  `CSV_IDNODES_PATH_TO_THING` varchar(400) NOT NULL COMMENT 'Comma separated ids from classes from TYPE to THING',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IDNODE_CLASS_TYPE_RELATIONSHIP_UNIQUE` (`IDNODE_CLASS_TYPE_RELATIONSHIP`)
) ENGINE=InnoDB AUTO_INCREMENT=232 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SUPERCLASSES_PATH`
--

LOCK TABLES `SUPERCLASSES_PATH` WRITE;
/*!40000 ALTER TABLE `SUPERCLASSES_PATH` DISABLE KEYS */;
INSERT INTO `SUPERCLASSES_PATH` VALUES (113,14372,'14373,14374,14375,'),(114,14374,'14375,'),(115,14376,'14372,14373,14374,14375,'),(116,14373,'14374,14375,'),(117,14379,'14380,14374,14375,'),(118,14380,'14374,14375,'),(119,14382,'14383,14385,14375,'),(120,14384,'14382,14383,14385,14375,'),(121,14385,'14375,'),(122,14383,'14385,14375,'),(123,14388,'14382,14383,14385,14375,'),(124,14391,'14392,14375,'),(125,14393,'14391,14392,14375,'),(126,14392,'14375,'),(127,14395,'14375,'),(128,14396,'14397,14395,14375,'),(129,14398,'14396,14397,14395,14375,'),(130,14397,'14395,14375,'),(131,14406,'14407,14408,14375,'),(132,14408,'14375,'),(133,14409,'14406,14407,14408,14375,'),(134,14407,'14408,14375,'),(135,14412,'14385,14375,'),(136,14413,'14412,14385,14375,'),(137,14417,'14380,14374,14375,'),(138,14422,'14375,'),(139,14424,'14374,14375,'),(140,14434,'14407,14408,14375,'),(141,14441,'14374,14375,'),(142,14444,'14383,14385,14375,'),(143,14446,'14374,14375,'),(144,14465,'14375,'),(145,14469,'14374,14375,'),(146,14471,'14412,14385,14375,'),(147,14488,'14489,14407,14408,14375,'),(148,14489,'14407,14408,14375,'),(149,14492,'14375,'),(150,14494,'14396,14397,14395,14375,'),(151,14504,'14383,14385,14375,'),(152,14523,'14524,14375,'),(153,14524,'14375,'),(154,14543,'14374,14375,'),(155,14571,'14396,14397,14395,14375,'),(156,14593,'14594,14412,14385,14375,'),(157,14594,'14412,14385,14375,'),(158,14604,'14383,14385,14375,'),(159,14605,'14604,14383,14385,14375,'),(160,14611,'14397,14395,14375,'),(161,14612,'14611,14397,14395,14375,'),(162,14623,'14375,'),(163,14628,'14594,14412,14385,14375,'),(164,14637,'14397,14395,14375,'),(165,14669,'14396,14397,14395,14375,'),(166,14682,'14383,14385,14375,'),(167,14732,'14733,14408,14375,'),(168,14734,'14732,14733,14408,14375,'),(169,14739,'14396,14397,14395,14375,'),(170,14747,'14748,14412,14385,14375,'),(171,14748,'14412,14385,14375,'),(172,14756,'14383,14385,14375,'),(173,14805,'14412,14385,14375,'),(174,14858,'14383,14385,14375,'),(175,14859,'14858,14383,14385,14375,'),(176,14891,'14396,14397,14395,14375,'),(177,14908,'14396,14397,14395,14375,'),(178,14965,'14966,14408,14375,'),(179,14967,'14968,14965,14966,14408,14375,'),(180,14968,'14965,14966,14408,14375,'),(181,14966,'14408,14375,'),(182,14977,'14375,'),(183,14981,'14982,14383,14385,14375,'),(184,14982,'14383,14385,14375,'),(185,15000,'14412,14385,14375,'),(186,15001,'15000,14412,14385,14375,'),(187,15017,'14756,14383,14385,14375,'),(188,15028,'14372,14373,14374,14375,'),(189,15033,'14383,14385,14375,'),(190,15082,'14383,14385,14375,'),(191,15252,'14446,14374,14375,'),(192,15280,'14375,'),(193,15324,'14407,14408,14375,'),(194,15362,'15363,14375,'),(195,15363,'14375,'),(196,15402,'15280,14375,'),(197,15413,'14380,14374,14375,'),(198,14733,'14408,14375,'),(199,15424,'15425,14733,14408,14375,'),(200,15425,'14733,14408,14375,'),(201,15533,'14733,14408,14375,'),(202,15534,'15533,14733,14408,14375,'),(203,15582,'14858,14383,14385,14375,'),(204,15584,'14373,14374,14375,'),(205,15733,'14396,14397,14395,14375,'),(206,16010,'14966,14408,14375,'),(207,16173,'14604,14383,14385,14375,'),(208,16179,'14858,14383,14385,14375,'),(209,16214,'14408,14375,'),(210,16267,'16268,14408,14375,'),(211,16268,'14408,14375,'),(212,16338,'14384,14382,14383,14385,14375,'),(213,16446,'14965,14966,14408,14375,'),(214,16452,'14396,14397,14395,14375,'),(215,17029,'14858,14383,14385,14375,'),(216,17095,'15363,14375,'),(217,17192,'14412,14385,14375,'),(218,17211,'14375,'),(219,17497,'15533,14733,14408,14375,'),(220,17498,'17497,15533,14733,14408,14375,'),(221,18043,'14395,14375,'),(222,18125,'16268,14408,14375,'),(223,18231,'14748,14412,14385,14375,'),(224,18255,'14858,14383,14385,14375,'),(225,18261,'14858,14383,14385,14375,'),(226,18262,'18261,14858,14383,14385,14375,'),(227,18321,'15533,14733,14408,14375,'),(228,18558,'14375,'),(229,18559,'18558,14375,'),(230,19189,'18558,14375,'),(231,19425,'15425,14733,14408,14375,');
/*!40000 ALTER TABLE `SUPERCLASSES_PATH` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-05-21 17:21:06
