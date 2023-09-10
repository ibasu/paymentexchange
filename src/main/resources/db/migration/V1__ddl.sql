create table IF NOT EXISTS WEX_TRANSACTION_MASTER(
 transaction_id varchar(37) PRIMARY KEY not null,
 transaction_description varchar(50) not null,
 transaction_date timestamp not null,
 transaction_amount number(15,2) not null,
 transaction_original_currency_code varchar(5) not null,
 created_user varchar(100) not null,
 last_updated_user varchar(100),
 created_date timestamp not null,
 last_updated_date timestamp,
 version integer
);