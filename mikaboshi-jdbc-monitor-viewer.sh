#!/bin/sh

cd `dirname $0`
CLASSPATH=conf
CLASSPATH=${CLASSPATH}:${JAVA_HOME}/lib/tools.jar

for jar in `ls lib/*.jar`; do
	CLASSPATH="${CLASSPATH}:$jar"
done

export CLASSPATH

java -Doracle.jdbc.mapDateToTimestamp=false net.mikaboshi.JdbcLogViewer $1 &
