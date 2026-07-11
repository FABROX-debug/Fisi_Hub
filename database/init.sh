#!/bin/bash
# Wrapper script to import fisihub_dump.sql into the database.
# Strips psql meta-commands (\restrict, \unrestrict) that are not supported
# by older PostgreSQL versions.

set -e

DUMP_FILE="/docker-entrypoint-initdb.d/fisihub_dump.sql"

if [ -f "$DUMP_FILE" ]; then
  echo "Importing FISIHUB database dump..."
  sed '/^\\restrict/d;/^\\unrestrict/d' "$DUMP_FILE" | psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB"
  echo "FISIHUB database dump imported successfully."
else
  echo "WARNING: $DUMP_FILE not found. Skipping import."
fi
