#!/bin/bash

resourcesFolder="../"
configurationFile="${resourcesFolder}/configuration/database.properties"

SQLFolder="../"
databaseInitializerScript="${SQLFolder}/initialization/database_initializer.sql"
tablesInitializerScript="${SQLFolder}/initialization/tables_initializer.sql"
tablesBuilderScript="${SQLFolder}/initialization/tables_builder.sql"

while IFS='=' read -r key value
do
  eval "${key}"="${value}"
done < "$configurationFile"
eval "${key}"="${value}"

export PGPASSWORD=$password
psql -h "$host" -p "$port" -U "$user" -d "postgres" -q -v "database=$database" < "$databaseInitializerScript"
psql -h "$host" -p "$port" -U "$user" -d "$database" -q  < "$tablesInitializerScript"
psql -h "$host" -p "$port" -U "$user" -d "$database" -q < "$tablesBuilderScript"