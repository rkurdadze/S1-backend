alter table orders add column if not exists first_name varchar(100);
alter table orders add column if not exists last_name varchar(100);
alter table orders add column if not exists address_line_1 varchar(500);
alter table orders add column if not exists address_line_2 varchar(500);
alter table orders add column if not exists municipality varchar(120);
alter table orders add column if not exists region varchar(120);
alter table orders add column if not exists country varchar(120);
