package net.mikaboshi.jdbc.monitor.log.aspect;

import java.sql.PreparedStatement;

import net.mikaboshi.jdbc.monitor.LogEntry;
import net.mikaboshi.jdbc.monitor.LogMode;
import net.mikaboshi.jdbc.monitor.LogModeMBean;
import net.mikaboshi.jdbc.monitor.LogType;
import net.mikaboshi.jdbc.monitor.LogWriter;
import net.mikaboshi.jdbc.monitor.Result;
import net.mikaboshi.jdbc.monitor.SqlUtils;
import net.mikaboshi.jdbc.monitor.log.wrapper.PreparedStatementWrapper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * SQL(Statement, PreparedStatement)の実行をログに出力する。
 *
 * @author Takuma Umezawa
 */
@Aspect
public class StatementLogger {

	@Pointcut("call(* java.sql.Connection+.prepareStatement(..))")
	public void prepareStatement() {}

	// execute(), executeQuery(), executeUpdate()
	@Pointcut("execution(* java.sql.PreparedStatement+.execute()) || " +
			"execution(* java.sql.PreparedStatement+.executeQuery()) || " +
			"execution(* java.sql.PreparedStatement+.executeUpdate())")
	public void executePreparedStatement() {}

	// execute(String sql)等、引数があるもの
	@Pointcut("execution(* java.sql.Statement+.execute(String)) || " +
			"execution(* java.sql.Statement+.execute(String, int)) || " +
			"execution(* java.sql.Statement+.execute(String, int[])) || " +
			"execution(* java.sql.Statement+.execute(String, String[])) || " +
			"execution(* java.sql.Statement+.executeQuery(String)) || " +
			"execution(* java.sql.Statement+.executeUpdate(String)) || " +
			"execution(* java.sql.Statement+.executeUpdate(String, int)) || " +
			"execution(* java.sql.Statement+.executeUpdate(String, int[])) || " +
			"execution(* java.sql.Statement+.executeUpdate(String, String[]))")
	public void executeStatement() {}

	@Pointcut("execution(void java.sql.Statement+.addBatch(String)) && args(sql)")
	public void addBatch(String sql) {}

	@Pointcut("execution(void java.sql.PreparedStatement+.addBatch())")
	public void addPreparedBatch() {}

	@Pointcut("execution(int[] java.sql.Statement+.executeBatch())")
	public void executeBatch() {}

	@Pointcut("execution(void java.sql.Statement+.close())")
	public void close() {}

	private LogModeMBean logMode = LogMode.getInstance();

	/**
	 * prepareStatementのログを複数回とらないようにチェックする
	 */
	private ThreadLocal<StatementLogger> prepareStatementMutex
		= new ThreadLocal<StatementLogger>();

	@Around("prepareStatement()")
	public Object logPrepareStatement(
			ProceedingJoinPoint thisJoinPoint)
			throws Throwable {

		if (!this.logMode.getPrepareStatementLoggable() ||
				this.prepareStatementMutex.get() != null) {
			return thisJoinPoint.proceed();
		}

		Object[] args = thisJoinPoint.getArgs();

		if (args == null || args.length == 0) {
			return thisJoinPoint.proceed();
		}

		LogEntry entry = new LogEntry(LogType.PREPARE_STMT);

		try {
			this.prepareStatementMutex.set(this);

			entry.setSql((String) args[0]);

			// Connection#prepareStatement実行
			PreparedStatement result
				= (PreparedStatement) AspectUtils.proceedAndReturn(entry, thisJoinPoint);

			PreparedStatementWrapper pstmt =
					new PreparedStatementWrapper(result, result.getConnection());

			pstmt.setSql(entry.getSql());
			entry.setStatement(pstmt);
			entry.setResult(Result.SUCCESS);
			return pstmt;

		} finally {
			LogWriter.put(entry);
			this.prepareStatementMutex.remove();
		}
	}

	/**
	 * executePreparedStatementのログを複数回とらないようにチェックする
	 */
	private ThreadLocal<StatementLogger> executePreparedStatementMutex
		= new ThreadLocal<StatementLogger>();

	@Around("executePreparedStatement()")
	public Object logExecutePreparedStatement(
			ProceedingJoinPoint thisJoinPoint) throws Throwable {

		if (!this.logMode.getExecutePreparedStatementLoggable() ||
				this.executePreparedStatementMutex.get() != null) {
			return thisJoinPoint.proceed();
		}

		if ( !(thisJoinPoint.getTarget() instanceof PreparedStatementWrapper) ||
				(thisJoinPoint.getArgs() != null && thisJoinPoint.getArgs().length > 0)) {
			return thisJoinPoint.proceed();
		}

		LogEntry entry = new LogEntry(LogType.EXE_PSTMT);

		try {
			this.executePreparedStatementMutex.set(this);

			PreparedStatementWrapper pstmt =
				(PreparedStatementWrapper) thisJoinPoint.getTarget();

			// パラメータを?に代入する
			entry.setSql( SqlUtils.replaceParameters(
					pstmt.getSql(), pstmt.getBoundParameters()) );

			// PreparedStatement#execute*()実行
			Object result = AspectUtils.proceedAndReturn(entry, thisJoinPoint);

			if (result instanceof Integer) {
				entry.setAffectedRows((Integer) result);
			}

			if (result instanceof Boolean && !((Boolean) result).booleanValue()) {
				// PreparedStatement#execute()がfalseの場合
				entry.setAffectedRows(pstmt.getUpdateCount());
			}

			entry.setResult(Result.SUCCESS);

			return result;

		} finally {
			LogWriter.put(entry);
			this.executePreparedStatementMutex.remove();
		}
	}

	/**
	 * executeStatementのログを複数回とらないようにチェックする
	 */
	private ThreadLocal<StatementLogger> executeStatementMutex
		= new ThreadLocal<StatementLogger>();

	@Around("executeStatement()")
	public Object logExecuteStatement(
			ProceedingJoinPoint thisJoinPoint) throws Throwable {

		if (!this.logMode.getExecuteStatementLoggable() ||
				this.executeStatementMutex.get() != null) {
			return thisJoinPoint.proceed();
		}

		Object[] args = thisJoinPoint.getArgs();

		if (args == null || args.length == 0) {
			return thisJoinPoint.proceed();
		}

		LogEntry entry = new LogEntry(LogType.EXE_STMT);

		try {
			this.executeStatementMutex.set(this);

			entry.setSql((String) args[0]);

			return AspectUtils.proceedAndReturn(entry, thisJoinPoint);

		} finally {
			LogWriter.put(entry);
			this.executeStatementMutex.remove();
		}
	}

	/**
	 * addBatchのログを複数回とらないようにチェックする
	 */
	private ThreadLocal<StatementLogger> addBatchMutex
		= new ThreadLocal<StatementLogger>();

	@Around("addBatch(String) && args(sql)")
	public Object logAddBatch(
			String sql, ProceedingJoinPoint thisJoinPoint) throws Throwable {

		if (!this.logMode.getAddBatchLoggable() ||
				this.addBatchMutex.get() != null) {
			return thisJoinPoint.proceed();
		}

		LogEntry entry = new LogEntry(LogType.ADD_BATCH);

		try {
			this.addBatchMutex.set(this);
			entry.setSql(sql);
			return AspectUtils.proceedAndReturn(entry, thisJoinPoint);

		} finally {
			LogWriter.put(entry);
			this.addBatchMutex.remove();
		}
	}

	/**
	 * addBatchのログを複数回とらないようにチェックする
	 */
	private ThreadLocal<StatementLogger> addPreparedBatchMutex
		= new ThreadLocal<StatementLogger>();

	@Around("addPreparedBatch()")
	public Object logAddPreparedBatch(ProceedingJoinPoint thisJoinPoint) throws Throwable {

		if (!this.logMode.getAddBatchLoggable() ||
				this.addBatchMutex.get() != null) {
			return thisJoinPoint.proceed();
		}

		LogEntry entry = new LogEntry(LogType.ADD_BATCH);

		try {
			this.addPreparedBatchMutex.set(this);

			PreparedStatementWrapper pstmt =
					(PreparedStatementWrapper) thisJoinPoint.getTarget();

			// パラメータを?に代入する
			entry.setSql( SqlUtils.replaceParameters(
					pstmt.getSql(), pstmt.getBoundParameters()) );

			return AspectUtils.proceedAndReturn(entry, thisJoinPoint);

		} finally {
			LogWriter.put(entry);
			this.addPreparedBatchMutex.remove();
		}
	}

	/**
	 * executeBatchのログを複数回とらないようにチェックする
	 */
	private ThreadLocal<StatementLogger> executeBatchMutex
		= new ThreadLocal<StatementLogger>();

	@Around("executeBatch()")
	public int[] logExecuteBatch(
			ProceedingJoinPoint thisJoinPoint) throws Throwable {

		if (!this.logMode.getExecuteBatchLoggable() ||
				this.executeBatchMutex.get() != null) {
			return (int[]) thisJoinPoint.proceed();
		}

		LogEntry entry = new LogEntry(LogType.EXE_BATCH);

		try {
			this.executeBatchMutex.set(this);

			int[] result =
				(int[]) AspectUtils.proceedAndReturn(entry, thisJoinPoint);

			int count = 0;
			for (int n : result) {
				if (n > 0) {
					count += n;
				}
			}

			entry.setAffectedRows(count);

			return result;

		} finally {
			LogWriter.put(entry);
			this.executeBatchMutex.remove();
		}
	}

	/**
	 * Statementクローズのログを複数回とらないようにチェックする
	 */
	private ThreadLocal<StatementLogger> closeStatementMutex
		= new ThreadLocal<StatementLogger>();

	@Around("close()")
	public void logClose(ProceedingJoinPoint thisJoinPoint) throws Throwable {

		if (!this.logMode.getCloseStatementLoggable() ||
				this.closeStatementMutex.get() != null) {
			thisJoinPoint.proceed();
			return;
		}

		try {
			this.closeStatementMutex.set(this);
			AspectUtils.proceed(LogType.CLOSE_STMT, thisJoinPoint);

		} finally {
			this.closeStatementMutex.remove();
		}
	}
}
