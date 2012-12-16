package net.mikaboshi.jdbc.monitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import net.mikaboshi.jdbc.DbUtils;
import net.mikaboshi.jdbc.DmlExecutor;

public class HSQLDBFailTestx {

	public static void main(String[] args) throws Exception {
		Connection conn = null;
		try {
			conn = DbUtils.getConnection(
					"src/test/resources/jdbc_hsqldb_in-memory.properties");
			conn.setAutoCommit(false);
			new HSQLDBFailTestx().execute(conn);	
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}
	
	public void execute(Connection conn) throws Exception {
		
		try {
			insertP(conn);
			conn.commit();
			
		} catch (SQLException e) {
			DbUtils.rollbackQuietly(conn);
		}
		
		try {
			insertS(conn);
			conn.commit();
			
		} catch (SQLException e) {
			DbUtils.rollbackQuietly(conn);
		}
	}
	
	private void insertP(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement("insert into no_such_table values (?, ?)");
			
			DmlExecutor.execute(pstmt, new Object[] {1, "aaa"});
			
		} finally {
			DbUtils.closeQuietly(pstmt);
		}
	}
	
	private void insertS(Connection conn) throws SQLException {
		Statement stmt = null;
		
		try {
			stmt = conn.createStatement();
			stmt.execute("insert into no_such_table values(1, 'aaa')");
			
		} finally {
			DbUtils.closeQuietly(stmt);
		}
	}

}
