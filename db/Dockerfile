FROM postgres:11.2
ENV POSTGRES_DB db
COPY schema_init.sql /docker-entrypoint-initdb.d/
