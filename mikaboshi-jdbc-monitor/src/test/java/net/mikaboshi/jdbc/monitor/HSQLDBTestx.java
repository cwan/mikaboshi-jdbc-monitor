package net.mikaboshi.jdbc.monitor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import net.mikaboshi.jdbc.DbUtils;
import net.mikaboshi.jdbc.DmlExecutor;
import net.mikaboshi.jdbc.QueryExecutor;

public class HSQLDBTestx {

	private Connection conn;
	
	public static void main(String[] args) throws Exception {
		new HSQLDBTestx().execute();
	}
	
	public void execute() throws Exception {
		try {
			this.conn = DbUtils.getConnection(
					"src/test/resources/jdbc_hsqldb_in-memory.properties");
			this.conn.setAutoCommit(false);

			createTable();
			insert();
			insert2();
			update();
			executeBatch();
			query();
			
			this.conn.commit();
			
		}catch (SQLException e) {
			DbUtils.rollbackQuietly(this.conn);
			
		} finally {
			DbUtils.closeQuietly(this.conn);
		}
	}
	
	private void createTable() throws SQLException, IOException {
		DmlExecutor.execute(this.conn, 
			"create table jdbc_aop (id integer primary key, name varchar)");
	}
	
	private void insert() throws SQLException {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = this.conn.prepareStatement("insert into jdbc_aop values (?, ?)");
			
			DmlExecutor.execute(pstmt, new Object[] {1, "aaa"});
			DmlExecutor.execute(pstmt, new Object[] {2, "bbb"});
			DmlExecutor.execute(pstmt, new Object[] {3, "ああ'あ"});
			
		} finally {
			DbUtils.closeQuietly(pstmt);
		}
	}
	
	private void update() throws SQLException {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = this.conn.prepareStatement("update jdbc_aop set name = ? where id = ?");
			
			DmlExecutor.execute(pstmt, new Object[] {"BBB", 2});
			
		} finally {
			DbUtils.closeQuietly(pstmt);
		}
	}
	
	private void insert2() throws SQLException {
		Statement stmt = null;
		
		try {
			stmt = this.conn.createStatement();
			stmt.executeUpdate("insert into jdbc_aop values (4, 'abc''drf')");
			
		} finally {
			DbUtils.closeQuietly(stmt);
		}
	}
	
	private void executeBatch() throws SQLException {
		Statement stmt = null;
		
		try {
			stmt = this.conn.createStatement();
			stmt.addBatch("insert into jdbc_aop values (5, 'aaaa')");
			stmt.addBatch("insert into jdbc_aop values (6, 'aaaa')");
			stmt.addBatch("insert into jdbc_aop values (7, 'aaaa')");
			stmt.addBatch("insert into jdbc_aop values (8, null)");
			stmt.executeBatch();
			
		} finally {
			DbUtils.closeQuietly(stmt);
		}
	}
	
	private void query() throws SQLException {
		List<Map<String, Object>> result = QueryExecutor.query(this.conn,
				"select * from jdbc_aop where id >= ?", new Object[] {2});
		
		for (Map<String, Object> row : result) {
			System.out.printf("#query id=%s, name=%s%n", row.get("id"), row.get("name"));
		}
	}

}
