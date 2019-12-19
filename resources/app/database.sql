drop table if exists sys_user;
create table sys_user (
	id varchar(32) not null unique,
	username varchar(32) not null unique,
	password varchar(64) not null,
	name varchar(32) default null,
	tel varchar(11) default null,
	avatar blob default null,
	primary key (id)
);
drop table if exists sys_login;
create table sys_login (
	id varchar(32) not null,
	user varchar(32) not null,
	ip varchar(16) default null,
	address varchar(64) default null,
	isp varchar(64) default null,
	platform varchar(64) default null,
	time datetime not null,
	success boolean default false,
	primary key (id)
);
insert into sys_user(id,username,password,name) values('f47073d84c224ab68b7e428b0149d0cb','admin','AC0E7D037817094E9E0B4441F9BAE3209D67B02FA484917065F71B16109A1A78','管理员');