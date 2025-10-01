-- 创建数据库
create database if not exists get_ai_code_mother;
-- 切换数据库
use get_ai_code_mother;

-- user表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',                             -- 最后一次代码修改时间
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间', -- 最后一次数据修改时间
    isDelete     tinyint      default 0                 not null comment '是否删除',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 修改 userAccount 单字段唯一约束为 联合唯一约束
ALTER TABLE user
    DROP INDEX uk_userAccount, -- 删除原单字段约束
    ADD UNIQUE KEY uk_userAccount_isDelete (userAccount, isDelete); -- 新增联合约束


