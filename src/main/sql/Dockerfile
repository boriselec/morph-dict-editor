FROM mariadb:10.4.12-bionic

COPY schema.sql /docker-entrypoint-initdb.d

ENV MYSQL_DATABASE morph

CMD ["--character-set-server=utf8mb4", "--collation-server=utf8mb4_general_ci", "--skip-character-set-client-handshake"]