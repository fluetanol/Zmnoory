#!/bin/bash
set -e
set -x # 실행되는 명령어를 로그에 출력합니다.

echo "### Custom init script is now running ###"

echo "Listing temporary config files in /tmp/postgres_conf..."
ls -l /tmp/postgres_conf

echo "Copying custom config files to /var/lib/postgresql/data..."
cp /tmp/postgres_conf/postgresql.conf /var/lib/postgresql/data/postgresql.conf
cp /tmp/postgres_conf/pg_hba.conf /var/lib/postgresql/data/pg_hba.conf

echo "### Custom init script has finished. ###"
