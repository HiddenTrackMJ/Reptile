-- user table
CREATE SEQUENCE user_id_seq START WITH 10000;
CREATE TABLE users (
  id        BIGINT PRIMARY KEY DEFAULT nextval('user_id_seq'),
  username      VARCHAR(255)   NOT NULL,
  secure_pwd    VARCHAR(255)   NOT NULL,
  email         VARCHAR(255)   NOT NULL,
  linux_account VARCHAR(128)   NOT NULL,
  coin_standard INT            NOT NULL,
  create_time   BIGINT         NOT NULL,
  state         INT            NOT NULL DEFAULT 0,
  coin          FLOAT          NOT NULL DEFAULT 0
);

CREATE TABLE admin (
  id          SERIAL8 PRIMARY KEY NOT NULL,
  account     VARCHAR(255) NOT NULL,
  secure_pwd  VARCHAR(255) NOT NULL,
  create_time BIGINT NOT NULL
);

CREATE TABLE coins_record (
  uid          BIGINT       NOT NULL,
  record_type  INT          NOT NULL,
  coins        FLOAT        NOT NULL,
  remark       VARCHAR(512) NOT NULL,
  order_id     BIGINT,
  create_time  BIGINT       NOT NULL
);

CREATE SEQUENCE rent_order_id_seq START WITH 5000000;
CREATE TABLE rent_order (
  id         BIGINT PRIMARY KEY DEFAULT nextval('rent_order_id_seq'),
  user_id    BIGINT      NOT NULL,
  user_name  VARCHAR(255)   NOT NULL,
  equ_id     BIGINT      NOT NULL,
  start_time BIGINT      NOT NULL,
  end_time   BIGINT      NOT NULL,
  cost_coins FLOAT         NOT NULL,
  state      INT         NOT NULL DEFAULT 0,
  create_time BIGINT NOT NULL,
  return_coins FLOAT,
  equ_ip     VARCHAR(255)   NOT NULL,
  equ_name   VARCHAR(255)   NOT NULL
);

CREATE TABLE abnormal_usage (
  id         SERIAL8 PRIMARY KEY NOT NULL,
  abnormal_type INT         NOT NULL,
  user_id       BIGINT      NOT NULL,
  equ_id        BIGINT      NOT NULL,
  equ_name      VARCHAR(255)  NOT NULL,
  start_time    BIGINT,
  end_time      BIGINT,
  duration      INT,
  fine          FLOAT         NOT NULL
);

CREATE SEQUENCE machine_id_seq START WITH 70000;
CREATE TABLE machine (
  id         BIGINT PRIMARY KEY DEFAULT nextval('machine_id_seq'),
  name       VARCHAR(128) NOT NULL,
  ip         VARCHAR(64) NOT NULL
);

CREATE SEQUENCE gpu_equipment_id_seq START WITH 90000;
CREATE TABLE gpu_equipment (
  id          BIGINT PRIMARY KEY DEFAULT nextval('gpu_equipment_id_seq'),
  machine_id  BIGINT       NOT NULL,
  machine_ip  VARCHAR(64) NOT NULL,
  name        VARCHAR(64) NOT NULL,
  fee         INT     NOT NULL,
  create_time BIGINT       NOT NULL
);

CREATE TABLE coins_standard_record (
  uid          BIGINT       NOT NULL,
  old_standard INT          NOT NULL,
  new_standard INT          NOT NULL,
  remark       VARCHAR(512) NOT NULL,
  create_time  BIGINT       NOT NULL
);

CREATE  SEQUENCE  process_kill_id_seq START WITH 200000;
CREATE TABLE process_kill (
  id             BIGINT PRIMARY KEY DEFAULT  NEXTVAL('process_kill_id_seq'),
  process_id     BIGINT        NOT NULL,
  linux_account  VARCHAR(128)  NOT NULL,
  gpu_ip         VARCHAR(64)   NOT NULL,
  gpu_name       VARCHAR(64)   NOT NULL,
  reason         VARCHAR(512)  NOT NULL,
  creat_time     BIGINT        NOT NULL
);

ALTER TABLE public.abnormal_usage ALTER COLUMN fine TYPE FLOAT USING fine::FLOAT;
ALTER TABLE public.abnormal_usage ALTER COLUMN user_id DROP NOT NULL;
ALTER TABLE public.abnormal_usage ADD user_name VARCHAR(255) DEFAULT '' NOT NULL;
ALTER TABLE public.abnormal_usage ADD equ_ip VARCHAR(255) DEFAULT '' NOT NULL;
ALTER TABLE public.abnormal_usage ALTER COLUMN duration SET NOT NULL;
ALTER TABLE public.users ALTER COLUMN email TYPE VARCHAR(512) USING email::VARCHAR(512);

ALTER TABLE public.rent_order ADD linux_account VARCHAR(255) DEFAULT '' NOT NULL;
ALTER TABLE public.rent_order ADD email VARCHAR(255) DEFAULT '' NOT NULL;

CREATE SEQUENCE rent_record_id_seq START WITH 1000000;
CREATE TABLE rent_record (
  id         BIGINT PRIMARY KEY DEFAULT nextval('rent_record_id_seq'),
  user_id    BIGINT      NOT NULL,
  user_name  VARCHAR(255)   NOT NULL,
  equ_id     BIGINT      NOT NULL,
  start_time BIGINT      NOT NULL,
  end_time   BIGINT      NOT NULL,
  cost_coins FLOAT         NOT NULL,
  state      INT         NOT NULL DEFAULT 0,
  create_time BIGINT NOT NULL,
  return_coins FLOAT,
  equ_ip     VARCHAR(255)   NOT NULL,
  equ_name   VARCHAR(255)   NOT NULL,
  linux_account VARCHAR(255) DEFAULT '' NOT NULL,
  email VARCHAR(255) DEFAULT '' NOT NULL
);

alter table public.machine add priority Int default 1;
alter table public.gpu_equipment add memory Int default 64;

ALTER TABLE public.gpu_equipment ALTER COLUMN memory SET DEFAULT 12198;
ALTER TABLE public.gpu_equipment ALTER COLUMN memory SET NOT NULL;













