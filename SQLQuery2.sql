
create database DS2_PROJ
use DS2_PROJ

create table HTMLS(
	ScrapedInfo Text not null
)
create table CURRENT_QUEUE(
	Urls varchar(900) not null
	constraint [PK_CQ] unique (Urls)
)
create table DATES(
	ScrapedInfo varchar(900) not null
	constraint [PK_D] primary key(ScrapedInfo)
)
create table EMAILS(
	ScrapedInfo varchar(900) not null
		constraint [PK_E] primary key(ScrapedInfo)

)
create table EXTERNAL_URLS(
	ScrapedInfo varchar(900) not null
		constraint [PK_EU] primary key(ScrapedInfo)

)
create table PHONE_NUMS(
	ScrapedInfo varchar(900) not null
		constraint [PK_PN] primary key(ScrapedInfo)

)
create table TIMES(
	ScrapedInfo varchar(900) not null
		constraint [PK_T] primary key(ScrapedInfo)

)
create table TOURO_URLS(
	ScrapedInfo varchar(900) not null
		constraint [PK_TU] primary key(ScrapedInfo)

)

