#!/bin/bash
# wait-for-postgres.sh

set -e

host="$1"
shift
cmd="$@"

export PGPASSWORD="postgres"
until psql -h "$host" -U "postgres" -W -c '\l'; do
  >&2 echo "Postgres is unavailable - sleeping"
  sleep 1
done

>&2 echo "Postgres is up - executing command"
exec $cmd
