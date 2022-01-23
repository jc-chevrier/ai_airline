#!/bin/bash

resourcesFolder="./src/main/resources/fr/ul/miage/ai_airline"
configurationFile="${resourcesFolder}/configuration/database.properties"

while IFS='=' read -r key value
do
  eval "${key}"="${value}"
done < "$configurationFile"
eval "${key}"="${value}"

export PGPASSWORD=$password
psql -h "$host" -p "$port" -U "$user" -d "$database" -c "$1"