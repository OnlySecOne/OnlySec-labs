/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : localhost:3306
 Source Schema         : OnlySec

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 19/04/2025 08:14:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of admin
-- ----------------------------
BEGIN;
INSERT INTO `admin` (`username`, `password`) VALUES ('admin', 'admin123');
COMMIT;

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(255) DEFAULT NULL,
  `userId` bigint DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of messages
-- ----------------------------
BEGIN;
INSERT INTO `messages` (`id`, `content`, `userId`, `createTime`) VALUES (7, 'hello world', 3, '2024-12-16 16:13:38');
INSERT INTO `messages` (`id`, `content`, `userId`, `createTime`) VALUES (8, '11555512', 3, '2025-03-08 12:46:30');
COMMIT;

-- ----------------------------
-- Table structure for transfer
-- ----------------------------
DROP TABLE IF EXISTS `transfer`;
CREATE TABLE `transfer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `to_account` varchar(100) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `timestamp` datetime NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of transfer
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话号码',
  `status` int DEFAULT '1' COMMENT '用户状态',
  `CreateTime` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `username`, `name`, `email`, `phone`, `status`, `CreateTime`) VALUES (13, 'abc', 'abc', 'abc@qq.com', '1121212', 1, '2024-12-23T14:11:26.387788');
INSERT INTO `user` (`id`, `username`, `name`, `email`, `phone`, `status`, `CreateTime`) VALUES (14, '测试', 'ceshi\"', 'chi@qq.com', '18888888888', 1, '2025-03-08T12:52:45.265839');
INSERT INTO `user` (`id`, `username`, `name`, `email`, `phone`, `status`, `CreateTime`) VALUES (15, '1\"', '1', '1', '1', 1, '2025-03-08T12:47:48.061015');
INSERT INTO `user` (`id`, `username`, `name`, `email`, `phone`, `status`, `CreateTime`) VALUES (16, '2', '2', '2', '2', 1, '2024-12-18T12:26:02.483270');
INSERT INTO `user` (`id`, `username`, `name`, `email`, `phone`, `status`, `CreateTime`) VALUES (18, '6', '6', '6', '6', 1, '2024-12-20T12:05:31.556453');
INSERT INTO `user` (`id`, `username`, `name`, `email`, `phone`, `status`, `CreateTime`) VALUES (25, '12', '12', '12', '1212', 1, '2024-12-22T17:10:33.384301');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
