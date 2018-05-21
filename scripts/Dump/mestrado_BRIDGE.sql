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
-- Table structure for table `BRIDGE`
--

DROP TABLE IF EXISTS `BRIDGE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `BRIDGE` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `IDNODE` int(11) NOT NULL,
  `HIGH_LEVEL_CLASS` varchar(200) NOT NULL,
  `TYPE` varchar(45) NOT NULL COMMENT 'KEY, NEW, INCONSISTENT',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2729 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `BRIDGE`
--

LOCK TABLES `BRIDGE` WRITE;
/*!40000 ALTER TABLE `BRIDGE` DISABLE KEYS */;
INSERT INTO `BRIDGE` VALUES (2650,14385,'more general than gr classes','Key'),(2651,14488,'gr:Location','Key'),(2652,14417,'gr:ProductOrService','Key'),(2653,14396,'gr:ProductOrService','Key'),(2654,14733,'no gr class','Key'),(2655,14382,'gr:BusinessEntity','Key'),(2656,14858,'gr:BusinessEntity','Key'),(2657,14492,'gr:Brand','Key'),(2658,18043,'no gr class','Key'),(2659,14448,'gr:BusinessEntity','Key'),(2660,15402,'gr:ProductOrService','Key'),(2661,14965,'gr:Location','Key'),(2662,15584,'gr:ProductOrService','Key'),(2663,14594,'gr:BusinessEntity','Key'),(2664,15533,'gr:Location','Key'),(2665,14523,'gr:ProductOrService','Key'),(2666,14524,'gr:ProductOrService','Key'),(2667,14409,'gr:Location','Key'),(2668,14471,'gr:BusinessEntity','Key'),(2669,14434,'gr:Location','Key'),(2670,14977,'gr:PriceSpecification.hasCurrency','Key'),(2671,14422,'no gr class','Key'),(2672,15000,'gr:BusinessEntity','Key'),(2673,17211,'No gr class, but may be relevant for e-business','Key'),(2674,14397,'no gr class','Key'),(2675,14623,'No gr class, but may be relevant for e-business','Key'),(2676,15082,'no gr class','Key'),(2677,14424,'gr:ProductOrService','Key'),(2678,14908,'no gr class','Key'),(2679,15280,'gr:ProductOrService','Key'),(2680,14391,'gr:ProductOrService','Key'),(2681,15425,'gr:BusinessEntity','Key'),(2682,14891,'no gr class','Key'),(2683,15324,'gr:Location','Key'),(2684,16446,'gr:Location','Key'),(2685,14465,'No gr class, but may be relevant for e-business','Key'),(2686,14376,'gr:ProductOrService','Key'),(2687,14739,'no gr class','Key'),(2688,15363,'gr:ProductOrService','Key'),(2689,17192,'no gr class','Key'),(2690,15534,'gr:BusinessEntity','Key'),(2691,14388,'gr:BusinessEntity','Key'),(2692,14380,'gr:ProductOrService','Key'),(2693,14621,'No gr class, but may be relevant for e-business','Key'),(2694,14393,'gr:ProductOrService','Key'),(2695,14966,'gr:Location','Key'),(2696,15028,'gr:ProductOrService','Key'),(2697,14412,'gr:BusinessEntity','Key'),(2698,14372,'gr:ProductOrService','Key'),(2699,14383,'No gr class, but may be relevant for e-business','Key'),(2700,14408,'gr:Location','Key'),(2701,14611,'no gr class','Key'),(2702,14413,'no gr class','Key'),(2703,14756,'no gr class','Key'),(2704,14407,'gr:Location','Key'),(2705,14593,'gr:BusinessEntity','Key'),(2706,14489,'gr:Location','Key'),(2707,14669,'no gr class','Key'),(2708,14967,'gr:Location','Key'),(2709,14444,'no gr class','Key'),(2710,14406,'gr:Location','Key'),(2711,17095,'gr:ProductOrService','Key'),(2712,14379,'gr:ProductOrService','Key'),(2713,14747,'gr:BusinessEntity','Key'),(2714,14859,'no gr class','Key'),(2715,14446,'gr:ProductOrService','Key'),(2716,14395,'no gr class','Key'),(2717,14748,'gr:BusinessEntity','Key'),(2718,14968,'gr:Location','Key'),(2719,14441,'gr:ProductOrService','Key'),(2720,14628,'gr:BusinessEntity','Key'),(2721,14392,'no gr class','Key'),(2722,15001,'gr:BusinessEntity','Key'),(2723,15252,'gr:ProductOrService','Key'),(2724,14469,'No gr class, but may be relevant for e-business','Key'),(2725,14374,'gr:Product','Key'),(2726,14373,'gr:ProductOrService','Key'),(2727,14448,'gr:BusinessEntity','New'),(2728,14621,'No gr class, but may be relevant for e-business','New');
/*!40000 ALTER TABLE `BRIDGE` ENABLE KEYS */;
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
