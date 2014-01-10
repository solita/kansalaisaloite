# NOTE: Set passwords in ~/.pgpass, e.g.
# localhost:5432:*:postgres:postgres
# localhost:5432:*:initest:1nitest

if [ ! -f schema/01_schema.sql ]
then
  echo "schema/01_schema.sql not found. Execute this script in it's own folder!"
  exit 0
fi

echo "== create_test_schema.sh =="

export PGCLIENTENCODING="UTF8"

# Drop/create schema 
psql -h localhost -U postgres -d initdb <<EOF
DROP SCHEMA IF EXISTS initest CASCADE;
DROP USER IF EXISTS initest;

CREATE SCHEMA initest;
CREATE USER initest WITH PASSWORD '1nitest';
\q
EOF

# Create tables 
export PGOPTIONS='--client-min-messages=warning --search-path=initest'

ls schema/*.sql | sort -f |
  while read file
  do
    echo "-- $file"
    psql -h localhost -U postgres -d initdb --single-transaction -f "$file"
  done

# Grant required rights
psql -h localhost -U postgres -d initdb <<EOF
GRANT CONNECT, TEMP ON DATABASE initdb TO initest;
GRANT USAGE ON SCHEMA initest TO initest;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA initest TO initest;
GRANT ALL ON ALL SEQUENCES IN SCHEMA  initest TO initest;
\q
EOF
