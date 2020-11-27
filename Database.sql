CREATE DATABASE `qq_bot` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

use qq_bot;

CREATE TABLE `group_has_qq_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creator` char(10) NOT NULL DEFAULT 'system',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modifier` char(10) NOT NULL DEFAULT 'system',
  `group_id` bigint NOT NULL,
  `qq_user_id` bigint NOT NULL,
  `message_count` int NOT NULL DEFAULT '0' COMMENT '累计统计到的消息数量',
  `name_card` varchar(45) NOT NULL DEFAULT '',
  `nick_name` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idx_qq_id` (`qq_user_id`) /*!80000 INVISIBLE */,
  KEY `idx_group_id` (`group_id`),
  CONSTRAINT `fk1` FOREIGN KEY (`group_id`) REFERENCES `qq_group` (`id`),
  CONSTRAINT `fk2` FOREIGN KEY (`qq_user_id`) REFERENCES `qq_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creator` char(10) NOT NULL DEFAULT 'system',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modifier` char(10) NOT NULL DEFAULT 'system',
  `message_type` tinyint NOT NULL DEFAULT '0' COMMENT '消息类型，0私聊1群聊',
  `sender_id` bigint NOT NULL DEFAULT '0' COMMENT '发送方',
  `receiver_id` bigint NOT NULL DEFAULT '0' COMMENT '接收方',
  `group_id` bigint NOT NULL DEFAULT '0',
  `sender_name` varchar(45) NOT NULL DEFAULT '' COMMENT '发送方昵称',
  `receiver_name` varchar(45) NOT NULL DEFAULT '' COMMENT '接收方昵称',
  `group_name` varchar(45) NOT NULL DEFAULT '',
  `message_detail` varchar(4096) NOT NULL DEFAULT '' COMMENT '消息详情',
  PRIMARY KEY (`id`),
  KEY `idx_group_message` (`create_time`,`group_id`,`sender_id`) /*!80000 INVISIBLE */
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `qq_group` (
  `id` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creator` char(10) NOT NULL DEFAULT 'system',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modifier` char(10) NOT NULL DEFAULT 'system',
  `name` varchar(45) NOT NULL DEFAULT '' COMMENT '群名',
  `user_count` int NOT NULL DEFAULT '0' COMMENT '群员数量',
  `message_count` int NOT NULL DEFAULT '0' COMMENT '累计统计到的消息数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `qq_user` (
  `id` bigint NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `creator` char(10) NOT NULL DEFAULT 'system',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modifier` char(10) NOT NULL DEFAULT 'system',
  `nick_name` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
