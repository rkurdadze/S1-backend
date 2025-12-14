------------------------------------------------------------
-- ITEM PRICE
------------------------------------------------------------
alter table item add column if not exists price numeric(12,2) default 0 not null;

------------------------------------------------------------
-- ORDERS
------------------------------------------------------------
create table if not exists orders
(
    id bigserial primary key,
    user_id bigint references users,
    contact_name varchar(255),
    contact_phone varchar(50),
    contact_email varchar(255),
    contact_address varchar(500),
    contact_city varchar(120),
    contact_postal varchar(40),
    delivery_option varchar(120),
    notes varchar(1000),
    total numeric(12,2) not null,
    payment_token text,
    email_notification boolean default false,
    created_at timestamp with time zone default now() not null
);

create table if not exists order_items
(
    id bigserial primary key,
    order_id bigint not null references orders on delete cascade,
    item_id integer references item,
    color_id integer references colors,
    size_id integer references size,
    item_name varchar(200),
    color_name varchar(120),
    size_name varchar(120),
    quantity integer not null,
    price numeric(12,2) not null
);
