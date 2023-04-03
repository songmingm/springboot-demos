/*
 Navicat Premium Data Transfer

 Source Server         : 101.43.217.193
 Source Server Type    : MySQL
 Source Server Version : 80024 (8.0.24)
 Source Host           : 101.43.217.193:3306
 Source Schema         : fanta

 Target Server Type    : MySQL
 Target Server Version : 80024 (8.0.24)
 File Encoding         : 65001

 Date: 01/02/2023 16:56:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_upload_task
-- ----------------------------
DROP TABLE IF EXISTS `sys_upload_task`;
CREATE TABLE `sys_upload_task`  (
  `id` bigint NOT NULL,
  `upload_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分片上传的uploadId',
  `file_identifier` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件唯一标识（md5）',
  `file_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',
  `bucket_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '所属桶名',
  `object_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件的key',
  `total_size` bigint NOT NULL COMMENT '文件大小（byte）',
  `chunk_size` bigint NOT NULL COMMENT '每个分片大小（byte）',
  `chunk_num` int NOT NULL COMMENT '分片数量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_file_identifier`(`file_identifier` ASC) USING BTREE,
  UNIQUE INDEX `uq_upload_id`(`upload_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分片上传-分片任务记录' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
