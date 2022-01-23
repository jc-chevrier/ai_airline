@ECHO OFF

SET resourcesFolder=..\src\main\resources\fr\ul\miage\ai_airline
SET configurationFile=%resourcesFolder%\configuration\database.properties

SET SQLFolder=..\src\main\sql\fr\ul\miage\ai_airline
SET databaseInitializerScript=%SQLFolder%\initialization\database_initializer.sql
SET tablesInitializerScript=%SQLFolder%\initialization\tables_initializer.sql
SET tablesBuilderScript=%SQLFolder%\initialization\tables_builder.sql

FOR /F "tokens=1,2 delims==" %%G IN (%configurationFile%) DO (SET %%G=%%H)

SET PGPASSWORD=%password%
psql -h %host% -p %port% -U %user% -d postgres -q -v database=%database% < %databaseInitializerScript%
psql -h %host% -p %port% -U %user% -d %database% -q  < %tablesInitializerScript%
psql -h %host% -p %port% -U %user% -d %database% -q < %tablesBuilderScript%