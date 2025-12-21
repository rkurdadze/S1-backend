## Guides
The following guides illustrate how to use some features concretely:

### To put in container / online:
#### `Maven CLEAN`

#### `Maven INSTALL`

#### if container creation locally - must have docker-compose.yml and putted like this:
#### `docker-compose up`  

#
### Without DockerHub
```bash
  docker build -t s1service:v1 .
```

```bash
  docker save -o s1service:v1.tar s1service:v1
```

* use ftp to transfer to the server, then:

```bash
    docker load -i /var/www/s1service\:v1.tar
```

special symbol in name must be considered, otherwise it wil not find it

## Запуск сервиса через Docker

```bash
  docker run -d \
  -v /var/www/studio101/cache:/var/www/studio101/cache \
  -e IMAGE_CACHE_DIR=/var/www/studio101/cache \
  -e "SPRING_DATASOURCE_URL=jdbc:postgresql://studio101.ge:5432/s1?currentSchema=public&charSet=UTF8" \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=saadmin \
  -e SERVER_SSL_ENABLED=true \
  -e SERVER_SSL_KEY_STORE=/app/keystore.p12 \
  -e SERVER_SSL_KEY_STORE_PASSWORD=123456 \
  -e SERVER_SSL_KEY_STORE_TYPE=PKCS12 \
  -p 4444:4444 \
  --restart always \
  --name s1service \
  s1service:v1
```

* To run in production, set the `IMAGE_CACHE_DIR` environment variable to `/var/www/studio101/cache` and mount a volume at that same path using the `-v` flag. This ensures cached images are written to the correct directory inside the container and persisted on the host.
* For local development, if the `IMAGE_CACHE_DIR` environment variable is not set, the application will default to using `/var/tmp/s1/` for caching.



#### `docker logs -f s1service`



#
#
# DB transfer to remote

1. at local docker:
#### `docker exec -t postgis pg_dump -U postgres -d s1 > s1_backup.sql`

2. on remote:
#### `docker cp /var/www/s1_backup.sql postgres-container:/s1_backup.sql`

3. on remote as well:
#### `docker exec -t postgres-container psql -U postgres -d s1 -f /s1_backup.sql`



#
#
# CERTIFICATES
### create certificates:
#### `sudo certbot --apache -d studio101.ge -d www.studio101.ge`

### renew certificates:
#### `sudo certbot renew`


### prepare:
#### `cp /etc/letsencrypt/live/studio101.ge/fullchain.pem /var/www/cert.pem`
#### `cp /etc/letsencrypt/live/studio101.ge/privkey.pem /var/www/key.pem`
#### `openssl pkcs12 -export -out /var/www/keystore.p12 -inkey /etc/letsencrypt/live/studio101.ge/privkey.pem -in /etc/letsencrypt/live/studio101.ge/fullchain.pem -certfile /etc/letsencrypt/live/studio101.ge/fullchain.pem -password pass:123456`

### before copy to local:
#### `sudo chmod 644 /var/www/cert.pem`
#### `sudo chmod 644 /var/www/key.pem`
#### `sudo chmod 644 /var/www/keystore.p12`