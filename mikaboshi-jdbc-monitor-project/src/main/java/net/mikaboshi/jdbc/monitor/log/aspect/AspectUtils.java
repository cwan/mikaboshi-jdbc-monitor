package net.mikaboshi.jdbc.monitor.log.aspect;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


import net.mikaboshi.jdbc.monitor.LogEntry;
import net.mikaboshi.jdbc.monitor.LogMode;
import net.mikaboshi.jdbc.monitor.LogType;
import net.mikaboshi.jdbc.monitor.LogWriter;
import net.mikaboshi.jdbc.monitor.Result;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 *
 * @author Takuma Umezawa
 *
 */
public class AspectUtils {
	private AspectUtils() {}

	public static void proceed(LogEntry entry,
			ProceedingJoinPoint thisJoinPoint)
			throws Throwable {

		setStatementOrConnection(entry, thisJoinPoint);

		try {
			if (LogMode.getInstance().getCallStackLevel() > 0) {
				entry.setCallStack(Thread.currentThread().getStackTrace());
			}
			entry.start();
			thisJoinPoint.proceed();
			entry.stop();
			entry.setResult(Result.SUCCESS);
		} catch (SQLException e) {
			entry.stop();
			entry.setException(e);
			throw e;
		}
	}

	public static void proceed(LogType logType,
			ProceedingJoinPoint thisJoinPoint)
			throws Throwable {

		LogEntry entry = new LogEntry(logType);
		proceed(entry, thisJoinPoint);
		LogWriter.put(entry);
	}

	public static Object proceedAndReturn(LogEntry entry,
			ProceedingJoinPoint thisJoinPoint)
			throws Throwable {

		setStatementOrConnection(entry, thisJoinPoint);

		try {
			if (LogMode.getInstance().getCallStackLevel() > 0) {
				entry.setCallStack(Thread.currentThread().getStackTrace());
			}
			entry.start();
			Object result = thisJoinPoint.proceed();
			entry.stop();

			entry.setResult(Result.SUCCESS);
			return result;

		} catch (SQLException e) {
			entry.stop();
			entry.setException(e);
			throw e;
		}
	}

	public static Object proceedAndReturn(LogType logType,
			ProceedingJoinPoint thisJoinPoint)
			throws Throwable {

		LogEntry entry = new LogEntry(logType);
		Object result = proceedAndReturn(entry, thisJoinPoint);
		LogWriter.put(entry);
		return result;
	}

	private static void setStatementOrConnection(
			LogEntry entry, ProceedingJoinPoint thisJoinPoint) {

		if (thisJoinPoint.getTarget() instanceof Statement) {
			entry.setStatement((Statement) thisJoinPoint.getTarget());
		} else if (thisJoinPoint.getTarget() instanceof Connection) {
			entry.setConnection((Connection) thisJoinPoint.getTarget());
		} else {}
	}
}
