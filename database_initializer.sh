#!/bin/bash

resourcesFolder="./src/main/resources/fr/ul/miage/ai_airline"
configurationFile="${resourcesFolder}/configuration/database.properties"

SQLFolder="./src/main/sql/fr/ul/miage/ai_airline"
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