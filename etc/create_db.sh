# Usage: sh ./create_db.sh <locale> <password for initiative>
# NOTE: Set passwords in ~/.pgpass, e.g.
# localhost:5432:*:postgres:postgres
# localhost:5432:*:initiative:Eskim0

# Verify user dir
if [ ! -f schema/01_schema.sql ]
then
  echo "ERROR: schema/01_schema.sql not found in the working directory."
  exit 1
fi

# Check parameter count
if [ "$#" -eq 2 ]
then 
  DBPWD=$2
elif [ "$#" -eq 1 ]
then 
  read -p "Password for initiative: " DBPWD
else
  echo "USAGE: $0 <locale> <password for initiative>"
  exit 2
fi

# Confirm drop/create
read -p "Drop and create database (yes/no)? " confirmation
confirmation="$(echo ${confirmation} | tr 'A-Z' 'a-z')"

if [[ $confirmation =~ ^(y|yes)$ ]]
then
     echo "== create_db.sh =="
else
     echo "Aborted create_db.sh!"
     exit 3
fi


# Drop/create database and schema

export PGCLIENTENCODING="UTF8"

# Create database as superuser
psql -h localhost -U postgres <<EOF
DROP DATABASE IF EXISTS initdb;
DROP USER IF EXISTS initiative;

CREATE DATABASE initdb ENCODING 'UTF8' LC_COLLATE '$1' LC_CTYPE '$1' TEMPLATE template0;
CREATE USER initiative WITH PASSWORD '$DBPWD';
\q
EOF

# Create schema 
psql -h localhost -U postgres -d initdb <<EOF
CREATE SCHEMA initiative;
\q
EOF

# Execute schema files
export PGOPTIONS='--client-min-messages=warning --search-path=initiative'

ls schema/*.sql | sort -f |
  while read file
  do
    echo "-- $file"
    psql -h localhost -U postgres -d initdb --single-transaction -f "$file"
  done

# Grant required rights
psql -h localhost -U postgres -d initdb <<EOF
GRANT CONNECT, TEMP ON DATABASE initdb TO initiative;
GRANT USAGE ON SCHEMA initiative TO initiative;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA initiative TO initiative;
GRANT ALL ON ALL SEQUENCES IN SCHEMA  initiative TO initiative;
\q
EOF
