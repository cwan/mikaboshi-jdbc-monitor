package net.mikaboshi.jdbc.monitor.log.aspect;

import java.sql.Connection;


import net.mikaboshi.jdbc.monitor.LogEntry;
import net.mikaboshi.jdbc.monitor.LogMode;
import net.mikaboshi.jdbc.monitor.LogModeMBean;
import net.mikaboshi.jdbc.monitor.LogType;
import net.mikaboshi.jdbc.monitor.LogWriter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * コネクションのopen/close, commit/rollback等をログに出力する。
 * @author Takuma Umezawa
 *
 */
@Aspect
public class ConnectionLogger {
	
	@Pointcut("execution(* java.sql.Driver+.connect(String, java.util.Properties)) || " +
			"execution(* javax.sql.DataSource+.getConnection()) || " +
			"execution(* java.sql.Driver+.getConnection(String, String))")
	public void connect() {}
	
	@Pointcut("execution(* java.sql.Connection+.close())")
	public void close() {}
	
	@Pointcut("execution(* java.sql.Connection+.commit())")
	public void commit() {}
	
	@Pointcut("execution(* java.sql.Connection+.rollback(..))")
	public void rollback() {}
	
	private LogModeMBean logMode = LogMode.getInstance();

	/**
	 * connectのログを複数回とらないようにチェックする
	 */
	private ThreadLocal<ConnectionLogger> connectMutex
		= new ThreadLocal<ConnectionLogger>();
	
	@Around("connect()")
	public Object logOpenConnection(
			ProceedingJoinPoint thisJoinPoint)
			throws Throwable {
		
		if (!this.logMode.getConnectionOpenLoggable() ||
				this.connectMutex.get() != null) {
			return thisJoinPoint.proceed();
		}
		
		LogEntry entry = new LogEntry(LogType.CONN_OPEN);
		
		try {
			this.connectMutex.set(this);
			
			Connection connection = (Connection) AspectUtils.proceedAndReturn(
					entry, thisJoinPoint);
			entry.setConnection(connection);
			return connection;
			
		} finally {
			LogWriter.put(entry);
			this.connectMutex.remove();
		}
	}
	
	/**
	 * closeのログを複数回とらないようにチェックする
	 */
	private ThreadLocal<ConnectionLogger> closeMutex
		= new ThreadLocal<ConnectionLogger>();
	
	@Around("close()")
	public void logCloseConnection(ProceedingJoinPoint thisJoinPoint) 
			throws Throwable {
		
		if (!this.logMode.getConnectionCloseLoggable() ||
				this.closeMutex.get() != null) {
			thisJoinPoint.proceed();
			return;
		}
		
		try {
			this.closeMutex.set(this);
			AspectUtils.proceed(LogType.CONN_CLOSE, thisJoinPoint);
			
		} finally {
			this.closeMutex.remove();
		}
	}
	
	/**
	 * commitのログを複数回とらないようにチェックする
	 */
	private ThreadLocal<ConnectionLogger> commitMutex
		= new ThreadLocal<ConnectionLogger>();
	
	@Around("commit()")
	public void logCommitConnection(ProceedingJoinPoint thisJoinPoint) 
			throws Throwable {
		
		if (!this.logMode.getCommitLoggable() ||
				this.commitMutex.get() != null) {
			thisJoinPoint.proceed();
			return;
		}
		
		try {
			this.commitMutex.set(this);
			AspectUtils.proceed(LogType.COMMIT, thisJoinPoint);
			
		} finally {
			this.commitMutex.remove();
		}
	}
	
	/**
	 * rollbackのログを複数回とらないようにチェックする
	 */
	private ThreadLocal<ConnectionLogger> rollbackMutex
		= new ThreadLocal<ConnectionLogger>();
	
	@Around("rollback()")
	public void logRollbackConnection(
			ProceedingJoinPoint thisJoinPoint) 
			throws Throwable {
		
		if (!this.logMode.getRollbackLoggable() ||
				this.rollbackMutex.get() != null) {
			thisJoinPoint.proceed();
			return;
		}
		
		try {
			this.rollbackMutex.set(this);
			AspectUtils.proceed(LogType.ROLLBACK, thisJoinPoint);
			
		} finally {
			this.rollbackMutex.remove();
		}
	}
}
