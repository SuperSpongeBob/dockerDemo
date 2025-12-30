/*
 Navicat Premium Data Transfer

 Source Server         : zyx
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : springboot_test

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 30/12/2025 18:29:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息ID',
  `session_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联的会话ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色: user/assistant/system',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容',
  `delete_flag` int NULL DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `sequence_num` bigint NULL DEFAULT NULL COMMENT '消息在会话中的序号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_session_seq`(`session_id` ASC, `sequence_num` ASC) USING BTREE,
  CONSTRAINT `fk_chat_message_session` FOREIGN KEY (`session_id`) REFERENCES `chat_session` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_message
-- ----------------------------
INSERT INTO `chat_message` VALUES ('10b5e1a6-1b2b-4e93-8250-d0e4afed4a94', '1b1027ee-4d55-42f7-8eba-a08408fd2ad7', 'user', '我刚刚问了你几个问题', 0, '2025-12-30 17:55:41', '2025-12-30 17:55:41', 5);
INSERT INTO `chat_message` VALUES ('1230d638-020f-49fe-8f25-7bc93d09ae3e', '6b5581f3-ffd1-4ec7-97b1-71c11cebd5b3', 'user', '续航怎么样', 0, '2025-12-30 17:54:25', '2025-12-30 17:54:25', 3);
INSERT INTO `chat_message` VALUES ('1941ce52-819c-47f6-a391-a14436135640', '1b1027ee-4d55-42f7-8eba-a08408fd2ad7', 'user', '你是什么模型', 0, '2025-12-30 17:55:28', '2025-12-30 17:55:28', 3);
INSERT INTO `chat_message` VALUES ('24a66d2b-6b88-4d71-b200-d73f69959b8e', '6b5581f3-ffd1-4ec7-97b1-71c11cebd5b3', 'user', '你重新算一下多少个问题了', 0, '2025-12-30 17:56:15', '2025-12-30 17:56:15', 9);
INSERT INTO `chat_message` VALUES ('3a3e2e4a-9122-42c3-8047-59d9943dd7a3', '1b1027ee-4d55-42f7-8eba-a08408fd2ad7', 'user', '商品防水效果如何', 0, '2025-12-30 17:55:19', '2025-12-30 17:55:19', 1);
INSERT INTO `chat_message` VALUES ('3a87b367-ef33-4b0e-a53e-11a01dcc451c', '1b1027ee-4d55-42f7-8eba-a08408fd2ad7', 'assistant', '是的，您之前询问了蓝牙耳机的防水效果，我为您提供了IPX4级别的防水信息。请问还有其他关于商品的问题需要帮助吗？', 0, '2025-12-30 17:55:41', '2025-12-30 17:55:41', 6);
INSERT INTO `chat_message` VALUES ('561be6fd-d680-43d8-8bd3-9b5ad9b56b45', '6b5581f3-ffd1-4ec7-97b1-71c11cebd5b3', 'user', '你是什么模型', 0, '2025-12-30 17:54:34', '2025-12-30 17:54:34', 5);
INSERT INTO `chat_message` VALUES ('689e9efa-0866-4055-803e-240be3c684a1', '1b1027ee-4d55-42f7-8eba-a08408fd2ad7', 'assistant', '商品A支持IPX4级防水，能防日常溅水，适合运动使用。', 0, '2025-12-30 17:55:19', '2025-12-30 17:55:19', 2);
INSERT INTO `chat_message` VALUES ('6940a3ac-f8f7-4592-9b21-56886074ec25', '6b5581f3-ffd1-4ec7-97b1-71c11cebd5b3', 'assistant', '问题：蓝牙耳机续航多久？  \n续航20小时。', 0, '2025-12-30 17:53:54', '2025-12-30 17:53:54', 2);
INSERT INTO `chat_message` VALUES ('77172ee4-636a-4411-81d3-2600ac59be53', '6b5581f3-ffd1-4ec7-97b1-71c11cebd5b3', 'user', '我刚刚问了你多少个问题', 0, '2025-12-30 17:54:44', '2025-12-30 17:54:44', 7);
INSERT INTO `chat_message` VALUES ('8265bc3c-7704-44fa-9356-0e9e0620a36f', '6b5581f3-ffd1-4ec7-97b1-71c11cebd5b3', 'assistant', '问题：我刚刚问了你多少个问题  \n您一共问了4个问题。', 0, '2025-12-30 17:54:44', '2025-12-30 17:54:44', 8);
INSERT INTO `chat_message` VALUES ('87c0ec70-38a5-49b8-b14f-0e8fb89a31a1', '6b5581f3-ffd1-4ec7-97b1-71c11cebd5b3', 'assistant', '问题：你是什么模型  \n我是电商客服助手，负责解答商品咨询。', 0, '2025-12-30 17:54:34', '2025-12-30 17:54:34', 6);
INSERT INTO `chat_message` VALUES ('92cc6ead-dd0a-4fff-b65a-8ff49906001a', '6b5581f3-ffd1-4ec7-97b1-71c11cebd5b3', 'assistant', '问题：你重新算一下多少个问题了  \n您一共问了6个问题。', 0, '2025-12-30 17:56:15', '2025-12-30 17:56:15', 10);
INSERT INTO `chat_message` VALUES ('93f711fc-12ae-452b-b49f-eb9df05e1514', '6b5581f3-ffd1-4ec7-97b1-71c11cebd5b3', 'assistant', '问题：续航怎么样  \n续航20小时。', 0, '2025-12-30 17:54:25', '2025-12-30 17:54:25', 4);
INSERT INTO `chat_message` VALUES ('970cc7ff-6d7e-4239-96e0-8f3a1e759227', '1b1027ee-4d55-42f7-8eba-a08408fd2ad7', 'assistant', '我是DeepSeek AI助手，由深度求索公司开发的人工智能模型，很高兴为您提供电商客服服务！', 0, '2025-12-30 17:55:28', '2025-12-30 17:55:28', 4);
INSERT INTO `chat_message` VALUES ('ec6ed72f-add8-4ef1-838d-6ca67c8e2552', '6b5581f3-ffd1-4ec7-97b1-71c11cebd5b3', 'user', '你每次回答我时都需要在前面加上我询问的问题，例如：问题：解析；以这种形式回答', 0, '2025-12-30 17:53:54', '2025-12-30 17:53:54', 1);

-- ----------------------------
-- Table structure for chat_session
-- ----------------------------
DROP TABLE IF EXISTS `chat_session`;
CREATE TABLE `chat_session`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会话标题',
  `delete_flag` int NULL DEFAULT 0 COMMENT '删除标记: 0-未删除, 1-已删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_delete_flag`(`delete_flag` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_session
-- ----------------------------
INSERT INTO `chat_session` VALUES ('1b1027ee-4d55-42f7-8eba-a08408fd2ad7', '商品防水效果如何', 0, '2025-12-30 17:55:17', '2025-12-30 17:55:41');
INSERT INTO `chat_session` VALUES ('6b5581f3-ffd1-4ec7-97b1-71c11cebd5b3', '你每次回答我时都需要在前面加上我询问的问', 0, '2025-12-30 17:53:52', '2025-12-30 17:56:15');

-- ----------------------------
-- Table structure for tb_test_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_test_user`;
CREATE TABLE `tb_test_user`  (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键ID（自增）',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名（唯一）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（测试用，实际需加密）',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `age` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '年龄（无符号，0-255）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Spring Boot MySQL 连接测试表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_test_user
-- ----------------------------
INSERT INTO `tb_test_user` VALUES ('02bfa02e-bab3-4b13-acdd-fc19f4b5513f', 'test03', '<PASSWORD>', '<EMAIL>', 18, 1, '2025-12-10 11:47:11', '2025-12-10 11:47:11', 0);
INSERT INTO `tb_test_user` VALUES ('08f1c04c-9722-4918-853c-6b40007c36f0', 'test02', '654321', 'test02@example.com', 25, 1, '2025-12-04 09:46:01', '2025-12-09 18:17:38', 0);
INSERT INTO `tb_test_user` VALUES ('3237de9a-d846-428d-ad62-5f8a84a4976a', 'test01', '123456', 'test01@example.com', 20, 2, '2025-12-04 09:46:01', '2025-12-10 11:48:47', 0);

SET FOREIGN_KEY_CHECKS = 1;
