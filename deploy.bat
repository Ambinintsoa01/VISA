@echo off
REM Script de deploiement pour Tomcat 10.1
REM =========================================

set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
set TOMCAT_HOME=C:\xampp\tomcat
set TOMCAT_WEBAPPS=%TOMCAT_HOME%\webapps
set WAR_NAME=visa.war
set SOURCE_WAR=target\visa-1.0.0.war

echo =========================================
echo    Deploiement sur Tomcat 10.1
echo =========================================

REM 1. Arreter Tomcat si en cours
echo.
echo [1/5] Arret de Tomcat...
call "%TOMCAT_HOME%\bin\shutdown.bat" 2>nul
timeout /t 5 /nobreak >nul

REM 2. Compiler et packager le WAR
echo.
echo [2/5] Compilation du projet...
call mvnw.cmd clean package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo ERREUR: La compilation a echoue!
    pause
    exit /b 1
)

REM 3. Supprimer l'ancien deploiement
echo.
echo [3/5] Suppression de l'ancien deploiement...
if exist "%TOMCAT_WEBAPPS%\%WAR_NAME%" del /f "%TOMCAT_WEBAPPS%\%WAR_NAME%"
if exist "%TOMCAT_WEBAPPS%\visa" rmdir /s /q "%TOMCAT_WEBAPPS%\visa"

REM 4. Copier le nouveau WAR
echo.
echo [4/5] Copie du WAR vers Tomcat...
copy /y "%SOURCE_WAR%" "%TOMCAT_WEBAPPS%\%WAR_NAME%"
if %ERRORLEVEL% neq 0 (
    echo ERREUR: Impossible de copier le WAR!
    pause
    exit /b 1
)

REM 5. Demarrer Tomcat
echo.
echo [5/5] Demarrage de Tomcat...
call "%TOMCAT_HOME%\bin\startup.bat"

echo.
echo =========================================
echo    Deploiement termine!
echo =========================================
echo.
echo URLs disponibles (attendre quelques secondes):
echo   - http://localhost:8080/visa/
echo   - http://localhost:8080/visa/api/personnes
echo   - http://localhost:8080/visa/api/personnes/search?nom=test
echo.
pause
