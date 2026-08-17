-- Run this file as a PostgreSQL superuser with psql:
--   psql -U postgres -f database/create_database.sql
--
-- The \gexec commands make role and database creation idempotent. Change the
-- development password before using the script outside a local environment.

SELECT 'CREATE ROLE smartcare LOGIN PASSWORD ''smartcare'''
WHERE NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'smartcare') \gexec

SELECT 'CREATE DATABASE smartcare OWNER smartcare ENCODING ''UTF8'''
WHERE NOT EXISTS (SELECT FROM pg_catalog.pg_database WHERE datname = 'smartcare') \gexec

\connect smartcare

GRANT ALL ON SCHEMA public TO smartcare;
