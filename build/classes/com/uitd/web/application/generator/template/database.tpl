drop table if exists {{prefix}}_{{name}};
create table {{prefix}}_{{name}} (
	id varchar(32) not null,
	{{#repeat columns}}
	{{columns.name}} {{columns.type}} {{columns.nullable}},
	{{#repeat}}
	primary key (id)
);
