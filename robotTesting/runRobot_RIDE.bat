set WORKING_DIRECTORY=%cd%
set ROOT_PATH=%WORKING_DIRECTORY%\execution
REM [ARGUMENTS]
set COMMAND_ARGUMENTS=%*
echo %COMMAND_ARGUMENTS%
REM [RF]
set RF_JAR=%ROOT_PATH%\robotframework.jar
REM [LIB]
set LIB_ROOT=%ROOT_PATH%\Lib
set LIB_PATH=%LIB_ROOT%\mysql-connector-java-5.0.8-bin.jar
REM [JAVA] check if JAVA installed
@echo JAVA_HOME="%JAVA_HOME%"
IF "%JAVA_HOME%"=="" (set JAVA_BIN=%ROOT_PATH%\portable-robotframework-win\jre\bin\java
) ELSE (set JAVA_BIN="%JAVA_HOME%\bin\java")
@echo JAVA_BIN="%JAVA_BIN%"
set JAVA_CLASSPATH=/a:%LIB_PATH%

%JAVA_BIN% -Xbootclasspath%JAVA_CLASSPATH% -jar %RF_JAR% run %COMMAND_ARGUMENTS%