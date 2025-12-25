------------------------------------------------------------
-- DELIVERY INTEGRATION
------------------------------------------------------------
alter table if exists orders
    add column if not exists delivery_provider varchar(50),
    add column if not exists tracking_uuid varchar(36),
    add column if not exists tracking_code varchar(50),
    add column if not exists delivery_status varchar(50),
    add column if not exists delivery_label_url text,
    add column if not exists sender_delivery_external_id varchar(120),
    add column if not exists receiver_delivery_external_id varchar(120);
