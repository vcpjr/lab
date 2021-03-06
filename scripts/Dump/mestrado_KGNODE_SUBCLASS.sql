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
-- Table structure for table `KGNODE_SUBCLASS`
--

DROP TABLE IF EXISTS `KGNODE_SUBCLASS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `KGNODE_SUBCLASS` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `IDNODE` int(11) NOT NULL,
  `IDSUBCLASS` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3479 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `KGNODE_SUBCLASS`
--

LOCK TABLES `KGNODE_SUBCLASS` WRITE;
/*!40000 ALTER TABLE `KGNODE_SUBCLASS` DISABLE KEYS */;
INSERT INTO `KGNODE_SUBCLASS` VALUES (3354,14372,14373),(3355,14374,14375),(3356,14376,14372),(3357,14373,14374),(3358,14379,14380),(3359,14380,14374),(3360,14382,14383),(3361,14384,14382),(3362,14385,14375),(3363,14383,14385),(3364,14388,14382),(3365,14391,14392),(3366,14393,14391),(3367,14392,14375),(3368,14395,14375),(3369,14396,14397),(3370,14398,14396),(3371,14397,14395),(3372,14406,14407),(3373,14408,14375),(3374,14409,14406),(3375,14407,14408),(3376,14412,14385),(3377,14413,14412),(3378,14417,14380),(3379,14422,14375),(3380,14424,14374),(3381,14434,14407),(3382,14441,14374),(3383,14444,14383),(3384,14446,14374),(3385,14448,14449),(3386,14465,14375),(3387,14469,14374),(3388,14471,14412),(3389,14488,14489),(3390,14489,14407),(3391,14492,14375),(3392,14494,14396),(3393,14504,14383),(3394,14523,14524),(3395,14524,14375),(3396,14543,14374),(3397,14571,14396),(3398,14593,14594),(3399,14594,14412),(3400,14604,14383),(3401,14605,14604),(3402,14611,14397),(3403,14612,14611),(3404,14621,14622),(3405,14623,14375),(3406,14628,14594),(3407,14637,14397),(3408,14669,14396),(3409,14682,14383),(3410,14732,14733),(3411,14734,14732),(3412,14739,14396),(3413,14747,14748),(3414,14748,14412),(3415,14756,14383),(3416,14805,14412),(3417,14858,14383),(3418,14859,14858),(3419,14891,14396),(3420,14908,14396),(3421,14965,14966),(3422,14967,14968),(3423,14968,14965),(3424,14966,14408),(3425,14977,14375),(3426,14981,14982),(3427,14982,14383),(3428,15000,14412),(3429,15001,15000),(3430,15017,14756),(3431,15028,14372),(3432,15033,14383),(3433,15082,14383),(3434,15190,14622),(3435,15252,14446),(3436,15280,14375),(3437,15324,14407),(3438,15362,15363),(3439,15363,14375),(3440,15402,15280),(3441,15413,14380),(3442,14733,14408),(3443,15424,15425),(3444,15425,14733),(3445,15533,14733),(3446,15534,15533),(3447,15582,14858),(3448,15584,14373),(3449,15733,14396),(3450,15901,14622),(3451,15902,15901),(3452,16010,14966),(3453,16173,14604),(3454,16179,14858),(3455,16214,14408),(3456,16267,16268),(3457,16268,14408),(3458,16338,14384),(3459,16446,14965),(3460,16452,14396),(3461,17029,14858),(3462,17095,15363),(3463,17192,14412),(3464,17211,14375),(3465,17316,17317),(3466,17497,15533),(3467,17498,17497),(3468,18043,14395),(3469,18125,16268),(3470,18231,14748),(3471,18255,14858),(3472,18261,14858),(3473,18262,18261),(3474,18321,15533),(3475,18558,14375),(3476,18559,18558),(3477,19189,18558),(3478,19425,15425);
/*!40000 ALTER TABLE `KGNODE_SUBCLASS` ENABLE KEYS */;
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
