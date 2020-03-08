###java的springboot 商城秒杀

```sql
CREATE TABLE `miaosha_user`(
	`id` bigint(20) NOT NULL COMMENT '用户ID，手机号码',
	`nickname` varchar(255) NOT NULL,
	`password` varchar(32) DEFAULT NULL COMMENT 'MD5(MD5(pass明文+固定salt)+salt)',
	`salt` varchar(10) default NULL,
	`head` varchar(128) default NULL COMMENT '头像云存储的ID',
	`register_date` datetime default NULL COMMENT '注册时间',
	`last_login_date` datetime DEFAULT NULL COMMENT '上次登录时间',
  `login_count` int(11) default 0 COMMENT '登陆次数',
	PRIMARY KEY(`id`)
)ENGINE=INNODB DEFAULT CHARSET=UTF8MB4
```