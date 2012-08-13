package net.mikaboshi.jdbc.monitor.log.wrapper;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * ポイントカットを設定するための {@link PreparedStatement} ラッパークラス。
 * 処理は全てコンストラクタに指定した {@code PreparedStatement} オブジェクトに委譲する。
 * 
 * @author Takuma Umezawa
 * @since 1.4.0
 */
public class PreparedStatementWrapper implements PreparedStatement {

	private Connection connection;
	private PreparedStatement preparedStatement;
	
	private SortedMap<Integer, Object> boundParameters =
			new TreeMap<Integer, Object>();
	
	private Integer savedUpdateCount = null;
	
	private String sql;
	
	/**
	 * このPreparedStatement設定されたパラメータを返す。
	 * @return
	 */
	public SortedMap<Integer, Object> getBoundParameters() {
		return this.boundParameters;
	}
	
	public PreparedStatementWrapper(
			PreparedStatement preparedStatement,
			Connection connection) {
		
		this.preparedStatement = preparedStatement;
		this.connection = connection;
	}
	
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public String getSql() {
		return this.sql;
	}
	
	@Override
	public void addBatch() throws SQLException {
		this.preparedStatement.addBatch();
	}

	@Override
	public void clearParameters() throws SQLException {
		this.preparedStatement.clearParameters();
		this.boundParameters = new TreeMap<Integer, Object>();
	}
	
	@Override
	public boolean execute() throws SQLException {
		this.savedUpdateCount = null;
		return this.preparedStatement.execute();
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		this.savedUpdateCount = null;
		return this.preparedStatement.executeQuery();
	}

	@Override
	public int executeUpdate() throws SQLException {
		this.savedUpdateCount = null;
		return this.preparedStatement.executeUpdate();
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return this.preparedStatement.getMetaData();
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return this.preparedStatement.getParameterMetaData();
	}

	@Override
	public void setArray(int i, Array x) throws SQLException {
		this.preparedStatement.setArray(i, x);
		this.boundParameters.put(i, x);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		this.preparedStatement.setAsciiStream(parameterIndex, x, length);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		this.preparedStatement.setBigDecimal(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		this.preparedStatement.setBinaryStream(parameterIndex, x, length);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setBlob(int i, Blob x) throws SQLException {
		this.preparedStatement.setBlob(i, x);
		this.boundParameters.put(i, x);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		this.preparedStatement.setBoolean(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		this.preparedStatement.setByte(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		this.preparedStatement.setBytes(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		this.preparedStatement.setCharacterStream(parameterIndex, reader, length);
		this.boundParameters.put(parameterIndex, reader);
	}

	@Override
	public void setClob(int i, Clob x) throws SQLException {
		this.preparedStatement.setClob(i, x);
		this.boundParameters.put(i, x);
	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		this.preparedStatement.setDate(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		this.preparedStatement.setDate(parameterIndex, x, cal);
		
		cal.setTime(x);
		this.boundParameters.put(parameterIndex, new Date(cal.getTimeInMillis()));
	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		this.preparedStatement.setDouble(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		this.preparedStatement.setFloat(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		this.preparedStatement.setInt(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		this.preparedStatement.setLong(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		this.preparedStatement.setNull(parameterIndex, sqlType);
		this.boundParameters.put(parameterIndex, null);
	}

	@Override
	public void setNull(int paramIndex, int sqlType, String typeName)
			throws SQLException {
		this.preparedStatement.setNull(paramIndex, sqlType, typeName);
		this.boundParameters.put(paramIndex, null);
	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		this.preparedStatement.setObject(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		this.preparedStatement.setObject(parameterIndex, x, targetSqlType);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scale) throws SQLException {
		this.preparedStatement.setObject(parameterIndex, x, targetSqlType, scale);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setRef(int i, Ref x) throws SQLException {
		this.preparedStatement.setRef(i, x);
		this.boundParameters.put(i, x);
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		this.preparedStatement.setShort(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		this.preparedStatement.setString(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		this.preparedStatement.setTime(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		this.preparedStatement.setTime(parameterIndex, x, cal);
		
		cal.setTime(x);
		this.boundParameters.put(parameterIndex, new Time(cal.getTimeInMillis()));
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		this.preparedStatement.setTimestamp(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}
	
	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		this.preparedStatement.setTimestamp(parameterIndex, x, cal);
		
		cal.setTime(x);
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		timestamp.setNanos(x.getNanos());
		
		this.boundParameters.put(parameterIndex, timestamp);
	}

	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		this.preparedStatement.setURL(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Deprecated
	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		this.preparedStatement.setUnicodeStream(parameterIndex, x, length);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		this.preparedStatement.addBatch(sql);
	}

	@Override
	public void cancel() throws SQLException {
		this.preparedStatement.cancel();
	}

	@Override
	public void clearBatch() throws SQLException {
		this.preparedStatement.clearBatch();
	}

	@Override
	public void clearWarnings() throws SQLException {
		this.preparedStatement.clearWarnings();
	}

	@Override
	public void close() throws SQLException {
		this.preparedStatement.close();
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		this.savedUpdateCount = null;
		return this.preparedStatement.execute(sql);
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		this.savedUpdateCount = null;
		return this.preparedStatement.execute(sql, autoGeneratedKeys);
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		this.savedUpdateCount = null;
		return this.preparedStatement.execute(sql, columnIndexes);
	}

	@Override
	public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		this.savedUpdateCount = null;
		return this.preparedStatement.execute(sql, columnNames);
	}

	@Override
	public int[] executeBatch() throws SQLException {
		this.savedUpdateCount = null;
		return this.preparedStatement.executeBatch();
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		this.savedUpdateCount = null;
		return this.preparedStatement.executeQuery(sql);
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		this.savedUpdateCount = null;
		return this.preparedStatement.executeUpdate(sql);
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
		this.savedUpdateCount = null;
		return this.preparedStatement.executeUpdate(sql, autoGeneratedKeys);
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		this.savedUpdateCount = null;
		return this.preparedStatement.executeUpdate(sql, columnIndexes);
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
		this.savedUpdateCount = null;
		return this.preparedStatement.executeUpdate(sql, columnNames);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return this.connection;
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return this.preparedStatement.getFetchDirection();
	}

	@Override
	public int getFetchSize() throws SQLException {
		return this.preparedStatement.getFetchSize();
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return this.preparedStatement.getGeneratedKeys();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return this.preparedStatement.getMaxFieldSize();
	}

	@Override
	public int getMaxRows() throws SQLException {
		return this.preparedStatement.getMaxRows();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return this.preparedStatement.getMoreResults();
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return this.preparedStatement.getMoreResults(current);
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return this.preparedStatement.getQueryTimeout();
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return this.preparedStatement.getResultSet();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return this.preparedStatement.getResultSetConcurrency();
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return this.preparedStatement.getResultSetHoldability();
	}

	@Override
	public int getResultSetType() throws SQLException {
		return this.preparedStatement.getResultSetType();
	}
	
	@Override
	public int getUpdateCount() throws SQLException {
		if (this.savedUpdateCount != null) {
			return this.savedUpdateCount;
		}
		
		this.savedUpdateCount = this.preparedStatement.getUpdateCount();
		return this.savedUpdateCount;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return this.preparedStatement.getWarnings();
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		this.preparedStatement.setCursorName(name);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		this.preparedStatement.setEscapeProcessing(enable);
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		this.preparedStatement.setFetchDirection(direction);
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		this.preparedStatement.setFetchSize(rows);
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		this.preparedStatement.setMaxFieldSize(max);
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		this.preparedStatement.setMaxRows(max);
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		this.preparedStatement.setQueryTimeout(seconds);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		this.preparedStatement.setAsciiStream(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		this.preparedStatement.setAsciiStream(parameterIndex, x, length);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		this.preparedStatement.setBinaryStream(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		this.preparedStatement.setBinaryStream(parameterIndex, x, length);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		this.preparedStatement.setBlob(parameterIndex, inputStream);
		this.boundParameters.put(parameterIndex, inputStream);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		this.preparedStatement.setBlob(parameterIndex, inputStream, length);
		this.boundParameters.put(parameterIndex, inputStream);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		this.preparedStatement.setCharacterStream(parameterIndex, reader);
		this.boundParameters.put(parameterIndex, reader);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		this.preparedStatement.setCharacterStream(parameterIndex, reader, length);
		this.boundParameters.put(parameterIndex, reader);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		this.preparedStatement.setClob(parameterIndex, reader);
		this.boundParameters.put(parameterIndex, reader);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		this.preparedStatement.setClob(parameterIndex, reader, length);
		this.boundParameters.put(parameterIndex, reader);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		this.preparedStatement.setNCharacterStream(parameterIndex, value);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		this.setNCharacterStream(parameterIndex, value);
		this.boundParameters.put(parameterIndex, value);
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		this.preparedStatement.setNClob(parameterIndex, value);
		this.boundParameters.put(parameterIndex, value);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		this.preparedStatement.setNClob(parameterIndex, reader);
		this.boundParameters.put(parameterIndex, reader);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		this.preparedStatement.setNClob(parameterIndex, reader, length);
		this.boundParameters.put(parameterIndex, reader);
	}

	@Override
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		this.preparedStatement.setNString(parameterIndex, value);
		this.boundParameters.put(parameterIndex, value);
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		this.preparedStatement.setRowId(parameterIndex, x);
		this.boundParameters.put(parameterIndex, x);
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		this.preparedStatement.setSQLXML(parameterIndex, xmlObject);
		this.boundParameters.put(parameterIndex, xmlObject);
	}

	@Override
	public boolean isClosed() throws SQLException {
		return this.preparedStatement.isClosed();
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return this.preparedStatement.isPoolable();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		this.preparedStatement.setPoolable(poolable);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.preparedStatement.isWrapperFor(iface);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this.preparedStatement.unwrap(iface);
	}
}
