@echo off

setlocal EnableDelayedExpansion

set CLASSPATH=conf
set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar

for %%f in ("%~dp0\lib\*.jar") do (
	set CLASSPATH=!CLASSPATH!;%%f
)

start javaw net.mikaboshi.JdbcLogViewer %1
