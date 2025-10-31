@echo off
echo Starting Spring Boot Application Manually...
echo.

REM Set Java classpath manually
set CLASSPATH=target\classes
set CLASSPATH=%CLASSPATH%;src\main\resources

REM Add all JAR dependencies (you'd need to list all JARs from Maven dependencies)
echo Setting up classpath...

REM Set Spring profile
set SPRING_PROFILES_ACTIVE=postgresql

echo Starting application...
java -cp "%CLASSPATH%" -Dspring.profiles.active=%SPRING_PROFILES_ACTIVE% com.playschool.management.JrTransportManagementApplication

echo.
echo Application stopped.
pause