CREATE TABLE IF NOT EXISTS delivery_service_settings
(
    service varchar(50) PRIMARY KEY,
    enabled boolean      NOT NULL DEFAULT true,
    label   varchar(200) NOT NULL
);

INSERT INTO delivery_service_settings (service, enabled, label)
VALUES ('INTERNAL', true, 'Внутренняя доставка')
ON CONFLICT DO NOTHING;

INSERT INTO delivery_service_settings (service, enabled, label)
VALUES ('TRACKINGS_GE', false, 'Trackings.ge')
ON CONFLICT DO NOTHING;
