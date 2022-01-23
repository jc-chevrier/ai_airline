@ECHO OFF

SET resourcesFolder=.\src\main\resources\fr\ul\miage\ai_airline
SET configurationFile=%resourcesFolder%\configuration\database.properties

FOR /F "tokens=1,2 delims==" %%G IN (%configurationFile%) DO (SET %%G=%%H)

SET PGPASSWORD=%password%
psql -h %host% -p %port% -U %user% -d %database% -c %1