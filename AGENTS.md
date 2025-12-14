# AGENTS Instructions

This repository currently has no other AGENTS files; these instructions apply to the entire project.

## Project Overview
- Java Spring Boot backend (see `pom.xml` and `mvnw`).
- Application entry point: `src/main/java/ge/studio101/service/S1ServiceApplication.java`.
- Configuration classes: `src/main/java/ge/studio101/service/configurations/` (e.g., `AppConfig`, `SecurityConfig`, `WebConfig`, `IpAddressFilter`, `NoCacheFilter`).
- REST controllers: `src/main/java/ge/studio101/service/controllers/` for authentication, items, colors, sizes, photos, inventory, and redirects.
- Services and helpers: `src/main/java/ge/studio101/service/services/` and `src/main/java/ge/studio101/service/helpers/ImageRoutines.java`.
- Data layer: `src/main/java/ge/studio101/service/models/` (entities) with corresponding DTOs, mappers, and repositories under `src/main/java/ge/studio101/service/dto/`, `mappers/`, and `repositories/`.
- Database migration: `src/main/resources/db/migration/V1__Initial_schema.sql`.
- Application properties: `src/main/resources/application.properties`.
- Tests: `src/test/java/ge/studio101/service/S1ServiceApplicationTests.java`.

## Schema Summary (migrations `V1__Initial_schema.sql`, `V2__Orders_and_pricing.sql`)
- Sequences: `color_id_seq`, `inventory_id_seq`, `item_id_seq`, `photo_id_seq`, `size_id_seq`, `user_role_id_seq`, `users_id_seq`.
- Tables:
  - `item`: `id` (PK, seq), `name` (varchar 200, not null), `description` (varchar 1000), `publish` (boolean, default true), `price` (numeric(12,2) not null, default 0).
  - `size`: `id` (PK, seq), `name` (varchar 20, not null).
  - `colors`: `id` (PK, seq), `item_id` (FK -> item cascade), `name` (varchar 40, not null).
  - `inventory`: `id` (PK, seq), `stock_count` (int, default 0), `color_id` (FK -> colors cascade), `size_id` (FK -> size, nullable).
  - `photo`: `id` (PK, seq), `image` (bytea, not null), `color_id` (FK -> colors cascade).
  - `user_role`: `id` (PK, seq), `name` (varchar 50, not null); seeded with Administrator (1), Manager (2), User (3) and sequence synced to max(id).
  - `users`: `id` (bigint PK, seq), `google_id` (unique, not null), `email` (unique, not null), `name` (nullable), `picture` (text), `role_id` (FK -> user_role, default 3), `image` (bytea).
  - `orders`: `id` (bigserial PK), `user_id` (FK -> users), embedded contact columns (`contact_*`), `delivery_option` (varchar 120), `notes` (varchar 1000), `total` (numeric(12,2) not null), `payment_token` (text), `email_notification` (boolean, default false), `created_at` (timestamptz, default now).
  - `order_items`: `id` (bigserial PK), `order_id` (FK -> orders cascade), `item_id` (FK -> item), `color_id` (FK -> colors), `size_id` (FK -> size), `item_name`/`color_name`/`size_name` (varchar), `quantity` (int not null), `price` (numeric(12,2) not null).

## File Structure Reference
All tracked project files (excluding Git metadata) as of this snapshot:
- Root: `Dockerfile`, `docker-compose.yml`, `pom.xml`, `HELP.md`, certificates/keys (`cert.pem`, `key.pem`, `keystore.p12`), Maven wrappers (`mvnw`, `mvnw.cmd`), `.gitignore`, `AGENTS.md`.
- `src/main/java/ge/studio101/service/`: application entry and package directories `configurations/`, `controllers/`, `dto/`, `helpers/`, `mappers/`, `models/`, `repositories/`, `services/`.
- `src/main/resources/`: `application.properties`, `db/migration/V1__Initial_schema.sql`.
- `src/test/java/ge/studio101/service/`: `S1ServiceApplicationTests.java`.

## Requirements for Future Changes
- Run the full test suite (`./mvnw test`) and include results before delivering a final answer.
- Follow existing package structure and naming conventions when adding classes or resources.
- Keep this file updated if project structure or schema changes.
- If any changes are needed for the frontend - generate AI prompt optimized for codex/gemini, describing all requirement and changes made.