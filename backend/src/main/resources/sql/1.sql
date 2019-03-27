CREATE SEQUENCE article_id_seq START WITH 10000000;
create table article
(
  aid            bigint       default nextval('article_id_seq'::regclass) not null
    constraint article_pkey
      primary key,
  app_id         integer                                                  not null,
  app_name       varchar(32)  default ''::character varying               not null,
  app_name_cn    varchar(32)  default ''::character varying               not null,
  column_name    varchar(32)  default ''::character varying               not null,
  column_name_cn varchar(32)  default ''::character varying               not null,
  title          text         default ''::text                            not null,
  content        text         default ''::text                            not null,
  html           text         default ''::text                            not null,
  post_time      bigint       default 0                                   not null,
  src            varchar(32),
  author         varchar(32),
  src_image      text,
  src_url        varchar(128) default ''::character varying               not null
);


CREATE INDEX article_time_index on article(post_time);
CREATE INDEX article_column_index on article(column_name);
CREATE UNIQUE INDEX article_src_url on article(src_url);
create index article_column_aid_index on article(column_name,aid);

CREATE TABLE article_image(
  src_image VARCHAR(128) PRIMARY KEY ,
  hestia_image VARCHAR(128) NOT NULL DEFAULT ''
);

CREATE SEQUENCE spider_failed_task_id_seq START WITH 10000000;
CREATE TABLE spider_failed_task(
  id        BIGINT PRIMARY KEY DEFAULT nextval('spider_failed_task_id_seq'),
  app_id INTEGER NOT NULL ,
  app_name VARCHAR(32) NOT NULL DEFAULT '',
  app_name_cn VARCHAR(32) NOT NULL DEFAULT '',
  url VARCHAR(128) NOT NULL DEFAULT '',
  task_type VARCHAR (16) NOT NULL DEFAULT '',
  error TEXT NOT NULL DEFAULT '',
  create_time BIGINT NOT NULL DEFAULT 0
);


CREATE TABLE news_column(
  column_name VARCHAR(32) PRIMARY KEY ,
  column_name_cn VARCHAR(32) NOT NULL DEFAULT ''
);

CREATE SEQUENCE user_deifin_column_id_seq START WITH 10000000;
CREATE TABLE user_define_column(
  id BIGINT PRIMARY KEY DEFAULT nextval('user_deifin_column_id_seq'),
  account_id BIGINT NOT NULL DEFAULT 0,
  column_name VARCHAR(32) NOT NULL DEFAULT '',
  column_name_cn VARCHAR(32) NOT NULL DEFAULT '',
  create_time BIGINT NOT NULL DEFAULT 0
);


CREATE SEQUENCE account_id_seq START WITH 10000000;
CREATE TABLE account(
  id        BIGINT PRIMARY KEY DEFAULT nextval('account_id_seq'),
  account_name VARCHAR(32) NOT NULL,
  password VARCHAR(128) NOT NULL DEFAULT '',
  create_time BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX account_name_index on account(account_name);


CREATE SEQUENCE comment_id_seq START WITH 10000000;
create TABLE comment(
  cid        BIGINT PRIMARY KEY DEFAULT nextval('comment_id_seq'),
  app_id    INTEGER NOT NULL ,
  app_name   VARCHAR(32)   NOT NULL DEFAULT '',
  app_name_cn   VARCHAR(32)   NOT NULL DEFAULT '',
  column_name VARCHAR(32)   NOT NULL DEFAULT '',
  column_name_cn VARCHAR(32)   NOT NULL DEFAULT '',
  content TEXT NOT NULL DEFAULT '' ,
  postTime BIGINT NOT NULL DEFAULT 0,
  source TEXT NOT NULL DEFAULT '' ,
  user VARCHAR(128) NULL,
  userId BIGINT NULL,
  imageList TEXT NULL,
  articleUrl VARCHAR(128) NOT NULL DEFAULT '',
  commentUrl VARCHAR(128) NOT NULL DEFAULT '',
  replyId BIGINT NULL,
  commentId BIGINT NOT NULL DEFAULT 0,
  buildLevel INTEGER NOT NULL DEFAULT 1,
  vote INTEGER NOT NULL DEFAULT 0
);
create index comment_article_index ON comment(article_id);


CREATE TABLE article_thumb_up(
  article_id BIGINT NOT NULL DEFAULT 0,
  account_id BIGINT NOT NULL DEFAULT 0,
  create_time BIGINT NOT NULL DEFAULT 0
);
create index article_thumb_up_index on article_thumb_up(article_id);
create index account_thumb_up_index on article_thumb_up(article_id,account_id);



ALTER TABLE public.article_image DROP CONSTRAINT article_image_pkey;
DROP INDEX public.article_image_pkey RESTRICT;
ALTER TABLE public.article_image ALTER COLUMN src_image SET DEFAULT '';
CREATE INDEX article_image_src_image_index ON public.article_image (src_image);

CREATE TABLE articleDuplicatedRecord(
   src_url VARCHAR(128) NOT NULL DEFAULT '',
   duplicated_url VARCHAR (128) NOT NULL DEFAULT '',
   create_time BIGINT NOT NULL DEFAULT 0
);