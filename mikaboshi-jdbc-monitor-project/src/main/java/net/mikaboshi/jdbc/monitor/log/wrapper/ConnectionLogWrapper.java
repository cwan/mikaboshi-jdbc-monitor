package net.mikaboshi.jdbc.monitor.log.wrapper;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

//import net.mikaboshi.jdbc.monitor.LogEntry;
//import net.mikaboshi.jdbc.monitor.LogMode;
//import net.mikaboshi.jdbc.monitor.LogModeMBean;
//import net.mikaboshi.jdbc.monitor.LogType;
//import net.mikaboshi.jdbc.monitor.LogWriter;
//import net.mikaboshi.jdbc.monitor.Result;

/**
 * JDBCログを取得するための {@link Connection} ラッパークラス。
 * 本来の処理は全てコンストラクタに指定した {@code Connection} オブジェクトに委譲する。
 *
 * @author Takuma Umezawa
 *
 */
public class ConnectionLogWrapper implements Connection {

	private Connection connection;

//	private LogModeMBean logMode = LogMode.getInstance();

	public ConnectionLogWrapper(Connection connection) {
		this.connection = connection;
	}

	public void clearWarnings() throws SQLException {
		this.connection.clearWarnings();
	}

	public void close() throws SQLException {
		this.connection.close();
//
//		if (!this.logMode.getConnectionCloseLoggable()) {
//			this.connection.close();
//			return;
//		}
//
//		LogEntry entry = new LogEntry(LogType.CONN_CLOSE);
//		entry.setConnection(this);
//
//		try {
//			if (LogMode.getInstance().getCallStackLevel() > 0) {
//				entry.setCallStack(Thread.currentThread().getStackTrace());
//			}
//
//			entry.start();
//			this.connection.close();
//			entry.stop();
//			entry.setResult(Result.SUCCESS);
//		} catch (SQLException e) {
//			entry.stop();
//			entry.setException(e);
//			throw e;
//		} finally {
//			LogWriter.put(entry);
//		}
	}

	public void commit() throws SQLException {
		this.connection.commit();
//		if (!this.logMode.getCommitLoggable()) {
//			this.connection.commit();
//			return;
//		}
//
//		LogEntry entry = new LogEntry(LogType.COMMIT);
//		entry.setConnection(this);
//
//		try {
//			if (LogMode.getInstance().getCallStackLevel() > 0) {
//				entry.setCallStack(Thread.currentThread().getStackTrace());
//			}
//
//			entry.start();
//			this.connection.commit();
//			entry.stop();
//			entry.setResult(Result.SUCCESS);
//		} catch (SQLException e) {
//			entry.stop();
//			entry.setException(e);
//			throw e;
//		} finally {
//			LogWriter.put(entry);
//		}
	}

	public Statement createStatement() throws SQLException {

		return new StatementWrapper(
				this.connection.createStatement(), this);
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {

		return new StatementWrapper(
				this.connection.createStatement(resultSetType, resultSetConcurrency),
				this);
	}

	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {

		return new StatementWrapper(
				this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability),
				this);
	}

	public boolean getAutoCommit() throws SQLException {
		return this.connection.getAutoCommit();
	}

	public String getCatalog() throws SQLException {
		return this.connection.getCatalog();
	}

	public int getHoldability() throws SQLException {
		return this.connection.getHoldability();
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return this.connection.getMetaData();
	}

	public int getTransactionIsolation() throws SQLException {
		return this.connection.getTransactionIsolation();
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return this.connection.getTypeMap();
	}

	public SQLWarning getWarnings() throws SQLException {
		return this.connection.getWarnings();
	}

	public boolean isClosed() throws SQLException {
		return this.connection.isClosed();
	}

	public boolean isReadOnly() throws SQLException {
		return this.connection.isReadOnly();
	}

	public String nativeSQL(String sql) throws SQLException {
		return this.connection.nativeSQL(sql);
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		return this.connection.prepareCall(sql);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {

		return this.connection.prepareStatement(sql);
//
//		if (!this.logMode.getPrepareStatementLoggable()) {
//			return this.connection.prepareStatement(sql);
//		}
//
//		LogEntry entry = new LogEntry(LogType.PREPARE_STMT);
//
//		try {
//			entry.setSql(sql);
//
//			if (LogMode.getInstance().getCallStackLevel() > 0) {
//				entry.setCallStack(Thread.currentThread().getStackTrace());
//			}
//
//			entry.start();
//			PreparedStatementLogWrapper pstmt = new PreparedStatementLogWrapper(
//					this.connection.prepareStatement(sql), this);
//			entry.stop();
//
//			pstmt.setSql(sql);
//			entry.setStatement(pstmt);
//			entry.setResult(Result.SUCCESS);
//
//			return pstmt;
//
//		} catch (SQLException e) {
//			entry.stop();
//			entry.setException(e);
//			throw e;
//		} finally {
//			LogWriter.put(entry);
//		}
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {

		return this.connection.prepareStatement(sql, autoGeneratedKeys);

//		if (!this.logMode.getPrepareStatementLoggable()) {
//			return this.connection.prepareStatement(sql, autoGeneratedKeys);
//		}
//
//		LogEntry entry = new LogEntry(LogType.PREPARE_STMT);
//
//		try {
//			entry.setSql(sql);
//
//			if (LogMode.getInstance().getCallStackLevel() > 0) {
//				entry.setCallStack(Thread.currentThread().getStackTrace());
//			}
//
//			entry.start();
//			PreparedStatementLogWrapper pstmt = new PreparedStatementLogWrapper(
//					this.connection.prepareStatement(sql, autoGeneratedKeys), this);
//			entry.stop();
//
//			pstmt.setSql(sql);
//			entry.setStatement(pstmt);
//			entry.setResult(Result.SUCCESS);
//
//			return pstmt;
//
//		} catch (SQLException e) {
//			entry.stop();
//			entry.setException(e);
//			throw e;
//		} finally {
//			LogWriter.put(entry);
//		}
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {

		return this.connection.prepareStatement(sql, columnIndexes);

//		if (!this.logMode.getPrepareStatementLoggable()) {
//			return this.connection.prepareStatement(sql, columnIndexes);
//		}
//
//		LogEntry entry = new LogEntry(LogType.PREPARE_STMT);
//
//		try {
//			entry.setSql(sql);
//
//			if (LogMode.getInstance().getCallStackLevel() > 0) {
//				entry.setCallStack(Thread.currentThread().getStackTrace());
//			}
//
//			entry.start();
//			PreparedStatementLogWrapper pstmt = new PreparedStatementLogWrapper(
//					this.connection.prepareStatement(sql, columnIndexes), this);
//			entry.stop();
//
//			pstmt.setSql(sql);
//			entry.setStatement(pstmt);
//			entry.setResult(Result.SUCCESS);
//
//			return pstmt;
//
//		} catch (SQLException e) {
//			entry.stop();
//			entry.setException(e);
//			throw e;
//		} finally {
//			LogWriter.put(entry);
//		}
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {

		return this.connection.prepareStatement(sql, columnNames);

//		if (!this.logMode.getPrepareStatementLoggable()) {
//			return this.connection.prepareStatement(sql, columnNames);
//		}
//
//		LogEntry entry = new LogEntry(LogType.PREPARE_STMT);
//
//		try {
//			entry.setSql(sql);
//
//			if (LogMode.getInstance().getCallStackLevel() > 0) {
//				entry.setCallStack(Thread.currentThread().getStackTrace());
//			}
//
//			entry.start();
//			PreparedStatementLogWrapper pstmt = new PreparedStatementLogWrapper(
//					this.connection.prepareStatement(sql, columnNames), this);
//			entry.stop();
//
//			pstmt.setSql(sql);
//			entry.setStatement(pstmt);
//			entry.setResult(Result.SUCCESS);
//
//			return pstmt;
//
//		} catch (SQLException e) {
//			entry.stop();
//			entry.setException(e);
//			throw e;
//		} finally {
//			LogWriter.put(entry);
//		}
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {

		return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
//
//		if (!this.logMode.getPrepareStatementLoggable()) {
//			return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
//		}
//
//		LogEntry entry = new LogEntry(LogType.PREPARE_STMT);
//
//		try {
//			entry.setSql(sql);
//
//			if (LogMode.getInstance().getCallStackLevel() > 0) {
//				entry.setCallStack(Thread.currentThread().getStackTrace());
//			}
//
//			entry.start();
//			PreparedStatementLogWrapper pstmt = new PreparedStatementLogWrapper(
//					this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency),
//					this);
//			entry.stop();
//
//			pstmt.setSql(sql);
//			entry.setStatement(pstmt);
//			entry.setResult(Result.SUCCESS);
//
//			return pstmt;
//
//		} catch (SQLException e) {
//			entry.stop();
//			entry.setException(e);
//			throw e;
//		} finally {
//			LogWriter.put(entry);
//		}
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {

		return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);

//		if (!this.logMode.getPrepareStatementLoggable()) {
//			return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
//		}
//
//		LogEntry entry = new LogEntry(LogType.PREPARE_STMT);
//
//		try {
//			entry.setSql(sql);
//
//			if (LogMode.getInstance().getCallStackLevel() > 0) {
//				entry.setCallStack(Thread.currentThread().getStackTrace());
//			}
//
//			entry.start();
//			PreparedStatementLogWrapper pstmt = new PreparedStatementLogWrapper(
//					this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability),
//					this);
//			entry.stop();
//
//			pstmt.setSql(sql);
//			entry.setStatement(pstmt);
//			entry.setResult(Result.SUCCESS);
//
//			return pstmt;
//
//		} catch (SQLException e) {
//			entry.stop();
//			entry.setException(e);
//			throw e;
//		} finally {
//			LogWriter.put(entry);
//		}
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		this.connection.releaseSavepoint(savepoint);
	}

	public void rollback() throws SQLException {
		this.connection.rollback();
//
//		LogEntry entry = new LogEntry(LogType.ROLLBACK);
//		entry.setConnection(this);
//
//		if (!this.logMode.getRollbackLoggable()) {
//			this.connection.rollback();
//			return;
//		}
//
//		try {
//			if (LogMode.getInstance().getCallStackLevel() > 0) {
//				entry.setCallStack(Thread.currentThread().getStackTrace());
//			}
//
//			entry.start();
//			this.connection.rollback();
//			entry.stop();
//			entry.setResult(Result.SUCCESS);
//
//		} catch (SQLException e) {
//			entry.stop();
//			entry.setException(e);
//			throw e;
//		} finally {
//			LogWriter.put(entry);
//		}
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		this.connection.rollback(savepoint);

//		if (!this.logMode.getRollbackLoggable()) {
//			this.connection.rollback(savepoint);
//			return;
//		}
//
//		LogEntry entry = new LogEntry(LogType.ROLLBACK);
//		entry.setConnection(this);
//
//		try {
//			if (LogMode.getInstance().getCallStackLevel() > 0) {
//				entry.setCallStack(Thread.currentThread().getStackTrace());
//			}
//
//			entry.start();
//			this.connection.rollback(savepoint);
//			entry.stop();
//			entry.setResult(Result.SUCCESS);
//
//		} catch (SQLException e) {
//			entry.stop();
//			entry.setException(e);
//			throw e;
//		} finally {
//			LogWriter.put(entry);
//		}
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.connection.setAutoCommit(autoCommit);
	}

	public void setCatalog(String catalog) throws SQLException {
		this.connection.setCatalog(catalog);
	}

	public void setHoldability(int holdability) throws SQLException {
		this.connection.setHoldability(holdability);
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		this.connection.setReadOnly(readOnly);
	}

	public Savepoint setSavepoint() throws SQLException {
		return this.connection.setSavepoint();
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		return this.connection.setSavepoint(name);
	}

	public void setTransactionIsolation(int level) throws SQLException {
		this.connection.setTransactionIsolation(level);
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		this.connection.setTypeMap(map);
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		return this.connection.createArrayOf(typeName, elements);
	}

	@Override
	public Blob createBlob() throws SQLException {
		return this.connection.createBlob();
	}

	@Override
	public Clob createClob() throws SQLException {
		return this.connection.createClob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return this.connection.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return this.connection.createSQLXML();
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		return this.connection.createStruct(typeName, attributes);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return this.connection.getClientInfo();
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return this.connection.getClientInfo(name);
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return this.connection.isValid(timeout);
	}

	@Override
	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		this.connection.setClientInfo(properties);
	}

	@Override
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		this.connection.setClientInfo(name, value);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.connection.isWrapperFor(iface);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this.connection.unwrap(iface);
	}
}

