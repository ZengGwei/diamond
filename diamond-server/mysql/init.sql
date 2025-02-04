--create database diamond default character set utf8 ;
--grant all privileges on diamond.* to 'dev'@'localhost';
--flush privileges;

drop table if exists config_info cascade;

create table config_info (
    id bigint(64) unsigned not null auto_increment,
    data_id varchar(255) not null default '',
    group_id varchar(128) not null default '',
    content longtext not null,
    md5 varchar(32) not null default '',
    gmt_create datetime not null default '2010-05-05 00:00:00',
    gmt_modified datetime not null default '2010-05-05 00:00:00',
    primary key  (id),
    unique key uk_config_datagroup (data_id,group_id)
);
drop table if exists config_info_history cascade;
CREATE TABLE `config_info_history` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL DEFAULT ' ',
  `group_id` varchar(128) NOT NULL DEFAULT ' ',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `md5` varchar(32) NOT NULL DEFAULT ' ',
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `memo` varchar(255) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `idex_dataid` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;