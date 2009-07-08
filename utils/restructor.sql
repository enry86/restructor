-- SQL script for restructor database --

create schema if not exists `restructor`;
use `restructor`;

create table if not exists `pairs` (
`pair_id` integer not null auto_increment,
`name` varchar(30) not null,
`value` varchar(200) not null,
unique key `pair` (`value`,`name`),
primary key (`pair_id`));

create table if not exists `attrs` (
`attr_id` integer not null auto_increment,
`name` varchar(30) not null,
`path` varchar(50) not null,
unique key `attr` (`name`,`path`),
primary key (`attr_id`));

create table if not exists `associations` (
`entity_id` integer not null,
`pair_id` integer not null,
`attr_id` integer not null,
primary key (`entity_id`, `pair_id`, `attr_id`),
foreign key (`pair_id`) references `pairs`(`pair_id`),
foreign key (`attr_id`) references `attrs`(`attr_id`));




